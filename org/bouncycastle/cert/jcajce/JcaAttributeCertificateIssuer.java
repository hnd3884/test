package org.bouncycastle.cert.jcajce;

import org.bouncycastle.asn1.x500.X500Name;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.AttributeCertificateIssuer;

public class JcaAttributeCertificateIssuer extends AttributeCertificateIssuer
{
    public JcaAttributeCertificateIssuer(final X509Certificate x509Certificate) {
        this(x509Certificate.getIssuerX500Principal());
    }
    
    public JcaAttributeCertificateIssuer(final X500Principal x500Principal) {
        super(X500Name.getInstance((Object)x500Principal.getEncoded()));
    }
}
