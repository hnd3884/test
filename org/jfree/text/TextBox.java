package org.jfree.text;

import java.io.ObjectOutputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectInputStream;
import org.jfree.util.ObjectUtilities;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import org.jfree.ui.Size2D;
import org.jfree.ui.RectangleAnchor;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import org.jfree.ui.RectangleInsets;
import java.awt.Stroke;
import java.awt.Paint;
import java.io.Serializable;

public class TextBox implements Serializable
{
    private static final long serialVersionUID = 3360220213180203706L;
    private transient Paint outlinePaint;
    private transient Stroke outlineStroke;
    private RectangleInsets interiorGap;
    private transient Paint backgroundPaint;
    private transient Paint shadowPaint;
    private double shadowXOffset;
    private double shadowYOffset;
    private TextBlock textBlock;
    
    public TextBox() {
        this((TextBlock)null);
    }
    
    public TextBox(final String text) {
        this((TextBlock)null);
        if (text != null) {
            (this.textBlock = new TextBlock()).addLine(text, new Font("SansSerif", 0, 10), Color.black);
        }
    }
    
    public TextBox(final TextBlock block) {
        this.shadowXOffset = 2.0;
        this.shadowYOffset = 2.0;
        this.outlinePaint = Color.black;
        this.outlineStroke = new BasicStroke(1.0f);
        this.interiorGap = new RectangleInsets(1.0, 3.0, 1.0, 3.0);
        this.backgroundPaint = new Color(255, 255, 192);
        this.shadowPaint = Color.gray;
        this.shadowXOffset = 2.0;
        this.shadowYOffset = 2.0;
        this.textBlock = block;
    }
    
    public void draw(final Graphics2D g2, final float x, final float y, final RectangleAnchor anchor) {
        final Size2D d1 = this.textBlock.calculateDimensions(g2);
        final double w = this.interiorGap.extendWidth(d1.getWidth());
        final double h = this.interiorGap.extendHeight(d1.getHeight());
        final Size2D d2 = new Size2D(w, h);
        final Rectangle2D bounds = RectangleAnchor.createRectangle(d2, x, y, anchor);
        if (this.shadowPaint != null) {
            final Rectangle2D shadow = new Rectangle2D.Double(bounds.getX() + this.shadowXOffset, bounds.getY() + this.shadowYOffset, bounds.getWidth(), bounds.getHeight());
            g2.setPaint(this.shadowPaint);
            g2.fill(shadow);
        }
        if (this.backgroundPaint != null) {
            g2.setPaint(this.backgroundPaint);
            g2.fill(bounds);
        }
        if (this.outlinePaint != null && this.outlineStroke != null) {
            g2.setPaint(this.outlinePaint);
            g2.setStroke(this.outlineStroke);
            g2.draw(bounds);
        }
        this.textBlock.draw(g2, (float)bounds.getCenterX(), (float)bounds.getCenterY(), TextBlockAnchor.CENTER);
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TextBox)) {
            return false;
        }
        final TextBox that = (TextBox)obj;
        return ObjectUtilities.equal(this.outlinePaint, that.outlinePaint) && ObjectUtilities.equal(this.outlineStroke, that.outlineStroke) && ObjectUtilities.equal(this.interiorGap, that.interiorGap) && ObjectUtilities.equal(this.backgroundPaint, that.backgroundPaint) && ObjectUtilities.equal(this.shadowPaint, that.shadowPaint) && this.shadowXOffset == that.shadowXOffset && this.shadowYOffset == that.shadowYOffset && ObjectUtilities.equal(this.textBlock, that.textBlock);
    }
    
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }
    
    public double getHeight(final Graphics2D g2) {
        final Size2D d = this.textBlock.calculateDimensions(g2);
        return this.interiorGap.extendHeight(d.getHeight());
    }
    
    public RectangleInsets getInteriorGap() {
        return this.interiorGap;
    }
    
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }
    
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }
    
    public Paint getShadowPaint() {
        return this.shadowPaint;
    }
    
    public double getShadowXOffset() {
        return this.shadowXOffset;
    }
    
    public double getShadowYOffset() {
        return this.shadowYOffset;
    }
    
    public TextBlock getTextBlock() {
        return this.textBlock;
    }
    
    public int hashCode() {
        int result = (this.outlinePaint != null) ? this.outlinePaint.hashCode() : 0;
        result = 29 * result + ((this.outlineStroke != null) ? this.outlineStroke.hashCode() : 0);
        result = 29 * result + ((this.interiorGap != null) ? this.interiorGap.hashCode() : 0);
        result = 29 * result + ((this.backgroundPaint != null) ? this.backgroundPaint.hashCode() : 0);
        result = 29 * result + ((this.shadowPaint != null) ? this.shadowPaint.hashCode() : 0);
        long temp = (this.shadowXOffset != 0.0) ? Double.doubleToLongBits(this.shadowXOffset) : 0L;
        result = 29 * result + (int)(temp ^ temp >>> 32);
        temp = ((this.shadowYOffset != 0.0) ? Double.doubleToLongBits(this.shadowYOffset) : 0L);
        result = 29 * result + (int)(temp ^ temp >>> 32);
        result = 29 * result + ((this.textBlock != null) ? this.textBlock.hashCode() : 0);
        return result;
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.shadowPaint = SerialUtilities.readPaint(stream);
    }
    
    public void setBackgroundPaint(final Paint paint) {
        this.backgroundPaint = paint;
    }
    
    public void setInteriorGap(final RectangleInsets gap) {
        this.interiorGap = gap;
    }
    
    public void setOutlinePaint(final Paint paint) {
        this.outlinePaint = paint;
    }
    
    public void setOutlineStroke(final Stroke stroke) {
        this.outlineStroke = stroke;
    }
    
    public void setShadowPaint(final Paint paint) {
        this.shadowPaint = paint;
    }
    
    public void setShadowXOffset(final double offset) {
        this.shadowXOffset = offset;
    }
    
    public void setShadowYOffset(final double offset) {
        this.shadowYOffset = offset;
    }
    
    public void setTextBlock(final TextBlock block) {
        this.textBlock = block;
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writePaint(this.backgroundPaint, stream);
        SerialUtilities.writePaint(this.shadowPaint, stream);
    }
}
