package org.bouncycastle.pqc.jcajce.provider.util;

import javax.crypto.ShortBufferException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.security.Key;
import java.io.ByteArrayOutputStream;
import java.security.spec.AlgorithmParameterSpec;

public abstract class AsymmetricBlockCipher extends CipherSpiExt
{
    protected AlgorithmParameterSpec paramSpec;
    protected ByteArrayOutputStream buf;
    protected int maxPlainTextSize;
    protected int cipherTextSize;
    
    public AsymmetricBlockCipher() {
        this.buf = new ByteArrayOutputStream();
    }
    
    @Override
    public final int getBlockSize() {
        return (this.opMode == 1) ? this.maxPlainTextSize : this.cipherTextSize;
    }
    
    @Override
    public final byte[] getIV() {
        return null;
    }
    
    @Override
    public final int getOutputSize(final int n) {
        final int n2 = n + this.buf.size();
        final int blockSize = this.getBlockSize();
        if (n2 > blockSize) {
            return 0;
        }
        return blockSize;
    }
    
    @Override
    public final AlgorithmParameterSpec getParameters() {
        return this.paramSpec;
    }
    
    public final void initEncrypt(final Key key) throws InvalidKeyException {
        try {
            this.initEncrypt(key, null, new SecureRandom());
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
        }
    }
    
    public final void initEncrypt(final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        try {
            this.initEncrypt(key, null, secureRandom);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
        }
    }
    
    public final void initEncrypt(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.initEncrypt(key, algorithmParameterSpec, new SecureRandom());
    }
    
    @Override
    public final void initEncrypt(final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.opMode = 1;
        this.initCipherEncrypt(key, algorithmParameterSpec, secureRandom);
    }
    
    public final void initDecrypt(final Key key) throws InvalidKeyException {
        try {
            this.initDecrypt(key, null);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
        }
    }
    
    @Override
    public final void initDecrypt(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.opMode = 2;
        this.initCipherDecrypt(key, algorithmParameterSpec);
    }
    
    @Override
    public final byte[] update(final byte[] array, final int n, final int n2) {
        if (n2 != 0) {
            this.buf.write(array, n, n2);
        }
        return new byte[0];
    }
    
    @Override
    public final int update(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        this.update(array, n, n2);
        return 0;
    }
    
    @Override
    public final byte[] doFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        this.checkLength(n2);
        this.update(array, n, n2);
        final byte[] byteArray = this.buf.toByteArray();
        this.buf.reset();
        switch (this.opMode) {
            case 1: {
                return this.messageEncrypt(byteArray);
            }
            case 2: {
                return this.messageDecrypt(byteArray);
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public final int doFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        if (array2.length < this.getOutputSize(n2)) {
            throw new ShortBufferException("Output buffer too short.");
        }
        final byte[] doFinal = this.doFinal(array, n, n2);
        System.arraycopy(doFinal, 0, array2, n3, doFinal.length);
        return doFinal.length;
    }
    
    @Override
    protected final void setMode(final String s) {
    }
    
    @Override
    protected final void setPadding(final String s) {
    }
    
    protected void checkLength(final int n) throws IllegalBlockSizeException {
        final int n2 = n + this.buf.size();
        if (this.opMode == 1) {
            if (n2 > this.maxPlainTextSize) {
                throw new IllegalBlockSizeException("The length of the plaintext (" + n2 + " bytes) is not supported by the cipher (max. " + this.maxPlainTextSize + " bytes).");
            }
        }
        else if (this.opMode == 2 && n2 != this.cipherTextSize) {
            throw new IllegalBlockSizeException("Illegal ciphertext length (expected " + this.cipherTextSize + " bytes, was " + n2 + " bytes).");
        }
    }
    
    protected abstract void initCipherEncrypt(final Key p0, final AlgorithmParameterSpec p1, final SecureRandom p2) throws InvalidKeyException, InvalidAlgorithmParameterException;
    
    protected abstract void initCipherDecrypt(final Key p0, final AlgorithmParameterSpec p1) throws InvalidKeyException, InvalidAlgorithmParameterException;
    
    protected abstract byte[] messageEncrypt(final byte[] p0) throws IllegalBlockSizeException, BadPaddingException;
    
    protected abstract byte[] messageDecrypt(final byte[] p0) throws IllegalBlockSizeException, BadPaddingException;
}
