package com.google.zxing.qrcode.decoder;

import com.google.zxing.common.BitMatrix;

abstract class DataMask
{
    private static final DataMask[] DATA_MASKS;
    
    private DataMask() {
    }
    
    final void unmaskBitMatrix(final BitMatrix bits, final int dimension) {
        for (int i = 0; i < dimension; ++i) {
            for (int j = 0; j < dimension; ++j) {
                if (this.isMasked(i, j)) {
                    bits.flip(j, i);
                }
            }
        }
    }
    
    abstract boolean isMasked(final int p0, final int p1);
    
    static DataMask forReference(final int reference) {
        if (reference < 0 || reference > 7) {
            throw new IllegalArgumentException();
        }
        return DataMask.DATA_MASKS[reference];
    }
    
    static {
        DATA_MASKS = new DataMask[] { new DataMask000(), new DataMask001(), new DataMask010(), new DataMask011(), new DataMask100(), new DataMask101(), new DataMask110(), new DataMask111() };
    }
    
    private static class DataMask000 extends DataMask
    {
        private DataMask000() {
            super(null);
        }
        
        @Override
        boolean isMasked(final int i, final int j) {
            return (i + j & 0x1) == 0x0;
        }
    }
    
    private static class DataMask001 extends DataMask
    {
        private DataMask001() {
            super(null);
        }
        
        @Override
        boolean isMasked(final int i, final int j) {
            return (i & 0x1) == 0x0;
        }
    }
    
    private static class DataMask010 extends DataMask
    {
        private DataMask010() {
            super(null);
        }
        
        @Override
        boolean isMasked(final int i, final int j) {
            return j % 3 == 0;
        }
    }
    
    private static class DataMask011 extends DataMask
    {
        private DataMask011() {
            super(null);
        }
        
        @Override
        boolean isMasked(final int i, final int j) {
            return (i + j) % 3 == 0;
        }
    }
    
    private static class DataMask100 extends DataMask
    {
        private DataMask100() {
            super(null);
        }
        
        @Override
        boolean isMasked(final int i, final int j) {
            return ((i >>> 1) + j / 3 & 0x1) == 0x0;
        }
    }
    
    private static class DataMask101 extends DataMask
    {
        private DataMask101() {
            super(null);
        }
        
        @Override
        boolean isMasked(final int i, final int j) {
            final int temp = i * j;
            return (temp & 0x1) + temp % 3 == 0;
        }
    }
    
    private static class DataMask110 extends DataMask
    {
        private DataMask110() {
            super(null);
        }
        
        @Override
        boolean isMasked(final int i, final int j) {
            final int temp = i * j;
            return ((temp & 0x1) + temp % 3 & 0x1) == 0x0;
        }
    }
    
    private static class DataMask111 extends DataMask
    {
        private DataMask111() {
            super(null);
        }
        
        @Override
        boolean isMasked(final int i, final int j) {
            return ((i + j & 0x1) + i * j % 3 & 0x1) == 0x0;
        }
    }
}
