package com.adventnet.client.components.chart.table;

import java.util.List;
import java.util.Map;
import javax.swing.table.TableModel;
import org.jfree.data.KeyedValues;
import org.jfree.data.general.PieDataset;

public class PieModelAdapter extends AbstractCategoryModelAdapter implements PieDataset, KeyedValues
{
    public PieModelAdapter(final TableModel model) {
        super(model, null);
    }
    
    public PieModelAdapter(final GraphData model) {
        super(model, null);
    }
    
    public PieModelAdapter(final TableModel model, final Map categoryMap) {
        super(model, categoryMap);
    }
    
    public PieModelAdapter(final GraphData model, final Map categoryMap) {
        super(model, categoryMap);
    }
    
    public Comparable getKey(final int index) {
        return this.getDisplayCategories().get(index);
    }
    
    public int getIndex(final Comparable key) {
        return this.getDisplayCategories().indexOf(key);
    }
    
    public List getKeys() {
        return this.getDisplayCategories();
    }
    
    public Number getValue(final Comparable key) {
        return this.getValue(true, -1, key, true);
    }
    
    public int getItemCount() {
        final List data = this.getActualCategories();
        if (data == null) {
            return 0;
        }
        return data.size();
    }
    
    public Number getValue(final int item) {
        final List categories = this.getActualCategories();
        if (categories != null) {
            return this.getValue(true, -1, categories.get(item), false);
        }
        return null;
    }
}
