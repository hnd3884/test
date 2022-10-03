package org.jfree.ui;

import java.awt.geom.Rectangle2D;
import org.jfree.util.UnitType;
import java.io.Serializable;

public class RectangleInsets implements Serializable
{
    private static final long serialVersionUID = 1902273207559319996L;
    public static final RectangleInsets ZERO_INSETS;
    private UnitType unitType;
    private double top;
    private double left;
    private double bottom;
    private double right;
    
    static {
        ZERO_INSETS = new RectangleInsets(UnitType.ABSOLUTE, 0.0, 0.0, 0.0, 0.0);
    }
    
    public RectangleInsets(final double top, final double left, final double bottom, final double right) {
        this(UnitType.ABSOLUTE, top, left, bottom, right);
    }
    
    public RectangleInsets(final UnitType unitType, final double top, final double left, final double bottom, final double right) {
        if (unitType == null) {
            throw new IllegalArgumentException("Null 'unitType' argument.");
        }
        this.unitType = unitType;
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }
    
    public double calculateBottomInset(final double height) {
        double result = this.bottom;
        if (this.unitType == UnitType.RELATIVE) {
            result = this.bottom * height;
        }
        return result;
    }
    
    public double calculateBottomOutset(final double height) {
        double result = this.bottom;
        if (this.unitType == UnitType.RELATIVE) {
            result = height / (1.0 - this.top - this.bottom) * this.bottom;
        }
        return result;
    }
    
    public double calculateLeftInset(final double width) {
        double result = this.left;
        if (this.unitType == UnitType.RELATIVE) {
            result = this.left * width;
        }
        return result;
    }
    
    public double calculateLeftOutset(final double width) {
        double result = this.left;
        if (this.unitType == UnitType.RELATIVE) {
            result = width / (1.0 - this.left - this.right) * this.left;
        }
        return result;
    }
    
    public double calculateRightInset(final double width) {
        double result = this.right;
        if (this.unitType == UnitType.RELATIVE) {
            result = this.right * width;
        }
        return result;
    }
    
    public double calculateRightOutset(final double width) {
        double result = this.right;
        if (this.unitType == UnitType.RELATIVE) {
            result = width / (1.0 - this.left - this.right) * this.right;
        }
        return result;
    }
    
    public double calculateTopInset(final double height) {
        double result = this.top;
        if (this.unitType == UnitType.RELATIVE) {
            result = this.top * height;
        }
        return result;
    }
    
    public double calculateTopOutset(final double height) {
        double result = this.top;
        if (this.unitType == UnitType.RELATIVE) {
            result = height / (1.0 - this.top - this.bottom) * this.top;
        }
        return result;
    }
    
    public Rectangle2D createAdjustedRectangle(final Rectangle2D base, final LengthAdjustmentType horizontal, final LengthAdjustmentType vertical) {
        if (base == null) {
            throw new IllegalArgumentException("Null 'base' argument.");
        }
        double x = base.getX();
        double y = base.getY();
        double w = base.getWidth();
        double h = base.getHeight();
        if (horizontal == LengthAdjustmentType.EXPAND) {
            final double leftOutset = this.calculateLeftOutset(w);
            x -= leftOutset;
            w = w + leftOutset + this.calculateRightOutset(w);
        }
        else if (horizontal == LengthAdjustmentType.CONTRACT) {
            final double leftMargin = this.calculateLeftInset(w);
            x += leftMargin;
            w = w - leftMargin - this.calculateRightInset(w);
        }
        if (vertical == LengthAdjustmentType.EXPAND) {
            final double topMargin = this.calculateTopOutset(h);
            y -= topMargin;
            h = h + topMargin + this.calculateBottomOutset(h);
        }
        else if (vertical == LengthAdjustmentType.CONTRACT) {
            final double topMargin = this.calculateTopInset(h);
            y += topMargin;
            h = h - topMargin - this.calculateBottomInset(h);
        }
        return new Rectangle2D.Double(x, y, w, h);
    }
    
    public Rectangle2D createInsetRectangle(final Rectangle2D base) {
        return this.createInsetRectangle(base, true, true);
    }
    
