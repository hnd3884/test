package com.adventnet.client.components.chart.web;

import java.util.Date;
import org.jfree.data.xy.XYZDataset;
import org.jfree.data.xy.XYDataset;
import java.io.Serializable;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.urls.XYURLGenerator;

public class ChartXYURLGenerator implements XYURLGenerator, XYToolTipGenerator, Serializable
{
    private String prefix;
    private String seriesParameterName;
    private String xParamName;
    private String yParamName;
    private String zParamName;
    boolean seriesAsYPfx;
    boolean isXTime;
    
    public ChartXYURLGenerator(final boolean isXTime, final String prefix, final String seriesParameterName, final String xParamName, final String yParamName, final String zParamName) {
        this.prefix = "index.html";
        this.seriesParameterName = "series";
        this.xParamName = "X";
        this.yParamName = "Y";
        this.zParamName = "Z";
        this.seriesAsYPfx = false;
        this.isXTime = false;
        this.isXTime = isXTime;
        this.prefix = prefix;
        this.seriesParameterName = seriesParameterName;
        this.xParamName = xParamName;
        this.yParamName = yParamName;
        this.zParamName = zParamName;
    }
    
    public ChartXYURLGenerator(final boolean isXTime, final String prefix, final String xParamName, final String zParamName, final boolean seriesAsYPfx) {
        this.prefix = "index.html";
        this.seriesParameterName = "series";
        this.xParamName = "X";
        this.yParamName = "Y";
        this.zParamName = "Z";
        this.seriesAsYPfx = false;
        this.isXTime = false;
        this.isXTime = isXTime;
        this.prefix = prefix;
        this.xParamName = xParamName;
        this.seriesAsYPfx = seriesAsYPfx;
        this.zParamName = zParamName;
    }
    
    public String generateURL(final XYDataset dataset, final int series, final int item) {
        String url = this.prefix;
        final String seriesName = dataset.getSeriesKey(series).toString();
        final String xValue = (dataset.getX(series, item) != null) ? dataset.getX(series, item).toString() : "Null";
        final String yValue = (dataset.getY(series, item) != null) ? dataset.getY(series, item).toString() : "Null";
        final boolean firstParameter = url.indexOf("?") == -1;
        url += (firstParameter ? "?" : "&");
        if (this.seriesAsYPfx) {
            url = url + this.xParamName + "=" + xValue + "&" + seriesName + "=" + yValue;
        }
        else {
            url = url + this.seriesParameterName + "=" + seriesName + "&" + this.xParamName + "=" + xValue + "&" + this.yParamName + "=" + yValue;
        }
        if (this.zParamName != null) {
            final XYZDataset xyz = (XYZDataset)dataset;
            final String zValue = (xyz.getZ(series, item) != null) ? xyz.getZ(series, item).toString() : "Null";
            url = url + "&" + this.zParamName + "=" + zValue;
        }
        return url;
    }
    
    public String generateToolTip(final XYDataset dataset, final int series, final int item) {
        String toolTip = null;
        final String seriesName = dataset.getSeriesKey(series).toString();
        final Object val = (dataset.getX(series, item) != null) ? dataset.getX(series, item) : "Null";
        String xValue = null;
        if (this.isXTime && val != null) {
            xValue = new Date(((Number)val).longValue()).toString();
        }
        else {
            xValue = val.toString();
        }
        final String yValue = (dataset.getY(series, item) != null) ? dataset.getY(series, item).toString() : "Null";
        if (this.seriesAsYPfx) {
            toolTip = this.xParamName + "=" + xValue + "<br>" + seriesName + "=" + yValue;
        }
        else {
            toolTip = this.seriesParameterName + "=" + seriesName + "<br>" + this.xParamName + "=" + xValue + "<br>" + this.yParamName + "=" + yValue;
        }
        if (this.zParamName != null) {
            final XYZDataset xyz = (XYZDataset)dataset;
            final String zValue = (xyz.getZ(series, item) != null) ? xyz.getZ(series, item).toString() : "Null";
            toolTip = toolTip + "<br>" + this.zParamName + "=" + zValue;
        }
        return toolTip;
    }
}
