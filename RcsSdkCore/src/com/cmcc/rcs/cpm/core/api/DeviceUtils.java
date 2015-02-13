package com.cmcc.rcs.cpm.core.api;

import java.util.UUID;

import com.cmcc.rcs.provider.settings.RcsSettings;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;


/***
 * Device utility functions
 * 
 * @author wuxiangpeng
 */
public class DeviceUtils {
	/**
	 * UUID
	 */
	private static UUID uuid = null;

	/**
	 * Returns unique UUID of the device
	 * 
	 * @param context Context 
	 * @return UUID
	 */
    public static UUID getDeviceUUID(Context context) {
		if (context == null) {
			return null;
		}

        if (uuid == null) {
            String imei = getImei(context);
            if (imei == null) {
                // For compatibility with device without telephony
                imei = getSerial();
            }
            if (imei != null) {
                uuid = UUID.nameUUIDFromBytes(imei.getBytes());
            }
		}

		return uuid;
	}

	/**
	 * Returns the serial number of the device. Only works from OS version Gingerbread.
	 * 
	 * @return Serial number
	 */
	private static String getSerial() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			return android.os.Build.SERIAL;
		} else {
			return null;
		}
	}

    /**
     * Returns instance ID
     *
     * @param context application context
     * @return instance Id
     */
    public static String getInstanceId(Context context) {
        if (context == null) {
            return null;
        }

        String instanceId = null;
        if (RcsSettings.getInstance().isImeiUsedAsDeviceId()) {
            String imei = getImei(context);
            if (imei != null) { 
                instanceId = "\"<urn:gsma:imei:" + imei + ">\"";
            }
        } else {
            UUID uuid = getDeviceUUID(context);
            if (uuid != null) {
                instanceId = "\"<urn:uuid:" + uuid.toString() + ">\"";
            }
        }
        return instanceId;
    }

    /**
     * Returns the IMEI of the device
     *
     * @param context application context
     * @return IMEI of the device
     */
    private static String getImei(Context context) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }
}