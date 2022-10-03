package jcifs.spnego.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public abstract class ASN1Set extends DERObject
{
    protected Vector set;
    
    public static ASN1Set getInstance(final Object obj) {
        if (obj == null || obj instanceof ASN1Set) {
            return (ASN1Set)obj;
        }
        throw new IllegalArgumentException("unknown object in getInstance");
    }
    
    public static ASN1Set getInstance(final ASN1TaggedObject obj, final boolean explicit) {
        if (explicit) {
            if (!obj.isExplicit()) {
                throw new IllegalArgumentException("object implicit - explicit expected.");
            }
            return (ASN1Set)obj.getObject();
        }
        else {
            if (obj.isExplicit()) {
                final ASN1Set set = new DERSet(obj.getObject());
                return set;
            }
            if (obj.getObject() instanceof ASN1Set) {
                return (ASN1Set)obj.getObject();
            }
            final ASN1EncodableVector v = new ASN1EncodableVector();
            if (obj.getObject() instanceof ASN1Sequence) {
                final ASN1Sequence s = (ASN1Sequence)obj.getObject();
                final Enumeration e = s.getObjects();
                while (e.hasMoreElements()) {
                    v.add(e.nextElement());
                }
                return new DERSet(v);
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
    
    public DEREncodable getObjectAt(final int index) {
        return this.set.elementAt(index);
    }
    
    public int size() {
        return this.set.size();
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
        if (o == null || !(o instanceof ASN1Set)) {
            return false;
        }
        final ASN1Set other = (ASN1Set)o;
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
        this.set.addElement(obj);
    }
    
    abstract void encode(final DEROutputStream p0) throws IOException;
}
