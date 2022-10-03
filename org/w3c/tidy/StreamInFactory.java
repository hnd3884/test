package org.w3c.tidy;

import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;

public final class StreamInFactory
{
    private StreamInFactory() {
    }
    
    public static StreamIn getStreamIn(final Configuration configuration, final InputStream inputStream) {
        try {
            return new StreamInJavaImpl(inputStream, configuration.getInCharEncodingName(), configuration.tabsize);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new RuntimeException("Unsupported encoding: " + ex.getMessage());
        }
    }
    
    public static StreamIn getStreamIn(final Configuration configuration, final Reader reader) {
        return new StreamInJavaImpl(reader, configuration.tabsize);
    }
}
