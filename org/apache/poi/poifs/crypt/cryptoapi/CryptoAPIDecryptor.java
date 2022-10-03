package org.apache.poi.poifs.crypt.cryptoapi;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.BitField;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import java.io.Closeable;
import org.apache.poi.util.BoundedInputStream;
import org.apache.poi.util.LittleEndianInput;
import java.io.EOFException;
import org.apache.poi.util.LittleEndianInputStream;
import java.io.OutputStream;
import org.apache.poi.util.IOUtils;
import java.io.ByteArrayOutputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.InputStream;
import java.io.IOException;
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

public class CryptoAPIDecryptor extends Decryptor
{
    private long length;
    private int chunkSize;
    
    protected CryptoAPIDecryptor() {
        this.length = -1L;
        this.chunkSize = -1;
    }
    
    protected CryptoAPIDecryptor(final CryptoAPIDecryptor other) {
        super(other);
        this.length = -1L;
        this.chunkSize = -1;
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
        final EncryptionInfo ei = this.getEncryptionInfo();
        final SecretKey sk = this.getSecretKey();
        return initCipherForBlock(cipher, block, ei, sk, 2);
    }
    
    protected static Cipher initCipherForBlock(Cipher cipher, final int block, final EncryptionInfo encryptionInfo, final SecretKey skey, final int encryptMode) throws GeneralSecurityException {
        final EncryptionVerifier ver = encryptionInfo.getVerifier();
        final HashAlgorithm hashAlgo = ver.getHashAlgorithm();
        final byte[] blockKey = new byte[4];
        LittleEndian.putUInt(blockKey, 0, block);
        final MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
        hashAlg.update(skey.getEncoded());
        byte[] encKey = hashAlg.digest(blockKey);
        final EncryptionHeader header = encryptionInfo.getHeader();
        final int keyBits = header.getKeySize();
        encKey = CryptoFunctions.getBlock0(encKey, keyBits / 8);
        if (keyBits == 40) {
            encKey = CryptoFunctions.getBlock0(encKey, 16);
        }
        final SecretKey key = new SecretKeySpec(encKey, skey.getAlgorithm());
        if (cipher == null) {
            cipher = CryptoFunctions.getCipher(key, header.getCipherAlgorithm(), null, null, encryptMode);
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
        hashAlg.update(ver.getSalt());
        final byte[] hash = hashAlg.digest(StringUtil.getToUnicodeLE(password));
        return new SecretKeySpec(hash, ver.getCipherAlgorithm().jceId);
    }
    
    @Override
    public ChunkedCipherInputStream getDataStream(final DirectoryNode dir) throws IOException, GeneralSecurityException {
        throw new IOException("not supported");
    }
    
    @Override
    public ChunkedCipherInputStream getDataStream(final InputStream stream, final int size, final int initialPos) throws IOException, GeneralSecurityException {
        return new CryptoAPICipherInputStream(stream, size, initialPos);
    }
    
    public POIFSFileSystem getSummaryEntries(final DirectoryNode root, final String encryptedStream) throws IOException, GeneralSecurityException {
        final DocumentNode es = (DocumentNode)root.getEntry(encryptedStream);
        final DocumentInputStream dis = root.createDocumentInputStream(es);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(dis, bos);
        dis.close();
        final CryptoAPIDocumentInputStream sbis = new CryptoAPIDocumentInputStream(this, bos.toByteArray());
        final LittleEndianInputStream leis = new LittleEndianInputStream(sbis);
        POIFSFileSystem fsOut = null;
        try {
            final int streamDescriptorArrayOffset = (int)leis.readUInt();
            leis.readUInt();
            final long skipN = streamDescriptorArrayOffset - 8L;
            if (sbis.skip(skipN) < skipN) {
                throw new EOFException("buffer underrun");
            }
            sbis.setBlock(0);
            final int encryptedStreamDescriptorCount = (int)leis.readUInt();
            final StreamDescriptorEntry[] entries = new StreamDescriptorEntry[encryptedStreamDescriptorCount];
            for (int i = 0; i < encryptedStreamDescriptorCount; ++i) {
                final StreamDescriptorEntry entry = new StreamDescriptorEntry();
                entries[i] = entry;
                entry.streamOffset = (int)leis.readUInt();
                entry.streamSize = (int)leis.readUInt();
                entry.block = leis.readUShort();
                final int nameSize = leis.readUByte();
                entry.flags = leis.readUByte();
                entry.reserved2 = leis.readInt();
                entry.streamName = StringUtil.readUnicodeLE(leis, nameSize);
                leis.readShort();
                assert entry.streamName.length() == nameSize;
            }
            fsOut = new POIFSFileSystem();
            for (final StreamDescriptorEntry entry2 : entries) {
                sbis.seek(entry2.streamOffset);
                sbis.setBlock(entry2.block);
                final InputStream is = new BoundedInputStream(sbis, entry2.streamSize);
                fsOut.createDocument(is, entry2.streamName);
                is.close();
            }
        }
        catch (final Exception e) {
            IOUtils.closeQuietly(fsOut);
            if (e instanceof GeneralSecurityException) {
                throw (GeneralSecurityException)e;
            }
            if (e instanceof IOException) {
                throw (IOException)e;
            }
            throw new IOException("summary entries can't be read", e);
        }
        finally {
            IOUtils.closeQuietly(leis);
            IOUtils.closeQuietly(sbis);
        }
        return fsOut;
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
    public CryptoAPIDecryptor copy() {
        return new CryptoAPIDecryptor(this);
    }
    
    static class StreamDescriptorEntry
    {
        static final BitField flagStream;
        int streamOffset;
        int streamSize;
        int block;
        int flags;
        int reserved2;
        String streamName;
        
        static {
            flagStream = BitFieldFactory.getInstance(1);
        }
    }
    
    private class CryptoAPICipherInputStream extends ChunkedCipherInputStream
    {
        @Override
        protected Cipher initCipherForBlock(final Cipher existing, final int block) throws GeneralSecurityException {
            return CryptoAPIDecryptor.this.initCipherForBlock(existing, block);
        }
        
        public CryptoAPICipherInputStream(final InputStream stream, final long size, final int initialPos) throws GeneralSecurityException {
            super(stream, size, CryptoAPIDecryptor.this.chunkSize, initialPos);
        }
    }
}
