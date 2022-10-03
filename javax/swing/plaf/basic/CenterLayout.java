package javax.swing.plaf.basic;

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
            return new Dimension(preferredSize.width + insets.left + insets.right, preferredSize.height + insets.top + insets.bottom);
        }
        return new Dimension(0, 0);
    }
    
    @Override
    public Dimension minimumLayoutSize(final Container container) {
        return this.preferredLayoutSize(container);
    }
    
    @Override
    public void layoutContainer(final Container container) {
        if (container.getComponentCount() > 0) {
            final Component component = container.getComponent(0);
            final Dimension preferredSize = component.getPreferredSize();
            final int width = container.getWidth();
            final int height = container.getHeight();
            final Insets insets = container.getInsets();
            component.setBounds((width - (insets.left + insets.right) - preferredSize.width) / 2 + insets.left, (height - (insets.top + insets.bottom) - preferredSize.height) / 2 + insets.top, preferredSize.width, preferredSize.height);
        }
    }
}
