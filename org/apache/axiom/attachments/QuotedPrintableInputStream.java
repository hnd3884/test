package org.apache.axiom.attachments;

import java.io.IOException;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.util.ByteArrayBuffer;
import java.io.InputStream;

final class QuotedPrintableInputStream extends InputStream
{
    private static final int DEFAULT_BUFFER_SIZE = 2048;
    private static final byte EQ = 61;
    private static final byte CR = 13;
    private static final byte LF = 10;
    private final byte[] singleByte;
    private final InputStream in;
    private final ByteArrayBuffer decodedBuf;
    private final ByteArrayBuffer blanks;
    private final byte[] encoded;
    private int pos;
    private int limit;
    private boolean lastWasCR;
    private boolean closed;
    private final DecodeMonitor monitor;
    
    public QuotedPrintableInputStream(final InputStream in, final DecodeMonitor monitor) {
        this(2048, in, monitor);
    }
    
    protected QuotedPrintableInputStream(final int bufsize, final InputStream in, final DecodeMonitor monitor) {
        this.singleByte = new byte[1];
        this.pos = 0;
        this.limit = 0;
        this.lastWasCR = false;
        this.in = in;
        this.encoded = new byte[bufsize];
        this.decodedBuf = new ByteArrayBuffer(512);
        this.blanks = new ByteArrayBuffer(512);
        this.closed = false;
        this.monitor = monitor;
    }
    
    protected QuotedPrintableInputStream(final int bufsize, final InputStream in, final boolean strict) {
        this(bufsize, in, strict ? DecodeMonitor.STRICT : DecodeMonitor.SILENT);
    }
    
    public QuotedPrintableInputStream(final InputStream in, final boolean strict) {
        this(2048, in, strict);
    }
    
    public QuotedPrintableInputStream(final InputStream in) {
        this(in, false);
    }
    
    @Override
    public void close() throws IOException {
        this.closed = true;
    }
    
    private int fillBuffer() throws IOException {
        if (this.pos < this.limit) {
            System.arraycopy(this.encoded, this.pos, this.encoded, 0, this.limit - this.pos);
            this.limit -= this.pos;
            this.pos = 0;
        }
        else {
            this.limit = 0;
            this.pos = 0;
        }
        final int capacity = this.encoded.length - this.limit;
        if (capacity > 0) {
            final int bytesRead = this.in.read(this.encoded, this.limit, capacity);
            if (bytesRead > 0) {
                this.limit += bytesRead;
            }
            return bytesRead;
        }
        return 0;
    }
    
    private int getnext() {
        if (this.pos < this.limit) {
            final byte b = this.encoded[this.pos];
            ++this.pos;
            return b & 0xFF;
        }
        return -1;
    }
    
    private int peek(final int i) {
        if (this.pos + i < this.limit) {
            return this.encoded[this.pos + i] & 0xFF;
        }
        return -1;
    }
    
    private int transfer(final int b, final byte[] buffer, final int from, final int to, final boolean keepblanks) throws IOException {
        int index = from;
        if (keepblanks && this.blanks.length() > 0) {
            final int chunk = Math.min(this.blanks.length(), to - index);
            System.arraycopy(this.blanks.buffer(), 0, buffer, index, chunk);
            index += chunk;
            final int remaining = this.blanks.length() - chunk;
            if (remaining > 0) {
                this.decodedBuf.append(this.blanks.buffer(), chunk, remaining);
            }
            this.blanks.clear();
        }
        else if (this.blanks.length() > 0 && !keepblanks) {
            final StringBuilder sb = new StringBuilder(this.blanks.length() * 3);
            for (int i = 0; i < this.blanks.length(); ++i) {
                sb.append(" " + this.blanks.byteAt(i));
            }
            if (this.monitor.warn("ignored blanks", sb.toString())) {
                throw new IOException("ignored blanks");
            }
        }
        if (b != -1) {
            if (index < to) {
                buffer[index++] = (byte)b;
            }
            else {
                this.decodedBuf.append(b);
            }
        }
        return index;
    }
    
