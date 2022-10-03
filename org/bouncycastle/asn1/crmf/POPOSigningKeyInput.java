package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.ASN1Object;

public class POPOSigningKeyInput extends ASN1Object
{
    private GeneralName sender;
    private PKMACValue publicKeyMAC;
    private SubjectPublicKeyInfo publicKey;
    
    private POPOSigningKeyInput(final ASN1Sequence asn1Sequence) {
        final ASN1Encodable object = asn1Sequence.getObjectAt(0);
        if (object instanceof ASN1TaggedObject) {
            final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)object;
            if (asn1TaggedObject.getTagNo() != 0) {
                throw new IllegalArgumentException("Unknown authInfo tag: " + asn1TaggedObject.getTagNo());
            }
            this.sender = GeneralName.getInstance(asn1TaggedObject.getObject());
        }
        else {
            this.publicKeyMAC = PKMACValue.getInstance(object);
        }
        this.publicKey = SubjectPublicKeyInfo.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public static POPOSigningKeyInput getInstance(final Object o) {
        if (o instanceof POPOSigningKeyInput) {
            return (POPOSigningKeyInput)o;
        }
        if (o != null) {
            return new POPOSigningKeyInput(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public POPOSigningKeyInput(final GeneralName sender, final SubjectPublicKeyInfo publicKey) {
        this.sender = sender;
        this.publicKey = publicKey;
    }
    
    public POPOSigningKeyInput(final PKMACValue publicKeyMAC, final SubjectPublicKeyInfo publicKey) {
        this.publicKeyMAC = publicKeyMAC;
        this.publicKey = publicKey;
    }
    
    public GeneralName getSender() {
        return this.sender;
    }
    
    public PKMACValue getPublicKeyMAC() {
        return this.publicKeyMAC;
    }
    
    public SubjectPublicKeyInfo getPublicKey() {
        return this.publicKey;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.sender != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.sender));
        }
        else {
            asn1EncodableVector.add(this.publicKeyMAC);
        }
        asn1EncodableVector.add(this.publicKey);
        return new DERSequence(asn1EncodableVector);
    }
}
