package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class Evidence extends ASN1Object implements ASN1Choice
{
    private TimeStampTokenEvidence tstEvidence;
    
    public Evidence(final TimeStampTokenEvidence tstEvidence) {
        this.tstEvidence = tstEvidence;
    }
    
    private Evidence(final ASN1TaggedObject asn1TaggedObject) {
        if (asn1TaggedObject.getTagNo() == 0) {
            this.tstEvidence = TimeStampTokenEvidence.getInstance(asn1TaggedObject, false);
        }
    }
    
    public static Evidence getInstance(final Object o) {
        if (o == null || o instanceof Evidence) {
            return (Evidence)o;
        }
        if (o instanceof ASN1TaggedObject) {
            return new Evidence(ASN1TaggedObject.getInstance(o));
        }
        throw new IllegalArgumentException("unknown object in getInstance");
    }
    
    public TimeStampTokenEvidence getTstEvidence() {
        return this.tstEvidence;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.tstEvidence != null) {
            return new DERTaggedObject(false, 0, this.tstEvidence);
        }
        return null;
    }
}
