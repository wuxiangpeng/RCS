package com.cmcc.rcs.cpm.core.api.header;

import java.util.Calendar;

public interface DateHeader extends Header {
    String NAME = "Date";

    Calendar getDate();
    void setDate(Calendar date);
}
