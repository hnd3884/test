package org.apache.xmlbeans;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.math.BigDecimal;
import java.io.Serializable;

public final class GDate implements GDateSpecification, Serializable
{
    private static final long serialVersionUID = 1L;
    static final int MAX_YEAR = 292277265;
    static final int MIN_YEAR = -292275295;
    private transient String _canonicalString;
    private transient String _string;
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
    static final BigDecimal _zero;
    static final BigDecimal _one;
    private static final char[] _tensDigit;
    private static final char[] _onesDigit;
    private static final TimeZone GMTZONE;
    private static final TimeZone[] MINUSZONE;
    private static final TimeZone[] PLUSZONE;
    
    public GDate(final CharSequence string) {
        int len = string.length();
        int start = 0;
        while (len > 0 && isSpace(string.charAt(len - 1))) {
            --len;
        }
        while (start < len && isSpace(string.charAt(start))) {
            ++start;
        }
        Label_0258: {
            if (len - start >= 1 && string.charAt(len - 1) == 'Z') {
                this._bits |= 0x1;
                --len;
            }
            else if (len - start >= 6) {
                if (string.charAt(len - 3) == ':') {
                    int tzsign = 0;
                    switch (string.charAt(len - 6)) {
                        case '-': {
                            tzsign = -1;
                            break;
                        }
                        case '+': {
                            tzsign = 1;
                            break;
                        }
                        default: {
                            break Label_0258;
                        }
                    }
                    final int tzhour = twoDigit(string, len - 5);
                    final int tzminute = twoDigit(string, len - 2);
                    if (tzhour > 14) {
                        throw new IllegalArgumentException("time zone hour must be two digits between -14 and +14");
                    }
                    if (tzminute > 59) {
                        throw new IllegalArgumentException("time zone minute must be two digits between 00 and 59");
                    }
                    this._bits |= 0x1;
                    this._tzsign = tzsign;
                    this._tzh = tzhour;
                    this._tzm = tzminute;
                    len -= 6;
                }
            }
        }
        if (start < len && (start + 2 >= len || string.charAt(start + 2) != ':')) {
            boolean negyear = false;
            if (start < len && string.charAt(start) == '-') {
                negyear = true;
                ++start;
            }
            int value = 0;
            int digits = -start;
            final boolean startsWithZero = start < len && digitVal(string.charAt(start)) == 0;
            while (true) {
                char ch = (start < len) ? string.charAt(start) : '\0';
                if (!isDigit(ch)) {
                    digits += start;
                    if (digits > 9) {
                        throw new IllegalArgumentException("year too long (up to 9 digits)");
                    }
                    if (digits >= 4) {
                        this._bits |= 0x2;
                        this._CY = (negyear ? (-value) : value);
                        if (this._CY == 0) {
                            throw new IllegalArgumentException("year must not be zero");
                        }
                    }
                    else if (digits > 0) {
                        throw new IllegalArgumentException("year must be four digits (may pad with zeroes, e.g., 0560)");
                    }
                    if (this._CY > 292277265) {
                        throw new IllegalArgumentException("year value not supported: too big, must be less than 292277265");
                    }
                    if (this._CY < -292275295) {
                        throw new IllegalArgumentException("year values not supported: too small, must be bigger than -292275295");
                    }
                    if (ch != '-') {
                        if (negyear && !this.hasYear()) {
                            throw new IllegalArgumentException();
                        }
                        break;
                    }
                    else {
                        ++start;
                        if (len - start >= 2) {
                            value = twoDigit(string, start);
                            if (value >= 1 && value <= 12) {
                                this._bits |= 0x4;
                                this._M = value;
                                start += 2;
                            }
                        }
                        ch = ((start < len) ? string.charAt(start) : '\0');
                        if (ch != '-') {
                            if (!this.hasMonth()) {
                                throw new IllegalArgumentException();
                            }
                            break;
                        }
                        else {
                            ++start;
                            if (len - start >= 2) {
                                value = twoDigit(string, start);
                                if (value >= 1 && value <= 31) {
                                    this._bits |= 0x8;
                                    this._D = value;
                                    start += 2;
                                }
                            }
                            if (!this.hasDay()) {
                                if (this.hasMonth() && !this.hasYear()) {
                                    ch = ((start < len) ? string.charAt(start) : '\0');
                                    if (ch == '-') {
                                        ++start;
                                        break;
                                    }
                                }
                                throw new IllegalArgumentException();
                            }
                            break;
                        }
                    }
                }
                else {
                    if (startsWithZero && start + digits >= 4) {
                        throw new IllegalArgumentException("year value starting with zero must be 4 or less digits: " + (Object)string);
                    }
                    value = value * 10 + digitVal(ch);
                    ++start;
                }
            }
        }
        if (start < len) {
            if (this.hasYear() || this.hasMonth() || this.hasDay()) {
                if (string.charAt(start) != 'T') {
                    throw new IllegalArgumentException("date and time must be separated by 'T'");
                }
                ++start;
            }
            if (len < start + 8 || string.charAt(start + 2) != ':' || string.charAt(start + 5) != ':') {
                throw new IllegalArgumentException();
            }
            final int h = twoDigit(string, start);
            if (h > 24) {
                throw new IllegalArgumentException("hour must be between 00 and 23");
            }
            final int m = twoDigit(string, start + 3);
            if (m >= 60) {
                throw new IllegalArgumentException("minute must be between 00 and 59");
            }
            final int s = twoDigit(string, start + 6);
            if (s >= 60) {
                throw new IllegalArgumentException("second must be between 00 and 59");
            }
            start += 8;
            BigDecimal fs = GDate._zero;
            if (start < len) {
                if (string.charAt(start) != '.') {
                    throw new IllegalArgumentException();
                }
                if (start + 1 < len) {
                    for (int i = start + 1; i < len; ++i) {
                        if (!isDigit(string.charAt(i))) {
                            throw new IllegalArgumentException();
                        }
                    }
                    try {
                        fs = new BigDecimal(string.subSequence(start, len).toString());
                    }
                    catch (final Throwable e) {
                        throw new IllegalArgumentException();
                    }
                }
            }
            this._bits |= 0x10;
            this._h = h;
            this._m = m;
            this._s = s;
            this._fs = fs;
        }
        if (this.hasTime() && this._h == 24) {
            if (this._m != 0 || this._s != 0 || this._fs.compareTo(GDate._zero) != 0) {
                throw new IllegalArgumentException("if hour is 24, minutes, seconds and fraction must be 0");
            }
            if (this.hasDate()) {
                final GDateBuilder gdb = new GDateBuilder(this._CY, this._M, this._D, this._h, this._m, this._s, this._fs, this._tzsign, this._tzh, this._tzm);
                gdb.normalize24h();
                this._D = gdb.getDay();
                this._M = gdb.getMonth();
                this._CY = gdb.getYear();
                this._h = 0;
            }
            else if (this.hasDay()) {
                ++this._D;
                this._h = 0;
            }
        }
        if (!this.isValid()) {
            throw new IllegalArgumentException("invalid date");
        }
    }
    
