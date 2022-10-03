package org.jfree.chart.annotations;

import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.util.PaintUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.ui.RectangleEdge;
import java.awt.geom.AffineTransform;
import org.jfree.chart.plot.PlotOrientation;
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
import java.awt.Shape;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class XYShapeAnnotation extends AbstractXYAnnotation implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -8553218317600684041L;
    private transient Shape shape;
    private transient Stroke stroke;
    private transient Paint outlinePaint;
    private transient Paint fillPaint;
    
    public XYShapeAnnotation(final Shape shape) {
        this(shape, new BasicStroke(1.0f), Color.black);
    }
    
    public XYShapeAnnotation(final Shape shape, final Stroke stroke, final Paint outlinePaint) {
        this(shape, stroke, outlinePaint, null);
    }
    
    public XYShapeAnnotation(final Shape shape, final Stroke stroke, final Paint outlinePaint, final Paint fillPaint) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        this.shape = shape;
        this.stroke = stroke;
        this.outlinePaint = outlinePaint;
        this.fillPaint = fillPaint;
    }
    
    public void draw(final Graphics2D g2, final XYPlot plot, final Rectangle2D dataArea, final ValueAxis domainAxis, final ValueAxis rangeAxis, final int rendererIndex, final PlotRenderingInfo info) {
        final PlotOrientation orientation = plot.getOrientation();
        final RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
        final RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
        final double m02 = domainAxis.valueToJava2D(0.0, dataArea, domainEdge);
        final double m3 = rangeAxis.valueToJava2D(0.0, dataArea, rangeEdge);
        final double m4 = domainAxis.valueToJava2D(1.0, dataArea, domainEdge) - m02;
        final double m5 = rangeAxis.valueToJava2D(1.0, dataArea, rangeEdge) - m3;
        Shape s = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            final AffineTransform t1 = new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f);
            final AffineTransform t2 = new AffineTransform(m5, 0.0, 0.0, m4, m3, m02);
            s = t1.createTransformedShape(this.shape);
            s = t2.createTransformedShape(s);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            final AffineTransform t3 = new AffineTransform(m4, 0.0, 0.0, m5, m02, m3);
            s = t3.createTransformedShape(this.shape);
        }
        if (this.fillPaint != null) {
            g2.setPaint(this.fillPaint);
            g2.fill(s);
        }
        if (this.stroke != null && this.outlinePaint != null) {
            g2.setPaint(this.outlinePaint);
            g2.setStroke(this.stroke);
            g2.draw(s);
        }
        this.addEntity(info, s, rendererIndex, this.getToolTipText(), this.getURL());
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof XYShapeAnnotation)) {
            return false;
        }
        final XYShapeAnnotation that = (XYShapeAnnotation)obj;
        return this.shape.equals(that.shape) && ObjectUtilities.equal((Object)this.stroke, (Object)that.stroke) && PaintUtilities.equal(this.outlinePaint, that.outlinePaint) && PaintUtilities.equal(this.fillPaint, that.fillPaint);
    }
    
    public int hashCode() {
        return this.shape.hashCode();
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.shape, stream);
        SerialUtilities.writeStroke(this.stroke, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writePaint(this.fillPaint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.shape = SerialUtilities.readShape(stream);
        this.stroke = SerialUtilities.readStroke(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.fillPaint = SerialUtilities.readPaint(stream);
    }
}
