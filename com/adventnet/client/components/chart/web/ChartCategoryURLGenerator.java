package com.adventnet.client.components.chart.web;

import com.adventnet.client.components.chart.table.AbstractCategoryModelAdapter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.urls.CategoryURLGenerator;

public class ChartCategoryURLGenerator implements CategoryURLGenerator, CategoryToolTipGenerator
{
    private String prefix;
    private String seriesParameterName;
    private String categoryParameterName;
    private String valueParameterName;
    
    public ChartCategoryURLGenerator(final String prefix, final String seriesParameterName, final String categoryParameterName, final String valueParameterName) {
        this.prefix = "index.html";
        this.seriesParameterName = "series";
        this.categoryParameterName = "category";
        this.valueParameterName = "Y";
        this.prefix = prefix;
        this.seriesParameterName = seriesParameterName;
        this.categoryParameterName = categoryParameterName;
        this.valueParameterName = valueParameterName;
    }
    
    public String generateURL(final CategoryDataset data, final int series, final int category) {
        String url = this.prefix;
        final boolean firstParameter = url.indexOf("?") == -1;
        url += (firstParameter ? "?" : "&");
        url = url + this.seriesParameterName + "=" + data.getRowKey(series);
        if (data instanceof AbstractCategoryModelAdapter) {
            url = url + "&" + this.categoryParameterName + "=" + ((AbstractCategoryModelAdapter)data).getActualCategories().get(category).toString();
        }
        else {
            url = url + "&" + this.categoryParameterName + "=" + data.getColumnKey(category);
        }
        return url;
    }
    
    public String generateToolTip(final CategoryDataset data, final int series, final int category) {
        String toolTip = "";
        toolTip = this.seriesParameterName + "=" + data.getRowKey(series);
        if (!this.seriesParameterName.equals(this.categoryParameterName)) {
            toolTip = toolTip + "<br>" + this.categoryParameterName + "=" + data.getColumnKey(category);
        }
        toolTip = toolTip + "<br>" + this.valueParameterName + "=" + data.getValue(series, category);
        return toolTip;
    }
}
