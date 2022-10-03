package org.apache.poi.poifs.crypt.standard;

import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import java.io.IOException;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import org.apache.poi.poifs.crypt.EncryptionHeader;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionInfoBuilder;

public class StandardEncryptionInfoBuilder implements EncryptionInfoBuilder
{
    @Override
    public void initialize(final EncryptionInfo info, final LittleEndianInput dis) throws IOException {
        dis.readInt();
        final StandardEncryptionHeader header = new StandardEncryptionHeader(dis);
        info.setHeader(header);
        info.setVerifier(new StandardEncryptionVerifier(dis, header));
        if (info.getVersionMinor() == 2 && (info.getVersionMajor() == 3 || info.getVersionMajor() == 4)) {
            final StandardDecryptor dec = new StandardDecryptor();
            dec.setEncryptionInfo(info);
            info.setDecryptor(dec);
        }
    }
    
    @Override
    public void initialize(final EncryptionInfo info, CipherAlgorithm cipherAlgorithm, HashAlgorithm hashAlgorithm, int keyBits, int blockSize, ChainingMode chainingMode) {
        if (cipherAlgorithm == null) {
            cipherAlgorithm = CipherAlgorithm.aes128;
        }
        if (cipherAlgorithm != CipherAlgorithm.aes128 && cipherAlgorithm != CipherAlgorithm.aes192 && cipherAlgorithm != CipherAlgorithm.aes256) {
            throw new EncryptedDocumentException("Standard encryption only supports AES128/192/256.");
        }
        if (hashAlgorithm == null) {
            hashAlgorithm = HashAlgorithm.sha1;
        }
        if (hashAlgorithm != HashAlgorithm.sha1) {
            throw new EncryptedDocumentException("Standard encryption only supports SHA-1.");
        }
        if (chainingMode == null) {
            chainingMode = ChainingMode.ecb;
        }
        if (chainingMode != ChainingMode.ecb) {
            throw new EncryptedDocumentException("Standard encryption only supports ECB chaining.");
        }
        if (keyBits == -1) {
            keyBits = cipherAlgorithm.defaultKeySize;
        }
        if (blockSize == -1) {
            blockSize = cipherAlgorithm.blockSize;
        }
        boolean found = false;
        for (final int ks : cipherAlgorithm.allowedKeySize) {
            found |= (ks == keyBits);
        }
        if (!found) {
            throw new EncryptedDocumentException("KeySize " + keyBits + " not allowed for Cipher " + cipherAlgorithm);
        }
        info.setHeader(new StandardEncryptionHeader(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode));
        info.setVerifier(new StandardEncryptionVerifier(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode));
        final StandardDecryptor dec = new StandardDecryptor();
        dec.setEncryptionInfo(info);
        info.setDecryptor(dec);
        final StandardEncryptor enc = new StandardEncryptor();
        enc.setEncryptionInfo(info);
        info.setEncryptor(enc);
    }
}
