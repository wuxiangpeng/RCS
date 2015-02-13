package com.cmcc.rcs.cpm;


import java.util.Collection;
import java.util.Iterator;


public interface CPMStack {
    /**
     * Deprecated. Use {@link #createListeningPoint(String, int, String)}
     * instead.
     */
    ListeningPoint createListeningPoint(int port, String transport)
            throws TransportNotSupportedException, InvalidArgumentException;
    ListeningPoint createListeningPoint(String ipAddress, int port,
            String transport) throws TransportNotSupportedException,
            InvalidArgumentException;
    void deleteListeningPoint(ListeningPoint listeningPoint)
            throws ObjectInUseException;

    CPMProvider createSipProvider(ListeningPoint listeningPoint)
            throws ObjectInUseException;
    void deleteSipProvider(CPMProvider sipProvider) throws ObjectInUseException;

    Collection<Dialog> getDialogs();
    String getIPAddress();
    Iterator<ListeningPointImpl> getListeningPoints();
//    Router getRouter();
    Iterator<CPMProviderImpl> getSipProviders();
    String getStackName();
    
    // Changed by Deutsche Telekom
     int getMtuSize();
     
    /**
     * @deprecated
     * Use {@link ServerTransaction#enableRetransmissionAlerts()} to enable
     * retransmission alerts instead.
     */
    boolean isRetransmissionFilterActive();

    void start() throws ProviderDoesNotExistException, CPMException;
    void stop();
}

