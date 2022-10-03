package com.google.zxing.common;

import com.google.zxing.Binarizer;
import com.google.zxing.NotFoundException;
import com.google.zxing.LuminanceSource;

public final class HybridBinarizer extends GlobalHistogramBinarizer
{
    private static final int BLOCK_SIZE_POWER = 3;
    private static final int BLOCK_SIZE = 8;
    private static final int BLOCK_SIZE_MASK = 7;
    private static final int MINIMUM_DIMENSION = 40;
    private BitMatrix matrix;
    
    public HybridBinarizer(final LuminanceSource source) {
        super(source);
    }
    
    @Override
    public BitMatrix getBlackMatrix() throws NotFoundException {
        if (this.matrix != null) {
            return this.matrix;
        }
        final LuminanceSource source = this.getLuminanceSource();
        if (source.getWidth() >= 40 && source.getHeight() >= 40) {
            final byte[] luminances = source.getMatrix();
            final int width = source.getWidth();
            final int height = source.getHeight();
            int subWidth = width >> 3;
            if ((width & 0x7) != 0x0) {
                ++subWidth;
            }
            int subHeight = height >> 3;
            if ((height & 0x7) != 0x0) {
                ++subHeight;
            }
            final int[][] blackPoints = calculateBlackPoints(luminances, subWidth, subHeight, width, height);
            final BitMatrix newMatrix = new BitMatrix(width, height);
            calculateThresholdForBlock(luminances, subWidth, subHeight, width, height, blackPoints, newMatrix);
            this.matrix = newMatrix;
        }
        else {
            this.matrix = super.getBlackMatrix();
        }
        return this.matrix;
    }
    
    @Override
    public Binarizer createBinarizer(final LuminanceSource source) {
        return new HybridBinarizer(source);
    }
    
    private static void calculateThresholdForBlock(final byte[] luminances, final int subWidth, final int subHeight, final int width, final int height, final int[][] blackPoints, final BitMatrix matrix) {
        for (int y = 0; y < subHeight; ++y) {
            int yoffset = y << 3;
            if (yoffset + 8 >= height) {
                yoffset = height - 8;
            }
            for (int x = 0; x < subWidth; ++x) {
                int xoffset = x << 3;
                if (xoffset + 8 >= width) {
                    xoffset = width - 8;
                }
                int left = (x > 1) ? x : 2;
                left = ((left < subWidth - 2) ? left : (subWidth - 3));
                int top = (y > 1) ? y : 2;
                top = ((top < subHeight - 2) ? top : (subHeight - 3));
                int sum = 0;
                for (int z = -2; z <= 2; ++z) {
                    final int[] blackRow = blackPoints[top + z];
                    sum += blackRow[left - 2] + blackRow[left - 1] + blackRow[left] + blackRow[left + 1] + blackRow[left + 2];
                }
                final int average = sum / 25;
                threshold8x8Block(luminances, xoffset, yoffset, average, width, matrix);
            }
        }
    }
    
    private static void threshold8x8Block(final byte[] luminances, final int xoffset, final int yoffset, final int threshold, final int stride, final BitMatrix matrix) {
        for (int y = 0, offset = yoffset * stride + xoffset; y < 8; ++y, offset += stride) {
            for (int x = 0; x < 8; ++x) {
                if ((luminances[offset + x] & 0xFF) <= threshold) {
                    matrix.set(xoffset + x, yoffset + y);
                }
            }
        }
    }
    
    private static int[][] calculateBlackPoints(final byte[] luminances, final int subWidth, final int subHeight, final int width, final int height) {
        final int[][] blackPoints = new int[subHeight][subWidth];
        for (int y = 0; y < subHeight; ++y) {
            int yoffset = y << 3;
            if (yoffset + 8 >= height) {
                yoffset = height - 8;
            }
            for (int x = 0; x < subWidth; ++x) {
                int xoffset = x << 3;
                if (xoffset + 8 >= width) {
                    xoffset = width - 8;
                }
                int sum = 0;
                int min = 255;
                int max = 0;
                for (int yy = 0, offset = yoffset * width + xoffset; yy < 8; ++yy, offset += width) {
                    for (int xx = 0; xx < 8; ++xx) {
                        final int pixel = luminances[offset + xx] & 0xFF;
                        sum += pixel;
                        if (pixel < min) {
                            min = pixel;
                        }
                        if (pixel > max) {
                            max = pixel;
                        }
                    }
                }
                int average = sum >> 6;
                if (max - min <= 24) {
                    average = min >> 1;
                    if (y > 0 && x > 0) {
                        final int averageNeighborBlackPoint = blackPoints[y - 1][x] + 2 * blackPoints[y][x - 1] + blackPoints[y - 1][x - 1] >> 2;
                        if (min < averageNeighborBlackPoint) {
                            average = averageNeighborBlackPoint;
                        }
                    }
                }
                blackPoints[y][x] = average;
            }
        }
        return blackPoints;
    }
}
