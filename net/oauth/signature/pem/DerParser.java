package net.oauth.signature.pem;

import java.math.BigInteger;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

class DerParser
{
    public static final int UNIVERSAL = 0;
    public static final int APPLICATION = 64;
    public static final int CONTEXT = 128;
    public static final int PRIVATE = 192;
    public static final int CONSTRUCTED = 32;
    public static final int ANY = 0;
    public static final int BOOLEAN = 1;
    public static final int INTEGER = 2;
    public static final int BIT_STRING = 3;
    public static final int OCTET_STRING = 4;
    public static final int NULL = 5;
    public static final int OBJECT_IDENTIFIER = 6;
    public static final int REAL = 9;
    public static final int ENUMERATED = 10;
    public static final int RELATIVE_OID = 13;
    public static final int SEQUENCE = 16;
    public static final int SET = 17;
    public static final int NUMERIC_STRING = 18;
    public static final int PRINTABLE_STRING = 19;
    public static final int T61_STRING = 20;
    public static final int VIDEOTEX_STRING = 21;
    public static final int IA5_STRING = 22;
    public static final int GRAPHIC_STRING = 25;
    public static final int ISO646_STRING = 26;
    public static final int GENERAL_STRING = 27;
    public static final int UTF8_STRING = 12;
    public static final int UNIVERSAL_STRING = 28;
    public static final int BMP_STRING = 30;
    public static final int UTC_TIME = 23;
    public static final int GENERALIZED_TIME = 24;
    protected InputStream in;
    
    public DerParser(final InputStream in) throws IOException {
        this.in = in;
    }
    
    public DerParser(final byte[] bytes) throws IOException {
        this(new ByteArrayInputStream(bytes));
    }
    
    public Asn1Object read() throws IOException {
        final int tag = this.in.read();
        if (tag == -1) {
            throw new IOException("Invalid DER: stream too short, missing tag");
        }
        final int length = this.getLength();
        final byte[] value = new byte[length];
        final int n = this.in.read(value);
        if (n < length) {
            throw new IOException("Invalid DER: stream too short, missing value");
        }
        final Asn1Object o = new Asn1Object(tag, length, value);
        return o;
    }
    
    private int getLength() throws IOException {
        final int i = this.in.read();
        if (i == -1) {
            throw new IOException("Invalid DER: length missing");
        }
        if ((i & 0xFFFFFF80) == 0x0) {
            return i;
        }
        final int num = i & 0x7F;
        if (i >= 255 || num > 4) {
            throw new IOException("Invalid DER: length field too big (" + i + ")");
        }
        final byte[] bytes = new byte[num];
        final int n = this.in.read(bytes);
        if (n < num) {
            throw new IOException("Invalid DER: length too short");
        }
        return new BigInteger(1, bytes).intValue();
    }
}
