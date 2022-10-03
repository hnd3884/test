package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Sequence;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.ASN1Integer;

public class TimeStampedDataParser
{
    private ASN1Integer version;
    private DERIA5String dataUri;
    private MetaData metaData;
    private ASN1OctetStringParser content;
    private Evidence temporalEvidence;
    private ASN1SequenceParser parser;
    
    private TimeStampedDataParser(final ASN1SequenceParser parser) throws IOException {
        this.parser = parser;
        this.version = ASN1Integer.getInstance(parser.readObject());
        ASN1Encodable asn1Encodable = parser.readObject();
        if (asn1Encodable instanceof DERIA5String) {
            this.dataUri = DERIA5String.getInstance(asn1Encodable);
            asn1Encodable = parser.readObject();
        }
        if (asn1Encodable instanceof MetaData || asn1Encodable instanceof ASN1SequenceParser) {
            this.metaData = MetaData.getInstance(asn1Encodable.toASN1Primitive());
            asn1Encodable = parser.readObject();
        }
        if (asn1Encodable instanceof ASN1OctetStringParser) {
            this.content = (ASN1OctetStringParser)asn1Encodable;
        }
    }
    
    public static TimeStampedDataParser getInstance(final Object o) throws IOException {
        if (o instanceof ASN1Sequence) {
            return new TimeStampedDataParser(((ASN1Sequence)o).parser());
        }
        if (o instanceof ASN1SequenceParser) {
            return new TimeStampedDataParser((ASN1SequenceParser)o);
        }
        return null;
    }
    
    public DERIA5String getDataUri() {
        return this.dataUri;
    }
    
    public MetaData getMetaData() {
        return this.metaData;
    }
    
    public ASN1OctetStringParser getContent() {
        return this.content;
    }
    
    public Evidence getTemporalEvidence() throws IOException {
        if (this.temporalEvidence == null) {
            this.temporalEvidence = Evidence.getInstance(this.parser.readObject().toASN1Primitive());
        }
        return this.temporalEvidence;
    }
}
