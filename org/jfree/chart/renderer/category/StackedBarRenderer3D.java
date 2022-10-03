package org.jfree.chart.renderer.category;

import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.ui.RectangleEdge;
import java.awt.Paint;
import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.Shape;
import org.jfree.data.Values2D;
import org.jfree.data.DataUtilities;
import org.jfree.chart.axis.ValueAxis;
import java.awt.Graphics2D;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.PlotOrientation;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.event.RendererChangeEvent;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class StackedBarRenderer3D extends BarRenderer3D implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -5832945916493247123L;
    private boolean renderAsPercentages;
    
    public StackedBarRenderer3D() {
        this(false);
    }
    
    public StackedBarRenderer3D(final double xOffset, final double yOffset) {
        super(xOffset, yOffset);
    }
    
    public StackedBarRenderer3D(final boolean renderAsPercentages) {
        this.renderAsPercentages = renderAsPercentages;
    }
    
    public StackedBarRenderer3D(final double xOffset, final double yOffset, final boolean renderAsPercentages) {
        super(xOffset, yOffset);
        this.renderAsPercentages = renderAsPercentages;
    }
    
    public boolean getRenderAsPercentages() {
        return this.renderAsPercentages;
    }
    
    public void setRenderAsPercentages(final boolean asPercentages) {
        this.renderAsPercentages = asPercentages;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public Range findRangeBounds(final CategoryDataset dataset) {
        if (this.renderAsPercentages) {
            return new Range(0.0, 1.0);
        }
        return DatasetUtilities.findStackedRangeBounds(dataset);
    }
    
    protected void calculateBarWidth(final CategoryPlot plot, final Rectangle2D dataArea, final int rendererIndex, final CategoryItemRendererState state) {
        final CategoryAxis domainAxis = this.getDomainAxis(plot, rendererIndex);
        final CategoryDataset data = plot.getDataset(rendererIndex);
        if (data != null) {
            final PlotOrientation orientation = plot.getOrientation();
            double space = 0.0;
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            final double maxWidth = space * this.getMaximumBarWidth();
            final int columns = data.getColumnCount();
            double categoryMargin = 0.0;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }
            final double used = space * (1.0 - domainAxis.getLowerMargin() - domainAxis.getUpperMargin() - categoryMargin);
            if (columns > 0) {
                state.setBarWidth(Math.min(used / columns, maxWidth));
            }
            else {
                state.setBarWidth(Math.min(used, maxWidth));
            }
        }
    }
    
    public void drawItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryDataset dataset, final int row, final int column, final int pass) {
        final Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }
        double value = dataValue.doubleValue();
        double total = 0.0;
        if (this.renderAsPercentages) {
            total = DataUtilities.calculateColumnTotal(dataset, column);
            value /= total;
        }
        final Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(), dataArea.getY() + this.getYOffset(), dataArea.getWidth() - this.getXOffset(), dataArea.getHeight() - this.getYOffset());
        final PlotOrientation orientation = plot.getOrientation();
        final double barW0 = domainAxis.getCategoryMiddle(column, this.getColumnCount(), adjusted, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;
        double negativeBase;
        double positiveBase = negativeBase = this.getBase();
        for (int i = 0; i < row; ++i) {
            final Number v = dataset.getValue(i, column);
            if (v != null) {
                double d = v.doubleValue();
                if (this.renderAsPercentages) {
                    d /= total;
                }
                if (d > 0.0) {
                    positiveBase += d;
                }
                else {
                    negativeBase += d;
                }
            }
        }
        final RectangleEdge location = plot.getRangeAxisEdge();
        double translatedBase;
        double translatedValue;
        if (value > 0.0) {
            translatedBase = rangeAxis.valueToJava2D(positiveBase, adjusted, location);
            translatedValue = rangeAxis.valueToJava2D(positiveBase + value, adjusted, location);
        }
        else {
            translatedBase = rangeAxis.valueToJava2D(negativeBase, adjusted, location);
            translatedValue = rangeAxis.valueToJava2D(negativeBase + value, adjusted, location);
        }
        final double barL0 = Math.min(translatedBase, translatedValue);
        final double barLength = Math.max(Math.abs(translatedValue - translatedBase), this.getMinimumBarLength());
        Rectangle2D bar = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(barL0, barW0, barLength, state.getBarWidth());
        }
        else {
            bar = new Rectangle2D.Double(barW0, barL0, state.getBarWidth(), barLength);
        }
        final Paint itemPaint = this.getItemPaint(row, column);
        g2.setPaint(itemPaint);
        g2.fill(bar);
        if (pass == 0) {
            final double x0 = bar.getMinX();
            final double x2 = x0 + this.getXOffset();
            final double x3 = bar.getMaxX();
            final double x4 = x3 + this.getXOffset();
            final double y0 = bar.getMinY() - this.getYOffset();
            final double y2 = bar.getMinY();
            final double y3 = bar.getMaxY() - this.getYOffset();
            final double y4 = bar.getMaxY();
            GeneralPath bar3dRight = null;
            GeneralPath bar3dTop = null;
            if (value > 0.0 || orientation == PlotOrientation.VERTICAL) {
                bar3dRight = new GeneralPath();
                bar3dRight.moveTo((float)x3, (float)y4);
                bar3dRight.lineTo((float)x3, (float)y2);
                bar3dRight.lineTo((float)x4, (float)y0);
                bar3dRight.lineTo((float)x4, (float)y3);
                bar3dRight.closePath();
                if (itemPaint instanceof Color) {
                    g2.setPaint(((Color)itemPaint).darker());
                }
                g2.fill(bar3dRight);
            }
            if (value > 0.0 || orientation == PlotOrientation.HORIZONTAL) {
                bar3dTop = new GeneralPath();
                bar3dTop.moveTo((float)x0, (float)y2);
                bar3dTop.lineTo((float)x2, (float)y0);
                bar3dTop.lineTo((float)x4, (float)y0);
                bar3dTop.lineTo((float)x3, (float)y2);
                bar3dTop.closePath();
                g2.fill(bar3dTop);
            }
            if (this.isDrawBarOutline() && state.getBarWidth() > 3.0) {
                g2.setStroke(this.getItemOutlineStroke(row, column));
                g2.setPaint(this.getItemOutlinePaint(row, column));
                g2.draw(bar);
                if (bar3dRight != null) {
                    g2.draw(bar3dRight);
                }
                if (bar3dTop != null) {
                    g2.draw(bar3dTop);
                }
            }
            final EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                this.addItemEntity(entities, dataset, row, column, bar);
            }
        }
        else if (pass == 1) {
            final CategoryItemLabelGenerator generator = this.getItemLabelGenerator(row, column);
            if (generator != null && this.isItemLabelVisible(row, column)) {
                this.drawItemLabel(g2, dataset, row, column, plot, generator, bar, value < 0.0);
            }
        }
    }
    
    public int getPassCount() {
        return 2;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StackedBarRenderer3D)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final StackedBarRenderer3D that = (StackedBarRenderer3D)obj;
        return this.renderAsPercentages == that.renderAsPercentages;
    }
}
