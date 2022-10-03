package com.maverick.crypto.asn1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public abstract class ASN1Encodable implements DEREncodable
{
    public byte[] getEncoded() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new ASN1OutputStream(byteArrayOutputStream).writeObject(this);
        return byteArrayOutputStream.toByteArray();
    }
    
    public int hashCode() {
        return this.getDERObject().hashCode();
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DEREncodable && this.getDERObject().equals(((DEREncodable)o).getDERObject());
    }
    
    public DERObject getDERObject() {
        return this.toASN1Object();
    }
    
    public abstract DERObject toASN1Object();
}
