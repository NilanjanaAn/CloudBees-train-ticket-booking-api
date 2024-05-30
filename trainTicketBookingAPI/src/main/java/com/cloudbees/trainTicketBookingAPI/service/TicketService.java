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

    /**
     * Purchases a new ticket for the specified user.
     *
     * @param user the user purchasing the ticket
     * @return the purchased ticket
     */
    public Ticket purchaseTicket(User user) {
        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setSeatAllocated(seatAllocationService.allocateNewSeat());
        ticketRepository.save(ticket);
        return ticket;
    }

    /**
     * Retrieves a ticket by its PNR (Passenger Name Record) number.
     *
     * @param pnr the PNR number of the ticket
     * @return receipt of the ticket with the specified PNR number and allocated seat
     * @throws NoTicketFoundException if no ticket is found with the specified PNR number
     */
    public Ticket getTicketByPnr(Long pnr) {
        Optional<Ticket> ticket = ticketRepository.findById(pnr);
        if (ticket.isEmpty()) throw new NoTicketFoundException();
        return ticket.get();
    }

    /**
     * Retrieves the current seat chart of the train, which shows the users and the seat they have been allocated.
     *
     * @return a list of SeatChartResponseDTO objects representing the current seat chart
     */
    public List<SeatChartResponseDTO> getSeatChart() {
        List<Ticket> allTickets = ticketRepository.findAllByOrderBySeatAllocatedSectionAscSeatAllocatedSeatNumberAsc();
        return allTickets
            .stream()
            .map(ticket -> new SeatChartResponseDTO(ticket.getUser(), ticket.getSeatAllocated()))
            .collect(Collectors.toList());
    }

    /**
     * Retrieves the current seat chart of the train, which shows the users and the seat they have been allocated by the requested section.
     *
     * @param section the section of the train whose seat chart must be retrieved (must be A or B)
     * @return a list of SeatChartResponseDTO objects representing the seat chart for the specified section
     * @throws NoSuchSectionException if the specified section is not A or B
     */
    public List<SeatChartResponseDTO> getSeatChartBySection(String section) {
        if (!Objects.equals(section, "A") && !Objects.equals(section, "B"))
            throw new NoSuchSectionException();
        List<Ticket> allTickets = ticketRepository.findBySeatAllocatedSectionOrderBySeatAllocatedSeatNumberAsc(section);
        return allTickets
            .stream()
            .map(ticket -> new SeatChartResponseDTO(ticket.getUser(), ticket.getSeatAllocated()))
            .collect(Collectors.toList());
    }

    /**
     * Removes a user's ticket by its PNR number.
     *
     * @param pnr the PNR number of the ticket to be removed
     */
    public void removeUserTicket(Long pnr) {
        Ticket ticket = getTicketByPnr(pnr);
        Seat freeSeat = ticket.getSeatAllocated();
        seatAllocationService.manageVacatedSeat(freeSeat);
        ticketRepository.deleteById(pnr);
    }

    /**
     * Modifies the seat allocated to a user's ticket, if present and unoccupied.
     *
     * @param pnr  the PNR number of the ticket to be modified
     * @param seat the new seat requested to be allocated to the ticket
     * @return the modified ticket
     * @throws RequestedSeatSameAsAllocatedException if the requested seat is the same as the allocated seat
     */
    public Ticket modifyUserTicket(Long pnr, Seat seat) {
        Ticket ticket = getTicketByPnr(pnr);
        if (ticket.getSeatAllocated().equals(seat))
            throw new RequestedSeatSameAsAllocatedException();
        Seat previousSeat = ticket.getSeatAllocated();
        seat.setSection(seat.getSection().toUpperCase());
        ticket.setSeatAllocated(seatAllocationService.allocateSpecificSeat(seat));
        seatAllocationService.manageVacatedSeat(previousSeat);
        ticketRepository.save(ticket);
        return ticket;
    }
}
