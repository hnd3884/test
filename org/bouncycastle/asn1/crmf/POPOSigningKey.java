package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class POPOSigningKey extends ASN1Object
{
    private POPOSigningKeyInput poposkInput;
    private AlgorithmIdentifier algorithmIdentifier;
    private DERBitString signature;
    
    private POPOSigningKey(final ASN1Sequence asn1Sequence) {
        int n = 0;
        if (asn1Sequence.getObjectAt(n) instanceof ASN1TaggedObject) {
            final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)asn1Sequence.getObjectAt(n++);
            if (asn1TaggedObject.getTagNo() != 0) {
                throw new IllegalArgumentException("Unknown POPOSigningKeyInput tag: " + asn1TaggedObject.getTagNo());
            }
            this.poposkInput = POPOSigningKeyInput.getInstance(asn1TaggedObject.getObject());
        }
        this.algorithmIdentifier = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(n++));
        this.signature = DERBitString.getInstance(asn1Sequence.getObjectAt(n));
    }
    
    public static POPOSigningKey getInstance(final Object o) {
        if (o instanceof POPOSigningKey) {
            return (POPOSigningKey)o;
        }
        if (o != null) {
            return new POPOSigningKey(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static POPOSigningKey getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public POPOSigningKey(final POPOSigningKeyInput poposkInput, final AlgorithmIdentifier algorithmIdentifier, final DERBitString signature) {
        this.poposkInput = poposkInput;
        this.algorithmIdentifier = algorithmIdentifier;
        this.signature = signature;
    }
    
    public POPOSigningKeyInput getPoposkInput() {
        return this.poposkInput;
    }
    
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.algorithmIdentifier;
    }
    
    public DERBitString getSignature() {
        return this.signature;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.poposkInput != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.poposkInput));
        }
        asn1EncodableVector.add(this.algorithmIdentifier);
        asn1EncodableVector.add(this.signature);
        return new DERSequence(asn1EncodableVector);
    }
}
