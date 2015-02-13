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
package com.cmcc.rcs.cpm;

import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;

import com.cmcc.rcs.cpm.core.Host;
import com.cmcc.rcs.cpm.core.HostPort;
import com.cmcc.rcs.cpm.message.CPMData;
import com.cmcc.rcs.cpm.stack.MessageChannel;
import com.cmcc.rcs.cpm.stack.MessageProcessor;



/**
 * Implementation of the ListeningPoint interface
 *
 * @version 1.2 $Revision: 1.15 $ $Date: 2009/11/19 05:26:58 $
 *
 * @author GaoXusong
 *
 *
 *
 */
public class ListeningPointImpl implements ListeningPoint {


    protected String transport;

    /** My port. (same thing as in the message processor) */

    int port;

    /**
     * Pointer to the imbedded mesage processor.
     */
    protected MessageProcessor messageProcessor;

    /**
     * Provider back pointer
     */
    protected CPMProviderImpl sipProvider;

    /**
     * Our stack
     */
    protected CPMStackImpl cpmStack;




    /**
     * Construct a key to refer to this structure from the SIP stack
     * @param host host string
     * @param port port
     * @param transport transport
     * @return a string that is used as a key
     */
    public static String makeKey(String host, int port, String transport) {
        return new StringBuffer(host)
            .append(":")
            .append(port)
            .append("/")
            .append(transport)
            .toString()
            .toLowerCase();
    }

    /**
     * Get the key for this strucut
     * @return  get the host
     */
    protected String getKey() {
        return makeKey(this.getIPAddress(), port, transport);
    }

    /**
     * Set the sip provider for this structure.
     * @param sipProvider provider to set
     */
    protected void setSipProvider(CPMProviderImpl sipProviderImpl) {
        this.sipProvider = sipProviderImpl;
    }

    /**
     * Remove the sip provider from this listening point.
     */
    protected void removeSipProvider() {
        this.sipProvider = null;
    }

    /**
     * Constructor
     * @param sipStack Our sip stack
     */
    protected ListeningPointImpl(
        CPMStackImpl cpmStack,
        int port,
        String transport) {
        this.cpmStack = (CPMStackImpl) cpmStack;

        this.port = port;
        this.transport = transport;

    }

    /**
     * Clone this listening point. Note that a message Processor is not
     * started. The transport is set to null.
     * @return cloned listening point.
     */
    public Object clone() {
        ListeningPointImpl lip =
            new ListeningPointImpl(this.cpmStack, this.port, null);
        lip.cpmStack = this.cpmStack;
        return lip;
    }



    /**
     * Gets the port of the ListeningPoint. The default port of a ListeningPoint
     * is dependent on the scheme and transport.  For example:
     * <ul>
     * <li>The default port is 5060 if the transport UDP the scheme is <i>sip:</i>.
     * <li>The default port is 5060 if the transport is TCP the scheme is <i>sip:</i>.
     * <li>The default port is 5060 if the transport is SCTP the scheme is <i>sip:</i>.
     * <li>The default port is 5061 if the transport is TLS over TCP the scheme is <i>sip:</i>.
     * <li>The default port is 5061 if the transport is TCP the scheme is <i>sips:</i>.
     * </ul>
     *
     * @return port of ListeningPoint
     */
    public int getPort() {
        return messageProcessor.getPort();
    }

    /**
     * Gets transport of the ListeningPoint.
     *
     * @return transport of ListeningPoint
     */
    public String getTransport() {
        return messageProcessor.getTransport();
    }

    /**
     * Get the provider.
     *
     * @return the provider.
     */
    public CPMProviderImpl getProvider() {
        return this.sipProvider;
    }

    /* (non-Javadoc)
     * @see javax2.sip.ListeningPoint#getIPAddress()
     */
    public String getIPAddress() {

        return this.messageProcessor.getIpAddress().getHostAddress();
    }



    /* (non-Javadoc)
     * @see javax2.sip.ListeningPoint#setSentBy(java.lang.String)
     */
    public void setSentBy(String sentBy) throws ParseException {
        this.messageProcessor.setSentBy(sentBy);

    }

    /* (non-Javadoc)
     * @see javax2.sip.ListeningPoint#getSentBy()
     */
    public String getSentBy() {

        return this.messageProcessor.getSentBy();
    }

    public boolean isSentBySet() {
        return this.messageProcessor.isSentBySet();
    }
    
//    public Via getViaHeader() {
//        return this.messageProcessor.getViaHeader();
//    }

    public MessageProcessor getMessageProcessor() {
        return this.messageProcessor;
    }

//    public ContactHeader createContactHeader() {
//        try {
//            String ipAddress = this.getIPAddress();
//            int port = this.getPort();
//            SipURI sipURI = new SipUri();
//            sipURI.setHost(ipAddress);
//            sipURI.setPort(port);
//            sipURI.setTransportParam(this.transport);
//            Contact contact = new Contact();
//            AddressImpl address = new AddressImpl();
//            address.setURI(sipURI);
//            contact.setAddress(address);
//            
//            return contact;
//        } catch (Exception ex) {
//            InternalErrorHandler.handleException("Unexpected exception",sipStack.getStackLogger());
//            return null;
//        }
//    }


    public void sendHeartbeat(String ipAddress, int port) throws IOException {

        HostPort targetHostPort  = new HostPort();
        targetHostPort.setHost(new Host( ipAddress));
        targetHostPort.setPort(port);
        MessageChannel messageChannel = this.messageProcessor.createMessageChannel(targetHostPort);
        CPMData cpmData = new CPMData();
        cpmData.setNullRequest();
        messageChannel.sendMessage(cpmData);

    }

    
//    public ViaHeader createViaHeader() {
//           return this.getViaHeader();
//    }

}
