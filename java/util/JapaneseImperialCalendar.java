package java.util;

import sun.util.calendar.CalendarSystem;
import java.io.IOException;
import java.io.ObjectInputStream;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.ZoneInfo;
import sun.util.calendar.BaseCalendar;
import sun.util.locale.provider.CalendarDataUtility;
import sun.util.calendar.AbstractCalendar;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.Era;
import sun.util.calendar.Gregorian;
import sun.util.calendar.LocalGregorianCalendar;

class JapaneseImperialCalendar extends Calendar
{
    public static final int BEFORE_MEIJI = 0;
    public static final int MEIJI = 1;
    public static final int TAISHO = 2;
    public static final int SHOWA = 3;
    public static final int HEISEI = 4;
    private static final int REIWA = 5;
    private static final int EPOCH_OFFSET = 719163;
    private static final int ONE_SECOND = 1000;
    private static final int ONE_MINUTE = 60000;
    private static final int ONE_HOUR = 3600000;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;
    private static final LocalGregorianCalendar jcal;
    private static final Gregorian gcal;
    private static final Era BEFORE_MEIJI_ERA;
    private static final Era[] eras;
    private static final long[] sinceFixedDates;
    private static final int currentEra;
    static final int[] MIN_VALUES;
    static final int[] LEAST_MAX_VALUES;
    static final int[] MAX_VALUES;
    private static final long serialVersionUID = -3364572813905467929L;
    private transient LocalGregorianCalendar.Date jdate;
    private transient int[] zoneOffsets;
    private transient int[] originalFields;
    private transient long cachedFixedDate;
    
    JapaneseImperialCalendar(final TimeZone timeZone, final Locale locale) {
        super(timeZone, locale);
        this.cachedFixedDate = Long.MIN_VALUE;
        this.jdate = JapaneseImperialCalendar.jcal.newCalendarDate(timeZone);
        this.setTimeInMillis(System.currentTimeMillis());
    }
    
    JapaneseImperialCalendar(final TimeZone timeZone, final Locale locale, final boolean b) {
        super(timeZone, locale);
        this.cachedFixedDate = Long.MIN_VALUE;
        this.jdate = JapaneseImperialCalendar.jcal.newCalendarDate(timeZone);
    }
    
    @Override
    public String getCalendarType() {
        return "japanese";
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof JapaneseImperialCalendar && super.equals(o);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ this.jdate.hashCode();
    }
    
    @Override
    public void add(final int n, final int n2) {
        if (n2 == 0) {
            return;
        }
        if (n < 0 || n >= 15) {
            throw new IllegalArgumentException();
        }
        this.complete();
        if (n == 1) {
            final LocalGregorianCalendar.Date date = (LocalGregorianCalendar.Date)this.jdate.clone();
            date.addYear(n2);
            this.pinDayOfMonth(date);
            this.set(0, getEraIndex(date));
            this.set(1, date.getYear());
            this.set(2, date.getMonth() - 1);
            this.set(5, date.getDayOfMonth());
        }
        else if (n == 2) {
            final LocalGregorianCalendar.Date date2 = (LocalGregorianCalendar.Date)this.jdate.clone();
            date2.addMonth(n2);
            this.pinDayOfMonth(date2);
            this.set(0, getEraIndex(date2));
            this.set(1, date2.getYear());
            this.set(2, date2.getMonth() - 1);
            this.set(5, date2.getDayOfMonth());
        }
        else if (n == 0) {
            int n3 = this.internalGet(0) + n2;
            if (n3 < 0) {
                n3 = 0;
            }
            else if (n3 > JapaneseImperialCalendar.eras.length - 1) {
                n3 = JapaneseImperialCalendar.eras.length - 1;
            }
            this.set(0, n3);
        }
        else {
            long n4 = n2;
            long n5 = 0L;
            switch (n) {
                case 10:
                case 11: {
                    n4 *= 3600000L;
                    break;
                }
                case 12: {
                    n4 *= 60000L;
                    break;
                }
                case 13: {
                    n4 *= 1000L;
                }
                case 3:
                case 4:
                case 8: {
                    n4 *= 7L;
                }
                case 9: {
                    n4 = n2 / 2;
                    n5 = 12 * (n2 % 2);
                    break;
                }
            }
            if (n >= 10) {
                this.setTimeInMillis(this.time + n4);
                return;
            }
            long cachedFixedDate = this.cachedFixedDate;
            long n6 = (((n5 + this.internalGet(11)) * 60L + this.internalGet(12)) * 60L + this.internalGet(13)) * 1000L + this.internalGet(14);
            if (n6 >= 86400000L) {
                ++cachedFixedDate;
                n6 -= 86400000L;
            }
            else if (n6 < 0L) {
                --cachedFixedDate;
                n6 += 86400000L;
            }
            final long n7 = cachedFixedDate + n4;
            final int n8 = this.internalGet(15) + this.internalGet(16);
            this.setTimeInMillis((n7 - 719163L) * 86400000L + n6 - n8);
            final int n9 = n8 - (this.internalGet(15) + this.internalGet(16));
            if (n9 != 0) {
                this.setTimeInMillis(this.time + n9);
                if (this.cachedFixedDate != n7) {
                    this.setTimeInMillis(this.time - n9);
                }
            }
        }
    }
    
    @Override
    public void roll(final int n, final boolean b) {
        this.roll(n, b ? 1 : -1);
    }
    
