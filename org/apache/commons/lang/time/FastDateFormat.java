package org.apache.commons.lang.time;

import java.util.HashMap;
import java.text.ParsePosition;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.text.DateFormatSymbols;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Map;
import java.text.Format;

public class FastDateFormat extends Format
{
    public static final int FULL = 0;
    public static final int LONG = 1;
    public static final int MEDIUM = 2;
    public static final int SHORT = 3;
    static final double LOG_10;
    private static String cDefaultPattern;
    private static Map cInstanceCache;
    private static Map cDateInstanceCache;
    private static Map cTimeInstanceCache;
    private static Map cDateTimeInstanceCache;
    private static Map cTimeZoneDisplayCache;
    private final String mPattern;
    private final TimeZone mTimeZone;
    private final boolean mTimeZoneForced;
    private final Locale mLocale;
    private final boolean mLocaleForced;
    private Rule[] mRules;
    private int mMaxLengthEstimate;
    
    public static FastDateFormat getInstance() {
        return getInstance(getDefaultPattern(), null, null);
    }
    
    public static FastDateFormat getInstance(final String pattern) {
        return getInstance(pattern, null, null);
    }
    
    public static FastDateFormat getInstance(final String pattern, final TimeZone timeZone) {
        return getInstance(pattern, timeZone, null);
    }
    
    public static FastDateFormat getInstance(final String pattern, final Locale locale) {
        return getInstance(pattern, null, locale);
    }
    
    public static synchronized FastDateFormat getInstance(final String pattern, final TimeZone timeZone, final Locale locale) {
        final FastDateFormat emptyFormat = new FastDateFormat(pattern, timeZone, locale);
        FastDateFormat format = FastDateFormat.cInstanceCache.get(emptyFormat);
        if (format == null) {
            format = emptyFormat;
            format.init();
            FastDateFormat.cInstanceCache.put(format, format);
        }
        return format;
    }
    
    public static synchronized FastDateFormat getDateInstance(final int style, final TimeZone timeZone, Locale locale) {
        Object key = new Integer(style);
        if (timeZone != null) {
            key = new Pair(key, timeZone);
        }
        if (locale == null) {
            key = new Pair(key, locale);
        }
        FastDateFormat format = FastDateFormat.cDateInstanceCache.get(key);
        if (format == null) {
            if (locale == null) {
                locale = Locale.getDefault();
            }
            try {
                final SimpleDateFormat formatter = (SimpleDateFormat)DateFormat.getDateInstance(style, locale);
                final String pattern = formatter.toPattern();
                format = getInstance(pattern, timeZone, locale);
                FastDateFormat.cDateInstanceCache.put(key, format);
            }
            catch (final ClassCastException ex) {
                throw new IllegalArgumentException("No date pattern for locale: " + locale);
            }
        }
        return format;
    }
    
    public static synchronized FastDateFormat getTimeInstance(final int style, final TimeZone timeZone, Locale locale) {
        Object key = new Integer(style);
        if (timeZone != null) {
            key = new Pair(key, timeZone);
        }
        if (locale != null) {
            key = new Pair(key, locale);
        }
        FastDateFormat format = FastDateFormat.cTimeInstanceCache.get(key);
        if (format == null) {
            if (locale == null) {
                locale = Locale.getDefault();
            }
            try {
                final SimpleDateFormat formatter = (SimpleDateFormat)DateFormat.getTimeInstance(style, locale);
                final String pattern = formatter.toPattern();
                format = getInstance(pattern, timeZone, locale);
                FastDateFormat.cTimeInstanceCache.put(key, format);
            }
            catch (final ClassCastException ex) {
                throw new IllegalArgumentException("No date pattern for locale: " + locale);
            }
        }
        return format;
    }
    
