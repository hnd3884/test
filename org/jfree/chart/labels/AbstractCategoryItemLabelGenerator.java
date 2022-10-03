package org.jfree.chart.labels;

import org.jfree.util.ObjectUtilities;
import org.jfree.data.Values2D;
import org.jfree.data.DataUtilities;
import java.text.MessageFormat;
import org.jfree.data.category.CategoryDataset;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public abstract class AbstractCategoryItemLabelGenerator implements PublicCloneable, Cloneable, Serializable
{
    private static final long serialVersionUID = -7108591260223293197L;
    private String labelFormat;
    private String nullValueString;
    private NumberFormat numberFormat;
    private DateFormat dateFormat;
    private NumberFormat percentFormat;
    
    protected AbstractCategoryItemLabelGenerator(final String labelFormat, final NumberFormat formatter) {
        this(labelFormat, formatter, NumberFormat.getPercentInstance());
    }
    
    protected AbstractCategoryItemLabelGenerator(final String labelFormat, final NumberFormat formatter, final NumberFormat percentFormatter) {
        if (labelFormat == null) {
            throw new IllegalArgumentException("Null 'labelFormat' argument.");
        }
        if (formatter == null) {
            throw new IllegalArgumentException("Null 'formatter' argument.");
        }
        if (percentFormatter == null) {
            throw new IllegalArgumentException("Null 'percentFormatter' argument.");
        }
        this.labelFormat = labelFormat;
        this.numberFormat = formatter;
        this.percentFormat = percentFormatter;
        this.dateFormat = null;
        this.nullValueString = "-";
    }
    
    protected AbstractCategoryItemLabelGenerator(final String labelFormat, final DateFormat formatter) {
        if (labelFormat == null) {
            throw new IllegalArgumentException("Null 'labelFormat' argument.");
        }
        if (formatter == null) {
            throw new IllegalArgumentException("Null 'formatter' argument.");
        }
        this.labelFormat = labelFormat;
        this.numberFormat = null;
        this.percentFormat = NumberFormat.getPercentInstance();
        this.dateFormat = formatter;
        this.nullValueString = "-";
    }
    
    public String generateRowLabel(final CategoryDataset dataset, final int row) {
        return dataset.getRowKey(row).toString();
    }
    
    public String generateColumnLabel(final CategoryDataset dataset, final int column) {
        return dataset.getColumnKey(column).toString();
    }
    
    public String getLabelFormat() {
        return this.labelFormat;
    }
    
    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }
    
    public DateFormat getDateFormat() {
        return this.dateFormat;
    }
    
    protected String generateLabelString(final CategoryDataset dataset, final int row, final int column) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        String result = null;
        final Object[] items = this.createItemArray(dataset, row, column);
        result = MessageFormat.format(this.labelFormat, items);
        return result;
    }
    
    protected Object[] createItemArray(final CategoryDataset dataset, final int row, final int column) {
        final Object[] result = { dataset.getRowKey(row).toString(), dataset.getColumnKey(column).toString(), null, null };
        final Number value = dataset.getValue(row, column);
        if (value != null) {
            if (this.numberFormat != null) {
                result[2] = this.numberFormat.format(value);
            }
            else if (this.dateFormat != null) {
                result[2] = this.dateFormat.format(value);
            }
        }
        else {
            result[2] = this.nullValueString;
        }
        if (value != null) {
            final double total = DataUtilities.calculateColumnTotal(dataset, column);
            final double percent = value.doubleValue() / total;
            result[3] = this.percentFormat.format(percent);
        }
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractCategoryItemLabelGenerator)) {
            return false;
        }
        final AbstractCategoryItemLabelGenerator that = (AbstractCategoryItemLabelGenerator)obj;
        return this.labelFormat.equals(that.labelFormat) && ObjectUtilities.equal((Object)this.dateFormat, (Object)that.dateFormat) && ObjectUtilities.equal((Object)this.numberFormat, (Object)that.numberFormat);
    }
    
    public Object clone() throws CloneNotSupportedException {
        final AbstractCategoryItemLabelGenerator clone = (AbstractCategoryItemLabelGenerator)super.clone();
        if (this.numberFormat != null) {
            clone.numberFormat = (NumberFormat)this.numberFormat.clone();
        }
        if (this.dateFormat != null) {
            clone.dateFormat = (DateFormat)this.dateFormat.clone();
        }
        return clone;
    }
}
