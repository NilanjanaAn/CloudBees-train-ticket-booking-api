package com.cloudbees.trainTicketBookingAPI.repository;

import com.cloudbees.trainTicketBookingAPI.domain.entity.Seat;
import com.cloudbees.trainTicketBookingAPI.domain.entity.Ticket;
import com.cloudbees.trainTicketBookingAPI.domain.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DataJpaTest
@ActiveProfiles("test")
class TicketRepositoryTest {
    @Autowired
    TicketRepository ticketRepository;
    private Ticket ticket;

    @BeforeEach
    void setup() {
        ticket = new Ticket();
        ticket.setUser(new User("firstName", "lastName", "email@domain.com"));
        ticket.setSeatAllocated(new Seat("A", 1));
    }

    @AfterEach
    void teardown() {
        ticketRepository.deleteAll();
    }

    @Test
    void testSave() {
        Ticket savedTicket = ticketRepository.save(ticket);
        assertThat(savedTicket).usingRecursiveComparison().ignoringFields("pnr").isEqualTo(ticket);
        assertNotEquals(savedTicket.getPnr(), null);
    }

    @Test
    void testFindByIdFound() {
        Ticket savedTicket = ticketRepository.save(ticket);
        Optional<Ticket> foundTicket = ticketRepository.findById(savedTicket.getPnr());
        assertThat(foundTicket).isPresent();
        assertThat(foundTicket.get()).usingRecursiveComparison().ignoringFields("pnr").isEqualTo(savedTicket);
    }

    @Test
    void testFindByIdNotFound() {
        Long pnr = 0L;
        Optional<Ticket> foundTicket = ticketRepository.findById(pnr);
        assertThat(foundTicket).isEmpty();
    }

    @Test
    void testDeleteById() {
        Ticket savedTicket = ticketRepository.save(ticket);
        ticketRepository.deleteById(savedTicket.getPnr());
        Optional<Ticket> deletedTicket = ticketRepository.findById(savedTicket.getPnr());
        assertThat(deletedTicket).isEmpty();
    }

    @Test
    void findAllByOrderBySeatAllocatedSectionAscSeatAllocatedSeatNumberAsc() {
        Ticket ticket1 = new Ticket();
        ticket1.setSeatAllocated(new Seat("A", 10));
        Ticket savedTicket1 = ticketRepository.save(ticket1);
        Ticket ticket2 = new Ticket();
        ticket2.setSeatAllocated(new Seat("B", 20));
        Ticket savedTicket2 = ticketRepository.save(ticket2);
        Ticket ticket3 = new Ticket();
        ticket3.setSeatAllocated(new Seat("A", 5));
        Ticket savedTicket3 = ticketRepository.save(ticket3);

        List<Ticket> tickets = List.of(savedTicket3, savedTicket1, savedTicket2);
        List<Ticket> result = ticketRepository.findAllByOrderBySeatAllocatedSectionAscSeatAllocatedSeatNumberAsc();
        assertThat(result).hasSize(3);
        assertThat(result).isEqualTo(tickets);
    }

    @Test
    void findBySeatAllocatedSectionOrderBySeatAllocatedSeatNumberAsc() {
        String section = "A";
        Ticket ticket1 = new Ticket();
        ticket1.setSeatAllocated(new Seat("A", 10));
        Ticket savedTicket1 = ticketRepository.save(ticket1);
        Ticket ticket2 = new Ticket();
        ticket2.setSeatAllocated(new Seat("B", 20));
        Ticket savedTicket2 = ticketRepository.save(ticket2);
        Ticket ticket3 = new Ticket();
        ticket3.setSeatAllocated(new Seat("A", 5));
        Ticket savedTicket3 = ticketRepository.save(ticket3);

        List<Ticket> tickets = List.of(savedTicket3, savedTicket1);
        List<Ticket> result = ticketRepository.findBySeatAllocatedSectionOrderBySeatAllocatedSeatNumberAsc(section);
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(tickets);
    }
}