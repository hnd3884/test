package com.google.zxing.qrcode.detector;

import com.google.zxing.ResultPoint;

public final class FinderPattern extends ResultPoint
{
    private final float estimatedModuleSize;
    private int count;
    
    FinderPattern(final float posX, final float posY, final float estimatedModuleSize) {
        this(posX, posY, estimatedModuleSize, 1);
    }
    
    FinderPattern(final float posX, final float posY, final float estimatedModuleSize, final int count) {
        super(posX, posY);
        this.estimatedModuleSize = estimatedModuleSize;
        this.count = count;
    }
    
    public float getEstimatedModuleSize() {
        return this.estimatedModuleSize;
    }
    
    int getCount() {
        return this.count;
    }
    
    void incrementCount() {
        ++this.count;
    }
    
    boolean aboutEquals(final float moduleSize, final float i, final float j) {
        if (Math.abs(i - this.getY()) <= moduleSize && Math.abs(j - this.getX()) <= moduleSize) {
            final float moduleSizeDiff = Math.abs(moduleSize - this.estimatedModuleSize);
            return moduleSizeDiff <= 1.0f || moduleSizeDiff <= this.estimatedModuleSize;
        }
        return false;
    }
    
    FinderPattern combineEstimate(final float i, final float j, final float newModuleSize) {
        final int combinedCount = this.count + 1;
        final float combinedX = (this.count * this.getX() + j) / combinedCount;
        final float combinedY = (this.count * this.getY() + i) / combinedCount;
        final float combinedModuleSize = (this.count * this.estimatedModuleSize + newModuleSize) / combinedCount;
        return new FinderPattern(combinedX, combinedY, combinedModuleSize, combinedCount);
    }
}
