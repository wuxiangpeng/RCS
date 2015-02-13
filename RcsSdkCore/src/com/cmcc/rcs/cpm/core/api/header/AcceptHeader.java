package com.cmcc.rcs.cpm.core.api.header;

import com.cmcc.rcs.core.connect.InvalidArgumentException;




public interface AcceptHeader extends Header, MediaType, Parameters {
    String NAME = "Accept";

    boolean allowsAllContentSubTypes();
    boolean allowsAllContentTypes();

    float getQValue();
    void setQValue(float qValue) throws InvalidArgumentException;
    boolean hasQValue();
    void removeQValue();
}
