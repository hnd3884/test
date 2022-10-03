package org.jfree.data.xy;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.AbstractSeriesDataset;

public abstract class AbstractXYDataset extends AbstractSeriesDataset implements XYDataset
{
    public DomainOrder getDomainOrder() {
        return DomainOrder.NONE;
    }
    
    public double getXValue(final int series, final int item) {
        double result = Double.NaN;
        final Number x = this.getX(series, item);
        if (x != null) {
            result = x.doubleValue();
        }
        return result;
    }
    
    public double getYValue(final int series, final int item) {
        double result = Double.NaN;
        final Number y = this.getY(series, item);
        if (y != null) {
            result = y.doubleValue();
        }
        return result;
    }
}
