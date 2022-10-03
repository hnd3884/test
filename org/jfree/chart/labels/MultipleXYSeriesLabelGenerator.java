package org.jfree.chart.labels;

import java.text.MessageFormat;
import org.jfree.data.xy.XYDataset;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class MultipleXYSeriesLabelGenerator implements XYSeriesLabelGenerator, Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = 138976236941898560L;
    public static final String DEFAULT_LABEL_FORMAT = "{0}";
    private String formatPattern;
    private String additionalFormatPattern;
    private Map seriesLabelLists;
    
    public MultipleXYSeriesLabelGenerator() {
        this("{0}");
    }
    
    public MultipleXYSeriesLabelGenerator(final String format) {
        if (format == null) {
            throw new IllegalArgumentException("Null 'format' argument.");
        }
        this.formatPattern = format;
        this.additionalFormatPattern = "\n{0}";
        this.seriesLabelLists = new HashMap();
    }
    
    public void addSeriesLabel(final int series, final String label) {
        final Integer key = new Integer(series);
        List labelList = this.seriesLabelLists.get(key);
        if (labelList == null) {
            labelList = new ArrayList();
            this.seriesLabelLists.put(key, labelList);
        }
        labelList.add(label);
    }
    
    public void clearSeriesLabels(final int series) {
        final Integer key = new Integer(series);
        this.seriesLabelLists.put(key, null);
    }
    
    public String generateLabel(final XYDataset dataset, final int series) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        final StringBuffer label = new StringBuffer();
        label.append(MessageFormat.format(this.formatPattern, this.createItemArray(dataset, series)));
        final Integer key = new Integer(series);
        final List extraLabels = this.seriesLabelLists.get(key);
        if (extraLabels != null) {
            final Object[] temp = { null };
            for (int i = 0; i < extraLabels.size(); ++i) {
                temp[0] = extraLabels.get(i);
                final String labelAddition = MessageFormat.format(this.additionalFormatPattern, temp);
                label.append(labelAddition);
            }
        }
        return label.toString();
    }
    
    protected Object[] createItemArray(final XYDataset dataset, final int series) {
        final Object[] result = { dataset.getSeriesKey(series).toString() };
        return result;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof StandardXYSeriesLabelGenerator && super.equals(obj));
    }
}
