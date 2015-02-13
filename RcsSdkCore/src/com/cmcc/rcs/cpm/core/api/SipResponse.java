package com.cmcc.rcs.cpm.core.api;

import com.cmcc.rcs.cpm.message.Response;



/**
 * SIP response
 * 
 * @author wuxiangpeng
 */
public class SipResponse extends SipMessage {
	
	/**
	 * Constructor
	 *
	 * @param response SIP stack response
	 */
	public SipResponse(Response response) {
		super(response);
	}

	/**
	 * Return the SIP stack message
	 * 
	 * @return SIP response
	 */
	public Response getStackMessage() {
		return (Response)stackMessage;
	}
	
	/**
	 * Returns the status code value
	 * 
	 * @return Status code or -1
	 */
	public int getStatusCode() {
		Response response = getStackMessage();
		if (response != null) {
			return response.getStatusCode();
		} else {
			return -1;
		}
	}
	
	/**
	 * Returns the reason phrase of the response
	 * 
	 * @return String or null
	 */
	public String getReasonPhrase() {
		Response response = getStackMessage();
		if (response != null) {
			return getStackMessage().getReasonPhrase();
		} else {
			return null;
		}
	}
}
