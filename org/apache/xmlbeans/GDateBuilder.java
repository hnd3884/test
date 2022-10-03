package org.apache.xmlbeans;

import java.util.TimeZone;
import java.util.Date;
import java.util.Calendar;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.io.Serializable;

public final class GDateBuilder implements GDateSpecification, Serializable
{
    private static final long serialVersionUID = 1L;
    private int _bits;
    private int _CY;
    private int _M;
    private int _D;
    private int _h;
    private int _m;
    private int _s;
    private BigDecimal _fs;
    private int _tzsign;
    private int _tzh;
    private int _tzm;
    static final BigInteger TEN;
    
    public GDateBuilder() {
    }
    
    public Object clone() {
        return new GDateBuilder(this);
    }
    
    public GDate toGDate() {
        return new GDate(this);
    }
    
    public GDateBuilder(final GDateSpecification gdate) {
        if (gdate.hasTimeZone()) {
            this.setTimeZone(gdate.getTimeZoneSign(), gdate.getTimeZoneHour(), gdate.getTimeZoneMinute());
        }
        if (gdate.hasTime()) {
            this.setTime(gdate.getHour(), gdate.getMinute(), gdate.getSecond(), gdate.getFraction());
        }
        if (gdate.hasDay()) {
            this.setDay(gdate.getDay());
        }
        if (gdate.hasMonth()) {
            this.setMonth(gdate.getMonth());
        }
        if (gdate.hasYear()) {
            this.setYear(gdate.getYear());
        }
    }
    
    public GDateBuilder(final CharSequence string) {
        this(new GDate(string));
    }
    
    public GDateBuilder(final Calendar calendar) {
        this(new GDate(calendar));
    }
    
    public GDateBuilder(final int year, final int month, final int day, final int hour, final int minute, final int second, final BigDecimal fraction) {
        this._bits = 30;
        if (year == 0) {
            throw new IllegalArgumentException();
        }
        this._CY = ((year > 0) ? year : (year + 1));
        this._M = month;
        this._D = day;
        this._h = hour;
        this._m = minute;
        this._s = second;
        this._fs = ((fraction == null) ? GDate._zero : fraction);
        if (!this.isValid()) {
            throw new IllegalArgumentException();
        }
    }
    
    public GDateBuilder(final int year, final int month, final int day, final int hour, final int minute, final int second, final BigDecimal fraction, final int tzSign, final int tzHour, final int tzMinute) {
        this._bits = 31;
        if (year == 0) {
            throw new IllegalArgumentException();
        }
        this._CY = ((year > 0) ? year : (year + 1));
        this._M = month;
        this._D = day;
        this._h = hour;
        this._m = minute;
        this._s = second;
        this._fs = ((fraction == null) ? GDate._zero : fraction);
        this._tzsign = tzSign;
        this._tzh = tzHour;
        this._tzm = tzMinute;
        if (!this.isValid()) {
            throw new IllegalArgumentException();
        }
    }
    
    public GDateBuilder(final Date date) {
        this.setDate(date);
    }
    
    @Override
    public boolean isImmutable() {
        return false;
    }
    
    @Override
    public int getFlags() {
        return this._bits;
    }
    
    @Override
    public final boolean hasTimeZone() {
        return (this._bits & 0x1) != 0x0;
    }
    
    @Override
    public final boolean hasYear() {
        return (this._bits & 0x2) != 0x0;
    }
    
    @Override
    public final boolean hasMonth() {
        return (this._bits & 0x4) != 0x0;
    }
    
    @Override
    public final boolean hasDay() {
        return (this._bits & 0x8) != 0x0;
    }
    
    @Override
    public final boolean hasTime() {
        return (this._bits & 0x10) != 0x0;
    }
    
    @Override
    public final boolean hasDate() {
        return (this._bits & 0xE) == 0xE;
    }
    
    @Override
    public final int getYear() {
        return (this._CY > 0) ? this._CY : (this._CY - 1);
    }
    
    @Override
    public final int getMonth() {
        return this._M;
    }
    
    @Override
    public final int getDay() {
        return this._D;
    }
    
