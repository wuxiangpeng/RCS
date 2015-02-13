package com.cmcc.rcs.cpm.core.api;

import java.util.ListIterator;
import java.util.Vector;

import com.cmcc.rcs.cpm.core.api.header.ExtensionHeader;
import com.cmcc.rcs.cpm.core.api.header.Header;
import com.cmcc.rcs.provider.settings.RcsSettings;


/**
 * User profile 
 *
 * @author JM. Auffret
 */
public class UserProfile {
	
	/**
	 * User name
	 */
	private String username;

	/**
	 * Private ID for HTTP digest
	 */
	private String privateID;

	/**
	 * Password for HTTP digest
	 */
	private String password;

	/**
	 * Realm for HTTP digest
	 */
	private String realm;

	/**
	 * Home domain
	 */
	private String homeDomain;

	/**
	 * XDM server address
	 */
	private String xdmServerAddr;

	/**
	 * XDM server login
	 */
	private String xdmServerLogin;

	/**
	 * XDM server password
	 */
	private String xdmServerPassword;

	/**
	 * IM conference URI
	 */
	private String imConferenceUri;

	/**
	 * Associated URIs
	 */
	private Vector<String> associatedUriList = new Vector<String>();
	
	/**
	 * Preferred URI
	 */
	private String preferredUri = null;
	
	/**
	 * Constructor
	 * 
	 * @param username Username
	 * @param homeDomain Home domain
	 * @param privateID Private id
	 * @param password Password
	 * @param realm Realm
	 * @param xdmServerAddr XDM server address
	 * @param xdmServerLogin Outbound proxy address
	 * @param xdmServerPassword Outbound proxy address
	 * @param imConferenceUri IM conference factory URI
	 */
	public UserProfile(String username,
			String homeDomain,
			String privateID,
			String password,
			String realm,
			String xdmServerAddr,
			String xdmServerLogin,
			String xdmServerPassword,
			String imConferenceUri) {
		this.username = username;
		this.homeDomain = homeDomain;
		this.privateID = privateID;
		this.password = password;
		this.realm = realm;
		this.xdmServerAddr = xdmServerAddr;
		this.xdmServerLogin = xdmServerLogin;
		this.xdmServerPassword = xdmServerPassword;
		this.imConferenceUri = imConferenceUri;
		// Changed by Deutsche Telekom
		// Continuation from the changes done by "AS" on "2012-09-01"
		// this.preferredUri = "sip:" + username + "@" + homeDomain;
		this.preferredUri = getPublicUriForRegistration();
	}

	/**
	 * Get the user name
	 * 
	 * @return Username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Set the user name
	 * 
	 * @param username Username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Get the user preferred URI
	 * 
	 * @return Preferred URI
	 */
	public String getPreferredUri() {
		return preferredUri;
	}	

	/**
	 * Get the user public URI
	 * 
	 * @return Public URI
	 */
	public String getPublicUri() {
		if (preferredUri == null) { 
			// Changed by Deutsche Telekom
		    // Continuation from the changes done by "AS" on "2012-09-01"
		    return getPublicUriForRegistration();
		} else {
			return preferredUri;
		}
	}
	
	// Changed by Deutsche Telekom
    /**
     * Get the user public URI for registration
     * 
     * @return Public URI
     */
    public String getPublicUriForRegistration() {
		//***###***AS 2012-09-01: added; only add schema and domain if public_user_identity didn't already contain these
        // Changed by Deutsche Telekom
        if (username == null) {
        	return null;
        }
		int index1 = username.indexOf("sip:");
		int index2 = username.indexOf("@");
		if ((index1 == -1) && (index2 == -1)) {
			return "sip:" + username + "@" + homeDomain;
		} else {
			return username;
		}
    }
    
