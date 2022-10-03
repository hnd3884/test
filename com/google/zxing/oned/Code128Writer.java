package com.google.zxing.oned;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.EncodeHintType;
import java.util.Map;
import com.google.zxing.BarcodeFormat;

public final class Code128Writer extends UPCEANWriter
{
    private static final int CODE_START_B = 104;
    private static final int CODE_START_C = 105;
    private static final int CODE_CODE_B = 100;
    private static final int CODE_CODE_C = 99;
    private static final int CODE_STOP = 106;
    private static final char ESCAPE_FNC_1 = '\u00f1';
    private static final char ESCAPE_FNC_2 = '\u00f2';
    private static final char ESCAPE_FNC_3 = '\u00f3';
    private static final char ESCAPE_FNC_4 = '\u00f4';
    private static final int CODE_FNC_1 = 102;
    private static final int CODE_FNC_2 = 97;
    private static final int CODE_FNC_3 = 96;
    private static final int CODE_FNC_4_B = 100;
    
    @Override
    public BitMatrix encode(final String contents, final BarcodeFormat format, final int width, final int height, final Map<EncodeHintType, ?> hints) throws WriterException {
        if (format != BarcodeFormat.CODE_128) {
            throw new IllegalArgumentException("Can only encode CODE_128, but got " + format);
        }
        return super.encode(contents, format, width, height, hints);
    }
    
    @Override
    public byte[] encode(final String contents) {
        final int length = contents.length();
        if (length < 1 || length > 80) {
            throw new IllegalArgumentException("Contents length should be between 1 and 80 characters, but got " + length);
        }
        for (int i = 0; i < length; ++i) {
            final char c = contents.charAt(i);
            if (c < ' ' || c > '~') {
                switch (c) {
                    case '\u00f1':
                    case '\u00f2':
                    case '\u00f3':
                    case '\u00f4': {
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Bad character in input: " + c);
                    }
                }
            }
        }
        final Collection<int[]> patterns = new ArrayList<int[]>();
        int checkSum = 0;
        int checkWeight = 1;
        int codeSet = 0;
        int position = 0;
        while (position < length) {
            final int requiredDigitCount = (codeSet == 99) ? 2 : 4;
            int newCodeSet;
            if (isDigits(contents, position, requiredDigitCount)) {
                newCodeSet = 99;
            }
            else {
                newCodeSet = 100;
            }
            int patternIndex = 0;
            if (newCodeSet == codeSet) {
                if (codeSet == 100) {
                    patternIndex = contents.charAt(position) - ' ';
                    ++position;
                }
                else {
                    switch (contents.charAt(position)) {
                        case '\u00f1': {
                            patternIndex = 102;
                            ++position;
                            break;
                        }
                        case '\u00f2': {
                            patternIndex = 97;
                            ++position;
                            break;
                        }
                        case '\u00f3': {
                            patternIndex = 96;
                            ++position;
                            break;
                        }
                        case '\u00f4': {
                            patternIndex = 100;
                            ++position;
                            break;
                        }
                        default: {
                            patternIndex = Integer.parseInt(contents.substring(position, position + 2));
                            position += 2;
                            break;
                        }
                    }
                }
            }
            else {
                if (codeSet == 0) {
                    if (newCodeSet == 100) {
                        patternIndex = 104;
                    }
                    else {
                        patternIndex = 105;
                    }
                }
                else {
                    patternIndex = newCodeSet;
                }
                codeSet = newCodeSet;
            }
            patterns.add(Code128Reader.CODE_PATTERNS[patternIndex]);
            checkSum += patternIndex * checkWeight;
            if (position != 0) {
                ++checkWeight;
            }
        }
        checkSum %= 103;
        patterns.add(Code128Reader.CODE_PATTERNS[checkSum]);
        patterns.add(Code128Reader.CODE_PATTERNS[106]);
        int codeWidth = 0;
        for (final int[] arr$ : patterns) {
            final int[] pattern = arr$;
            for (final int width : arr$) {
                codeWidth += width;
            }
        }
        final byte[] result = new byte[codeWidth];
        int pos = 0;
        for (final int[] pattern2 : patterns) {
            pos += OneDimensionalCodeWriter.appendPattern(result, pos, pattern2, 1);
        }
        return result;
    }
    
    private static boolean isDigits(final CharSequence value, final int start, final int length) {
        int end = start + length;
        final int last = value.length();
        for (int i = start; i < end && i < last; ++i) {
            final char c = value.charAt(i);
            if (c < '0' || c > '9') {
                if (c != '\u00f1') {
                    return false;
                }
                ++end;
            }
        }
        return end <= last;
    }
}
