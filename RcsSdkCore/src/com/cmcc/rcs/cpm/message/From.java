package com.cmcc.rcs.cpm.message;

import com.cmcc.rcs.provider.configure.CPMConfigure;
import com.cmcc.rcs.provider.configure.CPMConfigureData;

import android.content.Context;
import android.content.SharedPreferences;

public final class From {
    private String mFromName;
    private String mFromUri;
    private String mFromTag;
    private Context mContext;
    
    SharedPreferences preferences = mContext.getSharedPreferences(CPMConfigure.DATA_PREFS_SAVE, mContext.MODE_PRIVATE);

    public String getSzFromName() {
    	mFromName = preferences.getString(CPMConfigureData.CPM_FROM_ADDRESS, "");
        return mFromName;
    }
    
    
    public void setSzFromName(String fromName) {
        this.mFromName = fromName;
    }
    
    
    public String getFromUri() {
    	mFromUri = "sip:" + mFromName + "@" + preferences.getString(CPMConfigureData.CPM_SERVER_DOMAIN, "");
        return mFromUri;
    }
    
    
    public void setFromUri(String fromUri) {
        this.mFromUri = fromUri;
    }


    public String getFromTag() {
        return mFromTag;
    }


    public void setFromTag(String fromTag) {
        this.mFromTag = fromTag;
    }    
    
}
