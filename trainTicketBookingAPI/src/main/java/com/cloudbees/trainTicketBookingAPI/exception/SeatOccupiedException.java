package com.cloudbees.trainTicketBookingAPI.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SeatOccupiedException extends RuntimeException {
    public SeatOccupiedException() {
        super("Sorry, the seat you have requested is already occupied. Please refer to the seat chart to view available seats.");
    }
}

