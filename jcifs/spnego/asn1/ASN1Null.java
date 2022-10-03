package jcifs.spnego.asn1;

import java.io.IOException;

public abstract class ASN1Null extends DERObject
{
    public int hashCode() {
        return 0;
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof ASN1Null;
    }
    
    abstract void encode(final DEROutputStream p0) throws IOException;
}
