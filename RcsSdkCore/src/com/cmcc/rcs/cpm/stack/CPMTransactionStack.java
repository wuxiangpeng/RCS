/*
 * Conditions Of Use
 *
 * This software was developed by employees of the National Institute of
 * Standards and Technology (NIST), an agency of the Federal Government.
 * Pursuant to title 15 Untied States Code Section 105, works of NIST
 * employees are not subject to copyright protection in the United States
 * and are considered to be in the public domain.  As a result, a formal
 * license is not needed to use the software.
 *
 * This software is provided by NIST as a service and is expressly
 * provided "AS IS."  NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED
 * OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT
 * AND DATA ACCURACY.  NIST does not warrant or make any representations
 * regarding the use of the software or the results thereof, including but
 * not limited to the correctness, accuracy, reliability or usefulness of
 * the software.
 *
 * Permission to use this software is contingent upon your acceptance
 * of the terms of this agreement
 *
 * 
 *
 */
package com.cmcc.rcs.cpm.stack;



import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Timer;

import com.cmcc.rcs.cpm.core.ThreadAuditor;
import com.cmcc.rcs.cpm.core.net.DefaultNetworkLayer;
import com.cmcc.rcs.cpm.core.net.NetworkLayer;

/****
 * 
 * @author GaoXusong
 *
 */


public abstract class CPMTransactionStack {
    /*
     * Number of milliseconds between timer ticks (500).
     */
    public static final int BASE_TIMER_INTERVAL = 500;

    /*
     * Connection linger time (seconds) this is the time (in seconds) for which we linger the TCP
     * connection before closing it.
     */
    public static final int CONNECTION_LINGER_TIME = 8;
    
    // Global timer. Use this for all timer tasks.

    private Timer timer;
    
    /*
     * Flag that indicates that the stack is active.
     */
    protected boolean toExit;

    /*
     * Name of the stack.
     */
    protected String stackName;

    /*
     * IP address of stack -- this can be re-written by stun.
     *
     * @deprecated
     */
    protected String stackAddress;

    /*
     * INET address of stack (cached to avoid repeated lookup)
     *
     * @deprecated
     */
    protected InetAddress stackInetAddress;

    // Changed by Deutsche Telekom
    /**
     * Default SIP port
     */
    public final static int DEFAULT_MTU_SIZE = 1500;

    // Changed by Deutsche Telekom
    /**
     * MTU size
     */
    private int mtuSize = DEFAULT_MTU_SIZE;
    
    // The set of events for which subscriptions can be forked.

    protected HashSet<String> forkedEvents;
    
    /*
     * Outbound proxy String ( to be handed to the outbound proxy class on creation).
     */
    protected String outboundProxy;
    
    /*
     * Number of pre-allocated threads for processing udp messages. -1 means no preallocated
     * threads ( dynamically allocated threads).
     */
    protected int threadPoolSize;

    /*
     * max number of simultaneous connections.
     */
    protected int maxConnections;
    
    /*
     * Max size of message that can be read from a TCP connection.
     */
    protected int maxContentLength;

    /*
     * Max # of headers that a SIP message can contain.
     */
    protected int maxMessageSize;

    /*
     * A collection of message processors.
     */
    private Collection<MessageProcessor> messageProcessors;

    /*
     * Read timeout on TCP incoming sockets -- defines the time between reads for after delivery
     * of first byte of message.
     */
    protected int readTimeout;

    /*
     * Close accept socket on completion.
     */
    protected boolean cacheServerConnections;

    /*
     * Close connect socket on Tx termination.
     */
    protected boolean cacheClientConnections;
    
    // / Provides a mechanism for applications to check the health of threads in
    // the stack
    protected ThreadAuditor threadAuditor = new ThreadAuditor();
        
    /*
     * Class that handles caching of TCP/TLS connections.
     */
    protected IOHandler ioHandler;
    
    
    /*
     * The socket factory. Can be overriden by applications that want direct access to the
     * underlying socket.
     */

    protected NetworkLayer networkLayer;
    
    // / Timer to regularly ping the thread auditor (on behalf of the timer
    // thread)
    class PingTimer extends CPMStackTimerTask {
        // / Timer thread handle
        ThreadAuditor.ThreadHandle threadHandle;

        // / Constructor
        public PingTimer(ThreadAuditor.ThreadHandle a_oThreadHandle) {
            threadHandle = a_oThreadHandle;
        }

        protected void runTask() {
            // Check if we still have a timer (it may be null after shutdown)
            if (getTimer() != null) {
                // Register the timer task if we haven't done so
                if (threadHandle == null) {
                    // This happens only once since the thread handle is passed
                    // to the next scheduled ping timer
                    threadHandle = getThreadAuditor().addCurrentThread();
                }

                // Let the thread auditor know that the timer task is alive
                threadHandle.ping();

                // Schedule the next ping
                getTimer().schedule(new PingTimer(threadHandle),
                        threadHandle.getPingIntervalInMillisecs());
            }
        }
    }
    
