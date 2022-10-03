package cryptix.jce.provider.asn;

import java.io.IOException;

public final class AsnNull extends AsnObject
{
    protected void encodePayload(final AsnOutputStream os) throws IOException {
    }
    
    protected int getEncodedLengthOfPayload(final AsnOutputStream os) {
        return 0;
    }
    
    public String toString(final String indent) {
        return indent + "NULL";
    }
    
    public AsnNull(final AsnInputStream is) throws IOException {
        super((byte)5);
        final int len = is.readLength();
    }
    
    public AsnNull() {
        super((byte)5);
    }
}
