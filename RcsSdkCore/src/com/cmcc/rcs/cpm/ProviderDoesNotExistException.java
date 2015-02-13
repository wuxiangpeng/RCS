package com.cmcc.rcs.cpm;

public class ProviderDoesNotExistException extends CPMException {
    public ProviderDoesNotExistException(){
    }

    public ProviderDoesNotExistException(String message) {
        super(message);
    }

    public ProviderDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}

