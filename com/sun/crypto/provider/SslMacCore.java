package com.sun.crypto.provider;

import javax.crypto.MacSpi;
import java.security.DigestException;
import java.security.ProviderException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

final class SslMacCore
{
    private final MessageDigest md;
    private final byte[] pad1;
    private final byte[] pad2;
    private boolean first;
    private byte[] secret;
    
    SslMacCore(final String s, final byte[] pad1, final byte[] pad2) throws NoSuchAlgorithmException {
        this.md = MessageDigest.getInstance(s);
        this.pad1 = pad1;
        this.pad2 = pad2;
        this.first = true;
    }
    
    int getDigestLength() {
        return this.md.getDigestLength();
    }
    
    void init(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("SslMac does not use parameters");
        }
        if (!(key instanceof SecretKey)) {
            throw new InvalidKeyException("Secret key expected");
        }
        this.secret = key.getEncoded();
        if (this.secret == null || this.secret.length == 0) {
            throw new InvalidKeyException("Missing key data");
        }
        this.reset();
    }
    
    void update(final byte b) {
        if (this.first) {
            this.md.update(this.secret);
            this.md.update(this.pad1);
            this.first = false;
        }
        this.md.update(b);
    }
    
    void update(final byte[] array, final int n, final int n2) {
        if (this.first) {
            this.md.update(this.secret);
            this.md.update(this.pad1);
            this.first = false;
        }
        this.md.update(array, n, n2);
    }
    
    void update(final ByteBuffer byteBuffer) {
        if (this.first) {
            this.md.update(this.secret);
            this.md.update(this.pad1);
            this.first = false;
        }
        this.md.update(byteBuffer);
    }
    
    byte[] doFinal() {
        if (this.first) {
            this.md.update(this.secret);
            this.md.update(this.pad1);
        }
        else {
            this.first = true;
        }
        try {
            final byte[] digest = this.md.digest();
            this.md.update(this.secret);
            this.md.update(this.pad2);
            this.md.update(digest);
            this.md.digest(digest, 0, digest.length);
            return digest;
        }
        catch (final DigestException ex) {
            throw new ProviderException(ex);
        }
    }
    
    void reset() {
        if (!this.first) {
            this.md.reset();
            this.first = true;
        }
    }
    
    public static final class SslMacMD5 extends MacSpi
    {
        private final SslMacCore core;
        static final byte[] md5Pad1;
        static final byte[] md5Pad2;
        
        public SslMacMD5() throws NoSuchAlgorithmException {
            this.core = new SslMacCore("MD5", SslMacMD5.md5Pad1, SslMacMD5.md5Pad2);
        }
        
        @Override
        protected int engineGetMacLength() {
            return this.core.getDigestLength();
        }
        
        @Override
        protected void engineInit(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
            this.core.init(key, algorithmParameterSpec);
        }
        
        @Override
        protected void engineUpdate(final byte b) {
            this.core.update(b);
        }
        
        @Override
        protected void engineUpdate(final byte[] array, final int n, final int n2) {
            this.core.update(array, n, n2);
        }
        
        @Override
        protected void engineUpdate(final ByteBuffer byteBuffer) {
            this.core.update(byteBuffer);
        }
        
        @Override
        protected byte[] engineDoFinal() {
            return this.core.doFinal();
        }
        
        @Override
        protected void engineReset() {
            this.core.reset();
        }
        
        static {
            md5Pad1 = TlsPrfGenerator.genPad((byte)54, 48);
            md5Pad2 = TlsPrfGenerator.genPad((byte)92, 48);
        }
    }
    
    public static final class SslMacSHA1 extends MacSpi
    {
        private final SslMacCore core;
        static final byte[] shaPad1;
        static final byte[] shaPad2;
        
        public SslMacSHA1() throws NoSuchAlgorithmException {
            this.core = new SslMacCore("SHA", SslMacSHA1.shaPad1, SslMacSHA1.shaPad2);
        }
        
        @Override
        protected int engineGetMacLength() {
            return this.core.getDigestLength();
        }
        
        @Override
        protected void engineInit(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
            this.core.init(key, algorithmParameterSpec);
        }
        
        @Override
        protected void engineUpdate(final byte b) {
            this.core.update(b);
        }
        
        @Override
        protected void engineUpdate(final byte[] array, final int n, final int n2) {
            this.core.update(array, n, n2);
        }
        
        @Override
        protected void engineUpdate(final ByteBuffer byteBuffer) {
            this.core.update(byteBuffer);
        }
        
        @Override
        protected byte[] engineDoFinal() {
            return this.core.doFinal();
        }
        
        @Override
        protected void engineReset() {
            this.core.reset();
        }
        
        static {
            shaPad1 = TlsPrfGenerator.genPad((byte)54, 40);
            shaPad2 = TlsPrfGenerator.genPad((byte)92, 40);
        }
    }
}
