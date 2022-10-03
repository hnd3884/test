package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

final class AI013x0x1xDecoder extends AI01weightDecoder
{
    private static final int HEADER_SIZE = 8;
    private static final int WEIGHT_SIZE = 20;
    private static final int DATE_SIZE = 16;
    private final String dateCode;
    private final String firstAIdigits;
    
    AI013x0x1xDecoder(final BitArray information, final String firstAIdigits, final String dateCode) {
        super(information);
        this.dateCode = dateCode;
        this.firstAIdigits = firstAIdigits;
    }
    
    @Override
    public String parseInformation() throws NotFoundException {
        if (this.getInformation().getSize() != 84) {
            throw NotFoundException.getNotFoundInstance();
        }
        final StringBuilder buf = new StringBuilder();
        this.encodeCompressedGtin(buf, 8);
        this.encodeCompressedWeight(buf, 48, 20);
        this.encodeCompressedDate(buf, 68);
        return buf.toString();
    }
    
    private void encodeCompressedDate(final StringBuilder buf, final int currentPos) {
        int numericDate = this.getGeneralDecoder().extractNumericValueFromBitArray(currentPos, 16);
        if (numericDate == 38400) {
            return;
        }
        buf.append('(');
        buf.append(this.dateCode);
        buf.append(')');
        final int day = numericDate % 32;
        numericDate /= 32;
        final int month = numericDate % 12 + 1;
        final int year;
        numericDate = (year = numericDate / 12);
        if (year / 10 == 0) {
            buf.append('0');
        }
        buf.append(year);
        if (month / 10 == 0) {
            buf.append('0');
        }
        buf.append(month);
        if (day / 10 == 0) {
            buf.append('0');
        }
        buf.append(day);
    }
    
    @Override
    protected void addWeightCode(final StringBuilder buf, final int weight) {
        final int lastAI = weight / 100000;
        buf.append('(');
        buf.append(this.firstAIdigits);
        buf.append(lastAI);
        buf.append(')');
    }
    
    @Override
    protected int checkWeight(final int weight) {
        return weight % 100000;
    }
}
