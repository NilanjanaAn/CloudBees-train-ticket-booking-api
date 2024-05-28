package com.cloudbees.trainTicketBookingAPI.service;

import com.cloudbees.trainTicketBookingAPI.domain.entity.Seat;
import com.cloudbees.trainTicketBookingAPI.exception.SeatOccupiedException;
import com.cloudbees.trainTicketBookingAPI.exception.TicketsSoldOutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SeatAllocationServiceTest {
    @InjectMocks
    SeatAllocationService seatAllocationService;

    @BeforeEach
    void setup() {
        seatAllocationService.init();
    }

    @Test
    void testAllocateNewSeat() {
        Seat seat1 = seatAllocationService.allocateNewSeat();
        Seat seat2 = seatAllocationService.allocateNewSeat();
        assertNotEquals(seat1, seat2);
        assertThat(seat1.getSection()).isIn("A", "B");
        assertThat(seat1.getSeatNumber()).isBetween(1, SeatAllocationService.sectionSize);
        assertThat(seat2.getSection()).isIn("A", "B");
        assertThat(seat2.getSeatNumber()).isBetween(1, SeatAllocationService.sectionSize);
    }

    @Test
    void testAllocateNewSeatSkipOccupiedSeatInQueue() {
        seatAllocationService.allocateSpecificSeat(new Seat("A", 1));
        Seat seat2 = seatAllocationService.allocateNewSeat();
        assertEquals(seat2.getSection(), "A");
        assertEquals(seat2.getSeatNumber(), 2);
    }

    @Test
    void testAllocateNewSeatSoldOut() {
        for (int seat = 0; seat < SeatAllocationService.sectionSize * 2; seat++) {
            seatAllocationService.allocateNewSeat();
        }
        assertThrows(TicketsSoldOutException.class, () -> seatAllocationService.allocateNewSeat());
    }

    @Test
    void testAllocateSpecificSeat() {
        Seat seat = new Seat("A", 1);
        Seat allocatedSeat = seatAllocationService.allocateSpecificSeat(seat);
        assertEquals(allocatedSeat, seat);
    }

    @Test
    void testAllocateSpecificOccupied() {
        Seat seat = new Seat("A", 1);
        seatAllocationService.allocateSpecificSeat(seat);
        assertThrows(SeatOccupiedException.class, () -> seatAllocationService.allocateSpecificSeat(seat));
    }

    @Test
    void testManageVacatedSeat() {
        Seat seat = new Seat("A", 1);
        seatAllocationService.allocateSpecificSeat(seat);
        seatAllocationService.manageVacatedSeat(seat);
        Seat nextSeat = seatAllocationService.allocateSpecificSeat(seat);
        assertEquals(seat, nextSeat);
    }

    @Test
    void testSeatToNumber() {
        Seat seat = new Seat("B", 42);
        int expectedNumber = 64 + 42;
        int result = seatAllocationService.seatToNumber(seat);
        assertEquals(expectedNumber, result);
    }
}