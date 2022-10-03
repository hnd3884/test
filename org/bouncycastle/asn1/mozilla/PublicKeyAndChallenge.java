package org.bouncycastle.asn1.mozilla;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class PublicKeyAndChallenge extends ASN1Object
{
    private ASN1Sequence pkacSeq;
    private SubjectPublicKeyInfo spki;
    private DERIA5String challenge;
    
    public static PublicKeyAndChallenge getInstance(final Object o) {
        if (o instanceof PublicKeyAndChallenge) {
            return (PublicKeyAndChallenge)o;
        }
        if (o != null) {
            return new PublicKeyAndChallenge(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private PublicKeyAndChallenge(final ASN1Sequence pkacSeq) {
        this.pkacSeq = pkacSeq;
        this.spki = SubjectPublicKeyInfo.getInstance(pkacSeq.getObjectAt(0));
        this.challenge = DERIA5String.getInstance(pkacSeq.getObjectAt(1));
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.pkacSeq;
    }
    
    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.spki;
    }
    
    public DERIA5String getChallenge() {
        return this.challenge;
    }
}
