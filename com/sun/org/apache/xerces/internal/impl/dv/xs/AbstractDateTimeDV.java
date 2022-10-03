package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.xs.datatypes.XSDateTime;
import com.sun.org.apache.xerces.internal.jaxp.datatype.DatatypeFactoryImpl;
import java.math.BigDecimal;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeFactory;

public abstract class AbstractDateTimeDV extends TypeValidator
{
    private static final boolean DEBUG = false;
    protected static final int YEAR = 2000;
    protected static final int MONTH = 1;
    protected static final int DAY = 1;
    protected static final DatatypeFactory datatypeFactory;
    
    @Override
    public short getAllowedFacets() {
        return 2552;
    }
    
    @Override
    public boolean isIdentical(final Object value1, final Object value2) {
        if (!(value1 instanceof DateTimeData) || !(value2 instanceof DateTimeData)) {
            return false;
        }
        final DateTimeData v1 = (DateTimeData)value1;
        final DateTimeData v2 = (DateTimeData)value2;
        return v1.timezoneHr == v2.timezoneHr && v1.timezoneMin == v2.timezoneMin && v1.equals(v2);
    }
    
    @Override
    public int compare(final Object value1, final Object value2) {
        return this.compareDates((DateTimeData)value1, (DateTimeData)value2, true);
    }
    
    protected short compareDates(final DateTimeData date1, final DateTimeData date2, final boolean strict) {
        if (date1.utc == date2.utc) {
            return this.compareOrder(date1, date2);
        }
        final DateTimeData tempDate = new DateTimeData(null, this);
        if (date1.utc == 90) {
            this.cloneDate(date2, tempDate);
            tempDate.timezoneHr = 14;
            tempDate.timezoneMin = 0;
            tempDate.utc = 43;
            this.normalize(tempDate);
            final short c1 = this.compareOrder(date1, tempDate);
            if (c1 == -1) {
                return c1;
            }
            this.cloneDate(date2, tempDate);
            tempDate.timezoneHr = -14;
            tempDate.timezoneMin = 0;
            tempDate.utc = 45;
            this.normalize(tempDate);
            final short c2 = this.compareOrder(date1, tempDate);
            if (c2 == 1) {
                return c2;
            }
            return 2;
        }
        else {
            if (date2.utc != 90) {
                return 2;
            }
            this.cloneDate(date1, tempDate);
            tempDate.timezoneHr = -14;
            tempDate.timezoneMin = 0;
            tempDate.utc = 45;
            this.normalize(tempDate);
            final short c1 = this.compareOrder(tempDate, date2);
            if (c1 == -1) {
                return c1;
            }
            this.cloneDate(date1, tempDate);
            tempDate.timezoneHr = 14;
            tempDate.timezoneMin = 0;
            tempDate.utc = 43;
            this.normalize(tempDate);
            final short c2 = this.compareOrder(tempDate, date2);
            if (c2 == 1) {
                return c2;
            }
            return 2;
        }
    }
    
    protected short compareOrder(final DateTimeData date1, final DateTimeData date2) {
        if (date1.position < 1) {
            if (date1.year < date2.year) {
                return -1;
            }
            if (date1.year > date2.year) {
                return 1;
            }
        }
        if (date1.position < 2) {
            if (date1.month < date2.month) {
                return -1;
            }
            if (date1.month > date2.month) {
                return 1;
            }
        }
        if (date1.day < date2.day) {
            return -1;
        }
        if (date1.day > date2.day) {
            return 1;
        }
        if (date1.hour < date2.hour) {
            return -1;
        }
        if (date1.hour > date2.hour) {
            return 1;
        }
        if (date1.minute < date2.minute) {
            return -1;
        }
        if (date1.minute > date2.minute) {
            return 1;
        }
        if (date1.second < date2.second) {
            return -1;
        }
        if (date1.second > date2.second) {
            return 1;
        }
        if (date1.utc < date2.utc) {
            return -1;
        }
        if (date1.utc > date2.utc) {
            return 1;
        }
        return 0;
    }
    
    protected void getTime(final String buffer, int start, final int end, final DateTimeData data) throws RuntimeException {
        int stop = start + 2;
        data.hour = this.parseInt(buffer, start, stop);
        if (buffer.charAt(stop++) != ':') {
            throw new RuntimeException("Error in parsing time zone");
        }
        start = stop;
        stop += 2;
        data.minute = this.parseInt(buffer, start, stop);
        if (buffer.charAt(stop++) != ':') {
            throw new RuntimeException("Error in parsing time zone");
        }
        final int sign = this.findUTCSign(buffer, start, end);
        start = stop;
        stop = ((sign < 0) ? end : sign);
        data.second = this.parseSecond(buffer, start, stop);
        if (sign > 0) {
            this.getTimeZone(buffer, data, sign, end);
        }
    }
    
