package com.cmcc.rcs.provider.configure;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;



public class CPMConfigure {
    private static final String TAG = "CPMConfigure";
    
    private Context mContext;
    
    public static final String DATA_PREFS_SAVE = "DataSave";
    
    /**
     * Current instance
     */
    private static CPMConfigure instance = null;    
    
    /**
     * Create instance
     *
     * @param ctx Context
     */
    public static synchronized void createInstance(Context ctx) {
        if (instance == null) {
            instance = new CPMConfigure(ctx);
        }
    }

    /**
     * Returns instance
     *
     * @return Instance
     */
    public static CPMConfigure getInstance() {
        return instance;
    }
    
    /**
     * Constructor
     *
     * @param ctx Application context
     */
    private CPMConfigure(Context ctx) {
        super();

        this.mContext = ctx;
    }
    
    /**
     * Read a parameter
     *
     * @param key Key
     * @return Value
     */
    public String readParameter(String key) {
        
        if ( key == null ) {
            return null;
        }

        String result = null;
        SharedPreferences  sharedPreferences = mContext.getSharedPreferences(DATA_PREFS_SAVE, Context.MODE_PRIVATE);
        result = sharedPreferences.getString(key, null);  
        
        Log.d(TAG, " readParameter result = " + result);
        
        return result;
    }

    /**
     * Write a parameter
     *
     * @param key Key
     * @param value Value
     */
    public void writeParameter(String key, String value) {
        
        if ( (key == null) || (value == null) ) {
            return;
        }        
      
        Log.d(TAG, " readParameter key = " + key + " value = " + value);
        
        SharedPreferences  sharedPreferences = mContext.getSharedPreferences(DATA_PREFS_SAVE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    
    
  
}
