package org.bouncycastle.pqc.jcajce.provider.rainbow;

import org.bouncycastle.pqc.asn1.RainbowPublicKey;
import java.io.IOException;
import org.bouncycastle.pqc.asn1.RainbowPrivateKey;
import java.security.InvalidKeyException;
import java.security.Key;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.pqc.jcajce.spec.RainbowPublicKeySpec;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.ASN1Primitive;
import java.security.spec.PKCS8EncodedKeySpec;
import org.bouncycastle.pqc.jcajce.spec.RainbowPrivateKeySpec;
import java.security.PrivateKey;
import java.security.spec.KeySpec;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import java.security.KeyFactorySpi;

public class RainbowKeyFactorySpi extends KeyFactorySpi implements AsymmetricKeyInfoConverter
{
    public PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof RainbowPrivateKeySpec) {
            return new BCRainbowPrivateKey((RainbowPrivateKeySpec)keySpec);
        }
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            final byte[] encoded = ((PKCS8EncodedKeySpec)keySpec).getEncoded();
            try {
                return this.generatePrivate(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(encoded)));
            }
            catch (final Exception ex) {
                throw new InvalidKeySpecException(ex.toString());
            }
        }
        throw new InvalidKeySpecException("Unsupported key specification: " + keySpec.getClass() + ".");
    }
    
    public PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof RainbowPublicKeySpec) {
            return new BCRainbowPublicKey((RainbowPublicKeySpec)keySpec);
        }
        if (keySpec instanceof X509EncodedKeySpec) {
            final byte[] encoded = ((X509EncodedKeySpec)keySpec).getEncoded();
            try {
                return this.generatePublic(SubjectPublicKeyInfo.getInstance(encoded));
            }
            catch (final Exception ex) {
                throw new InvalidKeySpecException(ex.toString());
            }
        }
        throw new InvalidKeySpecException("Unknown key specification: " + keySpec + ".");
    }
    
    public final KeySpec engineGetKeySpec(final Key key, final Class clazz) throws InvalidKeySpecException {
        if (key instanceof BCRainbowPrivateKey) {
            if (PKCS8EncodedKeySpec.class.isAssignableFrom(clazz)) {
                return new PKCS8EncodedKeySpec(key.getEncoded());
            }
            if (RainbowPrivateKeySpec.class.isAssignableFrom(clazz)) {
                final BCRainbowPrivateKey bcRainbowPrivateKey = (BCRainbowPrivateKey)key;
                return new RainbowPrivateKeySpec(bcRainbowPrivateKey.getInvA1(), bcRainbowPrivateKey.getB1(), bcRainbowPrivateKey.getInvA2(), bcRainbowPrivateKey.getB2(), bcRainbowPrivateKey.getVi(), bcRainbowPrivateKey.getLayers());
            }
        }
        else {
            if (!(key instanceof BCRainbowPublicKey)) {
                throw new InvalidKeySpecException("Unsupported key type: " + key.getClass() + ".");
            }
            if (X509EncodedKeySpec.class.isAssignableFrom(clazz)) {
                return new X509EncodedKeySpec(key.getEncoded());
            }
            if (RainbowPublicKeySpec.class.isAssignableFrom(clazz)) {
                final BCRainbowPublicKey bcRainbowPublicKey = (BCRainbowPublicKey)key;
                return new RainbowPublicKeySpec(bcRainbowPublicKey.getDocLength(), bcRainbowPublicKey.getCoeffQuadratic(), bcRainbowPublicKey.getCoeffSingular(), bcRainbowPublicKey.getCoeffScalar());
            }
        }
        throw new InvalidKeySpecException("Unknown key specification: " + clazz + ".");
    }
    
    public final Key engineTranslateKey(final Key key) throws InvalidKeyException {
        if (key instanceof BCRainbowPrivateKey || key instanceof BCRainbowPublicKey) {
            return key;
        }
        throw new InvalidKeyException("Unsupported key type");
    }
    
    public PrivateKey generatePrivate(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final RainbowPrivateKey instance = RainbowPrivateKey.getInstance(privateKeyInfo.parsePrivateKey());
        return new BCRainbowPrivateKey(instance.getInvA1(), instance.getB1(), instance.getInvA2(), instance.getB2(), instance.getVi(), instance.getLayers());
    }
    
    public PublicKey generatePublic(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        final RainbowPublicKey instance = RainbowPublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey());
        return new BCRainbowPublicKey(instance.getDocLength(), instance.getCoeffQuadratic(), instance.getCoeffSingular(), instance.getCoeffScalar());
    }
}
