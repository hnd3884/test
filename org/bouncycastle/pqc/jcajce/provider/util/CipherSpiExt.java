package org.bouncycastle.pqc.jcajce.provider.util;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.ShortBufferException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import java.security.Key;
import javax.crypto.CipherSpi;

public abstract class CipherSpiExt extends CipherSpi
{
    public static final int ENCRYPT_MODE = 1;
    public static final int DECRYPT_MODE = 2;
    protected int opMode;
    
    @Override
    protected final void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        try {
            this.engineInit(n, key, (AlgorithmParameterSpec)null, secureRandom);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new InvalidParameterException(ex.getMessage());
        }
    }
    
    @Override
    protected final void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameters == null) {
            this.engineInit(n, key, secureRandom);
            return;
        }
        this.engineInit(n, key, (AlgorithmParameterSpec)null, secureRandom);
    }
    
    @Override
    protected void engineInit(final int opMode, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null && !(algorithmParameterSpec instanceof AlgorithmParameterSpec)) {
            throw new InvalidAlgorithmParameterException();
        }
        if (key == null || !(key instanceof Key)) {
            throw new InvalidKeyException();
        }
        if ((this.opMode = opMode) == 1) {
            this.initEncrypt(key, algorithmParameterSpec, secureRandom);
        }
        else if (opMode == 2) {
            this.initDecrypt(key, algorithmParameterSpec);
        }
    }
    
    @Override
    protected final byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        return this.doFinal(array, n, n2);
    }
    
    @Override
    protected final int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        return this.doFinal(array, n, n2, array2, n3);
    }
    
    @Override
    protected final int engineGetBlockSize() {
        return this.getBlockSize();
    }
    
    @Override
    protected final int engineGetKeySize(final Key key) throws InvalidKeyException {
        if (!(key instanceof Key)) {
            throw new InvalidKeyException("Unsupported key.");
        }
        return this.getKeySize(key);
    }
    
    @Override
    protected final byte[] engineGetIV() {
        return this.getIV();
    }
    
    @Override
    protected final int engineGetOutputSize(final int n) {
        return this.getOutputSize(n);
    }
    
    @Override
    protected final AlgorithmParameters engineGetParameters() {
        return null;
    }
    
    @Override
    protected final void engineSetMode(final String mode) throws NoSuchAlgorithmException {
        this.setMode(mode);
    }
    
    @Override
    protected final void engineSetPadding(final String padding) throws NoSuchPaddingException {
        this.setPadding(padding);
    }
    
    @Override
    protected final byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        return this.update(array, n, n2);
    }
    
    @Override
    protected final int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        return this.update(array, n, n2, array2, n3);
    }
    
    public abstract void initEncrypt(final Key p0, final AlgorithmParameterSpec p1, final SecureRandom p2) throws InvalidKeyException, InvalidAlgorithmParameterException;
    
    public abstract void initDecrypt(final Key p0, final AlgorithmParameterSpec p1) throws InvalidKeyException, InvalidAlgorithmParameterException;
    
    public abstract String getName();
    
    public abstract int getBlockSize();
    
    public abstract int getOutputSize(final int p0);
    
    public abstract int getKeySize(final Key p0) throws InvalidKeyException;
    
    public abstract AlgorithmParameterSpec getParameters();
    
    public abstract byte[] getIV();
    
    protected abstract void setMode(final String p0) throws NoSuchAlgorithmException;
    
    protected abstract void setPadding(final String p0) throws NoSuchPaddingException;
    
    public final byte[] update(final byte[] array) {
        return this.update(array, 0, array.length);
    }
    
    public abstract byte[] update(final byte[] p0, final int p1, final int p2);
    
    public abstract int update(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4) throws ShortBufferException;
    
    public final byte[] doFinal() throws IllegalBlockSizeException, BadPaddingException {
        return this.doFinal(null, 0, 0);
    }
    
    public final byte[] doFinal(final byte[] array) throws IllegalBlockSizeException, BadPaddingException {
        return this.doFinal(array, 0, array.length);
    }
    
    public abstract byte[] doFinal(final byte[] p0, final int p1, final int p2) throws IllegalBlockSizeException, BadPaddingException;
    
    public abstract int doFinal(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException;
}
