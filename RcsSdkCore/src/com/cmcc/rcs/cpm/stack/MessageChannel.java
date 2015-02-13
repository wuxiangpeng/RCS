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
 * Product of NIST/ITL Advanced Networking Technologies Division (ANTD).       *
 ******************************************************************************/

package com.cmcc.rcs.cpm.stack;



import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;

import android.util.Log;

import com.cmcc.rcs.cpm.LogRecord;
import com.cmcc.rcs.cpm.LogRecordFactory;
import com.cmcc.rcs.cpm.SendEventListener;
import com.cmcc.rcs.cpm.core.Host;
import com.cmcc.rcs.cpm.core.HostPort;
import com.cmcc.rcs.cpm.message.CPMData;
import com.cmcc.rcs.cpm.message.CPMStatus;




/**
 * Message channel abstraction for the SIP stack.
 * 
 * @author GaoXusong
 * 
 * @version 1.2 $Revision: 1.28 $ $Date: 2009/11/14 20:06:18 $
 * 
 * 
 */
public abstract class MessageChannel implements SendEventListener {
    private static final String TAG = "MessageChannel";
    
    protected LogRecordFactory logRecordFactory;

    // Incremented whenever a transaction gets assigned
    // to the message channel and decremented when
    // a transaction gets freed from the message channel.
	protected int useCount;
	
	/**
	 * Hook method, overridden by subclasses
	 */
	protected void uncache() {}
	
    /**
     * Message processor to whom I belong (if set).
     */
    protected transient MessageProcessor messageProcessor;

    /**
     * Close the message channel.
     */
    public abstract void close();

    /**
     * Get the SIPStack object from this message channel.
     * 
     * @return SIPStack object of this message channel
     */
    public abstract CPMTransactionStack getSIPStack();

    /**
     * Get transport string of this message channel.
     * 
     * @return Transport string of this message channel.
     */
    public abstract String getTransport();

    /**
     * Get whether this channel is reliable or not.
     * 
     * @return True if reliable, false if not.
     */
    public abstract boolean isReliable();

    /**
     * Return true if this is a secure channel.
     */
    public abstract boolean isSecure();

    /**
     * Send the message (after it has been formatted)
     * 
     * @param sipMessage Message to send.
     */
    public abstract void sendMessage(CPMData cpmData) throws IOException;
    
    /**
     * Send the message (after it has been formatted)
     * 
     * @param sipMessage Message to send.
     */
    public abstract void sendMessageAndWait(CPMData cpmData) throws IOException;
    
     
    /**
     * Get the peer address of the machine that sent us this message.
     * 
     * @return a string contianing the ip address or host name of the sender of the message.
     */
    public abstract String getPeerAddress();

    protected abstract InetAddress getPeerInetAddress();

    protected abstract String getPeerProtocol();

    /**
     * Get the sender port ( the port of the other end that sent me the message).
     */
    public abstract int getPeerPort();

    public abstract int getPeerPacketSourcePort();

    public abstract InetAddress getPeerPacketSourceAddress();

    /**
     * Generate a key which identifies the message channel. This allows us to cache the message
     * channel.
     */
    public abstract String getKey();

    /**
     * Get the host to assign for an outgoing Request via header.
     */
    public abstract String getViaHost();

    /**
     * Get the port to assign for the via header of an outgoing message.
     */
    public abstract int getViaPort();

//    /**
//     * Send the message (after it has been formatted), to a specified address and a specified port
//     * 
//     * @param message Message to send.
//     * @param receiverAddress Address of the receiver.
//     * @param receiverPort Port of the receiver.
//     */
//    protected abstract void sendMessage(byte[] message, InetAddress receiverAddress,
//            int receiverPort, boolean reconnectFlag) throws IOException;

    /**
     * Get the host of this message channel.
     * 
     * @return host of this messsage channel.
     */
    public String getHost() {
        return this.getMessageProcessor().getIpAddress().getHostAddress();
    }

