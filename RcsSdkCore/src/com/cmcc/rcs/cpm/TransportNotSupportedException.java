package com.cmcc.rcs.cpm;



public class TransportNotSupportedException extends CPMException {
    public TransportNotSupportedException() {
    }

    public TransportNotSupportedException(String message) {
        super(message);
    }

    public TransportNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}

