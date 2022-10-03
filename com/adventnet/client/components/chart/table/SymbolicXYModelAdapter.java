package com.adventnet.client.components.chart.table;

import java.util.Iterator;
import java.util.Collection;
import com.adventnet.client.components.chart.table.internal.FilterUtil;
import java.util.ArrayList;
import javax.swing.table.TableModel;
import java.util.List;
import java.util.Map;
import org.jfree.data.xy.XisSymbolic;
import org.jfree.data.xy.YisSymbolic;
import org.jfree.data.xy.XYDataset;

public class SymbolicXYModelAdapter extends AbstractXYModelAdapter implements XYDataset, YisSymbolic, XisSymbolic
{
    private String axis;
    Map categoryMap;
    private List actualCategories;
    private List displayCategories;
    
    public SymbolicXYModelAdapter(final TableModel model) {
        this(model, null);
    }
    
    public SymbolicXYModelAdapter(final GraphData model) {
        this(model, null);
    }
    
    public SymbolicXYModelAdapter(final GraphData model, final Map categoryMap) {
        super(model);
        this.axis = null;
        this.categoryMap = null;
        this.actualCategories = null;
        this.displayCategories = null;
        this.categoryMap = categoryMap;
    }
    
    public SymbolicXYModelAdapter(final TableModel model, final Map categoryMap) {
        super(model);
        this.axis = null;
        this.categoryMap = null;
        this.actualCategories = null;
        this.displayCategories = null;
        this.categoryMap = categoryMap;
    }
    
    public void setCategoryMap(final Map categoryMap) {
        this.categoryMap = categoryMap;
        this.fireDatasetChanged();
    }
    
    List getActualCategories() {
        if (this.actualCategories == null) {
            this.manipulateCategories();
        }
        return this.actualCategories;
    }
    
    List getDisplayCategories() {
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
            final Object col = axisColumns.get(this.getAxis());
            final int colIndex = FilterUtil.getIndex(tm, col);
            final Collection tempHolder = FilterUtil.getDistinctValue(tm, colIndex);
            final Iterator iterator = tempHolder.iterator();
            while (iterator.hasNext()) {
                if (this.actualCategories == null) {
                    this.actualCategories = new ArrayList();
                }
                this.actualCategories.add(iterator.next());
            }
        }
    }
    
    protected void fireDatasetChanged() {
        this.actualCategories = null;
        this.displayCategories = null;
        this.axis = null;
        super.fireDatasetChanged();
    }
    
    private String getAxis() {
        if (this.axis == null) {
            final TableModel tm = super.getModel().getData();
            final Map axisColumns = super.getModel().getAxisColumns();
            final Object col = axisColumns.get("X");
            final int xColIndex = FilterUtil.getIndex(tm, col);
            final Object value = tm.getValueAt(0, xColIndex);
            if (value instanceof String) {
                this.axis = "X";
            }
            else {
                this.axis = "Y";
            }
        }
        return this.axis;
    }
    
    private Number getNumericValue(final int series, final int item, final String type) {
        final Object value = this.getValue(series, item, type);
        if (this.getAxis().equals(type)) {
            final int index = this.getActualCategories().indexOf(value);
            final Number numValue = new Integer(index);
            return numValue;
        }
        return this.convertToNumber(value);
    }
    
    private String getDisplayValue(final Object value) {
        final int index = this.getActualCategories().indexOf(value);
        final String displayCategory = this.getDisplayCategories().get(index);
        return displayCategory;
    }
    
    public Number getX(final int series, final int item) {
        return this.getNumericValue(series, item, "X");
    }
    
    public Number getY(final int series, final int item) {
        return this.getNumericValue(series, item, "Y");
    }
    
    public String getYSymbolicValue(final Integer val) {
        return this.getDisplayCategories().get(val);
    }
    
    public String getYSymbolicValue(final int series, final int item) {
        final Object value = this.getValue(series, item, "Y");
        return this.getDisplayValue(value);
    }
    
    public String[] getYSymbolicValues() {
        final List tempDSC = this.getDisplayCategories();
        if (tempDSC.size() != 0) {
            return tempDSC.toArray(new String[tempDSC.size()]);
        }
        return new String[0];
    }
    
    public String getXSymbolicValue(final Integer val) {
        return this.getDisplayCategories().get(val);
    }
    
    public String getXSymbolicValue(final int series, final int item) {
        final Object value = this.getValue(series, item, "X");
        return this.getDisplayValue(value);
    }
    
    public String[] getXSymbolicValues() {
        return this.getYSymbolicValues();
    }
}
