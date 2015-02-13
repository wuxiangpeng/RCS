package com.cmcc.rcs.cpm;

public class ObjectInUseException extends CPMException {
    public ObjectInUseException() {
    }

    public ObjectInUseException(String message) {
        super(message);
    }

    public ObjectInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}

