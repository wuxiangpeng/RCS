package com.cmcc.rcs.core.connect;



public class TransportNotSupportedException extends ConnectException {
    public TransportNotSupportedException() {
    }

    public TransportNotSupportedException(String message) {
        super(message);
    }

    public TransportNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}

