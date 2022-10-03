package com.google.zxing.oned;

import com.google.zxing.WriterException;
import com.google.zxing.EncodeHintType;
import java.util.Map;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;

public abstract class OneDimensionalCodeWriter implements Writer
{
    private final int sidesMargin;
    
    protected OneDimensionalCodeWriter(final int sidesMargin) {
        this.sidesMargin = sidesMargin;
    }
    
    @Override
    public BitMatrix encode(final String contents, final BarcodeFormat format, final int width, final int height) throws WriterException {
        return this.encode(contents, format, width, height, null);
    }
    
    @Override
    public BitMatrix encode(final String contents, final BarcodeFormat format, final int width, final int height, final Map<EncodeHintType, ?> hints) throws WriterException {
        if (contents.length() == 0) {
            throw new IllegalArgumentException("Found empty contents");
        }
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Negative size is not allowed. Input: " + width + 'x' + height);
        }
        final byte[] code = this.encode(contents);
        return this.renderResult(code, width, height);
    }
    
    private BitMatrix renderResult(final byte[] code, final int width, final int height) {
        final int inputWidth = code.length;
        final int fullWidth = inputWidth + this.sidesMargin;
        final int outputWidth = Math.max(width, fullWidth);
        final int outputHeight = Math.max(1, height);
        final int multiple = outputWidth / fullWidth;
        final int leftPadding = (outputWidth - inputWidth * multiple) / 2;
        final BitMatrix output = new BitMatrix(outputWidth, outputHeight);
        for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; ++inputX, outputX += multiple) {
            if (code[inputX] == 1) {
                output.setRegion(outputX, 0, multiple, outputHeight);
            }
        }
        return output;
    }
    
    protected static int appendPattern(final byte[] target, int pos, final int[] pattern, final int startColor) {
        if (startColor != 0 && startColor != 1) {
            throw new IllegalArgumentException("startColor must be either 0 or 1, but got: " + startColor);
        }
        byte color = (byte)startColor;
        int numAdded = 0;
        for (final int len : pattern) {
            for (int j = 0; j < len; ++j) {
                target[pos] = color;
                ++pos;
                ++numAdded;
            }
            color ^= 0x1;
        }
        return numAdded;
    }
    
    public abstract byte[] encode(final String p0);
}
