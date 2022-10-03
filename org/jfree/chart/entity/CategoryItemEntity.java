package org.jfree.chart.entity;

import org.jfree.util.ObjectUtilities;
import java.awt.Shape;
import org.jfree.data.category.CategoryDataset;
import java.io.Serializable;

public class CategoryItemEntity extends ChartEntity implements Cloneable, Serializable
{
    private static final long serialVersionUID = -8657249457902337349L;
    private transient CategoryDataset dataset;
    private int series;
    private Object category;
    private int categoryIndex;
    
    public CategoryItemEntity(final Shape area, final String toolTipText, final String urlText, final CategoryDataset dataset, final int series, final Object category, final int categoryIndex) {
        super(area, toolTipText, urlText);
        this.dataset = dataset;
        this.series = series;
        this.category = category;
        this.categoryIndex = categoryIndex;
    }
    
    public CategoryDataset getDataset() {
        return this.dataset;
    }
    
    public void setDataset(final CategoryDataset dataset) {
        this.dataset = dataset;
    }
    
    public int getSeries() {
        return this.series;
    }
    
    public void setSeries(final int series) {
        this.series = series;
    }
    
    public Object getCategory() {
        return this.category;
    }
    
    public void setCategory(final Object category) {
        this.category = category;
    }
    
    public int getCategoryIndex() {
        return this.categoryIndex;
    }
    
    public void setCategoryIndex(final int index) {
        this.categoryIndex = index;
    }
    
    public String toString() {
        return "Category Item: series=" + this.series + ", category=" + this.category.toString();
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CategoryItemEntity && super.equals(obj)) {
            final CategoryItemEntity cie = (CategoryItemEntity)obj;
            return this.categoryIndex == cie.categoryIndex && this.series == cie.series && ObjectUtilities.equal(this.category, cie.category);
        }
        return false;
    }
}
