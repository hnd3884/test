package javax.swing.plaf;

import java.awt.Dimension;
import javax.swing.JPanel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseEvent;
import java.awt.event.FocusEvent;
import javax.swing.JLayer;
import java.awt.AWTEvent;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.awt.Component;

public class LayerUI<V extends Component> extends ComponentUI implements Serializable
{
    private final PropertyChangeSupport propertyChangeSupport;
    
    public LayerUI() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        component.paint(graphics);
    }
    
    public void eventDispatched(final AWTEvent awtEvent, final JLayer<? extends V> layer) {
        if (awtEvent instanceof FocusEvent) {
            this.processFocusEvent((FocusEvent)awtEvent, layer);
        }
        else if (awtEvent instanceof MouseEvent) {
            switch (awtEvent.getID()) {
                case 500:
                case 501:
                case 502:
                case 504:
                case 505: {
                    this.processMouseEvent((MouseEvent)awtEvent, layer);
                    break;
                }
                case 503:
                case 506: {
                    this.processMouseMotionEvent((MouseEvent)awtEvent, layer);
                    break;
                }
                case 507: {
                    this.processMouseWheelEvent((MouseWheelEvent)awtEvent, layer);
                    break;
                }
            }
        }
        else if (awtEvent instanceof KeyEvent) {
            this.processKeyEvent((KeyEvent)awtEvent, layer);
        }
        else if (awtEvent instanceof ComponentEvent) {
            this.processComponentEvent((ComponentEvent)awtEvent, layer);
        }
        else if (awtEvent instanceof InputMethodEvent) {
            this.processInputMethodEvent((InputMethodEvent)awtEvent, layer);
        }
        else if (awtEvent instanceof HierarchyEvent) {
            switch (awtEvent.getID()) {
                case 1400: {
                    this.processHierarchyEvent((HierarchyEvent)awtEvent, layer);
                    break;
                }
                case 1401:
                case 1402: {
                    this.processHierarchyBoundsEvent((HierarchyEvent)awtEvent, layer);
                    break;
                }
            }
        }
    }
    
    protected void processComponentEvent(final ComponentEvent componentEvent, final JLayer<? extends V> layer) {
    }
    
    protected void processFocusEvent(final FocusEvent focusEvent, final JLayer<? extends V> layer) {
    }
    
    protected void processKeyEvent(final KeyEvent keyEvent, final JLayer<? extends V> layer) {
    }
    
    protected void processMouseEvent(final MouseEvent mouseEvent, final JLayer<? extends V> layer) {
    }
    
    protected void processMouseMotionEvent(final MouseEvent mouseEvent, final JLayer<? extends V> layer) {
    }
    
    protected void processMouseWheelEvent(final MouseWheelEvent mouseWheelEvent, final JLayer<? extends V> layer) {
    }
    
    protected void processInputMethodEvent(final InputMethodEvent inputMethodEvent, final JLayer<? extends V> layer) {
    }
    
    protected void processHierarchyEvent(final HierarchyEvent hierarchyEvent, final JLayer<? extends V> layer) {
    }
    
    protected void processHierarchyBoundsEvent(final HierarchyEvent hierarchyEvent, final JLayer<? extends V> layer) {
    }
    
    public void updateUI(final JLayer<? extends V> layer) {
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.addPropertyChangeListener((PropertyChangeListener)component);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.removePropertyChangeListener((PropertyChangeListener)component);
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        this.propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        this.propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
    }
    
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return this.propertyChangeSupport.getPropertyChangeListeners();
    }
    
    public void addPropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        this.propertyChangeSupport.addPropertyChangeListener(s, propertyChangeListener);
    }
    
    public void removePropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        this.propertyChangeSupport.removePropertyChangeListener(s, propertyChangeListener);
    }
    
    public PropertyChangeListener[] getPropertyChangeListeners(final String s) {
        return this.propertyChangeSupport.getPropertyChangeListeners(s);
    }
    
    protected void firePropertyChange(final String s, final Object o, final Object o2) {
        this.propertyChangeSupport.firePropertyChange(s, o, o2);
    }
    
    public void applyPropertyChange(final PropertyChangeEvent propertyChangeEvent, final JLayer<? extends V> layer) {
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, final int n2) {
        final JLayer layer = (JLayer)component;
        if (layer.getView() != null) {
            return layer.getView().getBaseline(n, n2);
        }
        return super.getBaseline(component, n, n2);
    }
    
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final JComponent component) {
        final JLayer layer = (JLayer)component;
        if (layer.getView() != null) {
            return layer.getView().getBaselineResizeBehavior();
        }
        return super.getBaselineResizeBehavior(component);
    }
    
    public void doLayout(final JLayer<? extends V> layer) {
        final V view = (V)layer.getView();
        if (view != null) {
            view.setBounds(0, 0, layer.getWidth(), layer.getHeight());
        }
        final JPanel glassPane = layer.getGlassPane();
        if (glassPane != null) {
            glassPane.setBounds(0, 0, layer.getWidth(), layer.getHeight());
        }
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final Component view = ((JLayer)component).getView();
        if (view != null) {
            return view.getPreferredSize();
        }
        return super.getPreferredSize(component);
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        final Component view = ((JLayer)component).getView();
        if (view != null) {
            return view.getMinimumSize();
        }
        return super.getMinimumSize(component);
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        final Component view = ((JLayer)component).getView();
        if (view != null) {
            return view.getMaximumSize();
        }
        return super.getMaximumSize(component);
    }
    
    public void paintImmediately(final int n, final int n2, final int n3, final int n4, final JLayer<? extends V> layer) {
        layer.paintImmediately(n, n2, n3, n4);
    }
}
