package com.cmcc.rcs.cpm.core.api;

import com.cmcc.rcs.utils.logger.Logger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


/**
 * Periodic refresher
 *
 * @author JM. Auffret
 */
public abstract class PeriodicRefresher {
	/**
     * Keep alive manager
     */
    private KeepAlive alarmReceiver = new KeepAlive(); 

    /**
     * Alarm intent
     */
    private PendingIntent alarmIntent;

    /**
     * Action 
     */
    private String action;
    
    /**
     * Timer state
     */
    private boolean timerStarted = false;
    
    /**
     * Polling period
     */
    private int pollingPeriod;
    
    /**
     * The logger
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Constructor
     */
    public PeriodicRefresher() {
    	// Create a unique pending intent
    	this.action = this.toString(); // Unique action ID 
    	this.alarmIntent = PendingIntent.getBroadcast(
    			AndroidFactory.getApplicationContext(),
    			0,
    			new Intent(action),
    			0);
    }
    
    /**
     * Periodic processing
     */
    public abstract void periodicProcessing();
    
    /**
     * Start the timer
     * 
     * @param expirePeriod Expiration period in seconds
     */
    public void startTimer(int expirePeriod) {
    	startTimer(expirePeriod, 1.0);
    }
    	
    /**
     * Start the timer
     * 
     * @param expirePeriod Expiration period in seconds
     * @param delta Delta to apply on the expire period in percentage
     */
    public synchronized void startTimer(int expirePeriod, double delta) {
    	// Check expire period
    	if (expirePeriod <= 0) {
    		// Expire period is null
        	if (logger.isActivated()) {
        		logger.debug("Timer is deactivated");
        	}
    		return;
    	}

    	// Calculate the effective refresh period
    	pollingPeriod = (int)(expirePeriod * delta);
    	if (logger.isActivated()) {
    		logger.debug("Start timer at period=" + pollingPeriod +  "s (expiration=" + expirePeriod + "s)");
    	}

        // Register the alarm receiver
    	AndroidFactory.getApplicationContext().registerReceiver(alarmReceiver, new IntentFilter(action));		
    	
    	// Start alarm from now to the expire value
        AlarmManager am = (AlarmManager)AndroidFactory.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+pollingPeriod*1000, alarmIntent);

        // The timer is started
    	timerStarted = true;
    }

    /**
     * Stop the timer
     */
    public synchronized void stopTimer() {
    	if (!timerStarted) {
    		// Already stopped
    		return;
    	}
    	
    	if (logger.isActivated()) {
    		logger.debug("Stop timer");
    	}

    	// The timer is stopped
		timerStarted = false;
		
		// Cancel alarm
		AlarmManager am = (AlarmManager)AndroidFactory.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		am.cancel(alarmIntent);

		// Unregister the alarm receiver
		try {
			AndroidFactory.getApplicationContext().unregisterReceiver(alarmReceiver);
	    } catch (IllegalArgumentException e) {
	    	// Nothing to do
	    }
    }

    /**
     * Keep alive manager
     */
    private class KeepAlive extends BroadcastReceiver {
    	public void onReceive(Context context, Intent intent) {
    		Thread t = new Thread() {
    			public void run() {
    				// Processing
    				periodicProcessing();
    			}
    		};
    		t.start();
    	}
    }    
}