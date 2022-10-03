package org.apache.poi.poifs.crypt.agile;

import org.apache.poi.poifs.crypt.ChunkedCipherOutputStream;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.crypt.DataSpaceMapUtils;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.HashMap;
import org.apache.xmlbeans.XmlOptions;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import com.microsoft.schemas.office.x2006.encryption.STHashAlgorithm;
import com.microsoft.schemas.office.x2006.keyEncryptor.certificate.CTCertificateKeyEncryptor;
import com.microsoft.schemas.office.x2006.encryption.CTDataIntegrity;
import com.microsoft.schemas.office.x2006.keyEncryptor.password.CTPasswordKeyEncryptor;
import com.microsoft.schemas.office.x2006.encryption.CTKeyEncryptors;
import com.microsoft.schemas.office.x2006.encryption.CTKeyData;
import com.microsoft.schemas.office.x2006.encryption.CTEncryption;
import java.security.cert.CertificateEncodingException;
import com.microsoft.schemas.office.x2006.encryption.STCipherChaining;
import com.microsoft.schemas.office.x2006.encryption.STCipherAlgorithm;
import com.microsoft.schemas.office.x2006.encryption.EncryptionDocument;
import java.io.InputStream;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import java.io.FileInputStream;
import org.apache.poi.util.LittleEndian;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import javax.crypto.Mac;
import java.util.Iterator;
import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.security.GeneralSecurityException;
import org.apache.poi.EncryptedDocumentException;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import java.util.Random;
import org.apache.poi.util.IOUtils;
import java.security.SecureRandom;
import com.microsoft.schemas.office.x2006.encryption.CTKeyEncryptor;
import org.apache.poi.poifs.crypt.Encryptor;

public class AgileEncryptor extends Encryptor
{
    private static final int MAX_RECORD_LENGTH = 1000000;
    private byte[] integritySalt;
    private byte[] pwHash;
    private final CTKeyEncryptor.Uri.Enum passwordUri;
    private final CTKeyEncryptor.Uri.Enum certificateUri;
    
    protected AgileEncryptor() {
        this.passwordUri = CTKeyEncryptor.Uri.HTTP_SCHEMAS_MICROSOFT_COM_OFFICE_2006_KEY_ENCRYPTOR_PASSWORD;
        this.certificateUri = CTKeyEncryptor.Uri.HTTP_SCHEMAS_MICROSOFT_COM_OFFICE_2006_KEY_ENCRYPTOR_CERTIFICATE;
    }
    
    protected AgileEncryptor(final AgileEncryptor other) {
        super((Encryptor)other);
        this.passwordUri = CTKeyEncryptor.Uri.HTTP_SCHEMAS_MICROSOFT_COM_OFFICE_2006_KEY_ENCRYPTOR_PASSWORD;
        this.certificateUri = CTKeyEncryptor.Uri.HTTP_SCHEMAS_MICROSOFT_COM_OFFICE_2006_KEY_ENCRYPTOR_CERTIFICATE;
        this.integritySalt = (byte[])((other.integritySalt == null) ? null : ((byte[])other.integritySalt.clone()));
        this.pwHash = (byte[])((other.pwHash == null) ? null : ((byte[])other.pwHash.clone()));
    }
    
    public void confirmPassword(final String password) {
        final Random r = new SecureRandom();
        final AgileEncryptionHeader header = (AgileEncryptionHeader)this.getEncryptionInfo().getHeader();
        final int blockSize = header.getBlockSize();
        final int keySize = header.getKeySize() / 8;
        final int hashSize = header.getHashAlgorithm().hashSize;
        final byte[] newVerifierSalt = IOUtils.safelyAllocate((long)blockSize, 1000000);
        final byte[] newVerifier = IOUtils.safelyAllocate((long)blockSize, 1000000);
        final byte[] newKeySalt = IOUtils.safelyAllocate((long)blockSize, 1000000);
        final byte[] newKeySpec = IOUtils.safelyAllocate((long)keySize, 1000000);
        final byte[] newIntegritySalt = IOUtils.safelyAllocate((long)hashSize, 1000000);
        r.nextBytes(newVerifierSalt);
        r.nextBytes(newVerifier);
        r.nextBytes(newKeySalt);
        r.nextBytes(newKeySpec);
        r.nextBytes(newIntegritySalt);
        this.confirmPassword(password, newKeySpec, newKeySalt, newVerifierSalt, newVerifier, newIntegritySalt);
    }
    
