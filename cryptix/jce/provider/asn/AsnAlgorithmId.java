package cryptix.jce.provider.asn;

import java.io.IOException;

public final class AsnAlgorithmId extends AsnObject
{
    private final AsnSequence val;
    
    protected void encodePayload(final AsnOutputStream os) throws IOException {
        this.val.encodePayload(os);
    }
    
    protected int getEncodedLengthOfPayload(final AsnOutputStream os) {
        return this.val.getEncodedLengthOfPayload(os);
    }
    
    public String toString(final String indent) {
        return indent + "AlgorithmId";
    }
    
    public AsnAlgorithmId(final AsnObjectId oid) {
        super((byte)48);
        this.val = new AsnSequence(oid, new AsnNull());
    }
}
