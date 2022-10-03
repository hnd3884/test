package com.google.zxing.oned;

import com.google.zxing.NotFoundException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.FormatException;
import com.google.zxing.Result;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.common.BitArray;

public final class ITFReader extends OneDReader
{
    private static final int MAX_AVG_VARIANCE = 107;
    private static final int MAX_INDIVIDUAL_VARIANCE = 204;
    private static final int W = 3;
    private static final int N = 1;
    private static final int[] DEFAULT_ALLOWED_LENGTHS;
    private int narrowLineWidth;
    private static final int[] START_PATTERN;
    private static final int[] END_PATTERN_REVERSED;
    static final int[][] PATTERNS;
    
    public ITFReader() {
        this.narrowLineWidth = -1;
    }
    
    @Override
    public Result decodeRow(final int rowNumber, final BitArray row, final Map<DecodeHintType, ?> hints) throws FormatException, NotFoundException {
        final int[] startRange = this.decodeStart(row);
        final int[] endRange = this.decodeEnd(row);
        final StringBuilder result = new StringBuilder(20);
        decodeMiddle(row, startRange[1], endRange[0], result);
        final String resultString = result.toString();
        int[] allowedLengths = null;
        if (hints != null) {
            allowedLengths = (int[])(Object)hints.get(DecodeHintType.ALLOWED_LENGTHS);
        }
        if (allowedLengths == null) {
            allowedLengths = ITFReader.DEFAULT_ALLOWED_LENGTHS;
        }
        final int length = resultString.length();
        boolean lengthOK = false;
        for (final int allowedLength : allowedLengths) {
            if (length == allowedLength) {
                lengthOK = true;
                break;
            }
        }
        if (!lengthOK) {
            throw FormatException.getFormatInstance();
        }
        return new Result(resultString, null, new ResultPoint[] { new ResultPoint((float)startRange[1], (float)rowNumber), new ResultPoint((float)endRange[0], (float)rowNumber) }, BarcodeFormat.ITF);
    }
    
    private static void decodeMiddle(final BitArray row, int payloadStart, final int payloadEnd, final StringBuilder resultString) throws NotFoundException {
        final int[] counterDigitPair = new int[10];
        final int[] counterBlack = new int[5];
        final int[] counterWhite = new int[5];
        while (payloadStart < payloadEnd) {
            OneDReader.recordPattern(row, payloadStart, counterDigitPair);
            for (int k = 0; k < 5; ++k) {
                final int twoK = k << 1;
                counterBlack[k] = counterDigitPair[twoK];
                counterWhite[k] = counterDigitPair[twoK + 1];
            }
            int bestMatch = decodeDigit(counterBlack);
            resultString.append((char)(48 + bestMatch));
            bestMatch = decodeDigit(counterWhite);
            resultString.append((char)(48 + bestMatch));
            for (final int counterDigit : counterDigitPair) {
                payloadStart += counterDigit;
            }
        }
    }
    
    int[] decodeStart(final BitArray row) throws NotFoundException {
        final int endStart = skipWhiteSpace(row);
        final int[] startPattern = findGuardPattern(row, endStart, ITFReader.START_PATTERN);
        this.narrowLineWidth = startPattern[1] - startPattern[0] >> 2;
        this.validateQuietZone(row, startPattern[0]);
        return startPattern;
    }
    
    private void validateQuietZone(final BitArray row, final int startPattern) throws NotFoundException {
        int quietCount = this.narrowLineWidth * 10;
        for (int i = startPattern - 1; quietCount > 0 && i >= 0 && !row.get(i); --quietCount, --i) {}
        if (quietCount != 0) {
            throw NotFoundException.getNotFoundInstance();
        }
    }
    
    private static int skipWhiteSpace(final BitArray row) throws NotFoundException {
        final int width = row.getSize();
        final int endStart = row.getNextSet(0);
        if (endStart == width) {
            throw NotFoundException.getNotFoundInstance();
        }
        return endStart;
    }
    
    int[] decodeEnd(final BitArray row) throws NotFoundException {
        row.reverse();
        try {
            final int endStart = skipWhiteSpace(row);
            final int[] endPattern = findGuardPattern(row, endStart, ITFReader.END_PATTERN_REVERSED);
            this.validateQuietZone(row, endPattern[0]);
            final int temp = endPattern[0];
            endPattern[0] = row.getSize() - endPattern[1];
            endPattern[1] = row.getSize() - temp;
            return endPattern;
        }
        finally {
            row.reverse();
        }
    }
    
    private static int[] findGuardPattern(final BitArray row, final int rowOffset, final int[] pattern) throws NotFoundException {
        final int patternLength = pattern.length;
        final int[] counters = new int[patternLength];
        final int width = row.getSize();
        boolean isWhite = false;
        int counterPosition = 0;
        int patternStart = rowOffset;
        for (int x = rowOffset; x < width; ++x) {
            if (row.get(x) ^ isWhite) {
                final int[] array = counters;
                final int n = counterPosition;
                ++array[n];
            }
            else {
                if (counterPosition == patternLength - 1) {
                    if (OneDReader.patternMatchVariance(counters, pattern, 204) < 107) {
                        return new int[] { patternStart, x };
                    }
                    patternStart += counters[0] + counters[1];
                    System.arraycopy(counters, 2, counters, 0, patternLength - 2);
                    counters[patternLength - 1] = (counters[patternLength - 2] = 0);
                    --counterPosition;
                }
                else {
                    ++counterPosition;
                }
                counters[counterPosition] = 1;
                isWhite = !isWhite;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    private static int decodeDigit(final int[] counters) throws NotFoundException {
        int bestVariance = 107;
        int bestMatch = -1;
        for (int max = ITFReader.PATTERNS.length, i = 0; i < max; ++i) {
            final int[] pattern = ITFReader.PATTERNS[i];
            final int variance = OneDReader.patternMatchVariance(counters, pattern, 204);
            if (variance < bestVariance) {
                bestVariance = variance;
                bestMatch = i;
            }
        }
        if (bestMatch >= 0) {
            return bestMatch;
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    static {
        DEFAULT_ALLOWED_LENGTHS = new int[] { 44, 24, 20, 18, 16, 14, 12, 10, 8, 6 };
        START_PATTERN = new int[] { 1, 1, 1, 1 };
        END_PATTERN_REVERSED = new int[] { 1, 1, 3 };
        PATTERNS = new int[][] { { 1, 1, 3, 3, 1 }, { 3, 1, 1, 1, 3 }, { 1, 3, 1, 1, 3 }, { 3, 3, 1, 1, 1 }, { 1, 1, 3, 1, 3 }, { 3, 1, 3, 1, 1 }, { 1, 3, 3, 1, 1 }, { 1, 1, 1, 3, 3 }, { 3, 1, 1, 3, 1 }, { 1, 3, 1, 3, 1 } };
    }
}
