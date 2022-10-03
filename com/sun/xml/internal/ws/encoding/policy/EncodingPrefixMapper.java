package com.sun.xml.internal.ws.encoding.policy;

import java.util.HashMap;
import java.util.Map;
import com.sun.xml.internal.ws.policy.spi.PrefixMapper;

public class EncodingPrefixMapper implements PrefixMapper
{
    private static final Map<String, String> prefixMap;
    
    @Override
    public Map<String, String> getPrefixMap() {
        return EncodingPrefixMapper.prefixMap;
    }
    
    static {
        (prefixMap = new HashMap<String, String>()).put("http://schemas.xmlsoap.org/ws/2004/09/policy/encoding", "wspe");
        EncodingPrefixMapper.prefixMap.put("http://schemas.xmlsoap.org/ws/2004/09/policy/optimizedmimeserialization", "wsoma");
        EncodingPrefixMapper.prefixMap.put("http://java.sun.com/xml/ns/wsit/2006/09/policy/encoding/client", "cenc");
        EncodingPrefixMapper.prefixMap.put("http://java.sun.com/xml/ns/wsit/2006/09/policy/fastinfoset/service", "fi");
    }
}
