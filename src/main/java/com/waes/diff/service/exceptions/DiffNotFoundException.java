package com.waes.diff.service.exceptions;

public class DiffNotFoundException extends RuntimeException {
    public DiffNotFoundException(Long diffId) {
        super(String.format("Diff with id %d not found.", diffId));
    }
}
