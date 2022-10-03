package com.google.zxing.multi.qrcode.detector;

import java.io.Serializable;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import java.util.List;
import com.google.zxing.ResultPoint;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import com.google.zxing.NotFoundException;
import com.google.zxing.qrcode.detector.FinderPattern;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.detector.FinderPatternInfo;
import com.google.zxing.qrcode.detector.FinderPatternFinder;

final class MultiFinderPatternFinder extends FinderPatternFinder
{
    private static final FinderPatternInfo[] EMPTY_RESULT_ARRAY;
    private static final float MAX_MODULE_COUNT_PER_EDGE = 180.0f;
    private static final float MIN_MODULE_COUNT_PER_EDGE = 9.0f;
    private static final float DIFF_MODSIZE_CUTOFF_PERCENT = 0.05f;
    private static final float DIFF_MODSIZE_CUTOFF = 0.5f;
    
    MultiFinderPatternFinder(final BitMatrix image) {
        super(image);
    }
    
    MultiFinderPatternFinder(final BitMatrix image, final ResultPointCallback resultPointCallback) {
        super(image, resultPointCallback);
    }
    
    private FinderPattern[][] selectMutipleBestPatterns() throws NotFoundException {
        final List<FinderPattern> possibleCenters = this.getPossibleCenters();
        final int size = possibleCenters.size();
        if (size < 3) {
            throw NotFoundException.getNotFoundInstance();
        }
        if (size == 3) {
            return new FinderPattern[][] { { possibleCenters.get(0), possibleCenters.get(1), possibleCenters.get(2) } };
        }
        Collections.sort(possibleCenters, new ModuleSizeComparator());
        final List<FinderPattern[]> results = new ArrayList<FinderPattern[]>();
        for (int i1 = 0; i1 < size - 2; ++i1) {
            final FinderPattern p1 = possibleCenters.get(i1);
            if (p1 != null) {
                for (int i2 = i1 + 1; i2 < size - 1; ++i2) {
                    final FinderPattern p2 = possibleCenters.get(i2);
                    if (p2 != null) {
                        final float vModSize12 = (p1.getEstimatedModuleSize() - p2.getEstimatedModuleSize()) / Math.min(p1.getEstimatedModuleSize(), p2.getEstimatedModuleSize());
                        final float vModSize12A = Math.abs(p1.getEstimatedModuleSize() - p2.getEstimatedModuleSize());
                        if (vModSize12A > 0.5f && vModSize12 >= 0.05f) {
                            break;
                        }
                        for (int i3 = i2 + 1; i3 < size; ++i3) {
                            final FinderPattern p3 = possibleCenters.get(i3);
                            if (p3 != null) {
                                final float vModSize13 = (p2.getEstimatedModuleSize() - p3.getEstimatedModuleSize()) / Math.min(p2.getEstimatedModuleSize(), p3.getEstimatedModuleSize());
                                final float vModSize23A = Math.abs(p2.getEstimatedModuleSize() - p3.getEstimatedModuleSize());
                                if (vModSize23A > 0.5f && vModSize13 >= 0.05f) {
                                    break;
                                }
                                final FinderPattern[] test = { p1, p2, p3 };
                                ResultPoint.orderBestPatterns(test);
                                final FinderPatternInfo info = new FinderPatternInfo(test);
                                final float dA = ResultPoint.distance(info.getTopLeft(), info.getBottomLeft());
                                final float dC = ResultPoint.distance(info.getTopRight(), info.getBottomLeft());
                                final float dB = ResultPoint.distance(info.getTopLeft(), info.getTopRight());
                                final float estimatedModuleCount = (dA + dB) / (p1.getEstimatedModuleSize() * 2.0f);
                                if (estimatedModuleCount <= 180.0f) {
                                    if (estimatedModuleCount >= 9.0f) {
                                        final float vABBC = Math.abs((dA - dB) / Math.min(dA, dB));
                                        if (vABBC < 0.1f) {
                                            final float dCpy = (float)Math.sqrt(dA * dA + dB * dB);
                                            final float vPyC = Math.abs((dC - dCpy) / Math.min(dC, dCpy));
                                            if (vPyC < 0.1f) {
                                                results.add(test);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!results.isEmpty()) {
            return results.toArray(new FinderPattern[results.size()][]);
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    public FinderPatternInfo[] findMulti(final Map<DecodeHintType, ?> hints) throws NotFoundException {
        final boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
        final BitMatrix image = this.getImage();
        final int maxI = image.getHeight();
        final int maxJ = image.getWidth();
        int iSkip = (int)(maxI / 228.0f * 3.0f);
        if (iSkip < 3 || tryHarder) {
            iSkip = 3;
        }
        final int[] stateCount = new int[5];
        for (int i = iSkip - 1; i < maxI; i += iSkip) {
            stateCount[0] = 0;
            stateCount[2] = (stateCount[1] = 0);
            stateCount[4] = (stateCount[3] = 0);
            int currentState = 0;
            for (int j = 0; j < maxJ; ++j) {
                if (image.get(j, i)) {
                    if ((currentState & 0x1) == 0x1) {
                        ++currentState;
                    }
                    final int[] array = stateCount;
                    final int n = currentState;
                    ++array[n];
                }
                else if ((currentState & 0x1) == 0x0) {
                    if (currentState == 4) {
                        if (FinderPatternFinder.foundPatternCross(stateCount)) {
                            final boolean confirmed = this.handlePossibleCenter(stateCount, i, j);
                            if (!confirmed) {
                                while (++j < maxJ && !image.get(j, i)) {}
                                --j;
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
            if (FinderPatternFinder.foundPatternCross(stateCount)) {
                this.handlePossibleCenter(stateCount, i, maxJ);
            }
        }
        final FinderPattern[][] patternInfo = this.selectMutipleBestPatterns();
        final List<FinderPatternInfo> result = new ArrayList<FinderPatternInfo>();
        for (final FinderPattern[] pattern : patternInfo) {
            ResultPoint.orderBestPatterns(pattern);
            result.add(new FinderPatternInfo(pattern));
        }
        if (result.isEmpty()) {
            return MultiFinderPatternFinder.EMPTY_RESULT_ARRAY;
        }
        return result.toArray(new FinderPatternInfo[result.size()]);
    }
    
    static {
        EMPTY_RESULT_ARRAY = new FinderPatternInfo[0];
    }
    
    private static class ModuleSizeComparator implements Comparator<FinderPattern>, Serializable
    {
        @Override
        public int compare(final FinderPattern center1, final FinderPattern center2) {
            final float value = center2.getEstimatedModuleSize() - center1.getEstimatedModuleSize();
            return (value < 0.0) ? -1 : ((value > 0.0) ? 1 : 0);
        }
    }
}
