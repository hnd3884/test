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

abstract class AESWrapCipher extends CipherSpi
{
    private static final byte[] IV;
    private static final int blksize = 16;
    private AESCrypt cipher;
    private boolean decrypting;
    private final int fixedKeySize;
    
    public AESWrapCipher(final int fixedKeySize) {
        this.decrypting = false;
        this.cipher = new AESCrypt();
        this.fixedKeySize = fixedKeySize;
    }
    
    @Override
    protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
        if (!s.equalsIgnoreCase("ECB")) {
            throw new NoSuchAlgorithmException(s + " cannot be used");
        }
    }
    
    @Override
    protected void engineSetPadding(final String s) throws NoSuchPaddingException {
        if (!s.equalsIgnoreCase("NoPadding")) {
            throw new NoSuchPaddingException(s + " cannot be used");
        }
    }
    
    @Override
    protected int engineGetBlockSize() {
        return 16;
    }
    
    @Override
    protected int engineGetOutputSize(final int n) {
        int addExact;
        if (this.decrypting) {
            addExact = n - 8;
        }
        else {
            addExact = Math.addExact(n, 8);
        }
        return (addExact < 0) ? 0 : addExact;
    }
    
    @Override
    protected byte[] engineGetIV() {
        return null;
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        if (n == 3) {
            this.decrypting = false;
        }
        else {
            if (n != 4) {
                throw new UnsupportedOperationException("This cipher can only be used for key wrapping and unwrapping");
            }
            this.decrypting = true;
        }
        AESCipher.checkKeySize(key, this.fixedKeySize);
        this.cipher.init(this.decrypting, key.getAlgorithm(), key.getEncoded());
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("This cipher does not accept any parameters");
        }
        this.engineInit(n, key, secureRandom);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameters != null) {
            throw new InvalidAlgorithmParameterException("This cipher does not accept any parameters");
        }
        this.engineInit(n, key, secureRandom);
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        throw new IllegalStateException("Cipher has not been initialized");
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        throw new IllegalStateException("Cipher has not been initialized");
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        throw new IllegalStateException("Cipher has not been initialized");
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws IllegalBlockSizeException, ShortBufferException, BadPaddingException {
        throw new IllegalStateException("Cipher has not been initialized");
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }
    
    @Override
    protected int engineGetKeySize(final Key key) throws InvalidKeyException {
        final byte[] encoded = key.getEncoded();
        if (!AESCrypt.isKeySizeValid(encoded.length)) {
            throw new InvalidKeyException("Invalid key length: " + encoded.length + " bytes");
        }
        return Math.multiplyExact(encoded.length, 8);
    }
    
    @Override
    protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        final byte[] encoded = key.getEncoded();
        if (encoded == null || encoded.length == 0) {
            throw new InvalidKeyException("Cannot get an encoding of the key to be wrapped");
        }
        final byte[] array = new byte[Math.addExact(encoded.length, 8)];
        if (encoded.length == 8) {
            System.arraycopy(AESWrapCipher.IV, 0, array, 0, AESWrapCipher.IV.length);
            System.arraycopy(encoded, 0, array, AESWrapCipher.IV.length, 8);
            this.cipher.encryptBlock(array, 0, array, 0);
        }
        else {
            if (encoded.length % 8 != 0) {
                throw new IllegalBlockSizeException("length of the to be wrapped key should be multiples of 8 bytes");
            }
            System.arraycopy(AESWrapCipher.IV, 0, array, 0, AESWrapCipher.IV.length);
            System.arraycopy(encoded, 0, array, AESWrapCipher.IV.length, encoded.length);
            final int n = encoded.length / 8;
            final byte[] array2 = new byte[16];
            for (int i = 0; i < 6; ++i) {
                for (int j = 1; j <= n; ++j) {
                    int k = j + i * n;
                    System.arraycopy(array, 0, array2, 0, AESWrapCipher.IV.length);
                    System.arraycopy(array, j * 8, array2, AESWrapCipher.IV.length, 8);
                    this.cipher.encryptBlock(array2, 0, array2, 0);
                    for (int n2 = 1; k != 0; k >>>= 8, ++n2) {
                        final byte b = (byte)k;
                        final byte[] array3 = array2;
                        final int n3 = AESWrapCipher.IV.length - n2;
                        array3[n3] ^= b;
                    }
                    System.arraycopy(array2, 0, array, 0, AESWrapCipher.IV.length);
                    System.arraycopy(array2, 8, array, 8 * j, 8);
                }
            }
        }
        return array;
    }
    
    @Override
    protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        final int length = array.length;
        if (length == 0) {
            throw new InvalidKeyException("The wrapped key is empty");
        }
        if (length % 8 != 0) {
            throw new InvalidKeyException("The wrapped key has invalid key length");
        }
        final byte[] array2 = new byte[length - 8];
        final byte[] array3 = new byte[16];
        if (length == 16) {
            this.cipher.decryptBlock(array, 0, array3, 0);
            for (int i = 0; i < AESWrapCipher.IV.length; ++i) {
                if (AESWrapCipher.IV[i] != array3[i]) {
                    throw new InvalidKeyException("Integrity check failed");
                }
            }
            System.arraycopy(array3, AESWrapCipher.IV.length, array2, 0, array2.length);
        }
        else {
            System.arraycopy(array, 0, array3, 0, AESWrapCipher.IV.length);
            System.arraycopy(array, AESWrapCipher.IV.length, array2, 0, array2.length);
            final int n2 = array2.length / 8;
            for (int j = 5; j >= 0; --j) {
                for (int k = n2; k > 0; --k) {
                    int l = k + j * n2;
                    System.arraycopy(array2, 8 * (k - 1), array3, AESWrapCipher.IV.length, 8);
                    for (int n3 = 1; l != 0; l >>>= 8, ++n3) {
                        final byte b = (byte)l;
                        final byte[] array4 = array3;
                        final int n4 = AESWrapCipher.IV.length - n3;
                        array4[n4] ^= b;
                    }
                    this.cipher.decryptBlock(array3, 0, array3, 0);
                    System.arraycopy(array3, AESWrapCipher.IV.length, array2, 8 * (k - 1), 8);
                }
            }
            for (int n5 = 0; n5 < AESWrapCipher.IV.length; ++n5) {
                if (AESWrapCipher.IV[n5] != array3[n5]) {
                    throw new InvalidKeyException("Integrity check failed");
                }
            }
        }
        return ConstructKeys.constructKey(array2, s, n);
    }
    
    static {
        IV = new byte[] { -90, -90, -90, -90, -90, -90, -90, -90 };
    }
    
    public static final class General extends AESWrapCipher
    {
        public General() {
            super(-1);
        }
    }
    
    public static final class AES128 extends AESWrapCipher
    {
        public AES128() {
            super(16);
        }
    }
    
    public static final class AES192 extends AESWrapCipher
    {
        public AES192() {
            super(24);
        }
    }
    
    public static final class AES256 extends AESWrapCipher
    {
        public AES256() {
            super(32);
        }
    }
}
