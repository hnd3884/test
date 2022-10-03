package com.google.zxing.oned;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.EncodeHintType;
import java.util.Map;
import com.google.zxing.BarcodeFormat;

public final class ITFWriter extends UPCEANWriter
{
    @Override
    public BitMatrix encode(final String contents, final BarcodeFormat format, final int width, final int height, final Map<EncodeHintType, ?> hints) throws WriterException {
        if (format != BarcodeFormat.ITF) {
            throw new IllegalArgumentException("Can only encode ITF, but got " + format);
        }
        return super.encode(contents, format, width, height, hints);
    }
    
    @Override
    public byte[] encode(final String contents) {
        final int length = contents.length();
        if (length % 2 != 0) {
            throw new IllegalArgumentException("The lenght of the input should be even");
        }
        if (length > 80) {
            throw new IllegalArgumentException("Requested contents should be less than 80 digits long, but got " + length);
        }
        final byte[] result = new byte[9 + 9 * length];
        final int[] start = { 1, 1, 1, 1 };
        int pos = OneDimensionalCodeWriter.appendPattern(result, 0, start, 1);
        for (int i = 0; i < length; i += 2) {
            final int one = Character.digit(contents.charAt(i), 10);
            final int two = Character.digit(contents.charAt(i + 1), 10);
            final int[] encoding = new int[18];
            for (int j = 0; j < 5; ++j) {
                encoding[j << 1] = ITFReader.PATTERNS[one][j];
                encoding[(j << 1) + 1] = ITFReader.PATTERNS[two][j];
            }
            pos += OneDimensionalCodeWriter.appendPattern(result, pos, encoding, 1);
        }
        final int[] end = { 3, 1, 1 };
        pos += OneDimensionalCodeWriter.appendPattern(result, pos, end, 1);
        return result;
    }
}
