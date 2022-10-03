package org.apache.poi.poifs.crypt.agile;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import com.microsoft.schemas.office.x2006.encryption.CTDataIntegrity;
import com.microsoft.schemas.office.x2006.encryption.CTKeyData;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.EncryptedDocumentException;
import com.microsoft.schemas.office.x2006.encryption.EncryptionDocument;
import org.apache.poi.poifs.crypt.EncryptionHeader;

public class AgileEncryptionHeader extends EncryptionHeader
{
    private byte[] encryptedHmacKey;
    private byte[] encryptedHmacValue;
    
    public AgileEncryptionHeader(final String descriptor) {
        this(AgileEncryptionInfoBuilder.parseDescriptor(descriptor));
    }
    
    public AgileEncryptionHeader(final AgileEncryptionHeader other) {
        super((EncryptionHeader)other);
        this.encryptedHmacKey = (byte[])((other.encryptedHmacKey == null) ? null : ((byte[])other.encryptedHmacKey.clone()));
        this.encryptedHmacValue = (byte[])((other.encryptedHmacValue == null) ? null : ((byte[])other.encryptedHmacValue.clone()));
    }
    
    protected AgileEncryptionHeader(final EncryptionDocument ed) {
        CTKeyData keyData;
        try {
            keyData = ed.getEncryption().getKeyData();
            if (keyData == null) {
                throw new NullPointerException("keyData not set");
            }
        }
        catch (final Exception e) {
            throw new EncryptedDocumentException("Unable to parse keyData");
        }
        final int keyBits = (int)keyData.getKeyBits();
        final CipherAlgorithm ca = CipherAlgorithm.fromXmlId(keyData.getCipherAlgorithm().toString(), keyBits);
        this.setCipherAlgorithm(ca);
        this.setCipherProvider(ca.provider);
        this.setKeySize(keyBits);
        this.setFlags(0);
        this.setSizeExtra(0);
        this.setCspName((String)null);
        this.setBlockSize(keyData.getBlockSize());
        switch (keyData.getCipherChaining().intValue()) {
            case 1: {
                this.setChainingMode(ChainingMode.cbc);
                break;
            }
            case 2: {
                this.setChainingMode(ChainingMode.cfb);
                break;
            }
            default: {
                throw new EncryptedDocumentException("Unsupported chaining mode - " + keyData.getCipherChaining());
            }
        }
        final int hashSize = keyData.getHashSize();
        final HashAlgorithm ha = HashAlgorithm.fromEcmaId(keyData.getHashAlgorithm().toString());
        this.setHashAlgorithm(ha);
        if (this.getHashAlgorithm().hashSize != hashSize) {
            throw new EncryptedDocumentException("Unsupported hash algorithm: " + keyData.getHashAlgorithm() + " @ " + hashSize + " bytes");
        }
        final int saltLength = keyData.getSaltSize();
        this.setKeySalt(keyData.getSaltValue());
        if (this.getKeySalt().length != saltLength) {
            throw new EncryptedDocumentException("Invalid salt length");
        }
        final CTDataIntegrity di = ed.getEncryption().getDataIntegrity();
        this.setEncryptedHmacKey(di.getEncryptedHmacKey());
        this.setEncryptedHmacValue(di.getEncryptedHmacValue());
    }
    
    public AgileEncryptionHeader(final CipherAlgorithm algorithm, final HashAlgorithm hashAlgorithm, final int keyBits, final int blockSize, final ChainingMode chainingMode) {
        this.setCipherAlgorithm(algorithm);
        this.setHashAlgorithm(hashAlgorithm);
        this.setKeySize(keyBits);
        this.setBlockSize(blockSize);
        this.setChainingMode(chainingMode);
    }
    
    protected void setKeySalt(final byte[] salt) {
        if (salt == null || salt.length != this.getBlockSize()) {
            throw new EncryptedDocumentException("invalid verifier salt");
        }
        super.setKeySalt(salt);
    }
    
    public byte[] getEncryptedHmacKey() {
        return this.encryptedHmacKey;
    }
    
    protected void setEncryptedHmacKey(final byte[] encryptedHmacKey) {
        this.encryptedHmacKey = (byte[])((encryptedHmacKey == null) ? null : ((byte[])encryptedHmacKey.clone()));
    }
    
    public byte[] getEncryptedHmacValue() {
        return this.encryptedHmacValue;
    }
    
    protected void setEncryptedHmacValue(final byte[] encryptedHmacValue) {
        this.encryptedHmacValue = (byte[])((encryptedHmacValue == null) ? null : ((byte[])encryptedHmacValue.clone()));
    }
    
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "encryptedHmacKey", (Supplier)this::getEncryptedHmacKey, "encryptedHmacValue", (Supplier)this::getEncryptedHmacValue);
    }
    
    public AgileEncryptionHeader copy() {
        return new AgileEncryptionHeader(this);
    }
}
