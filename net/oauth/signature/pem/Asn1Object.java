package net.oauth.signature.pem;

import java.math.BigInteger;
import java.io.IOException;

class Asn1Object
{
    protected final int type;
    protected final int length;
    protected final byte[] value;
    protected final int tag;
    
    public Asn1Object(final int tag, final int length, final byte[] value) {
        this.tag = tag;
        this.type = (tag & 0x1F);
        this.length = length;
        this.value = value;
    }
    
    public int getType() {
        return this.type;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public byte[] getValue() {
        return this.value;
    }
    
    public boolean isConstructed() {
        return (this.tag & 0x20) == 0x20;
    }
    
    public DerParser getParser() throws IOException {
        if (!this.isConstructed()) {
            throw new IOException("Invalid DER: can't parse primitive entity");
        }
        return new DerParser(this.value);
    }
    
    public BigInteger getInteger() throws IOException {
        if (this.type != 2) {
            throw new IOException("Invalid DER: object is not integer");
        }
        return new BigInteger(this.value);
    }
    
    public String getString() throws IOException {
        String encoding = null;
        switch (this.type) {
            case 18:
            case 19:
            case 21:
            case 22:
            case 25:
            case 26:
            case 27: {
                encoding = "ISO-8859-1";
                break;
            }
            case 30: {
                encoding = "UTF-16BE";
                break;
            }
            case 12: {
                encoding = "UTF-8";
                break;
            }
            case 28: {
                throw new IOException("Invalid DER: can't handle UCS-4 string");
            }
            default: {
                throw new IOException("Invalid DER: object is not a string");
            }
        }
        return new String(this.value, encoding);
    }
}
