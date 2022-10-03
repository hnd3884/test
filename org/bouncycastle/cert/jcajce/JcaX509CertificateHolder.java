package org.bouncycastle.cert.jcajce;

import java.security.cert.CertificateEncodingException;
import org.bouncycastle.asn1.x509.Certificate;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.X509CertificateHolder;

public class JcaX509CertificateHolder extends X509CertificateHolder
{
    public JcaX509CertificateHolder(final X509Certificate x509Certificate) throws CertificateEncodingException {
        super(Certificate.getInstance((Object)x509Certificate.getEncoded()));
    }
}
