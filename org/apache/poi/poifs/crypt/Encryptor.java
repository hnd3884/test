package org.apache.poi.poifs.crypt;

import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import javax.crypto.SecretKey;
import org.apache.poi.common.usermodel.GenericRecord;

public abstract class Encryptor implements GenericRecord
{
    protected static final String DEFAULT_POIFS_ENTRY = "EncryptedPackage";
    private EncryptionInfo encryptionInfo;
    private SecretKey secretKey;
    
    protected Encryptor() {
    }
    
    protected Encryptor(final Encryptor other) {
        this.encryptionInfo = other.encryptionInfo;
        this.secretKey = other.secretKey;
    }
    
    public abstract OutputStream getDataStream(final DirectoryNode p0) throws IOException, GeneralSecurityException;
    
    public abstract void confirmPassword(final String p0, final byte[] p1, final byte[] p2, final byte[] p3, final byte[] p4, final byte[] p5);
    
    public abstract void confirmPassword(final String p0);
    
    public static Encryptor getInstance(final EncryptionInfo info) {
        return info.getEncryptor();
    }
    
    public OutputStream getDataStream(final POIFSFileSystem fs) throws IOException, GeneralSecurityException {
        return this.getDataStream(fs.getRoot());
    }
    
    public ChunkedCipherOutputStream getDataStream(final OutputStream stream, final int initialOffset) throws IOException, GeneralSecurityException {
        throw new EncryptedDocumentException("this decryptor doesn't support writing directly to a stream");
    }
    
    public SecretKey getSecretKey() {
        return this.secretKey;
    }
    
    public void setSecretKey(final SecretKey secretKey) {
        this.secretKey = secretKey;
    }
    
    public EncryptionInfo getEncryptionInfo() {
        return this.encryptionInfo;
    }
    
    public void setEncryptionInfo(final EncryptionInfo encryptionInfo) {
        this.encryptionInfo = encryptionInfo;
    }
    
    public void setChunkSize(final int chunkSize) {
        throw new EncryptedDocumentException("this decryptor doesn't support changing the chunk size");
    }
    
    public abstract Encryptor copy();
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("secretKey", this.secretKey::getEncoded);
    }
}
