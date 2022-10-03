package com.google.zxing.qrcode.detector;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import java.util.ArrayList;
import com.google.zxing.ResultPointCallback;
import java.util.List;
import com.google.zxing.common.BitMatrix;

public class FinderPatternFinder
{
    private static final int CENTER_QUORUM = 2;
    protected static final int MIN_SKIP = 3;
    protected static final int MAX_MODULES = 57;
    private static final int INTEGER_MATH_SHIFT = 8;
    private final BitMatrix image;
    private final List<FinderPattern> possibleCenters;
    private boolean hasSkipped;
    private final int[] crossCheckStateCount;
    private final ResultPointCallback resultPointCallback;
    
    public FinderPatternFinder(final BitMatrix image) {
        this(image, null);
    }
    
    public FinderPatternFinder(final BitMatrix image, final ResultPointCallback resultPointCallback) {
        this.image = image;
        this.possibleCenters = new ArrayList<FinderPattern>();
        this.crossCheckStateCount = new int[5];
        this.resultPointCallback = resultPointCallback;
    }
    
    protected BitMatrix getImage() {
        return this.image;
    }
    
    protected List<FinderPattern> getPossibleCenters() {
        return this.possibleCenters;
    }
    
    FinderPatternInfo find(final Map<DecodeHintType, ?> hints) throws NotFoundException {
        final boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
        final int maxI = this.image.getHeight();
        final int maxJ = this.image.getWidth();
        int iSkip = 3 * maxI / 228;
        if (iSkip < 3 || tryHarder) {
            iSkip = 3;
        }
        boolean done = false;
        final int[] stateCount = new int[5];
        for (int i = iSkip - 1; i < maxI && !done; i += iSkip) {
            stateCount[0] = 0;
            stateCount[2] = (stateCount[1] = 0);
            stateCount[4] = (stateCount[3] = 0);
            int currentState = 0;
            for (int j = 0; j < maxJ; ++j) {
                if (this.image.get(j, i)) {
                    if ((currentState & 0x1) == 0x1) {
                        ++currentState;
                    }
                    final int[] array = stateCount;
                    final int n = currentState;
                    ++array[n];
                }
                else if ((currentState & 0x1) == 0x0) {
                    if (currentState == 4) {
                        if (foundPatternCross(stateCount)) {
                            final boolean confirmed = this.handlePossibleCenter(stateCount, i, j);
                            if (confirmed) {
                                iSkip = 2;
                                if (this.hasSkipped) {
                                    done = this.haveMultiplyConfirmedCenters();
                                }
                                else {
                                    final int rowSkip = this.findRowSkip();
                                    if (rowSkip > stateCount[2]) {
                                        i += rowSkip - stateCount[2] - iSkip;
                                        j = maxJ - 1;
                                    }
                                }
                                currentState = 0;
                                stateCount[0] = 0;
                                stateCount[2] = (stateCount[1] = 0);
                                stateCount[4] = (stateCount[3] = 0);
                            }
                            else {
                                stateCount[0] = stateCount[2];
                                stateCount[1] = stateCount[3];
                                stateCount[2] = stateCount[4];
                                stateCount[3] = 1;
                                stateCount[4] = 0;
                                currentState = 3;
                            }
                        }
                        else {
                            stateCount[0] = stateCount[2];
                            stateCount[1] = stateCount[3];
                            stateCount[2] = stateCount[4];
                            stateCount[3] = 1;
                            stateCount[4] = 0;
                            currentState = 3;
                        }
                    }
                    else {
                        final int[] array2 = stateCount;
                        final int n2 = ++currentState;
                        ++array2[n2];
                    }
                }
                else {
                    final int[] array3 = stateCount;
                    final int n3 = currentState;
                    ++array3[n3];
                }
            }
            if (foundPatternCross(stateCount)) {
                final boolean confirmed2 = this.handlePossibleCenter(stateCount, i, maxJ);
                if (confirmed2) {
                    iSkip = stateCount[0];
                    if (this.hasSkipped) {
                        done = this.haveMultiplyConfirmedCenters();
                    }
                }
            }
        }
        final FinderPattern[] patternInfo = this.selectBestPatterns();
        ResultPoint.orderBestPatterns(patternInfo);
        return new FinderPatternInfo(patternInfo);
    }
    
