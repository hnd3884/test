package org.jfree.chart.axis;

import java.text.NumberFormat;
import java.text.DecimalFormat;

public class StandardTickUnitSource implements TickUnitSource
{
    private static final double LOG_10_VALUE;
    
    public TickUnit getLargerTickUnit(final TickUnit unit) {
        final double x = unit.getSize();
        final double log = Math.log(x) / StandardTickUnitSource.LOG_10_VALUE;
        final double higher = Math.ceil(log);
        return new NumberTickUnit(Math.pow(10.0, higher), new DecimalFormat("0.0E0"));
    }
    
    public TickUnit getCeilingTickUnit(final TickUnit unit) {
        return this.getLargerTickUnit(unit);
    }
    
    public TickUnit getCeilingTickUnit(final double size) {
        final double log = Math.log(size) / StandardTickUnitSource.LOG_10_VALUE;
        final double higher = Math.ceil(log);
        return new NumberTickUnit(Math.pow(10.0, higher), new DecimalFormat("0.0E0"));
    }
    
    static {
        LOG_10_VALUE = Math.log(10.0);
    }
}
