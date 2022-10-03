package com.microsoft.sqlserver.jdbc;

import java.security.GeneralSecurityException;
import javax.crypto.ShortBufferException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.Mac;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.text.MessageFormat;
import javax.crypto.spec.SecretKeySpec;
import java.util.logging.Logger;

class SQLServerAeadAes256CbcHmac256Algorithm extends SQLServerEncryptionAlgorithm
{
    private static final Logger aeLogger;
    static final String algorithmName = "AEAD_AES_256_CBC_HMAC_SHA256";
    private SQLServerAeadAes256CbcHmac256EncryptionKey columnEncryptionkey;
    private byte algorithmVersion;
    private boolean isDeterministic;
    private int blockSizeInBytes;
    private int keySizeInBytes;
    private byte[] version;
    private byte[] versionSize;
    private int minimumCipherTextLengthInBytesNoAuthenticationTag;
    private int minimumCipherTextLengthInBytesWithAuthenticationTag;
    
    SQLServerAeadAes256CbcHmac256Algorithm(final SQLServerAeadAes256CbcHmac256EncryptionKey columnEncryptionkey, final SQLServerEncryptionType encryptionType, final byte algorithmVersion) {
        this.isDeterministic = false;
        this.blockSizeInBytes = 16;
        this.keySizeInBytes = 32;
        this.version = new byte[] { 1 };
        this.versionSize = new byte[] { 1 };
        this.minimumCipherTextLengthInBytesNoAuthenticationTag = 1 + this.blockSizeInBytes + this.blockSizeInBytes;
        this.minimumCipherTextLengthInBytesWithAuthenticationTag = this.minimumCipherTextLengthInBytesNoAuthenticationTag + this.keySizeInBytes;
        this.columnEncryptionkey = columnEncryptionkey;
        if (encryptionType == SQLServerEncryptionType.Deterministic) {
            this.isDeterministic = true;
        }
        this.algorithmVersion = algorithmVersion;
        this.version[0] = algorithmVersion;
    }
    
    @Override
    byte[] encryptData(final byte[] plainText) throws SQLServerException {
        return this.encryptData(plainText, true);
    }
    
    protected byte[] encryptData(final byte[] plainText, final boolean hasAuthenticationTag) throws SQLServerException {
        SQLServerAeadAes256CbcHmac256Algorithm.aeLogger.entering(SQLServerAeadAes256CbcHmac256Algorithm.class.getName(), "encryptData", "Encrypting data.");
        assert plainText != null;
        byte[] iv = new byte[this.blockSizeInBytes];
        final SecretKeySpec skeySpec = new SecretKeySpec(this.columnEncryptionkey.getEncryptionKey(), "AES");
        Label_0148: {
            if (this.isDeterministic) {
                try {
                    iv = SQLServerSecurityUtility.getHMACWithSHA256(plainText, this.columnEncryptionkey.getIVKey(), this.blockSizeInBytes);
                    break Label_0148;
                }
                catch (final InvalidKeyException | NoSuchAlgorithmException e) {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_EncryptionFailed"));
                    final Object[] msgArgs = { e.getMessage() };
                    throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
                }
            }
            final SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
        }
        final int numBlocks = plainText.length / this.blockSizeInBytes + 1;
        final int hmacStartIndex = 1;
        final int authenticationTagLen = hasAuthenticationTag ? this.keySizeInBytes : 0;
        final int ivStartIndex = hmacStartIndex + authenticationTagLen;
        final int cipherStartIndex = ivStartIndex + this.blockSizeInBytes;
        final int outputBufSize = 1 + authenticationTagLen + iv.length + numBlocks * this.blockSizeInBytes;
        final byte[] outBuffer = new byte[outputBufSize];
        outBuffer[0] = this.algorithmVersion;
        System.arraycopy(iv, 0, outBuffer, ivStartIndex, iv.length);
        try {
            final IvParameterSpec ivector = new IvParameterSpec(iv);
            final Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            encryptCipher.init(1, skeySpec, ivector);
            int count = 0;
            int cipherIndex = cipherStartIndex;
            if (numBlocks > 1) {
                count = (numBlocks - 1) * this.blockSizeInBytes;
                cipherIndex += encryptCipher.update(plainText, 0, count, outBuffer, cipherIndex);
            }
            final byte[] buffTmp = encryptCipher.doFinal(plainText, count, plainText.length - count);
            System.arraycopy(buffTmp, 0, outBuffer, cipherIndex, buffTmp.length);
            if (hasAuthenticationTag) {
                final Mac hmac = Mac.getInstance("HmacSHA256");
                final SecretKeySpec initkey = new SecretKeySpec(this.columnEncryptionkey.getMacKey(), "HmacSHA256");
                hmac.init(initkey);
                hmac.update(this.version, 0, this.version.length);
                hmac.update(iv, 0, iv.length);
                hmac.update(outBuffer, cipherStartIndex, numBlocks * this.blockSizeInBytes);
                hmac.update(this.versionSize, 0, this.version.length);
                final byte[] hash = hmac.doFinal();
                System.arraycopy(hash, 0, outBuffer, hmacStartIndex, authenticationTagLen);
            }
        }
        catch (final NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | ShortBufferException e2) {
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_EncryptionFailed"));
            final Object[] msgArgs2 = { e2.getMessage() };
            throw new SQLServerException(this, form2.format(msgArgs2), null, 0, false);
        }
        SQLServerAeadAes256CbcHmac256Algorithm.aeLogger.exiting(SQLServerAeadAes256CbcHmac256Algorithm.class.getName(), "encryptData", "Data encrypted.");
        return outBuffer;
    }
    
