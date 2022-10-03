package org.jscep.asn1;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1Object;

public final class IssuerAndSubject extends ASN1Object
{
    private final X500Name issuer;
    private final X500Name subject;
    
    public IssuerAndSubject(final ASN1Sequence seq) {
        this.issuer = X500Name.getInstance((Object)seq.getObjectAt(0));
        this.subject = X500Name.getInstance((Object)seq.getObjectAt(1));
    }
    
    public IssuerAndSubject(final X500Name issuer, final X500Name subject) {
        this.issuer = issuer;
        this.subject = subject;
    }
    
    public IssuerAndSubject(final byte[] bytes) {
        this(ASN1Sequence.getInstance((Object)bytes));
    }
    
    public X500Name getIssuer() {
        return this.issuer;
    }
    
    public X500Name getSubject() {
        return this.subject;
    }
    
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add((ASN1Encodable)this.issuer);
        v.add((ASN1Encodable)this.subject);
        return (ASN1Primitive)new DERSequence(v);
    }
}
