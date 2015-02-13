package com.cmcc.rcs.cpm;

public class TransactionDoesNotExistException extends CPMException {
    public TransactionDoesNotExistException(){
    }

    public TransactionDoesNotExistException(String message) {
        super(message);
    }

    public TransactionDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}

