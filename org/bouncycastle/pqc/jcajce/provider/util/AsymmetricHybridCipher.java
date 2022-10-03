package org.bouncycastle.pqc.jcajce.provider.util;

import javax.crypto.BadPaddingException;
import javax.crypto.ShortBufferException;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

public abstract class AsymmetricHybridCipher extends CipherSpiExt
{
    protected AlgorithmParameterSpec paramSpec;
    
    @Override
    protected final void setMode(final String s) {
    }
    
    @Override
    protected final void setPadding(final String s) {
    }
    
    @Override
    public final byte[] getIV() {
        return null;
    }
    
    @Override
    public final int getBlockSize() {
        return 0;
    }
    
    @Override
    public final AlgorithmParameterSpec getParameters() {
        return this.paramSpec;
    }
    
    @Override
    public final int getOutputSize(final int n) {
        return (this.opMode == 1) ? this.encryptOutputSize(n) : this.decryptOutputSize(n);
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
    public abstract byte[] update(final byte[] p0, final int p1, final int p2);
    
    @Override
    public final int update(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        if (array2.length < this.getOutputSize(n2)) {
            throw new ShortBufferException("output");
        }
        final byte[] update = this.update(array, n, n2);
        System.arraycopy(update, 0, array2, n3, update.length);
        return update.length;
    }
    
    @Override
    public abstract byte[] doFinal(final byte[] p0, final int p1, final int p2) throws BadPaddingException;
    
    @Override
    public final int doFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, BadPaddingException {
        if (array2.length < this.getOutputSize(n2)) {
            throw new ShortBufferException("Output buffer too short.");
        }
        final byte[] doFinal = this.doFinal(array, n, n2);
        System.arraycopy(doFinal, 0, array2, n3, doFinal.length);
        return doFinal.length;
    }
    
    protected abstract int encryptOutputSize(final int p0);
    
    protected abstract int decryptOutputSize(final int p0);
    
    protected abstract void initCipherEncrypt(final Key p0, final AlgorithmParameterSpec p1, final SecureRandom p2) throws InvalidKeyException, InvalidAlgorithmParameterException;
    
    protected abstract void initCipherDecrypt(final Key p0, final AlgorithmParameterSpec p1) throws InvalidKeyException, InvalidAlgorithmParameterException;
}
