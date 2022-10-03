package com.adventnet.swissqlapi.util.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Arrays;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Vector;
import java.util.Date;
import java.text.ParseException;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;

public class StringFunctions
{
    private static SimpleDateFormat dateFormat;
    private static SimpleDateFormat dateTimeFormat;
    private static final String[] formats;
    
    public static String replaceFirst(final String replaceWith, final String replaceThis, final String original) {
        StringBuffer sb = new StringBuffer(original);
        final int lengthOfReplaceThis = replaceThis.length();
        final int indexOfReplaceThis = original.indexOf(replaceThis);
        if (indexOfReplaceThis >= 0) {
            sb = sb.replace(indexOfReplaceThis, indexOfReplaceThis + lengthOfReplaceThis, replaceWith);
        }
        return sb.toString();
    }
    
    public static String replaceAll(final String replaceWith, final String replaceThis, final String original) {
        String replacedString = original;
        final StringBuffer sb = new StringBuffer();
        while (true) {
            final int index = replacedString.indexOf(replaceThis);
            if (index == -1) {
                break;
            }
            sb.append(replacedString.substring(0, index) + replaceWith);
            replacedString = replacedString.substring(index + replaceThis.length());
        }
        if (replacedString.length() > 0) {
            sb.append(replacedString);
        }
        return sb.toString();
    }
    
