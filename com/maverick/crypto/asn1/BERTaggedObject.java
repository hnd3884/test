package com.maverick.crypto.asn1;

import java.io.IOException;
import java.util.Enumeration;

public class BERTaggedObject extends DERTaggedObject
{
    public BERTaggedObject(final int n, final DEREncodable derEncodable) {
        super(n, derEncodable);
    }
    
    public BERTaggedObject(final boolean b, final int n, final DEREncodable derEncodable) {
        super(b, n, derEncodable);
    }
    
    public BERTaggedObject(final int n) {
        super(false, n, new BERConstructedSequence());
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        if (derOutputStream instanceof ASN1OutputStream || derOutputStream instanceof BEROutputStream) {
            derOutputStream.write(0xA0 | super.bc);
            derOutputStream.write(128);
            if (!super.cc) {
                if (!super.ac) {
                    if (super.dc instanceof ASN1OctetString) {
                        Enumeration enumeration;
                        if (super.dc instanceof BERConstructedOctetString) {
                            enumeration = ((BERConstructedOctetString)super.dc).getObjects();
                        }
                        else {
                            enumeration = new BERConstructedOctetString(((ASN1OctetString)super.dc).getOctets()).getObjects();
                        }
                        while (enumeration.hasMoreElements()) {
                            derOutputStream.writeObject(enumeration.nextElement());
                        }
                    }
                    else if (super.dc instanceof ASN1Sequence) {
                        final Enumeration objects = ((ASN1Sequence)super.dc).getObjects();
                        while (objects.hasMoreElements()) {
                            derOutputStream.writeObject(objects.nextElement());
                        }
                    }
                    else {
                        if (!(super.dc instanceof ASN1Set)) {
                            throw new RuntimeException("not implemented: " + super.dc.getClass().getName());
                        }
                        final Enumeration objects2 = ((ASN1Set)super.dc).getObjects();
                        while (objects2.hasMoreElements()) {
                            derOutputStream.writeObject(objects2.nextElement());
                        }
                    }
                }
                else {
                    derOutputStream.writeObject(super.dc);
                }
            }
            derOutputStream.write(0);
            derOutputStream.write(0);
        }
        else {
            super.encode(derOutputStream);
        }
    }
}
