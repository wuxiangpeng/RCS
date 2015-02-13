package com.cmcc.rcs.cpm.core.api.header;

import com.cmcc.rcs.core.connect.InvalidArgumentException;


public interface RSeqHeader extends Header {
    String NAME = "RSeq";

    long getSeqNumber();
    void setSeqNumber(long sequenceNumber) throws InvalidArgumentException;

    /**
     * @deprecated
     * @see #getSeqNumber()
     */
    int getSequenceNumber();

    /**
     * @deprecated
     * @see #setSeqNumber(long)
     */
    void setSequenceNumber(int sequenceNumber) throws InvalidArgumentException;
}
