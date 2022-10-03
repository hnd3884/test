package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.util.Pack;

final class HashTreeAddress extends XMSSAddress
{
    private static final int TYPE = 2;
    private static final int PADDING = 0;
    private final int padding;
    private final int treeHeight;
    private final int treeIndex;
    
    private HashTreeAddress(final Builder builder) {
        super(builder);
        this.padding = 0;
        this.treeHeight = builder.treeHeight;
        this.treeIndex = builder.treeIndex;
    }
    
    @Override
    protected byte[] toByteArray() {
        final byte[] byteArray = super.toByteArray();
        Pack.intToBigEndian(this.padding, byteArray, 16);
        Pack.intToBigEndian(this.treeHeight, byteArray, 20);
        Pack.intToBigEndian(this.treeIndex, byteArray, 24);
        return byteArray;
    }
    
    protected int getPadding() {
        return this.padding;
    }
    
    protected int getTreeHeight() {
        return this.treeHeight;
    }
    
    protected int getTreeIndex() {
        return this.treeIndex;
    }
    
    protected static class Builder extends XMSSAddress.Builder<Builder>
    {
        private int treeHeight;
        private int treeIndex;
        
        protected Builder() {
            super(2);
            this.treeHeight = 0;
            this.treeIndex = 0;
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
            return new HashTreeAddress(this, null);
        }
        
        @Override
        protected Builder getThis() {
            return this;
        }
    }
}
