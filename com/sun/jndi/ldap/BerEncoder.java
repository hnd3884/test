package com.sun.jndi.ldap;

import java.io.UnsupportedEncodingException;

public final class BerEncoder extends Ber
{
    private int curSeqIndex;
    private int[] seqOffset;
    private static final int INITIAL_SEQUENCES = 16;
    private static final int DEFAULT_BUFSIZE = 1024;
    private static final int BUF_GROWTH_FACTOR = 8;
    
    public BerEncoder() {
        this(1024);
    }
    
    public BerEncoder(final int bufsize) {
        this.buf = new byte[bufsize];
        this.bufsize = bufsize;
        this.offset = 0;
        this.seqOffset = new int[16];
        this.curSeqIndex = 0;
    }
    
    public void reset() {
        while (this.offset > 0) {
            this.buf[--this.offset] = 0;
        }
        while (this.curSeqIndex > 0) {
            this.seqOffset[--this.curSeqIndex] = 0;
        }
    }
    
    public int getDataLen() {
        return this.offset;
    }
    
    public byte[] getBuf() {
        if (this.curSeqIndex != 0) {
            throw new IllegalStateException("BER encode error: Unbalanced SEQUENCEs.");
        }
        return this.buf;
    }
    
    public byte[] getTrimmedBuf() {
        final int dataLen = this.getDataLen();
        final byte[] array = new byte[dataLen];
        System.arraycopy(this.getBuf(), 0, array, 0, dataLen);
        return array;
    }
    
    public void beginSeq(final int n) {
        if (this.curSeqIndex >= this.seqOffset.length) {
            final int[] seqOffset = new int[this.seqOffset.length * 2];
            for (int i = 0; i < this.seqOffset.length; ++i) {
                seqOffset[i] = this.seqOffset[i];
            }
            this.seqOffset = seqOffset;
        }
        this.encodeByte(n);
        this.seqOffset[this.curSeqIndex] = this.offset;
        this.ensureFreeBytes(3);
        this.offset += 3;
        ++this.curSeqIndex;
    }
    
    public void endSeq() throws EncodeException {
        --this.curSeqIndex;
        if (this.curSeqIndex < 0) {
            throw new IllegalStateException("BER encode error: Unbalanced SEQUENCEs.");
        }
        final int n = this.seqOffset[this.curSeqIndex] + 3;
        final int n2 = this.offset - n;
        if (n2 <= 127) {
            this.shiftSeqData(n, n2, -2);
            this.buf[this.seqOffset[this.curSeqIndex]] = (byte)n2;
        }
        else if (n2 <= 255) {
            this.shiftSeqData(n, n2, -1);
            this.buf[this.seqOffset[this.curSeqIndex]] = -127;
            this.buf[this.seqOffset[this.curSeqIndex] + 1] = (byte)n2;
        }
        else if (n2 <= 65535) {
            this.buf[this.seqOffset[this.curSeqIndex]] = -126;
            this.buf[this.seqOffset[this.curSeqIndex] + 1] = (byte)(n2 >> 8);
            this.buf[this.seqOffset[this.curSeqIndex] + 2] = (byte)n2;
        }
        else {
            if (n2 > 16777215) {
                throw new EncodeException("SEQUENCE too long");
            }
            this.shiftSeqData(n, n2, 1);
            this.buf[this.seqOffset[this.curSeqIndex]] = -125;
            this.buf[this.seqOffset[this.curSeqIndex] + 1] = (byte)(n2 >> 16);
            this.buf[this.seqOffset[this.curSeqIndex] + 2] = (byte)(n2 >> 8);
            this.buf[this.seqOffset[this.curSeqIndex] + 3] = (byte)n2;
        }
    }
    
    private void shiftSeqData(final int n, final int n2, final int n3) {
        if (n3 > 0) {
            this.ensureFreeBytes(n3);
        }
        System.arraycopy(this.buf, n, this.buf, n + n3, n2);
        this.offset += n3;
    }
    
    public void encodeByte(final int n) {
        this.ensureFreeBytes(1);
        this.buf[this.offset++] = (byte)n;
    }
    
    public void encodeInt(final int n) {
        this.encodeInt(n, 2);
    }
    
