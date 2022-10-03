package javax.swing;

import java.beans.PropertyChangeEvent;
import java.awt.event.ComponentEvent;
import java.awt.Window;
import javax.swing.event.AncestorEvent;
import java.awt.Container;
import javax.swing.event.AncestorListener;
import javax.swing.event.EventListenerList;
import java.awt.Component;
import java.io.Serializable;
import java.beans.PropertyChangeListener;
import java.awt.event.ComponentListener;

class AncestorNotifier implements ComponentListener, PropertyChangeListener, Serializable
{
    transient Component firstInvisibleAncestor;
    EventListenerList listenerList;
    JComponent root;
    
    AncestorNotifier(final JComponent root) {
        this.listenerList = new EventListenerList();
        this.addListeners(this.root = root, true);
    }
    
    void addAncestorListener(final AncestorListener ancestorListener) {
        this.listenerList.add(AncestorListener.class, ancestorListener);
    }
    
    void removeAncestorListener(final AncestorListener ancestorListener) {
        this.listenerList.remove(AncestorListener.class, ancestorListener);
    }
    
    AncestorListener[] getAncestorListeners() {
        return this.listenerList.getListeners(AncestorListener.class);
    }
    
    protected void fireAncestorAdded(final JComponent component, final int n, final Container container, final Container container2) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == AncestorListener.class) {
                ((AncestorListener)listenerList[i + 1]).ancestorAdded(new AncestorEvent(component, n, container, container2));
            }
        }
    }
    
    protected void fireAncestorRemoved(final JComponent component, final int n, final Container container, final Container container2) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == AncestorListener.class) {
                ((AncestorListener)listenerList[i + 1]).ancestorRemoved(new AncestorEvent(component, n, container, container2));
            }
        }
    }
    
    protected void fireAncestorMoved(final JComponent component, final int n, final Container container, final Container container2) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == AncestorListener.class) {
                ((AncestorListener)listenerList[i + 1]).ancestorMoved(new AncestorEvent(component, n, container, container2));
            }
        }
    }
    
    void removeAllListeners() {
        this.removeListeners(this.root);
    }
    
    void addListeners(final Component component, final boolean b) {
        this.firstInvisibleAncestor = null;
        Component parent = component;
        while (this.firstInvisibleAncestor == null) {
            if (b || parent != component) {
                parent.addComponentListener(this);
                if (parent instanceof JComponent) {
                    ((JComponent)parent).addPropertyChangeListener(this);
                }
            }
            if (!parent.isVisible() || parent.getParent() == null || parent instanceof Window) {
                this.firstInvisibleAncestor = parent;
            }
            parent = parent.getParent();
        }
        if (this.firstInvisibleAncestor instanceof Window && this.firstInvisibleAncestor.isVisible()) {
            this.firstInvisibleAncestor = null;
        }
    }
    
    void removeListeners(final Component component) {
        for (Component parent = component; parent != null; parent = parent.getParent()) {
            parent.removeComponentListener(this);
            if (parent instanceof JComponent) {
                ((JComponent)parent).removePropertyChangeListener(this);
            }
            if (parent == this.firstInvisibleAncestor) {
                break;
            }
            if (parent instanceof Window) {
                break;
            }
        }
    }
    
    @Override
    public void componentResized(final ComponentEvent componentEvent) {
    }
    
    @Override
    public void componentMoved(final ComponentEvent componentEvent) {
        final Component component = componentEvent.getComponent();
        this.fireAncestorMoved(this.root, 3, (Container)component, component.getParent());
    }
    
    @Override
    public void componentShown(final ComponentEvent componentEvent) {
        final Component component = componentEvent.getComponent();
        if (component == this.firstInvisibleAncestor) {
            this.addListeners(component, false);
            if (this.firstInvisibleAncestor == null) {
                this.fireAncestorAdded(this.root, 1, (Container)component, component.getParent());
            }
        }
    }
    
    @Override
    public void componentHidden(final ComponentEvent componentEvent) {
        final Component component = componentEvent.getComponent();
        final boolean b = this.firstInvisibleAncestor == null;
        if (!(component instanceof Window)) {
            this.removeListeners(component.getParent());
        }
        this.firstInvisibleAncestor = component;
        if (b) {
            this.fireAncestorRemoved(this.root, 2, (Container)component, component.getParent());
        }
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        final String propertyName = propertyChangeEvent.getPropertyName();
        if (propertyName != null && (propertyName.equals("parent") || propertyName.equals("ancestor"))) {
            final JComponent firstInvisibleAncestor = (JComponent)propertyChangeEvent.getSource();
            if (propertyChangeEvent.getNewValue() != null) {
                if (firstInvisibleAncestor == this.firstInvisibleAncestor) {
                    this.addListeners(firstInvisibleAncestor, false);
                    if (this.firstInvisibleAncestor == null) {
                        this.fireAncestorAdded(this.root, 1, firstInvisibleAncestor, firstInvisibleAncestor.getParent());
                    }
                }
            }
            else {
                final boolean b = this.firstInvisibleAncestor == null;
                final Container container = (Container)propertyChangeEvent.getOldValue();
                this.removeListeners(container);
                this.firstInvisibleAncestor = firstInvisibleAncestor;
                if (b) {
                    this.fireAncestorRemoved(this.root, 2, firstInvisibleAncestor, container);
                }
            }
        }
    }
}
