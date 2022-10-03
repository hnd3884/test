package org.jfree.chart.urls;

import java.util.Date;
import org.jfree.data.xy.XYDataset;
import java.text.DateFormat;
import java.io.Serializable;

public class TimeSeriesURLGenerator implements XYURLGenerator, Serializable
{
    private static final long serialVersionUID = -9122773175671182445L;
    private DateFormat dateFormat;
    private String prefix;
    private String seriesParameterName;
    private String itemParameterName;
    
    public TimeSeriesURLGenerator() {
        this.dateFormat = DateFormat.getInstance();
        this.prefix = "index.html";
        this.seriesParameterName = "series";
        this.itemParameterName = "item";
    }
    
    public TimeSeriesURLGenerator(final DateFormat dDateFormat, final String sPrefix, final String sSeriesParameterName, final String sItemParameterName) {
        this.dateFormat = DateFormat.getInstance();
        this.prefix = "index.html";
        this.seriesParameterName = "series";
        this.itemParameterName = "item";
        this.dateFormat = dDateFormat;
        this.prefix = sPrefix;
        this.seriesParameterName = sSeriesParameterName;
        this.itemParameterName = sItemParameterName;
    }
    
    public String generateURL(final XYDataset dataset, final int series, final int item) {
        String result = this.prefix;
        boolean firstParameter = result.indexOf("?") == -1;
        final Comparable seriesKey = dataset.getSeriesKey(series);
        if (seriesKey != null) {
            result += (firstParameter ? "?" : "&amp;");
            result = result + this.seriesParameterName + "=" + seriesKey.toString();
            firstParameter = false;
        }
        final long x = (long)dataset.getXValue(series, item);
        final String xValue = this.dateFormat.format(new Date(x));
        result += (firstParameter ? "?" : "&amp;");
        result = result + this.itemParameterName + "=" + xValue;
        return result;
    }
}
