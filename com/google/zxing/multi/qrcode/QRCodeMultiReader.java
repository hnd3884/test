package com.google.zxing.multi.qrcode;

import com.google.zxing.ResultPoint;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.DetectorResult;
import java.util.List;
import com.google.zxing.ReaderException;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.multi.qrcode.detector.MultiDetector;
import java.util.ArrayList;
import com.google.zxing.NotFoundException;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.Result;
import com.google.zxing.multi.MultipleBarcodeReader;
import com.google.zxing.qrcode.QRCodeReader;

public final class QRCodeMultiReader extends QRCodeReader implements MultipleBarcodeReader
{
    private static final Result[] EMPTY_RESULT_ARRAY;
    
    @Override
    public Result[] decodeMultiple(final BinaryBitmap image) throws NotFoundException {
        return this.decodeMultiple(image, null);
    }
    
    @Override
    public Result[] decodeMultiple(final BinaryBitmap image, final Map<DecodeHintType, ?> hints) throws NotFoundException {
        final List<Result> results = new ArrayList<Result>();
        final DetectorResult[] arr$;
        final DetectorResult[] detectorResults = arr$ = new MultiDetector(image.getBlackMatrix()).detectMulti(hints);
        for (final DetectorResult detectorResult : arr$) {
            try {
                final DecoderResult decoderResult = this.getDecoder().decode(detectorResult.getBits());
                final ResultPoint[] points = detectorResult.getPoints();
                final Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.QR_CODE);
                final List<byte[]> byteSegments = decoderResult.getByteSegments();
                if (byteSegments != null) {
                    result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, byteSegments);
                }
                final String ecLevel = decoderResult.getECLevel();
                if (ecLevel != null) {
                    result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, ecLevel);
                }
                results.add(result);
            }
            catch (final ReaderException ex) {}
        }
        if (results.isEmpty()) {
            return QRCodeMultiReader.EMPTY_RESULT_ARRAY;
        }
        return results.toArray(new Result[results.size()]);
    }
    
    static {
        EMPTY_RESULT_ARRAY = new Result[0];
    }
}
