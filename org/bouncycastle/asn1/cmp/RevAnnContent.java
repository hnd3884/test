package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.crmf.CertId;
import org.bouncycastle.asn1.ASN1Object;

public class RevAnnContent extends ASN1Object
{
    private PKIStatus status;
    private CertId certId;
    private ASN1GeneralizedTime willBeRevokedAt;
    private ASN1GeneralizedTime badSinceDate;
    private Extensions crlDetails;
    
    private RevAnnContent(final ASN1Sequence asn1Sequence) {
        this.status = PKIStatus.getInstance(asn1Sequence.getObjectAt(0));
        this.certId = CertId.getInstance(asn1Sequence.getObjectAt(1));
        this.willBeRevokedAt = ASN1GeneralizedTime.getInstance(asn1Sequence.getObjectAt(2));
        this.badSinceDate = ASN1GeneralizedTime.getInstance(asn1Sequence.getObjectAt(3));
        if (asn1Sequence.size() > 4) {
            this.crlDetails = Extensions.getInstance(asn1Sequence.getObjectAt(4));
        }
    }
    
    public static RevAnnContent getInstance(final Object o) {
        if (o instanceof RevAnnContent) {
            return (RevAnnContent)o;
        }
        if (o != null) {
            return new RevAnnContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public PKIStatus getStatus() {
        return this.status;
    }
    
    public CertId getCertId() {
        return this.certId;
    }
    
    public ASN1GeneralizedTime getWillBeRevokedAt() {
        return this.willBeRevokedAt;
    }
    
    public ASN1GeneralizedTime getBadSinceDate() {
        return this.badSinceDate;
    }
    
    public Extensions getCrlDetails() {
        return this.crlDetails;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.status);
        asn1EncodableVector.add(this.certId);
        asn1EncodableVector.add(this.willBeRevokedAt);
        asn1EncodableVector.add(this.badSinceDate);
        if (this.crlDetails != null) {
            asn1EncodableVector.add(this.crlDetails);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
