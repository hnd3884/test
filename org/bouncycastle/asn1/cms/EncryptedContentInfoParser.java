package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class EncryptedContentInfoParser
{
    private ASN1ObjectIdentifier _contentType;
    private AlgorithmIdentifier _contentEncryptionAlgorithm;
    private ASN1TaggedObjectParser _encryptedContent;
    
    public EncryptedContentInfoParser(final ASN1SequenceParser asn1SequenceParser) throws IOException {
        this._contentType = (ASN1ObjectIdentifier)asn1SequenceParser.readObject();
        this._contentEncryptionAlgorithm = AlgorithmIdentifier.getInstance(asn1SequenceParser.readObject().toASN1Primitive());
        this._encryptedContent = (ASN1TaggedObjectParser)asn1SequenceParser.readObject();
    }
    
    public ASN1ObjectIdentifier getContentType() {
        return this._contentType;
    }
    
    public AlgorithmIdentifier getContentEncryptionAlgorithm() {
        return this._contentEncryptionAlgorithm;
    }
    
    public ASN1Encodable getEncryptedContent(final int n) throws IOException {
        return this._encryptedContent.getObjectParser(n, false);
    }
}
