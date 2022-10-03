package com.adventnet.iam.security;

import javax.servlet.ReadListener;
import java.io.IOException;
import java.util.logging.Level;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.servlet.ServletInputStream;

class CachedServletInputStream extends ServletInputStream
{
    private static final Logger LOGGER;
    private InputStream inputStream;
    
    public CachedServletInputStream(final String content, final String charset) {
        try {
            this.inputStream = new ByteArrayInputStream(content.getBytes(charset));
        }
        catch (final Exception e) {
            CachedServletInputStream.LOGGER.log(Level.WARNING, null, e);
        }
    }
    
    public CachedServletInputStream(final byte[] cachedContent) {
        this.inputStream = new ByteArrayInputStream(cachedContent);
    }
    
    public CachedServletInputStream(final InputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    public int read() throws IOException {
        return this.inputStream.read();
    }
    
    public int available() throws IOException {
        return this.inputStream.available();
    }
    
    public boolean isFinished() {
        return false;
    }
    
    public boolean isReady() {
        return false;
    }
    
    public void setReadListener(final ReadListener arg0) {
    }
    
    static {
        LOGGER = Logger.getLogger(CachedServletInputStream.class.getName());
    }
}
