package org.jfree.data.xy;

import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.Range;
import org.jfree.util.ObjectUtilities;
import java.util.Collections;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.general.DatasetChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import org.jfree.data.DomainInfo;

public class XYSeriesCollection extends AbstractIntervalXYDataset implements IntervalXYDataset, DomainInfo, Serializable
{
    private static final long serialVersionUID = -7590013825931496766L;
    private List data;
    private IntervalXYDelegate intervalDelegate;
    
    public XYSeriesCollection() {
        this(null);
    }
    
    public XYSeriesCollection(final XYSeries series) {
        this.data = new ArrayList();
        this.addChangeListener(this.intervalDelegate = new IntervalXYDelegate(this, false));
        if (series != null) {
            this.data.add(series);
            series.addChangeListener(this);
        }
    }
    
    public void addSeries(final XYSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.add(series);
        series.addChangeListener(this);
        this.fireDatasetChanged();
    }
    
    public void removeSeries(final int series) {
        if (series < 0 || series > this.getSeriesCount()) {
            throw new IllegalArgumentException("Series index out of bounds.");
        }
        final XYSeries ts = this.data.get(series);
        ts.removeChangeListener(this);
        this.data.remove(series);
        this.fireDatasetChanged();
    }
    
    public void removeSeries(final XYSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        if (this.data.contains(series)) {
            series.removeChangeListener(this);
            this.data.remove(series);
            this.fireDatasetChanged();
        }
    }
    
    public void removeAllSeries() {
        for (int i = 0; i < this.data.size(); ++i) {
            final XYSeries series = this.data.get(i);
            series.removeChangeListener(this);
        }
        this.data.clear();
        this.fireDatasetChanged();
    }
    
    public int getSeriesCount() {
        return this.data.size();
    }
    
    public List getSeries() {
        return Collections.unmodifiableList((List<?>)this.data);
    }
    
    public XYSeries getSeries(final int series) {
        if (series < 0 || series >= this.getSeriesCount()) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return this.data.get(series);
    }
    
    public Comparable getSeriesKey(final int series) {
        return this.getSeries(series).getKey();
    }
    
    public int getItemCount(final int series) {
        return this.getSeries(series).getItemCount();
    }
    
    public Number getX(final int series, final int item) {
        final XYSeries ts = this.data.get(series);
        final XYDataItem xyItem = ts.getDataItem(item);
        return xyItem.getX();
    }
    
    public Number getStartX(final int series, final int item) {
        return this.intervalDelegate.getStartX(series, item);
    }
    
    public Number getEndX(final int series, final int item) {
        return this.intervalDelegate.getEndX(series, item);
    }
    
    public Number getY(final int series, final int index) {
        final XYSeries ts = this.data.get(series);
        final XYDataItem xyItem = ts.getDataItem(index);
        return xyItem.getY();
    }
    
    public Number getStartY(final int series, final int item) {
        return this.getY(series, item);
    }
    
    public Number getEndY(final int series, final int item) {
        return this.getY(series, item);
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYSeriesCollection)) {
            return false;
        }
        final XYSeriesCollection that = (XYSeriesCollection)obj;
        return ObjectUtilities.equal((Object)this.data, (Object)that.data);
    }
    
    public int hashCode() {
        return (this.data != null) ? this.data.hashCode() : 0;
    }
    
    public double getDomainLowerBound(final boolean includeInterval) {
        return this.intervalDelegate.getDomainLowerBound(includeInterval);
    }
    
    public double getDomainUpperBound(final boolean includeInterval) {
        return this.intervalDelegate.getDomainUpperBound(includeInterval);
    }
    
    public Range getDomainBounds(final boolean includeInterval) {
        if (includeInterval) {
            return this.intervalDelegate.getDomainBounds(includeInterval);
        }
        return DatasetUtilities.iterateDomainBounds(this, includeInterval);
    }
    
    public double getIntervalWidth() {
        return this.intervalDelegate.getIntervalWidth();
    }
    
    public void setIntervalWidth(final double width) {
        if (width < 0.0) {
            throw new IllegalArgumentException("Negative 'width' argument.");
        }
        this.intervalDelegate.setFixedIntervalWidth(width);
        this.fireDatasetChanged();
    }
    
    public double getIntervalPositionFactor() {
        return this.intervalDelegate.getIntervalPositionFactor();
    }
    
    public void setIntervalPositionFactor(final double factor) {
        this.intervalDelegate.setIntervalPositionFactor(factor);
        this.fireDatasetChanged();
    }
    
    public boolean isAutoWidth() {
        return this.intervalDelegate.isAutoWidth();
    }
    
    public void setAutoWidth(final boolean b) {
        this.intervalDelegate.setAutoWidth(b);
        this.fireDatasetChanged();
    }
}
