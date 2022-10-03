package javax.swing;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.Iterator;
import java.util.ListIterator;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.util.List;
import java.awt.Container;
import sun.util.logging.PlatformLogger;
import java.awt.Component;
import java.util.Comparator;

public class SortingFocusTraversalPolicy extends InternalFrameFocusTraversalPolicy
{
    private Comparator<? super Component> comparator;
    private boolean implicitDownCycleTraversal;
    private PlatformLogger log;
    private transient Container cachedRoot;
    private transient List<Component> cachedCycle;
    private static final SwingContainerOrderFocusTraversalPolicy fitnessTestPolicy;
    private final int FORWARD_TRAVERSAL = 0;
    private final int BACKWARD_TRAVERSAL = 1;
    private static final boolean legacySortingFTPEnabled;
    private static final Method legacyMergeSortMethod;
    
    protected SortingFocusTraversalPolicy() {
        this.implicitDownCycleTraversal = true;
        this.log = PlatformLogger.getLogger("javax.swing.SortingFocusTraversalPolicy");
    }
    
    public SortingFocusTraversalPolicy(final Comparator<? super Component> comparator) {
        this.implicitDownCycleTraversal = true;
        this.log = PlatformLogger.getLogger("javax.swing.SortingFocusTraversalPolicy");
        this.comparator = comparator;
    }
    
    private List<Component> getFocusTraversalCycle(final Container container) {
        final ArrayList list = new ArrayList();
        this.enumerateAndSortCycle(container, list);
        return list;
    }
    