    /**
     * Get port of this message channel.
     * 
     * @return Port of this message channel.
     */
    public int getPort() {
        if (this.messageProcessor != null)
            return messageProcessor.getPort();
        else
            return -1;
    }

//    /**
//     * Send a formatted message to the specified target.
//     * 
//     * @param sipMessage Message to send.
//     * @param hop hop to send it to.
//     * @throws IOException If there is an error sending the message
//     */
//    public void sendMessage(CPMData cpmData, String host, int port) throws IOException {
//        long time = System.currentTimeMillis();
//        InetAddress hopAddr = InetAddress.getByName(host);
//
//        try {
//            
//            byte[] msg = cpmData.encodeAsBytes( cpmData.getMessageBody() );
//            
//            if (cpmData.getSipStatus() == CPMStatus.CPMREQUEST) {
//                this.sendMessage(msg, hopAddr, port, true);       
//            } else if (cpmData.getSipStatus() == CPMStatus.CPMRESPONSE) {
//                this.sendMessage(msg, hopAddr, port, false);
//            }
//            
//
//        } catch (IOException ioe) {
//            throw ioe;
//        } catch (Exception ex) {        	
//        	Log.e(TAG, "Error self routing message cause by: ", ex);
//        	// TODO: When moving to Java 6, use the IOExcpetion(message, exception) constructor
//            throw new IOException("Error self routing message");
//        } finally {              
//            Log.i(TAG, " sendMessage cpmData.encode() = " + cpmData.encode() +
//                  " hopAddr = " + hopAddr +  " port = " + port + " time = " + time  );
//        }
//    }

//    /**
//     * Send a message given SIP message.
//     * 
//     * @param sipMessage is the messge to send.
//     * @param receiverAddress is the address to which we want to send
//     * @param receiverPort is the port to which we want to send
//     */
//    public void sendMessage(CPMData cpmData, InetAddress receiverAddress, int receiverPort)
//            throws IOException {
//        long time = System.currentTimeMillis();
//        byte[] bytes = cpmData.encodeAsBytes( cpmData.getMessageBody() );
//        
//        if (cpmData.getSipStatus() == CPMStatus.CPMREQUEST) {
//            sendMessage(bytes, receiverAddress, receiverPort, true);       
//        } else if (cpmData.getSipStatus() == CPMStatus.CPMRESPONSE) {
//            sendMessage(bytes, receiverAddress, receiverPort, false); 
//        }
//        
//        Log.i(TAG, " cpmData.encode() = " + cpmData.encode() +
//                " receiverAddress = " + receiverAddress + " receiverPort = " +receiverPort
//                + " time = " + time);
//    }
    
    /**
   

    /**
     * Convenience function to get the raw IP source address of a SIP message as a String.
     */
    public String getRawIpSourceAddress() {
        String sourceAddress = getPeerAddress();
        String rawIpSourceAddress = null;
        try {
            InetAddress sourceInetAddress = InetAddress.getByName(sourceAddress);
            rawIpSourceAddress = sourceInetAddress.getHostAddress();
        } catch (Exception ex) {           
            ex.printStackTrace();
        }
        return rawIpSourceAddress;
    }

    /**
     * generate a key given the inet address port and transport.
     */
    public static String getKey(InetAddress inetAddr, int port, String transport) {
        return (transport + ":" + inetAddr.getHostAddress() + ":" + port).toLowerCase();
    }

    /**
     * Generate a key given host and port.
     */
    public static String getKey(HostPort hostPort, String transport) {
        return (transport + ":" + hostPort.getHost().getHostname() + ":" + hostPort.getPort())
                .toLowerCase();
    }

    /**
     * Get the hostport structure of this message channel.
     */
    public HostPort getHostPort() {
        HostPort retval = new HostPort();
        retval.setHost(new Host(this.getHost()));
        retval.setPort(this.getPort());
        return retval;
    }

    /**
     * Get the peer host and port.
     * 
     * @return a HostPort structure for the peer.
     */
    public HostPort getPeerHostPort() {
        HostPort retval = new HostPort();
        retval.setHost(new Host(this.getPeerAddress()));
        retval.setPort(this.getPeerPort());
        return retval;
    } 

    /**
     * Get the via header host:port structure. This is extracted from the topmost via header of
     * the request.
     * 
     * @return a host:port structure
     */
    public HostPort getViaHostPort() {
        HostPort retval = new HostPort();
        retval.setHost(new Host(this.getViaHost()));
        retval.setPort(this.getViaPort());
        return retval;
    }  
    
   
    
    /**
     * Get the message processor.
     */
    public MessageProcessor getMessageProcessor() {
        return this.messageProcessor;
    }
}
