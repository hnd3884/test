package org.bouncycastle.openssl.jcajce;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.PublicKey;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.security.Key;
import java.security.PrivateKey;
import java.security.KeyPair;
import java.security.cert.CRLException;
import org.bouncycastle.cert.jcajce.JcaX509CRLHolder;
import java.security.cert.X509CRL;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import java.security.cert.X509Certificate;
import org.bouncycastle.openssl.PEMEncryptor;
import java.io.IOException;
import java.security.Provider;
import java.security.SecureRandom;
import org.bouncycastle.openssl.MiscPEMGenerator;

public class JcaMiscPEMGenerator extends MiscPEMGenerator
{
    private Object obj;
    private String algorithm;
    private char[] password;
    private SecureRandom random;
    private Provider provider;
    
    public JcaMiscPEMGenerator(final Object o) throws IOException {
        super(convertObject(o));
    }
    
    public JcaMiscPEMGenerator(final Object o, final PEMEncryptor pemEncryptor) throws IOException {
        super(convertObject(o), pemEncryptor);
    }
    
    private static Object convertObject(final Object o) throws IOException {
        if (o instanceof X509Certificate) {
            try {
                return new JcaX509CertificateHolder((X509Certificate)o);
            }
            catch (final CertificateEncodingException ex) {
                throw new IllegalArgumentException("Cannot encode object: " + ex.toString());
            }
        }
        if (o instanceof X509CRL) {
            try {
                return new JcaX509CRLHolder((X509CRL)o);
            }
            catch (final CRLException ex2) {
                throw new IllegalArgumentException("Cannot encode object: " + ex2.toString());
            }
        }
        if (o instanceof KeyPair) {
            return convertObject(((KeyPair)o).getPrivate());
        }
        if (o instanceof PrivateKey) {
            return PrivateKeyInfo.getInstance((Object)((Key)o).getEncoded());
        }
        if (o instanceof PublicKey) {
            return SubjectPublicKeyInfo.getInstance((Object)((PublicKey)o).getEncoded());
        }
        return o;
    }
}