    public GDate(final int year, final int month, final int day, final int hour, final int minute, final int second, final BigDecimal fraction) {
        this._bits = 30;
        this._CY = year;
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
    
    public GDate(final int year, final int month, final int day, final int hour, final int minute, final int second, final BigDecimal fraction, final int tzSign, final int tzHour, final int tzMinute) {
        this._bits = 31;
        this._CY = year;
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
    
    public GDate(final Date date) {
        this(new GDateBuilder(date));
    }
    
    public GDate(final Calendar calendar) {
        final boolean isSetYear = calendar.isSet(1);
        final boolean isSetEra = calendar.isSet(0);
        final boolean isSetMonth = calendar.isSet(2);
        final boolean isSetDay = calendar.isSet(5);
        final boolean isSetHourOfDay = calendar.isSet(11);
        final boolean isSetHour = calendar.isSet(10);
        final boolean isSetAmPm = calendar.isSet(9);
        final boolean isSetMinute = calendar.isSet(12);
        final boolean isSetSecond = calendar.isSet(13);
        final boolean isSetMillis = calendar.isSet(14);
        final boolean isSetZone = calendar.isSet(15);
        final boolean isSetDst = calendar.isSet(16);
        if (isSetYear) {
            int y = calendar.get(1);
            if (isSetEra && calendar instanceof GregorianCalendar && calendar.get(0) == 0) {
                y = -y;
            }
            this._bits |= 0x2;
            this._CY = y;
        }
        if (isSetMonth) {
            this._bits |= 0x4;
            this._M = calendar.get(2) + 1;
        }
        if (isSetDay) {
            this._bits |= 0x8;
            this._D = calendar.get(5);
        }
        boolean gotTime = false;
        int h = 0;
        int m = 0;
        int s = 0;
        BigDecimal fs = GDate._zero;
        if (isSetHourOfDay) {
            h = calendar.get(11);
            gotTime = true;
        }
        else if (isSetHour && isSetAmPm) {
            h = calendar.get(10) + calendar.get(9) * 12;
            gotTime = true;
        }
        if (isSetMinute) {
            m = calendar.get(12);
            gotTime = true;
        }
        if (isSetSecond) {
            s = calendar.get(13);
            gotTime = true;
        }
        if (isSetMillis) {
            fs = BigDecimal.valueOf(calendar.get(14), 3);
            gotTime = true;
        }
        if (gotTime) {
            this._bits |= 0x10;
            this._h = h;
            this._m = m;
            this._s = s;
            this._fs = fs;
        }
        if (isSetZone) {
            int zoneOffsetInMilliseconds = calendar.get(15);
            if (isSetDst) {
                zoneOffsetInMilliseconds += calendar.get(16);
            }
            this._bits |= 0x1;
            if (zoneOffsetInMilliseconds == 0) {
                this._tzsign = 0;
                this._tzh = 0;
                this._tzm = 0;
                final TimeZone zone = calendar.getTimeZone();
                final String id = zone.getID();
                if (id != null && id.length() > 3) {
                    switch (id.charAt(3)) {
                        case '+': {
                            this._tzsign = 1;
                            break;
                        }
                        case '-': {
                            this._tzsign = -1;
                            break;
                        }
                    }
                }
            }
            else {
                this._tzsign = ((zoneOffsetInMilliseconds < 0) ? -1 : 1);
                zoneOffsetInMilliseconds *= this._tzsign;
                this._tzh = zoneOffsetInMilliseconds / 3600000;
                this._tzm = (zoneOffsetInMilliseconds - this._tzh * 3600000) / 60000;
            }
        }
    }
    
    public GDate(final GDateSpecification gdate) {
        if (gdate.hasTimeZone()) {
            this._bits |= 0x1;
            this._tzsign = gdate.getTimeZoneSign();
            this._tzh = gdate.getTimeZoneHour();
            this._tzm = gdate.getTimeZoneMinute();
        }
        if (gdate.hasTime()) {
            this._bits |= 0x10;
            this._h = gdate.getHour();
            this._m = gdate.getMinute();
            this._s = gdate.getSecond();
            this._fs = gdate.getFraction();
        }
        if (gdate.hasDay()) {
            this._bits |= 0x8;
            this._D = gdate.getDay();
        }
        if (gdate.hasMonth()) {
            this._bits |= 0x4;
            this._M = gdate.getMonth();
        }
        if (gdate.hasYear()) {
            this._bits |= 0x2;
            this._CY = gdate.getYear();
        }
    }
    
    static final boolean isDigit(final char ch) {
        return (char)(ch - '0') <= '\t';
    }
    
    static final boolean isSpace(final char ch) {
        switch (ch) {
            case '\t':
            case '\n':
            case '\r':
            case ' ': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static final int digitVal(final char ch) {
        return ch - '0';
    }
    
    private static final int twoDigit(final CharSequence str, final int index) {
        final char ch1 = str.charAt(index);
        final char ch2 = str.charAt(index + 1);
        if (!isDigit(ch1) || !isDigit(ch2)) {
            return 100;
        }
        return digitVal(ch1) * 10 + digitVal(ch2);
    }
    
    @Override
    public final boolean isImmutable() {
        return true;
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
    public final BigDecimal getFraction() {
        return this._fs;
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
    
    @Override
    public int getMillisecond() {
        if (this._fs == null) {
            return 0;
        }
        return this._fs.setScale(3, 1).unscaledValue().intValue();
    }
    
    @Override
    public String canonicalString() {
        this.ensureCanonicalString();
        return this._canonicalString;
    }
    
    @Override
    public boolean isValid() {
        return GDateBuilder.isValidGDate(this);
    }
    
    @Override
    public int getJulianDate() {
        return GDateBuilder.julianDateForGDate(this);
    }
    
    @Override
    public XmlCalendar getCalendar() {
        return new XmlCalendar(this);
    }
    
    @Override
    public Date getDate() {
        return GDateBuilder.dateForGDate(this);
    }
    
    @Override
    public int compareToGDate(final GDateSpecification datespec) {
        return GDateBuilder.compareGDate(this, datespec);
    }
    
    @Override
    public int getBuiltinTypeCode() {
        return GDateBuilder.btcForFlags(this._bits);
    }
    
    public GDate add(final GDurationSpecification duration) {
        final GDateBuilder builder = new GDateBuilder(this);
        builder.addGDuration(duration);
        return builder.toGDate();
    }
    
    public GDate subtract(final GDurationSpecification duration) {
        final GDateBuilder builder = new GDateBuilder(this);
        builder.subtractGDuration(duration);
        return builder.toGDate();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GDate)) {
            return false;
        }
        this.ensureCanonicalString();
        return this._canonicalString.equals(((GDate)obj).canonicalString());
    }
    
    @Override
    public int hashCode() {
        this.ensureCanonicalString();
        return this._canonicalString.hashCode();
    }
    
    private void ensureCanonicalString() {
        if (this._canonicalString != null) {
            return;
        }
        boolean needNormalize = this.hasTimeZone() && this.getTimeZoneSign() != 0 && this.hasTime() && this.hasDay() == this.hasMonth() && this.hasDay() == this.hasYear();
        if (!needNormalize && this.getFraction() != null && this.getFraction().scale() > 0) {
            final BigInteger bi = this.getFraction().unscaledValue();
            needNormalize = (bi.mod(GDateBuilder.TEN).signum() == 0);
        }
        if (!needNormalize) {
            this._canonicalString = this.toString();
        }
        else {
            final GDateBuilder gdb = new GDateBuilder(this);
            gdb.normalize();
            this._canonicalString = gdb.toString();
        }
    }
    
    @Override
    public String toString() {
        if (this._string == null) {
            this._string = formatGDate(this);
        }
        return this._string;
    }
    
    private static final int _padTwoAppend(final char[] b, final int i, final int n) {
        assert n >= 0 && n < 100;
        b[i] = GDate._tensDigit[n];
        b[i + 1] = GDate._onesDigit[n];
        return i + 2;
    }
    
    private static final int _padFourAppend(final char[] b, int i, int n) {
        if (n < 0) {
            b[i++] = '-';
            n = -n;
        }
        if (n >= 10000) {
            final String s = Integer.toString(n);
            s.getChars(0, s.length(), b, i);
            return i + s.length();
        }
        final int q = n / 100;
        final int r = n - q * 100;
        b[i] = GDate._tensDigit[q];
        b[i + 1] = GDate._onesDigit[q];
        b[i + 2] = GDate._tensDigit[r];
        b[i + 3] = GDate._onesDigit[r];
        return i + 4;
    }
    
    static final TimeZone timeZoneForGDate(final GDateSpecification date) {
        if (!date.hasTimeZone()) {
            return TimeZone.getDefault();
        }
        if (date.getTimeZoneSign() == 0) {
            return GDate.GMTZONE;
        }
        if (date.getTimeZoneMinute() == 0 && date.getTimeZoneHour() <= 14 && date.getTimeZoneHour() >= 0) {
            return (date.getTimeZoneSign() < 0) ? GDate.MINUSZONE[date.getTimeZoneHour()] : GDate.PLUSZONE[date.getTimeZoneHour()];
        }
        final char[] zb = { 'G', 'M', 'T', (date.getTimeZoneSign() < 0) ? '-' : '+', '\0', '\0', '\0', '\0', '\0' };
        _padTwoAppend(zb, 4, date.getTimeZoneHour());
        zb[6] = ':';
        _padTwoAppend(zb, 7, date.getTimeZoneMinute());
        return TimeZone.getTimeZone(new String(zb));
    }
    
    static String formatGDate(final GDateSpecification spec) {
        final BigDecimal fs = spec.getFraction();
        final char[] message = new char[33 + ((fs == null) ? 0 : fs.scale())];
        int i = 0;
        if (spec.hasYear() || spec.hasMonth() || spec.hasDay()) {
            if (spec.hasYear()) {
                i = _padFourAppend(message, 0, spec.getYear());
            }
            else {
                message[i++] = '-';
            }
            if (spec.hasMonth() || spec.hasDay()) {
                message[i++] = '-';
                if (spec.hasMonth()) {
                    i = _padTwoAppend(message, i, spec.getMonth());
                }
                if (spec.hasDay()) {
                    message[i++] = '-';
                    i = _padTwoAppend(message, i, spec.getDay());
                }
            }
            if (spec.hasTime()) {
                message[i++] = 'T';
            }
        }
        if (spec.hasTime()) {
            i = _padTwoAppend(message, i, spec.getHour());
            message[i++] = ':';
            i = _padTwoAppend(message, i, spec.getMinute());
            message[i++] = ':';
            i = _padTwoAppend(message, i, spec.getSecond());
            if (fs != GDate._zero) {
                final String frac = fs.toString();
                final int point = frac.indexOf(46);
                if (point >= 0) {
                    frac.getChars(point, frac.length(), message, i);
                    i += frac.length() - point;
                }
            }
        }
        if (spec.hasTimeZone()) {
            if (spec.getTimeZoneSign() == 0) {
                message[i++] = 'Z';
            }
            else {
                message[i++] = ((spec.getTimeZoneSign() > 0) ? '+' : '-');
                i = _padTwoAppend(message, i, spec.getTimeZoneHour());
                message[i++] = ':';
                i = _padTwoAppend(message, i, spec.getTimeZoneMinute());
            }
        }
        return new String(message, 0, i);
    }
    
    static {
        _zero = BigDecimal.valueOf(0L);
        _one = BigDecimal.valueOf(1L);
        _tensDigit = new char[] { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9', '9' };
        _onesDigit = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        GMTZONE = TimeZone.getTimeZone("GMT");
        MINUSZONE = new TimeZone[] { TimeZone.getTimeZone("GMT-00:00"), TimeZone.getTimeZone("GMT-01:00"), TimeZone.getTimeZone("GMT-02:00"), TimeZone.getTimeZone("GMT-03:00"), TimeZone.getTimeZone("GMT-04:00"), TimeZone.getTimeZone("GMT-05:00"), TimeZone.getTimeZone("GMT-06:00"), TimeZone.getTimeZone("GMT-07:00"), TimeZone.getTimeZone("GMT-08:00"), TimeZone.getTimeZone("GMT-09:00"), TimeZone.getTimeZone("GMT-10:00"), TimeZone.getTimeZone("GMT-11:00"), TimeZone.getTimeZone("GMT-12:00"), TimeZone.getTimeZone("GMT-13:00"), TimeZone.getTimeZone("GMT-14:00") };
        PLUSZONE = new TimeZone[] { TimeZone.getTimeZone("GMT+00:00"), TimeZone.getTimeZone("GMT+01:00"), TimeZone.getTimeZone("GMT+02:00"), TimeZone.getTimeZone("GMT+03:00"), TimeZone.getTimeZone("GMT+04:00"), TimeZone.getTimeZone("GMT+05:00"), TimeZone.getTimeZone("GMT+06:00"), TimeZone.getTimeZone("GMT+07:00"), TimeZone.getTimeZone("GMT+08:00"), TimeZone.getTimeZone("GMT+09:00"), TimeZone.getTimeZone("GMT+10:00"), TimeZone.getTimeZone("GMT+11:00"), TimeZone.getTimeZone("GMT+12:00"), TimeZone.getTimeZone("GMT+13:00"), TimeZone.getTimeZone("GMT+14:00") };
    }
}
