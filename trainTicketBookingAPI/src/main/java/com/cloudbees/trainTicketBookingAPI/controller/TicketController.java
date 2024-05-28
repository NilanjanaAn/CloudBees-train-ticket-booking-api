package com.cloudbees.trainTicketBookingAPI.controller;

import com.cloudbees.trainTicketBookingAPI.domain.entity.Seat;
import com.cloudbees.trainTicketBookingAPI.domain.entity.Ticket;
import com.cloudbees.trainTicketBookingAPI.domain.entity.User;
import com.cloudbees.trainTicketBookingAPI.domain.response.SeatChartResponseDTO;
import com.cloudbees.trainTicketBookingAPI.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/receipt/{pnr}")
    public Ticket getTicket(@PathVariable Long pnr) {
        return ticketService.getTicketByPnr(pnr);
    }

    @GetMapping("/seatchart")
    public List<SeatChartResponseDTO> getSeatChart() {
        return ticketService.getSeatChart();
    }

    @GetMapping("/seatchart/{section}")
    public List<SeatChartResponseDTO> getSeatChartBySection(@PathVariable String section) {
        return ticketService.getSeatChartBySection(section.toUpperCase());
    }

    @PostMapping("/purchase")
    public Ticket purchaseTicket(@RequestBody User user) {
        return ticketService.purchaseTicket(user);
    }

    @DeleteMapping("/remove/{pnr}")
    public void removeUserTicket(@PathVariable Long pnr) {
        ticketService.removeUserTicket(pnr);
    }

    @PutMapping("/modify/{pnr}")
    public Ticket modifySeat(@PathVariable Long pnr, @RequestBody Seat seat) {
        return ticketService.modifyUserTicket(pnr, seat);
    }
}