    @Override
    public void roll(final int n, int n2) {
        if (n2 == 0) {
            return;
        }
        if (n < 0 || n >= 15) {
            throw new IllegalArgumentException();
        }
        this.complete();
        int n3 = this.getMinimum(n);
        int n4 = this.getMaximum(n);
        switch (n) {
            case 10:
            case 11: {
                final int n5 = n4 + 1;
                final int internalGet = this.internalGet(n);
                int n6 = (internalGet + n2) % n5;
                if (n6 < 0) {
                    n6 += n5;
                }
                this.time += 3600000 * (n6 - internalGet);
                final LocalGregorianCalendar.Date calendarDate = JapaneseImperialCalendar.jcal.getCalendarDate(this.time, this.getZone());
                if (this.internalGet(5) != calendarDate.getDayOfMonth()) {
                    calendarDate.setEra(this.jdate.getEra());
                    calendarDate.setDate(this.internalGet(1), this.internalGet(2) + 1, this.internalGet(5));
                    if (n == 10) {
                        assert this.internalGet(9) == 1;
                        calendarDate.addHours(12);
                    }
                    this.time = JapaneseImperialCalendar.jcal.getTime(calendarDate);
                }
                final int hours = calendarDate.getHours();
                this.internalSet(n, hours % n5);
                if (n == 10) {
                    this.internalSet(11, hours);
                }
                else {
                    this.internalSet(9, hours / 12);
                    this.internalSet(10, hours % 12);
                }
                final int zoneOffset = calendarDate.getZoneOffset();
                final int daylightSaving = calendarDate.getDaylightSaving();
                this.internalSet(15, zoneOffset - daylightSaving);
                this.internalSet(16, daylightSaving);
                return;
            }
            case 1: {
                n3 = this.getActualMinimum(n);
                n4 = this.getActualMaximum(n);
                break;
            }
            case 2: {
                if (!this.isTransitionYear(this.jdate.getNormalizedYear())) {
                    final int year = this.jdate.getYear();
                    if (year == this.getMaximum(1)) {
                        final LocalGregorianCalendar.Date calendarDate2 = JapaneseImperialCalendar.jcal.getCalendarDate(this.time, this.getZone());
                        final LocalGregorianCalendar.Date calendarDate3 = JapaneseImperialCalendar.jcal.getCalendarDate(Long.MAX_VALUE, this.getZone());
                        final int n7 = calendarDate3.getMonth() - 1;
                        int rolledValue = getRolledValue(this.internalGet(n), n2, n3, n7);
                        if (rolledValue == n7) {
                            calendarDate2.addYear(-400);
                            calendarDate2.setMonth(rolledValue + 1);
                            if (calendarDate2.getDayOfMonth() > calendarDate3.getDayOfMonth()) {
                                calendarDate2.setDayOfMonth(calendarDate3.getDayOfMonth());
                                JapaneseImperialCalendar.jcal.normalize(calendarDate2);
                            }
                            if (calendarDate2.getDayOfMonth() == calendarDate3.getDayOfMonth() && calendarDate2.getTimeOfDay() > calendarDate3.getTimeOfDay()) {
                                calendarDate2.setMonth(rolledValue + 1);
                                calendarDate2.setDayOfMonth(calendarDate3.getDayOfMonth() - 1);
                                JapaneseImperialCalendar.jcal.normalize(calendarDate2);
                                rolledValue = calendarDate2.getMonth() - 1;
                            }
                            this.set(5, calendarDate2.getDayOfMonth());
                        }
                        this.set(2, rolledValue);
                    }
                    else if (year == this.getMinimum(1)) {
                        final LocalGregorianCalendar.Date calendarDate4 = JapaneseImperialCalendar.jcal.getCalendarDate(this.time, this.getZone());
                        final LocalGregorianCalendar.Date calendarDate5 = JapaneseImperialCalendar.jcal.getCalendarDate(Long.MIN_VALUE, this.getZone());
                        final int n8 = calendarDate5.getMonth() - 1;
                        int rolledValue2 = getRolledValue(this.internalGet(n), n2, n8, n4);
                        if (rolledValue2 == n8) {
                            calendarDate4.addYear(400);
                            calendarDate4.setMonth(rolledValue2 + 1);
                            if (calendarDate4.getDayOfMonth() < calendarDate5.getDayOfMonth()) {
                                calendarDate4.setDayOfMonth(calendarDate5.getDayOfMonth());
                                JapaneseImperialCalendar.jcal.normalize(calendarDate4);
                            }
                            if (calendarDate4.getDayOfMonth() == calendarDate5.getDayOfMonth() && calendarDate4.getTimeOfDay() < calendarDate5.getTimeOfDay()) {
                                calendarDate4.setMonth(rolledValue2 + 1);
                                calendarDate4.setDayOfMonth(calendarDate5.getDayOfMonth() + 1);
                                JapaneseImperialCalendar.jcal.normalize(calendarDate4);
                                rolledValue2 = calendarDate4.getMonth() - 1;
                            }
                            this.set(5, calendarDate4.getDayOfMonth());
                        }
                        this.set(2, rolledValue2);
                    }
                    else {
                        int n9 = (this.internalGet(2) + n2) % 12;
                        if (n9 < 0) {
                            n9 += 12;
                        }
                        this.set(2, n9);
                        final int monthLength = this.monthLength(n9);
                        if (this.internalGet(5) > monthLength) {
                            this.set(5, monthLength);
                        }
                    }
                }
                else {
                    final int eraIndex = getEraIndex(this.jdate);
                    CalendarDate calendarDate6 = null;
                    if (this.jdate.getYear() == 1) {
                        calendarDate6 = JapaneseImperialCalendar.eras[eraIndex].getSinceDate();
                        n3 = calendarDate6.getMonth() - 1;
                    }
                    else if (eraIndex < JapaneseImperialCalendar.eras.length - 1) {
                        calendarDate6 = JapaneseImperialCalendar.eras[eraIndex + 1].getSinceDate();
                        if (calendarDate6.getYear() == this.jdate.getNormalizedYear()) {
                            n4 = calendarDate6.getMonth() - 1;
                            if (calendarDate6.getDayOfMonth() == 1) {
                                --n4;
                            }
                        }
                    }
                    if (n3 == n4) {
                        return;
                    }
                    final int rolledValue3 = getRolledValue(this.internalGet(n), n2, n3, n4);
                    this.set(2, rolledValue3);
                    if (rolledValue3 == n3) {
                        if ((calendarDate6.getMonth() != 1 || calendarDate6.getDayOfMonth() != 1) && this.jdate.getDayOfMonth() < calendarDate6.getDayOfMonth()) {
                            this.set(5, calendarDate6.getDayOfMonth());
                        }
                    }
                    else if (rolledValue3 == n4 && calendarDate6.getMonth() - 1 == rolledValue3) {
                        final int dayOfMonth = calendarDate6.getDayOfMonth();
                        if (this.jdate.getDayOfMonth() >= dayOfMonth) {
                            this.set(5, dayOfMonth - 1);
                        }
                    }
                }
                return;
            }
            case 3: {
                final int normalizedYear = this.jdate.getNormalizedYear();
                n4 = this.getActualMaximum(3);
                this.set(7, this.internalGet(7));
                final int internalGet2 = this.internalGet(3);
                final int n10 = internalGet2 + n2;
                if (this.isTransitionYear(this.jdate.getNormalizedYear())) {
                    final long cachedFixedDate = this.cachedFixedDate;
                    final long n11 = cachedFixedDate - 7 * (internalGet2 - n3);
                    final LocalGregorianCalendar.Date calendarDate7 = getCalendarDate(n11);
                    if (calendarDate7.getEra() != this.jdate.getEra() || calendarDate7.getYear() != this.jdate.getYear()) {
                        ++n3;
                    }
                    JapaneseImperialCalendar.jcal.getCalendarDateFromFixedDate(calendarDate7, cachedFixedDate + 7 * (n4 - internalGet2));
                    if (calendarDate7.getEra() != this.jdate.getEra() || calendarDate7.getYear() != this.jdate.getYear()) {
                        --n4;
                    }
                    final LocalGregorianCalendar.Date calendarDate8 = getCalendarDate(n11 + (getRolledValue(internalGet2, n2, n3, n4) - 1) * 7);
                    this.set(2, calendarDate8.getMonth() - 1);
                    this.set(5, calendarDate8.getDayOfMonth());
                    return;
                }
                final int year2 = this.jdate.getYear();
                if (year2 == this.getMaximum(1)) {
                    n4 = this.getActualMaximum(3);
                }
                else if (year2 == this.getMinimum(1)) {
                    n3 = this.getActualMinimum(3);
                    n4 = this.getActualMaximum(3);
                    if (n10 > n3 && n10 < n4) {
                        this.set(3, n10);
                        return;
                    }
                }
                if (n10 > n3 && n10 < n4) {
                    this.set(3, n10);
                    return;
                }
                final long cachedFixedDate2 = this.cachedFixedDate;
                final long n12 = cachedFixedDate2 - 7 * (internalGet2 - n3);
                if (year2 != this.getMinimum(1)) {
                    if (JapaneseImperialCalendar.gcal.getYearFromFixedDate(n12) != normalizedYear) {
                        ++n3;
                    }
                }
                else if (n12 < JapaneseImperialCalendar.jcal.getFixedDate(JapaneseImperialCalendar.jcal.getCalendarDate(Long.MIN_VALUE, this.getZone()))) {
                    ++n3;
                }
                if (JapaneseImperialCalendar.gcal.getYearFromFixedDate(cachedFixedDate2 + 7 * (n4 - this.internalGet(3))) != normalizedYear) {
                    --n4;
                    break;
                }
                break;
            }
            case 4: {
                final boolean transitionYear = this.isTransitionYear(this.jdate.getNormalizedYear());
                int n13 = this.internalGet(7) - this.getFirstDayOfWeek();
                if (n13 < 0) {
                    n13 += 7;
                }
                final long cachedFixedDate3 = this.cachedFixedDate;
                long fixedDateMonth1;
                int n14;
                if (transitionYear) {
                    fixedDateMonth1 = this.getFixedDateMonth1(this.jdate, cachedFixedDate3);
                    n14 = this.actualMonthLength();
                }
                else {
                    fixedDateMonth1 = cachedFixedDate3 - this.internalGet(5) + 1L;
                    n14 = JapaneseImperialCalendar.jcal.getMonthLength(this.jdate);
                }
                long dayOfWeekDateOnOrBefore = AbstractCalendar.getDayOfWeekDateOnOrBefore(fixedDateMonth1 + 6L, this.getFirstDayOfWeek());
                if ((int)(dayOfWeekDateOnOrBefore - fixedDateMonth1) >= this.getMinimalDaysInFirstWeek()) {
                    dayOfWeekDateOnOrBefore -= 7L;
                }
                long n15 = dayOfWeekDateOnOrBefore + (getRolledValue(this.internalGet(n), n2, 1, this.getActualMaximum(n)) - 1) * 7 + n13;
                if (n15 < fixedDateMonth1) {
                    n15 = fixedDateMonth1;
                }
                else if (n15 >= fixedDateMonth1 + n14) {
                    n15 = fixedDateMonth1 + n14 - 1L;
                }
                this.set(5, (int)(n15 - fixedDateMonth1) + 1);
                return;
            }
            case 5: {
                if (!this.isTransitionYear(this.jdate.getNormalizedYear())) {
                    n4 = JapaneseImperialCalendar.jcal.getMonthLength(this.jdate);
                    break;
                }
                final long fixedDateMonth2 = this.getFixedDateMonth1(this.jdate, this.cachedFixedDate);
                final LocalGregorianCalendar.Date calendarDate9 = getCalendarDate(fixedDateMonth2 + getRolledValue((int)(this.cachedFixedDate - fixedDateMonth2), n2, 0, this.actualMonthLength() - 1));
                assert getEraIndex(calendarDate9) == this.internalGetEra() && calendarDate9.getYear() == this.internalGet(1) && calendarDate9.getMonth() - 1 == this.internalGet(2);
                this.set(5, calendarDate9.getDayOfMonth());
                return;
            }
            case 6: {
                n4 = this.getActualMaximum(n);
                if (!this.isTransitionYear(this.jdate.getNormalizedYear())) {
                    break;
                }
                final LocalGregorianCalendar.Date calendarDate10 = getCalendarDate(this.cachedFixedDate - this.internalGet(6) + getRolledValue(this.internalGet(6), n2, n3, n4));
                assert getEraIndex(calendarDate10) == this.internalGetEra() && calendarDate10.getYear() == this.internalGet(1);
                this.set(2, calendarDate10.getMonth() - 1);
                this.set(5, calendarDate10.getDayOfMonth());
                return;
            }
            case 7: {
                final int normalizedYear2 = this.jdate.getNormalizedYear();
                if (!this.isTransitionYear(normalizedYear2) && !this.isTransitionYear(normalizedYear2 - 1)) {
                    final int internalGet3 = this.internalGet(3);
                    if (internalGet3 > 1 && internalGet3 < 52) {
                        this.set(3, this.internalGet(3));
                        n4 = 7;
                        break;
                    }
                }
                n2 %= 7;
                if (n2 == 0) {
                    return;
                }
                final long cachedFixedDate4 = this.cachedFixedDate;
                final long dayOfWeekDateOnOrBefore2 = AbstractCalendar.getDayOfWeekDateOnOrBefore(cachedFixedDate4, this.getFirstDayOfWeek());
                long n16 = cachedFixedDate4 + n2;
                if (n16 < dayOfWeekDateOnOrBefore2) {
                    n16 += 7L;
                }
                else if (n16 >= dayOfWeekDateOnOrBefore2 + 7L) {
                    n16 -= 7L;
                }
                final LocalGregorianCalendar.Date calendarDate11 = getCalendarDate(n16);
                this.set(0, getEraIndex(calendarDate11));
                this.set(calendarDate11.getYear(), calendarDate11.getMonth() - 1, calendarDate11.getDayOfMonth());
                return;
            }
            case 8: {
                n3 = 1;
                if (!this.isTransitionYear(this.jdate.getNormalizedYear())) {
                    final int internalGet4 = this.internalGet(5);
                    final int monthLength2 = JapaneseImperialCalendar.jcal.getMonthLength(this.jdate);
                    final int n17 = monthLength2 % 7;
                    n4 = monthLength2 / 7;
                    if ((internalGet4 - 1) % 7 < n17) {
                        ++n4;
                    }
                    this.set(7, this.internalGet(7));
                    break;
                }
                final long cachedFixedDate5 = this.cachedFixedDate;
                final long fixedDateMonth3 = this.getFixedDateMonth1(this.jdate, cachedFixedDate5);
                final int actualMonthLength = this.actualMonthLength();
                final int n18 = actualMonthLength % 7;
                int n19 = actualMonthLength / 7;
                final int n20 = (int)(cachedFixedDate5 - fixedDateMonth3) % 7;
                if (n20 < n18) {
                    ++n19;
                }
                this.set(5, getCalendarDate(fixedDateMonth3 + (getRolledValue(this.internalGet(n), n2, n3, n19) - 1) * 7 + n20).getDayOfMonth());
                return;
            }
        }
        this.set(n, getRolledValue(this.internalGet(n), n2, n3, n4));
    }
    
