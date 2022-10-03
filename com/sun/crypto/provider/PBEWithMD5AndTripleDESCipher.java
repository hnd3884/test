package com.sun.crypto.provider;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import java.security.Key;
import java.security.AlgorithmParameters;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.CipherSpi;

public final class PBEWithMD5AndTripleDESCipher extends CipherSpi
{
    private PBES1Core core;
    
    public PBEWithMD5AndTripleDESCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.core = new PBES1Core("DESede");
    }
    
    @Override
    protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
        if (s != null && !s.equalsIgnoreCase("CBC")) {
            throw new NoSuchAlgorithmException("Invalid cipher mode: " + s);
        }
    }
    
    @Override
    protected void engineSetPadding(final String s) throws NoSuchPaddingException {
        if (s != null && !s.equalsIgnoreCase("PKCS5Padding")) {
            throw new NoSuchPaddingException("Invalid padding scheme: " + s);
        }
    }
    
    @Override
    protected int engineGetBlockSize() {
        return this.core.getBlockSize();
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
    protected AlgorithmParameters engineGetParameters() {
        return this.core.getParameters();
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        try {
            this.core.init(n, key, (AlgorithmParameterSpec)null, secureRandom);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            final InvalidKeyException ex2 = new InvalidKeyException("requires PBE parameters");
            ex2.initCause(ex);
            throw ex2;
        }
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
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        return this.core.doFinal(array, n, n2, array2, n3);
    }
    
    @Override
    protected int engineGetKeySize(final Key key) throws InvalidKeyException {
        return 168;
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
