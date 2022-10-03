package com.google.zxing.datamatrix;

import java.util.List;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.datamatrix.detector.Detector;
import com.google.zxing.FormatException;
import com.google.zxing.ChecksumException;
import com.google.zxing.NotFoundException;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.Result;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.datamatrix.decoder.Decoder;
import com.google.zxing.ResultPoint;
import com.google.zxing.Reader;

public final class DataMatrixReader implements Reader
{
    private static final ResultPoint[] NO_POINTS;
    private final Decoder decoder;
    
    public DataMatrixReader() {
        this.decoder = new Decoder();
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
            decoderResult = this.decoder.decode(bits);
            points = DataMatrixReader.NO_POINTS;
        }
        else {
            final DetectorResult detectorResult = new Detector(image.getBlackMatrix()).detect();
            decoderResult = this.decoder.decode(detectorResult.getBits());
            points = detectorResult.getPoints();
        }
        final Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.DATA_MATRIX);
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
        final int moduleSize = moduleSize(leftTopBlack, image);
        int top = leftTopBlack[1];
        final int bottom = rightBottomBlack[1];
        int left = leftTopBlack[0];
        final int right = rightBottomBlack[0];
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
        final int width = image.getWidth();
        int x = leftTopBlack[0];
        for (int y = leftTopBlack[1]; x < width && image.get(x, y); ++x) {}
        if (x == width) {
            throw NotFoundException.getNotFoundInstance();
        }
        final int moduleSize = x - leftTopBlack[0];
        if (moduleSize == 0) {
            throw NotFoundException.getNotFoundInstance();
        }
        return moduleSize;
    }
    
    static {
        NO_POINTS = new ResultPoint[0];
    }
}
