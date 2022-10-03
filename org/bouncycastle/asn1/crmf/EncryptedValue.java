package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class EncryptedValue extends ASN1Object
{
    private AlgorithmIdentifier intendedAlg;
    private AlgorithmIdentifier symmAlg;
    private DERBitString encSymmKey;
    private AlgorithmIdentifier keyAlg;
    private ASN1OctetString valueHint;
    private DERBitString encValue;
    
    private EncryptedValue(final ASN1Sequence asn1Sequence) {
        int n;
        for (n = 0; asn1Sequence.getObjectAt(n) instanceof ASN1TaggedObject; ++n) {
            final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)asn1Sequence.getObjectAt(n);
            switch (asn1TaggedObject.getTagNo()) {
                case 0: {
                    this.intendedAlg = AlgorithmIdentifier.getInstance(asn1TaggedObject, false);
                    break;
                }
                case 1: {
                    this.symmAlg = AlgorithmIdentifier.getInstance(asn1TaggedObject, false);
                    break;
                }
                case 2: {
                    this.encSymmKey = DERBitString.getInstance(asn1TaggedObject, false);
                    break;
                }
                case 3: {
                    this.keyAlg = AlgorithmIdentifier.getInstance(asn1TaggedObject, false);
                    break;
                }
                case 4: {
                    this.valueHint = ASN1OctetString.getInstance(asn1TaggedObject, false);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown tag encountered: " + asn1TaggedObject.getTagNo());
                }
            }
        }
        this.encValue = DERBitString.getInstance(asn1Sequence.getObjectAt(n));
    }
    
    public static EncryptedValue getInstance(final Object o) {
        if (o instanceof EncryptedValue) {
            return (EncryptedValue)o;
        }
        if (o != null) {
            return new EncryptedValue(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public EncryptedValue(final AlgorithmIdentifier intendedAlg, final AlgorithmIdentifier symmAlg, final DERBitString encSymmKey, final AlgorithmIdentifier keyAlg, final ASN1OctetString valueHint, final DERBitString encValue) {
        if (encValue == null) {
            throw new IllegalArgumentException("'encValue' cannot be null");
        }
        this.intendedAlg = intendedAlg;
        this.symmAlg = symmAlg;
        this.encSymmKey = encSymmKey;
        this.keyAlg = keyAlg;
        this.valueHint = valueHint;
        this.encValue = encValue;
    }
    
    public AlgorithmIdentifier getIntendedAlg() {
        return this.intendedAlg;
    }
    
    public AlgorithmIdentifier getSymmAlg() {
        return this.symmAlg;
    }
    
    public DERBitString getEncSymmKey() {
        return this.encSymmKey;
    }
    
    public AlgorithmIdentifier getKeyAlg() {
        return this.keyAlg;
    }
    
    public ASN1OctetString getValueHint() {
        return this.valueHint;
    }
    
    public DERBitString getEncValue() {
        return this.encValue;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        this.addOptional(asn1EncodableVector, 0, this.intendedAlg);
        this.addOptional(asn1EncodableVector, 1, this.symmAlg);
        this.addOptional(asn1EncodableVector, 2, this.encSymmKey);
        this.addOptional(asn1EncodableVector, 3, this.keyAlg);
        this.addOptional(asn1EncodableVector, 4, this.valueHint);
        asn1EncodableVector.add(this.encValue);
        return new DERSequence(asn1EncodableVector);
    }
    
    private void addOptional(final ASN1EncodableVector asn1EncodableVector, final int n, final ASN1Encodable asn1Encodable) {
        if (asn1Encodable != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, n, asn1Encodable));
        }
    }
}
