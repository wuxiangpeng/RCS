package com.cmcc.rcs.cpm.core.api.header;

import java.text.ParseException;

public interface MediaType {
    String getContentSubType();
    void setContentSubType(String contentSubType) throws ParseException;

    String getContentType();
    void setContentType(String contentType) throws ParseException;
}
