package com.google.zxing.pdf417.encoder;

final class BarcodeRow
{
    private final byte[] row;
    private int currentLocation;
    
    BarcodeRow(final int width) {
        this.row = new byte[width];
        this.currentLocation = 0;
    }
    
    void set(final int x, final byte value) {
        this.row[x] = value;
    }
    
    void set(final int x, final boolean black) {
        this.row[x] = (byte)(black ? 1 : 0);
    }
    
    void addBar(final boolean black, final int width) {
        for (int ii = 0; ii < width; ++ii) {
            this.set(this.currentLocation++, black);
        }
    }
    
    byte[] getRow() {
        return this.row;
    }
    
    byte[] getScaledRow(final int scale) {
        final byte[] output = new byte[this.row.length * scale];
        for (int i = 0; i < output.length; ++i) {
            output[i] = this.row[i / scale];
        }
        return output;
    }
}
