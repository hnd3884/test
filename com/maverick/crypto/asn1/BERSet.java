package com.maverick.crypto.asn1;

import java.io.IOException;
import java.util.Enumeration;

public class BERSet extends DERSet
{
    public BERSet() {
    }
    
    public BERSet(final DEREncodable derEncodable) {
        super(derEncodable);
    }
    
    public BERSet(final DEREncodableVector derEncodableVector) {
        super(derEncodableVector);
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        if (derOutputStream instanceof ASN1OutputStream || derOutputStream instanceof BEROutputStream) {
            derOutputStream.write(49);
            derOutputStream.write(128);
            final Enumeration objects = this.getObjects();
            while (objects.hasMoreElements()) {
                derOutputStream.writeObject(objects.nextElement());
            }
            derOutputStream.write(0);
            derOutputStream.write(0);
        }
        else {
            super.encode(derOutputStream);
        }
    }
}
