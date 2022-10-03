package com.sun.crypto.provider;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.spec.InvalidParameterSpecException;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.spec.RC2ParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.Key;
import java.security.AlgorithmParameters;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.CipherSpi;

public final class RC2Cipher extends CipherSpi
{
    private final CipherCore core;
    private final RC2Crypt embeddedCipher;
    
    public RC2Cipher() {
        this.embeddedCipher = new RC2Crypt();
        this.core = new CipherCore(this.embeddedCipher, 8);
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
    protected AlgorithmParameters engineGetParameters() {
        return this.core.getParameters("RC2");
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        this.embeddedCipher.initEffectiveKeyBits(0);
        this.core.init(n, key, secureRandom);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null && algorithmParameterSpec instanceof RC2ParameterSpec) {
            this.embeddedCipher.initEffectiveKeyBits(((RC2ParameterSpec)algorithmParameterSpec).getEffectiveKeyBits());
        }
        else {
            this.embeddedCipher.initEffectiveKeyBits(0);
        }
        this.core.init(n, key, algorithmParameterSpec, secureRandom);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameters != null && algorithmParameters.getAlgorithm().equals("RC2")) {
            try {
                this.engineInit(n, key, algorithmParameters.getParameterSpec(RC2ParameterSpec.class), secureRandom);
                return;
            }
            catch (final InvalidParameterSpecException ex) {
                throw new InvalidAlgorithmParameterException("Wrong parameter type: RC2 expected");
            }
        }
        this.embeddedCipher.initEffectiveKeyBits(0);
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
    protected int engineGetKeySize(final Key key) throws InvalidKeyException {
        final byte[] keyBytes = CipherCore.getKeyBytes(key);
        RC2Crypt.checkKey(key.getAlgorithm(), keyBytes.length);
        return keyBytes.length << 3;
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
