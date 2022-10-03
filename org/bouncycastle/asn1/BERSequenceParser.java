package org.bouncycastle.asn1;

import java.io.IOException;

public class BERSequenceParser implements ASN1SequenceParser
{
    private ASN1StreamParser _parser;
    
    BERSequenceParser(final ASN1StreamParser parser) {
        this._parser = parser;
    }
    
    public ASN1Encodable readObject() throws IOException {
        return this._parser.readObject();
    }
    
    public ASN1Primitive getLoadedObject() throws IOException {
        return new BERSequence(this._parser.readVector());
    }
    
    public ASN1Primitive toASN1Primitive() {
        try {
            return this.getLoadedObject();
        }
        catch (final IOException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
}
