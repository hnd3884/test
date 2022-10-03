package com.unboundid.util;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;

final class TimestampValuePatternComponent extends ValuePatternComponent
{
    private static final long serialVersionUID = 9209358760604151565L;
    private final boolean expressAsGeneralizedTime;
    private final boolean expressAsMillisecondsSinceEpoch;
    private final boolean expressAsSecondsSinceEpoch;
    private final long boundRange;
    private final long lowerBound;
    private final Random seedRandom;
    private final String dateFormatString;
    private final ThreadLocal<Random> threadLocalRandoms;
    private final ThreadLocal<SimpleDateFormat> threadLocalDateFormatters;
    
    TimestampValuePatternComponent(final String pattern, final long randomSeed) throws ParseException {
        this.seedRandom = new Random(randomSeed);
        this.threadLocalRandoms = new ThreadLocal<Random>();
        this.threadLocalDateFormatters = new ThreadLocal<SimpleDateFormat>();
        if (pattern.equals("timestamp")) {
            this.expressAsGeneralizedTime = true;
            this.expressAsMillisecondsSinceEpoch = false;
            this.expressAsSecondsSinceEpoch = false;
            this.lowerBound = -1L;
            this.boundRange = -1L;
            this.dateFormatString = null;
            return;
        }
        if (pattern.startsWith("timestamp:min=")) {
            final int maxPos = pattern.indexOf(":max=");
            if (maxPos < 0) {
                throw new ParseException(UtilityMessages.ERR_TIMESTAMP_VALUE_PATTERN_MIN_WITHOUT_MAX.get(pattern), 10);
            }
            final int formatPos = pattern.indexOf(":format");
            if (formatPos > 0 && formatPos < maxPos) {
                throw new ParseException(UtilityMessages.ERR_TIMESTAMP_VALUE_PATTERN_FORMAT_NOT_AT_END.get(pattern), formatPos);
            }
            final String lowerBoundString = pattern.substring(14, maxPos);
            try {
                this.lowerBound = StaticUtils.decodeGeneralizedTime(lowerBoundString).getTime();
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new ParseException(UtilityMessages.ERR_TIMESTAMP_VALUE_PATTERN_CANNOT_PARSE_MIN.get(pattern, lowerBoundString, StaticUtils.getExceptionMessage(e)), 14);
            }
            if (formatPos < 0) {
                final String upperBoundString = pattern.substring(maxPos + 5);
                long upperBound;
                try {
                    upperBound = StaticUtils.decodeGeneralizedTime(upperBoundString).getTime();
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    throw new ParseException(UtilityMessages.ERR_TIMESTAMP_VALUE_PATTERN_CANNOT_PARSE_MAX.get(pattern, upperBoundString, StaticUtils.getExceptionMessage(e2)), maxPos + 5);
                }
                if (upperBound <= this.lowerBound) {
                    throw new ParseException(UtilityMessages.ERR_TIMESTAMP_VALUE_PATTERN_MIN_NOT_LT_MAX.get(pattern, lowerBoundString, upperBoundString), maxPos + 5);
                }
                this.boundRange = upperBound - this.lowerBound + 1L;
                this.expressAsGeneralizedTime = true;
                this.expressAsMillisecondsSinceEpoch = false;
                this.expressAsSecondsSinceEpoch = false;
                this.dateFormatString = null;
            }
            else {
                final String upperBoundString = pattern.substring(maxPos + 5, formatPos);
                long upperBound;
                try {
                    upperBound = StaticUtils.decodeGeneralizedTime(upperBoundString).getTime();
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    throw new ParseException(UtilityMessages.ERR_TIMESTAMP_VALUE_PATTERN_CANNOT_PARSE_MAX.get(pattern, upperBoundString, StaticUtils.getExceptionMessage(e2)), maxPos + 5);
                }
                if (upperBound <= this.lowerBound) {
                    throw new ParseException(UtilityMessages.ERR_TIMESTAMP_VALUE_PATTERN_MIN_NOT_LT_MAX.get(pattern, lowerBoundString, upperBoundString), maxPos + 5);
                }
                this.boundRange = upperBound - this.lowerBound + 1L;
                this.expressAsGeneralizedTime = false;
                final String formatString = pattern.substring(formatPos + 8);
                if (formatString.equals("milliseconds")) {
                    this.expressAsMillisecondsSinceEpoch = true;
                    this.expressAsSecondsSinceEpoch = false;
                    this.dateFormatString = null;
                }
                else if (formatString.equals("seconds")) {
                    this.expressAsMillisecondsSinceEpoch = false;
                    this.expressAsSecondsSinceEpoch = true;
                    this.dateFormatString = null;
                }
                else {
                    this.expressAsMillisecondsSinceEpoch = false;
                    this.expressAsSecondsSinceEpoch = false;
                    this.dateFormatString = formatString;
                    try {
                        new SimpleDateFormat(this.dateFormatString);
                    }
                    catch (final Exception e3) {
                        throw new ParseException(UtilityMessages.ERR_TIMESTAMP_VALUE_PATTERN_CANNOT_PARSE_FORMAT_STRING.get(pattern, this.dateFormatString), formatPos + 8);
                    }
                }
            }
        }
        else {
            if (!pattern.startsWith("timestamp:format=")) {
                throw new ParseException(UtilityMessages.ERR_TIMESTAMP_VALUE_PATTERN_MALFORMED.get(pattern), 0);
            }
            if (pattern.contains(":min=") || pattern.contains(":max=")) {
                throw new ParseException(UtilityMessages.ERR_TIMESTAMP_VALUE_PATTERN_FORMAT_NOT_AT_END.get(pattern), 17);
            }
            this.lowerBound = -1L;
            this.boundRange = -1L;
            this.expressAsGeneralizedTime = false;
            final String formatString2 = pattern.substring(17);
            if (formatString2.equals("milliseconds")) {
                this.expressAsMillisecondsSinceEpoch = true;
                this.expressAsSecondsSinceEpoch = false;
                this.dateFormatString = null;
            }
            else if (formatString2.equals("seconds")) {
                this.expressAsMillisecondsSinceEpoch = false;
                this.expressAsSecondsSinceEpoch = true;
                this.dateFormatString = null;
            }
            else {
                this.expressAsMillisecondsSinceEpoch = false;
                this.expressAsSecondsSinceEpoch = false;
                this.dateFormatString = formatString2;
                try {
                    new SimpleDateFormat(this.dateFormatString);
                }
                catch (final Exception e4) {
                    throw new ParseException(UtilityMessages.ERR_TIMESTAMP_VALUE_PATTERN_CANNOT_PARSE_FORMAT_STRING.get(pattern, this.dateFormatString), 17);
                }
            }
        }
    }
    
