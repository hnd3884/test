package org.bouncycastle.asn1;

import java.util.Enumeration;
import java.util.Vector;

public class ASN1EncodableVector
{
    private final Vector v;
    
    public ASN1EncodableVector() {
        this.v = new Vector();
    }
    
    public void add(final ASN1Encodable asn1Encodable) {
        this.v.addElement(asn1Encodable);
    }
    
    public void addAll(final ASN1EncodableVector asn1EncodableVector) {
        final Enumeration elements = asn1EncodableVector.v.elements();
        while (elements.hasMoreElements()) {
            this.v.addElement(elements.nextElement());
        }
    }
    
    public ASN1Encodable get(final int n) {
        return this.v.elementAt(n);
    }
    
    public int size() {
        return this.v.size();
    }
}
