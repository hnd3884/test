package com.google.zxing.multi;

import com.google.zxing.ResultPoint;
import java.util.Iterator;
import com.google.zxing.ReaderException;
import java.util.List;
import java.util.ArrayList;
import com.google.zxing.NotFoundException;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.Result;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.Reader;

public final class GenericMultipleBarcodeReader implements MultipleBarcodeReader
{
    private static final int MIN_DIMENSION_TO_RECUR = 100;
    private final Reader delegate;
    
    public GenericMultipleBarcodeReader(final Reader delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public Result[] decodeMultiple(final BinaryBitmap image) throws NotFoundException {
        return this.decodeMultiple(image, null);
    }
    
    @Override
    public Result[] decodeMultiple(final BinaryBitmap image, final Map<DecodeHintType, ?> hints) throws NotFoundException {
        final List<Result> results = new ArrayList<Result>();
        this.doDecodeMultiple(image, hints, results, 0, 0);
        if (results.isEmpty()) {
            throw NotFoundException.getNotFoundInstance();
        }
        return results.toArray(new Result[results.size()]);
    }
    
    private void doDecodeMultiple(final BinaryBitmap image, final Map<DecodeHintType, ?> hints, final List<Result> results, final int xOffset, final int yOffset) {
        Result result;
        try {
            result = this.delegate.decode(image, hints);
        }
        catch (final ReaderException re) {
            return;
        }
        boolean alreadyFound = false;
        for (final Result existingResult : results) {
            if (existingResult.getText().equals(result.getText())) {
                alreadyFound = true;
                break;
            }
        }
        if (alreadyFound) {
            return;
        }
        results.add(translateResultPoints(result, xOffset, yOffset));
        final ResultPoint[] resultPoints = result.getResultPoints();
        if (resultPoints == null || resultPoints.length == 0) {
            return;
        }
        final int width = image.getWidth();
        final int height = image.getHeight();
        float minX = (float)width;
        float minY = (float)height;
        float maxX = 0.0f;
        float maxY = 0.0f;
        for (final ResultPoint point : resultPoints) {
            final float x = point.getX();
            final float y = point.getY();
            if (x < minX) {
                minX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
        }
        if (minX > 100.0f) {
            this.doDecodeMultiple(image.crop(0, 0, (int)minX, height), hints, results, xOffset, yOffset);
        }
        if (minY > 100.0f) {
            this.doDecodeMultiple(image.crop(0, 0, width, (int)minY), hints, results, xOffset, yOffset);
        }
        if (maxX < width - 100) {
            this.doDecodeMultiple(image.crop((int)maxX, 0, width - (int)maxX, height), hints, results, xOffset + (int)maxX, yOffset);
        }
        if (maxY < height - 100) {
            this.doDecodeMultiple(image.crop(0, (int)maxY, width, height - (int)maxY), hints, results, xOffset, yOffset + (int)maxY);
        }
    }
    
    private static Result translateResultPoints(final Result result, final int xOffset, final int yOffset) {
        final ResultPoint[] oldResultPoints = result.getResultPoints();
        if (oldResultPoints == null) {
            return result;
        }
        final ResultPoint[] newResultPoints = new ResultPoint[oldResultPoints.length];
        for (int i = 0; i < oldResultPoints.length; ++i) {
            final ResultPoint oldPoint = oldResultPoints[i];
            newResultPoints[i] = new ResultPoint(oldPoint.getX() + xOffset, oldPoint.getY() + yOffset);
        }
        return new Result(result.getText(), result.getRawBytes(), newResultPoints, result.getBarcodeFormat());
    }
}
