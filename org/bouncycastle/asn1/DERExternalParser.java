package org.bouncycastle.asn1;

import java.io.IOException;

public class DERExternalParser implements ASN1Encodable, InMemoryRepresentable
{
    private ASN1StreamParser _parser;
    
    public DERExternalParser(final ASN1StreamParser parser) {
        this._parser = parser;
    }
    
    public ASN1Encodable readObject() throws IOException {
        return this._parser.readObject();
    }
    
    public ASN1Primitive getLoadedObject() throws IOException {
        try {
            return new DERExternal(this._parser.readVector());
        }
        catch (final IllegalArgumentException ex) {
            throw new ASN1Exception(ex.getMessage(), ex);
        }
    }
    
    public ASN1Primitive toASN1Primitive() {
        try {
            return this.getLoadedObject();
        }
        catch (final IOException ex) {
            throw new ASN1ParsingException("unable to get DER object", ex);
        }
        catch (final IllegalArgumentException ex2) {
            throw new ASN1ParsingException("unable to get DER object", ex2);
        }
    }
}
