package org.bouncycastle.openssl.jcajce;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import java.util.HashMap;
import java.security.NoSuchProviderException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.PrivateKey;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.security.PublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.KeyFactory;
import org.bouncycastle.openssl.PEMException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import java.util.Map;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public class JcaPEMKeyConverter
{
    private JcaJceHelper helper;
    private static final Map algorithms;
    
    public JcaPEMKeyConverter() {
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
    }
    
    public JcaPEMKeyConverter setProvider(final Provider provider) {
        this.helper = (JcaJceHelper)new ProviderJcaJceHelper(provider);
        return this;
    }
    
    public JcaPEMKeyConverter setProvider(final String s) {
        this.helper = (JcaJceHelper)new NamedJcaJceHelper(s);
        return this;
    }
    
    public KeyPair getKeyPair(final PEMKeyPair pemKeyPair) throws PEMException {
        try {
            final KeyFactory keyFactory = this.getKeyFactory(pemKeyPair.getPrivateKeyInfo().getPrivateKeyAlgorithm());
            return new KeyPair(keyFactory.generatePublic(new X509EncodedKeySpec(pemKeyPair.getPublicKeyInfo().getEncoded())), keyFactory.generatePrivate(new PKCS8EncodedKeySpec(pemKeyPair.getPrivateKeyInfo().getEncoded())));
        }
        catch (final Exception ex) {
            throw new PEMException("unable to convert key pair: " + ex.getMessage(), ex);
        }
    }
    
    public PublicKey getPublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws PEMException {
        try {
            return this.getKeyFactory(subjectPublicKeyInfo.getAlgorithm()).generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
        }
        catch (final Exception ex) {
            throw new PEMException("unable to convert key pair: " + ex.getMessage(), ex);
        }
    }
    
    public PrivateKey getPrivateKey(final PrivateKeyInfo privateKeyInfo) throws PEMException {
        try {
            return this.getKeyFactory(privateKeyInfo.getPrivateKeyAlgorithm()).generatePrivate(new PKCS8EncodedKeySpec(privateKeyInfo.getEncoded()));
        }
        catch (final Exception ex) {
            throw new PEMException("unable to convert key pair: " + ex.getMessage(), ex);
        }
    }
    
    private KeyFactory getKeyFactory(final AlgorithmIdentifier algorithmIdentifier) throws NoSuchAlgorithmException, NoSuchProviderException {
        final ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
        String id = JcaPEMKeyConverter.algorithms.get(algorithm);
        if (id == null) {
            id = algorithm.getId();
        }
        try {
            return this.helper.createKeyFactory(id);
        }
        catch (final NoSuchAlgorithmException ex) {
            if (id.equals("ECDSA")) {
                return this.helper.createKeyFactory("EC");
            }
            throw ex;
        }
    }
    
    static {
        (algorithms = new HashMap()).put(X9ObjectIdentifiers.id_ecPublicKey, "ECDSA");
        JcaPEMKeyConverter.algorithms.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        JcaPEMKeyConverter.algorithms.put(X9ObjectIdentifiers.id_dsa, "DSA");
    }
}
