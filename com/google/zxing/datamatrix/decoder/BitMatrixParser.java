package com.google.zxing.datamatrix.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.common.BitMatrix;

final class BitMatrixParser
{
    private final BitMatrix mappingBitMatrix;
    private final BitMatrix readMappingMatrix;
    private final Version version;
    
    BitMatrixParser(final BitMatrix bitMatrix) throws FormatException {
        final int dimension = bitMatrix.getHeight();
        if (dimension < 8 || dimension > 144 || (dimension & 0x1) != 0x0) {
            throw FormatException.getFormatInstance();
        }
        this.version = readVersion(bitMatrix);
        this.mappingBitMatrix = this.extractDataRegion(bitMatrix);
        this.readMappingMatrix = new BitMatrix(this.mappingBitMatrix.getWidth(), this.mappingBitMatrix.getHeight());
    }
    
    Version getVersion() {
        return this.version;
    }
    
    private static Version readVersion(final BitMatrix bitMatrix) throws FormatException {
        final int numRows = bitMatrix.getHeight();
        final int numColumns = bitMatrix.getWidth();
        return Version.getVersionForDimensions(numRows, numColumns);
    }
    
    byte[] readCodewords() throws FormatException {
        final byte[] result = new byte[this.version.getTotalCodewords()];
        int resultOffset = 0;
        int row = 4;
        int column = 0;
        final int numRows = this.mappingBitMatrix.getHeight();
        final int numColumns = this.mappingBitMatrix.getWidth();
        boolean corner1Read = false;
        boolean corner2Read = false;
        boolean corner3Read = false;
        boolean corner4Read = false;
        do {
            if (row == numRows && column == 0 && !corner1Read) {
                result[resultOffset++] = (byte)this.readCorner1(numRows, numColumns);
                row -= 2;
                column += 2;
                corner1Read = true;
            }
            else if (row == numRows - 2 && column == 0 && (numColumns & 0x3) != 0x0 && !corner2Read) {
                result[resultOffset++] = (byte)this.readCorner2(numRows, numColumns);
                row -= 2;
                column += 2;
                corner2Read = true;
            }
            else if (row == numRows + 4 && column == 2 && (numColumns & 0x7) == 0x0 && !corner3Read) {
                result[resultOffset++] = (byte)this.readCorner3(numRows, numColumns);
                row -= 2;
                column += 2;
                corner3Read = true;
            }
            else if (row == numRows - 2 && column == 0 && (numColumns & 0x7) == 0x4 && !corner4Read) {
                result[resultOffset++] = (byte)this.readCorner4(numRows, numColumns);
                row -= 2;
                column += 2;
                corner4Read = true;
            }
            else {
                do {
                    if (row < numRows && column >= 0 && !this.readMappingMatrix.get(column, row)) {
                        result[resultOffset++] = (byte)this.readUtah(row, column, numRows, numColumns);
                    }
                    row -= 2;
                    column += 2;
                } while (row >= 0 && column < numColumns);
                ++row;
                column += 3;
                do {
                    if (row >= 0 && column < numColumns && !this.readMappingMatrix.get(column, row)) {
                        result[resultOffset++] = (byte)this.readUtah(row, column, numRows, numColumns);
                    }
                    row += 2;
                    column -= 2;
                } while (row < numRows && column >= 0);
                row += 3;
                ++column;
            }
        } while (row < numRows || column < numColumns);
        if (resultOffset != this.version.getTotalCodewords()) {
            throw FormatException.getFormatInstance();
        }
        return result;
    }
    
    boolean readModule(int row, int column, final int numRows, final int numColumns) {
        if (row < 0) {
            row += numRows;
            column += 4 - (numRows + 4 & 0x7);
        }
        if (column < 0) {
            column += numColumns;
            row += 4 - (numColumns + 4 & 0x7);
        }
        this.readMappingMatrix.set(column, row);
        return this.mappingBitMatrix.get(column, row);
    }
    
