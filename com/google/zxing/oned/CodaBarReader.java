package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.common.BitArray;

public final class CodaBarReader extends OneDReader
{
    private static final String ALPHABET_STRING = "0123456789-$:/.+ABCDTN";
    static final char[] ALPHABET;
    static final int[] CHARACTER_ENCODINGS;
    private static final int minCharacterLength = 6;
    private static final char[] STARTEND_ENCODING;
    
    @Override
    public Result decodeRow(final int rowNumber, final BitArray row, final Map<DecodeHintType, ?> hints) throws NotFoundException {
        final int[] start = findAsteriskPattern(row);
        start[1] = 0;
        int nextStart = row.getNextSet(start[1]);
        final int end = row.getSize();
        final StringBuilder result = new StringBuilder();
        final int[] counters = new int[7];
        int lastStart;
        do {
            for (int i = 0; i < counters.length; ++i) {
                counters[i] = 0;
            }
            OneDReader.recordPattern(row, nextStart, counters);
            final char decodedChar = toNarrowWidePattern(counters);
            if (decodedChar == '!') {
                throw NotFoundException.getNotFoundInstance();
            }
            result.append(decodedChar);
            lastStart = nextStart;
            for (final int counter : counters) {
                nextStart += counter;
            }
            nextStart = row.getNextSet(nextStart);
        } while (nextStart < end);
        int lastPatternSize = 0;
        for (final int counter : counters) {
            lastPatternSize += counter;
        }
        final int whiteSpaceAfterEnd = nextStart - lastStart - lastPatternSize;
        if (nextStart != end && whiteSpaceAfterEnd / 2 < lastPatternSize) {
            throw NotFoundException.getNotFoundInstance();
        }
        if (result.length() < 2) {
            throw NotFoundException.getNotFoundInstance();
        }
        final char startchar = result.charAt(0);
        if (!arrayContains(CodaBarReader.STARTEND_ENCODING, startchar)) {
            throw NotFoundException.getNotFoundInstance();
        }
        for (int k = 1; k < result.length(); ++k) {
            if (result.charAt(k) == startchar && k + 1 != result.length()) {
                result.delete(k + 1, result.length() - 1);
                break;
            }
        }
        if (result.length() <= 6) {
            throw NotFoundException.getNotFoundInstance();
        }
        result.deleteCharAt(result.length() - 1);
        result.deleteCharAt(0);
        final float left = (start[1] + start[0]) / 2.0f;
        final float right = (nextStart + lastStart) / 2.0f;
        return new Result(result.toString(), null, new ResultPoint[] { new ResultPoint(left, (float)rowNumber), new ResultPoint(right, (float)rowNumber) }, BarcodeFormat.CODABAR);
    }
    
    private static int[] findAsteriskPattern(final BitArray row) throws NotFoundException {
        final int width = row.getSize();
        final int rowOffset = row.getNextSet(0);
        int counterPosition = 0;
        final int[] counters = new int[7];
        int patternStart = rowOffset;
        boolean isWhite = false;
        final int patternLength = counters.length;
        for (int i = rowOffset; i < width; ++i) {
            if (row.get(i) ^ isWhite) {
                final int[] array = counters;
                final int n = counterPosition;
                ++array[n];
            }
            else {
                if (counterPosition == patternLength - 1) {
                    try {
                        if (arrayContains(CodaBarReader.STARTEND_ENCODING, toNarrowWidePattern(counters)) && row.isRange(Math.max(0, patternStart - (i - patternStart) / 2), patternStart, false)) {
                            return new int[] { patternStart, i };
                        }
                    }
                    catch (final IllegalArgumentException ex) {}
                    patternStart += counters[0] + counters[1];
                    System.arraycopy(counters, 2, counters, 0, patternLength - 2);
                    counters[patternLength - 1] = (counters[patternLength - 2] = 0);
                    --counterPosition;
                }
                else {
                    ++counterPosition;
                }
                counters[counterPosition] = 1;
                isWhite ^= true;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    static boolean arrayContains(final char[] array, final char key) {
        if (array != null) {
            for (final char c : array) {
                if (c == key) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static char toNarrowWidePattern(final int[] counters) {
        final int numCounters = counters.length;
        int maxNarrowCounter = 0;
        int minCounter = Integer.MAX_VALUE;
        for (int i = 0; i < numCounters; ++i) {
            if (counters[i] < minCounter) {
                minCounter = counters[i];
            }
            if (counters[i] > maxNarrowCounter) {
                maxNarrowCounter = counters[i];
            }
        }
        do {
            int wideCounters = 0;
            int pattern = 0;
            for (int j = 0; j < numCounters; ++j) {
                if (counters[j] > maxNarrowCounter) {
                    pattern |= 1 << numCounters - 1 - j;
                    ++wideCounters;
                }
            }
            if (wideCounters == 2 || wideCounters == 3) {
                for (int j = 0; j < CodaBarReader.CHARACTER_ENCODINGS.length; ++j) {
                    if (CodaBarReader.CHARACTER_ENCODINGS[j] == pattern) {
                        return CodaBarReader.ALPHABET[j];
                    }
                }
            }
        } while (--maxNarrowCounter > minCounter);
        return '!';
    }
    
    static {
        ALPHABET = "0123456789-$:/.+ABCDTN".toCharArray();
        CHARACTER_ENCODINGS = new int[] { 3, 6, 9, 96, 18, 66, 33, 36, 48, 72, 12, 24, 69, 81, 84, 21, 26, 41, 11, 14, 26, 41 };
        STARTEND_ENCODING = new char[] { 'E', '*', 'A', 'B', 'C', 'D', 'T', 'N' };
    }
}
