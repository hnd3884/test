package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class TimeStampedData extends ASN1Object
{
    private ASN1Integer version;
    private DERIA5String dataUri;
    private MetaData metaData;
    private ASN1OctetString content;
    private Evidence temporalEvidence;
    
    public TimeStampedData(final DERIA5String dataUri, final MetaData metaData, final ASN1OctetString content, final Evidence temporalEvidence) {
        this.version = new ASN1Integer(1L);
        this.dataUri = dataUri;
        this.metaData = metaData;
        this.content = content;
        this.temporalEvidence = temporalEvidence;
    }
    
    private TimeStampedData(final ASN1Sequence asn1Sequence) {
        this.version = ASN1Integer.getInstance(asn1Sequence.getObjectAt(0));
        int n = 1;
        if (asn1Sequence.getObjectAt(n) instanceof DERIA5String) {
            this.dataUri = DERIA5String.getInstance(asn1Sequence.getObjectAt(n++));
        }
        if (asn1Sequence.getObjectAt(n) instanceof MetaData || asn1Sequence.getObjectAt(n) instanceof ASN1Sequence) {
            this.metaData = MetaData.getInstance(asn1Sequence.getObjectAt(n++));
        }
        if (asn1Sequence.getObjectAt(n) instanceof ASN1OctetString) {
            this.content = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(n++));
        }
        this.temporalEvidence = Evidence.getInstance(asn1Sequence.getObjectAt(n));
    }
    
    public static TimeStampedData getInstance(final Object o) {
        if (o == null || o instanceof TimeStampedData) {
            return (TimeStampedData)o;
        }
        return new TimeStampedData(ASN1Sequence.getInstance(o));
    }
    
    public DERIA5String getDataUri() {
        return this.dataUri;
    }
    
    public MetaData getMetaData() {
        return this.metaData;
    }
    
    public ASN1OctetString getContent() {
        return this.content;
    }
    
    public Evidence getTemporalEvidence() {
        return this.temporalEvidence;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        if (this.dataUri != null) {
            asn1EncodableVector.add(this.dataUri);
        }
        if (this.metaData != null) {
            asn1EncodableVector.add(this.metaData);
        }
        if (this.content != null) {
            asn1EncodableVector.add(this.content);
        }
        asn1EncodableVector.add(this.temporalEvidence);
        return new BERSequence(asn1EncodableVector);
    }
}
