package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

class WriterToASCI extends Writer implements WriterChain
{
    private final OutputStream m_os;
    
    public WriterToASCI(final OutputStream os) {
        this.m_os = os;
    }
    
    @Override
    public void write(final char[] chars, final int start, final int length) throws IOException {
        for (int n = length + start, i = start; i < n; ++i) {
            this.m_os.write(chars[i]);
        }
    }
    
    @Override
    public void write(final int c) throws IOException {
        this.m_os.write(c);
    }
    
    @Override
    public void write(final String s) throws IOException {
        for (int n = s.length(), i = 0; i < n; ++i) {
            this.m_os.write(s.charAt(i));
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.m_os.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.m_os.close();
    }
    
    @Override
    public OutputStream getOutputStream() {
        return this.m_os;
    }
    
    @Override
    public Writer getWriter() {
        return null;
    }
}
