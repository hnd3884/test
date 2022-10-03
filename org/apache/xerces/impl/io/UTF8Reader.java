package org.apache.xerces.impl.io;

import java.io.IOException;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import java.util.Locale;
import org.apache.xerces.util.MessageFormatter;
import java.io.InputStream;
import java.io.Reader;

public final class UTF8Reader extends Reader
{
    public static final int DEFAULT_BUFFER_SIZE = 2048;
    private static final boolean DEBUG_READ = false;
    protected final InputStream fInputStream;
    protected final byte[] fBuffer;
    protected int fOffset;
    private int fSurrogate;
    private final MessageFormatter fFormatter;
    private final Locale fLocale;
    
    public UTF8Reader(final InputStream inputStream) {
        this(inputStream, 2048, new XMLMessageFormatter(), Locale.getDefault());
    }
    
    public UTF8Reader(final InputStream inputStream, final MessageFormatter messageFormatter, final Locale locale) {
        this(inputStream, 2048, messageFormatter, locale);
    }
    
    public UTF8Reader(final InputStream inputStream, final int n, final MessageFormatter messageFormatter, final Locale locale) {
        this(inputStream, new byte[n], messageFormatter, locale);
    }
    
    public UTF8Reader(final InputStream fInputStream, final byte[] fBuffer, final MessageFormatter fFormatter, final Locale fLocale) {
        this.fSurrogate = -1;
        this.fInputStream = fInputStream;
        this.fBuffer = fBuffer;
        this.fFormatter = fFormatter;
        this.fLocale = fLocale;
    }
    
    public int read() throws IOException {
        int fSurrogate = this.fSurrogate;
        if (this.fSurrogate == -1) {
            int n = 0;
            final int n2 = (n == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[n++] & 0xFF);
            if (n2 == -1) {
                return -1;
            }
            if (n2 < 128) {
                fSurrogate = (char)n2;
            }
            else if ((n2 & 0xE0) == 0xC0 && (n2 & 0x1E) != 0x0) {
                final int n3 = (n == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[n++] & 0xFF);
                if (n3 == -1) {
                    this.expectedByte(2, 2);
                }
                if ((n3 & 0xC0) != 0x80) {
                    this.invalidByte(2, 2, n3);
                }
                fSurrogate = ((n2 << 6 & 0x7C0) | (n3 & 0x3F));
            }
            else if ((n2 & 0xF0) == 0xE0) {
                final int n4 = (n == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[n++] & 0xFF);
                if (n4 == -1) {
                    this.expectedByte(2, 3);
                }
                if ((n4 & 0xC0) != 0x80 || (n2 == 237 && n4 >= 160) || ((n2 & 0xF) == 0x0 && (n4 & 0x20) == 0x0)) {
                    this.invalidByte(2, 3, n4);
                }
                final int n5 = (n == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[n++] & 0xFF);
                if (n5 == -1) {
                    this.expectedByte(3, 3);
                }
                if ((n5 & 0xC0) != 0x80) {
                    this.invalidByte(3, 3, n5);
                }
                fSurrogate = ((n2 << 12 & 0xF000) | (n4 << 6 & 0xFC0) | (n5 & 0x3F));
            }
            else if ((n2 & 0xF8) == 0xF0) {
                final int n6 = (n == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[n++] & 0xFF);
                if (n6 == -1) {
                    this.expectedByte(2, 4);
                }
                if ((n6 & 0xC0) != 0x80 || ((n6 & 0x30) == 0x0 && (n2 & 0x7) == 0x0)) {
                    this.invalidByte(2, 3, n6);
                }
                final int n7 = (n == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[n++] & 0xFF);
                if (n7 == -1) {
                    this.expectedByte(3, 4);
                }
                if ((n7 & 0xC0) != 0x80) {
                    this.invalidByte(3, 3, n7);
                }
                final int n8 = (n == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[n++] & 0xFF);
                if (n8 == -1) {
                    this.expectedByte(4, 4);
                }
                if ((n8 & 0xC0) != 0x80) {
                    this.invalidByte(4, 4, n8);
                }
                final int n9 = (n2 << 2 & 0x1C) | (n6 >> 4 & 0x3);
                if (n9 > 16) {
                    this.invalidSurrogate(n9);
                }
                final int n10 = 0xD800 | (n9 - 1 << 6 & 0x3C0) | (n6 << 2 & 0x3C) | (n7 >> 4 & 0x3);
                final int fSurrogate2 = 0xDC00 | (n7 << 6 & 0x3C0) | (n8 & 0x3F);
                fSurrogate = n10;
                this.fSurrogate = fSurrogate2;
            }
            else {
                this.invalidByte(1, 1, n2);
            }
        }
        else {
            this.fSurrogate = -1;
        }
        return fSurrogate;
    }
    
