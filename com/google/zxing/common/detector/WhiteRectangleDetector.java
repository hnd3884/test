package com.google.zxing.common.detector;

import com.google.zxing.ResultPoint;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitMatrix;

public final class WhiteRectangleDetector
{
    private static final int INIT_SIZE = 30;
    private static final int CORR = 1;
    private final BitMatrix image;
    private final int height;
    private final int width;
    private final int leftInit;
    private final int rightInit;
    private final int downInit;
    private final int upInit;
    
    public WhiteRectangleDetector(final BitMatrix image) throws NotFoundException {
        this.image = image;
        this.height = image.getHeight();
        this.width = image.getWidth();
        this.leftInit = this.width - 30 >> 1;
        this.rightInit = this.width + 30 >> 1;
        this.upInit = this.height - 30 >> 1;
        this.downInit = this.height + 30 >> 1;
        if (this.upInit < 0 || this.leftInit < 0 || this.downInit >= this.height || this.rightInit >= this.width) {
            throw NotFoundException.getNotFoundInstance();
        }
    }
    
    public WhiteRectangleDetector(final BitMatrix image, final int initSize, final int x, final int y) throws NotFoundException {
        this.image = image;
        this.height = image.getHeight();
        this.width = image.getWidth();
        final int halfsize = initSize >> 1;
        this.leftInit = x - halfsize;
        this.rightInit = x + halfsize;
        this.upInit = y - halfsize;
        this.downInit = y + halfsize;
        if (this.upInit < 0 || this.leftInit < 0 || this.downInit >= this.height || this.rightInit >= this.width) {
            throw NotFoundException.getNotFoundInstance();
        }
    }
    
    public ResultPoint[] detect() throws NotFoundException {
        int left = this.leftInit;
        int right = this.rightInit;
        int up = this.upInit;
        int down = this.downInit;
        boolean sizeExceeded = false;
        boolean aBlackPointFoundOnBorder = true;
        boolean atLeastOneBlackPointFoundOnBorder = false;
        while (aBlackPointFoundOnBorder) {
            aBlackPointFoundOnBorder = false;
            for (boolean rightBorderNotWhite = true; rightBorderNotWhite && right < this.width; ++right, aBlackPointFoundOnBorder = true) {
                rightBorderNotWhite = this.containsBlackPoint(up, down, right, false);
                if (rightBorderNotWhite) {}
            }
            if (right >= this.width) {
                sizeExceeded = true;
                break;
            }
            for (boolean bottomBorderNotWhite = true; bottomBorderNotWhite && down < this.height; ++down, aBlackPointFoundOnBorder = true) {
                bottomBorderNotWhite = this.containsBlackPoint(left, right, down, true);
                if (bottomBorderNotWhite) {}
            }
            if (down >= this.height) {
                sizeExceeded = true;
                break;
            }
            for (boolean leftBorderNotWhite = true; leftBorderNotWhite && left >= 0; --left, aBlackPointFoundOnBorder = true) {
                leftBorderNotWhite = this.containsBlackPoint(up, down, left, false);
                if (leftBorderNotWhite) {}
            }
            if (left < 0) {
                sizeExceeded = true;
                break;
            }
            for (boolean topBorderNotWhite = true; topBorderNotWhite && up >= 0; --up, aBlackPointFoundOnBorder = true) {
                topBorderNotWhite = this.containsBlackPoint(left, right, up, true);
                if (topBorderNotWhite) {}
            }
            if (up < 0) {
                sizeExceeded = true;
                break;
            }
            if (!aBlackPointFoundOnBorder) {
                continue;
            }
            atLeastOneBlackPointFoundOnBorder = true;
        }
        if (sizeExceeded || !atLeastOneBlackPointFoundOnBorder) {
            throw NotFoundException.getNotFoundInstance();
        }
        final int maxSize = right - left;
        ResultPoint z = null;
        for (int i = 1; i < maxSize; ++i) {
            z = this.getBlackPointOnSegment((float)left, (float)(down - i), (float)(left + i), (float)down);
            if (z != null) {
                break;
            }
        }
        if (z == null) {
            throw NotFoundException.getNotFoundInstance();
        }
        ResultPoint t = null;
        for (int j = 1; j < maxSize; ++j) {
            t = this.getBlackPointOnSegment((float)left, (float)(up + j), (float)(left + j), (float)up);
            if (t != null) {
                break;
            }
        }
        if (t == null) {
            throw NotFoundException.getNotFoundInstance();
        }
        ResultPoint x = null;
        for (int k = 1; k < maxSize; ++k) {
            x = this.getBlackPointOnSegment((float)right, (float)(up + k), (float)(right - k), (float)up);
            if (x != null) {
                break;
            }
        }
        if (x == null) {
            throw NotFoundException.getNotFoundInstance();
        }
        ResultPoint y = null;
        for (int l = 1; l < maxSize; ++l) {
            y = this.getBlackPointOnSegment((float)right, (float)(down - l), (float)(right - l), (float)down);
            if (y != null) {
                break;
            }
        }
        if (y == null) {
            throw NotFoundException.getNotFoundInstance();
        }
        return this.centerEdges(y, z, x, t);
    }
    
    private static int round(final float d) {
        return (int)(d + 0.5f);
    }
    
    private ResultPoint getBlackPointOnSegment(final float aX, final float aY, final float bX, final float bY) {
        final int dist = distanceL2(aX, aY, bX, bY);
        final float xStep = (bX - aX) / dist;
        final float yStep = (bY - aY) / dist;
        for (int i = 0; i < dist; ++i) {
            final int x = round(aX + i * xStep);
            final int y = round(aY + i * yStep);
            if (this.image.get(x, y)) {
                return new ResultPoint((float)x, (float)y);
            }
        }
        return null;
    }
    
    private static int distanceL2(final float aX, final float aY, final float bX, final float bY) {
        final float xDiff = aX - bX;
        final float yDiff = aY - bY;
        return round((float)Math.sqrt(xDiff * xDiff + yDiff * yDiff));
    }
    
    private ResultPoint[] centerEdges(final ResultPoint y, final ResultPoint z, final ResultPoint x, final ResultPoint t) {
        final float yi = y.getX();
        final float yj = y.getY();
        final float zi = z.getX();
        final float zj = z.getY();
        final float xi = x.getX();
        final float xj = x.getY();
        final float ti = t.getX();
        final float tj = t.getY();
        if (yi < this.width / 2) {
            return new ResultPoint[] { new ResultPoint(ti - 1.0f, tj + 1.0f), new ResultPoint(zi + 1.0f, zj + 1.0f), new ResultPoint(xi - 1.0f, xj - 1.0f), new ResultPoint(yi + 1.0f, yj - 1.0f) };
        }
        return new ResultPoint[] { new ResultPoint(ti + 1.0f, tj + 1.0f), new ResultPoint(zi + 1.0f, zj - 1.0f), new ResultPoint(xi - 1.0f, xj + 1.0f), new ResultPoint(yi - 1.0f, yj - 1.0f) };
    }
    
    private boolean containsBlackPoint(final int a, final int b, final int fixed, final boolean horizontal) {
        if (horizontal) {
            for (int x = a; x <= b; ++x) {
                if (this.image.get(x, fixed)) {
                    return true;
                }
            }
        }
        else {
            for (int y = a; y <= b; ++y) {
                if (this.image.get(fixed, y)) {
                    return true;
                }
            }
        }
        return false;
    }
}
