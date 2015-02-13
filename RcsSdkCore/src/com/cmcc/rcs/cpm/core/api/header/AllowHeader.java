package com.cmcc.rcs.cpm.core.api.header;

import java.text.ParseException;

public interface AllowHeader extends Header {
    String NAME = "Allow";

    String getMethod();
    void setMethod(String method) throws ParseException;
}
