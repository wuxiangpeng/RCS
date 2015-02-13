package com.cmcc.rcs.cpm.core.api.header;

import java.text.ParseException;

import com.cmcc.rcs.core.connect.InvalidArgumentException;


public interface ReasonHeader extends Header, Parameters {
    String NAME = "Reason";

    int getCause();
    void setCause(int cause) throws InvalidArgumentException;

    String getProtocol();
    void setProtocol(String protocol) throws ParseException;

    String getText();
    void setText(String text) throws ParseException;
}
