package org.jfree.chart.renderer.xy;

import org.jfree.ui.RectangleEdge;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.event.RendererChangeEvent;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class XYDotRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -2764344339073566425L;
    private int dotWidth;
    private int dotHeight;
    
    public XYDotRenderer() {
        this.dotWidth = 1;
        this.dotHeight = 1;
    }
    
    public int getDotWidth() {
        return this.dotWidth;
    }
    
    public void setDotWidth(final int w) {
        if (w < 1) {
            throw new IllegalArgumentException("Requires w > 0.");
        }
        this.dotWidth = w;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public int getDotHeight() {
        return this.dotHeight;
    }
    
    public void setDotHeight(final int h) {
        if (h < 1) {
            throw new IllegalArgumentException("Requires h > 0.");
        }
        this.dotHeight = h;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea, final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis, final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState, final int pass) {
        final double x = dataset.getXValue(series, item);
        final double y = dataset.getYValue(series, item);
        final double adjx = (this.dotWidth - 1) / 2.0;
        final double adjy = (this.dotHeight - 1) / 2.0;
        if (!Double.isNaN(y)) {
            final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            final double transX = domainAxis.valueToJava2D(x, dataArea, xAxisLocation) - adjx;
            final double transY = rangeAxis.valueToJava2D(y, dataArea, yAxisLocation) - adjy;
            g2.setPaint(this.getItemPaint(series, item));
            final PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                g2.fillRect((int)transY, (int)transX, this.dotHeight, this.dotWidth);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                g2.fillRect((int)transX, (int)transY, this.dotWidth, this.dotHeight);
            }
            this.updateCrosshairValues(crosshairState, x, y, transX, transY, orientation);
        }
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYDotRenderer)) {
            return false;
        }
        final XYDotRenderer that = (XYDotRenderer)obj;
        return this.dotWidth == that.dotWidth && this.dotHeight == that.dotHeight && super.equals(obj);
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
