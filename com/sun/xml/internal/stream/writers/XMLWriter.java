package com.sun.xml.internal.stream.writers;

import java.io.IOException;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import java.io.Writer;

public class XMLWriter extends Writer
{
    private Writer writer;
    private int size;
    private XMLStringBuffer buffer;
    private static final int THRESHHOLD_LENGTH = 4096;
    private static final boolean DEBUG = false;
    
    public XMLWriter(final Writer writer) {
        this(writer, 4096);
    }
    
    public XMLWriter(final Writer writer, final int size) {
        this.buffer = new XMLStringBuffer(12288);
        this.writer = writer;
        this.size = size;
    }
    
    @Override
    public void write(final int c) throws IOException {
        this.ensureOpen();
        this.buffer.append((char)c);
        this.conditionalWrite();
    }
    
    @Override
    public void write(final char[] cbuf) throws IOException {
        this.write(cbuf, 0, cbuf.length);
    }
    
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        this.ensureOpen();
        if (len > this.size) {
            this.writeBufferedData();
            this.writer.write(cbuf, off, len);
        }
        else {
            this.buffer.append(cbuf, off, len);
            this.conditionalWrite();
        }
    }
    
    @Override
    public void write(final String str, final int off, final int len) throws IOException {
        this.write(str.toCharArray(), off, len);
    }
    
    @Override
    public void write(final String str) throws IOException {
        if (str.length() > this.size) {
            this.writeBufferedData();
            this.writer.write(str);
        }
        else {
            this.buffer.append(str);
            this.conditionalWrite();
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.writer == null) {
            return;
        }
        this.flush();
        this.writer.close();
        this.writer = null;
    }
    
    @Override
    public void flush() throws IOException {
        this.ensureOpen();
        this.writeBufferedData();
        this.writer.flush();
    }
    
    public void reset() {
        this.writer = null;
        this.buffer.clear();
        this.size = 4096;
    }
    
    public void setWriter(final Writer writer) {
        this.writer = writer;
        this.buffer.clear();
        this.size = 4096;
    }
    
    public void setWriter(final Writer writer, final int size) {
        this.writer = writer;
        this.size = size;
    }
    
    protected Writer getWriter() {
        return this.writer;
    }
    
    private void conditionalWrite() throws IOException {
        if (this.buffer.length > this.size) {
            this.writeBufferedData();
        }
    }
    
    private void writeBufferedData() throws IOException {
        this.writer.write(this.buffer.ch, this.buffer.offset, this.buffer.length);
        this.buffer.clear();
    }
    
    private void ensureOpen() throws IOException {
        if (this.writer == null) {
            throw new IOException("Stream closed");
        }
    }
}
