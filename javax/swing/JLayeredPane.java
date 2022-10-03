package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import java.util.ArrayList;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Container;
import sun.awt.SunToolkit;
import java.awt.LayoutManager;
import java.awt.Component;
import java.util.Hashtable;
import javax.accessibility.Accessible;

public class JLayeredPane extends JComponent implements Accessible
{
    public static final Integer DEFAULT_LAYER;
    public static final Integer PALETTE_LAYER;
    public static final Integer MODAL_LAYER;
    public static final Integer POPUP_LAYER;
    public static final Integer DRAG_LAYER;
    public static final Integer FRAME_CONTENT_LAYER;
    public static final String LAYER_PROPERTY = "layeredContainerLayer";
    private Hashtable<Component, Integer> componentToLayer;
    private boolean optimizedDrawingPossible;
    
    public JLayeredPane() {
        this.optimizedDrawingPossible = true;
        this.setLayout(null);
    }
    
    private void validateOptimizedDrawing() {
        boolean b = false;
        synchronized (this.getTreeLock()) {
            for (final Component component : this.getComponents()) {
                Integer n = null;
                if ((SunToolkit.isInstanceOf(component, "javax.swing.JInternalFrame") || (component instanceof JComponent && (n = (Integer)((JComponent)component).getClientProperty("layeredContainerLayer")) != null)) && (n == null || !n.equals(JLayeredPane.FRAME_CONTENT_LAYER))) {
                    b = true;
                    break;
                }
            }
        }
        if (b) {
            this.optimizedDrawingPossible = false;
        }
        else {
            this.optimizedDrawingPossible = true;
        }
    }
    
    @Override
    protected void addImpl(final Component component, final Object o, final int n) {
        int n2;
        if (o instanceof Integer) {
            n2 = (int)o;
            this.setLayer(component, n2);
        }
        else {
            n2 = this.getLayer(component);
        }
        super.addImpl(component, o, this.insertIndexForLayer(n2, n));
        component.validate();
        component.repaint();
        this.validateOptimizedDrawing();
    }
    
    @Override
    public void remove(final int n) {
        final Component component = this.getComponent(n);
        super.remove(n);
        if (component != null && !(component instanceof JComponent)) {
            this.getComponentToLayer().remove(component);
        }
        this.validateOptimizedDrawing();
    }
    
    @Override
    public void removeAll() {
        final Component[] components = this.getComponents();
        final Hashtable<Component, Integer> componentToLayer = this.getComponentToLayer();
        for (int i = components.length - 1; i >= 0; --i) {
            final Component component = components[i];
            if (component != null && !(component instanceof JComponent)) {
                componentToLayer.remove(component);
            }
        }
        super.removeAll();
    }
    
    @Override
    public boolean isOptimizedDrawingEnabled() {
        return this.optimizedDrawingPossible;
    }
    
    public static void putLayer(final JComponent component, final int n) {
        component.putClientProperty("layeredContainerLayer", new Integer(n));
    }
    
    public static int getLayer(final JComponent component) {
        final Integer n;
        if ((n = (Integer)component.getClientProperty("layeredContainerLayer")) != null) {
            return n;
        }
        return JLayeredPane.DEFAULT_LAYER;
    }
    
    public static JLayeredPane getLayeredPaneAbove(final Component component) {
        if (component == null) {
            return null;
        }
        Container container;
        for (container = component.getParent(); container != null && !(container instanceof JLayeredPane); container = container.getParent()) {}
        return (JLayeredPane)container;
    }
    
    public void setLayer(final Component component, final int n) {
        this.setLayer(component, n, -1);
    }
    
    public void setLayer(final Component component, final int n, final int n2) {
        final Integer objectForLayer = this.getObjectForLayer(n);
        if (n == this.getLayer(component) && n2 == this.getPosition(component)) {
            this.repaint(component.getBounds());
            return;
        }
        if (component instanceof JComponent) {
            ((JComponent)component).putClientProperty("layeredContainerLayer", objectForLayer);
        }
        else {
            this.getComponentToLayer().put(component, objectForLayer);
        }
        if (component.getParent() == null || component.getParent() != this) {
            this.repaint(component.getBounds());
            return;
        }
        this.setComponentZOrder(component, this.insertIndexForLayer(component, n, n2));
        this.repaint(component.getBounds());
    }
    
    public int getLayer(final Component component) {
        Integer n;
        if (component instanceof JComponent) {
            n = (Integer)((JComponent)component).getClientProperty("layeredContainerLayer");
        }
        else {
            n = this.getComponentToLayer().get(component);
        }
        if (n == null) {
            return JLayeredPane.DEFAULT_LAYER;
        }
        return n;
    }
    
    public int getIndexOf(final Component component) {
        for (int componentCount = this.getComponentCount(), i = 0; i < componentCount; ++i) {
            if (component == this.getComponent(i)) {
                return i;
            }
        }
        return -1;
    }
    
    public void moveToFront(final Component component) {
        this.setPosition(component, 0);
    }
    
    public void moveToBack(final Component component) {
        this.setPosition(component, -1);
    }
    
