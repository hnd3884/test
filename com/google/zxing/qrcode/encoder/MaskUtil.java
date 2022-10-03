package com.google.zxing.qrcode.encoder;

final class MaskUtil
{
    private MaskUtil() {
    }
    
    static int applyMaskPenaltyRule1(final ByteMatrix matrix) {
        return applyMaskPenaltyRule1Internal(matrix, true) + applyMaskPenaltyRule1Internal(matrix, false);
    }
    
    static int applyMaskPenaltyRule2(final ByteMatrix matrix) {
        int penalty = 0;
        final byte[][] array = matrix.getArray();
        final int width = matrix.getWidth();
        for (int height = matrix.getHeight(), y = 0; y < height - 1; ++y) {
            for (int x = 0; x < width - 1; ++x) {
                final int value = array[y][x];
                if (value == array[y][x + 1] && value == array[y + 1][x] && value == array[y + 1][x + 1]) {
                    penalty += 3;
                }
            }
        }
        return penalty;
    }
    
    static int applyMaskPenaltyRule3(final ByteMatrix matrix) {
        int penalty = 0;
        final byte[][] array = matrix.getArray();
        final int width = matrix.getWidth();
        for (int height = matrix.getHeight(), y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (x + 6 < width && array[y][x] == 1 && array[y][x + 1] == 0 && array[y][x + 2] == 1 && array[y][x + 3] == 1 && array[y][x + 4] == 1 && array[y][x + 5] == 0 && array[y][x + 6] == 1 && ((x + 10 < width && array[y][x + 7] == 0 && array[y][x + 8] == 0 && array[y][x + 9] == 0 && array[y][x + 10] == 0) || (x - 4 >= 0 && array[y][x - 1] == 0 && array[y][x - 2] == 0 && array[y][x - 3] == 0 && array[y][x - 4] == 0))) {
                    penalty += 40;
                }
                if (y + 6 < height && array[y][x] == 1 && array[y + 1][x] == 0 && array[y + 2][x] == 1 && array[y + 3][x] == 1 && array[y + 4][x] == 1 && array[y + 5][x] == 0 && array[y + 6][x] == 1 && ((y + 10 < height && array[y + 7][x] == 0 && array[y + 8][x] == 0 && array[y + 9][x] == 0 && array[y + 10][x] == 0) || (y - 4 >= 0 && array[y - 1][x] == 0 && array[y - 2][x] == 0 && array[y - 3][x] == 0 && array[y - 4][x] == 0))) {
                    penalty += 40;
                }
            }
        }
        return penalty;
    }
    
    static int applyMaskPenaltyRule4(final ByteMatrix matrix) {
        int numDarkCells = 0;
        final byte[][] array = matrix.getArray();
        final int width = matrix.getWidth();
        for (int height = matrix.getHeight(), y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (array[y][x] == 1) {
                    ++numDarkCells;
                }
            }
        }
        final int numTotalCells = matrix.getHeight() * matrix.getWidth();
        final double darkRatio = numDarkCells / (double)numTotalCells;
        return Math.abs((int)(darkRatio * 100.0 - 50.0)) / 5 * 10;
    }
    
    static boolean getDataMaskBit(final int maskPattern, final int x, final int y) {
        if (!QRCode.isValidMaskPattern(maskPattern)) {
            throw new IllegalArgumentException("Invalid mask pattern");
        }
        int intermediate = 0;
        switch (maskPattern) {
            case 0: {
                intermediate = (y + x & 0x1);
                break;
            }
            case 1: {
                intermediate = (y & 0x1);
                break;
            }
            case 2: {
                intermediate = x % 3;
                break;
            }
            case 3: {
                intermediate = (y + x) % 3;
                break;
            }
            case 4: {
                intermediate = ((y >>> 1) + x / 3 & 0x1);
                break;
            }
            case 5: {
                final int temp = y * x;
                intermediate = (temp & 0x1) + temp % 3;
                break;
            }
            case 6: {
                final int temp = y * x;
                intermediate = ((temp & 0x1) + temp % 3 & 0x1);
                break;
            }
            case 7: {
                final int temp = y * x;
                intermediate = (temp % 3 + (y + x & 0x1) & 0x1);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid mask pattern: " + maskPattern);
            }
        }
        return intermediate == 0;
    }
    
    private static int applyMaskPenaltyRule1Internal(final ByteMatrix matrix, final boolean isHorizontal) {
        int penalty = 0;
        int numSameBitCells = 0;
        int prevBit = -1;
        final int iLimit = isHorizontal ? matrix.getHeight() : matrix.getWidth();
        final int jLimit = isHorizontal ? matrix.getWidth() : matrix.getHeight();
        final byte[][] array = matrix.getArray();
        for (int i = 0; i < iLimit; ++i) {
            for (int j = 0; j < jLimit; ++j) {
                final int bit = isHorizontal ? array[i][j] : array[j][i];
                if (bit == prevBit) {
                    if (++numSameBitCells == 5) {
                        penalty += 3;
                    }
                    else if (numSameBitCells > 5) {
                        ++penalty;
                    }
                }
                else {
                    numSameBitCells = 1;
                    prevBit = bit;
                }
            }
            numSameBitCells = 0;
        }
        return penalty;
    }
}
