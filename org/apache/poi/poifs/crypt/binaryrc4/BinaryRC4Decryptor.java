package org.apache.poi.poifs.crypt.binaryrc4;

import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.crypt.ChunkedCipherInputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.util.StringUtil;
import org.apache.poi.poifs.crypt.EncryptionHeader;
import java.security.Key;
import org.apache.poi.poifs.crypt.ChainingMode;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import java.security.MessageDigest;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import javax.crypto.SecretKey;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import java.security.GeneralSecurityException;
import org.apache.poi.EncryptedDocumentException;
import java.util.Arrays;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import javax.crypto.Cipher;
import org.apache.poi.poifs.crypt.Decryptor;

public class BinaryRC4Decryptor extends Decryptor
{
    private long length;
    private int chunkSize;
    
    protected BinaryRC4Decryptor() {
        this.length = -1L;
        this.chunkSize = 512;
    }
    
    protected BinaryRC4Decryptor(final BinaryRC4Decryptor other) {
        super(other);
        this.length = -1L;
        this.chunkSize = 512;
        this.length = other.length;
        this.chunkSize = other.chunkSize;
    }
    
    @Override
    public boolean verifyPassword(final String password) {
        final EncryptionVerifier ver = this.getEncryptionInfo().getVerifier();
        final SecretKey skey = generateSecretKey(password, ver);
        try {
            final Cipher cipher = initCipherForBlock(null, 0, this.getEncryptionInfo(), skey, 2);
            final byte[] encryptedVerifier = ver.getEncryptedVerifier();
            final byte[] verifier = new byte[encryptedVerifier.length];
            cipher.update(encryptedVerifier, 0, encryptedVerifier.length, verifier);
            this.setVerifier(verifier);
            final byte[] encryptedVerifierHash = ver.getEncryptedVerifierHash();
            final byte[] verifierHash = cipher.doFinal(encryptedVerifierHash);
            final HashAlgorithm hashAlgo = ver.getHashAlgorithm();
            final MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
            final byte[] calcVerifierHash = hashAlg.digest(verifier);
            if (Arrays.equals(calcVerifierHash, verifierHash)) {
                this.setSecretKey(skey);
                return true;
            }
        }
        catch (final GeneralSecurityException e) {
            throw new EncryptedDocumentException(e);
        }
        return false;
    }
    
    @Override
    public Cipher initCipherForBlock(final Cipher cipher, final int block) throws GeneralSecurityException {
        return initCipherForBlock(cipher, block, this.getEncryptionInfo(), this.getSecretKey(), 2);
    }
    
    protected static Cipher initCipherForBlock(Cipher cipher, final int block, final EncryptionInfo encryptionInfo, final SecretKey skey, final int encryptMode) throws GeneralSecurityException {
        final EncryptionVerifier ver = encryptionInfo.getVerifier();
        final HashAlgorithm hashAlgo = ver.getHashAlgorithm();
        final byte[] blockKey = new byte[4];
        LittleEndian.putUInt(blockKey, 0, block);
        final byte[] encKey = CryptoFunctions.generateKey(skey.getEncoded(), hashAlgo, blockKey, 16);
        final SecretKey key = new SecretKeySpec(encKey, skey.getAlgorithm());
        if (cipher == null) {
            final EncryptionHeader em = encryptionInfo.getHeader();
            cipher = CryptoFunctions.getCipher(key, em.getCipherAlgorithm(), null, null, encryptMode);
        }
        else {
            cipher.init(encryptMode, key);
        }
        return cipher;
    }
    
    protected static SecretKey generateSecretKey(String password, final EncryptionVerifier ver) {
        if (password.length() > 255) {
            password = password.substring(0, 255);
        }
        final HashAlgorithm hashAlgo = ver.getHashAlgorithm();
        final MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
        byte[] hash = hashAlg.digest(StringUtil.getToUnicodeLE(password));
        final byte[] salt = ver.getSalt();
        hashAlg.reset();
        for (int i = 0; i < 16; ++i) {
            hashAlg.update(hash, 0, 5);
            hashAlg.update(salt);
        }
        hash = new byte[5];
        System.arraycopy(hashAlg.digest(), 0, hash, 0, 5);
        return new SecretKeySpec(hash, ver.getCipherAlgorithm().jceId);
    }
    
    @Override
    public ChunkedCipherInputStream getDataStream(final DirectoryNode dir) throws IOException, GeneralSecurityException {
        final DocumentInputStream dis = dir.createDocumentInputStream("EncryptedPackage");
        this.length = dis.readLong();
        return new BinaryRC4CipherInputStream(dis, this.length);
    }
    
    @Override
    public InputStream getDataStream(final InputStream stream, final int size, final int initialPos) throws IOException, GeneralSecurityException {
        return new BinaryRC4CipherInputStream(stream, size, initialPos);
    }
    
    @Override
    public long getLength() {
        if (this.length == -1L) {
            throw new IllegalStateException("Decryptor.getDataStream() was not called");
        }
        return this.length;
    }
    
    @Override
    public void setChunkSize(final int chunkSize) {
        this.chunkSize = chunkSize;
    }
    
    @Override
    public BinaryRC4Decryptor copy() {
        return new BinaryRC4Decryptor(this);
    }
    
    private class BinaryRC4CipherInputStream extends ChunkedCipherInputStream
    {
        @Override
        protected Cipher initCipherForBlock(final Cipher existing, final int block) throws GeneralSecurityException {
            return BinaryRC4Decryptor.this.initCipherForBlock(existing, block);
        }
        
        public BinaryRC4CipherInputStream(final DocumentInputStream stream, final long size) throws GeneralSecurityException {
            super(stream, size, BinaryRC4Decryptor.this.chunkSize);
        }
        
        public BinaryRC4CipherInputStream(final InputStream stream, final int size, final int initialPos) throws GeneralSecurityException {
            super(stream, size, BinaryRC4Decryptor.this.chunkSize, initialPos);
        }
    }
}
