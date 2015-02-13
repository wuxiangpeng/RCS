package com.cmcc.rcs.cpm.core.api.header;

import com.cmcc.rcs.cpm.core.api.address.URI;




public interface CallInfoHeader extends Header, Parameters {
    String NAME = "Call-Info";

    URI getInfo();
    void setInfo(URI info);

    String getPurpose();
    void setPurpose(String purpose);
}
