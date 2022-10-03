package com.sun.crypto.provider;

import java.security.AccessController;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;
import java.security.ProviderException;
import javax.crypto.ShortBufferException;
import sun.security.util.KeyUtil;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.spec.DHParameterSpec;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import java.security.Key;
import java.math.BigInteger;
import javax.crypto.KeyAgreementSpi;

public final class DHKeyAgreement extends KeyAgreementSpi
{
    private boolean generateSecret;
    private BigInteger init_p;
    private BigInteger init_g;
    private BigInteger x;
    private BigInteger y;
    
    public DHKeyAgreement() {
        this.generateSecret = false;
        this.init_p = null;
        this.init_g = null;
        this.x = BigInteger.ZERO;
        this.y = BigInteger.ZERO;
    }
    
    @Override
    protected void engineInit(final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        try {
            this.engineInit(key, null, secureRandom);
        }
        catch (final InvalidAlgorithmParameterException ex) {}
    }
    
    @Override
    protected void engineInit(final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.generateSecret = false;
        this.init_p = null;
        this.init_g = null;
        if (algorithmParameterSpec != null && !(algorithmParameterSpec instanceof DHParameterSpec)) {
            throw new InvalidAlgorithmParameterException("Diffie-Hellman parameters expected");
        }
        if (!(key instanceof DHPrivateKey)) {
            throw new InvalidKeyException("Diffie-Hellman private key expected");
        }
        final DHPrivateKey dhPrivateKey = (DHPrivateKey)key;
        if (algorithmParameterSpec != null) {
            this.init_p = ((DHParameterSpec)algorithmParameterSpec).getP();
            this.init_g = ((DHParameterSpec)algorithmParameterSpec).getG();
        }
        final BigInteger p3 = dhPrivateKey.getParams().getP();
        final BigInteger g = dhPrivateKey.getParams().getG();
        if (this.init_p != null && p3 != null && !this.init_p.equals(p3)) {
            throw new InvalidKeyException("Incompatible parameters");
        }
        if (this.init_g != null && g != null && !this.init_g.equals(g)) {
            throw new InvalidKeyException("Incompatible parameters");
        }
        if ((this.init_p == null && p3 == null) || (this.init_g == null && g == null)) {
            throw new InvalidKeyException("Missing parameters");
        }
        this.init_p = p3;
        this.init_g = g;
        this.x = dhPrivateKey.getX();
    }
    
    @Override
    protected Key engineDoPhase(final Key key, final boolean b) throws InvalidKeyException, IllegalStateException {
        if (!(key instanceof DHPublicKey)) {
            throw new InvalidKeyException("Diffie-Hellman public key expected");
        }
        final DHPublicKey dhPublicKey = (DHPublicKey)key;
        if (this.init_p == null || this.init_g == null) {
            throw new IllegalStateException("Not initialized");
        }
        final BigInteger p2 = dhPublicKey.getParams().getP();
        final BigInteger g = dhPublicKey.getParams().getG();
        if (p2 != null && !this.init_p.equals(p2)) {
            throw new InvalidKeyException("Incompatible parameters");
        }
        if (g != null && !this.init_g.equals(g)) {
            throw new InvalidKeyException("Incompatible parameters");
        }
        KeyUtil.validate(dhPublicKey);
        this.y = dhPublicKey.getY();
        this.generateSecret = true;
        if (!b) {
            return new com.sun.crypto.provider.DHPublicKey(new BigInteger(1, this.engineGenerateSecret()), this.init_p, this.init_g);
        }
        return null;
    }
    
    @Override
    protected byte[] engineGenerateSecret() throws IllegalStateException {
        final byte[] array = new byte[this.init_p.bitLength() + 7 >>> 3];
        try {
            this.engineGenerateSecret(array, 0);
        }
        catch (final ShortBufferException ex) {}
        return array;
    }
    
    @Override
    protected int engineGenerateSecret(final byte[] array, final int n) throws IllegalStateException, ShortBufferException {
        if (!this.generateSecret) {
            throw new IllegalStateException("Key agreement has not been completed yet");
        }
        if (array == null) {
            throw new ShortBufferException("No buffer provided for shared secret");
        }
        final BigInteger init_p = this.init_p;
        final int n2 = init_p.bitLength() + 7 >>> 3;
        if (array.length - n < n2) {
            throw new ShortBufferException("Buffer too short for shared secret");
        }
        this.generateSecret = false;
        final byte[] byteArray = this.y.modPow(this.x, init_p).toByteArray();
        if (byteArray.length == n2) {
            System.arraycopy(byteArray, 0, array, n, byteArray.length);
        }
        else if (byteArray.length < n2) {
            System.arraycopy(byteArray, 0, array, n + (n2 - byteArray.length), byteArray.length);
        }
        else {
            if (byteArray.length != n2 + 1 || byteArray[0] != 0) {
                throw new ProviderException("Generated secret is out-of-range");
            }
            System.arraycopy(byteArray, 1, array, n, n2);
        }
        return n2;
    }
    
    @Override
    protected SecretKey engineGenerateSecret(final String s) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        if (s == null) {
            throw new NoSuchAlgorithmException("null algorithm");
        }
        if (!s.equalsIgnoreCase("TlsPremasterSecret") && !AllowKDF.VALUE) {
            throw new NoSuchAlgorithmException("Unsupported secret key algorithm: " + s);
        }
        final byte[] engineGenerateSecret = this.engineGenerateSecret();
        if (s.equalsIgnoreCase("DES")) {
            return new DESKey(engineGenerateSecret);
        }
        if (s.equalsIgnoreCase("DESede") || s.equalsIgnoreCase("TripleDES")) {
            return new DESedeKey(engineGenerateSecret);
        }
        if (s.equalsIgnoreCase("Blowfish")) {
            int length = engineGenerateSecret.length;
            if (length >= 56) {
                length = 56;
            }
            return new SecretKeySpec(engineGenerateSecret, 0, length, "Blowfish");
        }
        if (s.equalsIgnoreCase("AES")) {
            int length2 = engineGenerateSecret.length;
            SecretKey secretKey = null;
            for (int n = AESConstants.AES_KEYSIZES.length - 1; secretKey == null && n >= 0; --n) {
                if (length2 >= AESConstants.AES_KEYSIZES[n]) {
                    length2 = AESConstants.AES_KEYSIZES[n];
                    secretKey = new SecretKeySpec(engineGenerateSecret, 0, length2, "AES");
                }
            }
            if (secretKey == null) {
                throw new InvalidKeyException("Key material is too short");
            }
            return secretKey;
        }
        else {
            if (s.equals("TlsPremasterSecret")) {
                return new SecretKeySpec(KeyUtil.trimZeroes(engineGenerateSecret), "TlsPremasterSecret");
            }
            throw new NoSuchAlgorithmException("Unsupported secret key algorithm: " + s);
        }
    }
    
    private static class AllowKDF
    {
        private static final boolean VALUE;
        
        private static boolean getValue() {
            return AccessController.doPrivileged(() -> Boolean.getBoolean("jdk.crypto.KeyAgreement.legacyKDF"));
        }
        
        static {
            VALUE = getValue();
        }
    }
}
