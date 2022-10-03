package com.google.zxing.oned;

import java.util.EnumMap;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultMetadataType;
import java.util.Map;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.Result;
import com.google.zxing.common.BitArray;

final class UPCEANExtensionSupport
{
    private static final int[] EXTENSION_START_PATTERN;
    private static final int[] CHECK_DIGIT_ENCODINGS;
    private final int[] decodeMiddleCounters;
    private final StringBuilder decodeRowStringBuffer;
    
    UPCEANExtensionSupport() {
        this.decodeMiddleCounters = new int[4];
        this.decodeRowStringBuffer = new StringBuilder();
    }
    
    Result decodeRow(final int rowNumber, final BitArray row, final int rowOffset) throws NotFoundException {
        final int[] extensionStartRange = UPCEANReader.findGuardPattern(row, rowOffset, false, UPCEANExtensionSupport.EXTENSION_START_PATTERN);
        final StringBuilder result = this.decodeRowStringBuffer;
        result.setLength(0);
        final int end = this.decodeMiddle(row, extensionStartRange, result);
        final String resultString = result.toString();
        final Map<ResultMetadataType, Object> extensionData = parseExtensionString(resultString);
        final Result extensionResult = new Result(resultString, null, new ResultPoint[] { new ResultPoint((extensionStartRange[0] + extensionStartRange[1]) / 2.0f, (float)rowNumber), new ResultPoint((float)end, (float)rowNumber) }, BarcodeFormat.UPC_EAN_EXTENSION);
        if (extensionData != null) {
            extensionResult.putAllMetadata(extensionData);
        }
        return extensionResult;
    }
    
    int decodeMiddle(final BitArray row, final int[] startRange, final StringBuilder resultString) throws NotFoundException {
        final int[] counters = this.decodeMiddleCounters;
        counters[1] = (counters[0] = 0);
        counters[3] = (counters[2] = 0);
        final int end = row.getSize();
        int rowOffset = startRange[1];
        int lgPatternFound = 0;
        for (int x = 0; x < 5 && rowOffset < end; ++x) {
            final int bestMatch = UPCEANReader.decodeDigit(row, counters, rowOffset, UPCEANReader.L_AND_G_PATTERNS);
            resultString.append((char)(48 + bestMatch % 10));
            for (final int counter : counters) {
                rowOffset += counter;
            }
            if (bestMatch >= 10) {
                lgPatternFound |= 1 << 4 - x;
            }
            if (x != 4) {
                rowOffset = row.getNextSet(rowOffset);
                rowOffset = row.getNextUnset(rowOffset);
            }
        }
        if (resultString.length() != 5) {
            throw NotFoundException.getNotFoundInstance();
        }
        final int checkDigit = determineCheckDigit(lgPatternFound);
        if (extensionChecksum(resultString.toString()) != checkDigit) {
            throw NotFoundException.getNotFoundInstance();
        }
        return rowOffset;
    }
    
    private static int extensionChecksum(final CharSequence s) {
        final int length = s.length();
        int sum = 0;
        for (int i = length - 2; i >= 0; i -= 2) {
            sum += s.charAt(i) - '0';
        }
        sum *= 3;
        for (int i = length - 1; i >= 0; i -= 2) {
            sum += s.charAt(i) - '0';
        }
        sum *= 3;
        return sum % 10;
    }
    
    private static int determineCheckDigit(final int lgPatternFound) throws NotFoundException {
        for (int d = 0; d < 10; ++d) {
            if (lgPatternFound == UPCEANExtensionSupport.CHECK_DIGIT_ENCODINGS[d]) {
                return d;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    private static Map<ResultMetadataType, Object> parseExtensionString(final String raw) {
        ResultMetadataType type = null;
        Object value = null;
        switch (raw.length()) {
            case 2: {
                type = ResultMetadataType.ISSUE_NUMBER;
                value = parseExtension2String(raw);
                break;
            }
            case 5: {
                type = ResultMetadataType.SUGGESTED_PRICE;
                value = parseExtension5String(raw);
                break;
            }
            default: {
                return null;
            }
        }
        if (value == null) {
            return null;
        }
        final Map<ResultMetadataType, Object> result = new EnumMap<ResultMetadataType, Object>(ResultMetadataType.class);
        result.put(type, value);
        return result;
    }
    
    private static Integer parseExtension2String(final String raw) {
        return Integer.valueOf(raw);
    }
    
    private static String parseExtension5String(final String raw) {
        String currency = null;
        switch (raw.charAt(0)) {
            case '0': {
                currency = "£";
                break;
            }
            case '5': {
                currency = "$";
                break;
            }
            case '9': {
                if ("90000".equals(raw)) {
                    return null;
                }
                if ("99991".equals(raw)) {
                    return "0.00";
                }
                if ("99990".equals(raw)) {
                    return "Used";
                }
                currency = "";
                break;
            }
            default: {
                currency = "";
                break;
            }
        }
        final int rawAmount = Integer.parseInt(raw.substring(1));
        final String unitsString = String.valueOf(rawAmount / 100);
        final int hundredths = rawAmount % 100;
        final String hundredthsString = (hundredths < 10) ? ("0" + hundredths) : String.valueOf(hundredths);
        return currency + unitsString + '.' + hundredthsString;
    }
    
    static {
        EXTENSION_START_PATTERN = new int[] { 1, 1, 2 };
        CHECK_DIGIT_ENCODINGS = new int[] { 24, 20, 18, 17, 12, 6, 3, 10, 9, 5 };
    }
}
