package com.cmcc.rcs.cpm;

import java.util.TooManyListenersException;

import com.cmcc.rcs.cpm.message.Request;
import com.cmcc.rcs.cpm.message.Response;



public interface CPMProvider {
    /**
     * @deprecated
     * @see #addListeningPoint(ListeningPoint)
     */
    void setListeningPoint(ListeningPoint listeningPoint)
            throws ObjectInUseException;
    void addListeningPoint(ListeningPoint listeningPoint)
            throws ObjectInUseException;
    void removeListeningPoint(ListeningPoint listeningPoint)
            throws ObjectInUseException;
    void removeListeningPoints();

    /**
     * @deprecated
     * @see #getListeningPoints()
     */
    ListeningPoint getListeningPoint();
    ListeningPoint getListeningPoint(String transport);
    ListeningPoint[] getListeningPoints();

    void addCPMListener(CPMListener sipListener)
            throws TooManyListenersException;
    void removeCPMListener(CPMListener sipListener);

//    CallIdHeader getNewCallId();

    ClientTransaction getNewClientTransaction(Request request)
            throws TransactionUnavailableException;
    ServerTransaction getNewServerTransaction(Request request)
            throws TransactionAlreadyExistsException,
            TransactionUnavailableException;

    Dialog getNewDialog(Transaction transaction) throws CPMException;

    boolean isAutomaticDialogSupportEnabled();
    void setAutomaticDialogSupportEnabled(boolean flag);

    CPMStack getSipStack();

    void sendRequest(Request request) throws CPMException;
    void sendResponse(Response response) throws CPMException;
}

