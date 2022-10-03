package jcifs.spnego.asn1;

import java.util.Vector;

public class DEREncodableVector
{
    private Vector v;
    
    public DEREncodableVector() {
        this.v = new Vector();
    }
    
    public void add(final DEREncodable obj) {
        this.v.addElement(obj);
    }
    
    public DEREncodable get(final int i) {
        return this.v.elementAt(i);
    }
    
    public int size() {
        return this.v.size();
    }
}
