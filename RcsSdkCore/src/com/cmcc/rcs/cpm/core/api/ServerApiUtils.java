package com.cmcc.rcs.cpm.core.api;

import com.cmcc.rcs.core.Core;
import com.cmcc.rcs.core.ims.service.ImsServiceSession;

import android.content.pm.PackageManager;



/**
 * Server API utils
 * 
 * @author wuxiangpeng
 */
public class ServerApiUtils {
	/**
	 * Test permission
	 * 
	 * @throws SecurityException
	 */
	public static void testPermission() throws SecurityException {
		if (AndroidFactory.getApplicationContext().checkCallingOrSelfPermission(ClientApiPermission.RCS_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
			throw new SecurityException();
	    }
	}
	
	/**
	 * Test permission for extensions
	 * 
	 * @throws SecurityException
	 */
	public static void testPermissionForExtensions() throws SecurityException {
		if (AndroidFactory.getApplicationContext().checkCallingOrSelfPermission(ClientApiPermission.RCS_EXTENSION_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
			throw new SecurityException();
	    }
	}

	/**
	 * Test core
	 * 
	 * @throws ServerApiException
	 */
	public static void testCore() throws ServerApiException {
		if (Core.getInstance() == null) {
			throw new ServerApiException("Core is not instanciated");
		}
	}
	
	/**
	 * Test IMS connection
	 * 
	 * @throws ServerApiException
	 */
//	public static void testIms() throws ServerApiException {
//		if (!isImsConnected()) { 
//			throw new ServerApiException("Core is not connected to IMS"); 
//		}
//	}
	
	/**
	 * Is IMS connected
	 * 
	 * @return IMS connection state
	 */
//	public static boolean isImsConnected(){
//		return ((Core.getInstance() != null) &&
//				(Core.getInstance().getImsModule().getCurrentNetworkInterface() != null) &&
//				(Core.getInstance().getImsModule().getCurrentNetworkInterface().isRegistered()));
//	}
	
	/**
	 * Get session state
	 * 
	 * @return State
	 * @see SessionState
	 */
//	public static int getSessionState(ImsServiceSession session) {
//		int result = SessionState.UNKNOWN;
//		SipDialogPath dialogPath = session.getDialogPath();
//		if (dialogPath != null) {
//			if (dialogPath.isSessionCancelled()) {
//				// Canceled: CANCEL received
//				result = SessionState.CANCELLED;
//			} else
//			if (dialogPath.isSessionEstablished()) {
//				// Established: ACK exchanged
//				result = SessionState.ESTABLISHED;
//			} else
//			if (dialogPath.isSessionTerminated()) {
//				// Terminated: BYE received
//				result = SessionState.TERMINATED;
//			} else {
//				// Pending
//				result = SessionState.PENDING;
//			}
//		}
//		return result;
//	}	
}
