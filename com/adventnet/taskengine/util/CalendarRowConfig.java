package com.adventnet.taskengine.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import com.adventnet.persistence.Row;
import java.util.TimeZone;

public class CalendarRowConfig
{
    private ScheduleType schType;
    private TimeZone timeZone;
    private int firstDayOfWeek;
    private Date1 startDate;
    private Time1 executionTime;
    private int skipFrequency;
    private int[] daysOfWeek;
    private int[] weeksOfMonth;
    private int[] weeksOfYear;
    private int[] dates;
    private boolean useDatesInReverseOrder;
    private int[] months;
    private static String[] days_str;
    private static String[] months_str;
    private static final int[] BIT_MASK;
    private static final long[] BIT_MASK1;
    
    public CalendarRowConfig() {
        this.timeZone = TimeZone.getDefault();
        this.firstDayOfWeek = -1;
        this.startDate = null;
        this.executionTime = null;
        this.skipFrequency = 0;
        this.daysOfWeek = null;
        this.weeksOfMonth = null;
        this.weeksOfYear = null;
        this.dates = null;
        this.useDatesInReverseOrder = false;
        this.months = null;
    }
    
    public CalendarRowConfig(final Row calRow) {
        this.timeZone = TimeZone.getDefault();
        this.firstDayOfWeek = -1;
        this.startDate = null;
        this.executionTime = null;
        this.skipFrequency = 0;
        this.daysOfWeek = null;
        this.weeksOfMonth = null;
        this.weeksOfYear = null;
        this.dates = null;
        this.useDatesInReverseOrder = false;
        this.months = null;
        final String schTypeValue = (String)calRow.get(2);
        this.schType = ((schTypeValue != null) ? ScheduleType.valueOf(schTypeValue.toUpperCase()) : ScheduleType.NONE);
        final int skipFrequencyValue = (int)calRow.get(11);
        if (skipFrequencyValue > 0) {
            this.skipFrequency = skipFrequencyValue;
        }
        final String timeZoneId = (String)calRow.get(10);
        if (timeZoneId != null) {
            this.timeZone = TimeZone.getTimeZone(timeZoneId);
        }
        final Integer firstDayOfWeekValue = (Integer)calRow.get(13);
        if (firstDayOfWeekValue != null) {
            this.firstDayOfWeek = firstDayOfWeekValue;
        }
        final Long timeOfDay = (Long)calRow.get(3);
        if (timeOfDay == null) {
            throw new IllegalArgumentException("Mandatory Column 'TIME_OF_DAY' not set in this Calendar Row[" + calRow + "]");
        }
        final String unitOfTime = (String)calRow.get(4);
        this.executionTime = new Time1(timeOfDay.intValue(), (unitOfTime != null) ? unitOfTime : "Seconds");
        if (this.schType == ScheduleType.NONE || this.schType == ScheduleType.DAILY || this.schType == ScheduleType.WEEKLY) {
            if (this.schType == ScheduleType.WEEKLY) {
                this.daysOfWeek = decodeDaysOfWeekMask((int)calRow.get(5));
            }
            final int date = (int)calRow.get(7);
            final int month = (int)calRow.get(8);
            final int year = (int)calRow.get(9);
            this.startDate = ((date == -1 && month == -1 && year == -1) ? null : new Date1(date, month, year));
        }
        else if (this.schType == ScheduleType.MONTHLY) {
            final int month2 = (int)calRow.get(8);
            final int year2 = (int)calRow.get(9);
            this.startDate = ((month2 == -1 && year2 == -1) ? null : new Date1(1, month2, year2));
            this.daysOfWeek = decodeDaysOfWeekMask((int)calRow.get(5));
            this.weeksOfMonth = decodeWeeksOfMonthMask(((Long)calRow.get(6)).intValue());
            this.dates = decodeDatesMask((int)calRow.get(7));
            this.useDatesInReverseOrder = (boolean)calRow.get(12);
        }
        else if (this.schType == ScheduleType.YEARLY) {
            final int year3 = (int)calRow.get(9);
            this.startDate = ((year3 == -1) ? null : new Date1(1, 0, year3));
            this.months = decodeMonthsMask((int)calRow.get(8));
            this.dates = decodeDatesMask((int)calRow.get(7));
            this.useDatesInReverseOrder = (boolean)calRow.get(12);
            this.daysOfWeek = decodeDaysOfWeekMask((int)calRow.get(5));
            if (this.months != null) {
                this.weeksOfMonth = decodeWeeksOfMonthMask(((Long)calRow.get(6)).intValue());
            }
            else {
                this.weeksOfYear = decodeWeeksOfYearMask((long)calRow.get(6));
            }
        }
    }
    
