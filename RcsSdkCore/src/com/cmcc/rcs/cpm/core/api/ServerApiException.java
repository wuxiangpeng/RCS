package com.cmcc.rcs.cpm.core.api;

import android.os.RemoteException;

/**
 * Server API exception
 * 
 * @author wuxiangpeng
 */
public class ServerApiException extends RemoteException {
	static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 * 
	 * @param e Exception
	 */
	public ServerApiException(Exception e) {
		setStackTrace(e.getStackTrace());
	}

	/**
	 * Constructor
	 * 
	 * @param error Error message
	 */
	public ServerApiException(String error) {
		Exception e = new Exception(error);
		this.setStackTrace(e.getStackTrace());		
	}
}
