package com.cmcc.rcs.cpm.core.api.header;

import java.text.ParseException;

public interface ContentTypeHeader extends Header, MediaType, Parameters {
    String NAME = "Content-Type";

    String getCharset();
    void setContentType(String contentType, String contentSubType)
            throws ParseException;
}
