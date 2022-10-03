package org.jfree.chart.renderer.xy;

import org.jfree.chart.labels.XYToolTipGenerator;
import java.awt.Stroke;
import java.awt.Paint;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import java.awt.Shape;
import org.jfree.util.ShapeUtilities;
import java.awt.geom.Line2D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class YIntervalRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -2951586537224143260L;
    
    public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea, final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis, final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState, final int pass) {
        Shape entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        final IntervalXYDataset intervalDataset = (IntervalXYDataset)dataset;
        final double x = intervalDataset.getXValue(series, item);
        final double yLow = intervalDataset.getStartYValue(series, item);
        final double yHigh = intervalDataset.getEndYValue(series, item);
        final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        final double xx = domainAxis.valueToJava2D(x, dataArea, xAxisLocation);
        final double yyLow = rangeAxis.valueToJava2D(yLow, dataArea, yAxisLocation);
        final double yyHigh = rangeAxis.valueToJava2D(yHigh, dataArea, yAxisLocation);
        final Paint p = this.getItemPaint(series, item);
        final Stroke s = this.getItemStroke(series, item);
        Line2D line = null;
        final Shape shape = this.getItemShape(series, item);
        Shape top = null;
        Shape bottom = null;
        final PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(yyLow, xx, yyHigh, xx);
            top = ShapeUtilities.createTranslatedShape(shape, yyHigh, xx);
            bottom = ShapeUtilities.createTranslatedShape(shape, yyLow, xx);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(xx, yyLow, xx, yyHigh);
            top = ShapeUtilities.createTranslatedShape(shape, xx, yyHigh);
            bottom = ShapeUtilities.createTranslatedShape(shape, xx, yyLow);
        }
        g2.setPaint(p);
        g2.setStroke(s);
        g2.draw(line);
        g2.fill(top);
        g2.fill(bottom);
        if (entities != null) {
            if (entityArea == null) {
                entityArea = line.getBounds();
            }
            String tip = null;
            final XYToolTipGenerator generator = this.getToolTipGenerator(series, item);
            if (generator != null) {
                tip = generator.generateToolTip(dataset, series, item);
            }
            String url = null;
            if (this.getURLGenerator() != null) {
                url = this.getURLGenerator().generateURL(dataset, series, item);
            }
            final XYItemEntity entity = new XYItemEntity(entityArea, dataset, series, item, tip, url);
            entities.add(entity);
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
