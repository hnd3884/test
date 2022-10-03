package jcifs.spnego.asn1;

import java.io.IOException;
import java.util.Enumeration;

public class BERSet extends DERSet
{
    public BERSet() {
    }
    
    public BERSet(final DEREncodable obj) {
        super(obj);
    }
    
    public BERSet(final DEREncodableVector v) {
        super(v);
    }
    
    void encode(final DEROutputStream out) throws IOException {
        if (out instanceof ASN1OutputStream || out instanceof BEROutputStream) {
            out.write(49);
            out.write(128);
            final Enumeration e = this.getObjects();
            while (e.hasMoreElements()) {
                out.writeObject(e.nextElement());
            }
            out.write(0);
            out.write(0);
        }
        else {
            super.encode(out);
        }
    }
}
