package org.apache.xmlbeans;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.io.Serializable;

public class GDurationBuilder implements GDurationSpecification, Serializable
{
    private static final long serialVersionUID = 1L;
    private int _sign;
    private int _CY;
    private int _M;
    private int _D;
    private int _h;
    private int _m;
    private int _s;
    private BigDecimal _fs;
    private static final GDate[] _compDate;
    
    public GDurationBuilder() {
        this._sign = 1;
        this._fs = GDate._zero;
    }
    
    public GDurationBuilder(final String s) {
        this(new GDuration(s));
    }
    
    public GDurationBuilder(final int sign, final int year, final int month, final int day, final int hour, final int minute, final int second, final BigDecimal fraction) {
        if (sign != 1 && sign != -1) {
            throw new IllegalArgumentException();
        }
        this._sign = sign;
        this._CY = year;
        this._M = month;
        this._D = day;
        this._h = hour;
        this._m = minute;
        this._s = second;
        this._fs = ((fraction == null) ? GDate._zero : fraction);
    }
    
    public GDurationBuilder(final GDurationSpecification gDuration) {
        this._sign = gDuration.getSign();
        this._CY = gDuration.getYear();
        this._M = gDuration.getMonth();
        this._D = gDuration.getDay();
        this._h = gDuration.getHour();
        this._m = gDuration.getMinute();
        this._s = gDuration.getSecond();
        this._fs = gDuration.getFraction();
    }
    
    public Object clone() {
        return new GDurationBuilder(this);
    }
    
    public GDuration toGDuration() {
        return new GDuration(this);
    }
    
    public void addGDuration(final GDurationSpecification duration) {
        final int sign = this._sign * duration.getSign();
        this._add(duration, sign);
    }
    
    public void subtractGDuration(final GDurationSpecification duration) {
        final int sign = -this._sign * duration.getSign();
        this._add(duration, sign);
    }
    
    private void _add(final GDurationSpecification duration, final int sign) {
        this._CY += sign * duration.getYear();
        this._M += sign * duration.getMonth();
        this._D += sign * duration.getDay();
        this._h += sign * duration.getHour();
        this._m += sign * duration.getMinute();
        this._s += sign * duration.getSecond();
        if (duration.getFraction().signum() == 0) {
            return;
        }
        if (this._fs.signum() == 0 && sign == 1) {
            this._fs = duration.getFraction();
        }
        else {
            this._fs = ((sign > 0) ? this._fs.add(duration.getFraction()) : this._fs.subtract(duration.getFraction()));
        }
    }
    
    public final void setSign(final int sign) {
        if (sign != 1 && sign != -1) {
            throw new IllegalArgumentException();
        }
        this._sign = sign;
    }
    
    public void setYear(final int year) {
        this._CY = year;
    }
    
    public void setMonth(final int month) {
        this._M = month;
    }
    
    public void setDay(final int day) {
        this._D = day;
    }
    
    public void setHour(final int hour) {
        this._h = hour;
    }
    
    public void setMinute(final int minute) {
        this._m = minute;
    }
    
    public void setSecond(final int second) {
        this._s = second;
    }
    
    public void setFraction(final BigDecimal fraction) {
        this._fs = ((fraction == null) ? GDate._zero : fraction);
    }
    
    @Override
    public final boolean isImmutable() {
        return true;
    }
    
    @Override
    public final int getSign() {
        return this._sign;
    }
    
