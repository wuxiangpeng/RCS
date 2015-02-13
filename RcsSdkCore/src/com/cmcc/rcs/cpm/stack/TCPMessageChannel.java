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
 * .
 * 
 */
/******************************************************************************
 * Product of NIST/ITL Advanced Networking Technologies Division (ANTD).      *
 ******************************************************************************/
package com.cmcc.rcs.cpm.stack;





import java.net.*;
import java.io.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.TimerTask;



import android.util.Log;

import com.cmcc.rcs.cpm.SendEventListener;
import com.cmcc.rcs.cpm.SendEventScanner;
import com.cmcc.rcs.cpm.message.CPMData;
import com.cmcc.rcs.cpm.message.CPMSendType;
import com.cmcc.rcs.cpm.message.CPMStatus;
import com.cmcc.rcs.cpm.parser.CPMMessageListener;
import com.cmcc.rcs.cpm.parser.Pipeline;
import com.cmcc.rcs.cpm.parser.PipelinedMsgParser;



/*
 * Ahmet Uyar <auyar@csit.fsu.edu>sent in a bug report for TCP operation of the JAIN sipStack.
 * Niklas Uhrberg suggested that a mechanism be added to limit the number of simultaneous open
 * connections. The TLS Adaptations were contributed by Daniel Martinez. Hagai Sela contributed a
 * bug fix for symmetric nat. Jeroen van Bemmel added compensation for buggy clients ( Microsoft
 * RTC clients ). Bug fixes by viswashanti.kadiyala@antepo.com, Joost Yervante Damand
 */

/**
 * This is a stack abstraction for TCP connections. This abstracts a stream of parsed messages.
 * The SIP sipStack starts this from the main SIPStack class for each connection that it accepts.
 * It starts a message parser in its own thread and talks to the message parser via a pipe. The
 * message parser calls back via the parseError or processMessage functions that are defined as
 * part of the SIPMessageListener interface.
 * 
 * @see
 * 
 * 
 * @author GaoXusong
 * 
 * 
 */
public class TCPMessageChannel extends MessageChannel implements Runnable, CPMMessageListener  {
    
    private static final String TAG = "TCPMessageChannel";
    
    private static final int SLEEP_TIMER = 200;
    
    private Socket mySock;

    private CPMData mCpmData;
    
    private boolean allowSending = false;
    
    private PipelinedMsgParser myParser;

    protected InputStream myClientInputStream; // just to pass to thread.

    protected OutputStream myClientOutputStream;

    protected String key;

    protected boolean isCached;

    protected boolean isRunning;

    private Thread mythread;

    protected CPMTransactionStack sipStack;

    protected String myAddress;

    protected int myPort;

    protected InetAddress peerAddress;

    protected int peerPort;

    protected String peerProtocol;
    
    

    // Incremented whenever a transaction gets assigned
    // to the message channel and decremented when
    // a transaction gets freed from the message channel.
    // protected int useCount;

    private TCPMessageProcessor tcpMessageProcessor;
    
    private HashMap<String, CPMData> cpmDatas = new HashMap<String, CPMData>();

    protected TCPMessageChannel(CPMTransactionStack sipStack) {
        this.sipStack = sipStack;
        this.mCpmData = null;       
    }

    /**
     * Constructor - gets called from the SIPStack class with a socket on accepting a new client.
     * All the processing of the message is done here with the sipStack being freed up to handle
     * new connections. The sock input is the socket that is returned from the accept. Global data
     * that is shared by all threads is accessible in the Server structure.
     * 
     * @param sock Socket from which to read and write messages. The socket is already connected
     *        (was created as a result of an accept).
     * 
     * @param sipStack Ptr to SIP Stack
     */

    protected TCPMessageChannel(Socket sock, CPMTransactionStack sipStack,
            TCPMessageProcessor msgProcessor) throws IOException {     
        mySock = sock;
        peerAddress = mySock.getInetAddress();
        myAddress = msgProcessor.getIpAddress().getHostAddress();
        myClientInputStream = mySock.getInputStream();
        myClientOutputStream = mySock.getOutputStream();
        mythread = new Thread(this);
        mythread.setDaemon(true);
        mythread.setName("TCPMessageChannelThread");
        // Stash away a pointer to our sipStack structure.
        this.sipStack = sipStack;
        this.peerPort = mySock.getPort();       
        this.tcpMessageProcessor = msgProcessor;
        this.myPort = this.tcpMessageProcessor.getPort();
        this.mCpmData = null;       
        // Bug report by Vishwashanti Raj Kadiayl
        super.messageProcessor = msgProcessor;
        // Can drop this after response is sent potentially.
        mythread.start();
    }

