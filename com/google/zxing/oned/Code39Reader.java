package com.google.zxing.oned;

import com.google.zxing.FormatException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.ChecksumException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.common.BitArray;

public final class Code39Reader extends OneDReader
{
    static final String ALPHABET_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%";
    private static final char[] ALPHABET;
    static final int[] CHARACTER_ENCODINGS;
    private static final int ASTERISK_ENCODING;
    private final boolean usingCheckDigit;
    private final boolean extendedMode;
    
    public Code39Reader() {
        this.usingCheckDigit = false;
        this.extendedMode = false;
    }
    
    public Code39Reader(final boolean usingCheckDigit) {
        this.usingCheckDigit = usingCheckDigit;
        this.extendedMode = false;
    }
    
    public Code39Reader(final boolean usingCheckDigit, final boolean extendedMode) {
        this.usingCheckDigit = usingCheckDigit;
        this.extendedMode = extendedMode;
    }
    
    @Override
    public Result decodeRow(final int rowNumber, final BitArray row, final Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
        final int[] counters = new int[9];
        final int[] start = findAsteriskPattern(row, counters);
        int nextStart = row.getNextSet(start[1]);
        final int end = row.getSize();
        final StringBuilder result = new StringBuilder(20);
        char decodedChar;
        int lastStart;
        do {
            OneDReader.recordPattern(row, nextStart, counters);
            final int pattern = toNarrowWidePattern(counters);
            if (pattern < 0) {
                throw NotFoundException.getNotFoundInstance();
            }
            decodedChar = patternToChar(pattern);
            result.append(decodedChar);
            lastStart = nextStart;
            for (final int counter : counters) {
                nextStart += counter;
            }
            nextStart = row.getNextSet(nextStart);
        } while (decodedChar != '*');
        result.setLength(result.length() - 1);
        int lastPatternSize = 0;
        for (final int counter : counters) {
            lastPatternSize += counter;
        }
        final int whiteSpaceAfterEnd = nextStart - lastStart - lastPatternSize;
        if (nextStart != end && whiteSpaceAfterEnd >> 1 < lastPatternSize) {
            throw NotFoundException.getNotFoundInstance();
        }
        if (this.usingCheckDigit) {
            final int max = result.length() - 1;
            int total = 0;
            for (int i = 0; i < max; ++i) {
                total += "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".indexOf(result.charAt(i));
            }
            if (result.charAt(max) != Code39Reader.ALPHABET[total % 43]) {
                throw ChecksumException.getChecksumInstance();
            }
            result.setLength(max);
        }
        if (result.length() == 0) {
            throw NotFoundException.getNotFoundInstance();
        }
        String resultString;
        if (this.extendedMode) {
            resultString = decodeExtended(result);
        }
        else {
            resultString = result.toString();
        }
        final float left = (start[1] + start[0]) / 2.0f;
        final float right = (nextStart + lastStart) / 2.0f;
        return new Result(resultString, null, new ResultPoint[] { new ResultPoint(left, (float)rowNumber), new ResultPoint(right, (float)rowNumber) }, BarcodeFormat.CODE_39);
    }
    
    private static int[] findAsteriskPattern(final BitArray row, final int[] counters) throws NotFoundException {
        final int width = row.getSize();
        final int rowOffset = row.getNextSet(0);
        int counterPosition = 0;
        int patternStart = rowOffset;
        boolean isWhite = false;
        final int patternLength = counters.length;
        for (int i = rowOffset; i < width; ++i) {
            if (row.get(i) ^ isWhite) {
                final int n = counterPosition;
                ++counters[n];
            }
            else {
                if (counterPosition == patternLength - 1) {
                    if (toNarrowWidePattern(counters) == Code39Reader.ASTERISK_ENCODING && row.isRange(Math.max(0, patternStart - (i - patternStart >> 1)), patternStart, false)) {
                        return new int[] { patternStart, i };
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
    
    private static int toNarrowWidePattern(final int[] counters) {
        final int numCounters = counters.length;
        int maxNarrowCounter = 0;
        int wideCounters;
        do {
            int minCounter = Integer.MAX_VALUE;
            for (final int counter : counters) {
                if (counter < minCounter && counter > maxNarrowCounter) {
                    minCounter = counter;
                }
            }
            maxNarrowCounter = minCounter;
            wideCounters = 0;
            int totalWideCountersWidth = 0;
            int pattern = 0;
            for (int j = 0; j < numCounters; ++j) {
                final int counter2 = counters[j];
                if (counters[j] > maxNarrowCounter) {
                    pattern |= 1 << numCounters - 1 - j;
                    ++wideCounters;
                    totalWideCountersWidth += counter2;
                }
            }
            if (wideCounters == 3) {
                for (int j = 0; j < numCounters && wideCounters > 0; ++j) {
                    final int counter2 = counters[j];
                    if (counters[j] > maxNarrowCounter) {
                        --wideCounters;
                        if (counter2 << 1 >= totalWideCountersWidth) {
                            return -1;
                        }
                    }
                }
                return pattern;
            }
        } while (wideCounters > 3);
        return -1;
    }
    
    private static char patternToChar(final int pattern) throws NotFoundException {
        for (int i = 0; i < Code39Reader.CHARACTER_ENCODINGS.length; ++i) {
            if (Code39Reader.CHARACTER_ENCODINGS[i] == pattern) {
                return Code39Reader.ALPHABET[i];
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    private static String decodeExtended(final CharSequence encoded) throws FormatException {
        final int length = encoded.length();
        final StringBuilder decoded = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            final char c = encoded.charAt(i);
            if (c == '+' || c == '$' || c == '%' || c == '/') {
                final char next = encoded.charAt(i + 1);
                char decodedChar = '\0';
                switch (c) {
                    case '+': {
                        if (next >= 'A' && next <= 'Z') {
                            decodedChar = (char)(next + ' ');
                            break;
                        }
                        throw FormatException.getFormatInstance();
                    }
                    case '$': {
                        if (next >= 'A' && next <= 'Z') {
                            decodedChar = (char)(next - '@');
                            break;
                        }
                        throw FormatException.getFormatInstance();
                    }
                    case '%': {
                        if (next >= 'A' && next <= 'E') {
                            decodedChar = (char)(next - '&');
                            break;
                        }
                        if (next >= 'F' && next <= 'W') {
                            decodedChar = (char)(next - '\u000b');
                            break;
                        }
                        throw FormatException.getFormatInstance();
                    }
                    case '/': {
                        if (next >= 'A' && next <= 'O') {
                            decodedChar = (char)(next - ' ');
                            break;
                        }
                        if (next == 'Z') {
                            decodedChar = ':';
                            break;
                        }
                        throw FormatException.getFormatInstance();
                    }
                }
                decoded.append(decodedChar);
                ++i;
            }
            else {
                decoded.append(c);
            }
        }
        return decoded.toString();
    }
    
    static {
        ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".toCharArray();
        CHARACTER_ENCODINGS = new int[] { 52, 289, 97, 352, 49, 304, 112, 37, 292, 100, 265, 73, 328, 25, 280, 88, 13, 268, 76, 28, 259, 67, 322, 19, 274, 82, 7, 262, 70, 22, 385, 193, 448, 145, 400, 208, 133, 388, 196, 148, 168, 162, 138, 42 };
        ASTERISK_ENCODING = Code39Reader.CHARACTER_ENCODINGS[39];
    }
}
