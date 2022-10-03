package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Vector;
import java.io.OutputStream;

public class Certificate
{
    public static final Certificate EMPTY_CHAIN;
    protected org.bouncycastle.asn1.x509.Certificate[] certificateList;
    
    public Certificate(final org.bouncycastle.asn1.x509.Certificate[] certificateList) {
        if (certificateList == null) {
            throw new IllegalArgumentException("'certificateList' cannot be null");
        }
        this.certificateList = certificateList;
    }
    
    public org.bouncycastle.asn1.x509.Certificate[] getCertificateList() {
        return this.cloneCertificateList();
    }
    
    public org.bouncycastle.asn1.x509.Certificate getCertificateAt(final int n) {
        return this.certificateList[n];
    }
    
    public int getLength() {
        return this.certificateList.length;
    }
    
    public boolean isEmpty() {
        return this.certificateList.length == 0;
    }
    
    public void encode(final OutputStream outputStream) throws IOException {
        final Vector vector = new Vector(this.certificateList.length);
        int n = 0;
        for (int i = 0; i < this.certificateList.length; ++i) {
            final byte[] encoded = this.certificateList[i].getEncoded("DER");
            vector.addElement(encoded);
            n += encoded.length + 3;
        }
        TlsUtils.checkUint24(n);
        TlsUtils.writeUint24(n, outputStream);
        for (int j = 0; j < vector.size(); ++j) {
            TlsUtils.writeOpaque24((byte[])vector.elementAt(j), outputStream);
        }
    }
    
    public static Certificate parse(final InputStream inputStream) throws IOException {
        final int uint24 = TlsUtils.readUint24(inputStream);
        if (uint24 == 0) {
            return Certificate.EMPTY_CHAIN;
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(TlsUtils.readFully(uint24, inputStream));
        final Vector vector = new Vector();
        while (byteArrayInputStream.available() > 0) {
            vector.addElement(org.bouncycastle.asn1.x509.Certificate.getInstance(TlsUtils.readASN1Object(TlsUtils.readOpaque24(byteArrayInputStream))));
        }
        final org.bouncycastle.asn1.x509.Certificate[] array = new org.bouncycastle.asn1.x509.Certificate[vector.size()];
        for (int i = 0; i < vector.size(); ++i) {
            array[i] = (org.bouncycastle.asn1.x509.Certificate)vector.elementAt(i);
        }
        return new Certificate(array);
    }
    
    protected org.bouncycastle.asn1.x509.Certificate[] cloneCertificateList() {
        final org.bouncycastle.asn1.x509.Certificate[] array = new org.bouncycastle.asn1.x509.Certificate[this.certificateList.length];
        System.arraycopy(this.certificateList, 0, array, 0, array.length);
        return array;
    }
    
    static {
        EMPTY_CHAIN = new Certificate(new org.bouncycastle.asn1.x509.Certificate[0]);
    }
}