    @Override
    public final int getHour() {
        return this._h;
    }
    
    @Override
    public final int getMinute() {
        return this._m;
    }
    
    @Override
    public final int getSecond() {
        return this._s;
    }
    
    @Override
    public final BigDecimal getFraction() {
        return this._fs;
    }
    
    @Override
    public final int getMillisecond() {
        if (this._fs == null || this._fs == GDate._zero) {
            return 0;
        }
        return this._fs.setScale(3, 4).unscaledValue().intValue();
    }
    
    @Override
    public final int getTimeZoneSign() {
        return this._tzsign;
    }
    
    @Override
    public final int getTimeZoneHour() {
        return this._tzh;
    }
    
    @Override
    public final int getTimeZoneMinute() {
        return this._tzm;
    }
    
    public void setYear(final int year) {
        if (year < -292275295 || year > 292277265) {
            throw new IllegalArgumentException("year out of range");
        }
        if (year == 0) {
            throw new IllegalArgumentException("year cannot be 0");
        }
        this._bits |= 0x2;
        this._CY = ((year > 0) ? year : (year + 1));
    }
    
    public void setMonth(final int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("month out of range");
        }
        this._bits |= 0x4;
        this._M = month;
    }
    
    public void setDay(final int day) {
        if (day < 1 || day > 31) {
            throw new IllegalArgumentException("day out of range");
        }
        this._bits |= 0x8;
        this._D = day;
    }
    
    public void setTime(final int hour, final int minute, final int second, final BigDecimal fraction) {
        if (hour < 0 || hour > 24) {
            throw new IllegalArgumentException("hour out of range");
        }
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("minute out of range");
        }
        if (second < 0 || second > 59) {
            throw new IllegalArgumentException("second out of range");
        }
        if (fraction != null && (fraction.signum() < 0 || fraction.compareTo(GDate._one) > 1)) {
            throw new IllegalArgumentException("fraction out of range");
        }
        if (hour == 24 && (minute != 0 || second != 0 || (fraction != null && GDate._zero.compareTo(fraction) != 0))) {
            throw new IllegalArgumentException("when hour is 24, min sec and fracton must be 0");
        }
        this._bits |= 0x10;
        this._h = hour;
        this._m = minute;
        this._s = second;
        this._fs = ((fraction == null) ? GDate._zero : fraction);
    }
    
    public void setTimeZone(final int tzSign, final int tzHour, final int tzMinute) {
        if ((tzSign != 0 || tzHour != 0 || tzMinute != 0) && ((tzSign != -1 && tzSign != 1) || tzHour < 0 || tzMinute < 0 || ((tzHour != 14 || tzMinute != 0) && (tzHour >= 14 || tzMinute >= 60)))) {
            throw new IllegalArgumentException("time zone out of range (-14:00 to +14:00). (" + ((tzSign < 0) ? "-" : "+") + tzHour + ":" + tzMinute + ")");
        }
        this._bits |= 0x1;
        this._tzsign = tzSign;
        this._tzh = tzHour;
        this._tzm = tzMinute;
    }
    
    public void setTimeZone(int tzTotalMinutes) {
        if (tzTotalMinutes < -840 || tzTotalMinutes > 840) {
            throw new IllegalArgumentException("time zone out of range (-840 to 840 minutes). (" + tzTotalMinutes + ")");
        }
        final int tzSign = (tzTotalMinutes < 0) ? -1 : ((tzTotalMinutes > 0) ? 1 : 0);
        tzTotalMinutes *= tzSign;
        final int tzH = tzTotalMinutes / 60;
        final int tzM = tzTotalMinutes - tzH * 60;
        this.setTimeZone(tzSign, tzH, tzM);
    }
    
    public void clearYear() {
        this._bits &= 0xFFFFFFFD;
        this._CY = 0;
    }
    
    public void clearMonth() {
        this._bits &= 0xFFFFFFFB;
        this._M = 0;
    }
    
    public void clearDay() {
        this._bits &= 0xFFFFFFF7;
        this._D = 0;
    }
    
    public void clearTime() {
        this._bits &= 0xFFFFFFEF;
        this._h = 0;
        this._m = 0;
        this._s = 0;
        this._fs = null;
    }
    
    public void clearTimeZone() {
        this._bits &= 0xFFFFFFFE;
        this._tzsign = 0;
        this._tzh = 0;
        this._tzm = 0;
    }
    
    @Override
    public boolean isValid() {
        return isValidGDate(this);
    }
    
    static final boolean isValidGDate(final GDateSpecification date) {
        if (date.hasYear() && date.getYear() == 0) {
            return false;
        }
        if (date.hasMonth() && (date.getMonth() < 1 || date.getMonth() > 12)) {
            return false;
        }
        if (date.hasDay()) {
            if (date.getDay() >= 1 && date.getDay() <= 31) {
                if (date.getDay() <= 28 || !date.hasMonth()) {
                    return (!date.hasTime() || (date.getHour() >= 0 && date.getHour() <= 23 && date.getMinute() >= 0 && date.getMinute() <= 59 && date.getSecond() >= 0 && date.getSecond() <= 59 && date.getFraction().signum() >= 0 && date.getFraction().compareTo(GDate._one) < 0) || (date.getHour() == 24 && date.getMinute() == 0 && date.getSecond() == 0 && date.getFraction().compareTo(GDate._zero) == 0)) && (!date.hasTimeZone() || (date.getTimeZoneSign() == 0 && date.getTimeZoneHour() == 0 && date.getTimeZoneMinute() == 0) || ((date.getTimeZoneSign() == -1 || date.getTimeZoneSign() == 1) && date.getTimeZoneHour() >= 0 && date.getTimeZoneMinute() >= 0 && ((date.getTimeZoneHour() == 14 && date.getTimeZoneMinute() == 0) || (date.getTimeZoneHour() < 14 && date.getTimeZoneMinute() < 60))));
                }
                if (date.hasYear()) {
                    if (date.getDay() <= _maxDayInMonthFor((date.getYear() > 0) ? date.getYear() : (date.getYear() + 1), date.getMonth())) {
                        return (!date.hasTime() || (date.getHour() >= 0 && date.getHour() <= 23 && date.getMinute() >= 0 && date.getMinute() <= 59 && date.getSecond() >= 0 && date.getSecond() <= 59 && date.getFraction().signum() >= 0 && date.getFraction().compareTo(GDate._one) < 0) || (date.getHour() == 24 && date.getMinute() == 0 && date.getSecond() == 0 && date.getFraction().compareTo(GDate._zero) == 0)) && (!date.hasTimeZone() || (date.getTimeZoneSign() == 0 && date.getTimeZoneHour() == 0 && date.getTimeZoneMinute() == 0) || ((date.getTimeZoneSign() == -1 || date.getTimeZoneSign() == 1) && date.getTimeZoneHour() >= 0 && date.getTimeZoneMinute() >= 0 && ((date.getTimeZoneHour() == 14 && date.getTimeZoneMinute() == 0) || (date.getTimeZoneHour() < 14 && date.getTimeZoneMinute() < 60))));
                    }
                }
                else if (date.getDay() <= _maxDayInMonth(date.getMonth())) {
                    return (!date.hasTime() || (date.getHour() >= 0 && date.getHour() <= 23 && date.getMinute() >= 0 && date.getMinute() <= 59 && date.getSecond() >= 0 && date.getSecond() <= 59 && date.getFraction().signum() >= 0 && date.getFraction().compareTo(GDate._one) < 0) || (date.getHour() == 24 && date.getMinute() == 0 && date.getSecond() == 0 && date.getFraction().compareTo(GDate._zero) == 0)) && (!date.hasTimeZone() || (date.getTimeZoneSign() == 0 && date.getTimeZoneHour() == 0 && date.getTimeZoneMinute() == 0) || ((date.getTimeZoneSign() == -1 || date.getTimeZoneSign() == 1) && date.getTimeZoneHour() >= 0 && date.getTimeZoneMinute() >= 0 && ((date.getTimeZoneHour() == 14 && date.getTimeZoneMinute() == 0) || (date.getTimeZoneHour() < 14 && date.getTimeZoneMinute() < 60))));
                }
            }
            return false;
        }
        return (!date.hasTime() || (date.getHour() >= 0 && date.getHour() <= 23 && date.getMinute() >= 0 && date.getMinute() <= 59 && date.getSecond() >= 0 && date.getSecond() <= 59 && date.getFraction().signum() >= 0 && date.getFraction().compareTo(GDate._one) < 0) || (date.getHour() == 24 && date.getMinute() == 0 && date.getSecond() == 0 && date.getFraction().compareTo(GDate._zero) == 0)) && (!date.hasTimeZone() || (date.getTimeZoneSign() == 0 && date.getTimeZoneHour() == 0 && date.getTimeZoneMinute() == 0) || ((date.getTimeZoneSign() == -1 || date.getTimeZoneSign() == 1) && date.getTimeZoneHour() >= 0 && date.getTimeZoneMinute() >= 0 && ((date.getTimeZoneHour() == 14 && date.getTimeZoneMinute() == 0) || (date.getTimeZoneHour() < 14 && date.getTimeZoneMinute() < 60))));
    }
    
    public void normalize() {
        if (this.hasDay() == this.hasMonth() && this.hasDay() == this.hasYear() && this.hasTimeZone() && this.hasTime()) {
            this.normalizeToTimeZone(0, 0, 0);
        }
        else {
            this._normalizeTimeAndDate();
        }
        if (this.hasTime() && this._fs != null && this._fs.scale() > 0) {
            if (this._fs.signum() == 0) {
                this._fs = GDate._zero;
            }
            else {
                final BigInteger bi = this._fs.unscaledValue();
                String str;
                int lastzero;
                for (str = bi.toString(), lastzero = str.length(); lastzero > 0 && str.charAt(lastzero - 1) == '0'; --lastzero) {}
                if (lastzero < str.length()) {
                    this._fs = this._fs.setScale(this._fs.scale() - str.length() + lastzero);
                }
            }
        }
    }
    
    void normalize24h() {
        if (!this.hasTime() || this.getHour() != 24) {
            return;
        }
        this._normalizeTimeAndDate();
    }
    
    private void _normalizeTimeAndDate() {
        long carry = 0L;
        if (this.hasTime()) {
            carry = this._normalizeTime();
        }
        if (this.hasDay()) {
            this._D += (int)carry;
        }
        if (this.hasDate()) {
            this._normalizeDate();
        }
        else if (this.hasMonth() && (this._M < 1 || this._M > 12)) {
            final int temp = this._M;
            this._M = _modulo(temp, 1, 13);
            if (this.hasYear()) {
                this._CY += (int)_fQuotient(temp, 1, 13);
            }
        }
    }
    
    public void normalizeToTimeZone(final int tzSign, final int tzHour, final int tzMinute) {
        if ((tzSign != 0 || tzHour != 0 || tzMinute != 0) && ((tzSign != -1 && tzSign != 1) || tzHour < 0 || tzMinute < 0 || ((tzHour != 14 || tzMinute != 0) && (tzHour >= 14 || tzMinute >= 60)))) {
            throw new IllegalArgumentException("time zone must be between -14:00 and +14:00");
        }
        if (!this.hasTimeZone() || !this.hasTime()) {
            throw new IllegalStateException("cannot normalize time zone without both time and timezone");
        }
        if (this.hasDay() != this.hasMonth() || this.hasDay() != this.hasYear()) {
            throw new IllegalStateException("cannot do date math without a complete date");
        }
        final int hshift = tzSign * tzHour - this._tzsign * this._tzh;
        final int mshift = tzSign * tzMinute - this._tzsign * this._tzm;
        this._tzsign = tzSign;
        this._tzh = tzHour;
        this._tzm = tzMinute;
        this.addDuration(1, 0, 0, 0, hshift, mshift, 0, null);
    }
    
    public void normalizeToTimeZone(int tzTotalMinutes) {
        if (tzTotalMinutes < -840 || tzTotalMinutes > 840) {
            throw new IllegalArgumentException("time zone out of range (-840 to 840 minutes). (" + tzTotalMinutes + ")");
        }
        final int tzSign = (tzTotalMinutes < 0) ? -1 : ((tzTotalMinutes > 0) ? 1 : 0);
        tzTotalMinutes *= tzSign;
        final int tzH = tzTotalMinutes / 60;
        final int tzM = tzTotalMinutes - tzH * 60;
        this.normalizeToTimeZone(tzSign, tzH, tzM);
    }
    
    public void addGDuration(final GDurationSpecification duration) {
        this.addDuration(duration.getSign(), duration.getYear(), duration.getMonth(), duration.getDay(), duration.getHour(), duration.getMinute(), duration.getSecond(), duration.getFraction());
    }
    
    public void subtractGDuration(final GDurationSpecification duration) {
        this.addDuration(-duration.getSign(), duration.getYear(), duration.getMonth(), duration.getDay(), duration.getHour(), duration.getMinute(), duration.getSecond(), duration.getFraction());
    }
    
    private void _normalizeDate() {
        if (this._M < 1 || this._M > 12 || this._D < 1 || this._D > _maxDayInMonthFor(this._CY, this._M)) {
            final int temp = this._M;
            this._M = _modulo(temp, 1, 13);
            this._CY += (int)_fQuotient(temp, 1, 13);
            final int extradays = this._D - 1;
            this._D = 1;
            this.setJulianDate(this.getJulianDate() + extradays);
        }
    }
    
    private long _normalizeTime() {
        long carry = 0L;
        if (this._fs != null && (this._fs.signum() < 0 || this._fs.compareTo(GDate._one) >= 0)) {
            final BigDecimal bdcarry = this._fs.setScale(0, 3);
            this._fs = this._fs.subtract(bdcarry);
            carry = bdcarry.longValue();
        }
        if (carry != 0L || this._s < 0 || this._s > 59 || this._m < 0 || this._m > 50 || this._h < 0 || this._h > 23) {
            long temp = this._s + carry;
            carry = _fQuotient(temp, 60);
            this._s = _mod(temp, 60, carry);
            temp = this._m + carry;
            carry = _fQuotient(temp, 60);
            this._m = _mod(temp, 60, carry);
            temp = this._h + carry;
            carry = _fQuotient(temp, 24);
            this._h = _mod(temp, 24, carry);
        }
        return carry;
    }
    
    public void addDuration(final int sign, final int year, final int month, final int day, final int hour, final int minute, final int second, final BigDecimal fraction) {
        final boolean timemath = hour != 0 || minute != 0 || second != 0 || (fraction != null && fraction.signum() != 0);
        if (timemath && !this.hasTime()) {
            throw new IllegalStateException("cannot do time math without a complete time");
        }
        final boolean datemath = this.hasDay() && (day != 0 || timemath);
        if (datemath && !this.hasDate()) {
            throw new IllegalStateException("cannot do date math without a complete date");
        }
        if (month != 0 || year != 0) {
            if (this.hasDay()) {
                this._normalizeDate();
            }
            int temp = this._M + sign * month;
            this._M = _modulo(temp, 1, 13);
            this._CY = this._CY + sign * year + (int)_fQuotient(temp, 1, 13);
            if (this.hasDay()) {
                assert this._D >= 1;
                temp = _maxDayInMonthFor(this._CY, this._M);
                if (this._D > temp) {
                    this._D = temp;
                }
            }
        }
        long carry = 0L;
        if (timemath) {
            if (fraction != null && fraction.signum() != 0) {
                if (this._fs.signum() == 0 && sign == 1) {
                    this._fs = fraction;
                }
                else {
                    this._fs = ((sign == 1) ? this._fs.add(fraction) : this._fs.subtract(fraction));
                }
            }
            this._s += sign * second;
            this._m += sign * minute;
            this._h += sign * hour;
            carry = this._normalizeTime();
        }
        if (datemath) {
            this._D += (int)(sign * day + carry);
            this._normalizeDate();
        }
    }
    
    private static int _maxDayInMonthFor(final int year, final int month) {
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        }
        if (month == 2) {
            return _isLeapYear(year) ? 29 : 28;
        }
        return 31;
    }
    
    private static int _maxDayInMonth(final int month) {
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        }
        if (month == 2) {
            return 29;
        }
        return 31;
    }
    
    @Override
    public final int getJulianDate() {
        return julianDateForGDate(this);
    }
    
    public void setJulianDate(final int julianday) {
        if (julianday < 0) {
            throw new IllegalArgumentException("date before year -4713");
        }
        int temp = julianday + 68569;
        final int qepoc = 4 * temp / 146097;
        temp -= (146097 * qepoc + 3) / 4;
        this._CY = 4000 * (temp + 1) / 1461001;
        temp = temp - 1461 * this._CY / 4 + 31;
        this._M = 80 * temp / 2447;
        this._D = temp - 2447 * this._M / 80;
        temp = this._M / 11;
        this._M = this._M + 2 - 12 * temp;
        this._CY = 100 * (qepoc - 49) + this._CY + temp;
        this._bits |= 0xE;
    }
    
    public void setDate(final Date date) {
        final TimeZone dtz = TimeZone.getDefault();
        int offset = dtz.getOffset(date.getTime());
        int offsetsign = 1;
        if (offset < 0) {
            offsetsign = -1;
            offset = -offset;
        }
        int offsetmin = offset / 60000;
        final int offsethr = offsetmin / 60;
        offsetmin -= offsethr * 60;
        this.setTimeZone(offsetsign, offsethr, offsetmin);
        final int roundedoffset = offsetsign * (offsethr * 60 + offsetmin) * 60 * 1000;
        this.setTime(0, 0, 0, GDate._zero);
        this._bits |= 0xE;
        this._CY = 1970;
        this._M = 1;
        this._D = 1;
        this.addGDuration(new GDuration(1, 0, 0, 0, 0, 0, 0, BigDecimal.valueOf(date.getTime() + roundedoffset, 3)));
        if (this._fs.signum() == 0) {
            this._fs = GDate._zero;
        }
    }
    
    public void setGDate(final GDateSpecification gdate) {
        this._bits = (gdate.getFlags() & 0x1F);
        final int year = gdate.getYear();
        this._CY = ((year > 0) ? year : (year + 1));
        this._M = gdate.getMonth();
        this._D = gdate.getDay();
        this._h = gdate.getHour();
        this._m = gdate.getMinute();
        this._s = gdate.getSecond();
        this._fs = gdate.getFraction();
        this._tzsign = gdate.getTimeZoneSign();
        this._tzh = gdate.getTimeZoneHour();
        this._tzm = gdate.getTimeZoneMinute();
    }
    
    @Override
    public XmlCalendar getCalendar() {
        return new XmlCalendar(this);
    }
    
    @Override
    public Date getDate() {
        return dateForGDate(this);
    }
    
    static int julianDateForGDate(final GDateSpecification date) {
        if (!date.hasDate()) {
            throw new IllegalStateException("cannot do date math without a complete date");
        }
        final int day = date.getDay();
        final int month = date.getMonth();
        int year = date.getYear();
        year = ((year > 0) ? year : (year + 1));
        final int result = day - 32075 + 1461 * (year + 4800 + (month - 14) / 12) / 4 + 367 * (month - 2 - (month - 14) / 12 * 12) / 12 - 3 * ((year + 4900 + (month - 14) / 12) / 100) / 4;
        if (result < 0) {
            throw new IllegalStateException("date too far in the past (year allowed to -4713)");
        }
        return result;
    }
    
    static Date dateForGDate(final GDateSpecification date) {
        final long jDate = julianDateForGDate(date);
        final long to1970Date = jDate - 2440588L;
        long to1970Ms = 86400000L * to1970Date;
        to1970Ms += date.getMillisecond();
        to1970Ms += date.getSecond() * 1000;
        to1970Ms += date.getMinute() * 60 * 1000;
        to1970Ms += date.getHour() * 60 * 60 * 1000;
        if (date.hasTimeZone()) {
            to1970Ms -= date.getTimeZoneMinute() * date.getTimeZoneSign() * 60 * 1000;
            to1970Ms -= date.getTimeZoneHour() * date.getTimeZoneSign() * 60 * 60 * 1000;
        }
        else {
            final TimeZone def = TimeZone.getDefault();
            final int offset = def.getOffset(to1970Ms);
            to1970Ms -= offset;
        }
        return new Date(to1970Ms);
    }
    
    private static boolean _isLeapYear(final int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }
    
    private static final long _fQuotient(final long a, final int b) {
        if (a < 0L == b < 0) {
            return a / b;
        }
        return -((b - a - 1L) / b);
    }
    
    private static int _mod(final long a, final int b, final long quotient) {
        return (int)(a - quotient * b);
    }
    
    private static final int _modulo(final long temp, final int low, final int high) {
        final long a = temp - low;
        final int b = high - low;
        return _mod(a, b, _fQuotient(a, b)) + low;
    }
    
    private static final long _fQuotient(final long temp, final int low, final int high) {
        return _fQuotient(temp - low, high - low);
    }
    
    private void _setToFirstMoment() {
        if (!this.hasYear()) {
            this.setYear(1584);
        }
        if (!this.hasMonth()) {
            this.setMonth(1);
        }
        if (!this.hasDay()) {
            this.setDay(1);
        }
        if (!this.hasTime()) {
            this.setTime(0, 0, 0, GDate._zero);
        }
    }
    
    @Override
    public final int compareToGDate(final GDateSpecification datespec) {
        return compareGDate(this, datespec);
    }
    
    static final int compareGDate(GDateSpecification tdate, GDateSpecification datespec) {
        final int bitdiff = tdate.getFlags() ^ datespec.getFlags();
        if ((bitdiff & 0x1F) == 0x0) {
            if (tdate.hasTimeZone() && (datespec.getTimeZoneHour() != tdate.getTimeZoneHour() || datespec.getTimeZoneMinute() != tdate.getTimeZoneMinute() || datespec.getTimeZoneSign() != tdate.getTimeZoneSign())) {
                datespec = new GDateBuilder(datespec);
                final int flags = tdate.getFlags() & 0xE;
                if ((flags != 0 && flags != 14) || !tdate.hasTime()) {
                    ((GDateBuilder)datespec)._setToFirstMoment();
                    tdate = new GDateBuilder(tdate);
                    ((GDateBuilder)tdate)._setToFirstMoment();
                }
                ((GDateBuilder)datespec).normalizeToTimeZone(tdate.getTimeZoneSign(), tdate.getTimeZoneHour(), tdate.getTimeZoneMinute());
            }
            return fieldwiseCompare(tdate, datespec);
        }
        if ((bitdiff & 0x1E) != 0x0) {
            return 2;
        }
        if (!tdate.hasTimeZone()) {
            final int result = compareGDate(datespec, tdate);
            return (result == 2) ? 2 : (-result);
        }
        final GDateBuilder pdate = new GDateBuilder(tdate);
        if ((tdate.getFlags() & 0xE) == 0xC) {
            if (tdate.getDay() == 28 && tdate.getMonth() == 2) {
                if (datespec.getDay() == 1 && datespec.getMonth() == 3) {
                    pdate.setDay(29);
                }
            }
            else if (datespec.getDay() == 28 && datespec.getMonth() == 2 && tdate.getDay() == 1 && tdate.getMonth() == 3) {
                pdate.setMonth(2);
                pdate.setDay(29);
            }
        }
        pdate._setToFirstMoment();
        final GDateBuilder qplusdate = new GDateBuilder(datespec);
        qplusdate._setToFirstMoment();
        qplusdate.setTimeZone(1, 14, 0);
        qplusdate.normalizeToTimeZone(tdate.getTimeZoneSign(), tdate.getTimeZoneHour(), tdate.getTimeZoneMinute());
        if (fieldwiseCompare(pdate, qplusdate) == -1) {
            return -1;
        }
        final GDateBuilder qminusdate = qplusdate;
        qminusdate.setGDate(datespec);
        qminusdate._setToFirstMoment();
        qminusdate.setTimeZone(-1, 14, 0);
        qminusdate.normalizeToTimeZone(tdate.getTimeZoneSign(), tdate.getTimeZoneHour(), tdate.getTimeZoneMinute());
        if (fieldwiseCompare(pdate, qminusdate) == 1) {
            return 1;
        }
        return 2;
    }
    
    private static int fieldwiseCompare(final GDateSpecification tdate, final GDateSpecification date) {
        if (tdate.hasYear()) {
            final int CY = date.getYear();
            final int TCY = tdate.getYear();
            if (TCY < CY) {
                return -1;
            }
            if (TCY > CY) {
                return 1;
            }
        }
        if (tdate.hasMonth()) {
            final int M = date.getMonth();
            final int TM = tdate.getMonth();
            if (TM < M) {
                return -1;
            }
            if (TM > M) {
                return 1;
            }
        }
        if (tdate.hasDay()) {
            final int D = date.getDay();
            final int TD = tdate.getDay();
            if (TD < D) {
                return -1;
            }
            if (TD > D) {
                return 1;
            }
        }
        if (!tdate.hasTime()) {
            return 0;
        }
        final int h = date.getHour();
        final int th = tdate.getHour();
        if (th < h) {
            return -1;
        }
        if (th > h) {
            return 1;
        }
        final int m = date.getMinute();
        final int tm = tdate.getMinute();
        if (tm < m) {
            return -1;
        }
        if (tm > m) {
            return 1;
        }
        final int s = date.getSecond();
        final int ts = tdate.getSecond();
        if (ts < s) {
            return -1;
        }
        if (ts > s) {
            return 1;
        }
        final BigDecimal fs = date.getFraction();
        final BigDecimal tfs = tdate.getFraction();
        if (tfs == null && fs == null) {
            return 0;
        }
        return ((tfs == null) ? GDate._zero : tfs).compareTo((fs == null) ? GDate._zero : fs);
    }
    
    @Override
    public final int getBuiltinTypeCode() {
        return btcForFlags(this._bits);
    }
    
    static int btcForFlags(final int flags) {
        switch (flags & 0x1E) {
            case 2: {
                return 18;
            }
            case 6: {
                return 17;
            }
            case 4: {
                return 21;
            }
            case 12: {
                return 19;
            }
            case 8: {
                return 20;
            }
            case 14: {
                return 16;
            }
            case 30: {
                return 14;
            }
            case 16: {
                return 15;
            }
            default: {
                return 0;
            }
        }
    }
    
    public void setBuiltinTypeCode(final int typeCode) {
        switch (typeCode) {
            case 18: {
                this.clearMonth();
                this.clearDay();
                this.clearTime();
                return;
            }
            case 17: {
                this.clearDay();
                this.clearTime();
                return;
            }
            case 21: {
                this.clearYear();
                this.clearDay();
                this.clearTime();
                return;
            }
            case 19: {
                this.clearYear();
                this.clearTime();
                return;
            }
            case 20: {
                this.clearYear();
                this.clearMonth();
                this.clearTime();
                return;
            }
            case 16: {
                this.clearTime();
                return;
            }
            case 14: {
                return;
            }
            case 15: {
                this.clearYear();
                this.clearMonth();
                this.clearDay();
                return;
            }
            default: {
                throw new IllegalArgumentException("codeType must be one of SchemaType BTC_  DATE TIME related types.");
            }
        }
    }
    
    @Override
    public String canonicalString() {
        boolean needNormalize = this.hasTimeZone() && this.getTimeZoneSign() != 0 && this.hasTime() && this.hasDay() == this.hasMonth() && this.hasDay() == this.hasYear();
        if (!needNormalize && this.getFraction() != null && this.getFraction().scale() > 0) {
            final BigInteger bi = this.getFraction().unscaledValue();
            needNormalize = (bi.mod(GDateBuilder.TEN).signum() == 0);
        }
        if (!needNormalize) {
            return this.toString();
        }
        final GDateBuilder cdate = new GDateBuilder(this);
        cdate.normalize();
        return cdate.toString();
    }
    
    @Override
    public final String toString() {
        return GDate.formatGDate(this);
    }
    
    static {
        TEN = BigInteger.valueOf(10L);
    }
}
