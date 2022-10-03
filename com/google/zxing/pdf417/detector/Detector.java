package com.google.zxing.pdf417.detector;

import java.util.Arrays;
import com.google.zxing.common.GridSampler;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.ResultPoint;
import com.google.zxing.NotFoundException;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.BinaryBitmap;

public final class Detector
{
    private static final int MAX_AVG_VARIANCE = 107;
    private static final int MAX_INDIVIDUAL_VARIANCE = 204;
    private static final int SKEW_THRESHOLD = 2;
    private static final int[] START_PATTERN;
    private static final int[] START_PATTERN_REVERSE;
    private static final int[] STOP_PATTERN;
    private static final int[] STOP_PATTERN_REVERSE;
    private final BinaryBitmap image;
    
    public Detector(final BinaryBitmap image) {
        this.image = image;
    }
    
    public DetectorResult detect() throws NotFoundException {
        return this.detect(null);
    }
    
    public DetectorResult detect(final Map<DecodeHintType, ?> hints) throws NotFoundException {
        final BitMatrix matrix = this.image.getBlackMatrix();
        ResultPoint[] vertices = findVertices(matrix);
        if (vertices == null) {
            vertices = findVertices180(matrix);
            if (vertices != null) {
                correctCodeWordVertices(vertices, true);
            }
        }
        else {
            correctCodeWordVertices(vertices, false);
        }
        if (vertices == null) {
            throw NotFoundException.getNotFoundInstance();
        }
        final float moduleWidth = computeModuleWidth(vertices);
        if (moduleWidth < 1.0f) {
            throw NotFoundException.getNotFoundInstance();
        }
        final int dimension = computeDimension(vertices[4], vertices[6], vertices[5], vertices[7], moduleWidth);
        if (dimension < 1) {
            throw NotFoundException.getNotFoundInstance();
        }
        final BitMatrix bits = sampleGrid(matrix, vertices[4], vertices[5], vertices[6], vertices[7], dimension);
        return new DetectorResult(bits, new ResultPoint[] { vertices[5], vertices[4], vertices[6], vertices[7] });
    }
    
    private static ResultPoint[] findVertices(final BitMatrix matrix) {
        final int height = matrix.getHeight();
        final int width = matrix.getWidth();
        final ResultPoint[] result = new ResultPoint[8];
        boolean found = false;
        int[] counters = new int[Detector.START_PATTERN.length];
        for (int i = 0; i < height; ++i) {
            final int[] loc = findGuardPattern(matrix, 0, i, width, false, Detector.START_PATTERN, counters);
            if (loc != null) {
                result[0] = new ResultPoint((float)loc[0], (float)i);
                result[4] = new ResultPoint((float)loc[1], (float)i);
                found = true;
                break;
            }
        }
        if (found) {
            found = false;
            for (int i = height - 1; i > 0; --i) {
                final int[] loc = findGuardPattern(matrix, 0, i, width, false, Detector.START_PATTERN, counters);
                if (loc != null) {
                    result[1] = new ResultPoint((float)loc[0], (float)i);
                    result[5] = new ResultPoint((float)loc[1], (float)i);
                    found = true;
                    break;
                }
            }
        }
        counters = new int[Detector.STOP_PATTERN.length];
        if (found) {
            found = false;
            for (int i = 0; i < height; ++i) {
                final int[] loc = findGuardPattern(matrix, 0, i, width, false, Detector.STOP_PATTERN, counters);
                if (loc != null) {
                    result[2] = new ResultPoint((float)loc[1], (float)i);
                    result[6] = new ResultPoint((float)loc[0], (float)i);
                    found = true;
                    break;
                }
            }
        }
        if (found) {
            found = false;
            for (int i = height - 1; i > 0; --i) {
                final int[] loc = findGuardPattern(matrix, 0, i, width, false, Detector.STOP_PATTERN, counters);
                if (loc != null) {
                    result[3] = new ResultPoint((float)loc[1], (float)i);
                    result[7] = new ResultPoint((float)loc[0], (float)i);
                    found = true;
                    break;
                }
            }
        }
        return (ResultPoint[])(found ? result : null);
    }
    
