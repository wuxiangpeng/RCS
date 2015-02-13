package com.cmcc.rcs.cpm;

public class TransactionUnavailableException extends CPMException {
    public TransactionUnavailableException() {
    }

    public TransactionUnavailableException(String message) {
        super(message);
    }

    public TransactionUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

