package com.cloudbees.trainTicketBookingAPI.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Seat {
    private String section;
    private Integer seatNumber;

    public boolean equals(Seat otherSeat) {
        if (this == otherSeat)
            return true;
        return Objects.equals(this.section, otherSeat.section) && Objects.equals(this.seatNumber, otherSeat.seatNumber);
    }
}
