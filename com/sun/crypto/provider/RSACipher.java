package com.sun.crypto.provider;

import sun.security.util.KeyUtil;
import javax.crypto.ShortBufferException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.interfaces.RSAKey;
import javax.crypto.spec.PSource;
import java.security.spec.MGF1ParameterSpec;
import sun.security.internal.spec.TlsRsaPremasterSecretParameterSpec;
import sun.security.rsa.RSACore;
import sun.security.rsa.RSAKeyFactory;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.InvalidParameterSpecException;
import java.security.Provider;
import javax.crypto.spec.OAEPParameterSpec;
import java.security.AlgorithmParameters;
import sun.security.jca.Providers;
import javax.crypto.NoSuchPaddingException;
import java.util.Locale;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.rsa.RSAPadding;
import javax.crypto.CipherSpi;

public final class RSACipher extends CipherSpi
{
    private static final byte[] B0;
    private static final int MODE_ENCRYPT = 1;
    private static final int MODE_DECRYPT = 2;
    private static final int MODE_SIGN = 3;
    private static final int MODE_VERIFY = 4;
    private static final String PAD_NONE = "NoPadding";
    private static final String PAD_PKCS1 = "PKCS1Padding";
    private static final String PAD_OAEP_MGF1 = "OAEP";
    private int mode;
    private String paddingType;
    private RSAPadding padding;
    private AlgorithmParameterSpec spec;
    private byte[] buffer;
    private int bufOfs;
    private int outputSize;
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private String oaepHashAlgorithm;
    private SecureRandom random;
    
    public RSACipher() {
        this.spec = null;
        this.oaepHashAlgorithm = "SHA-1";
        this.paddingType = "PKCS1Padding";
    }
    
