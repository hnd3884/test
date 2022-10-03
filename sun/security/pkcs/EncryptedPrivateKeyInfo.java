package sun.security.pkcs;

import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

public class EncryptedPrivateKeyInfo
{
    private AlgorithmId algid;
    private byte[] encryptedData;
    private byte[] encoded;
    
    public EncryptedPrivateKeyInfo(final byte[] array) throws IOException {
        if (array == null) {
            throw new IllegalArgumentException("encoding must not be null");
        }
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
    
    public EncryptedPrivateKeyInfo(final AlgorithmId algid, final byte[] array) {
        this.algid = algid;
        this.encryptedData = array.clone();
    }
    
    public AlgorithmId getAlgorithm() {
        return this.algid;
    }
    
    public byte[] getEncryptedData() {
        return this.encryptedData.clone();
    }
    
    public byte[] getEncoded() throws IOException {
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
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EncryptedPrivateKeyInfo)) {
            return false;
        }
        try {
            final byte[] encoded = this.getEncoded();
            final byte[] encoded2 = ((EncryptedPrivateKeyInfo)o).getEncoded();
            if (encoded.length != encoded2.length) {
                return false;
            }
            for (int i = 0; i < encoded.length; ++i) {
                if (encoded[i] != encoded2[i]) {
                    return false;
                }
            }
            return true;
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        for (byte b = 0; b < this.encryptedData.length; ++b) {
            n += this.encryptedData[b] * b;
        }
        return n;
    }
}
