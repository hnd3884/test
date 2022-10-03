package org.bouncycastle.pqc.jcajce.provider.mceliece;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.InvalidKeyException;
import java.security.Key;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import org.bouncycastle.pqc.asn1.McEliecePrivateKey;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.PrivateKey;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePublicKeyParameters;
import org.bouncycastle.pqc.asn1.McEliecePublicKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1Primitive;
import java.security.spec.X509EncodedKeySpec;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import java.security.KeyFactorySpi;

public class McElieceKeyFactorySpi extends KeyFactorySpi implements AsymmetricKeyInfoConverter
{
    public static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.1";
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof X509EncodedKeySpec) {
            final byte[] encoded = ((X509EncodedKeySpec)keySpec).getEncoded();
            SubjectPublicKeyInfo instance;
            try {
                instance = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(encoded));
            }
            catch (final IOException ex) {
                throw new InvalidKeySpecException(ex.toString());
            }
            try {
                if (PQCObjectIdentifiers.mcEliece.equals(instance.getAlgorithm().getAlgorithm())) {
                    final McEliecePublicKey instance2 = McEliecePublicKey.getInstance(instance.parsePublicKey());
                    return new BCMcEliecePublicKey(new McEliecePublicKeyParameters(instance2.getN(), instance2.getT(), instance2.getG()));
                }
                throw new InvalidKeySpecException("Unable to recognise OID in McEliece public key");
            }
            catch (final IOException ex2) {
                throw new InvalidKeySpecException("Unable to decode X509EncodedKeySpec: " + ex2.getMessage());
            }
        }
        throw new InvalidKeySpecException("Unsupported key specification: " + keySpec.getClass() + ".");
    }
    
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            final byte[] encoded = ((PKCS8EncodedKeySpec)keySpec).getEncoded();
            PrivateKeyInfo instance;
            try {
                instance = PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(encoded));
            }
            catch (final IOException ex) {
                throw new InvalidKeySpecException("Unable to decode PKCS8EncodedKeySpec: " + ex);
            }
            try {
                if (PQCObjectIdentifiers.mcEliece.equals(instance.getPrivateKeyAlgorithm().getAlgorithm())) {
                    final McEliecePrivateKey instance2 = McEliecePrivateKey.getInstance(instance.parsePrivateKey());
                    return new BCMcEliecePrivateKey(new McEliecePrivateKeyParameters(instance2.getN(), instance2.getK(), instance2.getField(), instance2.getGoppaPoly(), instance2.getP1(), instance2.getP2(), instance2.getSInv()));
                }
                throw new InvalidKeySpecException("Unable to recognise OID in McEliece private key");
            }
            catch (final IOException ex2) {
                throw new InvalidKeySpecException("Unable to decode PKCS8EncodedKeySpec.");
            }
        }
        throw new InvalidKeySpecException("Unsupported key specification: " + keySpec.getClass() + ".");
    }
    
    public KeySpec getKeySpec(final Key key, final Class clazz) throws InvalidKeySpecException {
        if (key instanceof BCMcEliecePrivateKey) {
            if (PKCS8EncodedKeySpec.class.isAssignableFrom(clazz)) {
                return new PKCS8EncodedKeySpec(key.getEncoded());
            }
        }
        else {
            if (!(key instanceof BCMcEliecePublicKey)) {
                throw new InvalidKeySpecException("Unsupported key type: " + key.getClass() + ".");
            }
            if (X509EncodedKeySpec.class.isAssignableFrom(clazz)) {
                return new X509EncodedKeySpec(key.getEncoded());
            }
        }
        throw new InvalidKeySpecException("Unknown key specification: " + clazz + ".");
    }
    
    public Key translateKey(final Key key) throws InvalidKeyException {
        if (key instanceof BCMcEliecePrivateKey || key instanceof BCMcEliecePublicKey) {
            return key;
        }
        throw new InvalidKeyException("Unsupported key type.");
    }
    
    public PublicKey generatePublic(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        final McEliecePublicKey instance = McEliecePublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey());
        return new BCMcEliecePublicKey(new McEliecePublicKeyParameters(instance.getN(), instance.getT(), instance.getG()));
    }
    
    public PrivateKey generatePrivate(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final McEliecePrivateKey instance = McEliecePrivateKey.getInstance(privateKeyInfo.parsePrivateKey().toASN1Primitive());
        return new BCMcEliecePrivateKey(new McEliecePrivateKeyParameters(instance.getN(), instance.getK(), instance.getField(), instance.getGoppaPoly(), instance.getP1(), instance.getP2(), instance.getSInv()));
    }
    
    @Override
    protected KeySpec engineGetKeySpec(final Key key, final Class clazz) throws InvalidKeySpecException {
        return null;
    }
    
    @Override
    protected Key engineTranslateKey(final Key key) throws InvalidKeyException {
        return null;
    }
    
    private static Digest getDigest(final AlgorithmIdentifier algorithmIdentifier) {
        return new SHA256Digest();
    }
}
