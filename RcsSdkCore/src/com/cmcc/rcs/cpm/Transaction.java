package com.cmcc.rcs.cpm;

import java.io.Serializable;

import com.cmcc.rcs.cpm.message.Request;



public interface Transaction extends Serializable {
    Object getApplicationData();
    void setApplicationData (Object applicationData);

    String getBranchId();
    Dialog getDialog();
    String getHost();
    String getPeerAddress();
    int getPeerPort();
    int getPort();
    Request getRequest();
    CPMProvider getSipProvider();
    TransactionState getState();
    String getTransport();

    int getRetransmitTimer() throws UnsupportedOperationException;
    void setRetransmitTimer(int retransmitTimer)
            throws UnsupportedOperationException;
    void setRetransmitTimers(int timer_T1, int timer_T2, int timer_T4)
            throws UnsupportedOperationException; 	// Bug fix OrangeLabs, JOGUET Benoit

    void terminate() throws ObjectInUseException;
}
