package com.cloudbees.trainTicketBookingAPI.exception;

import com.cloudbees.trainTicketBookingAPI.service.SeatAllocationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSeatRequestException extends RuntimeException {
    public InvalidSeatRequestException() {
        super("Requested seat is invalid. This train has only two sections: A and B. Each section has seats numbered 1-" + SeatAllocationService.sectionSize + ".");
    }
}

