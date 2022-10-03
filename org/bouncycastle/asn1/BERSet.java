package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;

public class BERSet extends ASN1Set
{
    public BERSet() {
    }
    
    public BERSet(final ASN1Encodable asn1Encodable) {
        super(asn1Encodable);
    }
    
    public BERSet(final ASN1EncodableVector asn1EncodableVector) {
        super(asn1EncodableVector, false);
    }
    
    public BERSet(final ASN1Encodable[] array) {
        super(array, false);
    }
    
    @Override
    int encodedLength() throws IOException {
        int n = 0;
        final Enumeration objects = this.getObjects();
        while (objects.hasMoreElements()) {
            n += ((ASN1Encodable)objects.nextElement()).toASN1Primitive().encodedLength();
        }
        return 2 + n + 2;
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        asn1OutputStream.write(49);
        asn1OutputStream.write(128);
        final Enumeration objects = this.getObjects();
        while (objects.hasMoreElements()) {
            asn1OutputStream.writeObject((ASN1Encodable)objects.nextElement());
        }
        asn1OutputStream.write(0);
        asn1OutputStream.write(0);
    }
}