    public void confirmPassword(final String password, final byte[] keySpec, final byte[] keySalt, final byte[] verifier, final byte[] verifierSalt, final byte[] integritySalt) {
        final AgileEncryptionVerifier ver = (AgileEncryptionVerifier)this.getEncryptionInfo().getVerifier();
        final AgileEncryptionHeader header = (AgileEncryptionHeader)this.getEncryptionInfo().getHeader();
        ver.setSalt(verifierSalt);
        header.setKeySalt(keySalt);
        final int blockSize = header.getBlockSize();
        this.pwHash = CryptoFunctions.hashPassword(password, ver.getHashAlgorithm(), verifierSalt, ver.getSpinCount());
        final byte[] encryptedVerifier = AgileDecryptor.hashInput(ver, this.pwHash, AgileDecryptor.kVerifierInputBlock, verifier, 1);
        ver.setEncryptedVerifier(encryptedVerifier);
        final MessageDigest hashMD = CryptoFunctions.getMessageDigest(ver.getHashAlgorithm());
        final byte[] hashedVerifier = hashMD.digest(verifier);
        final byte[] encryptedVerifierHash = AgileDecryptor.hashInput(ver, this.pwHash, AgileDecryptor.kHashedVerifierBlock, hashedVerifier, 1);
        ver.setEncryptedVerifierHash(encryptedVerifierHash);
        final byte[] encryptedKey = AgileDecryptor.hashInput(ver, this.pwHash, AgileDecryptor.kCryptoKeyBlock, keySpec, 1);
        ver.setEncryptedKey(encryptedKey);
        final SecretKey secretKey = new SecretKeySpec(keySpec, header.getCipherAlgorithm().jceId);
        this.setSecretKey(secretKey);
        this.integritySalt = integritySalt.clone();
        try {
            final byte[] vec = CryptoFunctions.generateIv(header.getHashAlgorithm(), header.getKeySalt(), AgileDecryptor.kIntegrityKeyBlock, header.getBlockSize());
            Cipher cipher = CryptoFunctions.getCipher(secretKey, header.getCipherAlgorithm(), header.getChainingMode(), vec, 1);
            final byte[] hmacKey = CryptoFunctions.getBlock0(this.integritySalt, AgileDecryptor.getNextBlockSize(this.integritySalt.length, blockSize));
            final byte[] encryptedHmacKey = cipher.doFinal(hmacKey);
            header.setEncryptedHmacKey(encryptedHmacKey);
            cipher = Cipher.getInstance("RSA");
            for (final AgileEncryptionVerifier.AgileCertificateEntry ace : ver.getCertificates()) {
                cipher.init(1, ace.x509.getPublicKey());
                ace.encryptedKey = cipher.doFinal(this.getSecretKey().getEncoded());
                final Mac x509Hmac = CryptoFunctions.getMac(header.getHashAlgorithm());
                x509Hmac.init(this.getSecretKey());
                ace.certVerifier = x509Hmac.doFinal(ace.x509.getEncoded());
            }
        }
        catch (final GeneralSecurityException e) {
            throw new EncryptedDocumentException((Throwable)e);
        }
    }
    
    public OutputStream getDataStream(final DirectoryNode dir) throws IOException, GeneralSecurityException {
        return (OutputStream)new AgileCipherOutputStream(dir);
    }
    
