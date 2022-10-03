package com.google.zxing.pdf417.decoder;

import java.util.List;
import com.google.zxing.FormatException;
import com.google.zxing.common.DecoderResult;
import java.math.BigInteger;

final class DecodedBitStreamParser
{
    private static final int TEXT_COMPACTION_MODE_LATCH = 900;
    private static final int BYTE_COMPACTION_MODE_LATCH = 901;
    private static final int NUMERIC_COMPACTION_MODE_LATCH = 902;
    private static final int BYTE_COMPACTION_MODE_LATCH_6 = 924;
    private static final int BEGIN_MACRO_PDF417_CONTROL_BLOCK = 928;
    private static final int BEGIN_MACRO_PDF417_OPTIONAL_FIELD = 923;
    private static final int MACRO_PDF417_TERMINATOR = 922;
    private static final int MODE_SHIFT_TO_BYTE_COMPACTION_MODE = 913;
    private static final int MAX_NUMERIC_CODEWORDS = 15;
    private static final int PL = 25;
    private static final int LL = 27;
    private static final int AS = 27;
    private static final int ML = 28;
    private static final int AL = 28;
    private static final int PS = 29;
    private static final int PAL = 29;
    private static final char[] PUNCT_CHARS;
    private static final char[] MIXED_CHARS;
    private static final BigInteger[] EXP900;
    
    private DecodedBitStreamParser() {
    }
    
    static DecoderResult decode(final int[] codewords) throws FormatException {
        final StringBuilder result = new StringBuilder(100);
        for (int codeIndex = 1, code = codewords[codeIndex++]; codeIndex < codewords[0]; code = codewords[codeIndex++]) {
            switch (code) {
                case 900: {
                    codeIndex = textCompaction(codewords, codeIndex, result);
                    break;
                }
                case 901: {
                    codeIndex = byteCompaction(code, codewords, codeIndex, result);
                    break;
                }
                case 902: {
                    codeIndex = numericCompaction(codewords, codeIndex, result);
                    break;
                }
                case 913: {
                    codeIndex = byteCompaction(code, codewords, codeIndex, result);
                    break;
                }
                case 924: {
                    codeIndex = byteCompaction(code, codewords, codeIndex, result);
                    break;
                }
                default: {
                    --codeIndex;
                    codeIndex = textCompaction(codewords, codeIndex, result);
                    break;
                }
            }
            if (codeIndex >= codewords.length) {
                throw FormatException.getFormatInstance();
            }
        }
        return new DecoderResult(null, result.toString(), null, null);
    }
    
    private static int textCompaction(final int[] codewords, int codeIndex, final StringBuilder result) {
        final int[] textCompactionData = new int[codewords[0] << 1];
        final int[] byteCompactionData = new int[codewords[0] << 1];
        int index = 0;
        boolean end = false;
        while (codeIndex < codewords[0] && !end) {
            int code = codewords[codeIndex++];
            if (code < 900) {
                textCompactionData[index] = code / 30;
                textCompactionData[index + 1] = code % 30;
                index += 2;
            }
            else {
                switch (code) {
                    case 900: {
                        --codeIndex;
                        end = true;
                        continue;
                    }
                    case 901: {
                        --codeIndex;
                        end = true;
                        continue;
                    }
                    case 902: {
                        --codeIndex;
                        end = true;
                        continue;
                    }
                    case 913: {
                        textCompactionData[index] = 913;
                        code = codewords[codeIndex++];
                        byteCompactionData[index] = code;
                        ++index;
                        continue;
                    }
                    case 924: {
                        --codeIndex;
                        end = true;
                        continue;
                    }
                }
            }
        }
        decodeTextCompaction(textCompactionData, byteCompactionData, index, result);
        return codeIndex;
    }
    
