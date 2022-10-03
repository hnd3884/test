package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;

public class DEROctetStringParser implements ASN1OctetStringParser
{
    private DefiniteLengthInputStream stream;
    
    DEROctetStringParser(final DefiniteLengthInputStream stream) {
        this.stream = stream;
    }
    
    public InputStream getOctetStream() {
        return this.stream;
    }
    
    public ASN1Primitive getLoadedObject() throws IOException {
        return new DEROctetString(this.stream.toByteArray());
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