    @Override
    byte[] decryptData(final byte[] cipherText) throws SQLServerException {
        return this.decryptData(cipherText, true);
    }
    
    private byte[] decryptData(final byte[] cipherText, final boolean hasAuthenticationTag) throws SQLServerException {
        assert cipherText != null;
        final byte[] iv = new byte[this.blockSizeInBytes];
        final int minimumCipherTextLength = hasAuthenticationTag ? this.minimumCipherTextLengthInBytesWithAuthenticationTag : this.minimumCipherTextLengthInBytesNoAuthenticationTag;
        if (cipherText.length < minimumCipherTextLength) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidCipherTextSize"));
            final Object[] msgArgs = { cipherText.length, minimumCipherTextLength };
            throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
        }
        int startIndex = 0;
        if (cipherText[startIndex] != this.algorithmVersion) {
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_InvalidAlgorithmVersion"));
            final Object[] msgArgs2 = { String.format("%02X ", cipherText[startIndex]), String.format("%02X ", this.algorithmVersion) };
            throw new SQLServerException(this, form2.format(msgArgs2), null, 0, false);
        }
        ++startIndex;
        int authenticationTagOffset = 0;
        if (hasAuthenticationTag) {
            authenticationTagOffset = startIndex;
            startIndex += this.keySizeInBytes;
        }
        System.arraycopy(cipherText, startIndex, iv, 0, iv.length);
        final int cipherTextOffset;
        startIndex = (cipherTextOffset = startIndex + iv.length);
        final int cipherTextCount = cipherText.length - startIndex;
        if (hasAuthenticationTag) {
            byte[] authenticationTag;
            try {
                authenticationTag = this.prepareAuthenticationTag(iv, cipherText, cipherTextOffset, cipherTextCount);
            }
            catch (final InvalidKeyException | NoSuchAlgorithmException e) {
                final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_DecryptionFailed"));
                final Object[] msgArgs3 = { e.getMessage() };
                throw new SQLServerException(this, form3.format(msgArgs3), null, 0, false);
            }
            if (!SQLServerSecurityUtility.compareBytes(authenticationTag, cipherText, authenticationTagOffset, cipherTextCount)) {
                throw new SQLServerException(this, SQLServerException.getErrString("R_InvalidAuthenticationTag"), null, 0, false);
            }
        }
        return this.decryptData(iv, cipherText, cipherTextOffset, cipherTextCount);
    }
    
    private byte[] decryptData(final byte[] iv, final byte[] cipherText, final int offset, final int count) throws SQLServerException {
        SQLServerAeadAes256CbcHmac256Algorithm.aeLogger.entering(SQLServerAeadAes256CbcHmac256Algorithm.class.getName(), "decryptData", "Decrypting data.");
        assert cipherText != null;
        assert iv != null;
        byte[] plainText = null;
        final SecretKeySpec skeySpec = new SecretKeySpec(this.columnEncryptionkey.getEncryptionKey(), "AES");
        final IvParameterSpec ivector = new IvParameterSpec(iv);
        try {
            final Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            decryptCipher.init(2, skeySpec, ivector);
            plainText = decryptCipher.doFinal(cipherText, offset, count);
        }
        catch (final NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_DecryptionFailed"));
            final Object[] msgArgs = { e.getMessage() };
            throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
        }
        SQLServerAeadAes256CbcHmac256Algorithm.aeLogger.exiting(SQLServerAeadAes256CbcHmac256Algorithm.class.getName(), "decryptData", "Data decrypted.");
        return plainText;
    }
    
    private byte[] prepareAuthenticationTag(final byte[] iv, final byte[] cipherText, final int offset, final int length) throws NoSuchAlgorithmException, InvalidKeyException {
        assert cipherText != null;
        final byte[] authenticationTag = new byte[this.keySizeInBytes];
        final Mac hmac = Mac.getInstance("HmacSHA256");
        final SecretKeySpec key = new SecretKeySpec(this.columnEncryptionkey.getMacKey(), "HmacSHA256");
        hmac.init(key);
        hmac.update(this.version, 0, this.version.length);
        hmac.update(iv, 0, iv.length);
        hmac.update(cipherText, offset, length);
        hmac.update(this.versionSize, 0, this.version.length);
        final byte[] computedHash = hmac.doFinal();
        System.arraycopy(computedHash, 0, authenticationTag, 0, authenticationTag.length);
        return authenticationTag;
    }
    
    static {
        aeLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.SQLServerAeadAes256CbcHmac256Algorithm");
    }
}