    protected int getDate(final String buffer, int start, final int end, final DateTimeData date) throws RuntimeException {
        start = this.getYearMonth(buffer, start, end, date);
        if (buffer.charAt(start++) != '-') {
            throw new RuntimeException("CCYY-MM must be followed by '-' sign");
        }
        final int stop = start + 2;
        date.day = this.parseInt(buffer, start, stop);
        return stop;
    }
    
    protected int getYearMonth(final String buffer, int start, final int end, final DateTimeData date) throws RuntimeException {
        if (buffer.charAt(0) == '-') {
            ++start;
        }
        int i = this.indexOf(buffer, start, end, '-');
        if (i == -1) {
            throw new RuntimeException("Year separator is missing or misplaced");
        }
        final int length = i - start;
        if (length < 4) {
            throw new RuntimeException("Year must have 'CCYY' format");
        }
        if (length > 4 && buffer.charAt(start) == '0') {
            throw new RuntimeException("Leading zeros are required if the year value would otherwise have fewer than four digits; otherwise they are forbidden");
        }
        date.year = this.parseIntYear(buffer, i);
        if (buffer.charAt(i) != '-') {
            throw new RuntimeException("CCYY must be followed by '-' sign");
        }
        start = ++i;
        i = start + 2;
        date.month = this.parseInt(buffer, start, i);
        return i;
    }
    
    protected void parseTimeZone(final String buffer, final int start, final int end, final DateTimeData date) throws RuntimeException {
        if (start < end) {
            if (!this.isNextCharUTCSign(buffer, start, end)) {
                throw new RuntimeException("Error in month parsing");
            }
            this.getTimeZone(buffer, date, start, end);
        }
    }
    
    protected void getTimeZone(final String buffer, final DateTimeData data, int sign, final int end) throws RuntimeException {
        data.utc = buffer.charAt(sign);
        if (buffer.charAt(sign) == 'Z') {
            if (end > ++sign) {
                throw new RuntimeException("Error in parsing time zone");
            }
        }
        else {
            if (sign > end - 6) {
                throw new RuntimeException("Error in parsing time zone");
            }
            final int negate = (buffer.charAt(sign) == '-') ? -1 : 1;
            int stop = ++sign + 2;
            data.timezoneHr = negate * this.parseInt(buffer, sign, stop);
            if (buffer.charAt(stop++) != ':') {
                throw new RuntimeException("Error in parsing time zone");
            }
            data.timezoneMin = negate * this.parseInt(buffer, stop, stop + 2);
            if (stop + 2 != end) {
                throw new RuntimeException("Error in parsing time zone");
            }
            if (data.timezoneHr != 0 || data.timezoneMin != 0) {
                data.normalized = false;
            }
        }
    }
    
    protected int indexOf(final String buffer, final int start, final int end, final char ch) {
        for (int i = start; i < end; ++i) {
            if (buffer.charAt(i) == ch) {
                return i;
            }
        }
        return -1;
    }
    
    protected void validateDateTime(final DateTimeData data) {
        if (data.year == 0) {
            throw new RuntimeException("The year \"0000\" is an illegal year value");
        }
        if (data.month < 1 || data.month > 12) {
            throw new RuntimeException("The month must have values 1 to 12");
        }
        if (data.day > this.maxDayInMonthFor(data.year, data.month) || data.day < 1) {
            throw new RuntimeException("The day must have values 1 to 31");
        }
        if (data.hour > 23 || data.hour < 0) {
            if (data.hour != 24 || data.minute != 0 || data.second != 0.0) {
                throw new RuntimeException("Hour must have values 0-23, unless 24:00:00");
            }
            data.hour = 0;
            if (++data.day > this.maxDayInMonthFor(data.year, data.month)) {
                data.day = 1;
                if (++data.month > 12) {
                    data.month = 1;
                    if (++data.year == 0) {
                        data.year = 1;
                    }
                }
            }
        }
        if (data.minute > 59 || data.minute < 0) {
            throw new RuntimeException("Minute must have values 0-59");
        }
        if (data.second >= 60.0 || data.second < 0.0) {
            throw new RuntimeException("Second must have values 0-59");
        }
        if (data.timezoneHr > 14 || data.timezoneHr < -14) {
            throw new RuntimeException("Time zone should have range -14:00 to +14:00");
        }
        if ((data.timezoneHr == 14 || data.timezoneHr == -14) && data.timezoneMin != 0) {
            throw new RuntimeException("Time zone should have range -14:00 to +14:00");
        }
        if (data.timezoneMin > 59 || data.timezoneMin < -59) {
            throw new RuntimeException("Minute must have values 0-59");
        }
    }
    
