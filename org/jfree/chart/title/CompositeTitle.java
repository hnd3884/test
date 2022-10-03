package org.jfree.chart.title;

import java.awt.geom.Rectangle2D;
import org.jfree.ui.Size2D;
import org.jfree.chart.block.RectangleConstraint;
import java.awt.Graphics2D;
import org.jfree.chart.block.Arrangement;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.BlockContainer;
import java.io.Serializable;

public class CompositeTitle extends Title implements Cloneable, Serializable
{
    private static final long serialVersionUID = -6770854036232562290L;
    private BlockContainer container;
    
    public CompositeTitle() {
        this(new BlockContainer(new BorderArrangement()));
    }
    
    public CompositeTitle(final BlockContainer container) {
        if (container == null) {
            throw new IllegalArgumentException("Null 'container' argument.");
        }
        this.container = container;
    }
    
    public BlockContainer getContainer() {
        return this.container;
    }
    
    public void setTitleContainer(final BlockContainer container) {
        if (container == null) {
            throw new IllegalArgumentException("Null 'container' argument.");
        }
        this.container = container;
    }
    
    public Size2D arrange(final Graphics2D g2, final RectangleConstraint constraint) {
        final RectangleConstraint contentConstraint = this.toContentConstraint(constraint);
        final Size2D contentSize = this.container.arrange(g2, contentConstraint);
        return new Size2D(this.calculateTotalWidth(contentSize.getWidth()), this.calculateTotalHeight(contentSize.getHeight()));
    }
    
    public void draw(final Graphics2D g2, Rectangle2D area) {
        area = this.trimMargin(area);
        this.drawBorder(g2, area);
        area = this.trimBorder(area);
        area = this.trimPadding(area);
        this.container.draw(g2, area);
    }
    
    public Object draw(final Graphics2D g2, final Rectangle2D area, final Object params) {
        this.draw(g2, area);
        return null;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CompositeTitle)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final CompositeTitle that = (CompositeTitle)obj;
        return this.container.equals(that.container);
    }
}
