package com.microsoft.sqlserver.jdbc;

import java.util.List;
import java.text.MessageFormat;
import java.util.Iterator;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;

class SQLServerSecurityUtility
{
    static byte[] getHMACWithSHA256(final byte[] plainText, final byte[] key, final int length) throws NoSuchAlgorithmException, InvalidKeyException {
        final byte[] hash = new byte[length];
        final Mac mac = Mac.getInstance("HmacSHA256");
        final SecretKeySpec ivkeySpec = new SecretKeySpec(key, "HmacSHA256");
        mac.init(ivkeySpec);
        final byte[] computedHash = mac.doFinal(plainText);
        System.arraycopy(computedHash, 0, hash, 0, hash.length);
        return hash;
    }
    
    static boolean compareBytes(final byte[] buffer1, final byte[] buffer2, final int buffer2Index, final int lengthToCompare) {
        if (null == buffer1 || null == buffer2) {
            return false;
        }
        if (buffer2.length - buffer2Index < lengthToCompare) {
            return false;
        }
        for (int index = 0; index < buffer1.length && index < lengthToCompare; ++index) {
            if (buffer1[index] != buffer2[buffer2Index + index]) {
                return false;
            }
        }
        return true;
    }
    
    static byte[] encryptWithKey(final byte[] plainText, final CryptoMetadata md, final SQLServerConnection connection) throws SQLServerException {
        final String serverName = connection.getTrustedServerNameAE();
        assert serverName != null : "Server name should npt be null in EncryptWithKey";
        if (!md.IsAlgorithmInitialized()) {
            decryptSymmetricKey(md, connection);
        }
        assert md.IsAlgorithmInitialized();
        final byte[] cipherText = md.cipherAlgorithm.encryptData(plainText);
        if (null == cipherText || 0 == cipherText.length) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_NullCipherTextAE"), null, 0, false);
        }
        return cipherText;
    }
    
    private static String ValidateAndGetEncryptionAlgorithmName(final byte cipherAlgorithmId, final String cipherAlgorithmName) throws SQLServerException {
        if (2 != cipherAlgorithmId) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_CustomCipherAlgorithmNotSupportedAE"), null, 0, false);
        }
        return "AEAD_AES_256_CBC_HMAC_SHA256";
    }
    
    static void decryptSymmetricKey(final CryptoMetadata md, final SQLServerConnection connection) throws SQLServerException {
        assert null != md : "md should not be null in DecryptSymmetricKey.";
        assert null != md.cekTableEntry : "md.EncryptionInfo should not be null in DecryptSymmetricKey.";
        assert null != md.cekTableEntry.columnEncryptionKeyValues : "md.EncryptionInfo.ColumnEncryptionKeyValues should not be null in DecryptSymmetricKey.";
        SQLServerSymmetricKey symKey = null;
        EncryptionKeyInfo encryptionkeyInfoChosen = null;
        final SQLServerSymmetricKeyCache cache = SQLServerSymmetricKeyCache.getInstance();
        final Iterator<EncryptionKeyInfo> it = md.cekTableEntry.columnEncryptionKeyValues.iterator();
        SQLServerException lastException = null;
        while (it.hasNext()) {
            final EncryptionKeyInfo keyInfo = it.next();
            try {
                symKey = cache.getKey(keyInfo, connection);
                if (null != symKey) {
                    encryptionkeyInfoChosen = keyInfo;
                    break;
                }
                continue;
            }
            catch (final SQLServerException e) {
                lastException = e;
            }
        }
        if (null == symKey) {
            if (null != lastException) {
                throw lastException;
            }
            throw new SQLServerException(null, SQLServerException.getErrString("R_CEKDecryptionFailed"), null, 0, false);
        }
        else {
            md.cipherAlgorithm = null;
            SQLServerEncryptionAlgorithm cipherAlgorithm = null;
            final String algorithmName = ValidateAndGetEncryptionAlgorithmName(md.cipherAlgorithmId, md.cipherAlgorithmName);
            cipherAlgorithm = SQLServerEncryptionAlgorithmFactoryList.getInstance().getAlgorithm(symKey, md.encryptionType, algorithmName);
            assert null != cipherAlgorithm : "Cipher algorithm cannot be null in DecryptSymmetricKey";
            md.cipherAlgorithm = cipherAlgorithm;
            md.encryptionKeyInfo = encryptionkeyInfoChosen;
        }
    }
    
    static byte[] decryptWithKey(final byte[] cipherText, final CryptoMetadata md, final SQLServerConnection connection) throws SQLServerException {
        final String serverName = connection.getTrustedServerNameAE();
        assert null != serverName : "serverName should not be null in DecryptWithKey.";
        if (!md.IsAlgorithmInitialized()) {
            decryptSymmetricKey(md, connection);
        }
        assert md.IsAlgorithmInitialized() : "Decryption Algorithm is not initialized";
        final byte[] plainText = md.cipherAlgorithm.decryptData(cipherText);
        if (null == plainText) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_PlainTextNullAE"), null, 0, false);
        }
        return plainText;
    }
    
    static void verifyColumnMasterKeyMetadata(final SQLServerConnection connection, final String keyStoreName, final String keyPath, final String serverName, final boolean isEnclaveEnabled, final byte[] CMKSignature) throws SQLServerException {
        final Boolean[] hasEntry = { null };
        final List<String> trustedKeyPaths = SQLServerConnection.getColumnEncryptionTrustedMasterKeyPaths(serverName, hasEntry);
        if (hasEntry[0] && (null == trustedKeyPaths || 0 == trustedKeyPaths.size() || !trustedKeyPaths.contains(keyPath))) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UntrustedKeyPath"));
            final Object[] msgArgs = { keyPath, serverName };
            throw new SQLServerException(form.format(msgArgs), (Throwable)null);
        }
        if (!connection.getColumnEncryptionKeyStoreProvider(keyStoreName).verifyColumnMasterKeyMetadata(keyPath, isEnclaveEnabled, CMKSignature)) {
            throw new SQLServerException(SQLServerException.getErrString("R_VerifySignature"), (Throwable)null);
        }
    }
}
