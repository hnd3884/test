package org.apache.xerces.impl.io;

import java.io.IOException;
import java.util.Locale;
import org.apache.xerces.util.MessageFormatter;
import java.io.InputStream;
import java.io.Reader;

public final class ASCIIReader extends Reader
{
    public static final int DEFAULT_BUFFER_SIZE = 2048;
    protected final InputStream fInputStream;
    protected final byte[] fBuffer;
    private final MessageFormatter fFormatter;
    private final Locale fLocale;
    
    public ASCIIReader(final InputStream inputStream, final MessageFormatter messageFormatter, final Locale locale) {
        this(inputStream, 2048, messageFormatter, locale);
    }
    
    public ASCIIReader(final InputStream inputStream, final int n, final MessageFormatter messageFormatter, final Locale locale) {
        this(inputStream, new byte[n], messageFormatter, locale);
    }
    
    public ASCIIReader(final InputStream fInputStream, final byte[] fBuffer, final MessageFormatter fFormatter, final Locale fLocale) {
        this.fInputStream = fInputStream;
        this.fBuffer = fBuffer;
        this.fFormatter = fFormatter;
        this.fLocale = fLocale;
    }
    
    public int read() throws IOException {
        final int read = this.fInputStream.read();
        if (read >= 128) {
            throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidASCII", new Object[] { Integer.toString(read) });
        }
        return read;
    }
    
    public int read(final char[] array, final int n, int length) throws IOException {
        if (length > this.fBuffer.length) {
            length = this.fBuffer.length;
        }
        final int read = this.fInputStream.read(this.fBuffer, 0, length);
        for (int i = 0; i < read; ++i) {
            final byte b = this.fBuffer[i];
            if (b < 0) {
                throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidASCII", new Object[] { Integer.toString(b & 0xFF) });
            }
            array[n + i] = (char)b;
        }
        return read;
    }
    
    public long skip(final long n) throws IOException {
        return this.fInputStream.skip(n);
    }
    
    public boolean ready() throws IOException {
        return false;
    }
    
    public boolean markSupported() {
        return this.fInputStream.markSupported();
    }
    
    public void mark(final int n) throws IOException {
        this.fInputStream.mark(n);
    }
    
    public void reset() throws IOException {
        this.fInputStream.reset();
    }
    
    public void close() throws IOException {
        this.fInputStream.close();
    }
}