    private static void decodeTextCompaction(final int[] textCompactionData, final int[] byteCompactionData, final int length, final StringBuilder result) {
        Mode subMode = Mode.ALPHA;
        Mode priorToShiftMode = Mode.ALPHA;
        for (int i = 0; i < length; ++i) {
            final int subModeCh = textCompactionData[i];
            char ch = '\0';
            switch (subMode) {
                case ALPHA: {
                    if (subModeCh < 26) {
                        ch = (char)(65 + subModeCh);
                        break;
                    }
                    if (subModeCh == 26) {
                        ch = ' ';
                        break;
                    }
                    if (subModeCh == 27) {
                        subMode = Mode.LOWER;
                        break;
                    }
                    if (subModeCh == 28) {
                        subMode = Mode.MIXED;
                        break;
                    }
                    if (subModeCh == 29) {
                        priorToShiftMode = subMode;
                        subMode = Mode.PUNCT_SHIFT;
                        break;
                    }
                    if (subModeCh == 913) {
                        result.append((char)byteCompactionData[i]);
                        break;
                    }
                    break;
                }
                case LOWER: {
                    if (subModeCh < 26) {
                        ch = (char)(97 + subModeCh);
                        break;
                    }
                    if (subModeCh == 26) {
                        ch = ' ';
                        break;
                    }
                    if (subModeCh == 27) {
                        priorToShiftMode = subMode;
                        subMode = Mode.ALPHA_SHIFT;
                        break;
                    }
                    if (subModeCh == 28) {
                        subMode = Mode.MIXED;
                        break;
                    }
                    if (subModeCh == 29) {
                        priorToShiftMode = subMode;
                        subMode = Mode.PUNCT_SHIFT;
                        break;
                    }
                    if (subModeCh == 913) {
                        result.append((char)byteCompactionData[i]);
                        break;
                    }
                    break;
                }
                case MIXED: {
                    if (subModeCh < 25) {
                        ch = DecodedBitStreamParser.MIXED_CHARS[subModeCh];
                        break;
                    }
                    if (subModeCh == 25) {
                        subMode = Mode.PUNCT;
                        break;
                    }
                    if (subModeCh == 26) {
                        ch = ' ';
                        break;
                    }
                    if (subModeCh == 27) {
                        subMode = Mode.LOWER;
                        break;
                    }
                    if (subModeCh == 28) {
                        subMode = Mode.ALPHA;
                        break;
                    }
                    if (subModeCh == 29) {
                        priorToShiftMode = subMode;
                        subMode = Mode.PUNCT_SHIFT;
                        break;
                    }
                    if (subModeCh == 913) {
                        result.append((char)byteCompactionData[i]);
                        break;
                    }
                    break;
                }
                case PUNCT: {
                    if (subModeCh < 29) {
                        ch = DecodedBitStreamParser.PUNCT_CHARS[subModeCh];
                        break;
                    }
                    if (subModeCh == 29) {
                        subMode = Mode.ALPHA;
                        break;
                    }
                    if (subModeCh == 913) {
                        result.append((char)byteCompactionData[i]);
                        break;
                    }
                    break;
                }
                case ALPHA_SHIFT: {
                    subMode = priorToShiftMode;
                    if (subModeCh < 26) {
                        ch = (char)(65 + subModeCh);
                        break;
                    }
                    if (subModeCh == 26) {
                        ch = ' ';
                        break;
                    }
                    break;
                }
                case PUNCT_SHIFT: {
                    subMode = priorToShiftMode;
                    if (subModeCh < 29) {
                        ch = DecodedBitStreamParser.PUNCT_CHARS[subModeCh];
                        break;
                    }
                    if (subModeCh == 29) {
                        subMode = Mode.ALPHA;
                        break;
                    }
                    break;
                }
            }
            if (ch != '\0') {
                result.append(ch);
            }
        }
    }
    
