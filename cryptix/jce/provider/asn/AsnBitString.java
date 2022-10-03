package cryptix.jce.provider.asn;

import java.io.IOException;

public final class AsnBitString extends AsnObject
{
    private final byte[] val;
    
    public String toString(final String prefix) {
        return "BIT_STRING";
    }
    
    public byte[] toByteArray() {
        return this.val.clone();
    }
    
    protected void encodePayload(final AsnOutputStream os) throws IOException {
        os.writeByte((byte)0);
        os.writeBytes(this.val);
    }
    
    protected int getEncodedLengthOfPayload(final AsnOutputStream os) {
        return this.val.length + 1;
    }
    
    AsnBitString(final AsnInputStream is) throws AsnException, IOException {
        super((byte)3);
        final int len = is.readLength() - 1;
        if (len < 0) {
            throw new AsnException("Negative length.");
        }
        final byte unused = is.readByte();
        if (unused != 0) {
            throw new AsnException("Length not a multiple of 8.");
        }
        this.val = is.readBytes(len);
    }
    
    public AsnBitString(final byte[] value) {
        super((byte)3);
        this.val = value.clone();
    }
}