    @Override
    public String getDisplayName(final int n, final int n2, final Locale locale) {
        if (!this.checkDisplayNameParams(n, n2, 1, 4, locale, 647)) {
            return null;
        }
        final int value = this.get(n);
        if (n == 1 && (this.getBaseStyle(n2) != 2 || value != 1 || this.get(0) == 0)) {
            return null;
        }
        String retrieveFieldValueName = CalendarDataUtility.retrieveFieldValueName(this.getCalendarType(), n, value, n2, locale);
        if ((retrieveFieldValueName == null || retrieveFieldValueName.isEmpty()) && n == 0 && value < JapaneseImperialCalendar.eras.length) {
            final Era era = JapaneseImperialCalendar.eras[value];
            retrieveFieldValueName = ((n2 == 1) ? era.getAbbreviation() : era.getName());
        }
        return retrieveFieldValueName;
    }
    
    @Override
    public Map<String, Integer> getDisplayNames(final int n, final int n2, final Locale locale) {
        if (!this.checkDisplayNameParams(n, n2, 0, 4, locale, 647)) {
            return null;
        }
        final Map<String, Integer> retrieveFieldValueNames = CalendarDataUtility.retrieveFieldValueNames(this.getCalendarType(), n, n2, locale);
        if (retrieveFieldValueNames != null && n == 0) {
            int n3 = retrieveFieldValueNames.size();
            if (n2 == 0) {
                final HashSet set = new HashSet();
                final Iterator iterator = retrieveFieldValueNames.keySet().iterator();
                while (iterator.hasNext()) {
                    set.add(retrieveFieldValueNames.get(iterator.next()));
                }
                n3 = set.size();
            }
            if (n3 < JapaneseImperialCalendar.eras.length) {
                final int baseStyle = this.getBaseStyle(n2);
                for (int i = n3; i < JapaneseImperialCalendar.eras.length; ++i) {
                    final Era era = JapaneseImperialCalendar.eras[i];
                    if (baseStyle == 0 || baseStyle == 1 || baseStyle == 4) {
                        retrieveFieldValueNames.put(era.getAbbreviation(), i);
                    }
                    if (baseStyle == 0 || baseStyle == 2) {
                        retrieveFieldValueNames.put(era.getName(), i);
                    }
                }
            }
        }
        return retrieveFieldValueNames;
    }
    
    @Override
    public int getMinimum(final int n) {
        return JapaneseImperialCalendar.MIN_VALUES[n];
    }
    
    @Override
    public int getMaximum(final int n) {
        switch (n) {
            case 1: {
                return Math.max(JapaneseImperialCalendar.LEAST_MAX_VALUES[1], JapaneseImperialCalendar.jcal.getCalendarDate(Long.MAX_VALUE, this.getZone()).getYear());
            }
            default: {
                return JapaneseImperialCalendar.MAX_VALUES[n];
            }
        }
    }
    
    @Override
    public int getGreatestMinimum(final int n) {
        return (n == 1) ? 1 : JapaneseImperialCalendar.MIN_VALUES[n];
    }
    
    @Override
    public int getLeastMaximum(final int n) {
        switch (n) {
            case 1: {
                return Math.min(JapaneseImperialCalendar.LEAST_MAX_VALUES[1], this.getMaximum(1));
            }
            default: {
                return JapaneseImperialCalendar.LEAST_MAX_VALUES[n];
            }
        }
    }
    
