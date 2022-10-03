package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

public class BEROutputStream extends DEROutputStream
{
    public BEROutputStream(final OutputStream outputStream) {
        super(outputStream);
    }
    
    public void writeObject(final Object o) throws IOException {
        if (o == null) {
            this.writeNull();
        }
        else if (o instanceof ASN1Primitive) {
            ((ASN1Primitive)o).encode(this);
        }
        else {
            if (!(o instanceof ASN1Encodable)) {
                throw new IOException("object not BEREncodable");
            }
            ((ASN1Encodable)o).toASN1Primitive().encode(this);
        }
    }
}
