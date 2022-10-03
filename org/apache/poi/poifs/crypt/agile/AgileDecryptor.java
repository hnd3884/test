package org.apache.poi.poifs.crypt.agile;

import org.apache.poi.poifs.crypt.ChunkedCipherInputStream;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.poi.poifs.crypt.EncryptionHeader;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.EncryptedDocumentException;
import javax.crypto.Mac;
import java.util.Iterator;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import java.security.Key;
import java.security.cert.X509Certificate;
import java.security.KeyPair;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import java.nio.ByteBuffer;
import org.apache.poi.poifs.crypt.Decryptor;

public class AgileDecryptor extends Decryptor
{
    static final byte[] kVerifierInputBlock;
    static final byte[] kHashedVerifierBlock;
    static final byte[] kCryptoKeyBlock;
    static final byte[] kIntegrityKeyBlock;
    static final byte[] kIntegrityValueBlock;
    private long _length;
    
    protected AgileDecryptor() {
        this._length = -1L;
    }
    
    protected AgileDecryptor(final AgileDecryptor other) {
        super((Decryptor)other);
        this._length = -1L;
        this._length = other._length;
    }
    
    private static byte[] longToBytes(final long l) {
        return ByteBuffer.allocate(8).putLong(l).array();
    }
    
    public boolean verifyPassword(final String password) throws GeneralSecurityException {
        final AgileEncryptionVerifier ver = (AgileEncryptionVerifier)this.getEncryptionInfo().getVerifier();
        final AgileEncryptionHeader header = (AgileEncryptionHeader)this.getEncryptionInfo().getHeader();
        final int blockSize = header.getBlockSize();
        final byte[] pwHash = CryptoFunctions.hashPassword(password, ver.getHashAlgorithm(), ver.getSalt(), ver.getSpinCount());
        final byte[] verfierInputEnc = hashInput(ver, pwHash, AgileDecryptor.kVerifierInputBlock, ver.getEncryptedVerifier(), 2);
        this.setVerifier(verfierInputEnc);
        final MessageDigest hashMD = CryptoFunctions.getMessageDigest(ver.getHashAlgorithm());
        final byte[] verifierHash = hashMD.digest(verfierInputEnc);
        byte[] verifierHashDec = hashInput(ver, pwHash, AgileDecryptor.kHashedVerifierBlock, ver.getEncryptedVerifierHash(), 2);
        verifierHashDec = CryptoFunctions.getBlock0(verifierHashDec, ver.getHashAlgorithm().hashSize);
        byte[] keyspec = hashInput(ver, pwHash, AgileDecryptor.kCryptoKeyBlock, ver.getEncryptedKey(), 2);
        keyspec = CryptoFunctions.getBlock0(keyspec, header.getKeySize() / 8);
        final SecretKeySpec secretKey = new SecretKeySpec(keyspec, header.getCipherAlgorithm().jceId);
        byte[] vec = CryptoFunctions.generateIv(header.getHashAlgorithm(), header.getKeySalt(), AgileDecryptor.kIntegrityKeyBlock, blockSize);
        final CipherAlgorithm cipherAlgo = header.getCipherAlgorithm();
        Cipher cipher = CryptoFunctions.getCipher((SecretKey)secretKey, cipherAlgo, header.getChainingMode(), vec, 2);
        byte[] hmacKey = cipher.doFinal(header.getEncryptedHmacKey());
        hmacKey = CryptoFunctions.getBlock0(hmacKey, header.getHashAlgorithm().hashSize);
        vec = CryptoFunctions.generateIv(header.getHashAlgorithm(), header.getKeySalt(), AgileDecryptor.kIntegrityValueBlock, blockSize);
        cipher = CryptoFunctions.getCipher((SecretKey)secretKey, cipherAlgo, ver.getChainingMode(), vec, 2);
        byte[] hmacValue = cipher.doFinal(header.getEncryptedHmacValue());
        hmacValue = CryptoFunctions.getBlock0(hmacValue, header.getHashAlgorithm().hashSize);
        if (Arrays.equals(verifierHashDec, verifierHash)) {
            this.setSecretKey((SecretKey)secretKey);
            this.setIntegrityHmacKey(hmacKey);
            this.setIntegrityHmacValue(hmacValue);
            return true;
        }
        return false;
    }
    
    public boolean verifyPassword(final KeyPair keyPair, final X509Certificate x509) throws GeneralSecurityException {
        final AgileEncryptionVerifier ver = (AgileEncryptionVerifier)this.getEncryptionInfo().getVerifier();
        final AgileEncryptionHeader header = (AgileEncryptionHeader)this.getEncryptionInfo().getHeader();
        final HashAlgorithm hashAlgo = header.getHashAlgorithm();
        final CipherAlgorithm cipherAlgo = header.getCipherAlgorithm();
        final int blockSize = header.getBlockSize();
        AgileEncryptionVerifier.AgileCertificateEntry ace = null;
        for (final AgileEncryptionVerifier.AgileCertificateEntry aceEntry : ver.getCertificates()) {
            if (x509.equals(aceEntry.x509)) {
                ace = aceEntry;
                break;
            }
        }
        if (ace == null) {
            return false;
        }
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(2, keyPair.getPrivate());
        final byte[] keyspec = cipher.doFinal(ace.encryptedKey);
        final SecretKeySpec secretKey = new SecretKeySpec(keyspec, ver.getCipherAlgorithm().jceId);
        final Mac x509Hmac = CryptoFunctions.getMac(hashAlgo);
        x509Hmac.init(secretKey);
        final byte[] certVerifier = x509Hmac.doFinal(ace.x509.getEncoded());
        byte[] vec = CryptoFunctions.generateIv(hashAlgo, header.getKeySalt(), AgileDecryptor.kIntegrityKeyBlock, blockSize);
        cipher = CryptoFunctions.getCipher((SecretKey)secretKey, cipherAlgo, header.getChainingMode(), vec, 2);
        byte[] hmacKey = cipher.doFinal(header.getEncryptedHmacKey());
        hmacKey = CryptoFunctions.getBlock0(hmacKey, hashAlgo.hashSize);
        vec = CryptoFunctions.generateIv(hashAlgo, header.getKeySalt(), AgileDecryptor.kIntegrityValueBlock, blockSize);
        cipher = CryptoFunctions.getCipher((SecretKey)secretKey, cipherAlgo, header.getChainingMode(), vec, 2);
        byte[] hmacValue = cipher.doFinal(header.getEncryptedHmacValue());
        hmacValue = CryptoFunctions.getBlock0(hmacValue, hashAlgo.hashSize);
        if (Arrays.equals(ace.certVerifier, certVerifier)) {
            this.setSecretKey((SecretKey)secretKey);
            this.setIntegrityHmacKey(hmacKey);
            this.setIntegrityHmacValue(hmacValue);
            return true;
        }
        return false;
    }
    