    private static ResultPoint[] findVertices180(final BitMatrix matrix) {
        final int height = matrix.getHeight();
        final int width = matrix.getWidth();
        final int halfWidth = width >> 1;
        final ResultPoint[] result = new ResultPoint[8];
        boolean found = false;
        int[] counters = new int[Detector.START_PATTERN_REVERSE.length];
        for (int i = height - 1; i > 0; --i) {
            final int[] loc = findGuardPattern(matrix, halfWidth, i, halfWidth, true, Detector.START_PATTERN_REVERSE, counters);
            if (loc != null) {
                result[0] = new ResultPoint((float)loc[1], (float)i);
                result[4] = new ResultPoint((float)loc[0], (float)i);
                found = true;
                break;
            }
        }
        if (found) {
            found = false;
            for (int i = 0; i < height; ++i) {
                final int[] loc = findGuardPattern(matrix, halfWidth, i, halfWidth, true, Detector.START_PATTERN_REVERSE, counters);
                if (loc != null) {
                    result[1] = new ResultPoint((float)loc[1], (float)i);
                    result[5] = new ResultPoint((float)loc[0], (float)i);
                    found = true;
                    break;
                }
            }
        }
        counters = new int[Detector.STOP_PATTERN_REVERSE.length];
        if (found) {
            found = false;
            for (int i = height - 1; i > 0; --i) {
                final int[] loc = findGuardPattern(matrix, 0, i, halfWidth, false, Detector.STOP_PATTERN_REVERSE, counters);
                if (loc != null) {
                    result[2] = new ResultPoint((float)loc[0], (float)i);
                    result[6] = new ResultPoint((float)loc[1], (float)i);
                    found = true;
                    break;
                }
            }
        }
        if (found) {
            found = false;
            for (int i = 0; i < height; ++i) {
                final int[] loc = findGuardPattern(matrix, 0, i, halfWidth, false, Detector.STOP_PATTERN_REVERSE, counters);
                if (loc != null) {
                    result[3] = new ResultPoint((float)loc[0], (float)i);
                    result[7] = new ResultPoint((float)loc[1], (float)i);
                    found = true;
                    break;
                }
            }
        }
        return (ResultPoint[])(found ? result : null);
    }
    
    private static void correctCodeWordVertices(final ResultPoint[] vertices, final boolean upsideDown) {
        float skew = vertices[4].getY() - vertices[6].getY();
        if (upsideDown) {
            skew = -skew;
        }
        if (skew > 2.0f) {
            final float length = vertices[4].getX() - vertices[0].getX();
            final float deltax = vertices[6].getX() - vertices[0].getX();
            final float deltay = vertices[6].getY() - vertices[0].getY();
            final float correction = length * deltay / deltax;
            vertices[4] = new ResultPoint(vertices[4].getX(), vertices[4].getY() + correction);
        }
        else if (-skew > 2.0f) {
            final float length = vertices[2].getX() - vertices[6].getX();
            final float deltax = vertices[2].getX() - vertices[4].getX();
            final float deltay = vertices[2].getY() - vertices[4].getY();
            final float correction = length * deltay / deltax;
            vertices[6] = new ResultPoint(vertices[6].getX(), vertices[6].getY() - correction);
        }
        skew = vertices[7].getY() - vertices[5].getY();
        if (upsideDown) {
            skew = -skew;
        }
        if (skew > 2.0f) {
            final float length = vertices[5].getX() - vertices[1].getX();
            final float deltax = vertices[7].getX() - vertices[1].getX();
            final float deltay = vertices[7].getY() - vertices[1].getY();
            final float correction = length * deltay / deltax;
            vertices[5] = new ResultPoint(vertices[5].getX(), vertices[5].getY() + correction);
        }
        else if (-skew > 2.0f) {
            final float length = vertices[3].getX() - vertices[7].getX();
            final float deltax = vertices[3].getX() - vertices[5].getX();
            final float deltay = vertices[3].getY() - vertices[5].getY();
            final float correction = length * deltay / deltax;
            vertices[7] = new ResultPoint(vertices[7].getX(), vertices[7].getY() - correction);
        }
    }
    
