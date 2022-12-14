package com.google.zxing.maxicode;

import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.FormatException;
import com.google.zxing.ChecksumException;
import com.google.zxing.NotFoundException;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.Result;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.maxicode.decoder.Decoder;
import com.google.zxing.ResultPoint;
import com.google.zxing.Reader;

public final class MaxiCodeReader implements Reader
{
    private static final ResultPoint[] NO_POINTS;
    private static final int MATRIX_WIDTH = 30;
    private static final int MATRIX_HEIGHT = 33;
    private final Decoder decoder;
    
    public MaxiCodeReader() {
        this.decoder = new Decoder();
    }
    
    Decoder getDecoder() {
        return this.decoder;
    }
    
    @Override
    public Result decode(final BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
        return this.decode(image, null);
    }
    
    @Override
    public Result decode(final BinaryBitmap image, final Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
        if (hints != null && hints.containsKey(DecodeHintType.PURE_BARCODE)) {
            final BitMatrix bits = extractPureBits(image.getBlackMatrix());
            final DecoderResult decoderResult = this.decoder.decode(bits, hints);
            final ResultPoint[] points = MaxiCodeReader.NO_POINTS;
            final Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.MAXICODE);
            final String ecLevel = decoderResult.getECLevel();
            if (ecLevel != null) {
                result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, ecLevel);
            }
            return result;
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    @Override
    public void reset() {
    }
    
    private static BitMatrix extractPureBits(final BitMatrix image) throws NotFoundException {
        final int[] enclosingRectangle = image.getEnclosingRectangle();
        if (enclosingRectangle == null) {
            throw NotFoundException.getNotFoundInstance();
        }
        final int left = enclosingRectangle[0];
        final int top = enclosingRectangle[1];
        final int width = enclosingRectangle[2];
        final int height = enclosingRectangle[3];
        final BitMatrix bits = new BitMatrix(30, 33);
        for (int y = 0; y < 33; ++y) {
            final int iy = top + (y * height + height / 2) / 33;
            for (int x = 0; x < 30; ++x) {
                final int ix = left + (x * width + width / 2 + (y & 0x1) * width / 2) / 30;
                if (image.get(ix, iy)) {
                    bits.set(x, y);
                }
            }
        }
        return bits;
    }
    
    static {
        NO_POINTS = new ResultPoint[0];
    }
}
