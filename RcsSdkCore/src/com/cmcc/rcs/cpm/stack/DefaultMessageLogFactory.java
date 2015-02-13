package com.cmcc.rcs.cpm.stack;

import java.net.InetAddress;

import com.cmcc.rcs.cpm.LogRecord;
import com.cmcc.rcs.cpm.LogRecordFactory;



/**
 * The Default Message log factory. This can be replaced as a stack
 * configuration parameter.
 *
 * @author GaoXusong
 *
 */
public class DefaultMessageLogFactory implements LogRecordFactory {

    public LogRecord createLogRecord(String message, String source,
            String destination, String timeStamp, boolean isSender,
            String firstLine, String tid, String callId, long tsHeaderValue) {
        return new MessageLog(message, source, destination, timeStamp,
                isSender, firstLine, tid, callId, tsHeaderValue);
    }

    public LogRecord createLogRecord(String message, String source,
            String destination, long timeStamp, boolean isSender,
            String firstLine, String tid, String callId, long timestampVal) {
        return new MessageLog(message, source, destination, timeStamp,
                isSender, firstLine, tid, callId, timestampVal);
    }

    @Override
    public LogRecord createLogRecord(String encode, InetAddress hopAddr,
            String valueOf, long time) {
        // TODO Auto-generated method stub
        return null;
    }

}
