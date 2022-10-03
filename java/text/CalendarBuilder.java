package java.text;

import java.util.Calendar;

class CalendarBuilder
{
    private static final int UNSET = 0;
    private static final int COMPUTED = 1;
    private static final int MINIMUM_USER_STAMP = 2;
    private static final int MAX_FIELD = 18;
    public static final int WEEK_YEAR = 17;
    public static final int ISO_DAY_OF_WEEK = 1000;
    private final int[] field;
    private int nextStamp;
    private int maxFieldIndex;
    
    CalendarBuilder() {
        this.field = new int[36];
        this.nextStamp = 2;
        this.maxFieldIndex = -1;
    }
    
    CalendarBuilder set(int maxFieldIndex, int calendarDayOfWeek) {
        if (maxFieldIndex == 1000) {
            maxFieldIndex = 7;
            calendarDayOfWeek = toCalendarDayOfWeek(calendarDayOfWeek);
        }
        this.field[maxFieldIndex] = this.nextStamp++;
        this.field[18 + maxFieldIndex] = calendarDayOfWeek;
        if (maxFieldIndex > this.maxFieldIndex && maxFieldIndex < 17) {
            this.maxFieldIndex = maxFieldIndex;
        }
        return this;
    }
    
    CalendarBuilder addYear(final int n) {
        final int[] field = this.field;
        final int n2 = 19;
        field[n2] += n;
        final int[] field2 = this.field;
        final int n3 = 35;
        field2[n3] += n;
        return this;
    }
    
    boolean isSet(int n) {
        if (n == 1000) {
            n = 7;
        }
        return this.field[n] > 0;
    }
    
    CalendarBuilder clear(int n) {
        if (n == 1000) {
            n = 7;
        }
        this.field[n] = 0;
        this.field[18 + n] = 0;
        return this;
    }
    
    Calendar establish(final Calendar calendar) {
        int n = (this.isSet(17) && this.field[17] > this.field[1]) ? 1 : 0;
        if (n != 0 && !calendar.isWeekDateSupported()) {
            if (!this.isSet(1)) {
                this.set(1, this.field[35]);
            }
            n = 0;
        }
        calendar.clear();
        for (int i = 2; i < this.nextStamp; ++i) {
            for (int j = 0; j <= this.maxFieldIndex; ++j) {
                if (this.field[j] == i) {
                    calendar.set(j, this.field[18 + j]);
                    break;
                }
            }
        }
        if (n != 0) {
            int n2 = this.isSet(3) ? this.field[21] : 1;
            int k = this.isSet(7) ? this.field[25] : calendar.getFirstDayOfWeek();
            if (!isValidDayOfWeek(k) && calendar.isLenient()) {
                if (k >= 8) {
                    --k;
                    n2 += k / 7;
                    k = k % 7 + 1;
                }
                else {
                    while (k <= 0) {
                        k += 7;
                        --n2;
                    }
                }
                k = toCalendarDayOfWeek(k);
            }
            calendar.setWeekDate(this.field[35], n2, k);
        }
        return calendar;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CalendarBuilder:[");
        for (int i = 0; i < this.field.length; ++i) {
            if (this.isSet(i)) {
                sb.append(i).append('=').append(this.field[18 + i]).append(',');
            }
        }
        final int length = sb.length() - 1;
        if (sb.charAt(length) == ',') {
            sb.setLength(length);
        }
        sb.append(']');
        return sb.toString();
    }
    
    static int toISODayOfWeek(final int n) {
        return (n == 1) ? 7 : (n - 1);
    }
    
    static int toCalendarDayOfWeek(final int n) {
        if (!isValidDayOfWeek(n)) {
            return n;
        }
        return (n == 7) ? 1 : (n + 1);
    }
    
    static boolean isValidDayOfWeek(final int n) {
        return n > 0 && n <= 7;
    }
}
