package com.maverick.ssh.components.jce;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import com.maverick.ssh.SshException;
import java.security.interfaces.RSAPublicKey;
import java.security.cert.X509Certificate;

public class SshX509RsaPublicKey extends Ssh2RsaPublicKey
{
    public static final String X509V3_SIGN_RSA = "x509v3-sign-rsa";
    X509Certificate e;
    
    public SshX509RsaPublicKey() {
    }
    
    public SshX509RsaPublicKey(final X509Certificate e) {
        super((RSAPublicKey)e.getPublicKey());
        this.e = e;
    }
    
    public String getAlgorithm() {
        return "x509v3-sign-rsa";
    }
    
    public byte[] getEncoded() throws SshException {
        try {
            return this.e.getEncoded();
        }
        catch (final Throwable t) {
            throw new SshException("Failed to encoded key data", 5, t);
        }
    }
    
    public void init(final byte[] array, final int n, final int n2) throws SshException {
        try {
            this.e = (X509Certificate)((JCEProvider.getProviderForAlgorithm("X.509") == null) ? CertificateFactory.getInstance("X.509") : CertificateFactory.getInstance("X.509", JCEProvider.getProviderForAlgorithm("X.509"))).generateCertificate(new ByteArrayInputStream(array, n, n2));
            if (!(this.e.getPublicKey() instanceof RSAPublicKey)) {
                throw new SshException("Certificate public key is not an RSA public key!", 4);
            }
            super.c = (RSAPublicKey)this.e.getPublicKey();
        }
        catch (final Throwable t) {
            throw new SshException(t.getMessage(), 16, t);
        }
    }
    
    public X509Certificate getCertificate() {
        return this.e;
    }
}
