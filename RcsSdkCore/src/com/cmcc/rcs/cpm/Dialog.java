package com.cmcc.rcs.cpm;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Iterator;

import com.cmcc.rcs.cpm.message.Request;
import com.cmcc.rcs.cpm.message.Response;



public interface Dialog extends Serializable {
    Object getApplicationData();
    void setApplicationData(Object applicationData);

//    CallIdHeader getCallId();
    String getDialogId();

    /**
     * @deprecated
     */
    Transaction getFirstTransaction();

//    Address getLocalParty();

    /**
     * @deprecated
     * @see #getLocalSeqNumber()
     */
    int getLocalSequenceNumber();

    long getLocalSeqNumber();

    String getLocalTag();

//    Address getRemoteParty();

    /**
     * @deprecated
     * @see #getRemoteSeqNumber()
     */
    int getRemoteSequenceNumber();

    long getRemoteSeqNumber();

    String getRemoteTag();

//    Address getRemoteTarget();

    Iterator getRouteSet();

    CPMProvider getSipProvider();

    DialogState getState();

    boolean isSecure();

    boolean isServer();

    void delete();

    void incrementLocalSequenceNumber();

    Request createRequest(String method) throws CPMException;
    Request createAck(long cseq) throws InvalidArgumentException, CPMException;
    Request createPrack(Response relResponse)
            throws DialogDoesNotExistException, CPMException;
    Response createReliableProvisionalResponse(int statusCode)
            throws InvalidArgumentException, CPMException;


    void sendRequest(ClientTransaction clientTransaction)
            throws TransactionDoesNotExistException, CPMException;
    void sendAck(Request ackRequest) throws CPMException;
    void sendReliableProvisionalResponse(Response relResponse)
            throws CPMException;

    void setBackToBackUserAgent();

    void terminateOnBye(boolean terminateFlag) throws CPMException;
}
