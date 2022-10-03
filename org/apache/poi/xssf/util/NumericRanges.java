package org.apache.poi.xssf.util;

public class NumericRanges
{
    public static final int NO_OVERLAPS = -1;
    public static final int OVERLAPS_1_MINOR = 0;
    public static final int OVERLAPS_2_MINOR = 1;
    public static final int OVERLAPS_1_WRAPS = 2;
    public static final int OVERLAPS_2_WRAPS = 3;
    
    public static long[] getOverlappingRange(final long[] range1, final long[] range2) {
        switch (getOverlappingType(range1, range2)) {
            case 0: {
                return new long[] { range2[0], range1[1] };
            }
            case 1: {
                return new long[] { range1[0], range2[1] };
            }
            case 3: {
                return range1;
            }
            case 2: {
                return range2;
            }
            default: {
                return new long[] { -1L, -1L };
            }
        }
    }
    
    public static int getOverlappingType(final long[] range1, final long[] range2) {
        final long min1 = range1[0];
        final long max1 = range1[1];
        final long min2 = range2[0];
        final long max2 = range2[1];
        if (min1 >= min2) {
            if (max1 <= max2) {
                return 3;
            }
            if (min1 <= max2) {
                return 1;
            }
        }
        else {
            if (max1 >= max2) {
                return 2;
            }
            if (max1 >= min2) {
                return 0;
            }
        }
        return -1;
    }
}
