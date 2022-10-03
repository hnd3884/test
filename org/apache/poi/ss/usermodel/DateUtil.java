package org.apache.poi.ss.usermodel;

import java.time.temporal.TemporalField;
import java.time.temporal.ChronoField;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.time.ZoneId;
import java.time.temporal.TemporalQueries;
import java.time.LocalTime;
import org.apache.poi.ss.formula.ConditionalFormattingEvaluator;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoUnit;
import org.apache.poi.util.LocaleUtil;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.TimeZone;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import java.math.BigDecimal;

public class DateUtil
{
    public static final int SECONDS_PER_MINUTE = 60;
    public static final int MINUTES_PER_HOUR = 60;
    public static final int HOURS_PER_DAY = 24;
    public static final int SECONDS_PER_DAY = 86400;
    private static final int BAD_DATE = -1;
    public static final long DAY_MILLISECONDS = 86400000L;
    private static final BigDecimal BD_NANOSEC_DAY;
    private static final BigDecimal BD_MILISEC_RND;
    private static final BigDecimal BD_SECOND_RND;
    private static final Pattern TIME_SEPARATOR_PATTERN;
    private static final Pattern date_ptrn1;
    private static final Pattern date_ptrn2;
    private static final Pattern date_ptrn3a;
    private static final Pattern date_ptrn3b;
    private static final Pattern date_ptrn4;
    private static final Pattern date_ptrn5;
    private static final DateTimeFormatter dateTimeFormats;
    private static ThreadLocal<Integer> lastFormatIndex;
    private static ThreadLocal<String> lastFormatString;
    private static ThreadLocal<Boolean> lastCachedResult;
    
    protected DateUtil() {
    }
    
    public static LocalDateTime toLocalDateTime(final Date date) {
        return date.toInstant().atZone(TimeZone.getTimeZone("UTC").toZoneId()).toLocalDateTime();
    }
    
    public static LocalDateTime toLocalDateTime(final Calendar date) {
        return date.toInstant().atZone(TimeZone.getTimeZone("UTC").toZoneId()).toLocalDateTime();
    }
    
    public static double getExcelDate(final LocalDate date) {
        return getExcelDate(date, false);
    }
    
    public static double getExcelDate(final LocalDate date, final boolean use1904windowing) {
        final int year = date.getYear();
        final int dayOfYear = date.getDayOfYear();
        final int hour = 0;
        final int minute = 0;
        final int second = 0;
        final int milliSecond = 0;
        return internalGetExcelDate(year, dayOfYear, hour, minute, second, milliSecond, use1904windowing);
    }
    
    public static double getExcelDate(final LocalDateTime date) {
        return getExcelDate(date, false);
    }
    
    public static double getExcelDate(final LocalDateTime date, final boolean use1904windowing) {
        final int year = date.getYear();
        final int dayOfYear = date.getDayOfYear();
        final int hour = date.getHour();
        final int minute = date.getMinute();
        final int second = date.getSecond();
        final int milliSecond = date.getNano() / 1000000;
        return internalGetExcelDate(year, dayOfYear, hour, minute, second, milliSecond, use1904windowing);
    }
    
    public static double getExcelDate(final Date date) {
        return getExcelDate(date, false);
    }
    
    public static double getExcelDate(final Date date, final boolean use1904windowing) {
        final Calendar calStart = LocaleUtil.getLocaleCalendar();
        calStart.setTime(date);
        final int year = calStart.get(1);
        final int dayOfYear = calStart.get(6);
        final int hour = calStart.get(11);
        final int minute = calStart.get(12);
        final int second = calStart.get(13);
        final int milliSecond = calStart.get(14);
        return internalGetExcelDate(year, dayOfYear, hour, minute, second, milliSecond, use1904windowing);
    }
    
    public static double getExcelDate(final Calendar date, final boolean use1904windowing) {
        final int year = date.get(1);
        final int dayOfYear = date.get(6);
        final int hour = date.get(11);
        final int minute = date.get(12);
        final int second = date.get(13);
        final int milliSecond = date.get(14);
        return internalGetExcelDate(year, dayOfYear, hour, minute, second, milliSecond, use1904windowing);
    }
    
