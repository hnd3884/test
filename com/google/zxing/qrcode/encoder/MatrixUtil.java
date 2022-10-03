package com.google.zxing.qrcode.encoder;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.common.BitArray;

final class MatrixUtil
{
    private static final int[][] POSITION_DETECTION_PATTERN;
    private static final int[][] HORIZONTAL_SEPARATION_PATTERN;
    private static final int[][] VERTICAL_SEPARATION_PATTERN;
    private static final int[][] POSITION_ADJUSTMENT_PATTERN;
    private static final int[][] POSITION_ADJUSTMENT_PATTERN_COORDINATE_TABLE;
    private static final int[][] TYPE_INFO_COORDINATES;
    private static final int VERSION_INFO_POLY = 7973;
    private static final int TYPE_INFO_POLY = 1335;
    private static final int TYPE_INFO_MASK_PATTERN = 21522;
    
    private MatrixUtil() {
    }
    
    static void clearMatrix(final ByteMatrix matrix) {
        matrix.clear((byte)(-1));
    }
    
    static void buildMatrix(final BitArray dataBits, final ErrorCorrectionLevel ecLevel, final int version, final int maskPattern, final ByteMatrix matrix) throws WriterException {
        clearMatrix(matrix);
        embedBasicPatterns(version, matrix);
        embedTypeInfo(ecLevel, maskPattern, matrix);
        maybeEmbedVersionInfo(version, matrix);
        embedDataBits(dataBits, maskPattern, matrix);
    }
    
    static void embedBasicPatterns(final int version, final ByteMatrix matrix) throws WriterException {
        embedPositionDetectionPatternsAndSeparators(matrix);
        embedDarkDotAtLeftBottomCorner(matrix);
        maybeEmbedPositionAdjustmentPatterns(version, matrix);
        embedTimingPatterns(matrix);
    }
    
    static void embedTypeInfo(final ErrorCorrectionLevel ecLevel, final int maskPattern, final ByteMatrix matrix) throws WriterException {
        final BitArray typeInfoBits = new BitArray();
        makeTypeInfoBits(ecLevel, maskPattern, typeInfoBits);
        for (int i = 0; i < typeInfoBits.getSize(); ++i) {
            final boolean bit = typeInfoBits.get(typeInfoBits.getSize() - 1 - i);
            final int x1 = MatrixUtil.TYPE_INFO_COORDINATES[i][0];
            final int y1 = MatrixUtil.TYPE_INFO_COORDINATES[i][1];
            matrix.set(x1, y1, bit);
            if (i < 8) {
                final int x2 = matrix.getWidth() - i - 1;
                final int y2 = 8;
                matrix.set(x2, y2, bit);
            }
            else {
                final int x2 = 8;
                final int y2 = matrix.getHeight() - 7 + (i - 8);
                matrix.set(x2, y2, bit);
            }
        }
    }
    