    private static float computeModuleWidth(final ResultPoint[] vertices) {
        final float pixels1 = ResultPoint.distance(vertices[0], vertices[4]);
        final float pixels2 = ResultPoint.distance(vertices[1], vertices[5]);
        final float moduleWidth1 = (pixels1 + pixels2) / 34.0f;
        final float pixels3 = ResultPoint.distance(vertices[6], vertices[2]);
        final float pixels4 = ResultPoint.distance(vertices[7], vertices[3]);
        final float moduleWidth2 = (pixels3 + pixels4) / 36.0f;
        return (moduleWidth1 + moduleWidth2) / 2.0f;
    }
    
    private static int computeDimension(final ResultPoint topLeft, final ResultPoint topRight, final ResultPoint bottomLeft, final ResultPoint bottomRight, final float moduleWidth) {
        final int topRowDimension = round(ResultPoint.distance(topLeft, topRight) / moduleWidth);
        final int bottomRowDimension = round(ResultPoint.distance(bottomLeft, bottomRight) / moduleWidth);
        return ((topRowDimension + bottomRowDimension >> 1) + 8) / 17 * 17;
    }
    
    private static BitMatrix sampleGrid(final BitMatrix matrix, final ResultPoint topLeft, final ResultPoint bottomLeft, final ResultPoint topRight, final ResultPoint bottomRight, final int dimension) throws NotFoundException {
        final GridSampler sampler = GridSampler.getInstance();
        return sampler.sampleGrid(matrix, dimension, dimension, 0.0f, 0.0f, (float)dimension, 0.0f, (float)dimension, (float)dimension, 0.0f, (float)dimension, topLeft.getX(), topLeft.getY(), topRight.getX(), topRight.getY(), bottomRight.getX(), bottomRight.getY(), bottomLeft.getX(), bottomLeft.getY());
    }
    
    private static int round(final float d) {
        return (int)(d + 0.5f);
    }
    
    private static int[] findGuardPattern(final BitMatrix matrix, final int column, final int row, final int width, final boolean whiteFirst, final int[] pattern, final int[] counters) {
        Arrays.fill(counters, 0, counters.length, 0);
        final int patternLength = pattern.length;
        boolean isWhite = whiteFirst;
        int counterPosition = 0;
        int patternStart = column;
        for (int x = column; x < column + width; ++x) {
            final boolean pixel = matrix.get(x, row);
            if (pixel ^ isWhite) {
                final int n = counterPosition;
                ++counters[n];
            }
            else {
                if (counterPosition == patternLength - 1) {
                    if (patternMatchVariance(counters, pattern, 204) < 107) {
                        return new int[] { patternStart, x };
                    }
                    patternStart += counters[0] + counters[1];
                    System.arraycopy(counters, 2, counters, 0, patternLength - 2);
                    counters[patternLength - 1] = (counters[patternLength - 2] = 0);
                    --counterPosition;
                }
                else {
                    ++counterPosition;
                }
                counters[counterPosition] = 1;
                isWhite = !isWhite;
            }
        }
        return null;
    }
    
    private static int patternMatchVariance(final int[] counters, final int[] pattern, int maxIndividualVariance) {
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
    
    static {
        START_PATTERN = new int[] { 8, 1, 1, 1, 1, 1, 1, 3 };
        START_PATTERN_REVERSE = new int[] { 3, 1, 1, 1, 1, 1, 1, 8 };
        STOP_PATTERN = new int[] { 7, 1, 1, 3, 1, 1, 1, 2, 1 };
        STOP_PATTERN_REVERSE = new int[] { 1, 2, 1, 1, 1, 3, 1, 1, 7 };
    }
}
