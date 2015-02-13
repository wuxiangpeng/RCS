package com.cmcc.rcs.cpm;

public class PeerUnavailableException extends CPMException {
    public PeerUnavailableException() {
    }

    public PeerUnavailableException(String message) {
        super(message);
    }

    public PeerUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

