package com.cmcc.rcs.cpm.core.api.header;

import java.text.ParseException;

public interface SubjectHeader extends Header {
    String NAME = "Subject";

    String getSubject();
    void setSubject(String subject) throws ParseException;
}
