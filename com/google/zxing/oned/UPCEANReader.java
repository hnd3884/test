package com.google.zxing.oned;

import com.google.zxing.ResultMetadataType;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ReaderException;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.FormatException;
import com.google.zxing.ChecksumException;
import com.google.zxing.Result;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.NotFoundException;
import java.util.Arrays;
import com.google.zxing.common.BitArray;

public abstract class UPCEANReader extends OneDReader
{
    private static final int MAX_AVG_VARIANCE = 122;
    private static final int MAX_INDIVIDUAL_VARIANCE = 179;
    static final int[] START_END_PATTERN;
    static final int[] MIDDLE_PATTERN;
    static final int[][] L_PATTERNS;
    static final int[][] L_AND_G_PATTERNS;
    private final StringBuilder decodeRowStringBuffer;
    private final UPCEANExtensionSupport extensionReader;
    private final EANManufacturerOrgSupport eanManSupport;
    
    protected UPCEANReader() {
        this.decodeRowStringBuffer = new StringBuilder(20);
        this.extensionReader = new UPCEANExtensionSupport();
        this.eanManSupport = new EANManufacturerOrgSupport();
    }
    
    static int[] findStartGuardPattern(final BitArray row) throws NotFoundException {
        boolean foundStart = false;
        int[] startRange = null;
        int nextStart = 0;
        final int[] counters = new int[UPCEANReader.START_END_PATTERN.length];
        while (!foundStart) {
            Arrays.fill(counters, 0, UPCEANReader.START_END_PATTERN.length, 0);
            startRange = findGuardPattern(row, nextStart, false, UPCEANReader.START_END_PATTERN, counters);
            final int start = startRange[0];
            nextStart = startRange[1];
            final int quietStart = start - (nextStart - start);
            if (quietStart >= 0) {
                foundStart = row.isRange(quietStart, start, false);
            }
        }
        return startRange;
    }
    
    @Override
    public Result decodeRow(final int rowNumber, final BitArray row, final Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
        return this.decodeRow(rowNumber, row, findStartGuardPattern(row), hints);
    }
    
