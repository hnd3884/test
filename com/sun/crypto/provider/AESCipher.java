package com.sun.crypto.provider;

import java.security.GeneralSecurityException;
import java.security.ProviderException;
import java.nio.ByteBuffer;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import java.security.AlgorithmParameters;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.Key;
import javax.crypto.CipherSpi;

abstract class AESCipher extends CipherSpi
{
    private CipherCore core;
    private final int fixedKeySize;
    private boolean updateCalled;
    
    static final void checkKeySize(final Key key, final int n) throws InvalidKeyException {
        if (n != -1) {
            if (key == null) {
                throw new InvalidKeyException("The key must not be null");
            }
            final byte[] encoded = key.getEncoded();
            if (encoded == null) {
                throw new InvalidKeyException("Key encoding must not be null");
            }
            if (encoded.length != n) {
                throw new InvalidKeyException("The key must be " + n + " bytes");
            }
        }
    }
    
    protected AESCipher(final int fixedKeySize) {
        this.core = null;
        this.core = new CipherCore(new AESCrypt(), 16);
        this.fixedKeySize = fixedKeySize;
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
        return 16;
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
        return this.core.getParameters("AES");
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        checkKeySize(key, this.fixedKeySize);
        this.updateCalled = false;
        this.core.init(n, key, secureRandom);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        checkKeySize(key, this.fixedKeySize);
        this.updateCalled = false;
        this.core.init(n, key, algorithmParameterSpec, secureRandom);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        checkKeySize(key, this.fixedKeySize);
        this.updateCalled = false;
        this.core.init(n, key, algorithmParameters, secureRandom);
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        this.updateCalled = true;
        return this.core.update(array, n, n2);
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        this.updateCalled = true;
        return this.core.update(array, n, n2, array2, n3);
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        final byte[] doFinal = this.core.doFinal(array, n, n2);
        this.updateCalled = false;
        return doFinal;
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws IllegalBlockSizeException, ShortBufferException, BadPaddingException {
        final int doFinal = this.core.doFinal(array, n, n2, array2, n3);
        this.updateCalled = false;
        return doFinal;
    }
    
    @Override
    protected int engineGetKeySize(final Key key) throws InvalidKeyException {
        final byte[] encoded = key.getEncoded();
        if (!AESCrypt.isKeySizeValid(encoded.length)) {
            throw new InvalidKeyException("Invalid AES key length: " + encoded.length + " bytes");
        }
        return Math.multiplyExact(encoded.length, 8);
    }
    
    @Override
    protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        return this.core.wrap(key);
    }
    
    @Override
    protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        return this.core.unwrap(array, s, n);
    }
    
    @Override
    protected void engineUpdateAAD(final byte[] array, final int n, final int n2) {
        if (this.core.getMode() == 7 && this.updateCalled) {
            throw new IllegalStateException("AAD must be supplied before encryption/decryption starts");
        }
        this.core.updateAAD(array, n, n2);
    }
    
    @Override
    protected void engineUpdateAAD(final ByteBuffer byteBuffer) {
        if (this.core.getMode() == 7 && this.updateCalled) {
            throw new IllegalStateException("AAD must be supplied before encryption/decryption starts");
        }
        if (byteBuffer != null) {
            final int n = byteBuffer.limit() - byteBuffer.position();
            if (n > 0) {
                if (byteBuffer.hasArray()) {
                    this.core.updateAAD(byteBuffer.array(), Math.addExact(byteBuffer.arrayOffset(), byteBuffer.position()), n);
                    byteBuffer.position(byteBuffer.limit());
                }
                else {
                    final byte[] array = new byte[n];
                    byteBuffer.get(array);
                    this.core.updateAAD(array, 0, n);
                }
            }
        }
    }
    
    public static final class General extends AESCipher
    {
        public General() {
            super(-1);
        }
    }
    
    abstract static class OidImpl extends AESCipher
    {
        protected OidImpl(final int n, final String s, final String s2) {
            super(n);
            try {
                this.engineSetMode(s);
                this.engineSetPadding(s2);
            }
            catch (final GeneralSecurityException ex) {
                final ProviderException ex2 = new ProviderException("Internal Error");
                ex2.initCause(ex);
                throw ex2;
            }
        }
    }
    
    public static final class AES128_ECB_NoPadding extends OidImpl
    {
        public AES128_ECB_NoPadding() {
            super(16, "ECB", "NOPADDING");
        }
    }
    
    public static final class AES192_ECB_NoPadding extends OidImpl
    {
        public AES192_ECB_NoPadding() {
            super(24, "ECB", "NOPADDING");
        }
    }
    
    public static final class AES256_ECB_NoPadding extends OidImpl
    {
        public AES256_ECB_NoPadding() {
            super(32, "ECB", "NOPADDING");
        }
    }
    
    public static final class AES128_CBC_NoPadding extends OidImpl
    {
        public AES128_CBC_NoPadding() {
            super(16, "CBC", "NOPADDING");
        }
    }
    
    public static final class AES192_CBC_NoPadding extends OidImpl
    {
        public AES192_CBC_NoPadding() {
            super(24, "CBC", "NOPADDING");
        }
    }
    
    public static final class AES256_CBC_NoPadding extends OidImpl
    {
        public AES256_CBC_NoPadding() {
            super(32, "CBC", "NOPADDING");
        }
    }
    
    public static final class AES128_OFB_NoPadding extends OidImpl
    {
        public AES128_OFB_NoPadding() {
            super(16, "OFB", "NOPADDING");
        }
    }
    
    public static final class AES192_OFB_NoPadding extends OidImpl
    {
        public AES192_OFB_NoPadding() {
            super(24, "OFB", "NOPADDING");
        }
    }
    
    public static final class AES256_OFB_NoPadding extends OidImpl
    {
        public AES256_OFB_NoPadding() {
            super(32, "OFB", "NOPADDING");
        }
    }
    
    public static final class AES128_CFB_NoPadding extends OidImpl
    {
        public AES128_CFB_NoPadding() {
            super(16, "CFB", "NOPADDING");
        }
    }
    
    public static final class AES192_CFB_NoPadding extends OidImpl
    {
        public AES192_CFB_NoPadding() {
            super(24, "CFB", "NOPADDING");
        }
    }
    
    public static final class AES256_CFB_NoPadding extends OidImpl
    {
        public AES256_CFB_NoPadding() {
            super(32, "CFB", "NOPADDING");
        }
    }
    
    public static final class AES128_GCM_NoPadding extends OidImpl
    {
        public AES128_GCM_NoPadding() {
            super(16, "GCM", "NOPADDING");
        }
    }
    
    public static final class AES192_GCM_NoPadding extends OidImpl
    {
        public AES192_GCM_NoPadding() {
            super(24, "GCM", "NOPADDING");
        }
    }
    
    public static final class AES256_GCM_NoPadding extends OidImpl
    {
        public AES256_GCM_NoPadding() {
            super(32, "GCM", "NOPADDING");
        }
    }
}
