package jcifs.spnego.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class DERConstructedSet extends ASN1Set
{
    public DERConstructedSet() {
    }
    
    public DERConstructedSet(final DEREncodable obj) {
        this.addObject(obj);
    }
    
    public DERConstructedSet(final DEREncodableVector v) {
        for (int i = 0; i != v.size(); ++i) {
            this.addObject(v.get(i));
        }
    }
    
    public void addObject(final DEREncodable obj) {
        super.addObject(obj);
    }
    
    public int getSize() {
        return this.size();
    }
    
    void encode(final DEROutputStream out) throws IOException {
        final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        final DEROutputStream dOut = new DEROutputStream(bOut);
        final Enumeration e = this.getObjects();
        while (e.hasMoreElements()) {
            final Object obj = e.nextElement();
            dOut.writeObject(obj);
        }
        dOut.close();
        final byte[] bytes = bOut.toByteArray();
        out.writeEncoded(49, bytes);
    }
}