    private int read0(final byte[] buffer, final int off, final int len) throws IOException {
        boolean eof = false;
        final int from = off;
        final int to = off + len;
        int index = off;
        if (this.decodedBuf.length() > 0) {
            final int chunk = Math.min(this.decodedBuf.length(), to - index);
            System.arraycopy(this.decodedBuf.buffer(), 0, buffer, index, chunk);
            this.decodedBuf.remove(0, chunk);
            index += chunk;
        }
        while (index < to) {
            if (this.limit - this.pos < 3) {
                final int bytesRead = this.fillBuffer();
                eof = (bytesRead == -1);
            }
            if (this.limit - this.pos == 0 && eof) {
                return (index == from) ? -1 : (index - from);
            }
            while (this.pos < this.limit && index < to) {
                final int b = this.encoded[this.pos++] & 0xFF;
                if (this.lastWasCR && b != 10) {
                    if (this.monitor.warn("Found CR without LF", "Leaving it as is")) {
                        throw new IOException("Found CR without LF");
                    }
                    index = this.transfer(13, buffer, index, to, false);
                }
                else if (!this.lastWasCR && b == 10 && this.monitor.warn("Found LF without CR", "Translating to CRLF")) {
                    throw new IOException("Found LF without CR");
                }
                if (b == 13) {
                    this.lastWasCR = true;
                }
                else {
                    this.lastWasCR = false;
                    if (b == 10) {
                        if (this.blanks.length() == 0) {
                            index = this.transfer(13, buffer, index, to, false);
                            index = this.transfer(10, buffer, index, to, false);
                        }
                        else if (this.blanks.byteAt(0) != 61) {
                            index = this.transfer(13, buffer, index, to, false);
                            index = this.transfer(10, buffer, index, to, false);
                        }
                        this.blanks.clear();
                    }
                    else if (b == 61) {
                        if (this.limit - this.pos < 2 && !eof) {
                            --this.pos;
                            break;
                        }
                        final int b2 = this.getnext();
                        if (b2 == 61) {
                            index = this.transfer(b2, buffer, index, to, true);
                            final int bb1 = this.peek(0);
                            final int bb2 = this.peek(1);
                            if (bb1 == 10 || (bb1 == 13 && bb2 == 10)) {
                                this.monitor.warn("Unexpected ==EOL encountered", "== 0x" + bb1 + " 0x" + bb2);
                                this.blanks.append(b2);
                            }
                            else {
                                this.monitor.warn("Unexpected == encountered", "==");
                            }
                        }
                        else if (Character.isWhitespace((char)b2)) {
                            final int b3 = this.peek(0);
                            if ((b2 != 13 || b3 != 10) && this.monitor.warn("Found non-standard soft line break", "Translating to soft line break")) {
                                throw new IOException("Non-standard soft line break");
                            }
                            if (b3 == 10) {
                                this.lastWasCR = (b2 == 13);
                            }
                            index = this.transfer(-1, buffer, index, to, true);
                            if (b2 == 10) {
                                continue;
                            }
                            this.blanks.append(b);
                            this.blanks.append(b2);
                        }
                        else {
                            final int b3 = this.getnext();
                            final int upper = this.convert(b2);
                            final int lower = this.convert(b3);
                            if (upper < 0 || lower < 0) {
                                this.monitor.warn("Malformed encoded value encountered", "leaving =" + (char)b2 + (char)b3 + " as is");
                                index = this.transfer(61, buffer, index, to, true);
                                index = this.transfer(b2, buffer, index, to, false);
                                index = this.transfer(b3, buffer, index, to, false);
                            }
                            else {
                                index = this.transfer(upper << 4 | lower, buffer, index, to, true);
                            }
                        }
                    }
                    else if (Character.isWhitespace(b)) {
                        this.blanks.append(b);
                    }
                    else {
                        index = this.transfer(b & 0xFF, buffer, index, to, true);
                    }
                }
            }
        }
        return to - from;
    }
    
    private int convert(final int c) {
        if (c >= 48 && c <= 57) {
            return c - 48;
        }
        if (c >= 65 && c <= 70) {
            return 10 + (c - 65);
        }
        if (c >= 97 && c <= 102) {
            return 10 + (c - 97);
        }
        return -1;
    }
    
    @Override
    public int read() throws IOException {
        if (this.closed) {
            throw new IOException("Stream has been closed");
        }
        while (true) {
            final int bytes = this.read(this.singleByte, 0, 1);
            if (bytes == -1) {
                return -1;
            }
            if (bytes == 1) {
                return this.singleByte[0] & 0xFF;
            }
        }
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (this.closed) {
            throw new IOException("Stream has been closed");
        }
        return this.read0(b, off, len);
    }
}