    @Override
    public int getActualMinimum(final int n) {
        if (!Calendar.isFieldSet(14, n)) {
            return this.getMinimum(n);
        }
        int minimum = 0;
        final LocalGregorianCalendar.Date calendarDate = JapaneseImperialCalendar.jcal.getCalendarDate(this.getNormalizedCalendar().getTimeInMillis(), this.getZone());
        final int eraIndex = getEraIndex(calendarDate);
        switch (n) {
            case 1: {
                if (eraIndex <= 0) {
                    minimum = this.getMinimum(n);
                    final LocalGregorianCalendar.Date calendarDate2 = JapaneseImperialCalendar.jcal.getCalendarDate(Long.MIN_VALUE, this.getZone());
                    int year = calendarDate2.getYear();
                    if (year > 400) {
                        year -= 400;
                    }
                    calendarDate.setYear(year);
                    JapaneseImperialCalendar.jcal.normalize(calendarDate);
                    if (this.getYearOffsetInMillis(calendarDate) < this.getYearOffsetInMillis(calendarDate2)) {
                        ++minimum;
                    }
                    break;
                }
                minimum = 1;
                final LocalGregorianCalendar.Date calendarDate3 = JapaneseImperialCalendar.jcal.getCalendarDate(JapaneseImperialCalendar.eras[eraIndex].getSince(this.getZone()), this.getZone());
                calendarDate.setYear(calendarDate3.getYear());
                JapaneseImperialCalendar.jcal.normalize(calendarDate);
                assert calendarDate.isLeapYear() == calendarDate3.isLeapYear();
                if (this.getYearOffsetInMillis(calendarDate) < this.getYearOffsetInMillis(calendarDate3)) {
                    ++minimum;
                }
                break;
            }
            case 2: {
                if (eraIndex > 1 && calendarDate.getYear() == 1) {
                    final LocalGregorianCalendar.Date calendarDate4 = JapaneseImperialCalendar.jcal.getCalendarDate(JapaneseImperialCalendar.eras[eraIndex].getSince(this.getZone()), this.getZone());
                    minimum = calendarDate4.getMonth() - 1;
                    if (calendarDate.getDayOfMonth() < calendarDate4.getDayOfMonth()) {
                        ++minimum;
                    }
                    break;
                }
                break;
            }
            case 3: {
                minimum = 1;
                final LocalGregorianCalendar.Date calendarDate5 = JapaneseImperialCalendar.jcal.getCalendarDate(Long.MIN_VALUE, this.getZone());
                calendarDate5.addYear(400);
                JapaneseImperialCalendar.jcal.normalize(calendarDate5);
                calendarDate.setEra(calendarDate5.getEra());
                calendarDate.setYear(calendarDate5.getYear());
                JapaneseImperialCalendar.jcal.normalize(calendarDate);
                final long fixedDate = JapaneseImperialCalendar.jcal.getFixedDate(calendarDate5);
                final long fixedDate2 = JapaneseImperialCalendar.jcal.getFixedDate(calendarDate);
                final long n2 = fixedDate2 - 7 * (this.getWeekNumber(fixedDate, fixedDate2) - 1);
                if (n2 < fixedDate || (n2 == fixedDate && calendarDate.getTimeOfDay() < calendarDate5.getTimeOfDay())) {
                    ++minimum;
                    break;
                }
                break;
            }
        }
        return minimum;
    }
    
    @Override
    public int getActualMaximum(final int n) {
        if ((0x1FE81 & 1 << n) != 0x0) {
            return this.getMaximum(n);
        }
        JapaneseImperialCalendar normalizedCalendar = this.getNormalizedCalendar();
        final LocalGregorianCalendar.Date jdate = normalizedCalendar.jdate;
        jdate.getNormalizedYear();
        int year = 0;
        switch (n) {
            case 2: {
                year = 11;
                if (this.isTransitionYear(jdate.getNormalizedYear())) {
                    int eraIndex = getEraIndex(jdate);
                    if (jdate.getYear() != 1) {
                        ++eraIndex;
                        assert eraIndex < JapaneseImperialCalendar.eras.length;
                    }
                    final long n2 = JapaneseImperialCalendar.sinceFixedDates[eraIndex];
                    if (normalizedCalendar.cachedFixedDate < n2) {
                        final LocalGregorianCalendar.Date date = (LocalGregorianCalendar.Date)jdate.clone();
                        JapaneseImperialCalendar.jcal.getCalendarDateFromFixedDate(date, n2 - 1L);
                        year = date.getMonth() - 1;
                    }
                    break;
                }
                final LocalGregorianCalendar.Date calendarDate = JapaneseImperialCalendar.jcal.getCalendarDate(Long.MAX_VALUE, this.getZone());
                if (jdate.getEra() == calendarDate.getEra() && jdate.getYear() == calendarDate.getYear()) {
                    year = calendarDate.getMonth() - 1;
                }
                break;
            }
            case 5: {
                year = JapaneseImperialCalendar.jcal.getMonthLength(jdate);
                break;
            }
            case 6: {
                if (this.isTransitionYear(jdate.getNormalizedYear())) {
                    int eraIndex2 = getEraIndex(jdate);
                    if (jdate.getYear() != 1) {
                        ++eraIndex2;
                        assert eraIndex2 < JapaneseImperialCalendar.eras.length;
                    }
                    final long n3 = JapaneseImperialCalendar.sinceFixedDates[eraIndex2];
                    final long cachedFixedDate = normalizedCalendar.cachedFixedDate;
                    final Gregorian.Date calendarDate2 = JapaneseImperialCalendar.gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
                    calendarDate2.setDate(jdate.getNormalizedYear(), 1, 1);
                    if (cachedFixedDate < n3) {
                        year = (int)(n3 - JapaneseImperialCalendar.gcal.getFixedDate(calendarDate2));
                    }
                    else {
                        calendarDate2.addYear(1);
                        year = (int)(JapaneseImperialCalendar.gcal.getFixedDate(calendarDate2) - n3);
                    }
                    break;
                }
                final LocalGregorianCalendar.Date calendarDate3 = JapaneseImperialCalendar.jcal.getCalendarDate(Long.MAX_VALUE, this.getZone());
                if (jdate.getEra() == calendarDate3.getEra() && jdate.getYear() == calendarDate3.getYear()) {
                    final long fixedDate = JapaneseImperialCalendar.jcal.getFixedDate(calendarDate3);
                    year = (int)(fixedDate - this.getFixedDateJan1(calendarDate3, fixedDate)) + 1;
                }
                else if (jdate.getYear() == this.getMinimum(1)) {
                    final LocalGregorianCalendar.Date calendarDate4 = JapaneseImperialCalendar.jcal.getCalendarDate(Long.MIN_VALUE, this.getZone());
                    final long fixedDate2 = JapaneseImperialCalendar.jcal.getFixedDate(calendarDate4);
                    calendarDate4.addYear(1);
                    calendarDate4.setMonth(1).setDayOfMonth(1);
                    JapaneseImperialCalendar.jcal.normalize(calendarDate4);
                    year = (int)(JapaneseImperialCalendar.jcal.getFixedDate(calendarDate4) - fixedDate2);
                }
                else {
                    year = JapaneseImperialCalendar.jcal.getYearLength(jdate);
                }
                break;
            }
            case 3: {
                if (this.isTransitionYear(jdate.getNormalizedYear())) {
                    if (normalizedCalendar == this) {
                        normalizedCalendar = (JapaneseImperialCalendar)normalizedCalendar.clone();
                    }
                    final int actualMaximum = this.getActualMaximum(6);
                    normalizedCalendar.set(6, actualMaximum);
                    year = normalizedCalendar.get(3);
                    if (year == 1 && actualMaximum > 7) {
                        normalizedCalendar.add(3, -1);
                        year = normalizedCalendar.get(3);
                    }
                    break;
                }
                final LocalGregorianCalendar.Date calendarDate5 = JapaneseImperialCalendar.jcal.getCalendarDate(Long.MAX_VALUE, this.getZone());
                if (jdate.getEra() == calendarDate5.getEra() && jdate.getYear() == calendarDate5.getYear()) {
                    final long fixedDate3 = JapaneseImperialCalendar.jcal.getFixedDate(calendarDate5);
                    year = this.getWeekNumber(this.getFixedDateJan1(calendarDate5, fixedDate3), fixedDate3);
                    break;
                }
                if (jdate.getEra() == null && jdate.getYear() == this.getMinimum(1)) {
                    final LocalGregorianCalendar.Date calendarDate6 = JapaneseImperialCalendar.jcal.getCalendarDate(Long.MIN_VALUE, this.getZone());
                    calendarDate6.addYear(400);
                    JapaneseImperialCalendar.jcal.normalize(calendarDate6);
                    calendarDate5.setEra(calendarDate6.getEra());
                    calendarDate5.setDate(calendarDate6.getYear() + 1, 1, 1);
                    JapaneseImperialCalendar.jcal.normalize(calendarDate5);
                    final long fixedDate4 = JapaneseImperialCalendar.jcal.getFixedDate(calendarDate6);
                    final long fixedDate5 = JapaneseImperialCalendar.jcal.getFixedDate(calendarDate5);
                    long dayOfWeekDateOnOrBefore = AbstractCalendar.getDayOfWeekDateOnOrBefore(fixedDate5 + 6L, this.getFirstDayOfWeek());
                    if ((int)(dayOfWeekDateOnOrBefore - fixedDate5) >= this.getMinimalDaysInFirstWeek()) {
                        dayOfWeekDateOnOrBefore -= 7L;
                    }
                    year = this.getWeekNumber(fixedDate4, dayOfWeekDateOnOrBefore);
                    break;
                }
                final Gregorian.Date calendarDate7 = JapaneseImperialCalendar.gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
                calendarDate7.setDate(jdate.getNormalizedYear(), 1, 1);
                int n4 = JapaneseImperialCalendar.gcal.getDayOfWeek(calendarDate7) - this.getFirstDayOfWeek();
                if (n4 < 0) {
                    n4 += 7;
                }
                year = 52;
                final int n5 = n4 + this.getMinimalDaysInFirstWeek() - 1;
                if (n5 == 6 || (jdate.isLeapYear() && (n5 == 5 || n5 == 12))) {
                    ++year;
                }
                break;
            }
            case 4: {
                final LocalGregorianCalendar.Date calendarDate8 = JapaneseImperialCalendar.jcal.getCalendarDate(Long.MAX_VALUE, this.getZone());
                if (jdate.getEra() != calendarDate8.getEra() || jdate.getYear() != calendarDate8.getYear()) {
                    final Gregorian.Date calendarDate9 = JapaneseImperialCalendar.gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
                    calendarDate9.setDate(jdate.getNormalizedYear(), jdate.getMonth(), 1);
                    final int dayOfWeek = JapaneseImperialCalendar.gcal.getDayOfWeek(calendarDate9);
                    final int monthLength = JapaneseImperialCalendar.gcal.getMonthLength(calendarDate9);
                    int n6 = dayOfWeek - this.getFirstDayOfWeek();
                    if (n6 < 0) {
                        n6 += 7;
                    }
                    final int n7 = 7 - n6;
                    year = 3;
                    if (n7 >= this.getMinimalDaysInFirstWeek()) {
                        ++year;
                    }
                    final int n8 = monthLength - (n7 + 21);
                    if (n8 > 0) {
                        ++year;
                        if (n8 > 7) {
                            ++year;
                        }
                    }
                }
                else {
                    final long fixedDate6 = JapaneseImperialCalendar.jcal.getFixedDate(calendarDate8);
                    year = this.getWeekNumber(fixedDate6 - calendarDate8.getDayOfMonth() + 1L, fixedDate6);
                }
                break;
            }
            case 8: {
                final int dayOfWeek2 = jdate.getDayOfWeek();
                final BaseCalendar.Date date2 = (BaseCalendar.Date)jdate.clone();
                final int monthLength2 = JapaneseImperialCalendar.jcal.getMonthLength(date2);
                date2.setDayOfMonth(1);
                JapaneseImperialCalendar.jcal.normalize(date2);
                int n9 = dayOfWeek2 - date2.getDayOfWeek();
                if (n9 < 0) {
                    n9 += 7;
                }
                year = (monthLength2 - n9 + 6) / 7;
                break;
            }
            case 1: {
                final LocalGregorianCalendar.Date calendarDate10 = JapaneseImperialCalendar.jcal.getCalendarDate(normalizedCalendar.getTimeInMillis(), this.getZone());
                final int eraIndex3 = getEraIndex(jdate);
                LocalGregorianCalendar.Date date3;
                if (eraIndex3 == JapaneseImperialCalendar.eras.length - 1) {
                    date3 = JapaneseImperialCalendar.jcal.getCalendarDate(Long.MAX_VALUE, this.getZone());
                    year = date3.getYear();
                    if (year > 400) {
                        calendarDate10.setYear(year - 400);
                    }
                }
                else {
                    date3 = JapaneseImperialCalendar.jcal.getCalendarDate(JapaneseImperialCalendar.eras[eraIndex3 + 1].getSince(this.getZone()) - 1L, this.getZone());
                    year = date3.getYear();
                    calendarDate10.setYear(year);
                }
                JapaneseImperialCalendar.jcal.normalize(calendarDate10);
                if (this.getYearOffsetInMillis(calendarDate10) > this.getYearOffsetInMillis(date3)) {
                    --year;
                }
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(n);
            }
        }
        return year;
    }
    