    private static double internalGetExcelDate(final int year, final int dayOfYear, final int hour, final int minute, final int second, final int milliSecond, final boolean use1904windowing) {
        if ((!use1904windowing && year < 1900) || (use1904windowing && year < 1904)) {
            return -1.0;
        }
        final double fraction = (((hour * 60.0 + minute) * 60.0 + second) * 1000.0 + milliSecond) / 8.64E7;
        double value = fraction + absoluteDay(year, dayOfYear, use1904windowing);
        if (!use1904windowing && value >= 60.0) {
            ++value;
        }
        else if (use1904windowing) {
            --value;
        }
        return value;
    }
    
    public static Date getJavaDate(final double date, final TimeZone tz) {
        return getJavaDate(date, false, tz, false);
    }
    
    public static Date getJavaDate(final double date) {
        return getJavaDate(date, false, null, false);
    }
    
    public static Date getJavaDate(final double date, final boolean use1904windowing, final TimeZone tz) {
        return getJavaDate(date, use1904windowing, tz, false);
    }
    
    public static Date getJavaDate(final double date, final boolean use1904windowing, final TimeZone tz, final boolean roundSeconds) {
        final Calendar calendar = getJavaCalendar(date, use1904windowing, tz, roundSeconds);
        return (calendar == null) ? null : calendar.getTime();
    }
    
    public static Date getJavaDate(final double date, final boolean use1904windowing) {
        return getJavaDate(date, use1904windowing, null, false);
    }
    
    public static LocalDateTime getLocalDateTime(final double date) {
        return getLocalDateTime(date, false, false);
    }
    
    public static LocalDateTime getLocalDateTime(final double date, final boolean use1904windowing) {
        return getLocalDateTime(date, use1904windowing, false);
    }
    
    public static LocalDateTime getLocalDateTime(final double date, final boolean use1904windowing, final boolean roundSeconds) {
        if (!isValidExcelDate(date)) {
            return null;
        }
        final BigDecimal bd = new BigDecimal(date);
        final int wholeDays = bd.intValue();
        int startYear = 1900;
        int dayAdjust = -1;
        if (use1904windowing) {
            startYear = 1904;
            dayAdjust = 1;
        }
        else if (wholeDays < 61) {
            dayAdjust = 0;
        }
        LocalDateTime ldt = LocalDateTime.of(startYear, 1, 1, 0, 0);
        ldt = ldt.plusDays(wholeDays + dayAdjust - 1);
        final long nanosTime = bd.subtract(BigDecimal.valueOf(wholeDays)).multiply(DateUtil.BD_NANOSEC_DAY).add(roundSeconds ? DateUtil.BD_SECOND_RND : DateUtil.BD_MILISEC_RND).longValue();
        ldt = ldt.plusNanos(nanosTime);
        ldt = ldt.truncatedTo(roundSeconds ? ChronoUnit.SECONDS : ChronoUnit.MILLIS);
        return ldt;
    }
    
    public static void setCalendar(final Calendar calendar, final int wholeDays, final int millisecondsInDay, final boolean use1904windowing, final boolean roundSeconds) {
        int startYear = 1900;
        int dayAdjust = -1;
        if (use1904windowing) {
            startYear = 1904;
            dayAdjust = 1;
        }
        else if (wholeDays < 61) {
            dayAdjust = 0;
        }
        calendar.set(startYear, 0, wholeDays + dayAdjust, 0, 0, 0);
        calendar.set(14, millisecondsInDay);
        if (calendar.get(14) == 0) {
            calendar.clear(14);
        }
        if (roundSeconds) {
            calendar.add(14, 500);
            calendar.clear(14);
        }
    }
    
    public static Calendar getJavaCalendar(final double date) {
        return getJavaCalendar(date, false, null, false);
    }
    
