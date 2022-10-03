package sun.security.ssl;

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
    protected void engineInitSign(final PrivateKey privateKey, final SecureRandom secureRandom) throws InvalidKeyException {
        if (privateKey == null) {
            throw new InvalidKeyException("Private key must not be null");
        }
        this.mdMD5.reset();
        this.mdSHA.reset();
        this.rawRsa.initSign(privateKey, secureRandom);
    }
    
    @Override
    protected void engineUpdate(final byte b) {
        this.mdMD5.update(b);
        this.mdSHA.update(b);
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) {
        this.mdMD5.update(array, n, n2);
        this.mdSHA.update(array, n, n2);
    }
    
    private byte[] getDigest() throws SignatureException {
        try {
            final byte[] array = new byte[36];
            this.mdMD5.digest(array, 0, 16);
            this.mdSHA.digest(array, 16, 20);
            return array;
        }
        catch (final DigestException ex) {
            throw new SignatureException(ex);
        }
    }
    
    @Override
    protected byte[] engineSign() throws SignatureException {
        this.rawRsa.update(this.getDigest());
        return this.rawRsa.sign();
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws SignatureException {
        return this.engineVerify(array, 0, array.length);
    }
    
    @Override
    protected boolean engineVerify(final byte[] array, final int n, final int n2) throws SignatureException {
        this.rawRsa.update(this.getDigest());
        return this.rawRsa.verify(array, n, n2);
    }
    
    @Override
    protected void engineSetParameter(final String s, final Object o) throws InvalidParameterException {
        throw new InvalidParameterException("Parameters not supported");
    }
    
    @Override
    protected void engineSetParameter(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("No parameters accepted");
        }
    }
    
    @Override
    protected Object engineGetParameter(final String s) throws InvalidParameterException {
        throw new InvalidParameterException("Parameters not supported");
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }
}
