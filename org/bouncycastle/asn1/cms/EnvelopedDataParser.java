package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1SequenceParser;

public class EnvelopedDataParser
{
    private ASN1SequenceParser _seq;
    private ASN1Integer _version;
    private ASN1Encodable _nextObject;
    private boolean _originatorInfoCalled;
    
    public EnvelopedDataParser(final ASN1SequenceParser seq) throws IOException {
        this._seq = seq;
        this._version = ASN1Integer.getInstance(seq.readObject());
    }
    
    public ASN1Integer getVersion() {
        return this._version;
    }
    
    public OriginatorInfo getOriginatorInfo() throws IOException {
        this._originatorInfoCalled = true;
        if (this._nextObject == null) {
            this._nextObject = this._seq.readObject();
        }
        if (this._nextObject instanceof ASN1TaggedObjectParser && ((ASN1TaggedObjectParser)this._nextObject).getTagNo() == 0) {
            final ASN1SequenceParser asn1SequenceParser = (ASN1SequenceParser)((ASN1TaggedObjectParser)this._nextObject).getObjectParser(16, false);
            this._nextObject = null;
            return OriginatorInfo.getInstance(asn1SequenceParser.toASN1Primitive());
        }
        return null;
    }
    
    public ASN1SetParser getRecipientInfos() throws IOException {
        if (!this._originatorInfoCalled) {
            this.getOriginatorInfo();
        }
        if (this._nextObject == null) {
            this._nextObject = this._seq.readObject();
        }
        final ASN1SetParser asn1SetParser = (ASN1SetParser)this._nextObject;
        this._nextObject = null;
        return asn1SetParser;
    }
    
    public EncryptedContentInfoParser getEncryptedContentInfo() throws IOException {
        if (this._nextObject == null) {
            this._nextObject = this._seq.readObject();
        }
        if (this._nextObject != null) {
            final ASN1SequenceParser asn1SequenceParser = (ASN1SequenceParser)this._nextObject;
            this._nextObject = null;
            return new EncryptedContentInfoParser(asn1SequenceParser);
        }
        return null;
    }
    
    public ASN1SetParser getUnprotectedAttrs() throws IOException {
        if (this._nextObject == null) {
            this._nextObject = this._seq.readObject();
        }
        if (this._nextObject != null) {
            final ASN1Encodable nextObject = this._nextObject;
            this._nextObject = null;
            return (ASN1SetParser)((ASN1TaggedObjectParser)nextObject).getObjectParser(17, false);
        }
        return null;
    }
}
