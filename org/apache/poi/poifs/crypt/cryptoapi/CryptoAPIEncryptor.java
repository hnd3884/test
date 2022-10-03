package org.apache.poi.poifs.crypt.cryptoapi;

import java.io.File;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import java.util.Iterator;
import java.util.List;
import java.io.ByteArrayInputStream;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.LittleEndian;
import java.io.InputStream;
import org.apache.poi.util.IOUtils;
import org.apache.poi.poifs.filesystem.Entry;
import java.util.ArrayList;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.OutputStream;
import java.io.IOException;
import org.apache.poi.poifs.crypt.ChunkedCipherOutputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import java.security.MessageDigest;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import javax.crypto.Cipher;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import java.util.Random;
import java.security.SecureRandom;
import org.apache.poi.poifs.crypt.Encryptor;

public class CryptoAPIEncryptor extends Encryptor
{
    private int chunkSize;
    
    CryptoAPIEncryptor() {
        this.chunkSize = 512;
    }
    
    CryptoAPIEncryptor(final CryptoAPIEncryptor other) {
        super(other);
        this.chunkSize = 512;
        this.chunkSize = other.chunkSize;
    }
    
    @Override
    public void confirmPassword(final String password) {
        final Random r = new SecureRandom();
        final byte[] salt = new byte[16];
        final byte[] verifier = new byte[16];
        r.nextBytes(salt);
        r.nextBytes(verifier);
        this.confirmPassword(password, null, null, verifier, salt, null);
    }
    
    @Override
    public void confirmPassword(final String password, final byte[] keySpec, final byte[] keySalt, final byte[] verifier, final byte[] verifierSalt, final byte[] integritySalt) {
        assert verifier != null && verifierSalt != null;
        final CryptoAPIEncryptionVerifier ver = (CryptoAPIEncryptionVerifier)this.getEncryptionInfo().getVerifier();
        ver.setSalt(verifierSalt);
        final SecretKey skey = CryptoAPIDecryptor.generateSecretKey(password, ver);
        this.setSecretKey(skey);
        try {
            final Cipher cipher = this.initCipherForBlock(null, 0);
            final byte[] encryptedVerifier = new byte[verifier.length];
            cipher.update(verifier, 0, verifier.length, encryptedVerifier);
            ver.setEncryptedVerifier(encryptedVerifier);
            final HashAlgorithm hashAlgo = ver.getHashAlgorithm();
            final MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
            final byte[] calcVerifierHash = hashAlg.digest(verifier);
            final byte[] encryptedVerifierHash = cipher.doFinal(calcVerifierHash);
            ver.setEncryptedVerifierHash(encryptedVerifierHash);
        }
        catch (final GeneralSecurityException e) {
            throw new EncryptedDocumentException("Password confirmation failed", e);
        }
    }
    
    public Cipher initCipherForBlock(final Cipher cipher, final int block) throws GeneralSecurityException {
        return CryptoAPIDecryptor.initCipherForBlock(cipher, block, this.getEncryptionInfo(), this.getSecretKey(), 1);
    }
    
    @Override
    public ChunkedCipherOutputStream getDataStream(final DirectoryNode dir) throws IOException {
        throw new IOException("not supported");
    }
    
    @Override
    public CryptoAPICipherOutputStream getDataStream(final OutputStream stream, final int initialOffset) throws IOException, GeneralSecurityException {
        return new CryptoAPICipherOutputStream(stream);
    }
    