    /**
     * Default constructor.
     */
    protected CPMTransactionStack() {
        this.toExit = false;
        this.forkedEvents = new HashSet<String>();
        // set of events for which subscriptions can be forked.
        // Set an infinite thread pool size.
        this.threadPoolSize = -1;
        // Close response socket after infinte time.
        // for max performance
        this.cacheServerConnections = true;
        // Close the request socket after infinite time.
        // for max performance
        this.cacheClientConnections = true;
        // Max number of simultaneous connections.
        this.maxConnections = -1;
        // Array of message processors.
        messageProcessors = new ArrayList<MessageProcessor>();       
        // The read time out is infinite.
        this.readTimeout = -1;
     
        // Notify may or may not create a dialog. This is handled in
        // the code.
        // Create the transaction collections
    
        // Start the timer event thread.

        this.timer = new Timer();   
        
        this.maxMessageSize = 4096;
        
        if (getThreadAuditor().isEnabled()) {
            // Start monitoring the timer thread
            timer.schedule(new PingTimer(null), 0);
        }
    }
    
    /**
     * Maximum size of a single TCP message. Limiting the size of a single TCP message prevents
     * flooding attacks.
     *
     * @return the size of a single TCP message.
     */
    public int getMaxMessageSize() {
        return this.maxMessageSize;
    }
    /**
     * Creates the equivalent of a JAIN listening point and attaches to the stack.
     *
     * @param ipAddress -- ip address for the listening point.
     * @param port -- port for the listening point.
     * @param transport -- transport for the listening point.
     */
    protected MessageProcessor createMessageProcessor(InetAddress ipAddress, int port,
            String transport) throws java.io.IOException {
        if (transport.equalsIgnoreCase("udp")) {
             return null;
        } else if ( transport.equalsIgnoreCase("tcp") ) {
            TCPMessageProcessor tcpMessageProcessor = new TCPMessageProcessor(ipAddress, this,
                    port);
            this.addMessageProcessor(tcpMessageProcessor);
            // this.tcpFlag = true;
            return tcpMessageProcessor;
        } else if (transport.equalsIgnoreCase("tls")) {
            return null;
        } else if (transport.equalsIgnoreCase("sctp")) {            
           return null;
        } else {
            throw new IllegalArgumentException("bad transport");
        }
     }
    
    /**
     * Adds a new MessageProcessor to the list of running processors for this SIPStack and starts
     * it. You can use this method for dynamic stack configuration.
     */
    protected void addMessageProcessor(MessageProcessor newMessageProcessor) throws IOException {
        synchronized (messageProcessors) {
            // Suggested changes by Jeyashankher, jai@lucent.com
            // newMessageProcessor.start() can fail
            // because a local port is not available
            // This throws an IOException.
            // We should not add the message processor to the
            // local list of processors unless the start()
            // call is successful.
            // newMessageProcessor.start();
            messageProcessors.add(newMessageProcessor);

        }
    }
    
    /**
     * @param timer the timer to set
     */
    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    /**
     * @return the timer
     */
    public Timer getTimer() {
        return timer;
    }
    
    /**
     * get the thread auditor object
     *
     * @return -- the thread auditor of the stack
     */
    public ThreadAuditor getThreadAuditor() {
        return this.threadAuditor;
    }
    
    
    /**
     * Return the network layer (i.e. the interface for socket creation or the socket factory for
     * the stack).
     *
     * @return -- the registered Network Layer.
     */
    public NetworkLayer getNetworkLayer() {
        if (networkLayer == null) {
            return DefaultNetworkLayer.SINGLETON;
        } else {
            return networkLayer;
        }
    }
    
    public Socket createSocket(InetAddress address, int port)
            throws IOException {
        return new Socket(address, port);
    }
    
    public Socket createSocket(InetAddress address, int port,
            InetAddress myAddress) throws IOException {
        if (myAddress != null)
            return new Socket(address, port, myAddress, 0);
        else
            return new Socket(address, port);
    }

    /**
     * Creates a new Socket, binds it to myAddress:myPort and connects it to
     * address:port.
     *
     * @param address the InetAddress that we'd like to connect to.
     * @param port the port that we'd like to connect to
     * @param myAddress the address that we are supposed to bind on or null
     *        for the "any" address.
     * @param myPort the port that we are supposed to bind on or 0 for a random
     * one.
     *
     * @return a new Socket, bound on myAddress:myPort and connected to
     * address:port.
     * @throws IOException if binding or connecting the socket fail for a reason
     * (exception relayed from the correspoonding Socket methods)
     */
    public Socket createSocket(InetAddress address, int port,
                    InetAddress myAddress, int myPort)
        throws IOException
    {
        if (myAddress != null)
            return new Socket(address, port, myAddress, myPort);
        else if (port != 0)     {
            //myAddress is null (i.e. any)  but we have a port number
            Socket sock = new Socket();
            sock.bind(new InetSocketAddress(port));
            sock.connect(new InetSocketAddress(address, port));
            return sock;
        }
        else
            return new Socket(address, port);
    }
    
    public ServerSocket createServerSocket(int port, int backlog,
            InetAddress bindAddress) throws IOException {
        return new ServerSocket(port, backlog, bindAddress);
    }
}
