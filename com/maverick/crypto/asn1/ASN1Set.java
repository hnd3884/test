package com.maverick.crypto.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public abstract class ASN1Set extends DERObject
{
    protected Vector set;
    
    public static ASN1Set getInstance(final Object o) {
        if (o == null || o instanceof ASN1Set) {
            return (ASN1Set)o;
        }
        throw new IllegalArgumentException("unknown object in getInstance");
    }
    
    public static ASN1Set getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        if (b) {
            if (!asn1TaggedObject.isExplicit()) {
                throw new IllegalArgumentException("object implicit - explicit expected.");
            }
            return (ASN1Set)asn1TaggedObject.getObject();
        }
        else {
            if (asn1TaggedObject.isExplicit()) {
                return new DERSet(asn1TaggedObject.getObject());
            }
            if (asn1TaggedObject.getObject() instanceof ASN1Set) {
                return (ASN1Set)asn1TaggedObject.getObject();
            }
            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            if (asn1TaggedObject.getObject() instanceof ASN1Sequence) {
                final Enumeration objects = ((ASN1Sequence)asn1TaggedObject.getObject()).getObjects();
                while (objects.hasMoreElements()) {
                    asn1EncodableVector.add((DEREncodable)objects.nextElement());
                }
                return new DERSet(asn1EncodableVector);
            }
            throw new IllegalArgumentException("unknown object in getInstanceFromTagged");
        }
    }
    
    public ASN1Set() {
        this.set = new Vector();
    }
    
    public Enumeration getObjects() {
        return this.set.elements();
    }
    
    public DEREncodable getObjectAt(final int n) {
        return this.set.elementAt(n);
    }
    
    public int size() {
        return this.set.size();
    }
    
    public int hashCode() {
        final Enumeration objects = this.getObjects();
        int n = 0;
        while (objects.hasMoreElements()) {
            n ^= objects.nextElement().hashCode();
        }
        return n;
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof ASN1Set)) {
            return false;
        }
        final ASN1Set set = (ASN1Set)o;
        if (this.size() != set.size()) {
            return false;
        }
        final Enumeration objects = this.getObjects();
        final Enumeration objects2 = set.getObjects();
        while (objects.hasMoreElements()) {
            if (!objects.nextElement().equals(objects2.nextElement())) {
                return false;
            }
        }
        return true;
    }
    
    protected void addObject(final DEREncodable derEncodable) {
        this.set.addElement(derEncodable);
    }
    
    abstract void encode(final DEROutputStream p0) throws IOException;
}
