package com.microsoft.sqlserver.jdbc;

import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Date;

class GregorianChange
{
    static final Date PURE_CHANGE_DATE;
    static final Date STANDARD_CHANGE_DATE;
    static final int DAYS_SINCE_BASE_DATE_HINT;
    static final int EXTRA_DAYS_TO_BE_ADDED;
    
    private GregorianChange() {
    }
    
    static {
        PURE_CHANGE_DATE = new Date(Long.MIN_VALUE);
        STANDARD_CHANGE_DATE = new GregorianCalendar(Locale.US).getGregorianChange();
        DAYS_SINCE_BASE_DATE_HINT = DDC.daysSinceBaseDate(1583, 1, 1);
        final GregorianCalendar cal = new GregorianCalendar(Locale.US);
        cal.clear();
        cal.set(1, 1, 577738, 0, 0, 0);
        if (cal.get(5) == 15) {
            EXTRA_DAYS_TO_BE_ADDED = 2;
        }
        else {
            EXTRA_DAYS_TO_BE_ADDED = 0;
        }
    }
}
