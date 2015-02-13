/*******************************************************************************
 * Software Name : RCS IMS Stack
 *
 * Copyright (C) 2010 France Telecom S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.cmcc.rcs.core.ims;

import java.util.Calendar;
import java.util.Enumeration;



import com.cmcc.rcs.core.Core;
import com.cmcc.rcs.core.CoreException;
import com.cmcc.rcs.core.CoreListener;
import com.cmcc.rcs.core.ims.network.ImsNetworkInterface;
import com.cmcc.rcs.core.ims.protocol.cpm.CPMEventListener;
import com.cmcc.rcs.core.ims.service.ImsService;
import com.cmcc.rcs.core.ims.service.ImsServiceDispatcher;
import com.cmcc.rcs.core.ims.service.ImsServiceSession;
import com.cmcc.rcs.cpm.core.api.CallManager;
import com.cmcc.rcs.cpm.core.api.ImsConnectionManager;
import com.cmcc.rcs.cpm.core.api.SipManager;
import com.cmcc.rcs.cpm.core.api.SipService;
import com.cmcc.rcs.cpm.core.api.UserProfile;
import com.cmcc.rcs.cpm.message.CPMData;
import com.cmcc.rcs.utils.logger.Logger;



/**
 * IMS module
 *  
 * @author JM. Auffret
 */


public class ImsModule implements CPMEventListener {
    /**
     * Core
     */
    private Core core;

    /**
	 * IMS user profile
	 */
    public static UserProfile IMS_USER_PROFILE = null;
   
//    /**
//     * IMS connection manager
//     */
//    private ImsConnectionManager connectionManager;

    /**
     * IMS services
     */
    private ImsService services[];

    /**
     * Service dispatcher
     */
    private ImsServiceDispatcher serviceDispatcher;        
   
    /**
     * IMS connection manager
     */
    private ImsConnectionManager connectionManager;
    
    /**
   	 * Call manager
   	 */
   	private CallManager callManager;
    
    /**
     * flag to indicate whether instantiation is finished
     */
    private boolean isReady = false;

	/**
     * The logger
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

	

    /**
     * Constructor
     * 
     * @param core Core
     * @throws CoreException 
     */
    public ImsModule(Core core) throws CoreException {
    	this.core = core;
    	
    	if (logger.isActivated()) {
    		logger.info("IMS module initialization");
    	}
   	
//		// Create the IMS connection manager
//        try {
//			connectionManager = new ImsConnectionManager(this);
//        } catch(Exception e) {
//        	if (logger.isActivated()) {
//        		logger.error("IMS connection manager initialization has failed", e);
//        	}
//            throw new CoreException("Can't instanciate the IMS connection manager");
//        }

        
		
		// Instanciates the IMS services
        services = new ImsService[7];
        


        // Create the service dispatcher
        serviceDispatcher = new ImsServiceDispatcher(this);

      
        isReady = true;

    	if (logger.isActivated()) {
    		logger.info("IMS module has been created");
    	}
    }
    
    /**
     * Returns the SIP manager
     * 
     * @return SIP manager
     */
    public SipManager getSipManager() {
    	return getCurrentNetworkInterface().getSipManager();
    }
         
	/**
     * Returns the current network interface
     * 
     * @return Network interface
     */
	public ImsNetworkInterface getCurrentNetworkInterface() {
		return connectionManager.getCurrentNetworkInterface();
	}
	
	/**
     * Is connected to a Wi-Fi access
     * 
     * @return Boolean
     */
	public boolean isConnectedToWifiAccess() {
		return connectionManager.isConnectedToWifi();
	}
	
	/**
     * Is connected to a mobile access
     * 
     * @return Boolean
     */
	public boolean isConnectedToMobileAccess() {
		return connectionManager.isConnectedToMobile();
	}

	/**
	 * Returns the ImsConnectionManager
	 * 
	 * @return ImsConnectionManager
	 */
	public ImsConnectionManager getImsConnectionManager(){
		return connectionManager;
	}

