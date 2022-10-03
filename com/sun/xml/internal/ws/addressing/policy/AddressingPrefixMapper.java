package com.sun.xml.internal.ws.addressing.policy;

import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import java.util.HashMap;
import java.util.Map;
import com.sun.xml.internal.ws.policy.spi.PrefixMapper;

public class AddressingPrefixMapper implements PrefixMapper
{
    private static final Map<String, String> prefixMap;
    
    @Override
    public Map<String, String> getPrefixMap() {
        return AddressingPrefixMapper.prefixMap;
    }
    
    static {
        (prefixMap = new HashMap<String, String>()).put(AddressingVersion.MEMBER.policyNsUri, "wsap");
        AddressingPrefixMapper.prefixMap.put(AddressingVersion.MEMBER.nsUri, "wsa");
        AddressingPrefixMapper.prefixMap.put("http://www.w3.org/2007/05/addressing/metadata", "wsam");
    }
}
