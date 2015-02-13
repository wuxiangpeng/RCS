package com.cmcc.rcs.cpm.core.api;

import java.util.Timer;
import java.util.TimerTask;

import com.cmcc.rcs.core.CoreException;
import com.cmcc.rcs.core.ims.ImsModule;
import com.cmcc.rcs.utils.logger.Logger;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;


/**
 * Call manager. Note: for outgoing call the capability request is initiated
 * only when we receive the OPTIONS from the remote because the call state goes
 * directly to CONNETED even if the remote has not ringing. For the incoming
 * call, the capability are requested when phone is ringing.
 * 
 * @author jexa7410
 */
public class CallManager {
	/**
	 * Call state unknown
	 */
	public final static int UNKNOWN = 0;

	/**
	 * Call state ringing
	 */
	public final static int RINGING = 1;

	/**
	 * Call state connected
	 */
	public final static int CONNECTED = 2;

	/**
	 * Call state disconnected
	 */
	public final static int DISCONNECTED = 3;

	/**
	 * IMS module
	 */
	private ImsModule imsModule;

    /**
     * Call state
     */
    private int callState = UNKNOWN;

    /**
     * Remote party
     */
    private static String remoteParty = null;
    
    /**
     * Multiparty call
     */
    private boolean multipartyCall = false;
    
    /**
     * Call hold
     */
    private boolean callHold = false;

    /**
     * Telephony manager
     */
	private TelephonyManager tm;

	/**
     * The logger
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Constructor
     * 
     * @param core Core
     */
	public CallManager(ImsModule parent) throws CoreException {
		this.imsModule = parent;

		// Instantiate the telephony manager
		tm = (TelephonyManager)AndroidFactory.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
	}