    public int read(final char[] array, final int n, int length) throws IOException {
        int n2 = n;
        int fOffset;
        if (this.fOffset == 0) {
            if (length > this.fBuffer.length) {
                length = this.fBuffer.length;
            }
            if (this.fSurrogate != -1) {
                array[n2++] = (char)this.fSurrogate;
                this.fSurrogate = -1;
                --length;
            }
            final int read = this.fInputStream.read(this.fBuffer, 0, length);
            if (read == -1) {
                return -1;
            }
            fOffset = read + (n2 - n);
        }
        else {
            fOffset = this.fOffset;
            this.fOffset = 0;
        }
        int n3;
        int i;
        for (n3 = fOffset, i = 0; i < n3; ++i) {
            final byte b = this.fBuffer[i];
            if (b < 0) {
                break;
            }
            array[n2++] = (char)b;
        }
        while (i < n3) {
            final byte b2 = this.fBuffer[i];
            if (b2 >= 0) {
                array[n2++] = (char)b2;
            }
            else {
                final int n4 = b2 & 0xFF;
                if ((n4 & 0xE0) == 0xC0 && (n4 & 0x1E) != 0x0) {
                    int read2;
                    if (++i < n3) {
                        read2 = (this.fBuffer[i] & 0xFF);
                    }
                    else {
                        read2 = this.fInputStream.read();
                        if (read2 == -1) {
                            if (n2 > n) {
                                this.fBuffer[0] = (byte)n4;
                                this.fOffset = 1;
                                return n2 - n;
                            }
                            this.expectedByte(2, 2);
                        }
                        ++fOffset;
                    }
                    if ((read2 & 0xC0) != 0x80) {
                        if (n2 > n) {
                            this.fBuffer[0] = (byte)n4;
                            this.fBuffer[1] = (byte)read2;
                            this.fOffset = 2;
                            return n2 - n;
                        }
                        this.invalidByte(2, 2, read2);
                    }
                    array[n2++] = (char)((n4 << 6 & 0x7C0) | (read2 & 0x3F));
                    --fOffset;
                }
                else if ((n4 & 0xF0) == 0xE0) {
                    int read3;
                    if (++i < n3) {
                        read3 = (this.fBuffer[i] & 0xFF);
                    }
                    else {
                        read3 = this.fInputStream.read();
                        if (read3 == -1) {
                            if (n2 > n) {
                                this.fBuffer[0] = (byte)n4;
                                this.fOffset = 1;
                                return n2 - n;
                            }
                            this.expectedByte(2, 3);
                        }
                        ++fOffset;
                    }
                    if ((read3 & 0xC0) != 0x80 || (n4 == 237 && read3 >= 160) || ((n4 & 0xF) == 0x0 && (read3 & 0x20) == 0x0)) {
                        if (n2 > n) {
                            this.fBuffer[0] = (byte)n4;
                            this.fBuffer[1] = (byte)read3;
                            this.fOffset = 2;
                            return n2 - n;
                        }
                        this.invalidByte(2, 3, read3);
                    }
                    int read4;
                    if (++i < n3) {
                        read4 = (this.fBuffer[i] & 0xFF);
                    }
                    else {
                        read4 = this.fInputStream.read();
                        if (read4 == -1) {
                            if (n2 > n) {
                                this.fBuffer[0] = (byte)n4;
                                this.fBuffer[1] = (byte)read3;
                                this.fOffset = 2;
                                return n2 - n;
                            }
                            this.expectedByte(3, 3);
                        }
                        ++fOffset;
                    }
                    if ((read4 & 0xC0) != 0x80) {
                        if (n2 > n) {
                            this.fBuffer[0] = (byte)n4;
                            this.fBuffer[1] = (byte)read3;
                            this.fBuffer[2] = (byte)read4;
                            this.fOffset = 3;
                            return n2 - n;
                        }
                        this.invalidByte(3, 3, read4);
                    }
                    array[n2++] = (char)((n4 << 12 & 0xF000) | (read3 << 6 & 0xFC0) | (read4 & 0x3F));
                    fOffset -= 2;
                }
                else if ((n4 & 0xF8) == 0xF0) {
                    int read5;
                    if (++i < n3) {
                        read5 = (this.fBuffer[i] & 0xFF);
                    }
                    else {
                        read5 = this.fInputStream.read();
                        if (read5 == -1) {
                            if (n2 > n) {
                                this.fBuffer[0] = (byte)n4;
                                this.fOffset = 1;
                                return n2 - n;
                            }
                            this.expectedByte(2, 4);
                        }
                        ++fOffset;
                    }
                    if ((read5 & 0xC0) != 0x80 || ((read5 & 0x30) == 0x0 && (n4 & 0x7) == 0x0)) {
                        if (n2 > n) {
                            this.fBuffer[0] = (byte)n4;
                            this.fBuffer[1] = (byte)read5;
                            this.fOffset = 2;
                            return n2 - n;
                        }
                        this.invalidByte(2, 4, read5);
                    }
                    int read6;
                    if (++i < n3) {
                        read6 = (this.fBuffer[i] & 0xFF);
                    }
                    else {
                        read6 = this.fInputStream.read();
                        if (read6 == -1) {
                            if (n2 > n) {
                                this.fBuffer[0] = (byte)n4;
                                this.fBuffer[1] = (byte)read5;
                                this.fOffset = 2;
                                return n2 - n;
                            }
                            this.expectedByte(3, 4);
                        }
                        ++fOffset;
                    }
                    if ((read6 & 0xC0) != 0x80) {
                        if (n2 > n) {
                            this.fBuffer[0] = (byte)n4;
                            this.fBuffer[1] = (byte)read5;
                            this.fBuffer[2] = (byte)read6;
                            this.fOffset = 3;
                            return n2 - n;
                        }
                        this.invalidByte(3, 4, read6);
                    }
                    int read7;
                    if (++i < n3) {
                        read7 = (this.fBuffer[i] & 0xFF);
                    }
                    else {
                        read7 = this.fInputStream.read();
                        if (read7 == -1) {
                            if (n2 > n) {
                                this.fBuffer[0] = (byte)n4;
                                this.fBuffer[1] = (byte)read5;
                                this.fBuffer[2] = (byte)read6;
                                this.fOffset = 3;
                                return n2 - n;
                            }
                            this.expectedByte(4, 4);
                        }
                        ++fOffset;
                    }
                    if ((read7 & 0xC0) != 0x80) {
                        if (n2 > n) {
                            this.fBuffer[0] = (byte)n4;
                            this.fBuffer[1] = (byte)read5;
                            this.fBuffer[2] = (byte)read6;
                            this.fBuffer[3] = (byte)read7;
                            this.fOffset = 4;
                            return n2 - n;
                        }
                        this.invalidByte(4, 4, read6);
                    }
                    final int n5 = (n4 << 2 & 0x1C) | (read5 >> 4 & 0x3);
                    if (n5 > 16) {
                        this.invalidSurrogate(n5);
                    }
                    final int n6 = n5 - 1;
                    final int n7 = read5 & 0xF;
                    final int n8 = read6 & 0x3F;
                    final int n9 = read7 & 0x3F;
                    final int n10 = 0xD800 | (n6 << 6 & 0x3C0) | n7 << 2 | n8 >> 4;
                    final int fSurrogate = 0xDC00 | (n8 << 6 & 0x3C0) | n9;
                    array[n2++] = (char)n10;
                    fOffset -= 2;
                    if (fOffset <= length) {
                        array[n2++] = (char)fSurrogate;
                    }
                    else {
                        this.fSurrogate = fSurrogate;
                        --fOffset;
                    }
                }
                else {
                    if (n2 > n) {
                        this.fBuffer[0] = (byte)n4;
                        this.fOffset = 1;
                        return n2 - n;
                    }
                    this.invalidByte(1, 1, n4);
                }
            }
            ++i;
        }
        return fOffset;
    }
    
