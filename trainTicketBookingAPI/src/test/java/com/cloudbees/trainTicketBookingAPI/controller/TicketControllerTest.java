package com.cloudbees.trainTicketBookingAPI.controller;

import com.cloudbees.trainTicketBookingAPI.domain.entity.Seat;
import com.cloudbees.trainTicketBookingAPI.domain.entity.Ticket;
import com.cloudbees.trainTicketBookingAPI.domain.entity.User;
import com.cloudbees.trainTicketBookingAPI.domain.response.SeatChartResponseDTO;
import com.cloudbees.trainTicketBookingAPI.exception.NoSuchSectionException;
import com.cloudbees.trainTicketBookingAPI.exception.NoTicketFoundException;
import com.cloudbees.trainTicketBookingAPI.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TicketController.class)
class TicketControllerTest {

    @MockBean
    TicketService ticketService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetTicketFound() throws Exception {
        Long pnr = 1L;
        Ticket ticket = new Ticket();
        ticket.setPnr(pnr);
        ticket.setUser(new User("firstName", "lastName", "email@domain.com"));
        ticket.setSeatAllocated(new Seat("A", 1));
        when(ticketService.getTicketByPnr(pnr)).thenReturn(ticket);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/ticket/receipt/{pnr}", pnr))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pnr").value(pnr))
            .andExpect(jsonPath("$.user.firstName").value("firstName"))
            .andExpect(jsonPath("$.user.lastName").value("lastName"))
            .andExpect(jsonPath("$.user.email").value("email@domain.com"))
            .andExpect(jsonPath("$.seatAllocated.section").value("A"))
            .andExpect(jsonPath("$.seatAllocated.seatNumber").value(1));
    }

    @Test
    void testGetTicketNotFound() throws Exception {
        Long pnr = 1L;
        when(ticketService.getTicketByPnr(pnr)).thenThrow(NoTicketFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/ticket/receipt/{pnr}", pnr))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetSeatChart() throws Exception {
        List<SeatChartResponseDTO> seatChart = List.of(new SeatChartResponseDTO(), new SeatChartResponseDTO());
        when(ticketService.getSeatChart()).thenReturn(seatChart);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/ticket/seatchart"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetSeatChartBySectionValidSection() throws Exception {
        String section = "A";
        SeatChartResponseDTO seatChartResponseDTO1 = new SeatChartResponseDTO(new User(), new Seat("A", 10));
        SeatChartResponseDTO seatChartResponseDTO2 = new SeatChartResponseDTO(new User(), new Seat("B", 20));
        SeatChartResponseDTO seatChartResponseDTO3 = new SeatChartResponseDTO(new User(), new Seat("A", 5));
        List<SeatChartResponseDTO> seatChart = List.of(seatChartResponseDTO1, seatChartResponseDTO2, seatChartResponseDTO3);
        when(ticketService.getSeatChartBySection(section)).thenReturn(seatChart
            .stream()
            .filter(seatChartResponseDTO -> seatChartResponseDTO.getSeat()
                .getSection()
                .equals(section))
            .collect(Collectors.toList()));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/ticket//seatchart/{section}", section))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetSeatChartBySectionInvalidSection() throws Exception {
        String section = "C";
        when(ticketService.getSeatChartBySection(section)).thenThrow(NoSuchSectionException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/ticket//seatchart/{section}", section))
            .andExpect(status().isNotFound());
    }

    @Test
    void testPurchaseTicketValidEmail() throws Exception {
        User user = new User("firstName", "lastName", "email@domain.com");
        Ticket ticket = new Ticket();
        ticket.setUser(user);
        when(ticketService.purchaseTicket(user)).thenReturn(ticket);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/ticket/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
            .andExpect(status().isOk());
    }

    @Test
    void testPurchaseTicketInvalidEmail() throws Exception {
        User user = new User("firstName", "lastName", "email");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/ticket/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testRemoveUserTicket() throws Exception {
        Long pnr = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/ticket/remove/{pnr}", pnr)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(ticketService, times(1)).removeUserTicket(pnr);
    }

    @Test
    void testModifySeatPnrFoundAndValidSeat() throws Exception {
        Long pnr = 1L;
        Seat seat = new Seat("B", 1);
        Ticket ticket = new Ticket();
        ticket.setPnr(pnr);
        ticket.setSeatAllocated(seat);
        when(ticketService.modifyUserTicket(pnr, seat)).thenReturn(ticket);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/ticket/modify/{pnr}", pnr)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(seat)))
            .andExpect(status().isOk());
    }

    @Test
    void testModifySeatInvalidSeatSection() throws Exception {
        Long pnr = 1L;
        Seat seat = new Seat("C", 1);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/ticket/modify/{pnr}", pnr)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(seat)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testModifySeatInvalidSeatNumber() throws Exception {
        Long pnr = 1L;
        Seat seat = new Seat("A", 1000);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/ticket/modify/{pnr}", pnr)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(seat)))
            .andExpect(status().isBadRequest());
    }
}