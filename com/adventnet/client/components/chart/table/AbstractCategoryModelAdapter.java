package com.adventnet.client.components.chart.table;

import java.util.Iterator;
import java.util.Collection;
import com.adventnet.client.components.chart.table.internal.FilterUtil;
import java.util.ArrayList;
import javax.swing.table.TableModel;
import java.util.List;
import java.util.Map;

public class AbstractCategoryModelAdapter extends AbstractChartModelAdapter
{
    Map categoryMap;
    private List actualCategories;
    private List displayCategories;
    
    public AbstractCategoryModelAdapter(final TableModel model, final Map categoryMap) {
        super(model);
        this.categoryMap = null;
        this.actualCategories = null;
        this.displayCategories = null;
        this.categoryMap = categoryMap;
    }
    
    public AbstractCategoryModelAdapter(final GraphData model, final Map categoryMap) {
        super(model);
        this.categoryMap = null;
        this.actualCategories = null;
        this.displayCategories = null;
        this.categoryMap = categoryMap;
    }
    
    @Override
    public String getSeriesOrPieName(final int i) {
        final Object seriesCol = super.model.getAxisColumns().get("SERIES");
        if (seriesCol != null) {
            return this.getSeriesNames().get(i);
        }
        final Object xCol = super.model.getAxisColumns().get("X");
        if (xCol != null) {
            return this.getActualCategories().get(i).toString();
        }
        return null;
    }
    
    private String getSeriesName(final int series) {
        return super.getDisplayName(series);
    }
    
    public void setCategoryMap(final Map categoryMap) {
        this.categoryMap = categoryMap;
        this.fireDatasetChanged();
    }
    
    public List getActualCategories() {
        if (this.actualCategories == null) {
            this.manipulateCategories();
        }
        return this.actualCategories;
    }
    
    public List getDisplayCategories() {
        if (this.displayCategories == null) {
            if (this.getActualCategories() != null) {
                this.displayCategories = new ArrayList();
                for (int size = this.actualCategories.size(), i = 0; i < size; ++i) {
                    final Object key = this.actualCategories.get(i);
                    Object displayValue = (this.categoryMap != null) ? this.categoryMap.get(key) : key;
                    if (displayValue == null) {
                        displayValue = key;
                    }
                    if (displayValue instanceof String) {
                        this.displayCategories.add(displayValue);
                    }
                    else {
                        this.displayCategories.add(displayValue.toString());
                    }
                }
            }
            else {
                this.displayCategories = new ArrayList(1);
            }
        }
        return this.displayCategories;
    }
    
    private void manipulateCategories() {
        final TableModel tm = super.getModel().getData();
        if (tm.getRowCount() > 0) {
            final Map axisColumns = super.getModel().getAxisColumns();
            final Object xCol = axisColumns.get("X");
            final int xColIndex = FilterUtil.getIndex(tm, xCol);
            final Collection tempHolder = FilterUtil.getDistinctValue(tm, xColIndex);
            final Iterator iterator = tempHolder.iterator();
            while (iterator.hasNext()) {
                if (this.actualCategories == null) {
                    this.actualCategories = new ArrayList();
                }
                this.actualCategories.add(iterator.next());
            }
        }
    }
    
    @Override
    protected void fireDatasetChanged() {
        this.actualCategories = null;
        this.displayCategories = null;
        super.fireDatasetChanged();
    }
    
    Number getValue(final boolean fromBaseTable, final int row, final Object category, final boolean isDisplayValue) {
        TableModel tm = null;
        if (fromBaseTable) {
            tm = super.getModel().getData();
        }
        else {
            final Object[] data = this.getSeriesList().get(row);
            tm = (TableModel)data[1];
        }
        Object actualCategory = null;
        if (isDisplayValue) {
            final int pos = this.getDisplayCategories().indexOf(category);
            actualCategory = this.getActualCategories().get(pos);
        }
        else {
            actualCategory = category;
        }
        final Map axisColumns = super.getModel().getAxisColumns();
        final Object xCol = axisColumns.get("X");
        final int xColIndex = FilterUtil.getIndex(tm, xCol);
        final Object yCol = axisColumns.get("Y");
        final int yColIndex = FilterUtil.getIndex(tm, yCol);
        for (int rowCount = tm.getRowCount(), i = 0; i < rowCount; ++i) {
            final Object xValue = tm.getValueAt(i, xColIndex);
            final Object yValue = tm.getValueAt(i, yColIndex);
            if (xValue.equals(actualCategory)) {
                return this.convertToNumber(yValue);
            }
        }
        return null;
    }
}
