package cryptix.jce.provider.asn;

import java.io.IOException;
import java.math.BigInteger;

public final class AsnInteger extends AsnObject
{
    private final BigInteger val;
    
    public String toString(final String prefix) {
        return prefix + "BIGINTEGER (" + this.val.toString() + ")";
    }
    
    public BigInteger toBigInteger() {
        return this.val;
    }
    
    protected void encodePayload(final AsnOutputStream os) throws IOException {
        os.writeBytes(this.val.toByteArray());
    }
    
    protected int getEncodedLengthOfPayload(final AsnOutputStream os) {
        return this.val.toByteArray().length;
    }
    
    AsnInteger(final AsnInputStream is) throws IOException {
        super((byte)2);
        final int len = is.readLength();
        final byte[] data = is.readBytes(len);
        this.val = new BigInteger(data);
    }
    
    public AsnInteger(final BigInteger value) {
        super((byte)2);
        this.val = value;
    }
}
