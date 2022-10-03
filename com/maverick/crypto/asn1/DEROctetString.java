package com.maverick.crypto.asn1;

import java.io.IOException;

public class DEROctetString extends ASN1OctetString
{
    public DEROctetString(final byte[] array) {
        super(array);
    }
    
    public DEROctetString(final DEREncodable derEncodable) {
        super(derEncodable);
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        derOutputStream.b(4, super.ec);
    }
}
