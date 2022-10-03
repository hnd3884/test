package org.apache.poi.ss.formula.atp;

import java.util.Calendar;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.util.Internal;

@Internal
final class YearFracCalculator
{
    private static final int MS_PER_HOUR = 3600000;
    private static final int MS_PER_DAY = 86400000;
    private static final int DAYS_PER_NORMAL_YEAR = 365;
    private static final int DAYS_PER_LEAP_YEAR = 366;
    private static final int LONG_MONTH_LEN = 31;
    private static final int SHORT_MONTH_LEN = 30;
    private static final int SHORT_FEB_LEN = 28;
    private static final int LONG_FEB_LEN = 29;
    
    private YearFracCalculator() {
    }
    
    public static double calculate(final double pStartDateVal, final double pEndDateVal, final int basis) throws EvaluationException {
        if (basis < 0 || basis >= 5) {
            throw new EvaluationException(ErrorEval.NUM_ERROR);
        }
        int startDateVal = (int)Math.floor(pStartDateVal);
        int endDateVal = (int)Math.floor(pEndDateVal);
        if (startDateVal == endDateVal) {
            return 0.0;
        }
        if (startDateVal > endDateVal) {
            final int temp = startDateVal;
            startDateVal = endDateVal;
            endDateVal = temp;
        }
        switch (basis) {
            case 0: {
                return basis0(startDateVal, endDateVal);
            }
            case 1: {
                return basis1(startDateVal, endDateVal);
            }
            case 2: {
                return basis2(startDateVal, endDateVal);
            }
            case 3: {
                return basis3(startDateVal, endDateVal);
            }
            case 4: {
                return basis4(startDateVal, endDateVal);
            }
            default: {
                throw new IllegalStateException("cannot happen");
            }
        }
    }
    
    private static double basis0(final int startDateVal, final int endDateVal) {
        final SimpleDate startDate = createDate(startDateVal);
        final SimpleDate endDate = createDate(endDateVal);
        int date1day = startDate.day;
        int date2day = endDate.day;
        if (date1day == 31 && date2day == 31) {
            date1day = 30;
            date2day = 30;
        }
        else if (date1day == 31) {
            date1day = 30;
        }
        else if (date1day == 30 && date2day == 31) {
            date2day = 30;
        }
        else if (startDate.month == 2 && isLastDayOfMonth(startDate)) {
            date1day = 30;
            if (endDate.month == 2 && isLastDayOfMonth(endDate)) {
                date2day = 30;
            }
        }
        return calculateAdjusted(startDate, endDate, date1day, date2day);
    }
    
    private static double basis1(final int startDateVal, final int endDateVal) {
        assert startDateVal <= endDateVal;
        final SimpleDate startDate = createDate(startDateVal);
        final SimpleDate endDate = createDate(endDateVal);
        double yearLength;
        if (isGreaterThanOneYear(startDate, endDate)) {
            yearLength = averageYearLength(startDate.year, endDate.year);
            assert yearLength > 0.0;
        }
        else if (shouldCountFeb29(startDate, endDate)) {
            yearLength = 366.0;
        }
        else {
            yearLength = 365.0;
        }
        return dateDiff(startDate.tsMilliseconds, endDate.tsMilliseconds) / yearLength;
    }
    
    private static double basis2(final int startDateVal, final int endDateVal) {
        return (endDateVal - startDateVal) / 360.0;
    }
    
    private static double basis3(final double startDateVal, final double endDateVal) {
        return (endDateVal - startDateVal) / 365.0;
    }
    
    private static double basis4(final int startDateVal, final int endDateVal) {
        final SimpleDate startDate = createDate(startDateVal);
        final SimpleDate endDate = createDate(endDateVal);
        int date1day = startDate.day;
        int date2day = endDate.day;
        if (date1day == 31) {
            date1day = 30;
        }
        if (date2day == 31) {
            date2day = 30;
        }
        return calculateAdjusted(startDate, endDate, date1day, date2day);
    }
    
    private static double calculateAdjusted(final SimpleDate startDate, final SimpleDate endDate, final int date1day, final int date2day) {
        final double dayCount = (endDate.year - startDate.year) * 360.0 + (endDate.month - startDate.month) * 30.0 + (date2day - date1day) * 1.0;
        return dayCount / 360.0;
    }
    
    private static boolean isLastDayOfMonth(final SimpleDate date) {
        return date.day >= 28 && date.day == getLastDayOfMonth(date);
    }
    
    private static int getLastDayOfMonth(final SimpleDate date) {
        switch (date.month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12: {
                return 31;
            }
            case 4:
            case 6:
            case 9:
            case 11: {
                return 30;
            }
            default: {
                if (isLeapYear(date.year)) {
                    return 29;
                }
                return 28;
            }
        }
    }
    
    private static boolean shouldCountFeb29(final SimpleDate start, final SimpleDate end) {
        if (isLeapYear(start.year)) {
            if (start.year == end.year) {
                return true;
            }
            switch (start.month) {
                case 1:
                case 2: {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
        else {
            if (!isLeapYear(end.year)) {
                return false;
            }
            switch (end.month) {
                case 1: {
                    return false;
                }
                case 2: {
                    return end.day == 29;
                }
                default: {
                    return true;
                }
            }
        }
    }
    
    private static int dateDiff(final long startDateMS, final long endDateMS) {
        final long msDiff = endDateMS - startDateMS;
        final int remainderHours = (int)(msDiff % 86400000L / 3600000L);
        switch (remainderHours) {
            case 0: {
                return (int)(0.5 + msDiff / 8.64E7);
            }
            default: {
                throw new RuntimeException("Unexpected date diff between " + startDateMS + " and " + endDateMS);
            }
        }
    }
    
    private static double averageYearLength(final int startYear, final int endYear) {
        assert startYear <= endYear;
        int dayCount = 0;
        for (int i = startYear; i <= endYear; ++i) {
            dayCount += (isLeapYear(i) ? 366 : 365);
        }
        final double numberOfYears = endYear - startYear + 1;
        return dayCount / numberOfYears;
    }
    
    private static boolean isLeapYear(final int i) {
        return i % 4 == 0 && (i % 400 == 0 || i % 100 != 0);
    }
    
    private static boolean isGreaterThanOneYear(final SimpleDate start, final SimpleDate end) {
        assert start.year <= end.year;
        return start.year != end.year && (start.year + 1 != end.year || (start.month <= end.month && (start.month < end.month || start.day < end.day)));
    }
    
    private static SimpleDate createDate(final int dayCount) {
        final Calendar cal = LocaleUtil.getLocaleCalendar(LocaleUtil.TIMEZONE_UTC);
        DateUtil.setCalendar(cal, dayCount, 0, false, false);
        return new SimpleDate(cal);
    }
    
    private static final class SimpleDate
    {
        public static final int JANUARY = 1;
        public static final int FEBRUARY = 2;
        public final int year;
        public final int month;
        public final int day;
        public long tsMilliseconds;
        
        public SimpleDate(final Calendar cal) {
            this.year = cal.get(1);
            this.month = cal.get(2) + 1;
            this.day = cal.get(5);
            this.tsMilliseconds = cal.getTimeInMillis();
        }
    }
}