    private long getYearOffsetInMillis(final CalendarDate calendarDate) {
        return (JapaneseImperialCalendar.jcal.getDayOfYear(calendarDate) - 1L) * 86400000L + calendarDate.getTimeOfDay() - calendarDate.getZoneOffset();
    }
    
    @Override
    public Object clone() {
        final JapaneseImperialCalendar japaneseImperialCalendar = (JapaneseImperialCalendar)super.clone();
        japaneseImperialCalendar.jdate = (LocalGregorianCalendar.Date)this.jdate.clone();
        japaneseImperialCalendar.originalFields = null;
        japaneseImperialCalendar.zoneOffsets = null;
        return japaneseImperialCalendar;
    }
    
    @Override
    public TimeZone getTimeZone() {
        final TimeZone timeZone = super.getTimeZone();
        this.jdate.setZone(timeZone);
        return timeZone;
    }
    
    @Override
    public void setTimeZone(final TimeZone timeZone) {
        super.setTimeZone(timeZone);
        this.jdate.setZone(timeZone);
    }
    
    @Override
    protected void computeFields() {
        int setStateFields;
        if (this.isPartiallyNormalized()) {
            setStateFields = this.getSetStateFields();
            final int n = ~setStateFields & 0x1FFFF;
            if (n != 0 || this.cachedFixedDate == Long.MIN_VALUE) {
                setStateFields |= this.computeFields(n, setStateFields & 0x18000);
                assert setStateFields == 131071;
            }
        }
        else {
            setStateFields = 131071;
            this.computeFields(setStateFields, 0);
        }
        this.setFieldsComputed(setStateFields);
    }
    
