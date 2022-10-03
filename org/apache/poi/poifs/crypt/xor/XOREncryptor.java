package org.apache.poi.poifs.crypt.xor;

import com.zaxxer.sparsebits.SparseBitSet;
import org.apache.poi.EncryptedDocumentException;
import java.io.File;
import javax.crypto.Cipher;
import org.apache.poi.poifs.crypt.ChunkedCipherOutputStream;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.Encryptor;

public class XOREncryptor extends Encryptor
{
    protected XOREncryptor() {
    }
    
    protected XOREncryptor(final XOREncryptor other) {
        super(other);
    }
    
    @Override
    public void confirmPassword(final String password) {
        final int keyComp = CryptoFunctions.createXorKey1(password);
        final int verifierComp = CryptoFunctions.createXorVerifier1(password);
        final byte[] xorArray = CryptoFunctions.createXorArray1(password);
        final byte[] shortBuf = new byte[2];
        final XOREncryptionVerifier ver = (XOREncryptionVerifier)this.getEncryptionInfo().getVerifier();
        LittleEndian.putUShort(shortBuf, 0, keyComp);
        ver.setEncryptedKey(shortBuf);
        LittleEndian.putUShort(shortBuf, 0, verifierComp);
        ver.setEncryptedVerifier(shortBuf);
        this.setSecretKey(new SecretKeySpec(xorArray, "XOR"));
    }
    
    @Override
    public void confirmPassword(final String password, final byte[] keySpec, final byte[] keySalt, final byte[] verifier, final byte[] verifierSalt, final byte[] integritySalt) {
        this.confirmPassword(password);
    }
    
    @Override
    public OutputStream getDataStream(final DirectoryNode dir) throws IOException, GeneralSecurityException {
        return new XORCipherOutputStream(dir);
    }
    
    @Override
    public XORCipherOutputStream getDataStream(final OutputStream stream, final int initialOffset) throws IOException, GeneralSecurityException {
        return new XORCipherOutputStream(stream, initialOffset);
    }
    
    protected int getKeySizeInBytes() {
        return -1;
    }
    
    @Override
    public void setChunkSize(final int chunkSize) {
    }
    
    @Override
    public XOREncryptor copy() {
        return new XOREncryptor(this);
    }
    
    private class XORCipherOutputStream extends ChunkedCipherOutputStream
    {
        private int recordStart;
        private int recordEnd;
        
        public XORCipherOutputStream(final OutputStream stream, final int initialPos) throws IOException, GeneralSecurityException {
            super(stream, -1);
        }
        
        public XORCipherOutputStream(final DirectoryNode dir) throws IOException, GeneralSecurityException {
            super(dir, -1);
        }
        
        @Override
        protected Cipher initCipherForBlock(final Cipher cipher, final int block, final boolean lastChunk) throws GeneralSecurityException {
            return XORDecryptor.initCipherForBlock(cipher, block, XOREncryptor.this.getEncryptionInfo(), XOREncryptor.this.getSecretKey(), 1);
        }
        
        @Override
        protected void calculateChecksum(final File file, final int i) {
        }
        
        @Override
        protected void createEncryptionInfoEntry(final DirectoryNode dir, final File tmpFile) {
            throw new EncryptedDocumentException("createEncryptionInfoEntry not supported");
        }
        
        @Override
        public void setNextRecordSize(final int recordSize, final boolean isPlain) {
            if (this.recordEnd > 0 && !isPlain) {
                this.invokeCipher((int)this.getPos(), true);
            }
            this.recordStart = (int)this.getTotalPos() + 4;
            this.recordEnd = this.recordStart + recordSize;
        }
        
        @Override
        public void flush() throws IOException {
            this.setNextRecordSize(0, true);
            super.flush();
        }
        
        @Override
        protected int invokeCipher(final int posInChunk, final boolean doFinal) {
            if (posInChunk == 0) {
                return 0;
            }
            final int start = Math.max(posInChunk - (this.recordEnd - this.recordStart), 0);
            final SparseBitSet plainBytes = this.getPlainByteFlags();
            final byte[] xorArray = XOREncryptor.this.getEncryptionInfo().getEncryptor().getSecretKey().getEncoded();
            final byte[] chunk = this.getChunk();
            final byte[] plain = (byte[])(plainBytes.isEmpty() ? null : ((byte[])chunk.clone()));
            int xorArrayIndex = this.recordEnd + (start - this.recordStart);
            for (int i = start; i < posInChunk; ++i) {
                byte value = chunk[i];
                value ^= xorArray[xorArrayIndex++ & 0xF];
                value = this.rotateLeft(value, 5);
                chunk[i] = value;
            }
            if (plain != null) {
                for (int i = plainBytes.nextSetBit(start); i >= 0 && i < posInChunk; i = plainBytes.nextSetBit(i + 1)) {
                    chunk[i] = plain[i];
                }
            }
            return posInChunk;
        }
        
        private byte rotateLeft(final byte bits, final int shift) {
            return (byte)((bits & 0xFF) << shift | (bits & 0xFF) >>> 8 - shift);
        }
    }
}