    public void encodeInt(int n, final int n2) {
        int n3;
        int n4;
        for (n3 = -8388608, n4 = 4; ((n & n3) == 0x0 || (n & n3) == n3) && n4 > 1; --n4, n <<= 8) {}
        this.encodeInt(n, n2, n4);
    }
    
    private void encodeInt(int n, final int n2, int n3) {
        if (n3 > 4) {
            throw new IllegalArgumentException("BER encode error: INTEGER too long.");
        }
        this.ensureFreeBytes(2 + n3);
        this.buf[this.offset++] = (byte)n2;
        this.buf[this.offset++] = (byte)n3;
        final int n4 = -16777216;
        while (n3-- > 0) {
            this.buf[this.offset++] = (byte)((n & n4) >> 24);
            n <<= 8;
        }
    }
    
    public void encodeBoolean(final boolean b) {
        this.encodeBoolean(b, 1);
    }
    
    public void encodeBoolean(final boolean b, final int n) {
        this.ensureFreeBytes(3);
        this.buf[this.offset++] = (byte)n;
        this.buf[this.offset++] = 1;
        this.buf[this.offset++] = (byte)(b ? -1 : 0);
    }
    
    public void encodeString(final String s, final boolean b) throws EncodeException {
        this.encodeString(s, 4, b);
    }
    
    public void encodeString(final String s, final int n, final boolean b) throws EncodeException {
        this.encodeByte(n);
        int i = 0;
        byte[] array = null;
        int n2 = 0;
        Label_0081: {
            if (s == null) {
                n2 = 0;
            }
            else {
                if (b) {
                    try {
                        array = s.getBytes("UTF8");
                        n2 = array.length;
                        break Label_0081;
                    }
                    catch (final UnsupportedEncodingException ex) {
                        throw new EncodeException("UTF8 not available on platform");
                    }
                }
                try {
                    array = s.getBytes("8859_1");
                    n2 = array.length;
                }
                catch (final UnsupportedEncodingException ex2) {
                    throw new EncodeException("8859_1 not available on platform");
                }
            }
        }
        this.encodeLength(n2);
        this.ensureFreeBytes(n2);
        while (i < n2) {
            this.buf[this.offset++] = array[i++];
        }
    }
    
    public void encodeOctetString(final byte[] array, final int n, final int n2, final int n3) throws EncodeException {
        this.encodeByte(n);
        this.encodeLength(n3);
        if (n3 > 0) {
            this.ensureFreeBytes(n3);
            System.arraycopy(array, n2, this.buf, this.offset, n3);
            this.offset += n3;
        }
    }
    
    public void encodeOctetString(final byte[] array, final int n) throws EncodeException {
        this.encodeOctetString(array, n, 0, array.length);
    }
    
    private void encodeLength(final int n) throws EncodeException {
        this.ensureFreeBytes(4);
        if (n < 128) {
            this.buf[this.offset++] = (byte)n;
        }
        else if (n <= 255) {
            this.buf[this.offset++] = -127;
            this.buf[this.offset++] = (byte)n;
        }
        else if (n <= 65535) {
            this.buf[this.offset++] = -126;
            this.buf[this.offset++] = (byte)(n >> 8);
            this.buf[this.offset++] = (byte)(n & 0xFF);
        }
        else {
            if (n > 16777215) {
                throw new EncodeException("string too long");
            }
            this.buf[this.offset++] = -125;
            this.buf[this.offset++] = (byte)(n >> 16);
            this.buf[this.offset++] = (byte)(n >> 8);
            this.buf[this.offset++] = (byte)(n & 0xFF);
        }
    }
    
    public void encodeStringArray(final String[] array, final boolean b) throws EncodeException {
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            this.encodeString(array[i], b);
        }
    }
    
    private void ensureFreeBytes(final int n) {
        if (this.bufsize - this.offset < n) {
            int bufsize = this.bufsize * 8;
            if (bufsize - this.offset < n) {
                bufsize += n;
            }
            final byte[] buf = new byte[bufsize];
            System.arraycopy(this.buf, 0, buf, 0, this.offset);
            this.buf = buf;
            this.bufsize = bufsize;
        }
    }
}
