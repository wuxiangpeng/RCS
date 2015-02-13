package com.cmcc.rcs.cpm;


import java.util.HashMap;
import java.util.LinkedList;
import android.util.Log;

import com.cmcc.rcs.cpm.message.CPMData;
import com.cmcc.rcs.cpm.stack.MessageChannel;

public class SendEventScanner implements Runnable {
    private static final String TAG = "SendEventScanner";
    
    private static final int SLEEP_TIMER = 200;
    
    private boolean isRunning;

    private int refCount;
    
    private static SendEventScanner mSendEventScanner = null;  

    // SIPquest: Fix for deadlocks
    private LinkedList pendingEvents = new LinkedList();

    private int[] eventMutex = { 0 };

    //private CPMStackImpl sipStack;
    
    private MessageChannel mMessageChannel;
    
    public void incrementRefcount() {
        synchronized (eventMutex) {
            this.refCount++;
        }
    }
    
    public SendEventScanner(MessageChannel messageChannel) {
        this.pendingEvents = new LinkedList();
        mMessageChannel = messageChannel;
        Thread myThread = new Thread(this);
        // This needs to be set to false else the
        // main thread mysteriously exits.
        myThread.setDaemon(false);

        //this.sipStack = sipStackImpl;

        myThread.setName("SendEventScannerThread");

        myThread.start();

    }
    
    public static SendEventScanner getInstance(MessageChannel messageChannel) {
        
        if (mSendEventScanner == null) {  
            mSendEventScanner = new SendEventScanner(messageChannel);  
        } 
        
        return mSendEventScanner;  
    }  
    
    public void addEvent(CPMData cpmData) {       
        Log.i(TAG, "addEvent " + cpmData.toString() );
        synchronized (this.eventMutex) {

            pendingEvents.add(cpmData);

            // Add the event into the pending events list

            eventMutex.notify();
        }

    }
    
    /**
     * Stop the event scanner. Decrement the reference count and exit the
     * scanner thread if the ref count goes to 0.
     */

    public void stop() {
        if (!isRunning)
            return;
        isRunning = false;
    }
 

   
    
    public void deliverEvent() {
        String callID = null;
        String messageBody = null;
        String host = null;
        String port = null;
        
        Log.i(TAG, "deliverEvent " );
       
        //this here to call C/C++ JNI about TanLinLin
//        jint JniRecvNetPack(jstring callId, String host, jstring RcvPort, jstring RcvString);
        
        Log.i(TAG, "deliverEvent callID = " + callID + " host = " + host
              + " messageBody = " + messageBody + " port = " + port  );
       
        if (   (callID != null)
            && (messageBody != null) 
            && (host != null) 
            && (port != null) ) {
            mMessageChannel.processSendEvent(callID, messageBody, host, port);
        }             
    }
    
    @Override
    public void run() {        
        while (this.isRunning) {
            deliverEvent();            
            try {
                this.wait(SLEEP_TIMER);
            } catch (InterruptedException e) {               
                e.printStackTrace();
            }
        }
    }
    
}
