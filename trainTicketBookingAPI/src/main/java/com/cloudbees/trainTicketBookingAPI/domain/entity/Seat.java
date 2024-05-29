package com.cloudbees.trainTicketBookingAPI.domain.entity;

import com.cloudbees.trainTicketBookingAPI.service.SeatAllocationService;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[AB]$")
    private String section;
    @Min(1)
    @Max(SeatAllocationService.sectionSize)
    private Integer seatNumber;

    /**
     * Checks whether the current seat has the same section and seatNumber as the provided seat.
     *
     * @param otherSeat the seat to compare the current seat with
     * @return a boolean value representing whether the seats are equal
     */
    public boolean equals(Seat otherSeat) {
        if (this == otherSeat)
            return true;
        return Objects.equals(this.section, otherSeat.section) && Objects.equals(this.seatNumber, otherSeat.seatNumber);
    }
}
