package com.cmcc.rcs.cpm;

import com.cmcc.rcs.cpm.message.Request;



public interface ClientTransaction extends Transaction {
    /**
     * @deprecated
     * For 2xx response, use {@link Dialog.createAck(long)}. The application
     * should not need to handle non-2xx responses.
     */
    Request createAck() throws CPMException;

    Request createCancel() throws CPMException;
    void sendRequest() throws CPMException;

    void alertIfStillInCallingStateBy(int count);

//    Hop getNextHop();

    void setNotifyOnRetransmit(boolean notifyOnRetransmit);
}

