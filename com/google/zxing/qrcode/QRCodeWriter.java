package com.google.zxing.qrcode;

import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.WriterException;
import com.google.zxing.EncodeHintType;
import java.util.Map;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;

public final class QRCodeWriter implements Writer
{
    private static final int QUIET_ZONE_SIZE = 4;
    
    @Override
    public BitMatrix encode(final String contents, final BarcodeFormat format, final int width, final int height) throws WriterException {
        return this.encode(contents, format, width, height, null);
    }
    
    @Override
    public BitMatrix encode(final String contents, final BarcodeFormat format, final int width, final int height, final Map<EncodeHintType, ?> hints) throws WriterException {
        if (contents.length() == 0) {
            throw new IllegalArgumentException("Found empty contents");
        }
        if (format != BarcodeFormat.QR_CODE) {
            throw new IllegalArgumentException("Can only encode QR_CODE, but got " + format);
        }
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Requested dimensions are too small: " + width + 'x' + height);
        }
        ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
        if (hints != null) {
            final ErrorCorrectionLevel requestedECLevel = (ErrorCorrectionLevel)hints.get(EncodeHintType.ERROR_CORRECTION);
            if (requestedECLevel != null) {
                errorCorrectionLevel = requestedECLevel;
            }
        }
        final QRCode code = new QRCode();
        Encoder.encode(contents, errorCorrectionLevel, hints, code);
        return renderResult(code, width, height);
    }
    
    private static BitMatrix renderResult(final QRCode code, final int width, final int height) {
        final ByteMatrix input = code.getMatrix();
        if (input == null) {
            throw new IllegalStateException();
        }
        final int inputWidth = input.getWidth();
        final int inputHeight = input.getHeight();
        final int qrWidth = inputWidth + 8;
        final int qrHeight = inputHeight + 8;
        final int outputWidth = Math.max(width, qrWidth);
        final int outputHeight = Math.max(height, qrHeight);
        final int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
        final int leftPadding = (outputWidth - inputWidth * multiple) / 2;
        final int topPadding = (outputHeight - inputHeight * multiple) / 2;
        final BitMatrix output = new BitMatrix(outputWidth, outputHeight);
        for (int inputY = 0, outputY = topPadding; inputY < inputHeight; ++inputY, outputY += multiple) {
            for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; ++inputX, outputX += multiple) {
                if (input.get(inputX, inputY) == 1) {
                    output.setRegion(outputX, outputY, multiple, multiple);
                }
            }
        }
        return output;
    }
}
