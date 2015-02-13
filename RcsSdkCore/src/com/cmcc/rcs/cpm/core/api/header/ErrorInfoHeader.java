package com.cmcc.rcs.cpm.core.api.header;

import java.text.ParseException;

import com.cmcc.rcs.cpm.core.api.address.URI;



public interface ErrorInfoHeader extends Header, Parameters {
    String NAME = "Error-Info";

    URI getErrorInfo();
    void setErrorInfo(URI errorInfo);

    String getErrorMessage();
    void setErrorMessage(String errorMessage) throws ParseException;
}
