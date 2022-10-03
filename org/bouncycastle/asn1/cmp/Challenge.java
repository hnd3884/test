package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class Challenge extends ASN1Object
{
    private AlgorithmIdentifier owf;
    private ASN1OctetString witness;
    private ASN1OctetString challenge;
    
    private Challenge(final ASN1Sequence asn1Sequence) {
        int n = 0;
        if (asn1Sequence.size() == 3) {
            this.owf = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(n++));
        }
        this.witness = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(n++));
        this.challenge = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(n));
    }
    
    public static Challenge getInstance(final Object o) {
        if (o instanceof Challenge) {
            return (Challenge)o;
        }
        if (o != null) {
            return new Challenge(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public Challenge(final byte[] array, final byte[] array2) {
        this(null, array, array2);
    }
    
    public Challenge(final AlgorithmIdentifier owf, final byte[] array, final byte[] array2) {
        this.owf = owf;
        this.witness = new DEROctetString(array);
        this.challenge = new DEROctetString(array2);
    }
    
    public AlgorithmIdentifier getOwf() {
        return this.owf;
    }
    
    public byte[] getWitness() {
        return this.witness.getOctets();
    }
    
    public byte[] getChallenge() {
        return this.challenge.getOctets();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        this.addOptional(asn1EncodableVector, this.owf);
        asn1EncodableVector.add(this.witness);
        asn1EncodableVector.add(this.challenge);
        return new DERSequence(asn1EncodableVector);
    }
    
    private void addOptional(final ASN1EncodableVector asn1EncodableVector, final ASN1Encodable asn1Encodable) {
        if (asn1Encodable != null) {
            asn1EncodableVector.add(asn1Encodable);
        }
    }
}
