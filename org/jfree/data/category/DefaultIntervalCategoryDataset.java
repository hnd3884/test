package org.jfree.data.category;

import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.jfree.data.DataUtilities;
import org.jfree.data.general.AbstractSeriesDataset;

public class DefaultIntervalCategoryDataset extends AbstractSeriesDataset implements IntervalCategoryDataset
{
    private Comparable[] seriesKeys;
    private Comparable[] categoryKeys;
    private Number[][] startData;
    private Number[][] endData;
    
    public DefaultIntervalCategoryDataset(final double[][] starts, final double[][] ends) {
        this(DataUtilities.createNumberArray2D(starts), DataUtilities.createNumberArray2D(ends));
    }
    
    public DefaultIntervalCategoryDataset(final Number[][] starts, final Number[][] ends) {
        this(null, null, starts, ends);
    }
    
    public DefaultIntervalCategoryDataset(final String[] seriesNames, final Number[][] starts, final Number[][] ends) {
        this(seriesNames, null, starts, ends);
    }
    
    public DefaultIntervalCategoryDataset(final Comparable[] seriesKeys, final Comparable[] categoryKeys, final Number[][] starts, final Number[][] ends) {
        this.startData = starts;
        this.endData = ends;
        if (starts != null && ends != null) {
            final String baseName = "org.jfree.data.resources.DataPackageResources";
            final ResourceBundle resources = ResourceBundle.getBundle(baseName);
            final int seriesCount = starts.length;
            if (seriesCount != ends.length) {
                final String errMsg = "DefaultIntervalCategoryDataset: the number of series in the start value dataset does not match the number of series in the end value dataset.";
                throw new IllegalArgumentException(errMsg);
            }
            if (seriesCount > 0) {
                if (seriesKeys != null) {
                    if (seriesKeys.length != seriesCount) {
                        throw new IllegalArgumentException("The number of series keys does not match the number of series in the data.");
                    }
                    this.seriesKeys = seriesKeys;
                }
                else {
                    final String prefix = resources.getString("series.default-prefix") + " ";
                    this.seriesKeys = this.generateKeys(seriesCount, prefix);
                }
                final int categoryCount = starts[0].length;
                if (categoryCount != ends[0].length) {
                    final String errMsg2 = "DefaultIntervalCategoryDataset: the number of categories in the start value dataset does not match the number of categories in the end value dataset.";
                    throw new IllegalArgumentException(errMsg2);
                }
                if (categoryKeys != null) {
                    if (categoryKeys.length != categoryCount) {
                        throw new IllegalArgumentException("The number of category keys does not match the number of categories in the data.");
                    }
                    this.categoryKeys = categoryKeys;
                }
                else {
                    final String prefix2 = resources.getString("categories.default-prefix") + " ";
                    this.categoryKeys = this.generateKeys(categoryCount, prefix2);
                }
            }
            else {
                this.seriesKeys = null;
                this.categoryKeys = null;
            }
        }
    }
    
    public int getSeriesCount() {
        int result = 0;
        if (this.startData != null) {
            result = this.startData.length;
        }
        return result;
    }
    
    public int getItemCount() {
        return this.categoryKeys.length;
    }
    
    public int getSeriesIndex(final Comparable series) {
        final List seriesKeys = this.getSeries();
        return seriesKeys.indexOf(series);
    }
    
    public Comparable getSeriesKey(final int series) {
        if (series >= this.getSeriesCount() || series < 0) {
            throw new IllegalArgumentException("No such series : " + series);
        }
        return this.seriesKeys[series];
    }
    