    public void setPosition(final Component component, final int n) {
        this.setLayer(component, this.getLayer(component), n);
    }
    
    public int getPosition(final Component component) {
        int n = 0;
        this.getComponentCount();
        final int index = this.getIndexOf(component);
        if (index == -1) {
            return -1;
        }
        final int layer = this.getLayer(component);
        for (int i = index - 1; i >= 0; --i) {
            if (this.getLayer(this.getComponent(i)) != layer) {
                return n;
            }
            ++n;
        }
        return n;
    }
    
    public int highestLayer() {
        if (this.getComponentCount() > 0) {
            return this.getLayer(this.getComponent(0));
        }
        return 0;
    }
    
    public int lowestLayer() {
        final int componentCount = this.getComponentCount();
        if (componentCount > 0) {
            return this.getLayer(this.getComponent(componentCount - 1));
        }
        return 0;
    }
    
    public int getComponentCountInLayer(final int n) {
        int n2 = 0;
        for (int componentCount = this.getComponentCount(), i = 0; i < componentCount; ++i) {
            final int layer = this.getLayer(this.getComponent(i));
            if (layer == n) {
                ++n2;
            }
            else {
                if (n2 > 0) {
                    break;
                }
                if (layer < n) {
                    break;
                }
            }
        }
        return n2;
    }
    
    public Component[] getComponentsInLayer(final int n) {
        int n2 = 0;
        final Component[] array = new Component[this.getComponentCountInLayer(n)];
        for (int componentCount = this.getComponentCount(), i = 0; i < componentCount; ++i) {
            final int layer = this.getLayer(this.getComponent(i));
            if (layer == n) {
                array[n2++] = this.getComponent(i);
            }
            else {
                if (n2 > 0) {
                    break;
                }
                if (layer < n) {
                    break;
                }
            }
        }
        return array;
    }
    
    @Override
    public void paint(final Graphics graphics) {
        if (this.isOpaque()) {
            final Rectangle clipBounds = graphics.getClipBounds();
            Color color = this.getBackground();
            if (color == null) {
                color = Color.lightGray;
            }
            graphics.setColor(color);
            if (clipBounds != null) {
                graphics.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
            }
            else {
                graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
        }
        super.paint(graphics);
    }
    
    protected Hashtable<Component, Integer> getComponentToLayer() {
        if (this.componentToLayer == null) {
            this.componentToLayer = new Hashtable<Component, Integer>(4);
        }
        return this.componentToLayer;
    }
    
    protected Integer getObjectForLayer(final int n) {
        Integer n2 = null;
        switch (n) {
            case 0: {
                n2 = JLayeredPane.DEFAULT_LAYER;
                break;
            }
            case 100: {
                n2 = JLayeredPane.PALETTE_LAYER;
                break;
            }
            case 200: {
                n2 = JLayeredPane.MODAL_LAYER;
                break;
            }
            case 300: {
                n2 = JLayeredPane.POPUP_LAYER;
                break;
            }
            case 400: {
                n2 = JLayeredPane.DRAG_LAYER;
                break;
            }
            default: {
                n2 = new Integer(n);
                break;
            }
        }
        return n2;
    }
    
    protected int insertIndexForLayer(final int n, final int n2) {
        return this.insertIndexForLayer(null, n, n2);
    }
    
    private int insertIndexForLayer(final Component component, final int n, final int n2) {
        int n3 = -1;
        int n4 = -1;
        final int componentCount = this.getComponentCount();
        final ArrayList list = new ArrayList<Component>(componentCount);
        for (int i = 0; i < componentCount; ++i) {
            if (this.getComponent(i) != component) {
                list.add(this.getComponent(i));
            }
        }
        final int size = list.size();
        int j = 0;
        while (j < size) {
            final int layer = this.getLayer(list.get(j));
            if (n3 == -1 && layer == n) {
                n3 = j;
            }
            if (layer < n) {
                if (j == 0) {
                    n3 = 0;
                    n4 = 0;
                    break;
                }
                n4 = j;
                break;
            }
            else {
                ++j;
            }
        }
        if (n3 == -1 && n4 == -1) {
            return size;
        }
        if (n3 != -1 && n4 == -1) {
            n4 = size;
        }
        if (n4 != -1 && n3 == -1) {
            n3 = n4;
        }
        if (n2 == -1) {
            return n4;
        }
        if (n2 > -1 && n3 + n2 <= n4) {
            return n3 + n2;
        }
        return n4;
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",optimizedDrawingPossible=" + (this.optimizedDrawingPossible ? "true" : "false");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJLayeredPane();
        }
        return this.accessibleContext;
    }
    
    static {
        DEFAULT_LAYER = new Integer(0);
        PALETTE_LAYER = new Integer(100);
        MODAL_LAYER = new Integer(200);
        POPUP_LAYER = new Integer(300);
        DRAG_LAYER = new Integer(400);
        FRAME_CONTENT_LAYER = new Integer(-30000);
    }
    
    protected class AccessibleJLayeredPane extends AccessibleJComponent
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.LAYERED_PANE;
        }
    }
}
