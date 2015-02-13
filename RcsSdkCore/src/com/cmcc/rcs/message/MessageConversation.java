package com.cmcc.rcs.message;

import android.content.Intent;
import android.net.Uri;

public class MessageConversation extends Conversation {
    private static final String TAG = "IMMessageConversation";
    public static Uri MESSAGE_CONTENT_URI = SmsTable.Sms.CONTENT_URI;
    private static final long serialVersionUID = -4121164071934421838L;
    public static final String PARAM_SEND_TEXT_SAVE_DB = "isSaveDB";
    public static final String PARAM_SEND_TEXT_PAGE_MODE = "isPageMode";
    public static final String PARAM_SEND_TEXT_IP_PAGE_MESSAGE = "isIpMessage";
    public static final String PARAM_SEND_TEXT_REPLY_TO = "replyTo";
    public static final String PARAM_SEND_TEXT_REPLY_TO_CONTRIBUTION_ID = "replyToContributionId";
    public static final String PARAM_SEND_TEXT_SEND_MODE = "textSendMode";
    protected static final int TEXT_SEND_MODE_OFFLINE_STORE = 1;
    public static final int TEXT_SEND_MODE_OFFLINE_SMS = 2;
    public static final int TEXT_SEND_MODE_FORCE_SMS = 3;
    
    protected MessageConversation()  {
    }

    protected MessageConversation(String contact, Message lastmsg, int count,
            int unreadCount, long threadid)  {
        setNumbers(contact);
        this.lastMsg = lastmsg;
        this.msgCount = count;
        this.unReadMsgCount = unreadCount;
        this.threadId = threadid;
    }
    
    public String toString() {
        StringBuilder sBuilder = new StringBuilder(50);
        
        sBuilder.append("MessageConversation thread:").append(this.threadId)
                .append(",number:").append(this.numbers).append(",msgCount:")
                .append(this.msgCount).append(",unReadCount:")
                .append( getUnreadMessageCount() ).append(",body:")
                .append(this.lastMsg == null ? "null" : this.lastMsg.getBody() );
        return sBuilder.toString();
    }
    
    @Override
    public TextMessage  sendText(String msg) {
        return sendText(msg, null);
    }

    @Override
    public TextMessage  sendText(String msg, Intent intent) {
        return null;
    }

}
