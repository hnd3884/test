package jcifs.spnego.asn1;

import java.io.IOException;

public class DERNull extends ASN1Null
{
    byte[] zeroBytes;
    
    public DERNull() {
        this.zeroBytes = new byte[0];
    }
    
    void encode(final DEROutputStream out) throws IOException {
        out.writeEncoded(5, this.zeroBytes);
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DERNull;
    }
}
