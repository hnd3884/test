package com.sun.crypto.provider;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.Key;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.CipherSpi;

public final class DESedeCipher extends CipherSpi
{
    private CipherCore core;
    
    public DESedeCipher() {
        this.core = null;
        this.core = new CipherCore(new DESedeCrypt(), 8);
    }
    
    @Override
    protected void engineSetMode(final String mode) throws NoSuchAlgorithmException {
        this.core.setMode(mode);
    }
    
    @Override
    protected void engineSetPadding(final String padding) throws NoSuchPaddingException {
        this.core.setPadding(padding);
    }
    
    @Override
    protected int engineGetBlockSize() {
        return 8;
    }
    
    @Override
    protected int engineGetOutputSize(final int n) {
        return this.core.getOutputSize(n);
    }
    
    @Override
    protected byte[] engineGetIV() {
        return this.core.getIV();
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        this.core.init(n, key, secureRandom);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.core.init(n, key, algorithmParameterSpec, secureRandom);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.core.init(n, key, algorithmParameters, secureRandom);
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        return this.core.update(array, n, n2);
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        return this.core.update(array, n, n2, array2, n3);
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        return this.core.doFinal(array, n, n2);
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws IllegalBlockSizeException, ShortBufferException, BadPaddingException {
        return this.core.doFinal(array, n, n2, array2, n3);
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        return this.core.getParameters("DESede");
    }
    
    @Override
    protected int engineGetKeySize(final Key key) throws InvalidKeyException {
        final byte[] encoded = key.getEncoded();
        if (encoded.length != 24) {
            throw new InvalidKeyException("Invalid key length: " + encoded.length + " bytes");
        }
        return 112;
    }
    
    @Override
    protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        return this.core.wrap(key);
    }
    
    @Override
    protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        return this.core.unwrap(array, s, n);
    }
}
