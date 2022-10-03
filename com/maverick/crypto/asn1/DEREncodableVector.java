package com.maverick.crypto.asn1;

import java.util.Vector;

public class DEREncodableVector
{
    private Vector b;
    
    public DEREncodableVector() {
        this.b = new Vector();
    }
    
    public void add(final DEREncodable derEncodable) {
        this.b.addElement(derEncodable);
    }
    
    public DEREncodable get(final int n) {
        return this.b.elementAt(n);
    }
    
    public int size() {
        return this.b.size();
    }
}
