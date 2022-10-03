package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.ASN1Object;

public class TimeStampAndCRL extends ASN1Object
{
    private ContentInfo timeStamp;
    private CertificateList crl;
    
    public TimeStampAndCRL(final ContentInfo timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    private TimeStampAndCRL(final ASN1Sequence asn1Sequence) {
        this.timeStamp = ContentInfo.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() == 2) {
            this.crl = CertificateList.getInstance(asn1Sequence.getObjectAt(1));
        }
    }
    
    public static TimeStampAndCRL getInstance(final Object o) {
        if (o instanceof TimeStampAndCRL) {
            return (TimeStampAndCRL)o;
        }
        if (o != null) {
            return new TimeStampAndCRL(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ContentInfo getTimeStampToken() {
        return this.timeStamp;
    }
    
    @Deprecated
    public CertificateList getCertificateList() {
        return this.crl;
    }
    
    public CertificateList getCRL() {
        return this.crl;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.timeStamp);
        if (this.crl != null) {
            asn1EncodableVector.add(this.crl);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
