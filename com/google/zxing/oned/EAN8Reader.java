package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

public final class EAN8Reader extends UPCEANReader
{
    private final int[] decodeMiddleCounters;
    
    public EAN8Reader() {
        this.decodeMiddleCounters = new int[4];
    }
    
    @Override
    protected int decodeMiddle(final BitArray row, final int[] startRange, final StringBuilder result) throws NotFoundException {
        final int[] counters = this.decodeMiddleCounters;
        counters[1] = (counters[0] = 0);
        counters[3] = (counters[2] = 0);
        final int end = row.getSize();
        int rowOffset = startRange[1];
        for (int x = 0; x < 4 && rowOffset < end; ++x) {
            final int bestMatch = UPCEANReader.decodeDigit(row, counters, rowOffset, EAN8Reader.L_PATTERNS);
            result.append((char)(48 + bestMatch));
            for (final int counter : counters) {
                rowOffset += counter;
            }
        }
        final int[] middleRange = UPCEANReader.findGuardPattern(row, rowOffset, true, EAN8Reader.MIDDLE_PATTERN);
        rowOffset = middleRange[1];
        for (int x2 = 0; x2 < 4 && rowOffset < end; ++x2) {
            final int bestMatch2 = UPCEANReader.decodeDigit(row, counters, rowOffset, EAN8Reader.L_PATTERNS);
            result.append((char)(48 + bestMatch2));
            for (final int counter2 : counters) {
                rowOffset += counter2;
            }
        }
        return rowOffset;
    }
    
    @Override
    BarcodeFormat getBarcodeFormat() {
        return BarcodeFormat.EAN_8;
    }
}
