package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.ASN1Object;

public class TimeStampResp extends ASN1Object
{
    PKIStatusInfo pkiStatusInfo;
    ContentInfo timeStampToken;
    
    public static TimeStampResp getInstance(final Object o) {
        if (o instanceof TimeStampResp) {
            return (TimeStampResp)o;
        }
        if (o != null) {
            return new TimeStampResp(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private TimeStampResp(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.pkiStatusInfo = PKIStatusInfo.getInstance(objects.nextElement());
        if (objects.hasMoreElements()) {
            this.timeStampToken = ContentInfo.getInstance(objects.nextElement());
        }
    }
    
    public TimeStampResp(final PKIStatusInfo pkiStatusInfo, final ContentInfo timeStampToken) {
        this.pkiStatusInfo = pkiStatusInfo;
        this.timeStampToken = timeStampToken;
    }
    
    public PKIStatusInfo getStatus() {
        return this.pkiStatusInfo;
    }
    
    public ContentInfo getTimeStampToken() {
        return this.timeStampToken;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.pkiStatusInfo);
        if (this.timeStampToken != null) {
            asn1EncodableVector.add(this.timeStampToken);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
