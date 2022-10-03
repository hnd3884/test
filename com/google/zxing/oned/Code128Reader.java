package com.google.zxing.oned;

import java.util.List;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.ChecksumException;
import java.util.ArrayList;
import com.google.zxing.FormatException;
import com.google.zxing.Result;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

public final class Code128Reader extends OneDReader
{
    static final int[][] CODE_PATTERNS;
    private static final int MAX_AVG_VARIANCE = 64;
    private static final int MAX_INDIVIDUAL_VARIANCE = 179;
    private static final int CODE_SHIFT = 98;
    private static final int CODE_CODE_C = 99;
    private static final int CODE_CODE_B = 100;
    private static final int CODE_CODE_A = 101;
    private static final int CODE_FNC_1 = 102;
    private static final int CODE_FNC_2 = 97;
    private static final int CODE_FNC_3 = 96;
    private static final int CODE_FNC_4_A = 101;
    private static final int CODE_FNC_4_B = 100;
    private static final int CODE_START_A = 103;
    private static final int CODE_START_B = 104;
    private static final int CODE_START_C = 105;
    private static final int CODE_STOP = 106;
    
    private static int[] findStartPattern(final BitArray row) throws NotFoundException {
        final int width = row.getSize();
        final int rowOffset = row.getNextSet(0);
        int counterPosition = 0;
        final int[] counters = new int[6];
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
                    int bestVariance = 64;
                    int bestMatch = -1;
                    for (int startCode = 103; startCode <= 105; ++startCode) {
                        final int variance = OneDReader.patternMatchVariance(counters, Code128Reader.CODE_PATTERNS[startCode], 179);
                        if (variance < bestVariance) {
                            bestVariance = variance;
                            bestMatch = startCode;
                        }
                    }
                    if (bestMatch >= 0 && row.isRange(Math.max(0, patternStart - (i - patternStart) / 2), patternStart, false)) {
                        return new int[] { patternStart, i, bestMatch };
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
    
    private static int decodeCode(final BitArray row, final int[] counters, final int rowOffset) throws NotFoundException {
        OneDReader.recordPattern(row, rowOffset, counters);
        int bestVariance = 64;
        int bestMatch = -1;
        for (int d = 0; d < Code128Reader.CODE_PATTERNS.length; ++d) {
            final int[] pattern = Code128Reader.CODE_PATTERNS[d];
            final int variance = OneDReader.patternMatchVariance(counters, pattern, 179);
            if (variance < bestVariance) {
                bestVariance = variance;
                bestMatch = d;
            }
        }
        if (bestMatch >= 0) {
            return bestMatch;
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    @Override
    public Result decodeRow(final int rowNumber, final BitArray row, final Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException, ChecksumException {
        final int[] startPatternInfo = findStartPattern(row);
        final int startCode = startPatternInfo[2];
        int codeSet = 0;
        switch (startCode) {
            case 103: {
                codeSet = 101;
                break;
            }
            case 104: {
                codeSet = 100;
                break;
            }
            case 105: {
                codeSet = 99;
                break;
            }
            default: {
                throw FormatException.getFormatInstance();
            }
        }
        boolean done = false;
        boolean isNextShifted = false;
        final StringBuilder result = new StringBuilder(20);
        final List<Byte> rawCodes = new ArrayList<Byte>(20);
        int lastStart = startPatternInfo[0];
        int nextStart = startPatternInfo[1];
        final int[] counters = new int[6];
        int lastCode = 0;
        int code = 0;
        int checksumTotal = startCode;
        int multiplier = 0;
        boolean lastCharacterWasPrintable = true;
        while (!done) {
            final boolean unshift = isNextShifted;
            isNextShifted = false;
            lastCode = code;
            code = decodeCode(row, counters, nextStart);
            rawCodes.add((byte)code);
            if (code != 106) {
                lastCharacterWasPrintable = true;
            }
            if (code != 106) {
                ++multiplier;
                checksumTotal += multiplier * code;
            }
            lastStart = nextStart;
            for (final int counter : counters) {
                nextStart += counter;
            }
            switch (code) {
                case 103:
                case 104:
                case 105: {
                    throw FormatException.getFormatInstance();
                }
                default: {
                    Label_0680: {
                        switch (codeSet) {
                            case 101: {
                                if (code < 64) {
                                    result.append((char)(32 + code));
                                    break;
                                }
                                if (code < 96) {
                                    result.append((char)(code - 64));
                                    break;
                                }
                                if (code != 106) {
                                    lastCharacterWasPrintable = false;
                                }
                                switch (code) {
                                    case 98: {
                                        isNextShifted = true;
                                        codeSet = 100;
                                        break;
                                    }
                                    case 100: {
                                        codeSet = 100;
                                        break;
                                    }
                                    case 99: {
                                        codeSet = 99;
                                        break;
                                    }
                                    case 106: {
                                        done = true;
                                        break;
                                    }
                                }
                                break;
                            }
                            case 100: {
                                if (code < 96) {
                                    result.append((char)(32 + code));
                                    break;
                                }
                                if (code != 106) {
                                    lastCharacterWasPrintable = false;
                                }
                                switch (code) {
                                    case 98: {
                                        isNextShifted = true;
                                        codeSet = 101;
                                        break;
                                    }
                                    case 101: {
                                        codeSet = 101;
                                        break;
                                    }
                                    case 99: {
                                        codeSet = 99;
                                        break;
                                    }
                                    case 106: {
                                        done = true;
                                        break;
                                    }
                                }
                                break;
                            }
                            case 99: {
                                if (code < 100) {
                                    if (code < 10) {
                                        result.append('0');
                                    }
                                    result.append(code);
                                    break;
                                }
                                if (code != 106) {
                                    lastCharacterWasPrintable = false;
                                }
                                switch (code) {
                                    case 102: {
                                        break Label_0680;
                                    }
                                    case 101: {
                                        codeSet = 101;
                                        break Label_0680;
                                    }
                                    case 100: {
                                        codeSet = 100;
                                        break Label_0680;
                                    }
                                    case 106: {
                                        done = true;
                                        break Label_0680;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    if (!unshift) {
                        continue;
                    }
                    codeSet = ((codeSet == 101) ? 100 : 101);
                    continue;
                }
            }
        }
        nextStart = row.getNextUnset(nextStart);
        if (!row.isRange(nextStart, Math.min(row.getSize(), nextStart + (nextStart - lastStart) / 2), false)) {
            throw NotFoundException.getNotFoundInstance();
        }
        checksumTotal -= multiplier * lastCode;
        if (checksumTotal % 103 != lastCode) {
            throw ChecksumException.getChecksumInstance();
        }
        final int resultLength = result.length();
        if (resultLength == 0) {
            throw NotFoundException.getNotFoundInstance();
        }
        if (resultLength > 0 && lastCharacterWasPrintable) {
            if (codeSet == 99) {
                result.delete(resultLength - 2, resultLength);
            }
            else {
                result.delete(resultLength - 1, resultLength);
            }
        }
        final float left = (startPatternInfo[1] + startPatternInfo[0]) / 2.0f;
        final float right = (nextStart + lastStart) / 2.0f;
        final int rawCodesSize = rawCodes.size();
        final byte[] rawBytes = new byte[rawCodesSize];
        for (int i = 0; i < rawCodesSize; ++i) {
            rawBytes[i] = rawCodes.get(i);
        }
        return new Result(result.toString(), rawBytes, new ResultPoint[] { new ResultPoint(left, (float)rowNumber), new ResultPoint(right, (float)rowNumber) }, BarcodeFormat.CODE_128);
    }
    
    static {
        CODE_PATTERNS = new int[][] { { 2, 1, 2, 2, 2, 2 }, { 2, 2, 2, 1, 2, 2 }, { 2, 2, 2, 2, 2, 1 }, { 1, 2, 1, 2, 2, 3 }, { 1, 2, 1, 3, 2, 2 }, { 1, 3, 1, 2, 2, 2 }, { 1, 2, 2, 2, 1, 3 }, { 1, 2, 2, 3, 1, 2 }, { 1, 3, 2, 2, 1, 2 }, { 2, 2, 1, 2, 1, 3 }, { 2, 2, 1, 3, 1, 2 }, { 2, 3, 1, 2, 1, 2 }, { 1, 1, 2, 2, 3, 2 }, { 1, 2, 2, 1, 3, 2 }, { 1, 2, 2, 2, 3, 1 }, { 1, 1, 3, 2, 2, 2 }, { 1, 2, 3, 1, 2, 2 }, { 1, 2, 3, 2, 2, 1 }, { 2, 2, 3, 2, 1, 1 }, { 2, 2, 1, 1, 3, 2 }, { 2, 2, 1, 2, 3, 1 }, { 2, 1, 3, 2, 1, 2 }, { 2, 2, 3, 1, 1, 2 }, { 3, 1, 2, 1, 3, 1 }, { 3, 1, 1, 2, 2, 2 }, { 3, 2, 1, 1, 2, 2 }, { 3, 2, 1, 2, 2, 1 }, { 3, 1, 2, 2, 1, 2 }, { 3, 2, 2, 1, 1, 2 }, { 3, 2, 2, 2, 1, 1 }, { 2, 1, 2, 1, 2, 3 }, { 2, 1, 2, 3, 2, 1 }, { 2, 3, 2, 1, 2, 1 }, { 1, 1, 1, 3, 2, 3 }, { 1, 3, 1, 1, 2, 3 }, { 1, 3, 1, 3, 2, 1 }, { 1, 1, 2, 3, 1, 3 }, { 1, 3, 2, 1, 1, 3 }, { 1, 3, 2, 3, 1, 1 }, { 2, 1, 1, 3, 1, 3 }, { 2, 3, 1, 1, 1, 3 }, { 2, 3, 1, 3, 1, 1 }, { 1, 1, 2, 1, 3, 3 }, { 1, 1, 2, 3, 3, 1 }, { 1, 3, 2, 1, 3, 1 }, { 1, 1, 3, 1, 2, 3 }, { 1, 1, 3, 3, 2, 1 }, { 1, 3, 3, 1, 2, 1 }, { 3, 1, 3, 1, 2, 1 }, { 2, 1, 1, 3, 3, 1 }, { 2, 3, 1, 1, 3, 1 }, { 2, 1, 3, 1, 1, 3 }, { 2, 1, 3, 3, 1, 1 }, { 2, 1, 3, 1, 3, 1 }, { 3, 1, 1, 1, 2, 3 }, { 3, 1, 1, 3, 2, 1 }, { 3, 3, 1, 1, 2, 1 }, { 3, 1, 2, 1, 1, 3 }, { 3, 1, 2, 3, 1, 1 }, { 3, 3, 2, 1, 1, 1 }, { 3, 1, 4, 1, 1, 1 }, { 2, 2, 1, 4, 1, 1 }, { 4, 3, 1, 1, 1, 1 }, { 1, 1, 1, 2, 2, 4 }, { 1, 1, 1, 4, 2, 2 }, { 1, 2, 1, 1, 2, 4 }, { 1, 2, 1, 4, 2, 1 }, { 1, 4, 1, 1, 2, 2 }, { 1, 4, 1, 2, 2, 1 }, { 1, 1, 2, 2, 1, 4 }, { 1, 1, 2, 4, 1, 2 }, { 1, 2, 2, 1, 1, 4 }, { 1, 2, 2, 4, 1, 1 }, { 1, 4, 2, 1, 1, 2 }, { 1, 4, 2, 2, 1, 1 }, { 2, 4, 1, 2, 1, 1 }, { 2, 2, 1, 1, 1, 4 }, { 4, 1, 3, 1, 1, 1 }, { 2, 4, 1, 1, 1, 2 }, { 1, 3, 4, 1, 1, 1 }, { 1, 1, 1, 2, 4, 2 }, { 1, 2, 1, 1, 4, 2 }, { 1, 2, 1, 2, 4, 1 }, { 1, 1, 4, 2, 1, 2 }, { 1, 2, 4, 1, 1, 2 }, { 1, 2, 4, 2, 1, 1 }, { 4, 1, 1, 2, 1, 2 }, { 4, 2, 1, 1, 1, 2 }, { 4, 2, 1, 2, 1, 1 }, { 2, 1, 2, 1, 4, 1 }, { 2, 1, 4, 1, 2, 1 }, { 4, 1, 2, 1, 2, 1 }, { 1, 1, 1, 1, 4, 3 }, { 1, 1, 1, 3, 4, 1 }, { 1, 3, 1, 1, 4, 1 }, { 1, 1, 4, 1, 1, 3 }, { 1, 1, 4, 3, 1, 1 }, { 4, 1, 1, 1, 1, 3 }, { 4, 1, 1, 3, 1, 1 }, { 1, 1, 3, 1, 4, 1 }, { 1, 1, 4, 1, 3, 1 }, { 3, 1, 1, 1, 4, 1 }, { 4, 1, 1, 1, 3, 1 }, { 2, 1, 1, 4, 1, 2 }, { 2, 1, 1, 2, 1, 4 }, { 2, 1, 1, 2, 3, 2 }, { 2, 3, 3, 1, 1, 1, 2 } };
    }
}
