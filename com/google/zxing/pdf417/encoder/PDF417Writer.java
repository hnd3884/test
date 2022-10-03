package com.google.zxing.pdf417.encoder;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.EncodeHintType;
import java.util.Map;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;

public final class PDF417Writer implements Writer
{
    @Override
    public BitMatrix encode(final String contents, final BarcodeFormat format, final int width, final int height, final Map<EncodeHintType, ?> hints) throws WriterException {
        return this.encode(contents, format, width, height);
    }
    
    @Override
    public BitMatrix encode(final String contents, final BarcodeFormat format, final int width, final int height) throws WriterException {
        final PDF417 encoder = initializeEncoder(format, false);
        return bitMatrixFromEncoder(encoder, contents, width, height);
    }
    
    public BitMatrix encode(final String contents, final BarcodeFormat format, final boolean compact, final int width, final int height, final int minCols, final int maxCols, final int minRows, final int maxRows, final Compaction compaction) throws WriterException {
        final PDF417 encoder = initializeEncoder(format, compact);
        encoder.setDimensions(maxCols, minCols, maxRows, minRows);
        encoder.setCompaction(compaction);
        return bitMatrixFromEncoder(encoder, contents, width, height);
    }
    
    private static PDF417 initializeEncoder(final BarcodeFormat format, final boolean compact) {
        if (format != BarcodeFormat.PDF_417) {
            throw new IllegalArgumentException("Can only encode PDF_417, but got " + format);
        }
        final PDF417 encoder = new PDF417();
        encoder.setCompact(compact);
        return encoder;
    }
    
    private static BitMatrix bitMatrixFromEncoder(final PDF417 encoder, final String contents, final int width, final int height) throws WriterException {
        final int errorCorrectionLevel = 2;
        encoder.generateBarcodeLogic(contents, errorCorrectionLevel);
        final int lineThickness = 2;
        final int aspectRatio = 4;
        byte[][] originalScale = encoder.getBarcodeMatrix().getScaledMatrix(lineThickness, aspectRatio * lineThickness);
        boolean rotated = false;
        if (height > width ^ originalScale[0].length < originalScale.length) {
            originalScale = rotateArray(originalScale);
            rotated = true;
        }
        final int scaleX = width / originalScale[0].length;
        final int scaleY = height / originalScale.length;
        int scale;
        if (scaleX < scaleY) {
            scale = scaleX;
        }
        else {
            scale = scaleY;
        }
        if (scale > 1) {
            byte[][] scaledMatrix = encoder.getBarcodeMatrix().getScaledMatrix(scale * lineThickness, scale * aspectRatio * lineThickness);
            if (rotated) {
                scaledMatrix = rotateArray(scaledMatrix);
            }
            return bitMatrixFrombitArray(scaledMatrix);
        }
        return bitMatrixFrombitArray(originalScale);
    }
    
    private static BitMatrix bitMatrixFrombitArray(final byte[][] input) {
        final int whiteSpace = 30;
        final BitMatrix output = new BitMatrix(input.length + 2 * whiteSpace, input[0].length + 2 * whiteSpace);
        output.clear();
        for (int ii = 0; ii < input.length; ++ii) {
            for (int jj = 0; jj < input[0].length; ++jj) {
                if (input[ii][jj] == 1) {
                    output.set(ii + whiteSpace, jj + whiteSpace);
                }
            }
        }
        return output;
    }
    
    private static byte[][] rotateArray(final byte[][] bitarray) {
        final byte[][] temp = new byte[bitarray[0].length][bitarray.length];
        for (int ii = 0; ii < bitarray.length; ++ii) {
            final int inverseii = bitarray.length - ii - 1;
            for (int jj = 0; jj < bitarray[0].length; ++jj) {
                temp[jj][inverseii] = bitarray[ii][jj];
            }
        }
        return temp;
    }
}
