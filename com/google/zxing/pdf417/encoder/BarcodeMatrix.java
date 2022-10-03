package com.google.zxing.pdf417.encoder;

final class BarcodeMatrix
{
    private final BarcodeRow[] matrix;
    private int currentRow;
    private final int height;
    private final int width;
    
    BarcodeMatrix(final int height, final int width) {
        this.matrix = new BarcodeRow[height + 2];
        for (int i = 0, matrixLength = this.matrix.length; i < matrixLength; ++i) {
            this.matrix[i] = new BarcodeRow((width + 4) * 17 + 1);
        }
        this.width = width * 17;
        this.height = height + 2;
        this.currentRow = 0;
    }
    
    void set(final int x, final int y, final byte value) {
        this.matrix[y].set(x, value);
    }
    
    void setMatrix(final int x, final int y, final boolean black) {
        this.set(x, y, (byte)(black ? 1 : 0));
    }
    
    void startRow() {
        ++this.currentRow;
    }
    
    BarcodeRow getCurrentRow() {
        return this.matrix[this.currentRow];
    }
    
    byte[][] getMatrix() {
        return this.getScaledMatrix(1, 1);
    }
    
    byte[][] getScaledMatrix(final int Scale) {
        return this.getScaledMatrix(Scale, Scale);
    }
    
    byte[][] getScaledMatrix(final int xScale, final int yScale) {
        final byte[][] matrixOut = new byte[this.height * yScale][this.width * xScale];
        for (int yMax = this.height * yScale, ii = 0; ii < yMax; ++ii) {
            matrixOut[yMax - ii - 1] = this.matrix[ii / yScale].getScaledRow(xScale);
        }
        return matrixOut;
    }
}
