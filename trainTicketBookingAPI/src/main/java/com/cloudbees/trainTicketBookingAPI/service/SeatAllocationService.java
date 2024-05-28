package com.cloudbees.trainTicketBookingAPI.service;

import com.cloudbees.trainTicketBookingAPI.domain.entity.Seat;
import com.cloudbees.trainTicketBookingAPI.exception.SeatOccupiedException;
import com.cloudbees.trainTicketBookingAPI.exception.TicketsSoldOutException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Service
public class SeatAllocationService {
    public static final int sectionSize = 64;
    Map<Integer, Boolean> seatOccupiedMap = new HashMap<>();
    Queue<Seat> seatOrder = new LinkedList<>();

    @PostConstruct
    public void init() {
        for (int seats = 0; seats < sectionSize * 2; seats++) {
            seatOrder.add(new Seat(seats < sectionSize ? "A" : "B", seats % sectionSize + 1));
        }
    }

    public Seat allocateNewSeat() {
        while (!seatOrder.isEmpty() && seatOccupiedMap.getOrDefault(seatToNumber(seatOrder.peek()), false)) {
            seatOrder.poll();
        }
        if (seatOrder.isEmpty())
            throw new TicketsSoldOutException();
        Seat nextSeat = seatOrder.poll();
        seatOccupiedMap.put(seatToNumber(nextSeat), true);
        return nextSeat;
    }

    public Seat allocateSpecificSeat(Seat seat) {
        if (seatOccupiedMap.getOrDefault(seatToNumber(seat), false))
            throw new SeatOccupiedException();
        seatOccupiedMap.put(seatToNumber(seat), true);
        return seat;
    }

    public void manageVacatedSeat(Seat seat) {
        seatOccupiedMap.put(seatToNumber(seat), false);
        seatOrder.add(seat);
    }

    public int seatToNumber(Seat seat) {
        return (seat.getSection().equals("A") ? 0 : sectionSize) + seat.getSeatNumber();
    }
}
