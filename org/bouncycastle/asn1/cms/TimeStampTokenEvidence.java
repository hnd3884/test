package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class TimeStampTokenEvidence extends ASN1Object
{
    private TimeStampAndCRL[] timeStampAndCRLs;
    
    public TimeStampTokenEvidence(final TimeStampAndCRL[] timeStampAndCRLs) {
        this.timeStampAndCRLs = timeStampAndCRLs;
    }
    
    public TimeStampTokenEvidence(final TimeStampAndCRL timeStampAndCRL) {
        (this.timeStampAndCRLs = new TimeStampAndCRL[1])[0] = timeStampAndCRL;
    }
    
    private TimeStampTokenEvidence(final ASN1Sequence asn1Sequence) {
        this.timeStampAndCRLs = new TimeStampAndCRL[asn1Sequence.size()];
        int n = 0;
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            this.timeStampAndCRLs[n++] = TimeStampAndCRL.getInstance(objects.nextElement());
        }
    }
    
    public static TimeStampTokenEvidence getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static TimeStampTokenEvidence getInstance(final Object o) {
        if (o instanceof TimeStampTokenEvidence) {
            return (TimeStampTokenEvidence)o;
        }
        if (o != null) {
            return new TimeStampTokenEvidence(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public TimeStampAndCRL[] toTimeStampAndCRLArray() {
        return this.timeStampAndCRLs;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != this.timeStampAndCRLs.length; ++i) {
            asn1EncodableVector.add(this.timeStampAndCRLs[i]);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
