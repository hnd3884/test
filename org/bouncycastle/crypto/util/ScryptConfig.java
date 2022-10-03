package org.bouncycastle.crypto.util;

import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;

public class ScryptConfig extends PBKDFConfig
{
    private final int costParameter;
    private final int blockSize;
    private final int parallelizationParameter;
    private final int saltLength;
    
    private ScryptConfig(final Builder builder) {
        super(MiscObjectIdentifiers.id_scrypt);
        this.costParameter = builder.costParameter;
        this.blockSize = builder.blockSize;
        this.parallelizationParameter = builder.parallelizationParameter;
        this.saltLength = builder.saltLength;
    }
    
    public int getCostParameter() {
        return this.costParameter;
    }
    
    public int getBlockSize() {
        return this.blockSize;
    }
    
    public int getParallelizationParameter() {
        return this.parallelizationParameter;
    }
    
    public int getSaltLength() {
        return this.saltLength;
    }
    
    public static class Builder
    {
        private final int costParameter;
        private final int blockSize;
        private final int parallelizationParameter;
        private int saltLength;
        
        public Builder(final int costParameter, final int blockSize, final int parallelizationParameter) {
            this.saltLength = 16;
            if (costParameter <= 1 || !isPowerOf2(costParameter)) {
                throw new IllegalArgumentException("Cost parameter N must be > 1 and a power of 2");
            }
            this.costParameter = costParameter;
            this.blockSize = blockSize;
            this.parallelizationParameter = parallelizationParameter;
        }
        
        public Builder withSaltLength(final int saltLength) {
            this.saltLength = saltLength;
            return this;
        }
        
        public ScryptConfig build() {
            return new ScryptConfig(this, null);
        }
        
        private static boolean isPowerOf2(final int n) {
            return (n & n - 1) == 0x0;
        }
    }
}
