package com.cmcc.rcs.message;

import java.io.Serializable;
import java.util.HashMap;

public class Message  implements Serializable {
    private static final long serialVersionUID = 3935634353571408964L;
    protected static final String TAG = "IM_" + Message.class.getSimpleName();
    public static final int MESSAGE_TYPE_SMS = 0;
    public static final int MESSAGE_TYPE_TEXT = 1;
    public static final int MESSAGE_TYPE_SYS_TEXT = 2;
    public static final int MESSAGE_TYPE_LOCATION = 3;
    public static final int MESSAGE_TYPE_FILE = 4;
    public static final int MESSAGE_TYPE_IMAGE = 5;
    public static final int MESSAGE_TYPE_VOICE = 6;
    public static final int MESSAGE_TYPE_VIDEO = 7;
    public static final int MESSAGE_TYPE_VCARD = 8;
    public static final int MESSAGE_TYPE_TIPS = 10;
    public static final int MESSAGE_TYPE_MEMBER_ENTER = 11;
    public static final int MESSAGE_TYPE_MEMBER_LEAVE = 12;
    public static final int MESSAGE_TYPE_MEMBER_INIVTE = 13;
    public static final int MESSAGE_TYPE_GROUP_INIVTE = 14;
    public static final int MESSAGE_TYPE_GROUP_END = 15;
    public static final int MESSAGE_TYPE_CLEAR_MARK = 16;
    public static final int MESSAGE_TYPE_IP_MESSAGE = 17;
    protected static final int MESSAGE_TYPE_TEXT_OFFLINE_STORE = 18;
    public static final int MESSAGE_TYPE_TEXT_OFFLINE_SMS = 19;
    public static final int MESSAGE_TYPE_TEXT_FORCE_SMS = 20;
    public static final int MESSAGE_TYPE_UNKNOW = -1;
    public static final int STATUS_DRAFT = 2;
    public static final int STATUS_PROGRESSING = 32;
    public static final int STATUS_SEND_LAST = 128;
    public static final int STATUS_SEND_FAILED = 64;
    public static final int STATUS_SEND_OK = 16;
    public static final int STATUS_DELIVERY_OK = 8;
    public static final int STATUS_DISPLAY_OK = 0;
    public static final int STATUS_RECV_FAILED = 64;
    public static final int STATUS_RECV_OK = 4;
    public static final int STATUS_READ = 0;
    public static final int CHAT_TYPE_CUSTOM = 4;
    public static final int CHAT_TYPE_MASS = 3;
    public static final int CHAT_TYPE_GROUP = 2;
    public static final int CHAT_TYPE_SINGLE = 1;
    protected long keyId;
    protected boolean isSender;   
    protected String globalMsgId;
    protected String globalMsgTime;
    protected long dateTime = 0L;

    protected long localDateTime = 0L;
    protected int type;
    protected String customType;
    protected int status;
    protected int chatType;
    protected String body;
    private long conversationId;
    private String contributionId;
    private String replyToContributionId;
    private String replyTo;
    private HashMap<Object, Object> extendDatas;
    
    
}
