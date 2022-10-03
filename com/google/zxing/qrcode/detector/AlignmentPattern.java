package com.google.zxing.qrcode.detector;

import com.google.zxing.ResultPoint;

public final class AlignmentPattern extends ResultPoint
{
    private final float estimatedModuleSize;
    
    AlignmentPattern(final float posX, final float posY, final float estimatedModuleSize) {
        super(posX, posY);
        this.estimatedModuleSize = estimatedModuleSize;
    }
    
    boolean aboutEquals(final float moduleSize, final float i, final float j) {
        if (Math.abs(i - this.getY()) <= moduleSize && Math.abs(j - this.getX()) <= moduleSize) {
            final float moduleSizeDiff = Math.abs(moduleSize - this.estimatedModuleSize);
            return moduleSizeDiff <= 1.0f || moduleSizeDiff <= this.estimatedModuleSize;
        }
        return false;
    }
    
    AlignmentPattern combineEstimate(final float i, final float j, final float newModuleSize) {
        final float combinedX = (this.getX() + j) / 2.0f;
        final float combinedY = (this.getY() + i) / 2.0f;
        final float combinedModuleSize = (this.estimatedModuleSize + newModuleSize) / 2.0f;
        return new AlignmentPattern(combinedX, combinedY, combinedModuleSize);
    }
}
