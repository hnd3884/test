package com.google.zxing;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.BitArray;

public abstract class Binarizer
{
    private final LuminanceSource source;
    
    protected Binarizer(final LuminanceSource source) {
        this.source = source;
    }
    
    public LuminanceSource getLuminanceSource() {
        return this.source;
    }
    
    public abstract BitArray getBlackRow(final int p0, final BitArray p1) throws NotFoundException;
    
    public abstract BitMatrix getBlackMatrix() throws NotFoundException;
    
    public abstract Binarizer createBinarizer(final LuminanceSource p0);
    
    public int getWidth() {
        return this.source.getWidth();
    }
    
    public int getHeight() {
        return this.source.getHeight();
    }
}
