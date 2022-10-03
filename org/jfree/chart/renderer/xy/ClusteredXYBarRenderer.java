package org.jfree.chart.renderer.xy;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.ui.RectangleEdge;
import java.awt.Paint;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import java.awt.Shape;
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

public class ClusteredXYBarRenderer extends XYBarRenderer implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = 5864462149177133147L;
    private boolean centerBarAtStartValue;
    
    public ClusteredXYBarRenderer() {
        this(0.0, false);
    }
    
    public ClusteredXYBarRenderer(final double margin, final boolean centerBarAtStartValue) {
        super(margin);
        this.centerBarAtStartValue = centerBarAtStartValue;
    }
    
    public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea, final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis, final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState, final int pass) {
        final IntervalXYDataset intervalDataset = (IntervalXYDataset)dataset;
        final Paint seriesPaint = this.getItemPaint(series, item);
        double value0;
        double value2;
        if (this.getUseYInterval()) {
            value0 = intervalDataset.getStartYValue(series, item);
            value2 = intervalDataset.getEndYValue(series, item);
        }
        else {
            value0 = this.getBase();
            value2 = intervalDataset.getYValue(series, item);
        }
        if (Double.isNaN(value0) || Double.isNaN(value2)) {
            return;
        }
        final double translatedValue0 = rangeAxis.valueToJava2D(value0, dataArea, plot.getRangeAxisEdge());
        final double translatedValue2 = rangeAxis.valueToJava2D(value2, dataArea, plot.getRangeAxisEdge());
        final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        final double x1 = intervalDataset.getStartXValue(series, item);
        double translatedX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        final double x2 = intervalDataset.getEndXValue(series, item);
        final double translatedX2 = domainAxis.valueToJava2D(x2, dataArea, xAxisLocation);
        double translatedWidth = Math.max(1.0, Math.abs(translatedX2 - translatedX1));
        final double translatedHeight = Math.abs(translatedValue0 - translatedValue2);
        if (this.centerBarAtStartValue) {
            translatedX1 -= translatedWidth / 2.0;
        }
        final PlotOrientation orientation = plot.getOrientation();
        final double m = this.getMargin();
        if (m > 0.0) {
            final double cut = translatedWidth * this.getMargin();
            translatedWidth -= cut;
            if (orientation == PlotOrientation.HORIZONTAL) {
                translatedX1 -= cut / 2.0;
            }
            else {
                translatedX1 += cut / 2.0;
            }
        }
        final int numSeries = dataset.getSeriesCount();
        final double seriesBarWidth = translatedWidth / numSeries;
        Rectangle2D bar = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(Math.min(translatedValue0, translatedValue2), translatedX1 - seriesBarWidth * (numSeries - series), translatedHeight, seriesBarWidth);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            bar = new Rectangle2D.Double(translatedX1 + seriesBarWidth * series, Math.min(translatedValue0, translatedValue2), seriesBarWidth, translatedHeight);
        }
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if (this.isDrawBarOutline() && Math.abs(translatedX2 - translatedX1) > 3.0) {
            g2.setStroke(this.getItemOutlineStroke(series, item));
            g2.setPaint(this.getItemOutlinePaint(series, item));
            g2.draw(bar);
        }
        if (this.isItemLabelVisible(series, item)) {
            final XYItemLabelGenerator generator = this.getItemLabelGenerator(series, item);
            this.drawItemLabel(g2, dataset, series, item, plot, generator, bar, value2 < 0.0);
        }
        if (info != null) {
            final EntityCollection entities = info.getOwner().getEntityCollection();
            if (entities != null) {
                String tip = null;
                final XYToolTipGenerator generator2 = this.getToolTipGenerator(series, item);
                if (generator2 != null) {
                    tip = generator2.generateToolTip(dataset, series, item);
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
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ClusteredXYBarRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final ClusteredXYBarRenderer that = (ClusteredXYBarRenderer)obj;
        return this.centerBarAtStartValue == that.centerBarAtStartValue;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
