package javax.swing;

import java.awt.AWTEvent;
import java.awt.KeyEventDispatcher;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeListener;
import java.awt.Container;
import java.awt.AWTKeyStroke;
import java.util.Set;
import java.awt.FocusTraversalPolicy;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.Component;
import java.awt.KeyboardFocusManager;

final class DelegatingDefaultFocusManager extends DefaultFocusManager
{
    private final KeyboardFocusManager delegate;
    
    DelegatingDefaultFocusManager(final KeyboardFocusManager delegate) {
        this.delegate = delegate;
        this.setDefaultFocusTraversalPolicy(this.gluePolicy);
    }
    
    KeyboardFocusManager getDelegate() {
        return this.delegate;
    }
    
    @Override
    public void processKeyEvent(final Component component, final KeyEvent keyEvent) {
        this.delegate.processKeyEvent(component, keyEvent);
    }
    
    @Override
    public void focusNextComponent(final Component component) {
        this.delegate.focusNextComponent(component);
    }
    
    @Override
    public void focusPreviousComponent(final Component component) {
        this.delegate.focusPreviousComponent(component);
    }
    
    @Override
    public Component getFocusOwner() {
        return this.delegate.getFocusOwner();
    }
    
    @Override
    public void clearGlobalFocusOwner() {
        this.delegate.clearGlobalFocusOwner();
    }
    
    @Override
    public Component getPermanentFocusOwner() {
        return this.delegate.getPermanentFocusOwner();
    }
    
    @Override
    public Window getFocusedWindow() {
        return this.delegate.getFocusedWindow();
    }
    
    @Override
    public Window getActiveWindow() {
        return this.delegate.getActiveWindow();
    }
    
    @Override
    public FocusTraversalPolicy getDefaultFocusTraversalPolicy() {
        return this.delegate.getDefaultFocusTraversalPolicy();
    }
    
    @Override
    public void setDefaultFocusTraversalPolicy(final FocusTraversalPolicy defaultFocusTraversalPolicy) {
        if (this.delegate != null) {
            this.delegate.setDefaultFocusTraversalPolicy(defaultFocusTraversalPolicy);
        }
    }
    
    @Override
    public void setDefaultFocusTraversalKeys(final int n, final Set<? extends AWTKeyStroke> set) {
        this.delegate.setDefaultFocusTraversalKeys(n, set);
    }
    
    @Override
    public Set<AWTKeyStroke> getDefaultFocusTraversalKeys(final int n) {
        return this.delegate.getDefaultFocusTraversalKeys(n);
    }
    
    @Override
    public Container getCurrentFocusCycleRoot() {
        return this.delegate.getCurrentFocusCycleRoot();
    }
    
    @Override
    public void setGlobalCurrentFocusCycleRoot(final Container globalCurrentFocusCycleRoot) {
        this.delegate.setGlobalCurrentFocusCycleRoot(globalCurrentFocusCycleRoot);
    }
    
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        this.delegate.addPropertyChangeListener(propertyChangeListener);
    }
    
    @Override
    public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        this.delegate.removePropertyChangeListener(propertyChangeListener);
    }
    
    @Override
    public void addPropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        this.delegate.addPropertyChangeListener(s, propertyChangeListener);
    }
    
    @Override
    public void removePropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        this.delegate.removePropertyChangeListener(s, propertyChangeListener);
    }
    
    @Override
    public void addVetoableChangeListener(final VetoableChangeListener vetoableChangeListener) {
        this.delegate.addVetoableChangeListener(vetoableChangeListener);
    }
    
    @Override
    public void removeVetoableChangeListener(final VetoableChangeListener vetoableChangeListener) {
        this.delegate.removeVetoableChangeListener(vetoableChangeListener);
    }
    
    @Override
    public void addVetoableChangeListener(final String s, final VetoableChangeListener vetoableChangeListener) {
        this.delegate.addVetoableChangeListener(s, vetoableChangeListener);
    }
    
    @Override
    public void removeVetoableChangeListener(final String s, final VetoableChangeListener vetoableChangeListener) {
        this.delegate.removeVetoableChangeListener(s, vetoableChangeListener);
    }
    
    @Override
    public void addKeyEventDispatcher(final KeyEventDispatcher keyEventDispatcher) {
        this.delegate.addKeyEventDispatcher(keyEventDispatcher);
    }
    
    @Override
    public void removeKeyEventDispatcher(final KeyEventDispatcher keyEventDispatcher) {
        this.delegate.removeKeyEventDispatcher(keyEventDispatcher);
    }
    
    @Override
    public boolean dispatchEvent(final AWTEvent awtEvent) {
        return this.delegate.dispatchEvent(awtEvent);
    }
    
    @Override
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        return this.delegate.dispatchKeyEvent(keyEvent);
    }
    
    @Override
    public void upFocusCycle(final Component component) {
        this.delegate.upFocusCycle(component);
    }
    
    @Override
    public void downFocusCycle(final Container container) {
        this.delegate.downFocusCycle(container);
    }
}