    private static float centerFromEnd(final int[] stateCount, final int end) {
        return end - stateCount[4] - stateCount[3] - stateCount[2] / 2.0f;
    }
    
    protected static boolean foundPatternCross(final int[] stateCount) {
        int totalModuleSize = 0;
        for (int i = 0; i < 5; ++i) {
            final int count = stateCount[i];
            if (count == 0) {
                return false;
            }
            totalModuleSize += count;
        }
        if (totalModuleSize < 7) {
            return false;
        }
        final int moduleSize = (totalModuleSize << 8) / 7;
        final int maxVariance = moduleSize / 2;
        return Math.abs(moduleSize - (stateCount[0] << 8)) < maxVariance && Math.abs(moduleSize - (stateCount[1] << 8)) < maxVariance && Math.abs(3 * moduleSize - (stateCount[2] << 8)) < 3 * maxVariance && Math.abs(moduleSize - (stateCount[3] << 8)) < maxVariance && Math.abs(moduleSize - (stateCount[4] << 8)) < maxVariance;
    }
    
    private int[] getCrossCheckStateCount() {
        this.crossCheckStateCount[0] = 0;
        this.crossCheckStateCount[1] = 0;
        this.crossCheckStateCount[2] = 0;
        this.crossCheckStateCount[3] = 0;
        this.crossCheckStateCount[4] = 0;
        return this.crossCheckStateCount;
    }
    
