package com.cmcc.rcs.cpm;

import com.cmcc.rcs.cpm.message.Response;



public interface ServerTransaction extends Transaction {
    void sendResponse(Response response)
            throws CPMException, InvalidArgumentException;

    void enableRetransmissionAlerts() throws CPMException;

    ServerTransaction getCanceledInviteTransaction();
}
