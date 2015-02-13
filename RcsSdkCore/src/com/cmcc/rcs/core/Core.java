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

package com.cmcc.rcs.core;

import com.cmcc.rcs.core.ims.ImsModule;
import com.cmcc.rcs.cpm.core.api.SipService;
import com.cmcc.rcs.utils.logger.Logger;



/**
 * Core (singleton pattern)
 *  
 * @author JM. Auffret
 */
public class Core {
	/**
	 * Singleton instance
	 */
	private static Core instance = null;
	
    /**
     * Core listener
     */
    private CoreListener listener;
    
    /**
     * Core status
     */
	private boolean started = false;

    /**
	 * IMS module
	 */
	private ImsModule imsModule;

	/**
     * The logger
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

   
    
    /**
     * Returns the singleton instance
     * 
     * @return Core instance
     */
    public static Core getInstance() {
    	return instance;
    }
    
    /**
     * Instanciate the core
     * 
	 * @param listener Listener
     * @return Core instance
     * @throws CoreException
     */
    public synchronized static Core createCore(CoreListener listener) throws CoreException {
    	if (instance == null) {
    		instance = new Core(listener);
    	}
    	return instance;
    }
    
    /**
     * Terminate the core
     */
    public synchronized static void terminateCore() {
    	if (instance != null) {
    		instance.stopCore();
    	}
   		instance = null;
    }

    /**
     * Constructor
     * 
	 * @param listener Listener
     * @throws CoreException
     */
    private Core(CoreListener listener) throws CoreException {
		

		// Set core event listener
		this.listener = listener;  
       

     
        // Create the IMS module
        imsModule = new ImsModule(this);
     
    }

	/**
	 * Returns the event listener
	 * 
	 * @return Listener
	 */
	public CoreListener getListener() {
		return listener;
	}

	/**
     * Returns the IMS module
     * 
     * @return IMS module
     */
	public ImsModule getImsModule() {
		return imsModule;
	}

	
	
	/**
     * Is core started
     * 
     * @return Boolean
     */
    public boolean isCoreStarted() {
    	return started;
    }

    /**
     * Start the terminal core
     * 
     * @throws CoreException
     */
    public synchronized void startCore() throws CoreException {
    	if (started) {
    		// Already started
    		return;
    	}

    	// Start the IMS module 
    	imsModule.start();

    	
    	
    	// Notify event listener
		listener.handleCoreLayerStarted();
		
		started = true;
    	
    }
    	
    /**
     * Stop the terminal core
     */
    public synchronized void stopCore() {
    	if (!started) {
    		// Already stopped
    		return;
    	}    	
    	
    	
    	try {
	    	// Stop the IMS module 
	    	imsModule.stop();	    	
    	} catch(Exception e) {
    		if (logger.isActivated()) {
    			logger.error("Error during core shutdown", e);
    		}
    	}
    	
    	// Notify event listener
		listener.handleCoreLayerStopped();

    	started = false;
    	if (logger.isActivated()) {
    		logger.info("RCS core service has been stopped with success");
    	}
    }
    
    /**
	 * Returns the SIP service
	 * 
	 * @return SIP service
	 */
	public SipService getSipService() {
		return getImsModule().getSipService();
	}
}
