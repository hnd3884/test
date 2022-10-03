package jcifs.spnego.asn1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Vector;

public abstract class ASN1OctetString extends DERObject
{
    byte[] string;
    
    public static ASN1OctetString getInstance(final ASN1TaggedObject obj, final boolean explicit) {
        return getInstance(obj.getObject());
    }
    
    public static ASN1OctetString getInstance(final Object obj) {
        if (obj == null || obj instanceof ASN1OctetString) {
            return (ASN1OctetString)obj;
        }
        if (obj instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)obj).getObject());
        }
        if (obj instanceof ASN1Sequence) {
            final Vector v = new Vector();
            final Enumeration e = ((ASN1Sequence)obj).getObjects();
            while (e.hasMoreElements()) {
                v.addElement(e.nextElement());
            }
            return new BERConstructedOctetString(v);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }
    
    public ASN1OctetString(final byte[] string) {
        this.string = string;
    }
    
    public ASN1OctetString(final DEREncodable obj) {
        try {
            final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            final DEROutputStream dOut = new DEROutputStream(bOut);
            dOut.writeObject(obj);
            dOut.close();
            this.string = bOut.toByteArray();
        }
        catch (final IOException e) {
            throw new IllegalArgumentException("Error processing object : " + e.toString());
        }
    }
    
    public byte[] getOctets() {
        return this.string;
    }
    
    public int hashCode() {
        final byte[] b = this.getOctets();
        int value = 0;
        for (int i = 0; i != b.length; ++i) {
            value ^= (b[i] & 0xFF) << i % 4;
        }
        return value;
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof DEROctetString)) {
            return false;
        }
        final DEROctetString other = (DEROctetString)o;
        final byte[] b1 = other.getOctets();
        final byte[] b2 = this.getOctets();
        if (b1.length != b2.length) {
            return false;
        }
        for (int i = 0; i != b1.length; ++i) {
            if (b1[i] != b2[i]) {
                return false;
            }
        }
        return true;
    }
    
    abstract void encode(final DEROutputStream p0) throws IOException;
}
