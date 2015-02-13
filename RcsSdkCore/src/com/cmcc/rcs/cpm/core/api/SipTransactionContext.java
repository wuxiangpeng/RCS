package com.cmcc.rcs.cpm.core.api;

import com.cmcc.rcs.cpm.Transaction;
import com.cmcc.rcs.cpm.core.api.header.CallIdHeader;
import com.cmcc.rcs.cpm.core.api.message.Message;


/**
 * SIP transaction context object composed of a request and of the corresponding
 * response. The Transaction context is used for waiting responses of requests
 * and also for waiting an ACK message (special case).
 *
 * @author JM. Auffret
 */
public class SipTransactionContext extends Object {
	
	/**
	 * Transaction
	 */
	private Transaction transaction;
	
	/**
	 * Received message 
	 */
	private SipMessage recvMsg = null;

	/**
	 * Constructor
	 * 
	 * @param transaction SIP transaction
	 */
	public SipTransactionContext(Transaction transaction) {
		this.transaction = transaction;
	}

	/**
	 * Get the SIP transaction
	 * 
	 * @return Transaction
	 */
	public Transaction getTransaction() {
		return transaction;
	}

	/**
	 * Get the SIP message that has been received
	 * 
	 * @return SIP message
	 */
	public SipMessage getMessageReceived() {
		return recvMsg;
	}

	/**
	 * Determine if a timeout has occured
	 * 
	 * @return Returns True if there is a timeout else returns False
	 */
	public boolean isTimeout() {
		return (recvMsg == null);
	}

	/**
	 * Determine if the received message is a SIP response
	 * 
	 * @return Returns True if it's a SIP response else returns False
	 */
	public boolean isSipResponse() {
		if (recvMsg != null) {
			return (recvMsg instanceof SipResponse);
		} else {
			return false;
		}
	}

	/**
	 * Determine if the received message is an intermediate response
	 * 
	 * @return Returns True if it's an intermediate response else returns False
	 */
	public boolean isSipIntermediateResponse() {
		int code = getStatusCode();
	    return (code < 200);
	}

	/**
	 * Determine if the received message is a successful response
	 * 
	 * @return Returns True if it's a successful response else returns False
	 */
	public boolean isSipSuccessfullResponse() {
		int code = getStatusCode();
	    return ((code >= 200) && (code < 300));
	}

	/**
	 * Determine if the received message is a SIP ACK
	 * 
	 * @return Returns True if it's a SIP ACK else returns False
	 */
	public boolean isSipAck() {
		if (recvMsg != null) {
			SipRequest req = (SipRequest)recvMsg;
			if (req.getMethod().equals("ACK")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Get the SIP response that has been received
	 * 
	 * @return SIP response or null if it's not a response (e.g. ACK message)
	 */
	public SipResponse getSipResponse() {
		if (isSipResponse()) {
			return (SipResponse)recvMsg;
		} else {
			return null;
		}
	}

	/**
	 * Get the status code of the received SIP response
	 * 
	 * @return Returns a status code or -1 if it's not a SIP response (e.g. ACK message)
	 */
	public int getStatusCode() {
	    int ret = -1;
		if (isSipResponse()) {
			SipResponse resp = (SipResponse)recvMsg;
			ret = resp.getStatusCode();
		}
		return ret;
	}

	/**
	 * Get the reason phrase of the received SIP response
	 * 
	 * @return Returns a reason phrase or null if it's not a SIP response (e.g. ACK message)
	 */
	public String getReasonPhrase() {
	    String ret = null;
		SipResponse resp = getSipResponse();
		if (resp != null) {
		    ret = resp.getReasonPhrase();
		}
		return ret;
	}

	/**
	 * Wait the response of a request until a timeout occurs
	 * 
	 * @param timeout Timeout value
	 */
	public void waitResponse(int timeout) {
		try {
			if (recvMsg != null) {
				// Response already received, no need to wait
				return;
			}			
			synchronized(this) {
				super.wait(timeout * 1000);
			}
		} catch(InterruptedException e) {
			// Thread has been interrupted
			recvMsg = null;
		}
	}

	/**
	 * A response has been received (SIP response or ACK or any other SIP message) 
	 * 
	 * @param msg SIP message object
	 */
	public void responseReceived(SipMessage msg) {
		synchronized(this) {
			recvMsg = msg;
			super.notify();
		}
	}

	/**
	 * Reset transaction context
	 */
	public void resetContext() {
		synchronized (this) {
			recvMsg = null;
			super.notify();
		}
	}

	/**
	 * Get the transaction context ID associated a SIP message
	 * 
	 * @param msg SIP message
	 * @return Transaction context ID
	 */
	public static String getTransactionContextId(SipMessage msg)  {
		return getTransactionContextId(msg.getStackMessage());
	}

	/**
	 * Get the transaction context ID associated a SIP message
	 * 
	 * @param msg SIP message
	 * @return Transaction context ID
	 */
	public static String getTransactionContextId(Message msg)  {
		CallIdHeader header = (CallIdHeader)msg.getHeader(CallIdHeader.NAME);
		return header.getCallId();
	}
}
