package com.cmcc.rcs.cpm;

import com.cmcc.rcs.cpm.message.CPMData;

/**
 * 
 * @author GaoXusong
 *
 */

public interface SendEventListener {
    void processSendEvent(String callId, String messageBody, 
                          String host, String port);
}
