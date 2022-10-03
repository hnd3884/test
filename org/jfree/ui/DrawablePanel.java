package org.jfree.ui;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.Graphics;
import javax.swing.JComponent;

public class DrawablePanel extends JComponent
{
    private Drawable drawable;
    
    public DrawablePanel() {
        this.setOpaque(false);
    }
    
    public Drawable getDrawable() {
        return this.drawable;
    }
    
    public boolean isOpaque() {
        return this.drawable != null && super.isOpaque();
    }
    
    protected void paintComponent(final Graphics g) {
        if (this.drawable == null) {
            return;
        }
        final Rectangle bounds = this.getBounds();
        final Graphics2D g2 = (Graphics2D)g.create(bounds.x, bounds.y, bounds.width, bounds.height);
        this.drawable.draw(g2, bounds);
        g2.dispose();
    }
    
    public void setDrawable(final Drawable drawable) {
        this.drawable = drawable;
    }
}
