package com.cmcc.rcs.cpm.core.api;

import com.cmcc.rcs.cpm.core.api.header.ExpiresHeader;
import com.cmcc.rcs.cpm.message.Request;

/**
 * SIP request
 * 
 * @author wuxiangpeng
 */
public class SipRequest extends SipMessage {
	
	/**
	 * Constructor
	 *
	 * @param request SIP stack request
	 */
	public SipRequest(Request request) {
		super(request);
	}

	/**
	 * Return the SIP stack message
	 * 
	 * @return SIP request
	 */
	public Request getStackMessage() {
		return (Request)stackMessage;
	}
	
	/**
	 * Returns the method value
	 * 
	 * @return Method name or null is case of response
	 */
	public String getMethod() {
		return getStackMessage().getMethod();
	}
	
	/**
	 * Return the request URI
	 * 
	 * @return String
	 */
	public String getRequestURI() {
		return getStackMessage().getRequestURI().toString();
	}
	
	/**
	 * Return the expires value
	 * 
	 * @return Expire value
	 */
	public int getExpires() {
        ExpiresHeader expires = (ExpiresHeader)getStackMessage().getHeader(ExpiresHeader.NAME);
    	if (expires != null) {
            return expires.getExpires();            
        } else {
        	return -1;
        }
	}	
}
