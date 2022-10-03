package java.awt;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import sun.util.logging.PlatformLogger;
import java.io.Serializable;

public class ContainerOrderFocusTraversalPolicy extends FocusTraversalPolicy implements Serializable
{
    private static final PlatformLogger log;
    private final int FORWARD_TRAVERSAL = 0;
    private final int BACKWARD_TRAVERSAL = 1;
    private static final long serialVersionUID = 486933713763926351L;
    private boolean implicitDownCycleTraversal;
    private transient Container cachedRoot;
    private transient List<Component> cachedCycle;
    
    public ContainerOrderFocusTraversalPolicy() {
        this.implicitDownCycleTraversal = true;
    }
    
    private List<Component> getFocusTraversalCycle(final Container container) {
        final ArrayList list = new ArrayList();
        this.enumerateCycle(container, list);
        return list;
    }
    
    private int getComponentIndex(final List<Component> list, final Component component) {
        return list.indexOf(component);
    }
    
    private void enumerateCycle(final Container container, final List<Component> list) {
        if (!container.isVisible() || !container.isDisplayable()) {
            return;
        }
        list.add(container);
        final Component[] components = container.getComponents();
        for (int i = 0; i < components.length; ++i) {
            final Component component = components[i];
            if (component instanceof Container) {
                final Container container2 = (Container)component;
                if (!container2.isFocusCycleRoot() && !container2.isFocusTraversalPolicyProvider()) {
                    this.enumerateCycle(container2, list);
                    continue;
                }
            }
            list.add(component);
        }
    }
    
    private Container getTopmostProvider(final Container container, final Component component) {
        Container container2 = component.getParent();
        Container container3 = null;
        while (container2 != container && container2 != null) {
            if (container2.isFocusTraversalPolicyProvider()) {
                container3 = container2;
            }
            container2 = container2.getParent();
        }
        if (container2 == null) {
            return null;
        }
        return container3;
    }
    
    private Component getComponentDownCycle(final Component component, final int n) {
        Object defaultComponent = null;
        if (component instanceof Container) {
            final Container container = (Container)component;
            if (container.isFocusCycleRoot()) {
                if (!this.getImplicitDownCycleTraversal()) {
                    return null;
                }
                defaultComponent = container.getFocusTraversalPolicy().getDefaultComponent(container);
                if (defaultComponent != null && ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
                    ContainerOrderFocusTraversalPolicy.log.fine("### Transfered focus down-cycle to " + defaultComponent + " in the focus cycle root " + container);
                }
            }
            else if (container.isFocusTraversalPolicyProvider()) {
                defaultComponent = ((n == 0) ? container.getFocusTraversalPolicy().getDefaultComponent(container) : container.getFocusTraversalPolicy().getLastComponent(container));
                if (defaultComponent != null && ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
                    ContainerOrderFocusTraversalPolicy.log.fine("### Transfered focus to " + defaultComponent + " in the FTP provider " + container);
                }
            }
        }
        return (Component)defaultComponent;
    }
    
