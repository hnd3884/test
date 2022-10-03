package org.apache.poi.poifs.crypt;

import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import javax.crypto.Cipher;
import org.apache.poi.EncryptedDocumentException;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import javax.crypto.SecretKey;
import org.apache.poi.common.usermodel.GenericRecord;

public abstract class Decryptor implements GenericRecord
{
    public static final String DEFAULT_PASSWORD = "VelvetSweatshop";
    public static final String DEFAULT_POIFS_ENTRY = "EncryptedPackage";
    protected EncryptionInfo encryptionInfo;
    private SecretKey secretKey;
    private byte[] verifier;
    private byte[] integrityHmacKey;
    private byte[] integrityHmacValue;
    
    protected Decryptor() {
    }
    
    protected Decryptor(final Decryptor other) {
        this.encryptionInfo = other.encryptionInfo;
        this.secretKey = other.secretKey;
        this.verifier = (byte[])((other.verifier == null) ? null : ((byte[])other.verifier.clone()));
        this.integrityHmacKey = (byte[])((other.integrityHmacKey == null) ? null : ((byte[])other.integrityHmacKey.clone()));
        this.integrityHmacValue = (byte[])((other.integrityHmacValue == null) ? null : ((byte[])other.integrityHmacValue.clone()));
    }
    
    public abstract InputStream getDataStream(final DirectoryNode p0) throws IOException, GeneralSecurityException;
    
    public InputStream getDataStream(final InputStream stream, final int size, final int initialPos) throws IOException, GeneralSecurityException {
        throw new EncryptedDocumentException("this decryptor doesn't support reading from a stream");
    }
    
    public void setChunkSize(final int chunkSize) {
        throw new EncryptedDocumentException("this decryptor doesn't support changing the chunk size");
    }
    
    public Cipher initCipherForBlock(final Cipher cipher, final int block) throws GeneralSecurityException {
        throw new EncryptedDocumentException("this decryptor doesn't support initCipherForBlock");
    }
    
    public abstract boolean verifyPassword(final String p0) throws GeneralSecurityException;
    
    public abstract long getLength();
    
    public static Decryptor getInstance(final EncryptionInfo info) {
        final Decryptor d = info.getDecryptor();
        if (d == null) {
            throw new EncryptedDocumentException("Unsupported version");
        }
        return d;
    }
    
    public InputStream getDataStream(final POIFSFileSystem fs) throws IOException, GeneralSecurityException {
        return this.getDataStream(fs.getRoot());
    }
    
    public byte[] getVerifier() {
        return this.verifier;
    }
    
    public SecretKey getSecretKey() {
        return this.secretKey;
    }
    
    public byte[] getIntegrityHmacKey() {
        return this.integrityHmacKey;
    }
    
    public byte[] getIntegrityHmacValue() {
        return this.integrityHmacValue;
    }
    
    protected void setSecretKey(final SecretKey secretKey) {
        this.secretKey = secretKey;
    }
    
    protected void setVerifier(final byte[] verifier) {
        this.verifier = (byte[])((verifier == null) ? null : ((byte[])verifier.clone()));
    }
    
    protected void setIntegrityHmacKey(final byte[] integrityHmacKey) {
        this.integrityHmacKey = (byte[])((integrityHmacKey == null) ? null : ((byte[])integrityHmacKey.clone()));
    }
    
    protected void setIntegrityHmacValue(final byte[] integrityHmacValue) {
        this.integrityHmacValue = (byte[])((integrityHmacValue == null) ? null : ((byte[])integrityHmacValue.clone()));
    }
    
    protected int getBlockSizeInBytes() {
        return this.encryptionInfo.getHeader().getBlockSize();
    }
    
    protected int getKeySizeInBytes() {
        return this.encryptionInfo.getHeader().getKeySize() / 8;
    }
    
    public EncryptionInfo getEncryptionInfo() {
        return this.encryptionInfo;
    }
    
    public void setEncryptionInfo(final EncryptionInfo encryptionInfo) {
        this.encryptionInfo = encryptionInfo;
    }
    
    public abstract Decryptor copy();
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("secretKey", this.secretKey::getEncoded, "verifier", this::getVerifier, "integrityHmacKey", this::getIntegrityHmacKey, "integrityHmacValue", this::getIntegrityHmacValue);
    }
}
