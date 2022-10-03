package com.maverick.crypto.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DERObjectIdentifier extends DERObject
{
    String yb;
    
    public static DERObjectIdentifier getInstance(final Object o) {
        if (o == null || o instanceof DERObjectIdentifier) {
            return (DERObjectIdentifier)o;
        }
        if (o instanceof ASN1OctetString) {
            return new DERObjectIdentifier(((ASN1OctetString)o).getOctets());
        }
        if (o instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)o).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERObjectIdentifier getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    DERObjectIdentifier(final byte[] array) {
        final StringBuffer sb = new StringBuffer();
        int n = 0;
        int n2 = 1;
        for (int i = 0; i != array.length; ++i) {
            final int n3 = array[i] & 0xFF;
            n = n * 128 + (n3 & 0x7F);
            if ((n3 & 0x80) == 0x0) {
                if (n2 != 0) {
                    switch (n / 40) {
                        case 0: {
                            sb.append('0');
                            break;
                        }
                        case 1: {
                            sb.append('1');
                            n -= 40;
                            break;
                        }
                        default: {
                            sb.append('2');
                            n -= 80;
                            break;
                        }
                    }
                    n2 = 0;
                }
                sb.append('.');
                sb.append(Integer.toString(n));
                n = 0;
            }
        }
        this.yb = sb.toString();
    }
    
    public DERObjectIdentifier(final String yb) {
        this.yb = yb;
    }
    
    public String getId() {
        return this.yb;
    }
    
    private void b(final OutputStream outputStream, final int n) throws IOException {
        if (n >= 128) {
            if (n >= 16384) {
                if (n >= 2097152) {
                    if (n >= 268435456) {
                        outputStream.write(n >> 28 | 0x80);
                    }
                    outputStream.write(n >> 21 | 0x80);
                }
                outputStream.write(n >> 14 | 0x80);
            }
            outputStream.write(n >> 7 | 0x80);
        }
        outputStream.write(n & 0x7F);
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        final OIDTokenizer oidTokenizer = new OIDTokenizer(this.yb);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DEROutputStream derOutputStream2 = new DEROutputStream(byteArrayOutputStream);
        this.b(byteArrayOutputStream, Integer.parseInt(oidTokenizer.nextToken()) * 40 + Integer.parseInt(oidTokenizer.nextToken()));
        while (oidTokenizer.hasMoreTokens()) {
            this.b(byteArrayOutputStream, Integer.parseInt(oidTokenizer.nextToken()));
        }
        derOutputStream2.close();
        derOutputStream.b(6, byteArrayOutputStream.toByteArray());
    }
    
    public int hashCode() {
        return this.yb.hashCode();
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DERObjectIdentifier && this.yb.equals(((DERObjectIdentifier)o).yb);
    }
}
