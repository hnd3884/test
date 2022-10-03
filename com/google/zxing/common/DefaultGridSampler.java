package com.google.zxing.common;

import com.google.zxing.NotFoundException;

public final class DefaultGridSampler extends GridSampler
{
    @Override
    public BitMatrix sampleGrid(final BitMatrix image, final int dimensionX, final int dimensionY, final float p1ToX, final float p1ToY, final float p2ToX, final float p2ToY, final float p3ToX, final float p3ToY, final float p4ToX, final float p4ToY, final float p1FromX, final float p1FromY, final float p2FromX, final float p2FromY, final float p3FromX, final float p3FromY, final float p4FromX, final float p4FromY) throws NotFoundException {
        final PerspectiveTransform transform = PerspectiveTransform.quadrilateralToQuadrilateral(p1ToX, p1ToY, p2ToX, p2ToY, p3ToX, p3ToY, p4ToX, p4ToY, p1FromX, p1FromY, p2FromX, p2FromY, p3FromX, p3FromY, p4FromX, p4FromY);
        return this.sampleGrid(image, dimensionX, dimensionY, transform);
    }
    
    @Override
    public BitMatrix sampleGrid(final BitMatrix image, final int dimensionX, final int dimensionY, final PerspectiveTransform transform) throws NotFoundException {
        if (dimensionX <= 0 || dimensionY <= 0) {
            throw NotFoundException.getNotFoundInstance();
        }
        final BitMatrix bits = new BitMatrix(dimensionX, dimensionY);
        final float[] points = new float[dimensionX << 1];
        for (int y = 0; y < dimensionY; ++y) {
            final int max = points.length;
            final float iValue = y + 0.5f;
            for (int x = 0; x < max; x += 2) {
                points[x] = (x >> 1) + 0.5f;
                points[x + 1] = iValue;
            }
            transform.transformPoints(points);
            GridSampler.checkAndNudgePoints(image, points);
            try {
                for (int x = 0; x < max; x += 2) {
                    if (image.get((int)points[x], (int)points[x + 1])) {
                        bits.set(x >> 1, y);
                    }
                }
            }
            catch (final ArrayIndexOutOfBoundsException aioobe) {
                throw NotFoundException.getNotFoundInstance();
            }
        }
        return bits;
    }
}
