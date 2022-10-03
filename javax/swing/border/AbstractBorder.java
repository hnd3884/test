package javax.swing.border;

import java.awt.Rectangle;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Component;
import java.io.Serializable;

public abstract class AbstractBorder implements Border, Serializable
{
    @Override
    public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
    }
    
    @Override
    public Insets getBorderInsets(final Component component) {
        return this.getBorderInsets(component, new Insets(0, 0, 0, 0));
    }
    
    public Insets getBorderInsets(final Component component, final Insets insets) {
        final int n = 0;
        insets.bottom = n;
        insets.right = n;
        insets.top = n;
        insets.left = n;
        return insets;
    }
    
    @Override
    public boolean isBorderOpaque() {
        return false;
    }
    
    public Rectangle getInteriorRectangle(final Component component, final int n, final int n2, final int n3, final int n4) {
        return getInteriorRectangle(component, this, n, n2, n3, n4);
    }
    
    public static Rectangle getInteriorRectangle(final Component component, final Border border, final int n, final int n2, final int n3, final int n4) {
        Insets borderInsets;
        if (border != null) {
            borderInsets = border.getBorderInsets(component);
        }
        else {
            borderInsets = new Insets(0, 0, 0, 0);
        }
        return new Rectangle(n + borderInsets.left, n2 + borderInsets.top, n3 - borderInsets.right - borderInsets.left, n4 - borderInsets.top - borderInsets.bottom);
    }
    
    public int getBaseline(final Component component, final int n, final int n2) {
        if (n < 0 || n2 < 0) {
            throw new IllegalArgumentException("Width and height must be >= 0");
        }
        return -1;
    }
    
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final Component component) {
        if (component == null) {
            throw new NullPointerException("Component must be non-null");
        }
        return Component.BaselineResizeBehavior.OTHER;
    }
    
    static boolean isLeftToRight(final Component component) {
        return component.getComponentOrientation().isLeftToRight();
    }
}