    public static synchronized FastDateFormat getDateTimeInstance(final int dateStyle, final int timeStyle, final TimeZone timeZone, Locale locale) {
        Object key = new Pair(new Integer(dateStyle), new Integer(timeStyle));
        if (timeZone != null) {
            key = new Pair(key, timeZone);
        }
        if (locale != null) {
            key = new Pair(key, locale);
        }
        FastDateFormat format = FastDateFormat.cDateTimeInstanceCache.get(key);
        if (format == null) {
            if (locale == null) {
                locale = Locale.getDefault();
            }
            try {
                final SimpleDateFormat formatter = (SimpleDateFormat)DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
                final String pattern = formatter.toPattern();
                format = getInstance(pattern, timeZone, locale);
                FastDateFormat.cDateTimeInstanceCache.put(key, format);
            }
            catch (final ClassCastException ex) {
                throw new IllegalArgumentException("No date time pattern for locale: " + locale);
            }
        }
        return format;
    }
    
    static synchronized String getTimeZoneDisplay(final TimeZone tz, final boolean daylight, final int style, final Locale locale) {
        final Object key = new TimeZoneDisplayKey(tz, daylight, style, locale);
        String value = FastDateFormat.cTimeZoneDisplayCache.get(key);
        if (value == null) {
            value = tz.getDisplayName(daylight, style, locale);
            FastDateFormat.cTimeZoneDisplayCache.put(key, value);
        }
        return value;
    }
    
    private static synchronized String getDefaultPattern() {
        if (FastDateFormat.cDefaultPattern == null) {
            FastDateFormat.cDefaultPattern = new SimpleDateFormat().toPattern();
        }
        return FastDateFormat.cDefaultPattern;
    }
    
    protected FastDateFormat(final String pattern, TimeZone timeZone, Locale locale) {
        if (pattern == null) {
            throw new IllegalArgumentException("The pattern must not be null");
        }
        this.mPattern = pattern;
        this.mTimeZoneForced = (timeZone != null);
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        this.mTimeZone = timeZone;
        this.mLocaleForced = (locale != null);
        if (locale == null) {
            locale = Locale.getDefault();
        }
        this.mLocale = locale;
    }
    
    protected void init() {
        final List rulesList = this.parsePattern();
        this.mRules = rulesList.toArray(new Rule[rulesList.size()]);
        int len = 0;
        int i = this.mRules.length;
        while (--i >= 0) {
            len += this.mRules[i].estimateLength();
        }
        this.mMaxLengthEstimate = len;
    }
    
