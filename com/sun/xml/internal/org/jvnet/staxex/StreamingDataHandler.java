package com.sun.xml.internal.org.jvnet.staxex;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataSource;
import java.net.URL;
import java.io.Closeable;
import javax.activation.DataHandler;

public abstract class StreamingDataHandler extends DataHandler implements Closeable
{
    public StreamingDataHandler(final Object o, final String s) {
        super(o, s);
    }
    
    public StreamingDataHandler(final URL url) {
        super(url);
    }
    
    public StreamingDataHandler(final DataSource dataSource) {
        super(dataSource);
    }
    
    public abstract InputStream readOnce() throws IOException;
    
    public abstract void moveTo(final File p0) throws IOException;
    
    @Override
    public abstract void close() throws IOException;
}
