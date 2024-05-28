package com.cloudbees.trainTicketBookingAPI.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchSectionException extends RuntimeException {
    public NoSuchSectionException() {
        super("This train has only two sections: A and B.");
    }
}

