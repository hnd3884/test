package javax.crypto.spec;

import java.security.MessageDigest;
import java.util.Locale;
import javax.crypto.SecretKey;
import java.security.spec.KeySpec;

public class SecretKeySpec implements KeySpec, SecretKey
{
    private static final long serialVersionUID = 6577238317307289933L;
    private byte[] key;
    private String algorithm;
    
    public SecretKeySpec(final byte[] array, final String algorithm) {
        if (array == null || algorithm == null) {
            throw new IllegalArgumentException("Missing argument");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("Empty key");
        }
        this.key = array.clone();
        this.algorithm = algorithm;
    }
    
    public SecretKeySpec(final byte[] array, final int n, final int n2, final String algorithm) {
        if (array == null || algorithm == null) {
            throw new IllegalArgumentException("Missing argument");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("Empty key");
        }
        if (array.length - n < n2) {
            throw new IllegalArgumentException("Invalid offset/length combination");
        }
        if (n2 < 0) {
            throw new ArrayIndexOutOfBoundsException("len is negative");
        }
        System.arraycopy(array, n, this.key = new byte[n2], 0, n2);
        this.algorithm = algorithm;
    }
    
    @Override
    public String getAlgorithm() {
        return this.algorithm;
    }
    
    @Override
    public String getFormat() {
        return "RAW";
    }
    
    @Override
    public byte[] getEncoded() {
        return this.key.clone();
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        for (byte b = 1; b < this.key.length; ++b) {
            n += this.key[b] * b;
        }
        if (this.algorithm.equalsIgnoreCase("TripleDES")) {
            return n ^ "desede".hashCode();
        }
        return n ^ this.algorithm.toLowerCase(Locale.ENGLISH).hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecretKey)) {
            return false;
        }
        final String algorithm = ((SecretKey)o).getAlgorithm();
        return (algorithm.equalsIgnoreCase(this.algorithm) || (algorithm.equalsIgnoreCase("DESede") && this.algorithm.equalsIgnoreCase("TripleDES")) || (algorithm.equalsIgnoreCase("TripleDES") && this.algorithm.equalsIgnoreCase("DESede"))) && MessageDigest.isEqual(this.key, ((SecretKey)o).getEncoded());
    }
}
