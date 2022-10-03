package com.maverick.crypto.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class DERSet extends ASN1Set
{
    public DERSet() {
    }
    
    public DERSet(final DEREncodable derEncodable) {
        this.addObject(derEncodable);
    }
    
    public DERSet(final DEREncodableVector derEncodableVector) {
        for (int i = 0; i != derEncodableVector.size(); ++i) {
            this.addObject(derEncodableVector.get(i));
        }
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DEROutputStream derOutputStream2 = new DEROutputStream(byteArrayOutputStream);
        final Enumeration objects = this.getObjects();
        while (objects.hasMoreElements()) {
            derOutputStream2.writeObject(objects.nextElement());
        }
        derOutputStream2.close();
        derOutputStream.b(49, byteArrayOutputStream.toByteArray());
    }
}
