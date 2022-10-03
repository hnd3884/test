package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.common.BitArray;

abstract class AI01weightDecoder extends AI01decoder
{
    AI01weightDecoder(final BitArray information) {
        super(information);
    }
    
    protected void encodeCompressedWeight(final StringBuilder buf, final int currentPos, final int weightSize) {
        final int originalWeightNumeric = this.getGeneralDecoder().extractNumericValueFromBitArray(currentPos, weightSize);
        this.addWeightCode(buf, originalWeightNumeric);
        final int weightNumeric = this.checkWeight(originalWeightNumeric);
        int currentDivisor = 100000;
        for (int i = 0; i < 5; ++i) {
            if (weightNumeric / currentDivisor == 0) {
                buf.append('0');
            }
            currentDivisor /= 10;
        }
        buf.append(weightNumeric);
    }
    
    protected abstract void addWeightCode(final StringBuilder p0, final int p1);
    
    protected abstract int checkWeight(final int p0);
}
