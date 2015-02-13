package com.cmcc.rcs.cpm;

public class TransactionAlreadyExistsException extends CPMException {
    public TransactionAlreadyExistsException(){
    }

    public TransactionAlreadyExistsException(String message) {
        super(message);
    }

    public TransactionAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

