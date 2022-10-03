package com.google.zxing.qrcode.encoder;

public final class ByteMatrix
{
    private final byte[][] bytes;
    private final int width;
    private final int height;
    
    public ByteMatrix(final int width, final int height) {
        this.bytes = new byte[height][width];
        this.width = width;
        this.height = height;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public byte get(final int x, final int y) {
        return this.bytes[y][x];
    }
    
    public byte[][] getArray() {
        return this.bytes;
    }
    
    public void set(final int x, final int y, final byte value) {
        this.bytes[y][x] = value;
    }
    
    public void set(final int x, final int y, final int value) {
        this.bytes[y][x] = (byte)value;
    }
    
    public void set(final int x, final int y, final boolean value) {
        this.bytes[y][x] = (byte)(value ? 1 : 0);
    }
    
    public void clear(final byte value) {
        for (int y = 0; y < this.height; ++y) {
            for (int x = 0; x < this.width; ++x) {
                this.bytes[y][x] = value;
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder(2 * this.width * this.height + 2);
        for (int y = 0; y < this.height; ++y) {
            for (int x = 0; x < this.width; ++x) {
                switch (this.bytes[y][x]) {
                    case 0: {
                        result.append(" 0");
                        break;
                    }
                    case 1: {
                        result.append(" 1");
                        break;
                    }
                    default: {
                        result.append("  ");
                        break;
                    }
                }
            }
            result.append('\n');
        }
        return result.toString();
    }
}
