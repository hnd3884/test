package org.jfree.chart.block;

import java.util.List;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import org.jfree.ui.Size2D;
import java.awt.Graphics2D;
import org.jfree.ui.VerticalAlignment;
import org.jfree.ui.HorizontalAlignment;
import java.io.Serializable;

public class ColumnArrangement implements Arrangement, Serializable
{
    private static final long serialVersionUID = -5315388482898581555L;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;
    private double horizontalGap;
    private double verticalGap;
    
    public ColumnArrangement() {
    }
    
    public ColumnArrangement(final HorizontalAlignment hAlign, final VerticalAlignment vAlign, final double hGap, final double vGap) {
        this.horizontalAlignment = hAlign;
        this.verticalAlignment = vAlign;
        this.horizontalGap = hGap;
        this.verticalGap = vGap;
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
                throw new RuntimeException("Not implemented.");
            }
            if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not implemented.");
            }
        }
        else if (w == LengthConstraintType.FIXED) {
            if (h == LengthConstraintType.NONE) {
                throw new RuntimeException("Not implemented.");
            }
            if (h == LengthConstraintType.FIXED) {
                return this.arrangeFF(container, g2, constraint);
            }
            if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not implemented.");
            }
        }
        else if (w == LengthConstraintType.RANGE) {
            if (h == LengthConstraintType.NONE) {
                throw new RuntimeException("Not implemented.");
            }
            if (h == LengthConstraintType.FIXED) {
                return this.arrangeRF(container, g2, constraint);
            }
            if (h == LengthConstraintType.RANGE) {
                return this.arrangeRR(container, g2, constraint);
            }
        }
        return new Size2D();
    }
    
    protected Size2D arrangeFF(final BlockContainer container, final Graphics2D g2, final RectangleConstraint constraint) {
        return this.arrangeNF(container, g2, constraint);
    }
    
    protected Size2D arrangeNF(final BlockContainer container, final Graphics2D g2, final RectangleConstraint constraint) {
        final List blocks = container.getBlocks();
        double height = constraint.getHeight();
        if (height <= 0.0) {
            height = Double.POSITIVE_INFINITY;
        }
        double x = 0.0;
        double y = 0.0;
        double maxWidth = 0.0;
        final List itemsInColumn = new ArrayList();
        for (int i = 0; i < blocks.size(); ++i) {
            final Block block = blocks.get(i);
            final Size2D size = block.arrange(g2, RectangleConstraint.NONE);
            if (y + size.height <= height) {
                itemsInColumn.add(block);
                block.setBounds(new Rectangle2D.Double(x, y, size.width, size.height));
                y = y + size.height + this.verticalGap;
                maxWidth = Math.max(maxWidth, size.width);
            }
            else if (itemsInColumn.isEmpty()) {
                block.setBounds(new Rectangle2D.Double(x, y, size.width, Math.min(size.height, height - y)));
                y = 0.0;
                x = x + size.width + this.horizontalGap;
            }
            else {
                itemsInColumn.clear();
                x = x + maxWidth + this.horizontalGap;
                y = 0.0;
                maxWidth = size.width;
                block.setBounds(new Rectangle2D.Double(x, y, size.width, Math.min(size.height, height)));
                y = size.height + this.verticalGap;
                itemsInColumn.add(block);
            }
        }
        return new Size2D(x + maxWidth, constraint.getHeight());
    }
    
    protected Size2D arrangeRR(final BlockContainer container, final Graphics2D g2, final RectangleConstraint constraint) {
        final Size2D s1 = this.arrangeNN(container, g2);
        if (constraint.getHeightRange().contains(s1.height)) {
            return s1;
        }
        final RectangleConstraint c = constraint.toFixedHeight(constraint.getHeightRange().getUpperBound());
        return this.arrangeRF(container, g2, c);
    }
    
    protected Size2D arrangeRF(final BlockContainer container, final Graphics2D g2, final RectangleConstraint constraint) {
        final Size2D s = this.arrangeNF(container, g2, constraint);
        if (constraint.getWidthRange().contains(s.width)) {
            return s;
        }
        final RectangleConstraint c = constraint.toFixedWidth(constraint.getWidthRange().constrain(s.getWidth()));
        return this.arrangeFF(container, g2, c);
    }
    
    protected Size2D arrangeNN(final BlockContainer container, final Graphics2D g2) {
        double y = 0.0;
        double height = 0.0;
        double maxWidth = 0.0;
        final List blocks = container.getBlocks();
        final int blockCount = blocks.size();
        if (blockCount > 0) {
            final Size2D[] sizes = new Size2D[blocks.size()];
            for (int i = 0; i < blocks.size(); ++i) {
                final Block block = blocks.get(i);
                sizes[i] = block.arrange(g2, RectangleConstraint.NONE);
                height += sizes[i].getHeight();
                maxWidth = Math.max(sizes[i].width, maxWidth);
                block.setBounds(new Rectangle2D.Double(0.0, y, sizes[i].width, sizes[i].height));
                y = y + sizes[i].height + this.verticalGap;
            }
            if (blockCount > 1) {
                height += this.verticalGap * (blockCount - 1);
            }
            if (this.horizontalAlignment != HorizontalAlignment.LEFT) {
                for (int i = 0; i < blocks.size(); ++i) {
                    if (this.horizontalAlignment != HorizontalAlignment.CENTER) {
                        if (this.horizontalAlignment == HorizontalAlignment.RIGHT) {}
                    }
                }
            }
        }
        return new Size2D(maxWidth, height);
    }
    
    public void clear() {
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ColumnArrangement)) {
            return false;
        }
        final ColumnArrangement that = (ColumnArrangement)obj;
        return this.horizontalAlignment == that.horizontalAlignment && this.verticalAlignment == that.verticalAlignment && this.horizontalGap == that.horizontalGap && this.verticalGap == that.verticalGap;
    }
}
