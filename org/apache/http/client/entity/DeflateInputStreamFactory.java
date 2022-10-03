package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DeflateInputStreamFactory implements InputStreamFactory
{
    private static final DeflateInputStreamFactory INSTANCE;
    
    public static DeflateInputStreamFactory getInstance() {
        return DeflateInputStreamFactory.INSTANCE;
    }
    
    @Override
    public InputStream create(final InputStream inputStream) throws IOException {
        return new DeflateInputStream(inputStream);
    }
    
    static {
        INSTANCE = new DeflateInputStreamFactory();
    }
}