    protected void updateIntegrityHMAC(final File tmpFile, final int oleStreamSize) throws GeneralSecurityException, IOException {
        final AgileEncryptionHeader header = (AgileEncryptionHeader)this.getEncryptionInfo().getHeader();
        final int blockSize = header.getBlockSize();
        final HashAlgorithm hashAlgo = header.getHashAlgorithm();
        final Mac integrityMD = CryptoFunctions.getMac(hashAlgo);
        final byte[] hmacKey = CryptoFunctions.getBlock0(this.integritySalt, AgileDecryptor.getNextBlockSize(this.integritySalt.length, blockSize));
        integrityMD.init(new SecretKeySpec(hmacKey, hashAlgo.jceHmacId));
        final byte[] buf = new byte[1024];
        LittleEndian.putLong(buf, 0, (long)oleStreamSize);
        integrityMD.update(buf, 0, 8);
        try (final InputStream fis = new FileInputStream(tmpFile)) {
            int readBytes;
            while ((readBytes = fis.read(buf)) != -1) {
                integrityMD.update(buf, 0, readBytes);
            }
        }
        final byte[] hmacValue = integrityMD.doFinal();
        final byte[] hmacValueFilled = CryptoFunctions.getBlock0(hmacValue, AgileDecryptor.getNextBlockSize(hmacValue.length, blockSize));
        final byte[] iv = CryptoFunctions.generateIv(header.getHashAlgorithm(), header.getKeySalt(), AgileDecryptor.kIntegrityValueBlock, blockSize);
        final Cipher cipher = CryptoFunctions.getCipher(this.getSecretKey(), header.getCipherAlgorithm(), header.getChainingMode(), iv, 1);
        final byte[] encryptedHmacValue = cipher.doFinal(hmacValueFilled);
        header.setEncryptedHmacValue(encryptedHmacValue);
    }
    
    protected EncryptionDocument createEncryptionDocument() {
        final AgileEncryptionVerifier ver = (AgileEncryptionVerifier)this.getEncryptionInfo().getVerifier();
        final AgileEncryptionHeader header = (AgileEncryptionHeader)this.getEncryptionInfo().getHeader();
        final EncryptionDocument ed = EncryptionDocument.Factory.newInstance();
        final CTEncryption edRoot = ed.addNewEncryption();
        final CTKeyData keyData = edRoot.addNewKeyData();
        final CTKeyEncryptors keyEncList = edRoot.addNewKeyEncryptors();
        CTKeyEncryptor keyEnc = keyEncList.addNewKeyEncryptor();
        keyEnc.setUri(this.passwordUri);
        final CTPasswordKeyEncryptor keyPass = keyEnc.addNewEncryptedPasswordKey();
        keyPass.setSpinCount(ver.getSpinCount());
        keyData.setSaltSize(header.getBlockSize());
        keyPass.setSaltSize(ver.getBlockSize());
        keyData.setBlockSize(header.getBlockSize());
        keyPass.setBlockSize(ver.getBlockSize());
        keyData.setKeyBits((long)header.getKeySize());
        keyPass.setKeyBits((long)ver.getKeySize());
        keyData.setHashSize(header.getHashAlgorithm().hashSize);
        keyPass.setHashSize(ver.getHashAlgorithm().hashSize);
        if (!header.getCipherAlgorithm().xmlId.equals(ver.getCipherAlgorithm().xmlId)) {
            throw new EncryptedDocumentException("Cipher algorithm of header and verifier have to match");
        }
        final STCipherAlgorithm.Enum xmlCipherAlgo = STCipherAlgorithm.Enum.forString(header.getCipherAlgorithm().xmlId);
        if (xmlCipherAlgo == null) {
            throw new EncryptedDocumentException("CipherAlgorithm " + header.getCipherAlgorithm() + " not supported.");
        }
        keyData.setCipherAlgorithm(xmlCipherAlgo);
        keyPass.setCipherAlgorithm(xmlCipherAlgo);
        switch (header.getChainingMode()) {
            case cbc: {
                keyData.setCipherChaining(STCipherChaining.CHAINING_MODE_CBC);
                keyPass.setCipherChaining(STCipherChaining.CHAINING_MODE_CBC);
                break;
            }
            case cfb: {
                keyData.setCipherChaining(STCipherChaining.CHAINING_MODE_CFB);
                keyPass.setCipherChaining(STCipherChaining.CHAINING_MODE_CFB);
                break;
            }
            default: {
                throw new EncryptedDocumentException("ChainingMode " + header.getChainingMode() + " not supported.");
            }
        }
        keyData.setHashAlgorithm(mapHashAlgorithm(header.getHashAlgorithm()));
        keyPass.setHashAlgorithm(mapHashAlgorithm(ver.getHashAlgorithm()));
        keyData.setSaltValue(header.getKeySalt());
        keyPass.setSaltValue(ver.getSalt());
        keyPass.setEncryptedVerifierHashInput(ver.getEncryptedVerifier());
        keyPass.setEncryptedVerifierHashValue(ver.getEncryptedVerifierHash());
        keyPass.setEncryptedKeyValue(ver.getEncryptedKey());
        final CTDataIntegrity hmacData = edRoot.addNewDataIntegrity();
        hmacData.setEncryptedHmacKey(header.getEncryptedHmacKey());
        hmacData.setEncryptedHmacValue(header.getEncryptedHmacValue());
        for (final AgileEncryptionVerifier.AgileCertificateEntry ace : ver.getCertificates()) {
            keyEnc = keyEncList.addNewKeyEncryptor();
            keyEnc.setUri(this.certificateUri);
            final CTCertificateKeyEncryptor certData = keyEnc.addNewEncryptedCertificateKey();
            try {
                certData.setX509Certificate(ace.x509.getEncoded());
            }
            catch (final CertificateEncodingException e) {
                throw new EncryptedDocumentException((Throwable)e);
            }
            certData.setEncryptedKeyValue(ace.encryptedKey);
            certData.setCertVerifier(ace.certVerifier);
        }
        return ed;
    }
    
