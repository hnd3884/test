package com.sun.xml.internal.ws.encoding;

import java.io.OutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataSource;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import com.sun.xml.internal.ws.developer.StreamingDataHandler;

public class MIMEPartStreamingDataHandler extends StreamingDataHandler
{
    private final StreamingDataSource ds;
    
    public MIMEPartStreamingDataHandler(final MIMEPart part) {
        super(new StreamingDataSource(part));
        this.ds = (StreamingDataSource)this.getDataSource();
    }
    
    @Override
    public InputStream readOnce() throws IOException {
        return this.ds.readOnce();
    }
    
    @Override
    public void moveTo(final File file) throws IOException {
        this.ds.moveTo(file);
    }
    
    @Override
    public void close() throws IOException {
        this.ds.close();
    }
    
    private static final class StreamingDataSource implements DataSource
    {
        private final MIMEPart part;
        
        StreamingDataSource(final MIMEPart part) {
            this.part = part;
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            return this.part.read();
        }
        
        InputStream readOnce() throws IOException {
            try {
                return this.part.readOnce();
            }
            catch (final Exception e) {
                throw new MyIOException(e);
            }
        }
        
        void moveTo(final File file) throws IOException {
            this.part.moveTo(file);
        }
        
        @Override
        public OutputStream getOutputStream() throws IOException {
            return null;
        }
        
        @Override
        public String getContentType() {
            return this.part.getContentType();
        }
        
        @Override
        public String getName() {
            return "";
        }
        
        public void close() throws IOException {
            this.part.close();
        }
    }
    
    private static final class MyIOException extends IOException
    {
        private final Exception linkedException;
        
        MyIOException(final Exception linkedException) {
            this.linkedException = linkedException;
        }
        
        @Override
        public Throwable getCause() {
            return this.linkedException;
        }
    }
}
