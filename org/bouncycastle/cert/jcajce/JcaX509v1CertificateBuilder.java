package org.bouncycastle.cert.jcajce;

import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.PublicKey;
import java.util.Date;
import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v1CertificateBuilder;

public class JcaX509v1CertificateBuilder extends X509v1CertificateBuilder
{
    public JcaX509v1CertificateBuilder(final X500Name x500Name, final BigInteger bigInteger, final Date date, final Date date2, final X500Name x500Name2, final PublicKey publicKey) {
        super(x500Name, bigInteger, date, date2, x500Name2, SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }
    
    public JcaX509v1CertificateBuilder(final X500Principal x500Principal, final BigInteger bigInteger, final Date date, final Date date2, final X500Principal x500Principal2, final PublicKey publicKey) {
        super(X500Name.getInstance((Object)x500Principal.getEncoded()), bigInteger, date, date2, X500Name.getInstance((Object)x500Principal2.getEncoded()), SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }
}
