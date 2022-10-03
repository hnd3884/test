package com.maverick.crypto.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public abstract class ASN1Sequence extends DERObject
{
    private Vector zb;
    
    public ASN1Sequence() {
        this.zb = new Vector();
    }
    
    public static ASN1Sequence getInstance(final Object o) {
        if (o == null || o instanceof ASN1Sequence) {
            return (ASN1Sequence)o;
        }
        throw new IllegalArgumentException("unknown object in getInstance");
    }
    
    public static ASN1Sequence getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        if (b) {
            if (!asn1TaggedObject.isExplicit()) {
                throw new IllegalArgumentException("object implicit - explicit expected.");
            }
            return (ASN1Sequence)asn1TaggedObject.getObject();
        }
        else if (asn1TaggedObject.isExplicit()) {
            if (asn1TaggedObject instanceof BERTaggedObject) {
                return new BERSequence(asn1TaggedObject.getObject());
            }
            return new DERSequence(asn1TaggedObject.getObject());
        }
        else {
            if (asn1TaggedObject.getObject() instanceof ASN1Sequence) {
                return (ASN1Sequence)asn1TaggedObject.getObject();
            }
            throw new IllegalArgumentException("unknown object in getInstanceFromTagged");
        }
    }
    
    public Enumeration getObjects() {
        return this.zb.elements();
    }
    
    public DEREncodable getObjectAt(final int n) {
        return this.zb.elementAt(n);
    }
    
    public int size() {
        return this.zb.size();
    }
    
    public int hashCode() {
        final Enumeration objects = this.getObjects();
        int n = 0;
        while (objects.hasMoreElements()) {
            final Object nextElement = objects.nextElement();
            if (nextElement != null) {
                n ^= nextElement.hashCode();
            }
        }
        return n;
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof ASN1Sequence)) {
            return false;
        }
        final ASN1Sequence asn1Sequence = (ASN1Sequence)o;
        if (this.size() != asn1Sequence.size()) {
            return false;
        }
        final Enumeration objects = this.getObjects();
        final Enumeration objects2 = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            final Object nextElement = objects.nextElement();
            final Object nextElement2 = objects2.nextElement();
            if (nextElement != null && nextElement2 != null) {
                if (!nextElement.equals(nextElement2)) {
                    return false;
                }
                continue;
            }
            else {
                if (nextElement == null && nextElement2 == null) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }
    
    protected void addObject(final DEREncodable derEncodable) {
        this.zb.addElement(derEncodable);
    }
    
    abstract void encode(final DEROutputStream p0) throws IOException;
}