    @Override
    public Component getComponentAfter(final Container cachedRoot, Component component) {
        if (ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
            ContainerOrderFocusTraversalPolicy.log.fine("### Searching in " + cachedRoot + " for component after " + component);
        }
        if (cachedRoot == null || component == null) {
            throw new IllegalArgumentException("aContainer and aComponent cannot be null");
        }
        if (!cachedRoot.isFocusTraversalPolicyProvider() && !cachedRoot.isFocusCycleRoot()) {
            throw new IllegalArgumentException("aContainer should be focus cycle root or focus traversal policy provider");
        }
        if (cachedRoot.isFocusCycleRoot() && !component.isFocusCycleRoot(cachedRoot)) {
            throw new IllegalArgumentException("aContainer is not a focus cycle root of aComponent");
        }
        synchronized (cachedRoot.getTreeLock()) {
            if (!cachedRoot.isVisible() || !cachedRoot.isDisplayable()) {
                return null;
            }
            final Component componentDownCycle = this.getComponentDownCycle(component, 0);
            if (componentDownCycle != null) {
                return componentDownCycle;
            }
            final Container topmostProvider = this.getTopmostProvider(cachedRoot, component);
            if (topmostProvider != null) {
                if (ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
                    ContainerOrderFocusTraversalPolicy.log.fine("### Asking FTP " + topmostProvider + " for component after " + component);
                }
                final Component componentAfter = topmostProvider.getFocusTraversalPolicy().getComponentAfter(topmostProvider, component);
                if (componentAfter != null) {
                    if (ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
                        ContainerOrderFocusTraversalPolicy.log.fine("### FTP returned " + componentAfter);
                    }
                    return componentAfter;
                }
                component = topmostProvider;
            }
            final List<Component> focusTraversalCycle = this.getFocusTraversalCycle(cachedRoot);
            if (ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
                ContainerOrderFocusTraversalPolicy.log.fine("### Cycle is " + focusTraversalCycle + ", component is " + component);
            }
            int i = this.getComponentIndex(focusTraversalCycle, component);
            if (i < 0) {
                if (ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
                    ContainerOrderFocusTraversalPolicy.log.fine("### Didn't find component " + component + " in a cycle " + cachedRoot);
                }
                return this.getFirstComponent(cachedRoot);
            }
            ++i;
            while (i < focusTraversalCycle.size()) {
                final Component component2 = focusTraversalCycle.get(i);
                if (this.accept(component2)) {
                    return component2;
                }
                final Component componentDownCycle2;
                if ((componentDownCycle2 = this.getComponentDownCycle(component2, 0)) != null) {
                    return componentDownCycle2;
                }
                ++i;
            }
            if (cachedRoot.isFocusCycleRoot()) {
                this.cachedRoot = cachedRoot;
                this.cachedCycle = focusTraversalCycle;
                final Component firstComponent = this.getFirstComponent(cachedRoot);
                this.cachedRoot = null;
                this.cachedCycle = null;
                return firstComponent;
            }
        }
        return null;
    }
    
    @Override
    public Component getComponentBefore(final Container cachedRoot, Component component) {
        if (cachedRoot == null || component == null) {
            throw new IllegalArgumentException("aContainer and aComponent cannot be null");
        }
        if (!cachedRoot.isFocusTraversalPolicyProvider() && !cachedRoot.isFocusCycleRoot()) {
            throw new IllegalArgumentException("aContainer should be focus cycle root or focus traversal policy provider");
        }
        if (cachedRoot.isFocusCycleRoot() && !component.isFocusCycleRoot(cachedRoot)) {
            throw new IllegalArgumentException("aContainer is not a focus cycle root of aComponent");
        }
        synchronized (cachedRoot.getTreeLock()) {
            if (!cachedRoot.isVisible() || !cachedRoot.isDisplayable()) {
                return null;
            }
            final Container topmostProvider = this.getTopmostProvider(cachedRoot, component);
            if (topmostProvider != null) {
                if (ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
                    ContainerOrderFocusTraversalPolicy.log.fine("### Asking FTP " + topmostProvider + " for component after " + component);
                }
                final Component componentBefore = topmostProvider.getFocusTraversalPolicy().getComponentBefore(topmostProvider, component);
                if (componentBefore != null) {
                    if (ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
                        ContainerOrderFocusTraversalPolicy.log.fine("### FTP returned " + componentBefore);
                    }
                    return componentBefore;
                }
                component = topmostProvider;
                if (this.accept(component)) {
                    return component;
                }
            }
            final List<Component> focusTraversalCycle = this.getFocusTraversalCycle(cachedRoot);
            if (ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
                ContainerOrderFocusTraversalPolicy.log.fine("### Cycle is " + focusTraversalCycle + ", component is " + component);
            }
            int i = this.getComponentIndex(focusTraversalCycle, component);
            if (i < 0) {
                if (ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
                    ContainerOrderFocusTraversalPolicy.log.fine("### Didn't find component " + component + " in a cycle " + cachedRoot);
                }
                return this.getLastComponent(cachedRoot);
            }
            --i;
            while (i >= 0) {
                final Component component2 = focusTraversalCycle.get(i);
                final Component componentDownCycle;
                if (component2 != cachedRoot && (componentDownCycle = this.getComponentDownCycle(component2, 1)) != null) {
                    return componentDownCycle;
                }
                if (this.accept(component2)) {
                    return component2;
                }
                --i;
            }
            if (cachedRoot.isFocusCycleRoot()) {
                this.cachedRoot = cachedRoot;
                this.cachedCycle = focusTraversalCycle;
                final Component lastComponent = this.getLastComponent(cachedRoot);
                this.cachedRoot = null;
                this.cachedCycle = null;
                return lastComponent;
            }
        }
        return null;
    }
    