    public void setSummaryEntries(final DirectoryNode dir, final String encryptedStream, final POIFSFileSystem entries) throws IOException, GeneralSecurityException {
        final CryptoAPIDocumentOutputStream bos = new CryptoAPIDocumentOutputStream(this);
        final byte[] buf = new byte[8];
        bos.write(buf, 0, 8);
        final List<CryptoAPIDecryptor.StreamDescriptorEntry> descList = new ArrayList<CryptoAPIDecryptor.StreamDescriptorEntry>();
        int block = 0;
        for (final Entry entry : entries.getRoot()) {
            if (entry.isDirectoryEntry()) {
                continue;
            }
            final CryptoAPIDecryptor.StreamDescriptorEntry descEntry = new CryptoAPIDecryptor.StreamDescriptorEntry();
            descEntry.block = block;
            descEntry.streamOffset = bos.size();
            descEntry.streamName = entry.getName();
            descEntry.flags = CryptoAPIDecryptor.StreamDescriptorEntry.flagStream.setValue(0, 1);
            descEntry.reserved2 = 0;
            bos.setBlock(block);
            final DocumentInputStream dis = dir.createDocumentInputStream(entry);
            IOUtils.copy(dis, bos);
            dis.close();
            descEntry.streamSize = bos.size() - descEntry.streamOffset;
            descList.add(descEntry);
            ++block;
        }
        final int streamDescriptorArrayOffset = bos.size();
        bos.setBlock(0);
        LittleEndian.putUInt(buf, 0, descList.size());
        bos.write(buf, 0, 4);
        for (final CryptoAPIDecryptor.StreamDescriptorEntry sde : descList) {
            LittleEndian.putUInt(buf, 0, sde.streamOffset);
            bos.write(buf, 0, 4);
            LittleEndian.putUInt(buf, 0, sde.streamSize);
            bos.write(buf, 0, 4);
            LittleEndian.putUShort(buf, 0, sde.block);
            bos.write(buf, 0, 2);
            LittleEndian.putUByte(buf, 0, (short)sde.streamName.length());
            bos.write(buf, 0, 1);
            LittleEndian.putUByte(buf, 0, (short)sde.flags);
            bos.write(buf, 0, 1);
            LittleEndian.putUInt(buf, 0, sde.reserved2);
            bos.write(buf, 0, 4);
            final byte[] nameBytes = StringUtil.getToUnicodeLE(sde.streamName);
            bos.write(nameBytes, 0, nameBytes.length);
            LittleEndian.putShort(buf, 0, (short)0);
            bos.write(buf, 0, 2);
        }
        final int savedSize = bos.size();
        final int streamDescriptorArraySize = savedSize - streamDescriptorArrayOffset;
        LittleEndian.putUInt(buf, 0, streamDescriptorArrayOffset);
        LittleEndian.putUInt(buf, 4, streamDescriptorArraySize);
        bos.reset();
        bos.setBlock(0);
        bos.write(buf, 0, 8);
        bos.setSize(savedSize);
        dir.createDocument(encryptedStream, new ByteArrayInputStream(bos.getBuf(), 0, savedSize));
    }
    
    @Override
    public void setChunkSize(final int chunkSize) {
        this.chunkSize = chunkSize;
    }
    
    @Override
    public CryptoAPIEncryptor copy() {
        return new CryptoAPIEncryptor(this);
    }
    
    protected class CryptoAPICipherOutputStream extends ChunkedCipherOutputStream
    {
        @Override
        protected Cipher initCipherForBlock(final Cipher cipher, final int block, final boolean lastChunk) throws IOException, GeneralSecurityException {
            this.flush();
            return this.initCipherForBlockNoFlush(cipher, block, lastChunk);
        }
        
        @Override
        protected Cipher initCipherForBlockNoFlush(final Cipher existing, final int block, final boolean lastChunk) throws GeneralSecurityException {
            final EncryptionInfo ei = CryptoAPIEncryptor.this.getEncryptionInfo();
            final SecretKey sk = CryptoAPIEncryptor.this.getSecretKey();
            return CryptoAPIDecryptor.initCipherForBlock(existing, block, ei, sk, 1);
        }
        
        @Override
        protected void calculateChecksum(final File file, final int i) {
        }
        
        @Override
        protected void createEncryptionInfoEntry(final DirectoryNode dir, final File tmpFile) {
            throw new EncryptedDocumentException("createEncryptionInfoEntry not supported");
        }
        
        CryptoAPICipherOutputStream(final OutputStream stream) throws IOException, GeneralSecurityException {
            super(stream, CryptoAPIEncryptor.this.chunkSize);
        }
        
        @Override
        public void flush() throws IOException {
            this.writeChunk(false);
            super.flush();
        }
    }
}
