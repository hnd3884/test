package com.sun.crypto.provider;

import java.security.DigestException;
import java.security.ProviderException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.security.InvalidKeyException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import javax.crypto.MacSpi;

abstract class HmacCore extends MacSpi implements Cloneable
{
    private MessageDigest md;
    private byte[] k_ipad;
    private byte[] k_opad;
    private boolean first;
    private final int blockLen;
    
    HmacCore(final MessageDigest md, final int blockLen) {
        this.md = md;
        this.blockLen = blockLen;
        this.k_ipad = new byte[this.blockLen];
        this.k_opad = new byte[this.blockLen];
        this.first = true;
    }
    
    HmacCore(final String s, final int n) throws NoSuchAlgorithmException {
        this(MessageDigest.getInstance(s), n);
    }
    
    @Override
    protected int engineGetMacLength() {
        return this.md.getDigestLength();
    }
    
    @Override
    protected void engineInit(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("HMAC does not use parameters");
        }
        if (!(key instanceof SecretKey)) {
            throw new InvalidKeyException("Secret key expected");
        }
        byte[] encoded = key.getEncoded();
        if (encoded == null) {
            throw new InvalidKeyException("Missing key data");
        }
        if (encoded.length > this.blockLen) {
            final byte[] digest = this.md.digest(encoded);
            Arrays.fill(encoded, (byte)0);
            encoded = digest;
        }
        for (int i = 0; i < this.blockLen; ++i) {
            final byte b = (byte)((i < encoded.length) ? encoded[i] : 0);
            this.k_ipad[i] = (byte)(b ^ 0x36);
            this.k_opad[i] = (byte)(b ^ 0x5C);
        }
        Arrays.fill(encoded, (byte)0);
        this.engineReset();
    }
    
    @Override
    protected void engineUpdate(final byte b) {
        if (this.first) {
            this.md.update(this.k_ipad);
            this.first = false;
        }
        this.md.update(b);
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) {
        if (this.first) {
            this.md.update(this.k_ipad);
            this.first = false;
        }
        this.md.update(array, n, n2);
    }
    
    @Override
    protected void engineUpdate(final ByteBuffer byteBuffer) {
        if (this.first) {
            this.md.update(this.k_ipad);
            this.first = false;
        }
        this.md.update(byteBuffer);
    }
    
    @Override
    protected byte[] engineDoFinal() {
        if (this.first) {
            this.md.update(this.k_ipad);
        }
        else {
            this.first = true;
        }
        try {
            final byte[] digest = this.md.digest();
            this.md.update(this.k_opad);
            this.md.update(digest);
            this.md.digest(digest, 0, digest.length);
            return digest;
        }
        catch (final DigestException ex) {
            throw new ProviderException(ex);
        }
    }
    
    @Override
    protected void engineReset() {
        if (!this.first) {
            this.md.reset();
            this.first = true;
        }
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final HmacCore hmacCore = (HmacCore)super.clone();
        hmacCore.md = (MessageDigest)this.md.clone();
        hmacCore.k_ipad = this.k_ipad.clone();
        hmacCore.k_opad = this.k_opad.clone();
        return hmacCore;
    }
    
    public static final class HmacSHA224 extends HmacCore
    {
        public HmacSHA224() throws NoSuchAlgorithmException {
            super("SHA-224", 64);
        }
    }
    
    public static final class HmacSHA256 extends HmacCore
    {
        public HmacSHA256() throws NoSuchAlgorithmException {
            super("SHA-256", 64);
        }
    }
    
    public static final class HmacSHA384 extends HmacCore
    {
        public HmacSHA384() throws NoSuchAlgorithmException {
            super("SHA-384", 128);
        }
    }
    
    public static final class HmacSHA512 extends HmacCore
    {
        public HmacSHA512() throws NoSuchAlgorithmException {
            super("SHA-512", 128);
        }
    }
}
