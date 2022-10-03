package org.bouncycastle.cert.jcajce;

import java.security.cert.CertificateEncodingException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.PublicKey;
import java.util.Date;
import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;

public class JcaX509v3CertificateBuilder extends X509v3CertificateBuilder
{
    public JcaX509v3CertificateBuilder(final X500Name x500Name, final BigInteger bigInteger, final Date date, final Date date2, final X500Name x500Name2, final PublicKey publicKey) {
        super(x500Name, bigInteger, date, date2, x500Name2, SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }
    
    public JcaX509v3CertificateBuilder(final X500Name x500Name, final BigInteger bigInteger, final Time time, final Time time2, final X500Name x500Name2, final PublicKey publicKey) {
        super(x500Name, bigInteger, time, time2, x500Name2, SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }
    
    public JcaX509v3CertificateBuilder(final X500Principal x500Principal, final BigInteger bigInteger, final Date date, final Date date2, final X500Principal x500Principal2, final PublicKey publicKey) {
        super(X500Name.getInstance((Object)x500Principal.getEncoded()), bigInteger, date, date2, X500Name.getInstance((Object)x500Principal2.getEncoded()), SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }
    
    public JcaX509v3CertificateBuilder(final X509Certificate x509Certificate, final BigInteger bigInteger, final Date date, final Date date2, final X500Principal x500Principal, final PublicKey publicKey) {
        this(x509Certificate.getSubjectX500Principal(), bigInteger, date, date2, x500Principal, publicKey);
    }
    
    public JcaX509v3CertificateBuilder(final X509Certificate x509Certificate, final BigInteger bigInteger, final Date date, final Date date2, final X500Name x500Name, final PublicKey publicKey) {
        this(X500Name.getInstance((Object)x509Certificate.getSubjectX500Principal().getEncoded()), bigInteger, date, date2, x500Name, publicKey);
    }
    
    public JcaX509v3CertificateBuilder copyAndAddExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final X509Certificate x509Certificate) throws CertificateEncodingException {
        this.copyAndAddExtension(asn1ObjectIdentifier, b, new JcaX509CertificateHolder(x509Certificate));
        return this;
    }
}
