package org.bouncycastle.cert.selector.jcajce;

import org.bouncycastle.cert.selector.X509CertificateHolderSelector;
import org.bouncycastle.asn1.DEROctetString;
import java.io.IOException;
import java.security.cert.X509CertSelector;
import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;

public class JcaX509CertSelectorConverter
{
    protected X509CertSelector doConversion(final X500Name x500Name, final BigInteger serialNumber, final byte[] array) {
        final X509CertSelector x509CertSelector = new X509CertSelector();
        if (x500Name != null) {
            try {
                x509CertSelector.setIssuer(x500Name.getEncoded());
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("unable to convert issuer: " + ex.getMessage());
            }
        }
        if (serialNumber != null) {
            x509CertSelector.setSerialNumber(serialNumber);
        }
        if (array != null) {
            try {
                x509CertSelector.setSubjectKeyIdentifier(new DEROctetString(array).getEncoded());
            }
            catch (final IOException ex2) {
                throw new IllegalArgumentException("unable to convert issuer: " + ex2.getMessage());
            }
        }
        return x509CertSelector;
    }
    
    public X509CertSelector getCertSelector(final X509CertificateHolderSelector x509CertificateHolderSelector) {
        return this.doConversion(x509CertificateHolderSelector.getIssuer(), x509CertificateHolderSelector.getSerialNumber(), x509CertificateHolderSelector.getSubjectKeyIdentifier());
    }
}
