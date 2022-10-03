package org.jfree.data.time;

import org.jfree.util.ObjectUtilities;
import java.util.Iterator;
import org.jfree.data.Range;
import org.jfree.data.general.SeriesChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import org.jfree.data.DomainInfo;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.AbstractIntervalXYDataset;

public class TimePeriodValuesCollection extends AbstractIntervalXYDataset implements IntervalXYDataset, DomainInfo, Serializable
{
    private static final long serialVersionUID = -3077934065236454199L;
    private List data;
    private TimePeriodAnchor xPosition;
    private boolean domainIsPointsInTime;
    
    public TimePeriodValuesCollection() {
        this(null);
    }
    
    public TimePeriodValuesCollection(final TimePeriodValues series) {
        this.data = new ArrayList();
        this.xPosition = TimePeriodAnchor.MIDDLE;
        this.domainIsPointsInTime = true;
        if (series != null) {
            this.data.add(series);
            series.addChangeListener(this);
        }
    }
    
    public TimePeriodAnchor getXPosition() {
        return this.xPosition;
    }
    
    public void setXPosition(final TimePeriodAnchor position) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        this.xPosition = position;
    }
    
    public boolean getDomainIsPointsInTime() {
        return this.domainIsPointsInTime;
    }
    
    public void setDomainIsPointsInTime(final boolean flag) {
        this.domainIsPointsInTime = flag;
    }
    
    public int getSeriesCount() {
        return this.data.size();
    }
    
    public TimePeriodValues getSeries(final int series) {
        if (series < 0 || series > this.getSeriesCount()) {
            throw new IllegalArgumentException("Index 'series' out of range.");
        }
        return this.data.get(series);
    }
    
    public Comparable getSeriesKey(final int series) {
        return this.getSeries(series).getKey();
    }
    
    public void addSeries(final TimePeriodValues series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.add(series);
        series.addChangeListener(this);
        this.fireDatasetChanged();
    }
    
    public void removeSeries(final TimePeriodValues series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.remove(series);
        series.removeChangeListener(this);
        this.fireDatasetChanged();
    }
    
    public void removeSeries(final int index) {
        final TimePeriodValues series = this.getSeries(index);
        if (series != null) {
            this.removeSeries(series);
        }
    }
    
    public int getItemCount(final int series) {
        return this.getSeries(series).getItemCount();
    }
    
    public Number getX(final int series, final int item) {
        final TimePeriodValues ts = this.data.get(series);
        final TimePeriodValue dp = ts.getDataItem(item);
        final TimePeriod period = dp.getPeriod();
        return new Long(this.getX(period));
    }
    
    private long getX(final TimePeriod period) {
        if (this.xPosition == TimePeriodAnchor.START) {
            return period.getStart().getTime();
        }
        if (this.xPosition == TimePeriodAnchor.MIDDLE) {
            return period.getStart().getTime() / 2L + period.getEnd().getTime() / 2L;
        }
        if (this.xPosition == TimePeriodAnchor.END) {
            return period.getEnd().getTime();
        }
        throw new IllegalStateException("TimePeriodAnchor unknown.");
    }
    
    public Number getStartX(final int series, final int item) {
        final TimePeriodValues ts = this.data.get(series);
        final TimePeriodValue dp = ts.getDataItem(item);
        return new Long(dp.getPeriod().getStart().getTime());
    }
    
    public Number getEndX(final int series, final int item) {
        final TimePeriodValues ts = this.data.get(series);
        final TimePeriodValue dp = ts.getDataItem(item);
        return new Long(dp.getPeriod().getEnd().getTime());
    }
    
    public Number getY(final int series, final int item) {
        final TimePeriodValues ts = this.data.get(series);
        final TimePeriodValue dp = ts.getDataItem(item);
        return dp.getValue();
    }
    
    public Number getStartY(final int series, final int item) {
        return this.getY(series, item);
    }
    
    public Number getEndY(final int series, final int item) {
        return this.getY(series, item);
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
        Range temp = null;
        final Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            final TimePeriodValues series = iterator.next();
            final int count = series.getItemCount();
            if (count > 0) {
                final TimePeriod start = series.getTimePeriod(series.getMinStartIndex());
                final TimePeriod end = series.getTimePeriod(series.getMaxEndIndex());
                if (this.domainIsPointsInTime) {
                    if (this.xPosition == TimePeriodAnchor.START) {
                        final TimePeriod maxStart = series.getTimePeriod(series.getMaxStartIndex());
                        temp = new Range((double)start.getStart().getTime(), (double)maxStart.getStart().getTime());
                    }
                    else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
                        final TimePeriod minMiddle = series.getTimePeriod(series.getMinMiddleIndex());
                        final long s1 = minMiddle.getStart().getTime();
                        final long e1 = minMiddle.getEnd().getTime();
                        final TimePeriod maxMiddle = series.getTimePeriod(series.getMaxMiddleIndex());
                        final long s2 = maxMiddle.getStart().getTime();
                        final long e2 = maxMiddle.getEnd().getTime();
                        temp = new Range((double)(s1 + (e1 - s1) / 2L), (double)(s2 + (e2 - s2) / 2L));
                    }
                    else if (this.xPosition == TimePeriodAnchor.END) {
                        final TimePeriod minEnd = series.getTimePeriod(series.getMinEndIndex());
                        temp = new Range((double)minEnd.getEnd().getTime(), (double)end.getEnd().getTime());
                    }
                }
                else {
                    temp = new Range((double)start.getStart().getTime(), (double)end.getEnd().getTime());
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
        if (!(obj instanceof TimePeriodValuesCollection)) {
            return false;
        }
        final TimePeriodValuesCollection that = (TimePeriodValuesCollection)obj;
        return this.domainIsPointsInTime == that.domainIsPointsInTime && this.xPosition == that.xPosition && ObjectUtilities.equal((Object)this.data, (Object)that.data);
    }
}
