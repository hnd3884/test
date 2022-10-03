package org.openjsse.sun.security.ssl;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.security.DigestException;
import java.security.SignatureException;
import java.security.SecureRandom;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureSpi;

public final class RSASignature extends SignatureSpi
{
    private final Signature rawRsa;
    private final MessageDigest mdMD5;
    private final MessageDigest mdSHA;
    
    public RSASignature() throws NoSuchAlgorithmException {
        this.rawRsa = JsseJce.getSignature("NONEwithRSA");
        this.mdMD5 = JsseJce.getMessageDigest("MD5");
        this.mdSHA = JsseJce.getMessageDigest("SHA");
    }
    
    static Signature getInstance() throws NoSuchAlgorithmException {
        return JsseJce.getSignature("MD5andSHA1withRSA");
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        if (publicKey == null) {
            throw new InvalidKeyException("Public key must not be null");
        }
        this.mdMD5.reset();
        this.mdSHA.reset();
        this.rawRsa.initVerify(publicKey);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        this.engineInitSign(privateKey, null);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey, final SecureRandom random) throws InvalidKeyException {
        if (privateKey == null) {
            throw new InvalidKeyException("Private key must not be null");
        }
        this.mdMD5.reset();
        this.mdSHA.reset();
        this.rawRsa.initSign(privateKey, random);
    }
    
    @Override
    protected void engineUpdate(final byte b) {
        this.mdMD5.update(b);
        this.mdSHA.update(b);
    }
    
    @Override
    protected void engineUpdate(final byte[] b, final int off, final int len) {
        this.mdMD5.update(b, off, len);
        this.mdSHA.update(b, off, len);
    }
    
    private byte[] getDigest() throws SignatureException {
        try {
            final byte[] data = new byte[36];
            this.mdMD5.digest(data, 0, 16);
            this.mdSHA.digest(data, 16, 20);
            return data;
        }
        catch (final DigestException e) {
            throw new SignatureException(e);
        }
    }
    
    @Override
    protected byte[] engineSign() throws SignatureException {
        this.rawRsa.update(this.getDigest());
        return this.rawRsa.sign();
    }
    
    @Override
    protected boolean engineVerify(final byte[] sigBytes) throws SignatureException {
        return this.engineVerify(sigBytes, 0, sigBytes.length);
    }
    
    @Override
    protected boolean engineVerify(final byte[] sigBytes, final int offset, final int length) throws SignatureException {
        this.rawRsa.update(this.getDigest());
        return this.rawRsa.verify(sigBytes, offset, length);
    }
    
    @Override
    protected void engineSetParameter(final String param, final Object value) throws InvalidParameterException {
        throw new InvalidParameterException("Parameters not supported");
    }
    
    @Override
    protected void engineSetParameter(final AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params != null) {
            throw new InvalidAlgorithmParameterException("No parameters accepted");
        }
    }
    
    @Override
    protected Object engineGetParameter(final String param) throws InvalidParameterException {
        throw new InvalidParameterException("Parameters not supported");
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }
}
