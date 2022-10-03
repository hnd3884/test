package org.apache.commons.lang.time;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils
{
    public static final TimeZone UTC_TIME_ZONE;
    public static final int MILLIS_IN_SECOND = 1000;
    public static final int MILLIS_IN_MINUTE = 60000;
    public static final int MILLIS_IN_HOUR = 3600000;
    public static final int MILLIS_IN_DAY = 86400000;
    public static final int SEMI_MONTH = 1001;
    private static final int[][] fields;
    public static final int RANGE_WEEK_SUNDAY = 1;
    public static final int RANGE_WEEK_MONDAY = 2;
    public static final int RANGE_WEEK_RELATIVE = 3;
    public static final int RANGE_WEEK_CENTER = 4;
    public static final int RANGE_MONTH_SUNDAY = 5;
    public static final int RANGE_MONTH_MONDAY = 6;
    
    public static Date round(final Date date, final int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final GregorianCalendar gval = new GregorianCalendar();
        gval.setTime(date);
        modify(gval, field, true);
        return gval.getTime();
    }
    
    public static Calendar round(final Calendar date, final int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar rounded = (Calendar)date.clone();
        modify(rounded, field, true);
        return rounded;
    }
    
    public static Date round(final Object date, final int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if (date instanceof Date) {
            return round((Date)date, field);
        }
        if (date instanceof Calendar) {
            return round((Calendar)date, field).getTime();
        }
        throw new ClassCastException("Could not round " + date);
    }
    
    public static Date truncate(final Date date, final int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final GregorianCalendar gval = new GregorianCalendar();
        gval.setTime(date);
        modify(gval, field, false);
        return gval.getTime();
    }
    
    public static Calendar truncate(final Calendar date, final int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar truncated = (Calendar)date.clone();
        modify(truncated, field, false);
        return truncated;
    }
    
    public static Date truncate(final Object date, final int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if (date instanceof Date) {
            return truncate((Date)date, field);
        }
        if (date instanceof Calendar) {
            return truncate((Calendar)date, field).getTime();
        }
        throw new ClassCastException("Could not truncate " + date);
    }
    
    private static void modify(final Calendar val, final int field, final boolean round) {
        boolean roundUp = false;
        for (int i = 0; i < DateUtils.fields.length; ++i) {
            for (int j = 0; j < DateUtils.fields[i].length; ++j) {
                if (DateUtils.fields[i][j] == field) {
                    if (round && roundUp) {
                        if (field == 1001) {
                            if (val.get(5) == 1) {
                                val.add(5, 15);
                            }
                            else {
                                val.add(5, -15);
                                val.add(2, 1);
                            }
                        }
                        else {
                            val.add(DateUtils.fields[i][0], 1);
                        }
                    }
                    return;
                }
            }
            int offset = 0;
            boolean offsetSet = false;
            switch (field) {
                case 1001: {
                    if (DateUtils.fields[i][0] == 5) {
                        offset = val.get(5) - 1;
                        if (offset >= 15) {
                            offset -= 15;
                        }
                        roundUp = (offset > 7);
                        offsetSet = true;
                        break;
                    }
                    break;
                }
                case 9: {
                    if (DateUtils.fields[i][0] == 10) {
                        offset = val.get(10);
                        if (offset >= 12) {
                            offset -= 12;
                        }
                        roundUp = (offset > 6);
                        offsetSet = true;
                        break;
                    }
                    break;
                }
            }
            if (!offsetSet) {
                final int min = val.getActualMinimum(DateUtils.fields[i][0]);
                final int max = val.getActualMaximum(DateUtils.fields[i][0]);
                offset = val.get(DateUtils.fields[i][0]) - min;
                roundUp = (offset > (max - min) / 2);
            }
            val.add(DateUtils.fields[i][0], -offset);
        }
        throw new IllegalArgumentException("The field " + field + " is not supported");
    }
    
    public static Iterator iterator(final Date focus, final int rangeStyle) {
        if (focus == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final GregorianCalendar gval = new GregorianCalendar();
        gval.setTime(focus);
        return iterator(gval, rangeStyle);
    }
    
    public static Iterator iterator(final Calendar focus, final int rangeStyle) {
        if (focus == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar start = null;
        Calendar end = null;
        int startCutoff = 1;
        int endCutoff = 7;
        switch (rangeStyle) {
            case 5:
            case 6: {
                start = truncate(focus, 2);
                end = (Calendar)start.clone();
                end.add(2, 1);
                end.add(5, -1);
                if (rangeStyle == 6) {
                    startCutoff = 2;
                    endCutoff = 1;
                    break;
                }
                break;
            }
            case 1:
            case 2:
            case 3:
            case 4: {
                start = truncate(focus, 5);
                end = truncate(focus, 5);
                switch (rangeStyle) {
                    case 2: {
                        startCutoff = 2;
                        endCutoff = 1;
                        break;
                    }
                    case 3: {
                        startCutoff = focus.get(7);
                        endCutoff = startCutoff - 1;
                        break;
                    }
                    case 4: {
                        startCutoff = focus.get(7) - 3;
                        endCutoff = focus.get(7) + 3;
                        break;
                    }
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("The range style " + rangeStyle + " is not valid.");
            }
        }
        if (startCutoff < 1) {
            startCutoff += 7;
        }
        if (startCutoff > 7) {
            startCutoff -= 7;
        }
        if (endCutoff < 1) {
            endCutoff += 7;
        }
        if (endCutoff > 7) {
            endCutoff -= 7;
        }
        while (start.get(7) != startCutoff) {
            start.add(5, -1);
        }
        while (end.get(7) != endCutoff) {
            end.add(5, 1);
        }
        return new DateIterator(start, end);
    }
    
    public static Iterator iterator(final Object focus, final int rangeStyle) {
        if (focus == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if (focus instanceof Date) {
            return iterator((Date)focus, rangeStyle);
        }
        if (focus instanceof Calendar) {
            return iterator((Calendar)focus, rangeStyle);
        }
        throw new ClassCastException("Could not iterate based on " + focus);
    }
    
    static {
        UTC_TIME_ZONE = TimeZone.getTimeZone("GMT");
        fields = new int[][] { { 14 }, { 13 }, { 12 }, { 11, 10 }, { 5, 5, 9 }, { 2, 1001 }, { 1 }, { 0 } };
    }
    
    static class DateIterator implements Iterator
    {
        private final Calendar endFinal;
        private final Calendar spot;
        
        DateIterator(final Calendar startFinal, final Calendar endFinal) {
            this.endFinal = endFinal;
            (this.spot = startFinal).add(5, -1);
        }
        
        public boolean hasNext() {
            return this.spot.before(this.endFinal);
        }
        
        public Object next() {
            if (this.spot.equals(this.endFinal)) {
                throw new NoSuchElementException();
            }
            this.spot.add(5, 1);
            return this.spot.clone();
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
