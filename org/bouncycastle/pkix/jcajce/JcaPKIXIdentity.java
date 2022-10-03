package org.bouncycastle.pkix.jcajce;

import java.security.cert.CertificateEncodingException;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.security.cert.X509Certificate;
import java.security.PrivateKey;
import org.bouncycastle.pkix.PKIXIdentity;

public class JcaPKIXIdentity extends PKIXIdentity
{
    private final PrivateKey privKey;
    private final X509Certificate[] certs;
    
    private static PrivateKeyInfo getPrivateKeyInfo(final PrivateKey privateKey) {
        try {
            return PrivateKeyInfo.getInstance((Object)privateKey.getEncoded());
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    private static X509CertificateHolder[] getCertificates(final X509Certificate[] array) {
        final X509CertificateHolder[] array2 = new X509CertificateHolder[array.length];
        try {
            for (int i = 0; i != array2.length; ++i) {
                array2[i] = new JcaX509CertificateHolder(array[i]);
            }
            return array2;
        }
        catch (final CertificateEncodingException ex) {
            throw new IllegalArgumentException("Unable to process certificates: " + ex.getMessage());
        }
    }
    
    public JcaPKIXIdentity(final PrivateKey privKey, final X509Certificate[] array) {
        super(getPrivateKeyInfo(privKey), getCertificates(array));
        this.privKey = privKey;
        System.arraycopy(array, 0, this.certs = new X509Certificate[array.length], 0, array.length);
    }
    
    public PrivateKey getPrivateKey() {
        return this.privKey;
    }
    
    public X509Certificate getX509Certificate() {
        return this.certs[0];
    }
}