	/**
	 * Get the user public address
	 * 
	 * @return Public address
	 */
	public String getPublicAddress() {
		String addr = getPublicUri();
		String displayName = RcsSettings.getInstance().getUserProfileImsDisplayName();
		if ((displayName != null) && (displayName.length() > 0)) {
			String number = PhoneUtils.extractNumberFromUri(addr);
			if (number != null && number.equals(displayName)) {
				// Do no insert display name if it is equal to the international number
				return addr;
			}
			addr = "\"" + displayName + "\" <" + addr + ">";
		}
		return addr;
	}

	/**
	 * Set the user associated URIs
	 * 
	 * @param uris List of URIs
	 */
	public void setAssociatedUri(ListIterator<Header> uris) {
		if (uris == null) {
			return;
		}
		
		String sipUri = null;
		String telUri = null;
		while(uris.hasNext()) {
			ExtensionHeader header = (ExtensionHeader)uris.next();
			String value = header.getValue();
			value = SipUtils.extractUriFromAddress(value);
			associatedUriList.addElement(value);

			if (value.startsWith("sip:")) {
				sipUri = value;
			} else
			if (value.startsWith("tel:")) {
				telUri = value;
			}
		}
		
		if ((sipUri != null) && (telUri != null)) {
			preferredUri = telUri;
		} else
		if (telUri != null) {
			preferredUri = telUri;
		} else
		if (sipUri != null) {
			preferredUri = sipUri;
		}
	}
	
	/**
	 * Get the private ID used for HTTP Digest authentication
	 * 
	 * @return Private ID
	 */
	public String getPrivateID() {
		return privateID;
	}
	
	/**
	 * Returns the password used for HTTP Digest authentication
	 * 
	 * @return Password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Returns the realm used for HTTP Digest authentication
	 * 
	 * @return Realm
	 */
	public String getRealm() {
		return realm;
	}

	/**
	 * Returns the home domain
	 * 
	 * @return Home domain
	 */
	public String getHomeDomain() {
		return homeDomain;
	}
	
	/**
	 * Set the home domain
	 * 
	 * @param domain Home domain
	 */
	public void setHomeDomain(String domain) {
		this.homeDomain = domain;
	}
	
	/**
	 * Set the XDM server address
	 * 
	 * @param addr Server address
	 */
	public void setXdmServerAddr(String addr) {
		this.xdmServerAddr = addr;
	}

	/**
	 * Returns the XDM server address
	 * 
	 * @return Server address
	 */
	public String getXdmServerAddr() {
		return xdmServerAddr;
	}
	
	/**
	 * Set the XDM server login
	 * 
	 * @param login Login
	 */
	public void setXdmServerLogin(String login) {
		this.xdmServerLogin = login;
	}

	/**
	 * Returns the XDM server login
	 * 
	 * @return Login
	 */
	public String getXdmServerLogin() {
		return xdmServerLogin;
	}

	/**
	 * Set the XDM server password
	 * 
	 * @param pwd Password
	 */
	public void setXdmServerPassword(String pwd) {
		this.xdmServerPassword = pwd;
	}

	/**
	 * Returns the XDM server password
	 * 
	 * @return Password
	 */
	public String getXdmServerPassword() {
		return xdmServerPassword;
	}
	
	/**
	 * Set the IM conference URI
	 * 
	 * @param uri URI
	 */
	public void setImConferenceUri(String uri) {
		this.imConferenceUri = uri;
	}

	/**
	 * Returns the IM conference URI
	 * 
	 * @return URI
	 */
	public String getImConferenceUri() {
		return imConferenceUri;
	}

	/**
     * Returns the profile value as string
     * 
     * @return String
     */
	public String toString() {
		String result = "IMS username=" + username + ", " 
			+ "IMS private ID=" + privateID + ", "
			+ "IMS password=" + password + ", "
			+ "IMS home domain=" + homeDomain + ", "
			+ "XDM server=" + xdmServerAddr + ", "
			+ "XDM login=" + xdmServerLogin + ", "
			+ "XDM password=" + xdmServerPassword + ", " 
			+ "IM Conference URI=" + imConferenceUri;
		return result;
	}
}
