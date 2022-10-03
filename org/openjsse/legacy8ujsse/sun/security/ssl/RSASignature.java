package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.security.DigestException;
import java.security.SignatureException;
import java.security.SecureRandom;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureSpi;

public final class RSASignature extends SignatureSpi
{
    private final Signature rawRsa;
    private MessageDigest md5;
    private MessageDigest sha;
    private boolean isReset;
    
    public RSASignature() throws NoSuchAlgorithmException {
        this.rawRsa = JsseJce.getSignature("NONEwithRSA");
        this.isReset = true;
    }
    
    static Signature getInstance() throws NoSuchAlgorithmException {
        return JsseJce.getSignature("MD5andSHA1withRSA");
    }
    
    static Signature getInternalInstance() throws NoSuchAlgorithmException, NoSuchProviderException {
        return Signature.getInstance("MD5andSHA1withRSA", "Legacy8uJSSE");
    }
    
    static void setHashes(final Signature sig, final MessageDigest md5, final MessageDigest sha) {
        sig.setParameter("hashes", new MessageDigest[] { md5, sha });
    }
    
    private void reset() {
        if (!this.isReset) {
            this.md5.reset();
            this.sha.reset();
            this.isReset = true;
        }
    }
    
    private static void checkNull(final Key key) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("Key must not be null");
        }
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        checkNull(publicKey);
        this.reset();
        this.rawRsa.initVerify(publicKey);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        this.engineInitSign(privateKey, null);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey, final SecureRandom random) throws InvalidKeyException {
        checkNull(privateKey);
        this.reset();
        this.rawRsa.initSign(privateKey, random);
    }
    
    private void initDigests() {
        if (this.md5 == null) {
            this.md5 = JsseJce.getMD5();
            this.sha = JsseJce.getSHA();
        }
    }
    
    @Override
    protected void engineUpdate(final byte b) {
        this.initDigests();
        this.isReset = false;
        this.md5.update(b);
        this.sha.update(b);
    }
    
    @Override
    protected void engineUpdate(final byte[] b, final int off, final int len) {
        this.initDigests();
        this.isReset = false;
        this.md5.update(b, off, len);
        this.sha.update(b, off, len);
    }
    
    private byte[] getDigest() throws SignatureException {
        try {
            this.initDigests();
            final byte[] data = new byte[36];
            this.md5.digest(data, 0, 16);
            this.sha.digest(data, 16, 20);
            this.isReset = true;
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
        if (!param.equals("hashes")) {
            throw new InvalidParameterException("Parameter not supported: " + param);
        }
        if (!(value instanceof MessageDigest[])) {
            throw new InvalidParameterException("value must be MessageDigest[]");
        }
        final MessageDigest[] digests = (MessageDigest[])value;
        this.md5 = digests[0];
        this.sha = digests[1];
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
