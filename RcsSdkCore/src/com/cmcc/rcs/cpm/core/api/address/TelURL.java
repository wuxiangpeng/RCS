package com.cmcc.rcs.cpm.core.api.address;

import java.text.ParseException;

import com.cmcc.rcs.cpm.core.api.header.Parameters;


public interface TelURL extends URI, Parameters {
    String getIsdnSubAddress();
    void setIsdnSubAddress(String isdnSubAddress) throws ParseException;

    String getPhoneContext();
    void setPhoneContext(String phoneContext) throws ParseException;

    String getPhoneNumber();
    void setPhoneNumber(String phoneNumber) throws ParseException;

    String getPostDial();
    void setPostDial(String postDial) throws ParseException;

    boolean isGlobal();
    void setGlobal(boolean global);
}
