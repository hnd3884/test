package org.jfree.chart.renderer.xy;

import java.io.ObjectOutputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectInputStream;
import org.jfree.util.PaintUtilities;
import org.jfree.chart.labels.XYToolTipGenerator;
import java.awt.Stroke;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import java.awt.Shape;
import java.awt.geom.Line2D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.event.RendererChangeEvent;
import java.awt.Paint;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class HighLowRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -8135673815876552516L;
    private boolean drawOpenTicks;
    private boolean drawCloseTicks;
    private transient Paint openTickPaint;
    private transient Paint closeTickPaint;
    
    public HighLowRenderer() {
        this.drawOpenTicks = true;
        this.drawCloseTicks = true;
    }
    
    public boolean getDrawOpenTicks() {
        return this.drawOpenTicks;
    }
    
    public void setDrawOpenTicks(final boolean draw) {
        this.drawOpenTicks = draw;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public boolean getDrawCloseTicks() {
        return this.drawCloseTicks;
    }
    
    public void setDrawCloseTicks(final boolean draw) {
        this.drawCloseTicks = draw;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public Paint getOpenTickPaint() {
        return this.openTickPaint;
    }
    
    public void setOpenTickPaint(final Paint paint) {
        this.openTickPaint = paint;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public Paint getCloseTickPaint() {
        return this.closeTickPaint;
    }
    
    public void setCloseTickPaint(final Paint paint) {
        this.closeTickPaint = paint;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea, final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis, final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState, final int pass) {
        final double x = dataset.getXValue(series, item);
        if (!domainAxis.getRange().contains(x)) {
            return;
        }
        final double xx = domainAxis.valueToJava2D(x, dataArea, plot.getDomainAxisEdge());
        Shape entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        final PlotOrientation orientation = plot.getOrientation();
        final RectangleEdge location = plot.getRangeAxisEdge();
        final Paint itemPaint = this.getItemPaint(series, item);
        final Stroke itemStroke = this.getItemStroke(series, item);
        g2.setPaint(itemPaint);
        g2.setStroke(itemStroke);
        if (dataset instanceof OHLCDataset) {
            final OHLCDataset hld = (OHLCDataset)dataset;
            final double yHigh = hld.getHighValue(series, item);
            final double yLow = hld.getLowValue(series, item);
            if (!Double.isNaN(yHigh) && !Double.isNaN(yLow)) {
                final double yyHigh = rangeAxis.valueToJava2D(yHigh, dataArea, location);
                final double yyLow = rangeAxis.valueToJava2D(yLow, dataArea, location);
                if (orientation == PlotOrientation.HORIZONTAL) {
                    g2.draw(new Line2D.Double(yyLow, xx, yyHigh, xx));
                    entityArea = new Rectangle2D.Double(Math.min(yyLow, yyHigh), xx - 1.0, Math.abs(yyHigh - yyLow), 2.0);
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    g2.draw(new Line2D.Double(xx, yyLow, xx, yyHigh));
                    entityArea = new Rectangle2D.Double(xx - 1.0, Math.min(yyLow, yyHigh), 2.0, Math.abs(yyHigh - yyLow));
                }
            }
            double delta = 2.0;
            if (domainAxis.isInverted()) {
                delta = -delta;
            }
            if (this.getDrawOpenTicks()) {
                final double yOpen = hld.getOpenValue(series, item);
                if (!Double.isNaN(yOpen)) {
                    final double yyOpen = rangeAxis.valueToJava2D(yOpen, dataArea, location);
                    if (this.openTickPaint != null) {
                        g2.setPaint(this.openTickPaint);
                    }
                    else {
                        g2.setPaint(itemPaint);
                    }
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        g2.draw(new Line2D.Double(yyOpen, xx + delta, yyOpen, xx));
                    }
                    else if (orientation == PlotOrientation.VERTICAL) {
                        g2.draw(new Line2D.Double(xx - delta, yyOpen, xx, yyOpen));
                    }
                }
            }
            if (this.getDrawCloseTicks()) {
                final double yClose = hld.getCloseValue(series, item);
                if (!Double.isNaN(yClose)) {
                    final double yyClose = rangeAxis.valueToJava2D(yClose, dataArea, location);
                    if (this.closeTickPaint != null) {
                        g2.setPaint(this.closeTickPaint);
                    }
                    else {
                        g2.setPaint(itemPaint);
                    }
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        g2.draw(new Line2D.Double(yyClose, xx, yyClose, xx - delta));
                    }
                    else if (orientation == PlotOrientation.VERTICAL) {
                        g2.draw(new Line2D.Double(xx, yyClose, xx + delta, yyClose));
                    }
                }
            }
        }
        else if (item > 0) {
            final double x2 = dataset.getXValue(series, item - 1);
            final double y0 = dataset.getYValue(series, item - 1);
            final double y2 = dataset.getYValue(series, item);
            if (Double.isNaN(x2) || Double.isNaN(y0) || Double.isNaN(y2)) {
                return;
            }
            final double xx2 = domainAxis.valueToJava2D(x2, dataArea, plot.getDomainAxisEdge());
            final double yy0 = rangeAxis.valueToJava2D(y0, dataArea, location);
            final double yy2 = rangeAxis.valueToJava2D(y2, dataArea, location);
            if (orientation == PlotOrientation.HORIZONTAL) {
                g2.draw(new Line2D.Double(yy0, xx2, yy2, xx));
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                g2.draw(new Line2D.Double(xx2, yy0, xx, yy2));
            }
        }
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
            final XYItemEntity entity = new XYItemEntity(entityArea, dataset, series, item, tip, url);
            entities.add(entity);
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HighLowRenderer)) {
            return false;
        }
        final HighLowRenderer that = (HighLowRenderer)obj;
        return this.drawOpenTicks == that.drawOpenTicks && this.drawCloseTicks == that.drawCloseTicks && PaintUtilities.equal(this.openTickPaint, that.openTickPaint) && PaintUtilities.equal(this.closeTickPaint, that.closeTickPaint) && super.equals(obj);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.openTickPaint = SerialUtilities.readPaint(stream);
        this.closeTickPaint = SerialUtilities.readPaint(stream);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.openTickPaint, stream);
        SerialUtilities.writePaint(this.closeTickPaint, stream);
    }
}
