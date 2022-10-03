package com.microsoft.sqlserver.jdbc;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

class SQLServerAeadAes256CbcHmac256EncryptionKey extends SQLServerSymmetricKey
{
    static final int keySize = 256;
    private final String algorithmName;
    private String encryptionKeySaltFormat;
    private String macKeySaltFormat;
    private String ivKeySaltFormat;
    private SQLServerSymmetricKey encryptionKey;
    private SQLServerSymmetricKey macKey;
    private SQLServerSymmetricKey ivKey;
    
    SQLServerAeadAes256CbcHmac256EncryptionKey(final byte[] rootKey, final String algorithmName) throws SQLServerException {
        super(rootKey);
        this.algorithmName = algorithmName;
        this.encryptionKeySaltFormat = "Microsoft SQL Server cell encryption key with encryption algorithm:" + this.algorithmName + " and key length:" + 256;
        this.macKeySaltFormat = "Microsoft SQL Server cell MAC key with encryption algorithm:" + this.algorithmName + " and key length:" + 256;
        this.ivKeySaltFormat = "Microsoft SQL Server cell IV key with encryption algorithm:" + this.algorithmName + " and key length:" + 256;
        final int keySizeInBytes = 32;
        if (rootKey.length != keySizeInBytes) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidKeySize"));
            final Object[] msgArgs = { rootKey.length, keySizeInBytes, this.algorithmName };
            throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
        }
        byte[] encKeyBuff = new byte[keySizeInBytes];
        try {
            encKeyBuff = SQLServerSecurityUtility.getHMACWithSHA256(this.encryptionKeySaltFormat.getBytes(StandardCharsets.UTF_16LE), rootKey, encKeyBuff.length);
            this.encryptionKey = new SQLServerSymmetricKey(encKeyBuff);
            byte[] macKeyBuff = new byte[keySizeInBytes];
            macKeyBuff = SQLServerSecurityUtility.getHMACWithSHA256(this.macKeySaltFormat.getBytes(StandardCharsets.UTF_16LE), rootKey, macKeyBuff.length);
            this.macKey = new SQLServerSymmetricKey(macKeyBuff);
            byte[] ivKeyBuff = new byte[keySizeInBytes];
            ivKeyBuff = SQLServerSecurityUtility.getHMACWithSHA256(this.ivKeySaltFormat.getBytes(StandardCharsets.UTF_16LE), rootKey, ivKeyBuff.length);
            this.ivKey = new SQLServerSymmetricKey(ivKeyBuff);
        }
        catch (final InvalidKeyException | NoSuchAlgorithmException e) {
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_KeyExtractionFailed"));
            final Object[] msgArgs2 = { e.getMessage() };
            throw new SQLServerException(this, form2.format(msgArgs2), null, 0, false);
        }
    }
    
    byte[] getEncryptionKey() {
        return this.encryptionKey.getRootKey();
    }
    
    byte[] getMacKey() {
        return this.macKey.getRootKey();
    }
    
    byte[] getIVKey() {
        return this.ivKey.getRootKey();
    }
}
