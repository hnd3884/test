package org.jfree.data.statistics;

import java.util.Date;
import java.util.ArrayList;
import org.jfree.data.Range;
import java.util.List;
import org.jfree.data.RangeInfo;
import org.jfree.data.xy.AbstractXYDataset;

public class DefaultBoxAndWhiskerXYDataset extends AbstractXYDataset implements BoxAndWhiskerXYDataset, RangeInfo
{
    private Comparable seriesKey;
    private List dates;
    private List items;
    private Number minimumRangeValue;
    private Number maximumRangeValue;
    private Range rangeBounds;
    private double outlierCoefficient;
    private double faroutCoefficient;
    
    public DefaultBoxAndWhiskerXYDataset(final Comparable seriesKey) {
        this.outlierCoefficient = 1.5;
        this.faroutCoefficient = 2.0;
        this.seriesKey = seriesKey;
        this.dates = new ArrayList();
        this.items = new ArrayList();
        this.minimumRangeValue = null;
        this.maximumRangeValue = null;
        this.rangeBounds = null;
    }
    
    public void add(final Date date, final BoxAndWhiskerItem item) {
        this.dates.add(date);
        this.items.add(item);
        if (this.minimumRangeValue == null) {
            this.minimumRangeValue = item.getMinRegularValue();
        }
        else if (item.getMinRegularValue().doubleValue() < this.minimumRangeValue.doubleValue()) {
            this.minimumRangeValue = item.getMinRegularValue();
        }
        if (this.maximumRangeValue == null) {
            this.maximumRangeValue = item.getMaxRegularValue();
        }
        else if (item.getMaxRegularValue().doubleValue() > this.maximumRangeValue.doubleValue()) {
            this.maximumRangeValue = item.getMaxRegularValue();
        }
        this.rangeBounds = new Range(this.minimumRangeValue.doubleValue(), this.maximumRangeValue.doubleValue());
    }
    
    public Comparable getSeriesKey(final int i) {
        return this.seriesKey;
    }
    
    public BoxAndWhiskerItem getItem(final int series, final int item) {
        return this.items.get(item);
    }
    
    public Number getX(final int series, final int item) {
        return new Long(this.dates.get(item).getTime());
    }
    
    public Date getXDate(final int series, final int item) {
        return this.dates.get(item);
    }
    
    public Number getY(final int series, final int item) {
        return new Double(this.getMeanValue(series, item).doubleValue());
    }
    
    public Number getMeanValue(final int series, final int item) {
        Number result = null;
        final BoxAndWhiskerItem stats = this.items.get(item);
        if (stats != null) {
            result = stats.getMean();
        }
        return result;
    }
    
    public Number getMedianValue(final int series, final int item) {
        Number result = null;
        final BoxAndWhiskerItem stats = this.items.get(item);
        if (stats != null) {
            result = stats.getMedian();
        }
        return result;
    }
    
    public Number getQ1Value(final int series, final int item) {
        Number result = null;
        final BoxAndWhiskerItem stats = this.items.get(item);
        if (stats != null) {
            result = stats.getQ1();
        }
        return result;
    }
    
    public Number getQ3Value(final int series, final int item) {
        Number result = null;
        final BoxAndWhiskerItem stats = this.items.get(item);
        if (stats != null) {
            result = stats.getQ3();
        }
        return result;
    }
    
    public Number getMinRegularValue(final int series, final int item) {
        Number result = null;
        final BoxAndWhiskerItem stats = this.items.get(item);
        if (stats != null) {
            result = stats.getMinRegularValue();
        }
        return result;
    }
    
    public Number getMaxRegularValue(final int series, final int item) {
        Number result = null;
        final BoxAndWhiskerItem stats = this.items.get(item);
        if (stats != null) {
            result = stats.getMaxRegularValue();
        }
        return result;
    }
    
    public Number getMinOutlier(final int series, final int item) {
        Number result = null;
        final BoxAndWhiskerItem stats = this.items.get(item);
        if (stats != null) {
            result = stats.getMinOutlier();
        }
        return result;
    }
    
    public Number getMaxOutlier(final int series, final int item) {
        Number result = null;
        final BoxAndWhiskerItem stats = this.items.get(item);
        if (stats != null) {
            result = stats.getMaxOutlier();
        }
        return result;
    }
    
    public List getOutliers(final int series, final int item) {
        List result = null;
        final BoxAndWhiskerItem stats = this.items.get(item);
        if (stats != null) {
            result = stats.getOutliers();
        }
        return result;
    }
    
    public double getOutlierCoefficient() {
        return this.outlierCoefficient;
    }
    
    public double getFaroutCoefficient() {
        return this.faroutCoefficient;
    }
    
    public int getSeriesCount() {
        return 1;
    }
    
    public int getItemCount(final int series) {
        return this.dates.size();
    }
    
    public void setOutlierCoefficient(final double outlierCoefficient) {
        this.outlierCoefficient = outlierCoefficient;
    }
    
    public void setFaroutCoefficient(final double faroutCoefficient) {
        if (faroutCoefficient > this.getOutlierCoefficient()) {
            this.faroutCoefficient = faroutCoefficient;
            return;
        }
        throw new IllegalArgumentException("Farout value must be greater than the outlier value, which is currently set at: (" + this.getOutlierCoefficient() + ")");
    }
    
    public double getRangeLowerBound(final boolean includeInterval) {
        double result = Double.NaN;
        if (this.minimumRangeValue != null) {
            result = this.minimumRangeValue.doubleValue();
        }
        return result;
    }
    
    public double getRangeUpperBound(final boolean includeInterval) {
        double result = Double.NaN;
        if (this.maximumRangeValue != null) {
            result = this.maximumRangeValue.doubleValue();
        }
        return result;
    }
    
    public Range getRangeBounds(final boolean includeInterval) {
        return this.rangeBounds;
    }
}
