package com.cmcc.rcs.cpm.core.api;

import com.cmcc.rcs.platform.FactoryException;
import com.cmcc.rcs.platform.network.NetworkFactory;

import android.content.Context;


/**
 * Android platform
 * 
 * @author wuxiangpeng
 */
public class AndroidFactory {
	/**
	 * Android application context
	 */
	private static Context context = null;

	/**
	 * Returns the application context
	 * 
	 * @return Context
	 */
	public static Context getApplicationContext() {
		return context;
	}

	/**
	 * Load factory
	 * 
	 * @param context Context
	 */
	public static void setApplicationContext(Context context) {
		AndroidFactory.context = context;
		try {
			NetworkFactory.loadFactory("package com.cmcc.rcs.platform.network.AndroidNetworkFactory");
//			RegistryFactory.loadFactory("com.orangelabs.rcs.platform.registry.AndroidRegistryFactory");
//			FileFactory.loadFactory("com.orangelabs.rcs.platform.file.AndroidFileFactory");
		} catch(FactoryException e) {
			e.printStackTrace();
		}
	}
}
