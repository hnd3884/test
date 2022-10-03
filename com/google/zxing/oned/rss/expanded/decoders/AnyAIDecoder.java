package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

final class AnyAIDecoder extends AbstractExpandedDecoder
{
    private static final int HEADER_SIZE = 5;
    
    AnyAIDecoder(final BitArray information) {
        super(information);
    }
    
    @Override
    public String parseInformation() throws NotFoundException {
        final StringBuilder buf = new StringBuilder();
        return this.getGeneralDecoder().decodeAllCodes(buf, 5);
    }
}
