package org.apache.commons.math3.util;

import java.util.Arrays;
import org.apache.commons.math3.exception.NullArgumentException;
import java.io.Serializable;

public class KthSelector implements Serializable
{
    private static final long serialVersionUID = 20140713L;
    private static final int MIN_SELECT_SIZE = 15;
    private final PivotingStrategyInterface pivotingStrategy;
    
    public KthSelector() {
        this.pivotingStrategy = new MedianOf3PivotingStrategy();
    }
    
    public KthSelector(final PivotingStrategyInterface pivotingStrategy) throws NullArgumentException {
        MathUtils.checkNotNull(pivotingStrategy);
        this.pivotingStrategy = pivotingStrategy;
    }
    
    public PivotingStrategyInterface getPivotingStrategy() {
        return this.pivotingStrategy;
    }
    
    public double select(final double[] work, final int[] pivotsHeap, final int k) {
        int begin = 0;
        int end = work.length;
        int node = 0;
        final boolean usePivotsHeap = pivotsHeap != null;
        while (end - begin > 15) {
            int pivot;
            if (usePivotsHeap && node < pivotsHeap.length && pivotsHeap[node] >= 0) {
                pivot = pivotsHeap[node];
            }
            else {
                pivot = this.partition(work, begin, end, this.pivotingStrategy.pivotIndex(work, begin, end));
                if (usePivotsHeap && node < pivotsHeap.length) {
                    pivotsHeap[node] = pivot;
                }
            }
            if (k == pivot) {
                return work[k];
            }
            if (k < pivot) {
                end = pivot;
                node = FastMath.min(2 * node + 1, usePivotsHeap ? pivotsHeap.length : end);
            }
            else {
                begin = pivot + 1;
                node = FastMath.min(2 * node + 2, usePivotsHeap ? pivotsHeap.length : end);
            }
        }
        Arrays.sort(work, begin, end);
        return work[k];
    }
    
    private int partition(final double[] work, final int begin, final int end, final int pivot) {
        final double value = work[pivot];
        work[pivot] = work[begin];
        int i = begin + 1;
        double tmp = 0.0;
        for (int j = end - 1; i < j; work[i++] = work[j], work[j--] = tmp) {
            while (i < j && work[j] > value) {
                --j;
            }
            while (i < j && work[i] < value) {
                ++i;
            }
            if (i < j) {
                tmp = work[i];
            }
        }
        if (i >= end || work[i] > value) {
            --i;
        }
        work[begin] = work[i];
        work[i] = value;
        return i;
    }
}
