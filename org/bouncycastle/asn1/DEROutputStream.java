package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

public class DEROutputStream extends ASN1OutputStream
{
    public DEROutputStream(final OutputStream outputStream) {
        super(outputStream);
    }
    
    @Override
    public void writeObject(final ASN1Encodable asn1Encodable) throws IOException {
        if (asn1Encodable != null) {
            asn1Encodable.toASN1Primitive().toDERObject().encode(this);
            return;
        }
        throw new IOException("null object detected");
    }
    
    @Override
    ASN1OutputStream getDERSubStream() {
        return this;
    }
    
    @Override
    ASN1OutputStream getDLSubStream() {
        return this;
    }
}
