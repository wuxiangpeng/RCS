package com.cmcc.rcs.core.ims.service;


import com.cmcc.rcs.utils.FifoBuffer;
import com.cmcc.rcs.core.ims.ImsModule;
import com.cmcc.rcs.cpm.message.CPMData;

/**
 * 
 * @author GaoXusong
 *
 */
public class ImsServiceDispatcher  extends Thread  {
    
    private ImsModule mImsModule;
    /**
     * Buffer of messages
     */
    private FifoBuffer buffer = new FifoBuffer();
    
    /**
     * Constructor
     * 
     * @param imsModule IMS module
     */
    public ImsServiceDispatcher(ImsModule imsModule) {
        super("SipDispatcher");
        
        this.mImsModule = imsModule;
    }
    
    /**
     * Terminate the SIP dispatcher
     */
    public void terminate() {      
        buffer.close();       
    }
    
    /**
     * Post a SIP request in the buffer
     * 
     * @param request SIP request
     */
    public void postSipRequest(CPMData cpmData) {
        buffer.addObject(cpmData);
    }
    
    /**
     * Background processing
     */
    public void run() {     
        CPMData cpmData = null; 
        while((cpmData = (CPMData)buffer.getObject()) != null) {
            try {
                // Dispatch the received SIP request
                dispatch(cpmData);
            } catch(Exception e) {
              
            }
        }       
    }
    
    /**
     * Dispatch the received SIP request
     * 
     * @param request SIP request
     */
    private void dispatch(CPMData cpmData) {
        
    }
         
}