    @Override
    void append(final StringBuilder buffer) {
        long selectedTime;
        if (this.lowerBound == -1L) {
            selectedTime = System.currentTimeMillis();
        }
        else {
            final long positiveRandomValue = this.getRandom().nextLong() & Long.MAX_VALUE;
            selectedTime = this.lowerBound + positiveRandomValue % this.boundRange;
        }
        if (this.expressAsMillisecondsSinceEpoch) {
            buffer.append(selectedTime);
        }
        else if (this.expressAsSecondsSinceEpoch) {
            buffer.append(selectedTime / 1000L);
        }
        else if (this.expressAsGeneralizedTime) {
            buffer.append(StaticUtils.encodeGeneralizedTime(selectedTime));
        }
        else {
            buffer.append(this.getDateFormatter().format(new Date(selectedTime)));
        }
    }
    
    @Override
    boolean supportsBackReference() {
        return true;
    }
    
    private Random getRandom() {
        Random random = this.threadLocalRandoms.get();
        if (random == null) {
            synchronized (this.seedRandom) {
                random = new Random(this.seedRandom.nextLong());
            }
            this.threadLocalRandoms.set(random);
        }
        return random;
    }
    
    private SimpleDateFormat getDateFormatter() {
        SimpleDateFormat dateFormatter = this.threadLocalDateFormatters.get();
        if (dateFormatter == null) {
            dateFormatter = new SimpleDateFormat(this.dateFormatString);
            this.threadLocalDateFormatters.set(dateFormatter);
        }
        return dateFormatter;
    }
}