    private int computeFields(final int n, final int n2) {
        int n3 = 0;
        final TimeZone zone = this.getZone();
        if (this.zoneOffsets == null) {
            this.zoneOffsets = new int[2];
        }
        if (n2 != 98304) {
            if (zone instanceof ZoneInfo) {
                n3 = ((ZoneInfo)zone).getOffsets(this.time, this.zoneOffsets);
            }
            else {
                n3 = zone.getOffset(this.time);
                this.zoneOffsets[0] = zone.getRawOffset();
                this.zoneOffsets[1] = n3 - this.zoneOffsets[0];
            }
        }
        if (n2 != 0) {
            if (Calendar.isFieldSet(n2, 15)) {
                this.zoneOffsets[0] = this.internalGet(15);
            }
            if (Calendar.isFieldSet(n2, 16)) {
                this.zoneOffsets[1] = this.internalGet(16);
            }
            n3 = this.zoneOffsets[0] + this.zoneOffsets[1];
        }
        final long n4 = n3 / 86400000L;
        final int n5 = n3 % 86400000;
        long n6 = n4 + this.time / 86400000L;
        int i = n5 + (int)(this.time % 86400000L);
        if (i >= 86400000L) {
            i -= (int)86400000L;
            ++n6;
        }
        else {
            while (i < 0) {
                i += (int)86400000L;
                --n6;
            }
        }
        final long cachedFixedDate = n6 + 719163L;
        if (cachedFixedDate != this.cachedFixedDate || cachedFixedDate < 0L) {
            JapaneseImperialCalendar.jcal.getCalendarDateFromFixedDate(this.jdate, cachedFixedDate);
            this.cachedFixedDate = cachedFixedDate;
        }
        final int eraIndex = getEraIndex(this.jdate);
        final int year = this.jdate.getYear();
        this.internalSet(0, eraIndex);
        this.internalSet(1, year);
        int n7 = n | 0x3;
        final int n8 = this.jdate.getMonth() - 1;
        final int dayOfMonth = this.jdate.getDayOfMonth();
        if ((n & 0xA4) != 0x0) {
            this.internalSet(2, n8);
            this.internalSet(5, dayOfMonth);
            this.internalSet(7, this.jdate.getDayOfWeek());
            n7 |= 0xA4;
        }
        if ((n & 0x7E00) != 0x0) {
            if (i != 0) {
                final int n9 = i / 3600000;
                this.internalSet(11, n9);
                this.internalSet(9, n9 / 12);
                this.internalSet(10, n9 % 12);
                final int n10 = i % 3600000;
                this.internalSet(12, n10 / 60000);
                final int n11 = n10 % 60000;
                this.internalSet(13, n11 / 1000);
                this.internalSet(14, n11 % 1000);
            }
            else {
                this.internalSet(11, 0);
                this.internalSet(9, 0);
                this.internalSet(10, 0);
                this.internalSet(12, 0);
                this.internalSet(13, 0);
                this.internalSet(14, 0);
            }
            n7 |= 0x7E00;
        }
        if ((n & 0x18000) != 0x0) {
            this.internalSet(15, this.zoneOffsets[0]);
            this.internalSet(16, this.zoneOffsets[1]);
            n7 |= 0x18000;
        }
        if ((n & 0x158) != 0x0) {
            final int normalizedYear = this.jdate.getNormalizedYear();
            final boolean transitionYear = this.isTransitionYear(this.jdate.getNormalizedYear());
            long n12;
            int n13;
            if (transitionYear) {
                n12 = this.getFixedDateJan1(this.jdate, cachedFixedDate);
                n13 = (int)(cachedFixedDate - n12) + 1;
            }
            else if (normalizedYear == JapaneseImperialCalendar.MIN_VALUES[1]) {
                n12 = JapaneseImperialCalendar.jcal.getFixedDate(JapaneseImperialCalendar.jcal.getCalendarDate(Long.MIN_VALUE, this.getZone()));
                n13 = (int)(cachedFixedDate - n12) + 1;
            }
            else {
                n13 = (int)JapaneseImperialCalendar.jcal.getDayOfYear(this.jdate);
                n12 = cachedFixedDate - n13 + 1L;
            }
            final long n14 = transitionYear ? this.getFixedDateMonth1(this.jdate, cachedFixedDate) : (cachedFixedDate - dayOfMonth + 1L);
            this.internalSet(6, n13);
            this.internalSet(8, (dayOfMonth - 1) / 7 + 1);
            int n15 = this.getWeekNumber(n12, cachedFixedDate);
            if (n15 == 0) {
                final long n16 = n12 - 1L;
                final LocalGregorianCalendar.Date calendarDate = getCalendarDate(n16);
                long n17;
                if (!transitionYear && !this.isTransitionYear(calendarDate.getNormalizedYear())) {
                    n17 = n12 - 365L;
                    if (calendarDate.isLeapYear()) {
                        --n17;
                    }
                }
                else if (transitionYear) {
                    if (this.jdate.getYear() == 1) {
                        if (eraIndex > 5) {
                            final CalendarDate sinceDate = JapaneseImperialCalendar.eras[eraIndex - 1].getSinceDate();
                            if (normalizedYear == sinceDate.getYear()) {
                                calendarDate.setMonth(sinceDate.getMonth()).setDayOfMonth(sinceDate.getDayOfMonth());
                            }
                        }
                        else {
                            calendarDate.setMonth(1).setDayOfMonth(1);
                        }
                        JapaneseImperialCalendar.jcal.normalize(calendarDate);
                        n17 = JapaneseImperialCalendar.jcal.getFixedDate(calendarDate);
                    }
                    else {
                        n17 = n12 - 365L;
                        if (calendarDate.isLeapYear()) {
                            --n17;
                        }
                    }
                }
                else {
                    final CalendarDate sinceDate2 = JapaneseImperialCalendar.eras[getEraIndex(this.jdate)].getSinceDate();
                    calendarDate.setMonth(sinceDate2.getMonth()).setDayOfMonth(sinceDate2.getDayOfMonth());
                    JapaneseImperialCalendar.jcal.normalize(calendarDate);
                    n17 = JapaneseImperialCalendar.jcal.getFixedDate(calendarDate);
                }
                n15 = this.getWeekNumber(n17, n16);
            }
            else if (!transitionYear) {
                if (n15 >= 52) {
                    long n18 = n12 + 365L;
                    if (this.jdate.isLeapYear()) {
                        ++n18;
                    }
                    final long dayOfWeekDateOnOrBefore = AbstractCalendar.getDayOfWeekDateOnOrBefore(n18 + 6L, this.getFirstDayOfWeek());
                    if ((int)(dayOfWeekDateOnOrBefore - n18) >= this.getMinimalDaysInFirstWeek() && cachedFixedDate >= dayOfWeekDateOnOrBefore - 7L) {
                        n15 = 1;
                    }
                }
            }
            else {
                final LocalGregorianCalendar.Date date = (LocalGregorianCalendar.Date)this.jdate.clone();
                long n19;
                if (this.jdate.getYear() == 1) {
                    date.addYear(1);
                    date.setMonth(1).setDayOfMonth(1);
                    n19 = JapaneseImperialCalendar.jcal.getFixedDate(date);
                }
                else {
                    final int n20 = getEraIndex(date) + 1;
                    final CalendarDate sinceDate3 = JapaneseImperialCalendar.eras[n20].getSinceDate();
                    date.setEra(JapaneseImperialCalendar.eras[n20]);
                    date.setDate(1, sinceDate3.getMonth(), sinceDate3.getDayOfMonth());
                    JapaneseImperialCalendar.jcal.normalize(date);
                    n19 = JapaneseImperialCalendar.jcal.getFixedDate(date);
                }
                final long dayOfWeekDateOnOrBefore2 = AbstractCalendar.getDayOfWeekDateOnOrBefore(n19 + 6L, this.getFirstDayOfWeek());
                if ((int)(dayOfWeekDateOnOrBefore2 - n19) >= this.getMinimalDaysInFirstWeek() && cachedFixedDate >= dayOfWeekDateOnOrBefore2 - 7L) {
                    n15 = 1;
                }
            }
            this.internalSet(3, n15);
            this.internalSet(4, this.getWeekNumber(n14, cachedFixedDate));
            n7 |= 0x158;
        }
        return n7;
    }
    
    private int getWeekNumber(final long n, final long n2) {
        long dayOfWeekDateOnOrBefore = AbstractCalendar.getDayOfWeekDateOnOrBefore(n + 6L, this.getFirstDayOfWeek());
        final int n3 = (int)(dayOfWeekDateOnOrBefore - n);
        assert n3 <= 7;
        if (n3 >= this.getMinimalDaysInFirstWeek()) {
            dayOfWeekDateOnOrBefore -= 7L;
        }
        final int n4 = (int)(n2 - dayOfWeekDateOnOrBefore);
        if (n4 >= 0) {
            return n4 / 7 + 1;
        }
        return CalendarUtils.floorDivide(n4, 7) + 1;
    }
    
    @Override
    protected void computeTime() {
        if (!this.isLenient()) {
            if (this.originalFields == null) {
                this.originalFields = new int[17];
            }
            for (int i = 0; i < 17; ++i) {
                final int internalGet = this.internalGet(i);
                if (this.isExternallySet(i) && (internalGet < this.getMinimum(i) || internalGet > this.getMaximum(i))) {
                    throw new IllegalArgumentException(Calendar.getFieldName(i));
                }
                this.originalFields[i] = internalGet;
            }
        }
        final int selectFields = this.selectFields();
        int n;
        int internalGet2;
        if (this.isSet(0)) {
            n = this.internalGet(0);
            internalGet2 = (this.isSet(1) ? this.internalGet(1) : 1);
        }
        else if (this.isSet(1)) {
            n = JapaneseImperialCalendar.currentEra;
            internalGet2 = this.internalGet(1);
        }
        else {
            n = 3;
            internalGet2 = 45;
        }
        final long n2 = 0L;
        long n3;
        if (Calendar.isFieldSet(selectFields, 11)) {
            n3 = n2 + this.internalGet(11);
        }
        else {
            n3 = n2 + this.internalGet(10);
            if (Calendar.isFieldSet(selectFields, 9)) {
                n3 += 12 * this.internalGet(9);
            }
        }
        final long n4 = ((n3 * 60L + this.internalGet(12)) * 60L + this.internalGet(13)) * 1000L + this.internalGet(14);
        long n5;
        long n6;
        for (n5 = n4 / 86400000L, n6 = n4 % 86400000L; n6 < 0L; n6 += 86400000L, --n5) {}
        final long n7 = (n5 + this.getFixedDate(n, internalGet2, selectFields) - 719163L) * 86400000L + n6;
        final TimeZone zone = this.getZone();
        if (this.zoneOffsets == null) {
            this.zoneOffsets = new int[2];
        }
        final int n8 = selectFields & 0x18000;
        if (n8 != 98304) {
            if (zone instanceof ZoneInfo) {
                ((ZoneInfo)zone).getOffsetsByWall(n7, this.zoneOffsets);
            }
            else {
                zone.getOffsets(n7 - zone.getRawOffset(), this.zoneOffsets);
            }
        }
        if (n8 != 0) {
            if (Calendar.isFieldSet(n8, 15)) {
                this.zoneOffsets[0] = this.internalGet(15);
            }
            if (Calendar.isFieldSet(n8, 16)) {
                this.zoneOffsets[1] = this.internalGet(16);
            }
        }
        this.time = n7 - (this.zoneOffsets[0] + this.zoneOffsets[1]);
        final int computeFields = this.computeFields(selectFields | this.getSetStateFields(), n8);
        if (!this.isLenient()) {
            for (int j = 0; j < 17; ++j) {
                if (this.isExternallySet(j)) {
                    if (this.originalFields[j] != this.internalGet(j)) {
                        final int internalGet3 = this.internalGet(j);
                        System.arraycopy(this.originalFields, 0, this.fields, 0, this.fields.length);
                        throw new IllegalArgumentException(Calendar.getFieldName(j) + "=" + internalGet3 + ", expected " + this.originalFields[j]);
                    }
                }
            }
        }
        this.setFieldsNormalized(computeFields);
    }
    