    public static boolean isLowerCase(final String s) {
        if (s == null) {
            return false;
        }
        for (int l = s.length(), i = 0; i < l; ++i) {
            if (!Character.isDigit(s.charAt(i))) {
                if (s.charAt(i) != '_') {
                    if (!Character.isLowerCase(s.charAt(i))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public static boolean isUpperCase(final String s) {
        if (s == null) {
            return false;
        }
        for (int l = s.length(), i = 0; i < l; ++i) {
            if (!Character.isDigit(s.charAt(i))) {
                if (s.charAt(i) != '_') {
                    if (!Character.isUpperCase(s.charAt(i))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public static String getLastStrToken(final String str, final String delim) {
        final String[] split = str.split(delim);
        return split[split.length - 1];
    }
    
    public static String convertToAnsiTimeLiteral(final String argument, final boolean canHandleStringLiterals, final boolean forToChar) {
        String resultString = argument;
        if (canHandleStringLiterals) {
            resultString = convertToAnsiDateFormatIfDateLiteralString(argument, false, true, forToChar);
            if (resultString.equalsIgnoreCase("NULL")) {
                resultString = convertToAnsiTimeFormatIfTimeLiteralString(argument, false, true, forToChar);
            }
        }
        return resultString;
    }
    
    public static String convertToAnsiDateLiteral(final String argument, final boolean canHandleStringLiterals) {
        String resultString = argument;
        if (canHandleStringLiterals) {
            resultString = convertToAnsiDateFormatIfDateLiteralString(argument, false, true, false);
        }
        return resultString;
    }
    
    public static String convertToAnsiDateFormatIfDateLiteralString(final String argument, final boolean isCoalesceWithNull) {
        return convertToAnsiDateFormatIfDateLiteralString(argument, isCoalesceWithNull, false, false);
    }
    
    private static String convertToAnsiDateFormatIfDateLiteralString(final String argument, final boolean isCoalesceWithNull, final boolean canHandleStringLiterals, final boolean forAnsiTime) {
        String resultString = argument;
        if (argument.trim().startsWith("'") && argument.trim().endsWith("'")) {
            final Pattern pattern = Pattern.compile("^'\\d{1,4}/\\d{1,2}/\\d{1,4}(\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}(.\\d{1,6})?)?'$");
            final Pattern pattern2 = Pattern.compile("^'\\d{1,4}-\\d{1,2}-\\d{1,2}(\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}(.\\d{1,6})?)?'$");
            try {
                final boolean slashPattern = pattern.matcher(argument.trim()).matches();
                final boolean hyphenPattern = pattern2.matcher(argument.trim()).matches();
                if (slashPattern || hyphenPattern) {
                    final String splitString = hyphenPattern ? "-" : "/";
                    final String[] arg = argument.replaceAll("'", "").split("\\s+");
                    final String datepart = arg[0].trim();
                    String timepart = (arg.length == 2) ? arg[1].trim() : "";
                    String timewithoutmicropart = "";
                    boolean isDateTimeFormat = false;
                    boolean isNULL = false;
                    if (timepart != null && !timepart.isEmpty()) {
                        timepart = convertToAnsiTimeFormatIfTimeLiteralString("'" + timepart + "'", false, canHandleStringLiterals, false);
                        if (timepart.equalsIgnoreCase("NULL")) {
                            isNULL = true;
                        }
                        else {
                            timepart = " " + timepart.replaceAll("'", "");
                            timewithoutmicropart = timepart.split("[.]")[0];
                            isDateTimeFormat = true;
                        }
                    }
                    else if (forAnsiTime) {
                        timepart = " 00:00:00";
                    }
                    final String[] arr = datepart.split(splitString);
                    if (!isNULL && arr != null && arr.length == 3) {
                        int year = 0;
                        int month = 0;
                        int day = 0;
                        String yearStr = "";
                        String monthStr = "";
                        String dayStr = "";
                        try {
                            year = Integer.parseInt(arr[0]);
                            month = Integer.parseInt(arr[1]);
                            day = Integer.parseInt(arr[2]);
                            if (year == 0 && month == 0 && day == 0) {
                                isNULL = true;
                            }
                            else if ((year > 0 || arr[0].equalsIgnoreCase("00")) && month > 0 && day > 0) {
                                if (month <= 12 && day <= 31) {
                                    if (year == 0) {
                                        yearStr = "2000";
                                    }
                                    else if (year <= 9) {
                                        yearStr = "000" + year;
                                    }
                                    else if (year >= 10 && year <= 69) {
                                        yearStr = "20" + year;
                                    }
                                    else if (year >= 70 && year <= 99) {
                                        yearStr = "19" + year;
                                    }
                                    else if (year < 1000) {
                                        yearStr = "0" + year;
                                    }
                                    else {
                                        yearStr = String.valueOf(year);
                                    }
                                    if (month <= 9) {
                                        monthStr = "0" + month;
                                    }
                                    else {
                                        monthStr = String.valueOf(month);
                                    }
                                    if (day <= 9) {
                                        dayStr = "0" + day;
                                    }
                                    else {
                                        dayStr = String.valueOf(day);
                                    }
                                    String tempStr = "" + yearStr + "-" + monthStr + "-" + dayStr;
                                    final String tempwithoutmicroStr = tempStr + timewithoutmicropart;
                                    tempStr += timepart;
                                    try {
                                        final Date date = isDateTimeFormat ? StringFunctions.dateTimeFormat.parse(tempwithoutmicroStr) : StringFunctions.dateFormat.parse(tempwithoutmicroStr);
                                        final String result = isDateTimeFormat ? StringFunctions.dateTimeFormat.format(date) : StringFunctions.dateFormat.format(date);
                                        if (!result.equals(tempwithoutmicroStr)) {
                                            isNULL = true;
                                        }
                                        else {
                                            resultString = "'" + tempStr + "'";
                                        }
                                    }
                                    catch (final ParseException e) {
                                        isNULL = true;
                                    }
                                }
                                else {
                                    isNULL = true;
                                }
                            }
                        }
                        catch (final Exception ex) {}
                    }
                    if (isNULL) {
                        if (isCoalesceWithNull) {
                            resultString = "COALESCE(NULL)";
                        }
                        else {
                            resultString = "NULL";
                        }
                    }
                }
                else if (canHandleStringLiterals) {
                    resultString = "NULL";
                }
            }
            catch (final Exception ex2) {}
        }
        return resultString;
    }
    
    private static String convertToAnsiTimeFormatIfTimeLiteralString(final String argument, final boolean isCoalesceWithNull, final boolean canHandleStringLiterals, final boolean forToChar) {
        String resultString = argument;
        if (argument.trim().startsWith("'") && argument.trim().endsWith("'")) {
            final Pattern pattern = Pattern.compile("^'\\d{1,2}:\\d{1,2}:\\d{1,2}(.\\d+)?'$");
            try {
                if (pattern.matcher(argument.trim()).matches()) {
                    final String[] array = argument.trim().replaceAll("'", "").split("[.]");
                    String microStr = "";
                    String microsecondStr = "0";
                    if (array.length == 2) {
                        microsecondStr = array[1];
                        microStr = "." + microsecondStr;
                    }
                    final String[] arr = array[0].trim().split(":");
                    boolean isNULL = false;
                    if (arr != null && arr.length == 3) {
                        int hour = 0;
                        int minute = 0;
                        int second = 0;
                        int microsecond = 0;
                        String hourStr = "";
                        String minuteStr = "";
                        String secondStr = "";
                        try {
                            hour = Integer.parseInt(arr[0].toString());
                            minute = Integer.parseInt(arr[1].toString());
                            second = Integer.parseInt(arr[2].toString());
                            microsecond = Integer.parseInt(microsecondStr);
                            if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59 && second >= 0 && second <= 59 && microsecond >= 0 && microsecond <= 999999) {
                                hourStr = String.valueOf(hour);
                                minuteStr = String.valueOf(minute);
                                secondStr = String.valueOf(second);
                                if (hour <= 9) {
                                    hourStr = "0" + hourStr;
                                }
                                if (minute <= 9) {
                                    minuteStr = "0" + minuteStr;
                                }
                                if (second <= 9) {
                                    secondStr = "0" + secondStr;
                                }
                                String dateStr = "";
                                if (forToChar) {
                                    dateStr = "0001-01-01 ";
                                }
                                resultString = "'" + dateStr + hourStr + ":" + minuteStr + ":" + secondStr + microStr + "'";
                            }
                            else {
                                isNULL = true;
                            }
                            if (isNULL) {
                                if (isCoalesceWithNull) {
                                    resultString = "COALESCE(NULL)";
                                }
                                else {
                                    resultString = "NULL";
                                }
                            }
                        }
                        catch (final Exception ex) {}
                    }
                }
                else if (canHandleStringLiterals) {
                    resultString = "NULL";
                }
            }
            catch (final Exception ex2) {}
        }
        return resultString;
    }
    
    public static boolean isNumericValue(final String argument) {
        boolean numericBool = false;
        try {
            final Pattern numberPattern = Pattern.compile("'(-)?[0-9]+(.[0-9]+)?'");
            if (numberPattern.matcher(argument.trim()).matches()) {
                numericBool = true;
            }
        }
        catch (final Exception ex) {}
        return numericBool;
    }
    
    public static boolean isDateTimeValue(final String argument) {
        boolean dateTimeBool = false;
        try {
            final Pattern pattern = Pattern.compile("^'\\d{1,4}/\\d{1,2}/\\d{1,4}(\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}(.\\d{1,6})?)?'$");
            final Pattern pattern2 = Pattern.compile("^'\\d{1,4}-\\d{1,2}-\\d{1,2}(\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}(.\\d{1,6})?)?'$");
            if (pattern.matcher(argument.trim()).matches() || pattern2.matcher(argument.trim()).matches()) {
                dateTimeBool = true;
            }
        }
        catch (final Exception ex) {}
        return dateTimeBool;
    }
    
    public static String getDecimalString(final String argument, final String defaultString) {
        String decimalStr = null;
        final Pattern pattern = Pattern.compile("(-)?[0-9]+(.[0-9]+)?");
        if (pattern.matcher(argument).matches()) {
            try {
                final int value = Integer.parseInt(argument);
                if (Math.abs(value) > 0) {
                    decimalStr = String.valueOf(value) + ".0";
                    return decimalStr;
                }
            }
            catch (final Exception ex) {}
            try {
                final long value2 = Long.parseLong(argument);
                if (Math.abs(value2) > 0L) {
                    return defaultString;
                }
            }
            catch (final Exception ex2) {}
            try {
                final double value3 = Double.parseDouble(argument);
                if (Math.abs(value3) > 0.0) {
                    return argument;
                }
            }
            catch (final Exception ex3) {}
        }
        return defaultString;
    }
    
    public static Integer getIntegerValue(String numberString) {
        Integer value = null;
        if (numberString != null && !numberString.isEmpty()) {
            numberString = numberString.replaceAll("'", "").trim();
            try {
                value = Integer.parseInt(numberString);
                return value;
            }
            catch (final Exception ex) {
                try {
                    final double indexd = Double.parseDouble(numberString);
                    value = (int)Math.round(indexd);
                    return value;
                }
                catch (final Exception e) {
                    value = null;
                }
            }
        }
        return null;
    }
    
    public static Vector getStringFunctionsListForCasting() {
        final Vector list = new Vector();
        list.add("CHAR");
        list.add("CONCAT");
        list.add("CONCAT_WS");
        list.add("LPAD");
        list.add("LEFT");
        list.add("RPAD");
        list.add("RIGHT");
        list.add("LTRIM");
        list.add("RTRIM");
        list.add("REPEAT");
        list.add("REPLACE");
        list.add("REVERSE");
        list.add("SOUNDEX");
        list.add("SPACE");
        list.add("STRCMP");
        list.add("SUBSTR");
        list.add("SUBSTRING");
        list.add("SUBSTRING_INDEX");
        list.add("TRIM");
        list.add("UPPER");
        list.add("LOWER");
        list.add("LCASE");
        list.add("UCASE");
        list.add("INSERT");
        list.add("MID");
        return list;
    }
    
    public static String identifyDateFormate(final String date) {
        if (date != null) {
            final String[] formats = StringFunctions.formats;
            final int length = formats.length;
            int i = 0;
            while (i < length) {
                final String parse = formats[i];
                try {
                    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(parse);
                    final LocalDate dateTime = LocalDate.parse(date, formatter);
                    return parse;
                }
                catch (final Exception ex) {
                    ++i;
                    continue;
                }
                break;
            }
        }
        return null;
    }
    
    public static String handleLiteralStringDateForOracle(final String date) throws ConvertException {
        if (!date.trim().startsWith("'") || !date.trim().endsWith("'")) {
            return date;
        }
        final String datevalue = date.trim().replaceAll("'", "");
        String dateformat = identifyDateFormate(datevalue);
        if (dateformat != null) {
            dateformat = dateformat.replace("mm", "mi");
            return "TO_DATE('" + datevalue + "', '" + dateformat + "')";
        }
        throw new ConvertException("Date format not supported, Please give in formats : " + Arrays.toString(StringFunctions.formats));
    }
    
    static {
        StringFunctions.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        StringFunctions.dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formats = new String[] { "yyyy-MM-dd", "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS", "yyyy/MM/dd HH:mm:ss.SSS" };
    }
}
