package com.cmcc.rcs.cpm.core.api.header;

import java.text.ParseException;

public interface Encoding {
    String getEncoding();
    void setEncoding(String encoding) throws ParseException;
}
