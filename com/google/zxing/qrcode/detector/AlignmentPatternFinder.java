package com.google.zxing.qrcode.detector;

import java.util.Iterator;
import com.google.zxing.ResultPoint;
import com.google.zxing.NotFoundException;
import java.util.ArrayList;
import com.google.zxing.ResultPointCallback;
import java.util.List;
import com.google.zxing.common.BitMatrix;

final class AlignmentPatternFinder
{
    private final BitMatrix image;
    private final List<AlignmentPattern> possibleCenters;
    private final int startX;
    private final int startY;
    private final int width;
    private final int height;
    private final float moduleSize;
    private final int[] crossCheckStateCount;
    private final ResultPointCallback resultPointCallback;
    
    AlignmentPatternFinder(final BitMatrix image, final int startX, final int startY, final int width, final int height, final float moduleSize, final ResultPointCallback resultPointCallback) {
        this.image = image;
        this.possibleCenters = new ArrayList<AlignmentPattern>(5);
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.moduleSize = moduleSize;
        this.crossCheckStateCount = new int[3];
        this.resultPointCallback = resultPointCallback;
    }
    
    AlignmentPattern find() throws NotFoundException {
        final int startX = this.startX;
        final int height = this.height;
        final int maxJ = startX + this.width;
        final int middleI = this.startY + (height >> 1);
        final int[] stateCount = new int[3];
        for (int iGen = 0; iGen < height; ++iGen) {
            final int i = middleI + (((iGen & 0x1) == 0x0) ? (iGen + 1 >> 1) : (-(iGen + 1 >> 1)));
            stateCount[0] = 0;
            stateCount[2] = (stateCount[1] = 0);
            int j;
            for (j = startX; j < maxJ && !this.image.get(j, i); ++j) {}
            int currentState = 0;
            while (j < maxJ) {
                if (this.image.get(j, i)) {
                    if (currentState == 1) {
                        final int[] array = stateCount;
                        final int n = currentState;
                        ++array[n];
                    }
                    else if (currentState == 2) {
                        if (this.foundPatternCross(stateCount)) {
                            final AlignmentPattern confirmed = this.handlePossibleCenter(stateCount, i, j);
                            if (confirmed != null) {
                                return confirmed;
                            }
                        }
                        stateCount[0] = stateCount[2];
                        stateCount[1] = 1;
                        stateCount[2] = 0;
                        currentState = 1;
                    }
                    else {
                        final int[] array2 = stateCount;
                        final int n2 = ++currentState;
                        ++array2[n2];
                    }
                }
                else {
                    if (currentState == 1) {
                        ++currentState;
                    }
                    final int[] array3 = stateCount;
                    final int n3 = currentState;
                    ++array3[n3];
                }
                ++j;
            }
            if (this.foundPatternCross(stateCount)) {
                final AlignmentPattern confirmed = this.handlePossibleCenter(stateCount, i, maxJ);
                if (confirmed != null) {
                    return confirmed;
                }
            }
        }
        if (!this.possibleCenters.isEmpty()) {
            return this.possibleCenters.get(0);
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    private static float centerFromEnd(final int[] stateCount, final int end) {
        return end - stateCount[2] - stateCount[1] / 2.0f;
    }
    
    private boolean foundPatternCross(final int[] stateCount) {
        final float moduleSize = this.moduleSize;
        final float maxVariance = moduleSize / 2.0f;
        for (int i = 0; i < 3; ++i) {
            if (Math.abs(moduleSize - stateCount[i]) >= maxVariance) {
                return false;
            }
        }
        return true;
    }
    
    private float crossCheckVertical(final int startI, final int centerJ, final int maxCount, final int originalStateCountTotal) {
        final BitMatrix image = this.image;
        final int maxI = image.getHeight();
        final int[] stateCount = this.crossCheckStateCount;
        stateCount[0] = 0;
        stateCount[2] = (stateCount[1] = 0);
        int i;
        for (i = startI; i >= 0 && image.get(centerJ, i) && stateCount[1] <= maxCount; --i) {
            final int[] array = stateCount;
            final int n = 1;
            ++array[n];
        }
        if (i < 0 || stateCount[1] > maxCount) {
            return Float.NaN;
        }
        while (i >= 0 && !image.get(centerJ, i) && stateCount[0] <= maxCount) {
            final int[] array2 = stateCount;
            final int n2 = 0;
            ++array2[n2];
            --i;
        }
        if (stateCount[0] > maxCount) {
            return Float.NaN;
        }
        for (i = startI + 1; i < maxI && image.get(centerJ, i) && stateCount[1] <= maxCount; ++i) {
            final int[] array3 = stateCount;
            final int n3 = 1;
            ++array3[n3];
        }
        if (i == maxI || stateCount[1] > maxCount) {
            return Float.NaN;
        }
        while (i < maxI && !image.get(centerJ, i) && stateCount[2] <= maxCount) {
            final int[] array4 = stateCount;
            final int n4 = 2;
            ++array4[n4];
            ++i;
        }
        if (stateCount[2] > maxCount) {
            return Float.NaN;
        }
        final int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2];
        if (5 * Math.abs(stateCountTotal - originalStateCountTotal) >= 2 * originalStateCountTotal) {
            return Float.NaN;
        }
        return this.foundPatternCross(stateCount) ? centerFromEnd(stateCount, i) : Float.NaN;
    }
    
    private AlignmentPattern handlePossibleCenter(final int[] stateCount, final int i, final int j) {
        final int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2];
        final float centerJ = centerFromEnd(stateCount, j);
        final float centerI = this.crossCheckVertical(i, (int)centerJ, 2 * stateCount[1], stateCountTotal);
        if (!Float.isNaN(centerI)) {
            final float estimatedModuleSize = (stateCount[0] + stateCount[1] + stateCount[2]) / 3.0f;
            for (final AlignmentPattern center : this.possibleCenters) {
                if (center.aboutEquals(estimatedModuleSize, centerI, centerJ)) {
                    return center.combineEstimate(centerI, centerJ, estimatedModuleSize);
                }
            }
            final AlignmentPattern point = new AlignmentPattern(centerJ, centerI, estimatedModuleSize);
            this.possibleCenters.add(point);
            if (this.resultPointCallback != null) {
                this.resultPointCallback.foundPossibleResultPoint(point);
            }
        }
        return null;
    }
}
