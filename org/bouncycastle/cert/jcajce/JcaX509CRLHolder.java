package org.bouncycastle.cert.jcajce;

import java.security.cert.CRLException;
import org.bouncycastle.asn1.x509.CertificateList;
import java.security.cert.X509CRL;
import org.bouncycastle.cert.X509CRLHolder;

public class JcaX509CRLHolder extends X509CRLHolder
{
    public JcaX509CRLHolder(final X509CRL x509CRL) throws CRLException {
        super(CertificateList.getInstance((Object)x509CRL.getEncoded()));
    }
}
