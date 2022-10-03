package javax.swing.border;

import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Component;
import java.beans.ConstructorProperties;

public class CompoundBorder extends AbstractBorder
{
    protected Border outsideBorder;
    protected Border insideBorder;
    
    public CompoundBorder() {
        this.outsideBorder = null;
        this.insideBorder = null;
    }
    
    @ConstructorProperties({ "outsideBorder", "insideBorder" })
    public CompoundBorder(final Border outsideBorder, final Border insideBorder) {
        this.outsideBorder = outsideBorder;
        this.insideBorder = insideBorder;
    }
    
    @Override
    public boolean isBorderOpaque() {
        return (this.outsideBorder == null || this.outsideBorder.isBorderOpaque()) && (this.insideBorder == null || this.insideBorder.isBorderOpaque());
    }
    
    @Override
    public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        int n5 = n;
        int n6 = n2;
        int n7 = n3;
        int n8 = n4;
        if (this.outsideBorder != null) {
            this.outsideBorder.paintBorder(component, graphics, n5, n6, n7, n8);
            final Insets borderInsets = this.outsideBorder.getBorderInsets(component);
            n5 += borderInsets.left;
            n6 += borderInsets.top;
            n7 = n7 - borderInsets.right - borderInsets.left;
            n8 = n8 - borderInsets.bottom - borderInsets.top;
        }
        if (this.insideBorder != null) {
            this.insideBorder.paintBorder(component, graphics, n5, n6, n7, n8);
        }
    }
    
    @Override
    public Insets getBorderInsets(final Component component, final Insets insets) {
        final int n = 0;
        insets.bottom = n;
        insets.right = n;
        insets.left = n;
        insets.top = n;
        if (this.outsideBorder != null) {
            final Insets borderInsets = this.outsideBorder.getBorderInsets(component);
            insets.top += borderInsets.top;
            insets.left += borderInsets.left;
            insets.right += borderInsets.right;
            insets.bottom += borderInsets.bottom;
        }
        if (this.insideBorder != null) {
            final Insets borderInsets2 = this.insideBorder.getBorderInsets(component);
            insets.top += borderInsets2.top;
            insets.left += borderInsets2.left;
            insets.right += borderInsets2.right;
            insets.bottom += borderInsets2.bottom;
        }
        return insets;
    }
    
    public Border getOutsideBorder() {
        return this.outsideBorder;
    }
    
    public Border getInsideBorder() {
        return this.insideBorder;
    }
}
