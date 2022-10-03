package org.apache.commons.compress.archivers.zip;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.charset.Charset;

public abstract class ZipEncodingHelper
{
    static final String UTF8 = "UTF8";
    static final ZipEncoding UTF8_ZIP_ENCODING;
    
    public static ZipEncoding getZipEncoding(final String name) {
        Charset cs = Charset.defaultCharset();
        if (name != null) {
            try {
                cs = Charset.forName(name);
            }
            catch (final UnsupportedCharsetException ex) {}
        }
        final boolean useReplacement = isUTF8(cs.name());
        return new NioZipEncoding(cs, useReplacement);
    }
    
    static boolean isUTF8(String charsetName) {
        if (charsetName == null) {
            charsetName = Charset.defaultCharset().name();
        }
        if (StandardCharsets.UTF_8.name().equalsIgnoreCase(charsetName)) {
            return true;
        }
        for (final String alias : StandardCharsets.UTF_8.aliases()) {
            if (alias.equalsIgnoreCase(charsetName)) {
                return true;
            }
        }
        return false;
    }
    
    static ByteBuffer growBufferBy(final ByteBuffer buffer, final int increment) {
        buffer.limit(buffer.position());
        buffer.rewind();
        final ByteBuffer on = ByteBuffer.allocate(buffer.capacity() + increment);
        on.put(buffer);
        return on;
    }
    
    static {
        UTF8_ZIP_ENCODING = getZipEncoding("UTF8");
    }
}