    public Row toCalendarRow() {
        if (this.executionTime == null) {
            throw new IllegalStateException("The field 'ExecutionTime' is NOT yet set");
        }
        if (this.schType == null) {
            throw new IllegalStateException("The field 'ScheduleType' is NOT yet set");
        }
        final Row calRow = new Row("Calendar");
        calRow.set(2, (Object)this.schType.name());
        calRow.set(10, (Object)this.timeZone.getID());
        if (this.firstDayOfWeek != -1) {
            calRow.set(13, (Object)this.firstDayOfWeek);
        }
        calRow.set(11, (Object)this.skipFrequency);
        if (this.executionTime.minutes == 0 && this.executionTime.seconds == 0) {
            calRow.set(4, (Object)"Hours");
            calRow.set(3, (Object)new Long(this.executionTime.hours));
        }
        else if (this.executionTime.seconds == 0) {
            calRow.set(4, (Object)"Minutes");
            calRow.set(3, (Object)new Long(this.executionTime.hours * 60 + this.executionTime.minutes));
        }
        else {
            calRow.set(4, (Object)"Seconds");
            calRow.set(3, (Object)new Long(this.executionTime.hours * 3600 + this.executionTime.minutes * 60 + this.executionTime.seconds));
        }
        if (this.schType == ScheduleType.NONE || this.schType == ScheduleType.DAILY || this.schType == ScheduleType.WEEKLY) {
            if (this.startDate != null) {
                calRow.set(7, (Object)this.startDate.date());
                calRow.set(8, (Object)this.startDate.month());
                calRow.set(9, (Object)this.startDate.year());
            }
            if (this.schType == ScheduleType.WEEKLY) {
                calRow.set("DAY_OF_WEEK", (Object)encodeDaysOfWeek(this.daysOfWeek));
            }
        }
        else if (this.schType == ScheduleType.MONTHLY) {
            if (this.startDate != null) {
                calRow.set(8, (Object)this.startDate.month());
                calRow.set(9, (Object)this.startDate.year());
            }
            calRow.set(5, (Object)encodeDaysOfWeek(this.daysOfWeek));
            calRow.set(6, (Object)new Long(encodeWeeksOfMonth(this.weeksOfMonth)));
            calRow.set(7, (Object)encodeDates(this.dates));
            calRow.set(12, (Object)this.useDatesInReverseOrder);
        }
        else if (this.schType == ScheduleType.YEARLY) {
            if (this.weeksOfMonth != null && this.weeksOfYear != null) {
                throw new IllegalStateException("Both weeksOfMonth and weeksOfYear Field are Set.");
            }
            if (this.startDate != null) {
                calRow.set(9, (Object)this.startDate.year());
            }
            calRow.set(8, (Object)encodeMonths(this.months));
            calRow.set(7, (Object)encodeDates(this.dates));
            calRow.set(12, (Object)this.useDatesInReverseOrder);
            if (this.months != null) {
                calRow.set(6, (Object)new Long(encodeWeeksOfMonth(this.weeksOfMonth)));
            }
            else {
                calRow.set(6, (Object)encodeWeeksOfYear(this.weeksOfYear));
            }
            calRow.set(5, (Object)encodeDaysOfWeek(this.daysOfWeek));
        }
        return calRow;
    }
    
    public Calendar createCalendarObj() {
        final Calendar cal = Calendar.getInstance(this.timeZone);
        if (this.firstDayOfWeek != -1) {
            cal.setFirstDayOfWeek(this.firstDayOfWeek);
        }
        return cal;
    }
    
    public Calendar createCalendarObjForStartDate() {
        if (this.startDate == null) {
            return null;
        }
        final Calendar cal = this.createCalendarObj();
        cal.set(1, this.startDate.year());
        cal.set(2, this.startDate.month());
        cal.set(5, this.startDate.date());
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.getTimeInMillis();
        return cal;
    }
    
    public ScheduleType getScheduleType() {
        return this.schType;
    }
    
    public TimeZone getTimeZone() {
        return this.timeZone;
    }
    
    public int getFirstDayOfWeekValue() {
        return this.firstDayOfWeek;
    }
    
    public int getSkipFrequency() {
        return this.skipFrequency;
    }
    
    public Date1 getStartDate() {
        return this.startDate;
    }
    
