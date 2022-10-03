package com.sun.crypto.provider;

import java.security.InvalidKeyException;
import javax.crypto.interfaces.PBEKey;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKey;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactorySpi;

abstract class PBKDF2Core extends SecretKeyFactorySpi
{
    private final String prfAlgo;
    
    PBKDF2Core(final String prfAlgo) {
        this.prfAlgo = prfAlgo;
    }
    
    @Override
    protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
        if (!(keySpec instanceof PBEKeySpec)) {
            throw new InvalidKeySpecException("Invalid key spec");
        }
        return new PBKDF2KeyImpl((PBEKeySpec)keySpec, this.prfAlgo);
    }
    
    @Override
    protected KeySpec engineGetKeySpec(final SecretKey secretKey, final Class<?> clazz) throws InvalidKeySpecException {
        if (!(secretKey instanceof PBEKey)) {
            throw new InvalidKeySpecException("Invalid key format/algorithm");
        }
        if (clazz != null && PBEKeySpec.class.isAssignableFrom(clazz)) {
            final PBEKey pbeKey = (PBEKey)secretKey;
            return new PBEKeySpec(pbeKey.getPassword(), pbeKey.getSalt(), pbeKey.getIterationCount(), pbeKey.getEncoded().length * 8);
        }
        throw new InvalidKeySpecException("Invalid key spec");
    }
    
    @Override
    protected SecretKey engineTranslateKey(final SecretKey secretKey) throws InvalidKeyException {
        if (secretKey != null && secretKey.getAlgorithm().equalsIgnoreCase("PBKDF2With" + this.prfAlgo) && secretKey.getFormat().equalsIgnoreCase("RAW")) {
            if (secretKey instanceof PBKDF2KeyImpl) {
                return secretKey;
            }
            if (secretKey instanceof PBEKey) {
                final PBEKey pbeKey = (PBEKey)secretKey;
                try {
                    return new PBKDF2KeyImpl(new PBEKeySpec(pbeKey.getPassword(), pbeKey.getSalt(), pbeKey.getIterationCount(), pbeKey.getEncoded().length * 8), this.prfAlgo);
                }
                catch (final InvalidKeySpecException ex) {
                    final InvalidKeyException ex2 = new InvalidKeyException("Invalid key component(s)");
                    ex2.initCause(ex);
                    throw ex2;
                }
            }
        }
        throw new InvalidKeyException("Invalid key format/algorithm");
    }
    
    public static final class HmacSHA1 extends PBKDF2Core
    {
        public HmacSHA1() {
            super("HmacSHA1");
        }
    }
    
    public static final class HmacSHA224 extends PBKDF2Core
    {
        public HmacSHA224() {
            super("HmacSHA224");
        }
    }
    
    public static final class HmacSHA256 extends PBKDF2Core
    {
        public HmacSHA256() {
            super("HmacSHA256");
        }
    }
    
    public static final class HmacSHA384 extends PBKDF2Core
    {
        public HmacSHA384() {
            super("HmacSHA384");
        }
    }
    
    public static final class HmacSHA512 extends PBKDF2Core
    {
        public HmacSHA512() {
            super("HmacSHA512");
        }
    }
}
