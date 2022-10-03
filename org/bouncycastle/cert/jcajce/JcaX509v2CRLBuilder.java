package org.bouncycastle.cert.jcajce;

import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x500.X500Name;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.cert.X509v2CRLBuilder;

public class JcaX509v2CRLBuilder extends X509v2CRLBuilder
{
    public JcaX509v2CRLBuilder(final X500Principal x500Principal, final Date date) {
        super(X500Name.getInstance((Object)x500Principal.getEncoded()), date);
    }
    
    public JcaX509v2CRLBuilder(final X509Certificate x509Certificate, final Date date) {
        this(x509Certificate.getSubjectX500Principal(), date);
    }
}
