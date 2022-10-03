package org.jfree.chart.labels;

import java.text.MessageFormat;
import org.jfree.data.xy.XYDataset;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class StandardXYSeriesLabelGenerator implements XYSeriesLabelGenerator, Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = 1916017081848400024L;
    public static final String DEFAULT_LABEL_FORMAT = "{0}";
    private String formatPattern;
    
    public StandardXYSeriesLabelGenerator() {
        this("{0}");
    }
    
    public StandardXYSeriesLabelGenerator(final String format) {
        if (format == null) {
            throw new IllegalArgumentException("Null 'format' argument.");
        }
        this.formatPattern = format;
    }
    
    public String generateLabel(final XYDataset dataset, final int series) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        final String label = MessageFormat.format(this.formatPattern, this.createItemArray(dataset, series));
        return label;
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
