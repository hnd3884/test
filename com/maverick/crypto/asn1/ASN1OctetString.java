package com.maverick.crypto.asn1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Vector;

public abstract class ASN1OctetString extends DERObject
{
    byte[] ec;
    
    public static ASN1OctetString getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public static ASN1OctetString getInstance(final Object o) {
        if (o == null || o instanceof ASN1OctetString) {
            return (ASN1OctetString)o;
        }
        if (o instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)o).getObject());
        }
        if (o instanceof ASN1Sequence) {
            final Vector vector = new Vector();
            final Enumeration objects = ((ASN1Sequence)o).getObjects();
            while (objects.hasMoreElements()) {
                vector.addElement(objects.nextElement());
            }
            return new BERConstructedOctetString(vector);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public ASN1OctetString(final byte[] ec) {
        this.ec = ec;
    }
    
    public ASN1OctetString(final DEREncodable derEncodable) {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final DEROutputStream derOutputStream = new DEROutputStream(byteArrayOutputStream);
            derOutputStream.writeObject(derEncodable);
            derOutputStream.close();
            this.ec = byteArrayOutputStream.toByteArray();
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("Error processing object : " + ex.toString());
        }
    }
    
    public byte[] getOctets() {
        return this.ec;
    }
    
    public int hashCode() {
        final byte[] octets = this.getOctets();
        int n = 0;
        for (int i = 0; i != octets.length; ++i) {
            n ^= (octets[i] & 0xFF) << i % 4;
        }
        return n;
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof DEROctetString)) {
            return false;
        }
        final byte[] octets = ((DEROctetString)o).getOctets();
        final byte[] octets2 = this.getOctets();
        if (octets.length != octets2.length) {
            return false;
        }
        for (int i = 0; i != octets.length; ++i) {
            if (octets[i] != octets2[i]) {
                return false;
            }
        }
        return true;
    }
    
    abstract void encode(final DEROutputStream p0) throws IOException;
}
