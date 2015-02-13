package com.cmcc.rcs.cpm.core.api.header;

import com.cmcc.rcs.core.connect.InvalidArgumentException;


public interface MimeVersionHeader extends Header {
    String NAME = "MIME-Version";

    int getMajorVersion();
    void setMajorVersion(int majorVersion) throws InvalidArgumentException;

    int getMinorVersion();
    void setMinorVersion(int minorVersion) throws InvalidArgumentException;
}
