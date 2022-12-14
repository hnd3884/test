package org.jfree.chart.labels;

import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.category.CategoryDataset;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class IntervalCategoryItemLabelGenerator extends StandardCategoryItemLabelGenerator implements CategoryItemLabelGenerator, PublicCloneable, Cloneable, Serializable
{
    private static final long serialVersionUID = 5056909225610630529L;
    public static final String DEFAULT_LABEL_FORMAT_STRING = "({0}, {1}) = {3} - {4}";
    
    public IntervalCategoryItemLabelGenerator() {
        super("({0}, {1}) = {3} - {4}", NumberFormat.getInstance());
    }
    
    public IntervalCategoryItemLabelGenerator(final String labelFormat, final NumberFormat formatter) {
        super(labelFormat, formatter);
    }
    
    public IntervalCategoryItemLabelGenerator(final String labelFormat, final DateFormat formatter) {
        super(labelFormat, formatter);
    }
    
    protected Object[] createItemArray(final CategoryDataset dataset, final int row, final int column) {
        final Object[] result = { dataset.getRowKey(row).toString(), dataset.getColumnKey(column).toString(), null, null, null };
        final Number value = dataset.getValue(row, column);
        if (this.getNumberFormat() != null) {
            result[2] = this.getNumberFormat().format(value);
        }
        else if (this.getDateFormat() != null) {
            result[2] = this.getDateFormat().format(value);
        }
        if (dataset instanceof IntervalCategoryDataset) {
            final IntervalCategoryDataset icd = (IntervalCategoryDataset)dataset;
            final Number start = icd.getStartValue(row, column);
            final Number end = icd.getEndValue(row, column);
            if (this.getNumberFormat() != null) {
                result[3] = this.getNumberFormat().format(start);
                result[4] = this.getNumberFormat().format(end);
            }
            else if (this.getDateFormat() != null) {
                result[3] = this.getDateFormat().format(start);
                result[4] = this.getDateFormat().format(end);
            }
        }
        return result;
    }
}
