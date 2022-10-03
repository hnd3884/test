package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Object;

public class DigestInfo extends ASN1Object
{
    private byte[] digest;
    private AlgorithmIdentifier algId;
    
    public static DigestInfo getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static DigestInfo getInstance(final Object o) {
        if (o instanceof DigestInfo) {
            return (DigestInfo)o;
        }
        if (o != null) {
            return new DigestInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public DigestInfo(final AlgorithmIdentifier algId, final byte[] digest) {
        this.digest = digest;
        this.algId = algId;
    }
    
    public DigestInfo(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.algId = AlgorithmIdentifier.getInstance(objects.nextElement());
        this.digest = ASN1OctetString.getInstance(objects.nextElement()).getOctets();
    }
    
    public AlgorithmIdentifier getAlgorithmId() {
        return this.algId;
    }
    
    public byte[] getDigest() {
        return this.digest;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.algId);
        asn1EncodableVector.add(new DEROctetString(this.digest));
        return new DERSequence(asn1EncodableVector);
    }
}
