package org.jfree.chart.renderer.xy;

import org.jfree.chart.entity.EntityCollection;
import java.awt.geom.Line2D;
import org.jfree.ui.RectangleEdge;
import java.awt.Stroke;
import java.awt.Paint;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import java.awt.Shape;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class XYStepRenderer extends XYLineAndShapeRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -8918141928884796108L;
    
    public XYStepRenderer() {
        this(null, null);
    }
    
    public XYStepRenderer(final XYToolTipGenerator toolTipGenerator, final XYURLGenerator urlGenerator) {
        this.setBaseToolTipGenerator(toolTipGenerator);
        this.setURLGenerator(urlGenerator);
        this.setShapesVisible(false);
    }
    
    public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea, final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis, final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState, final int pass) {
        if (!this.getItemVisible(series, item)) {
            return;
        }
        final PlotOrientation orientation = plot.getOrientation();
        final Paint seriesPaint = this.getItemPaint(series, item);
        final Stroke seriesStroke = this.getItemStroke(series, item);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);
        final double x1 = dataset.getXValue(series, item);
        final double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1)) {
            return;
        }
        final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        final double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        final double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        if (item > 0) {
            final double x2 = dataset.getXValue(series, item - 1);
            final double y2 = dataset.getYValue(series, item - 1);
            if (!Double.isNaN(y2)) {
                final double transX2 = domainAxis.valueToJava2D(x2, dataArea, xAxisLocation);
                final double transY2 = rangeAxis.valueToJava2D(y2, dataArea, yAxisLocation);
                final Line2D line = state.workingLine;
                if (orientation == PlotOrientation.HORIZONTAL) {
                    if (transY2 == transY1) {
                        line.setLine(transY2, transX2, transY1, transX1);
                        g2.draw(line);
                    }
                    else {
                        line.setLine(transY2, transX2, transY1, transX2);
                        g2.draw(line);
                        line.setLine(transY1, transX2, transY1, transX1);
                        g2.draw(line);
                    }
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    if (transY2 == transY1) {
                        line.setLine(transX2, transY2, transX1, transY1);
                        g2.draw(line);
                    }
                    else {
                        line.setLine(transX2, transY2, transX1, transY2);
                        g2.draw(line);
                        line.setLine(transX1, transY2, transX1, transY1);
                        g2.draw(line);
                    }
                }
            }
        }
        if (this.isItemLabelVisible(series, item)) {
            double xx = transX1;
            double yy = transY1;
            if (orientation == PlotOrientation.HORIZONTAL) {
                xx = transY1;
                yy = transX1;
            }
            this.drawItemLabel(g2, orientation, dataset, series, item, xx, yy, y1 < 0.0);
        }
        this.updateCrosshairValues(crosshairState, x1, y1, transX1, transY1, orientation);
        if (state.getInfo() != null) {
            final EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                final int r = this.getDefaultEntityRadius();
                final Shape shape = (orientation == PlotOrientation.VERTICAL) ? new Rectangle2D.Double(transX1 - r, transY1 - r, 2 * r, 2 * r) : new Rectangle2D.Double(transY1 - r, transX1 - r, 2 * r, 2 * r);
                if (shape != null) {
                    String tip = null;
                    final XYToolTipGenerator generator = this.getToolTipGenerator(series, item);
                    if (generator != null) {
                        tip = generator.generateToolTip(dataset, series, item);
                    }
                    String url = null;
                    if (this.getURLGenerator() != null) {
                        url = this.getURLGenerator().generateURL(dataset, series, item);
                    }
                    final XYItemEntity entity = new XYItemEntity(shape, dataset, series, item, tip, url);
                    entities.add(entity);
                }
            }
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
