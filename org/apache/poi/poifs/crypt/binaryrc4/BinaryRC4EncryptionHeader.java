package org.apache.poi.poifs.crypt.binaryrc4;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.CipherProvider;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.poifs.crypt.EncryptionHeader;

public class BinaryRC4EncryptionHeader extends EncryptionHeader implements EncryptionRecord
{
    protected BinaryRC4EncryptionHeader() {
        this.setCipherAlgorithm(CipherAlgorithm.rc4);
        this.setKeySize(40);
        this.setBlockSize(-1);
        this.setCipherProvider(CipherProvider.rc4);
        this.setHashAlgorithm(HashAlgorithm.md5);
        this.setSizeExtra(0);
        this.setFlags(0);
        this.setCspName("");
        this.setChainingMode(null);
    }
    
    protected BinaryRC4EncryptionHeader(final BinaryRC4EncryptionHeader other) {
        super(other);
    }
    
    @Override
    public void write(final LittleEndianByteArrayOutputStream littleendianbytearrayoutputstream) {
    }
    
    @Override
    public BinaryRC4EncryptionHeader copy() {
        return new BinaryRC4EncryptionHeader(this);
    }
}
