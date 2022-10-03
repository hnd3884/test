package org.bouncycastle.pqc.jcajce.provider.xmss;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.spec.X509EncodedKeySpec;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.ASN1Primitive;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.PrivateKey;
import java.security.spec.KeySpec;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import java.security.KeyFactorySpi;

public class XMSSMTKeyFactorySpi extends KeyFactorySpi implements AsymmetricKeyInfoConverter
{
    public PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            final byte[] encoded = ((PKCS8EncodedKeySpec)keySpec).getEncoded();
            try {
                return this.generatePrivate(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(encoded)));
            }
            catch (final Exception ex) {
                throw new InvalidKeySpecException(ex.toString());
            }
        }
        throw new InvalidKeySpecException("unsupported key specification: " + keySpec.getClass() + ".");
    }
    
    public PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof X509EncodedKeySpec) {
            final byte[] encoded = ((X509EncodedKeySpec)keySpec).getEncoded();
            try {
                return this.generatePublic(SubjectPublicKeyInfo.getInstance(encoded));
            }
            catch (final Exception ex) {
                throw new InvalidKeySpecException(ex.toString());
            }
        }
        throw new InvalidKeySpecException("unknown key specification: " + keySpec + ".");
    }
    
    public final KeySpec engineGetKeySpec(final Key key, final Class clazz) throws InvalidKeySpecException {
        if (key instanceof BCXMSSMTPrivateKey) {
            if (PKCS8EncodedKeySpec.class.isAssignableFrom(clazz)) {
                return new PKCS8EncodedKeySpec(key.getEncoded());
            }
        }
        else {
            if (!(key instanceof BCXMSSMTPublicKey)) {
                throw new InvalidKeySpecException("unsupported key type: " + key.getClass() + ".");
            }
            if (X509EncodedKeySpec.class.isAssignableFrom(clazz)) {
                return new X509EncodedKeySpec(key.getEncoded());
            }
        }
        throw new InvalidKeySpecException("unknown key specification: " + clazz + ".");
    }
    
    public final Key engineTranslateKey(final Key key) throws InvalidKeyException {
        if (key instanceof BCXMSSMTPrivateKey || key instanceof BCXMSSMTPublicKey) {
            return key;
        }
        throw new InvalidKeyException("unsupported key type");
    }
    
    public PrivateKey generatePrivate(final PrivateKeyInfo privateKeyInfo) throws IOException {
        return new BCXMSSMTPrivateKey(privateKeyInfo);
    }
    
    public PublicKey generatePublic(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        return new BCXMSSMTPublicKey(subjectPublicKeyInfo);
    }
}
