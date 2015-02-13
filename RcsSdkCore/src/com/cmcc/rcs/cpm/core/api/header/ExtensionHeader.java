package com.cmcc.rcs.cpm.core.api.header;

import java.text.ParseException;

public interface ExtensionHeader extends Header {
    String getValue();
    void setValue(String value) throws ParseException;
}
