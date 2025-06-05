package com.zeta.digital_insurance_management_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPolicyRenewalException extends RuntimeException {
    public InvalidPolicyRenewalException(String message) {
        super(message);
    }
}
