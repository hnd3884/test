package org.jfree.data.statistics;

import org.jfree.data.Range;
import java.util.List;
import org.jfree.data.KeyedObjects2D;
import org.jfree.data.RangeInfo;
import org.jfree.data.general.AbstractDataset;

public class DefaultStatisticalCategoryDataset extends AbstractDataset implements StatisticalCategoryDataset, RangeInfo
{
    private KeyedObjects2D data;
    private double minimumRangeValue;
    private double minimumRangeValueIncStdDev;
    private double maximumRangeValue;
    private double maximumRangeValueIncStdDev;
    
    public DefaultStatisticalCategoryDataset() {
        this.data = new KeyedObjects2D();
        this.minimumRangeValue = Double.NaN;
        this.maximumRangeValue = Double.NaN;
        this.minimumRangeValueIncStdDev = Double.NaN;
        this.maximumRangeValueIncStdDev = Double.NaN;
    }
    
    public Number getMeanValue(final int row, final int column) {
        Number result = null;
        final MeanAndStandardDeviation masd = (MeanAndStandardDeviation)this.data.getObject(row, column);
        if (masd != null) {
            result = masd.getMean();
        }
        return result;
    }
    
    public Number getValue(final int row, final int column) {
        return this.getMeanValue(row, column);
    }
    
    public Number getValue(final Comparable rowKey, final Comparable columnKey) {
        return this.getMeanValue(rowKey, columnKey);
    }
    
    public Number getMeanValue(final Comparable rowKey, final Comparable columnKey) {
        Number result = null;
        final MeanAndStandardDeviation masd = (MeanAndStandardDeviation)this.data.getObject(rowKey, columnKey);
        if (masd != null) {
            result = masd.getMean();
        }
        return result;
    }
    
    public Number getStdDevValue(final int row, final int column) {
        Number result = null;
        final MeanAndStandardDeviation masd = (MeanAndStandardDeviation)this.data.getObject(row, column);
        if (masd != null) {
            result = masd.getStandardDeviation();
        }
        return result;
    }
    
    public Number getStdDevValue(final Comparable rowKey, final Comparable columnKey) {
        Number result = null;
        final MeanAndStandardDeviation masd = (MeanAndStandardDeviation)this.data.getObject(rowKey, columnKey);
        if (masd != null) {
            result = masd.getStandardDeviation();
        }
        return result;
    }
    
    public int getColumnIndex(final Comparable key) {
        return this.data.getColumnIndex(key);
    }
    
    public Comparable getColumnKey(final int column) {
        return this.data.getColumnKey(column);
    }
    
    public List getColumnKeys() {
        return this.data.getColumnKeys();
    }
    
    public int getRowIndex(final Comparable key) {
        return this.data.getRowIndex(key);
    }
    
    public Comparable getRowKey(final int row) {
        return this.data.getRowKey(row);
    }
    
    public List getRowKeys() {
        return this.data.getRowKeys();
    }
    
    public int getRowCount() {
        return this.data.getRowCount();
    }
    
    public int getColumnCount() {
        return this.data.getColumnCount();
    }
    
    public void add(final double mean, final double standardDeviation, final Comparable rowKey, final Comparable columnKey) {
        this.add(new Double(mean), new Double(standardDeviation), rowKey, columnKey);
    }
    
    public void add(final Number mean, final Number standardDeviation, final Comparable rowKey, final Comparable columnKey) {
        final MeanAndStandardDeviation item = new MeanAndStandardDeviation(mean, standardDeviation);
        this.data.addObject(item, rowKey, columnKey);
        double m = 0.0;
        double sd = 0.0;
        if (mean != null) {
            m = mean.doubleValue();
        }
        if (standardDeviation != null) {
            sd = standardDeviation.doubleValue();
        }
        if (!Double.isNaN(m) && (Double.isNaN(this.maximumRangeValue) || m > this.maximumRangeValue)) {
            this.maximumRangeValue = m;
        }
        if (!Double.isNaN(m + sd) && (Double.isNaN(this.maximumRangeValueIncStdDev) || m + sd > this.maximumRangeValueIncStdDev)) {
            this.maximumRangeValueIncStdDev = m + sd;
        }
        if (!Double.isNaN(m) && (Double.isNaN(this.minimumRangeValue) || m < this.minimumRangeValue)) {
            this.minimumRangeValue = m;
        }
        if (!Double.isNaN(m - sd) && (Double.isNaN(this.minimumRangeValueIncStdDev) || m - sd < this.minimumRangeValueIncStdDev)) {
            this.minimumRangeValueIncStdDev = m - sd;
        }
        this.fireDatasetChanged();
    }
    
    public double getRangeLowerBound(final boolean includeInterval) {
        return this.minimumRangeValue;
    }
    
    public double getRangeUpperBound(final boolean includeInterval) {
        return this.maximumRangeValue;
    }
    
    public Range getRangeBounds(final boolean includeInterval) {
        Range result = null;
        if (includeInterval) {
            if (!Double.isNaN(this.minimumRangeValueIncStdDev) && !Double.isNaN(this.maximumRangeValueIncStdDev)) {
                result = new Range(this.minimumRangeValueIncStdDev, this.maximumRangeValueIncStdDev);
            }
        }
        else if (!Double.isNaN(this.minimumRangeValue) && !Double.isNaN(this.maximumRangeValue)) {
            result = new Range(this.minimumRangeValue, this.maximumRangeValue);
        }
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultStatisticalCategoryDataset)) {
            return false;
        }
        final DefaultStatisticalCategoryDataset that = (DefaultStatisticalCategoryDataset)obj;
        return this.data.equals(that.data);
    }
}
