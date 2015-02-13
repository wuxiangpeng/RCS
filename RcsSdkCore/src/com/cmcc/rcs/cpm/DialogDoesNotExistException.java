package com.cmcc.rcs.cpm;

public class DialogDoesNotExistException extends CPMException {
    public DialogDoesNotExistException(){
    }

    public DialogDoesNotExistException(String message) {
        super(message);
    }

    public DialogDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}

