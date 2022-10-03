package com.ocpsoft.pretty.time.units;

import java.io.Serializable;
import com.ocpsoft.pretty.time.TimeUnit;
import java.util.Comparator;

public class TimeUnitComparator implements Comparator<TimeUnit>, Serializable
{
    private static final long serialVersionUID = 1L;
    
    public int compare(final TimeUnit left, final TimeUnit right) {
        if (left.getMillisPerUnit() < right.getMillisPerUnit()) {
            return 1;
        }
        if (left.getMillisPerUnit() > right.getMillisPerUnit()) {
            return -1;
        }
        return 0;
    }
}
