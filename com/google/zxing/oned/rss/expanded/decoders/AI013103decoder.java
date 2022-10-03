package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.common.BitArray;

final class AI013103decoder extends AI013x0xDecoder
{
    AI013103decoder(final BitArray information) {
        super(information);
    }
    
    @Override
    protected void addWeightCode(final StringBuilder buf, final int weight) {
        buf.append("(3103)");
    }
    
    @Override
    protected int checkWeight(final int weight) {
        return weight;
    }
}
