package org.jfree.chart.block;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.awt.Shape;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.data.Range;
import org.jfree.ui.Size2D;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.jfree.ui.RectangleInsets;
import java.io.Serializable;

public class AbstractBlock implements Serializable
{
    private static final long serialVersionUID = 7689852412141274563L;
    private String id;
    private RectangleInsets margin;
    private BlockBorder border;
    private RectangleInsets padding;
    private double width;
    private double height;
    private transient Rectangle2D bounds;
    
    protected AbstractBlock() {
        this.id = null;
        this.width = 0.0;
        this.height = 0.0;
        this.bounds = new Rectangle2D.Float();
        this.margin = RectangleInsets.ZERO_INSETS;
        this.border = BlockBorder.NONE;
        this.padding = RectangleInsets.ZERO_INSETS;
    }
    
    public String getID() {
        return this.id;
    }
    
    public void setID(final String id) {
        this.id = id;
    }
    
    public double getWidth() {
        return this.width;
    }
    
    public void setWidth(final double width) {
        this.width = width;
    }
    
    public double getHeight() {
        return this.height;
    }
    
    public void setHeight(final double height) {
        this.height = height;
    }
    
    public RectangleInsets getMargin() {
        return this.margin;
    }
    
    public void setMargin(final RectangleInsets margin) {
        if (margin == null) {
            throw new IllegalArgumentException("Null 'margin' argument.");
        }
        this.margin = margin;
    }
    
    public void setMargin(final double top, final double left, final double bottom, final double right) {
        this.setMargin(new RectangleInsets(top, left, bottom, right));
    }
    
    public BlockBorder getBorder() {
        return this.border;
    }
    
    public void setBorder(final BlockBorder border) {
        if (border == null) {
            throw new IllegalArgumentException("Null 'border' argument.");
        }
        this.border = border;
    }
    
    public void setBorder(final double top, final double left, final double bottom, final double right) {
        this.setBorder(new BlockBorder(top, left, bottom, right));
    }
    
    public RectangleInsets getPadding() {
        return this.padding;
    }
    
    public void setPadding(final RectangleInsets padding) {
        if (padding == null) {
            throw new IllegalArgumentException("Null 'padding' argument.");
        }
        this.padding = padding;
    }
    
    public double getContentXOffset() {
        return this.margin.getLeft() + this.border.getInsets().getLeft() + this.padding.getLeft();
    }
    
    public double getContentYOffset() {
        return this.margin.getTop() + this.border.getInsets().getTop() + this.padding.getTop();
    }
    
    public void setPadding(final double top, final double left, final double bottom, final double right) {
        this.setPadding(new RectangleInsets(top, left, bottom, right));
    }
    
    public Size2D arrange(final Graphics2D g2) {
        return this.arrange(g2, RectangleConstraint.NONE);
    }
    
    public Size2D arrange(final Graphics2D g2, final RectangleConstraint constraint) {
        final Size2D base = new Size2D(this.getWidth(), this.getHeight());
        return constraint.calculateConstrainedSize(base);
    }
    
    public Rectangle2D getBounds() {
        return this.bounds;
    }
    
    public void setBounds(final Rectangle2D bounds) {
        if (bounds == null) {
            throw new IllegalArgumentException("Null 'bounds' argument.");
        }
        this.bounds = bounds;
    }
    
    protected double trimToContentWidth(final double fixedWidth) {
        double result = this.margin.trimWidth(fixedWidth);
        result = this.border.getInsets().trimWidth(result);
        result = this.padding.trimWidth(result);
        return Math.max(result, 0.0);
    }
    
    protected double trimToContentHeight(final double fixedHeight) {
        double result = this.margin.trimHeight(fixedHeight);
        result = this.border.getInsets().trimHeight(result);
        result = this.padding.trimHeight(result);
        return Math.max(result, 0.0);
    }
    
    protected RectangleConstraint toContentConstraint(final RectangleConstraint c) {
        if (c == null) {
            throw new IllegalArgumentException("Null 'c' argument.");
        }
        if (c.equals(RectangleConstraint.NONE)) {
            return c;
        }
        final double w = c.getWidth();
        final Range wr = c.getWidthRange();
        final double h = c.getHeight();
        final Range hr = c.getHeightRange();
        final double ww = this.trimToContentWidth(w);
        final double hh = this.trimToContentHeight(h);
        final Range wwr = this.trimToContentWidth(wr);
        final Range hhr = this.trimToContentHeight(hr);
        return new RectangleConstraint(ww, wwr, c.getWidthConstraintType(), hh, hhr, c.getHeightConstraintType());
    }
    
    private Range trimToContentWidth(final Range r) {
        if (r == null) {
            return null;
        }
        double lowerBound = 0.0;
        double upperBound = Double.POSITIVE_INFINITY;
        if (r.getLowerBound() > 0.0) {
            lowerBound = this.trimToContentWidth(r.getLowerBound());
        }
        if (r.getUpperBound() < Double.POSITIVE_INFINITY) {
            upperBound = this.trimToContentWidth(r.getUpperBound());
        }
        return new Range(lowerBound, upperBound);
    }
    
    private Range trimToContentHeight(final Range r) {
        if (r == null) {
            return null;
        }
        double lowerBound = 0.0;
        double upperBound = Double.POSITIVE_INFINITY;
        if (r.getLowerBound() > 0.0) {
            lowerBound = this.trimToContentHeight(r.getLowerBound());
        }
        if (r.getUpperBound() < Double.POSITIVE_INFINITY) {
            upperBound = this.trimToContentHeight(r.getUpperBound());
        }
        return new Range(lowerBound, upperBound);
    }
    
    protected double calculateTotalWidth(final double contentWidth) {
        double result = contentWidth;
        result = this.padding.extendWidth(result);
        result = this.border.getInsets().extendWidth(result);
        result = this.margin.extendWidth(result);
        return result;
    }
    
    protected double calculateTotalHeight(final double contentHeight) {
        double result = contentHeight;
        result = this.padding.extendHeight(result);
        result = this.border.getInsets().extendHeight(result);
        result = this.margin.extendHeight(result);
        return result;
    }
    
    protected Rectangle2D trimMargin(final Rectangle2D area) {
        this.margin.trim(area);
        return area;
    }
    
    protected Rectangle2D trimBorder(final Rectangle2D area) {
        this.border.getInsets().trim(area);
        return area;
    }
    
    protected Rectangle2D trimPadding(final Rectangle2D area) {
        this.padding.trim(area);
        return area;
    }
    
    protected void drawBorder(final Graphics2D g2, final Rectangle2D area) {
        this.border.draw(g2, area);
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractBlock)) {
            return false;
        }
        final AbstractBlock that = (AbstractBlock)obj;
        return this.border.equals(that.border) && this.bounds.equals(that.bounds) && this.margin.equals((Object)that.margin) && this.padding.equals((Object)that.padding) && this.height == that.height && this.width == that.width;
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape((Shape)this.bounds, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.bounds = (Rectangle2D)SerialUtilities.readShape(stream);
    }
}
