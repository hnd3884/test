package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.common.BitArray;

final class AI01320xDecoder extends AI013x0xDecoder
{
    AI01320xDecoder(final BitArray information) {
        super(information);
    }
    
    @Override
    protected void addWeightCode(final StringBuilder buf, final int weight) {
        if (weight < 10000) {
            buf.append("(3202)");
        }
        else {
            buf.append("(3203)");
        }
    }
    
    @Override
    protected int checkWeight(final int weight) {
        if (weight < 10000) {
            return weight;
        }
        return weight - 10000;
    }
}
