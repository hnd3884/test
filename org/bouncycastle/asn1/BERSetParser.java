package org.bouncycastle.asn1;

import java.io.IOException;

public class BERSetParser implements ASN1SetParser
{
    private ASN1StreamParser _parser;
    
    BERSetParser(final ASN1StreamParser parser) {
        this._parser = parser;
    }
    
    public ASN1Encodable readObject() throws IOException {
        return this._parser.readObject();
    }
    
    public ASN1Primitive getLoadedObject() throws IOException {
        return new BERSet(this._parser.readVector());
    }
    
    public ASN1Primitive toASN1Primitive() {
        try {
            return this.getLoadedObject();
        }
        catch (final IOException ex) {
            throw new ASN1ParsingException(ex.getMessage(), ex);
        }
    }
}