	/**
     * Start the IMS module
     */
    public void start() {
    	if (logger.isActivated()) {
    		logger.info("Start the IMS module");
    	}
    	
    	// Start the service dispatcher
    	serviceDispatcher.start();

		// Start call monitoring
    	callManager.startCallMonitoring();
    	
    	if (logger.isActivated()) {
    		logger.info("IMS module is started");
    	}
    }
    	
    /**
     * Stop the IMS module
     */
    public void stop() {
    	if (logger.isActivated()) {
    		logger.info("Stop the IMS module");
    	}
         	

    	// Terminate the connection manager
    	connectionManager.terminate();

    	// Terminate the service dispatcher
    	serviceDispatcher.terminate();

    	if (logger.isActivated()) {
    		logger.info("IMS module has been stopped");
    	}
    }

    /**
     * Start IMS services
     */
    public void startImsServices() {
    	
    	// Start each services
		for(int i=0; i < services.length; i++) {
			if (services[i].isActivated()) {
				if (logger.isActivated()) {
					logger.info("Start IMS service: " + services[i].getClass().getName());
				}
				services[i].start();
			}
		}	
	
    }
    
    /**
     * Stop IMS services
     */
    public void stopImsServices() {
    	// Abort all pending sessions
    	abortAllSessions();
    	
    	// Stop each services
    	for(int i=0; i < services.length; i++) {
    		if (services[i].isActivated()) {
				if (logger.isActivated()) {
					logger.info("Stop IMS service: " + services[i].getClass().getName());
				}
    			services[i].stop();
    		}
    	}    	
	
    }

    /**
     * Check IMS services
     */
    public void checkImsServices() {
    	for(int i=0; i < services.length; i++) {
    		if (services[i].isActivated()) {
				if (logger.isActivated()) {
					logger.info("Check IMS service: " + services[i].getClass().getName());
				}
    			services[i].check();
    		}
    	}
    }

	
	
	/**
     * Returns the IMS service
     * 
     * @param id Id of the IMS service
     * @return IMS service
     */
    public ImsService getImsService(int id) {
    	return services[id]; 
    }

    /**
     * Returns the IMS services
     * 
     * @return Table of IMS service
     */
    public ImsService[] getImsServices() {
    	return services; 
    }   

   
   
    /**
     * Return the core instance
     * 
     * @return Core instance
     */
    public Core getCore() {
    	return core;
    }
    	
	/**
     * Return the core listener
     * 
     * @return Core listener
     */
    public CoreListener getCoreListener() {
    	return core.getListener();
    }
	
//	/**
//	 * Receive SIP request
//	 * 
//	 * @param request SIP request
//	 */
//	public void receiveSipRequest(SipRequest request) {
//        // Post the incoming request to the service dispatcher
//    	serviceDispatcher.postSipRequest(request);
//	}
	
	/**
	 * Abort all sessions
	 */
	public void abortAllSessions() {
        try {
            if (logger.isActivated()) {
                logger.debug("Abort all pending sessions");
            }
            ImsService[] services = getImsServices();
            for (int i = 0; i < services.length; i++) {
                ImsService service = services[i];
                for (Enumeration<ImsServiceSession> e = service.getSessions(); e.hasMoreElements();) {
                    ImsServiceSession session = (ImsServiceSession) e.nextElement();
                    if (logger.isActivated()) {
                        logger.debug("Abort session " + session.getSessionID());
                    }
                    session.abortSession(ImsServiceSession.TERMINATION_BY_SYSTEM);
                }
            }
        } catch (Exception e) {
            // Aborting sessions may fail (e.g. due to ConcurrentModificationException)
            // we don't want the whole shutdown to be interrupted just because of this
            if (logger.isActivated()) {
                logger.error("Aborting all sessions failed", e);
            }
        }
	}

    /**
     * Check whether ImsModule instantiation has finished
     *
     * @return true if ImsModule is completely initialized
     */
    public boolean isReady(){
        return isReady;
    }

	@Override
	public void receiveSipRequest(CPMData request) {
		
		
	}
	
	/**
     * Returns the SIP service
     * 
     * @return SIP service
     */
    public SipService getSipService() {
    	return (SipService)services[ImsService.SIP_SERVICE];
    }
}