    protected List parsePattern() {
        final DateFormatSymbols symbols = new DateFormatSymbols(this.mLocale);
        final List rules = new ArrayList();
        final String[] ERAs = symbols.getEras();
        final String[] months = symbols.getMonths();
        final String[] shortMonths = symbols.getShortMonths();
        final String[] weekdays = symbols.getWeekdays();
        final String[] shortWeekdays = symbols.getShortWeekdays();
        final String[] AmPmStrings = symbols.getAmPmStrings();
        final int length = this.mPattern.length();
        final int[] indexRef = { 0 };
        for (int i = 0; i < length; ++i) {
            indexRef[0] = i;
            final String token = this.parseToken(this.mPattern, indexRef);
            i = indexRef[0];
            final int tokenLen = token.length();
            if (tokenLen == 0) {
                break;
            }
            final char c = token.charAt(0);
            switch (c) {
                case 'G': {
                    final Rule rule = new TextField(0, ERAs);
                    break;
                }
                case 'y': {
                    if (tokenLen >= 4) {
                        final Rule rule = UnpaddedNumberField.INSTANCE_YEAR;
                        break;
                    }
                    final Rule rule = TwoDigitYearField.INSTANCE;
                    break;
                }
                case 'M': {
                    if (tokenLen >= 4) {
                        final Rule rule = new TextField(2, months);
                        break;
                    }
                    if (tokenLen == 3) {
                        final Rule rule = new TextField(2, shortMonths);
                        break;
                    }
                    if (tokenLen == 2) {
                        final Rule rule = TwoDigitMonthField.INSTANCE;
                        break;
                    }
                    final Rule rule = UnpaddedMonthField.INSTANCE;
                    break;
                }
                case 'd': {
                    final Rule rule = this.selectNumberRule(5, tokenLen);
                    break;
                }
                case 'h': {
                    final Rule rule = new TwelveHourField(this.selectNumberRule(10, tokenLen));
                    break;
                }
                case 'H': {
                    final Rule rule = this.selectNumberRule(11, tokenLen);
                    break;
                }
                case 'm': {
                    final Rule rule = this.selectNumberRule(12, tokenLen);
                    break;
                }
                case 's': {
                    final Rule rule = this.selectNumberRule(13, tokenLen);
                    break;
                }
                case 'S': {
                    final Rule rule = this.selectNumberRule(14, tokenLen);
                    break;
                }
                case 'E': {
                    final Rule rule = new TextField(7, (tokenLen < 4) ? shortWeekdays : weekdays);
                    break;
                }
                case 'D': {
                    final Rule rule = this.selectNumberRule(6, tokenLen);
                    break;
                }
                case 'F': {
                    final Rule rule = this.selectNumberRule(8, tokenLen);
                    break;
                }
                case 'w': {
                    final Rule rule = this.selectNumberRule(3, tokenLen);
                    break;
                }
                case 'W': {
                    final Rule rule = this.selectNumberRule(4, tokenLen);
                    break;
                }
                case 'a': {
                    final Rule rule = new TextField(9, AmPmStrings);
                    break;
                }
                case 'k': {
                    final Rule rule = new TwentyFourHourField(this.selectNumberRule(11, tokenLen));
                    break;
                }
                case 'K': {
                    final Rule rule = this.selectNumberRule(10, tokenLen);
                    break;
                }
                case 'z': {
                    if (tokenLen >= 4) {
                        final Rule rule = new TimeZoneNameRule(this.mTimeZone, this.mTimeZoneForced, this.mLocale, 1);
                        break;
                    }
                    final Rule rule = new TimeZoneNameRule(this.mTimeZone, this.mTimeZoneForced, this.mLocale, 0);
                    break;
                }
                case 'Z': {
                    if (tokenLen == 1) {
                        final Rule rule = TimeZoneNumberRule.INSTANCE_NO_COLON;
                        break;
                    }
                    final Rule rule = TimeZoneNumberRule.INSTANCE_COLON;
                    break;
                }
                case '\'': {
                    final String sub = token.substring(1);
                    if (sub.length() == 1) {
                        final Rule rule = new CharacterLiteral(sub.charAt(0));
                        break;
                    }
                    final Rule rule = new StringLiteral(sub);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Illegal pattern component: " + token);
                }
            }
            final Object o;
            rules.add(o);
        }
        return rules;
    }
    