    @Override
    protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
        if (!s.equalsIgnoreCase("ECB")) {
            throw new NoSuchAlgorithmException("Unsupported mode " + s);
        }
    }
    
    @Override
    protected void engineSetPadding(final String s) throws NoSuchPaddingException {
        if (s.equalsIgnoreCase("NoPadding")) {
            this.paddingType = "NoPadding";
        }
        else if (s.equalsIgnoreCase("PKCS1Padding")) {
            this.paddingType = "PKCS1Padding";
        }
        else {
            final String lowerCase = s.toLowerCase(Locale.ENGLISH);
            if (lowerCase.equals("oaeppadding")) {
                this.paddingType = "OAEP";
            }
            else {
                if (!lowerCase.startsWith("oaepwith") || !lowerCase.endsWith("andmgf1padding")) {
                    throw new NoSuchPaddingException("Padding " + s + " not supported");
                }
                this.paddingType = "OAEP";
                this.oaepHashAlgorithm = s.substring(8, s.length() - 14);
                if (Providers.getProviderList().getService("MessageDigest", this.oaepHashAlgorithm) == null) {
                    throw new NoSuchPaddingException("MessageDigest not available for " + s);
                }
            }
        }
    }
    
    @Override
    protected int engineGetBlockSize() {
        return 0;
    }
    
    @Override
    protected int engineGetOutputSize(final int n) {
        return this.outputSize;
    }
    
    @Override
    protected byte[] engineGetIV() {
        return null;
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.spec != null && this.spec instanceof OAEPParameterSpec) {
            try {
                final AlgorithmParameters instance = AlgorithmParameters.getInstance("OAEP", SunJCE.getInstance());
                instance.init(this.spec);
                return instance;
            }
            catch (final NoSuchAlgorithmException ex) {
                throw new RuntimeException("Cannot find OAEP  AlgorithmParameters implementation in SunJCE provider");
            }
            catch (final InvalidParameterSpecException ex2) {
                throw new RuntimeException("OAEPParameterSpec not supported");
            }
        }
        return null;
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        try {
            this.init(n, key, secureRandom, null);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            final InvalidKeyException ex2 = new InvalidKeyException("Wrong parameters");
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.init(n, key, secureRandom, algorithmParameterSpec);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameters == null) {
            this.init(n, key, secureRandom, null);
        }
        else {
            try {
                this.init(n, key, secureRandom, algorithmParameters.getParameterSpec(OAEPParameterSpec.class));
            }
            catch (final InvalidParameterSpecException ex) {
                final InvalidAlgorithmParameterException ex2 = new InvalidAlgorithmParameterException("Wrong parameter");
                ex2.initCause(ex);
                throw ex2;
            }
        }
    }
    
    private void init(final int n, final Key key, final SecureRandom random, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        boolean b = false;
        switch (n) {
            case 1:
            case 3: {
                b = true;
                break;
            }
            case 2:
            case 4: {
                b = false;
                break;
            }
            default: {
                throw new InvalidKeyException("Unknown mode: " + n);
            }
        }
        final RSAKey rsaKey = RSAKeyFactory.toRSAKey(key);
        if (key instanceof RSAPublicKey) {
            this.mode = (b ? 1 : 4);
            this.publicKey = (RSAPublicKey)key;
            this.privateKey = null;
        }
        else {
            this.mode = (b ? 3 : 2);
            this.privateKey = (RSAPrivateKey)key;
            this.publicKey = null;
        }
        final int byteLength = RSACore.getByteLength(rsaKey.getModulus());
        this.outputSize = byteLength;
        this.bufOfs = 0;
        if (this.paddingType == "NoPadding") {
            if (algorithmParameterSpec != null) {
                throw new InvalidAlgorithmParameterException("Parameters not supported");
            }
            this.padding = RSAPadding.getInstance(3, byteLength, random);
            this.buffer = new byte[byteLength];
        }
        else if (this.paddingType == "PKCS1Padding") {
            if (algorithmParameterSpec != null) {
                if (!(algorithmParameterSpec instanceof TlsRsaPremasterSecretParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("Parameters not supported");
                }
                this.spec = algorithmParameterSpec;
                this.random = random;
            }
            this.padding = RSAPadding.getInstance((this.mode <= 2) ? 2 : 1, byteLength, random);
            if (b) {
                this.buffer = new byte[this.padding.getMaxDataSize()];
            }
            else {
                this.buffer = new byte[byteLength];
            }
        }
        else {
            if (this.mode == 3 || this.mode == 4) {
                throw new InvalidKeyException("OAEP cannot be used to sign or verify signatures");
            }
            if (algorithmParameterSpec != null) {
                if (!(algorithmParameterSpec instanceof OAEPParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("Wrong Parameters for OAEP Padding");
                }
                this.spec = algorithmParameterSpec;
            }
            else {
                this.spec = new OAEPParameterSpec(this.oaepHashAlgorithm, "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
            }
            this.padding = RSAPadding.getInstance(4, byteLength, random, (OAEPParameterSpec)this.spec);
            if (b) {
                this.buffer = new byte[this.padding.getMaxDataSize()];
            }
            else {
                this.buffer = new byte[byteLength];
            }
        }
    }
    
    private void update(final byte[] array, final int n, final int n2) {
        if (n2 == 0 || array == null) {
            return;
        }
        if (n2 > this.buffer.length - this.bufOfs) {
            this.bufOfs = this.buffer.length + 1;
            return;
        }
        System.arraycopy(array, n, this.buffer, this.bufOfs, n2);
        this.bufOfs += n2;
    }
    
    private byte[] doFinal() throws BadPaddingException, IllegalBlockSizeException {
        if (this.bufOfs > this.buffer.length) {
            throw new IllegalBlockSizeException("Data must not be longer than " + this.buffer.length + " bytes");
        }
        try {
            switch (this.mode) {
                case 3: {
                    return RSACore.rsa(this.padding.pad(this.buffer, 0, this.bufOfs), this.privateKey, true);
                }
                case 4: {
                    return this.padding.unpad(RSACore.rsa(RSACore.convert(this.buffer, 0, this.bufOfs), this.publicKey));
                }
                case 1: {
                    return RSACore.rsa(this.padding.pad(this.buffer, 0, this.bufOfs), this.publicKey);
                }
                case 2: {
                    return this.padding.unpad(RSACore.rsa(RSACore.convert(this.buffer, 0, this.bufOfs), this.privateKey, false));
                }
                default: {
                    throw new AssertionError((Object)"Internal error");
                }
            }
        }
        finally {
            this.bufOfs = 0;
        }
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        this.update(array, n, n2);
        return RSACipher.B0;
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        this.update(array, n, n2);
        return 0;
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws BadPaddingException, IllegalBlockSizeException {
        this.update(array, n, n2);
        return this.doFinal();
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, BadPaddingException, IllegalBlockSizeException {
        if (this.outputSize > array2.length - n3) {
            throw new ShortBufferException("Need " + this.outputSize + " bytes for output");
        }
        this.update(array, n, n2);
        final byte[] doFinal = this.doFinal();
        final int length = doFinal.length;
        System.arraycopy(doFinal, 0, array2, n3, length);
        return length;
    }
    
    @Override
    protected byte[] engineWrap(final Key key) throws InvalidKeyException, IllegalBlockSizeException {
        final byte[] encoded = key.getEncoded();
        if (encoded == null || encoded.length == 0) {
            throw new InvalidKeyException("Could not obtain encoded key");
        }
        if (encoded.length > this.buffer.length) {
            throw new InvalidKeyException("Key is too long for wrapping");
        }
        this.update(encoded, 0, encoded.length);
        try {
            return this.doFinal();
        }
        catch (final BadPaddingException ex) {
            throw new InvalidKeyException("Wrapping failed", ex);
        }
    }
    
    @Override
    protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        if (array.length > this.buffer.length) {
            throw new InvalidKeyException("Key is too long for unwrapping");
        }
        final boolean equals = s.equals("TlsRsaPremasterSecret");
        BadPaddingException ex = null;
        byte[] array2 = null;
        this.update(array, 0, array.length);
        try {
            array2 = this.doFinal();
        }
        catch (final BadPaddingException ex2) {
            if (!equals) {
                throw new InvalidKeyException("Unwrapping failed", ex2);
            }
            ex = ex2;
        }
        catch (final IllegalBlockSizeException ex3) {
            throw new InvalidKeyException("Unwrapping failed", ex3);
        }
        if (equals) {
            if (!(this.spec instanceof TlsRsaPremasterSecretParameterSpec)) {
                throw new IllegalStateException("No TlsRsaPremasterSecretParameterSpec specified");
            }
            array2 = KeyUtil.checkTlsPreMasterSecretKey(((TlsRsaPremasterSecretParameterSpec)this.spec).getClientVersion(), ((TlsRsaPremasterSecretParameterSpec)this.spec).getServerVersion(), this.random, array2, ex != null);
        }
        return ConstructKeys.constructKey(array2, s, n);
    }
    
    @Override
    protected int engineGetKeySize(final Key key) throws InvalidKeyException {
        return RSAKeyFactory.toRSAKey(key).getModulus().bitLength();
    }
    
    static {
        B0 = new byte[0];
    }
}
