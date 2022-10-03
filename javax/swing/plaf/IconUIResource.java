package javax.swing.plaf;

import java.awt.Graphics;
import java.awt.Component;
import java.io.Serializable;
import javax.swing.Icon;

public class IconUIResource implements Icon, UIResource, Serializable
{
    private Icon delegate;
    
    public IconUIResource(final Icon delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("null delegate icon argument");
        }
        this.delegate = delegate;
    }
    
    @Override
    public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
        this.delegate.paintIcon(component, graphics, n, n2);
    }
    
    @Override
    public int getIconWidth() {
        return this.delegate.getIconWidth();
    }
    
    @Override
    public int getIconHeight() {
        return this.delegate.getIconHeight();
    }
}
