package javax.swing.border;

import java.awt.Insets;
import java.io.Serializable;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Component;
import java.beans.ConstructorProperties;
import java.awt.Color;

public class LineBorder extends AbstractBorder
{
    private static Border blackLine;
    private static Border grayLine;
    protected int thickness;
    protected Color lineColor;
    protected boolean roundedCorners;
    
    public static Border createBlackLineBorder() {
        if (LineBorder.blackLine == null) {
            LineBorder.blackLine = new LineBorder(Color.black, 1);
        }
        return LineBorder.blackLine;
    }
    
    public static Border createGrayLineBorder() {
        if (LineBorder.grayLine == null) {
            LineBorder.grayLine = new LineBorder(Color.gray, 1);
        }
        return LineBorder.grayLine;
    }
    
    public LineBorder(final Color color) {
        this(color, 1, false);
    }
    
    public LineBorder(final Color color, final int n) {
        this(color, n, false);
    }
    
    @ConstructorProperties({ "lineColor", "thickness", "roundedCorners" })
    public LineBorder(final Color lineColor, final int thickness, final boolean roundedCorners) {
        this.lineColor = lineColor;
        this.thickness = thickness;
        this.roundedCorners = roundedCorners;
    }
    
    @Override
    public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        if (this.thickness > 0 && graphics instanceof Graphics2D) {
            final Graphics2D graphics2D = (Graphics2D)graphics;
            final Color color = graphics2D.getColor();
            graphics2D.setColor(this.lineColor);
            final int thickness = this.thickness;
            final int n5 = thickness + thickness;
            Serializable s;
            Serializable s2;
            if (this.roundedCorners) {
                final float n6 = 0.2f * thickness;
                s = new RoundRectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)thickness, (float)thickness);
                s2 = new RoundRectangle2D.Float((float)(n + thickness), (float)(n2 + thickness), (float)(n3 - n5), (float)(n4 - n5), n6, n6);
            }
            else {
                s = new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4);
                s2 = new Rectangle2D.Float((float)(n + thickness), (float)(n2 + thickness), (float)(n3 - n5), (float)(n4 - n5));
            }
            final Path2D.Float float1 = new Path2D.Float(0);
            float1.append((Shape)s, false);
            float1.append((Shape)s2, false);
            graphics2D.fill(float1);
            graphics2D.setColor(color);
        }
    }
    
    @Override
    public Insets getBorderInsets(final Component component, final Insets insets) {
        insets.set(this.thickness, this.thickness, this.thickness, this.thickness);
        return insets;
    }
    
    public Color getLineColor() {
        return this.lineColor;
    }
    
    public int getThickness() {
        return this.thickness;
    }
    
    public boolean getRoundedCorners() {
        return this.roundedCorners;
    }
    
    @Override
    public boolean isBorderOpaque() {
        return !this.roundedCorners;
    }
}
