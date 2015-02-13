package com.cmcc.rcs.cpm.core.api;

import java.util.Enumeration;
import java.util.Vector;

import com.cmcc.rcs.core.CoreException;
import com.cmcc.rcs.core.ims.ImsModule;
import com.cmcc.rcs.core.ims.service.ImsService;
import com.cmcc.rcs.core.ims.service.ImsServiceSession;
import com.cmcc.rcs.cpm.message.CPMData;
import com.cmcc.rcs.cpm.message.SessionData;
import com.cmcc.rcs.utils.IdGenerator;
import com.cmcc.rcs.utils.logger.Logger;

import android.content.Intent;


/**
 * SIP service
 * 
 * @author wuxiangpeng
 */
public class SipService extends ImsService {
	/**
     * The logger
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Constructor
     * 
     * @param parent IMS module
     * @throws CoreException
     */
	public SipService(ImsModule parent) throws CoreException {
        super(parent, true);
	}

    /**
     * /** Start the IMS service
     */
	public synchronized void start() {
		if (isServiceStarted()) {
			// Already started
			return;
		}
		setServiceStarted(true);
	}

    /**
     * Stop the IMS service
     */
	public synchronized void stop() {
		if (!isServiceStarted()) {
			// Already stopped
			return;
		}
		setServiceStarted(false);
	}

	/**
     * Check the IMS service
     */
	public void check() {
	}


	/**
	 * Send an instant message (SIP MESSAGE)
	 * 
     * @param SessionData SendMsgSession
	 * @param String szMsgBody
	 * @return True if successful else returns false
	 */
	public boolean sendInstantMessage(String contact, String featureTag, String szMsgBody, String contentType) {
		boolean result = false;
		try {
			if (logger.isActivated()) {
       			logger.debug("Send instant message to " + contact);
       		}
			
		    // Create authentication agent 
       		SessionAuthenticationAgent authenticationAgent = new SessionAuthenticationAgent(getImsModule());
       		
       		// Create a dialog path
//        	String contactUri = PhoneUtils.formatNumberToSipUri(SendMsgSession.getFrom().getFromUri());
        	
//        	SipDialogPath dialogPath = new SipDialogPath(
//        			getImsModule().getSipManager().getSipStack(),
//        			getImsModule().getSipManager().getSipStack().generateCallId(),
//    				1,
//    				contactUri,
//    				ImsModule.IMS_USER_PROFILE.getPublicUri(),
//    				contactUri,
//    				getImsModule().getSipManager().getSipStack().getServiceRoutePath());        	
        	
        	CPMData cpmdata = new CPMData();
//        	cpmdata.setSendType(sendType);
        	
	        // Create MESSAGE request
        	if (logger.isActivated()) {
        		logger.info("Send first MESSAGE");
        	}
	        SipRequest msg = SipMessageFactory.createMessage(cpmdata, featureTag, szMsgBody, contentType);
	        
	        // Send MESSAGE request
	        SipTransactionContext ctx = getImsModule().getSipManager().sendSipMessageAndWait(msg);
	
	        // Analyze received message
            if (ctx.getStatusCode() == 407) {
                // 407 response received
            	if (logger.isActivated()) {
            		logger.info("407 response received");
            	}

    	        // Set the Proxy-Authorization header
            	authenticationAgent.readProxyAuthenticateHeader(ctx.getSipResponse());

                // Increment the Cseq number of the dialog path
//                dialogPath.incrementCseq();

                // Create a second MESSAGE request with the right token
                if (logger.isActivated()) {
                	logger.info("Send second MESSAGE");
                }
    	        msg = SipMessageFactory.createMessage(cpmdata, featureTag, szMsgBody, contentType);

    	        // Set the Authorization header
    	        authenticationAgent.setProxyAuthorizationHeader(msg);
                
                // Send MESSAGE request
    	        ctx = getImsModule().getSipManager().sendSipMessageAndWait(msg);

                // Analyze received message
                if ((ctx.getStatusCode() == 200) || (ctx.getStatusCode() == 202)) {
                    // 200 OK response
                	if (logger.isActivated()) {
                		logger.info("20x OK response received");
                	}
                	result = true;
                } else {
                    // Error
                	if (logger.isActivated()) {
                		logger.info("Send instant message has failed: " + ctx.getStatusCode()
    	                    + " response received");
                	}
                }
            } else
            if ((ctx.getStatusCode() == 200) || (ctx.getStatusCode() == 202)) {
	            // 200 OK received
            	if (logger.isActivated()) {
            		logger.info("20x OK response received");
            	}
            	result = true;
	        } else {
	            // Error responses
            	if (logger.isActivated()) {
            		logger.info("Send instant message has failed: " + ctx.getStatusCode()
	                    + " response received");
            	}
	        }
        } catch(Exception e) {
        	if (logger.isActivated()) {
        		logger.error("Can't send MESSAGE request", e);
        	}
        }
        return result;
	}

    /**
     * Receive an instant message
     * 
     * @param intent Resolved intent
     * @param message Instant message request
     */
//	public void receiveInstantMessage(Intent intent, SipRequest message) {
//		// Send a 200 OK response
//		try {
//			if (logger.isActivated()) {
//				logger.info("Send 200 OK");
//			}
//	        SipResponse response = SipMessageFactory.createResponse(message,
//	        		IdGenerator.getIdentifier(), 200);
//			getImsModule().getSipManager().sendSipResponse(response);
//		} catch(Exception e) {
//	       	if (logger.isActivated()) {
//	    		logger.error("Can't send 200 OK response", e);
//	    	}
//	       	return;
//		}
//
//		// Update intent
//		String contact = SipUtils.getAssertedIdentity(message);
//		String number = PhoneUtils.extractNumberFromUri(contact);
//		intent.putExtra("contact", number);
//		intent.putExtra("contactDisplayname", SipUtils.getDisplayNameFromUri(message.getFrom()));
//		intent.putExtra("content", message.getContent());
//		intent.putExtra("contentType", message.getContentType());
//		
//		// Notify listener
//		getImsModule().getCore().getListener().handleSipInstantMessageReceived(intent);
//	}
}