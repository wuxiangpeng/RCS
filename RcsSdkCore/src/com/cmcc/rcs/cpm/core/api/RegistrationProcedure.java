package com.cmcc.rcs.cpm.core.api;

import com.cmcc.rcs.core.CoreException;



/**
 * Abstract registration procedure
 * 
 * @author wuxiangpeng
 */
public abstract class RegistrationProcedure {
	/**
	 * Initialize procedure
	 */
	public abstract void init(); 

	/**
	 * Returns the home domain name
	 * 
	 * @return Domain name
	 */
	public abstract String getHomeDomain(); 
	
	/**
	 * Returns the public URI or IMPU for registration
	 * 
	 * @return Public URI
	 */
	public abstract String getPublicUri();
	
	/**
	 * Write the security header to REGISTER request
	 * 
	 * @param request Request
	 * @throws CoreException
	 */
	public abstract void writeSecurityHeader(SipRequest request) throws CoreException;

	/**
	 * Read the security header from REGISTER response
	 * 
	 * @param response Response
	 * @throws CoreException
	 */
	public abstract void readSecurityHeader(SipResponse response) throws CoreException;
}
