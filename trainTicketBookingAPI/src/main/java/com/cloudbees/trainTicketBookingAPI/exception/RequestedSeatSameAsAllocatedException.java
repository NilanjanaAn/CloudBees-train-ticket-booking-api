package com.cloudbees.trainTicketBookingAPI.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequestedSeatSameAsAllocatedException extends RuntimeException {
    public RequestedSeatSameAsAllocatedException() {
        super("The requested seat is the same as the one you have been allocated.");
    }
}
