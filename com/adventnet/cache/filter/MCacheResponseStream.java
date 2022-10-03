package com.adventnet.cache.filter;

import javax.servlet.WriteListener;
import java.util.logging.Level;
import java.io.IOException;
import java.util.logging.Logger;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;

public class MCacheResponseStream extends ServletOutputStream
{
    private OutputStream stream;
    private static final Logger LOG;
    
    public MCacheResponseStream(final OutputStream stream) throws IOException {
        this.stream = null;
        this.stream = stream;
    }
    
    public void write(final int b) throws IOException {
        this.stream.write(b);
    }
    
    public void write(final byte[] b) throws IOException {
        this.stream.write(b);
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.stream.write(b, off, len);
    }
    
    public void close() throws IOException {
        MCacheResponseStream.LOG.log(Level.INFO, "close is called");
        super.close();
    }
    
    public boolean isReady() {
        return false;
    }
    
    public void setWriteListener(final WriteListener arg0) {
    }
    
    static {
        LOG = Logger.getLogger(MCacheResponseStream.class.getName());
    }
}
