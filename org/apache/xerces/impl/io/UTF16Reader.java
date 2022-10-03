package org.apache.xerces.impl.io;

import java.io.IOException;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import java.util.Locale;
import org.apache.xerces.util.MessageFormatter;
import java.io.InputStream;
import java.io.Reader;

public final class UTF16Reader extends Reader
{
    public static final int DEFAULT_BUFFER_SIZE = 4096;
    protected final InputStream fInputStream;
    protected final byte[] fBuffer;
    protected final boolean fIsBigEndian;
    private final MessageFormatter fFormatter;
    private final Locale fLocale;
    
    public UTF16Reader(final InputStream inputStream, final boolean b) {
        this(inputStream, 4096, b, new XMLMessageFormatter(), Locale.getDefault());
    }
    
    public UTF16Reader(final InputStream inputStream, final boolean b, final MessageFormatter messageFormatter, final Locale locale) {
        this(inputStream, 4096, b, messageFormatter, locale);
    }
    
    public UTF16Reader(final InputStream inputStream, final int n, final boolean b, final MessageFormatter messageFormatter, final Locale locale) {
        this(inputStream, new byte[n], b, messageFormatter, locale);
    }
    
    public UTF16Reader(final InputStream fInputStream, final byte[] fBuffer, final boolean fIsBigEndian, final MessageFormatter fFormatter, final Locale fLocale) {
        this.fInputStream = fInputStream;
        this.fBuffer = fBuffer;
        this.fIsBigEndian = fIsBigEndian;
        this.fFormatter = fFormatter;
        this.fLocale = fLocale;
    }
    
    public int read() throws IOException {
        final int read = this.fInputStream.read();
        if (read == -1) {
            return -1;
        }
        final int read2 = this.fInputStream.read();
        if (read2 == -1) {
            this.expectedTwoBytes();
        }
        if (this.fIsBigEndian) {
            return read << 8 | read2;
        }
        return read2 << 8 | read;
    }
    
    public int read(final char[] array, final int n, final int n2) throws IOException {
        int length = n2 << 1;
        if (length > this.fBuffer.length) {
            length = this.fBuffer.length;
        }
        int read = this.fInputStream.read(this.fBuffer, 0, length);
        if (read == -1) {
            return -1;
        }
        if ((read & 0x1) != 0x0) {
            final int read2 = this.fInputStream.read();
            if (read2 == -1) {
                this.expectedTwoBytes();
            }
            this.fBuffer[read++] = (byte)read2;
        }
        final int n3 = read >> 1;
        if (this.fIsBigEndian) {
            this.processBE(array, n, n3);
        }
        else {
            this.processLE(array, n, n3);
        }
        return n3;
    }
    
    public long skip(final long n) throws IOException {
        long skip = this.fInputStream.skip(n << 1);
        if ((skip & 0x1L) != 0x0L) {
            if (this.fInputStream.read() == -1) {
                this.expectedTwoBytes();
            }
            ++skip;
        }
        return skip >> 1;
    }
    
    public boolean ready() throws IOException {
        return false;
    }
    
    public boolean markSupported() {
        return false;
    }
    
    public void mark(final int n) throws IOException {
        throw new IOException(this.fFormatter.formatMessage(this.fLocale, "OperationNotSupported", new Object[] { "mark()", "UTF-16" }));
    }
    
    public void reset() throws IOException {
    }
    
    public void close() throws IOException {
        this.fInputStream.close();
    }
    
    private void processBE(final char[] array, int n, final int n2) {
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            array[n++] = (char)((this.fBuffer[n3++] & 0xFF) << 8 | (this.fBuffer[n3++] & 0xFF));
        }
    }
    
    private void processLE(final char[] array, int n, final int n2) {
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            array[n++] = (char)((this.fBuffer[n3++] & 0xFF) << 8 | (this.fBuffer[n3++] & 0xFF));
        }
    }
    
    private void expectedTwoBytes() throws MalformedByteSequenceException {
        throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "ExpectedByte", new Object[] { "2", "2" });
    }
}
