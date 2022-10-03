package com.sun.org.apache.xerces.internal.impl.io;

import java.io.IOException;
import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import java.io.InputStream;
import java.io.Reader;

public class ASCIIReader extends Reader
{
    public static final int DEFAULT_BUFFER_SIZE = 2048;
    protected InputStream fInputStream;
    protected byte[] fBuffer;
    private MessageFormatter fFormatter;
    private Locale fLocale;
    
    public ASCIIReader(final InputStream inputStream, final MessageFormatter messageFormatter, final Locale locale) {
        this(inputStream, 2048, messageFormatter, locale);
    }
    
    public ASCIIReader(final InputStream inputStream, final int size, final MessageFormatter messageFormatter, final Locale locale) {
        this.fFormatter = null;
        this.fLocale = null;
        this.fInputStream = inputStream;
        final BufferAllocator ba = ThreadLocalBufferAllocator.getBufferAllocator();
        this.fBuffer = ba.getByteBuffer(size);
        if (this.fBuffer == null) {
            this.fBuffer = new byte[size];
        }
        this.fFormatter = messageFormatter;
        this.fLocale = locale;
    }
    
    @Override
    public int read() throws IOException {
        final int b0 = this.fInputStream.read();
        if (b0 >= 128) {
            throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidASCII", new Object[] { Integer.toString(b0) });
        }
        return b0;
    }
    
    @Override
    public int read(final char[] ch, final int offset, int length) throws IOException {
        if (length > this.fBuffer.length) {
            length = this.fBuffer.length;
        }
        final int count = this.fInputStream.read(this.fBuffer, 0, length);
        for (int i = 0; i < count; ++i) {
            final int b0 = this.fBuffer[i];
            if (b0 < 0) {
                throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidASCII", new Object[] { Integer.toString(b0 & 0xFF) });
            }
            ch[offset + i] = (char)b0;
        }
        return count;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return this.fInputStream.skip(n);
    }
    
    @Override
    public boolean ready() throws IOException {
        return false;
    }
    
    @Override
    public boolean markSupported() {
        return this.fInputStream.markSupported();
    }
    
    @Override
    public void mark(final int readAheadLimit) throws IOException {
        this.fInputStream.mark(readAheadLimit);
    }
    
    @Override
    public void reset() throws IOException {
        this.fInputStream.reset();
    }
    
    @Override
    public void close() throws IOException {
        final BufferAllocator ba = ThreadLocalBufferAllocator.getBufferAllocator();
        ba.returnByteBuffer(this.fBuffer);
        this.fBuffer = null;
        this.fInputStream.close();
    }
}
