package com.adventnet.client.components.chart.web;

import com.adventnet.client.components.chart.table.PieModelAdapter;
import org.jfree.data.general.PieDataset;
import org.jfree.chart.labels.PieToolTipGenerator;
import java.io.Serializable;
import org.jfree.chart.urls.PieURLGenerator;

public class ChartPieURLGenerator implements PieURLGenerator, Serializable, PieToolTipGenerator
{
    private String prefix;
    private String categoryParameterName;
    String valuePrefix;
    
    public ChartPieURLGenerator(final String prefix, final String categoryParameterName) {
        this.prefix = "index.html";
        this.categoryParameterName = "X";
        this.valuePrefix = "Value";
        this.prefix = prefix;
        this.categoryParameterName = categoryParameterName;
    }
    
    public ChartPieURLGenerator(final String prefix, final String categoryParameterName, final String valuePrefix) {
        this.prefix = "index.html";
        this.categoryParameterName = "X";
        this.valuePrefix = "Value";
        this.prefix = prefix;
        this.categoryParameterName = categoryParameterName;
        this.valuePrefix = valuePrefix;
    }
    
    public String generateURL(final PieDataset data, final Comparable key, final int pieIndex) {
        String url = this.prefix;
        String category = (key == null) ? "null" : key.toString();
        if (data instanceof PieModelAdapter) {
            final int colIndex = data.getIndex(key);
            final Object val = ((PieModelAdapter)data).getActualCategories().get(colIndex);
            category = ((val == null) ? "null" : val.toString());
        }
        if (url.indexOf("?") > -1) {
            url = url + "&" + this.categoryParameterName + "=" + category;
        }
        else {
            url = url + "?" + this.categoryParameterName + "=" + category;
        }
        return url;
    }
    
    public String generateToolTip(final PieDataset data, final Comparable key) {
        final int colIndex = data.getIndex(key);
        String toolTip = this.categoryParameterName + "=" + key;
        toolTip = toolTip + "<br>" + this.valuePrefix + "=" + data.getValue(key);
        return toolTip;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o instanceof ChartPieURLGenerator) {
            final ChartPieURLGenerator generator = (ChartPieURLGenerator)o;
            return this.prefix.equals(generator.prefix);
        }
        return false;
    }
}
