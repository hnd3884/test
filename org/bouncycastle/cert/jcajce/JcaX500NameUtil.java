package org.bouncycastle.cert.jcajce;

import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.asn1.x500.X500Name;
import java.security.cert.X509Certificate;

public class JcaX500NameUtil
{
    public static X500Name getIssuer(final X509Certificate x509Certificate) {
        return X500Name.getInstance((Object)x509Certificate.getIssuerX500Principal().getEncoded());
    }
    
    public static X500Name getSubject(final X509Certificate x509Certificate) {
        return X500Name.getInstance((Object)x509Certificate.getSubjectX500Principal().getEncoded());
    }
    
    public static X500Name getIssuer(final X500NameStyle x500NameStyle, final X509Certificate x509Certificate) {
        return X500Name.getInstance(x500NameStyle, (Object)x509Certificate.getIssuerX500Principal().getEncoded());
    }
    
    public static X500Name getSubject(final X500NameStyle x500NameStyle, final X509Certificate x509Certificate) {
        return X500Name.getInstance(x500NameStyle, (Object)x509Certificate.getSubjectX500Principal().getEncoded());
    }
}
