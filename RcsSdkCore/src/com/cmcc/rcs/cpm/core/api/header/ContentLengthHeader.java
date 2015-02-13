package com.cmcc.rcs.cpm.core.api.header;

import com.cmcc.rcs.core.connect.InvalidArgumentException;





public interface ContentLengthHeader extends Header {
    String NAME = "Content-Length";

    int getContentLength();
    void setContentLength(int contentLength) throws InvalidArgumentException;
}
