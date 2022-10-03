package org.apache.xmlbeans.impl.common;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.Reader;

public class ReaderInputStream extends PushedInputStream
{
    private Reader reader;
    private Writer writer;
    private char[] buf;
    public static int defaultBufferSize;
    
    public ReaderInputStream(final Reader reader, final String encoding) throws UnsupportedEncodingException {
        this(reader, encoding, ReaderInputStream.defaultBufferSize);
    }
    
    public ReaderInputStream(final Reader reader, final String encoding, final int bufferSize) throws UnsupportedEncodingException {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        this.reader = reader;
        this.writer = new OutputStreamWriter(this.getOutputStream(), encoding);
        this.buf = new char[bufferSize];
    }
    
    public void fill(final int requestedBytes) throws IOException {
        do {
            final int chars = this.reader.read(this.buf);
            if (chars < 0) {
                return;
            }
            this.writer.write(this.buf, 0, chars);
            this.writer.flush();
        } while (this.available() <= 0);
    }
    
    static {
        ReaderInputStream.defaultBufferSize = 2048;
    }
}
