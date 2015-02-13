package com.cmcc.rcs.core.connect;




public interface RawMessageChannel {

    public abstract void processMessage(SIPMessage sipMessage) throws Exception ;

}