    private long getFixedDate(final int n, int n2, final int n3) {
        int internalGet = 0;
        int n4 = 1;
        if (Calendar.isFieldSet(n3, 2)) {
            internalGet = this.internalGet(2);
            if (internalGet > 11) {
                n2 += internalGet / 12;
                internalGet %= 12;
            }
            else if (internalGet < 0) {
                final int[] array = { 0 };
                n2 += CalendarUtils.floorDivide(internalGet, 12, array);
                internalGet = array[0];
            }
        }
        else if (n2 == 1 && n != 0) {
            final CalendarDate sinceDate = JapaneseImperialCalendar.eras[n].getSinceDate();
            internalGet = sinceDate.getMonth() - 1;
            n4 = sinceDate.getDayOfMonth();
        }
        if (n2 == JapaneseImperialCalendar.MIN_VALUES[1]) {
            final LocalGregorianCalendar.Date calendarDate = JapaneseImperialCalendar.jcal.getCalendarDate(Long.MIN_VALUE, this.getZone());
            final int n5 = calendarDate.getMonth() - 1;
            if (internalGet < n5) {
                internalGet = n5;
            }
            if (internalGet == n5) {
                n4 = calendarDate.getDayOfMonth();
            }
        }
        final LocalGregorianCalendar.Date calendarDate2 = JapaneseImperialCalendar.jcal.newCalendarDate(TimeZone.NO_TIMEZONE);
        calendarDate2.setEra((n > 0) ? JapaneseImperialCalendar.eras[n] : null);
        calendarDate2.setDate(n2, internalGet + 1, n4);
        JapaneseImperialCalendar.jcal.normalize(calendarDate2);
        long n6 = JapaneseImperialCalendar.jcal.getFixedDate(calendarDate2);
        if (Calendar.isFieldSet(n3, 2)) {
            if (Calendar.isFieldSet(n3, 5)) {
                if (this.isSet(5)) {
                    n6 = n6 + this.internalGet(5) - n4;
                }
            }
            else if (Calendar.isFieldSet(n3, 4)) {
                long n7 = AbstractCalendar.getDayOfWeekDateOnOrBefore(n6 + 6L, this.getFirstDayOfWeek());
                if (n7 - n6 >= this.getMinimalDaysInFirstWeek()) {
                    n7 -= 7L;
                }
                if (Calendar.isFieldSet(n3, 7)) {
                    n7 = AbstractCalendar.getDayOfWeekDateOnOrBefore(n7 + 6L, this.internalGet(7));
                }
                n6 = n7 + 7 * (this.internalGet(4) - 1);
            }
            else {
                int n8;
                if (Calendar.isFieldSet(n3, 7)) {
                    n8 = this.internalGet(7);
                }
                else {
                    n8 = this.getFirstDayOfWeek();
                }
                int internalGet2;
                if (Calendar.isFieldSet(n3, 8)) {
                    internalGet2 = this.internalGet(8);
                }
                else {
                    internalGet2 = 1;
                }
                if (internalGet2 >= 0) {
                    n6 = AbstractCalendar.getDayOfWeekDateOnOrBefore(n6 + 7 * internalGet2 - 1L, n8);
                }
                else {
                    n6 = AbstractCalendar.getDayOfWeekDateOnOrBefore(n6 + (this.monthLength(internalGet, n2) + 7 * (internalGet2 + 1)) - 1L, n8);
                }
            }
        }
        else if (Calendar.isFieldSet(n3, 6)) {
            if (this.isTransitionYear(calendarDate2.getNormalizedYear())) {
                n6 = this.getFixedDateJan1(calendarDate2, n6);
            }
            n6 = n6 + this.internalGet(6) - 1L;
        }
        else {
            long n9 = AbstractCalendar.getDayOfWeekDateOnOrBefore(n6 + 6L, this.getFirstDayOfWeek());
            if (n9 - n6 >= this.getMinimalDaysInFirstWeek()) {
                n9 -= 7L;
            }
            if (Calendar.isFieldSet(n3, 7)) {
                final int internalGet3 = this.internalGet(7);
                if (internalGet3 != this.getFirstDayOfWeek()) {
                    n9 = AbstractCalendar.getDayOfWeekDateOnOrBefore(n9 + 6L, internalGet3);
                }
            }
            n6 = n9 + 7L * (this.internalGet(3) - 1L);
        }
        return n6;
    }
    
    private long getFixedDateJan1(final LocalGregorianCalendar.Date date, final long n) {
        date.getEra();
        if (date.getEra() != null && date.getYear() == 1) {
            for (int i = getEraIndex(date); i > 0; --i) {
                final long fixedDate = JapaneseImperialCalendar.gcal.getFixedDate(JapaneseImperialCalendar.eras[i].getSinceDate());
                if (fixedDate <= n) {
                    return fixedDate;
                }
            }
        }
        final Gregorian.Date calendarDate = JapaneseImperialCalendar.gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
        calendarDate.setDate(date.getNormalizedYear(), 1, 1);
        return JapaneseImperialCalendar.gcal.getFixedDate(calendarDate);
    }
    
    private long getFixedDateMonth1(final LocalGregorianCalendar.Date date, final long n) {
        final int transitionEraIndex = getTransitionEraIndex(date);
        if (transitionEraIndex != -1) {
            final long n2 = JapaneseImperialCalendar.sinceFixedDates[transitionEraIndex];
            if (n2 <= n) {
                return n2;
            }
        }
        return n - date.getDayOfMonth() + 1L;
    }
    
    private static LocalGregorianCalendar.Date getCalendarDate(final long n) {
        final LocalGregorianCalendar.Date calendarDate = JapaneseImperialCalendar.jcal.newCalendarDate(TimeZone.NO_TIMEZONE);
        JapaneseImperialCalendar.jcal.getCalendarDateFromFixedDate(calendarDate, n);
        return calendarDate;
    }
    
    private int monthLength(final int n, final int n2) {
        return CalendarUtils.isGregorianLeapYear(n2) ? GregorianCalendar.LEAP_MONTH_LENGTH[n] : GregorianCalendar.MONTH_LENGTH[n];
    }
    
    private int monthLength(final int n) {
        assert this.jdate.isNormalized();
        return this.jdate.isLeapYear() ? GregorianCalendar.LEAP_MONTH_LENGTH[n] : GregorianCalendar.MONTH_LENGTH[n];
    }
    
    private int actualMonthLength() {
        int monthLength = JapaneseImperialCalendar.jcal.getMonthLength(this.jdate);
        final int transitionEraIndex = getTransitionEraIndex(this.jdate);
        if (transitionEraIndex == -1) {
            final long n = JapaneseImperialCalendar.sinceFixedDates[transitionEraIndex];
            final CalendarDate sinceDate = JapaneseImperialCalendar.eras[transitionEraIndex].getSinceDate();
            if (n <= this.cachedFixedDate) {
                monthLength -= sinceDate.getDayOfMonth() - 1;
            }
            else {
                monthLength = sinceDate.getDayOfMonth() - 1;
            }
        }
        return monthLength;
    }
    
