package com.microsoft.sqlserver.jdbc;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

final class SQLServerEncryptionAlgorithmFactoryList
{
    private ConcurrentHashMap<String, SQLServerEncryptionAlgorithmFactory> encryptionAlgoFactoryMap;
    private static final SQLServerEncryptionAlgorithmFactoryList instance;
    
    private SQLServerEncryptionAlgorithmFactoryList() {
        (this.encryptionAlgoFactoryMap = new ConcurrentHashMap<String, SQLServerEncryptionAlgorithmFactory>()).putIfAbsent("AEAD_AES_256_CBC_HMAC_SHA256", new SQLServerAeadAes256CbcHmac256Factory());
    }
    
    static SQLServerEncryptionAlgorithmFactoryList getInstance() {
        return SQLServerEncryptionAlgorithmFactoryList.instance;
    }
    
    String getRegisteredCipherAlgorithmNames() {
        final StringBuffer stringBuff = new StringBuffer();
        boolean first = true;
        for (final String key : this.encryptionAlgoFactoryMap.keySet()) {
            if (first) {
                stringBuff.append("'");
                first = false;
            }
            else {
                stringBuff.append(", '");
            }
            stringBuff.append(key);
            stringBuff.append("'");
        }
        return stringBuff.toString();
    }
    
    SQLServerEncryptionAlgorithm getAlgorithm(final SQLServerSymmetricKey key, final SQLServerEncryptionType encryptionType, final String algorithmName) throws SQLServerException {
        SQLServerEncryptionAlgorithm encryptionAlgorithm = null;
        SQLServerEncryptionAlgorithmFactory factory = null;
        if (!this.encryptionAlgoFactoryMap.containsKey(algorithmName)) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UnknownColumnEncryptionAlgorithm"));
            final Object[] msgArgs = { algorithmName, getInstance().getRegisteredCipherAlgorithmNames() };
            throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
        }
        factory = this.encryptionAlgoFactoryMap.get(algorithmName);
        assert null != factory : "Null Algorithm Factory class detected";
        encryptionAlgorithm = factory.create(key, encryptionType, algorithmName);
        return encryptionAlgorithm;
    }
    
    static {
        instance = new SQLServerEncryptionAlgorithmFactoryList();
    }
}
