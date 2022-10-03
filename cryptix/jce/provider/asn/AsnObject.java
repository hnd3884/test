package cryptix.jce.provider.asn;

import java.io.IOException;

public abstract class AsnObject
{
    static final byte TAG_MASK = 31;
    static final byte TAG_INTEGER = 2;
    static final byte TAG_BITSTRING = 3;
    static final byte TAG_NULL = 5;
    static final byte TAG_OBJECT_ID = 6;
    static final byte TAG_SEQUENCE = 48;
    static final byte TAG_SET = 49;
    static final byte TAG_PRINTABLE_STRING = 19;
    static final byte TAG_UTCTime = 23;
    private final byte tag;
    
    public final boolean equals(final Object o) {
        throw new RuntimeException("AsnObject.equals(...) not implemented.");
    }
    
    public final int hashCode() {
        throw new RuntimeException("AsnObject.hashCode(...) not implemented.");
    }
    
    public final String toString() {
        return this.toString("");
    }
    
    final void encode(final AsnOutputStream os) throws IOException {
        os.writeType(this.tag);
        os.writeLength(this.getEncodedLengthOfPayload(os));
        this.encodePayload(os);
    }
    
    final int getEncodedLength(final AsnOutputStream os) {
        int len = this.getEncodedLengthOfPayload(os);
        len += os.getLengthOfLength(len);
        return ++len;
    }
    
    final byte getTag() {
        return this.tag;
    }
    
    protected abstract void encodePayload(final AsnOutputStream p0) throws IOException;
    
    protected abstract int getEncodedLengthOfPayload(final AsnOutputStream p0);
    
    public abstract String toString(final String p0);
    
    protected AsnObject(final byte tag) {
        this.tag = tag;
    }
}
