package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1SequenceParser;

public class AuthenticatedDataParser
{
    private ASN1SequenceParser seq;
    private ASN1Integer version;
    private ASN1Encodable nextObject;
    private boolean originatorInfoCalled;
    
    public AuthenticatedDataParser(final ASN1SequenceParser seq) throws IOException {
        this.seq = seq;
        this.version = ASN1Integer.getInstance(seq.readObject());
    }
    
    public ASN1Integer getVersion() {
        return this.version;
    }
    
    public OriginatorInfo getOriginatorInfo() throws IOException {
        this.originatorInfoCalled = true;
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if (this.nextObject instanceof ASN1TaggedObjectParser && ((ASN1TaggedObjectParser)this.nextObject).getTagNo() == 0) {
            final ASN1SequenceParser asn1SequenceParser = (ASN1SequenceParser)((ASN1TaggedObjectParser)this.nextObject).getObjectParser(16, false);
            this.nextObject = null;
            return OriginatorInfo.getInstance(asn1SequenceParser.toASN1Primitive());
        }
        return null;
    }
    
    public ASN1SetParser getRecipientInfos() throws IOException {
        if (!this.originatorInfoCalled) {
            this.getOriginatorInfo();
        }
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        final ASN1SetParser asn1SetParser = (ASN1SetParser)this.nextObject;
        this.nextObject = null;
        return asn1SetParser;
    }
    
    public AlgorithmIdentifier getMacAlgorithm() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if (this.nextObject != null) {
            final ASN1SequenceParser asn1SequenceParser = (ASN1SequenceParser)this.nextObject;
            this.nextObject = null;
            return AlgorithmIdentifier.getInstance(asn1SequenceParser.toASN1Primitive());
        }
        return null;
    }
    
    public AlgorithmIdentifier getDigestAlgorithm() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if (this.nextObject instanceof ASN1TaggedObjectParser) {
            final AlgorithmIdentifier instance = AlgorithmIdentifier.getInstance((ASN1TaggedObject)this.nextObject.toASN1Primitive(), false);
            this.nextObject = null;
            return instance;
        }
        return null;
    }
    
    @Deprecated
    public ContentInfoParser getEnapsulatedContentInfo() throws IOException {
        return this.getEncapsulatedContentInfo();
    }
    
    public ContentInfoParser getEncapsulatedContentInfo() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if (this.nextObject != null) {
            final ASN1SequenceParser asn1SequenceParser = (ASN1SequenceParser)this.nextObject;
            this.nextObject = null;
            return new ContentInfoParser(asn1SequenceParser);
        }
        return null;
    }
    
    public ASN1SetParser getAuthAttrs() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if (this.nextObject instanceof ASN1TaggedObjectParser) {
            final ASN1Encodable nextObject = this.nextObject;
            this.nextObject = null;
            return (ASN1SetParser)((ASN1TaggedObjectParser)nextObject).getObjectParser(17, false);
        }
        return null;
    }
    
    public ASN1OctetString getMac() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        final ASN1Encodable nextObject = this.nextObject;
        this.nextObject = null;
        return ASN1OctetString.getInstance(nextObject.toASN1Primitive());
    }
    
    public ASN1SetParser getUnauthAttrs() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if (this.nextObject != null) {
            final ASN1Encodable nextObject = this.nextObject;
            this.nextObject = null;
            return (ASN1SetParser)((ASN1TaggedObjectParser)nextObject).getObjectParser(17, false);
        }
        return null;
    }
}
