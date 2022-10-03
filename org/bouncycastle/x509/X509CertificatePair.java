package org.bouncycastle.x509;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.ASN1InputStream;
import java.security.cert.CertificateParsingException;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.asn1.x509.CertificatePair;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import java.security.cert.X509Certificate;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public class X509CertificatePair
{
    private final JcaJceHelper bcHelper;
    private X509Certificate forward;
    private X509Certificate reverse;
    
    public X509CertificatePair(final X509Certificate forward, final X509Certificate reverse) {
        this.bcHelper = new BCJcaJceHelper();
        this.forward = forward;
        this.reverse = reverse;
    }
    
    public X509CertificatePair(final CertificatePair certificatePair) throws CertificateParsingException {
        this.bcHelper = new BCJcaJceHelper();
        if (certificatePair.getForward() != null) {
            this.forward = new X509CertificateObject(certificatePair.getForward());
        }
        if (certificatePair.getReverse() != null) {
            this.reverse = new X509CertificateObject(certificatePair.getReverse());
        }
    }
    
    public byte[] getEncoded() throws CertificateEncodingException {
        Certificate instance = null;
        Certificate instance2 = null;
        try {
            if (this.forward != null) {
                instance = Certificate.getInstance(new ASN1InputStream(this.forward.getEncoded()).readObject());
                if (instance == null) {
                    throw new CertificateEncodingException("unable to get encoding for forward");
                }
            }
            if (this.reverse != null) {
                instance2 = Certificate.getInstance(new ASN1InputStream(this.reverse.getEncoded()).readObject());
                if (instance2 == null) {
                    throw new CertificateEncodingException("unable to get encoding for reverse");
                }
            }
            return new CertificatePair(instance, instance2).getEncoded("DER");
        }
        catch (final IllegalArgumentException ex) {
            throw new ExtCertificateEncodingException(ex.toString(), ex);
        }
        catch (final IOException ex2) {
            throw new ExtCertificateEncodingException(ex2.toString(), ex2);
        }
    }
    
    public X509Certificate getForward() {
        return this.forward;
    }
    
    public X509Certificate getReverse() {
        return this.reverse;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof X509CertificatePair)) {
            return false;
        }
        final X509CertificatePair x509CertificatePair = (X509CertificatePair)o;
        boolean equals = true;
        boolean equals2 = true;
        if (this.forward != null) {
            equals2 = this.forward.equals(x509CertificatePair.forward);
        }
        else if (x509CertificatePair.forward != null) {
            equals2 = false;
        }
        if (this.reverse != null) {
            equals = this.reverse.equals(x509CertificatePair.reverse);
        }
        else if (x509CertificatePair.reverse != null) {
            equals = false;
        }
        return equals2 && equals;
    }
    
    @Override
    public int hashCode() {
        int n = -1;
        if (this.forward != null) {
            n ^= this.forward.hashCode();
        }
        if (this.reverse != null) {
            n = (n * 17 ^ this.reverse.hashCode());
        }
        return n;
    }
}
