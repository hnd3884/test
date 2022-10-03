package org.jfree.data.xy;

import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;

public class XYBarDataset extends AbstractIntervalXYDataset implements IntervalXYDataset, DatasetChangeListener
{
    private XYDataset underlying;
    private double barWidth;
    
    public XYBarDataset(final XYDataset underlying, final double barWidth) {
        (this.underlying = underlying).addChangeListener(this);
        this.barWidth = barWidth;
    }
    
    public int getSeriesCount() {
        return this.underlying.getSeriesCount();
    }
    
    public Comparable getSeriesKey(final int series) {
        return this.underlying.getSeriesKey(series);
    }
    
    public int getItemCount(final int series) {
        return this.underlying.getItemCount(series);
    }
    
    public Number getX(final int series, final int item) {
        return this.underlying.getX(series, item);
    }
    
    public Number getY(final int series, final int item) {
        return this.underlying.getY(series, item);
    }
    
    public Number getStartX(final int series, final int item) {
        Number result = null;
        final Number xnum = this.underlying.getX(series, item);
        if (xnum != null) {
            result = new Double(xnum.doubleValue() - this.barWidth / 2.0);
        }
        return result;
    }
    
    public Number getEndX(final int series, final int item) {
        Number result = null;
        final Number xnum = this.underlying.getX(series, item);
        if (xnum != null) {
            result = new Double(xnum.doubleValue() + this.barWidth / 2.0);
        }
        return result;
    }
    
    public Number getStartY(final int series, final int item) {
        return this.underlying.getY(series, item);
    }
    
    public Number getEndY(final int series, final int item) {
        return this.underlying.getY(series, item);
    }
    
    public void datasetChanged(final DatasetChangeEvent event) {
        this.notifyListeners(event);
    }
}
