package com.cmcc.rcs.cpm.core.api.header;

import com.cmcc.rcs.core.connect.InvalidArgumentException;



public interface AcceptEncodingHeader extends Encoding, Header, Parameters {
    String NAME = "Accept-Encoding";

    float getQValue();
    void setQValue(float qValue) throws InvalidArgumentException;
}
