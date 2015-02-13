package com.cmcc.rcs.cpm;

import java.util.TooManyListenersException;

import com.cmcc.rcs.cpm.message.Request;
import com.cmcc.rcs.cpm.message.Response;

public class CPMProviderImpl implements CPMProvider {

    @Override
    public void setListeningPoint(ListeningPoint listeningPoint)
            throws ObjectInUseException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addListeningPoint(ListeningPoint listeningPoint)
            throws ObjectInUseException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeListeningPoint(ListeningPoint listeningPoint)
            throws ObjectInUseException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeListeningPoints() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ListeningPoint getListeningPoint() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListeningPoint getListeningPoint(String transport) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListeningPoint[] getListeningPoints() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addCPMListener(CPMListener sipListener)
            throws TooManyListenersException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeCPMListener(CPMListener sipListener) {
        // TODO Auto-generated method stub
        
    }

//    @Override
//    public CallIdHeader getNewCallId() {
//        // TODO Auto-generated method stub
//        return null;
//    }

    @Override
    public ClientTransaction getNewClientTransaction(Request request)
            throws TransactionUnavailableException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServerTransaction getNewServerTransaction(Request request)
            throws TransactionAlreadyExistsException,
            TransactionUnavailableException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Dialog getNewDialog(Transaction transaction) throws CPMException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAutomaticDialogSupportEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setAutomaticDialogSupportEnabled(boolean flag) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public CPMStack getSipStack() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sendRequest(Request request) throws CPMException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendResponse(Response response) throws CPMException {
        // TODO Auto-generated method stub
        
    }
    
}