    public static Calendar getJavaCalendar(final double date, final boolean use1904windowing) {
        return getJavaCalendar(date, use1904windowing, null, false);
    }
    
    public static Calendar getJavaCalendarUTC(final double date, final boolean use1904windowing) {
        return getJavaCalendar(date, use1904windowing, LocaleUtil.TIMEZONE_UTC, false);
    }
    
    public static Calendar getJavaCalendar(final double date, final boolean use1904windowing, final TimeZone timeZone) {
        return getJavaCalendar(date, use1904windowing, timeZone, false);
    }
    
    public static Calendar getJavaCalendar(final double date, final boolean use1904windowing, final TimeZone timeZone, final boolean roundSeconds) {
        if (!isValidExcelDate(date)) {
            return null;
        }
        final int wholeDays = (int)Math.floor(date);
        final int millisecondsInDay = (int)((date - wholeDays) * 8.64E7 + 0.5);
        Calendar calendar;
        if (timeZone != null) {
            calendar = LocaleUtil.getLocaleCalendar(timeZone);
        }
        else {
            calendar = LocaleUtil.getLocaleCalendar();
        }
        setCalendar(calendar, wholeDays, millisecondsInDay, use1904windowing, roundSeconds);
        return calendar;
    }
    
    private static boolean isCached(final String formatString, final int formatIndex) {
        return formatIndex == DateUtil.lastFormatIndex.get() && formatString.equals(DateUtil.lastFormatString.get());
    }
    
    private static void cache(final String formatString, final int formatIndex, final boolean cached) {
        if (formatString == null || "".equals(formatString)) {
            DateUtil.lastFormatString.remove();
        }
        else {
            DateUtil.lastFormatString.set(formatString);
        }
        if (formatIndex == -1) {
            DateUtil.lastFormatIndex.remove();
        }
        else {
            DateUtil.lastFormatIndex.set(formatIndex);
        }
        DateUtil.lastCachedResult.set(cached);
    }
    
    public static boolean isADateFormat(final ExcelNumberFormat numFmt) {
        return numFmt != null && isADateFormat(numFmt.getIdx(), numFmt.getFormat());
    }
    
