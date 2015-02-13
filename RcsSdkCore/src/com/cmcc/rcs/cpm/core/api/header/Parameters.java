package com.cmcc.rcs.cpm.core.api.header;


import java.text.ParseException;
import java.util.Iterator;

import com.cmcc.rcs.cpm.core.api.NameValue;

public interface Parameters {
    String getParameter(String name);
    void setParameter(String name, String value) throws ParseException;
    void setParameter(NameValue nameValue) throws ParseException;

    Iterator getParameterNames();
    void removeParameter(String name);
}
