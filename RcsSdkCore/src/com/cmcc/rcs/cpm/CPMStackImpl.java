package com.cmcc.rcs.cpm;



import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Properties;



import android.util.Log;

import com.cmcc.rcs.core.connect.InvalidArgumentException;
import com.cmcc.rcs.core.connect.TransportNotSupportedException;
import com.cmcc.rcs.cpm.stack.CPMTransactionStack;
import com.cmcc.rcs.cpm.stack.MessageProcessor;

/**
 * 
 * @author GaoXusong
 *
 */

public class CPMStackImpl extends CPMTransactionStack  { 
    private static final String TAG = "CPMStackImpl";
    
    private EventScanner eventScanner;
    private SendEventListener sendEventListener;
    private Hashtable<String, ListeningPointImpl> listeningPoints;
    private LinkedList<CPMProviderImpl> cpmProviders;
    
    protected CPMStackImpl() {
        super();
//        NistSipMessageFactoryImpl msgFactory = new NistSipMessageFactoryImpl(
//                this);
        super.setMessageFactory(msgFactory);
        this.eventScanner = new EventScanner(this);
        this.listeningPoints = new Hashtable<String, ListeningPointImpl>();
        this.cpmProviders = new LinkedList<CPMProviderImpl>();
    }
    
    
    /**
     * Constructor for the stack.
     * 
     * @param configurationProperties
     *            -- stack configuration properties including NIST-specific
     *            extensions.
     * @throws PeerUnavailableException
     */
    public CPMStackImpl(String outboundProxy)
            throws PeerUnavailableException {
       this();
       this.outboundProxy = outboundProxy;
    }
       
        
    
    
    
    /*
     * (non-Javadoc)
     * 
     * @see javax2.sip.SipStack#createListeningPoint(java.lang.String, int,
     * java.lang.String)
     */
    public synchronized ListeningPoint createListeningPoint(String address,
            int port, String transport) throws TransportNotSupportedException,
            InvalidArgumentException {
        
        Log.i(TAG,  "createListeningPoint : address = " + address + " port = "
                + port + " transport = " + transport);
       

        if (address == null)
            throw new NullPointerException(
                    "Address for listening point is null!");
        if (transport == null)
            throw new NullPointerException("null transport");
        if (port <= 0)
            throw new InvalidArgumentException("bad port");

        if (!transport.equalsIgnoreCase("UDP")
                && !transport.equalsIgnoreCase("TLS")
                && !transport.equalsIgnoreCase("TCP")
                && !transport.equalsIgnoreCase("SCTP"))
            throw new TransportNotSupportedException("bad transport "
                    + transport);

        /** Reusing an old stack instance */
        if (!this.isAlive()) {
            this.toExit = false;
            this.reInitialize();
        }

        String key = ListeningPointImpl.makeKey(address, port, transport);

        ListeningPointImpl lip = listeningPoints.get(key);
        if (lip != null) {
            return lip;
        } else {
            try {
                InetAddress inetAddr = InetAddress.getByName(address);
                MessageProcessor messageProcessor = this
                        .createMessageProcessor(inetAddr, port, transport);                
                Log.i(TAG, "Created Message Processor: " + address
                                    + " port = " + port + " transport = "
                                    + transport);
                lip = new ListeningPointImpl(this, port, transport);
                lip.messageProcessor = messageProcessor;
                messageProcessor.setListeningPoint(lip);
                this.listeningPoints.put(key, lip);
                // start processing messages.
                messageProcessor.start();
                return (ListeningPoint) lip;
            } catch (java.io.IOException ex) {               
                Log.i(TAG,  "Invalid argument address = " + address + " port = "
                        + port + " transport = " + transport, ex);
                throw new InvalidArgumentException(ex.getMessage(), ex);
            }
        }
    }
    
    /**
     * Get the listener for the stack. A stack can have only one listener. To
     * get an event from a provider, the listener has to be registered with the
     * provider. The SipListener is application code.
     * 
     * @return -- the stack SipListener
     * 
     */
    public SendEventListener getSendEventListener() {
        return this.sendEventListener;
    }
    
    
    public void setSendEventListener(SendEventListener sendEventListener) {
        this.sendEventListener = sendEventListener;
    }
}
