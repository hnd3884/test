package com.adventnet.client.components.chart.table;

import javax.swing.table.TableModel;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.data.xy.XYDataset;

public class XYModelAdapter extends AbstractXYModelAdapter implements XYDataset, XYZDataset, TableXYDataset
{
    public XYModelAdapter(final TableModel model) {
        super(model);
    }
    
    public XYModelAdapter(final GraphData model) {
        super(model);
    }
    
    public Number getX(final int series, final int item) {
        return this.convertToNumber(this.getValue(series, item, "X"));
    }
    
    public Number getY(final int series, final int item) {
        return this.convertToNumber(this.getValue(series, item, "Y"));
    }
    
    public double getZValue(final int series, final int item) {
        double result = Double.NaN;
        final Number z = this.getZ(series, item);
        if (z != null) {
            result = z.doubleValue();
        }
        return result;
    }
    
    public Number getZ(final int series, final int item) {
        return this.convertToNumber(this.getValue(series, item, "Z"));
    }
    
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
