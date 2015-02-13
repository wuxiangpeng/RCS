package com.cmcc.rcs.cpm.core.api;

/**
 * Terminal information
 * 
 * @author wuxiangpeng
 */
public class TerminalInfo {
	/**
	 * Product name
	 */
	private static final String productName = "OrangeLabs-RCS-client";

	/**
	 * Product version
	 */
	private static String productVersion = "v2.2";
	
	/**
	 * Returns the product name
	 * 
	 * @return Name
	 */
	public static String getProductName() {
		return productName;
	}

	/**
	 * Returns the product version
	 * 
	 * @return Version
	 */
	public static String getProductVersion() {
		return productVersion;
	}

	/**
	 * Set the product version
	 * 
	 * @param version Version
	 */
	public static void setProductVersion(String version) {
		TerminalInfo.productVersion = version;
	}

    /**
     * Returns the product name + version
     *
     * @return product information
     */
    public static String getProductInfo() {
        return productName + "/" + productVersion;
    }
}