package com.sun.jndi.ldap;

import java.io.UnsupportedEncodingException;

public final class BerDecoder extends Ber
{
    private int origOffset;
    
    public BerDecoder(final byte[] buf, final int origOffset, final int bufsize) {
        this.buf = buf;
        this.bufsize = bufsize;
        this.origOffset = origOffset;
        this.reset();
    }
    
    public void reset() {
        this.offset = this.origOffset;
    }
    
    public int getParsePosition() {
        return this.offset;
    }
    
    public int parseLength() throws DecodeException {
        final int byte1 = this.parseByte();
        if ((byte1 & 0x80) != 0x80) {
            return byte1;
        }
        final int n = byte1 & 0x7F;
        if (n == 0) {
            throw new DecodeException("Indefinite length not supported");
        }
        if (n > 4) {
            throw new DecodeException("encoding too long");
        }
        if (this.bufsize - this.offset < n) {
            throw new DecodeException("Insufficient data");
        }
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            n2 = (n2 << 8) + (this.buf[this.offset++] & 0xFF);
        }
        if (n2 < 0) {
            throw new DecodeException("Invalid length bytes");
        }
        return n2;
    }
    
    public int parseSeq(final int[] array) throws DecodeException {
        final int byte1 = this.parseByte();
        final int length = this.parseLength();
        if (array != null) {
            array[0] = length;
        }
        return byte1;
    }
    
    void seek(final int n) throws DecodeException {
        if (this.offset + n > this.bufsize || this.offset + n < 0) {
            throw new DecodeException("array index out of bounds");
        }
        this.offset += n;
    }
    
    public int parseByte() throws DecodeException {
        if (this.bufsize - this.offset < 1) {
            throw new DecodeException("Insufficient data");
        }
        return this.buf[this.offset++] & 0xFF;
    }
    
    public int peekByte() throws DecodeException {
        if (this.bufsize - this.offset < 1) {
            throw new DecodeException("Insufficient data");
        }
        return this.buf[this.offset] & 0xFF;
    }
    
    public boolean parseBoolean() throws DecodeException {
        return this.parseIntWithTag(1) != 0;
    }
    
    public int parseEnumeration() throws DecodeException {
        return this.parseIntWithTag(10);
    }
    
    public int parseInt() throws DecodeException {
        return this.parseIntWithTag(2);
    }
    
    private int parseIntWithTag(final int n) throws DecodeException {
        if (this.parseByte() != n) {
            String string;
            if (this.offset > 0) {
                string = Integer.toString(this.buf[this.offset - 1] & 0xFF);
            }
            else {
                string = "Empty tag";
            }
            throw new DecodeException("Encountered ASN.1 tag " + string + " (expected tag " + Integer.toString(n) + ")");
        }
        final int length = this.parseLength();
        if (length > 4) {
            throw new DecodeException("INTEGER too long");
        }
        if (length > this.bufsize - this.offset) {
            throw new DecodeException("Insufficient data");
        }
        final byte b = this.buf[this.offset++];
        int n2 = b & 0x7F;
        for (int i = 1; i < length; ++i) {
            n2 = (n2 << 8 | (this.buf[this.offset++] & 0xFF));
        }
        if ((b & 0x80) == 0x80) {
            n2 = -n2;
        }
        return n2;
    }
    
    public String parseString(final boolean b) throws DecodeException {
        return this.parseStringWithTag(4, b, null);
    }
    
    public String parseStringWithTag(final int n, final boolean b, final int[] array) throws DecodeException {
        final int offset = this.offset;
        final int byte1;
        if ((byte1 = this.parseByte()) != n) {
            throw new DecodeException("Encountered ASN.1 tag " + Integer.toString((byte)byte1) + " (expected tag " + n + ")");
        }
        final int length = this.parseLength();
        if (length > this.bufsize - this.offset) {
            throw new DecodeException("Insufficient data");
        }
        String s;
        if (length == 0) {
            s = "";
        }
        else {
            final byte[] array2 = new byte[length];
            System.arraycopy(this.buf, this.offset, array2, 0, length);
            Label_0187: {
                if (b) {
                    try {
                        s = new String(array2, "UTF8");
                        break Label_0187;
                    }
                    catch (final UnsupportedEncodingException ex) {
                        throw new DecodeException("UTF8 not available on platform");
                    }
                }
                try {
                    s = new String(array2, "8859_1");
                }
                catch (final UnsupportedEncodingException ex2) {
                    throw new DecodeException("8859_1 not available on platform");
                }
            }
            this.offset += length;
        }
        if (array != null) {
            array[0] = this.offset - offset;
        }
        return s;
    }
    
    public byte[] parseOctetString(final int n, final int[] array) throws DecodeException {
        final int offset = this.offset;
        final int byte1;
        if ((byte1 = this.parseByte()) != n) {
            throw new DecodeException("Encountered ASN.1 tag " + Integer.toString(byte1) + " (expected tag " + Integer.toString(n) + ")");
        }
        final int length = this.parseLength();
        if (length > this.bufsize - this.offset) {
            throw new DecodeException("Insufficient data");
        }
        final byte[] array2 = new byte[length];
        if (length > 0) {
            System.arraycopy(this.buf, this.offset, array2, 0, length);
            this.offset += length;
        }
        if (array != null) {
            array[0] = this.offset - offset;
        }
        return array2;
    }
    
    public int bytesLeft() {
        return this.bufsize - this.offset;
    }
}
