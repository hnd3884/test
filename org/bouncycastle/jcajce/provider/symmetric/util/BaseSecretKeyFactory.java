package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.spec.KeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import javax.crypto.SecretKeyFactorySpi;

public class BaseSecretKeyFactory extends SecretKeyFactorySpi implements PBE
{
    protected String algName;
    protected ASN1ObjectIdentifier algOid;
    
    protected BaseSecretKeyFactory(final String algName, final ASN1ObjectIdentifier algOid) {
        this.algName = algName;
        this.algOid = algOid;
    }
    
    @Override
    protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof SecretKeySpec) {
            return new SecretKeySpec(((SecretKeySpec)keySpec).getEncoded(), this.algName);
        }
        throw new InvalidKeySpecException("Invalid KeySpec");
    }
    
    @Override
    protected KeySpec engineGetKeySpec(final SecretKey secretKey, final Class clazz) throws InvalidKeySpecException {
        if (clazz == null) {
            throw new InvalidKeySpecException("keySpec parameter is null");
        }
        if (secretKey == null) {
            throw new InvalidKeySpecException("key parameter is null");
        }
        if (SecretKeySpec.class.isAssignableFrom(clazz)) {
            return new SecretKeySpec(secretKey.getEncoded(), this.algName);
        }
        try {
            return clazz.getConstructor(byte[].class).newInstance(secretKey.getEncoded());
        }
        catch (final Exception ex) {
            throw new InvalidKeySpecException(ex.toString());
        }
    }
    
    @Override
    protected SecretKey engineTranslateKey(final SecretKey secretKey) throws InvalidKeyException {
        if (secretKey == null) {
            throw new InvalidKeyException("key parameter is null");
        }
        if (!secretKey.getAlgorithm().equalsIgnoreCase(this.algName)) {
            throw new InvalidKeyException("Key not of type " + this.algName + ".");
        }
        return new SecretKeySpec(secretKey.getEncoded(), this.algName);
    }
}
