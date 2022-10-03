package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

public final class UPCEReader extends UPCEANReader
{
    private static final int[] MIDDLE_END_PATTERN;
    private static final int[][] NUMSYS_AND_CHECK_DIGIT_PATTERNS;
    private final int[] decodeMiddleCounters;
    
    public UPCEReader() {
        this.decodeMiddleCounters = new int[4];
    }
    
    @Override
    protected int decodeMiddle(final BitArray row, final int[] startRange, final StringBuilder result) throws NotFoundException {
        final int[] counters = this.decodeMiddleCounters;
        counters[1] = (counters[0] = 0);
        counters[3] = (counters[2] = 0);
        final int end = row.getSize();
        int rowOffset = startRange[1];
        int lgPatternFound = 0;
        for (int x = 0; x < 6 && rowOffset < end; ++x) {
            final int bestMatch = UPCEANReader.decodeDigit(row, counters, rowOffset, UPCEReader.L_AND_G_PATTERNS);
            result.append((char)(48 + bestMatch % 10));
            for (final int counter : counters) {
                rowOffset += counter;
            }
            if (bestMatch >= 10) {
                lgPatternFound |= 1 << 5 - x;
            }
        }
        determineNumSysAndCheckDigit(result, lgPatternFound);
        return rowOffset;
    }
    
    protected int[] decodeEnd(final BitArray row, final int endStart) throws NotFoundException {
        return UPCEANReader.findGuardPattern(row, endStart, true, UPCEReader.MIDDLE_END_PATTERN);
    }
    
    protected boolean checkChecksum(final String s) throws FormatException, ChecksumException {
        return super.checkChecksum(convertUPCEtoUPCA(s));
    }
    
    private static void determineNumSysAndCheckDigit(final StringBuilder resultString, final int lgPatternFound) throws NotFoundException {
        for (int numSys = 0; numSys <= 1; ++numSys) {
            for (int d = 0; d < 10; ++d) {
                if (lgPatternFound == UPCEReader.NUMSYS_AND_CHECK_DIGIT_PATTERNS[numSys][d]) {
                    resultString.insert(0, (char)(48 + numSys));
                    resultString.append((char)(48 + d));
                    return;
                }
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    @Override
    BarcodeFormat getBarcodeFormat() {
        return BarcodeFormat.UPC_E;
    }
    
    public static String convertUPCEtoUPCA(final String upce) {
        final char[] upceChars = new char[6];
        upce.getChars(1, 7, upceChars, 0);
        final StringBuilder result = new StringBuilder(12);
        result.append(upce.charAt(0));
        final char lastChar = upceChars[5];
        switch (lastChar) {
            case '0':
            case '1':
            case '2': {
                result.append(upceChars, 0, 2);
                result.append(lastChar);
                result.append("0000");
                result.append(upceChars, 2, 3);
                break;
            }
            case '3': {
                result.append(upceChars, 0, 3);
                result.append("00000");
                result.append(upceChars, 3, 2);
                break;
            }
            case '4': {
                result.append(upceChars, 0, 4);
                result.append("00000");
                result.append(upceChars[4]);
                break;
            }
            default: {
                result.append(upceChars, 0, 5);
                result.append("0000");
                result.append(lastChar);
                break;
            }
        }
        result.append(upce.charAt(7));
        return result.toString();
    }
    
    static {
        MIDDLE_END_PATTERN = new int[] { 1, 1, 1, 1, 1, 1 };
        NUMSYS_AND_CHECK_DIGIT_PATTERNS = new int[][] { { 56, 52, 50, 49, 44, 38, 35, 42, 41, 37 }, { 7, 11, 13, 14, 19, 25, 28, 21, 22, 26 } };
    }
}