    public void setSeriesKeys(final Comparable[] seriesKeys) {
        if (seriesKeys == null) {
            throw new IllegalArgumentException("Null 'seriesKeys' argument.");
        }
        if (seriesKeys.length != this.getSeriesCount()) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setSeriesKeys(): the number of series keys does not match the data.");
        }
        this.seriesKeys = seriesKeys;
        this.fireDatasetChanged();
    }
    
    public int getCategoryCount() {
        int result = 0;
        if (this.startData != null && this.getSeriesCount() > 0) {
            result = this.startData[0].length;
        }
        return result;
    }
    
    public List getSeries() {
        if (this.seriesKeys == null) {
            return new ArrayList();
        }
        return Collections.unmodifiableList((List<?>)Arrays.asList((T[])this.seriesKeys));
    }
    
    public List getCategories() {
        return this.getColumnKeys();
    }
    
    public List getColumnKeys() {
        if (this.categoryKeys == null) {
            return new ArrayList();
        }
        return Collections.unmodifiableList((List<?>)Arrays.asList((T[])this.categoryKeys));
    }
    
    public void setCategoryKeys(final Comparable[] categoryKeys) {
        if (categoryKeys == null) {
            throw new IllegalArgumentException("Null 'categoryKeys' argument.");
        }
        if (categoryKeys.length != this.startData[0].length) {
            throw new IllegalArgumentException("The number of categories does not match the data.");
        }
        for (int i = 0; i < categoryKeys.length; ++i) {
            if (categoryKeys[i] == null) {
                throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setCategoryKeys(): null category not permitted.");
            }
        }
        this.categoryKeys = categoryKeys;
        this.fireDatasetChanged();
    }
    
    public Number getValue(final Comparable series, final Comparable category) {
        final int seriesIndex = this.getSeriesIndex(series);
        final int itemIndex = this.getColumnIndex(category);
        return this.getValue(seriesIndex, itemIndex);
    }
    
    public Number getValue(final int series, final int category) {
        return this.getEndValue(series, category);
    }
    
    public Number getStartValue(final Comparable series, final Comparable category) {
        final int seriesIndex = this.getSeriesIndex(series);
        final int itemIndex = this.getColumnIndex(category);
        return this.getStartValue(seriesIndex, itemIndex);
    }
    
    public Number getStartValue(final int series, final int category) {
        if (series < 0 || series >= this.getSeriesCount()) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.getValue(): series index out of range.");
        }
        if (category < 0 || category >= this.getCategoryCount()) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.getValue(): category index out of range.");
        }
        return this.startData[series][category];
    }
    
    public Number getEndValue(final Comparable series, final Comparable category) {
        final int seriesIndex = this.getSeriesIndex(series);
        final int itemIndex = this.getColumnIndex(category);
        return this.getEndValue(seriesIndex, itemIndex);
    }
    
    public Number getEndValue(final int series, final int category) {
        if (series < 0 || series >= this.getSeriesCount()) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.getValue(): series index out of range.");
        }
        if (category < 0 || category >= this.getCategoryCount()) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.getValue(): category index out of range.");
        }
        return this.endData[series][category];
    }
    
    public void setStartValue(final int series, final Comparable category, final Number value) {
        if (series < 0 || series > this.getSeriesCount()) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setValue: series outside valid range.");
        }
        final int categoryIndex = this.getCategoryIndex(category);
        if (categoryIndex < 0) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setValue: unrecognised category.");
        }
        this.startData[series][categoryIndex] = value;
        this.fireDatasetChanged();
    }
    
    public void setEndValue(final int series, final Comparable category, final Number value) {
        if (series < 0 || series > this.getSeriesCount()) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setValue: series outside valid range.");
        }
        final int categoryIndex = this.getCategoryIndex(category);
        if (categoryIndex < 0) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setValue: unrecognised category.");
        }
        this.endData[series][categoryIndex] = value;
        this.fireDatasetChanged();
    }
    
    private int getCategoryIndex(final Comparable category) {
        int result = -1;
        for (int i = 0; i < this.categoryKeys.length; ++i) {
            if (category.equals(this.categoryKeys[i])) {
                result = i;
                break;
            }
        }
        return result;
    }
    
    private Comparable[] generateKeys(final int count, final String prefix) {
        final Comparable[] result = new Comparable[count];
        for (int i = 0; i < count; ++i) {
            final String name = prefix + (i + 1);
            result[i] = name;
        }
        return result;
    }
    
    public Comparable getColumnKey(final int column) {
        return this.categoryKeys[column];
    }
    
    public int getColumnIndex(final Comparable columnKey) {
        final List categories = this.getCategories();
        return categories.indexOf(columnKey);
    }
    
    public int getRowIndex(final Comparable rowKey) {
        final List seriesKeys = this.getSeries();
        return seriesKeys.indexOf(rowKey);
    }
    
    public List getRowKeys() {
        if (this.seriesKeys == null) {
            return new ArrayList();
        }
        return Collections.unmodifiableList((List<?>)Arrays.asList((T[])this.seriesKeys));
    }
    
    public Comparable getRowKey(final int row) {
        if (row >= this.getRowCount() || row < 0) {
            throw new IllegalArgumentException("The 'row' argument is out of bounds.");
        }
        return this.seriesKeys[row];
    }
    
    public int getColumnCount() {
        int result = 0;
        if (this.startData != null && this.getSeriesCount() > 0) {
            result = this.startData[0].length;
        }
        return result;
    }
    
    public int getRowCount() {
        int result = 0;
        if (this.startData != null) {
            result = this.startData.length;
        }
        return result;
    }
}
