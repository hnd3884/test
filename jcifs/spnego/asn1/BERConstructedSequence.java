package jcifs.spnego.asn1;

import java.io.IOException;
import java.util.Enumeration;

public class BERConstructedSequence extends DERConstructedSequence
{
    void encode(final DEROutputStream out) throws IOException {
        if (out instanceof ASN1OutputStream || out instanceof BEROutputStream) {
            out.write(48);
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
