package com.cmcc.rcs.cpm.core.api.header;

import com.cmcc.rcs.core.connect.InvalidArgumentException;



public interface ExpiresHeader extends Header {
    String NAME = "Expires";

    int getExpires();
    void setExpires(int expires) throws InvalidArgumentException;
}