    private static int byteCompaction(final int mode, final int[] codewords, int codeIndex, final StringBuilder result) {
        if (mode == 901) {
            int count = 0;
            long value = 0L;
            final char[] decodedData = new char[6];
            final int[] byteCompactedCodewords = new int[6];
            boolean end = false;
            while (codeIndex < codewords[0] && !end) {
                final int code = codewords[codeIndex++];
                if (code < 900) {
                    byteCompactedCodewords[count] = code;
                    ++count;
                    value = 900L * value + code;
                }
                else if (code == 900 || code == 901 || code == 902 || code == 924 || code == 928 || code == 923 || code == 922) {
                    --codeIndex;
                    end = true;
                }
                if (count % 5 == 0 && count > 0) {
                    for (int j = 0; j < 6; ++j) {
                        decodedData[5 - j] = (char)(value % 256L);
                        value >>= 8;
                    }
                    result.append(decodedData);
                    count = 0;
                }
            }
            for (int i = count / 5 * 5; i < count; ++i) {
                result.append((char)byteCompactedCodewords[i]);
            }
        }
        else if (mode == 924) {
            int count = 0;
            long value = 0L;
            boolean end2 = false;
            while (codeIndex < codewords[0] && !end2) {
                final int code2 = codewords[codeIndex++];
                if (code2 < 900) {
                    ++count;
                    value = 900L * value + code2;
                }
                else if (code2 == 900 || code2 == 901 || code2 == 902 || code2 == 924 || code2 == 928 || code2 == 923 || code2 == 922) {
                    --codeIndex;
                    end2 = true;
                }
                if (count % 5 == 0 && count > 0) {
                    final char[] decodedData2 = new char[6];
                    for (int k = 0; k < 6; ++k) {
                        decodedData2[5 - k] = (char)(value & 0xFFL);
                        value >>= 8;
                    }
                    result.append(decodedData2);
                }
            }
        }
        return codeIndex;
    }
    
    private static int numericCompaction(final int[] codewords, int codeIndex, final StringBuilder result) throws FormatException {
        int count = 0;
        boolean end = false;
        final int[] numericCodewords = new int[15];
        while (codeIndex < codewords[0] && !end) {
            final int code = codewords[codeIndex++];
            if (codeIndex == codewords[0]) {
                end = true;
            }
            if (code < 900) {
                numericCodewords[count] = code;
                ++count;
            }
            else if (code == 900 || code == 901 || code == 924 || code == 928 || code == 923 || code == 922) {
                --codeIndex;
                end = true;
            }
            if (count % 15 == 0 || code == 902 || end) {
                final String s = decodeBase900toBase10(numericCodewords, count);
                result.append(s);
                count = 0;
            }
        }
        return codeIndex;
    }
    
    private static String decodeBase900toBase10(final int[] codewords, final int count) throws FormatException {
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < count; ++i) {
            result = result.add(DecodedBitStreamParser.EXP900[count - i - 1].multiply(BigInteger.valueOf(codewords[i])));
        }
        final String resultString = result.toString();
        if (resultString.charAt(0) != '1') {
            throw FormatException.getFormatInstance();
        }
        return resultString.substring(1);
    }
    
    static {
        PUNCT_CHARS = new char[] { ';', '<', '>', '@', '[', '\\', '}', '_', '`', '~', '!', '\r', '\t', ',', ':', '\n', '-', '.', '$', '/', '\"', '|', '*', '(', ')', '?', '{', '}', '\'' };
        MIXED_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '&', '\r', '\t', ',', ':', '#', '-', '.', '$', '/', '+', '%', '*', '=', '^' };
        (EXP900 = new BigInteger[16])[0] = BigInteger.ONE;
        final BigInteger nineHundred = BigInteger.valueOf(900L);
        DecodedBitStreamParser.EXP900[1] = nineHundred;
        for (int i = 2; i < DecodedBitStreamParser.EXP900.length; ++i) {
            DecodedBitStreamParser.EXP900[i] = DecodedBitStreamParser.EXP900[i - 1].multiply(nineHundred);
        }
    }
    
    private enum Mode
    {
        ALPHA, 
        LOWER, 
        MIXED, 
        PUNCT, 
        ALPHA_SHIFT, 
        PUNCT_SHIFT;
    }
}
