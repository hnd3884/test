package com.maverick.crypto.asn1;

import java.io.IOException;

public class BERNull extends DERNull
{
    void encode(final DEROutputStream derOutputStream) throws IOException {
        if (derOutputStream instanceof ASN1OutputStream || derOutputStream instanceof BEROutputStream) {
            derOutputStream.write(5);
            derOutputStream.write(0);
            derOutputStream.write(0);
        }
        else {
            super.encode(derOutputStream);
        }
    }
}
