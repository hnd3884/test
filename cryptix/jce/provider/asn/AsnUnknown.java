package cryptix.jce.provider.asn;

import java.io.IOException;

public final class AsnUnknown extends AsnObject
{
    private final byte[] data;
    
    protected void encodePayload(final AsnOutputStream os) throws IOException {
        os.writeBytes(this.data);
    }
    
    protected int getEncodedLengthOfPayload(final AsnOutputStream os) {
        return this.data.length;
    }
    
    public String toString(final String indent) {
        return indent + "<unknown> (tag: " + this.getTag() + ", len: " + this.data.length + ")";
    }
    
    public AsnUnknown(final byte tag, final AsnInputStream is) throws IOException {
        super(tag);
        final int len = is.readLength();
        this.data = is.readBytes(len);
    }
}
