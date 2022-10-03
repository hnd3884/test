package com.zoho.security.validator.url;

import java.util.HashMap;
import java.util.Map;

public class CustomScheme extends Scheme
{
    private Map<String, Scheme> schemeMap;
    
    public CustomScheme(final String name) {
        this.setSchemeName(name);
        this.setURLComponents(DefaultConfiguration.DEFAULT_URL_VALIDATOR_CONFIGURATION.domainAuthority, DefaultConfiguration.DEFAULT_URL_VALIDATOR_CONFIGURATION.pathInfo, DefaultConfiguration.DEFAULT_URL_VALIDATOR_CONFIGURATION.queryString, DefaultConfiguration.DEFAULT_URL_VALIDATOR_CONFIGURATION.fragment);
    }
    
    public void addScheme(final Scheme scheme) {
        if (this.schemeMap == null) {
            this.schemeMap = new HashMap<String, Scheme>();
        }
        this.schemeMap.put(scheme.getSchemeName(), scheme);
    }
    
    public Scheme getScheme(final String schemeName) {
        return (this.schemeMap == null) ? null : this.schemeMap.get(schemeName);
    }
}
