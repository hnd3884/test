package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.io.Streams;
import java.io.InputStream;

public class BEROctetStringParser implements ASN1OctetStringParser
{
    private ASN1StreamParser _parser;
    
    BEROctetStringParser(final ASN1StreamParser parser) {
        this._parser = parser;
    }
    
    public InputStream getOctetStream() {
        return new ConstructedOctetStream(this._parser);
    }
    
    public ASN1Primitive getLoadedObject() throws IOException {
        return new BEROctetString(Streams.readAll(this.getOctetStream()));
    }
    
    public ASN1Primitive toASN1Primitive() {
        try {
            return this.getLoadedObject();
        }
        catch (final IOException ex) {
            throw new ASN1ParsingException("IOException converting stream to byte array: " + ex.getMessage(), ex);
        }
    }
}
