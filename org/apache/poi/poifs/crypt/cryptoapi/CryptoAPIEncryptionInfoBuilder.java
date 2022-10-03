package org.apache.poi.poifs.crypt.cryptoapi;

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

public class CryptoAPIEncryptionInfoBuilder implements EncryptionInfoBuilder
{
    @Override
    public void initialize(final EncryptionInfo info, final LittleEndianInput dis) throws IOException {
        dis.readInt();
        final CryptoAPIEncryptionHeader header = new CryptoAPIEncryptionHeader(dis);
        info.setHeader(header);
        info.setVerifier(new CryptoAPIEncryptionVerifier(dis, header));
        final CryptoAPIDecryptor dec = new CryptoAPIDecryptor();
        dec.setEncryptionInfo(info);
        info.setDecryptor(dec);
        final CryptoAPIEncryptor enc = new CryptoAPIEncryptor();
        enc.setEncryptionInfo(info);
        info.setEncryptor(enc);
    }
    
    @Override
    public void initialize(final EncryptionInfo info, CipherAlgorithm cipherAlgorithm, HashAlgorithm hashAlgorithm, int keyBits, final int blockSize, final ChainingMode chainingMode) {
        if (cipherAlgorithm == null) {
            cipherAlgorithm = CipherAlgorithm.rc4;
        }
        if (hashAlgorithm == null) {
            hashAlgorithm = HashAlgorithm.sha1;
        }
        if (keyBits == -1) {
            keyBits = 40;
        }
        assert cipherAlgorithm == CipherAlgorithm.rc4 && hashAlgorithm == HashAlgorithm.sha1;
        info.setHeader(new CryptoAPIEncryptionHeader(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode));
        info.setVerifier(new CryptoAPIEncryptionVerifier(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode));
        final CryptoAPIDecryptor dec = new CryptoAPIDecryptor();
        dec.setEncryptionInfo(info);
        info.setDecryptor(dec);
        final CryptoAPIEncryptor enc = new CryptoAPIEncryptor();
        enc.setEncryptionInfo(info);
        info.setEncryptor(enc);
    }
}
