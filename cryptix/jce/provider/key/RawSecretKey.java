package cryptix.jce.provider.key;

import javax.crypto.SecretKey;

public class RawSecretKey implements SecretKey
{
    private final String algorithm;
    private final byte[] keyBytes;
    
    public String getAlgorithm() {
        return this.algorithm;
    }
    
    public String getFormat() {
        return "RAW";
    }
    
    public byte[] getEncoded() {
        return this.keyBytes.clone();
    }
    
    public RawSecretKey(final String algorithm, final byte[] keyBytes) {
        this.algorithm = algorithm;
        this.keyBytes = keyBytes.clone();
    }
}