    protected String parseToken(final String pattern, final int[] indexRef) {
        final StringBuffer buf = new StringBuffer();
        int i = indexRef[0];
        final int length = pattern.length();
        char c = pattern.charAt(i);
        if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
            buf.append(c);
            while (i + 1 < length) {
                final char peek = pattern.charAt(i + 1);
                if (peek != c) {
                    break;
                }
                buf.append(c);
                ++i;
            }
        }
        else {
            buf.append('\'');
            boolean inLiteral = false;
            while (i < length) {
                c = pattern.charAt(i);
                if (c == '\'') {
                    if (i + 1 < length && pattern.charAt(i + 1) == '\'') {
                        ++i;
                        buf.append(c);
                    }
                    else {
                        inLiteral = !inLiteral;
                    }
                }
                else {
                    if (!inLiteral && ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
                        --i;
                        break;
                    }
                    buf.append(c);
                }
                ++i;
            }
        }
        indexRef[0] = i;
        return buf.toString();
    }
    
    protected NumberRule selectNumberRule(final int field, final int padding) {
        switch (padding) {
            case 1: {
                return new UnpaddedNumberField(field);
            }
            case 2: {
                return new TwoDigitNumberField(field);
            }
            default: {
                return new PaddedNumberField(field, padding);
            }
        }
    }
    
    public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
        if (obj instanceof Date) {
            return this.format((Date)obj, toAppendTo);
        }
        if (obj instanceof Calendar) {
            return this.format((Calendar)obj, toAppendTo);
        }
        throw new IllegalArgumentException("Unknown class: " + ((obj == null) ? "<null>" : obj.getClass().getName()));
    }
    
    public String format(final Date date) {
        final Calendar c = new GregorianCalendar(this.mTimeZone);
        c.setTime(date);
        return this.applyRules(c, new StringBuffer(this.mMaxLengthEstimate)).toString();
    }
    
    public String format(final Calendar calendar) {
        return this.format(calendar, new StringBuffer(this.mMaxLengthEstimate)).toString();
    }
    
    public StringBuffer format(final Date date, final StringBuffer buf) {
        final Calendar c = new GregorianCalendar(this.mTimeZone);
        c.setTime(date);
        return this.applyRules(c, buf);
    }
    
    public StringBuffer format(Calendar calendar, final StringBuffer buf) {
        if (this.mTimeZoneForced) {
            calendar = (Calendar)calendar.clone();
            calendar.setTimeZone(this.mTimeZone);
        }
        return this.applyRules(calendar, buf);
    }
    
    protected StringBuffer applyRules(final Calendar calendar, final StringBuffer buf) {
        final Rule[] rules = this.mRules;
        for (int len = this.mRules.length, i = 0; i < len; ++i) {
            rules[i].appendTo(buf, calendar);
        }
        return buf;
    }
    
    public Object parseObject(final String source, final ParsePosition pos) {
        pos.setIndex(0);
        pos.setErrorIndex(0);
        return null;
    }
    
    public String getPattern() {
        return this.mPattern;
    }
    
    public TimeZone getTimeZone() {
        return this.mTimeZone;
    }
    
    public boolean getTimeZoneOverridesCalendar() {
        return this.mTimeZoneForced;
    }
    
    public Locale getLocale() {
        return this.mLocale;
    }
    
    public int getMaxLengthEstimate() {
        return this.mMaxLengthEstimate;
    }
    
    public boolean equals(final Object obj) {
        if (!(obj instanceof FastDateFormat)) {
            return false;
        }
        final FastDateFormat other = (FastDateFormat)obj;
        return (this.mPattern == other.mPattern || this.mPattern.equals(other.mPattern)) && (this.mTimeZone == other.mTimeZone || this.mTimeZone.equals(other.mTimeZone)) && (this.mLocale == other.mLocale || this.mLocale.equals(other.mLocale)) && this.mTimeZoneForced == other.mTimeZoneForced && this.mLocaleForced == other.mLocaleForced;
    }
    
    public int hashCode() {
        int total = 0;
        total += this.mPattern.hashCode();
        total += this.mTimeZone.hashCode();
        total += (this.mTimeZoneForced ? 1 : 0);
        total += this.mLocale.hashCode();
        total += (this.mLocaleForced ? 1 : 0);
        return total;
    }
    
    public String toString() {
        return "FastDateFormat[" + this.mPattern + "]";
    }
    
    static {
        LOG_10 = Math.log(10.0);
        FastDateFormat.cInstanceCache = new HashMap(7);
        FastDateFormat.cDateInstanceCache = new HashMap(7);
        FastDateFormat.cTimeInstanceCache = new HashMap(7);
        FastDateFormat.cDateTimeInstanceCache = new HashMap(7);
        FastDateFormat.cTimeZoneDisplayCache = new HashMap(7);
    }
    
    private static class CharacterLiteral implements Rule
    {
        private final char mValue;
        
        CharacterLiteral(final char value) {
            this.mValue = value;
        }
        
        public int estimateLength() {
            return 1;
        }
        
        public void appendTo(final StringBuffer buffer, final Calendar calendar) {
            buffer.append(this.mValue);
        }
    }
    
    private static class StringLiteral implements Rule
    {
        private final String mValue;
        
        StringLiteral(final String value) {
            this.mValue = value;
        }
        
        public int estimateLength() {
            return this.mValue.length();
        }
        
        public void appendTo(final StringBuffer buffer, final Calendar calendar) {
            buffer.append(this.mValue);
        }
    }
    
    private static class TextField implements Rule
    {
        private final int mField;
        private final String[] mValues;
        
        TextField(final int field, final String[] values) {
            this.mField = field;
            this.mValues = values;
        }
        
        public int estimateLength() {
            int max = 0;
            int i = this.mValues.length;
            while (--i >= 0) {
                final int len = this.mValues[i].length();
                if (len > max) {
                    max = len;
                }
            }
            return max;
        }
        
        public void appendTo(final StringBuffer buffer, final Calendar calendar) {
            buffer.append(this.mValues[calendar.get(this.mField)]);
        }
    }
    
    private static class UnpaddedNumberField implements NumberRule
    {
        static final UnpaddedNumberField INSTANCE_YEAR;
        private final int mField;
        
        UnpaddedNumberField(final int field) {
            this.mField = field;
        }
        
        public int estimateLength() {
            return 4;
        }
        
        public void appendTo(final StringBuffer buffer, final Calendar calendar) {
            this.appendTo(buffer, calendar.get(this.mField));
        }
        
        public final void appendTo(final StringBuffer buffer, final int value) {
            if (value < 10) {
                buffer.append((char)(value + 48));
            }
            else if (value < 100) {
                buffer.append((char)(value / 10 + 48));
                buffer.append((char)(value % 10 + 48));
            }
            else {
                buffer.append(Integer.toString(value));
            }
        }
        
        static {
            INSTANCE_YEAR = new UnpaddedNumberField(1);
        }
    }
    
    private static class UnpaddedMonthField implements NumberRule
    {
        static final UnpaddedMonthField INSTANCE;
        
        UnpaddedMonthField() {
        }
        
        public int estimateLength() {
            return 2;
        }
        
        public void appendTo(final StringBuffer buffer, final Calendar calendar) {
            this.appendTo(buffer, calendar.get(2) + 1);
        }
        
        public final void appendTo(final StringBuffer buffer, final int value) {
            if (value < 10) {
                buffer.append((char)(value + 48));
            }
            else {
                buffer.append((char)(value / 10 + 48));
                buffer.append((char)(value % 10 + 48));
            }
        }
        
        static {
            INSTANCE = new UnpaddedMonthField();
        }
    }
    
    private static class PaddedNumberField implements NumberRule
    {
        private final int mField;
        private final int mSize;
        
        PaddedNumberField(final int field, final int size) {
            if (size < 3) {
                throw new IllegalArgumentException();
            }
            this.mField = field;
            this.mSize = size;
        }
        
        public int estimateLength() {
            return 4;
        }
        
        public void appendTo(final StringBuffer buffer, final Calendar calendar) {
            this.appendTo(buffer, calendar.get(this.mField));
        }
        
        public final void appendTo(final StringBuffer buffer, final int value) {
            if (value < 100) {
                int i = this.mSize;
                while (--i >= 2) {
                    buffer.append('0');
                }
                buffer.append((char)(value / 10 + 48));
                buffer.append((char)(value % 10 + 48));
            }
            else {
                int digits;
                if (value < 1000) {
                    digits = 3;
                }
                else {
                    digits = (int)(Math.log(value) / FastDateFormat.LOG_10) + 1;
                }
                int j = this.mSize;
                while (--j >= digits) {
                    buffer.append('0');
                }
                buffer.append(Integer.toString(value));
            }
        }
    }
    
    private static class TwoDigitNumberField implements NumberRule
    {
        private final int mField;
        
        TwoDigitNumberField(final int field) {
            this.mField = field;
        }
        
        public int estimateLength() {
            return 2;
        }
        
        public void appendTo(final StringBuffer buffer, final Calendar calendar) {
            this.appendTo(buffer, calendar.get(this.mField));
        }
        
        public final void appendTo(final StringBuffer buffer, final int value) {
            if (value < 100) {
                buffer.append((char)(value / 10 + 48));
                buffer.append((char)(value % 10 + 48));
            }
            else {
                buffer.append(Integer.toString(value));
            }
        }
    }
    
    private static class TwoDigitYearField implements NumberRule
    {
        static final TwoDigitYearField INSTANCE;
        
        TwoDigitYearField() {
        }
        
        public int estimateLength() {
            return 2;
        }
        
        public void appendTo(final StringBuffer buffer, final Calendar calendar) {
            this.appendTo(buffer, calendar.get(1) % 100);
        }
        
        public final void appendTo(final StringBuffer buffer, final int value) {
            buffer.append((char)(value / 10 + 48));
            buffer.append((char)(value % 10 + 48));
        }
        
        static {
            INSTANCE = new TwoDigitYearField();
        }
    }
    
    private static class TwoDigitMonthField implements NumberRule
    {
        static final TwoDigitMonthField INSTANCE;
        
        TwoDigitMonthField() {
        }
        
        public int estimateLength() {
            return 2;
        }
        
        public void appendTo(final StringBuffer buffer, final Calendar calendar) {
            this.appendTo(buffer, calendar.get(2) + 1);
        }
        
        public final void appendTo(final StringBuffer buffer, final int value) {
            buffer.append((char)(value / 10 + 48));
            buffer.append((char)(value % 10 + 48));
        }
        
        static {
            INSTANCE = new TwoDigitMonthField();
        }
    }
    
    private static class TwelveHourField implements NumberRule
    {
        private final NumberRule mRule;
        
        TwelveHourField(final NumberRule rule) {
            this.mRule = rule;
        }
        
        public int estimateLength() {
            return this.mRule.estimateLength();
        }
        
        public void appendTo(final StringBuffer buffer, final Calendar calendar) {
            int value = calendar.get(10);
            if (value == 0) {
                value = calendar.getLeastMaximum(10) + 1;
            }
            this.mRule.appendTo(buffer, value);
        }
        
        public void appendTo(final StringBuffer buffer, final int value) {
            this.mRule.appendTo(buffer, value);
        }
    }
    
    private static class TwentyFourHourField implements NumberRule
    {
        private final NumberRule mRule;
        
        TwentyFourHourField(final NumberRule rule) {
            this.mRule = rule;
        }
        
        public int estimateLength() {
            return this.mRule.estimateLength();
        }
        
        public void appendTo(final StringBuffer buffer, final Calendar calendar) {
            int value = calendar.get(11);
            if (value == 0) {
                value = calendar.getMaximum(11) + 1;
            }
            this.mRule.appendTo(buffer, value);
        }
        
        public void appendTo(final StringBuffer buffer, final int value) {
            this.mRule.appendTo(buffer, value);
        }
    }
    
    private static class TimeZoneNameRule implements Rule
    {
        private final TimeZone mTimeZone;
        private final boolean mTimeZoneForced;
        private final Locale mLocale;
        private final int mStyle;
        private final String mStandard;
        private final String mDaylight;
        
        TimeZoneNameRule(final TimeZone timeZone, final boolean timeZoneForced, final Locale locale, final int style) {
            this.mTimeZone = timeZone;
            this.mTimeZoneForced = timeZoneForced;
            this.mLocale = locale;
            this.mStyle = style;
            if (timeZoneForced) {
                this.mStandard = FastDateFormat.getTimeZoneDisplay(timeZone, false, style, locale);
                this.mDaylight = FastDateFormat.getTimeZoneDisplay(timeZone, true, style, locale);
            }
            else {
                this.mStandard = null;
                this.mDaylight = null;
            }
        }
        
        public int estimateLength() {
            if (this.mTimeZoneForced) {
                return Math.max(this.mStandard.length(), this.mDaylight.length());
            }
            if (this.mStyle == 0) {
                return 4;
            }
            return 40;
        }
        
        public void appendTo(final StringBuffer buffer, final Calendar calendar) {
            if (this.mTimeZoneForced) {
                if (this.mTimeZone.useDaylightTime() && calendar.get(16) != 0) {
                    buffer.append(this.mDaylight);
                }
                else {
                    buffer.append(this.mStandard);
                }
            }
            else {
                final TimeZone timeZone = calendar.getTimeZone();
                if (timeZone.useDaylightTime() && calendar.get(16) != 0) {
                    buffer.append(FastDateFormat.getTimeZoneDisplay(timeZone, true, this.mStyle, this.mLocale));
                }
                else {
                    buffer.append(FastDateFormat.getTimeZoneDisplay(timeZone, false, this.mStyle, this.mLocale));
                }
            }
        }
    }
    
    private static class TimeZoneNumberRule implements Rule
    {
        static final TimeZoneNumberRule INSTANCE_COLON;
        static final TimeZoneNumberRule INSTANCE_NO_COLON;
        final boolean mColon;
        
        TimeZoneNumberRule(final boolean colon) {
            this.mColon = colon;
        }
        
        public int estimateLength() {
            return 5;
        }
        
        public void appendTo(final StringBuffer buffer, final Calendar calendar) {
            int offset = calendar.get(15) + calendar.get(16);
            if (offset < 0) {
                buffer.append('-');
                offset = -offset;
            }
            else {
                buffer.append('+');
            }
            final int hours = offset / 3600000;
            buffer.append((char)(hours / 10 + 48));
            buffer.append((char)(hours % 10 + 48));
            if (this.mColon) {
                buffer.append(':');
            }
            final int minutes = offset / 60000 - 60 * hours;
            buffer.append((char)(minutes / 10 + 48));
            buffer.append((char)(minutes % 10 + 48));
        }
        
        static {
            INSTANCE_COLON = new TimeZoneNumberRule(true);
            INSTANCE_NO_COLON = new TimeZoneNumberRule(false);
        }
    }
    
    private static class TimeZoneDisplayKey
    {
        private final TimeZone mTimeZone;
        private final int mStyle;
        private final Locale mLocale;
        
        TimeZoneDisplayKey(final TimeZone timeZone, final boolean daylight, int style, final Locale locale) {
            this.mTimeZone = timeZone;
            if (daylight) {
                style |= Integer.MIN_VALUE;
            }
            this.mStyle = style;
            this.mLocale = locale;
        }
        
        public int hashCode() {
            return this.mStyle * 31 + this.mLocale.hashCode();
        }
        
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof TimeZoneDisplayKey) {
                final TimeZoneDisplayKey other = (TimeZoneDisplayKey)obj;
                return this.mTimeZone.equals(other.mTimeZone) && this.mStyle == other.mStyle && this.mLocale.equals(other.mLocale);
            }
            return false;
        }
    }
    
    private static class Pair
    {
        private final Object mObj1;
        private final Object mObj2;
        
        public Pair(final Object obj1, final Object obj2) {
            this.mObj1 = obj1;
            this.mObj2 = obj2;
        }
        
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Pair)) {
                return false;
            }
            final Pair key = (Pair)obj;
            if (this.mObj1 == null) {
                if (key.mObj1 != null) {
                    return false;
                }
            }
            else if (!this.mObj1.equals(key.mObj1)) {
                return false;
            }
            if ((this.mObj2 != null) ? this.mObj2.equals(key.mObj2) : (key.mObj2 == null)) {
                return true;
            }
            return false;
        }
        
        public int hashCode() {
            return ((this.mObj1 == null) ? 0 : this.mObj1.hashCode()) + ((this.mObj2 == null) ? 0 : this.mObj2.hashCode());
        }
        
        public String toString() {
            return "[" + this.mObj1 + ':' + this.mObj2 + ']';
        }
    }
    
    private interface Rule
    {
        int estimateLength();
        
        void appendTo(final StringBuffer p0, final Calendar p1);
    }
    
    private interface NumberRule extends Rule
    {
        void appendTo(final StringBuffer p0, final int p1);
    }
}
