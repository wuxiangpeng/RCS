/*******************************************************************************
 * Software Name : RCS IMS Stack
 *
 * Copyright (C) 2010 France Telecom S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.cmcc.rcs.service;

import java.util.Date;

import com.cmcc.rcs.utils.logger.Logger;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;



/***
 * Launcher utility functions
 * @author GaoXusong
 *
 */
public class LauncherUtils {
	private static final String TAG ="LauncherUtils";
	
	private static StartLauncherManager startLauncher  = null;
	private static RcsCoreManager       rcsCoreManager = null;
	
	/**
	 * Launch the RCS CPM start launcher manager
	 * 
	 * @param context
	 * @param boot
	 *            start RCS service upon boot
	 * @param user
	 *            start RCS service upon user action
	 */
	public static StartLauncherManager launchStartLauncherManager(Context context, 
			                                       boolean boot, boolean user) {        
		StartLauncherManager.createInstance(context, boot, user);
		StartLauncherManager launcher =  StartLauncherManager.getInstance();
		return launcher;
	}
	
	 /**
     * Launch the RCS core service
     *
     * @param context Application context
     */
    public static void launchRcsCoreManager(Context context) {        
        Log.i(TAG, "Launch core service");
        rcsCoreManager = new RcsCoreManager(context);
       
      
    }
    
    /**
     * Stop the RCS CPM start launcher manager
     *
     * @param context Application context
     */
    public static void stopStartLauncherManager(Context context) {        
        Log.i(TAG, "Stop RCS service");
        startLauncher.stopStartLauncher(); 
        rcsCoreManager.stopCore();
    }
    
    /**
     * Stop the RCS core service (but keep provisioning)
     *
     * @param context Application context
     */
    public static void stopRcsCoreManager( Context context) {        
        Log.i(TAG, "Stop RCS core service");
        rcsCoreManager.stopCore();
    }
}
