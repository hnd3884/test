package com.adventnet.client.components.chart.table;

import javax.swing.table.TableModel;
import org.jfree.data.xy.IntervalXYDataset;

public class IntervalXYModelAdapter extends AbstractXYModelAdapter implements IntervalXYDataset
{
    public IntervalXYModelAdapter(final TableModel model) {
        super(model);
    }
    
    public IntervalXYModelAdapter(final GraphData model) {
        super(model);
    }
    
    public Number getX(final int series, final int item) {
        return this.getStartX(series, item);
    }
    
    public Number getY(final int series, final int item) {
        return this.getEndY(series, item);
    }
    
    public double getStartXValue(final int series, final int item) {
        double result = Double.NaN;
        final Number x = this.getStartX(series, item);
        if (x != null) {
            result = x.doubleValue();
        }
        return result;
    }
    
    public double getEndXValue(final int series, final int item) {
        double result = Double.NaN;
        final Number x = this.getEndX(series, item);
        if (x != null) {
            result = x.doubleValue();
        }
        return result;
    }
    
    public double getStartYValue(final int series, final int item) {
        double result = Double.NaN;
        final Number y = this.getStartY(series, item);
        if (y != null) {
            result = y.doubleValue();
        }
        return result;
    }
    
    public double getEndYValue(final int series, final int item) {
        double result = Double.NaN;
        final Number y = this.getEndY(series, item);
        if (y != null) {
            result = y.doubleValue();
        }
        return result;
    }
    
    public Number getStartX(final int series, final int item) {
        return this.convertToNumber(this.getValue(series, item, "X_START"));
    }
    
    public Number getEndX(final int series, final int item) {
        return this.convertToNumber(this.getValue(series, item, "X_END"));
    }
    
    public Number getStartY(final int series, final int item) {
        if (super.getModel().getAxisColumns().get("Y_START") != null) {
            return this.convertToNumber(this.getValue(series, item, "Y_START"));
        }
        return null;
    }
    
    public Number getEndY(final int series, final int item) {
        return this.convertToNumber(this.getValue(series, item, "Y_END"));
    }
}
