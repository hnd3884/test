package org.bouncycastle.crypto.util;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.io.IOException;
import org.bouncycastle.asn1.DERSequence;

public class DEROtherInfo
{
    private final DERSequence sequence;
    
    private DEROtherInfo(final DERSequence sequence) {
        this.sequence = sequence;
    }
    
    public byte[] getEncoded() throws IOException {
        return this.sequence.getEncoded();
    }
    
    public static final class Builder
    {
        private final AlgorithmIdentifier algorithmID;
        private final ASN1OctetString partyUVInfo;
        private final ASN1OctetString partyVInfo;
        private ASN1TaggedObject suppPubInfo;
        private ASN1TaggedObject suppPrivInfo;
        
        public Builder(final AlgorithmIdentifier algorithmID, final byte[] array, final byte[] array2) {
            this.algorithmID = algorithmID;
            this.partyUVInfo = DerUtil.getOctetString(array);
            this.partyVInfo = DerUtil.getOctetString(array2);
        }
        
        public Builder withSuppPubInfo(final byte[] array) {
            this.suppPubInfo = new DERTaggedObject(false, 0, DerUtil.getOctetString(array));
            return this;
        }
        
        public Builder withSuppPrivInfo(final byte[] array) {
            this.suppPrivInfo = new DERTaggedObject(false, 1, DerUtil.getOctetString(array));
            return this;
        }
        
        public DEROtherInfo build() {
            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            asn1EncodableVector.add(this.algorithmID);
            asn1EncodableVector.add(this.partyUVInfo);
            asn1EncodableVector.add(this.partyVInfo);
            if (this.suppPubInfo != null) {
                asn1EncodableVector.add(this.suppPubInfo);
            }
            if (this.suppPrivInfo != null) {
                asn1EncodableVector.add(this.suppPrivInfo);
            }
            return new DEROtherInfo(new DERSequence(asn1EncodableVector), null);
        }
    }
}
