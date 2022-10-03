package org.jfree.data.time;

import org.jfree.util.ObjectUtilities;
import org.jfree.data.Range;
import java.util.Iterator;
import java.util.Collections;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.List;
import java.io.Serializable;
import org.jfree.data.DomainInfo;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.AbstractIntervalXYDataset;

public class TimeSeriesCollection extends AbstractIntervalXYDataset implements XYDataset, IntervalXYDataset, DomainInfo, Serializable
{
    private static final long serialVersionUID = 834149929022371137L;
    private List data;
    private Calendar workingCalendar;
    private TimePeriodAnchor xPosition;
    private boolean domainIsPointsInTime;
    
    public TimeSeriesCollection() {
        this(null, TimeZone.getDefault());
    }
    
    public TimeSeriesCollection(final TimeZone zone) {
        this(null, zone);
    }
    
    public TimeSeriesCollection(final TimeSeries series) {
        this(series, TimeZone.getDefault());
    }
    
    public TimeSeriesCollection(final TimeSeries series, TimeZone zone) {
        if (zone == null) {
            zone = TimeZone.getDefault();
        }
        this.workingCalendar = Calendar.getInstance(zone);
        this.data = new ArrayList();
        if (series != null) {
            this.data.add(series);
            series.addChangeListener(this);
        }
        this.xPosition = TimePeriodAnchor.START;
        this.domainIsPointsInTime = true;
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
    
    public List getSeries() {
        return Collections.unmodifiableList((List<?>)this.data);
    }
    
    public int getSeriesCount() {
        return this.data.size();
    }
    
    public TimeSeries getSeries(final int series) {
        if (series < 0 || series >= this.getSeriesCount()) {
            throw new IllegalArgumentException("The 'series' argument is out of bounds (" + series + ").");
        }
        return this.data.get(series);
    }
    
    public TimeSeries getSeries(final String key) {
        TimeSeries result = null;
        final Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            final TimeSeries series = iterator.next();
            final Comparable k = series.getKey();
            if (k != null && k.equals(key)) {
                result = series;
            }
        }
        return result;
    }
    
    public Comparable getSeriesKey(final int series) {
        return this.getSeries(series).getKey();
    }
    
    public void addSeries(final TimeSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.add(series);
        series.addChangeListener(this);
        this.fireDatasetChanged();
    }
    
    public void removeSeries(final TimeSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.remove(series);
        series.removeChangeListener(this);
        this.fireDatasetChanged();
    }
    
    public void removeSeries(final int index) {
        final TimeSeries series = this.getSeries(index);
        if (series != null) {
            this.removeSeries(series);
        }
    }
    
    public void removeAllSeries() {
        for (int i = 0; i < this.data.size(); ++i) {
            final TimeSeries series = this.data.get(i);
            series.removeChangeListener(this);
        }
        this.data.clear();
        this.fireDatasetChanged();
    }
    
    public int getItemCount(final int series) {
        return this.getSeries(series).getItemCount();
    }
    
    public double getXValue(final int series, final int item) {
        final TimeSeries s = this.data.get(series);
        final TimeSeriesDataItem i = s.getDataItem(item);
        final RegularTimePeriod period = i.getPeriod();
        return (double)this.getX(period);
    }
    
    public Number getX(final int series, final int item) {
        final TimeSeries ts = this.data.get(series);
        final TimeSeriesDataItem dp = ts.getDataItem(item);
        final RegularTimePeriod period = dp.getPeriod();
        return new Long(this.getX(period));
    }
    
    protected synchronized long getX(final RegularTimePeriod period) {
        long result = 0L;
        if (this.xPosition == TimePeriodAnchor.START) {
            result = period.getFirstMillisecond(this.workingCalendar);
        }
        else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
            result = period.getMiddleMillisecond(this.workingCalendar);
        }
        else if (this.xPosition == TimePeriodAnchor.END) {
            result = period.getLastMillisecond(this.workingCalendar);
        }
        return result;
    }
    
    public synchronized Number getStartX(final int series, final int item) {
        final TimeSeries ts = this.data.get(series);
        final TimeSeriesDataItem dp = ts.getDataItem(item);
        return new Long(dp.getPeriod().getFirstMillisecond(this.workingCalendar));
    }
    
    public synchronized Number getEndX(final int series, final int item) {
        final TimeSeries ts = this.data.get(series);
        final TimeSeriesDataItem dp = ts.getDataItem(item);
        return new Long(dp.getPeriod().getLastMillisecond(this.workingCalendar));
    }
    
    public Number getY(final int series, final int item) {
        final TimeSeries ts = this.data.get(series);
        final TimeSeriesDataItem dp = ts.getDataItem(item);
        return dp.getValue();
    }
    
    public Number getStartY(final int series, final int item) {
        return this.getY(series, item);
    }
    
    public Number getEndY(final int series, final int item) {
        return this.getY(series, item);
    }
    
    public int[] getSurroundingItems(final int series, final long milliseconds) {
        final int[] result = { -1, -1 };
        final TimeSeries timeSeries = this.getSeries(series);
        for (int i = 0; i < timeSeries.getItemCount(); ++i) {
            final Number x = this.getX(series, i);
            final long m = x.longValue();
            if (m <= milliseconds) {
                result[0] = i;
            }
            if (m >= milliseconds) {
                result[1] = i;
                break;
            }
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
        Range result = null;
        final Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            final TimeSeries series = iterator.next();
            final int count = series.getItemCount();
            if (count > 0) {
                final RegularTimePeriod start = series.getTimePeriod(0);
                final RegularTimePeriod end = series.getTimePeriod(count - 1);
                Range temp;
                if (!includeInterval) {
                    temp = new Range((double)this.getX(start), (double)this.getX(end));
                }
                else {
                    temp = new Range((double)start.getFirstMillisecond(this.workingCalendar), (double)end.getLastMillisecond(this.workingCalendar));
                }
                result = Range.combine(result, temp);
            }
        }
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimeSeriesCollection)) {
            return false;
        }
        final TimeSeriesCollection that = (TimeSeriesCollection)obj;
        return this.xPosition == that.xPosition && this.domainIsPointsInTime == that.domainIsPointsInTime && ObjectUtilities.equal((Object)this.data, (Object)that.data);
    }
    
    public int hashCode() {
        int result = this.data.hashCode();
        result = 29 * result + ((this.workingCalendar != null) ? this.workingCalendar.hashCode() : 0);
        result = 29 * result + ((this.xPosition != null) ? this.xPosition.hashCode() : 0);
        result = 29 * result + (this.domainIsPointsInTime ? 1 : 0);
        return result;
    }
}
