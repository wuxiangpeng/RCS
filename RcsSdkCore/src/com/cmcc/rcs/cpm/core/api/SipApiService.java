package com.cmcc.rcs.cpm.core.api;

import java.util.Hashtable;

import android.content.Intent;

import com.cmcc.rcs.core.Core;
import com.cmcc.rcs.cpm.message.SessionData;
import com.cmcc.rcs.utils.logger.Logger;

/**
 * SIP Api service
 * 
 * @author wuxiangpeng
 */
public class SipApiService {
	
	/**
	 * The logger
	 */
	private static Logger logger = Logger.getLogger(SipApiService.class.getName());
	
	/**
	 * Constructor
	 */
	public SipApiService() {
		if (logger.isActivated()) {
			logger.info("SIP API is loaded");
		}
	}
	
	/**
	 * Send an instant message (SIP MESSAGE)
	 * 
     * @param SessionData SendMsgSession
     * @param String featureTag
	 * @param String szMsgBody
	 * @param String contentType
	 * @return True if successful else returns false
	 * @throws ServerApiException
	 */
	public boolean sendSipInstantMessage(String contact, String featureTag, String szMsgBody, String contentType) throws ServerApiException {
		//contact
		if (logger.isActivated()) {
			logger.info("Send an instant message to " + contact);
		}
		
		// Check permission
		ServerApiUtils.testPermissionForExtensions();

		// Test core availability
		ServerApiUtils.testCore();
		
		try {
			return Core.getInstance().getSipService().sendInstantMessage(contact,featureTag, szMsgBody, contentType);
		} catch(Exception e) {
			if (logger.isActivated()) {
				logger.error("Unexpected error", e);
			}
			throw new ServerApiException(e.getMessage());
		}
	}
	
	/**
	 * Receive an instant message (SIP MESSAGE)
	 *  
	 * @param intent Resolved intent
	 */
//	public void receiveSipInstantMessage(Intent intent) {
//		// Broadcast intent related to the received message
//		AndroidFactory.getApplicationContext().sendBroadcast(intent);
//	}
	
}
