package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.util.Encodable;

public abstract class ASN1Object implements ASN1Encodable, Encodable
{
    public byte[] getEncoded() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new ASN1OutputStream(byteArrayOutputStream).writeObject(this);
        return byteArrayOutputStream.toByteArray();
    }
    
    public byte[] getEncoded(final String s) throws IOException {
        if (s.equals("DER")) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new DEROutputStream(byteArrayOutputStream).writeObject(this);
            return byteArrayOutputStream.toByteArray();
        }
        if (s.equals("DL")) {
            final ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            new DLOutputStream(byteArrayOutputStream2).writeObject(this);
            return byteArrayOutputStream2.toByteArray();
        }
        return this.getEncoded();
    }
    
    @Override
    public int hashCode() {
        return this.toASN1Primitive().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ASN1Encodable && this.toASN1Primitive().equals(((ASN1Encodable)o).toASN1Primitive()));
    }
    
    @Deprecated
    public ASN1Primitive toASN1Object() {
        return this.toASN1Primitive();
    }
    
    protected static boolean hasEncodedTagValue(final Object o, final int n) {
        return o instanceof byte[] && ((byte[])o)[0] == n;
    }
    
    public abstract ASN1Primitive toASN1Primitive();
}
