package org.bouncycastle.cert.selector.jcajce;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x500.X500Name;
import java.math.BigInteger;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

public class JcaX509CertificateHolderSelector extends X509CertificateHolderSelector
{
    public JcaX509CertificateHolderSelector(final X509Certificate x509Certificate) {
        super(convertPrincipal(x509Certificate.getIssuerX500Principal()), x509Certificate.getSerialNumber(), getSubjectKeyId(x509Certificate));
    }
    
    public JcaX509CertificateHolderSelector(final X500Principal x500Principal, final BigInteger bigInteger) {
        super(convertPrincipal(x500Principal), bigInteger);
    }
    
    public JcaX509CertificateHolderSelector(final X500Principal x500Principal, final BigInteger bigInteger, final byte[] array) {
        super(convertPrincipal(x500Principal), bigInteger, array);
    }
    
    private static X500Name convertPrincipal(final X500Principal x500Principal) {
        if (x500Principal == null) {
            return null;
        }
        return X500Name.getInstance((Object)x500Principal.getEncoded());
    }
    
    private static byte[] getSubjectKeyId(final X509Certificate x509Certificate) {
        final byte[] extensionValue = x509Certificate.getExtensionValue(Extension.subjectKeyIdentifier.getId());
        if (extensionValue != null) {
            return ASN1OctetString.getInstance((Object)ASN1OctetString.getInstance((Object)extensionValue).getOctets()).getOctets();
        }
        return null;
    }
}
