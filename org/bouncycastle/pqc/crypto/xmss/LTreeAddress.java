package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.util.Pack;

final class LTreeAddress extends XMSSAddress
{
    private static final int TYPE = 1;
    private final int lTreeAddress;
    private final int treeHeight;
    private final int treeIndex;
    
    private LTreeAddress(final Builder builder) {
        super(builder);
        this.lTreeAddress = builder.lTreeAddress;
        this.treeHeight = builder.treeHeight;
        this.treeIndex = builder.treeIndex;
    }
    
    @Override
    protected byte[] toByteArray() {
        final byte[] byteArray = super.toByteArray();
        Pack.intToBigEndian(this.lTreeAddress, byteArray, 16);
        Pack.intToBigEndian(this.treeHeight, byteArray, 20);
        Pack.intToBigEndian(this.treeIndex, byteArray, 24);
        return byteArray;
    }
    
    protected int getLTreeAddress() {
        return this.lTreeAddress;
    }
    
    protected int getTreeHeight() {
        return this.treeHeight;
    }
    
    protected int getTreeIndex() {
        return this.treeIndex;
    }
    
    protected static class Builder extends XMSSAddress.Builder<Builder>
    {
        private int lTreeAddress;
        private int treeHeight;
        private int treeIndex;
        
        protected Builder() {
            super(1);
            this.lTreeAddress = 0;
            this.treeHeight = 0;
            this.treeIndex = 0;
        }
        
        protected Builder withLTreeAddress(final int lTreeAddress) {
            this.lTreeAddress = lTreeAddress;
            return this;
        }
        
        protected Builder withTreeHeight(final int treeHeight) {
            this.treeHeight = treeHeight;
            return this;
        }
        
        protected Builder withTreeIndex(final int treeIndex) {
            this.treeIndex = treeIndex;
            return this;
        }
        
        @Override
        protected XMSSAddress build() {
            return new LTreeAddress(this, null);
        }
        
        @Override
        protected Builder getThis() {
            return this;
        }
    }
}
