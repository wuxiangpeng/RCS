package com.cmcc.rcs.service;


import com.cmcc.rcs.provider.configure.CPMConfigure;
import com.cmcc.rcs.provider.configure.CPMConfigureData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class StartLauncherManager implements Runnable {
	private static final String TAG = "StartLauncherManager";
	
	 /**
     * Connection manager
     */
    private ConnectivityManager connMgr = null;

    /**
     * Network state listener
     */
    private BroadcastReceiver networkStateListener = null;
	
	/**
     * Launch boot flag
     */
	private boolean boot = false;
	
	 /**
     * Launch user flag
     */
	private boolean user = false;
	
	private Context context;
	
	  /**
     * Current instance
     */
    private static StartLauncherManager instance = null;    
    
    /**
     * Create instance
     *
     * @param ctx Context
     */
    public static synchronized void createInstance(Context ctx,
                                    boolean boot, boolean user) {
        if (instance == null) {
            instance = new StartLauncherManager(ctx, boot, user);
        }
    }

    /**
     * Returns instance
     *
     * @return Instance
     */
    public static StartLauncherManager getInstance() {
        return instance;
    }
    
	public StartLauncherManager(Context context, 
			                boolean boot, boolean user) {
		this.boot = boot;
		this.user = user;
		this.context = context;
		Thread myThread = new Thread(this);
		myThread.start();
		registerNetworkStateListener();
	}
	
	/**
     * Initialize the RCS Data
     *
     * @param authentiMode 
     * @param fromAddress 
     * @param toAddress 
     * @param serverDomain 
     * @param serverAddress 
     * @param serverPort
     * @return connectMode  
     */
    public synchronized void initRcsData(String authentiMode, String fromAddress,
            String toAddress, String serverDomain, String serverAddress, String serverPort,
            String connectMode) {
        
        CPMConfigure.createInstance(context);
        CPMConfigure configure = CPMConfigure.getInstance();
        configure.writeParameter(CPMConfigureData.AUTHENTI_MODE, authentiMode);
        configure.writeParameter(CPMConfigureData.CPM_FROM_ADDRESS, fromAddress);
        configure.writeParameter(CPMConfigureData.CPM_TO_ADDRESS, toAddress);
        configure.writeParameter(CPMConfigureData.CPM_SERVER_DOMAIN, serverDomain);
        configure.writeParameter(CPMConfigureData.CPM_SERVER_ADDRESS, serverAddress);
        configure.writeParameter(CPMConfigureData.CPM_SERVER_PORT, serverPort);
        configure.writeParameter(CPMConfigureData.CPM_CONNECT_MODE, connectMode);
    }

	
	@Override
	public void run() {
		
		
	}
	
	
	/**
     * Register a broadcast receiver for network state changes
     */
    private void registerNetworkStateListener() {
        // Get connectivity manager
        if (connMgr == null) {
            connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        // Instantiate the network listener
        networkStateListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                new Thread() {
                    public void run() {
                        connectionEvent(intent.getAction());
                    }
                }.start();
            }
        };
        // Register network state listener
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkStateListener, intentFilter);
    }
    
    /**
     * Connection event
     *
     * @param action Connectivity action
     */
    private void connectionEvent(String action) {
    	
    	Log.i(TAG, "Connection event " + action);       
        // Try to start the service only if a data connectivity is available
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if ((networkInfo != null) && networkInfo.isConnected()) {
                
                Log.i(TAG, "Device connected - Launch RCS service"); 
                // Start the RCS core service
                LauncherUtils.launchRcsCoreManager(context.getApplicationContext());
                
                // Stop Network listener
                if (networkStateListener != null) {
                	try {
                		context.unregisterReceiver(networkStateListener);
	    	        } catch (IllegalArgumentException e) {
	    	        	// Nothing to do
	    	        }
                	networkStateListener = null;
                }
            }
        }
    }
	
    
    /**
     * Stop the start launcher
     */
    public synchronized void stopStartLauncher() {
        // Unregister network state listener
        if (networkStateListener != null) {
        	try {
	            context.unregisterReceiver(networkStateListener);
	        } catch (IllegalArgumentException e) {
	        	// Nothing to do
	        	e.printStackTrace();
	        }
        }
    }

}
