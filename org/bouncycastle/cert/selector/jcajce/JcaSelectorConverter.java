package org.bouncycastle.cert.selector.jcajce;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;
import java.security.cert.X509CertSelector;

public class JcaSelectorConverter
{
    public X509CertificateHolderSelector getCertificateHolderSelector(final X509CertSelector x509CertSelector) {
        try {
            if (x509CertSelector.getSubjectKeyIdentifier() != null) {
                return new X509CertificateHolderSelector(X500Name.getInstance((Object)x509CertSelector.getIssuerAsBytes()), x509CertSelector.getSerialNumber(), ASN1OctetString.getInstance((Object)x509CertSelector.getSubjectKeyIdentifier()).getOctets());
            }
            return new X509CertificateHolderSelector(X500Name.getInstance((Object)x509CertSelector.getIssuerAsBytes()), x509CertSelector.getSerialNumber());
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("unable to convert issuer: " + ex.getMessage());
        }
    }
}
