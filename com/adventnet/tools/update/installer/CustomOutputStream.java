package com.adventnet.tools.update.installer;

import java.io.IOException;
import java.io.OutputStream;

public class CustomOutputStream extends OutputStream
{
    String curStr;
    
    public CustomOutputStream() {
        this.curStr = "";
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.curStr = String.valueOf(b);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.curStr = new String(b);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.curStr = new String(b, off, len);
    }
    
    @Override
    public void flush() throws IOException {
    }
    
    @Override
    public void close() throws IOException {
    }
    
    public String getString() {
        return this.curStr;
    }
}