    @Override
    public Component getFirstComponent(final Container container) {
        if (ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
            ContainerOrderFocusTraversalPolicy.log.fine("### Getting first component in " + container);
        }
        if (container == null) {
            throw new IllegalArgumentException("aContainer cannot be null");
        }
        synchronized (container.getTreeLock()) {
            if (!container.isVisible() || !container.isDisplayable()) {
                return null;
            }
            List<Component> list;
            if (this.cachedRoot == container) {
                list = this.cachedCycle;
            }
            else {
                list = this.getFocusTraversalCycle(container);
            }
            if (list.size() == 0) {
                if (ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
                    ContainerOrderFocusTraversalPolicy.log.fine("### Cycle is empty");
                }
                return null;
            }
            if (ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
                ContainerOrderFocusTraversalPolicy.log.fine("### Cycle is " + list);
            }
            for (final Component component : list) {
                if (this.accept(component)) {
                    return component;
                }
                final Component componentDownCycle;
                if (component != container && (componentDownCycle = this.getComponentDownCycle(component, 0)) != null) {
                    return componentDownCycle;
                }
            }
        }
        return null;
    }
    
    @Override
    public Component getLastComponent(final Container container) {
        if (ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
            ContainerOrderFocusTraversalPolicy.log.fine("### Getting last component in " + container);
        }
        if (container == null) {
            throw new IllegalArgumentException("aContainer cannot be null");
        }
        synchronized (container.getTreeLock()) {
            if (!container.isVisible() || !container.isDisplayable()) {
                return null;
            }
            List<Component> list;
            if (this.cachedRoot == container) {
                list = this.cachedCycle;
            }
            else {
                list = this.getFocusTraversalCycle(container);
            }
            if (list.size() == 0) {
                if (ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
                    ContainerOrderFocusTraversalPolicy.log.fine("### Cycle is empty");
                }
                return null;
            }
            if (ContainerOrderFocusTraversalPolicy.log.isLoggable(PlatformLogger.Level.FINE)) {
                ContainerOrderFocusTraversalPolicy.log.fine("### Cycle is " + list);
            }
            for (int i = list.size() - 1; i >= 0; --i) {
                final Component component = list.get(i);
                if (this.accept(component)) {
                    return component;
                }
                if (component instanceof Container && component != container) {
                    final Container container2 = (Container)component;
                    if (container2.isFocusTraversalPolicyProvider()) {
                        final Component lastComponent = container2.getFocusTraversalPolicy().getLastComponent(container2);
                        if (lastComponent != null) {
                            return lastComponent;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public Component getDefaultComponent(final Container container) {
        return this.getFirstComponent(container);
    }
    
    public void setImplicitDownCycleTraversal(final boolean implicitDownCycleTraversal) {
        this.implicitDownCycleTraversal = implicitDownCycleTraversal;
    }
    
    public boolean getImplicitDownCycleTraversal() {
        return this.implicitDownCycleTraversal;
    }
    
    protected boolean accept(final Component component) {
        if (!component.canBeFocusOwner()) {
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
    
    static {
        log = PlatformLogger.getLogger("java.awt.ContainerOrderFocusTraversalPolicy");
    }
}
