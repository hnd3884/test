package org.bouncycastle.jcajce.provider.asymmetric.ec;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.io.IOException;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.jce.spec.ECParameterSpec;
import java.security.spec.ECPrivateKeySpec;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.KeySpec;
import java.security.InvalidKeyException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.Key;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;

public class KeyFactorySpi extends BaseKeyFactorySpi implements AsymmetricKeyInfoConverter
{
    String algorithm;
    ProviderConfiguration configuration;
    
    KeyFactorySpi(final String algorithm, final ProviderConfiguration configuration) {
        this.algorithm = algorithm;
        this.configuration = configuration;
    }
    
    @Override
    protected Key engineTranslateKey(final Key key) throws InvalidKeyException {
        if (key instanceof ECPublicKey) {
            return new BCECPublicKey((ECPublicKey)key, this.configuration);
        }
        if (key instanceof ECPrivateKey) {
            return new BCECPrivateKey((ECPrivateKey)key, this.configuration);
        }
        throw new InvalidKeyException("key type unknown");
    }
    
    @Override
    protected KeySpec engineGetKeySpec(final Key key, final Class clazz) throws InvalidKeySpecException {
        if (clazz.isAssignableFrom(ECPublicKeySpec.class) && key instanceof ECPublicKey) {
            final ECPublicKey ecPublicKey = (ECPublicKey)key;
            if (ecPublicKey.getParams() != null) {
                return new ECPublicKeySpec(ecPublicKey.getW(), ecPublicKey.getParams());
            }
            final ECParameterSpec ecImplicitlyCa = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new ECPublicKeySpec(ecPublicKey.getW(), EC5Util.convertSpec(EC5Util.convertCurve(ecImplicitlyCa.getCurve(), ecImplicitlyCa.getSeed()), ecImplicitlyCa));
        }
        else if (clazz.isAssignableFrom(ECPrivateKeySpec.class) && key instanceof ECPrivateKey) {
            final ECPrivateKey ecPrivateKey = (ECPrivateKey)key;
            if (ecPrivateKey.getParams() != null) {
                return new ECPrivateKeySpec(ecPrivateKey.getS(), ecPrivateKey.getParams());
            }
            final ECParameterSpec ecImplicitlyCa2 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new ECPrivateKeySpec(ecPrivateKey.getS(), EC5Util.convertSpec(EC5Util.convertCurve(ecImplicitlyCa2.getCurve(), ecImplicitlyCa2.getSeed()), ecImplicitlyCa2));
        }
        else if (clazz.isAssignableFrom(org.bouncycastle.jce.spec.ECPublicKeySpec.class) && key instanceof ECPublicKey) {
            final ECPublicKey ecPublicKey2 = (ECPublicKey)key;
            if (ecPublicKey2.getParams() != null) {
                return new org.bouncycastle.jce.spec.ECPublicKeySpec(EC5Util.convertPoint(ecPublicKey2.getParams(), ecPublicKey2.getW(), false), EC5Util.convertSpec(ecPublicKey2.getParams(), false));
            }
            return new org.bouncycastle.jce.spec.ECPublicKeySpec(EC5Util.convertPoint(ecPublicKey2.getParams(), ecPublicKey2.getW(), false), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa());
        }
        else {
            if (!clazz.isAssignableFrom(org.bouncycastle.jce.spec.ECPrivateKeySpec.class) || !(key instanceof ECPrivateKey)) {
                return super.engineGetKeySpec(key, clazz);
            }
            final ECPrivateKey ecPrivateKey2 = (ECPrivateKey)key;
            if (ecPrivateKey2.getParams() != null) {
                return new org.bouncycastle.jce.spec.ECPrivateKeySpec(ecPrivateKey2.getS(), EC5Util.convertSpec(ecPrivateKey2.getParams(), false));
            }
            return new org.bouncycastle.jce.spec.ECPrivateKeySpec(ecPrivateKey2.getS(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa());
        }
    }
    
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof org.bouncycastle.jce.spec.ECPrivateKeySpec) {
            return new BCECPrivateKey(this.algorithm, (org.bouncycastle.jce.spec.ECPrivateKeySpec)keySpec, this.configuration);
        }
        if (keySpec instanceof ECPrivateKeySpec) {
            return new BCECPrivateKey(this.algorithm, (ECPrivateKeySpec)keySpec, this.configuration);
        }
        return super.engineGeneratePrivate(keySpec);
    }
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        try {
            if (keySpec instanceof org.bouncycastle.jce.spec.ECPublicKeySpec) {
                return new BCECPublicKey(this.algorithm, (org.bouncycastle.jce.spec.ECPublicKeySpec)keySpec, this.configuration);
            }
            if (keySpec instanceof ECPublicKeySpec) {
                return new BCECPublicKey(this.algorithm, (ECPublicKeySpec)keySpec, this.configuration);
            }
        }
        catch (final Exception ex) {
            throw new InvalidKeySpecException("invalid KeySpec: " + ex.getMessage(), ex);
        }
        return super.engineGeneratePublic(keySpec);
    }
    
    public PrivateKey generatePrivate(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final ASN1ObjectIdentifier algorithm = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        if (algorithm.equals(X9ObjectIdentifiers.id_ecPublicKey)) {
            return new BCECPrivateKey(this.algorithm, privateKeyInfo, this.configuration);
        }
        throw new IOException("algorithm identifier " + algorithm + " in key not recognised");
    }
    
    public PublicKey generatePublic(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        final ASN1ObjectIdentifier algorithm = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
        if (algorithm.equals(X9ObjectIdentifiers.id_ecPublicKey)) {
            return new BCECPublicKey(this.algorithm, subjectPublicKeyInfo, this.configuration);
        }
        throw new IOException("algorithm identifier " + algorithm + " in key not recognised");
    }
    
    public static class EC extends KeyFactorySpi
    {
        public EC() {
            super("EC", BouncyCastleProvider.CONFIGURATION);
        }
    }
    
    public static class ECDH extends KeyFactorySpi
    {
        public ECDH() {
            super("ECDH", BouncyCastleProvider.CONFIGURATION);
        }
    }
    
    public static class ECDHC extends KeyFactorySpi
    {
        public ECDHC() {
            super("ECDHC", BouncyCastleProvider.CONFIGURATION);
        }
    }
    
    public static class ECDSA extends KeyFactorySpi
    {
        public ECDSA() {
            super("ECDSA", BouncyCastleProvider.CONFIGURATION);
        }
    }
    
    public static class ECGOST3410 extends KeyFactorySpi
    {
        public ECGOST3410() {
            super("ECGOST3410", BouncyCastleProvider.CONFIGURATION);
        }
    }
    
    public static class ECGOST3410_2012 extends KeyFactorySpi
    {
        public ECGOST3410_2012() {
            super("ECGOST3410-2012", BouncyCastleProvider.CONFIGURATION);
        }
    }
    
    public static class ECMQV extends KeyFactorySpi
    {
        public ECMQV() {
            super("ECMQV", BouncyCastleProvider.CONFIGURATION);
        }
    }
}
