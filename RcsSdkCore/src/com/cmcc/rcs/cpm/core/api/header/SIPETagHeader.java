package com.cmcc.rcs.cpm.core.api.header;

import java.text.ParseException;

public interface SIPETagHeader extends ExtensionHeader {
    String NAME = "SIP-ETag";

    String getETag();
    void setETag(String etag) throws ParseException;
}
