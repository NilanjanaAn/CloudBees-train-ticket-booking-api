package com.cloudbees.trainTicketBookingAPI.service;

import com.cloudbees.trainTicketBookingAPI.domain.entity.Seat;
import com.cloudbees.trainTicketBookingAPI.domain.entity.Ticket;
import com.cloudbees.trainTicketBookingAPI.domain.entity.User;
import com.cloudbees.trainTicketBookingAPI.domain.response.SeatChartResponseDTO;
import com.cloudbees.trainTicketBookingAPI.exception.NoSuchSectionException;
import com.cloudbees.trainTicketBookingAPI.exception.NoTicketFoundException;
import com.cloudbees.trainTicketBookingAPI.exception.RequestedSeatSameAsAllocatedException;
import com.cloudbees.trainTicketBookingAPI.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketService {
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    SeatAllocationService seatAllocationService;

    public Ticket purchaseTicket(User user) {
        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setSeatAllocated(seatAllocationService.allocateNewSeat());
        ticketRepository.save(ticket);
        return ticket;
    }

    public Ticket getTicketByPnr(Long pnr) {
        Optional<Ticket> ticket = ticketRepository.findById(pnr);
        if (ticket.isEmpty()) throw new NoTicketFoundException();
        return ticket.get();
    }

    public List<SeatChartResponseDTO> getSeatChart() {
        List<Ticket> allTickets = ticketRepository.findAllByOrderBySeatAllocatedSectionAscSeatAllocatedSeatNumberAsc();
        return allTickets
            .stream()
            .map(ticket -> new SeatChartResponseDTO(ticket.getUser(), ticket.getSeatAllocated()))
            .collect(Collectors.toList());
    }

    public List<SeatChartResponseDTO> getSeatChartBySection(String section) {
        if (!Objects.equals(section, "A") && !Objects.equals(section, "B"))
            throw new NoSuchSectionException();
        List<Ticket> allTickets = ticketRepository.findBySeatAllocatedSectionOrderBySeatAllocatedSeatNumberAsc(section);
        return allTickets
            .stream()
            .map(ticket -> new SeatChartResponseDTO(ticket.getUser(), ticket.getSeatAllocated()))
            .collect(Collectors.toList());
    }

    public void removeUserTicket(Long pnr) {
        Ticket ticket = getTicketByPnr(pnr);
        Seat freeSeat = ticket.getSeatAllocated();
        seatAllocationService.manageVacatedSeat(freeSeat);
        ticketRepository.deleteById(pnr);
    }

    public Ticket modifyUserTicket(Long pnr, Seat seat) {
        Ticket ticket = getTicketByPnr(pnr);
        if (ticket.getSeatAllocated().equals(seat))
            throw new RequestedSeatSameAsAllocatedException();
        Seat previousSeat = ticket.getSeatAllocated();
        ticket.setSeatAllocated(seatAllocationService.allocateSpecificSeat(seat));
        seatAllocationService.manageVacatedSeat(previousSeat);
        ticketRepository.save(ticket);
        return ticket;
    }
}
