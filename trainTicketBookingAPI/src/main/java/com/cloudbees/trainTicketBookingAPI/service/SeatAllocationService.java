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

    /**
     * Initializes the seatOrder queue with seats from sections A and B.
     */
    @PostConstruct
    public void init() {
        for (int seats = 0; seats < sectionSize * 2; seats++) {
            seatOrder.add(new Seat(seats < sectionSize ? "A" : "B", seats % sectionSize + 1));
        }
    }

    /**
     * Allocates the next available seat.
     *
     * @return the allocated seat
     * @throws TicketsSoldOutException if all tickets are sold out
     */
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

    /**
     * Allocates a requested seat if it is available.
     *
     * @param seat the seat to allocate
     * @return the allocated seat
     * @throws SeatOccupiedException if the seat is already occupied
     */
    public Seat allocateSpecificSeat(Seat seat) {
        if (seatOccupiedMap.getOrDefault(seatToNumber(seat), false))
            throw new SeatOccupiedException();
        seatOccupiedMap.put(seatToNumber(seat), true);
        return seat;
    }

    /**
     * Manages a vacated seat by marking it as available and adding it back to the seat order queue.
     *
     * @param seat the seat vacated due to removal or modification of ticket
     */
    public void manageVacatedSeat(Seat seat) {
        seatOccupiedMap.put(seatToNumber(seat), false);
        seatOrder.add(seat);
    }

    /**
     * Converts a seat object to a unique seat number based on section and seatNumber.
     *
     * @param seat the seat to convert
     * @return the unique seat number
     */
    public int seatToNumber(Seat seat) {
        return (seat.getSection().equals("A") ? 0 : sectionSize) + seat.getSeatNumber();
    }
}
