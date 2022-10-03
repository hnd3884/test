package com.google.zxing.oned.rss;

import com.google.zxing.NotFoundException;
import com.google.zxing.oned.OneDReader;

public abstract class AbstractRSSReader extends OneDReader
{
    private static final int MAX_AVG_VARIANCE = 51;
    private static final int MAX_INDIVIDUAL_VARIANCE = 102;
    private static final float MIN_FINDER_PATTERN_RATIO = 0.7916667f;
    private static final float MAX_FINDER_PATTERN_RATIO = 0.89285713f;
    private final int[] decodeFinderCounters;
    private final int[] dataCharacterCounters;
    private final float[] oddRoundingErrors;
    private final float[] evenRoundingErrors;
    private final int[] oddCounts;
    private final int[] evenCounts;
    
    protected AbstractRSSReader() {
        this.decodeFinderCounters = new int[4];
        this.dataCharacterCounters = new int[8];
        this.oddRoundingErrors = new float[4];
        this.evenRoundingErrors = new float[4];
        this.oddCounts = new int[this.dataCharacterCounters.length / 2];
        this.evenCounts = new int[this.dataCharacterCounters.length / 2];
    }
    
    protected int[] getDecodeFinderCounters() {
        return this.decodeFinderCounters;
    }
    
    protected int[] getDataCharacterCounters() {
        return this.dataCharacterCounters;
    }
    
    protected float[] getOddRoundingErrors() {
        return this.oddRoundingErrors;
    }
    
    protected float[] getEvenRoundingErrors() {
        return this.evenRoundingErrors;
    }
    
    protected int[] getOddCounts() {
        return this.oddCounts;
    }
    
    protected int[] getEvenCounts() {
        return this.evenCounts;
    }
    
    protected static int parseFinderValue(final int[] counters, final int[][] finderPatterns) throws NotFoundException {
        for (int value = 0; value < finderPatterns.length; ++value) {
            if (OneDReader.patternMatchVariance(counters, finderPatterns[value], 102) < 51) {
                return value;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    protected static int count(final int[] array) {
        int count = 0;
        for (final int a : array) {
            count += a;
        }
        return count;
    }
    
    protected static void increment(final int[] array, final float[] errors) {
        int index = 0;
        float biggestError = errors[0];
        for (int i = 1; i < array.length; ++i) {
            if (errors[i] > biggestError) {
                biggestError = errors[i];
                index = i;
            }
        }
        final int n = index;
        ++array[n];
    }
    
    protected static void decrement(final int[] array, final float[] errors) {
        int index = 0;
        float biggestError = errors[0];
        for (int i = 1; i < array.length; ++i) {
            if (errors[i] < biggestError) {
                biggestError = errors[i];
                index = i;
            }
        }
        final int n = index;
        --array[n];
    }
    
    protected static boolean isFinderPattern(final int[] counters) {
        final int firstTwoSum = counters[0] + counters[1];
        final int sum = firstTwoSum + counters[2] + counters[3];
        final float ratio = firstTwoSum / (float)sum;
        if (ratio >= 0.7916667f && ratio <= 0.89285713f) {
            int minCounter = Integer.MAX_VALUE;
            int maxCounter = Integer.MIN_VALUE;
            for (final int counter : counters) {
                if (counter > maxCounter) {
                    maxCounter = counter;
                }
                if (counter < minCounter) {
                    minCounter = counter;
                }
            }
            return maxCounter < 10 * minCounter;
        }
        return false;
    }
}