    private static STHashAlgorithm.Enum mapHashAlgorithm(final HashAlgorithm hashAlgo) {
        final STHashAlgorithm.Enum xmlHashAlgo = STHashAlgorithm.Enum.forString(hashAlgo.ecmaString);
        if (xmlHashAlgo == null) {
            throw new EncryptedDocumentException("HashAlgorithm " + hashAlgo + " not supported.");
        }
        return xmlHashAlgo;
    }
    
    protected void marshallEncryptionDocument(final EncryptionDocument ed, final LittleEndianByteArrayOutputStream os) {
        final XmlOptions xo = new XmlOptions();
        xo.setCharacterEncoding("UTF-8");
        final Map<String, String> nsMap = new HashMap<String, String>();
        nsMap.put(this.passwordUri.toString(), "p");
        nsMap.put(this.certificateUri.toString(), "c");
        xo.setUseDefaultNamespace();
        xo.setSaveSuggestedPrefixes((Map)nsMap);
        xo.setSaveNamespacesFirst();
        xo.setSaveAggressiveNamespaces();
        xo.setSaveNoXmlDecl();
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n".getBytes(StandardCharsets.UTF_8));
            ed.save((OutputStream)bos, xo);
            bos.writeTo((OutputStream)os);
        }
        catch (final IOException e) {
            throw new EncryptedDocumentException("error marshalling encryption info document", (Throwable)e);
        }
    }
    
    protected void createEncryptionInfoEntry(final DirectoryNode dir, final File tmpFile) throws IOException, GeneralSecurityException {
        DataSpaceMapUtils.addDefaultDataSpace((DirectoryEntry)dir);
        final EncryptionInfo info = this.getEncryptionInfo();
        final EncryptionRecord er = (EncryptionRecord)new EncryptionRecord() {
            public void write(final LittleEndianByteArrayOutputStream bos) {
                bos.writeShort(info.getVersionMajor());
                bos.writeShort(info.getVersionMinor());
                bos.writeInt(info.getEncryptionFlags());
                final EncryptionDocument ed = AgileEncryptor.this.createEncryptionDocument();
                AgileEncryptor.this.marshallEncryptionDocument(ed, bos);
            }
        };
        DataSpaceMapUtils.createEncryptionEntry((DirectoryEntry)dir, "EncryptionInfo", er);
    }
    
    public AgileEncryptor copy() {
        return new AgileEncryptor(this);
    }
    
    private class AgileCipherOutputStream extends ChunkedCipherOutputStream
    {
        public AgileCipherOutputStream(final DirectoryNode dir) throws IOException, GeneralSecurityException {
            super(dir, 4096);
        }
        
        protected Cipher initCipherForBlock(final Cipher existing, final int block, final boolean lastChunk) throws GeneralSecurityException {
            return AgileDecryptor.initCipherForBlock(existing, block, lastChunk, AgileEncryptor.this.getEncryptionInfo(), AgileEncryptor.this.getSecretKey(), 1);
        }
        
        protected void calculateChecksum(final File fileOut, final int oleStreamSize) throws GeneralSecurityException, IOException {
            AgileEncryptor.this.updateIntegrityHMAC(fileOut, oleStreamSize);
        }
        
        protected void createEncryptionInfoEntry(final DirectoryNode dir, final File tmpFile) throws IOException, GeneralSecurityException {
            AgileEncryptor.this.createEncryptionInfoEntry(dir, tmpFile);
        }
    }
}
