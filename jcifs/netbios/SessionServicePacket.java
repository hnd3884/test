package jcifs.netbios;

import java.io.IOException;
import java.io.InputStream;

public abstract class SessionServicePacket
{
    static final int SESSION_MESSAGE = 0;
    static final int SESSION_REQUEST = 129;
    public static final int POSITIVE_SESSION_RESPONSE = 130;
    public static final int NEGATIVE_SESSION_RESPONSE = 131;
    static final int SESSION_RETARGET_RESPONSE = 132;
    static final int SESSION_KEEP_ALIVE = 133;
    static final int MAX_MESSAGE_SIZE = 131071;
    static final int HEADER_LENGTH = 4;
    int type;
    int length;
    
    static void writeInt2(final int val, final byte[] dst, int dstIndex) {
        dst[dstIndex++] = (byte)(val >> 8 & 0xFF);
        dst[dstIndex] = (byte)(val & 0xFF);
    }
    
    static void writeInt4(final int val, final byte[] dst, int dstIndex) {
        dst[dstIndex++] = (byte)(val >> 24 & 0xFF);
        dst[dstIndex++] = (byte)(val >> 16 & 0xFF);
        dst[dstIndex++] = (byte)(val >> 8 & 0xFF);
        dst[dstIndex] = (byte)(val & 0xFF);
    }
    
    static int readInt2(final byte[] src, final int srcIndex) {
        return ((src[srcIndex] & 0xFF) << 8) + (src[srcIndex + 1] & 0xFF);
    }
    
    static int readInt4(final byte[] src, final int srcIndex) {
        return ((src[srcIndex] & 0xFF) << 24) + ((src[srcIndex + 1] & 0xFF) << 16) + ((src[srcIndex + 2] & 0xFF) << 8) + (src[srcIndex + 3] & 0xFF);
    }
    
    static int readLength(final byte[] src, int srcIndex) {
        ++srcIndex;
        return ((src[srcIndex++] & 0x1) << 16) + ((src[srcIndex++] & 0xFF) << 8) + (src[srcIndex++] & 0xFF);
    }
    
    static int readn(final InputStream in, final byte[] b, final int off, final int len) throws IOException {
        int i;
        int n;
        for (i = 0; i < len; i += n) {
            n = in.read(b, off + i, len - i);
            if (n <= 0) {
                break;
            }
        }
        return i;
    }
    
    static int readPacketType(final InputStream in, final byte[] buffer, final int bufferIndex) throws IOException {
        final int n;
        if ((n = readn(in, buffer, bufferIndex, 4)) == 4) {
            final int t = buffer[bufferIndex] & 0xFF;
            return t;
        }
        if (n == -1) {
            return -1;
        }
        throw new IOException("unexpected EOF reading netbios session header");
    }
    
    public int writeWireFormat(final byte[] dst, final int dstIndex) {
        this.length = this.writeTrailerWireFormat(dst, dstIndex + 4);
        this.writeHeaderWireFormat(dst, dstIndex);
        return 4 + this.length;
    }
    
    int readWireFormat(final InputStream in, final byte[] buffer, final int bufferIndex) throws IOException {
        this.readHeaderWireFormat(in, buffer, bufferIndex);
        return 4 + this.readTrailerWireFormat(in, buffer, bufferIndex);
    }
    
    int writeHeaderWireFormat(final byte[] dst, int dstIndex) {
        dst[dstIndex++] = (byte)this.type;
        if (this.length > 65535) {
            dst[dstIndex] = 1;
        }
        ++dstIndex;
        writeInt2(this.length, dst, dstIndex);
        return 4;
    }
    
    int readHeaderWireFormat(final InputStream in, final byte[] buffer, int bufferIndex) throws IOException {
        this.type = (buffer[bufferIndex++] & 0xFF);
        this.length = ((buffer[bufferIndex] & 0x1) << 16) + readInt2(buffer, bufferIndex + 1);
        return 4;
    }
    
    abstract int writeTrailerWireFormat(final byte[] p0, final int p1);
    
    abstract int readTrailerWireFormat(final InputStream p0, final byte[] p1, final int p2) throws IOException;
}