    public Time1 getExecutionTime() {
        return this.executionTime;
    }
    
    public int[] getDaysOfWeek() {
        return this.daysOfWeek;
    }
    
    public int[] getWeeksOfMonth() {
        return this.weeksOfMonth;
    }
    
    public int[] getWeeksOfYear() {
        return this.weeksOfYear;
    }
    
    public int[] getDates() {
        return this.dates;
    }
    
    public boolean isDatesInReverseOrder() {
        return this.useDatesInReverseOrder;
    }
    
    public int[] getMonths() {
        return this.months;
    }
    
    public void setTimeZone(final String timeZoneId) {
        this.timeZone = TimeZone.getTimeZone(timeZoneId);
    }
    
    public void setFirstDayOfWeek(final int firstDayOfWeek) {
        validateDayOfWeek(firstDayOfWeek);
        this.firstDayOfWeek = firstDayOfWeek;
        if (this.daysOfWeek != null && firstDayOfWeek > 1) {
            this.daysOfWeek = rotateDaysArr(this.daysOfWeek, firstDayOfWeek);
        }
    }
    
    public static int[] rotateDaysArr(final int[] daysArr, final int startDay) {
        if (daysArr.length == 1 || startDay == 1) {
            return daysArr;
        }
        int pos = Arrays.binarySearch(daysArr, startDay);
        if (pos < 0) {
            pos = -pos - 1;
        }
        if (pos == 0 || pos == daysArr.length) {
            return daysArr;
        }
        final int[] toRet = new int[daysArr.length];
        int i = 0;
        int j = pos;
        while (i < toRet.length) {
            toRet[i] = daysArr[j++];
            if (j == daysArr.length) {
                j = 0;
            }
            ++i;
        }
        return toRet;
    }
    
    public void setScheduleType(final ScheduleType scheduleType) {
        this.schType = scheduleType;
    }
    
    public void setScheduleType(final String scheduleType) {
        this.schType = ScheduleType.valueOf(scheduleType.toUpperCase());
    }
    
    public void setSkipFrequency(final int skipFrequency) {
        if (skipFrequency < 0) {
            throw new IllegalArgumentException("Skip-Frequency value should be greater than 0");
        }
        this.skipFrequency = skipFrequency;
    }
    
    public void setExecutionTime(final int hours, final int mins, final int seconds) {
        this.executionTime = new Time1(hours, mins, seconds);
    }
    
    public void setStartDate(final int date, int month, int year) {
        if (this.schType == ScheduleType.MONTHLY || this.schType == ScheduleType.YEARLY) {
            throw new UnsupportedOperationException("This method is NOT supported for schedule type [" + this.schType + "].Use the other appropriate setStartDate*() method.");
        }
        if (month == -1 || year == -1) {
            final Calendar curr = Calendar.getInstance(this.timeZone);
            month = ((month == -1) ? curr.get(2) : month);
            year = ((year == -1) ? curr.get(1) : year);
        }
        this.startDate = new Date1(date, month, year);
    }
    
    public void setStartDateForMonthlySchedule(final int month, int year) {
        if (this.schType != ScheduleType.MONTHLY) {
            throw new UnsupportedOperationException("This method not supported for schedule type [" + this.schType + "]");
        }
        if (year == -1) {
            final Calendar curr = Calendar.getInstance(this.timeZone);
            year = ((year == -1) ? curr.get(1) : year);
        }
        this.startDate = new Date1(1, month, year);
    }
    
    public void setStartDateForYearlySchedule(final int year) {
        if (this.schType != ScheduleType.YEARLY) {
            throw new UnsupportedOperationException("This method not supported for schedule type [" + this.schType + "]");
        }
        this.startDate = new Date1(1, 0, year);
    }
    
    public void setDaysOfWeek(final int... daysOfWeek) {
        for (final int dayOfWeek : daysOfWeek) {
            validateDayOfWeek(dayOfWeek);
        }
        Arrays.sort(this.daysOfWeek = daysOfWeek);
        if (this.firstDayOfWeek > 1) {
            this.daysOfWeek = rotateDaysArr(this.daysOfWeek, this.firstDayOfWeek);
        }
    }
    
    public void setWeeksOfMonth(final int... weeksOfMonth) {
        for (final int weekOfMonth : weeksOfMonth) {
            validateWeekOfMonth(weekOfMonth);
        }
        Arrays.sort(this.weeksOfMonth = weeksOfMonth);
    }
    