    protected int findUTCSign(final String buffer, final int start, final int end) {
        for (int i = start; i < end; ++i) {
            final int c = buffer.charAt(i);
            if (c == 90 || c == 43 || c == 45) {
                return i;
            }
        }
        return -1;
    }
    
    protected final boolean isNextCharUTCSign(final String buffer, final int start, final int end) {
        if (start < end) {
            final char c = buffer.charAt(start);
            return c == 'Z' || c == '+' || c == '-';
        }
        return false;
    }
    
    protected int parseInt(final String buffer, final int start, final int end) throws NumberFormatException {
        final int radix = 10;
        int result = 0;
        int digit = 0;
        final int limit = -2147483647;
        final int multmin = limit / radix;
        int i = start;
        do {
            digit = TypeValidator.getDigit(buffer.charAt(i));
            if (digit < 0) {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
            if (result < multmin) {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
            result *= radix;
            if (result < limit + digit) {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
            result -= digit;
        } while (++i < end);
        return -result;
    }
    
    protected int parseIntYear(final String buffer, final int end) {
        final int radix = 10;
        int result = 0;
        boolean negative = false;
        int i = 0;
        int digit = 0;
        int limit;
        if (buffer.charAt(0) == '-') {
            negative = true;
            limit = Integer.MIN_VALUE;
            ++i;
        }
        else {
            limit = -2147483647;
        }
        final int multmin = limit / radix;
        while (i < end) {
            digit = TypeValidator.getDigit(buffer.charAt(i++));
            if (digit < 0) {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
            if (result < multmin) {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
            result *= radix;
            if (result < limit + digit) {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
            result -= digit;
        }
        if (!negative) {
            return -result;
        }
        if (i > 1) {
            return result;
        }
        throw new NumberFormatException("'" + buffer + "' has wrong format");
    }
    
    protected void normalize(final DateTimeData date) {
        final int negate = -1;
        int temp = date.minute + negate * date.timezoneMin;
        int carry = this.fQuotient(temp, 60);
        date.minute = this.mod(temp, 60, carry);
        temp = date.hour + negate * date.timezoneHr + carry;
        carry = this.fQuotient(temp, 24);
        date.hour = this.mod(temp, 24, carry);
        date.day += carry;
        while (true) {
            temp = this.maxDayInMonthFor(date.year, date.month);
            if (date.day < 1) {
                date.day += this.maxDayInMonthFor(date.year, date.month - 1);
                carry = -1;
            }
            else {
                if (date.day <= temp) {
                    break;
                }
                date.day -= temp;
                carry = 1;
            }
            temp = date.month + carry;
            date.month = this.modulo(temp, 1, 13);
            date.year += this.fQuotient(temp, 1, 13);
            if (date.year == 0) {
                date.year = ((date.timezoneHr < 0 || date.timezoneMin < 0) ? 1 : -1);
            }
        }
        date.utc = 90;
    }
    
    protected void saveUnnormalized(final DateTimeData date) {
        date.unNormYear = date.year;
        date.unNormMonth = date.month;
        date.unNormDay = date.day;
        date.unNormHour = date.hour;
        date.unNormMinute = date.minute;
        date.unNormSecond = date.second;
    }
    
    protected void resetDateObj(final DateTimeData data) {
        data.year = 0;
        data.month = 0;
        data.day = 0;
        data.hour = 0;
        data.minute = 0;
        data.second = 0.0;
        data.utc = 0;
        data.timezoneHr = 0;
        data.timezoneMin = 0;
    }
    
    protected int maxDayInMonthFor(final int year, final int month) {
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        }
        if (month != 2) {
            return 31;
        }
        if (this.isLeapYear(year)) {
            return 29;
        }
        return 28;
    }
    
    private boolean isLeapYear(final int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }
    
    protected int mod(final int a, final int b, final int quotient) {
        return a - quotient * b;
    }
    
    protected int fQuotient(final int a, final int b) {
        return (int)Math.floor(a / (float)b);
    }
    
    protected int modulo(final int temp, final int low, final int high) {
        final int a = temp - low;
        final int b = high - low;
        return this.mod(a, b, this.fQuotient(a, b)) + low;
    }
    
    protected int fQuotient(final int temp, final int low, final int high) {
        return this.fQuotient(temp - low, high - low);
    }
    
    protected String dateToString(final DateTimeData date) {
        final StringBuffer message = new StringBuffer(25);
        this.append(message, date.year, 4);
        message.append('-');
        this.append(message, date.month, 2);
        message.append('-');
        this.append(message, date.day, 2);
        message.append('T');
        this.append(message, date.hour, 2);
        message.append(':');
        this.append(message, date.minute, 2);
        message.append(':');
        this.append(message, date.second);
        this.append(message, (char)date.utc, 0);
        return message.toString();
    }
    
    protected final void append(final StringBuffer message, int value, final int nch) {
        if (value == Integer.MIN_VALUE) {
            message.append(value);
            return;
        }
        if (value < 0) {
            message.append('-');
            value = -value;
        }
        if (nch == 4) {
            if (value < 10) {
                message.append("000");
            }
            else if (value < 100) {
                message.append("00");
            }
            else if (value < 1000) {
                message.append('0');
            }
            message.append(value);
        }
        else if (nch == 2) {
            if (value < 10) {
                message.append('0');
            }
            message.append(value);
        }
        else if (value != 0) {
            message.append((char)value);
        }
    }
    
    protected final void append(final StringBuffer message, double value) {
        if (value < 0.0) {
            message.append('-');
            value = -value;
        }
        if (value < 10.0) {
            message.append('0');
        }
        this.append2(message, value);
    }
    
    protected final void append2(final StringBuffer message, final double value) {
        final int intValue = (int)value;
        if (value == intValue) {
            message.append(intValue);
        }
        else {
            this.append3(message, value);
        }
    }
    
    private void append3(final StringBuffer message, final double value) {
        final String d = String.valueOf(value);
        final int eIndex = d.indexOf(69);
        if (eIndex == -1) {
            message.append(d);
            return;
        }
        if (value < 1.0) {
            int exp;
            try {
                exp = this.parseInt(d, eIndex + 2, d.length());
            }
            catch (final Exception e) {
                message.append(d);
                return;
            }
            message.append("0.");
            for (int i = 1; i < exp; ++i) {
                message.append('0');
            }
            int end;
            for (end = eIndex - 1; end > 0; --end) {
                final char c = d.charAt(end);
                if (c != '0') {
                    break;
                }
            }
            for (int j = 0; j <= end; ++j) {
                final char c2 = d.charAt(j);
                if (c2 != '.') {
                    message.append(c2);
                }
            }
        }
        else {
            int exp;
            try {
                exp = this.parseInt(d, eIndex + 1, d.length());
            }
            catch (final Exception e) {
                message.append(d);
                return;
            }
            final int integerEnd = exp + 2;
            for (int j = 0; j < eIndex; ++j) {
                final char c2 = d.charAt(j);
                if (c2 != '.') {
                    if (j == integerEnd) {
                        message.append('.');
                    }
                    message.append(c2);
                }
            }
            for (int j = integerEnd - eIndex; j > 0; --j) {
                message.append('0');
            }
        }
    }
    
    protected double parseSecond(final String buffer, final int start, final int end) throws NumberFormatException {
        int dot = -1;
        for (int i = start; i < end; ++i) {
            final char ch = buffer.charAt(i);
            if (ch == '.') {
                dot = i;
            }
            else if (ch > '9' || ch < '0') {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
        }
        if (dot == -1) {
            if (start + 2 != end) {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
        }
        else if (start + 2 != dot || dot + 1 == end) {
            throw new NumberFormatException("'" + buffer + "' has wrong format");
        }
        return Double.parseDouble(buffer.substring(start, end));
    }
    
    private void cloneDate(final DateTimeData finalValue, final DateTimeData tempDate) {
        tempDate.year = finalValue.year;
        tempDate.month = finalValue.month;
        tempDate.day = finalValue.day;
        tempDate.hour = finalValue.hour;
        tempDate.minute = finalValue.minute;
        tempDate.second = finalValue.second;
        tempDate.utc = finalValue.utc;
        tempDate.timezoneHr = finalValue.timezoneHr;
        tempDate.timezoneMin = finalValue.timezoneMin;
    }
    
    protected XMLGregorianCalendar getXMLGregorianCalendar(final DateTimeData data) {
        return null;
    }
    
    protected Duration getDuration(final DateTimeData data) {
        return null;
    }
    
    protected final BigDecimal getFractionalSecondsAsBigDecimal(final DateTimeData data) {
        final StringBuffer buf = new StringBuffer();
        this.append3(buf, data.unNormSecond);
        String value = buf.toString();
        final int index = value.indexOf(46);
        if (index == -1) {
            return null;
        }
        value = value.substring(index);
        final BigDecimal _val = new BigDecimal(value);
        if (_val.compareTo(BigDecimal.valueOf(0L)) == 0) {
            return null;
        }
        return _val;
    }
    
    static {
        datatypeFactory = new DatatypeFactoryImpl();
    }
    
    static final class DateTimeData implements XSDateTime
    {
        int year;
        int month;
        int day;
        int hour;
        int minute;
        int utc;
        double second;
        int timezoneHr;
        int timezoneMin;
        private String originalValue;
        boolean normalized;
        int unNormYear;
        int unNormMonth;
        int unNormDay;
        int unNormHour;
        int unNormMinute;
        double unNormSecond;
        int position;
        final AbstractDateTimeDV type;
        private volatile String canonical;
        
        public DateTimeData(final String originalValue, final AbstractDateTimeDV type) {
            this.normalized = true;
            this.originalValue = originalValue;
            this.type = type;
        }
        
        public DateTimeData(final int year, final int month, final int day, final int hour, final int minute, final double second, final int utc, final String originalValue, final boolean normalized, final AbstractDateTimeDV type) {
            this.normalized = true;
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
            this.second = second;
            this.utc = utc;
            this.type = type;
            this.originalValue = originalValue;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof DateTimeData && this.type.compareDates(this, (DateTimeData)obj, true) == 0;
        }
        
        @Override
        public int hashCode() {
            final DateTimeData tempDate = new DateTimeData(null, this.type);
            this.type.cloneDate(this, tempDate);
            this.type.normalize(tempDate);
            return this.type.dateToString(tempDate).hashCode();
        }
        
        @Override
        public String toString() {
            if (this.canonical == null) {
                this.canonical = this.type.dateToString(this);
            }
            return this.canonical;
        }
        
        @Override
        public int getYears() {
            if (this.type instanceof DurationDV) {
                return 0;
            }
            return this.normalized ? this.year : this.unNormYear;
        }
        
        @Override
        public int getMonths() {
            if (this.type instanceof DurationDV) {
                return this.year * 12 + this.month;
            }
            return this.normalized ? this.month : this.unNormMonth;
        }
        
        @Override
        public int getDays() {
            if (this.type instanceof DurationDV) {
                return 0;
            }
            return this.normalized ? this.day : this.unNormDay;
        }
        
        @Override
        public int getHours() {
            if (this.type instanceof DurationDV) {
                return 0;
            }
            return this.normalized ? this.hour : this.unNormHour;
        }
        
        @Override
        public int getMinutes() {
            if (this.type instanceof DurationDV) {
                return 0;
            }
            return this.normalized ? this.minute : this.unNormMinute;
        }
        
        @Override
        public double getSeconds() {
            if (this.type instanceof DurationDV) {
                return this.day * 24 * 60 * 60 + this.hour * 60 * 60 + this.minute * 60 + this.second;
            }
            return this.normalized ? this.second : this.unNormSecond;
        }
        
        @Override
        public boolean hasTimeZone() {
            return this.utc != 0;
        }
        
        @Override
        public int getTimeZoneHours() {
            return this.timezoneHr;
        }
        
        @Override
        public int getTimeZoneMinutes() {
            return this.timezoneMin;
        }
        
        @Override
        public String getLexicalValue() {
            return this.originalValue;
        }
        
        @Override
        public XSDateTime normalize() {
            if (!this.normalized) {
                final DateTimeData dt = (DateTimeData)this.clone();
                dt.normalized = true;
                return dt;
            }
            return this;
        }
        
        @Override
        public boolean isNormalized() {
            return this.normalized;
        }
        
        public Object clone() {
            final DateTimeData dt = new DateTimeData(this.year, this.month, this.day, this.hour, this.minute, this.second, this.utc, this.originalValue, this.normalized, this.type);
            dt.canonical = this.canonical;
            dt.position = this.position;
            dt.timezoneHr = this.timezoneHr;
            dt.timezoneMin = this.timezoneMin;
            dt.unNormYear = this.unNormYear;
            dt.unNormMonth = this.unNormMonth;
            dt.unNormDay = this.unNormDay;
            dt.unNormHour = this.unNormHour;
            dt.unNormMinute = this.unNormMinute;
            dt.unNormSecond = this.unNormSecond;
            return dt;
        }
        
        @Override
        public XMLGregorianCalendar getXMLGregorianCalendar() {
            return this.type.getXMLGregorianCalendar(this);
        }
        
        @Override
        public Duration getDuration() {
            return this.type.getDuration(this);
        }
    }
}
