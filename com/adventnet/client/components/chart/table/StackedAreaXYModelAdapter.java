package com.adventnet.client.components.chart.table;

import javax.swing.table.TableModel;
import org.jfree.data.xy.TableXYDataset;

public class StackedAreaXYModelAdapter extends XYModelAdapter implements TableXYDataset
{
    public StackedAreaXYModelAdapter(final TableModel model) {
        super(model);
    }
    
    public StackedAreaXYModelAdapter(final GraphData model) {
        super(model);
    }
    
    @Override
    public int getItemCount() {
        final int sc = this.getSeriesCount();
        int itemSize = 0;
        if (sc > 0) {
            itemSize = this.getItemCount(0);
            for (int i = 1; i < sc; ++i) {
                final int ic = this.getItemCount(i);
                if (ic != itemSize) {
                    throw new RuntimeException("In stacked area chart all X values should be samevalue and same count");
                }
            }
        }
        return itemSize;
    }
}