    @Override
    public final int getYear() {
        return this._CY;
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
    public BigDecimal getFraction() {
        return this._fs;
    }
    
    @Override
    public boolean isValid() {
        return isValidDuration(this);
    }
    
    public void normalize() {
        this._normalizeImpl(true);
    }
    
    private static final long _fQuotient(final long a, final int b) {
        if (a < 0L == b < 0) {
            return a / b;
        }
        return -((b - a - 1L) / b);
    }
    
    private static final int _mod(final long a, final int b, final long quotient) {
        return (int)(a - quotient * b);
    }
    
    private void _normalizeImpl(final boolean adjustSign) {
        if (this._M < 0 || this._M > 11) {
            final long temp = this._M;
            final long ycarry = _fQuotient(temp, 12);
            this._M = _mod(temp, 12, ycarry);
            this._CY += (int)ycarry;
        }
        long carry = 0L;
        if (this._fs != null && (this._fs.signum() < 0 || this._fs.compareTo(GDate._one) >= 0)) {
            final BigDecimal bdcarry = this._fs.setScale(0, 3);
            this._fs = this._fs.subtract(bdcarry);
            carry = bdcarry.intValue();
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
            this._D += (int)carry;
        }
        if (this._CY == 0 && this._M == 0 && this._D == 0 && this._h == 0 && this._m == 0 && this._s == 0 && (this._fs == null || this._fs.signum() == 0)) {
            this._sign = 1;
        }
        if (adjustSign && (this._D < 0 || this._CY < 0)) {
            int sign = (this._D <= 0 && (this._CY < 0 || (this._CY == 0 && this._M == 0))) ? (-this._sign) : this._getTotalSignSlowly();
            if (sign == 2) {
                sign = ((this._CY < 0) ? (-this._sign) : this._sign);
            }
            if (sign == 0) {
                sign = 1;
            }
            if (sign != this._sign) {
                this._sign = sign;
                this._CY = -this._CY;
                this._M = -this._M;
                this._D = -this._D;
                this._h = -this._h;
                this._m = -this._m;
                this._s = -this._s;
                if (this._fs != null) {
                    this._fs = this._fs.negate();
                }
            }
            this._normalizeImpl(false);
        }
    }
    
    static boolean isValidDuration(final GDurationSpecification spec) {
        return (spec.getSign() == 1 || spec.getSign() == -1) && spec.getYear() >= 0 && spec.getMonth() >= 0 && spec.getDay() >= 0 && spec.getHour() >= 0 && spec.getMinute() >= 0 && spec.getSecond() >= 0 && spec.getFraction().signum() >= 0;
    }
    
    @Override
    public final int compareToGDuration(final GDurationSpecification duration) {
        return compareDurations(this, duration);
    }
    
    @Override
    public String toString() {
        return formatDuration(this);
    }
    
    static int compareDurations(final GDurationSpecification d1, final GDurationSpecification d2) {
        if (d1.getFraction().signum() == 0 && d2.getFraction().signum() == 0) {
            final int s1 = d1.getSign();
            final int s2 = d2.getSign();
            final long month1 = s1 * (d1.getYear() * 12L + d1.getMonth());
            final long month2 = s2 * (d2.getYear() * 12L + d2.getMonth());
            final long sec1 = s1 * (((d1.getDay() * 24L + d1.getHour()) * 60L + d1.getMinute()) * 60L + d1.getSecond());
            final long sec2 = s2 * (((d2.getDay() * 24L + d2.getHour()) * 60L + d2.getMinute()) * 60L + d2.getSecond());
            if (month1 == month2) {
                if (sec1 == sec2) {
                    return 0;
                }
                if (sec1 < sec2) {
                    return -1;
                }
                if (sec1 > sec2) {
                    return 1;
                }
            }
            if (month1 < month2 && sec1 - sec2 < 2419200L) {
                return -1;
            }
            if (month1 > month2 && sec2 - sec1 < 2419200L) {
                return 1;
            }
        }
        final GDurationBuilder diff = new GDurationBuilder(d1);
        diff.subtractGDuration(d2);
        return diff._getTotalSignSlowly();
    }
    
    private int _getTotalSignSlowly() {
        int pos = 0;
        int neg = 0;
        int zer = 0;
        final GDateBuilder enddate = new GDateBuilder();
        for (int i = 0; i < GDurationBuilder._compDate.length; ++i) {
            enddate.setGDate(GDurationBuilder._compDate[i]);
            enddate.addGDuration(this);
            switch (enddate.compareToGDate(GDurationBuilder._compDate[i])) {
                case -1: {
                    ++neg;
                    break;
                }
                case 0: {
                    ++zer;
                    break;
                }
                case 1: {
                    ++pos;
                    break;
                }
            }
        }
        if (pos == GDurationBuilder._compDate.length) {
            return 1;
        }
        if (neg == GDurationBuilder._compDate.length) {
            return -1;
        }
        if (zer == GDurationBuilder._compDate.length) {
            return 0;
        }
        return 2;
    }
    
    static String formatDuration(final GDurationSpecification duration) {
        final StringBuffer message = new StringBuffer(30);
        if (duration.getSign() < 0) {
            message.append('-');
        }
        message.append('P');
        if (duration.getYear() != 0) {
            message.append(duration.getYear());
            message.append('Y');
        }
        if (duration.getMonth() != 0) {
            message.append(duration.getMonth());
            message.append('M');
        }
        if (duration.getDay() != 0) {
            message.append(duration.getDay());
            message.append('D');
        }
        if (duration.getHour() != 0 || duration.getMinute() != 0 || duration.getSecond() != 0 || duration.getFraction().signum() != 0) {
            message.append('T');
        }
        if (duration.getHour() != 0) {
            message.append(duration.getHour());
            message.append('H');
        }
        if (duration.getMinute() != 0) {
            message.append(duration.getMinute());
            message.append('M');
        }
        if (duration.getFraction().signum() != 0) {
            BigDecimal s = duration.getFraction();
            if (duration.getSecond() != 0) {
                s = s.add(BigDecimal.valueOf(duration.getSecond()));
            }
            message.append(stripTrailingZeros(toPlainString(s)));
            message.append('S');
        }
        else if (duration.getSecond() != 0) {
            message.append(duration.getSecond());
            message.append('S');
        }
        else if (message.length() <= 2) {
            message.append("T0S");
        }
        return message.toString();
    }
    
    public static String toPlainString(final BigDecimal bd) {
        final BigInteger intVal = bd.unscaledValue();
        final int scale = bd.scale();
        final String intValStr = intVal.toString();
        if (scale == 0) {
            return intValStr;
        }
        final boolean isNegative = intValStr.charAt(0) == '-';
        int point = intValStr.length() - scale - (isNegative ? 1 : 0);
        final StringBuffer sb = new StringBuffer(intValStr.length() + 2 + ((point <= 0) ? (-point + 1) : 0));
        if (point <= 0) {
            if (isNegative) {
                sb.append('-');
            }
            sb.append('0').append('.');
            while (point < 0) {
                sb.append('0');
                ++point;
            }
            sb.append(intValStr.substring((int)(isNegative ? 1 : 0)));
        }
        else if (point < intValStr.length()) {
            sb.append(intValStr);
            sb.insert(point + (isNegative ? 1 : 0), '.');
        }
        else {
            sb.append(intValStr);
            if (!intVal.equals(BigInteger.ZERO)) {
                for (int i = intValStr.length(); i < point; ++i) {
                    sb.append('0');
                }
            }
        }
        return sb.toString();
    }
    
    public static String stripTrailingZeros(final String s) {
        boolean seenDot = false;
        int zeroIndex;
        int i;
        for (i = (zeroIndex = s.length() - 1); i >= 0; --i, --zeroIndex) {
            if (s.charAt(i) != '0') {
                break;
            }
        }
        while (i >= 0) {
            if (s.charAt(i) == 'E') {
                return s;
            }
            if (s.charAt(i) == '.') {
                seenDot = true;
                break;
            }
            --i;
        }
        return seenDot ? s.substring(0, zeroIndex + 1) : s;
    }
    
    static {
        _compDate = new GDate[] { new GDate(1696, 9, 1, 0, 0, 0, null, 0, 0, 0), new GDate(1697, 2, 1, 0, 0, 0, null, 0, 0, 0), new GDate(1903, 3, 1, 0, 0, 0, null, 0, 0, 0), new GDate(1903, 7, 1, 0, 0, 0, null, 0, 0, 0) };
    }
}
