package com.maverick.crypto.asn1;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class DERApplicationSpecific extends DERObject
{
    private int tb;
    private byte[] ub;
    
    public DERApplicationSpecific(final int tb, final byte[] ub) {
        this.tb = tb;
        this.ub = ub;
    }
    
    public DERApplicationSpecific(final int n, final DEREncodable derEncodable) throws IOException {
        this.tb = (n | 0x20);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new DEROutputStream(byteArrayOutputStream).writeObject(derEncodable);
        this.ub = byteArrayOutputStream.toByteArray();
    }
    
    public boolean isConstructed() {
        return (this.tb & 0x20) != 0x0;
    }
    
    public byte[] getContents() {
        return this.ub;
    }
    
    public int getApplicationTag() {
        return this.tb & 0x1F;
    }
    
    public DERObject getObject() throws IOException {
        return new ASN1InputStream(new ByteArrayInputStream(this.getContents())).readObject();
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        derOutputStream.b(0x40 | this.tb, this.ub);
    }
}
