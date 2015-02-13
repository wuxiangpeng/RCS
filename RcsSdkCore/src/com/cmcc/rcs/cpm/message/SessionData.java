package com.cmcc.rcs.cpm.message;




/**
 * Call-Id
 * From
 * To
 * ConversationId and MessageId 
 * 
 * @author GaoXusong
 *
 */
public class SessionData {
    private String mcallId;
    private From   mFrom;
    private To     mTo;
    private String mConversationId;
    private String mMessageId;
    private int    mCseqNum;
	private String localIpAddress;  //Local IP address
    
    
    public String getCallId() {
		mcallId = IdGenerator.getIdentifier() + "@" + localIpAddress;
    	return mcallId;
    }
    
    public void setCallId(String callId) {
        this.mcallId = callId;
    }
    
    public From getFrom() {
        return mFrom;
    }
    
    public void setFrom(From from) {
        this.mFrom = from;
    }

    public To getTo() {
        return mTo;
    }

    public void setTo(To to) {
        this.mTo = to;
    }

    public String getConversationId() {
        return mConversationId;
    }

    public void setConversationId(String conversationId) {
        this.mConversationId = conversationId;
    }

    public String getMessageId() {
    	mMessageId = IdGenerator.generateMessageID();
        return mMessageId;
    }

    public void setMessageId(String messageId) {
        this.mMessageId = messageId;
    }

    public int getCseqNum() {
        return mCseqNum;
    }

    public void setCseqNum(int cseqNum) {
        this.mCseqNum = cseqNum;
    }
    
    
    
}
