package org.apache.poi.poifs.crypt.xor;

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

public class XOREncryptionInfoBuilder implements EncryptionInfoBuilder
{
    @Override
    public void initialize(final EncryptionInfo info, final LittleEndianInput dis) throws IOException {
        info.setHeader(new XOREncryptionHeader());
        info.setVerifier(new XOREncryptionVerifier(dis));
        final Decryptor dec = new XORDecryptor();
        dec.setEncryptionInfo(info);
        info.setDecryptor(dec);
        final Encryptor enc = new XOREncryptor();
        enc.setEncryptionInfo(info);
        info.setEncryptor(enc);
    }
    
    @Override
    public void initialize(final EncryptionInfo info, final CipherAlgorithm cipherAlgorithm, final HashAlgorithm hashAlgorithm, final int keyBits, final int blockSize, final ChainingMode chainingMode) {
        info.setHeader(new XOREncryptionHeader());
        info.setVerifier(new XOREncryptionVerifier());
        final Decryptor dec = new XORDecryptor();
        dec.setEncryptionInfo(info);
        info.setDecryptor(dec);
        final Encryptor enc = new XOREncryptor();
        enc.setEncryptionInfo(info);
        info.setEncryptor(enc);
    }
}
