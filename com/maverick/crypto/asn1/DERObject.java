package com.maverick.crypto.asn1;

import java.io.IOException;

public abstract class DERObject implements DERTags, DEREncodable
{
    public DERObject getDERObject() {
        return this;
    }
    
    abstract void encode(final DEROutputStream p0) throws IOException;
}