    private static int getTransitionEraIndex(final LocalGregorianCalendar.Date date) {
        int eraIndex = getEraIndex(date);
        final CalendarDate sinceDate = JapaneseImperialCalendar.eras[eraIndex].getSinceDate();
        if (sinceDate.getYear() == date.getNormalizedYear() && sinceDate.getMonth() == date.getMonth()) {
            return eraIndex;
        }
        if (eraIndex < JapaneseImperialCalendar.eras.length - 1) {
            final CalendarDate sinceDate2 = JapaneseImperialCalendar.eras[++eraIndex].getSinceDate();
            if (sinceDate2.getYear() == date.getNormalizedYear() && sinceDate2.getMonth() == date.getMonth()) {
                return eraIndex;
            }
        }
        return -1;
    }
    
    private boolean isTransitionYear(final int n) {
        for (int i = JapaneseImperialCalendar.eras.length - 1; i > 0; --i) {
            final int year = JapaneseImperialCalendar.eras[i].getSinceDate().getYear();
            if (n == year) {
                return true;
            }
            if (n > year) {
                break;
            }
        }
        return false;
    }
    
    private static int getEraIndex(final LocalGregorianCalendar.Date date) {
        final Era era = date.getEra();
        for (int i = JapaneseImperialCalendar.eras.length - 1; i > 0; --i) {
            if (JapaneseImperialCalendar.eras[i] == era) {
                return i;
            }
        }
        return 0;
    }
    
    private JapaneseImperialCalendar getNormalizedCalendar() {
        JapaneseImperialCalendar japaneseImperialCalendar;
        if (this.isFullyNormalized()) {
            japaneseImperialCalendar = this;
        }
        else {
            japaneseImperialCalendar = (JapaneseImperialCalendar)this.clone();
            japaneseImperialCalendar.setLenient(true);
            japaneseImperialCalendar.complete();
        }
        return japaneseImperialCalendar;
    }
    
    private void pinDayOfMonth(final LocalGregorianCalendar.Date date) {
        final int year = date.getYear();
        final int dayOfMonth = date.getDayOfMonth();
        if (year != this.getMinimum(1)) {
            date.setDayOfMonth(1);
            JapaneseImperialCalendar.jcal.normalize(date);
            final int monthLength = JapaneseImperialCalendar.jcal.getMonthLength(date);
            if (dayOfMonth > monthLength) {
                date.setDayOfMonth(monthLength);
            }
            else {
                date.setDayOfMonth(dayOfMonth);
            }
            JapaneseImperialCalendar.jcal.normalize(date);
        }
        else {
            final LocalGregorianCalendar.Date calendarDate = JapaneseImperialCalendar.jcal.getCalendarDate(Long.MIN_VALUE, this.getZone());
            final LocalGregorianCalendar.Date calendarDate2 = JapaneseImperialCalendar.jcal.getCalendarDate(this.time, this.getZone());
            final long timeOfDay = calendarDate2.getTimeOfDay();
            calendarDate2.addYear(400);
            calendarDate2.setMonth(date.getMonth());
            calendarDate2.setDayOfMonth(1);
            JapaneseImperialCalendar.jcal.normalize(calendarDate2);
            final int monthLength2 = JapaneseImperialCalendar.jcal.getMonthLength(calendarDate2);
            if (dayOfMonth > monthLength2) {
                calendarDate2.setDayOfMonth(monthLength2);
            }
            else if (dayOfMonth < calendarDate.getDayOfMonth()) {
                calendarDate2.setDayOfMonth(calendarDate.getDayOfMonth());
            }
            else {
                calendarDate2.setDayOfMonth(dayOfMonth);
            }
            if (calendarDate2.getDayOfMonth() == calendarDate.getDayOfMonth() && timeOfDay < calendarDate.getTimeOfDay()) {
                calendarDate2.setDayOfMonth(Math.min(dayOfMonth + 1, monthLength2));
            }
            date.setDate(year, calendarDate2.getMonth(), calendarDate2.getDayOfMonth());
        }
    }
    
    private static int getRolledValue(final int n, int n2, final int n3, final int n4) {
        assert n >= n3 && n <= n4;
        final int n5 = n4 - n3 + 1;
        n2 %= n5;
        int n6 = n + n2;
        if (n6 > n4) {
            n6 -= n5;
        }
        else if (n6 < n3) {
            n6 += n5;
        }
        assert n6 >= n3 && n6 <= n4;
        return n6;
    }
    
    private int internalGetEra() {
        return this.isSet(0) ? this.internalGet(0) : JapaneseImperialCalendar.currentEra;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.jdate == null) {
            this.jdate = JapaneseImperialCalendar.jcal.newCalendarDate(this.getZone());
            this.cachedFixedDate = Long.MIN_VALUE;
        }
    }
    
    static {
        jcal = (LocalGregorianCalendar)CalendarSystem.forName("japanese");
        gcal = CalendarSystem.getGregorianCalendar();
        BEFORE_MEIJI_ERA = new Era("BeforeMeiji", "BM", Long.MIN_VALUE, false);
        MIN_VALUES = new int[] { 0, -292275055, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, -46800000, 0 };
        LEAST_MAX_VALUES = new int[] { 0, 0, 0, 0, 4, 28, 0, 7, 4, 1, 11, 23, 59, 59, 999, 50400000, 1200000 };
        MAX_VALUES = new int[] { 0, 292278994, 11, 53, 6, 31, 366, 7, 6, 1, 11, 23, 59, 59, 999, 50400000, 7200000 };
        final Era[] eras2 = JapaneseImperialCalendar.jcal.getEras();
        final int n = eras2.length + 1;
        eras = new Era[n];
        sinceFixedDates = new long[n];
        int currentEra2;
        int n2 = currentEra2 = 0;
        JapaneseImperialCalendar.sinceFixedDates[n2] = JapaneseImperialCalendar.gcal.getFixedDate(JapaneseImperialCalendar.BEFORE_MEIJI_ERA.getSinceDate());
        JapaneseImperialCalendar.eras[n2++] = JapaneseImperialCalendar.BEFORE_MEIJI_ERA;
        for (final Era era : eras2) {
            if (era.getSince(TimeZone.NO_TIMEZONE) < System.currentTimeMillis()) {
                currentEra2 = n2;
            }
            JapaneseImperialCalendar.sinceFixedDates[n2] = JapaneseImperialCalendar.gcal.getFixedDate(era.getSinceDate());
            JapaneseImperialCalendar.eras[n2++] = era;
        }
        currentEra = currentEra2;
        JapaneseImperialCalendar.LEAST_MAX_VALUES[0] = (JapaneseImperialCalendar.MAX_VALUES[0] = JapaneseImperialCalendar.eras.length - 1);
        int min = Integer.MAX_VALUE;
        int n3 = Integer.MAX_VALUE;
        final Gregorian.Date calendarDate = JapaneseImperialCalendar.gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
        for (int j = 1; j < JapaneseImperialCalendar.eras.length; ++j) {
            final long n4 = JapaneseImperialCalendar.sinceFixedDates[j];
            final CalendarDate sinceDate = JapaneseImperialCalendar.eras[j].getSinceDate();
            calendarDate.setDate(sinceDate.getYear(), 1, 1);
            final long fixedDate = JapaneseImperialCalendar.gcal.getFixedDate(calendarDate);
            if (n4 != fixedDate) {
                n3 = Math.min((int)(n4 - fixedDate) + 1, n3);
            }
            calendarDate.setDate(sinceDate.getYear(), 12, 31);
            final long fixedDate2 = JapaneseImperialCalendar.gcal.getFixedDate(calendarDate);
            if (n4 != fixedDate2) {
                n3 = Math.min((int)(fixedDate2 - n4) + 1, n3);
            }
            final LocalGregorianCalendar.Date calendarDate2 = getCalendarDate(n4 - 1L);
            int year = calendarDate2.getYear();
            if (calendarDate2.getMonth() != 1 || calendarDate2.getDayOfMonth() != 1) {
                --year;
            }
            min = Math.min(year, min);
        }
        JapaneseImperialCalendar.LEAST_MAX_VALUES[1] = min;
        JapaneseImperialCalendar.LEAST_MAX_VALUES[6] = n3;
    }
}
