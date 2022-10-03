package javax.swing.colorchooser;

import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Component;
import java.io.Serializable;
import java.awt.LayoutManager;

class CenterLayout implements LayoutManager, Serializable
{
    @Override
    public void addLayoutComponent(final String s, final Component component) {
    }
    
    @Override
    public void removeLayoutComponent(final Component component) {
    }
    
    @Override
    public Dimension preferredLayoutSize(final Container container) {
        final Component component = container.getComponent(0);
        if (component != null) {
            final Dimension preferredSize = component.getPreferredSize();
            final Insets insets = container.getInsets();
            final Dimension dimension = preferredSize;
            dimension.width += insets.left + insets.right;
            final Dimension dimension2 = preferredSize;
            dimension2.height += insets.top + insets.bottom;
            return preferredSize;
        }
        return new Dimension(0, 0);
    }
    
    @Override
    public Dimension minimumLayoutSize(final Container container) {
        return this.preferredLayoutSize(container);
    }
    
    @Override
    public void layoutContainer(final Container container) {
        try {
            final Component component = container.getComponent(0);
            component.setSize(component.getPreferredSize());
            final Dimension size = component.getSize();
            final Dimension size2 = container.getSize();
            final Insets insets = container.getInsets();
            final Dimension dimension = size2;
            dimension.width -= insets.left + insets.right;
            final Dimension dimension2 = size2;
            dimension2.height -= insets.top + insets.bottom;
            component.setBounds(size2.width / 2 - size.width / 2 + insets.left, size2.height / 2 - size.height / 2 + insets.top, size.width, size.height);
        }
        catch (final Exception ex) {}
    }
}
