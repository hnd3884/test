package com.google.zxing.aztec;

import java.util.List;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.aztec.decoder.Decoder;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.aztec.detector.Detector;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.Result;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.Reader;

public final class AztecReader implements Reader
{
    @Override
    public Result decode(final BinaryBitmap image) throws NotFoundException, FormatException {
        return this.decode(image, null);
    }
    
    @Override
    public Result decode(final BinaryBitmap image, final Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException {
        final AztecDetectorResult detectorResult = new Detector(image.getBlackMatrix()).detect();
        final ResultPoint[] points = detectorResult.getPoints();
        if (hints != null) {
            final ResultPointCallback rpcb = (ResultPointCallback)hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK);
            if (rpcb != null) {
                for (final ResultPoint point : points) {
                    rpcb.foundPossibleResultPoint(point);
                }
            }
        }
        final DecoderResult decoderResult = new Decoder().decode(detectorResult);
        final Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.AZTEC);
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
}
