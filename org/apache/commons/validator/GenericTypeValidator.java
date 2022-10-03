package org.apache.commons.validator;

import java.text.SimpleDateFormat;
import org.apache.commons.logging.Log;
import java.text.ParseException;
import org.apache.commons.logging.LogFactory;
import java.text.DateFormat;
import java.util.Date;
import java.text.ParsePosition;
import java.text.NumberFormat;
import java.util.Locale;
import java.io.Serializable;

public class GenericTypeValidator implements Serializable
{
    public static Byte formatByte(final String value) {
        if (value == null) {
            return null;
        }
        try {
            return new Byte(value);
        }
        catch (final NumberFormatException e) {
            return null;
        }
    }
    
    public static Byte formatByte(final String value, final Locale locale) {
        Byte result = null;
        if (value != null) {
            NumberFormat formatter = null;
            if (locale != null) {
                formatter = NumberFormat.getNumberInstance(locale);
            }
            else {
                formatter = NumberFormat.getNumberInstance(Locale.getDefault());
            }
            formatter.setParseIntegerOnly(true);
            final ParsePosition pos = new ParsePosition(0);
            final Number num = formatter.parse(value, pos);
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() && num.doubleValue() >= -128.0 && num.doubleValue() <= 127.0) {
                result = new Byte(num.byteValue());
            }
        }
        return result;
    }
    
    public static Short formatShort(final String value) {
        if (value == null) {
            return null;
        }
        try {
            return new Short(value);
        }
        catch (final NumberFormatException e) {
            return null;
        }
    }
    
    public static Short formatShort(final String value, final Locale locale) {
        Short result = null;
        if (value != null) {
            NumberFormat formatter = null;
            if (locale != null) {
                formatter = NumberFormat.getNumberInstance(locale);
            }
            else {
                formatter = NumberFormat.getNumberInstance(Locale.getDefault());
            }
            formatter.setParseIntegerOnly(true);
            final ParsePosition pos = new ParsePosition(0);
            final Number num = formatter.parse(value, pos);
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() && num.doubleValue() >= -32768.0 && num.doubleValue() <= 32767.0) {
                result = new Short(num.shortValue());
            }
        }
        return result;
    }
    
    public static Integer formatInt(final String value) {
        if (value == null) {
            return null;
        }
        try {
            return new Integer(value);
        }
        catch (final NumberFormatException e) {
            return null;
        }
    }
    
    public static Integer formatInt(final String value, final Locale locale) {
        Integer result = null;
        if (value != null) {
            NumberFormat formatter = null;
            if (locale != null) {
                formatter = NumberFormat.getNumberInstance(locale);
            }
            else {
                formatter = NumberFormat.getNumberInstance(Locale.getDefault());
            }
            formatter.setParseIntegerOnly(true);
            final ParsePosition pos = new ParsePosition(0);
            final Number num = formatter.parse(value, pos);
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() && num.doubleValue() >= -2.147483648E9 && num.doubleValue() <= 2.147483647E9) {
                result = new Integer(num.intValue());
            }
        }
        return result;
    }
    
    public static Long formatLong(final String value) {
        if (value == null) {
            return null;
        }
        try {
            return new Long(value);
        }
        catch (final NumberFormatException e) {
            return null;
        }
    }
    
    public static Long formatLong(final String value, final Locale locale) {
        Long result = null;
        if (value != null) {
            NumberFormat formatter = null;
            if (locale != null) {
                formatter = NumberFormat.getNumberInstance(locale);
            }
            else {
                formatter = NumberFormat.getNumberInstance(Locale.getDefault());
            }
            formatter.setParseIntegerOnly(true);
            final ParsePosition pos = new ParsePosition(0);
            final Number num = formatter.parse(value, pos);
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() && num.doubleValue() >= -9.223372036854776E18 && num.doubleValue() <= 9.223372036854776E18) {
                result = new Long(num.longValue());
            }
        }
        return result;
    }
    
    public static Float formatFloat(final String value) {
        if (value == null) {
            return null;
        }
        try {
            return new Float(value);
        }
        catch (final NumberFormatException e) {
            return null;
        }
    }
    
    public static Float formatFloat(final String value, final Locale locale) {
        Float result = null;
        if (value != null) {
            NumberFormat formatter = null;
            if (locale != null) {
                formatter = NumberFormat.getInstance(locale);
            }
            else {
                formatter = NumberFormat.getInstance(Locale.getDefault());
            }
            final ParsePosition pos = new ParsePosition(0);
            final Number num = formatter.parse(value, pos);
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() && num.doubleValue() >= -3.4028234663852886E38 && num.doubleValue() <= 3.4028234663852886E38) {
                result = new Float(num.floatValue());
            }
        }
        return result;
    }
    
    public static Double formatDouble(final String value) {
        if (value == null) {
            return null;
        }
        try {
            return new Double(value);
        }
        catch (final NumberFormatException e) {
            return null;
        }
    }
    
    public static Double formatDouble(final String value, final Locale locale) {
        Double result = null;
        if (value != null) {
            NumberFormat formatter = null;
            if (locale != null) {
                formatter = NumberFormat.getInstance(locale);
            }
            else {
                formatter = NumberFormat.getInstance(Locale.getDefault());
            }
            final ParsePosition pos = new ParsePosition(0);
            final Number num = formatter.parse(value, pos);
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() && num.doubleValue() >= -1.7976931348623157E308 && num.doubleValue() <= Double.MAX_VALUE) {
                result = new Double(num.doubleValue());
            }
        }
        return result;
    }
    
    public static Date formatDate(final String value, final Locale locale) {
        Date date = null;
        if (value == null) {
            return null;
        }
        try {
            DateFormat formatter = null;
            if (locale != null) {
                formatter = DateFormat.getDateInstance(3, locale);
            }
            else {
                formatter = DateFormat.getDateInstance(3, Locale.getDefault());
            }
            formatter.setLenient(false);
            date = formatter.parse(value);
        }
        catch (final ParseException e) {
            final Log log = LogFactory.getLog(GenericTypeValidator.class);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Date parse failed value=[" + value + "], " + "locale=[" + locale + "] " + e));
            }
        }
        return date;
    }
    
    public static Date formatDate(final String value, final String datePattern, final boolean strict) {
        Date date = null;
        if (value == null || datePattern == null || datePattern.length() == 0) {
            return null;
        }
        try {
            final SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
            formatter.setLenient(false);
            date = formatter.parse(value);
            if (strict && datePattern.length() != value.length()) {
                date = null;
            }
        }
        catch (final ParseException e) {
            final Log log = LogFactory.getLog(GenericTypeValidator.class);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Date parse failed value=[" + value + "], " + "pattern=[" + datePattern + "], " + "strict=[" + strict + "] " + e));
            }
        }
        return date;
    }
    
    public static Long formatCreditCard(final String value) {
        return GenericValidator.isCreditCard(value) ? new Long(value) : null;
    }
}
