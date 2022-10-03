package com.sun.xml.internal.ws.config.management.policy;

import java.util.HashMap;
import java.util.Map;
import com.sun.xml.internal.ws.policy.spi.PrefixMapper;

public class ManagementPrefixMapper implements PrefixMapper
{
    private static final Map<String, String> prefixMap;
    
    @Override
    public Map<String, String> getPrefixMap() {
        return ManagementPrefixMapper.prefixMap;
    }
    
    static {
        (prefixMap = new HashMap<String, String>()).put("http://java.sun.com/xml/ns/metro/management", "sunman");
    }
}
