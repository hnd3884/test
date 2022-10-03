package org.bouncycastle.pkcs.jcajce;

import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.security.KeyFactory;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;
import java.security.spec.KeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.X509EncodedKeySpec;
import java.security.PublicKey;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import java.io.IOException;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import java.util.Hashtable;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

public class JcaPKCS10CertificationRequest extends PKCS10CertificationRequest
{
    private static Hashtable keyAlgorithms;
    private JcaJceHelper helper;
    
    public JcaPKCS10CertificationRequest(final CertificationRequest certificationRequest) {
        super(certificationRequest);
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
    }
    
    public JcaPKCS10CertificationRequest(final byte[] array) throws IOException {
        super(array);
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
    }
    
    public JcaPKCS10CertificationRequest(final PKCS10CertificationRequest pkcs10CertificationRequest) {
        super(pkcs10CertificationRequest.toASN1Structure());
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
    }
    
    public JcaPKCS10CertificationRequest setProvider(final String s) {
        this.helper = (JcaJceHelper)new NamedJcaJceHelper(s);
        return this;
    }
    
    public JcaPKCS10CertificationRequest setProvider(final Provider provider) {
        this.helper = (JcaJceHelper)new ProviderJcaJceHelper(provider);
        return this;
    }
    
    public PublicKey getPublicKey() throws InvalidKeyException, NoSuchAlgorithmException {
        try {
            final SubjectPublicKeyInfo subjectPublicKeyInfo = this.getSubjectPublicKeyInfo();
            final X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded());
            KeyFactory keyFactory;
            try {
                keyFactory = this.helper.createKeyFactory(subjectPublicKeyInfo.getAlgorithm().getAlgorithm().getId());
            }
            catch (final NoSuchAlgorithmException ex) {
                if (JcaPKCS10CertificationRequest.keyAlgorithms.get(subjectPublicKeyInfo.getAlgorithm().getAlgorithm()) == null) {
                    throw ex;
                }
                keyFactory = this.helper.createKeyFactory((String)JcaPKCS10CertificationRequest.keyAlgorithms.get(subjectPublicKeyInfo.getAlgorithm().getAlgorithm()));
            }
            return keyFactory.generatePublic(x509EncodedKeySpec);
        }
        catch (final InvalidKeySpecException ex2) {
            throw new InvalidKeyException("error decoding public key");
        }
        catch (final IOException ex3) {
            throw new InvalidKeyException("error extracting key encoding");
        }
        catch (final NoSuchProviderException ex4) {
            throw new NoSuchAlgorithmException("cannot find provider: " + ex4.getMessage());
        }
    }
    
    static {
        (JcaPKCS10CertificationRequest.keyAlgorithms = new Hashtable()).put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        JcaPKCS10CertificationRequest.keyAlgorithms.put(X9ObjectIdentifiers.id_dsa, "DSA");
    }
}
