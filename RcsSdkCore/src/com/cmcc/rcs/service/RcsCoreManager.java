package com.cmcc.rcs.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cmcc.rcs.core.Core;
import com.cmcc.rcs.core.CoreException;
import com.cmcc.rcs.core.CoreListener;
import com.cmcc.rcs.core.ims.ImsError;
import com.cmcc.rcs.cpm.message.CPMData;





public class RcsCoreManager implements Runnable, CoreListener {
    private static final String TAG = "RcsCoreManager";
    
    private Context context = null;
    /**
     * CPU manager
     */
    private CpuManager cpuManager = null;
    
    
    public RcsCoreManager(Context context) {
        this.context = context;
        cpuManager = new CpuManager(this.context);
     // Start the core
        startCore();
    }
    
    
	@Override
	public void run() {
		
	    CPMData cpmData = new CPMData();
	    cpmData.setSendType(sendType);
	    
	    cpmData.setText();
	    
		
	}
    
    /**
     * Start core
     */
    public synchronized void startCore() {
        if (Core.getInstance() != null) {
            // Already started
            return;
        }
        
        
        
        // Create the core
        try {
            Log.i(TAG, "Start RCS core service");
            
            Core.createCore(this);
            // Start the core
            Core.getInstance().startCore();            
            // Init CPU manager
            cpuManager.init();
            
            Log.i(TAG, "RCS core service started with success");
        } catch (Exception e) {            
            e.printStackTrace();
            stopCore();
        }
    }
    
    /**
     * Stop core
     */
    public synchronized void stopCore() {
        if (Core.getInstance() == null) {
            // Already stopped
            return;
        }
        
        Log.i(TAG, "Stop RCS core service");
        
        

        // Terminate the core in background
        Core.terminateCore();

        // Close CPU manager
        cpuManager.close();

      
        Log.i(TAG, "RCS core service stopped with success");
        
    }
    
    
    @Override
    public void handleCoreLayerStarted() {
        
        
    }

    @Override
    public void handleCoreLayerStopped() {
        
        
    }

    @Override
    public void handleRegistrationSuccessful() {
        
        
    }

    @Override
    public void handleRegistrationFailed(ImsError error) {
       
        
    }

    @Override
    public void handleRegistrationTerminated() {
       
        
    }

    @Override
    public void handlePresenceSharingNotification(String contact,
            String status, String reason) {
        
        
    }

    @Override
    public void handlePresenceSharingInvitation(String contact) {
        
        
    }

    @Override
    public void handleMessageDeliveryStatus(String contact, String msgId,
            String status) {
       
        
    }

    @Override
    public void handleFileDeliveryStatus(String ftSessionId, String status,
            String contact) {
        
        
    }

    @Override
    public void handleSipInstantMessageReceived(Intent intent) {
       
        
    }

    @Override
    public void handleUserConfirmationRequest(String remote, String id,
            String type, boolean pin, String subject, String text,
            String btnLabelAccept, String btnLabelReject, int timeout) {
        
        
    }

    @Override
    public void handleUserConfirmationAck(String remote, String id,
            String status, String subject, String text) {
       
        
    }

    @Override
    public void handleUserNotification(String remote, String id,
            String subject, String text, String btnLabel) {
        
        
    }

    @Override
    public void handleSimHasChanged() {
        
        
    }

}