    int readUtah(final int row, final int column, final int numRows, final int numColumns) {
        int currentByte = 0;
        if (this.readModule(row - 2, column - 2, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(row - 2, column - 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(row - 1, column - 2, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(row - 1, column - 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(row - 1, column, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(row, column - 2, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(row, column - 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(row, column, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        return currentByte;
    }
    
    int readCorner1(final int numRows, final int numColumns) {
        int currentByte = 0;
        if (this.readModule(numRows - 1, 0, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(numRows - 1, 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(numRows - 1, 2, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(0, numColumns - 2, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(0, numColumns - 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(1, numColumns - 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(2, numColumns - 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(3, numColumns - 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        return currentByte;
    }
    
    int readCorner2(final int numRows, final int numColumns) {
        int currentByte = 0;
        if (this.readModule(numRows - 3, 0, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(numRows - 2, 0, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(numRows - 1, 0, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(0, numColumns - 4, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(0, numColumns - 3, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(0, numColumns - 2, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(0, numColumns - 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(1, numColumns - 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        return currentByte;
    }
    
    int readCorner3(final int numRows, final int numColumns) {
        int currentByte = 0;
        if (this.readModule(numRows - 1, 0, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(numRows - 1, numColumns - 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(0, numColumns - 3, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(0, numColumns - 2, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(0, numColumns - 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(1, numColumns - 3, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(1, numColumns - 2, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(1, numColumns - 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        return currentByte;
    }
    
    int readCorner4(final int numRows, final int numColumns) {
        int currentByte = 0;
        if (this.readModule(numRows - 3, 0, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(numRows - 2, 0, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(numRows - 1, 0, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(0, numColumns - 2, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(0, numColumns - 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(1, numColumns - 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(2, numColumns - 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        currentByte <<= 1;
        if (this.readModule(3, numColumns - 1, numRows, numColumns)) {
            currentByte |= 0x1;
        }
        return currentByte;
    }
    
    BitMatrix extractDataRegion(final BitMatrix bitMatrix) {
        final int symbolSizeRows = this.version.getSymbolSizeRows();
        final int symbolSizeColumns = this.version.getSymbolSizeColumns();
        if (bitMatrix.getHeight() != symbolSizeRows) {
            throw new IllegalArgumentException("Dimension of bitMarix must match the version size");
        }
        final int dataRegionSizeRows = this.version.getDataRegionSizeRows();
        final int dataRegionSizeColumns = this.version.getDataRegionSizeColumns();
        final int numDataRegionsRow = symbolSizeRows / dataRegionSizeRows;
        final int numDataRegionsColumn = symbolSizeColumns / dataRegionSizeColumns;
        final int sizeDataRegionRow = numDataRegionsRow * dataRegionSizeRows;
        final int sizeDataRegionColumn = numDataRegionsColumn * dataRegionSizeColumns;
        final BitMatrix bitMatrixWithoutAlignment = new BitMatrix(sizeDataRegionColumn, sizeDataRegionRow);
        for (int dataRegionRow = 0; dataRegionRow < numDataRegionsRow; ++dataRegionRow) {
            final int dataRegionRowOffset = dataRegionRow * dataRegionSizeRows;
            for (int dataRegionColumn = 0; dataRegionColumn < numDataRegionsColumn; ++dataRegionColumn) {
                final int dataRegionColumnOffset = dataRegionColumn * dataRegionSizeColumns;
                for (int i = 0; i < dataRegionSizeRows; ++i) {
                    final int readRowOffset = dataRegionRow * (dataRegionSizeRows + 2) + 1 + i;
                    final int writeRowOffset = dataRegionRowOffset + i;
                    for (int j = 0; j < dataRegionSizeColumns; ++j) {
                        final int readColumnOffset = dataRegionColumn * (dataRegionSizeColumns + 2) + 1 + j;
                        if (bitMatrix.get(readColumnOffset, readRowOffset)) {
                            final int writeColumnOffset = dataRegionColumnOffset + j;
                            bitMatrixWithoutAlignment.set(writeColumnOffset, writeRowOffset);
                        }
                    }
                }
            }
        }
        return bitMatrixWithoutAlignment;
    }
}
