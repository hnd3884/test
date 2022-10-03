package com.maverick.crypto.asn1;

import java.io.IOException;

public abstract class ASN1TaggedObject extends DERObject
{
    int bc;
    boolean cc;
    boolean ac;
    DEREncodable dc;
    
    public static ASN1TaggedObject getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        if (b) {
            return (ASN1TaggedObject)asn1TaggedObject.getObject();
        }
        throw new IllegalArgumentException("implicitly tagged tagged object");
    }
    
    public ASN1TaggedObject(final int bc, final DEREncodable dc) {
        this.cc = false;
        this.ac = true;
        this.dc = null;
        this.ac = true;
        this.bc = bc;
        this.dc = dc;
    }
    
    public ASN1TaggedObject(final boolean ac, final int bc, final DEREncodable dc) {
        this.cc = false;
        this.ac = true;
        this.dc = null;
        this.ac = ac;
        this.bc = bc;
        this.dc = dc;
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof ASN1TaggedObject)) {
            return false;
        }
        final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)o;
        if (this.bc != asn1TaggedObject.bc || this.cc != asn1TaggedObject.cc || this.ac != asn1TaggedObject.ac) {
            return false;
        }
        if (this.dc == null) {
            if (asn1TaggedObject.dc != null) {
                return false;
            }
        }
        else if (!this.dc.equals(asn1TaggedObject.dc)) {
            return false;
        }
        return true;
    }
    
    public int hashCode() {
        int bc = this.bc;
        if (this.dc != null) {
            bc ^= this.dc.hashCode();
        }
        return bc;
    }
    
    public int getTagNo() {
        return this.bc;
    }
    
    public boolean isExplicit() {
        return this.ac;
    }
    
    public boolean isEmpty() {
        return this.cc;
    }
    
    public DERObject getObject() {
        if (this.dc != null) {
            return this.dc.getDERObject();
        }
        return null;
    }
    
    abstract void encode(final DEROutputStream p0) throws IOException;
}
