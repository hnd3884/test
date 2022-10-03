package com.google.zxing.oned;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.EncodeHintType;
import java.util.Map;
import com.google.zxing.BarcodeFormat;

public final class Code39Writer extends UPCEANWriter
{
    @Override
    public BitMatrix encode(final String contents, final BarcodeFormat format, final int width, final int height, final Map<EncodeHintType, ?> hints) throws WriterException {
        if (format != BarcodeFormat.CODE_39) {
            throw new IllegalArgumentException("Can only encode CODE_39, but got " + format);
        }
        return super.encode(contents, format, width, height, hints);
    }
    
    @Override
    public byte[] encode(final String contents) {
        final int length = contents.length();
        if (length > 80) {
            throw new IllegalArgumentException("Requested contents should be less than 80 digits long, but got " + length);
        }
        final int[] widths = new int[9];
        int codeWidth = 25 + length;
        for (int i = 0; i < length; ++i) {
            final int indexInString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".indexOf(contents.charAt(i));
            toIntArray(Code39Reader.CHARACTER_ENCODINGS[indexInString], widths);
            for (final int width : widths) {
                codeWidth += width;
            }
        }
        final byte[] result = new byte[codeWidth];
        toIntArray(Code39Reader.CHARACTER_ENCODINGS[39], widths);
        int pos = OneDimensionalCodeWriter.appendPattern(result, 0, widths, 1);
        final int[] narrowWhite = { 1 };
        pos += OneDimensionalCodeWriter.appendPattern(result, pos, narrowWhite, 0);
        for (int j = length - 1; j >= 0; --j) {
            final int indexInString2 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".indexOf(contents.charAt(j));
            toIntArray(Code39Reader.CHARACTER_ENCODINGS[indexInString2], widths);
            pos += OneDimensionalCodeWriter.appendPattern(result, pos, widths, 1);
            pos += OneDimensionalCodeWriter.appendPattern(result, pos, narrowWhite, 0);
        }
        toIntArray(Code39Reader.CHARACTER_ENCODINGS[39], widths);
        pos += OneDimensionalCodeWriter.appendPattern(result, pos, widths, 1);
        return result;
    }
    
    private static void toIntArray(final int a, final int[] toReturn) {
        for (int i = 0; i < 9; ++i) {
            final int temp = a & 1 << i;
            toReturn[i] = ((temp == 0) ? 1 : 2);
        }
    }
}
