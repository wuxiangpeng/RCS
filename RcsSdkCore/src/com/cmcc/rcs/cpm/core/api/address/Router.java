package com.cmcc.rcs.cpm.core.api.address;

import java.util.ListIterator;

import com.cmcc.rcs.cpm.core.api.SipException;
import com.cmcc.rcs.cpm.message.Request;


public interface Router {
    Hop getNextHop(Request request) throws SipException;
    ListIterator getNextHops(Request request);
    Hop getOutboundProxy();
}

