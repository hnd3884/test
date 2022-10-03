package org.openjsse.sun.security.ssl;

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
    
    default int getInt8(final ByteBuffer m) throws IOException {
        verifyLength(m, 1);
        return m.get() & 0xFF;
    }
    
    default int getInt16(final ByteBuffer m) throws IOException {
        verifyLength(m, 2);
        return (m.get() & 0xFF) << 8 | (m.get() & 0xFF);
    }
    
    default int getInt24(final ByteBuffer m) throws IOException {
        verifyLength(m, 3);
        return (m.get() & 0xFF) << 16 | (m.get() & 0xFF) << 8 | (m.get() & 0xFF);
    }
    
    default int getInt32(final ByteBuffer m) throws IOException {
        verifyLength(m, 4);
        return (m.get() & 0xFF) << 24 | (m.get() & 0xFF) << 16 | (m.get() & 0xFF) << 8 | (m.get() & 0xFF);
    }
    
    default byte[] getBytes8(final ByteBuffer m) throws IOException {
        final int len = getInt8(m);
        verifyLength(m, len);
        final byte[] b = new byte[len];
        m.get(b);
        return b;
    }
    
    default byte[] getBytes16(final ByteBuffer m) throws IOException {
        final int len = getInt16(m);
        verifyLength(m, len);
        final byte[] b = new byte[len];
        m.get(b);
        return b;
    }
    
    default byte[] getBytes24(final ByteBuffer m) throws IOException {
        final int len = getInt24(m);
        verifyLength(m, len);
        final byte[] b = new byte[len];
        m.get(b);
        return b;
    }
    
    default void putInt8(final ByteBuffer m, final int i) throws IOException {
        verifyLength(m, 1);
        m.put((byte)(i & 0xFF));
    }
    
    default void putInt16(final ByteBuffer m, final int i) throws IOException {
        verifyLength(m, 2);
        m.put((byte)(i >> 8 & 0xFF));
        m.put((byte)(i & 0xFF));
    }
    
    default void putInt24(final ByteBuffer m, final int i) throws IOException {
        verifyLength(m, 3);
        m.put((byte)(i >> 16 & 0xFF));
        m.put((byte)(i >> 8 & 0xFF));
        m.put((byte)(i & 0xFF));
    }
    
    default void putInt32(final ByteBuffer m, final int i) throws IOException {
        m.put((byte)(i >> 24 & 0xFF));
        m.put((byte)(i >> 16 & 0xFF));
        m.put((byte)(i >> 8 & 0xFF));
        m.put((byte)(i & 0xFF));
    }
    
    default void putBytes8(final ByteBuffer m, final byte[] s) throws IOException {
        if (s == null || s.length == 0) {
            verifyLength(m, 1);
            putInt8(m, 0);
        }
        else {
            verifyLength(m, 1 + s.length);
            putInt8(m, s.length);
            m.put(s);
        }
    }
    
    default void putBytes16(final ByteBuffer m, final byte[] s) throws IOException {
        if (s == null || s.length == 0) {
            verifyLength(m, 2);
            putInt16(m, 0);
        }
        else {
            verifyLength(m, 2 + s.length);
            putInt16(m, s.length);
            m.put(s);
        }
    }
    
    default void putBytes24(final ByteBuffer m, final byte[] s) throws IOException {
        if (s == null || s.length == 0) {
            verifyLength(m, 3);
            putInt24(m, 0);
        }
        else {
            verifyLength(m, 3 + s.length);
            putInt24(m, s.length);
            m.put(s);
        }
    }
    
    default void verifyLength(final ByteBuffer m, final int len) throws SSLException {
        if (len > m.remaining()) {
            throw new SSLException("Insufficient space in the buffer, may be cause by an unexpected end of handshake data.");
        }
    }
}
