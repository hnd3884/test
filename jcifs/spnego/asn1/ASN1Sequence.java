package jcifs.spnego.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public abstract class ASN1Sequence extends DERObject
{
    private Vector seq;
    
    public ASN1Sequence() {
        this.seq = new Vector();
    }
    
    public static ASN1Sequence getInstance(final Object obj) {
        if (obj == null || obj instanceof ASN1Sequence) {
            return (ASN1Sequence)obj;
        }
        throw new IllegalArgumentException("unknown object in getInstance");
    }
    
    public static ASN1Sequence getInstance(final ASN1TaggedObject obj, final boolean explicit) {
        if (explicit) {
            if (!obj.isExplicit()) {
                throw new IllegalArgumentException("object implicit - explicit expected.");
            }
            return (ASN1Sequence)obj.getObject();
        }
        else if (obj.isExplicit()) {
            if (obj instanceof BERTaggedObject) {
                return new BERSequence(obj.getObject());
            }
            return new DERSequence(obj.getObject());
        }
        else {
            if (obj.getObject() instanceof ASN1Sequence) {
                return (ASN1Sequence)obj.getObject();
            }
            throw new IllegalArgumentException("unknown object in getInstanceFromTagged");
        }
    }
    
    public Enumeration getObjects() {
        return this.seq.elements();
    }
    
    public DEREncodable getObjectAt(final int index) {
        return this.seq.elementAt(index);
    }
    
    public int size() {
        return this.seq.size();
    }
    
    public int hashCode() {
        final Enumeration e = this.getObjects();
        int hashCode = 0;
        while (e.hasMoreElements()) {
            hashCode ^= e.nextElement().hashCode();
        }
        return hashCode;
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof ASN1Sequence)) {
            return false;
        }
        final ASN1Sequence other = (ASN1Sequence)o;
        if (this.size() != other.size()) {
            return false;
        }
        final Enumeration s1 = this.getObjects();
        final Enumeration s2 = other.getObjects();
        while (s1.hasMoreElements()) {
            if (!s1.nextElement().equals(s2.nextElement())) {
                return false;
            }
        }
        return true;
    }
    
    protected void addObject(final DEREncodable obj) {
        this.seq.addElement(obj);
    }
    
    abstract void encode(final DEROutputStream p0) throws IOException;
}
