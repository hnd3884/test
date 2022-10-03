package org.apache.commons.text.lookup;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

final class Base64EncoderStringLookup extends AbstractStringLookup
{
    static final Base64EncoderStringLookup INSTANCE;
    
    private Base64EncoderStringLookup() {
    }
    
    @Override
    public String lookup(final String key) {
        if (key == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.ISO_8859_1));
    }
    
    static {
        INSTANCE = new Base64EncoderStringLookup();
    }
}