    private float crossCheckVertical(final int startI, final int centerJ, final int maxCount, final int originalStateCountTotal) {
        final BitMatrix image = this.image;
        final int maxI = image.getHeight();
        final int[] stateCount = this.getCrossCheckStateCount();
        int i;
        for (i = startI; i >= 0 && image.get(centerJ, i); --i) {
            final int[] array = stateCount;
            final int n = 2;
            ++array[n];
        }
        if (i < 0) {
            return Float.NaN;
        }
        while (i >= 0 && !image.get(centerJ, i) && stateCount[1] <= maxCount) {
            final int[] array2 = stateCount;
            final int n2 = 1;
            ++array2[n2];
            --i;
        }
        if (i < 0 || stateCount[1] > maxCount) {
            return Float.NaN;
        }
        while (i >= 0 && image.get(centerJ, i) && stateCount[0] <= maxCount) {
            final int[] array3 = stateCount;
            final int n3 = 0;
            ++array3[n3];
            --i;
        }
        if (stateCount[0] > maxCount) {
            return Float.NaN;
        }
        for (i = startI + 1; i < maxI && image.get(centerJ, i); ++i) {
            final int[] array4 = stateCount;
            final int n4 = 2;
            ++array4[n4];
        }
        if (i == maxI) {
            return Float.NaN;
        }
        while (i < maxI && !image.get(centerJ, i) && stateCount[3] < maxCount) {
            final int[] array5 = stateCount;
            final int n5 = 3;
            ++array5[n5];
            ++i;
        }
        if (i == maxI || stateCount[3] >= maxCount) {
            return Float.NaN;
        }
        while (i < maxI && image.get(centerJ, i) && stateCount[4] < maxCount) {
            final int[] array6 = stateCount;
            final int n6 = 4;
            ++array6[n6];
            ++i;
        }
        if (stateCount[4] >= maxCount) {
            return Float.NaN;
        }
        final int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2] + stateCount[3] + stateCount[4];
        if (5 * Math.abs(stateCountTotal - originalStateCountTotal) >= 2 * originalStateCountTotal) {
            return Float.NaN;
        }
        return foundPatternCross(stateCount) ? centerFromEnd(stateCount, i) : Float.NaN;
    }
    
    private float crossCheckHorizontal(final int startJ, final int centerI, final int maxCount, final int originalStateCountTotal) {
        final BitMatrix image = this.image;
        final int maxJ = image.getWidth();
        final int[] stateCount = this.getCrossCheckStateCount();
        int j;
        for (j = startJ; j >= 0 && image.get(j, centerI); --j) {
            final int[] array = stateCount;
            final int n = 2;
            ++array[n];
        }
        if (j < 0) {
            return Float.NaN;
        }
        while (j >= 0 && !image.get(j, centerI) && stateCount[1] <= maxCount) {
            final int[] array2 = stateCount;
            final int n2 = 1;
            ++array2[n2];
            --j;
        }
        if (j < 0 || stateCount[1] > maxCount) {
            return Float.NaN;
        }
        while (j >= 0 && image.get(j, centerI) && stateCount[0] <= maxCount) {
            final int[] array3 = stateCount;
            final int n3 = 0;
            ++array3[n3];
            --j;
        }
        if (stateCount[0] > maxCount) {
            return Float.NaN;
        }
        for (j = startJ + 1; j < maxJ && image.get(j, centerI); ++j) {
            final int[] array4 = stateCount;
            final int n4 = 2;
            ++array4[n4];
        }
        if (j == maxJ) {
            return Float.NaN;
        }
        while (j < maxJ && !image.get(j, centerI) && stateCount[3] < maxCount) {
            final int[] array5 = stateCount;
            final int n5 = 3;
            ++array5[n5];
            ++j;
        }
        if (j == maxJ || stateCount[3] >= maxCount) {
            return Float.NaN;
        }
        while (j < maxJ && image.get(j, centerI) && stateCount[4] < maxCount) {
            final int[] array6 = stateCount;
            final int n6 = 4;
            ++array6[n6];
            ++j;
        }
        if (stateCount[4] >= maxCount) {
            return Float.NaN;
        }
        final int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2] + stateCount[3] + stateCount[4];
        if (5 * Math.abs(stateCountTotal - originalStateCountTotal) >= originalStateCountTotal) {
            return Float.NaN;
        }
        return foundPatternCross(stateCount) ? centerFromEnd(stateCount, j) : Float.NaN;
    }
    
    protected boolean handlePossibleCenter(final int[] stateCount, final int i, final int j) {
        final int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2] + stateCount[3] + stateCount[4];
        float centerJ = centerFromEnd(stateCount, j);
        final float centerI = this.crossCheckVertical(i, (int)centerJ, stateCount[2], stateCountTotal);
        if (!Float.isNaN(centerI)) {
            centerJ = this.crossCheckHorizontal((int)centerJ, (int)centerI, stateCount[2], stateCountTotal);
            if (!Float.isNaN(centerJ)) {
                final float estimatedModuleSize = stateCountTotal / 7.0f;
                boolean found = false;
                for (int index = 0; index < this.possibleCenters.size(); ++index) {
                    final FinderPattern center = this.possibleCenters.get(index);
                    if (center.aboutEquals(estimatedModuleSize, centerI, centerJ)) {
                        this.possibleCenters.set(index, center.combineEstimate(centerI, centerJ, estimatedModuleSize));
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    final FinderPattern point = new FinderPattern(centerJ, centerI, estimatedModuleSize);
                    this.possibleCenters.add(point);
                    if (this.resultPointCallback != null) {
                        this.resultPointCallback.foundPossibleResultPoint(point);
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    private int findRowSkip() {
        final int max = this.possibleCenters.size();
        if (max <= 1) {
            return 0;
        }
        FinderPattern firstConfirmedCenter = null;
        for (final FinderPattern center : this.possibleCenters) {
            if (center.getCount() >= 2) {
                if (firstConfirmedCenter != null) {
                    this.hasSkipped = true;
                    return (int)(Math.abs(firstConfirmedCenter.getX() - center.getX()) - Math.abs(firstConfirmedCenter.getY() - center.getY())) / 2;
                }
                firstConfirmedCenter = center;
            }
        }
        return 0;
    }
    
    private boolean haveMultiplyConfirmedCenters() {
        int confirmedCount = 0;
        float totalModuleSize = 0.0f;
        final int max = this.possibleCenters.size();
        for (final FinderPattern pattern : this.possibleCenters) {
            if (pattern.getCount() >= 2) {
                ++confirmedCount;
                totalModuleSize += pattern.getEstimatedModuleSize();
            }
        }
        if (confirmedCount < 3) {
            return false;
        }
        final float average = totalModuleSize / max;
        float totalDeviation = 0.0f;
        for (final FinderPattern pattern2 : this.possibleCenters) {
            totalDeviation += Math.abs(pattern2.getEstimatedModuleSize() - average);
        }
        return totalDeviation <= 0.05f * totalModuleSize;
    }
    
    private FinderPattern[] selectBestPatterns() throws NotFoundException {
        final int startSize = this.possibleCenters.size();
        if (startSize < 3) {
            throw NotFoundException.getNotFoundInstance();
        }
        if (startSize > 3) {
            float totalModuleSize = 0.0f;
            float square = 0.0f;
            for (final FinderPattern center : this.possibleCenters) {
                final float size = center.getEstimatedModuleSize();
                totalModuleSize += size;
                square += size * size;
            }
            final float average = totalModuleSize / startSize;
            final float stdDev = (float)Math.sqrt(square / startSize - average * average);
            Collections.sort(this.possibleCenters, new FurthestFromAverageComparator(average));
            final float limit = Math.max(0.2f * average, stdDev);
            for (int i = 0; i < this.possibleCenters.size() && this.possibleCenters.size() > 3; ++i) {
                final FinderPattern pattern = this.possibleCenters.get(i);
                if (Math.abs(pattern.getEstimatedModuleSize() - average) > limit) {
                    this.possibleCenters.remove(i);
                    --i;
                }
            }
        }
        if (this.possibleCenters.size() > 3) {
            float totalModuleSize = 0.0f;
            for (final FinderPattern possibleCenter : this.possibleCenters) {
                totalModuleSize += possibleCenter.getEstimatedModuleSize();
            }
            final float average2 = totalModuleSize / this.possibleCenters.size();
            Collections.sort(this.possibleCenters, new CenterComparator(average2));
            this.possibleCenters.subList(3, this.possibleCenters.size()).clear();
        }
        return new FinderPattern[] { this.possibleCenters.get(0), this.possibleCenters.get(1), this.possibleCenters.get(2) };
    }
    
    private static class FurthestFromAverageComparator implements Comparator<FinderPattern>, Serializable
    {
        private final float average;
        
        private FurthestFromAverageComparator(final float f) {
            this.average = f;
        }
        
        @Override
        public int compare(final FinderPattern center1, final FinderPattern center2) {
            final float dA = Math.abs(center2.getEstimatedModuleSize() - this.average);
            final float dB = Math.abs(center1.getEstimatedModuleSize() - this.average);
            return (dA < dB) ? -1 : ((dA == dB) ? 0 : 1);
        }
    }
    
    private static class CenterComparator implements Comparator<FinderPattern>, Serializable
    {
        private final float average;
        
        private CenterComparator(final float f) {
            this.average = f;
        }
        
        @Override
        public int compare(final FinderPattern center1, final FinderPattern center2) {
            if (center2.getCount() == center1.getCount()) {
                final float dA = Math.abs(center2.getEstimatedModuleSize() - this.average);
                final float dB = Math.abs(center1.getEstimatedModuleSize() - this.average);
                return (dA < dB) ? 1 : ((dA == dB) ? 0 : -1);
            }
            return center2.getCount() - center1.getCount();
        }
    }
}
