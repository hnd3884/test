package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

final class AI01AndOtherAIs extends AI01decoder
{
    private static final int HEADER_SIZE = 4;
    
    AI01AndOtherAIs(final BitArray information) {
        super(information);
    }
    
    @Override
    public String parseInformation() throws NotFoundException {
        final StringBuilder buff = new StringBuilder();
        buff.append("(01)");
        final int initialGtinPosition = buff.length();
        final int firstGtinDigit = this.getGeneralDecoder().extractNumericValueFromBitArray(4, 4);
        buff.append(firstGtinDigit);
        this.encodeCompressedGtinWithoutAI(buff, 8, initialGtinPosition);
        return this.getGeneralDecoder().decodeAllCodes(buff, 48);
    }
}
