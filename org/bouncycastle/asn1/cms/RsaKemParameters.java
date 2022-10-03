package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import java.math.BigInteger;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class RsaKemParameters extends ASN1Object
{
    private final AlgorithmIdentifier keyDerivationFunction;
    private final BigInteger keyLength;
    
    private RsaKemParameters(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("ASN.1 SEQUENCE should be of length 2");
        }
        this.keyDerivationFunction = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.keyLength = ASN1Integer.getInstance(asn1Sequence.getObjectAt(1)).getValue();
    }
    
    public static RsaKemParameters getInstance(final Object o) {
        if (o instanceof RsaKemParameters) {
            return (RsaKemParameters)o;
        }
        if (o != null) {
            return new RsaKemParameters(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public RsaKemParameters(final AlgorithmIdentifier keyDerivationFunction, final int n) {
        this.keyDerivationFunction = keyDerivationFunction;
        this.keyLength = BigInteger.valueOf(n);
    }
    
    public AlgorithmIdentifier getKeyDerivationFunction() {
        return this.keyDerivationFunction;
    }
    
    public BigInteger getKeyLength() {
        return this.keyLength;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.keyDerivationFunction);
        asn1EncodableVector.add(new ASN1Integer(this.keyLength));
        return new DERSequence(asn1EncodableVector);
    }
}
