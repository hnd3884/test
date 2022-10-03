package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class ContentInfoParser
{
    private ASN1ObjectIdentifier contentType;
    private ASN1TaggedObjectParser content;
    
    public ContentInfoParser(final ASN1SequenceParser asn1SequenceParser) throws IOException {
        this.contentType = (ASN1ObjectIdentifier)asn1SequenceParser.readObject();
        this.content = (ASN1TaggedObjectParser)asn1SequenceParser.readObject();
    }
    
    public ASN1ObjectIdentifier getContentType() {
        return this.contentType;
    }
    
    public ASN1Encodable getContent(final int n) throws IOException {
        if (this.content != null) {
            return this.content.getObjectParser(n, true);
        }
        return null;
    }
}
