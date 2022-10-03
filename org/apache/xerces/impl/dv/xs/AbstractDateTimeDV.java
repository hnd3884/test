package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.xs.datatypes.XSDateTime;
import org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl;
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
    
    public boolean isIdentical(final Object o, final Object o2) {
        if (!(o instanceof DateTimeData) || !(o2 instanceof DateTimeData)) {
            return false;
        }
        final DateTimeData dateTimeData = (DateTimeData)o;
        final DateTimeData dateTimeData2 = (DateTimeData)o2;
        return dateTimeData.timezoneHr == dateTimeData2.timezoneHr && dateTimeData.timezoneMin == dateTimeData2.timezoneMin && dateTimeData.equals(dateTimeData2);
    }
    
    public int compare(final Object o, final Object o2) {
        return this.compareDates((DateTimeData)o, (DateTimeData)o2, true);
    }
    
    public boolean hasTimeZone(final Object o) {
        return ((DateTimeData)o).hasTimeZone();
    }
    
    protected short compareDates(final DateTimeData dateTimeData, final DateTimeData dateTimeData2, final boolean b) {
        if (dateTimeData.utc == dateTimeData2.utc) {
            return this.compareOrder(dateTimeData, dateTimeData2);
        }
        final DateTimeData dateTimeData3 = new DateTimeData(null, this);
        if (dateTimeData.utc == 90) {
            this.cloneDate(dateTimeData2, dateTimeData3);
            dateTimeData3.timezoneHr = 14;
            dateTimeData3.timezoneMin = 0;
            dateTimeData3.utc = 43;
            this.normalize(dateTimeData3);
            final short compareOrder = this.compareOrder(dateTimeData, dateTimeData3);
            if (compareOrder == -1) {
                return compareOrder;
            }
            this.cloneDate(dateTimeData2, dateTimeData3);
            dateTimeData3.timezoneHr = -14;
            dateTimeData3.timezoneMin = 0;
            dateTimeData3.utc = 45;
            this.normalize(dateTimeData3);
            final short compareOrder2 = this.compareOrder(dateTimeData, dateTimeData3);
            if (compareOrder2 == 1) {
                return compareOrder2;
            }
            return 2;
        }
        else {
            if (dateTimeData2.utc != 90) {
                return 2;
            }
            this.cloneDate(dateTimeData, dateTimeData3);
            dateTimeData3.timezoneHr = -14;
            dateTimeData3.timezoneMin = 0;
            dateTimeData3.utc = 45;
            this.normalize(dateTimeData3);
            final short compareOrder3 = this.compareOrder(dateTimeData3, dateTimeData2);
            if (compareOrder3 == -1) {
                return compareOrder3;
            }
            this.cloneDate(dateTimeData, dateTimeData3);
            dateTimeData3.timezoneHr = 14;
            dateTimeData3.timezoneMin = 0;
            dateTimeData3.utc = 43;
            this.normalize(dateTimeData3);
            final short compareOrder4 = this.compareOrder(dateTimeData3, dateTimeData2);
            if (compareOrder4 == 1) {
                return compareOrder4;
            }
            return 2;
        }
    }
    
    protected short compareOrder(final DateTimeData dateTimeData, final DateTimeData dateTimeData2) {
        if (dateTimeData.position < 1) {
            if (dateTimeData.year < dateTimeData2.year) {
                return -1;
            }
            if (dateTimeData.year > dateTimeData2.year) {
                return 1;
            }
        }
        if (dateTimeData.position < 2) {
            if (dateTimeData.month < dateTimeData2.month) {
                return -1;
            }
            if (dateTimeData.month > dateTimeData2.month) {
                return 1;
            }
        }
        if (dateTimeData.day < dateTimeData2.day) {
            return -1;
        }
        if (dateTimeData.day > dateTimeData2.day) {
            return 1;
        }
        if (dateTimeData.hour < dateTimeData2.hour) {
            return -1;
        }
        if (dateTimeData.hour > dateTimeData2.hour) {
            return 1;
        }
        if (dateTimeData.minute < dateTimeData2.minute) {
            return -1;
        }
        if (dateTimeData.minute > dateTimeData2.minute) {
            return 1;
        }
        if (dateTimeData.second < dateTimeData2.second) {
            return -1;
        }
        if (dateTimeData.second > dateTimeData2.second) {
            return 1;
        }
        if (dateTimeData.utc < dateTimeData2.utc) {
            return -1;
        }
        if (dateTimeData.utc > dateTimeData2.utc) {
            return 1;
        }
        return 0;
    }
    
    protected void getTime(final String s, int n, final int n2, final DateTimeData dateTimeData) throws RuntimeException {
        int n3 = n + 2;
        dateTimeData.hour = this.parseInt(s, n, n3);
        if (s.charAt(n3++) != ':') {
            throw new RuntimeException("Error in parsing time zone");
        }
        n = n3;
        int n4 = n3 + 2;
        dateTimeData.minute = this.parseInt(s, n, n4);
        if (s.charAt(n4++) != ':') {
            throw new RuntimeException("Error in parsing time zone");
        }
        final int utcSign = this.findUTCSign(s, n, n2);
        n = n4;
        dateTimeData.second = this.parseSecond(s, n, (utcSign < 0) ? n2 : utcSign);
        if (utcSign > 0) {
            this.getTimeZone(s, dateTimeData, utcSign, n2);
        }
    }
    
    protected int getDate(final String s, int yearMonth, final int n, final DateTimeData dateTimeData) throws RuntimeException {
        yearMonth = this.getYearMonth(s, yearMonth, n, dateTimeData);
        if (s.charAt(yearMonth++) != '-') {
            throw new RuntimeException("CCYY-MM must be followed by '-' sign");
        }
        final int n2 = yearMonth + 2;
        dateTimeData.day = this.parseInt(s, yearMonth, n2);
        return n2;
    }
    
    protected int getYearMonth(final String s, int n, final int n2, final DateTimeData dateTimeData) throws RuntimeException {
        if (s.charAt(0) == '-') {
            ++n;
        }
        int index = this.indexOf(s, n, n2, '-');
        if (index == -1) {
            throw new RuntimeException("Year separator is missing or misplaced");
        }
        final int n3 = index - n;
        if (n3 < 4) {
            throw new RuntimeException("Year must have 'CCYY' format");
        }
        if (n3 > 4 && s.charAt(n) == '0') {
            throw new RuntimeException("Leading zeros are required if the year value would otherwise have fewer than four digits; otherwise they are forbidden");
        }
        dateTimeData.year = this.parseIntYear(s, index);
        if (s.charAt(index) != '-') {
            throw new RuntimeException("CCYY must be followed by '-' sign");
        }
        n = ++index;
        final int n4 = n + 2;
        dateTimeData.month = this.parseInt(s, n, n4);
        return n4;
    }
    
    protected void parseTimeZone(final String s, final int n, final int n2, final DateTimeData dateTimeData) throws RuntimeException {
        if (n < n2) {
            if (!this.isNextCharUTCSign(s, n, n2)) {
                throw new RuntimeException("Error in month parsing");
            }
            this.getTimeZone(s, dateTimeData, n, n2);
        }
    }
    
    protected void getTimeZone(final String s, final DateTimeData dateTimeData, int n, final int n2) throws RuntimeException {
        dateTimeData.utc = s.charAt(n);
        if (s.charAt(n) == 'Z') {
            if (n2 > ++n) {
                throw new RuntimeException("Error in parsing time zone");
            }
        }
        else {
            if (n > n2 - 6) {
                throw new RuntimeException("Error in parsing time zone");
            }
            final int n3 = (s.charAt(n) == '-') ? -1 : 1;
            int n4 = ++n + 2;
            dateTimeData.timezoneHr = n3 * this.parseInt(s, n, n4);
            if (s.charAt(n4++) != ':') {
                throw new RuntimeException("Error in parsing time zone");
            }
            dateTimeData.timezoneMin = n3 * this.parseInt(s, n4, n4 + 2);
            if (n4 + 2 != n2) {
                throw new RuntimeException("Error in parsing time zone");
            }
            if (dateTimeData.timezoneHr != 0 || dateTimeData.timezoneMin != 0) {
                dateTimeData.normalized = false;
            }
        }
    }
    
    protected int indexOf(final String s, final int n, final int n2, final char c) {
        for (int i = n; i < n2; ++i) {
            if (s.charAt(i) == c) {
                return i;
            }
        }
        return -1;
    }
    
    protected void validateDateTime(final DateTimeData dateTimeData, final boolean b) {
        if (!b && dateTimeData.year == 0) {
            throw new RuntimeException("The year \"0000\" is an illegal year value");
        }
        if (dateTimeData.month < 1 || dateTimeData.month > 12) {
            throw new RuntimeException("The month must have values 1 to 12");
        }
        if (dateTimeData.day > this.maxDayInMonthFor(dateTimeData.year, dateTimeData.month) || dateTimeData.day < 1) {
            throw new RuntimeException("The day must have values 1 to 31");
        }
        if (dateTimeData.hour > 23 || dateTimeData.hour < 0) {
            if (dateTimeData.hour != 24 || dateTimeData.minute != 0 || dateTimeData.second != 0.0) {
                throw new RuntimeException("Hour must have values 0-23, unless 24:00:00");
            }
            dateTimeData.hour = 0;
            if (++dateTimeData.day > this.maxDayInMonthFor(dateTimeData.year, dateTimeData.month)) {
                dateTimeData.day = 1;
                if (++dateTimeData.month > 12) {
                    dateTimeData.month = 1;
                    if (++dateTimeData.year == 0) {
                        dateTimeData.year = 1;
                    }
                }
            }
        }
        if (dateTimeData.minute > 59 || dateTimeData.minute < 0) {
            throw new RuntimeException("Minute must have values 0-59");
        }
        if (dateTimeData.second >= 60.0 || dateTimeData.second < 0.0) {
            throw new RuntimeException("Second must have values 0-59");
        }
        if (dateTimeData.timezoneHr > 14 || dateTimeData.timezoneHr < -14) {
            throw new RuntimeException("Time zone should have range -14:00 to +14:00");
        }
        if ((dateTimeData.timezoneHr == 14 || dateTimeData.timezoneHr == -14) && dateTimeData.timezoneMin != 0) {
            throw new RuntimeException("Time zone should have range -14:00 to +14:00");
        }
        if (dateTimeData.timezoneMin > 59 || dateTimeData.timezoneMin < -59) {
            throw new RuntimeException("Minute must have values 0-59");
        }
    }
    
    protected int findUTCSign(final String s, final int n, final int n2) {
        for (int i = n; i < n2; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == 'Z' || char1 == '+' || char1 == '-') {
                return i;
            }
        }
        return -1;
    }
    
    protected final boolean isNextCharUTCSign(final String s, final int n, final int n2) {
        if (n < n2) {
            final char char1 = s.charAt(n);
            return char1 == 'Z' || char1 == '+' || char1 == '-';
        }
        return false;
    }
    
    protected int parseInt(final String s, final int n, final int n2) throws NumberFormatException {
        final int n3 = 10;
        int n4 = 0;
        final int n5 = -2147483647;
        final int n6 = n5 / n3;
        int n7 = n;
        do {
            final int digit = TypeValidator.getDigit(s.charAt(n7));
            if (digit < 0) {
                throw new NumberFormatException("'" + s + "' has wrong format");
            }
            if (n4 < n6) {
                throw new NumberFormatException("'" + s + "' has wrong format");
            }
            final int n8 = n4 * n3;
            if (n8 < n5 + digit) {
                throw new NumberFormatException("'" + s + "' has wrong format");
            }
            n4 = n8 - digit;
        } while (++n7 < n2);
        return -n4;
    }
    
    protected int parseIntYear(final String s, final int n) {
        final int n2 = 10;
        int n3 = 0;
        boolean b = false;
        int i = 0;
        int n4;
        if (s.charAt(0) == '-') {
            b = true;
            n4 = Integer.MIN_VALUE;
            ++i;
        }
        else {
            n4 = -2147483647;
        }
        final int n5 = n4 / n2;
        while (i < n) {
            final int digit = TypeValidator.getDigit(s.charAt(i++));
            if (digit < 0) {
                throw new NumberFormatException("'" + s + "' has wrong format");
            }
            if (n3 < n5) {
                throw new NumberFormatException("'" + s + "' has wrong format");
            }
            final int n6 = n3 * n2;
            if (n6 < n4 + digit) {
                throw new NumberFormatException("'" + s + "' has wrong format");
            }
            n3 = n6 - digit;
        }
        if (!b) {
            return -n3;
        }
        if (i > 1) {
            return n3;
        }
        throw new NumberFormatException("'" + s + "' has wrong format");
    }
    
    protected void normalize(final DateTimeData dateTimeData) {
        final int n = -1;
        final int n2 = dateTimeData.minute + n * dateTimeData.timezoneMin;
        final int fQuotient = this.fQuotient(n2, 60);
        dateTimeData.minute = this.mod(n2, 60, fQuotient);
        final int n3 = dateTimeData.hour + n * dateTimeData.timezoneHr + fQuotient;
        final int fQuotient2 = this.fQuotient(n3, 24);
        dateTimeData.hour = this.mod(n3, 24, fQuotient2);
        dateTimeData.day += fQuotient2;
        while (true) {
            final int maxDayInMonth = this.maxDayInMonthFor(dateTimeData.year, dateTimeData.month);
            int n4;
            if (dateTimeData.day < 1) {
                dateTimeData.day += this.maxDayInMonthFor(dateTimeData.year, dateTimeData.month - 1);
                n4 = -1;
            }
            else {
                if (dateTimeData.day <= maxDayInMonth) {
                    break;
                }
                dateTimeData.day -= maxDayInMonth;
                n4 = 1;
            }
            final int n5 = dateTimeData.month + n4;
            dateTimeData.month = this.modulo(n5, 1, 13);
            dateTimeData.year += this.fQuotient(n5, 1, 13);
            if (dateTimeData.year == 0) {
                dateTimeData.year = ((dateTimeData.timezoneHr < 0 || dateTimeData.timezoneMin < 0) ? 1 : -1);
            }
        }
        dateTimeData.utc = 90;
    }
    
    protected void saveUnnormalized(final DateTimeData dateTimeData) {
        dateTimeData.unNormYear = dateTimeData.year;
        dateTimeData.unNormMonth = dateTimeData.month;
        dateTimeData.unNormDay = dateTimeData.day;
        dateTimeData.unNormHour = dateTimeData.hour;
        dateTimeData.unNormMinute = dateTimeData.minute;
        dateTimeData.unNormSecond = dateTimeData.second;
    }
    
    protected void resetDateObj(final DateTimeData dateTimeData) {
        dateTimeData.year = 0;
        dateTimeData.month = 0;
        dateTimeData.day = 0;
        dateTimeData.hour = 0;
        dateTimeData.minute = 0;
        dateTimeData.second = 0.0;
        dateTimeData.utc = 0;
        dateTimeData.timezoneHr = 0;
        dateTimeData.timezoneMin = 0;
    }
    
    protected int maxDayInMonthFor(final int n, final int n2) {
        if (n2 == 4 || n2 == 6 || n2 == 9 || n2 == 11) {
            return 30;
        }
        if (n2 != 2) {
            return 31;
        }
        if (this.isLeapYear(n)) {
            return 29;
        }
        return 28;
    }
    
    private boolean isLeapYear(final int n) {
        return n % 4 == 0 && (n % 100 != 0 || n % 400 == 0);
    }
    
    protected int mod(final int n, final int n2, final int n3) {
        return n - n3 * n2;
    }
    
    protected int fQuotient(final int n, final int n2) {
        return (int)Math.floor(n / (float)n2);
    }
    
    protected int modulo(final int n, final int n2, final int n3) {
        final int n4 = n - n2;
        final int n5 = n3 - n2;
        return this.mod(n4, n5, this.fQuotient(n4, n5)) + n2;
    }
    
    protected int fQuotient(final int n, final int n2, final int n3) {
        return this.fQuotient(n - n2, n3 - n2);
    }
    
    protected String dateToString(final DateTimeData dateTimeData) {
        final StringBuffer sb = new StringBuffer(25);
        this.append(sb, dateTimeData.year, 4);
        sb.append('-');
        this.append(sb, dateTimeData.month, 2);
        sb.append('-');
        this.append(sb, dateTimeData.day, 2);
        sb.append('T');
        this.append(sb, dateTimeData.hour, 2);
        sb.append(':');
        this.append(sb, dateTimeData.minute, 2);
        sb.append(':');
        this.append(sb, dateTimeData.second);
        this.append(sb, (char)dateTimeData.utc, 0);
        return sb.toString();
    }
    
    protected final void append(final StringBuffer sb, int n, final int n2) {
        if (n == Integer.MIN_VALUE) {
            sb.append(n);
            return;
        }
        if (n < 0) {
            sb.append('-');
            n = -n;
        }
        if (n2 == 4) {
            if (n < 10) {
                sb.append("000");
            }
            else if (n < 100) {
                sb.append("00");
            }
            else if (n < 1000) {
                sb.append('0');
            }
            sb.append(n);
        }
        else if (n2 == 2) {
            if (n < 10) {
                sb.append('0');
            }
            sb.append(n);
        }
        else if (n != 0) {
            sb.append((char)n);
        }
    }
    
    protected final void append(final StringBuffer sb, double n) {
        if (n < 0.0) {
            sb.append('-');
            n = -n;
        }
        if (n < 10.0) {
            sb.append('0');
        }
        this.append2(sb, n);
    }
    
    protected final void append2(final StringBuffer sb, final double n) {
        final int n2 = (int)n;
        if (n == n2) {
            sb.append(n2);
        }
        else {
            this.append3(sb, n);
        }
    }
    
    private void append3(final StringBuffer sb, final double n) {
        final String value = String.valueOf(n);
        final int index = value.indexOf(69);
        if (index == -1) {
            sb.append(value);
            return;
        }
        if (n < 1.0) {
            int int1;
            try {
                int1 = this.parseInt(value, index + 2, value.length());
            }
            catch (final Exception ex) {
                sb.append(value);
                return;
            }
            sb.append("0.");
            for (int i = 1; i < int1; ++i) {
                sb.append('0');
            }
            int n2;
            for (n2 = index - 1; n2 > 0 && value.charAt(n2) == '0'; --n2) {}
            for (int j = 0; j <= n2; ++j) {
                final char char1 = value.charAt(j);
                if (char1 != '.') {
                    sb.append(char1);
                }
            }
        }
        else {
            int int2;
            try {
                int2 = this.parseInt(value, index + 1, value.length());
            }
            catch (final Exception ex2) {
                sb.append(value);
                return;
            }
            final int n3 = int2 + 2;
            for (int k = 0; k < index; ++k) {
                final char char2 = value.charAt(k);
                if (char2 != '.') {
                    if (k == n3) {
                        sb.append('.');
                    }
                    sb.append(char2);
                }
            }
            for (int l = n3 - index; l > 0; --l) {
                sb.append('0');
            }
        }
    }
    
    protected double parseSecond(final String s, final int n, final int n2) throws NumberFormatException {
        int n3 = -1;
        for (int i = n; i < n2; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '.') {
                n3 = i;
            }
            else if (char1 > '9' || char1 < '0') {
                throw new NumberFormatException("'" + s + "' has wrong format");
            }
        }
        if (n3 == -1) {
            if (n + 2 != n2) {
                throw new NumberFormatException("'" + s + "' has wrong format");
            }
        }
        else if (n + 2 != n3 || n3 + 1 == n2) {
            throw new NumberFormatException("'" + s + "' has wrong format");
        }
        return Double.parseDouble(s.substring(n, n2));
    }
    
    private void cloneDate(final DateTimeData dateTimeData, final DateTimeData dateTimeData2) {
        dateTimeData2.year = dateTimeData.year;
        dateTimeData2.month = dateTimeData.month;
        dateTimeData2.day = dateTimeData.day;
        dateTimeData2.hour = dateTimeData.hour;
        dateTimeData2.minute = dateTimeData.minute;
        dateTimeData2.second = dateTimeData.second;
        dateTimeData2.utc = dateTimeData.utc;
        dateTimeData2.timezoneHr = dateTimeData.timezoneHr;
        dateTimeData2.timezoneMin = dateTimeData.timezoneMin;
    }
    
    protected XMLGregorianCalendar getXMLGregorianCalendar(final DateTimeData dateTimeData) {
        return null;
    }
    
    protected Duration getDuration(final DateTimeData dateTimeData) {
        return null;
    }
    
    protected final BigDecimal getFractionalSecondsAsBigDecimal(final DateTimeData dateTimeData) {
        final StringBuffer sb = new StringBuffer();
        this.append3(sb, dateTimeData.unNormSecond);
        final String string = sb.toString();
        final int index = string.indexOf(46);
        if (index == -1) {
            return null;
        }
        final BigDecimal bigDecimal = new BigDecimal(string.substring(index));
        if (bigDecimal.compareTo(BigDecimal.valueOf(0L)) == 0) {
            return null;
        }
        return bigDecimal;
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
        private String canonical;
        
        public DateTimeData(final String originalValue, final AbstractDateTimeDV type) {
            this.normalized = true;
            this.originalValue = originalValue;
            this.type = type;
        }
        
        public DateTimeData(final int year, final int month, final int day, final int hour, final int minute, final double second, final int utc, final String originalValue, final boolean b, final AbstractDateTimeDV type) {
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
        
        public boolean equals(final Object o) {
            return o instanceof DateTimeData && this.type.compareDates(this, (DateTimeData)o, true) == 0;
        }
        
        public synchronized String toString() {
            if (this.canonical == null) {
                this.canonical = this.type.dateToString(this);
            }
            return this.canonical;
        }
        
        public int getYears() {
            if (this.type instanceof DurationDV) {
                return 0;
            }
            return this.normalized ? this.year : this.unNormYear;
        }
        
        public int getMonths() {
            if (this.type instanceof DurationDV) {
                return this.year * 12 + this.month;
            }
            return this.normalized ? this.month : this.unNormMonth;
        }
        
        public int getDays() {
            if (this.type instanceof DurationDV) {
                return 0;
            }
            return this.normalized ? this.day : this.unNormDay;
        }
        
        public int getHours() {
            if (this.type instanceof DurationDV) {
                return 0;
            }
            return this.normalized ? this.hour : this.unNormHour;
        }
        
        public int getMinutes() {
            if (this.type instanceof DurationDV) {
                return 0;
            }
            return this.normalized ? this.minute : this.unNormMinute;
        }
        
        public double getSeconds() {
            if (this.type instanceof DurationDV) {
                return this.day * 24 * 60 * 60 + this.hour * 60 * 60 + this.minute * 60 + this.second;
            }
            return this.normalized ? this.second : this.unNormSecond;
        }
        
        public boolean hasTimeZone() {
            return this.utc != 0;
        }
        
        public int getTimeZoneHours() {
            return this.timezoneHr;
        }
        
        public int getTimeZoneMinutes() {
            return this.timezoneMin;
        }
        
        public String getLexicalValue() {
            return this.originalValue;
        }
        
        public XSDateTime normalize() {
            if (!this.normalized) {
                final DateTimeData dateTimeData = (DateTimeData)this.clone();
                dateTimeData.normalized = true;
                return dateTimeData;
            }
            return this;
        }
        
        public boolean isNormalized() {
            return this.normalized;
        }
        
        public Object clone() {
            final DateTimeData dateTimeData = new DateTimeData(this.year, this.month, this.day, this.hour, this.minute, this.second, this.utc, this.originalValue, this.normalized, this.type);
            dateTimeData.canonical = this.canonical;
            dateTimeData.position = this.position;
            dateTimeData.timezoneHr = this.timezoneHr;
            dateTimeData.timezoneMin = this.timezoneMin;
            dateTimeData.unNormYear = this.unNormYear;
            dateTimeData.unNormMonth = this.unNormMonth;
            dateTimeData.unNormDay = this.unNormDay;
            dateTimeData.unNormHour = this.unNormHour;
            dateTimeData.unNormMinute = this.unNormMinute;
            dateTimeData.unNormSecond = this.unNormSecond;
            return dateTimeData;
        }
        
        public XMLGregorianCalendar getXMLGregorianCalendar() {
            return this.type.getXMLGregorianCalendar(this);
        }
        
        public Duration getDuration() {
            return this.type.getDuration(this);
        }
    }
}
