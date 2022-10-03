package com.google.zxing.common;

import com.google.zxing.NotFoundException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Binarizer;

public class GlobalHistogramBinarizer extends Binarizer
{
    private static final int LUMINANCE_BITS = 5;
    private static final int LUMINANCE_SHIFT = 3;
    private static final int LUMINANCE_BUCKETS = 32;
    private byte[] luminances;
    private final int[] buckets;
    
    public GlobalHistogramBinarizer(final LuminanceSource source) {
        super(source);
        this.luminances = new byte[0];
        this.buckets = new int[32];
    }
    
    @Override
    public BitArray getBlackRow(final int y, BitArray row) throws NotFoundException {
        final LuminanceSource source = this.getLuminanceSource();
        final int width = source.getWidth();
        if (row == null || row.getSize() < width) {
            row = new BitArray(width);
        }
        else {
            row.clear();
        }
        this.initArrays(width);
        final byte[] localLuminances = source.getRow(y, this.luminances);
        final int[] localBuckets = this.buckets;
        for (int x = 0; x < width; ++x) {
            final int pixel = localLuminances[x] & 0xFF;
            final int[] array = localBuckets;
            final int n = pixel >> 3;
            ++array[n];
        }
        final int blackPoint = estimateBlackPoint(localBuckets);
        int left = localLuminances[0] & 0xFF;
        int center = localLuminances[1] & 0xFF;
        for (int x2 = 1; x2 < width - 1; ++x2) {
            final int right = localLuminances[x2 + 1] & 0xFF;
            final int luminance = (center << 2) - left - right >> 1;
            if (luminance < blackPoint) {
                row.set(x2);
            }
            left = center;
            center = right;
        }
        return row;
    }
    
    @Override
    public BitMatrix getBlackMatrix() throws NotFoundException {
        final LuminanceSource source = this.getLuminanceSource();
        final int width = source.getWidth();
        final int height = source.getHeight();
        final BitMatrix matrix = new BitMatrix(width, height);
        this.initArrays(width);
        final int[] localBuckets = this.buckets;
        for (int y = 1; y < 5; ++y) {
            final int row = height * y / 5;
            final byte[] localLuminances = source.getRow(row, this.luminances);
            for (int right = (width << 2) / 5, x = width / 5; x < right; ++x) {
                final int pixel = localLuminances[x] & 0xFF;
                final int[] array = localBuckets;
                final int n = pixel >> 3;
                ++array[n];
            }
        }
        final int blackPoint = estimateBlackPoint(localBuckets);
        final byte[] localLuminances2 = source.getMatrix();
        for (int y2 = 0; y2 < height; ++y2) {
            final int offset = y2 * width;
            for (int x = 0; x < width; ++x) {
                final int pixel = localLuminances2[offset + x] & 0xFF;
                if (pixel < blackPoint) {
                    matrix.set(x, y2);
                }
            }
        }
        return matrix;
    }
    
    @Override
    public Binarizer createBinarizer(final LuminanceSource source) {
        return new GlobalHistogramBinarizer(source);
    }
    
    private void initArrays(final int luminanceSize) {
        if (this.luminances.length < luminanceSize) {
            this.luminances = new byte[luminanceSize];
        }
        for (int x = 0; x < 32; ++x) {
            this.buckets[x] = 0;
        }
    }
    
    private static int estimateBlackPoint(final int[] buckets) throws NotFoundException {
        final int numBuckets = buckets.length;
        int maxBucketCount = 0;
        int firstPeak = 0;
        int firstPeakSize = 0;
        for (int x = 0; x < numBuckets; ++x) {
            if (buckets[x] > firstPeakSize) {
                firstPeak = x;
                firstPeakSize = buckets[x];
            }
            if (buckets[x] > maxBucketCount) {
                maxBucketCount = buckets[x];
            }
        }
        int secondPeak = 0;
        int secondPeakScore = 0;
        for (int x2 = 0; x2 < numBuckets; ++x2) {
            final int distanceToBiggest = x2 - firstPeak;
            final int score = buckets[x2] * distanceToBiggest * distanceToBiggest;
            if (score > secondPeakScore) {
                secondPeak = x2;
                secondPeakScore = score;
            }
        }
        if (firstPeak > secondPeak) {
            final int temp = firstPeak;
            firstPeak = secondPeak;
            secondPeak = temp;
        }
        if (secondPeak - firstPeak <= numBuckets >> 4) {
            throw NotFoundException.getNotFoundInstance();
        }
        int bestValley = secondPeak - 1;
        int bestValleyScore = -1;
        for (int x3 = secondPeak - 1; x3 > firstPeak; --x3) {
            final int fromFirst = x3 - firstPeak;
            final int score2 = fromFirst * fromFirst * (secondPeak - x3) * (maxBucketCount - buckets[x3]);
            if (score2 > bestValleyScore) {
                bestValley = x3;
                bestValleyScore = score2;
            }
        }
        return bestValley << 3;
    }
}
