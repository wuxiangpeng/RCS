package com.cmcc.rcs.cpm.core.api;

public class SipException extends Exception {
    public SipException() {
    }

    public SipException(String message) {
        super(message);
    }

    public SipException(String message, Throwable cause) {
        super(message, cause);
    }
}
