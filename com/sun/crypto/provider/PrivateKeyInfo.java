package com.sun.crypto.provider;

import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;
import java.math.BigInteger;

final class PrivateKeyInfo
{
    private static final BigInteger VERSION;
    private AlgorithmId algid;
    private byte[] privkey;
    
    PrivateKeyInfo(final byte[] array) throws IOException {
        final DerValue derValue = new DerValue(array);
        if (derValue.tag != 48) {
            throw new IOException("private key parse error: not a sequence");
        }
        final BigInteger bigInteger = derValue.data.getBigInteger();
        if (!bigInteger.equals(PrivateKeyInfo.VERSION)) {
            throw new IOException("version mismatch: (supported: " + PrivateKeyInfo.VERSION + ", parsed: " + bigInteger);
        }
        this.algid = AlgorithmId.parse(derValue.data.getDerValue());
        this.privkey = derValue.data.getOctetString();
    }
    
    AlgorithmId getAlgorithm() {
        return this.algid;
    }
    
    static {
        VERSION = BigInteger.ZERO;
    }
}
