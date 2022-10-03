package com.sun.crypto.provider;

import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

final class EncryptedPrivateKeyInfo
{
    private AlgorithmId algid;
    private byte[] encryptedData;
    private byte[] encoded;
    
    EncryptedPrivateKeyInfo(final byte[] array) throws IOException {
        final DerValue derValue = new DerValue(array);
        final DerValue[] array2 = { derValue.data.getDerValue(), derValue.data.getDerValue() };
        if (derValue.data.available() != 0) {
            throw new IOException("overrun, bytes = " + derValue.data.available());
        }
        this.algid = AlgorithmId.parse(array2[0]);
        if (array2[0].data.available() != 0) {
            throw new IOException("encryptionAlgorithm field overrun");
        }
        this.encryptedData = array2[1].getOctetString();
        if (array2[1].data.available() != 0) {
            throw new IOException("encryptedData field overrun");
        }
        this.encoded = array.clone();
    }
    
    EncryptedPrivateKeyInfo(final AlgorithmId algid, final byte[] array) {
        this.algid = algid;
        this.encryptedData = array.clone();
        this.encoded = null;
    }
    
    AlgorithmId getAlgorithm() {
        return this.algid;
    }
    
    byte[] getEncryptedData() {
        return this.encryptedData.clone();
    }
    
    byte[] getEncoded() throws IOException {
        if (this.encoded != null) {
            return this.encoded.clone();
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        this.algid.encode(derOutputStream2);
        derOutputStream2.putOctetString(this.encryptedData);
        derOutputStream.write((byte)48, derOutputStream2);
        this.encoded = derOutputStream.toByteArray();
        return this.encoded.clone();
    }
}
