package com.cmcc.rcs.cpm.core.api;

import com.cmcc.rcs.core.ims.ImsModule;
import com.cmcc.rcs.provider.settings.RcsSettings;

import android.content.Context;
import android.telephony.PhoneNumberUtils;


/**
 * Phone utility functions
 * 
 * @author jexa7410
 */
public class PhoneUtils {
	/**
	 * Tel-URI format
	 */
	private static boolean TEL_URI_SUPPORTED = true;

	/**
	 * Country code
	 */
	private static String COUNTRY_CODE = "+33";
	
	/**
	 * Country area code
	 */
	private static String COUNTRY_AREA_CODE = "0";
	
	/**
	 * Set the country code
	 * 
	 * @param context Context
	 */
	public static synchronized void initialize(Context context) {
		RcsSettings.createInstance(context);
		TEL_URI_SUPPORTED = RcsSettings.getInstance().isTelUriFormatUsed();
		COUNTRY_CODE = RcsSettings.getInstance().getCountryCode();
		COUNTRY_AREA_CODE = RcsSettings.getInstance().getCountryAreaCode();
	}

	/**
	 * Returns the country code
	 * 
	 * @return Country code
	 */
	public static String getCountryCode() {
		return COUNTRY_CODE;
	}
	
	/**
	 * Format a phone number to international format
	 * 
	 * @param number Phone number
	 * @return International number
	 */
	public static String formatNumberToInternational(String number) {
		if (number == null) {
			return null;
		}
		
		// Remove spaces
		number = number.trim();

		// Strip all non digits
		String phoneNumber = PhoneNumberUtils.stripSeparators(number);

		// Format into international
		if (phoneNumber.startsWith("00" + COUNTRY_CODE.substring(1))) {
			// International format
			phoneNumber = COUNTRY_CODE + phoneNumber.substring(4);
		} else
		if ((COUNTRY_AREA_CODE != null) && (COUNTRY_AREA_CODE.length() > 0) &&
				phoneNumber.startsWith(COUNTRY_AREA_CODE)) {
			// National number with area code
			phoneNumber = COUNTRY_CODE + phoneNumber.substring(COUNTRY_AREA_CODE.length());
		} else
		if (!phoneNumber.startsWith("+")) {
			// National number
			phoneNumber = COUNTRY_CODE + phoneNumber;
		}
		return phoneNumber;
	}
	
	/**
	 * Format a phone number to a SIP URI
	 * 
	 * @param number Phone number
	 * @return SIP URI
	 */
	public static String formatNumberToSipUri(String number) {
		if (number == null) {
			return null;
		}

		// Remove spaces
		number = number.trim();
		
		// Extract username part
		if (number.startsWith("tel:")) {
			number = number.substring(4);
		} else		
		if (number.startsWith("sip:")) {
			number = number.substring(4, number.indexOf("@"));
		}
		
		if (TEL_URI_SUPPORTED) {
			// Tel-URI format
			return "tel:" + formatNumberToInternational(number);
		} else {
			// SIP-URI format
			return "sip:" + formatNumberToInternational(number) + "@" +
				ImsModule.IMS_USER_PROFILE.getHomeDomain() + ";user=phone";	 
		}
	}

	/**
	 * Extract user part phone number from a SIP-URI or Tel-URI or SIP address
	 * 
	 * @param uri SIP or Tel URI
	 * @return Number or null in case of error
	 */
	public static String extractNumberFromUri(String uri) {
		if (uri == null) {
			return null;
		}

		try {
			// Extract URI from address
			int index0 = uri.indexOf("<");
			if (index0 != -1) {
				uri = uri.substring(index0+1, uri.indexOf(">", index0));
			}			
			
			// Extract a Tel-URI
			int index1 = uri.indexOf("tel:");
			if (index1 != -1) {
				uri = uri.substring(index1+4);
			}
			
			// Extract a SIP-URI
			index1 = uri.indexOf("sip:");
			if (index1 != -1) {
				int index2 = uri.indexOf("@", index1);
				uri = uri.substring(index1+4, index2);
			}
			
			// Remove URI parameters
			int index2 = uri.indexOf(";"); 
			if (index2 != -1) {
				uri = uri.substring(0, index2);
			}
			
			// Format the extracted number (username part of the URI)
			return formatNumberToInternational(uri);
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * Compare phone number between two contacts
	 * 
	 * @param contact1 First contact
	 * @param contact2 Second contact
	 * @return Returns true if numbers are equals
	 */
	public static boolean compareNumbers(String contact1, String contact2) {
		String number1 = PhoneUtils.extractNumberFromUri(contact1);
		String number2 = PhoneUtils.extractNumberFromUri(contact2);
		if ((number1 == null) || (number2 == null)) {
			return false;
		}
		return number1.equals(number2);
	}
	
	/**
	 * Check if phone number is valid
	 * 
	 * <pre>
	 * <br>
	 * It is not valid if : 
	 * <li>well formatted (not digits only or '+') 
	 * <li>minimum length
	 * </pre>
	 * 
	 * @param phone
	 *            Phone number
	 * @return Boolean true if it is a phone valid number
	 */
	public static boolean isGlobalPhoneNumber(final String phone) {
		if (phone == null) {
			return false;
		}
		if (PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
			if (phone.length() > PhoneUtils.getCountryCode().length()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Clean URI from '<' heading character or '>' trailing character
	 * 
	 * @param uri
	 *            the identity
	 * @return the cleaned Uri
	 */
	public static String cleanUriHeadingTrailingChar(String uri) {
		if (uri == null)
			return uri;
		return uri.replaceAll("^<|>$", "");
	}
}

