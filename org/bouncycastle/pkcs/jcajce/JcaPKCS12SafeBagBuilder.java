package org.bouncycastle.pkcs.jcajce;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.operator.OutputEncryptor;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.pkcs.PKCSIOException;
import org.bouncycastle.asn1.x509.Certificate;
import java.io.IOException;
import java.security.cert.X509Certificate;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;

public class JcaPKCS12SafeBagBuilder extends PKCS12SafeBagBuilder
{
    public JcaPKCS12SafeBagBuilder(final X509Certificate x509Certificate) throws IOException {
        super(convertCert(x509Certificate));
    }
    
    private static Certificate convertCert(final X509Certificate x509Certificate) throws IOException {
        try {
            return Certificate.getInstance((Object)x509Certificate.getEncoded());
        }
        catch (final CertificateEncodingException ex) {
            throw new PKCSIOException("cannot encode certificate: " + ex.getMessage(), ex);
        }
    }
    
    public JcaPKCS12SafeBagBuilder(final PrivateKey privateKey, final OutputEncryptor outputEncryptor) {
        super(PrivateKeyInfo.getInstance((Object)privateKey.getEncoded()), outputEncryptor);
    }
    
    public JcaPKCS12SafeBagBuilder(final PrivateKey privateKey) {
        super(PrivateKeyInfo.getInstance((Object)privateKey.getEncoded()));
    }
}
