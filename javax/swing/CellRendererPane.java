package javax.swing;

import javax.accessibility.AccessibleRole;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.LayoutManager;
import javax.accessibility.AccessibleContext;
import javax.accessibility.Accessible;
import java.awt.Container;

public class CellRendererPane extends Container implements Accessible
{
    protected AccessibleContext accessibleContext;
    
    public CellRendererPane() {
        this.accessibleContext = null;
        this.setLayout(null);
        this.setVisible(false);
    }
    
    @Override
    public void invalidate() {
    }
    
    @Override
    public void paint(final Graphics graphics) {
    }
    
    @Override
    public void update(final Graphics graphics) {
    }
    
    @Override
    protected void addImpl(final Component component, final Object o, final int n) {
        if (component.getParent() == this) {
            return;
        }
        super.addImpl(component, o, n);
    }
    
    public void paintComponent(final Graphics graphics, final Component component, final Container container, final int n, final int n2, final int n3, final int n4, final boolean b) {
        if (component == null) {
            if (container != null) {
                final Color color = graphics.getColor();
                graphics.setColor(container.getBackground());
                graphics.fillRect(n, n2, n3, n4);
                graphics.setColor(color);
            }
            return;
        }
        if (component.getParent() != this) {
            this.add(component);
        }
        component.setBounds(n, n2, n3, n4);
        if (b) {
            component.validate();
        }
        boolean b2 = false;
        if (component instanceof JComponent && ((JComponent)component).isDoubleBuffered()) {
            b2 = true;
            ((JComponent)component).setDoubleBuffered(false);
        }
        final Graphics create = graphics.create(n, n2, n3, n4);
        try {
            component.paint(create);
        }
        finally {
            create.dispose();
        }
        if (b2 && component instanceof JComponent) {
            ((JComponent)component).setDoubleBuffered(true);
        }
        component.setBounds(-n3, -n4, 0, 0);
    }
    
    public void paintComponent(final Graphics graphics, final Component component, final Container container, final int n, final int n2, final int n3, final int n4) {
        this.paintComponent(graphics, component, container, n, n2, n3, n4, false);
    }
    
    public void paintComponent(final Graphics graphics, final Component component, final Container container, final Rectangle rectangle) {
        this.paintComponent(graphics, component, container, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        this.removeAll();
        objectOutputStream.defaultWriteObject();
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleCellRendererPane();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleCellRendererPane extends AccessibleAWTContainer
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }
    }
}
