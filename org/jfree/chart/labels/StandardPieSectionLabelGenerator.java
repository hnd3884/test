package org.jfree.chart.labels;

import org.jfree.data.general.PieDataset;
import java.text.AttributedString;
import java.text.NumberFormat;
import org.jfree.util.ObjectList;
import java.io.Serializable;

public class StandardPieSectionLabelGenerator extends AbstractPieItemLabelGenerator implements PieSectionLabelGenerator, Cloneable, Serializable
{
    private static final long serialVersionUID = 3064190563760203668L;
    public static final String DEFAULT_SECTION_LABEL_FORMAT = "{0}";
    private ObjectList attributedLabels;
    
    public StandardPieSectionLabelGenerator() {
        this("{0}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance());
    }
    
    public StandardPieSectionLabelGenerator(final String labelFormat) {
        this(labelFormat, NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance());
    }
    
    public StandardPieSectionLabelGenerator(final String labelFormat, final NumberFormat numberFormat, final NumberFormat percentFormat) {
        super(labelFormat, numberFormat, percentFormat);
        this.attributedLabels = new ObjectList();
    }
    
    public AttributedString getAttributedLabel(final int section) {
        return (AttributedString)this.attributedLabels.get(section);
    }
    
    public void setAttributedLabel(final int section, final AttributedString label) {
        this.attributedLabels.set(section, (Object)label);
    }
    
    public String generateSectionLabel(final PieDataset dataset, final Comparable key) {
        return super.generateSectionLabel(dataset, key);
    }
    
    public AttributedString generateAttributedSectionLabel(final PieDataset dataset, final Comparable key) {
        return this.getAttributedLabel(dataset.getIndex(key));
    }
    
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof StandardPieSectionLabelGenerator && super.equals(obj));
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
