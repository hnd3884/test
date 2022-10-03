package org.jfree.data.time;

import java.util.Calendar;
import java.util.Date;
import java.io.Serializable;

public class FixedMillisecond extends RegularTimePeriod implements Serializable
{
    private static final long serialVersionUID = 7867521484545646931L;
    private Date time;
    
    public FixedMillisecond() {
        this(new Date());
    }
    
    public FixedMillisecond(final long millisecond) {
        this(new Date(millisecond));
    }
    
    public FixedMillisecond(final Date time) {
        this.time = time;
    }
    
    public Date getTime() {
        return this.time;
    }
    
    public RegularTimePeriod previous() {
        RegularTimePeriod result = null;
        final long t = this.time.getTime();
        if (t != Long.MIN_VALUE) {
            result = new FixedMillisecond(t - 1L);
        }
        return result;
    }
    
    public RegularTimePeriod next() {
        RegularTimePeriod result = null;
        final long t = this.time.getTime();
        if (t != Long.MAX_VALUE) {
            result = new FixedMillisecond(t + 1L);
        }
        return result;
    }
    
    public boolean equals(final Object object) {
        if (object instanceof FixedMillisecond) {
            final FixedMillisecond m = (FixedMillisecond)object;
            return this.time.equals(m.getTime());
        }
        return false;
    }
    
    public int hashCode() {
        return this.time.hashCode();
    }
    
    public int compareTo(final Object o1) {
        int result;
        if (o1 instanceof FixedMillisecond) {
            final FixedMillisecond t1 = (FixedMillisecond)o1;
            final long difference = this.time.getTime() - t1.time.getTime();
            if (difference > 0L) {
                result = 1;
            }
            else if (difference < 0L) {
                result = -1;
            }
            else {
                result = 0;
            }
        }
        else if (o1 instanceof RegularTimePeriod) {
            result = 0;
        }
        else {
            result = 1;
        }
        return result;
    }
    
    public long getFirstMillisecond() {
        return this.time.getTime();
    }
    
    public long getFirstMillisecond(final Calendar calendar) {
        return this.time.getTime();
    }
    
    public long getLastMillisecond() {
        return this.time.getTime();
    }
    
    public long getLastMillisecond(final Calendar calendar) {
        return this.time.getTime();
    }
    
    public long getMiddleMillisecond() {
        return this.time.getTime();
    }
    
    public long getMiddleMillisecond(final Calendar calendar) {
        return this.time.getTime();
    }
    
    public long getSerialIndex() {
        return this.time.getTime();
    }
}
