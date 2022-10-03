package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.util.Pack;

public abstract class XMSSAddress
{
    private final int layerAddress;
    private final long treeAddress;
    private final int type;
    private final int keyAndMask;
    
    protected XMSSAddress(final Builder builder) {
        this.layerAddress = builder.layerAddress;
        this.treeAddress = builder.treeAddress;
        this.type = builder.type;
        this.keyAndMask = builder.keyAndMask;
    }
    
    protected byte[] toByteArray() {
        final byte[] array = new byte[32];
        Pack.intToBigEndian(this.layerAddress, array, 0);
        Pack.longToBigEndian(this.treeAddress, array, 4);
        Pack.intToBigEndian(this.type, array, 12);
        Pack.intToBigEndian(this.keyAndMask, array, 28);
        return array;
    }
    
    protected final int getLayerAddress() {
        return this.layerAddress;
    }
    
    protected final long getTreeAddress() {
        return this.treeAddress;
    }
    
    public final int getType() {
        return this.type;
    }
    
    public final int getKeyAndMask() {
        return this.keyAndMask;
    }
    
    protected abstract static class Builder<T extends Builder>
    {
        private final int type;
        private int layerAddress;
        private long treeAddress;
        private int keyAndMask;
        
        protected Builder(final int type) {
            this.layerAddress = 0;
            this.treeAddress = 0L;
            this.keyAndMask = 0;
            this.type = type;
        }
        
        protected T withLayerAddress(final int layerAddress) {
            this.layerAddress = layerAddress;
            return this.getThis();
        }
        
        protected T withTreeAddress(final long treeAddress) {
            this.treeAddress = treeAddress;
            return this.getThis();
        }
        
        protected T withKeyAndMask(final int keyAndMask) {
            this.keyAndMask = keyAndMask;
            return this.getThis();
        }
        
        protected abstract XMSSAddress build();
        
        protected abstract T getThis();
    }
}
