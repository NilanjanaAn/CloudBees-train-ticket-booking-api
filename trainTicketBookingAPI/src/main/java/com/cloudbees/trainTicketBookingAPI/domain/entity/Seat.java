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

    public boolean equals(Seat otherSeat) {
        if (this == otherSeat)
            return true;
        return Objects.equals(this.section, otherSeat.section) && Objects.equals(this.seatNumber, otherSeat.seatNumber);
    }
}