	/**
     * Start call monitoring
     */
	public void startCallMonitoring() {
		if (logger.isActivated()) {
			logger.info("Start call monitoring");
		}

		// Monitor phone state
	    tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

    /**
     * Stop call monitoring
     */
	public void stopCallMonitoring() {
		if (logger.isActivated()) {
			logger.info("Stop call monitoring");
		}

		// Unmonitor phone state
	    tm.listen(listener, PhoneStateListener.LISTEN_NONE);
	}

	/**
	 * Phone state listener
	 */
	private PhoneStateListener listener = new PhoneStateListener() {
		public void onCallStateChanged(int state, String incomingNumber) {
			switch(state) {
				case TelephonyManager.CALL_STATE_RINGING:
					if (callState == CallManager.CONNECTED) {
						// Tentative of multipaty call
						return;
					}

					if (logger.isActivated()) {
						logger.debug("Call is RINGING: incoming number=" + incomingNumber);
					}
					
					// Phone is ringing: this state is only used for incoming call
					callState = CallManager.RINGING;

					// Set remote party
				    remoteParty = incomingNumber;

					break;

				case TelephonyManager.CALL_STATE_IDLE:
					if (logger.isActivated()) {
						logger.debug("Call is IDLE: last number=" + remoteParty);
					}
					
					// No more call in progress
					callState = CallManager.DISCONNECTED;
				    multipartyCall = false;
				    callHold = false;

				    // Abort pending richcall sessions
			    	imsModule.getRichcallService().abortAllSessions();

                    if (remoteParty != null) {
                        // Disable content sharing capabilities
                        imsModule.getCapabilityService().resetContactCapabilitiesForContentSharing(remoteParty);

                        // Request capabilities to the remote
                        imsModule.getCapabilityService().requestContactCapabilities(remoteParty);
                    }

					// Reset remote party
					remoteParty = null;
					break;

				case TelephonyManager.CALL_STATE_OFFHOOK:
					if (callState == CallManager.CONNECTED) {
					    // Request capabilities only if not a multiparty call or call hold
						if (logger.isActivated()) {
							logger.debug("Multiparty call established");
						}
						return;
					}

					if (logger.isActivated()) {
						logger.debug("Call is CONNECTED: connected number=" + remoteParty);
					}

					// Both parties are connected
					callState = CallManager.CONNECTED;

                    // Delay option request 2 seconds according to implementation guideline ID_4_20
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // Request capabilities
                            requestCapabilities(remoteParty);
                        }
                    }, 2000);
					break;

				default:
					if (logger.isActivated()) {
						logger.debug("Unknown call state " + state);
					}
					break;
			}
		}
    };

	/**
     * Set the remote phone number
     * 
     * @param number Phone number
     */
	public static void setRemoteParty(String number) {
		CallManager.remoteParty = number;
	}

	/**
     * Get the remote phone number
     * 
     * @return Phone number
     */
	public String getRemotePhoneNumber() {
		if (callState == CallManager.DISCONNECTED) {
			return null;
		} else {
			return remoteParty;
		}
	}

	/**
     * Returns the calling remote party
     * 
     * @return MSISDN
     */
	public String getRemoteParty() {
		return remoteParty;
	}

	/**
     * Is call connected
     * 
     * @return Boolean
     */
	public boolean isCallConnected() {
		return (callState == CONNECTED);
	}

	/**
     * Is call connected with a given contact
     * 
     * @param contact Contact
     * @return Boolean
     */
	public boolean isCallConnectedWith(String contact) {
		return (isCallConnected() && PhoneUtils.compareNumbers(contact, getRemotePhoneNumber()));
	}

	/**
	 * Is richcall supported with a given contact
	 * 
	 * @param contact Contact
	 * @return Boolean
	 */
	public boolean isRichcallSupportedWith(String contact) {
		if (this.multipartyCall || this.callHold) {
			return false;
		}
		
		return isCallConnectedWith(contact);
	}
	
	/**
     * Is a multiparty call
     * 
     * @return Boolean
     */
	public boolean isMultipartyCall() {
		return multipartyCall;
	}	
	
	/**
     * Is call hold
     * 
     * @return Boolean
     */
	public boolean isCallHold() {
		return callHold;
	}

	/**
     * Request capabilities to a given contact
     * 
     * @param contact Contact
     */
	private void requestCapabilities(String contact) {
        if ((contact != null) && (contact.length() > 0)
                && (imsModule.getCapabilityService().isServiceStarted())) {
			imsModule.getCapabilityService().requestContactCapabilities(contact);
		 }
    }
	
	/**
	 * Call leg has changed
	 */
	private void callLegHasChanged() {
		if (multipartyCall | callHold) {
		    // Abort pending richcall sessions if call hold or multiparty call
	    	imsModule.getRichcallService().abortAllSessions();
    	}
		
		// Request new capabilities
    	requestCapabilities(remoteParty);
	}
	
	/**
	 * Set multiparty call
	 * 
	 * @param state State
	 */
	public void setMultiPartyCall(boolean state) {
		if (logger.isActivated()) {
			logger.info("Set multiparty call to " + state);
		}
		this.multipartyCall = state;
		
		callLegHasChanged();	
	}

	/**
	 * Set call hold
	 * 
	 * @param state State
	 */
	public void setCallHold(boolean state)  {
		if (logger.isActivated()) {
			logger.info("Set call hold to " + state);
		}
		this.callHold = state;
		
		callLegHasChanged();	
	}
	
	/**
	 * Connection event
	 * 
	 * @param connected Connection state
	 */
	public void connectionEvent(boolean connected) {
		if (remoteParty == null) {
			return;
		}
		
		if (connected) {
			if (logger.isActivated()) {
				logger.info("Connectivity changed: update content sharing capabilities");
			}

			// Update content sharing capabilities
			requestCapabilities(remoteParty);
		} else {
			if (logger.isActivated()) {
				logger.info("Connectivity changed: disable content sharing capabilities");
			}

			// Disable content sharing capabilities
			imsModule.getCapabilityService().resetContactCapabilitiesForContentSharing(remoteParty);
		}
	}
}