    public Result decodeRow(final int rowNumber, final BitArray row, final int[] startGuardRange, final Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
        final ResultPointCallback resultPointCallback = (hints == null) ? null : ((ResultPointCallback)hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK));
        if (resultPointCallback != null) {
            resultPointCallback.foundPossibleResultPoint(new ResultPoint((startGuardRange[0] + startGuardRange[1]) / 2.0f, (float)rowNumber));
        }
        final StringBuilder result = this.decodeRowStringBuffer;
        result.setLength(0);
        final int endStart = this.decodeMiddle(row, startGuardRange, result);
        if (resultPointCallback != null) {
            resultPointCallback.foundPossibleResultPoint(new ResultPoint((float)endStart, (float)rowNumber));
        }
        final int[] endRange = this.decodeEnd(row, endStart);
        if (resultPointCallback != null) {
            resultPointCallback.foundPossibleResultPoint(new ResultPoint((endRange[0] + endRange[1]) / 2.0f, (float)rowNumber));
        }
        final int end = endRange[1];
        final int quietEnd = end + (end - endRange[0]);
        if (quietEnd >= row.getSize() || !row.isRange(end, quietEnd, false)) {
            throw NotFoundException.getNotFoundInstance();
        }
        final String resultString = result.toString();
        if (!this.checkChecksum(resultString)) {
            throw ChecksumException.getChecksumInstance();
        }
        final float left = (startGuardRange[1] + startGuardRange[0]) / 2.0f;
        final float right = (endRange[1] + endRange[0]) / 2.0f;
        final BarcodeFormat format = this.getBarcodeFormat();
        final Result decodeResult = new Result(resultString, null, new ResultPoint[] { new ResultPoint(left, (float)rowNumber), new ResultPoint(right, (float)rowNumber) }, format);
        try {
            final Result extensionResult = this.extensionReader.decodeRow(rowNumber, row, endRange[1]);
            decodeResult.putAllMetadata(extensionResult.getResultMetadata());
            decodeResult.addResultPoints(extensionResult.getResultPoints());
        }
        catch (final ReaderException ex) {}
        if (format == BarcodeFormat.EAN_13 || format == BarcodeFormat.UPC_A) {
            final String countryID = this.eanManSupport.lookupCountryIdentifier(resultString);
            if (countryID != null) {
                decodeResult.putMetadata(ResultMetadataType.POSSIBLE_COUNTRY, countryID);
            }
        }
        return decodeResult;
    }
    
    boolean checkChecksum(final String s) throws ChecksumException, FormatException {
        return checkStandardUPCEANChecksum(s);
    }
    
    private static boolean checkStandardUPCEANChecksum(final CharSequence s) throws FormatException {
        final int length = s.length();
        if (length == 0) {
            return false;
        }
        int sum = 0;
        for (int i = length - 2; i >= 0; i -= 2) {
            final int digit = s.charAt(i) - '0';
            if (digit < 0 || digit > 9) {
                throw FormatException.getFormatInstance();
            }
            sum += digit;
        }
        sum *= 3;
        for (int i = length - 1; i >= 0; i -= 2) {
            final int digit = s.charAt(i) - '0';
            if (digit < 0 || digit > 9) {
                throw FormatException.getFormatInstance();
            }
            sum += digit;
        }
        return sum % 10 == 0;
    }
    
    int[] decodeEnd(final BitArray row, final int endStart) throws NotFoundException {
        return findGuardPattern(row, endStart, false, UPCEANReader.START_END_PATTERN);
    }
    
    static int[] findGuardPattern(final BitArray row, final int rowOffset, final boolean whiteFirst, final int[] pattern) throws NotFoundException {
        return findGuardPattern(row, rowOffset, whiteFirst, pattern, new int[pattern.length]);
    }
    
    static int[] findGuardPattern(final BitArray row, int rowOffset, final boolean whiteFirst, final int[] pattern, final int[] counters) throws NotFoundException {
        final int patternLength = pattern.length;
        final int width = row.getSize();
        boolean isWhite = whiteFirst;
        rowOffset = (whiteFirst ? row.getNextUnset(rowOffset) : row.getNextSet(rowOffset));
        int counterPosition = 0;
        int patternStart = rowOffset;
        for (int x = rowOffset; x < width; ++x) {
            if (row.get(x) ^ isWhite) {
                final int n = counterPosition;
                ++counters[n];
            }
            else {
                if (counterPosition == patternLength - 1) {
                    if (OneDReader.patternMatchVariance(counters, pattern, 179) < 122) {
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
    
    static int decodeDigit(final BitArray row, final int[] counters, final int rowOffset, final int[][] patterns) throws NotFoundException {
        OneDReader.recordPattern(row, rowOffset, counters);
        int bestVariance = 122;
        int bestMatch = -1;
        for (int max = patterns.length, i = 0; i < max; ++i) {
            final int[] pattern = patterns[i];
            final int variance = OneDReader.patternMatchVariance(counters, pattern, 179);
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
    
    abstract BarcodeFormat getBarcodeFormat();
    
    protected abstract int decodeMiddle(final BitArray p0, final int[] p1, final StringBuilder p2) throws NotFoundException;
    
    static {
        START_END_PATTERN = new int[] { 1, 1, 1 };
        MIDDLE_PATTERN = new int[] { 1, 1, 1, 1, 1 };
        L_PATTERNS = new int[][] { { 3, 2, 1, 1 }, { 2, 2, 2, 1 }, { 2, 1, 2, 2 }, { 1, 4, 1, 1 }, { 1, 1, 3, 2 }, { 1, 2, 3, 1 }, { 1, 1, 1, 4 }, { 1, 3, 1, 2 }, { 1, 2, 1, 3 }, { 3, 1, 1, 2 } };
        L_AND_G_PATTERNS = new int[20][];
        System.arraycopy(UPCEANReader.L_PATTERNS, 0, UPCEANReader.L_AND_G_PATTERNS, 0, 10);
        for (int i = 10; i < 20; ++i) {
            final int[] widths = UPCEANReader.L_PATTERNS[i - 10];
            final int[] reversedWidths = new int[widths.length];
            for (int j = 0; j < widths.length; ++j) {
                reversedWidths[j] = widths[widths.length - j - 1];
            }
            UPCEANReader.L_AND_G_PATTERNS[i] = reversedWidths;
        }
    }
}
