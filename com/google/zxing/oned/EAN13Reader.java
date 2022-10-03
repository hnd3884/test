package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

public final class EAN13Reader extends UPCEANReader
{
    static final int[] FIRST_DIGIT_ENCODINGS;
    private final int[] decodeMiddleCounters;
    
    public EAN13Reader() {
        this.decodeMiddleCounters = new int[4];
    }
    
    @Override
    protected int decodeMiddle(final BitArray row, final int[] startRange, final StringBuilder resultString) throws NotFoundException {
        final int[] counters = this.decodeMiddleCounters;
        counters[1] = (counters[0] = 0);
        counters[3] = (counters[2] = 0);
        final int end = row.getSize();
        int rowOffset = startRange[1];
        int lgPatternFound = 0;
        for (int x = 0; x < 6 && rowOffset < end; ++x) {
            final int bestMatch = UPCEANReader.decodeDigit(row, counters, rowOffset, EAN13Reader.L_AND_G_PATTERNS);
            resultString.append((char)(48 + bestMatch % 10));
            for (final int counter : counters) {
                rowOffset += counter;
            }
            if (bestMatch >= 10) {
                lgPatternFound |= 1 << 5 - x;
            }
        }
        determineFirstDigit(resultString, lgPatternFound);
        final int[] middleRange = UPCEANReader.findGuardPattern(row, rowOffset, true, EAN13Reader.MIDDLE_PATTERN);
        rowOffset = middleRange[1];
        for (int x2 = 0; x2 < 6 && rowOffset < end; ++x2) {
            final int bestMatch2 = UPCEANReader.decodeDigit(row, counters, rowOffset, EAN13Reader.L_PATTERNS);
            resultString.append((char)(48 + bestMatch2));
            for (final int counter2 : counters) {
                rowOffset += counter2;
            }
        }
        return rowOffset;
    }
    
    @Override
    BarcodeFormat getBarcodeFormat() {
        return BarcodeFormat.EAN_13;
    }
    
    private static void determineFirstDigit(final StringBuilder resultString, final int lgPatternFound) throws NotFoundException {
        for (int d = 0; d < 10; ++d) {
            if (lgPatternFound == EAN13Reader.FIRST_DIGIT_ENCODINGS[d]) {
                resultString.insert(0, (char)(48 + d));
                return;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    static {
        FIRST_DIGIT_ENCODINGS = new int[] { 0, 11, 13, 14, 19, 25, 28, 21, 22, 26 };
    }
}
