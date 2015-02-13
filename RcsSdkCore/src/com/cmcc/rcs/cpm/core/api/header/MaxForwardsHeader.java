package com.cmcc.rcs.cpm.core.api.header;

import com.cmcc.rcs.core.connect.InvalidArgumentException;



public interface MaxForwardsHeader extends Header {
    String NAME = "Max-Forwards";

    void decrementMaxForwards() throws TooManyHopsException;

    int getMaxForwards();
    void setMaxForwards(int maxForwards) throws InvalidArgumentException;

    boolean hasReachedZero();
}
