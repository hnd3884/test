package com.adventnet.client.components.chart.web;

import org.jfree.data.xy.IntervalXYDataset;
import com.adventnet.client.components.chart.table.AbstractXYModelAdapter;
import org.jfree.data.xy.XYDataset;
import java.io.Serializable;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.urls.XYURLGenerator;

public class ChartIntervalURLGenerator implements XYURLGenerator, XYToolTipGenerator, Serializable
{
    private String prefix;
    private String seriesParameterName;
    private String xParamName;
    private String yParamName;
    private String startX;
    private String startY;
    private String endX;
    private String endY;
    
    public ChartIntervalURLGenerator(final String prefix, final String seriesParameterName, final String startX, final String endX, final String startY, final String endY) {
        this.prefix = "index.html";
        this.seriesParameterName = "series";
        this.xParamName = "X";
        this.yParamName = "Y";
        this.startX = "XSTART";
        this.startY = "YSTART";
        this.endX = "YSTART";
        this.endY = "YEND";
        this.prefix = prefix;
        this.seriesParameterName = seriesParameterName;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }
    
    public String generateURL(final XYDataset dataset, final int series, final int item) {
        String url = this.prefix;
        final AbstractXYModelAdapter model = (AbstractXYModelAdapter)dataset;
        final String seriesName = model.getSeriesKey(series).toString();
        final IntervalXYDataset data = (IntervalXYDataset)dataset;
        final String xStartVValue = (data.getStartX(series, item) != null) ? data.getStartX(series, item).toString() : "null";
        final String xEndValue = (data.getEndX(series, item) != null) ? data.getEndX(series, item).toString() : "null";
        final String yStartVValue = (data.getStartY(series, item) != null) ? data.getStartY(series, item).toString() : "null";
        final String yEndValue = (data.getEndY(series, item) != null) ? data.getEndY(series, item).toString() : "null";
        final boolean firstParameter = url.indexOf("?") == -1;
        url += (firstParameter ? "?" : "&");
        url = url + this.seriesParameterName + "=" + seriesName + "&" + this.startX + "=" + xStartVValue + "&" + this.endX + "=" + xEndValue + "&" + this.startY + "=" + yStartVValue + "&" + this.endY + "=" + yEndValue;
        return url;
    }
    
    public String generateToolTip(final XYDataset dataset, final int series, final int item) {
        final String seriesName = dataset.getSeriesKey(series).toString();
        final IntervalXYDataset data = (IntervalXYDataset)dataset;
        final String xStartVValue = (data.getStartX(series, item) != null) ? data.getStartX(series, item).toString() : "null";
        final String xEndValue = (data.getEndX(series, item) != null) ? data.getEndX(series, item).toString() : "null";
        final String yStartVValue = (data.getStartY(series, item) != null) ? data.getStartY(series, item).toString() : "null";
        final String yEndValue = (data.getEndY(series, item) != null) ? data.getEndY(series, item).toString() : "null";
        String toolTip = this.seriesParameterName + "=" + seriesName + "<br>" + this.startX + "=" + xStartVValue + "<br>" + this.endX + "=" + xEndValue;
        if (!this.startX.equals(this.endX)) {
            toolTip = toolTip + "<br>" + this.startY + "=" + yStartVValue + "<br>" + this.endY + "=" + yEndValue;
        }
        else {
            toolTip = toolTip + "<br>" + this.startY + "=" + yStartVValue;
        }
        return toolTip;
    }
}
