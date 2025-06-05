package com.zeta.digital_insurance_management_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalArgumentFoundException extends RuntimeException {
    public IllegalArgumentFoundException(String message) {
        super(message);
    }
}
