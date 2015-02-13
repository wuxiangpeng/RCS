package com.cmcc.rcs.cpm.core.api;

import com.cmcc.rcs.provider.settings.RcsSettings;
import com.cmcc.rcs.utils.logger.Logger;


/**
 * Keep-alive manager (see RFC 5626)
 *
 * @author BJ
 */
public class KeepAliveManager extends PeriodicRefresher {
    /**
     * Keep-alive period (in seconds)
     */
	private int period;
	
    /**
     * SIP interface
     */
    private SipInterface sip;
    
	/**
	 * The logger
	 */
	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Constructor
	 */
	public KeepAliveManager(SipInterface sip) {
		this.sip = sip;
		this.period = RcsSettings.getInstance().getSipKeepAlivePeriod();
	}
	
	/**
	 * Start
	 */
	public void start() {
		if (logger.isActivated()) {
			logger.debug("Start keep-alive");
		}
		startTimer(period, 1);
	}
	
	/**
	 * Start
	 */
	public void stop() {
		if (logger.isActivated()) {
			logger.debug("Stop keep-alive");
		}
		stopTimer();
	}
	
	/**
     * Keep-alive processing
     */
    public void periodicProcessing() {
        try {
    		if (logger.isActivated()) {
    			logger.debug("Send keep-alive");
    		}

    		// Send a double-CRLF
        	sip.getDefaultSipProvider().getListeningPoints()[0].sendHeartbeat(sip.getOutboundProxyAddr(), sip.getOutboundProxyPort());
        	
        	// Start timer
    		startTimer(period, 1);
        } catch(Exception e) {
            if (logger.isActivated()) {
                logger.error("SIP heartbeat has failed", e);
            }
        }
    }

	/**
	 * @param period the keep alive period in seconds
	 */
	public void setPeriod(int period) {
		this.period = period;
		if (logger.isActivated()) {
			logger.debug("Set keep-alive period \"" + period + "\"");
		}
	}
}
