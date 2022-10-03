package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.ASN1Object;

public class EncKeyWithID extends ASN1Object
{
    private final PrivateKeyInfo privKeyInfo;
    private final ASN1Encodable identifier;
    
    public static EncKeyWithID getInstance(final Object o) {
        if (o instanceof EncKeyWithID) {
            return (EncKeyWithID)o;
        }
        if (o != null) {
            return new EncKeyWithID(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private EncKeyWithID(final ASN1Sequence asn1Sequence) {
        this.privKeyInfo = PrivateKeyInfo.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() > 1) {
            if (!(asn1Sequence.getObjectAt(1) instanceof DERUTF8String)) {
                this.identifier = GeneralName.getInstance(asn1Sequence.getObjectAt(1));
            }
            else {
                this.identifier = asn1Sequence.getObjectAt(1);
            }
        }
        else {
            this.identifier = null;
        }
    }
    
    public EncKeyWithID(final PrivateKeyInfo privKeyInfo) {
        this.privKeyInfo = privKeyInfo;
        this.identifier = null;
    }
    
    public EncKeyWithID(final PrivateKeyInfo privKeyInfo, final DERUTF8String identifier) {
        this.privKeyInfo = privKeyInfo;
        this.identifier = identifier;
    }
    
    public EncKeyWithID(final PrivateKeyInfo privKeyInfo, final GeneralName identifier) {
        this.privKeyInfo = privKeyInfo;
        this.identifier = identifier;
    }
    
    public PrivateKeyInfo getPrivateKey() {
        return this.privKeyInfo;
    }
    
    public boolean hasIdentifier() {
        return this.identifier != null;
    }
    
    public boolean isIdentifierUTF8String() {
        return this.identifier instanceof DERUTF8String;
    }
    
    public ASN1Encodable getIdentifier() {
        return this.identifier;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.privKeyInfo);
        if (this.identifier != null) {
            asn1EncodableVector.add(this.identifier);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
