package javax.swing;

import java.awt.Point;
import java.security.AccessController;
import java.awt.Toolkit;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.awt.event.InputEvent;
import java.awt.AWTEvent;
import java.util.ArrayList;
import java.awt.event.AWTEventListener;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import javax.swing.border.Border;
import java.awt.LayoutManager;
import java.awt.Shape;
import java.awt.Rectangle;
import sun.awt.AWTAccessor;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LayerUI;
import javax.accessibility.Accessible;
import java.beans.PropertyChangeListener;
import java.awt.Component;

public final class JLayer<V extends Component> extends JComponent implements Scrollable, PropertyChangeListener, Accessible
{
    private V view;
    private LayerUI<? super V> layerUI;
    private JPanel glassPane;
    private long eventMask;
    private transient boolean isPainting;
    private transient boolean isPaintingImmediately;
    private static final LayerEventController eventController;
    
    public JLayer() {
        this(null);
    }
    
    public JLayer(final V v) {
        this(v, new LayerUI<Component>());
    }
    
    public JLayer(final V view, final LayerUI<V> ui) {
        this.setGlassPane(this.createGlassPane());
        this.setView(view);
        this.setUI(ui);
    }
    
    public V getView() {
        return this.view;
    }
    
    public void setView(final V view) {
        final Component view2 = this.getView();
        if (view2 != null) {
            super.remove(view2);
        }
        if (view != null) {
            super.addImpl(view, null, this.getComponentCount());
        }
        this.firePropertyChange("view", view2, this.view = view);
        this.revalidate();
        this.repaint();
    }
    
    public void setUI(final LayerUI<? super V> layerUI) {
        super.setUI(this.layerUI = layerUI);
    }
    
    public LayerUI<? super V> getUI() {
        return this.layerUI;
    }
    
    public JPanel getGlassPane() {
        return this.glassPane;
    }
    
    public void setGlassPane(final JPanel glassPane) {
        final JPanel glassPane2 = this.getGlassPane();
        boolean visible = false;
        if (glassPane2 != null) {
            visible = glassPane2.isVisible();
            super.remove(glassPane2);
        }
        if (glassPane != null) {
            AWTAccessor.getComponentAccessor().setMixingCutoutShape(glassPane, new Rectangle());
            glassPane.setVisible(visible);
            super.addImpl(glassPane, null, 0);
        }
        this.firePropertyChange("glassPane", glassPane2, this.glassPane = glassPane);
        this.revalidate();
        this.repaint();
    }
    
    public JPanel createGlassPane() {
        return new DefaultLayerGlassPane();
    }
    
    @Override
    public void setLayout(final LayoutManager layoutManager) {
        if (layoutManager != null) {
            throw new IllegalArgumentException("JLayer.setLayout() not supported");
        }
    }
    
    @Override
    public void setBorder(final Border border) {
        if (border != null) {
            throw new IllegalArgumentException("JLayer.setBorder() not supported");
        }
    }
    
    @Override
    protected void addImpl(final Component component, final Object o, final int n) {
        throw new UnsupportedOperationException("Adding components to JLayer is not supported, use setView() or setGlassPane() instead");
    }
    
    @Override
    public void remove(final Component component) {
        if (component == null) {
            super.remove(component);
        }
        else if (component == this.getView()) {
            this.setView(null);
        }
        else if (component == this.getGlassPane()) {
            this.setGlassPane(null);
        }
        else {
            super.remove(component);
        }
    }
    
    @Override
    public void removeAll() {
        if (this.view != null) {
            this.setView(null);
        }
        if (this.glassPane != null) {
            this.setGlassPane(null);
        }
    }
    
    @Override
    protected boolean isPaintingOrigin() {
        return true;
    }
    
    @Override
    public void paintImmediately(final int n, final int n2, final int n3, final int n4) {
        if (!this.isPaintingImmediately && this.getUI() != null) {
            this.isPaintingImmediately = true;
            try {
                this.getUI().paintImmediately(n, n2, n3, n4, (JLayer<? extends V>)this);
            }
            finally {
                this.isPaintingImmediately = false;
            }
        }
        else {
            super.paintImmediately(n, n2, n3, n4);
        }
    }
    
    @Override
    public void paint(final Graphics graphics) {
        if (!this.isPainting) {
            this.isPainting = true;
            try {
                super.paintComponent(graphics);
            }
            finally {
                this.isPainting = false;
            }
        }
        else {
            super.paint(graphics);
        }
    }
    
