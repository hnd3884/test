package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.util.Pack;

final class OTSHashAddress extends XMSSAddress
{
    private static final int TYPE = 0;
    private final int otsAddress;
    private final int chainAddress;
    private final int hashAddress;
    
    private OTSHashAddress(final Builder builder) {
        super(builder);
        this.otsAddress = builder.otsAddress;
        this.chainAddress = builder.chainAddress;
        this.hashAddress = builder.hashAddress;
    }
    
    @Override
    protected byte[] toByteArray() {
        final byte[] byteArray = super.toByteArray();
        Pack.intToBigEndian(this.otsAddress, byteArray, 16);
        Pack.intToBigEndian(this.chainAddress, byteArray, 20);
        Pack.intToBigEndian(this.hashAddress, byteArray, 24);
        return byteArray;
    }
    
    protected int getOTSAddress() {
        return this.otsAddress;
    }
    
    protected int getChainAddress() {
        return this.chainAddress;
    }
    
    protected int getHashAddress() {
        return this.hashAddress;
    }
    
    protected static class Builder extends XMSSAddress.Builder<Builder>
    {
        private int otsAddress;
        private int chainAddress;
        private int hashAddress;
        
        protected Builder() {
            super(0);
            this.otsAddress = 0;
            this.chainAddress = 0;
            this.hashAddress = 0;
        }
        
        protected Builder withOTSAddress(final int otsAddress) {
            this.otsAddress = otsAddress;
            return this;
        }
        
        protected Builder withChainAddress(final int chainAddress) {
            this.chainAddress = chainAddress;
            return this;
        }
        
        protected Builder withHashAddress(final int hashAddress) {
            this.hashAddress = hashAddress;
            return this;
        }
        
        @Override
        protected XMSSAddress build() {
            return new OTSHashAddress(this, null);
        }
        
        @Override
        protected Builder getThis() {
            return this;
        }
    }
}
