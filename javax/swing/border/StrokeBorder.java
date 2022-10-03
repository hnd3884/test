package javax.swing.border;

import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Component;
import java.beans.ConstructorProperties;
import java.awt.Paint;
import java.awt.BasicStroke;

public class StrokeBorder extends AbstractBorder
{
    private final BasicStroke stroke;
    private final Paint paint;
    
    public StrokeBorder(final BasicStroke basicStroke) {
        this(basicStroke, null);
    }
    
    @ConstructorProperties({ "stroke", "paint" })
    public StrokeBorder(final BasicStroke stroke, final Paint paint) {
        if (stroke == null) {
            throw new NullPointerException("border's stroke");
        }
        this.stroke = stroke;
        this.paint = paint;
    }
    
    @Override
    public void paintBorder(final Component component, Graphics create, final int n, final int n2, final int n3, final int n4) {
        final float lineWidth = this.stroke.getLineWidth();
        if (lineWidth > 0.0f) {
            create = create.create();
            if (create instanceof Graphics2D) {
                final Graphics2D graphics2D = (Graphics2D)create;
                graphics2D.setStroke(this.stroke);
                graphics2D.setPaint((this.paint != null) ? this.paint : ((component == null) ? null : component.getForeground()));
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics2D.draw(new Rectangle2D.Float(n + lineWidth / 2.0f, n2 + lineWidth / 2.0f, n3 - lineWidth, n4 - lineWidth));
            }
            create.dispose();
        }
    }
    
    @Override
    public Insets getBorderInsets(final Component component, final Insets insets) {
        final int n = (int)Math.ceil(this.stroke.getLineWidth());
        insets.set(n, n, n, n);
        return insets;
    }
    
    public BasicStroke getStroke() {
        return this.stroke;
    }
    
    public Paint getPaint() {
        return this.paint;
    }
}
