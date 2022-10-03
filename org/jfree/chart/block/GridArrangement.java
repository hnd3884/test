package org.jfree.chart.block;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import org.jfree.ui.Size2D;
import java.awt.Graphics2D;
import java.io.Serializable;

public class GridArrangement implements Arrangement, Serializable
{
    private static final long serialVersionUID = -2563758090144655938L;
    private int rows;
    private int columns;
    
    public GridArrangement(final int rows, final int columns) {
        this.rows = rows;
        this.columns = columns;
    }
    
    public void add(final Block block, final Object key) {
    }
    
    public Size2D arrange(final BlockContainer container, final Graphics2D g2, final RectangleConstraint constraint) {
        final LengthConstraintType w = constraint.getWidthConstraintType();
        final LengthConstraintType h = constraint.getHeightConstraintType();
        if (w == LengthConstraintType.NONE) {
            if (h == LengthConstraintType.NONE) {
                return this.arrangeNN(container, g2);
            }
            if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
            if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not yet implemented.");
            }
        }
        else if (w == LengthConstraintType.FIXED) {
            if (h == LengthConstraintType.NONE) {
                return this.arrangeFN(container, g2, constraint);
            }
            if (h == LengthConstraintType.FIXED) {
                return this.arrangeFF(container, g2, constraint);
            }
            if (h == LengthConstraintType.RANGE) {
                return this.arrangeFR(container, g2, constraint);
            }
        }
        else if (w == LengthConstraintType.RANGE) {
            if (h == LengthConstraintType.NONE) {
                throw new RuntimeException("Not yet implemented.");
            }
            if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
            if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not yet implemented.");
            }
        }
        return new Size2D();
    }
    
    protected Size2D arrangeNN(final BlockContainer container, final Graphics2D g2) {
        double maxW = 0.0;
        double maxH = 0.0;
        final List blocks = container.getBlocks();
        final Iterator iterator = blocks.iterator();
        while (iterator.hasNext()) {
            final Block b = iterator.next();
            final Size2D s = b.arrange(g2, RectangleConstraint.NONE);
            maxW = Math.max(maxW, s.width);
            maxH = Math.max(maxH, s.height);
        }
        final double width = this.columns * maxW;
        final double height = this.rows * maxH;
        final RectangleConstraint c = new RectangleConstraint(width, height);
        return this.arrangeFF(container, g2, c);
    }
    
    protected Size2D arrangeFF(final BlockContainer container, final Graphics2D g2, final RectangleConstraint constraint) {
        final double width = constraint.getWidth() / this.columns;
        final double height = constraint.getHeight() / this.rows;
        final List blocks = container.getBlocks();
        for (int c = 0; c < this.columns; ++c) {
            for (int r = 0; r < this.rows; ++r) {
                final int index = r * this.columns + c;
                if (index == blocks.size()) {
                    break;
                }
                final Block b = blocks.get(index);
                b.setBounds(new Rectangle2D.Double(c * width, r * height, width, height));
            }
        }
        return new Size2D(this.columns * width, this.rows * height);
    }
    
    protected Size2D arrangeFR(final BlockContainer container, final Graphics2D g2, final RectangleConstraint constraint) {
        final RectangleConstraint c1 = constraint.toUnconstrainedHeight();
        final Size2D size1 = this.arrange(container, g2, c1);
        if (constraint.getHeightRange().contains(size1.getHeight())) {
            return size1;
        }
        final double h = constraint.getHeightRange().constrain(size1.getHeight());
        final RectangleConstraint c2 = constraint.toFixedHeight(h);
        return this.arrange(container, g2, c2);
    }
    
    protected Size2D arrangeFN(final BlockContainer container, final Graphics2D g2, final RectangleConstraint constraint) {
        final double width = constraint.getWidth() / this.columns;
        final RectangleConstraint constraint2 = constraint.toFixedWidth(width);
        final List blocks = container.getBlocks();
        double maxH = 0.0;
        for (int r = 0; r < this.rows; ++r) {
            for (int c = 0; c < this.columns; ++c) {
                final int index = r * this.columns + c;
                if (index == blocks.size()) {
                    break;
                }
                final Block b = blocks.get(index);
                final Size2D s = b.arrange(g2, constraint2);
                maxH = Math.max(maxH, s.getHeight());
            }
        }
        final RectangleConstraint constraint3 = constraint.toFixedHeight(maxH * this.rows);
        return this.arrange(container, g2, constraint3);
    }
    
    public void clear() {
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GridArrangement)) {
            return false;
        }
        final GridArrangement that = (GridArrangement)obj;
        return this.columns == that.columns && this.rows == that.rows;
    }
}
