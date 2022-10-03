package org.apache.http.client.entity;

import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class GZIPInputStreamFactory implements InputStreamFactory
{
    private static final GZIPInputStreamFactory INSTANCE;
    
    public static GZIPInputStreamFactory getInstance() {
        return GZIPInputStreamFactory.INSTANCE;
    }
    
    @Override
    public InputStream create(final InputStream inputStream) throws IOException {
        return new GZIPInputStream(inputStream);
    }
    
    static {
        INSTANCE = new GZIPInputStreamFactory();
    }
}
