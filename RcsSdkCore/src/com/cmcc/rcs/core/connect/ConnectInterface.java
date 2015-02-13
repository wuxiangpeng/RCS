package com.cmcc.rcs.core.connect;

import java.util.Vector;

import com.cmcc.rcs.cpm.CPMProvider;
import com.cmcc.rcs.cpm.CPMStack;
import com.cmcc.rcs.cpm.core.api.KeepAliveManager;
import com.cmcc.rcs.utils.NetworkRessourceManager;
import com.cmcc.rcs.utils.logger.Logger;



public class ConnectInterface {
    /**
     * Trace separator
     */
    private final static String TRACE_SEPARATOR = "-----------------------------------------------------------------------------";

    /**
     * Default SIP port
     */
    public final static int DEFAULT_SIP_PORT = 5060; 
        
    /**
     * Local IP address
     */
    private String localIpAddress;

    /**
     * Outbound proxy address
     */
    private String outboundProxyAddr;

    /**
     * Outbound proxy port
     */
    private int outboundProxyPort;

    /**
     * Default route path
     */
    private Vector<String> defaultRoutePath;

    /**
     * Service route path
     */
    private Vector<String> serviceRoutePath;

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
     *  List of current SIP transactions
     */
    private SipTransactionList transactions = new SipTransactionList();

    /**
     * SIP interface listeners
     */
    private Vector<SipEventListener> listeners = new Vector<SipEventListener>();

    /**
     * SIP stack
     */
    private CPMStack sipStack;

    /**
     * Default SIP stack provider
     */
    private CPMProvider defaultSipProvider;

    /**
     * SIP stack providers
     */
    private Vector<CPMProvider> sipProviders = new Vector<CPMProvider>();

    /**
     * Keep-alive manager
     */
    private KeepAliveManager keepAliveManager = new KeepAliveManager(this);

    /**
     * Public GRUU
     */
    private String publicGruu = null;

    /**
     * Temporary GRUU
     */
    private String tempGruu = null;

    /**
     * Instance ID
     */
    private String instanceId = null;

    /**
     * Base timer T1 (in ms)
     */
    private int timerT1 = 500;

    /**
     * Base timer T2 (in ms)
     */
    private int timerT2 = 4000;

    /**
     * Base timer T4 (in ms)
     */
    private int timerT4 = 5000;

    /**
     * The logger
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());
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
    public ConnectInterface(String localIpAddress, String proxyAddr,
            int proxyPort, String defaultProtocol, boolean tcpFallback, int networkType) throws ConnectException {
        this.localIpAddress = localIpAddress;
        this.defaultProtocol = defaultProtocol;
        this.tcpFallback = tcpFallback;
        this.listeningPort = NetworkRessourceManager.generateLocalSipPort();
        this.outboundProxyAddr = proxyAddr;
        this.outboundProxyPort = proxyPort;
    }
}
