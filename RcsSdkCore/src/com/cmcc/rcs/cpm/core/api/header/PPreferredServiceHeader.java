package com.cmcc.rcs.cpm.core.api.header;


/**
*
* @author aayush.bhatnagar
*
* The ABNF for this header is all follows:
*
*  PPreferredService = "P-Preferred-Service"
*                       HCOLON PPreferredService-value
*
*  PPreferredService-value = Service-ID *(COMMA Service-ID)
*
*  where,
*
*     Service-ID      = "urn:urn-7:" urn-service-id
*     urn-service-id  = top-level *("." sub-service-id)
*     top-level       = let-dig [ *26let-dig ]
*     sub-service-id  = let-dig [ *let-dig ]
*     let-dig         = ALPHA / DIGIT / "-"
*
* Egs: P-Preferred-Service: urn:urn-7:3gpp-service.exampletelephony.version1
*      P-Preferred-Service: urn:urn-7:3gpp-application.exampletelephony.version1
*
*/
public interface PPreferredServiceHeader extends Header{

   public static final String NAME = "P-Preferred-Service";

   public void setSubserviceIdentifiers(String subservices);

   public String getSubserviceIdentifiers();

   public void setApplicationIdentifiers(String appids);

   public String getApplicationIdentifiers();


}
