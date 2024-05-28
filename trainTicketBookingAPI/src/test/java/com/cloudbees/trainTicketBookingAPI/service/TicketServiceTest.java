package com.cloudbees.trainTicketBookingAPI.service;

import com.cloudbees.trainTicketBookingAPI.domain.entity.Seat;
import com.cloudbees.trainTicketBookingAPI.domain.entity.Ticket;
import com.cloudbees.trainTicketBookingAPI.domain.entity.User;
import com.cloudbees.trainTicketBookingAPI.domain.response.SeatChartResponseDTO;
import com.cloudbees.trainTicketBookingAPI.exception.NoSuchSectionException;
import com.cloudbees.trainTicketBookingAPI.exception.NoTicketFoundException;
import com.cloudbees.trainTicketBookingAPI.exception.RequestedSeatSameAsAllocatedException;
import com.cloudbees.trainTicketBookingAPI.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class TicketServiceTest {

    @Mock
    TicketRepository ticketRepository;
    @Mock
    SeatAllocationService seatAllocationService;
    @InjectMocks
    TicketService ticketService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPurchaseTicket() {
        User user = new User("firstName", "lastName", "email@domain.com");
        Seat seat = new Seat("A", 1);
        Ticket ticket = new Ticket();
        when(seatAllocationService.allocateNewSeat()).thenReturn(seat);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        Ticket result = ticketService.purchaseTicket(user);
        assertEquals(user, result.getUser());
        assertEquals(seat, result.getSeatAllocated());
    }

    @Test
    void testGetTicketByPnr() {
        Long pnr = 1L;
        Ticket ticket = new Ticket();
        ticket.setPnr(pnr);
        when(ticketRepository.findById(pnr)).thenReturn(Optional.of(ticket));
        Ticket result = ticketService.getTicketByPnr(pnr);
        assertEquals(ticket, result);
        assertEquals(pnr, result.getPnr());
    }

    @Test
    void testGetTicketByPnrNotFound() {
        Long pnr = 1L;
        when(ticketRepository.findById(pnr)).thenReturn(Optional.empty());
        assertThrows(NoTicketFoundException.class, () -> ticketService.getTicketByPnr(pnr));
    }

    @Test
    void testGetSeatChart() {
        List<Ticket> tickets = List.of(new Ticket(), new Ticket());
        when(ticketRepository.findAllByOrderBySeatAllocatedSectionAscSeatAllocatedSeatNumberAsc()).thenReturn(tickets);
        List<SeatChartResponseDTO> result = ticketService.getSeatChart();
        assertEquals(tickets.size(), result.size());
    }

    @Test
    void testGetSeatChartBySection() {
        String section = "A";
        Ticket ticket1 = new Ticket();
        ticket1.setSeatAllocated(new Seat("A", 1));
        Ticket ticket2 = new Ticket();
        ticket2.setSeatAllocated(new Seat("B", 1));
        Ticket ticket3 = new Ticket();
        ticket3.setSeatAllocated(new Seat("A", 2));
        List<Ticket> tickets = List.of(ticket1, ticket2, ticket3);
        when(ticketRepository.findBySeatAllocatedSectionOrderBySeatAllocatedSeatNumberAsc(section))
            .thenReturn(tickets
                .stream()
                .filter(ticket -> Objects.equals(ticket.getSeatAllocated().getSection(), section))
                .collect(Collectors.toList()));
        List<SeatChartResponseDTO> result = ticketService.getSeatChartBySection(section);
        for (SeatChartResponseDTO seatChartResponseDTO : result) {
            assertEquals(section, seatChartResponseDTO.getSeat().getSection());
        }
        assertEquals(result.size(), 2);
    }

    @Test
    void testGetSeatChartBySectionInvalidSection() {
        String section = "C";
        assertThrows(NoSuchSectionException.class, () -> ticketService.getSeatChartBySection(section));
    }

    @Test
    void testRemoveUserTicket() {
        Long pnr = 1L;
        Ticket ticket = new Ticket();
        ticket.setPnr(pnr);
        ticket.setSeatAllocated(new Seat());
        when(ticketRepository.findById(pnr)).thenReturn(Optional.of(ticket));
        ticketService.removeUserTicket(pnr);
        verify(ticketRepository, times(1)).deleteById(pnr);
        verify(seatAllocationService, times(1)).manageVacatedSeat(ticket.getSeatAllocated());
    }

    @Test
    void testRemoveUserTicketNotFound() {
        Long pnr = 1L;
        when(ticketRepository.findById(pnr)).thenReturn(Optional.empty());
        assertThrows(NoTicketFoundException.class, () -> ticketService.removeUserTicket(pnr));
    }

    @Test
    void testModifyUserTicket() {
        Long pnr = 1L;
        Ticket ticket = new Ticket();
        ticket.setPnr(pnr);
        Seat previousSeat = new Seat("A", 1);
        ticket.setSeatAllocated(previousSeat);
        Seat requestedSeat = new Seat("B", 1);
        when(ticketRepository.findById(pnr)).thenReturn(Optional.of(ticket));
        when(seatAllocationService.allocateSpecificSeat(requestedSeat)).thenReturn(requestedSeat);
        ticketService.modifyUserTicket(pnr, requestedSeat);
        verify(seatAllocationService, times(1)).manageVacatedSeat(previousSeat);
        verify(seatAllocationService, times(1)).allocateSpecificSeat(requestedSeat);
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void testModifyUserTicketSameTicket() {
        Long pnr = 1L;
        Ticket ticket = new Ticket();
        ticket.setPnr(pnr);
        ticket.setSeatAllocated(new Seat("A", 1));
        Seat seat = new Seat("A", 1);
        when(ticketRepository.findById(pnr)).thenReturn(Optional.of(ticket));
        assertThrows(RequestedSeatSameAsAllocatedException.class, () -> ticketService.modifyUserTicket(pnr, seat));
    }

    @Test
    void testModifyUserTicketNotFound() {
        Long pnr = 1L;
        Seat seat = new Seat();
        when(ticketRepository.findById(pnr)).thenReturn(Optional.empty());
        assertThrows(NoTicketFoundException.class, () -> ticketService.modifyUserTicket(pnr, seat));
    }
}