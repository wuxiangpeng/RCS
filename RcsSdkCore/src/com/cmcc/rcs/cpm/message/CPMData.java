package com.cmcc.rcs.cpm.message;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Iterator;

import com.cmcc.rcs.cpm.core.api.SipInterface;


/**
 * 
 * @author GaoXusong
 *
 */

public class CPMData extends MessageObject {    

    private static String defaultContentEncodingCharset = "UTF-8";
    
	/**
	 * Call-Id
	 */
	private String callId;

	/**
	 * CSeq number
	 */
	private long cseq;
	/**
	 * Local tag
	 */
	private String localTag;

	/**
	 * Remote tag
	 */
	private String remoteTag;
	
	/**
	 * From Address
	 */
	private String fromAddress;
	
	/***
	 * To Address
	 */
	private String toAddress;
	
	/***
	 * SipRequest acquire method name
	 */
	private String method;
	
	/** 
	 * SIPResponse acquire status code field
     */
    private int statusCode;
    
    /**
     * Mark SipRequest Or SIPResponse
     */
	private CPMStatus cpmStatus;
	
	/**
	 * The entire Message Body
	 */
	private String messageBody;

    private String host;
    
    private String port;
    
    private CPMSendType sendType;
    
    private SessionData sessionData;
    
    /*
     * True if this is a null request.
     */
    protected boolean nullRequest;
    
    /**
	 * SIP stack interface
	 */
	private SipInterface stack;
    
//	public  String getCallId() {
//		return callId;
//	}
	
	public String createOrobtainCallId() {
	    if ( callId == null ) {
	        
	    } else {
	        
	    }
	    
	    return callId;
	}

	public  void setCallId(String callId) {
		this.callId = callId;
	}

	public  long getCseq() {
		return cseq;
	}

	public  void setCseq(long cseq) {
		this.cseq = cseq;
	}

	public String getLocalTag() {
		return localTag;
	}

	public void setLocalTag(String localTag) {
		this.localTag = localTag;
	}

	public String getRemoteTag() {
		return remoteTag;
	}

	public void setRemoteTag(String remoteTag) {
		this.remoteTag = remoteTag;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public CPMStatus getSipStatus() {
		return cpmStatus;
	}

	public void setSipStatus(CPMStatus cpmStatus) {
		this.cpmStatus = cpmStatus;
	}
	
	public String getMessageBody() {
	    return messageBody;
	}
	
	public void setMessageBody(String messageBody) {
	    this.messageBody = messageBody;
	}
	
	public String getHost() {
	    return host;
	}

	public void setHost(String host) {
	    this.host = host;
	}

	public String getPort() {
	    return port;
	}

	public void setPort(String port) {
	    this.port = port;
	}
	    
	public CPMSendType getSendType() {
        return sendType;
    }

    public void setSendType(CPMSendType sendType) {
        this.sendType = sendType;
    }
    
    
    /**
     * Return true if this is a null request (i.e. does not have a request line ).
     *
     * @return true if null request.
     */
    public boolean isNullRequest() {
        return  this.nullRequest;
    } 
  
    /**
     * Set a flag to indiate this is a special message ( encoded with CRLFCRLF ).
     * 
     */
    public void setNullRequest() {
        this.nullRequest = true;
    }

    /**
     * Encode the message as a byte array. Use this when the message payload is a binary byte
     * array.
     * 
     * @return The Canonical byte array representation of the message (including the canonical
     *         byte array representation of the SDP payload if it exists all in one contiguous
     *         byte array).
     */
   
    @TargetApi(Build.VERSION_CODES.GINGERBREAD) 
    public byte[] encodeAsBytes(String messageBody) {
        byte[] retval = null;
        
        if (  ( messageBody == null )
           || (messageBody.isEmpty() )
           || (messageBody.length() == 0 ) ) {
            return "\r\n\r\n".getBytes();
        } else {
            try {
                retval = messageBody.getBytes(defaultContentEncodingCharset);
            } catch (UnsupportedEncodingException e) {
               
                e.printStackTrace();
            }
        }
       
        return retval;
    }
    
    public int obtainContentLengthFromJni(String messageBody) {
        //Here obtain content length through messageBody to the call JNI from TanLinLin
        return 0;
    }
    
    public void obtainCallIDfromJni(String messageBody) {
      //Here obtain call id through messageBody to the call JNI from TanLinLin
        
      // callId = 
        
    }
    
    public void obtainCpmStatusfromJni(String messageBody) {
        //Here obtain cpm status through messageBody to the call JNI from TanLinLin
        // cpmStatus = 
    }
    
    
    @Override
    public String encode() {
        
        return messageBody;
    }

    /**
	 * Get the current SIP stack interface
	 * 
	 * @return SIP stack interface
	 */
	public SipInterface getSipStack() {
		return stack;
	}
}
