package com.cmcc.rcs.cpm.core.api.header;

import java.util.Locale;

import com.cmcc.rcs.core.connect.InvalidArgumentException;



public interface AcceptLanguageHeader extends Header, Parameters {
    String NAME = "Accept-Language";

    Locale getAcceptLanguage();
    void setAcceptLanguage(Locale acceptLanguage);
    void setLanguageRange(String languageRange);

    float getQValue();
    void setQValue(float qValue) throws InvalidArgumentException;
    boolean hasQValue();
    void removeQValue();
}
