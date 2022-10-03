package com.google.zxing.oned;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.EncodeHintType;
import java.util.Map;
import com.google.zxing.BarcodeFormat;

public final class EAN8Writer extends UPCEANWriter
{
    private static final int CODE_WIDTH = 67;
    
    @Override
    public BitMatrix encode(final String contents, final BarcodeFormat format, final int width, final int height, final Map<EncodeHintType, ?> hints) throws WriterException {
        if (format != BarcodeFormat.EAN_8) {
            throw new IllegalArgumentException("Can only encode EAN_8, but got " + format);
        }
        return super.encode(contents, format, width, height, hints);
    }
    
    @Override
    public byte[] encode(final String contents) {
        if (contents.length() != 8) {
            throw new IllegalArgumentException("Requested contents should be 8 digits long, but got " + contents.length());
        }
        final byte[] result = new byte[67];
        int pos = 0;
        pos += OneDimensionalCodeWriter.appendPattern(result, pos, UPCEANReader.START_END_PATTERN, 1);
        for (int i = 0; i <= 3; ++i) {
            final int digit = Integer.parseInt(contents.substring(i, i + 1));
            pos += OneDimensionalCodeWriter.appendPattern(result, pos, UPCEANReader.L_PATTERNS[digit], 0);
        }
        pos += OneDimensionalCodeWriter.appendPattern(result, pos, UPCEANReader.MIDDLE_PATTERN, 0);
        for (int i = 4; i <= 7; ++i) {
            final int digit = Integer.parseInt(contents.substring(i, i + 1));
            pos += OneDimensionalCodeWriter.appendPattern(result, pos, UPCEANReader.L_PATTERNS[digit], 1);
        }
        pos += OneDimensionalCodeWriter.appendPattern(result, pos, UPCEANReader.START_END_PATTERN, 1);
        return result;
    }
}
