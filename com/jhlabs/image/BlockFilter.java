package com.jhlabs.image;

public class BlockFilter extends TransformFilter
{
    private int blockSize;
    
    public void setBlockSize(final int blockSize) {
        this.blockSize = blockSize;
    }
    
    public int getBlockSize() {
        return this.blockSize;
    }
    
    public BlockFilter() {
        this.blockSize = 2;
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        out[0] = (float)(x / this.blockSize * this.blockSize);
        out[1] = (float)(y / this.blockSize * this.blockSize);
    }
    
    @Override
    public String toString() {
        return "Stylize/Mosaic...";
    }
}
