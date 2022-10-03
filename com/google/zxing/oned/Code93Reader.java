package com.google.zxing.oned;

import com.google.zxing.FormatException;
import com.google.zxing.ChecksumException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.common.BitArray;

public final class Code93Reader extends OneDReader
{
    private static final String ALPHABET_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*";
    private static final char[] ALPHABET;
    private static final int[] CHARACTER_ENCODINGS;
    private static final int ASTERISK_ENCODING;
    
    @Override
    public Result decodeRow(final int rowNumber, final BitArray row, final Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
        final int[] start = findAsteriskPattern(row);
        int nextStart = row.getNextSet(start[1]);
        final int end = row.getSize();
        final StringBuilder result = new StringBuilder(20);
        final int[] counters = new int[6];
        char decodedChar;
        int lastStart;
        do {
            OneDReader.recordPattern(row, nextStart, counters);
            final int pattern = toPattern(counters);
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
        result.deleteCharAt(result.length() - 1);
        if (nextStart == end || !row.get(nextStart)) {
            throw NotFoundException.getNotFoundInstance();
        }
        if (result.length() < 2) {
            throw NotFoundException.getNotFoundInstance();
        }
        checkChecksums(result);
        result.setLength(result.length() - 2);
        final String resultString = decodeExtended(result);
        final float left = (start[1] + start[0]) / 2.0f;
        final float right = (nextStart + lastStart) / 2.0f;
        return new Result(resultString, null, new ResultPoint[] { new ResultPoint(left, (float)rowNumber), new ResultPoint(right, (float)rowNumber) }, BarcodeFormat.CODE_93);
    }
    
    private static int[] findAsteriskPattern(final BitArray row) throws NotFoundException {
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
                    if (toPattern(counters) == Code93Reader.ASTERISK_ENCODING) {
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
    
    private static int toPattern(final int[] counters) {
        final int max = counters.length;
        int sum = 0;
        for (int i = 0; i < max; ++i) {
            sum += counters[i];
        }
        int pattern = 0;
        for (int j = 0; j < max; ++j) {
            final int scaledShifted = (counters[j] << 8) * 9 / sum;
            int scaledUnshifted = scaledShifted >> 8;
            if ((scaledShifted & 0xFF) > 127) {
                ++scaledUnshifted;
            }
            if (scaledUnshifted < 1 || scaledUnshifted > 4) {
                return -1;
            }
            if ((j & 0x1) == 0x0) {
                for (int k = 0; k < scaledUnshifted; ++k) {
                    pattern = (pattern << 1 | 0x1);
                }
            }
            else {
                pattern <<= scaledUnshifted;
            }
        }
        return pattern;
    }
    
    private static char patternToChar(final int pattern) throws NotFoundException {
        for (int i = 0; i < Code93Reader.CHARACTER_ENCODINGS.length; ++i) {
            if (Code93Reader.CHARACTER_ENCODINGS[i] == pattern) {
                return Code93Reader.ALPHABET[i];
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    private static String decodeExtended(final CharSequence encoded) throws FormatException {
        final int length = encoded.length();
        final StringBuilder decoded = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            final char c = encoded.charAt(i);
            if (c >= 'a' && c <= 'd') {
                final char next = encoded.charAt(i + 1);
                char decodedChar = '\0';
                switch (c) {
                    case 'd': {
                        if (next >= 'A' && next <= 'Z') {
                            decodedChar = (char)(next + ' ');
                            break;
                        }
                        throw FormatException.getFormatInstance();
                    }
                    case 'a': {
                        if (next >= 'A' && next <= 'Z') {
                            decodedChar = (char)(next - '@');
                            break;
                        }
                        throw FormatException.getFormatInstance();
                    }
                    case 'b': {
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
                    case 'c': {
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
    
    private static void checkChecksums(final CharSequence result) throws ChecksumException {
        final int length = result.length();
        checkOneChecksum(result, length - 2, 20);
        checkOneChecksum(result, length - 1, 15);
    }
    
    private static void checkOneChecksum(final CharSequence result, final int checkPosition, final int weightMax) throws ChecksumException {
        int weight = 1;
        int total = 0;
        for (int i = checkPosition - 1; i >= 0; --i) {
            total += weight * "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".indexOf(result.charAt(i));
            if (++weight > weightMax) {
                weight = 1;
            }
        }
        if (result.charAt(checkPosition) != Code93Reader.ALPHABET[total % 47]) {
            throw ChecksumException.getChecksumInstance();
        }
    }
    
    static {
        ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".toCharArray();
        CHARACTER_ENCODINGS = new int[] { 276, 328, 324, 322, 296, 292, 290, 336, 274, 266, 424, 420, 418, 404, 402, 394, 360, 356, 354, 308, 282, 344, 332, 326, 300, 278, 436, 434, 428, 422, 406, 410, 364, 358, 310, 314, 302, 468, 466, 458, 366, 374, 430, 294, 474, 470, 306, 350 };
        ASTERISK_ENCODING = Code93Reader.CHARACTER_ENCODINGS[47];
    }
}
