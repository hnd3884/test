package com.sun.crypto.provider;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.Key;
import java.security.AlgorithmParameters;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.CipherSpi;

public final class ARCFOURCipher extends CipherSpi
{
    private final int[] S;
    private int is;
    private int js;
    private byte[] lastKey;
    
    public ARCFOURCipher() {
        this.S = new int[256];
    }
    
    private void init(final byte[] array) {
        for (int i = 0; i < 256; ++i) {
            this.S[i] = i;
        }
        int j = 0;
        int n = 0;
        int n2 = 0;
        while (j < 256) {
            final int n3 = this.S[j];
            n = (n + n3 + array[n2] & 0xFF);
            this.S[j] = this.S[n];
            this.S[n] = n3;
            if (++n2 == array.length) {
                n2 = 0;
            }
            ++j;
        }
        this.is = 0;
        this.js = 0;
    }
    
    private void crypt(final byte[] array, int n, int n2, final byte[] array2, int n3) {
        if (this.is < 0) {
            this.init(this.lastKey);
        }
        while (n2-- > 0) {
            this.is = (this.is + 1 & 0xFF);
            final int n4 = this.S[this.is];
            this.js = (this.js + n4 & 0xFF);
            final int n5 = this.S[this.js];
            this.S[this.is] = n5;
            this.S[this.js] = n4;
            array2[n3++] = (byte)(array[n++] ^ this.S[n4 + n5 & 0xFF]);
        }
    }
    
    @Override
    protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
        if (!s.equalsIgnoreCase("ECB")) {
            throw new NoSuchAlgorithmException("Unsupported mode " + s);
        }
    }
    
    @Override
    protected void engineSetPadding(final String s) throws NoSuchPaddingException {
        if (!s.equalsIgnoreCase("NoPadding")) {
            throw new NoSuchPaddingException("Padding must be NoPadding");
        }
    }
    
    @Override
    protected int engineGetBlockSize() {
        return 0;
    }
    
    @Override
    protected int engineGetOutputSize(final int n) {
        return n;
    }
    
    @Override
    protected byte[] engineGetIV() {
        return null;
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        this.init(n, key);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("Parameters not supported");
        }
        this.init(n, key);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameters != null) {
            throw new InvalidAlgorithmParameterException("Parameters not supported");
        }
        this.init(n, key);
    }
    
    private void init(final int n, final Key key) throws InvalidKeyException {
        if (n < 1 || n > 4) {
            throw new InvalidKeyException("Unknown opmode: " + n);
        }
        this.init(this.lastKey = getEncodedKey(key));
    }
    
    private static byte[] getEncodedKey(final Key key) throws InvalidKeyException {
        final String algorithm = key.getAlgorithm();
        if (!algorithm.equals("RC4") && !algorithm.equals("ARCFOUR")) {
            throw new InvalidKeyException("Not an ARCFOUR key: " + algorithm);
        }
        if (!"RAW".equals(key.getFormat())) {
            throw new InvalidKeyException("Key encoding format must be RAW");
        }
        final byte[] encoded = key.getEncoded();
        if (encoded.length < 5 || encoded.length > 128) {
            throw new InvalidKeyException("Key length must be between 40 and 1024 bit");
        }
        return encoded;
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        final byte[] array2 = new byte[n2];
        this.crypt(array, n, n2, array2, 0);
        return array2;
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        if (array2.length - n3 < n2) {
            throw new ShortBufferException("Output buffer too small");
        }
        this.crypt(array, n, n2, array2, n3);
        return n2;
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) {
        final byte[] engineUpdate = this.engineUpdate(array, n, n2);
        this.is = -1;
        return engineUpdate;
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        final int engineUpdate = this.engineUpdate(array, n, n2, array2, n3);
        this.is = -1;
        return engineUpdate;
    }
    
    @Override
    protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        final byte[] encoded = key.getEncoded();
        if (encoded == null || encoded.length == 0) {
            throw new InvalidKeyException("Could not obtain encoded key");
        }
        return this.engineDoFinal(encoded, 0, encoded.length);
    }
    
    @Override
    protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        return ConstructKeys.constructKey(this.engineDoFinal(array, 0, array.length), s, n);
    }
    
    @Override
    protected int engineGetKeySize(final Key key) throws InvalidKeyException {
        return Math.multiplyExact(getEncodedKey(key).length, 8);
    }
}
