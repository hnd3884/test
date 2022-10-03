package org.jfree.chart.renderer.category;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.plot.PlotOrientation;
import java.awt.geom.GeneralPath;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.CategoryAxis;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.Paint;
import java.awt.Shape;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.plot.CategoryPlot;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.LegendItem;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.renderer.AreaRendererEndType;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class AreaRenderer extends AbstractCategoryItemRenderer implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -4231878281385812757L;
    private AreaRendererEndType endType;
    
    public AreaRenderer() {
        this.endType = AreaRendererEndType.TAPER;
    }
    
    public AreaRendererEndType getEndType() {
        return this.endType;
    }
    
    public void setEndType(final AreaRendererEndType type) {
        if (type == null) {
            throw new IllegalArgumentException("Null 'type' argument.");
        }
        this.endType = type;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public LegendItem getLegendItem(final int datasetIndex, final int series) {
        final CategoryPlot cp = this.getPlot();
        if (cp == null) {
            return null;
        }
        final CategoryDataset dataset = cp.getDataset(datasetIndex);
        final String description;
        final String label = description = this.getLegendItemLabelGenerator().generateLabel(dataset, series);
        String toolTipText = null;
        if (this.getLegendItemToolTipGenerator() != null) {
            toolTipText = this.getLegendItemToolTipGenerator().generateLabel(dataset, series);
        }
        String urlText = null;
        if (this.getLegendItemURLGenerator() != null) {
            urlText = this.getLegendItemURLGenerator().generateLabel(dataset, series);
        }
        final Shape shape = new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0);
        final Paint paint = this.getSeriesPaint(series);
        final Paint outlinePaint = this.getSeriesOutlinePaint(series);
        final Stroke outlineStroke = this.getSeriesOutlineStroke(series);
        return new LegendItem(label, description, toolTipText, urlText, shape, paint, outlineStroke, outlinePaint);
    }
    
    public void drawItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryDataset dataset, final int row, final int column, final int pass) {
        final Number value = dataset.getValue(row, column);
        if (value != null) {
            final PlotOrientation orientation = plot.getOrientation();
            final RectangleEdge axisEdge = plot.getDomainAxisEdge();
            final int count = dataset.getColumnCount();
            float x0 = (float)domainAxis.getCategoryStart(column, count, dataArea, axisEdge);
            float x2 = (float)domainAxis.getCategoryMiddle(column, count, dataArea, axisEdge);
            float x3 = (float)domainAxis.getCategoryEnd(column, count, dataArea, axisEdge);
            x0 = (float)Math.round(x0);
            x2 = (float)Math.round(x2);
            x3 = (float)Math.round(x3);
            if (this.endType == AreaRendererEndType.TRUNCATE) {
                if (column == 0) {
                    x0 = x2;
                }
                else if (column == this.getColumnCount() - 1) {
                    x3 = x2;
                }
            }
            final double yy1 = value.doubleValue();
            double yy2 = 0.0;
            if (column > 0) {
                final Number n0 = dataset.getValue(row, column - 1);
                if (n0 != null) {
                    yy2 = (n0.doubleValue() + yy1) / 2.0;
                }
            }
            double yy3 = 0.0;
            if (column < dataset.getColumnCount() - 1) {
                final Number n2 = dataset.getValue(row, column + 1);
                if (n2 != null) {
                    yy3 = (n2.doubleValue() + yy1) / 2.0;
                }
            }
            final RectangleEdge edge = plot.getRangeAxisEdge();
            final float y0 = (float)rangeAxis.valueToJava2D(yy2, dataArea, edge);
            final float y2 = (float)rangeAxis.valueToJava2D(yy1, dataArea, edge);
            final float y3 = (float)rangeAxis.valueToJava2D(yy3, dataArea, edge);
            final float yz = (float)rangeAxis.valueToJava2D(0.0, dataArea, edge);
            g2.setPaint(this.getItemPaint(row, column));
            g2.setStroke(this.getItemStroke(row, column));
            final GeneralPath area = new GeneralPath();
            if (orientation == PlotOrientation.VERTICAL) {
                area.moveTo(x0, yz);
                area.lineTo(x0, y0);
                area.lineTo(x2, y2);
                area.lineTo(x3, y3);
                area.lineTo(x3, yz);
            }
            else if (orientation == PlotOrientation.HORIZONTAL) {
                area.moveTo(yz, x0);
                area.lineTo(y0, x0);
                area.lineTo(y2, x2);
                area.lineTo(y3, x3);
                area.lineTo(yz, x3);
            }
            area.closePath();
            g2.setPaint(this.getItemPaint(row, column));
            g2.fill(area);
            if (this.isItemLabelVisible(row, column)) {
                this.drawItemLabel(g2, orientation, dataset, row, column, x2, y2, value.doubleValue() < 0.0);
            }
            final EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                this.addItemEntity(entities, dataset, row, column, area);
            }
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
