package com.adventnet.client.components.chart.web;

import org.jfree.data.xy.XYDataset;
import org.jfree.chart.labels.StandardXYToolTipGenerator;

public class ChartXYToolTipGenerator extends StandardXYToolTipGenerator
{
    protected Object[] createItemArray(final XYDataset dataset, final int series, final int item) {
        final Object[] result = { dataset.getSeriesKey(series), null, null };
        final Number x = dataset.getXValue(series, item);
        if (super.getXDateFormat() != null) {
            result[1] = super.getXDateFormat().format(x);
        }
        else {
            result[1] = super.getXFormat().format(x);
        }
        final Number y = dataset.getYValue(series, item);
        if (y == null) {
            result[2] = "Null";
        }
        else if (super.getYDateFormat() != null) {
            result[2] = super.getYDateFormat().format(y);
        }
        else {
            result[2] = super.getYFormat().format(y);
        }
        return result;
    }
}
