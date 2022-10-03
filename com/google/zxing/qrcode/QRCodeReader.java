package com.google.zxing.qrcode;

import java.util.List;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.detector.Detector;
import com.google.zxing.FormatException;
import com.google.zxing.ChecksumException;
import com.google.zxing.NotFoundException;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.Result;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.qrcode.decoder.Decoder;
import com.google.zxing.ResultPoint;
import com.google.zxing.Reader;

public class QRCodeReader implements Reader
{
    private static final ResultPoint[] NO_POINTS;
    private final Decoder decoder;
    
    public QRCodeReader() {
        this.decoder = new Decoder();
    }
    
    protected Decoder getDecoder() {
        return this.decoder;
    }
    
    @Override
    public Result decode(final BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
        return this.decode(image, null);
    }
    
    @Override
    public Result decode(final BinaryBitmap image, final Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
        DecoderResult decoderResult;
        ResultPoint[] points;
        if (hints != null && hints.containsKey(DecodeHintType.PURE_BARCODE)) {
            final BitMatrix bits = extractPureBits(image.getBlackMatrix());
            decoderResult = this.decoder.decode(bits, hints);
            points = QRCodeReader.NO_POINTS;
        }
        else {
            final DetectorResult detectorResult = new Detector(image.getBlackMatrix()).detect(hints);
            decoderResult = this.decoder.decode(detectorResult.getBits(), hints);
            points = detectorResult.getPoints();
        }
        final Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.QR_CODE);
        final List<byte[]> byteSegments = decoderResult.getByteSegments();
        if (byteSegments != null) {
            result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, byteSegments);
        }
        final String ecLevel = decoderResult.getECLevel();
        if (ecLevel != null) {
            result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, ecLevel);
        }
        return result;
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
        final float moduleSize = moduleSize(leftTopBlack, image);
        int top = leftTopBlack[1];
        final int bottom = rightBottomBlack[1];
        int left = leftTopBlack[0];
        int right = rightBottomBlack[0];
        if (bottom - top != right - left) {
            right = left + (bottom - top);
        }
        final int matrixWidth = Math.round((right - left + 1) / moduleSize);
        final int matrixHeight = Math.round((bottom - top + 1) / moduleSize);
        if (matrixWidth <= 0 || matrixHeight <= 0) {
            throw NotFoundException.getNotFoundInstance();
        }
        if (matrixHeight != matrixWidth) {
            throw NotFoundException.getNotFoundInstance();
        }
        final int nudge = Math.round(moduleSize / 2.0f);
        top += nudge;
        left += nudge;
        final BitMatrix bits = new BitMatrix(matrixWidth, matrixHeight);
        for (int y = 0; y < matrixHeight; ++y) {
            final int iOffset = top + (int)(y * moduleSize);
            for (int x = 0; x < matrixWidth; ++x) {
                if (image.get(left + (int)(x * moduleSize), iOffset)) {
                    bits.set(x, y);
                }
            }
        }
        return bits;
    }
    
    private static float moduleSize(final int[] leftTopBlack, final BitMatrix image) throws NotFoundException {
        final int height = image.getHeight();
        final int width = image.getWidth();
        int x = leftTopBlack[0];
        int y = leftTopBlack[1];
        boolean inBlack = true;
        int transitions = 0;
        while (x < width && y < height) {
            if (inBlack != image.get(x, y)) {
                if (++transitions == 5) {
                    break;
                }
                inBlack = !inBlack;
            }
            ++x;
            ++y;
        }
        if (x == width || y == height) {
            throw NotFoundException.getNotFoundInstance();
        }
        return (x - leftTopBlack[0]) / 7.0f;
    }
    
    static {
        NO_POINTS = new ResultPoint[0];
    }
}
