package com.cmcc.rcs.cpm.core.api;

import com.cmcc.rcs.core.CoreException;
import com.cmcc.rcs.core.ims.ImsModule;
import com.cmcc.rcs.cpm.core.api.header.ProxyAuthenticateHeader;
import com.cmcc.rcs.cpm.core.api.header.ProxyAuthorizationHeader;
import com.cmcc.rcs.utils.logger.Logger;


public class SessionAuthenticationAgent {
	/**
     * The logger
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * HTTP Digest MD5 agent for session
	 */
	private HttpDigestMd5Authentication digest = new HttpDigestMd5Authentication();

	/**
	 * HTTP Digest MD5 agent for register (nonce caching procedure)
	 */
	private HttpDigestMd5Authentication registerDigest = null;

	/**
	 * Constructor
	 * 
	 * @param imsModule IMS module
	 */
	public SessionAuthenticationAgent(ImsModule imsModule) {
		// Re-use the registration authentication (nonce caching)
		RegistrationProcedure procedure = imsModule.getCurrentNetworkInterface().getRegistrationManager().getRegistrationProcedure();
		if (procedure instanceof HttpDigestRegistrationProcedure) {
			registerDigest = ((HttpDigestRegistrationProcedure)procedure).getHttpDigest();
		}
	}

	/**
	 * Set the proxy authorization header on the INVITE request
	 * 
	 * @param request SIP request
	 * @throws CoreException
	 */
	public void setProxyAuthorizationHeader(SipRequest request) throws CoreException {
		if ((digest.getRealm() == null) || (digest.getNextnonce() == null)) {
			return;
		}

		try {
	   		// Update nonce parameters
			digest.updateNonceParameters();
	
			// Calculate response
			String user = ImsModule.IMS_USER_PROFILE.getPrivateID();
			String password = ImsModule.IMS_USER_PROFILE.getPassword();
	   		String response = digest.calculateResponse(user,
	   				password,
	   				request.getMethod(),
	   				request.getRequestURI(),
					digest.buildNonceCounter(),
					request.getContent());			
	   		
			// Build the Proxy-Authorization header
			String auth = "Digest username=\"" + ImsModule.IMS_USER_PROFILE.getPrivateID() + "\"" +
				",uri=\"" + request.getRequestURI() + "\"" +
				",algorithm=MD5" +
				",realm=\"" + digest.getRealm() + "\"" +
				",nc=" + digest.buildNonceCounter() +
				",nonce=\"" + digest.getNonce() + "\"" +
				",response=\"" + response +	"\"" +
				",cnonce=\"" + digest.getCnonce() + "\"";
			String qop = digest.getQop();
			if (qop != null) {
				auth += ",qop=" + qop;
			}
			
			// Set header in the SIP message 
			request.addHeader(ProxyAuthorizationHeader.NAME, auth);

		} catch(Exception e) {
			if (logger.isActivated()) {
				logger.error("Can't create the proxy authorization header", e);
			}
			throw new CoreException("Can't create the proxy authorization header");
		}
	}

	/**
	 * Read parameters of the Proxy-Authenticate header
	 * 
	 * @param response SIP response
	 */
	public void readProxyAuthenticateHeader(SipResponse response) {
		ProxyAuthenticateHeader header = (ProxyAuthenticateHeader)response.getHeader(ProxyAuthenticateHeader.NAME);
		if (header != null) {
	   		// Get domain name
			digest.setRealm(header.getRealm());

			// Get qop
			digest.setQop(header.getQop());
	   		
	   		// New nonce to be used
			digest.setNextnonce(header.getNonce());
		}
	}	

	/**
	 * Set the authorization header on the INVITE request
	 * 
	 * @param request SIP request
	 * @throws CoreException
	 */
	public void setAuthorizationHeader(SipRequest request) throws CoreException {
		try {
			// Re-use the registration authentication (nonce caching)
			if ((registerDigest == null) || (registerDigest.getNextnonce() == null)) {
				return;
			}
			
	   		// Update nonce parameters
			registerDigest.updateNonceParameters();
	
			// Calculate response
			String user = ImsModule.IMS_USER_PROFILE.getPrivateID();
			String password = ImsModule.IMS_USER_PROFILE.getPassword();
	   		String response = registerDigest.calculateResponse(user,
	   				password,
	   				request.getMethod(),
	   				request.getRequestURI(),
	   				registerDigest.buildNonceCounter(),
					request.getContent());			
	   		
			// Build the Authorization header
			String auth = "Digest username=\"" + ImsModule.IMS_USER_PROFILE.getPrivateID() + "\"" +
				",uri=\"" + request.getRequestURI() + "\"" +
				",algorithm=MD5" +
				",realm=\"" + registerDigest.getRealm() + "\"" +
				",nc=" + registerDigest.buildNonceCounter() +
				",nonce=\"" + registerDigest.getNextnonce() + "\"" +
				",response=\"" + response +	"\"" +
				",cnonce=\"" + registerDigest.getCnonce() + "\"";
			String qop = registerDigest.getQop();
			if (qop != null) {
				auth += ",qop=" + qop;
			}
			
			// Set header in the SIP message 
			request.addHeader(ProxyAuthorizationHeader.NAME, auth);

		} catch(Exception e) {
			if (logger.isActivated()) {
				logger.error("Can't create the authorization header", e);
			}
			throw new CoreException("Can't create the authorization header");
		}
    }
}
