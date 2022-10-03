package org.apache.tika.detect;

public class TextStatistics
{
    private final int[] counts;
    private int total;
    
    public TextStatistics() {
        this.counts = new int[256];
        this.total = 0;
    }
    
    public void addData(final byte[] buffer, final int offset, final int length) {
        for (int i = 0; i < length; ++i) {
            final int[] counts = this.counts;
            final int n = buffer[offset + i] & 0xFF;
            ++counts[n];
            ++this.total;
        }
    }
    
    public boolean isMostlyAscii() {
        final int control = this.count(0, 32);
        final int ascii = this.count(32, 128);
        final int safe = this.countSafeControl();
        return this.total > 0 && (control - safe) * 100 < this.total * 2 && (ascii + safe) * 100 > this.total * 90;
    }
    
    public boolean looksLikeUTF8() {
        final int control = this.count(0, 32);
        int utf8 = this.count(32, 128);
        final int safe = this.countSafeControl();
        int expectedContinuation = 0;
        final int[] leading = { this.count(192, 224), this.count(224, 240), this.count(240, 248) };
        for (int i = 0; i < leading.length; ++i) {
            utf8 += leading[i];
            expectedContinuation += (i + 1) * leading[i];
        }
        final int continuation = this.count(128, 192);
        return utf8 > 0 && continuation <= expectedContinuation && continuation >= expectedContinuation - 3 && this.count(3968, 256) == 0 && (control - safe) * 100 < utf8 * 2;
    }
    
    public int count() {
        return this.total;
    }
    
    public int count(final int b) {
        return this.counts[b & 0xFF];
    }
    
    public int countControl() {
        return this.count(0, 32) - this.countSafeControl();
    }
    
    public int countSafeAscii() {
        return this.count(32, 128) + this.countSafeControl();
    }
    
    public int countEightBit() {
        return this.count(128, 256);
    }
    
    private int count(final int from, final int to) {
        assert 0 <= from && to <= this.counts.length;
        int count = 0;
        for (int i = from; i < to; ++i) {
            count += this.counts[i];
        }
        return count;
    }
    
    private int countSafeControl() {
        return this.count(9) + this.count(10) + this.count(13) + this.count(12) + this.count(27);
    }
}