    static void maybeEmbedVersionInfo(final int version, final ByteMatrix matrix) throws WriterException {
        if (version < 7) {
            return;
        }
        final BitArray versionInfoBits = new BitArray();
        makeVersionInfoBits(version, versionInfoBits);
        int bitIndex = 17;
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 3; ++j) {
                final boolean bit = versionInfoBits.get(bitIndex);
                --bitIndex;
                matrix.set(i, matrix.getHeight() - 11 + j, bit);
                matrix.set(matrix.getHeight() - 11 + j, i, bit);
            }
        }
    }
    
    static void embedDataBits(final BitArray dataBits, final int maskPattern, final ByteMatrix matrix) throws WriterException {
        int bitIndex = 0;
        int direction = -1;
        int x = matrix.getWidth() - 1;
        int y = matrix.getHeight() - 1;
        while (x > 0) {
            if (x == 6) {
                --x;
            }
            while (y >= 0 && y < matrix.getHeight()) {
                for (int i = 0; i < 2; ++i) {
                    final int xx = x - i;
                    if (isEmpty(matrix.get(xx, y))) {
                        boolean bit;
                        if (bitIndex < dataBits.getSize()) {
                            bit = dataBits.get(bitIndex);
                            ++bitIndex;
                        }
                        else {
                            bit = false;
                        }
                        if (maskPattern != -1 && MaskUtil.getDataMaskBit(maskPattern, xx, y)) {
                            bit = !bit;
                        }
                        matrix.set(xx, y, bit);
                    }
                }
                y += direction;
            }
            direction = -direction;
            y += direction;
            x -= 2;
        }
        if (bitIndex != dataBits.getSize()) {
            throw new WriterException("Not all bits consumed: " + bitIndex + '/' + dataBits.getSize());
        }
    }
    
    static int findMSBSet(int value) {
        int numDigits;
        for (numDigits = 0; value != 0; value >>>= 1, ++numDigits) {}
        return numDigits;
    }
    
    static int calculateBCHCode(int value, final int poly) {
        int msbSetInPoly;
        for (msbSetInPoly = findMSBSet(poly), value <<= msbSetInPoly - 1; findMSBSet(value) >= msbSetInPoly; value ^= poly << findMSBSet(value) - msbSetInPoly) {}
        return value;
    }
    
    static void makeTypeInfoBits(final ErrorCorrectionLevel ecLevel, final int maskPattern, final BitArray bits) throws WriterException {
        if (!QRCode.isValidMaskPattern(maskPattern)) {
            throw new WriterException("Invalid mask pattern");
        }
        final int typeInfo = ecLevel.getBits() << 3 | maskPattern;
        bits.appendBits(typeInfo, 5);
        final int bchCode = calculateBCHCode(typeInfo, 1335);
        bits.appendBits(bchCode, 10);
        final BitArray maskBits = new BitArray();
        maskBits.appendBits(21522, 15);
        bits.xor(maskBits);
        if (bits.getSize() != 15) {
            throw new WriterException("should not happen but we got: " + bits.getSize());
        }
    }
    
    static void makeVersionInfoBits(final int version, final BitArray bits) throws WriterException {
        bits.appendBits(version, 6);
        final int bchCode = calculateBCHCode(version, 7973);
        bits.appendBits(bchCode, 12);
        if (bits.getSize() != 18) {
            throw new WriterException("should not happen but we got: " + bits.getSize());
        }
    }
    
    private static boolean isEmpty(final int value) {
        return value == -1;
    }
    
    private static boolean isValidValue(final int value) {
        return value == -1 || value == 0 || value == 1;
    }
    
    private static void embedTimingPatterns(final ByteMatrix matrix) throws WriterException {
        for (int i = 8; i < matrix.getWidth() - 8; ++i) {
            final int bit = (i + 1) % 2;
            if (!isValidValue(matrix.get(i, 6))) {
                throw new WriterException();
            }
            if (isEmpty(matrix.get(i, 6))) {
                matrix.set(i, 6, bit);
            }
            if (!isValidValue(matrix.get(6, i))) {
                throw new WriterException();
            }
            if (isEmpty(matrix.get(6, i))) {
                matrix.set(6, i, bit);
            }
        }
    }
    
    private static void embedDarkDotAtLeftBottomCorner(final ByteMatrix matrix) throws WriterException {
        if (matrix.get(8, matrix.getHeight() - 8) == 0) {
            throw new WriterException();
        }
        matrix.set(8, matrix.getHeight() - 8, 1);
    }
    
    private static void embedHorizontalSeparationPattern(final int xStart, final int yStart, final ByteMatrix matrix) throws WriterException {
        if (MatrixUtil.HORIZONTAL_SEPARATION_PATTERN[0].length != 8 || MatrixUtil.HORIZONTAL_SEPARATION_PATTERN.length != 1) {
            throw new WriterException("Bad horizontal separation pattern");
        }
        for (int x = 0; x < 8; ++x) {
            if (!isEmpty(matrix.get(xStart + x, yStart))) {
                throw new WriterException();
            }
            matrix.set(xStart + x, yStart, MatrixUtil.HORIZONTAL_SEPARATION_PATTERN[0][x]);
        }
    }
    
    private static void embedVerticalSeparationPattern(final int xStart, final int yStart, final ByteMatrix matrix) throws WriterException {
        if (MatrixUtil.VERTICAL_SEPARATION_PATTERN[0].length != 1 || MatrixUtil.VERTICAL_SEPARATION_PATTERN.length != 7) {
            throw new WriterException("Bad vertical separation pattern");
        }
        for (int y = 0; y < 7; ++y) {
            if (!isEmpty(matrix.get(xStart, yStart + y))) {
                throw new WriterException();
            }
            matrix.set(xStart, yStart + y, MatrixUtil.VERTICAL_SEPARATION_PATTERN[y][0]);
        }
    }
    
    private static void embedPositionAdjustmentPattern(final int xStart, final int yStart, final ByteMatrix matrix) throws WriterException {
        if (MatrixUtil.POSITION_ADJUSTMENT_PATTERN[0].length != 5 || MatrixUtil.POSITION_ADJUSTMENT_PATTERN.length != 5) {
            throw new WriterException("Bad position adjustment");
        }
        for (int y = 0; y < 5; ++y) {
            for (int x = 0; x < 5; ++x) {
                if (!isEmpty(matrix.get(xStart + x, yStart + y))) {
                    throw new WriterException();
                }
                matrix.set(xStart + x, yStart + y, MatrixUtil.POSITION_ADJUSTMENT_PATTERN[y][x]);
            }
        }
    }
    
    private static void embedPositionDetectionPattern(final int xStart, final int yStart, final ByteMatrix matrix) throws WriterException {
        if (MatrixUtil.POSITION_DETECTION_PATTERN[0].length != 7 || MatrixUtil.POSITION_DETECTION_PATTERN.length != 7) {
            throw new WriterException("Bad position detection pattern");
        }
        for (int y = 0; y < 7; ++y) {
            for (int x = 0; x < 7; ++x) {
                if (!isEmpty(matrix.get(xStart + x, yStart + y))) {
                    throw new WriterException();
                }
                matrix.set(xStart + x, yStart + y, MatrixUtil.POSITION_DETECTION_PATTERN[y][x]);
            }
        }
    }
    
    private static void embedPositionDetectionPatternsAndSeparators(final ByteMatrix matrix) throws WriterException {
        final int pdpWidth = MatrixUtil.POSITION_DETECTION_PATTERN[0].length;
        embedPositionDetectionPattern(0, 0, matrix);
        embedPositionDetectionPattern(matrix.getWidth() - pdpWidth, 0, matrix);
        embedPositionDetectionPattern(0, matrix.getWidth() - pdpWidth, matrix);
        final int hspWidth = MatrixUtil.HORIZONTAL_SEPARATION_PATTERN[0].length;
        embedHorizontalSeparationPattern(0, hspWidth - 1, matrix);
        embedHorizontalSeparationPattern(matrix.getWidth() - hspWidth, hspWidth - 1, matrix);
        embedHorizontalSeparationPattern(0, matrix.getWidth() - hspWidth, matrix);
        final int vspSize = MatrixUtil.VERTICAL_SEPARATION_PATTERN.length;
        embedVerticalSeparationPattern(vspSize, 0, matrix);
        embedVerticalSeparationPattern(matrix.getHeight() - vspSize - 1, 0, matrix);
        embedVerticalSeparationPattern(vspSize, matrix.getHeight() - vspSize, matrix);
    }
    
    private static void maybeEmbedPositionAdjustmentPatterns(final int version, final ByteMatrix matrix) throws WriterException {
        if (version < 2) {
            return;
        }
        final int index = version - 1;
        final int[] coordinates = MatrixUtil.POSITION_ADJUSTMENT_PATTERN_COORDINATE_TABLE[index];
        for (int numCoordinates = MatrixUtil.POSITION_ADJUSTMENT_PATTERN_COORDINATE_TABLE[index].length, i = 0; i < numCoordinates; ++i) {
            for (int j = 0; j < numCoordinates; ++j) {
                final int y = coordinates[i];
                final int x = coordinates[j];
                if (x != -1) {
                    if (y != -1) {
                        if (isEmpty(matrix.get(x, y))) {
                            embedPositionAdjustmentPattern(x - 2, y - 2, matrix);
                        }
                    }
                }
            }
        }
    }
    
    static {
        POSITION_DETECTION_PATTERN = new int[][] { { 1, 1, 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 0, 0, 1 }, { 1, 0, 1, 1, 1, 0, 1 }, { 1, 0, 1, 1, 1, 0, 1 }, { 1, 0, 1, 1, 1, 0, 1 }, { 1, 0, 0, 0, 0, 0, 1 }, { 1, 1, 1, 1, 1, 1, 1 } };
        HORIZONTAL_SEPARATION_PATTERN = new int[][] { { 0, 0, 0, 0, 0, 0, 0, 0 } };
        VERTICAL_SEPARATION_PATTERN = new int[][] { { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 } };
        POSITION_ADJUSTMENT_PATTERN = new int[][] { { 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 1 }, { 1, 0, 1, 0, 1 }, { 1, 0, 0, 0, 1 }, { 1, 1, 1, 1, 1 } };
        POSITION_ADJUSTMENT_PATTERN_COORDINATE_TABLE = new int[][] { { -1, -1, -1, -1, -1, -1, -1 }, { 6, 18, -1, -1, -1, -1, -1 }, { 6, 22, -1, -1, -1, -1, -1 }, { 6, 26, -1, -1, -1, -1, -1 }, { 6, 30, -1, -1, -1, -1, -1 }, { 6, 34, -1, -1, -1, -1, -1 }, { 6, 22, 38, -1, -1, -1, -1 }, { 6, 24, 42, -1, -1, -1, -1 }, { 6, 26, 46, -1, -1, -1, -1 }, { 6, 28, 50, -1, -1, -1, -1 }, { 6, 30, 54, -1, -1, -1, -1 }, { 6, 32, 58, -1, -1, -1, -1 }, { 6, 34, 62, -1, -1, -1, -1 }, { 6, 26, 46, 66, -1, -1, -1 }, { 6, 26, 48, 70, -1, -1, -1 }, { 6, 26, 50, 74, -1, -1, -1 }, { 6, 30, 54, 78, -1, -1, -1 }, { 6, 30, 56, 82, -1, -1, -1 }, { 6, 30, 58, 86, -1, -1, -1 }, { 6, 34, 62, 90, -1, -1, -1 }, { 6, 28, 50, 72, 94, -1, -1 }, { 6, 26, 50, 74, 98, -1, -1 }, { 6, 30, 54, 78, 102, -1, -1 }, { 6, 28, 54, 80, 106, -1, -1 }, { 6, 32, 58, 84, 110, -1, -1 }, { 6, 30, 58, 86, 114, -1, -1 }, { 6, 34, 62, 90, 118, -1, -1 }, { 6, 26, 50, 74, 98, 122, -1 }, { 6, 30, 54, 78, 102, 126, -1 }, { 6, 26, 52, 78, 104, 130, -1 }, { 6, 30, 56, 82, 108, 134, -1 }, { 6, 34, 60, 86, 112, 138, -1 }, { 6, 30, 58, 86, 114, 142, -1 }, { 6, 34, 62, 90, 118, 146, -1 }, { 6, 30, 54, 78, 102, 126, 150 }, { 6, 24, 50, 76, 102, 128, 154 }, { 6, 28, 54, 80, 106, 132, 158 }, { 6, 32, 58, 84, 110, 136, 162 }, { 6, 26, 54, 82, 110, 138, 166 }, { 6, 30, 58, 86, 114, 142, 170 } };
        TYPE_INFO_COORDINATES = new int[][] { { 8, 0 }, { 8, 1 }, { 8, 2 }, { 8, 3 }, { 8, 4 }, { 8, 5 }, { 8, 7 }, { 8, 8 }, { 7, 8 }, { 5, 8 }, { 4, 8 }, { 3, 8 }, { 2, 8 }, { 1, 8 }, { 0, 8 } };
    }
}
