package org.jfree.chart.renderer.category;

import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import java.awt.Stroke;
import java.awt.Paint;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.CategoryItemEntity;
import java.awt.Shape;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class IntervalBarRenderer extends BarRenderer implements CategoryItemRenderer, Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -5068857361615528725L;
    
    public void drawItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryDataset dataset, final int row, final int column, final int pass) {
        if (dataset instanceof IntervalCategoryDataset) {
            final IntervalCategoryDataset d = (IntervalCategoryDataset)dataset;
            this.drawInterval(g2, state, dataArea, plot, domainAxis, rangeAxis, d, row, column);
        }
        else {
            super.drawItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column, pass);
        }
    }
    
    protected void drawInterval(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final IntervalCategoryDataset dataset, final int row, final int column) {
        final int seriesCount = this.getRowCount();
        final int categoryCount = this.getColumnCount();
        final PlotOrientation orientation = plot.getOrientation();
        double rectX = 0.0;
        double rectY = 0.0;
        final RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        final RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        Number value0 = dataset.getEndValue(row, column);
        if (value0 == null) {
            return;
        }
        double java2dValue0 = rangeAxis.valueToJava2D(value0.doubleValue(), dataArea, rangeAxisLocation);
        Number value2 = dataset.getStartValue(row, column);
        if (value2 == null) {
            return;
        }
        double java2dValue2 = rangeAxis.valueToJava2D(value2.doubleValue(), dataArea, rangeAxisLocation);
        if (java2dValue2 < java2dValue0) {
            final double temp = java2dValue2;
            java2dValue2 = java2dValue0;
            java2dValue0 = temp;
            final Number tempNum = value2;
            value2 = value0;
            value0 = tempNum;
        }
        double rectWidth = state.getBarWidth();
        double rectHeight = Math.abs(java2dValue2 - java2dValue0);
        if (orientation == PlotOrientation.HORIZONTAL) {
            rectY = domainAxis.getCategoryStart(column, this.getColumnCount(), dataArea, domainAxisLocation);
            if (seriesCount > 1) {
                final double seriesGap = dataArea.getHeight() * this.getItemMargin() / (categoryCount * (seriesCount - 1));
                rectY += row * (state.getBarWidth() + seriesGap);
            }
            else {
                rectY += row * state.getBarWidth();
            }
            rectX = java2dValue0;
            rectHeight = state.getBarWidth();
            rectWidth = Math.abs(java2dValue2 - java2dValue0);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            rectX = domainAxis.getCategoryStart(column, this.getColumnCount(), dataArea, domainAxisLocation);
            if (seriesCount > 1) {
                final double seriesGap = dataArea.getWidth() * this.getItemMargin() / (categoryCount * (seriesCount - 1));
                rectX += row * (state.getBarWidth() + seriesGap);
            }
            else {
                rectX += row * state.getBarWidth();
            }
            rectY = java2dValue0;
        }
        final Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
        final Paint seriesPaint = this.getItemPaint(row, column);
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if (state.getBarWidth() > 3.0) {
            final Stroke stroke = this.getItemOutlineStroke(row, column);
            final Paint paint = this.getItemOutlinePaint(row, column);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }
        final CategoryItemLabelGenerator generator = this.getItemLabelGenerator(row, column);
        if (generator != null && this.isItemLabelVisible(row, column)) {
            this.drawItemLabel(g2, dataset, row, column, plot, generator, bar, false);
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
                final CategoryItemEntity entity = new CategoryItemEntity(bar, tip, url, dataset, row, dataset.getColumnKey(column), column);
                entities.add(entity);
            }
        }
    }
}
