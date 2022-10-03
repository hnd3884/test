package org.bouncycastle.cms.jcajce;

import org.bouncycastle.cms.KeyTransRecipientId;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cms.SignerId;
import java.security.cert.X509CertSelector;

public class JcaSelectorConverter
{
    public SignerId getSignerId(final X509CertSelector x509CertSelector) {
        try {
            if (x509CertSelector.getSubjectKeyIdentifier() != null) {
                return new SignerId(X500Name.getInstance((Object)x509CertSelector.getIssuerAsBytes()), x509CertSelector.getSerialNumber(), ASN1OctetString.getInstance((Object)x509CertSelector.getSubjectKeyIdentifier()).getOctets());
            }
            return new SignerId(X500Name.getInstance((Object)x509CertSelector.getIssuerAsBytes()), x509CertSelector.getSerialNumber());
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("unable to convert issuer: " + ex.getMessage());
        }
    }
    
    public KeyTransRecipientId getKeyTransRecipientId(final X509CertSelector x509CertSelector) {
        try {
            if (x509CertSelector.getSubjectKeyIdentifier() != null) {
                return new KeyTransRecipientId(X500Name.getInstance((Object)x509CertSelector.getIssuerAsBytes()), x509CertSelector.getSerialNumber(), ASN1OctetString.getInstance((Object)x509CertSelector.getSubjectKeyIdentifier()).getOctets());
            }
            return new KeyTransRecipientId(X500Name.getInstance((Object)x509CertSelector.getIssuerAsBytes()), x509CertSelector.getSerialNumber());
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("unable to convert issuer: " + ex.getMessage());
        }
    }
}
