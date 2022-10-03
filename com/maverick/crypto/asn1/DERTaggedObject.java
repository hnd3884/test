package com.maverick.crypto.asn1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class DERTaggedObject extends ASN1TaggedObject
{
    public DERTaggedObject(final int n, final DEREncodable derEncodable) {
        super(n, derEncodable);
    }
    
    public DERTaggedObject(final boolean b, final int n, final DEREncodable derEncodable) {
        super(b, n, derEncodable);
    }
    
    public DERTaggedObject(final int n) {
        super(false, n, new DERSequence());
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        if (!super.cc) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final DEROutputStream derOutputStream2 = new DEROutputStream(byteArrayOutputStream);
            derOutputStream2.writeObject(super.dc);
            derOutputStream2.close();
            final byte[] byteArray = byteArrayOutputStream.toByteArray();
            if (super.ac) {
                derOutputStream.b(0xA0 | super.bc, byteArray);
            }
            else {
                if ((byteArray[0] & 0x20) != 0x0) {
                    byteArray[0] = (byte)(0xA0 | super.bc);
                }
                else {
                    byteArray[0] = (byte)(0x80 | super.bc);
                }
                derOutputStream.write(byteArray);
            }
        }
        else {
            derOutputStream.b(0xA0 | super.bc, new byte[0]);
        }
    }
}