    public long skip(final long n) throws IOException {
        long n2 = n;
        final char[] array = new char[this.fBuffer.length];
        do {
            final int read = this.read(array, 0, (array.length < n2) ? array.length : ((int)n2));
            if (read <= 0) {
                break;
            }
            n2 -= read;
        } while (n2 > 0L);
        return n - n2;
    }
    
    public boolean ready() throws IOException {
        return false;
    }
    
    public boolean markSupported() {
        return false;
    }
    
    public void mark(final int n) throws IOException {
        throw new IOException(this.fFormatter.formatMessage(this.fLocale, "OperationNotSupported", new Object[] { "mark()", "UTF-8" }));
    }
    
    public void reset() throws IOException {
        this.fOffset = 0;
        this.fSurrogate = -1;
    }
    
    public void close() throws IOException {
        this.fInputStream.close();
    }
    
    private void expectedByte(final int n, final int n2) throws MalformedByteSequenceException {
        throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "ExpectedByte", new Object[] { Integer.toString(n), Integer.toString(n2) });
    }
    
    private void invalidByte(final int n, final int n2, final int n3) throws MalformedByteSequenceException {
        throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidByte", new Object[] { Integer.toString(n), Integer.toString(n2) });
    }
    
    private void invalidSurrogate(final int n) throws MalformedByteSequenceException {
        throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidHighSurrogate", new Object[] { Integer.toHexString(n) });
    }
}
