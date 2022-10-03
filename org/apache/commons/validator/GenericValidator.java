package org.apache.commons.validator;

import java.util.Locale;
import org.apache.oro.text.perl.Perl5Util;
import java.io.Serializable;

public class GenericValidator implements Serializable
{
    private static final UrlValidator URL_VALIDATOR;
    private static final CreditCardValidator CREDIT_CARD_VALIDATOR;
    
    public static boolean isBlankOrNull(final String value) {
        return value == null || value.trim().length() == 0;
    }
    
    public static boolean matchRegexp(final String value, final String regexp) {
        if (regexp == null || regexp.length() <= 0) {
            return false;
        }
        final Perl5Util matcher = new Perl5Util();
        return matcher.match("/" + regexp + "/", value);
    }
    
    public static boolean isByte(final String value) {
        return GenericTypeValidator.formatByte(value) != null;
    }
    
    public static boolean isShort(final String value) {
        return GenericTypeValidator.formatShort(value) != null;
    }
    
    public static boolean isInt(final String value) {
        return GenericTypeValidator.formatInt(value) != null;
    }
    
    public static boolean isLong(final String value) {
        return GenericTypeValidator.formatLong(value) != null;
    }
    
    public static boolean isFloat(final String value) {
        return GenericTypeValidator.formatFloat(value) != null;
    }
    
    public static boolean isDouble(final String value) {
        return GenericTypeValidator.formatDouble(value) != null;
    }
    
    public static boolean isDate(final String value, final Locale locale) {
        return DateValidator.getInstance().isValid(value, locale);
    }
    
    public static boolean isDate(final String value, final String datePattern, final boolean strict) {
        return DateValidator.getInstance().isValid(value, datePattern, strict);
    }
    
    public static boolean isInRange(final byte value, final byte min, final byte max) {
        return value >= min && value <= max;
    }
    
    public static boolean isInRange(final int value, final int min, final int max) {
        return value >= min && value <= max;
    }
    
    public static boolean isInRange(final float value, final float min, final float max) {
        return value >= min && value <= max;
    }
    
    public static boolean isInRange(final short value, final short min, final short max) {
        return value >= min && value <= max;
    }
    
    public static boolean isInRange(final long value, final long min, final long max) {
        return value >= min && value <= max;
    }
    
    public static boolean isInRange(final double value, final double min, final double max) {
        return value >= min && value <= max;
    }
    
    public static boolean isCreditCard(final String value) {
        return GenericValidator.CREDIT_CARD_VALIDATOR.isValid(value);
    }
    
    public static boolean isEmail(final String value) {
        return EmailValidator.getInstance().isValid(value);
    }
    
    public static boolean isUrl(final String value) {
        return GenericValidator.URL_VALIDATOR.isValid(value);
    }
    
    public static boolean maxLength(final String value, final int max) {
        return value.length() <= max;
    }
    
    public static boolean maxLength(final String value, final int max, final int lineEndLength) {
        final int adjustAmount = adjustForLineEnding(value, lineEndLength);
        return value.length() + adjustAmount <= max;
    }
    
    public static boolean minLength(final String value, final int min) {
        return value.length() >= min;
    }
    
    public static boolean minLength(final String value, final int min, final int lineEndLength) {
        final int adjustAmount = adjustForLineEnding(value, lineEndLength);
        return value.length() + adjustAmount >= min;
    }
    
    private static int adjustForLineEnding(final String value, final int lineEndLength) {
        int nCount = 0;
        int rCount = 0;
        for (int i = 0; i < value.length(); ++i) {
            if (value.charAt(i) == '\n') {
                ++nCount;
            }
            if (value.charAt(i) == '\r') {
                ++rCount;
            }
        }
        return nCount * lineEndLength - (rCount + nCount);
    }
    
    public static boolean minValue(final int value, final int min) {
        return value >= min;
    }
    
    public static boolean minValue(final long value, final long min) {
        return value >= min;
    }
    
    public static boolean minValue(final double value, final double min) {
        return value >= min;
    }
    
    public static boolean minValue(final float value, final float min) {
        return value >= min;
    }
    
    public static boolean maxValue(final int value, final int max) {
        return value <= max;
    }
    
    public static boolean maxValue(final long value, final long max) {
        return value <= max;
    }
    
    public static boolean maxValue(final double value, final double max) {
        return value <= max;
    }
    
    public static boolean maxValue(final float value, final float max) {
        return value <= max;
    }
    
    static {
        URL_VALIDATOR = new UrlValidator();
        CREDIT_CARD_VALIDATOR = new CreditCardValidator();
    }
}
