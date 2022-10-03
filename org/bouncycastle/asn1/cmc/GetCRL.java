package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.ReasonFlags;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1Object;

public class GetCRL extends ASN1Object
{
    private final X500Name issuerName;
    private GeneralName cRLName;
    private ASN1GeneralizedTime time;
    private ReasonFlags reasons;
    
    public GetCRL(final X500Name issuerName, final GeneralName crlName, final ASN1GeneralizedTime time, final ReasonFlags reasons) {
        this.issuerName = issuerName;
        this.cRLName = crlName;
        this.time = time;
        this.reasons = reasons;
    }
    
    private GetCRL(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 1 || asn1Sequence.size() > 4) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.issuerName = X500Name.getInstance(asn1Sequence.getObjectAt(0));
        int n = 1;
        if (asn1Sequence.size() > n && asn1Sequence.getObjectAt(n).toASN1Primitive() instanceof ASN1TaggedObject) {
            this.cRLName = GeneralName.getInstance(asn1Sequence.getObjectAt(n++));
        }
        if (asn1Sequence.size() > n && asn1Sequence.getObjectAt(n).toASN1Primitive() instanceof ASN1GeneralizedTime) {
            this.time = ASN1GeneralizedTime.getInstance(asn1Sequence.getObjectAt(n++));
        }
        if (asn1Sequence.size() > n && asn1Sequence.getObjectAt(n).toASN1Primitive() instanceof DERBitString) {
            this.reasons = new ReasonFlags(DERBitString.getInstance(asn1Sequence.getObjectAt(n)));
        }
    }
    
    public static GetCRL getInstance(final Object o) {
        if (o instanceof GetCRL) {
            return (GetCRL)o;
        }
        if (o != null) {
            return new GetCRL(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public X500Name getIssuerName() {
        return this.issuerName;
    }
    
    public GeneralName getcRLName() {
        return this.cRLName;
    }
    
    public ASN1GeneralizedTime getTime() {
        return this.time;
    }
    
    public ReasonFlags getReasons() {
        return this.reasons;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.issuerName);
        if (this.cRLName != null) {
            asn1EncodableVector.add(this.cRLName);
        }
        if (this.time != null) {
            asn1EncodableVector.add(this.time);
        }
        if (this.reasons != null) {
            asn1EncodableVector.add(this.reasons);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
