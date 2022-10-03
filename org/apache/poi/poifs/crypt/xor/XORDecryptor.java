package org.apache.poi.poifs.crypt.xor;

import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChunkedCipherInputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.poifs.crypt.Decryptor;

public class XORDecryptor extends Decryptor
{
    private long length;
    private int chunkSize;
    
    protected XORDecryptor() {
        this.length = -1L;
        this.chunkSize = 512;
    }
    
    protected XORDecryptor(final XORDecryptor other) {
        super(other);
        this.length = -1L;
        this.chunkSize = 512;
        this.length = other.length;
        this.chunkSize = other.chunkSize;
    }
    
    @Override
    public boolean verifyPassword(final String password) {
        final XOREncryptionVerifier ver = (XOREncryptionVerifier)this.getEncryptionInfo().getVerifier();
        final int keyVer = LittleEndian.getUShort(ver.getEncryptedKey());
        final int verifierVer = LittleEndian.getUShort(ver.getEncryptedVerifier());
        final int keyComp = CryptoFunctions.createXorKey1(password);
        final int verifierComp = CryptoFunctions.createXorVerifier1(password);
        if (keyVer == keyComp && verifierVer == verifierComp) {
            final byte[] xorArray = CryptoFunctions.createXorArray1(password);
            this.setSecretKey(new SecretKeySpec(xorArray, "XOR"));
            return true;
        }
        return false;
    }
    
    @Override
    public Cipher initCipherForBlock(final Cipher cipher, final int block) throws GeneralSecurityException {
        return null;
    }
    
    protected static Cipher initCipherForBlock(final Cipher cipher, final int block, final EncryptionInfo encryptionInfo, final SecretKey skey, final int encryptMode) throws GeneralSecurityException {
        return null;
    }
    
    @Override
    public ChunkedCipherInputStream getDataStream(final DirectoryNode dir) throws IOException, GeneralSecurityException {
        throw new EncryptedDocumentException("not supported");
    }
    
    @Override
    public InputStream getDataStream(final InputStream stream, final int size, final int initialPos) throws IOException, GeneralSecurityException {
        return new XORCipherInputStream(stream, initialPos);
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
    public XORDecryptor copy() {
        return new XORDecryptor(this);
    }
    
    private class XORCipherInputStream extends ChunkedCipherInputStream
    {
        private final int initialOffset;
        private int recordStart;
        private int recordEnd;
        
        public XORCipherInputStream(final InputStream stream, final int initialPos) throws GeneralSecurityException {
            super(stream, 2147483647L, XORDecryptor.this.chunkSize);
            this.initialOffset = initialPos;
        }
        
        @Override
        protected Cipher initCipherForBlock(final Cipher existing, final int block) throws GeneralSecurityException {
            return XORDecryptor.this.initCipherForBlock(existing, block);
        }
        
        @Override
        protected int invokeCipher(final int totalBytes, final boolean doFinal) {
            final int pos = (int)this.getPos();
            final byte[] xorArray = XORDecryptor.this.getEncryptionInfo().getDecryptor().getSecretKey().getEncoded();
            final byte[] chunk = this.getChunk();
            final byte[] plain = this.getPlain();
            final int posInChunk = pos & this.getChunkMask();
            final int xorArrayIndex = this.initialOffset + this.recordEnd + (pos - this.recordStart);
            for (int i = 0; pos + i < this.recordEnd && i < totalBytes; ++i) {
                byte value = plain[posInChunk + i];
                value = this.rotateLeft(value, 3);
                value ^= xorArray[xorArrayIndex + i & 0xF];
                chunk[posInChunk + i] = value;
            }
            return totalBytes;
        }
        
        private byte rotateLeft(final byte bits, final int shift) {
            return (byte)((bits & 0xFF) << shift | (bits & 0xFF) >>> 8 - shift);
        }
        
        @Override
        public void setNextRecordSize(final int recordSize) {
            final int pos = (int)this.getPos();
            final byte[] chunk = this.getChunk();
            final int chunkMask = this.getChunkMask();
            this.recordStart = pos;
            this.recordEnd = this.recordStart + recordSize;
            final int nextBytes = Math.min(recordSize, chunk.length - (pos & chunkMask));
            this.invokeCipher(nextBytes, true);
        }
    }
}
