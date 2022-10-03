package org.apache.tomcat.util.buf;

import java.math.BigInteger;
import org.apache.tomcat.util.res.StringManager;

public class Asn1Parser
{
    private static final StringManager sm;
    private final byte[] source;
    private int pos;
    
    public Asn1Parser(final byte[] source) {
        this.pos = 0;
        this.source = source;
    }
    
    public void parseTag(final int tag) {
        final int value = this.next();
        if (value != tag) {
            throw new IllegalArgumentException(Asn1Parser.sm.getString("asn1Parser.tagMismatch", tag, value));
        }
    }
    
    public void parseFullLength() {
        final int len = this.parseLength();
        if (len + this.pos != this.source.length) {
            throw new IllegalArgumentException(Asn1Parser.sm.getString("asn1Parser.lengthInvalid", len, this.source.length - this.pos));
        }
    }
    
    public int parseLength() {
        int len = this.next();
        if (len > 127) {
            final int bytes = len - 128;
            len = 0;
            for (int i = 0; i < bytes; ++i) {
                len <<= 8;
                len += this.next();
            }
        }
        return len;
    }
    
    public BigInteger parseInt() {
        this.parseTag(2);
        final int len = this.parseLength();
        final byte[] val = new byte[len];
        System.arraycopy(this.source, this.pos, val, 0, len);
        this.pos += len;
        return new BigInteger(val);
    }
    
    public void parseBytes(final byte[] dest) {
        System.arraycopy(this.source, this.pos, dest, 0, dest.length);
        this.pos += dest.length;
    }
    
    private int next() {
        return this.source[this.pos++] & 0xFF;
    }
    
    static {
        sm = StringManager.getManager(Asn1Parser.class);
    }
}