    protected static int getNextBlockSize(final int inputLen, final int blockSize) {
        int fillSize;
        for (fillSize = blockSize; fillSize < inputLen; fillSize += blockSize) {}
        return fillSize;
    }
    
    static byte[] hashInput(final AgileEncryptionVerifier ver, final byte[] pwHash, final byte[] blockKey, byte[] inputKey, final int cipherMode) {
        final CipherAlgorithm cipherAlgo = ver.getCipherAlgorithm();
        final ChainingMode chainMode = ver.getChainingMode();
        final int keySize = ver.getKeySize() / 8;
        final int blockSize = ver.getBlockSize();
        final HashAlgorithm hashAlgo = ver.getHashAlgorithm();
        final byte[] intermedKey = CryptoFunctions.generateKey(pwHash, hashAlgo, blockKey, keySize);
        final SecretKey skey = new SecretKeySpec(intermedKey, cipherAlgo.jceId);
        final byte[] iv = CryptoFunctions.generateIv(hashAlgo, ver.getSalt(), (byte[])null, blockSize);
        final Cipher cipher = CryptoFunctions.getCipher(skey, cipherAlgo, chainMode, iv, cipherMode);
        try {
            inputKey = CryptoFunctions.getBlock0(inputKey, getNextBlockSize(inputKey.length, blockSize));
            final byte[] hashFinal = cipher.doFinal(inputKey);
            return hashFinal;
        }
        catch (final GeneralSecurityException e) {
            throw new EncryptedDocumentException((Throwable)e);
        }
    }
    
    public InputStream getDataStream(final DirectoryNode dir) throws IOException, GeneralSecurityException {
        final DocumentInputStream dis = dir.createDocumentInputStream("EncryptedPackage");
        this._length = dis.readLong();
        return (InputStream)new AgileCipherInputStream(dis, this._length);
    }
    
    public long getLength() {
        if (this._length == -1L) {
            throw new IllegalStateException("EcmaDecryptor.getDataStream() was not called");
        }
        return this._length;
    }
    
    protected static Cipher initCipherForBlock(Cipher existing, final int block, final boolean lastChunk, final EncryptionInfo encryptionInfo, final SecretKey skey, final int encryptionMode) throws GeneralSecurityException {
        final EncryptionHeader header = encryptionInfo.getHeader();
        final String padding = lastChunk ? "PKCS5Padding" : "NoPadding";
        if (existing == null || !existing.getAlgorithm().endsWith(padding)) {
            existing = CryptoFunctions.getCipher((Key)skey, header.getCipherAlgorithm(), header.getChainingMode(), header.getKeySalt(), encryptionMode, padding);
        }
        final byte[] blockKey = new byte[4];
        LittleEndian.putInt(blockKey, 0, block);
        final byte[] iv = CryptoFunctions.generateIv(header.getHashAlgorithm(), header.getKeySalt(), blockKey, header.getBlockSize());
        AlgorithmParameterSpec aps;
        if (header.getCipherAlgorithm() == CipherAlgorithm.rc2) {
            aps = new RC2ParameterSpec(skey.getEncoded().length * 8, iv);
        }
        else {
            aps = new IvParameterSpec(iv);
        }
        existing.init(encryptionMode, skey, aps);
        return existing;
    }
    
    public AgileDecryptor copy() {
        return new AgileDecryptor(this);
    }
    
    static {
        kVerifierInputBlock = longToBytes(-96877461722390919L);
        kHashedVerifierBlock = longToBytes(-2906493647876705202L);
        kCryptoKeyBlock = longToBytes(1472127217842311382L);
        kIntegrityKeyBlock = longToBytes(6895764199477731830L);
        kIntegrityValueBlock = longToBytes(-6888397455483960269L);
    }
    
    private class AgileCipherInputStream extends ChunkedCipherInputStream
    {
        public AgileCipherInputStream(final DocumentInputStream stream, final long size) throws GeneralSecurityException {
            super((InputStream)stream, size, 4096);
        }
        
        protected Cipher initCipherForBlock(final Cipher cipher, final int block) throws GeneralSecurityException {
            return AgileDecryptor.initCipherForBlock(cipher, block, false, AgileDecryptor.this.getEncryptionInfo(), AgileDecryptor.this.getSecretKey(), 2);
        }
    }
}
