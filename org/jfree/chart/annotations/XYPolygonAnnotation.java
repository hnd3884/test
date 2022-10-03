package org.jfree.chart.annotations;

import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import java.awt.GradientPaint;
import org.jfree.util.PaintUtilities;
import org.jfree.util.ObjectUtilities;
import java.util.Arrays;
import org.jfree.ui.RectangleEdge;
import java.awt.Shape;
import org.jfree.chart.plot.PlotOrientation;
import java.awt.geom.GeneralPath;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.axis.ValueAxis;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.plot.XYPlot;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class XYPolygonAnnotation extends AbstractXYAnnotation implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -6984203651995900036L;
    private double[] polygon;
    private transient Stroke stroke;
    private transient Paint outlinePaint;
    private transient Paint fillPaint;
    
    public XYPolygonAnnotation(final double[] polygon) {
        this(polygon, new BasicStroke(1.0f), Color.black);
    }
    
    public XYPolygonAnnotation(final double[] polygon, final Stroke stroke, final Paint outlinePaint) {
        this(polygon, stroke, outlinePaint, null);
    }
    
    public XYPolygonAnnotation(final double[] polygon, final Stroke stroke, final Paint outlinePaint, final Paint fillPaint) {
        if (polygon == null) {
            throw new IllegalArgumentException("Null 'polygon' argument.");
        }
        if (polygon.length % 2 != 0) {
            throw new IllegalArgumentException("The 'polygon' array must contain an even number of items.");
        }
        this.polygon = polygon.clone();
        this.stroke = stroke;
        this.outlinePaint = outlinePaint;
        this.fillPaint = fillPaint;
    }
    
    public double[] getPolygonCoordinates() {
        return this.polygon.clone();
    }
    
    public Paint getFillPaint() {
        return this.fillPaint;
    }
    
    public Stroke getOutlineStroke() {
        return this.stroke;
    }
    
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }
    
    public void draw(final Graphics2D g2, final XYPlot plot, final Rectangle2D dataArea, final ValueAxis domainAxis, final ValueAxis rangeAxis, final int rendererIndex, final PlotRenderingInfo info) {
        if (this.polygon.length < 4) {
            return;
        }
        final PlotOrientation orientation = plot.getOrientation();
        final RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
        final RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
        final GeneralPath area = new GeneralPath();
        double x = domainAxis.valueToJava2D(this.polygon[0], dataArea, domainEdge);
        double y = rangeAxis.valueToJava2D(this.polygon[1], dataArea, rangeEdge);
        if (orientation == PlotOrientation.HORIZONTAL) {
            area.moveTo((float)y, (float)x);
            for (int i = 2; i < this.polygon.length; i += 2) {
                x = domainAxis.valueToJava2D(this.polygon[i], dataArea, domainEdge);
                y = rangeAxis.valueToJava2D(this.polygon[i + 1], dataArea, rangeEdge);
                area.lineTo((float)y, (float)x);
            }
            area.closePath();
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            area.moveTo((float)x, (float)y);
            for (int i = 2; i < this.polygon.length; i += 2) {
                x = domainAxis.valueToJava2D(this.polygon[i], dataArea, domainEdge);
                y = rangeAxis.valueToJava2D(this.polygon[i + 1], dataArea, rangeEdge);
                area.lineTo((float)x, (float)y);
            }
            area.closePath();
        }
        if (this.fillPaint != null) {
            g2.setPaint(this.fillPaint);
            g2.fill(area);
        }
        if (this.stroke != null && this.outlinePaint != null) {
            g2.setPaint(this.outlinePaint);
            g2.setStroke(this.stroke);
            g2.draw(area);
        }
        this.addEntity(info, area, rendererIndex, this.getToolTipText(), this.getURL());
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof XYPolygonAnnotation)) {
            return false;
        }
        final XYPolygonAnnotation that = (XYPolygonAnnotation)obj;
        return Arrays.equals(this.polygon, that.polygon) && ObjectUtilities.equal((Object)this.stroke, (Object)that.stroke) && PaintUtilities.equal(this.outlinePaint, that.outlinePaint) && PaintUtilities.equal(this.fillPaint, that.fillPaint);
    }
    
    public int hashCode() {
        int result = 193;
        result = 37 * result + hashCodeForDoubleArray(this.polygon);
        result = 37 * result + hashCodeForPaint(this.fillPaint);
        result = 37 * result + hashCodeForPaint(this.outlinePaint);
        if (this.stroke != null) {
            result = 37 * result + this.stroke.hashCode();
        }
        return result;
    }
    
    private static int hashCodeForPaint(final Paint p) {
        if (p == null) {
            return 0;
        }
        int result = 0;
        if (p instanceof GradientPaint) {
            final GradientPaint gp = (GradientPaint)p;
            result = 193;
            result = 37 * result + gp.getColor1().hashCode();
            result = 37 * result + gp.getPoint1().hashCode();
            result = 37 * result + gp.getColor2().hashCode();
            result = 37 * result + gp.getPoint2().hashCode();
        }
        else {
            result = p.hashCode();
        }
        return result;
    }
    
    private static int hashCodeForDoubleArray(final double[] a) {
        if (a == null) {
            return 0;
        }
        int result = 0;
        for (int i = 0; i < a.length; ++i) {
            final long temp = Double.doubleToLongBits(a[i]);
            result = (int)(temp ^ temp >>> 32);
        }
        return result;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.stroke, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writePaint(this.fillPaint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.stroke = SerialUtilities.readStroke(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.fillPaint = SerialUtilities.readPaint(stream);
    }
}
