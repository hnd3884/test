package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

public class DLOutputStream extends ASN1OutputStream
{
    public DLOutputStream(final OutputStream outputStream) {
        super(outputStream);
    }
    
    @Override
    public void writeObject(final ASN1Encodable asn1Encodable) throws IOException {
        if (asn1Encodable != null) {
            asn1Encodable.toASN1Primitive().toDLObject().encode(this);
            return;
        }
        throw new IOException("null object detected");
    }
}
