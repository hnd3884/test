package org.apache.commons.text.lookup;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

final class UrlEncoderStringLookup extends AbstractStringLookup
{
    static final UrlEncoderStringLookup INSTANCE;
    
    private UrlEncoderStringLookup() {
    }
    
    @Override
    public String lookup(final String key) {
        if (key == null) {
            return null;
        }
        final String enc = StandardCharsets.UTF_8.name();
        try {
            return URLEncoder.encode(key, enc);
        }
        catch (final UnsupportedEncodingException e) {
            throw IllegalArgumentExceptions.format(e, "%s: source=%s, encoding=%s", e, key, enc);
        }
    }
    
    static {
        INSTANCE = new UrlEncoderStringLookup();
    }
}
