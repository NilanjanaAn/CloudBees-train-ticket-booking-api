package com.cloudbees.trainTicketBookingAPI.domain.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@Table
@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pnr;
    private String fromStation;
    private String toStation;
    @Embedded
    private User user;
    private Double pricePaidInDollars;
    @Embedded
    private Seat seatAllocated;

    public Ticket() {
        this.fromStation = "London";
        this.toStation = "France";
        this.pricePaidInDollars = 20.0;
    }
}