    public static boolean isADateFormat(final int formatIndex, final String formatString) {
        if (isInternalDateFormat(formatIndex)) {
            cache(formatString, formatIndex, true);
            return true;
        }
        if (formatString == null || formatString.length() == 0) {
            return false;
        }
        if (isCached(formatString, formatIndex)) {
            return DateUtil.lastCachedResult.get();
        }
        String fs = formatString;
        final int length = fs.length();
        final StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            final char c = fs.charAt(i);
            if (i < length - 1) {
                final char nc = fs.charAt(i + 1);
                if (c == '\\') {
                    switch (nc) {
                        case ' ':
                        case ',':
                        case '-':
                        case '.':
                        case '\\': {
                            continue;
                        }
                    }
                }
                else if (c == ';' && nc == '@') {
                    ++i;
                    continue;
                }
            }
            sb.append(c);
        }
        fs = sb.toString();
        if (DateUtil.date_ptrn4.matcher(fs).matches()) {
            cache(formatString, formatIndex, true);
            return true;
        }
        fs = DateUtil.date_ptrn5.matcher(fs).replaceAll("");
        fs = DateUtil.date_ptrn1.matcher(fs).replaceAll("");
        fs = DateUtil.date_ptrn2.matcher(fs).replaceAll("");
        final int separatorIndex = fs.indexOf(59);
        if (0 < separatorIndex && separatorIndex < fs.length() - 1) {
            fs = fs.substring(0, separatorIndex);
        }
        if (!DateUtil.date_ptrn3a.matcher(fs).find()) {
            return false;
        }
        final boolean result = DateUtil.date_ptrn3b.matcher(fs).matches();
        cache(formatString, formatIndex, result);
        return result;
    }
    
    public static boolean isInternalDateFormat(final int format) {
        switch (format) {
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 45:
            case 46:
            case 47: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isCellDateFormatted(final Cell cell) {
        return isCellDateFormatted(cell, null);
    }
    
    public static boolean isCellDateFormatted(final Cell cell, final ConditionalFormattingEvaluator cfEvaluator) {
        if (cell == null) {
            return false;
        }
        boolean bDate = false;
        final double d = cell.getNumericCellValue();
        if (isValidExcelDate(d)) {
            final ExcelNumberFormat nf = ExcelNumberFormat.from(cell, cfEvaluator);
            if (nf == null) {
                return false;
            }
            bDate = isADateFormat(nf);
        }
        return bDate;
    }
    
    public static boolean isCellInternalDateFormatted(final Cell cell) {
        if (cell == null) {
            return false;
        }
        boolean bDate = false;
        final double d = cell.getNumericCellValue();
        if (isValidExcelDate(d)) {
            final CellStyle style = cell.getCellStyle();
            final int i = style.getDataFormat();
            bDate = isInternalDateFormat(i);
        }
        return bDate;
    }
    
    public static boolean isValidExcelDate(final double value) {
        return value > -4.9E-324;
    }
    
    protected static int absoluteDay(final Calendar cal, final boolean use1904windowing) {
        return absoluteDay(cal.get(1), cal.get(6), use1904windowing);
    }
    
    protected static int absoluteDay(final LocalDateTime date, final boolean use1904windowing) {
        return absoluteDay(date.getYear(), date.getDayOfYear(), use1904windowing);
    }
    
    private static int absoluteDay(final int year, final int dayOfYear, final boolean use1904windowing) {
        return dayOfYear + daysInPriorYears(year, use1904windowing);
    }
    
    static int daysInPriorYears(final int yr, final boolean use1904windowing) {
        if ((!use1904windowing && yr < 1900) || (use1904windowing && yr < 1904)) {
            throw new IllegalArgumentException("'year' must be 1900 or greater");
        }
        final int yr2 = yr - 1;
        final int leapDays = yr2 / 4 - yr2 / 100 + yr2 / 400 - 460;
        return 365 * (yr - (use1904windowing ? 1904 : 1900)) + leapDays;
    }
    
    private static Calendar dayStart(final Calendar cal) {
        cal.get(11);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        cal.get(11);
        return cal;
    }
    
    public static double convertTime(final String timeStr) {
        try {
            return convertTimeInternal(timeStr);
        }
        catch (final FormatException e) {
            final String msg = "Bad time format '" + timeStr + "' expected 'HH:MM' or 'HH:MM:SS' - " + e.getMessage();
            throw new IllegalArgumentException(msg);
        }
    }
    
    private static double convertTimeInternal(final String timeStr) throws FormatException {
        final int len = timeStr.length();
        if (len < 4 || len > 8) {
            throw new FormatException("Bad length");
        }
        final String[] parts = DateUtil.TIME_SEPARATOR_PATTERN.split(timeStr);
        String secStr = null;
        switch (parts.length) {
            case 2: {
                secStr = "00";
                break;
            }
            case 3: {
                secStr = parts[2];
                break;
            }
            default: {
                throw new FormatException("Expected 2 or 3 fields but got (" + parts.length + ")");
            }
        }
        final String hourStr = parts[0];
        final String minStr = parts[1];
        final int hours = parseInt(hourStr, "hour", 24);
        final int minutes = parseInt(minStr, "minute", 60);
        final int seconds = parseInt(secStr, "second", 60);
        final double totalSeconds = seconds + (minutes + hours * 60.0) * 60.0;
        return totalSeconds / 86400.0;
    }
    
    public static Date parseYYYYMMDDDate(final String dateStr) {
        try {
            return parseYYYYMMDDDateInternal(dateStr);
        }
        catch (final FormatException e) {
            final String msg = "Bad time format " + dateStr + " expected 'YYYY/MM/DD' - " + e.getMessage();
            throw new IllegalArgumentException(msg);
        }
    }
    
    private static Date parseYYYYMMDDDateInternal(final String timeStr) throws FormatException {
        if (timeStr.length() != 10) {
            throw new FormatException("Bad length");
        }
        final String yearStr = timeStr.substring(0, 4);
        final String monthStr = timeStr.substring(5, 7);
        final String dayStr = timeStr.substring(8, 10);
        final int year = parseInt(yearStr, "year", -32768, 32767);
        final int month = parseInt(monthStr, "month", 1, 12);
        final int day = parseInt(dayStr, "day", 1, 31);
        final Calendar cal = LocaleUtil.getLocaleCalendar(year, month - 1, day);
        return cal.getTime();
    }
    
    private static int parseInt(final String strVal, final String fieldName, final int rangeMax) throws FormatException {
        return parseInt(strVal, fieldName, 0, rangeMax - 1);
    }
    
    private static int parseInt(final String strVal, final String fieldName, final int lowerLimit, final int upperLimit) throws FormatException {
        int result;
        try {
            result = Integer.parseInt(strVal);
        }
        catch (final NumberFormatException e) {
            throw new FormatException("Bad int format '" + strVal + "' for " + fieldName + " field");
        }
        if (result < lowerLimit || result > upperLimit) {
            throw new FormatException(fieldName + " value (" + result + ") is outside the allowable range(0.." + upperLimit + ")");
        }
        return result;
    }
    
    public static Double parseDateTime(final String str) {
        final TemporalAccessor tmp = DateUtil.dateTimeFormats.parse(str.replaceAll("\\s+", " "));
        final LocalTime time = tmp.query(TemporalQueries.localTime());
        final LocalDate date = tmp.query(TemporalQueries.localDate());
        if (time == null && date == null) {
            return null;
        }
        double tm = 0.0;
        if (date != null) {
            final Date d = Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            tm = getExcelDate(d);
        }
        if (time != null) {
            tm += 1.0 * time.toSecondOfDay() / 86400.0;
        }
        return tm;
    }
    
    static {
        BD_NANOSEC_DAY = BigDecimal.valueOf(8.64E13);
        BD_MILISEC_RND = BigDecimal.valueOf(500000.0);
        BD_SECOND_RND = BigDecimal.valueOf(5.0E8);
        TIME_SEPARATOR_PATTERN = Pattern.compile(":");
        date_ptrn1 = Pattern.compile("^\\[\\$\\-.*?\\]");
        date_ptrn2 = Pattern.compile("^\\[[a-zA-Z]+\\]");
        date_ptrn3a = Pattern.compile("[yYmMdDhHsS]");
        date_ptrn3b = Pattern.compile("^[\\[\\]yYmMdDhHsS\\-T/\u5e74\u6708\u65e5,. :\"\\\\]+0*[ampAMP/]*$");
        date_ptrn4 = Pattern.compile("^\\[([hH]+|[mM]+|[sS]+)\\]");
        date_ptrn5 = Pattern.compile("^\\[DBNum(1|2|3)\\]");
        dateTimeFormats = new DateTimeFormatterBuilder().appendPattern("[dd MMM[ yyyy]][[ ]h:m[:s] a][[ ]H:m[:s]]").appendPattern("[[yyyy ]dd-MMM[-yyyy]][[ ]h:m[:s] a][[ ]H:m[:s]]").appendPattern("[M/dd[/yyyy]][[ ]h:m[:s] a][[ ]H:m[:s]]").appendPattern("[[yyyy/]M/dd][[ ]h:m[:s] a][[ ]H:m[:s]]").parseDefaulting(ChronoField.YEAR_OF_ERA, LocaleUtil.getLocaleCalendar().get(1)).toFormatter();
        DateUtil.lastFormatIndex = ThreadLocal.withInitial(() -> -1);
        DateUtil.lastFormatString = new ThreadLocal<String>();
        DateUtil.lastCachedResult = new ThreadLocal<Boolean>();
    }
    
    private static final class FormatException extends Exception
    {
        public FormatException(final String msg) {
            super(msg);
        }
    }
}
