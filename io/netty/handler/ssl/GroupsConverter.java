package io.netty.handler.ssl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class GroupsConverter
{
    private static final Map<String, String> mappings;
    
    static String toOpenSsl(final String key) {
        final String mapping = GroupsConverter.mappings.get(key);
        if (mapping == null) {
            return key;
        }
        return mapping;
    }
    
    private GroupsConverter() {
    }
    
    static {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("secp224r1", "P-224");
        map.put("prime256v1", "P-256");
        map.put("secp256r1", "P-256");
        map.put("secp384r1", "P-384");
        map.put("secp521r1", "P-521");
        map.put("x25519", "X25519");
        mappings = Collections.unmodifiableMap((Map<? extends String, ? extends String>)map);
    }
}
