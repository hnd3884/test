package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1SequenceParser;

public class AuthEnvelopedDataParser
{
    private ASN1SequenceParser seq;
    private ASN1Integer version;
    private ASN1Encodable nextObject;
    private boolean originatorInfoCalled;
    private EncryptedContentInfoParser authEncryptedContentInfoParser;
    
    public AuthEnvelopedDataParser(final ASN1SequenceParser seq) throws IOException {
        this.seq = seq;
        this.version = ASN1Integer.getInstance(seq.readObject());
        if (this.version.getValue().intValue() != 0) {
            throw new ASN1ParsingException("AuthEnvelopedData version number must be 0");
        }
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
    
    public EncryptedContentInfoParser getAuthEncryptedContentInfo() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if (this.nextObject != null) {
            final ASN1SequenceParser asn1SequenceParser = (ASN1SequenceParser)this.nextObject;
            this.nextObject = null;
            return this.authEncryptedContentInfoParser = new EncryptedContentInfoParser(asn1SequenceParser);
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
        if (!this.authEncryptedContentInfoParser.getContentType().equals(CMSObjectIdentifiers.data)) {
            throw new ASN1ParsingException("authAttrs must be present with non-data content");
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
