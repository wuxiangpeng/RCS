package com.cmcc.rcs.message;

import java.io.Serializable;

import android.content.Intent;

public abstract class Conversation implements Serializable {
    private        static final long     serialVersionUID          = 3935634353571408964L;
    protected static final String TAG                       = "IMConversation";
    public          static final String    PARAM_SEND_TEXT_LONGITUDE = "longitude";
    public          static final String    PARAM_SEND_TEXT_LATITUDE  = "latitude";
    public          static final String    PARAM_SEND_TEXT_ACCURACY  = "accuracy";
    protected static final String PARAM_SEND_TEXT_BODY      = "body";
    private        static final int      TEXT_LOCATION_MSG_LENGTH  = 200;  
    
    protected  long            threadId                  = 0L;
    protected  int               chatType                  = 1;
    protected  Message    lastMsg;
    protected  int               msgCount;
    protected  int               unReadMsgCount;
    private        long            time;
    protected String           numbers;
    protected long             msgKeyId;
  
    
    protected void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    protected long getThreadId() {
        return this.threadId;
    }

    public long getKeyId() {
        return this.threadId;
    }

    public int getChatType() {
        return this.chatType;
    }
    
    public abstract TextMessage sendText(String msg);

    public abstract TextMessage sendText(String msg, Intent intent);
}
