package org.apache.commons.text.lookup;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

final class Base64DecoderStringLookup extends AbstractStringLookup
{
    static final Base64DecoderStringLookup INSTANCE;
    
    private Base64DecoderStringLookup() {
    }
    
    @Override
    public String lookup(final String key) {
        if (key == null) {
            return null;
        }
        return new String(Base64.getDecoder().decode(key), StandardCharsets.ISO_8859_1);
    }
    
    static {
        INSTANCE = new Base64DecoderStringLookup();
    }
}
