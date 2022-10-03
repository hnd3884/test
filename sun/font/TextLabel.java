package sun.font;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public abstract class TextLabel
{
    public abstract Rectangle2D getVisualBounds(final float p0, final float p1);
    
    public abstract Rectangle2D getLogicalBounds(final float p0, final float p1);
    
    public abstract Rectangle2D getAlignBounds(final float p0, final float p1);
    
    public abstract Rectangle2D getItalicBounds(final float p0, final float p1);
    
    public abstract Shape getOutline(final float p0, final float p1);
    
    public abstract void draw(final Graphics2D p0, final float p1, final float p2);
    
    public Rectangle2D getVisualBounds() {
        return this.getVisualBounds(0.0f, 0.0f);
    }
    
    public Rectangle2D getLogicalBounds() {
        return this.getLogicalBounds(0.0f, 0.0f);
    }
    
    public Rectangle2D getAlignBounds() {
        return this.getAlignBounds(0.0f, 0.0f);
    }
    
    public Rectangle2D getItalicBounds() {
        return this.getItalicBounds(0.0f, 0.0f);
    }
    
    public Shape getOutline() {
        return this.getOutline(0.0f, 0.0f);
    }
    
    public void draw(final Graphics2D graphics2D) {
        this.draw(graphics2D, 0.0f, 0.0f);
    }
}
