package org.jfree.chart.annotations;

import org.jfree.util.ObjectUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.plot.PlotOrientation;
import java.awt.Shape;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.axis.ValueAxis;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.plot.XYPlot;
import java.awt.Graphics2D;
import org.jfree.ui.Drawable;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class XYDrawableAnnotation extends AbstractXYAnnotation implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -6540812859722691020L;
    private double x;
    private double y;
    private double width;
    private double height;
    private Drawable drawable;
    
    public XYDrawableAnnotation(final double x, final double y, final double width, final double height, final Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Null 'drawable' argument.");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.drawable = drawable;
    }
    
    public void draw(final Graphics2D g2, final XYPlot plot, final Rectangle2D dataArea, final ValueAxis domainAxis, final ValueAxis rangeAxis, final int rendererIndex, final PlotRenderingInfo info) {
        final PlotOrientation orientation = plot.getOrientation();
        final RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
        final RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
        final float j2DX = (float)domainAxis.valueToJava2D(this.x, dataArea, domainEdge);
        final float j2DY = (float)rangeAxis.valueToJava2D(this.y, dataArea, rangeEdge);
        final Rectangle2D area = new Rectangle2D.Double(j2DX - this.width / 2.0, j2DY - this.height / 2.0, this.width, this.height);
        this.drawable.draw(g2, area);
        final String toolTip = this.getToolTipText();
        final String url = this.getURL();
        if (toolTip != null || url != null) {
            this.addEntity(info, area, rendererIndex, toolTip, url);
        }
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof XYDrawableAnnotation)) {
            return false;
        }
        final XYDrawableAnnotation that = (XYDrawableAnnotation)obj;
        return this.x == that.x && this.y == that.y && this.width == that.width && this.height == that.height && ObjectUtilities.equal((Object)this.drawable, (Object)that.drawable);
    }
    
    public int hashCode() {
        long temp = Double.doubleToLongBits(this.x);
        int result = (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.y);
        result = 29 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.width);
        result = 29 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.height);
        result = 29 * result + (int)(temp ^ temp >>> 32);
        return result;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
