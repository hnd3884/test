package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.common.BitArray;

abstract class AI01decoder extends AbstractExpandedDecoder
{
    protected static final int GTIN_SIZE = 40;
    
    AI01decoder(final BitArray information) {
        super(information);
    }
    
    protected void encodeCompressedGtin(final StringBuilder buf, final int currentPos) {
        buf.append("(01)");
        final int initialPosition = buf.length();
        buf.append('9');
        this.encodeCompressedGtinWithoutAI(buf, currentPos, initialPosition);
    }
    
    protected void encodeCompressedGtinWithoutAI(final StringBuilder buf, final int currentPos, final int initialBufferPosition) {
        for (int i = 0; i < 4; ++i) {
            final int currentBlock = this.getGeneralDecoder().extractNumericValueFromBitArray(currentPos + 10 * i, 10);
            if (currentBlock / 100 == 0) {
                buf.append('0');
            }
            if (currentBlock / 10 == 0) {
                buf.append('0');
            }
            buf.append(currentBlock);
        }
        appendCheckDigit(buf, initialBufferPosition);
    }
    
    private static void appendCheckDigit(final StringBuilder buf, final int currentPos) {
        int checkDigit = 0;
        for (int i = 0; i < 13; ++i) {
            final int digit = buf.charAt(i + currentPos) - '0';
            checkDigit += (((i & 0x1) == 0x0) ? (3 * digit) : digit);
        }
        checkDigit = 10 - checkDigit % 10;
        if (checkDigit == 10) {
            checkDigit = 0;
        }
        buf.append(checkDigit);
    }
}