    public void setWeeksOfYear(final int... weeksOfYear) {
        for (final int weekOfYear : weeksOfYear) {
            validateWeekOfYear(weekOfYear);
        }
        Arrays.sort(this.weeksOfYear = weeksOfYear);
    }
    
    public void setDates(final int... dates) {
        for (final int date : dates) {
            validateDate(date);
        }
        Arrays.sort(this.dates = dates);
    }
    
    public void useDatesInReverseOrder() {
        this.useDatesInReverseOrder = true;
    }
    
    public void setMonths(final int... months) {
        for (final int month : months) {
            validateMonth(month);
        }
        Arrays.sort(this.months = months);
    }
    
    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder();
        buff.append("\nschedule-type  : " + this.schType.name());
        buff.append("\ntime-zone      : " + this.timeZone.getDisplayName());
        if (this.skipFrequency > 0) {
            buff.append("\nskip-frequency : " + this.skipFrequency);
        }
        if (this.startDate != null) {
            buff.append("\nstart-date     : " + this.startDate);
        }
        if (this.executionTime != null) {
            buff.append("\nexecution-time : " + this.executionTime);
        }
        if (this.months != null) {
            buff.append("\nmonths         : " + this.monthsAsString());
        }
        if (this.dates != null) {
            buff.append(this.useDatesInReverseOrder ? "\nreverse-dates  : " : "\ndates          : ").append(Arrays.toString(this.dates));
        }
        if (this.weeksOfMonth != null) {
            buff.append("\nweeks-of-month : " + Arrays.toString(this.weeksOfMonth));
        }
        if (this.weeksOfYear != null) {
            buff.append("\nweeks-of-year  : " + Arrays.toString(this.weeksOfYear));
        }
        if (this.daysOfWeek != null) {
            buff.append("\ndays-of-week   : " + this.daysAsString());
        }
        return buff.toString();
    }
    
    private String daysAsString() {
        final StringBuilder sb = new StringBuilder(CalendarRowConfig.days_str[this.daysOfWeek[0]]);
        for (int i = 1; i < this.daysOfWeek.length; ++i) {
            sb.append(',' + CalendarRowConfig.days_str[this.daysOfWeek[i]]);
        }
        return sb.toString();
    }
    
    private String monthsAsString() {
        final StringBuilder sb = new StringBuilder(CalendarRowConfig.months_str[this.months[0]]);
        for (int i = 1; i < this.months.length; ++i) {
            sb.append(',' + CalendarRowConfig.months_str[this.months[i]]);
        }
        return sb.toString();
    }
    
    private static void validateHour(final int hour) {
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("InCorrect 'Hour' value [" + hour + "]. Value  should be in the range [0-23]");
        }
    }
    
    private static void validateMinute(final int minute) {
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("InCorrect 'Minute' value [" + minute + "]. Value  should be in the range [0-59]");
        }
    }
    
    private static void validateSecond(final int second) {
        if (second < 0 || second > 59) {
            throw new IllegalArgumentException("InCorrect 'Seconds' value [" + second + "]. Value  should be in the range [0-59]");
        }
    }
    
    private static void validateDate(final int date) {
        if (date < 1 || date > 31) {
            throw new IllegalArgumentException("InCorrect 'Date' value [" + date + "]. Value  should be in the range [1-31]");
        }
    }
    
    private static void validateMonth(final int month) {
        if (month < 0 || month > 11) {
            throw new IllegalArgumentException("InCorrect 'Month' value [" + month + "]. Value  should be in the range [0-11]");
        }
    }
    
    private static void validateDayOfWeek(final int dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("InCorrect 'Day_Of_Week' value [" + dayOfWeek + "]. Value should be in the range [1-7]");
        }
    }
    
    private static void validateWeekOfMonth(final int weekOfMonth) {
        if (weekOfMonth < 1 || weekOfMonth > 5) {
            throw new IllegalArgumentException("InCorrect 'Week_of_Month' value [" + weekOfMonth + "]. Value should be in the range [1-5]");
        }
    }
    
    private static void validateWeekOfYear(final int weekOfYear) {
        if (weekOfYear < 1 || weekOfYear > 53) {
            throw new IllegalArgumentException("InCorrect 'Week_of_Year' value [" + weekOfYear + "]. Value should be in the range [1-53]");
        }
    }
    
    private static boolean isBitSet(final int value, final int bitIndex) {
        return (value & CalendarRowConfig.BIT_MASK[bitIndex]) != 0x0;
    }
    
    private static boolean isBitSet1(final long value, final int bitIndex) {
        return (bitIndex < 32) ? ((value & (long)CalendarRowConfig.BIT_MASK[bitIndex]) != 0x0L) : ((value & CalendarRowConfig.BIT_MASK1[bitIndex - 32]) != 0x0L);
    }
    
    public static int encodeDaysOfWeek(final int... daysOfWeek) {
        if (daysOfWeek == null || daysOfWeek.length == 0) {
            return -1;
        }
        if (daysOfWeek.length == 1) {
            return daysOfWeek[0];
        }
        int result = 0;
        for (int i = 0; i < daysOfWeek.length; ++i) {
            result |= CalendarRowConfig.BIT_MASK[daysOfWeek[i] + 8];
        }
        return result;
    }
    
    public static int encodeWeeksOfMonth(final int... weeksOfMonth) {
        if (weeksOfMonth == null || weeksOfMonth.length == 0) {
            return -1;
        }
        if (weeksOfMonth.length == 1) {
            return weeksOfMonth[0];
        }
        int result = 0;
        for (int i = 0; i < weeksOfMonth.length; ++i) {
            result |= CalendarRowConfig.BIT_MASK[weeksOfMonth[i] + 8];
        }
        return result;
    }
    
    public static long encodeWeeksOfYear(final int... weeksOfYear) {
        if (weeksOfYear == null || weeksOfYear.length == 0) {
            return -1L;
        }
        if (weeksOfYear.length == 1) {
            return weeksOfYear[0];
        }
        long result = 0L;
        for (int i = 0; i < weeksOfYear.length; ++i) {
            final int bitIndex = weeksOfYear[i] + 8;
            result |= ((bitIndex < 32) ? CalendarRowConfig.BIT_MASK[bitIndex] : CalendarRowConfig.BIT_MASK1[bitIndex - 32]);
        }
        return result;
    }
    
    public static int encodeDates(final int... dates) {
        if (dates == null || dates.length == 0) {
            return -1;
        }
        if (dates.length == 1) {
            return dates[0];
        }
        int result = 0;
        for (int i = 0; i < dates.length; ++i) {
            result |= CalendarRowConfig.BIT_MASK[dates[i]];
        }
        result |= CalendarRowConfig.BIT_MASK[32];
        return result;
    }
    
    public static int encodeMonths(final int... months) {
        if (months == null || months.length == 0) {
            return -1;
        }
        if (months.length == 1) {
            return months[0];
        }
        int result = 0;
        for (int i = 0; i < months.length; ++i) {
            result |= CalendarRowConfig.BIT_MASK[months[i] + 9];
        }
        return result;
    }
    
    public static int[] decodeDaysOfWeekMask(final int daysOfWeekMask) {
        if (daysOfWeekMask == -1 || daysOfWeekMask == 0) {
            return null;
        }
        if (daysOfWeekMask < 8) {
            return singletonArray(daysOfWeekMask);
        }
        final List<Integer> daysList = new ArrayList<Integer>();
        for (int i = 1; i < 8; ++i) {
            if (isBitSet(daysOfWeekMask, i + 8)) {
                daysList.add(i);
            }
        }
        return toArray(daysList);
    }
    
    public static int[] decodeWeeksOfMonthMask(final int weeksOfMonthMask) {
        if (weeksOfMonthMask == -1 || weeksOfMonthMask == 0) {
            return null;
        }
        if (weeksOfMonthMask < 6) {
            return singletonArray(weeksOfMonthMask);
        }
        final List<Integer> weeksList = new ArrayList<Integer>();
        for (int i = 1; i < 6; ++i) {
            if (isBitSet(weeksOfMonthMask, i + 8)) {
                weeksList.add(i);
            }
        }
        return toArray(weeksList);
    }
    
    public static int[] decodeWeeksOfYearMask(final long weeksOfYearMask) {
        if (weeksOfYearMask == -1L || weeksOfYearMask == 0L) {
            return null;
        }
        if (weeksOfYearMask < 54L) {
            return singletonArray((int)weeksOfYearMask);
        }
        final List<Integer> weeksList = new ArrayList<Integer>();
        for (int i = 1; i < 54; ++i) {
            if (isBitSet1(weeksOfYearMask, i + 8)) {
                weeksList.add(i);
            }
        }
        return toArray(weeksList);
    }
    
    public static int[] decodeDatesMask(final int datesMask) {
        if (datesMask == -1 || datesMask == 0) {
            return null;
        }
        if (isBitSet(datesMask, 32)) {
            final List<Integer> datesList = new ArrayList<Integer>();
            for (int i = 1; i < 32; ++i) {
                if (isBitSet(datesMask, i)) {
                    datesList.add(i);
                }
            }
            return toArray(datesList);
        }
        if (datesMask < 32) {
            return singletonArray(datesMask);
        }
        throw new IllegalArgumentException("Incorrect Date value [" + datesMask + "]");
    }
    
    public static int[] decodeMonthsMask(final int monthsMask) {
        if (monthsMask == -1) {
            return null;
        }
        if (monthsMask < 12) {
            return singletonArray(monthsMask);
        }
        final List<Integer> monthsList = new ArrayList<Integer>();
        for (int i = 0; i < 12; ++i) {
            if (isBitSet(monthsMask, i + 9)) {
                monthsList.add(i);
            }
        }
        return toArray(monthsList);
    }
    
    private static int[] singletonArray(final int value) {
        return new int[] { value };
    }
    
    private static int[] toArray(final List<Integer> integerList) {
        final int[] toRet = new int[integerList.size()];
        for (int i = 0; i < integerList.size(); ++i) {
            toRet[i] = integerList.get(i);
        }
        return toRet;
    }
    
    private static String twoDigitStr(final int val) {
        if (val < 0) {
            return "00";
        }
        return (val < 10) ? ("0" + val) : String.valueOf(val);
    }
    
    static {
        CalendarRowConfig.days_str = new String[] { "-", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
        CalendarRowConfig.months_str = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        BIT_MASK = new int[] { 0, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824, Integer.MIN_VALUE };
        BIT_MASK1 = new long[] { 2147483648L, 4294967296L, 8589934592L, 17179869184L, 34359738368L, 68719476736L, 137438953472L, 274877906944L, 549755813888L, 1099511627776L, 2199023255552L, 4398046511104L, 8796093022208L, 17592186044416L, 35184372088832L, 70368744177664L, 140737488355328L, 281474976710656L, 562949953421312L, 1125899906842624L, 2251799813685248L, 4503599627370496L, 9007199254740992L, 18014398509481984L, 36028797018963968L, 72057594037927936L, 144115188075855872L, 288230376151711744L, 576460752303423488L, 1152921504606846976L, 2305843009213693952L, 4611686018427387904L, Long.MIN_VALUE };
    }
    
    public enum ScheduleType
    {
        NONE, 
        DAILY, 
        WEEKLY, 
        MONTHLY, 
        YEARLY;
    }
    
    public static class Date1
    {
        private int date;
        private int month;
        private int year;
        
        public Date1(final int date, final int month, final int year) {
            validateDate(date);
            validateMonth(month);
            this.date = date;
            this.month = month;
            this.year = year;
        }
        
        public int date() {
            return this.date;
        }
        
        public int month() {
            return this.month;
        }
        
        public int year() {
            return this.year;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(twoDigitStr(this.date) + "/");
            sb.append(twoDigitStr(this.month + 1) + "/");
            if (this.year > 0) {
                sb.append(String.valueOf(this.year));
            }
            return sb.toString();
        }
    }
    
    public static class Time1
    {
        private int hours;
        private int minutes;
        private int seconds;
        
        public Time1(final int value, final String unit) {
            if ("Hours".equalsIgnoreCase(unit)) {
                this.hours = value;
            }
            else if ("Minutes".equalsIgnoreCase(unit)) {
                this.hours = value / 60;
                this.minutes = value % 60;
            }
            else {
                if (!"Seconds".equalsIgnoreCase(unit)) {
                    throw new IllegalArgumentException("The value [" + unit + "] specified for 'unit' is incorrect");
                }
                this.hours = value / 3600;
                final int reminder = value % 3600;
                this.minutes = reminder / 60;
                this.seconds = reminder % 60;
            }
            validateHour(this.hours);
            validateMinute(this.minutes);
            validateSecond(this.seconds);
        }
        
        public Time1(final int hours, final int minutes, final int seconds) {
            validateHour(hours);
            validateMinute(minutes);
            validateSecond(seconds);
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
        }
        
        public int hours() {
            return this.hours;
        }
        
        public int minutes() {
            return this.minutes;
        }
        
        public int seconds() {
            return this.seconds;
        }
        
        @Override
        public String toString() {
            return twoDigitStr(this.hours) + ":" + twoDigitStr(this.minutes) + ":" + twoDigitStr(this.seconds);
        }
    }
}
