package com.google.zxing;

public abstract class LuminanceSource
{
    private final int width;
    private final int height;
    
    protected LuminanceSource(final int width, final int height) {
        this.width = width;
        this.height = height;
    }
    
    public abstract byte[] getRow(final int p0, final byte[] p1);
    
    public abstract byte[] getMatrix();
    
    public final int getWidth() {
        return this.width;
    }
    
    public final int getHeight() {
        return this.height;
    }
    
    public boolean isCropSupported() {
        return false;
    }
    
    public LuminanceSource crop(final int left, final int top, final int width, final int height) {
        throw new UnsupportedOperationException("This luminance source does not support cropping.");
    }
    
    public boolean isRotateSupported() {
        return false;
    }
    
    public LuminanceSource rotateCounterClockwise() {
        throw new UnsupportedOperationException("This luminance source does not support rotation.");
    }
    
    @Override
    public String toString() {
        byte[] row = new byte[this.width];
        final StringBuilder result = new StringBuilder(this.height * (this.width + 1));
        for (int y = 0; y < this.height; ++y) {
            row = this.getRow(y, row);
            for (int x = 0; x < this.width; ++x) {
                final int luminance = row[x] & 0xFF;
                char c;
                if (luminance < 64) {
                    c = '#';
                }
                else if (luminance < 128) {
                    c = '+';
                }
                else if (luminance < 192) {
                    c = '.';
                }
                else {
                    c = ' ';
                }
                result.append(c);
            }
            result.append('\n');
        }
        return result.toString();
    }
}
