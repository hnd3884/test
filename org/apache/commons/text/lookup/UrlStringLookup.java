package org.apache.commons.text.lookup;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.StringWriter;
import java.net.URL;

final class UrlStringLookup extends AbstractStringLookup
{
    static final UrlStringLookup INSTANCE;
    
    private UrlStringLookup() {
    }
    
    @Override
    public String lookup(final String key) {
        if (key == null) {
            return null;
        }
        final String[] keys = key.split(UrlStringLookup.SPLIT_STR);
        final int keyLen = keys.length;
        if (keyLen < 2) {
            throw IllegalArgumentExceptions.format("Bad URL key format [%s]; expected format is DocumentPath:Key.", key);
        }
        final String charsetName = keys[0];
        final String urlStr = this.substringAfter(key, ':');
        try {
            final URL url = new URL(urlStr);
            final int size = 8192;
            final StringWriter writer = new StringWriter(8192);
            final char[] buffer = new char[8192];
            try (final InputStreamReader reader = new InputStreamReader(new BufferedInputStream(url.openStream()), charsetName)) {
                int n;
                while (-1 != (n = reader.read(buffer))) {
                    writer.write(buffer, 0, n);
                }
            }
            return writer.toString();
        }
        catch (final Exception e) {
            throw IllegalArgumentExceptions.format(e, "Error looking up URL [%s] with Charset [%s].", urlStr, charsetName);
        }
    }
    
    static {
        INSTANCE = new UrlStringLookup();
    }
}
