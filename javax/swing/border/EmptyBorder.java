package javax.swing.border;

import java.awt.Graphics;
import java.awt.Component;
import java.beans.ConstructorProperties;
import java.awt.Insets;
import java.io.Serializable;

public class EmptyBorder extends AbstractBorder implements Serializable
{
    protected int left;
    protected int right;
    protected int top;
    protected int bottom;
    
    public EmptyBorder(final int top, final int left, final int bottom, final int right) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }
    
    @ConstructorProperties({ "borderInsets" })
    public EmptyBorder(final Insets insets) {
        this.top = insets.top;
        this.right = insets.right;
        this.bottom = insets.bottom;
        this.left = insets.left;
    }
    
    @Override
    public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
    }
    
    @Override
    public Insets getBorderInsets(final Component component, final Insets insets) {
        insets.left = this.left;
        insets.top = this.top;
        insets.right = this.right;
        insets.bottom = this.bottom;
        return insets;
    }
    
    public Insets getBorderInsets() {
        return new Insets(this.top, this.left, this.bottom, this.right);
    }
    
    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
