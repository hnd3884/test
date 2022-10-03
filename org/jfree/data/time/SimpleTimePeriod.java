package org.jfree.data.time;

import java.util.Date;
import java.io.Serializable;

public class SimpleTimePeriod implements TimePeriod, Comparable, Serializable
{
    private static final long serialVersionUID = 8684672361131829554L;
    private Date start;
    private Date end;
    
    public SimpleTimePeriod(final long start, final long end) {
        this(new Date(start), new Date(end));
    }
    
    public SimpleTimePeriod(final Date start, final Date end) {
        if (start.getTime() > end.getTime()) {
            throw new IllegalArgumentException("Requires end >= start.");
        }
        this.start = start;
        this.end = end;
    }
    
    public Date getStart() {
        return this.start;
    }
    
    public Date getEnd() {
        return this.end;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimePeriod)) {
            return false;
        }
        final TimePeriod that = (TimePeriod)obj;
        return this.start.equals(that.getStart()) && this.end.equals(that.getEnd());
    }
    
    public int compareTo(final Object obj) {
        final TimePeriod that = (TimePeriod)obj;
        final long t0 = this.getStart().getTime();
        final long t2 = this.getEnd().getTime();
        final long m0 = t0 + (t2 - t0) / 2L;
        final long t3 = that.getStart().getTime();
        final long t4 = that.getEnd().getTime();
        final long m2 = t3 + (t4 - t3) / 2L;
        if (m0 < m2) {
            return -1;
        }
        if (m0 > m2) {
            return 1;
        }
        if (t0 < t3) {
            return -1;
        }
        if (t0 > t3) {
            return 1;
        }
        if (t2 < t4) {
            return -1;
        }
        if (t2 > t4) {
            return 1;
        }
        return 0;
    }
    
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.start.hashCode();
        result = 37 * result + this.end.hashCode();
        return result;
    }
}
