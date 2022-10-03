package org.apache.commons.math3.stat.correlation;

import org.apache.commons.math3.util.FastMath;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class KendallsCorrelation
{
    private final RealMatrix correlationMatrix;
    
    public KendallsCorrelation() {
        this.correlationMatrix = null;
    }
    
    public KendallsCorrelation(final double[][] data) {
        this(MatrixUtils.createRealMatrix(data));
    }
    
    public KendallsCorrelation(final RealMatrix matrix) {
        this.correlationMatrix = this.computeCorrelationMatrix(matrix);
    }
    
    public RealMatrix getCorrelationMatrix() {
        return this.correlationMatrix;
    }
    
    public RealMatrix computeCorrelationMatrix(final RealMatrix matrix) {
        final int nVars = matrix.getColumnDimension();
        final RealMatrix outMatrix = new BlockRealMatrix(nVars, nVars);
        for (int i = 0; i < nVars; ++i) {
            for (int j = 0; j < i; ++j) {
                final double corr = this.correlation(matrix.getColumn(i), matrix.getColumn(j));
                outMatrix.setEntry(i, j, corr);
                outMatrix.setEntry(j, i, corr);
            }
            outMatrix.setEntry(i, i, 1.0);
        }
        return outMatrix;
    }
    
    public RealMatrix computeCorrelationMatrix(final double[][] matrix) {
        return this.computeCorrelationMatrix(new BlockRealMatrix(matrix));
    }
    
    public double correlation(final double[] xArray, final double[] yArray) throws DimensionMismatchException {
        if (xArray.length != yArray.length) {
            throw new DimensionMismatchException(xArray.length, yArray.length);
        }
        final int n = xArray.length;
        final long numPairs = sum(n - 1);
        Pair<Double, Double>[] pairs = new Pair[n];
        for (int i = 0; i < n; ++i) {
            pairs[i] = new Pair<Double, Double>(xArray[i], yArray[i]);
        }
        Arrays.sort(pairs, new Comparator<Pair<Double, Double>>() {
            public int compare(final Pair<Double, Double> pair1, final Pair<Double, Double> pair2) {
                final int compareFirst = pair1.getFirst().compareTo(pair2.getFirst());
                return (compareFirst != 0) ? compareFirst : pair1.getSecond().compareTo(pair2.getSecond());
            }
        });
        long tiedXPairs = 0L;
        long tiedXYPairs = 0L;
        long consecutiveXTies = 1L;
        long consecutiveXYTies = 1L;
        Pair<Double, Double> prev = pairs[0];
        for (int j = 1; j < n; ++j) {
            final Pair<Double, Double> curr = pairs[j];
            if (curr.getFirst().equals(prev.getFirst())) {
                ++consecutiveXTies;
                if (curr.getSecond().equals(prev.getSecond())) {
                    ++consecutiveXYTies;
                }
                else {
                    tiedXYPairs += sum(consecutiveXYTies - 1L);
                    consecutiveXYTies = 1L;
                }
            }
            else {
                tiedXPairs += sum(consecutiveXTies - 1L);
                consecutiveXTies = 1L;
                tiedXYPairs += sum(consecutiveXYTies - 1L);
                consecutiveXYTies = 1L;
            }
            prev = curr;
        }
        tiedXPairs += sum(consecutiveXTies - 1L);
        tiedXYPairs += sum(consecutiveXYTies - 1L);
        long swaps = 0L;
        Pair<Double, Double>[] pairsDestination = new Pair[n];
        for (int segmentSize = 1; segmentSize < n; segmentSize <<= 1) {
            for (int offset = 0; offset < n; offset += 2 * segmentSize) {
                int k = offset;
                int l;
                final int iEnd = l = FastMath.min(k + segmentSize, n);
                final int jEnd = FastMath.min(l + segmentSize, n);
                int copyLocation = offset;
                while (k < iEnd || l < jEnd) {
                    if (k < iEnd) {
                        if (l < jEnd) {
                            if (pairs[k].getSecond().compareTo(pairs[l].getSecond()) <= 0) {
                                pairsDestination[copyLocation] = pairs[k];
                                ++k;
                            }
                            else {
                                pairsDestination[copyLocation] = pairs[l];
                                ++l;
                                swaps += iEnd - k;
                            }
                        }
                        else {
                            pairsDestination[copyLocation] = pairs[k];
                            ++k;
                        }
                    }
                    else {
                        pairsDestination[copyLocation] = pairs[l];
                        ++l;
                    }
                    ++copyLocation;
                }
            }
            final Pair<Double, Double>[] pairsTemp = pairs;
            pairs = pairsDestination;
            pairsDestination = pairsTemp;
        }
        long tiedYPairs = 0L;
        long consecutiveYTies = 1L;
        prev = pairs[0];
        for (int m = 1; m < n; ++m) {
            final Pair<Double, Double> curr2 = pairs[m];
            if (curr2.getSecond().equals(prev.getSecond())) {
                ++consecutiveYTies;
            }
            else {
                tiedYPairs += sum(consecutiveYTies - 1L);
                consecutiveYTies = 1L;
            }
            prev = curr2;
        }
        tiedYPairs += sum(consecutiveYTies - 1L);
        final long concordantMinusDiscordant = numPairs - tiedXPairs - tiedYPairs + tiedXYPairs - 2L * swaps;
        final double nonTiedPairsMultiplied = (numPairs - tiedXPairs) * (double)(numPairs - tiedYPairs);
        return concordantMinusDiscordant / FastMath.sqrt(nonTiedPairsMultiplied);
    }
    
    private static long sum(final long n) {
        return n * (n + 1L) / 2L;
    }
}