    private int getComponentIndex(final List<Component> list, final Component component) {
        int n;
        try {
            n = Collections.binarySearch(list, component, this.comparator);
        }
        catch (final ClassCastException ex) {
            if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
                this.log.fine("### During the binary search for " + component + " the exception occurred: ", ex);
            }
            return -1;
        }
        if (n < 0) {
            n = list.indexOf(component);
        }
        return n;
    }
    
    private void enumerateAndSortCycle(final Container container, final List<Component> list) {
        if (container.isShowing()) {
            this.enumerateCycle(container, list);
            if (!SortingFocusTraversalPolicy.legacySortingFTPEnabled || !this.legacySort(list, this.comparator)) {
                Collections.sort((List<Object>)list, (Comparator<? super Object>)this.comparator);
            }
        }
    }
    
    private boolean legacySort(final List<Component> list, final Comparator<? super Component> comparator) {
        if (SortingFocusTraversalPolicy.legacyMergeSortMethod == null) {
            return false;
        }
        final Object[] array = list.toArray();
        try {
            SortingFocusTraversalPolicy.legacyMergeSortMethod.invoke(null, array, comparator);
        }
        catch (final IllegalAccessException | InvocationTargetException ex) {
            return false;
        }
        final ListIterator listIterator = list.listIterator();
        for (final Object o : array) {
            listIterator.next();
            listIterator.set(o);
        }
        return true;
    }
    
    private void enumerateCycle(final Container container, final List<Component> list) {
        if (!container.isVisible() || !container.isDisplayable()) {
            return;
        }
        list.add(container);
        for (final Component component : container.getComponents()) {
            Label_0122: {
                if (component instanceof Container) {
                    final Container container2 = (Container)component;
                    if (!container2.isFocusCycleRoot() && !container2.isFocusTraversalPolicyProvider() && (!(container2 instanceof JComponent) || !((JComponent)container2).isManagingFocus())) {
                        this.enumerateCycle(container2, list);
                        break Label_0122;
                    }
                }
                list.add(component);
            }
        }
    }
    
    Container getTopmostProvider(final Container container, final Component component) {
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
                if (defaultComponent != null && this.log.isLoggable(PlatformLogger.Level.FINE)) {
                    this.log.fine("### Transfered focus down-cycle to " + defaultComponent + " in the focus cycle root " + container);
                }
            }
            else if (container.isFocusTraversalPolicyProvider()) {
                defaultComponent = ((n == 0) ? container.getFocusTraversalPolicy().getDefaultComponent(container) : container.getFocusTraversalPolicy().getLastComponent(container));
                if (defaultComponent != null && this.log.isLoggable(PlatformLogger.Level.FINE)) {
                    this.log.fine("### Transfered focus to " + defaultComponent + " in the FTP provider " + container);
                }
            }
        }
        return (Component)defaultComponent;
    }
    
    @Override
    public Component getComponentAfter(final Container cachedRoot, Component component) {
        if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
            this.log.fine("### Searching in " + cachedRoot + " for component after " + component);
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
        final Component componentDownCycle = this.getComponentDownCycle(component, 0);
        if (componentDownCycle != null) {
            return componentDownCycle;
        }
        final Container topmostProvider = this.getTopmostProvider(cachedRoot, component);
        if (topmostProvider != null) {
            if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
                this.log.fine("### Asking FTP " + topmostProvider + " for component after " + component);
            }
            final Component componentAfter = topmostProvider.getFocusTraversalPolicy().getComponentAfter(topmostProvider, component);
            if (componentAfter != null) {
                if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
                    this.log.fine("### FTP returned " + componentAfter);
                }
                return componentAfter;
            }
            component = topmostProvider;
        }
        final List<Component> focusTraversalCycle = this.getFocusTraversalCycle(cachedRoot);
        if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
            this.log.fine("### Cycle is " + focusTraversalCycle + ", component is " + component);
        }
        int i = this.getComponentIndex(focusTraversalCycle, component);
        if (i < 0) {
            if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
                this.log.fine("### Didn't find component " + component + " in a cycle " + cachedRoot);
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
        final Container topmostProvider = this.getTopmostProvider(cachedRoot, component);
        if (topmostProvider != null) {
            if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
                this.log.fine("### Asking FTP " + topmostProvider + " for component after " + component);
            }
            final Component componentBefore = topmostProvider.getFocusTraversalPolicy().getComponentBefore(topmostProvider, component);
            if (componentBefore != null) {
                if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
                    this.log.fine("### FTP returned " + componentBefore);
                }
                return componentBefore;
            }
            component = topmostProvider;
            if (this.accept(component)) {
                return component;
            }
        }
        final List<Component> focusTraversalCycle = this.getFocusTraversalCycle(cachedRoot);
        if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
            this.log.fine("### Cycle is " + focusTraversalCycle + ", component is " + component);
        }
        int i = this.getComponentIndex(focusTraversalCycle, component);
        if (i < 0) {
            if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
                this.log.fine("### Didn't find component " + component + " in a cycle " + cachedRoot);
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
        return null;
    }
    
    @Override
    public Component getFirstComponent(final Container container) {
        if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
            this.log.fine("### Getting first component in " + container);
        }
        if (container == null) {
            throw new IllegalArgumentException("aContainer cannot be null");
        }
        List<Component> list;
        if (this.cachedRoot == container) {
            list = this.cachedCycle;
        }
        else {
            list = this.getFocusTraversalCycle(container);
        }
        if (list.size() == 0) {
            if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
                this.log.fine("### Cycle is empty");
            }
            return null;
        }
        if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
            this.log.fine("### Cycle is " + list);
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
        return null;
    }
    
    @Override
    public Component getLastComponent(final Container container) {
        if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
            this.log.fine("### Getting last component in " + container);
        }
        if (container == null) {
            throw new IllegalArgumentException("aContainer cannot be null");
        }
        List<Component> list;
        if (this.cachedRoot == container) {
            list = this.cachedCycle;
        }
        else {
            list = this.getFocusTraversalCycle(container);
        }
        if (list.size() == 0) {
            if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
                this.log.fine("### Cycle is empty");
            }
            return null;
        }
        if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
            this.log.fine("### Cycle is " + list);
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
    
    protected void setComparator(final Comparator<? super Component> comparator) {
        this.comparator = comparator;
    }
    
    protected Comparator<? super Component> getComparator() {
        return this.comparator;
    }
    
    protected boolean accept(final Component component) {
        return SortingFocusTraversalPolicy.fitnessTestPolicy.accept(component);
    }
    
    static {
        fitnessTestPolicy = new SwingContainerOrderFocusTraversalPolicy();
        legacySortingFTPEnabled = "true".equals(AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("swing.legacySortingFTPEnabled", "true")));
        legacyMergeSortMethod = (SortingFocusTraversalPolicy.legacySortingFTPEnabled ? AccessController.doPrivileged((PrivilegedAction<Method>)new PrivilegedAction<Method>() {
            @Override
            public Method run() {
                try {
                    final Method declaredMethod = Class.forName("java.util.Arrays").getDeclaredMethod("legacyMergeSort", Object[].class, Comparator.class);
                    declaredMethod.setAccessible(true);
                    return declaredMethod;
                }
                catch (final ClassNotFoundException | NoSuchMethodException ex) {
                    return null;
                }
            }
        }) : null);
    }
}
