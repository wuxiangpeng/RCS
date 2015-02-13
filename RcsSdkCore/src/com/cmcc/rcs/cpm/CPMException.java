package com.cmcc.rcs.cpm;

public class CPMException extends Exception {
    public CPMException() {
    }

    public CPMException(String message) {
        super(message);
    }

    public CPMException(String message, Throwable cause) {
        super(message, cause);
    }
}

