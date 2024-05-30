package com.cloudbees.trainTicketBookingAPI.controller;

import com.cloudbees.trainTicketBookingAPI.domain.entity.Seat;
import com.cloudbees.trainTicketBookingAPI.domain.entity.Ticket;
import com.cloudbees.trainTicketBookingAPI.domain.entity.User;
import com.cloudbees.trainTicketBookingAPI.domain.response.SeatChartResponseDTO;
import com.cloudbees.trainTicketBookingAPI.exception.InvalidEmailFormatException;
import com.cloudbees.trainTicketBookingAPI.exception.InvalidSeatRequestException;
import com.cloudbees.trainTicketBookingAPI.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    @Autowired
    TicketService ticketService;

    /**
     * Retrieves a ticket by its Passenger Name Record (PNR) number.
     *
     * @param pnr the PNR number of the ticket
     * @return receipt of the ticket with the specified PNR number and allocated seat
     */
    @Operation(summary = "Get Ticket Receipt",
        description = "Get the receipt of a ticket by its Passenger Name Record (PNR) number.")
    @GetMapping("/receipt/{pnr}")
    public Ticket getTicket(@PathVariable Long pnr) {
        return ticketService.getTicketByPnr(pnr);
    }

    /**
     * Retrieves the current seat chart of the train, which shows the users and the seat they have been allocated.
     *
     * @return a list of SeatChartResponseDTO objects representing the current seat chart
     */
    @Operation(summary = "Get Seat Chart",
        description = "Get the current seat chart of the train, which shows the users and the seat they have been allocated.")
    @GetMapping("/seatchart")
    public List<SeatChartResponseDTO> getSeatChart() {
        return ticketService.getSeatChart();
    }

    /**
     * Retrieves the current seat chart of the train, which shows the users and the seat they have been allocated by the requested section.
     *
     * @param section the section of the train whose seat chart must be retrieved
     * @return a list of SeatChartResponseDTO objects representing the seat chart for the requested section
     */
    @Operation(summary = "Get Seat Chart By Section",
        description = "Get the current seat chart of the requested section of the train, which shows the users and the seat they have been allocated.")
    @GetMapping("/seatchart/{section}")
    public List<SeatChartResponseDTO> getSeatChartBySection(@PathVariable String section) {
        return ticketService.getSeatChartBySection(section.toUpperCase());
    }

    /**
     * Purchases a new ticket for the specified user.
     *
     * @param user          the user purchasing the ticket
     * @param bindingResult the result of validating the user object (email format validation)
     * @return the purchased ticket
     * @throws InvalidEmailFormatException if the user's email format is invalid
     */
    @Operation(summary = "Purchase Ticket",
        description = "Purchase a ticket for the specified user in the train from London to France for $20.")
    @PostMapping("/purchase")
    public Ticket purchaseTicket(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new InvalidEmailFormatException();
        return ticketService.purchaseTicket(user);
    }

    /**
     * Removes a user's ticket by its PNR number.
     *
     * @param pnr the PNR number of the ticket to be removed
     */
    @Operation(summary = "Remove Ticket",
        description = "Remove a user's ticket by its PNR number.")
    @DeleteMapping("/remove/{pnr}")
    public void removeUserTicket(@PathVariable Long pnr) {
        ticketService.removeUserTicket(pnr);
    }

    /**
     * Modifies the seat allocated to a user's ticket, if present and unoccupied.
     *
     * @param pnr           the PNR number of the ticket to be modified
     * @param seat          the new seat to be allocated to the ticket
     * @param bindingResult the result of validating the seat object
     * @return the modified ticket
     * @throws InvalidSeatRequestException if the seat request is invalid
     */
    @Operation(summary = "Modify Seat",
        description = "Modify the seat allocated to a user's ticket, if present and unoccupied. You may check the seatchart to identify vacant seats.")
    @PutMapping("/modify/{pnr}")
    public Ticket modifySeat(@PathVariable Long pnr, @RequestBody @Valid Seat seat, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new InvalidSeatRequestException();
        return ticketService.modifyUserTicket(pnr, seat);
    }
}
