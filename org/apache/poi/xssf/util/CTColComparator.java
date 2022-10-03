package org.apache.poi.xssf.util;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCol;
import java.util.Comparator;

public class CTColComparator
{
    public static final Comparator<CTCol> BY_MAX;
    public static final Comparator<CTCol> BY_MIN_MAX;
    
    private CTColComparator() {
    }
    
    static {
        BY_MAX = new Comparator<CTCol>() {
            @Override
            public int compare(final CTCol col1, final CTCol col2) {
                final long col1max = col1.getMax();
                final long col2max = col2.getMax();
                return Long.compare(col1max, col2max);
            }
        };
        BY_MIN_MAX = new Comparator<CTCol>() {
            @Override
            public int compare(final CTCol col1, final CTCol col2) {
                final long col11min = col1.getMin();
                final long col2min = col2.getMin();
                return (col11min < col2min) ? -1 : ((col11min > col2min) ? 1 : CTColComparator.BY_MAX.compare(col1, col2));
            }
        };
    }
}
