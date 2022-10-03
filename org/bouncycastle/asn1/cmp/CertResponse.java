package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class CertResponse extends ASN1Object
{
    private ASN1Integer certReqId;
    private PKIStatusInfo status;
    private CertifiedKeyPair certifiedKeyPair;
    private ASN1OctetString rspInfo;
    
    private CertResponse(final ASN1Sequence asn1Sequence) {
        this.certReqId = ASN1Integer.getInstance(asn1Sequence.getObjectAt(0));
        this.status = PKIStatusInfo.getInstance(asn1Sequence.getObjectAt(1));
        if (asn1Sequence.size() >= 3) {
            if (asn1Sequence.size() == 3) {
                final ASN1Encodable object = asn1Sequence.getObjectAt(2);
                if (object instanceof ASN1OctetString) {
                    this.rspInfo = ASN1OctetString.getInstance(object);
                }
                else {
                    this.certifiedKeyPair = CertifiedKeyPair.getInstance(object);
                }
            }
            else {
                this.certifiedKeyPair = CertifiedKeyPair.getInstance(asn1Sequence.getObjectAt(2));
                this.rspInfo = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(3));
            }
        }
    }
    
    public static CertResponse getInstance(final Object o) {
        if (o instanceof CertResponse) {
            return (CertResponse)o;
        }
        if (o != null) {
            return new CertResponse(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public CertResponse(final ASN1Integer asn1Integer, final PKIStatusInfo pkiStatusInfo) {
        this(asn1Integer, pkiStatusInfo, null, null);
    }
    
    public CertResponse(final ASN1Integer certReqId, final PKIStatusInfo status, final CertifiedKeyPair certifiedKeyPair, final ASN1OctetString rspInfo) {
        if (certReqId == null) {
            throw new IllegalArgumentException("'certReqId' cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("'status' cannot be null");
        }
        this.certReqId = certReqId;
        this.status = status;
        this.certifiedKeyPair = certifiedKeyPair;
        this.rspInfo = rspInfo;
    }
    
    public ASN1Integer getCertReqId() {
        return this.certReqId;
    }
    
    public PKIStatusInfo getStatus() {
        return this.status;
    }
    
    public CertifiedKeyPair getCertifiedKeyPair() {
        return this.certifiedKeyPair;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.certReqId);
        asn1EncodableVector.add(this.status);
        if (this.certifiedKeyPair != null) {
            asn1EncodableVector.add(this.certifiedKeyPair);
        }
        if (this.rspInfo != null) {
            asn1EncodableVector.add(this.rspInfo);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
