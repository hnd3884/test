package com.ocpsoft.pretty.time;

import java.util.Arrays;
import java.util.Collections;
import java.util.Collection;
import com.ocpsoft.pretty.time.units.Millennium;
import com.ocpsoft.pretty.time.units.Century;
import com.ocpsoft.pretty.time.units.Decade;
import com.ocpsoft.pretty.time.units.Year;
import com.ocpsoft.pretty.time.units.Month;
import com.ocpsoft.pretty.time.units.Week;
import com.ocpsoft.pretty.time.units.Day;
import com.ocpsoft.pretty.time.units.Hour;
import com.ocpsoft.pretty.time.units.Minute;
import com.ocpsoft.pretty.time.units.Second;
import com.ocpsoft.pretty.time.units.Millisecond;
import com.ocpsoft.pretty.time.units.JustNow;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;
import java.util.Date;

public class PrettyTime
{
    private volatile Date reference;
    private volatile List<TimeUnit> timeUnits;
    private volatile Locale locale;
    
    public PrettyTime() {
        this.locale = Locale.getDefault();
        this.initTimeUnits();
    }
    
    public PrettyTime(final Date reference) {
        this();
        this.setReference(reference);
    }
    
    public PrettyTime(final Locale locale) {
        this.locale = Locale.getDefault();
        this.setLocale(locale);
        this.initTimeUnits();
    }
    
    public PrettyTime(final Date reference, final Locale locale) {
        this(locale);
        this.setReference(reference);
    }
    
    public Duration approximateDuration(final Date then) {
        Date ref = this.reference;
        if (ref == null) {
            ref = new Date();
        }
        final long difference = then.getTime() - ref.getTime();
        return this.calculateDuration(difference);
    }
    
    private void initTimeUnits() {
        (this.timeUnits = new ArrayList<TimeUnit>()).add(new JustNow(this.locale));
        this.timeUnits.add(new Millisecond(this.locale));
        this.timeUnits.add(new Second(this.locale));
        this.timeUnits.add(new Minute(this.locale));
        this.timeUnits.add(new Hour(this.locale));
        this.timeUnits.add(new Day(this.locale));
        this.timeUnits.add(new Week(this.locale));
        this.timeUnits.add(new Month(this.locale));
        this.timeUnits.add(new Year(this.locale));
        this.timeUnits.add(new Decade(this.locale));
        this.timeUnits.add(new Century(this.locale));
        this.timeUnits.add(new Millennium(this.locale));
    }
    
    private Duration calculateDuration(final long difference) {
        final long absoluteDifference = Math.abs(difference);
        final List<TimeUnit> units = new ArrayList<TimeUnit>(this.timeUnits.size());
        units.addAll(this.timeUnits);
        final Duration result = new Duration();
        for (int i = 0; i < units.size(); ++i) {
            final TimeUnit unit = units.get(i);
            final long millisPerUnit = Math.abs(unit.getMillisPerUnit());
            long quantity = Math.abs(unit.getMaxQuantity());
            final boolean isLastUnit = i == units.size() - 1;
            if (0L == quantity && !isLastUnit) {
                quantity = units.get(i + 1).getMillisPerUnit() / unit.getMillisPerUnit();
            }
            if (millisPerUnit * quantity > absoluteDifference || isLastUnit) {
                result.setUnit(unit);
                if (millisPerUnit > absoluteDifference) {
                    result.setQuantity(this.getSign(difference, absoluteDifference));
                }
                else {
                    result.setQuantity(difference / millisPerUnit);
                }
                result.setDelta(difference - result.getQuantity() * millisPerUnit);
                break;
            }
        }
        return result;
    }
    
    private long getSign(final long difference, final long absoluteDifference) {
        if (0L > difference) {
            return -1L;
        }
        return 1L;
    }
    
    public List<Duration> calculatePreciseDuration(final Date then) {
        if (this.reference == null) {
            this.reference = new Date();
        }
        final List<Duration> result = new ArrayList<Duration>();
        final long difference = then.getTime() - this.reference.getTime();
        Duration duration = this.calculateDuration(difference);
        result.add(duration);
        while (0L < duration.getDelta()) {
            duration = this.calculateDuration(duration.getDelta());
            result.add(duration);
        }
        return result;
    }
    
    public String format(final Duration duration) {
        final TimeFormat format = duration.getUnit().getFormat();
        return format.format(duration);
    }
    
    public String format(Date then) {
        if (then == null) {
            then = new Date();
        }
        final Duration d = this.approximateDuration(then);
        return this.format(d);
    }
    
    public Date getReference() {
        return this.reference;
    }
    
    public void setReference(final Date timestamp) {
        this.reference = timestamp;
    }
    
    public List<TimeUnit> getUnits() {
        return Collections.unmodifiableList((List<? extends TimeUnit>)this.timeUnits);
    }
    
    public void setUnits(final List<TimeUnit> units) {
        this.timeUnits = units;
    }
    
    public void setUnits(final TimeUnit... units) {
        this.timeUnits = Arrays.asList(units);
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    public void setLocale(final Locale locale) {
        this.locale = locale;
    }
}
