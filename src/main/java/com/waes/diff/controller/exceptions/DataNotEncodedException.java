package com.waes.diff.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DataNotEncodedException extends RuntimeException {
    public DataNotEncodedException(String message) {
        super(message);
    }
}
