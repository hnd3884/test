package org.apache.poi.poifs.crypt.standard;

import org.apache.poi.util.LittleEndianByteArrayOutputStream;

public interface EncryptionRecord
{
    void write(final LittleEndianByteArrayOutputStream p0);
}
