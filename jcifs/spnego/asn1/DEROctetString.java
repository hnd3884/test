package jcifs.spnego.asn1;

import java.io.IOException;

public class DEROctetString extends ASN1OctetString
{
    public DEROctetString(final byte[] string) {
        super(string);
    }
    
    public DEROctetString(final DEREncodable obj) {
        super(obj);
    }
    
    void encode(final DEROutputStream out) throws IOException {
        out.writeEncoded(4, this.string);
    }
}
