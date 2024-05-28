package com.cloudbees.trainTicketBookingAPI.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TicketsSoldOutException extends RuntimeException {
    public TicketsSoldOutException() {
        super("Sorry, there are no more tickets available for this train at the moment.");
    }
}
