package org.jfree.data.general;

import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.RangeInfo;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.function.Function2D;
import org.jfree.data.KeyedValues;
import org.jfree.util.ArrayUtilities;
import org.jfree.data.category.DefaultCategoryDataset;
import java.util.ArrayList;
import org.jfree.data.category.CategoryDataset;
import java.util.Iterator;
import java.util.List;

public final class DatasetUtilities
{
    private DatasetUtilities() {
    }
    
    public static double calculatePieDatasetTotal(final PieDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        final List keys = dataset.getKeys();
        double totalValue = 0.0;
        final Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            final Comparable current = iterator.next();
            if (current != null) {
                final Number value = dataset.getValue(current);
                double v = 0.0;
                if (value != null) {
                    v = value.doubleValue();
                }
                if (v <= 0.0) {
                    continue;
                }
                totalValue += v;
            }
        }
        return totalValue;
    }
    
    public static PieDataset createPieDatasetForRow(final CategoryDataset dataset, final Comparable rowKey) {
        final int row = dataset.getRowIndex(rowKey);
        return createPieDatasetForRow(dataset, row);
    }
    
    public static PieDataset createPieDatasetForRow(final CategoryDataset dataset, final int row) {
        final DefaultPieDataset result = new DefaultPieDataset();
        for (int columnCount = dataset.getColumnCount(), current = 0; current < columnCount; ++current) {
            final Comparable columnKey = dataset.getColumnKey(current);
            result.setValue(columnKey, dataset.getValue(row, current));
        }
        return result;
    }
    
    public static PieDataset createPieDatasetForColumn(final CategoryDataset dataset, final Comparable columnKey) {
        final int column = dataset.getColumnIndex(columnKey);
        return createPieDatasetForColumn(dataset, column);
    }
    
    public static PieDataset createPieDatasetForColumn(final CategoryDataset dataset, final int column) {
        final DefaultPieDataset result = new DefaultPieDataset();
        for (int rowCount = dataset.getRowCount(), i = 0; i < rowCount; ++i) {
            final Comparable rowKey = dataset.getRowKey(i);
            result.setValue(rowKey, dataset.getValue(i, column));
        }
        return result;
    }
    
    public static PieDataset createConsolidatedPieDataset(final PieDataset source, final Comparable key, final double minimumPercent) {
        return createConsolidatedPieDataset(source, key, minimumPercent, 2);
    }
    
    public static PieDataset createConsolidatedPieDataset(final PieDataset source, final Comparable key, final double minimumPercent, final int minItems) {
        final DefaultPieDataset result = new DefaultPieDataset();
        final double total = calculatePieDatasetTotal(source);
        final List keys = source.getKeys();
        final ArrayList otherKeys = new ArrayList();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            final Comparable currentKey = iterator.next();
            final Number dataValue = source.getValue(currentKey);
            if (dataValue != null) {
                final double value = dataValue.doubleValue();
                if (value / total >= minimumPercent) {
                    continue;
                }
                otherKeys.add(currentKey);
            }
        }
        iterator = keys.iterator();
        double otherValue = 0.0;
        while (iterator.hasNext()) {
            final Comparable currentKey2 = iterator.next();
            final Number dataValue2 = source.getValue(currentKey2);
            if (dataValue2 != null) {
                if (otherKeys.contains(currentKey2) && otherKeys.size() >= minItems) {
                    otherValue += dataValue2.doubleValue();
                }
                else {
                    result.setValue(currentKey2, dataValue2);
                }
            }
        }
        if (otherKeys.size() >= minItems) {
            result.setValue(key, otherValue);
        }
        return result;
    }
    
    public static CategoryDataset createCategoryDataset(final String rowKeyPrefix, final String columnKeyPrefix, final double[][] data) {
        final DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; ++r) {
            final String rowKey = rowKeyPrefix + (r + 1);
            for (int c = 0; c < data[r].length; ++c) {
                final String columnKey = columnKeyPrefix + (c + 1);
                result.addValue(new Double(data[r][c]), rowKey, columnKey);
            }
        }
        return result;
    }
    
    public static CategoryDataset createCategoryDataset(final String rowKeyPrefix, final String columnKeyPrefix, final Number[][] data) {
        final DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; ++r) {
            final String rowKey = rowKeyPrefix + (r + 1);
            for (int c = 0; c < data[r].length; ++c) {
                final String columnKey = columnKeyPrefix + (c + 1);
                result.addValue(data[r][c], rowKey, columnKey);
            }
        }
        return result;
    }
    
    public static CategoryDataset createCategoryDataset(final Comparable[] rowKeys, final Comparable[] columnKeys, final double[][] data) {
        if (rowKeys == null) {
            throw new IllegalArgumentException("Null 'rowKeys' argument.");
        }
        if (columnKeys == null) {
            throw new IllegalArgumentException("Null 'columnKeys' argument.");
        }
        if (ArrayUtilities.hasDuplicateItems((Object[])rowKeys)) {
            throw new IllegalArgumentException("Duplicate items in 'rowKeys'.");
        }
        if (ArrayUtilities.hasDuplicateItems((Object[])columnKeys)) {
            throw new IllegalArgumentException("Duplicate items in 'columnKeys'.");
        }
        if (rowKeys.length != data.length) {
            throw new IllegalArgumentException("The number of row keys does not match the number of rows in the data array.");
        }
        int columnCount = 0;
        for (int r = 0; r < data.length; ++r) {
            columnCount = Math.max(columnCount, data[r].length);
        }
        if (columnKeys.length != columnCount) {
            throw new IllegalArgumentException("The number of column keys does not match the number of columns in the data array.");
        }
        final DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r2 = 0; r2 < data.length; ++r2) {
            final Comparable rowKey = rowKeys[r2];
            for (int c = 0; c < data[r2].length; ++c) {
                final Comparable columnKey = columnKeys[c];
                result.addValue(new Double(data[r2][c]), rowKey, columnKey);
            }
        }
        return result;
    }
    
    public static CategoryDataset createCategoryDataset(final Comparable rowKey, final KeyedValues rowData) {
        if (rowKey == null) {
            throw new IllegalArgumentException("Null 'rowKey' argument.");
        }
        if (rowData == null) {
            throw new IllegalArgumentException("Null 'rowData' argument.");
        }
        final DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int i = 0; i < rowData.getItemCount(); ++i) {
            result.addValue(rowData.getValue(i), rowKey, rowData.getKey(i));
        }
        return result;
    }
    
    public static XYDataset sampleFunction2D(final Function2D f, final double start, final double end, final int samples, final Comparable seriesKey) {
        if (f == null) {
            throw new IllegalArgumentException("Null 'f' argument.");
        }
        if (seriesKey == null) {
            throw new IllegalArgumentException("Null 'seriesKey' argument.");
        }
        if (start >= end) {
            throw new IllegalArgumentException("Requires 'start' < 'end'.");
        }
        if (samples < 2) {
            throw new IllegalArgumentException("Requires 'samples' > 1");
        }
        final XYSeries series = new XYSeries(seriesKey);
        final double step = (end - start) / samples;
        for (int i = 0; i <= samples; ++i) {
            final double x = start + step * i;
            series.add(x, f.getValue(x));
        }
        final XYSeriesCollection collection = new XYSeriesCollection(series);
        return collection;
    }
    
    public static boolean isEmptyOrNull(final PieDataset dataset) {
        if (dataset == null) {
            return true;
        }
        final int itemCount = dataset.getItemCount();
        if (itemCount == 0) {
            return true;
        }
        for (int item = 0; item < itemCount; ++item) {
            final Number y = dataset.getValue(item);
            if (y != null) {
                final double yy = y.doubleValue();
                if (yy > 0.0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean isEmptyOrNull(final CategoryDataset dataset) {
        if (dataset == null) {
            return true;
        }
        final int rowCount = dataset.getRowCount();
        final int columnCount = dataset.getColumnCount();
        if (rowCount == 0 || columnCount == 0) {
            return true;
        }
        for (int r = 0; r < rowCount; ++r) {
            for (int c = 0; c < columnCount; ++c) {
                if (dataset.getValue(r, c) != null) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean isEmptyOrNull(final XYDataset dataset) {
        boolean result = true;
        if (dataset != null) {
            for (int s = 0; s < dataset.getSeriesCount(); ++s) {
                if (dataset.getItemCount(s) > 0) {
                    result = false;
                }
            }
        }
        return result;
    }
    
    public static Range findDomainBounds(final XYDataset dataset) {
        return findDomainBounds(dataset, true);
    }
    
    public static Range findDomainBounds(final XYDataset dataset, final boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        if (dataset instanceof DomainInfo) {
            final DomainInfo info = (DomainInfo)dataset;
            result = info.getDomainBounds(includeInterval);
        }
        else {
            result = iterateDomainBounds(dataset, includeInterval);
        }
        return result;
    }
    
    public static Range iterateDomainBounds(final XYDataset dataset) {
        return iterateDomainBounds(dataset, true);
    }
    
    public static Range iterateDomainBounds(final XYDataset dataset, final boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        final int seriesCount = dataset.getSeriesCount();
        if (includeInterval && dataset instanceof IntervalXYDataset) {
            final IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
            for (int series = 0; series < seriesCount; ++series) {
                for (int itemCount = dataset.getItemCount(series), item = 0; item < itemCount; ++item) {
                    final double lvalue = intervalXYData.getStartXValue(series, item);
                    final double uvalue = intervalXYData.getEndXValue(series, item);
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        else {
            for (int series2 = 0; series2 < seriesCount; ++series2) {
                for (int itemCount2 = dataset.getItemCount(series2), item2 = 0; item2 < itemCount2; ++item2) {
                    final double uvalue;
                    final double lvalue = uvalue = dataset.getXValue(series2, item2);
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        if (minimum > maximum) {
            return null;
        }
        return new Range(minimum, maximum);
    }
    
    public static Range findRangeBounds(final CategoryDataset dataset) {
        return findRangeBounds(dataset, true);
    }
    
    public static Range findRangeBounds(final CategoryDataset dataset, final boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        if (dataset instanceof RangeInfo) {
            final RangeInfo info = (RangeInfo)dataset;
            result = info.getRangeBounds(includeInterval);
        }
        else {
            result = iterateCategoryRangeBounds(dataset, includeInterval);
        }
        return result;
    }
    
    public static Range findRangeBounds(final XYDataset dataset) {
        return findRangeBounds(dataset, true);
    }
    
    public static Range findRangeBounds(final XYDataset dataset, final boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        if (dataset instanceof RangeInfo) {
            final RangeInfo info = (RangeInfo)dataset;
            result = info.getRangeBounds(includeInterval);
        }
        else {
            result = iterateXYRangeBounds(dataset);
        }
        return result;
    }
    
    public static Range iterateCategoryRangeBounds(final CategoryDataset dataset, final boolean includeInterval) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        final boolean interval = includeInterval && dataset instanceof IntervalCategoryDataset;
        final int rowCount = dataset.getRowCount();
        final int columnCount = dataset.getColumnCount();
        for (int row = 0; row < rowCount; ++row) {
            for (int column = 0; column < columnCount; ++column) {
                Number lvalue;
                Number uvalue;
                if (interval) {
                    final IntervalCategoryDataset icd = (IntervalCategoryDataset)dataset;
                    lvalue = icd.getStartValue(row, column);
                    uvalue = icd.getEndValue(row, column);
                }
                else {
                    lvalue = (uvalue = dataset.getValue(row, column));
                }
                if (lvalue != null) {
                    minimum = Math.min(minimum, lvalue.doubleValue());
                }
                if (uvalue != null) {
                    maximum = Math.max(maximum, uvalue.doubleValue());
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Range(minimum, maximum);
    }
    
    public static Range iterateXYRangeBounds(final XYDataset dataset) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        for (int seriesCount = dataset.getSeriesCount(), series = 0; series < seriesCount; ++series) {
            for (int itemCount = dataset.getItemCount(series), item = 0; item < itemCount; ++item) {
                double lvalue;
                double uvalue;
                if (dataset instanceof IntervalXYDataset) {
                    final IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
                    lvalue = intervalXYData.getStartYValue(series, item);
                    uvalue = intervalXYData.getEndYValue(series, item);
                }
                else if (dataset instanceof OHLCDataset) {
                    final OHLCDataset highLowData = (OHLCDataset)dataset;
                    lvalue = highLowData.getLowValue(series, item);
                    uvalue = highLowData.getHighValue(series, item);
                }
                else {
                    lvalue = (uvalue = dataset.getYValue(series, item));
                }
                if (!Double.isNaN(lvalue)) {
                    minimum = Math.min(minimum, lvalue);
                }
                if (!Double.isNaN(uvalue)) {
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Range(minimum, maximum);
    }
    
    public static Number findMinimumDomainValue(final XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Number result = null;
        if (dataset instanceof DomainInfo) {
            final DomainInfo info = (DomainInfo)dataset;
            return new Double(info.getDomainLowerBound(true));
        }
        double minimum = Double.POSITIVE_INFINITY;
        for (int seriesCount = dataset.getSeriesCount(), series = 0; series < seriesCount; ++series) {
            for (int itemCount = dataset.getItemCount(series), item = 0; item < itemCount; ++item) {
                double value;
                if (dataset instanceof IntervalXYDataset) {
                    final IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
                    value = intervalXYData.getStartXValue(series, item);
                }
                else {
                    value = dataset.getXValue(series, item);
                }
                if (!Double.isNaN(value)) {
                    minimum = Math.min(minimum, value);
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            result = null;
        }
        else {
            result = new Double(minimum);
        }
        return result;
    }
    
    public static Number findMaximumDomainValue(final XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Number result = null;
        if (dataset instanceof DomainInfo) {
            final DomainInfo info = (DomainInfo)dataset;
            return new Double(info.getDomainUpperBound(true));
        }
        double maximum = Double.NEGATIVE_INFINITY;
        for (int seriesCount = dataset.getSeriesCount(), series = 0; series < seriesCount; ++series) {
            for (int itemCount = dataset.getItemCount(series), item = 0; item < itemCount; ++item) {
                double value;
                if (dataset instanceof IntervalXYDataset) {
                    final IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
                    value = intervalXYData.getEndXValue(series, item);
                }
                else {
                    value = dataset.getXValue(series, item);
                }
                if (!Double.isNaN(value)) {
                    maximum = Math.max(maximum, value);
                }
            }
        }
        if (maximum == Double.NEGATIVE_INFINITY) {
            result = null;
        }
        else {
            result = new Double(maximum);
        }
        return result;
    }
    
    public static Number findMinimumRangeValue(final CategoryDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        if (dataset instanceof RangeInfo) {
            final RangeInfo info = (RangeInfo)dataset;
            return new Double(info.getRangeLowerBound(true));
        }
        double minimum = Double.POSITIVE_INFINITY;
        final int seriesCount = dataset.getRowCount();
        final int itemCount = dataset.getColumnCount();
        for (int series = 0; series < seriesCount; ++series) {
            for (int item = 0; item < itemCount; ++item) {
                Number value;
                if (dataset instanceof IntervalCategoryDataset) {
                    final IntervalCategoryDataset icd = (IntervalCategoryDataset)dataset;
                    value = icd.getStartValue(series, item);
                }
                else {
                    value = dataset.getValue(series, item);
                }
                if (value != null) {
                    minimum = Math.min(minimum, value.doubleValue());
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Double(minimum);
    }
    
    public static Number findMinimumRangeValue(final XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        if (dataset instanceof RangeInfo) {
            final RangeInfo info = (RangeInfo)dataset;
            return new Double(info.getRangeLowerBound(true));
        }
        double minimum = Double.POSITIVE_INFINITY;
        for (int seriesCount = dataset.getSeriesCount(), series = 0; series < seriesCount; ++series) {
            for (int itemCount = dataset.getItemCount(series), item = 0; item < itemCount; ++item) {
                double value;
                if (dataset instanceof IntervalXYDataset) {
                    final IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
                    value = intervalXYData.getStartYValue(series, item);
                }
                else if (dataset instanceof OHLCDataset) {
                    final OHLCDataset highLowData = (OHLCDataset)dataset;
                    value = highLowData.getLowValue(series, item);
                }
                else {
                    value = dataset.getYValue(series, item);
                }
                if (!Double.isNaN(value)) {
                    minimum = Math.min(minimum, value);
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Double(minimum);
    }
    
    public static Number findMaximumRangeValue(final CategoryDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        if (dataset instanceof RangeInfo) {
            final RangeInfo info = (RangeInfo)dataset;
            return new Double(info.getRangeUpperBound(true));
        }
        double maximum = Double.NEGATIVE_INFINITY;
        final int seriesCount = dataset.getRowCount();
        final int itemCount = dataset.getColumnCount();
        for (int series = 0; series < seriesCount; ++series) {
            for (int item = 0; item < itemCount; ++item) {
                Number value;
                if (dataset instanceof IntervalCategoryDataset) {
                    final IntervalCategoryDataset icd = (IntervalCategoryDataset)dataset;
                    value = icd.getEndValue(series, item);
                }
                else {
                    value = dataset.getValue(series, item);
                }
                if (value != null) {
                    maximum = Math.max(maximum, value.doubleValue());
                }
            }
        }
        if (maximum == Double.NEGATIVE_INFINITY) {
            return null;
        }
        return new Double(maximum);
    }
    
    public static Number findMaximumRangeValue(final XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        if (dataset instanceof RangeInfo) {
            final RangeInfo info = (RangeInfo)dataset;
            return new Double(info.getRangeUpperBound(true));
        }
        double maximum = Double.NEGATIVE_INFINITY;
        for (int seriesCount = dataset.getSeriesCount(), series = 0; series < seriesCount; ++series) {
            for (int itemCount = dataset.getItemCount(series), item = 0; item < itemCount; ++item) {
                double value;
                if (dataset instanceof IntervalXYDataset) {
                    final IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
                    value = intervalXYData.getEndYValue(series, item);
                }
                else if (dataset instanceof OHLCDataset) {
                    final OHLCDataset highLowData = (OHLCDataset)dataset;
                    value = highLowData.getHighValue(series, item);
                }
                else {
                    value = dataset.getYValue(series, item);
                }
                if (!Double.isNaN(value)) {
                    maximum = Math.max(maximum, value);
                }
            }
        }
        if (maximum == Double.NEGATIVE_INFINITY) {
            return null;
        }
        return new Double(maximum);
    }
    
    public static Range findStackedRangeBounds(final CategoryDataset dataset) {
        return findStackedRangeBounds(dataset, 0.0);
    }
    
    public static Range findStackedRangeBounds(final CategoryDataset dataset, final double base) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        for (int categoryCount = dataset.getColumnCount(), item = 0; item < categoryCount; ++item) {
            double positive = base;
            double negative = base;
            for (int seriesCount = dataset.getRowCount(), series = 0; series < seriesCount; ++series) {
                final Number number = dataset.getValue(series, item);
                if (number != null) {
                    final double value = number.doubleValue();
                    if (value > 0.0) {
                        positive += value;
                    }
                    if (value < 0.0) {
                        negative += value;
                    }
                }
            }
            minimum = Math.min(minimum, negative);
            maximum = Math.max(maximum, positive);
        }
        if (minimum <= maximum) {
            result = new Range(minimum, maximum);
        }
        return result;
    }
    
    public static Range findStackedRangeBounds(final CategoryDataset dataset, final KeyToGroupMap map) {
        Range result = null;
        if (dataset != null) {
            final int[] groupIndex = new int[dataset.getRowCount()];
            for (int i = 0; i < dataset.getRowCount(); ++i) {
                groupIndex[i] = map.getGroupIndex(map.getGroup(dataset.getRowKey(i)));
            }
            final int groupCount = map.getGroupCount();
            final double[] minimum = new double[groupCount];
            final double[] maximum = new double[groupCount];
            for (int categoryCount = dataset.getColumnCount(), item = 0; item < categoryCount; ++item) {
                final double[] positive = new double[groupCount];
                final double[] negative = new double[groupCount];
                for (int seriesCount = dataset.getRowCount(), series = 0; series < seriesCount; ++series) {
                    final Number number = dataset.getValue(series, item);
                    if (number != null) {
                        final double value = number.doubleValue();
                        if (value > 0.0) {
                            positive[groupIndex[series]] += value;
                        }
                        if (value < 0.0) {
                            negative[groupIndex[series]] += value;
                        }
                    }
                }
                for (int g = 0; g < groupCount; ++g) {
                    minimum[g] = Math.min(minimum[g], negative[g]);
                    maximum[g] = Math.max(maximum[g], positive[g]);
                }
            }
            for (int j = 0; j < groupCount; ++j) {
                result = Range.combine(result, new Range(minimum[j], maximum[j]));
            }
        }
        return result;
    }
    
    public static Number findMinimumStackedRangeValue(final CategoryDataset dataset) {
        Number result = null;
        if (dataset != null) {
            double minimum = 0.0;
            for (int categoryCount = dataset.getRowCount(), item = 0; item < categoryCount; ++item) {
                double total = 0.0;
                for (int seriesCount = dataset.getColumnCount(), series = 0; series < seriesCount; ++series) {
                    final Number number = dataset.getValue(series, item);
                    if (number != null) {
                        final double value = number.doubleValue();
                        if (value < 0.0) {
                            total += value;
                        }
                    }
                }
                minimum = Math.min(minimum, total);
            }
            result = new Double(minimum);
        }
        return result;
    }
    
    public static Number findMaximumStackedRangeValue(final CategoryDataset dataset) {
        Number result = null;
        if (dataset != null) {
            double maximum = 0.0;
            for (int categoryCount = dataset.getColumnCount(), item = 0; item < categoryCount; ++item) {
                double total = 0.0;
                for (int seriesCount = dataset.getRowCount(), series = 0; series < seriesCount; ++series) {
                    final Number number = dataset.getValue(series, item);
                    if (number != null) {
                        final double value = number.doubleValue();
                        if (value > 0.0) {
                            total += value;
                        }
                    }
                }
                maximum = Math.max(maximum, total);
            }
            result = new Double(maximum);
        }
        return result;
    }
    
    public static Range findStackedRangeBounds(final TableXYDataset dataset) {
        return findStackedRangeBounds(dataset, 0.0);
    }
    
    public static Range findStackedRangeBounds(final TableXYDataset dataset, final double base) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        double minimum = base;
        double maximum = base;
        for (int itemNo = 0; itemNo < dataset.getItemCount(); ++itemNo) {
            double positive = base;
            double negative = base;
            for (int seriesCount = dataset.getSeriesCount(), seriesNo = 0; seriesNo < seriesCount; ++seriesNo) {
                final double y = dataset.getYValue(seriesNo, itemNo);
                if (!Double.isNaN(y)) {
                    if (y > 0.0) {
                        positive += y;
                    }
                    else {
                        negative += y;
                    }
                }
            }
            if (positive > maximum) {
                maximum = positive;
            }
            if (negative < minimum) {
                minimum = negative;
            }
        }
        if (minimum <= maximum) {
            return new Range(minimum, maximum);
        }
        return null;
    }
    
    public static Range findCumulativeRangeBounds(final CategoryDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        boolean allItemsNull = true;
        double minimum = 0.0;
        double maximum = 0.0;
        for (int row = 0; row < dataset.getRowCount(); ++row) {
            double runningTotal = 0.0;
            for (int column = 0; column < dataset.getColumnCount() - 1; ++column) {
                final Number n = dataset.getValue(row, column);
                if (n != null) {
                    allItemsNull = false;
                    final double value = n.doubleValue();
                    runningTotal += value;
                    minimum = Math.min(minimum, runningTotal);
                    maximum = Math.max(maximum, runningTotal);
                }
            }
        }
        if (!allItemsNull) {
            return new Range(minimum, maximum);
        }
        return null;
    }
}