    /**
     * Constructor - connects to the given inet address. Acknowledgement -- Lamine Brahimi (IBM
     * Zurich) sent in a bug fix for this method. A thread was being uncessarily created.
     * 
     * @param inetAddr inet address to connect to.
     * @param sipStack is the sip sipStack from which we are created.
     * @throws IOException if we cannot connect.
     */
    protected TCPMessageChannel(InetAddress inetAddr, int port, CPMTransactionStack sipStack,
            TCPMessageProcessor messageProcessor) throws IOException {    
        this.peerAddress = inetAddr;
        this.peerPort = port;
        this.myPort = messageProcessor.getPort();
        this.peerProtocol = "TCP";
        this.sipStack = sipStack;
        this.tcpMessageProcessor = messageProcessor;
        this.myAddress = messageProcessor.getIpAddress().getHostAddress();
        // Bug report by Vishwashanti Raj Kadiayl
        this.key = MessageChannel.getKey(peerAddress, peerPort, "TCP");
        this.mCpmData = null;       
        super.messageProcessor = messageProcessor;

    }

    /**
     * Returns "true" as this is a reliable transport.
     */
    public boolean isReliable() {
        return true;
    }

    /**
     * Close the message channel.
     */
    public void close() {
        try {
            if (mySock != null) {
                mySock.close();
                mySock = null;
            }           
        } catch (IOException ex) {
             ex.printStackTrace();
        }
    }

    /**
     * Get my SIP Stack.
     * 
     * @return The SIP Stack for this message channel.
     */
    public CPMTransactionStack getSIPStack() {
        return sipStack;
    }

    /**
     * get the transport string.
     * 
     * @return "tcp" in this case.
     */
    public String getTransport() {
        return "TCP";
    }

    /**
     * get the address of the client that sent the data to us.
     * 
     * @return Address of the client that sent us data that resulted in this channel being
     *         created.
     */
    public String getPeerAddress() {
        if (peerAddress != null) {
            return peerAddress.getHostAddress();
        } else
            return getHost();
    }

    protected InetAddress getPeerInetAddress() {
        return peerAddress;
    }

    public String getPeerProtocol() {
        return this.peerProtocol;
    }

    /**
     * Send message to whoever is connected to us. Uses the topmost via address to send to.
     * 
     * @param msg is the message to send.
     * @param retry
     */
    private void sendMessage(byte[] msg, String host, String port, boolean retry) throws IOException {

        /*
         * Patch from kircuv@dev.java.net (Issue 119 ) This patch avoids the case where two
         * TCPMessageChannels are now pointing to the same socket.getInputStream().
         * 
         * JvB 22/5 removed
         */
       // Socket s = this.sipStack.ioHandler.getSocket(IOHandler.makeKey(
       // this.peerAddress, this.peerPort));
        
        InetAddress hopAddr = InetAddress.getByName(host);
        Socket sock = this.sipStack.ioHandler.sendBytes(this.messageProcessor.getIpAddress(),
                hopAddr, Integer.parseInt(port), this.peerProtocol, msg, retry, this);

        // Created a new socket so close the old one and stick the new
        // one in its place but dont do this if it is a datagram socket.
        // (could have replied via udp but received via tcp!).
        // if (mySock == null && s != null) {
        // this.uncache();
        // } else
        if (sock != mySock && sock != null) {
            try {
                if (mySock != null)
                    mySock.close();
            } catch (IOException ex) {
            }
            mySock = sock;
            this.myClientInputStream = mySock.getInputStream();
            this.myClientOutputStream = mySock.getOutputStream();
            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.setName("TCPMessageChannelThread");
            thread.start();            
            
        }

    }


//    /**
//     * Send a message to a specified address.
//     * 
//     * @param message Pre-formatted message to send.
//     * @param receiverAddress Address to send it to.
//     * @param receiverPort Receiver port.
//     * @throws IOException If there is a problem connecting or sending.
//     */
//    public void sendMessage(byte message[], InetAddress receiverAddress, int receiverPort,
//            boolean retry) throws IOException {
//        if (message == null || receiverAddress == null)
//            throw new IllegalArgumentException("Null argument");
//        
//         Socket sock = this.sipStack.ioHandler.sendBytes(this.messageProcessor.getIpAddress(),
//                receiverAddress, receiverPort, "TCP", message, retry, this);
//         
//        if (sock != mySock && sock != null) {
//            if (mySock != null) {
//                /*
//                 * Delay the close of the socket for some time in case it is being used.
//                 */
//                sipStack.getTimer().schedule(new TimerTask() {
//                    @Override
//                    public boolean cancel() {
//                        try {
//                            mySock.close();
//                            super.cancel();
//                        } catch (IOException ex) {
//
//                        }
//                        return true;
//                    }
//
//                    @Override
//                    public void run() {
//                        try {
//                            mySock.close();
//                        } catch (IOException ex) {
//
//                        }
//                    }
//                }, 8000);
//            }
//
//            mySock = sock;
//            this.myClientInputStream = mySock.getInputStream();
//            this.myClientOutputStream = mySock.getOutputStream();
//            // start a new reader on this end of the pipe.
//            Thread mythread = new Thread(this);
//            mythread.setDaemon(true);
//            mythread.setName("TCPMessageChannelThread");
//            mythread.start();
//            
//            SendEventScanner sendEventScanner = new SendEventScanner(this);
//            
//        }
//    }
   
