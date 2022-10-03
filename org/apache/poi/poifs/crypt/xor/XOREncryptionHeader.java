package org.apache.poi.poifs.crypt.xor;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.poifs.crypt.EncryptionHeader;

public class XOREncryptionHeader extends EncryptionHeader implements EncryptionRecord
{
    protected XOREncryptionHeader() {
    }
    
    protected XOREncryptionHeader(final XOREncryptionHeader other) {
        super(other);
    }
    
    @Override
    public void write(final LittleEndianByteArrayOutputStream leos) {
    }
    
    @Override
    public XOREncryptionHeader copy() {
        return new XOREncryptionHeader(this);
    }
}
