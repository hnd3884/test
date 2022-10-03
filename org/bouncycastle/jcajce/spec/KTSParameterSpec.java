package org.bouncycastle.jcajce.spec;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.spec.AlgorithmParameterSpec;

public class KTSParameterSpec implements AlgorithmParameterSpec
{
    private final String wrappingKeyAlgorithm;
    private final int keySizeInBits;
    private final AlgorithmParameterSpec parameterSpec;
    private final AlgorithmIdentifier kdfAlgorithm;
    private byte[] otherInfo;
    
    private KTSParameterSpec(final String wrappingKeyAlgorithm, final int keySizeInBits, final AlgorithmParameterSpec parameterSpec, final AlgorithmIdentifier kdfAlgorithm, final byte[] otherInfo) {
        this.wrappingKeyAlgorithm = wrappingKeyAlgorithm;
        this.keySizeInBits = keySizeInBits;
        this.parameterSpec = parameterSpec;
        this.kdfAlgorithm = kdfAlgorithm;
        this.otherInfo = otherInfo;
    }
    
    public String getKeyAlgorithmName() {
        return this.wrappingKeyAlgorithm;
    }
    
    public int getKeySize() {
        return this.keySizeInBits;
    }
    
    public AlgorithmParameterSpec getParameterSpec() {
        return this.parameterSpec;
    }
    
    public AlgorithmIdentifier getKdfAlgorithm() {
        return this.kdfAlgorithm;
    }
    
    public byte[] getOtherInfo() {
        return Arrays.clone(this.otherInfo);
    }
    
    public static final class Builder
    {
        private final String algorithmName;
        private final int keySizeInBits;
        private AlgorithmParameterSpec parameterSpec;
        private AlgorithmIdentifier kdfAlgorithm;
        private byte[] otherInfo;
        
        public Builder(final String s, final int n) {
            this(s, n, null);
        }
        
        public Builder(final String algorithmName, final int keySizeInBits, final byte[] array) {
            this.algorithmName = algorithmName;
            this.keySizeInBits = keySizeInBits;
            this.kdfAlgorithm = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
            this.otherInfo = ((array == null) ? new byte[0] : Arrays.clone(array));
        }
        
        public Builder withParameterSpec(final AlgorithmParameterSpec parameterSpec) {
            this.parameterSpec = parameterSpec;
            return this;
        }
        
        public Builder withKdfAlgorithm(final AlgorithmIdentifier kdfAlgorithm) {
            this.kdfAlgorithm = kdfAlgorithm;
            return this;
        }
        
        public KTSParameterSpec build() {
            return new KTSParameterSpec(this.algorithmName, this.keySizeInBits, this.parameterSpec, this.kdfAlgorithm, this.otherInfo, null);
        }
    }
}
