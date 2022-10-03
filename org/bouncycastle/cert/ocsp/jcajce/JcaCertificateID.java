package org.bouncycastle.cert.ocsp.jcajce;

import java.security.cert.CertificateEncodingException;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.cert.ocsp.CertificateID;

public class JcaCertificateID extends CertificateID
{
    public JcaCertificateID(final DigestCalculator digestCalculator, final X509Certificate x509Certificate, final BigInteger bigInteger) throws OCSPException, CertificateEncodingException {
        super(digestCalculator, new JcaX509CertificateHolder(x509Certificate), bigInteger);
    }
}