    @Override
    protected void paintComponent(final Graphics graphics) {
    }
    
    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false;
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (this.getUI() != null) {
            this.getUI().applyPropertyChange(propertyChangeEvent, (JLayer<? extends V>)this);
        }
    }
    
    public void setLayerEventMask(final long eventMask) {
        final long layerEventMask = this.getLayerEventMask();
        this.firePropertyChange("layerEventMask", layerEventMask, this.eventMask = eventMask);
        if (eventMask != layerEventMask) {
            this.disableEvents(layerEventMask);
            this.enableEvents(this.eventMask);
            if (this.isDisplayable()) {
                JLayer.eventController.updateAWTEventListener(layerEventMask, eventMask);
            }
        }
    }
    
    public long getLayerEventMask() {
        return this.eventMask;
    }
    
    @Override
    public void updateUI() {
        if (this.getUI() != null) {
            this.getUI().updateUI((JLayer<? extends V>)this);
        }
    }
    
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        if (this.getView() instanceof Scrollable) {
            return this.getView().getPreferredScrollableViewportSize();
        }
        return this.getPreferredSize();
    }
    
    @Override
    public int getScrollableBlockIncrement(final Rectangle rectangle, final int n, final int n2) {
        if (this.getView() instanceof Scrollable) {
            return this.getView().getScrollableBlockIncrement(rectangle, n, n2);
        }
        return (n == 1) ? rectangle.height : rectangle.width;
    }
    
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return this.getView() instanceof Scrollable && this.getView().getScrollableTracksViewportHeight();
    }
    
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return this.getView() instanceof Scrollable && this.getView().getScrollableTracksViewportWidth();
    }
    
    @Override
    public int getScrollableUnitIncrement(final Rectangle rectangle, final int n, final int n2) {
        if (this.getView() instanceof Scrollable) {
            return this.getView().getScrollableUnitIncrement(rectangle, n, n2);
        }
        return 1;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.layerUI != null) {
            this.setUI(this.layerUI);
        }
        if (this.eventMask != 0L) {
            JLayer.eventController.updateAWTEventListener(0L, this.eventMask);
        }
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        JLayer.eventController.updateAWTEventListener(0L, this.eventMask);
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        JLayer.eventController.updateAWTEventListener(this.eventMask, 0L);
    }
    
    @Override
    public void doLayout() {
        if (this.getUI() != null) {
            this.getUI().doLayout((JLayer<? extends V>)this);
        }
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJComponent() {
                @Override
                public AccessibleRole getAccessibleRole() {
                    return AccessibleRole.PANEL;
                }
            };
        }
        return this.accessibleContext;
    }
    
    static {
        eventController = new LayerEventController();
    }
    
    private static class LayerEventController implements AWTEventListener
    {
        private ArrayList<Long> layerMaskList;
        private long currentEventMask;
        private static final long ACCEPTED_EVENTS = 231487L;
        
        private LayerEventController() {
            this.layerMaskList = new ArrayList<Long>();
        }
        
        @Override
        public void eventDispatched(final AWTEvent awtEvent) {
            final Object source = awtEvent.getSource();
            if (source instanceof Component) {
                for (Component parent = (Component)source; parent != null; parent = parent.getParent()) {
                    if (parent instanceof JLayer) {
                        final JLayer layer = (JLayer)parent;
                        final LayerUI<? super Component> ui = layer.getUI();
                        if (ui != null && this.isEventEnabled(layer.getLayerEventMask(), awtEvent.getID()) && (!(awtEvent instanceof InputEvent) || !((InputEvent)awtEvent).isConsumed())) {
                            ui.eventDispatched(awtEvent, layer);
                        }
                    }
                }
            }
        }
        
        private void updateAWTEventListener(final long n, final long n2) {
            if (n != 0L) {
                this.layerMaskList.remove(n);
            }
            if (n2 != 0L) {
                this.layerMaskList.add(n2);
            }
            long n3 = 0L;
            final Iterator<Long> iterator = this.layerMaskList.iterator();
            while (iterator.hasNext()) {
                n3 |= iterator.next();
            }
            final long currentEventMask = n3 & 0x3883FL;
            if (currentEventMask == 0L) {
                this.removeAWTEventListener();
            }
            else if (this.getCurrentEventMask() != currentEventMask) {
                this.removeAWTEventListener();
                this.addAWTEventListener(currentEventMask);
            }
            this.currentEventMask = currentEventMask;
        }
        
        private long getCurrentEventMask() {
            return this.currentEventMask;
        }
        
        private void addAWTEventListener(final long n) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    Toolkit.getDefaultToolkit().addAWTEventListener(LayerEventController.this, n);
                    return null;
                }
            });
        }
        
        private void removeAWTEventListener() {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    Toolkit.getDefaultToolkit().removeAWTEventListener(LayerEventController.this);
                    return null;
                }
            });
        }
        
        private boolean isEventEnabled(final long n, final int n2) {
            return ((n & 0x1L) != 0x0L && n2 >= 100 && n2 <= 103) || ((n & 0x2L) != 0x0L && n2 >= 300 && n2 <= 301) || ((n & 0x4L) != 0x0L && n2 >= 1004 && n2 <= 1005) || ((n & 0x8L) != 0x0L && n2 >= 400 && n2 <= 402) || ((n & 0x20000L) != 0x0L && n2 == 507) || ((n & 0x20L) != 0x0L && (n2 == 503 || n2 == 506)) || ((n & 0x10L) != 0x0L && n2 != 503 && n2 != 506 && n2 != 507 && n2 >= 500 && n2 <= 507) || ((n & 0x800L) != 0x0L && n2 >= 1100 && n2 <= 1101) || ((n & 0x8000L) != 0x0L && n2 == 1400) || ((n & 0x10000L) != 0x0L && (n2 == 1401 || n2 == 1402));
        }
    }
    
    private static class DefaultLayerGlassPane extends JPanel
    {
        public DefaultLayerGlassPane() {
            this.setOpaque(false);
        }
        
        @Override
        public boolean contains(final int n, final int n2) {
            for (int i = 0; i < this.getComponentCount(); ++i) {
                final Component component = this.getComponent(i);
                final Point convertPoint = SwingUtilities.convertPoint(this, new Point(n, n2), component);
                if (component.isVisible() && component.contains(convertPoint)) {
                    return true;
                }
            }
            return (this.getMouseListeners().length != 0 || this.getMouseMotionListeners().length != 0 || this.getMouseWheelListeners().length != 0 || this.isCursorSet()) && super.contains(n, n2);
        }
    }
}
