package sun.security.ssl;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.nio.ByteBuffer;

interface Record
{
    public static final int maxMacSize = 48;
    public static final int maxDataSize = 16384;
    public static final int maxPadding = 256;
    public static final int maxIVLength = 16;
    public static final int maxFragmentSize = 18432;
    public static final boolean enableCBCProtection = Utilities.getBooleanProperty("jsse.enableCBCProtection", true);
    public static final int OVERFLOW_OF_INT08 = 256;
    public static final int OVERFLOW_OF_INT16 = 65536;
    public static final int OVERFLOW_OF_INT24 = 16777216;
    
    default int getInt8(final ByteBuffer byteBuffer) throws IOException {
        verifyLength(byteBuffer, 1);
        return byteBuffer.get() & 0xFF;
    }
    
    default int getInt16(final ByteBuffer byteBuffer) throws IOException {
        verifyLength(byteBuffer, 2);
        return (byteBuffer.get() & 0xFF) << 8 | (byteBuffer.get() & 0xFF);
    }
    
    default int getInt24(final ByteBuffer byteBuffer) throws IOException {
        verifyLength(byteBuffer, 3);
        return (byteBuffer.get() & 0xFF) << 16 | (byteBuffer.get() & 0xFF) << 8 | (byteBuffer.get() & 0xFF);
    }
    
    default int getInt32(final ByteBuffer byteBuffer) throws IOException {
        verifyLength(byteBuffer, 4);
        return (byteBuffer.get() & 0xFF) << 24 | (byteBuffer.get() & 0xFF) << 16 | (byteBuffer.get() & 0xFF) << 8 | (byteBuffer.get() & 0xFF);
    }
    
    default byte[] getBytes8(final ByteBuffer byteBuffer) throws IOException {
        final int int8 = getInt8(byteBuffer);
        verifyLength(byteBuffer, int8);
        final byte[] array = new byte[int8];
        byteBuffer.get(array);
        return array;
    }
    
    default byte[] getBytes16(final ByteBuffer byteBuffer) throws IOException {
        final int int16 = getInt16(byteBuffer);
        verifyLength(byteBuffer, int16);
        final byte[] array = new byte[int16];
        byteBuffer.get(array);
        return array;
    }
    
    default byte[] getBytes24(final ByteBuffer byteBuffer) throws IOException {
        final int int24 = getInt24(byteBuffer);
        verifyLength(byteBuffer, int24);
        final byte[] array = new byte[int24];
        byteBuffer.get(array);
        return array;
    }
    
    default void putInt8(final ByteBuffer byteBuffer, final int n) throws IOException {
        verifyLength(byteBuffer, 1);
        byteBuffer.put((byte)(n & 0xFF));
    }
    
    default void putInt16(final ByteBuffer byteBuffer, final int n) throws IOException {
        verifyLength(byteBuffer, 2);
        byteBuffer.put((byte)(n >> 8 & 0xFF));
        byteBuffer.put((byte)(n & 0xFF));
    }
    
    default void putInt24(final ByteBuffer byteBuffer, final int n) throws IOException {
        verifyLength(byteBuffer, 3);
        byteBuffer.put((byte)(n >> 16 & 0xFF));
        byteBuffer.put((byte)(n >> 8 & 0xFF));
        byteBuffer.put((byte)(n & 0xFF));
    }
    
    default void putInt32(final ByteBuffer byteBuffer, final int n) throws IOException {
        byteBuffer.put((byte)(n >> 24 & 0xFF));
        byteBuffer.put((byte)(n >> 16 & 0xFF));
        byteBuffer.put((byte)(n >> 8 & 0xFF));
        byteBuffer.put((byte)(n & 0xFF));
    }
    
    default void putBytes8(final ByteBuffer byteBuffer, final byte[] array) throws IOException {
        if (array == null || array.length == 0) {
            verifyLength(byteBuffer, 1);
            putInt8(byteBuffer, 0);
        }
        else {
            verifyLength(byteBuffer, 1 + array.length);
            putInt8(byteBuffer, array.length);
            byteBuffer.put(array);
        }
    }
    
    default void putBytes16(final ByteBuffer byteBuffer, final byte[] array) throws IOException {
        if (array == null || array.length == 0) {
            verifyLength(byteBuffer, 2);
            putInt16(byteBuffer, 0);
        }
        else {
            verifyLength(byteBuffer, 2 + array.length);
            putInt16(byteBuffer, array.length);
            byteBuffer.put(array);
        }
    }
    
    default void putBytes24(final ByteBuffer byteBuffer, final byte[] array) throws IOException {
        if (array == null || array.length == 0) {
            verifyLength(byteBuffer, 3);
            putInt24(byteBuffer, 0);
        }
        else {
            verifyLength(byteBuffer, 3 + array.length);
            putInt24(byteBuffer, array.length);
            byteBuffer.put(array);
        }
    }
    
    default void verifyLength(final ByteBuffer byteBuffer, final int n) throws SSLException {
        if (n > byteBuffer.remaining()) {
            throw new SSLException("Insufficient space in the buffer, may be cause by an unexpected end of handshake data.");
        }
    }
}
