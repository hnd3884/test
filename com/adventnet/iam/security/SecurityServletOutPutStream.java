package com.adventnet.iam.security;

import javax.servlet.WriteListener;
import java.io.IOException;
import javax.servlet.ServletOutputStream;

public class SecurityServletOutPutStream extends ServletOutputStream
{
    private StringBuilder response;
    ServletOutputStream servletOutputStream;
    
    SecurityServletOutPutStream(final ServletOutputStream servletOutputStream) {
        this.response = new StringBuilder();
        this.servletOutputStream = null;
        this.servletOutputStream = servletOutputStream;
    }
    
    public String getResponse() {
        return this.response.toString();
    }
    
    public void write(final int b) throws IOException {
        this.servletOutputStream.write(b);
    }
    
    public void flush() throws IOException {
        this.servletOutputStream.flush();
    }
    
    public void close() throws IOException {
        this.servletOutputStream.close();
    }
    
    public void setWriteListener(final WriteListener listener) {
    }
    
    public boolean isReady() {
        return false;
    }
}
