package org.apache.poi.util;

import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.io.InputStream;

public class RLEDecompressingInputStream extends InputStream
{
    private static final int[] POWER2;
    private final InputStream in;
    private final byte[] buf;
    private int pos;
    private int len;
    
    public RLEDecompressingInputStream(final InputStream in) throws IOException {
        this.in = in;
        this.buf = new byte[4096];
        this.pos = 0;
        final int header = in.read();
        if (header != 1) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Header byte 0x01 expected, received 0x%02X", header & 0xFF));
        }
        this.len = this.readChunk();
    }
    
    @Override
    public int read() throws IOException {
        if (this.len == -1) {
            return -1;
        }
        if (this.pos >= this.len && (this.len = this.readChunk()) == -1) {
            return -1;
        }
        return this.buf[this.pos++] & 0xFF;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int l) throws IOException {
        if (this.len == -1) {
            return -1;
        }
        int c;
        for (int offset = off, length = l; length > 0; length -= c, offset += c) {
            if (this.pos >= this.len && (this.len = this.readChunk()) == -1) {
                return (offset > off) ? (offset - off) : -1;
            }
            c = Math.min(length, this.len - this.pos);
            System.arraycopy(this.buf, this.pos, b, offset, c);
            this.pos += c;
        }
        return l;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        int c;
        for (long length = n; length > 0L; length -= c) {
            if (this.pos >= this.len && (this.len = this.readChunk()) == -1) {
                return -1L;
            }
            c = (int)Math.min(n, this.len - this.pos);
            this.pos += c;
        }
        return n;
    }
    
    @Override
    public int available() {
        return (this.len > 0) ? (this.len - this.pos) : 0;
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
    }
    
    private int readChunk() throws IOException {
        this.pos = 0;
        final int w = this.readShort(this.in);
        if (w == -1 || w == 0) {
            return -1;
        }
        final int chunkSize = (w & 0xFFF) + 1;
        if ((w & 0x7000) != 0x3000) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Chunksize header A should be 0x3000, received 0x%04X", w & 0xE000));
        }
        final boolean rawChunk = (w & 0x8000) == 0x0;
        if (!rawChunk) {
            int inOffset = 0;
            int outOffset = 0;
            while (inOffset < chunkSize) {
                final int tokenFlags = this.in.read();
                ++inOffset;
                if (tokenFlags == -1) {
                    break;
                }
                for (int n = 0; n < 8 && inOffset < chunkSize; ++n) {
                    if ((tokenFlags & RLEDecompressingInputStream.POWER2[n]) == 0x0) {
                        final int b = this.in.read();
                        if (b == -1) {
                            return -1;
                        }
                        this.buf[outOffset++] = (byte)b;
                        ++inOffset;
                    }
                    else {
                        final int token = this.readShort(this.in);
                        if (token == -1) {
                            return -1;
                        }
                        inOffset += 2;
                        final int copyLenBits = getCopyLenBits(outOffset - 1);
                        final int copyOffset = (token >> copyLenBits) + 1;
                        final int copyLen = (token & RLEDecompressingInputStream.POWER2[copyLenBits] - 1) + 3;
                        final int startPos = outOffset - copyOffset;
                        for (int endPos = startPos + copyLen, i = startPos; i < endPos; ++i) {
                            this.buf[outOffset++] = this.buf[i];
                        }
                    }
                }
            }
            return outOffset;
        }
        if (IOUtils.readFully(this.in, this.buf, 0, chunkSize) < chunkSize) {
            throw new IllegalStateException(String.format(Locale.ROOT, "Not enough bytes read, expected %d", chunkSize));
        }
        return chunkSize;
    }
    
    static int getCopyLenBits(final int offset) {
        for (int n = 11; n >= 4; --n) {
            if ((offset & RLEDecompressingInputStream.POWER2[n]) != 0x0) {
                return 15 - n;
            }
        }
        return 12;
    }
    
    public int readShort() throws IOException {
        return this.readShort(this);
    }
    
    public int readInt() throws IOException {
        return this.readInt(this);
    }
    
    private int readShort(final InputStream stream) throws IOException {
        final int b0;
        if ((b0 = stream.read()) == -1) {
            return -1;
        }
        final int b2;
        if ((b2 = stream.read()) == -1) {
            return -1;
        }
        return (b0 & 0xFF) | (b2 & 0xFF) << 8;
    }
    
    private int readInt(final InputStream stream) throws IOException {
        final int b0;
        if ((b0 = stream.read()) == -1) {
            return -1;
        }
        final int b2;
        if ((b2 = stream.read()) == -1) {
            return -1;
        }
        final int b3;
        if ((b3 = stream.read()) == -1) {
            return -1;
        }
        final int b4;
        if ((b4 = stream.read()) == -1) {
            return -1;
        }
        return (b0 & 0xFF) | (b2 & 0xFF) << 8 | (b3 & 0xFF) << 16 | (b4 & 0xFF) << 24;
    }
    
    public static byte[] decompress(final byte[] compressed) throws IOException {
        return decompress(compressed, 0, compressed.length);
    }
    
    public static byte[] decompress(final byte[] compressed, final int offset, final int length) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final InputStream instream = new ByteArrayInputStream(compressed, offset, length);
        final InputStream stream = new RLEDecompressingInputStream(instream);
        IOUtils.copy(stream, out);
        stream.close();
        out.close();
        return out.toByteArray();
    }
    
    static {
        POWER2 = new int[] { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768 };
    }
}
