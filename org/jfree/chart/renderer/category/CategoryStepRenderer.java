package org.jfree.chart.renderer.category;

import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import org.jfree.chart.plot.PlotOrientation;
import java.awt.Graphics2D;
import org.jfree.chart.event.RendererChangeEvent;
import java.awt.geom.Line2D;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class CategoryStepRenderer extends AbstractCategoryItemRenderer implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -5121079703118261470L;
    public static final int STAGGER_WIDTH = 5;
    private boolean stagger;
    private transient Line2D line;
    
    public CategoryStepRenderer() {
        this(false);
    }
    
    public CategoryStepRenderer(final boolean stagger) {
        this.stagger = false;
        this.line = new Line2D.Double(0.0, 0.0, 0.0, 0.0);
        this.stagger = stagger;
    }
    
    public boolean getStagger() {
        return this.stagger;
    }
    
    public void setStagger(final boolean shouldStagger) {
        this.stagger = shouldStagger;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    protected void drawLine(final Graphics2D g2, final PlotOrientation orientation, final double x0, final double y0, final double x1, final double y1) {
        if (orientation == PlotOrientation.VERTICAL) {
            this.line.setLine(x0, y0, x1, y1);
            g2.draw(this.line);
        }
        else if (orientation == PlotOrientation.HORIZONTAL) {
            this.line.setLine(y0, x0, y1, x1);
            g2.draw(this.line);
        }
    }
    
    public void drawItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryDataset dataset, final int row, final int column, final int pass) {
        final Number value = dataset.getValue(row, column);
        if (value == null) {
            return;
        }
        final PlotOrientation orientation = plot.getOrientation();
        double x1s = domainAxis.getCategoryStart(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        final double x1 = domainAxis.getCategoryMiddle(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        final double x1e = 2.0 * x1 - x1s;
        final double y1 = rangeAxis.valueToJava2D(value.doubleValue(), dataArea, plot.getRangeAxisEdge());
        g2.setPaint(this.getItemPaint(row, column));
        g2.setStroke(this.getItemStroke(row, column));
        if (column != 0) {
            final Number previousValue = dataset.getValue(row, column - 1);
            if (previousValue != null) {
                final double previous = previousValue.doubleValue();
                final double x0s = domainAxis.getCategoryStart(column - 1, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
                final double x2 = domainAxis.getCategoryMiddle(column - 1, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
                final double x0e = 2.0 * x2 - x0s;
                final double y2 = rangeAxis.valueToJava2D(previous, dataArea, plot.getRangeAxisEdge());
                if (this.getStagger()) {
                    int xStagger = row * 5;
                    if (xStagger > x1s - x0e) {
                        xStagger = (int)(x1s - x0e);
                    }
                    x1s = x0e + xStagger;
                }
                this.drawLine(g2, orientation, x0e, y2, x1s, y2);
                this.drawLine(g2, orientation, x1s, y2, x1s, y1);
            }
        }
        this.drawLine(g2, orientation, x1s, y1, x1e, y1);
        if (this.isItemLabelVisible(row, column)) {
            this.drawItemLabel(g2, orientation, dataset, row, column, x1, y1, value.doubleValue() < 0.0);
        }
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryStepRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final CategoryStepRenderer that = (CategoryStepRenderer)obj;
        return this.stagger == that.stagger;
    }
}