    /**
     * This gets invoked when thread.start is called from the constructor. Implements a message
     * loop - reading the tcp connection and processing messages until we are done or the other
     * end has closed.
     */
    public void run() {
        Pipeline hispipe = null;
        // Create a pipeline to connect to our message parser.
        hispipe = new Pipeline(myClientInputStream, sipStack.readTimeout, 
                sipStack.getTimer() );
        // Create a pipelined message parser to read and parse
        // messages that we write out to him.
        myParser = new PipelinedMsgParser(this, hispipe, this.sipStack.getMaxMessageSize());
        // Start running the parser thread.
        myParser.processInput();
        // bug fix by Emmanuel Proulx
        int bufferSize = 4096;
        this.tcpMessageProcessor.useCount++;
        this.isRunning = true;
        try {
            while (true) {
                try {
                    byte[] msg = new byte[bufferSize];
                    int nbytes = myClientInputStream.read(msg, 0, bufferSize);
                    // no more bytes to read...
                    if (nbytes == -1) {
                        hispipe.write("\r\n\r\n".getBytes("UTF-8"));
                        try {
                            if (sipStack.maxConnections != -1) {
                                synchronized (tcpMessageProcessor) {
                                    tcpMessageProcessor.nConnections--;
                                    tcpMessageProcessor.notify();
                                }
                            }
                            hispipe.close();
                            mySock.close();
                        } catch (IOException ioex) {
                        }
                        return;
                    }
                    hispipe.write(msg, 0, nbytes);

                } catch (IOException ex) {
                    // Terminate the message.
                    try {
                        hispipe.write("\r\n\r\n".getBytes("UTF-8"));
                    } catch (Exception e) {
                        // InternalErrorHandler.handleException(e);
                    }

                    try {                     
                        try {
                            if (sipStack.maxConnections != -1) {
                                synchronized (tcpMessageProcessor) {
                                    tcpMessageProcessor.nConnections--;
                                    // System.out.println("Notifying!");
                                    tcpMessageProcessor.notify();
                                }
                            }
                            mySock.close();
                            hispipe.close();
                        } catch (IOException ioex) {
                        }
                    } catch (Exception ex1) {
                        // Do nothing.
                    }
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            this.isRunning = false;
            this.tcpMessageProcessor.remove(this);
            this.tcpMessageProcessor.useCount--;
            myParser.close();
        }

    }

    protected void uncache() {
    	if (isCached && !isRunning) {
    		this.tcpMessageProcessor.remove(this);
    	}
    }

    /**
     * Equals predicate.
     * 
     * @param other is the other object to compare ourselves to for equals
     */

    public boolean equals(Object other) {

        if (!this.getClass().equals(other.getClass()))
            return false;
        else {
            TCPMessageChannel that = (TCPMessageChannel) other;
            if (this.mySock != that.mySock)
                return false;
            else
                return true;
        }
    }
    
    
    /**
     * Wait the response of a request until mCpmData has value
     * 
     * 
     */
    public void waitResponse( ) {        
        try {
            
            while ( mCpmData == null ) {
                super.wait(SLEEP_TIMER);
            }
            
            while ( mCpmData != null ) {
                if (   ( mCpmData.getMessageBody() != null )
                    && ( mCpmData.getHost() != null)
                    && ( mCpmData.getPort() != null )) {
                    return;
                } else {
                    super.wait(SLEEP_TIMER);
                }
            }           
        } catch(InterruptedException e) {
            // Thread has been interrupted
            mCpmData = null;
        }
    }

    /**
     * Get an identifying key. This key is used to cache the connection and re-use it if
     * necessary.
     */
    public String getKey() {
        if (this.key != null) {
            return this.key;
        } else {
            this.key = MessageChannel.getKey(this.peerAddress, this.peerPort, "TCP");
            return this.key;
        }
    }

    /**
     * Get the host to assign to outgoing messages.
     * 
     * @return the host to assign to the via header.
     */
    public String getViaHost() {
        return myAddress;
    }

    /**
     * Get the port for outgoing messages sent from the channel.
     * 
     * @return the port to assign to the via header.
     */
    public int getViaPort() {
        return myPort;
    }

    /**
     * Get the port of the peer to whom we are sending messages.
     * 
     * @return the peer port.
     */
    public int getPeerPort() {
        return peerPort;
    }

    public int getPeerPacketSourcePort() {
        return this.peerPort;
    }

    public InetAddress getPeerPacketSourceAddress() {
        return this.peerAddress;
    }

    /**
     * TCP Is not a secure protocol.
     */
    public boolean isSecure() {
        return false;
    }
    
    /**
     * Return a formatted message to the client. We try to re-connect with the peer on the other
     * end if possible.
     * 
     * @param sipMessage Message to send.
     * @throws IOException If there is an error sending the message
     */
    @Override
    public void sendMessage(CPMData cpmData) throws IOException {        
        byte[] msg = cpmData.encodeAsBytes(cpmData.getMessageBody());

        long time = System.currentTimeMillis();

        // JvB: also retry for responses, if the connection is gone we should
        // try to reconnect
        this.sendMessage(msg, cpmData.getHost(), cpmData.getPort(), true);
  
        Log.i(TAG, " cpmData.getMessageBody() = " + cpmData.getMessageBody()
                + " peerAddress = " +  peerAddress 
                + " peerPort = " + peerPort
                +  " time = " + time);
        
    }

    @Override
    public void processSendEvent(String callId, String messageBody,
            String host, String port) {
        
        //Mainly used to judge whether to allow sending message
        allowSending = false;
        
        if ( ( callId != null )
          && ( messageBody != null)
          && ( host != null )
          && ( port != null ) ) {
            return;
        }
        
        if ( mCpmData == null ) {
            mCpmData = cpmDatas.get(callId);
        } else {
            String oldCallId = mCpmData.createOrobtainCallId();
            
            if (oldCallId == callId) {
                return;
            }
            
            mCpmData = cpmDatas.get(callId);
        }
        
        try {
            allowSending = true;
            mCpmData.setMessageBody(messageBody);
            mCpmData.setHost(host);
            mCpmData.setPort(port);         
            peerAddress = InetAddress.getByName(host);
            peerPort = Integer.parseInt(port);
        } catch (UnknownHostException e) {           
            e.printStackTrace();
        }
        
        cpmDatas.remove(callId);
    }

    @Override
    public void sendMessageAndWait(CPMData cpmData) throws IOException {
        
        if (cpmData == null) {
            return;
        }
        
        mCpmData = null;
        cpmDatas.put(cpmData.createOrobtainCallId(), cpmData);
        
        SendEventScanner.getInstance(this);
        
        switch ( cpmData.getSendType() ) {
            case ONE_ONE_CHAT:
                //TanLinLin
                break;
            default:
                break;
            
        }        

        this.waitResponse();
        this.sendMessage(mCpmData);
        
    }

    @Override
    public void handleException(ParseException ex, CPMData cpmData,
            Class headerClass, String headerText, String messageText)
            throws ParseException {
        
        
    }

    @Override
    public void processMessage(CPMData cpmData) throws Exception {
        
        if (cpmData.getSipStatus() == CPMStatus.CPMREQUEST) {
         
            this.peerAddress = mySock.getInetAddress();
            // Use this for outgoing messages as well.
            if (!this.isCached) {
                ((TCPMessageProcessor) this.messageProcessor).cacheMessageChannel(this);
                this.isCached = true;
                int remotePort = ((java.net.InetSocketAddress) mySock.getRemoteSocketAddress()).getPort();
                String key = IOHandler.makeKey(mySock.getInetAddress(), remotePort);
                sipStack.ioHandler.putSocket(key, mySock);
            }
        }
        
    }

   
}
