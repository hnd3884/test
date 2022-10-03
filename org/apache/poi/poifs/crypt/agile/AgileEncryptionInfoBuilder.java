package org.apache.poi.poifs.crypt.agile;

import org.apache.xmlbeans.XmlException;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import java.io.IOException;
import com.microsoft.schemas.office.x2006.encryption.EncryptionDocument;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import org.apache.poi.poifs.crypt.EncryptionHeader;
import java.io.InputStream;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionInfoBuilder;

public class AgileEncryptionInfoBuilder implements EncryptionInfoBuilder
{
    public void initialize(final EncryptionInfo info, final LittleEndianInput dis) throws IOException {
        final EncryptionDocument ed = parseDescriptor((InputStream)dis);
        info.setHeader((EncryptionHeader)new AgileEncryptionHeader(ed));
        info.setVerifier((EncryptionVerifier)new AgileEncryptionVerifier(ed));
        if (info.getVersionMajor() == EncryptionMode.agile.versionMajor && info.getVersionMinor() == EncryptionMode.agile.versionMinor) {
            final AgileDecryptor dec = new AgileDecryptor();
            dec.setEncryptionInfo(info);
            info.setDecryptor((Decryptor)dec);
            final AgileEncryptor enc = new AgileEncryptor();
            enc.setEncryptionInfo(info);
            info.setEncryptor((Encryptor)enc);
        }
    }
    
    public void initialize(final EncryptionInfo info, CipherAlgorithm cipherAlgorithm, HashAlgorithm hashAlgorithm, int keyBits, int blockSize, ChainingMode chainingMode) {
        if (cipherAlgorithm == null) {
            cipherAlgorithm = CipherAlgorithm.aes128;
        }
        if (cipherAlgorithm == CipherAlgorithm.rc4) {
            throw new EncryptedDocumentException("RC4 must not be used with agile encryption.");
        }
        if (hashAlgorithm == null) {
            hashAlgorithm = HashAlgorithm.sha1;
        }
        if (chainingMode == null) {
            chainingMode = ChainingMode.cbc;
        }
        if (chainingMode != ChainingMode.cbc && chainingMode != ChainingMode.cfb) {
            throw new EncryptedDocumentException("Agile encryption only supports CBC/CFB chaining.");
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
        info.setHeader((EncryptionHeader)new AgileEncryptionHeader(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode));
        info.setVerifier((EncryptionVerifier)new AgileEncryptionVerifier(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode));
        final AgileDecryptor dec = new AgileDecryptor();
        dec.setEncryptionInfo(info);
        info.setDecryptor((Decryptor)dec);
        final AgileEncryptor enc = new AgileEncryptor();
        enc.setEncryptionInfo(info);
        info.setEncryptor((Encryptor)enc);
    }
    
    protected static EncryptionDocument parseDescriptor(final String descriptor) {
        try {
            return EncryptionDocument.Factory.parse(descriptor, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        }
        catch (final XmlException e) {
            throw new EncryptedDocumentException("Unable to parse encryption descriptor", (Throwable)e);
        }
    }
    
    protected static EncryptionDocument parseDescriptor(final InputStream descriptor) {
        try {
            return EncryptionDocument.Factory.parse(descriptor, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        }
        catch (final Exception e) {
            throw new EncryptedDocumentException("Unable to parse encryption descriptor", (Throwable)e);
        }
    }
}
