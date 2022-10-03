package org.openjsse.sun.security.internal.spec;

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
    
    public TlsMasterSecretParameterSpec(final SecretKey premasterSecret, final int majorVersion, final int minorVersion, final byte[] clientRandom, final byte[] serverRandom, final String prfHashAlg, final int prfHashLength, final int prfBlockSize) {
        this(premasterSecret, majorVersion, minorVersion, clientRandom, serverRandom, new byte[0], prfHashAlg, prfHashLength, prfBlockSize);
    }
    
    public TlsMasterSecretParameterSpec(final SecretKey premasterSecret, final int majorVersion, final int minorVersion, final byte[] extendedMasterSecretSessionHash, final String prfHashAlg, final int prfHashLength, final int prfBlockSize) {
        this(premasterSecret, majorVersion, minorVersion, new byte[0], new byte[0], extendedMasterSecretSessionHash, prfHashAlg, prfHashLength, prfBlockSize);
    }
    
    private TlsMasterSecretParameterSpec(final SecretKey premasterSecret, final int majorVersion, final int minorVersion, final byte[] clientRandom, final byte[] serverRandom, final byte[] extendedMasterSecretSessionHash, final String prfHashAlg, final int prfHashLength, final int prfBlockSize) {
        if (premasterSecret == null) {
            throw new NullPointerException("premasterSecret must not be null");
        }
        this.premasterSecret = premasterSecret;
        this.majorVersion = checkVersion(majorVersion);
        this.minorVersion = checkVersion(minorVersion);
        this.clientRandom = clientRandom.clone();
        this.serverRandom = serverRandom.clone();
        this.extendedMasterSecretSessionHash = ((extendedMasterSecretSessionHash != null) ? extendedMasterSecretSessionHash.clone() : new byte[0]);
        this.prfHashAlg = prfHashAlg;
        this.prfHashLength = prfHashLength;
        this.prfBlockSize = prfBlockSize;
    }
    
    static int checkVersion(final int version) {
        if (version < 0 || version > 255) {
            throw new IllegalArgumentException("Version must be between 0 and 255");
        }
        return version;
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