    public Rectangle2D createInsetRectangle(final Rectangle2D base, final boolean horizontal, final boolean vertical) {
        if (base == null) {
            throw new IllegalArgumentException("Null 'base' argument.");
        }
        double topMargin = 0.0;
        double bottomMargin = 0.0;
        if (vertical) {
            topMargin = this.calculateTopInset(base.getHeight());
            bottomMargin = this.calculateBottomInset(base.getHeight());
        }
        double leftMargin = 0.0;
        double rightMargin = 0.0;
        if (horizontal) {
            leftMargin = this.calculateLeftInset(base.getWidth());
            rightMargin = this.calculateRightInset(base.getWidth());
        }
        return new Rectangle2D.Double(base.getX() + leftMargin, base.getY() + topMargin, base.getWidth() - leftMargin - rightMargin, base.getHeight() - topMargin - bottomMargin);
    }
    
    public Rectangle2D createOutsetRectangle(final Rectangle2D base) {
        return this.createOutsetRectangle(base, true, true);
    }
    
    public Rectangle2D createOutsetRectangle(final Rectangle2D base, final boolean horizontal, final boolean vertical) {
        if (base == null) {
            throw new IllegalArgumentException("Null 'base' argument.");
        }
        double topMargin = 0.0;
        double bottomMargin = 0.0;
        if (vertical) {
            topMargin = this.calculateTopOutset(base.getHeight());
            bottomMargin = this.calculateBottomOutset(base.getHeight());
        }
        double leftMargin = 0.0;
        double rightMargin = 0.0;
        if (horizontal) {
            leftMargin = this.calculateLeftOutset(base.getWidth());
            rightMargin = this.calculateRightOutset(base.getWidth());
        }
        return new Rectangle2D.Double(base.getX() - leftMargin, base.getY() - topMargin, base.getWidth() + leftMargin + rightMargin, base.getHeight() + topMargin + bottomMargin);
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RectangleInsets)) {
            return false;
        }
        final RectangleInsets that = (RectangleInsets)obj;
        return that.unitType == this.unitType && this.left == that.left && this.right == that.right && this.top == that.top && this.bottom == that.bottom;
    }
    
    public double extendHeight(final double height) {
        return height + this.calculateTopOutset(height) + this.calculateBottomOutset(height);
    }
    
    public double extendWidth(final double width) {
        return width + this.calculateLeftOutset(width) + this.calculateRightOutset(width);
    }
    
    public double getBottom() {
        return this.bottom;
    }
    
    public double getLeft() {
        return this.left;
    }
    
    public double getRight() {
        return this.right;
    }
    
    public double getTop() {
        return this.top;
    }
    
    public UnitType getUnitType() {
        return this.unitType;
    }
    
    public int hashCode() {
        int result = (this.unitType != null) ? this.unitType.hashCode() : 0;
        long temp = (this.top != 0.0) ? Double.doubleToLongBits(this.top) : 0L;
        result = 29 * result + (int)(temp ^ temp >>> 32);
        temp = ((this.bottom != 0.0) ? Double.doubleToLongBits(this.bottom) : 0L);
        result = 29 * result + (int)(temp ^ temp >>> 32);
        temp = ((this.left != 0.0) ? Double.doubleToLongBits(this.left) : 0L);
        result = 29 * result + (int)(temp ^ temp >>> 32);
        temp = ((this.right != 0.0) ? Double.doubleToLongBits(this.right) : 0L);
        result = 29 * result + (int)(temp ^ temp >>> 32);
        return result;
    }
    
    public String toString() {
        return "RectangleInsets[t=" + this.top + ",l=" + this.left + ",b=" + this.bottom + ",r=" + this.right + "]";
    }
    
    public void trim(final Rectangle2D area) {
        final double w = area.getWidth();
        final double h = area.getHeight();
        final double l = this.calculateLeftInset(w);
        final double r = this.calculateRightInset(w);
        final double t = this.calculateTopInset(h);
        final double b = this.calculateBottomInset(h);
        area.setRect(area.getX() + l, area.getY() + t, w - l - r, h - t - b);
    }
    
    public double trimHeight(final double height) {
        return height - this.calculateTopInset(height) - this.calculateBottomInset(height);
    }
    
    public double trimWidth(final double width) {
        return width - this.calculateLeftInset(width) - this.calculateRightInset(width);
    }
}
