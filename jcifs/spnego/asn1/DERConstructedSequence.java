package jcifs.spnego.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class DERConstructedSequence extends ASN1Sequence
{
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
        out.writeEncoded(48, bytes);
    }
}
