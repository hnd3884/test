package com.adventnet.client.components.chart.table;

import java.util.List;
import java.util.Map;
import javax.swing.table.TableModel;
import org.jfree.data.Values2D;
import org.jfree.data.KeyedValues2D;
import org.jfree.data.category.CategoryDataset;

public class CategoryModelAdapter extends AbstractCategoryModelAdapter implements CategoryDataset, KeyedValues2D, Values2D
{
    public CategoryModelAdapter(final TableModel model) {
        super(model, null);
    }
    
    public CategoryModelAdapter(final GraphData model) {
        super(model, null);
    }
    
    public CategoryModelAdapter(final TableModel model, final Map categoryModelMap) {
        super(model, categoryModelMap);
    }
    
    public CategoryModelAdapter(final GraphData model, final Map categoryModelMap) {
        super(model, categoryModelMap);
    }
    
    public int getRowCount() {
        return this.getSeriesList().size();
    }
    
    public int getColumnCount() {
        final List data = this.getActualCategories();
        if (data != null) {
            return data.size();
        }
        return 0;
    }
    
    public Number getValue(final int row, final int column) {
        return this.getValue(false, row, this.getActualCategories().get(column), false);
    }
    
    public Comparable getRowKey(final int row) {
        return super.getDisplayName(row);
    }
    
    public int getRowIndex(final Comparable key) {
        return super.getSeriesNames().indexOf(key);
    }
    
    public List getRowKeys() {
        return super.getSeriesNames();
    }
    
    public Comparable getColumnKey(final int column) {
        return this.getDisplayCategories().get(column);
    }
    
    public int getColumnIndex(final Comparable key) {
        return this.getDisplayCategories().indexOf(key);
    }
    
    public List getColumnKeys() {
        return this.getDisplayCategories();
    }
    
    public Number getValue(final Comparable rowKey, final Comparable columnKey) {
        int rowIndex = 0;
        int columnIndex = 0;
        rowIndex = this.getRowIndex(rowKey);
        columnIndex = this.getColumnIndex(columnKey);
        return this.getValue(rowIndex, columnIndex);
    }
}
