package org.jfree.chart.axis;

import org.jfree.ui.TextAnchor;
import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.RectangleAnchor;
import java.io.Serializable;

public class CategoryLabelPosition implements Serializable
{
    private static final long serialVersionUID = 5168681143844183864L;
    private RectangleAnchor categoryAnchor;
    private TextBlockAnchor labelAnchor;
    private TextAnchor rotationAnchor;
    private double angle;
    private CategoryLabelWidthType widthType;
    private float widthRatio;
    
    public CategoryLabelPosition() {
        this(RectangleAnchor.CENTER, TextBlockAnchor.BOTTOM_CENTER, TextAnchor.CENTER, 0.0, CategoryLabelWidthType.CATEGORY, 0.95f);
    }
    
    public CategoryLabelPosition(final RectangleAnchor categoryAnchor, final TextBlockAnchor labelAnchor) {
        this(categoryAnchor, labelAnchor, TextAnchor.CENTER, 0.0, CategoryLabelWidthType.CATEGORY, 0.95f);
    }
    
    public CategoryLabelPosition(final RectangleAnchor categoryAnchor, final TextBlockAnchor labelAnchor, final CategoryLabelWidthType widthType, final float widthRatio) {
        this(categoryAnchor, labelAnchor, TextAnchor.CENTER, 0.0, widthType, widthRatio);
    }
    
    public CategoryLabelPosition(final RectangleAnchor categoryAnchor, final TextBlockAnchor labelAnchor, final TextAnchor rotationAnchor, final double angle, final CategoryLabelWidthType widthType, final float widthRatio) {
        if (categoryAnchor == null) {
            throw new IllegalArgumentException("Null 'categoryAnchor' argument.");
        }
        if (labelAnchor == null) {
            throw new IllegalArgumentException("Null 'labelAnchor' argument.");
        }
        if (rotationAnchor == null) {
            throw new IllegalArgumentException("Null 'rotationAnchor' argument.");
        }
        if (widthType == null) {
            throw new IllegalArgumentException("Null 'widthType' argument.");
        }
        this.categoryAnchor = categoryAnchor;
        this.labelAnchor = labelAnchor;
        this.rotationAnchor = rotationAnchor;
        this.angle = angle;
        this.widthType = widthType;
        this.widthRatio = widthRatio;
    }
    
    public RectangleAnchor getCategoryAnchor() {
        return this.categoryAnchor;
    }
    
    public TextBlockAnchor getLabelAnchor() {
        return this.labelAnchor;
    }
    
    public TextAnchor getRotationAnchor() {
        return this.rotationAnchor;
    }
    
    public double getAngle() {
        return this.angle;
    }
    
    public CategoryLabelWidthType getWidthType() {
        return this.widthType;
    }
    
    public float getWidthRatio() {
        return this.widthRatio;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryLabelPosition)) {
            return false;
        }
        final CategoryLabelPosition that = (CategoryLabelPosition)obj;
        return this.categoryAnchor.equals((Object)that.categoryAnchor) && this.labelAnchor.equals((Object)that.labelAnchor) && this.rotationAnchor.equals((Object)that.rotationAnchor) && this.angle == that.angle && this.widthType == that.widthType && this.widthRatio == that.widthRatio;
    }
    
    public int hashCode() {
        int result = 19;
        result = 37 * result + this.categoryAnchor.hashCode();
        result = 37 * result + this.labelAnchor.hashCode();
        result = 37 * result + this.rotationAnchor.hashCode();
        return result;
    }
}
