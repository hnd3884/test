package jcifs.spnego.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class DERSet extends ASN1Set
{
    public DERSet() {
    }
    
    public DERSet(final DEREncodable obj) {
        this.addObject(obj);
    }
    
    public DERSet(final DEREncodableVector v) {
        for (int i = 0; i != v.size(); ++i) {
            this.addObject(v.get(i));
        }
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
