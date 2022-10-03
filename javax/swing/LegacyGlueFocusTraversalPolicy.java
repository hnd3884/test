package javax.swing;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.Window;
import java.util.HashSet;
import java.awt.Container;
import java.awt.Component;
import java.util.HashMap;
import java.io.Serializable;
import java.awt.FocusTraversalPolicy;

final class LegacyGlueFocusTraversalPolicy extends FocusTraversalPolicy implements Serializable
{
    private transient FocusTraversalPolicy delegatePolicy;
    private transient DefaultFocusManager delegateManager;
    private HashMap<Component, Component> forwardMap;
    private HashMap<Component, Component> backwardMap;
    
    LegacyGlueFocusTraversalPolicy(final FocusTraversalPolicy delegatePolicy) {
        this.forwardMap = new HashMap<Component, Component>();
        this.backwardMap = new HashMap<Component, Component>();
        this.delegatePolicy = delegatePolicy;
    }
    
    LegacyGlueFocusTraversalPolicy(final DefaultFocusManager delegateManager) {
        this.forwardMap = new HashMap<Component, Component>();
        this.backwardMap = new HashMap<Component, Component>();
        this.delegateManager = delegateManager;
    }
    
    void setNextFocusableComponent(final Component component, final Component component2) {
        this.forwardMap.put(component, component2);
        this.backwardMap.put(component2, component);
    }
    
    void unsetNextFocusableComponent(final Component component, final Component component2) {
        this.forwardMap.remove(component);
        this.backwardMap.remove(component2);
    }
    
    @Override
    public Component getComponentAfter(final Container container, final Component component) {
        Component component2 = component;
        final HashSet set = new HashSet();
        do {
            final Component component3 = component2;
            component2 = this.forwardMap.get(component2);
            if (component2 == null) {
                if (this.delegatePolicy != null && component3.isFocusCycleRoot(container)) {
                    return this.delegatePolicy.getComponentAfter(container, component3);
                }
                if (this.delegateManager != null) {
                    return this.delegateManager.getComponentAfter(container, component);
                }
                return null;
            }
            else {
                if (set.contains(component2)) {
                    return null;
                }
                set.add(component2);
            }
        } while (!this.accept(component2));
        return component2;
    }
    
    @Override
    public Component getComponentBefore(final Container container, final Component component) {
        Component component2 = component;
        final HashSet set = new HashSet();
        do {
            final Component component3 = component2;
            component2 = this.backwardMap.get(component2);
            if (component2 == null) {
                if (this.delegatePolicy != null && component3.isFocusCycleRoot(container)) {
                    return this.delegatePolicy.getComponentBefore(container, component3);
                }
                if (this.delegateManager != null) {
                    return this.delegateManager.getComponentBefore(container, component);
                }
                return null;
            }
            else {
                if (set.contains(component2)) {
                    return null;
                }
                set.add(component2);
            }
        } while (!this.accept(component2));
        return component2;
    }
    
    @Override
    public Component getFirstComponent(final Container container) {
        if (this.delegatePolicy != null) {
            return this.delegatePolicy.getFirstComponent(container);
        }
        if (this.delegateManager != null) {
            return this.delegateManager.getFirstComponent(container);
        }
        return null;
    }
    
    @Override
    public Component getLastComponent(final Container container) {
        if (this.delegatePolicy != null) {
            return this.delegatePolicy.getLastComponent(container);
        }
        if (this.delegateManager != null) {
            return this.delegateManager.getLastComponent(container);
        }
        return null;
    }
    
    @Override
    public Component getDefaultComponent(final Container container) {
        if (this.delegatePolicy != null) {
            return this.delegatePolicy.getDefaultComponent(container);
        }
        return this.getFirstComponent(container);
    }
    
    private boolean accept(final Component component) {
        if (!component.isVisible() || !component.isDisplayable() || !component.isFocusable() || !component.isEnabled()) {
            return false;
        }
        if (!(component instanceof Window)) {
            for (Container container = component.getParent(); container != null; container = container.getParent()) {
                if (!container.isEnabled() && !container.isLightweight()) {
                    return false;
                }
                if (container instanceof Window) {
                    break;
                }
            }
        }
        return true;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.delegatePolicy instanceof Serializable) {
            objectOutputStream.writeObject(this.delegatePolicy);
        }
        else {
            objectOutputStream.writeObject(null);
        }
        if (this.delegateManager instanceof Serializable) {
            objectOutputStream.writeObject(this.delegateManager);
        }
        else {
            objectOutputStream.writeObject(null);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.delegatePolicy = (FocusTraversalPolicy)objectInputStream.readObject();
        this.delegateManager = (DefaultFocusManager)objectInputStream.readObject();
    }
}
