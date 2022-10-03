package com.maverick.ssh.components.jce;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import com.maverick.util.ByteArrayReader;
import com.maverick.ssh.SshException;
import com.maverick.util.ByteArrayWriter;
import java.security.interfaces.RSAPublicKey;
import java.security.cert.X509Certificate;

public class SshX509RsaSha1PublicKey extends Ssh2RsaPublicKey
{
    public static final String X509V3_SIGN_RSA_SHA1 = "x509v3-sign-rsa-sha1";
    X509Certificate d;
    
    public SshX509RsaSha1PublicKey() {
    }
    
    public SshX509RsaSha1PublicKey(final X509Certificate d) {
        super((RSAPublicKey)d.getPublicKey());
        this.d = d;
    }
    
    public String getAlgorithm() {
        return "x509v3-sign-rsa-sha1";
    }
    
    public byte[] getEncoded() throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString(this.getAlgorithm());
            byteArrayWriter.writeBinaryString(this.d.getEncoded());
            return byteArrayWriter.toByteArray();
        }
        catch (final Throwable t) {
            throw new SshException("Failed to encoded key data", 5, t);
        }
    }
    
    public void init(final byte[] array, final int n, final int n2) throws SshException {
        try {
            final ByteArrayReader byteArrayReader = new ByteArrayReader(array, n, n2);
            if (!byteArrayReader.readString().equals("x509v3-sign-rsa-sha1")) {
                throw new SshException("The encoded key is not X509 RSA", 5);
            }
            this.d = (X509Certificate)((JCEProvider.getProviderForAlgorithm("X.509") == null) ? CertificateFactory.getInstance("X.509") : CertificateFactory.getInstance("X.509", JCEProvider.getProviderForAlgorithm("X.509"))).generateCertificate(new ByteArrayInputStream(byteArrayReader.readBinaryString()));
            if (!(this.d.getPublicKey() instanceof RSAPublicKey)) {
                throw new SshException("Certificate public key is not an RSA public key!", 4);
            }
            super.c = (RSAPublicKey)this.d.getPublicKey();
        }
        catch (final Throwable t) {
            throw new SshException(t.getMessage(), 16, t);
        }
    }
    
    public X509Certificate getCertificate() {
        return this.d;
    }
}
