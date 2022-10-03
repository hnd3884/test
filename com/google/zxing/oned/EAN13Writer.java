package com.google.zxing.oned;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.EncodeHintType;
import java.util.Map;
import com.google.zxing.BarcodeFormat;

public final class EAN13Writer extends UPCEANWriter
{
    private static final int CODE_WIDTH = 95;
    
    @Override
    public BitMatrix encode(final String contents, final BarcodeFormat format, final int width, final int height, final Map<EncodeHintType, ?> hints) throws WriterException {
        if (format != BarcodeFormat.EAN_13) {
            throw new IllegalArgumentException("Can only encode EAN_13, but got " + format);
        }
        return super.encode(contents, format, width, height, hints);
    }
    
    @Override
    public byte[] encode(final String contents) {
        if (contents.length() != 13) {
            throw new IllegalArgumentException("Requested contents should be 13 digits long, but got " + contents.length());
        }
        final int firstDigit = Integer.parseInt(contents.substring(0, 1));
        final int parities = EAN13Reader.FIRST_DIGIT_ENCODINGS[firstDigit];
        final byte[] result = new byte[95];
        int pos = 0;
        pos += OneDimensionalCodeWriter.appendPattern(result, pos, UPCEANReader.START_END_PATTERN, 1);
        for (int i = 1; i <= 6; ++i) {
            int digit = Integer.parseInt(contents.substring(i, i + 1));
            if ((parities >> 6 - i & 0x1) == 0x1) {
                digit += 10;
            }
            pos += OneDimensionalCodeWriter.appendPattern(result, pos, UPCEANReader.L_AND_G_PATTERNS[digit], 0);
        }
        pos += OneDimensionalCodeWriter.appendPattern(result, pos, UPCEANReader.MIDDLE_PATTERN, 0);
        for (int i = 7; i <= 12; ++i) {
            final int digit = Integer.parseInt(contents.substring(i, i + 1));
            pos += OneDimensionalCodeWriter.appendPattern(result, pos, UPCEANReader.L_PATTERNS[digit], 1);
        }
        pos += OneDimensionalCodeWriter.appendPattern(result, pos, UPCEANReader.START_END_PATTERN, 1);
        return result;
    }
}
