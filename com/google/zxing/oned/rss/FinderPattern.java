package com.google.zxing.oned.rss;

import com.google.zxing.ResultPoint;

public final class FinderPattern
{
    private final int value;
    private final int[] startEnd;
    private final ResultPoint[] resultPoints;
    
    public FinderPattern(final int value, final int[] startEnd, final int start, final int end, final int rowNumber) {
        this.value = value;
        this.startEnd = startEnd;
        this.resultPoints = new ResultPoint[] { new ResultPoint((float)start, (float)rowNumber), new ResultPoint((float)end, (float)rowNumber) };
    }
    
    public int getValue() {
        return this.value;
    }
    
    public int[] getStartEnd() {
        return this.startEnd;
    }
    
    public ResultPoint[] getResultPoints() {
        return this.resultPoints;
    }
}
