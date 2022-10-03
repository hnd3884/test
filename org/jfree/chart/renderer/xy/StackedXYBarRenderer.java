package org.jfree.chart.renderer.xy;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import java.awt.Shape;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.Range;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.plot.XYPlot;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.io.Serializable;

public class StackedXYBarRenderer extends XYBarRenderer implements Serializable
{
    private static final long serialVersionUID = -7049101055533436444L;
    
    public StackedXYBarRenderer() {
    }
    
    public StackedXYBarRenderer(final double margin) {
        super(margin);
    }
    
    public XYItemRendererState initialise(final Graphics2D g2, final Rectangle2D dataArea, final XYPlot plot, final XYDataset data, final PlotRenderingInfo info) {
        return new XYBarRendererState(info);
    }
    
    public Range findRangeBounds(final XYDataset dataset) {
        if (dataset != null) {
            return DatasetUtilities.findStackedRangeBounds((TableXYDataset)dataset);
        }
        return null;
    }
    
    public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea, final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis, final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState, final int pass) {
        if (!(dataset instanceof IntervalXYDataset) || !(dataset instanceof TableXYDataset)) {
            String message = "dataset (type " + dataset.getClass().getName() + ") has wrong type:";
            boolean and = false;
            if (!IntervalXYDataset.class.isAssignableFrom(dataset.getClass())) {
                message += " it is no IntervalXYDataset";
                and = true;
            }
            if (!TableXYDataset.class.isAssignableFrom(dataset.getClass())) {
                if (and) {
                    message += " and";
                }
                message += " it is no TableXYDataset";
            }
            throw new IllegalArgumentException(message);
        }
        final IntervalXYDataset intervalDataset = (IntervalXYDataset)dataset;
        final double value = intervalDataset.getYValue(series, item);
        if (Double.isNaN(value)) {
            return;
        }
        double positiveBase = 0.0;
        double negativeBase = 0.0;
        for (int i = 0; i < series; ++i) {
            final double v = dataset.getYValue(i, item);
            if (!Double.isNaN(v)) {
                if (v > 0.0) {
                    positiveBase += v;
                }
                else {
                    negativeBase += v;
                }
            }
        }
        final RectangleEdge edgeR = plot.getRangeAxisEdge();
        double translatedBase;
        double translatedValue;
        if (value > 0.0) {
            translatedBase = rangeAxis.valueToJava2D(positiveBase, dataArea, edgeR);
            translatedValue = rangeAxis.valueToJava2D(positiveBase + value, dataArea, edgeR);
        }
        else {
            translatedBase = rangeAxis.valueToJava2D(negativeBase, dataArea, edgeR);
            translatedValue = rangeAxis.valueToJava2D(negativeBase + value, dataArea, edgeR);
        }
        final RectangleEdge edgeD = plot.getDomainAxisEdge();
        final double startX = intervalDataset.getStartXValue(series, item);
        if (Double.isNaN(startX)) {
            return;
        }
        double translatedStartX = domainAxis.valueToJava2D(startX, dataArea, edgeD);
        final double endX = intervalDataset.getEndXValue(series, item);
        if (Double.isNaN(endX)) {
            return;
        }
        final double translatedEndX = domainAxis.valueToJava2D(endX, dataArea, edgeD);
        double translatedWidth = Math.max(1.0, Math.abs(translatedEndX - translatedStartX));
        final double translatedHeight = Math.abs(translatedValue - translatedBase);
        if (this.getMargin() > 0.0) {
            final double cut = translatedWidth * this.getMargin();
            translatedWidth -= cut;
            translatedStartX += cut / 2.0;
        }
        Rectangle2D bar = null;
        final PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(Math.min(translatedBase, translatedValue), translatedEndX, translatedHeight, translatedWidth);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            bar = new Rectangle2D.Double(translatedStartX, Math.min(translatedBase, translatedValue), translatedWidth, translatedHeight);
        }
        g2.setPaint(this.getItemPaint(series, item));
        g2.fill(bar);
        if (this.isDrawBarOutline() && Math.abs(translatedEndX - translatedStartX) > 3.0) {
            g2.setStroke(this.getItemStroke(series, item));
            g2.setPaint(this.getItemOutlinePaint(series, item));
            g2.draw(bar);
        }
        if (info != null) {
            final EntityCollection entities = info.getOwner().getEntityCollection();
            if (entities != null) {
                String tip = null;
                final XYToolTipGenerator generator = this.getToolTipGenerator(series, item);
                if (generator != null) {
                    tip = generator.generateToolTip(dataset, series, item);
                }
                String url = null;
                if (this.getURLGenerator() != null) {
                    url = this.getURLGenerator().generateURL(dataset, series, item);
                }
                final XYItemEntity entity = new XYItemEntity(bar, dataset, series, item, tip, url);
                entities.add(entity);
            }
        }
    }
}
