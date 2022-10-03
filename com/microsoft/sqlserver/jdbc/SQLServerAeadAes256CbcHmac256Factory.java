package com.microsoft.sqlserver.jdbc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

class SQLServerAeadAes256CbcHmac256Factory extends SQLServerEncryptionAlgorithmFactory
{
    private byte algorithmVersion;
    private ConcurrentHashMap<String, SQLServerAeadAes256CbcHmac256Algorithm> encryptionAlgorithms;
    
    SQLServerAeadAes256CbcHmac256Factory() {
        this.algorithmVersion = 1;
        this.encryptionAlgorithms = new ConcurrentHashMap<String, SQLServerAeadAes256CbcHmac256Algorithm>();
    }
    
    @Override
    SQLServerEncryptionAlgorithm create(final SQLServerSymmetricKey columnEncryptionKey, final SQLServerEncryptionType encryptionType, final String encryptionAlgorithm) throws SQLServerException {
        assert columnEncryptionKey != null;
        if (encryptionType != SQLServerEncryptionType.Deterministic && encryptionType != SQLServerEncryptionType.Randomized) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidEncryptionType"));
            final Object[] msgArgs = { encryptionType, encryptionAlgorithm, "'" + SQLServerEncryptionType.Deterministic + "," + SQLServerEncryptionType.Randomized + "'" };
            throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
        }
        final StringBuilder factoryKeyBuilder = new StringBuilder();
        factoryKeyBuilder.append(Base64.getEncoder().encodeToString(new String(columnEncryptionKey.getRootKey(), StandardCharsets.UTF_8).getBytes()));
        factoryKeyBuilder.append(":");
        factoryKeyBuilder.append(encryptionType);
        factoryKeyBuilder.append(":");
        factoryKeyBuilder.append(this.algorithmVersion);
        final String factoryKey = factoryKeyBuilder.toString();
        if (!this.encryptionAlgorithms.containsKey(factoryKey)) {
            final SQLServerAeadAes256CbcHmac256EncryptionKey encryptedKey = new SQLServerAeadAes256CbcHmac256EncryptionKey(columnEncryptionKey.getRootKey(), "AEAD_AES_256_CBC_HMAC_SHA256");
            final SQLServerAeadAes256CbcHmac256Algorithm aesAlgorithm = new SQLServerAeadAes256CbcHmac256Algorithm(encryptedKey, encryptionType, this.algorithmVersion);
            this.encryptionAlgorithms.putIfAbsent(factoryKey, aesAlgorithm);
        }
        return this.encryptionAlgorithms.get(factoryKey);
    }
}
