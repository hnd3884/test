package com.google.zxing.oned;

import com.google.zxing.ChecksumException;
import java.util.Arrays;
import com.google.zxing.ReaderException;
import java.util.EnumMap;
import com.google.zxing.common.BitArray;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.Result;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.Reader;

public abstract class OneDReader implements Reader
{
    protected static final int INTEGER_MATH_SHIFT = 8;
    protected static final int PATTERN_MATCH_RESULT_SCALE_FACTOR = 256;
    
    @Override
    public Result decode(final BinaryBitmap image) throws NotFoundException, FormatException {
        return this.decode(image, null);
    }
    
    @Override
    public Result decode(final BinaryBitmap image, final Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException {
        try {
            return this.doDecode(image, hints);
        }
        catch (final NotFoundException nfe) {
            final boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
            if (tryHarder && image.isRotateSupported()) {
                final BinaryBitmap rotatedImage = image.rotateCounterClockwise();
                final Result result = this.doDecode(rotatedImage, hints);
                final Map<ResultMetadataType, ?> metadata = result.getResultMetadata();
                int orientation = 270;
                if (metadata != null && metadata.containsKey(ResultMetadataType.ORIENTATION)) {
                    orientation = (orientation + (int)metadata.get(ResultMetadataType.ORIENTATION)) % 360;
                }
                result.putMetadata(ResultMetadataType.ORIENTATION, orientation);
                final ResultPoint[] points = result.getResultPoints();
                if (points != null) {
                    final int height = rotatedImage.getHeight();
                    for (int i = 0; i < points.length; ++i) {
                        points[i] = new ResultPoint(height - points[i].getY() - 1.0f, points[i].getX());
                    }
                }
                return result;
            }
            throw nfe;
        }
    }
    
    @Override
    public void reset() {
    }
    
    private Result doDecode(final BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException {
        final int width = image.getWidth();
        final int height = image.getHeight();
        BitArray row = new BitArray(width);
        final int middle = height >> 1;
        final boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
        final int rowStep = Math.max(1, height >> (tryHarder ? 8 : 5));
        int maxLines;
        if (tryHarder) {
            maxLines = height;
        }
        else {
            maxLines = 15;
        }
        for (int x = 0; x < maxLines; ++x) {
            final int rowStepsAboveOrBelow = x + 1 >> 1;
            final boolean isAbove = (x & 0x1) == 0x0;
            final int rowNumber = middle + rowStep * (isAbove ? rowStepsAboveOrBelow : (-rowStepsAboveOrBelow));
            if (rowNumber < 0) {
                break;
            }
            if (rowNumber >= height) {
                break;
            }
            try {
                row = image.getBlackRow(rowNumber, row);
            }
            catch (final NotFoundException nfe) {
                continue;
            }
            int attempt = 0;
            while (attempt < 2) {
                if (attempt == 1) {
                    row.reverse();
                    if (hints != null && hints.containsKey(DecodeHintType.NEED_RESULT_POINT_CALLBACK)) {
                        final Map<DecodeHintType, Object> newHints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
                        newHints.putAll(hints);
                        newHints.remove(DecodeHintType.NEED_RESULT_POINT_CALLBACK);
                        hints = newHints;
                    }
                }
                try {
                    final Result result = this.decodeRow(rowNumber, row, hints);
                    if (attempt == 1) {
                        result.putMetadata(ResultMetadataType.ORIENTATION, 180);
                        final ResultPoint[] points = result.getResultPoints();
                        if (points != null) {
                            points[0] = new ResultPoint(width - points[0].getX() - 1.0f, points[0].getY());
                            points[1] = new ResultPoint(width - points[1].getX() - 1.0f, points[1].getY());
                        }
                    }
                    return result;
                }
                catch (final ReaderException re) {
                    ++attempt;
                    continue;
                }
                break;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    protected static void recordPattern(final BitArray row, final int start, final int[] counters) throws NotFoundException {
        final int numCounters = counters.length;
        Arrays.fill(counters, 0, numCounters, 0);
        final int end = row.getSize();
        if (start >= end) {
            throw NotFoundException.getNotFoundInstance();
        }
        boolean isWhite = !row.get(start);
        int counterPosition = 0;
        int i;
        for (i = start; i < end; ++i) {
            if (row.get(i) ^ isWhite) {
                final int n = counterPosition;
                ++counters[n];
            }
            else {
                if (++counterPosition == numCounters) {
                    break;
                }
                counters[counterPosition] = 1;
                isWhite = !isWhite;
            }
        }
        if (counterPosition != numCounters && (counterPosition != numCounters - 1 || i != end)) {
            throw NotFoundException.getNotFoundInstance();
        }
    }
    
    protected static void recordPatternInReverse(final BitArray row, int start, final int[] counters) throws NotFoundException {
        int numTransitionsLeft = counters.length;
        for (boolean last = row.get(start); start > 0 && numTransitionsLeft >= 0; --numTransitionsLeft, last = !last) {
            if (row.get(--start) != last) {}
        }
        if (numTransitionsLeft >= 0) {
            throw NotFoundException.getNotFoundInstance();
        }
        recordPattern(row, start + 1, counters);
    }
    
    protected static int patternMatchVariance(final int[] counters, final int[] pattern, int maxIndividualVariance) {
        final int numCounters = counters.length;
        int total = 0;
        int patternLength = 0;
        for (int i = 0; i < numCounters; ++i) {
            total += counters[i];
            patternLength += pattern[i];
        }
        if (total < patternLength) {
            return Integer.MAX_VALUE;
        }
        final int unitBarWidth = (total << 8) / patternLength;
        maxIndividualVariance = maxIndividualVariance * unitBarWidth >> 8;
        int totalVariance = 0;
        for (int x = 0; x < numCounters; ++x) {
            final int counter = counters[x] << 8;
            final int scaledPattern = pattern[x] * unitBarWidth;
            final int variance = (counter > scaledPattern) ? (counter - scaledPattern) : (scaledPattern - counter);
            if (variance > maxIndividualVariance) {
                return Integer.MAX_VALUE;
            }
            totalVariance += variance;
        }
        return totalVariance / total;
    }
    
    public abstract Result decodeRow(final int p0, final BitArray p1, final Map<DecodeHintType, ?> p2) throws NotFoundException, ChecksumException, FormatException;
}
