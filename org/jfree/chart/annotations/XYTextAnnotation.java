package org.jfree.chart.annotations;

import java.awt.Color;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.util.PaintUtilities;
import java.awt.Shape;
import org.jfree.ui.RectangleEdge;
import org.jfree.text.TextUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.axis.ValueAxis;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.plot.XYPlot;
import java.awt.Graphics2D;
import org.jfree.ui.TextAnchor;
import java.awt.Paint;
import java.awt.Font;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class XYTextAnnotation extends AbstractXYAnnotation implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -2946063342782506328L;
    public static final Font DEFAULT_FONT;
    public static final Paint DEFAULT_PAINT;
    public static final TextAnchor DEFAULT_TEXT_ANCHOR;
    public static final TextAnchor DEFAULT_ROTATION_ANCHOR;
    public static final double DEFAULT_ROTATION_ANGLE = 0.0;
    private String text;
    private Font font;
    private transient Paint paint;
    private double x;
    private double y;
    private TextAnchor textAnchor;
    private TextAnchor rotationAnchor;
    private double rotationAngle;
    
    public XYTextAnnotation(final String text, final double x, final double y) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        this.text = text;
        this.font = XYTextAnnotation.DEFAULT_FONT;
        this.paint = XYTextAnnotation.DEFAULT_PAINT;
        this.x = x;
        this.y = y;
        this.textAnchor = XYTextAnnotation.DEFAULT_TEXT_ANCHOR;
        this.rotationAnchor = XYTextAnnotation.DEFAULT_ROTATION_ANCHOR;
        this.rotationAngle = 0.0;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(final String text) {
        this.text = text;
    }
    
    public Font getFont() {
        return this.font;
    }
    
    public void setFont(final Font font) {
        this.font = font;
    }
    
    public Paint getPaint() {
        return this.paint;
    }
    
    public void setPaint(final Paint paint) {
        this.paint = paint;
    }
    
    public TextAnchor getTextAnchor() {
        return this.textAnchor;
    }
    
    public void setTextAnchor(final TextAnchor anchor) {
        this.textAnchor = anchor;
    }
    
    public TextAnchor getRotationAnchor() {
        return this.rotationAnchor;
    }
    
    public void setRotationAnchor(final TextAnchor anchor) {
        this.rotationAnchor = anchor;
    }
    
    public double getRotationAngle() {
        return this.rotationAngle;
    }
    
    public void setRotationAngle(final double angle) {
        this.rotationAngle = angle;
    }
    
    public double getX() {
        return this.x;
    }
    
    public void setX(final double x) {
        this.x = x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public void setY(final double y) {
        this.y = y;
    }
    
    public void draw(final Graphics2D g2, final XYPlot plot, final Rectangle2D dataArea, final ValueAxis domainAxis, final ValueAxis rangeAxis, final int rendererIndex, final PlotRenderingInfo info) {
        final PlotOrientation orientation = plot.getOrientation();
        final RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
        final RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
        float anchorX = (float)domainAxis.valueToJava2D(this.x, dataArea, domainEdge);
        float anchorY = (float)rangeAxis.valueToJava2D(this.y, dataArea, rangeEdge);
        if (orientation == PlotOrientation.HORIZONTAL) {
            final float tempAnchor = anchorX;
            anchorX = anchorY;
            anchorY = tempAnchor;
        }
        g2.setFont(this.getFont());
        g2.setPaint(this.getPaint());
        TextUtilities.drawRotatedString(this.getText(), g2, anchorX, anchorY, this.getTextAnchor(), this.getRotationAngle(), this.getRotationAnchor());
        final Shape hotspot = TextUtilities.calculateRotatedStringBounds(this.getText(), g2, anchorX, anchorY, this.getTextAnchor(), this.getRotationAngle(), this.getRotationAnchor());
        final String toolTip = this.getToolTipText();
        final String url = this.getURL();
        if (toolTip != null || url != null) {
            this.addEntity(info, hotspot, rendererIndex, toolTip, url);
        }
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYTextAnnotation)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final XYTextAnnotation that = (XYTextAnnotation)obj;
        return this.text.equals(that.text) && this.x == that.x && this.y == that.y && this.font.equals(that.font) && PaintUtilities.equal(this.paint, that.paint) && this.rotationAnchor.equals((Object)that.rotationAnchor) && this.rotationAngle == that.rotationAngle && this.textAnchor.equals((Object)that.textAnchor);
    }
    
    public int hashCode() {
        return this.text.hashCode();
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
    }
    
    static {
        DEFAULT_FONT = new Font("SansSerif", 0, 10);
        DEFAULT_PAINT = Color.black;
        DEFAULT_TEXT_ANCHOR = TextAnchor.CENTER;
        DEFAULT_ROTATION_ANCHOR = TextAnchor.CENTER;
    }
}
