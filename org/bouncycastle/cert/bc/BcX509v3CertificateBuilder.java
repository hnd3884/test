package org.bouncycastle.cert.bc;

import org.bouncycastle.cert.X509CertificateHolder;
import java.io.IOException;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.util.Date;
import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;

public class BcX509v3CertificateBuilder extends X509v3CertificateBuilder
{
    public BcX509v3CertificateBuilder(final X500Name x500Name, final BigInteger bigInteger, final Date date, final Date date2, final X500Name x500Name2, final AsymmetricKeyParameter asymmetricKeyParameter) throws IOException {
        super(x500Name, bigInteger, date, date2, x500Name2, SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(asymmetricKeyParameter));
    }
    
    public BcX509v3CertificateBuilder(final X509CertificateHolder x509CertificateHolder, final BigInteger bigInteger, final Date date, final Date date2, final X500Name x500Name, final AsymmetricKeyParameter asymmetricKeyParameter) throws IOException {
        super(x509CertificateHolder.getSubject(), bigInteger, date, date2, x500Name, SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(asymmetricKeyParameter));
    }
}
