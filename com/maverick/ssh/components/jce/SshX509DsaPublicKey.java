package com.maverick.ssh.components.jce;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import com.maverick.ssh.SshException;
import java.security.interfaces.DSAPublicKey;
import java.security.cert.X509Certificate;

public class SshX509DsaPublicKey extends Ssh2DsaPublicKey
{
    public static final String X509V3_SIGN_DSA = "x509v3-sign-dss";
    X509Certificate b;
    
    public SshX509DsaPublicKey() {
    }
    
    public SshX509DsaPublicKey(final X509Certificate b) {
        super((DSAPublicKey)b.getPublicKey());
        this.b = b;
    }
    
    public String getAlgorithm() {
        return "x509v3-sign-dss";
    }
    
    public byte[] getEncoded() throws SshException {
        try {
            return this.b.getEncoded();
        }
        catch (final Throwable t) {
            throw new SshException("Failed to encoded key data", 5, t);
        }
    }
    
    public void init(final byte[] array, final int n, final int n2) throws SshException {
        try {
            this.b = (X509Certificate)((JCEProvider.getProviderForAlgorithm("X.509") == null) ? CertificateFactory.getInstance("X.509") : CertificateFactory.getInstance("X.509", JCEProvider.getProviderForAlgorithm("X.509"))).generateCertificate(new ByteArrayInputStream(array, n, n2));
            if (!(this.b.getPublicKey() instanceof DSAPublicKey)) {
                throw new SshException("Certificate public key is not an DSA public key!", 4);
            }
            super.pubkey = (DSAPublicKey)this.b.getPublicKey();
        }
        catch (final Throwable t) {
            throw new SshException(t.getMessage(), 16, t);
        }
    }
    
    public X509Certificate getCertificate() {
        return this.b;
    }
}
