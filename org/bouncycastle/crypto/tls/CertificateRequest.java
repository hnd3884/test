package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import org.bouncycastle.asn1.x500.X500Name;
import java.io.OutputStream;
import java.util.Vector;

public class CertificateRequest
{
    protected short[] certificateTypes;
    protected Vector supportedSignatureAlgorithms;
    protected Vector certificateAuthorities;
    
    public CertificateRequest(final short[] certificateTypes, final Vector supportedSignatureAlgorithms, final Vector certificateAuthorities) {
        this.certificateTypes = certificateTypes;
        this.supportedSignatureAlgorithms = supportedSignatureAlgorithms;
        this.certificateAuthorities = certificateAuthorities;
    }
    
    public short[] getCertificateTypes() {
        return this.certificateTypes;
    }
    
    public Vector getSupportedSignatureAlgorithms() {
        return this.supportedSignatureAlgorithms;
    }
    
    public Vector getCertificateAuthorities() {
        return this.certificateAuthorities;
    }
    
    public void encode(final OutputStream outputStream) throws IOException {
        if (this.certificateTypes == null || this.certificateTypes.length == 0) {
            TlsUtils.writeUint8(0, outputStream);
        }
        else {
            TlsUtils.writeUint8ArrayWithUint8Length(this.certificateTypes, outputStream);
        }
        if (this.supportedSignatureAlgorithms != null) {
            TlsUtils.encodeSupportedSignatureAlgorithms(this.supportedSignatureAlgorithms, false, outputStream);
        }
        if (this.certificateAuthorities == null || this.certificateAuthorities.isEmpty()) {
            TlsUtils.writeUint16(0, outputStream);
        }
        else {
            final Vector vector = new Vector(this.certificateAuthorities.size());
            int n = 0;
            for (int i = 0; i < this.certificateAuthorities.size(); ++i) {
                final byte[] encoded = this.certificateAuthorities.elementAt(i).getEncoded("DER");
                vector.addElement(encoded);
                n += encoded.length + 2;
            }
            TlsUtils.checkUint16(n);
            TlsUtils.writeUint16(n, outputStream);
            for (int j = 0; j < vector.size(); ++j) {
                TlsUtils.writeOpaque16((byte[])vector.elementAt(j), outputStream);
            }
        }
    }
    
    public static CertificateRequest parse(final TlsContext tlsContext, final InputStream inputStream) throws IOException {
        final short uint8 = TlsUtils.readUint8(inputStream);
        final short[] array = new short[uint8];
        for (short n = 0; n < uint8; ++n) {
            array[n] = TlsUtils.readUint8(inputStream);
        }
        Vector supportedSignatureAlgorithms = null;
        if (TlsUtils.isTLSv12(tlsContext)) {
            supportedSignatureAlgorithms = TlsUtils.parseSupportedSignatureAlgorithms(false, inputStream);
        }
        final Vector<X500Name> vector = new Vector<X500Name>();
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(TlsUtils.readOpaque16(inputStream));
        while (byteArrayInputStream.available() > 0) {
            vector.addElement(X500Name.getInstance(TlsUtils.readDERObject(TlsUtils.readOpaque16(byteArrayInputStream))));
        }
        return new CertificateRequest(array, supportedSignatureAlgorithms, vector);
    }
}
