package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import java.util.Date;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class DVCSTime extends ASN1Object implements ASN1Choice
{
    private final ASN1GeneralizedTime genTime;
    private final ContentInfo timeStampToken;
    
    public DVCSTime(final Date date) {
        this(new ASN1GeneralizedTime(date));
    }
    
    public DVCSTime(final ASN1GeneralizedTime genTime) {
        this.genTime = genTime;
        this.timeStampToken = null;
    }
    
    public DVCSTime(final ContentInfo timeStampToken) {
        this.genTime = null;
        this.timeStampToken = timeStampToken;
    }
    
    public static DVCSTime getInstance(final Object o) {
        if (o instanceof DVCSTime) {
            return (DVCSTime)o;
        }
        if (o instanceof ASN1GeneralizedTime) {
            return new DVCSTime(ASN1GeneralizedTime.getInstance(o));
        }
        if (o != null) {
            return new DVCSTime(ContentInfo.getInstance(o));
        }
        return null;
    }
    
    public static DVCSTime getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public ASN1GeneralizedTime getGenTime() {
        return this.genTime;
    }
    
    public ContentInfo getTimeStampToken() {
        return this.timeStampToken;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.genTime != null) {
            return this.genTime;
        }
        return this.timeStampToken.toASN1Primitive();
    }
    
    @Override
    public String toString() {
        if (this.genTime != null) {
            return this.genTime.toString();
        }
        return this.timeStampToken.toString();
    }
}
