package com.cmcc.rcs.cpm.message;

import java.text.ParseException;

import com.cmcc.rcs.cpm.core.api.address.URI;
import com.cmcc.rcs.cpm.core.api.message.Message;



public interface Request  extends Message{
    String ACK = "ACK";
    String BYE = "BYE";
    String CANCEL = "CANCEL";
    String INVITE = "INVITE";
    String OPTIONS = "OPTIONS";
    String REGISTER = "REGISTER";

    String INFO = "INFO";
    String MESSAGE = "MESSAGE";
    String NOTIFY = "NOTIFY";
    String PRACK = "PRACK";
    String PUBLISH = "PUBLISH";
    String REFER = "REFER";
    String SUBSCRIBE = "SUBSCRIBE";
    String UPDATE = "UPDATE";

    String getMethod();
    void setMethod(String method) throws ParseException;

    URI getRequestURI();
    void setRequestURI(URI requestURI);
}

