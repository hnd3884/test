package com.sun.org.apache.xalan.internal.xsltc.runtime.output;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.Writer;

class WriterOutputBuffer implements OutputBuffer
{
    private static final int KB = 1024;
    private static int BUFFER_SIZE;
    private Writer _writer;
    
    public WriterOutputBuffer(final Writer writer) {
        this._writer = new BufferedWriter(writer, WriterOutputBuffer.BUFFER_SIZE);
    }
    
    @Override
    public String close() {
        try {
            this._writer.flush();
        }
        catch (final IOException e) {
            throw new RuntimeException(e.toString());
        }
        return "";
    }
    
    @Override
    public OutputBuffer append(final String s) {
        try {
            this._writer.write(s);
        }
        catch (final IOException e) {
            throw new RuntimeException(e.toString());
        }
        return this;
    }
    
    @Override
    public OutputBuffer append(final char[] s, final int from, final int to) {
        try {
            this._writer.write(s, from, to);
        }
        catch (final IOException e) {
            throw new RuntimeException(e.toString());
        }
        return this;
    }
    
    @Override
    public OutputBuffer append(final char ch) {
        try {
            this._writer.write(ch);
        }
        catch (final IOException e) {
            throw new RuntimeException(e.toString());
        }
        return this;
    }
    
    static {
        WriterOutputBuffer.BUFFER_SIZE = 4096;
        final String osName = SecuritySupport.getSystemProperty("os.name");
        if (osName.equalsIgnoreCase("solaris")) {
            WriterOutputBuffer.BUFFER_SIZE = 32768;
        }
    }
}
