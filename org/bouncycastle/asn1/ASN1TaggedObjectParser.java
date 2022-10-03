package org.bouncycastle.asn1;

import java.io.IOException;

public interface ASN1TaggedObjectParser extends ASN1Encodable, InMemoryRepresentable
{
    int getTagNo();
    
    ASN1Encodable getObjectParser(final int p0, final boolean p1) throws IOException;
}
