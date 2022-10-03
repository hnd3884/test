package com.google.zxing.common;

public final class BitSource
{
    private final byte[] bytes;
    private int byteOffset;
    private int bitOffset;
    
    public BitSource(final byte[] bytes) {
        this.bytes = bytes;
    }
    
    public int getByteOffset() {
        return this.byteOffset;
    }
    
    public int readBits(int numBits) {
        if (numBits < 1 || numBits > 32) {
            throw new IllegalArgumentException();
        }
        int result = 0;
        if (this.bitOffset > 0) {
            final int bitsLeft = 8 - this.bitOffset;
            final int toRead = (numBits < bitsLeft) ? numBits : bitsLeft;
            final int bitsToNotRead = bitsLeft - toRead;
            final int mask = 255 >> 8 - toRead << bitsToNotRead;
            result = (this.bytes[this.byteOffset] & mask) >> bitsToNotRead;
            numBits -= toRead;
            this.bitOffset += toRead;
            if (this.bitOffset == 8) {
                this.bitOffset = 0;
                ++this.byteOffset;
            }
        }
        if (numBits > 0) {
            while (numBits >= 8) {
                result = (result << 8 | (this.bytes[this.byteOffset] & 0xFF));
                ++this.byteOffset;
                numBits -= 8;
            }
            if (numBits > 0) {
                final int bitsToNotRead2 = 8 - numBits;
                final int mask2 = 255 >> bitsToNotRead2 << bitsToNotRead2;
                result = (result << numBits | (this.bytes[this.byteOffset] & mask2) >> bitsToNotRead2);
                this.bitOffset += numBits;
            }
        }
        return result;
    }
    
    public int available() {
        return 8 * (this.bytes.length - this.byteOffset) - this.bitOffset;
    }
}
