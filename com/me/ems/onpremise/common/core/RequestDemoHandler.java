package com.me.ems.onpremise.common.core;

import java.util.Properties;
import java.util.List;
import java.util.Map;

public interface RequestDemoHandler
{
    public static final String[] CONSENT_NEEDED_COUNTRIES = { "UK", "GB", "SE", "SI", "LU", "LV", "IE", "HU", "FR", "FI", "EE", "AD", "AL", "AT", "AX", "BA", "BE", "BG", "BY", "CH", "CY", "CZ", "DE", "DK", "ES", "FO", "GG", "GI", "GR", "HR", "IM", "IS", "IT", "JE", "XK", "LI", "LT", "MC", "MD", "ME", "MK", "MT", "NL", "NO", "PL", "PT", "RO", "RS", "RU", "SJ", "SK", "SM", "UA", "VA", "CS", "AU", "CA" };
    
    int registerRequestDemo(final Map<String, Object> p0) throws Exception;
    
    List<Map<String, Object>> getCountries() throws Exception;
    
    Map<String, Object> productSpecificHandling(final Map<String, Object> p0) throws Exception;
    
    Properties getAdditionalPropsToPost(final Map<String, Object> p0) throws Exception;
}
