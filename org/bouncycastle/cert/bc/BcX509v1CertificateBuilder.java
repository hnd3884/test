package org.bouncycastle.cert.bc;

import java.io.IOException;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.util.Date;
import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v1CertificateBuilder;

public class BcX509v1CertificateBuilder extends X509v1CertificateBuilder
{
    public BcX509v1CertificateBuilder(final X500Name x500Name, final BigInteger bigInteger, final Date date, final Date date2, final X500Name x500Name2, final AsymmetricKeyParameter asymmetricKeyParameter) throws IOException {
        super(x500Name, bigInteger, date, date2, x500Name2, SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(asymmetricKeyParameter));
    }
}
