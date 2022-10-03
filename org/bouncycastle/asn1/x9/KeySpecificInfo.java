package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class KeySpecificInfo extends ASN1Object
{
    private ASN1ObjectIdentifier algorithm;
    private ASN1OctetString counter;
    
    public KeySpecificInfo(final ASN1ObjectIdentifier algorithm, final ASN1OctetString counter) {
        this.algorithm = algorithm;
        this.counter = counter;
    }
    
    public static KeySpecificInfo getInstance(final Object o) {
        if (o instanceof KeySpecificInfo) {
            return (KeySpecificInfo)o;
        }
        if (o != null) {
            return new KeySpecificInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private KeySpecificInfo(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.algorithm = (ASN1ObjectIdentifier)objects.nextElement();
        this.counter = (ASN1OctetString)objects.nextElement();
    }
    
    public ASN1ObjectIdentifier getAlgorithm() {
        return this.algorithm;
    }
    
    public ASN1OctetString getCounter() {
        return this.counter;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.algorithm);
        asn1EncodableVector.add(this.counter);
        return new DERSequence(asn1EncodableVector);
    }
}
