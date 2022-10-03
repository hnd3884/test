package org.jfree.chart.renderer.category;

import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import java.awt.Paint;
import java.awt.Stroke;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.CategoryItemEntity;
import java.awt.Shape;
import java.awt.geom.Line2D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.CategoryPlot;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.event.RendererChangeEvent;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class LevelRenderer extends AbstractCategoryItemRenderer implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -8204856624355025117L;
    public static final double DEFAULT_ITEM_MARGIN = 0.2;
    private double itemMargin;
    private double maxItemWidth;
    
    public LevelRenderer() {
        this.itemMargin = 0.2;
        this.maxItemWidth = 1.0;
    }
    
    public double getItemMargin() {
        return this.itemMargin;
    }
    
    public void setItemMargin(final double percent) {
        this.itemMargin = percent;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public double getMaxItemWidth() {
        return this.maxItemWidth;
    }
    
    public void setMaxItemWidth(final double percent) {
        this.maxItemWidth = percent;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public double getMaximumItemWidth() {
        return this.getMaxItemWidth();
    }
    
    public void setMaximumItemWidth(final double percent) {
        this.setMaxItemWidth(percent);
    }
    
    public CategoryItemRendererState initialise(final Graphics2D g2, final Rectangle2D dataArea, final CategoryPlot plot, final int rendererIndex, final PlotRenderingInfo info) {
        final CategoryItemRendererState state = super.initialise(g2, dataArea, plot, rendererIndex, info);
        this.calculateItemWidth(plot, dataArea, rendererIndex, state);
        return state;
    }
    
    protected void calculateItemWidth(final CategoryPlot plot, final Rectangle2D dataArea, final int rendererIndex, final CategoryItemRendererState state) {
        final CategoryAxis domainAxis = this.getDomainAxis(plot, rendererIndex);
        final CategoryDataset dataset = plot.getDataset(rendererIndex);
        if (dataset != null) {
            final int columns = dataset.getColumnCount();
            final int rows = dataset.getRowCount();
            double space = 0.0;
            final PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            final double maxWidth = space * this.getMaxItemWidth();
            double categoryMargin = 0.0;
            double currentItemMargin = 0.0;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }
            if (rows > 1) {
                currentItemMargin = this.getItemMargin();
            }
            final double used = space * (1.0 - domainAxis.getLowerMargin() - domainAxis.getUpperMargin() - categoryMargin - currentItemMargin);
            if (rows * columns > 0) {
                state.setBarWidth(Math.min(used / (rows * columns), maxWidth));
            }
            else {
                state.setBarWidth(Math.min(used, maxWidth));
            }
        }
    }
    
    protected double calculateBarW0(final CategoryPlot plot, final PlotOrientation orientation, final Rectangle2D dataArea, final CategoryAxis domainAxis, final CategoryItemRendererState state, final int row, final int column) {
        double space = 0.0;
        if (orientation == PlotOrientation.HORIZONTAL) {
            space = dataArea.getHeight();
        }
        else {
            space = dataArea.getWidth();
        }
        double barW0 = domainAxis.getCategoryStart(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        final int seriesCount = this.getRowCount();
        final int categoryCount = this.getColumnCount();
        if (seriesCount > 1) {
            final double seriesGap = space * this.getItemMargin() / (categoryCount * (seriesCount - 1));
            final double seriesW = this.calculateSeriesWidth(space, domainAxis, categoryCount, seriesCount);
            barW0 = barW0 + row * (seriesW + seriesGap) + seriesW / 2.0 - state.getBarWidth() / 2.0;
        }
        else {
            barW0 = domainAxis.getCategoryMiddle(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;
        }
        return barW0;
    }
    
    public void drawItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryDataset dataset, final int row, final int column, final int pass) {
        final Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }
        final double value = dataValue.doubleValue();
        final PlotOrientation orientation = plot.getOrientation();
        final double barW0 = this.calculateBarW0(plot, orientation, dataArea, domainAxis, state, row, column);
        final RectangleEdge edge = plot.getRangeAxisEdge();
        final double barL = rangeAxis.valueToJava2D(value, dataArea, edge);
        Line2D line = null;
        double x = 0.0;
        double y = 0.0;
        if (orientation == PlotOrientation.HORIZONTAL) {
            x = barL;
            y = barW0 + state.getBarWidth() / 2.0;
            line = new Line2D.Double(barL, barW0, barL, barW0 + state.getBarWidth());
        }
        else {
            x = barW0 + state.getBarWidth() / 2.0;
            y = barL;
            line = new Line2D.Double(barW0, barL, barW0 + state.getBarWidth(), barL);
        }
        final Stroke itemStroke = this.getItemStroke(row, column);
        final Paint itemPaint = this.getItemPaint(row, column);
        g2.setStroke(itemStroke);
        g2.setPaint(itemPaint);
        g2.draw(line);
        final CategoryItemLabelGenerator generator = this.getItemLabelGenerator(row, column);
        if (generator != null && this.isItemLabelVisible(row, column)) {
            this.drawItemLabel(g2, orientation, dataset, row, column, x, y, value < 0.0);
        }
        if (state.getInfo() != null) {
            final EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                String tip = null;
                final CategoryToolTipGenerator tipster = this.getToolTipGenerator(row, column);
                if (tipster != null) {
                    tip = tipster.generateToolTip(dataset, row, column);
                }
                String url = null;
                if (this.getItemURLGenerator(row, column) != null) {
                    url = this.getItemURLGenerator(row, column).generateURL(dataset, row, column);
                }
                final CategoryItemEntity entity = new CategoryItemEntity(line.getBounds(), tip, url, dataset, row, dataset.getColumnKey(column), column);
                entities.add(entity);
            }
        }
    }
    
    protected double calculateSeriesWidth(final double space, final CategoryAxis axis, final int categories, final int series) {
        double factor = 1.0 - this.getItemMargin() - axis.getLowerMargin() - axis.getUpperMargin();
        if (categories > 1) {
            factor -= axis.getCategoryMargin();
        }
        return space * factor / (categories * series);
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LevelRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final LevelRenderer that = (LevelRenderer)obj;
        return this.itemMargin == that.itemMargin && this.maxItemWidth == that.maxItemWidth;
    }
}
