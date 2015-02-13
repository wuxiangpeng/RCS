package com.cmcc.rcs.core.ims.protocol.cpm;

import java.io.File;
import java.util.Properties;
import java.util.Vector;



import com.cmcc.rcs.cpm.CPMListener;
import com.cmcc.rcs.cpm.CPMProvider;
import com.cmcc.rcs.cpm.CPMStack;
import com.cmcc.rcs.cpm.CPMStackImpl;
import com.cmcc.rcs.cpm.ListeningPoint;

import com.cmcc.rcs.utils.NetworkRessourceManager;



import android.net.ConnectivityManager;
import android.util.Log;



public class CPMInterface implements CPMListener {
    
    private static final String TAG = "CPMInterface";
    /**
     * Local IP address
     */
    private String localIpAddress;

    /**
     *  proxy address
     */
    private String outboundProxyAddr;

    /**
     * Outbound proxy port
     */
    private int outboundProxyPort;
    
    /**
     * SIP listening port
     */
    private int listeningPort;

    /**
     * SIP default protocol
     */
    private String defaultProtocol;
    
    /*
     * TCP fallback according to RFC3261 chapter 18.1.1
     */
    private boolean tcpFallback;
    
    /**
     * SIP stack
     */
    private CPMStack cpmStack;

    /**
     * Default SIP stack provider
     */
    private CPMProvider defaultCpmProvider;

    /**
     * SIP stack providers
     */
    private Vector<CPMProvider> cpmProviders = new Vector<CPMProvider>(); 
    
    /**
     * Constructor
     *
     * @param localIpAddress Local IP address
     * @param proxyAddr Outbound proxy address
     * @param proxyPort Outbound proxy port
     * @param defaultProtocol Default protocol
     * @param tcpFallback TCP fallback according to RFC3261 chapter 18.1.1
     * @param networkType Type of network 
     * @throws CPMException
     */
    public CPMInterface(String localIpAddress, String proxyAddr,
            int proxyPort, String defaultProtocol, boolean tcpFallback, int networkType) throws CPMException {
        this.localIpAddress = localIpAddress;
        this.defaultProtocol = defaultProtocol;
        this.tcpFallback = tcpFallback;
        this.listeningPort = NetworkRessourceManager.generateLocalSipPort();
        this.outboundProxyAddr = proxyAddr;
        this.outboundProxyPort = proxyPort;
        try {
            
            // Create the CPM stack
            cpmStack = (CPMStack) new CPMStackImpl(defaultProtocol);  
            
            if (defaultProtocol.equals(ListeningPoint.TCP)) {
                // Create TCP provider
                ListeningPoint tcp = cpmStack.createListeningPoint(localIpAddress, listeningPort, ListeningPoint.TCP);
                CPMProvider tcpCpmProvider = cpmStack.createSipProvider(tcp);
                tcpCpmProvider.addCPMListener(this);
                cpmProviders.addElement(tcpCpmProvider);
    
                // TCP protocol used by default
                defaultCpmProvider = tcpCpmProvider;
            }
            
            Log.i(TAG, "Default SIP provider is " + defaultProtocol);
    
            // Start the stack
            cpmStack.start();

        } catch(Exception e) {            
            Log.e(TAG, "SIP stack initialization has failed", e);
            throw new CPMException("Can't create the SIP stack");
        }
 
      
        Log.i(TAG, "SIP stack started at " + localIpAddress + ":" + listeningPort);
    }

        @Override
        public void processDialogTerminated(
                DialogTerminatedEvent dialogTerminatedEvent) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void processIOException(IOExceptionEvent exceptionEvent) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void processRequest(RequestEvent requestEvent) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void processResponse(ResponseEvent responseEvent) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void processTimeout(TimeoutEvent timeoutEvent) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void processTransactionTerminated(
                TransactionTerminatedEvent transactionTerminatedEvent) {
            // TODO Auto-generated method stub
            
        }
    
}
