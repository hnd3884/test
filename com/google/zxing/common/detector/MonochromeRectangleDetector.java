package com.google.zxing.common.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;

public final class MonochromeRectangleDetector
{
    private static final int MAX_MODULES = 32;
    private final BitMatrix image;
    
    public MonochromeRectangleDetector(final BitMatrix image) {
        this.image = image;
    }
    
    public ResultPoint[] detect() throws NotFoundException {
        final int height = this.image.getHeight();
        final int width = this.image.getWidth();
        final int halfHeight = height >> 1;
        final int halfWidth = width >> 1;
        final int deltaY = Math.max(1, height / 256);
        final int deltaX = Math.max(1, width / 256);
        int top = 0;
        int bottom = height;
        int left = 0;
        int right = width;
        ResultPoint pointA = this.findCornerFromCenter(halfWidth, 0, left, right, halfHeight, -deltaY, top, bottom, halfWidth >> 1);
        top = (int)pointA.getY() - 1;
        final ResultPoint pointB = this.findCornerFromCenter(halfWidth, -deltaX, left, right, halfHeight, 0, top, bottom, halfHeight >> 1);
        left = (int)pointB.getX() - 1;
        final ResultPoint pointC = this.findCornerFromCenter(halfWidth, deltaX, left, right, halfHeight, 0, top, bottom, halfHeight >> 1);
        right = (int)pointC.getX() + 1;
        final ResultPoint pointD = this.findCornerFromCenter(halfWidth, 0, left, right, halfHeight, deltaY, top, bottom, halfWidth >> 1);
        bottom = (int)pointD.getY() + 1;
        pointA = this.findCornerFromCenter(halfWidth, 0, left, right, halfHeight, -deltaY, top, bottom, halfWidth >> 2);
        return new ResultPoint[] { pointA, pointB, pointC, pointD };
    }
    
    private ResultPoint findCornerFromCenter(final int centerX, final int deltaX, final int left, final int right, final int centerY, final int deltaY, final int top, final int bottom, final int maxWhiteRun) throws NotFoundException {
        int[] lastRange = null;
        int y = centerY;
        int x = centerX;
        while (y < bottom && y >= top && x < right && x >= left) {
            int[] range;
            if (deltaX == 0) {
                range = this.blackWhiteRange(y, maxWhiteRun, left, right, true);
            }
            else {
                range = this.blackWhiteRange(x, maxWhiteRun, top, bottom, false);
            }
            if (range == null) {
                if (lastRange == null) {
                    throw NotFoundException.getNotFoundInstance();
                }
                if (deltaX == 0) {
                    final int lastY = y - deltaY;
                    if (lastRange[0] >= centerX) {
                        return new ResultPoint((float)lastRange[1], (float)lastY);
                    }
                    if (lastRange[1] > centerX) {
                        return new ResultPoint((deltaY > 0) ? ((float)lastRange[0]) : ((float)lastRange[1]), (float)lastY);
                    }
                    return new ResultPoint((float)lastRange[0], (float)lastY);
                }
                else {
                    final int lastX = x - deltaX;
                    if (lastRange[0] >= centerY) {
                        return new ResultPoint((float)lastX, (float)lastRange[1]);
                    }
                    if (lastRange[1] > centerY) {
                        return new ResultPoint((float)lastX, (deltaX < 0) ? ((float)lastRange[0]) : ((float)lastRange[1]));
                    }
                    return new ResultPoint((float)lastX, (float)lastRange[0]);
                }
            }
            else {
                lastRange = range;
                y += deltaY;
                x += deltaX;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    private int[] blackWhiteRange(final int fixedDimension, final int maxWhiteRun, final int minDim, final int maxDim, final boolean horizontal) {
        int start;
        final int center = start = minDim + maxDim >> 1;
        while (start >= minDim) {
            Label_0058: {
                if (horizontal) {
                    if (!this.image.get(start, fixedDimension)) {
                        break Label_0058;
                    }
                }
                else if (!this.image.get(fixedDimension, start)) {
                    break Label_0058;
                }
                --start;
                continue;
            }
            final int whiteRunStart = start;
            while (--start >= minDim) {
                if (horizontal) {
                    if (this.image.get(start, fixedDimension)) {
                        break;
                    }
                    continue;
                }
                else {
                    if (this.image.get(fixedDimension, start)) {
                        break;
                    }
                    continue;
                }
            }
            final int whiteRunSize = whiteRunStart - start;
            if (start < minDim || whiteRunSize > maxWhiteRun) {
                start = whiteRunStart;
                break;
            }
        }
        ++start;
        int end = center;
        while (end < maxDim) {
            Label_0188: {
                if (horizontal) {
                    if (!this.image.get(end, fixedDimension)) {
                        break Label_0188;
                    }
                }
                else if (!this.image.get(fixedDimension, end)) {
                    break Label_0188;
                }
                ++end;
                continue;
            }
            final int whiteRunStart2 = end;
            while (++end < maxDim) {
                if (horizontal) {
                    if (this.image.get(end, fixedDimension)) {
                        break;
                    }
                    continue;
                }
                else {
                    if (this.image.get(fixedDimension, end)) {
                        break;
                    }
                    continue;
                }
            }
            final int whiteRunSize2 = end - whiteRunStart2;
            if (end >= maxDim || whiteRunSize2 > maxWhiteRun) {
                end = whiteRunStart2;
                break;
            }
        }
        return (int[])((--end > start) ? new int[] { start, end } : null);
    }
}
