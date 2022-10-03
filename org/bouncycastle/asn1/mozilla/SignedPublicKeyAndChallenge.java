package org.bouncycastle.asn1.mozilla;

import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class SignedPublicKeyAndChallenge extends ASN1Object
{
    private final PublicKeyAndChallenge pubKeyAndChal;
    private final ASN1Sequence pkacSeq;
    
    public static SignedPublicKeyAndChallenge getInstance(final Object o) {
        if (o instanceof SignedPublicKeyAndChallenge) {
            return (SignedPublicKeyAndChallenge)o;
        }
        if (o != null) {
            return new SignedPublicKeyAndChallenge(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private SignedPublicKeyAndChallenge(final ASN1Sequence pkacSeq) {
        this.pkacSeq = pkacSeq;
        this.pubKeyAndChal = PublicKeyAndChallenge.getInstance(pkacSeq.getObjectAt(0));
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.pkacSeq;
    }
    
    public PublicKeyAndChallenge getPublicKeyAndChallenge() {
        return this.pubKeyAndChal;
    }
    
    public AlgorithmIdentifier getSignatureAlgorithm() {
        return AlgorithmIdentifier.getInstance(this.pkacSeq.getObjectAt(1));
    }
    
    public DERBitString getSignature() {
        return DERBitString.getInstance(this.pkacSeq.getObjectAt(2));
    }
}
