package com.cmcc.rcs.cpm.core.api.header;

import java.text.ParseException;

public interface AllowEventsHeader extends Header {
    String NAME = "Allow-Events";

    String getEventType();
    void setEventType(String eventType) throws ParseException;
}
