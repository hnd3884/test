package com.google.zxing.pdf417;

import com.google.zxing.common.DetectorResult;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.pdf417.detector.Detector;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.Result;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.pdf417.decoder.Decoder;
import com.google.zxing.ResultPoint;
import com.google.zxing.Reader;

public final class PDF417Reader implements Reader
{
    private static final ResultPoint[] NO_POINTS;
    private final Decoder decoder;
    
    public PDF417Reader() {
        this.decoder = new Decoder();
    }
    
    @Override
    public Result decode(final BinaryBitmap image) throws NotFoundException, FormatException {
        return this.decode(image, null);
    }
    
    @Override
    public Result decode(final BinaryBitmap image, final Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException {
        DecoderResult decoderResult;
        ResultPoint[] points;
        if (hints != null && hints.containsKey(DecodeHintType.PURE_BARCODE)) {
            final BitMatrix bits = extractPureBits(image.getBlackMatrix());
            decoderResult = this.decoder.decode(bits);
            points = PDF417Reader.NO_POINTS;
        }
        else {
            final DetectorResult detectorResult = new Detector(image).detect();
            decoderResult = this.decoder.decode(detectorResult.getBits());
            points = detectorResult.getPoints();
        }
        return new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.PDF_417);
    }
    
    @Override
    public void reset() {
    }
    
    private static BitMatrix extractPureBits(final BitMatrix image) throws NotFoundException {
        final int[] leftTopBlack = image.getTopLeftOnBit();
        final int[] rightBottomBlack = image.getBottomRightOnBit();
        if (leftTopBlack == null || rightBottomBlack == null) {
            throw NotFoundException.getNotFoundInstance();
        }
        final int moduleSize = moduleSize(leftTopBlack, image);
        int top = leftTopBlack[1];
        final int bottom = rightBottomBlack[1];
        int left = findPatternStart(leftTopBlack[0], top, image);
        final int right = findPatternEnd(leftTopBlack[0], top, image);
        final int matrixWidth = (right - left + 1) / moduleSize;
        final int matrixHeight = (bottom - top + 1) / moduleSize;
        if (matrixWidth <= 0 || matrixHeight <= 0) {
            throw NotFoundException.getNotFoundInstance();
        }
        final int nudge = moduleSize >> 1;
        top += nudge;
        left += nudge;
        final BitMatrix bits = new BitMatrix(matrixWidth, matrixHeight);
        for (int y = 0; y < matrixHeight; ++y) {
            final int iOffset = top + y * moduleSize;
            for (int x = 0; x < matrixWidth; ++x) {
                if (image.get(left + x * moduleSize, iOffset)) {
                    bits.set(x, y);
                }
            }
        }
        return bits;
    }
    
    private static int moduleSize(final int[] leftTopBlack, final BitMatrix image) throws NotFoundException {
        int x;
        int y;
        int width;
        for (x = leftTopBlack[0], y = leftTopBlack[1], width = image.getWidth(); x < width && image.get(x, y); ++x) {}
        if (x == width) {
            throw NotFoundException.getNotFoundInstance();
        }
        final int moduleSize = x - leftTopBlack[0] >>> 3;
        if (moduleSize == 0) {
            throw NotFoundException.getNotFoundInstance();
        }
        return moduleSize;
    }
    
    private static int findPatternStart(final int x, final int y, final BitMatrix image) throws NotFoundException {
        final int width = image.getWidth();
        int start = x;
        int transitions = 0;
        boolean black = true;
        while (start < width - 1 && transitions < 8) {
            ++start;
            final boolean newBlack = image.get(start, y);
            if (black != newBlack) {
                ++transitions;
            }
            black = newBlack;
        }
        if (start == width - 1) {
            throw NotFoundException.getNotFoundInstance();
        }
        return start;
    }
    
    private static int findPatternEnd(final int x, final int y, final BitMatrix image) throws NotFoundException {
        final int width = image.getWidth();
        int end;
        for (end = width - 1; end > x && !image.get(end, y); --end) {}
        int transitions = 0;
        boolean black = true;
        while (end > x && transitions < 9) {
            --end;
            final boolean newBlack = image.get(end, y);
            if (black != newBlack) {
                ++transitions;
            }
            black = newBlack;
        }
        if (end == x) {
            throw NotFoundException.getNotFoundInstance();
        }
        return end;
    }
    
    static {
        NO_POINTS = new ResultPoint[0];
    }
}
