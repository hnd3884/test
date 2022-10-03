package sun.security.internal.spec;

import javax.crypto.SecretKey;
import java.security.spec.AlgorithmParameterSpec;

@Deprecated
public class TlsKeyMaterialParameterSpec implements AlgorithmParameterSpec
{
    private final SecretKey masterSecret;
    private final int majorVersion;
    private final int minorVersion;
    private final byte[] clientRandom;
    private final byte[] serverRandom;
    private final String cipherAlgorithm;
    private final int cipherKeyLength;
    private final int ivLength;
    private final int macKeyLength;
    private final int expandedCipherKeyLength;
    private final String prfHashAlg;
    private final int prfHashLength;
    private final int prfBlockSize;
    
    public TlsKeyMaterialParameterSpec(final SecretKey masterSecret, final int n, final int n2, final byte[] array, final byte[] array2, final String cipherAlgorithm, final int n3, final int n4, final int n5, final int n6, final String prfHashAlg, final int prfHashLength, final int prfBlockSize) {
        if (!masterSecret.getAlgorithm().equals("TlsMasterSecret")) {
            throw new IllegalArgumentException("Not a TLS master secret");
        }
        if (cipherAlgorithm == null) {
            throw new NullPointerException();
        }
        this.masterSecret = masterSecret;
        this.majorVersion = TlsMasterSecretParameterSpec.checkVersion(n);
        this.minorVersion = TlsMasterSecretParameterSpec.checkVersion(n2);
        this.clientRandom = array.clone();
        this.serverRandom = array2.clone();
        this.cipherAlgorithm = cipherAlgorithm;
        this.cipherKeyLength = checkSign(n3);
        this.expandedCipherKeyLength = checkSign(n4);
        this.ivLength = checkSign(n5);
        this.macKeyLength = checkSign(n6);
        this.prfHashAlg = prfHashAlg;
        this.prfHashLength = prfHashLength;
        this.prfBlockSize = prfBlockSize;
    }
    
    private static int checkSign(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Value must not be negative");
        }
        return n;
    }
    
    public SecretKey getMasterSecret() {
        return this.masterSecret;
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
    
    public String getCipherAlgorithm() {
        return this.cipherAlgorithm;
    }
    
    public int getCipherKeyLength() {
        return this.cipherKeyLength;
    }
    
    public int getExpandedCipherKeyLength() {
        if (this.majorVersion >= 3 && this.minorVersion >= 2) {
            return 0;
        }
        return this.expandedCipherKeyLength;
    }
    
    public int getIvLength() {
        return this.ivLength;
    }
    
    public int getMacKeyLength() {
        return this.macKeyLength;
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
