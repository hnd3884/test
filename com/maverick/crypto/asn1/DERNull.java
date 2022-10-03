package com.maverick.crypto.asn1;

import java.io.IOException;

public class DERNull extends ASN1Null
{
    byte[] sb;
    
    public DERNull() {
        this.sb = new byte[0];
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        derOutputStream.b(5, this.sb);
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DERNull;
    }
}
