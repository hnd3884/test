package org.jfree.data.statistics;

import org.jfree.util.ObjectUtilities;
import java.util.List;
import org.jfree.data.Range;
import org.jfree.data.KeyedObjects2D;
import org.jfree.data.RangeInfo;
import org.jfree.data.general.AbstractDataset;

public class DefaultBoxAndWhiskerCategoryDataset extends AbstractDataset implements BoxAndWhiskerCategoryDataset, RangeInfo
{
    protected KeyedObjects2D data;
    private Number minimumRangeValue;
    private Number maximumRangeValue;
    private Range rangeBounds;
    
    public DefaultBoxAndWhiskerCategoryDataset() {
        this.data = new KeyedObjects2D();
        this.minimumRangeValue = null;
        this.maximumRangeValue = null;
        this.rangeBounds = new Range(0.0, 0.0);
    }
    
    public void add(final List list, final Comparable rowKey, final Comparable columnKey) {
        final BoxAndWhiskerItem item = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(list);
        this.add(item, rowKey, columnKey);
    }
    
    public void add(final BoxAndWhiskerItem item, final Comparable rowKey, final Comparable columnKey) {
        this.data.addObject(item, rowKey, columnKey);
        final double minval = item.getMinOutlier().doubleValue();
        final double maxval = item.getMaxOutlier().doubleValue();
        if (this.maximumRangeValue == null) {
            this.maximumRangeValue = new Double(maxval);
        }
        else if (maxval > this.maximumRangeValue.doubleValue()) {
            this.maximumRangeValue = new Double(maxval);
        }
        if (this.minimumRangeValue == null) {
            this.minimumRangeValue = new Double(minval);
        }
        else if (minval < this.minimumRangeValue.doubleValue()) {
            this.minimumRangeValue = new Double(minval);
        }
        this.rangeBounds = new Range(this.minimumRangeValue.doubleValue(), this.maximumRangeValue.doubleValue());
        this.fireDatasetChanged();
    }
    
    public BoxAndWhiskerItem getItem(final int row, final int column) {
        return (BoxAndWhiskerItem)this.data.getObject(row, column);
    }
    
    public Number getValue(final int row, final int column) {
        return this.getMedianValue(row, column);
    }
    
    public Number getValue(final Comparable rowKey, final Comparable columnKey) {
        return this.getMedianValue(rowKey, columnKey);
    }
    
    public Number getMeanValue(final int row, final int column) {
        Number result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(row, column);
        if (item != null) {
            result = item.getMean();
        }
        return result;
    }
    
    public Number getMeanValue(final Comparable rowKey, final Comparable columnKey) {
        Number result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getMean();
        }
        return result;
    }
    
    public Number getMedianValue(final int row, final int column) {
        Number result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(row, column);
        if (item != null) {
            result = item.getMedian();
        }
        return result;
    }
    
    public Number getMedianValue(final Comparable rowKey, final Comparable columnKey) {
        Number result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getMedian();
        }
        return result;
    }
    
    public Number getQ1Value(final int row, final int column) {
        Number result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(row, column);
        if (item != null) {
            result = item.getQ1();
        }
        return result;
    }
    
    public Number getQ1Value(final Comparable rowKey, final Comparable columnKey) {
        Number result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getQ1();
        }
        return result;
    }
    
    public Number getQ3Value(final int row, final int column) {
        Number result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(row, column);
        if (item != null) {
            result = item.getQ3();
        }
        return result;
    }
    
    public Number getQ3Value(final Comparable rowKey, final Comparable columnKey) {
        Number result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getQ3();
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
    
    public Number getMinRegularValue(final int row, final int column) {
        Number result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(row, column);
        if (item != null) {
            result = item.getMinRegularValue();
        }
        return result;
    }
    
    public Number getMinRegularValue(final Comparable rowKey, final Comparable columnKey) {
        Number result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getMinRegularValue();
        }
        return result;
    }
    
    public Number getMaxRegularValue(final int row, final int column) {
        Number result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(row, column);
        if (item != null) {
            result = item.getMaxRegularValue();
        }
        return result;
    }
    
    public Number getMaxRegularValue(final Comparable rowKey, final Comparable columnKey) {
        Number result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getMaxRegularValue();
        }
        return result;
    }
    
    public Number getMinOutlier(final int row, final int column) {
        Number result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(row, column);
        if (item != null) {
            result = item.getMinOutlier();
        }
        return result;
    }
    
    public Number getMinOutlier(final Comparable rowKey, final Comparable columnKey) {
        Number result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getMinOutlier();
        }
        return result;
    }
    
    public Number getMaxOutlier(final int row, final int column) {
        Number result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(row, column);
        if (item != null) {
            result = item.getMaxOutlier();
        }
        return result;
    }
    
    public Number getMaxOutlier(final Comparable rowKey, final Comparable columnKey) {
        Number result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getMaxOutlier();
        }
        return result;
    }
    
    public List getOutliers(final int row, final int column) {
        List result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(row, column);
        if (item != null) {
            result = item.getOutliers();
        }
        return result;
    }
    
    public List getOutliers(final Comparable rowKey, final Comparable columnKey) {
        List result = null;
        final BoxAndWhiskerItem item = (BoxAndWhiskerItem)this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getOutliers();
        }
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof DefaultBoxAndWhiskerCategoryDataset) {
            final DefaultBoxAndWhiskerCategoryDataset dataset = (DefaultBoxAndWhiskerCategoryDataset)obj;
            return ObjectUtilities.equal((Object)this.data, (Object)dataset.data);
        }
        return false;
    }
}
