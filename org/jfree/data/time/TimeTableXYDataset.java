package org.jfree.data.time;

import java.util.List;
import org.jfree.data.Range;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetChangeEvent;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;
import org.jfree.data.DefaultKeyedValues2D;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.DomainInfo;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.util.PublicCloneable;
import org.jfree.data.xy.AbstractIntervalXYDataset;

public class TimeTableXYDataset extends AbstractIntervalXYDataset implements Cloneable, PublicCloneable, IntervalXYDataset, DomainInfo, TableXYDataset
{
    private DefaultKeyedValues2D values;
    private boolean domainIsPointsInTime;
    private TimePeriodAnchor xPosition;
    private Calendar workingCalendar;
    
    public TimeTableXYDataset() {
        this(TimeZone.getDefault(), Locale.getDefault());
    }
    
    public TimeTableXYDataset(final TimeZone zone) {
        this(zone, Locale.getDefault());
    }
    
    public TimeTableXYDataset(final TimeZone zone, final Locale locale) {
        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        if (locale == null) {
            throw new IllegalArgumentException("Null 'locale' argument.");
        }
        this.values = new DefaultKeyedValues2D(true);
        this.workingCalendar = Calendar.getInstance(zone, locale);
        this.xPosition = TimePeriodAnchor.START;
    }
    
    public boolean getDomainIsPointsInTime() {
        return this.domainIsPointsInTime;
    }
    
    public void setDomainIsPointsInTime(final boolean flag) {
        this.domainIsPointsInTime = flag;
        this.notifyListeners(new DatasetChangeEvent(this, this));
    }
    
    public TimePeriodAnchor getXPosition() {
        return this.xPosition;
    }
    
    public void setXPosition(final TimePeriodAnchor anchor) {
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        this.xPosition = anchor;
        this.notifyListeners(new DatasetChangeEvent(this, this));
    }
    
    public void add(final TimePeriod period, final double y, final String seriesName) {
        this.add(period, new Double(y), seriesName, true);
    }
    
    public void add(final TimePeriod period, final Number y, final String seriesName, final boolean notify) {
        this.values.addValue(y, period, seriesName);
        if (notify) {
            this.fireDatasetChanged();
        }
    }
    
    public void remove(final TimePeriod period, final String seriesName) {
        this.remove(period, seriesName, true);
    }
    
    public void remove(final TimePeriod period, final String seriesName, final boolean notify) {
        this.values.removeValue(period, seriesName);
        if (notify) {
            this.fireDatasetChanged();
        }
    }
    
    public TimePeriod getTimePeriod(final int item) {
        return (TimePeriod)this.values.getRowKey(item);
    }
    
    public int getItemCount() {
        return this.values.getRowCount();
    }
    
    public int getItemCount(final int series) {
        return this.getItemCount();
    }
    
    public int getSeriesCount() {
        return this.values.getColumnCount();
    }
    
    public Comparable getSeriesKey(final int series) {
        return this.values.getColumnKey(series);
    }
    
    public Number getX(final int series, final int item) {
        return new Double(this.getXValue(series, item));
    }
    
    public double getXValue(final int series, final int item) {
        final TimePeriod period = (TimePeriod)this.values.getRowKey(item);
        return (double)this.getXValue(period);
    }
    
    public Number getStartX(final int series, final int item) {
        return new Double(this.getStartXValue(series, item));
    }
    
    public double getStartXValue(final int series, final int item) {
        final TimePeriod period = (TimePeriod)this.values.getRowKey(item);
        return (double)period.getStart().getTime();
    }
    
    public Number getEndX(final int series, final int item) {
        return new Double(this.getEndXValue(series, item));
    }
    
    public double getEndXValue(final int series, final int item) {
        final TimePeriod period = (TimePeriod)this.values.getRowKey(item);
        return (double)period.getEnd().getTime();
    }
    
    public Number getY(final int series, final int item) {
        return this.values.getValue(item, series);
    }
    
    public Number getStartY(final int series, final int item) {
        return this.getY(series, item);
    }
    
    public Number getEndY(final int series, final int item) {
        return this.getY(series, item);
    }
    
    private long getXValue(final TimePeriod period) {
        long result = 0L;
        if (this.xPosition == TimePeriodAnchor.START) {
            result = period.getStart().getTime();
        }
        else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
            final long t0 = period.getStart().getTime();
            final long t2 = period.getEnd().getTime();
            result = t0 + (t2 - t0) / 2L;
        }
        else if (this.xPosition == TimePeriodAnchor.END) {
            result = period.getEnd().getTime();
        }
        return result;
    }
    
    public double getDomainLowerBound(final boolean includeInterval) {
        double result = Double.NaN;
        final Range r = this.getDomainBounds(includeInterval);
        if (r != null) {
            result = r.getLowerBound();
        }
        return result;
    }
    
    public double getDomainUpperBound(final boolean includeInterval) {
        double result = Double.NaN;
        final Range r = this.getDomainBounds(includeInterval);
        if (r != null) {
            result = r.getUpperBound();
        }
        return result;
    }
    
    public Range getDomainBounds(final boolean includeInterval) {
        final List keys = this.values.getRowKeys();
        if (keys.isEmpty()) {
            return null;
        }
        final TimePeriod first = keys.get(0);
        final TimePeriod last = keys.get(keys.size() - 1);
        if (!includeInterval || this.domainIsPointsInTime) {
            return new Range((double)this.getXValue(first), (double)this.getXValue(last));
        }
        return new Range((double)first.getStart().getTime(), (double)last.getEnd().getTime());
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimeTableXYDataset)) {
            return false;
        }
        final TimeTableXYDataset that = (TimeTableXYDataset)obj;
        return this.domainIsPointsInTime == that.domainIsPointsInTime && this.xPosition == that.xPosition && this.workingCalendar.getTimeZone().equals(that.workingCalendar.getTimeZone()) && this.values.equals(that.values);
    }
    
    public Object clone() throws CloneNotSupportedException {
        final TimeTableXYDataset clone = (TimeTableXYDataset)super.clone();
        clone.values = (DefaultKeyedValues2D)this.values.clone();
        clone.workingCalendar = (Calendar)this.workingCalendar.clone();
        return clone;
    }
}
