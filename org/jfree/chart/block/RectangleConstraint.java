package org.jfree.chart.block;

import org.jfree.ui.Size2D;
import org.jfree.data.Range;

public class RectangleConstraint
{
    public static final RectangleConstraint NONE;
    private double width;
    private Range widthRange;
    private LengthConstraintType widthConstraintType;
    private double height;
    private Range heightRange;
    private LengthConstraintType heightConstraintType;
    
    public RectangleConstraint(final double w, final double h) {
        this(w, null, LengthConstraintType.FIXED, h, null, LengthConstraintType.FIXED);
    }
    
    public RectangleConstraint(final Range w, final Range h) {
        this(0.0, w, LengthConstraintType.RANGE, 0.0, h, LengthConstraintType.RANGE);
    }
    
    public RectangleConstraint(final Range w, final double h) {
        this(0.0, w, LengthConstraintType.RANGE, h, null, LengthConstraintType.FIXED);
    }
    
    public RectangleConstraint(final double w, final Range h) {
        this(w, null, LengthConstraintType.FIXED, 0.0, h, LengthConstraintType.RANGE);
    }
    
    public RectangleConstraint(final double w, final Range widthRange, final LengthConstraintType widthConstraintType, final double h, final Range heightRange, final LengthConstraintType heightConstraintType) {
        if (widthConstraintType == null) {
            throw new IllegalArgumentException("Null 'widthType' argument.");
        }
        if (heightConstraintType == null) {
            throw new IllegalArgumentException("Null 'heightType' argument.");
        }
        this.width = w;
        this.widthRange = widthRange;
        this.widthConstraintType = widthConstraintType;
        this.height = h;
        this.heightRange = heightRange;
        this.heightConstraintType = heightConstraintType;
    }
    
    public double getWidth() {
        return this.width;
    }
    
    public Range getWidthRange() {
        return this.widthRange;
    }
    
    public LengthConstraintType getWidthConstraintType() {
        return this.widthConstraintType;
    }
    
    public double getHeight() {
        return this.height;
    }
    
    public Range getHeightRange() {
        return this.heightRange;
    }
    
    public LengthConstraintType getHeightConstraintType() {
        return this.heightConstraintType;
    }
    
    public RectangleConstraint toUnconstrainedWidth() {
        if (this.widthConstraintType == LengthConstraintType.NONE) {
            return this;
        }
        return new RectangleConstraint(this.width, this.widthRange, LengthConstraintType.NONE, this.height, this.heightRange, this.heightConstraintType);
    }
    
    public RectangleConstraint toUnconstrainedHeight() {
        if (this.heightConstraintType == LengthConstraintType.NONE) {
            return this;
        }
        return new RectangleConstraint(this.width, this.widthRange, this.widthConstraintType, 0.0, this.heightRange, LengthConstraintType.NONE);
    }
    
    public RectangleConstraint toFixedWidth(final double width) {
        return new RectangleConstraint(width, this.widthRange, LengthConstraintType.FIXED, this.height, this.heightRange, this.heightConstraintType);
    }
    
    public RectangleConstraint toFixedHeight(final double height) {
        return new RectangleConstraint(this.width, this.widthRange, this.widthConstraintType, height, this.heightRange, LengthConstraintType.FIXED);
    }
    
    public RectangleConstraint toRangeWidth(final Range range) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        return new RectangleConstraint(range.getUpperBound(), range, LengthConstraintType.RANGE, this.height, this.heightRange, this.heightConstraintType);
    }
    
    public RectangleConstraint toRangeHeight(final Range range) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        return new RectangleConstraint(this.width, this.widthRange, this.widthConstraintType, range.getUpperBound(), range, LengthConstraintType.RANGE);
    }
    
    public String toString() {
        return "RectangleConstraint[" + this.widthConstraintType.toString() + ": width=" + this.width + ", height=" + this.height + "]";
    }
    
    public Size2D calculateConstrainedSize(final Size2D base) {
        final Size2D result = new Size2D();
        if (this.widthConstraintType == LengthConstraintType.NONE) {
            result.width = base.width;
            if (this.heightConstraintType == LengthConstraintType.NONE) {
                result.height = base.height;
            }
            else if (this.heightConstraintType == LengthConstraintType.RANGE) {
                result.height = this.heightRange.constrain(base.height);
            }
            else if (this.heightConstraintType == LengthConstraintType.FIXED) {
                result.height = this.height;
            }
        }
        else if (this.widthConstraintType == LengthConstraintType.RANGE) {
            result.width = this.widthRange.constrain(base.width);
            if (this.heightConstraintType == LengthConstraintType.NONE) {
                result.height = base.height;
            }
            else if (this.heightConstraintType == LengthConstraintType.RANGE) {
                result.height = this.heightRange.constrain(base.height);
            }
            else if (this.heightConstraintType == LengthConstraintType.FIXED) {
                result.height = this.height;
            }
        }
        else if (this.widthConstraintType == LengthConstraintType.FIXED) {
            result.width = this.width;
            if (this.heightConstraintType == LengthConstraintType.NONE) {
                result.height = base.height;
            }
            else if (this.heightConstraintType == LengthConstraintType.RANGE) {
                result.height = this.heightRange.constrain(base.height);
            }
            else if (this.heightConstraintType == LengthConstraintType.FIXED) {
                result.height = this.height;
            }
        }
        return result;
    }
    
    static {
        NONE = new RectangleConstraint(0.0, null, LengthConstraintType.NONE, 0.0, null, LengthConstraintType.NONE);
    }
}
