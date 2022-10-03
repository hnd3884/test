package sun.security.internal.spec;

import javax.crypto.SecretKey;
import java.security.spec.AlgorithmParameterSpec;

@Deprecated
public class TlsMasterSecretParameterSpec implements AlgorithmParameterSpec
{
    private final SecretKey premasterSecret;
    private final int majorVersion;
    private final int minorVersion;
    private final byte[] clientRandom;
    private final byte[] serverRandom;
    private final byte[] extendedMasterSecretSessionHash;
    private final String prfHashAlg;
    private final int prfHashLength;
    private final int prfBlockSize;
    
    public TlsMasterSecretParameterSpec(final SecretKey secretKey, final int n, final int n2, final byte[] array, final byte[] array2, final String s, final int n3, final int n4) {
        this(secretKey, n, n2, array, array2, new byte[0], s, n3, n4);
    }
    
    public TlsMasterSecretParameterSpec(final SecretKey secretKey, final int n, final int n2, final byte[] array, final String s, final int n3, final int n4) {
        this(secretKey, n, n2, new byte[0], new byte[0], array, s, n3, n4);
    }
    
    private TlsMasterSecretParameterSpec(final SecretKey premasterSecret, final int n, final int n2, final byte[] array, final byte[] array2, final byte[] array3, final String prfHashAlg, final int prfHashLength, final int prfBlockSize) {
        if (premasterSecret == null) {
            throw new NullPointerException("premasterSecret must not be null");
        }
        this.premasterSecret = premasterSecret;
        this.majorVersion = checkVersion(n);
        this.minorVersion = checkVersion(n2);
        this.clientRandom = array.clone();
        this.serverRandom = array2.clone();
        this.extendedMasterSecretSessionHash = ((array3 != null) ? array3.clone() : new byte[0]);
        this.prfHashAlg = prfHashAlg;
        this.prfHashLength = prfHashLength;
        this.prfBlockSize = prfBlockSize;
    }
    
    static int checkVersion(final int n) {
        if (n < 0 || n > 255) {
            throw new IllegalArgumentException("Version must be between 0 and 255");
        }
        return n;
    }
    
    public SecretKey getPremasterSecret() {
        return this.premasterSecret;
    }
    
    public int getMajorVersion() {
        return this.majorVersion;
    }
    
    public int getMinorVersion() {
        return this.minorVersion;
    }
    
    public byte[] getClientRandom() {
        return this.clientRandom.clone();
    }
    
    public byte[] getServerRandom() {
        return this.serverRandom.clone();
    }
    
    public byte[] getExtendedMasterSecretSessionHash() {
        return this.extendedMasterSecretSessionHash.clone();
    }
    
    public String getPRFHashAlg() {
        return this.prfHashAlg;
    }
    
    public int getPRFHashLength() {
        return this.prfHashLength;
    }
    
    public int getPRFBlockSize() {
        return this.prfBlockSize;
    }
}
