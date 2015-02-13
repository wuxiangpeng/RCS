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

package com.cmcc.rcs.core.ims.network;

import com.cmcc.rcs.core.ims.ImsModule;
import com.cmcc.rcs.cpm.core.api.RegistrationManager;
import com.cmcc.rcs.cpm.core.api.SipManager;



/**
 * Abstract IMS network interface
 *
 * @author GaoXusong
 */
public class ImsNetworkInterface {
 // Changed by Deutsche Telekom
    private static final String REGEX_IPV4 = "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b";
    
    // Changed by Deutsche Telekom
    /**
     * Class containing the resolved fields
     */
    public class DnsResolvedFields {
        public String ipAddress = null;
        public int port = -1;

        public DnsResolvedFields(String ipAddress, int port) {
            this.ipAddress = ipAddress;
            this.port = port;
        }
    }
    
    
    /**
     * SIP manager
     */
    private SipManager sip;
    
    /**
	 * IMS module
	 */
	private ImsModule imsModule;
	
	/**
     * Registration manager
     */
    private RegistrationManager registration;
    
    /**
     * Returns the SIP manager
     *
     * @return SIP manager
     */
    public SipManager getSipManager() {
    	return sip;
    }
    
    /**
     * Returns the registration manager
     *
     * @return Registration manager
     */
	public RegistrationManager getRegistrationManager() {
		return registration;
	}
	
	/**
     * Returns the IMS module
     *
     * @return IMS module
     */
	public ImsModule getImsModule() {
		return imsModule;
	}
}
