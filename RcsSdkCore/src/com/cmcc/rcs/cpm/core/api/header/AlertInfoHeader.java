package com.cmcc.rcs.cpm.core.api.header;

import com.cmcc.rcs.cpm.core.api.address.URI;




public interface AlertInfoHeader extends Header, Parameters {
    String NAME = "Alert-Info";

    URI getAlertInfo();
    void setAlertInfo(URI alertInfo);
    void setAlertInfo(String alertInfo);
}
