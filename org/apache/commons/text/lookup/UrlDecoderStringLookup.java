package org.apache.commons.text.lookup;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

final class UrlDecoderStringLookup extends AbstractStringLookup
{
    static final UrlDecoderStringLookup INSTANCE;
    
    private UrlDecoderStringLookup() {
    }
    
    @Override
    public String lookup(final String key) {
        if (key == null) {
            return null;
        }
        final String enc = StandardCharsets.UTF_8.name();
        try {
            return URLDecoder.decode(key, enc);
        }
        catch (final UnsupportedEncodingException e) {
            throw IllegalArgumentExceptions.format(e, "%s: source=%s, encoding=%s", e, key, enc);
        }
    }
    
    static {
        INSTANCE = new UrlDecoderStringLookup();
    }
}
