package org.apache.poi.poifs.crypt.binaryrc4;

import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import java.io.IOException;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import org.apache.poi.poifs.crypt.EncryptionHeader;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionInfoBuilder;

public class BinaryRC4EncryptionInfoBuilder implements EncryptionInfoBuilder
{
    @Override
    public void initialize(final EncryptionInfo info, final LittleEndianInput dis) throws IOException {
        final int vMajor = info.getVersionMajor();
        final int vMinor = info.getVersionMinor();
        assert vMajor == 1 && vMinor == 1;
        info.setHeader(new BinaryRC4EncryptionHeader());
        info.setVerifier(new BinaryRC4EncryptionVerifier(dis));
        final Decryptor dec = new BinaryRC4Decryptor();
        dec.setEncryptionInfo(info);
        info.setDecryptor(dec);
        final Encryptor enc = new BinaryRC4Encryptor();
        enc.setEncryptionInfo(info);
        info.setEncryptor(enc);
    }
    
    @Override
    public void initialize(final EncryptionInfo info, final CipherAlgorithm cipherAlgorithm, final HashAlgorithm hashAlgorithm, final int keyBits, final int blockSize, final ChainingMode chainingMode) {
        info.setHeader(new BinaryRC4EncryptionHeader());
        info.setVerifier(new BinaryRC4EncryptionVerifier());
        final Decryptor dec = new BinaryRC4Decryptor();
        dec.setEncryptionInfo(info);
        info.setDecryptor(dec);
        final Encryptor enc = new BinaryRC4Encryptor();
        enc.setEncryptionInfo(info);
        info.setEncryptor(enc);
    }
}
