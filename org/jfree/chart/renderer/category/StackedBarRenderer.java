package org.jfree.chart.renderer.category;

import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.ui.GradientPaintTransformer;
import java.awt.Paint;
import org.jfree.ui.RectangleEdge;
import java.awt.Shape;
import java.awt.GradientPaint;
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
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.ui.TextAnchor;
import org.jfree.chart.labels.ItemLabelAnchor;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class StackedBarRenderer extends BarRenderer implements Cloneable, PublicCloneable, Serializable
{
    static final long serialVersionUID = 6402943811500067531L;
    private boolean renderAsPercentages;
    
    public StackedBarRenderer() {
        this(false);
    }
    
    public StackedBarRenderer(final boolean renderAsPercentages) {
        this.renderAsPercentages = renderAsPercentages;
        final ItemLabelPosition p = new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER);
        this.setBasePositiveItemLabelPosition(p);
        this.setBaseNegativeItemLabelPosition(p);
        this.setPositiveItemLabelPositionFallback(null);
        this.setNegativeItemLabelPositionFallback(null);
    }
    
    public boolean getRenderAsPercentages() {
        return this.renderAsPercentages;
    }
    
    public void setRenderAsPercentages(final boolean asPercentages) {
        this.renderAsPercentages = asPercentages;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public int getPassCount() {
        return 2;
    }
    
    public Range findRangeBounds(final CategoryDataset dataset) {
        if (this.renderAsPercentages) {
            return new Range(0.0, 1.0);
        }
        return DatasetUtilities.findStackedRangeBounds(dataset, this.getBase());
    }
    
    protected void calculateBarWidth(final CategoryPlot plot, final Rectangle2D dataArea, final int rendererIndex, final CategoryItemRendererState state) {
        final CategoryAxis xAxis = plot.getDomainAxisForDataset(rendererIndex);
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
                categoryMargin = xAxis.getCategoryMargin();
            }
            final double used = space * (1.0 - xAxis.getLowerMargin() - xAxis.getUpperMargin() - categoryMargin);
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
        final PlotOrientation orientation = plot.getOrientation();
        final double barW0 = domainAxis.getCategoryMiddle(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;
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
        if (value >= 0.0) {
            translatedBase = rangeAxis.valueToJava2D(positiveBase, dataArea, location);
            translatedValue = rangeAxis.valueToJava2D(positiveBase + value, dataArea, location);
        }
        else {
            translatedBase = rangeAxis.valueToJava2D(negativeBase, dataArea, location);
            translatedValue = rangeAxis.valueToJava2D(negativeBase + value, dataArea, location);
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
        if (pass == 0) {
            Paint itemPaint = this.getItemPaint(row, column);
            final GradientPaintTransformer t = this.getGradientPaintTransformer();
            if (t != null && itemPaint instanceof GradientPaint) {
                itemPaint = t.transform((GradientPaint)itemPaint, (Shape)bar);
            }
            g2.setPaint(itemPaint);
            g2.fill(bar);
            if (this.isDrawBarOutline() && state.getBarWidth() > 3.0) {
                g2.setStroke(this.getItemOutlineStroke(row, column));
                g2.setPaint(this.getItemOutlinePaint(row, column));
                g2.draw(bar);
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
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StackedBarRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final StackedBarRenderer that = (StackedBarRenderer)obj;
        return this.renderAsPercentages == that.renderAsPercentages;
    }
}
