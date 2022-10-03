package java.awt;

import java.awt.dnd.DropTarget;
import sun.security.action.GetBooleanAction;
import sun.awt.AWTAccessor;
import sun.java2d.pipe.Region;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.Accessible;
import java.io.OptionalDataException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.beans.PropertyChangeListener;
import java.awt.event.KeyEvent;
import sun.awt.CausedFocusEvent;
import java.io.PrintWriter;
import java.io.PrintStream;
import sun.awt.SunToolkit;
import sun.awt.PeerEvent;
import javax.swing.JInternalFrame;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;
import java.awt.peer.LightweightPeer;
import java.awt.event.ContainerEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.util.ArrayList;
import sun.awt.AppContext;
import java.io.ObjectStreamField;
import java.awt.event.ContainerListener;
import java.util.Set;
import java.util.List;
import sun.util.logging.PlatformLogger;

public class Container extends Component
{
    private static final PlatformLogger log;
    private static final PlatformLogger eventLog;
    private static final Component[] EMPTY_ARRAY;
    private List<Component> component;
    LayoutManager layoutMgr;
    private LightweightDispatcher dispatcher;
    private transient FocusTraversalPolicy focusTraversalPolicy;
    private boolean focusCycleRoot;
    private boolean focusTraversalPolicyProvider;
    private transient Set<Thread> printingThreads;
    private transient boolean printing;
    transient ContainerListener containerListener;
    transient int listeningChildren;
    transient int listeningBoundsChildren;
    transient int descendantsCount;
    transient Color preserveBackgroundColor;
    private static final long serialVersionUID = 4613797578919906343L;
    static final boolean INCLUDE_SELF = true;
    static final boolean SEARCH_HEAVYWEIGHTS = true;
    private transient int numOfHWComponents;
    private transient int numOfLWComponents;
    private static final PlatformLogger mixingLog;
    private static final ObjectStreamField[] serialPersistentFields;
    private static final boolean isJavaAwtSmartInvalidate;
    private static boolean descendUnconditionallyWhenValidating;
    transient Component modalComp;
    transient AppContext modalAppContext;
    private int containerSerializedDataVersion;
    
    private static native void initIDs();
    
    public Container() {
        this.component = new ArrayList<Component>();
        this.focusCycleRoot = false;
        this.printing = false;
        this.preserveBackgroundColor = null;
        this.numOfHWComponents = 0;
        this.numOfLWComponents = 0;
        this.containerSerializedDataVersion = 1;
    }
    
    @Override
    void initializeFocusTraversalKeys() {
        this.focusTraversalKeys = new Set[4];
    }
    
    public int getComponentCount() {
        return this.countComponents();
    }
    
    @Deprecated
    public int countComponents() {
        return this.component.size();
    }
    
    public Component getComponent(final int n) {
        try {
            return this.component.get(n);
        }
        catch (final IndexOutOfBoundsException ex) {
            throw new ArrayIndexOutOfBoundsException("No such child: " + n);
        }
    }
    
    public Component[] getComponents() {
        return this.getComponents_NoClientCode();
    }
    
    final Component[] getComponents_NoClientCode() {
        return this.component.toArray(Container.EMPTY_ARRAY);
    }
    
    Component[] getComponentsSync() {
        synchronized (this.getTreeLock()) {
            return this.getComponents();
        }
    }
    
    public Insets getInsets() {
        return this.insets();
    }
    
    @Deprecated
    public Insets insets() {
        final ComponentPeer peer = this.peer;
        if (peer instanceof ContainerPeer) {
            return (Insets)((ContainerPeer)peer).getInsets().clone();
        }
        return new Insets(0, 0, 0, 0);
    }
    
    public Component add(final Component component) {
        this.addImpl(component, null, -1);
        return component;
    }
    
    public Component add(final String s, final Component component) {
        this.addImpl(component, s, -1);
        return component;
    }
    
    public Component add(final Component component, final int n) {
        this.addImpl(component, null, n);
        return component;
    }
    
    private void checkAddToSelf(final Component component) {
        if (component instanceof Container) {
            for (Container parent = this; parent != null; parent = parent.parent) {
                if (parent == component) {
                    throw new IllegalArgumentException("adding container's parent to itself");
                }
            }
        }
    }
    
    private void checkNotAWindow(final Component component) {
        if (component instanceof Window) {
            throw new IllegalArgumentException("adding a window to a container");
        }
    }
    
    private void checkAdding(final Component component, final int n) {
        this.checkTreeLock();
        final GraphicsConfiguration graphicsConfiguration = this.getGraphicsConfiguration();
        if (n > this.component.size() || n < 0) {
            throw new IllegalArgumentException("illegal component position");
        }
        if (component.parent == this && n == this.component.size()) {
            throw new IllegalArgumentException("illegal component position " + n + " should be less then " + this.component.size());
        }
        this.checkAddToSelf(component);
        this.checkNotAWindow(component);
        if (this.getContainingWindow() != component.getContainingWindow()) {
            throw new IllegalArgumentException("component and container should be in the same top-level window");
        }
        if (graphicsConfiguration != null) {
            component.checkGD(graphicsConfiguration.getDevice().getIDstring());
        }
    }
    
    private boolean removeDelicately(final Component component, final Container container, final int n) {
        this.checkTreeLock();
        final int componentZOrder = this.getComponentZOrder(component);
        final boolean removeNotifyNeeded = isRemoveNotifyNeeded(component, this, container);
        if (removeNotifyNeeded) {
            component.removeNotify();
        }
        if (container != this) {
            if (this.layoutMgr != null) {
                this.layoutMgr.removeLayoutComponent(component);
            }
            this.adjustListeningChildren(32768L, -component.numListening(32768L));
            this.adjustListeningChildren(65536L, -component.numListening(65536L));
            this.adjustDescendants(-component.countHierarchyMembers());
            component.parent = null;
            if (removeNotifyNeeded) {
                component.setGraphicsConfiguration(null);
            }
            this.component.remove(componentZOrder);
            this.invalidateIfValid();
        }
        else {
            this.component.remove(componentZOrder);
            this.component.add(n, component);
        }
        if (component.parent == null) {
            if (this.containerListener != null || (this.eventMask & 0x2L) != 0x0L || Toolkit.enabledOnToolkit(2L)) {
                this.dispatchEvent(new ContainerEvent(this, 301, component));
            }
            component.createHierarchyEvents(1400, component, this, 1L, Toolkit.enabledOnToolkit(32768L));
            if (this.peer != null && this.layoutMgr == null && this.isVisible()) {
                this.updateCursorImmediately();
            }
        }
        return removeNotifyNeeded;
    }
    
    boolean canContainFocusOwner(final Component component) {
        if (!this.isEnabled() || !this.isDisplayable() || !this.isVisible() || !this.isFocusable()) {
            return false;
        }
        if (this.isFocusCycleRoot()) {
            final FocusTraversalPolicy focusTraversalPolicy = this.getFocusTraversalPolicy();
            if (focusTraversalPolicy instanceof DefaultFocusTraversalPolicy && !((DefaultFocusTraversalPolicy)focusTraversalPolicy).accept(component)) {
                return false;
            }
        }
        synchronized (this.getTreeLock()) {
            if (this.parent != null) {
                return this.parent.canContainFocusOwner(component);
            }
        }
        return true;
    }
    
    final boolean hasHeavyweightDescendants() {
        this.checkTreeLock();
        return this.numOfHWComponents > 0;
    }
    
    final boolean hasLightweightDescendants() {
        this.checkTreeLock();
        return this.numOfLWComponents > 0;
    }
    
    Container getHeavyweightContainer() {
        this.checkTreeLock();
        if (this.peer != null && !(this.peer instanceof LightweightPeer)) {
            return this;
        }
        return this.getNativeContainer();
    }
    
    private static boolean isRemoveNotifyNeeded(final Component component, final Container container, final Container container2) {
        if (container == null) {
            return false;
        }
        if (component.peer == null) {
            return false;
        }
        if (container2.peer == null) {
            return true;
        }
        if (component.isLightweight()) {
            final boolean b = component instanceof Container;
            if (!b || (b && !((Container)component).hasHeavyweightDescendants())) {
                return false;
            }
        }
        return container.getHeavyweightContainer() != container2.getHeavyweightContainer() && !component.peer.isReparentSupported();
    }
    
    public void setComponentZOrder(final Component component, final int n) {
        synchronized (this.getTreeLock()) {
            final Container parent = component.parent;
            final int componentZOrder = this.getComponentZOrder(component);
            if (parent == this && n == componentZOrder) {
                return;
            }
            this.checkAdding(component, n);
            final boolean b = parent != null && parent.removeDelicately(component, this, n);
            this.addDelicately(component, parent, n);
            if (!b && componentZOrder != -1) {
                component.mixOnZOrderChanging(componentZOrder, n);
            }
        }
    }
    
    private void reparentTraverse(final ContainerPeer containerPeer, final Container container) {
        this.checkTreeLock();
        for (int i = 0; i < container.getComponentCount(); ++i) {
            final Component component = container.getComponent(i);
            if (component.isLightweight()) {
                if (component instanceof Container) {
                    this.reparentTraverse(containerPeer, (Container)component);
                }
            }
            else {
                component.getPeer().reparent(containerPeer);
            }
        }
    }
    
    private void reparentChild(final Component component) {
        this.checkTreeLock();
        if (component == null) {
            return;
        }
        if (component.isLightweight()) {
            if (component instanceof Container) {
                this.reparentTraverse((ContainerPeer)this.getPeer(), (Container)component);
            }
        }
        else {
            component.getPeer().reparent((ContainerPeer)this.getPeer());
        }
    }
    
    private void addDelicately(final Component component, final Container container, final int n) {
        this.checkTreeLock();
        if (container != this) {
            if (n == -1) {
                this.component.add(component);
            }
            else {
                this.component.add(n, component);
            }
            component.parent = this;
            component.setGraphicsConfiguration(this.getGraphicsConfiguration());
            this.adjustListeningChildren(32768L, component.numListening(32768L));
            this.adjustListeningChildren(65536L, component.numListening(65536L));
            this.adjustDescendants(component.countHierarchyMembers());
        }
        else if (n < this.component.size()) {
            this.component.set(n, component);
        }
        this.invalidateIfValid();
        if (this.peer != null) {
            if (component.peer == null) {
                component.addNotify();
            }
            else {
                final Container heavyweightContainer = this.getHeavyweightContainer();
                if (container.getHeavyweightContainer() != heavyweightContainer) {
                    heavyweightContainer.reparentChild(component);
                }
                component.updateZOrder();
                if (!component.isLightweight() && this.isLightweight()) {
                    component.relocateComponent();
                }
            }
        }
        if (container != this) {
            if (this.layoutMgr != null) {
                if (this.layoutMgr instanceof LayoutManager2) {
                    ((LayoutManager2)this.layoutMgr).addLayoutComponent(component, null);
                }
                else {
                    this.layoutMgr.addLayoutComponent(null, component);
                }
            }
            if (this.containerListener != null || (this.eventMask & 0x2L) != 0x0L || Toolkit.enabledOnToolkit(2L)) {
                this.dispatchEvent(new ContainerEvent(this, 300, component));
            }
            component.createHierarchyEvents(1400, component, this, 1L, Toolkit.enabledOnToolkit(32768L));
            if (component.isFocusOwner() && !component.canBeFocusOwnerRecursively()) {
                component.transferFocus();
            }
            else if (component instanceof Container) {
                final Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                if (focusOwner != null && this.isParentOf(focusOwner) && !focusOwner.canBeFocusOwnerRecursively()) {
                    focusOwner.transferFocus();
                }
            }
        }
        else {
            component.createHierarchyEvents(1400, component, this, 1400L, Toolkit.enabledOnToolkit(32768L));
        }
        if (this.peer != null && this.layoutMgr == null && this.isVisible()) {
            this.updateCursorImmediately();
        }
    }
    
    public int getComponentZOrder(final Component component) {
        if (component == null) {
            return -1;
        }
        synchronized (this.getTreeLock()) {
            if (component.parent != this) {
                return -1;
            }
            return this.component.indexOf(component);
        }
    }
    
    public void add(final Component component, final Object o) {
        this.addImpl(component, o, -1);
    }
    
    public void add(final Component component, final Object o, final int n) {
        this.addImpl(component, o, n);
    }
    
    protected void addImpl(final Component component, final Object o, final int n) {
        synchronized (this.getTreeLock()) {
            final GraphicsConfiguration graphicsConfiguration = this.getGraphicsConfiguration();
            if (n > this.component.size() || (n < 0 && n != -1)) {
                throw new IllegalArgumentException("illegal component position");
            }
            this.checkAddToSelf(component);
            this.checkNotAWindow(component);
            if (component.parent != null) {
                component.parent.remove(component);
                if (n > this.component.size()) {
                    throw new IllegalArgumentException("illegal component position");
                }
            }
            if (graphicsConfiguration != null) {
                component.checkGD(graphicsConfiguration.getDevice().getIDstring());
            }
            if (n == -1) {
                this.component.add(component);
            }
            else {
                this.component.add(n, component);
            }
            component.parent = this;
            component.setGraphicsConfiguration(graphicsConfiguration);
            this.adjustListeningChildren(32768L, component.numListening(32768L));
            this.adjustListeningChildren(65536L, component.numListening(65536L));
            this.adjustDescendants(component.countHierarchyMembers());
            this.invalidateIfValid();
            if (this.peer != null) {
                component.addNotify();
            }
            if (this.layoutMgr != null) {
                if (this.layoutMgr instanceof LayoutManager2) {
                    ((LayoutManager2)this.layoutMgr).addLayoutComponent(component, o);
                }
                else if (o instanceof String) {
                    this.layoutMgr.addLayoutComponent((String)o, component);
                }
            }
            if (this.containerListener != null || (this.eventMask & 0x2L) != 0x0L || Toolkit.enabledOnToolkit(2L)) {
                this.dispatchEvent(new ContainerEvent(this, 300, component));
            }
            component.createHierarchyEvents(1400, component, this, 1L, Toolkit.enabledOnToolkit(32768L));
            if (this.peer != null && this.layoutMgr == null && this.isVisible()) {
                this.updateCursorImmediately();
            }
        }
    }
    
    @Override
    boolean updateGraphicsData(final GraphicsConfiguration graphicsConfiguration) {
        this.checkTreeLock();
        boolean updateGraphicsData = super.updateGraphicsData(graphicsConfiguration);
        for (final Component component : this.component) {
            if (component != null) {
                updateGraphicsData |= component.updateGraphicsData(graphicsConfiguration);
            }
        }
        return updateGraphicsData;
    }
    
    @Override
    void checkGD(final String s) {
        for (final Component component : this.component) {
            if (component != null) {
                component.checkGD(s);
            }
        }
    }
    
    public void remove(final int n) {
        synchronized (this.getTreeLock()) {
            if (n < 0 || n >= this.component.size()) {
                throw new ArrayIndexOutOfBoundsException(n);
            }
            final Component component = this.component.get(n);
            if (this.peer != null) {
                component.removeNotify();
            }
            if (this.layoutMgr != null) {
                this.layoutMgr.removeLayoutComponent(component);
            }
            this.adjustListeningChildren(32768L, -component.numListening(32768L));
            this.adjustListeningChildren(65536L, -component.numListening(65536L));
            this.adjustDescendants(-component.countHierarchyMembers());
            component.parent = null;
            this.component.remove(n);
            component.setGraphicsConfiguration(null);
            this.invalidateIfValid();
            if (this.containerListener != null || (this.eventMask & 0x2L) != 0x0L || Toolkit.enabledOnToolkit(2L)) {
                this.dispatchEvent(new ContainerEvent(this, 301, component));
            }
            component.createHierarchyEvents(1400, component, this, 1L, Toolkit.enabledOnToolkit(32768L));
            if (this.peer != null && this.layoutMgr == null && this.isVisible()) {
                this.updateCursorImmediately();
            }
        }
    }
    
    public void remove(final Component component) {
        synchronized (this.getTreeLock()) {
            if (component.parent == this) {
                final int index = this.component.indexOf(component);
                if (index >= 0) {
                    this.remove(index);
                }
            }
        }
    }
    
    public void removeAll() {
        synchronized (this.getTreeLock()) {
            this.adjustListeningChildren(32768L, -this.listeningChildren);
            this.adjustListeningChildren(65536L, -this.listeningBoundsChildren);
            this.adjustDescendants(-this.descendantsCount);
            while (!this.component.isEmpty()) {
                final Component component = this.component.remove(this.component.size() - 1);
                if (this.peer != null) {
                    component.removeNotify();
                }
                if (this.layoutMgr != null) {
                    this.layoutMgr.removeLayoutComponent(component);
                }
                component.parent = null;
                component.setGraphicsConfiguration(null);
                if (this.containerListener != null || (this.eventMask & 0x2L) != 0x0L || Toolkit.enabledOnToolkit(2L)) {
                    this.dispatchEvent(new ContainerEvent(this, 301, component));
                }
                component.createHierarchyEvents(1400, component, this, 1L, Toolkit.enabledOnToolkit(32768L));
            }
            if (this.peer != null && this.layoutMgr == null && this.isVisible()) {
                this.updateCursorImmediately();
            }
            this.invalidateIfValid();
        }
    }
    
    @Override
    int numListening(final long n) {
        final int numListening = super.numListening(n);
        if (n == 32768L) {
            if (Container.eventLog.isLoggable(PlatformLogger.Level.FINE)) {
                int n2 = 0;
                final Iterator<Component> iterator = this.component.iterator();
                while (iterator.hasNext()) {
                    n2 += iterator.next().numListening(n);
                }
                if (this.listeningChildren != n2) {
                    Container.eventLog.fine("Assertion (listeningChildren == sum) failed");
                }
            }
            return this.listeningChildren + numListening;
        }
        if (n == 65536L) {
            if (Container.eventLog.isLoggable(PlatformLogger.Level.FINE)) {
                int n3 = 0;
                final Iterator<Component> iterator2 = this.component.iterator();
                while (iterator2.hasNext()) {
                    n3 += iterator2.next().numListening(n);
                }
                if (this.listeningBoundsChildren != n3) {
                    Container.eventLog.fine("Assertion (listeningBoundsChildren == sum) failed");
                }
            }
            return this.listeningBoundsChildren + numListening;
        }
        if (Container.eventLog.isLoggable(PlatformLogger.Level.FINE)) {
            Container.eventLog.fine("This code must never be reached");
        }
        return numListening;
    }
    
    void adjustListeningChildren(final long n, final int n2) {
        if (Container.eventLog.isLoggable(PlatformLogger.Level.FINE) && n != 32768L && n != 65536L && n != 98304L) {
            Container.eventLog.fine("Assertion failed");
        }
        if (n2 == 0) {
            return;
        }
        if ((n & 0x8000L) != 0x0L) {
            this.listeningChildren += n2;
        }
        if ((n & 0x10000L) != 0x0L) {
            this.listeningBoundsChildren += n2;
        }
        this.adjustListeningChildrenOnParent(n, n2);
    }
    
    void adjustDescendants(final int n) {
        if (n == 0) {
            return;
        }
        this.descendantsCount += n;
        this.adjustDecendantsOnParent(n);
    }
    
    void adjustDecendantsOnParent(final int n) {
        if (this.parent != null) {
            this.parent.adjustDescendants(n);
        }
    }
    
    @Override
    int countHierarchyMembers() {
        if (Container.log.isLoggable(PlatformLogger.Level.FINE)) {
            int n = 0;
            final Iterator<Component> iterator = this.component.iterator();
            while (iterator.hasNext()) {
                n += iterator.next().countHierarchyMembers();
            }
            if (this.descendantsCount != n) {
                Container.log.fine("Assertion (descendantsCount == sum) failed");
            }
        }
        return this.descendantsCount + 1;
    }
    
    private int getListenersCount(final int n, final boolean b) {
        this.checkTreeLock();
        if (b) {
            return this.descendantsCount;
        }
        switch (n) {
            case 1400: {
                return this.listeningChildren;
            }
            case 1401:
            case 1402: {
                return this.listeningBoundsChildren;
            }
            default: {
                return 0;
            }
        }
    }
    
    @Override
    final int createHierarchyEvents(final int n, final Component component, final Container container, final long n2, final boolean b) {
        this.checkTreeLock();
        int i;
        final int n3 = i = this.getListenersCount(n, b);
        for (int n4 = 0; i > 0; i -= this.component.get(n4).createHierarchyEvents(n, component, container, n2, b), ++n4) {}
        return n3 + super.createHierarchyEvents(n, component, container, n2, b);
    }
    
    final void createChildHierarchyEvents(final int n, final long n2, final boolean b) {
        this.checkTreeLock();
        if (this.component.isEmpty()) {
            return;
        }
        for (int i = this.getListenersCount(n, b), n3 = 0; i > 0; i -= this.component.get(n3).createHierarchyEvents(n, this, this.parent, n2, b), ++n3) {}
    }
    
    public LayoutManager getLayout() {
        return this.layoutMgr;
    }
    
    public void setLayout(final LayoutManager layoutMgr) {
        this.layoutMgr = layoutMgr;
        this.invalidateIfValid();
    }
    
    @Override
    public void doLayout() {
        this.layout();
    }
    
    @Deprecated
    @Override
    public void layout() {
        final LayoutManager layoutMgr = this.layoutMgr;
        if (layoutMgr != null) {
            layoutMgr.layoutContainer(this);
        }
    }
    
    public boolean isValidateRoot() {
        return false;
    }
    
    @Override
    void invalidateParent() {
        if (!Container.isJavaAwtSmartInvalidate || !this.isValidateRoot()) {
            super.invalidateParent();
        }
    }
    
    @Override
    public void invalidate() {
        final LayoutManager layoutMgr = this.layoutMgr;
        if (layoutMgr instanceof LayoutManager2) {
            ((LayoutManager2)layoutMgr).invalidateLayout(this);
        }
        super.invalidate();
    }
    
    @Override
    public void validate() {
        boolean visible = false;
        synchronized (this.getTreeLock()) {
            if ((!this.isValid() || Container.descendUnconditionallyWhenValidating) && this.peer != null) {
                ContainerPeer containerPeer = null;
                if (this.peer instanceof ContainerPeer) {
                    containerPeer = (ContainerPeer)this.peer;
                }
                if (containerPeer != null) {
                    containerPeer.beginValidate();
                }
                this.validateTree();
                if (containerPeer != null) {
                    containerPeer.endValidate();
                    if (!Container.descendUnconditionallyWhenValidating) {
                        visible = this.isVisible();
                    }
                }
            }
        }
        if (visible) {
            this.updateCursorImmediately();
        }
    }
    
    final void validateUnconditionally() {
        boolean visible = false;
        synchronized (this.getTreeLock()) {
            Container.descendUnconditionallyWhenValidating = true;
            this.validate();
            if (this.peer instanceof ContainerPeer) {
                visible = this.isVisible();
            }
            Container.descendUnconditionallyWhenValidating = false;
        }
        if (visible) {
            this.updateCursorImmediately();
        }
    }
    
    protected void validateTree() {
        this.checkTreeLock();
        if (!this.isValid() || Container.descendUnconditionallyWhenValidating) {
            if (this.peer instanceof ContainerPeer) {
                ((ContainerPeer)this.peer).beginLayout();
            }
            if (!this.isValid()) {
                this.doLayout();
            }
            for (int i = 0; i < this.component.size(); ++i) {
                final Component component = this.component.get(i);
                if (component instanceof Container && !(component instanceof Window) && (!component.isValid() || Container.descendUnconditionallyWhenValidating)) {
                    ((Container)component).validateTree();
                }
                else {
                    component.validate();
                }
            }
            if (this.peer instanceof ContainerPeer) {
                ((ContainerPeer)this.peer).endLayout();
            }
        }
        super.validate();
    }
    
    void invalidateTree() {
        synchronized (this.getTreeLock()) {
            for (int i = 0; i < this.component.size(); ++i) {
                final Component component = this.component.get(i);
                if (component instanceof Container) {
                    ((Container)component).invalidateTree();
                }
                else {
                    component.invalidateIfValid();
                }
            }
            this.invalidateIfValid();
        }
    }
    
    @Override
    public void setFont(final Font font) {
        final Font font2 = this.getFont();
        super.setFont(font);
        final Font font3 = this.getFont();
        if (font3 != font2 && (font2 == null || !font2.equals(font3))) {
            this.invalidateTree();
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        return this.preferredSize();
    }
    
    @Deprecated
    @Override
    public Dimension preferredSize() {
        Dimension dimension = this.prefSize;
        if (dimension == null || (!this.isPreferredSizeSet() && !this.isValid())) {
            synchronized (this.getTreeLock()) {
                this.prefSize = ((this.layoutMgr != null) ? this.layoutMgr.preferredLayoutSize(this) : super.preferredSize());
                dimension = this.prefSize;
            }
        }
        if (dimension != null) {
            return new Dimension(dimension);
        }
        return dimension;
    }
    
    @Override
    public Dimension getMinimumSize() {
        return this.minimumSize();
    }
    
    @Deprecated
    @Override
    public Dimension minimumSize() {
        Dimension dimension = this.minSize;
        if (dimension == null || (!this.isMinimumSizeSet() && !this.isValid())) {
            synchronized (this.getTreeLock()) {
                this.minSize = ((this.layoutMgr != null) ? this.layoutMgr.minimumLayoutSize(this) : super.minimumSize());
                dimension = this.minSize;
            }
        }
        if (dimension != null) {
            return new Dimension(dimension);
        }
        return dimension;
    }
    
    @Override
    public Dimension getMaximumSize() {
        Dimension dimension = this.maxSize;
        if (dimension == null || (!this.isMaximumSizeSet() && !this.isValid())) {
            synchronized (this.getTreeLock()) {
                if (this.layoutMgr instanceof LayoutManager2) {
                    this.maxSize = ((LayoutManager2)this.layoutMgr).maximumLayoutSize(this);
                }
                else {
                    this.maxSize = super.getMaximumSize();
                }
                dimension = this.maxSize;
            }
        }
        if (dimension != null) {
            return new Dimension(dimension);
        }
        return dimension;
    }
    
    @Override
    public float getAlignmentX() {
        float n;
        if (this.layoutMgr instanceof LayoutManager2) {
            synchronized (this.getTreeLock()) {
                n = ((LayoutManager2)this.layoutMgr).getLayoutAlignmentX(this);
            }
        }
        else {
            n = super.getAlignmentX();
        }
        return n;
    }
    
    @Override
    public float getAlignmentY() {
        float n;
        if (this.layoutMgr instanceof LayoutManager2) {
            synchronized (this.getTreeLock()) {
                n = ((LayoutManager2)this.layoutMgr).getLayoutAlignmentY(this);
            }
        }
        else {
            n = super.getAlignmentY();
        }
        return n;
    }
    
    @Override
    public void paint(final Graphics graphics) {
        if (this.isShowing()) {
            synchronized (this.getObjectLock()) {
                if (this.printing && this.printingThreads.contains(Thread.currentThread())) {
                    return;
                }
            }
            GraphicsCallback.PaintCallback.getInstance().runComponents(this.getComponentsSync(), graphics, 2);
        }
    }
    
    @Override
    public void update(final Graphics graphics) {
        if (this.isShowing()) {
            if (!(this.peer instanceof LightweightPeer)) {
                graphics.clearRect(0, 0, this.width, this.height);
            }
            this.paint(graphics);
        }
    }
    
    @Override
    public void print(final Graphics graphics) {
        if (this.isShowing()) {
            final Thread currentThread = Thread.currentThread();
            try {
                synchronized (this.getObjectLock()) {
                    if (this.printingThreads == null) {
                        this.printingThreads = new HashSet<Thread>();
                    }
                    this.printingThreads.add(currentThread);
                    this.printing = true;
                }
                super.print(graphics);
            }
            finally {
                synchronized (this.getObjectLock()) {
                    this.printingThreads.remove(currentThread);
                    this.printing = !this.printingThreads.isEmpty();
                }
            }
            GraphicsCallback.PrintCallback.getInstance().runComponents(this.getComponentsSync(), graphics, 2);
        }
    }
    
    public void paintComponents(final Graphics graphics) {
        if (this.isShowing()) {
            GraphicsCallback.PaintAllCallback.getInstance().runComponents(this.getComponentsSync(), graphics, 4);
        }
    }
    
    @Override
    void lightweightPaint(final Graphics graphics) {
        super.lightweightPaint(graphics);
        this.paintHeavyweightComponents(graphics);
    }
    
    @Override
    void paintHeavyweightComponents(final Graphics graphics) {
        if (this.isShowing()) {
            GraphicsCallback.PaintHeavyweightComponentsCallback.getInstance().runComponents(this.getComponentsSync(), graphics, 3);
        }
    }
    
    public void printComponents(final Graphics graphics) {
        if (this.isShowing()) {
            GraphicsCallback.PrintAllCallback.getInstance().runComponents(this.getComponentsSync(), graphics, 4);
        }
    }
    
    @Override
    void lightweightPrint(final Graphics graphics) {
        super.lightweightPrint(graphics);
        this.printHeavyweightComponents(graphics);
    }
    
    @Override
    void printHeavyweightComponents(final Graphics graphics) {
        if (this.isShowing()) {
            GraphicsCallback.PrintHeavyweightComponentsCallback.getInstance().runComponents(this.getComponentsSync(), graphics, 3);
        }
    }
    
    public synchronized void addContainerListener(final ContainerListener containerListener) {
        if (containerListener == null) {
            return;
        }
        this.containerListener = AWTEventMulticaster.add(this.containerListener, containerListener);
        this.newEventsOnly = true;
    }
    
    public synchronized void removeContainerListener(final ContainerListener containerListener) {
        if (containerListener == null) {
            return;
        }
        this.containerListener = AWTEventMulticaster.remove(this.containerListener, containerListener);
    }
    
    public synchronized ContainerListener[] getContainerListeners() {
        return this.getListeners(ContainerListener.class);
    }
    
    @Override
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        if (clazz == ContainerListener.class) {
            return AWTEventMulticaster.getListeners(this.containerListener, clazz);
        }
        return super.getListeners(clazz);
    }
    
    @Override
    boolean eventEnabled(final AWTEvent awtEvent) {
        final int id = awtEvent.getID();
        if (id == 300 || id == 301) {
            return (this.eventMask & 0x2L) != 0x0L || this.containerListener != null;
        }
        return super.eventEnabled(awtEvent);
    }
    
    @Override
    protected void processEvent(final AWTEvent awtEvent) {
        if (awtEvent instanceof ContainerEvent) {
            this.processContainerEvent((ContainerEvent)awtEvent);
            return;
        }
        super.processEvent(awtEvent);
    }
    
    protected void processContainerEvent(final ContainerEvent containerEvent) {
        final ContainerListener containerListener = this.containerListener;
        if (containerListener != null) {
            switch (containerEvent.getID()) {
                case 300: {
                    containerListener.componentAdded(containerEvent);
                    break;
                }
                case 301: {
                    containerListener.componentRemoved(containerEvent);
                    break;
                }
            }
        }
    }
    
    @Override
    void dispatchEventImpl(final AWTEvent awtEvent) {
        if (this.dispatcher != null && this.dispatcher.dispatchEvent(awtEvent)) {
            awtEvent.consume();
            if (this.peer != null) {
                this.peer.handleEvent(awtEvent);
            }
            return;
        }
        super.dispatchEventImpl(awtEvent);
        synchronized (this.getTreeLock()) {
            switch (awtEvent.getID()) {
                case 101: {
                    this.createChildHierarchyEvents(1402, 0L, Toolkit.enabledOnToolkit(65536L));
                    break;
                }
                case 100: {
                    this.createChildHierarchyEvents(1401, 0L, Toolkit.enabledOnToolkit(65536L));
                    break;
                }
            }
        }
    }
    
    void dispatchEventToSelf(final AWTEvent awtEvent) {
        super.dispatchEventImpl(awtEvent);
    }
    
    Component getMouseEventTarget(final int n, final int n2, final boolean b) {
        return this.getMouseEventTarget(n, n2, b, MouseEventTargetFilter.FILTER, false);
    }
    
    Component getDropTargetEventTarget(final int n, final int n2, final boolean b) {
        return this.getMouseEventTarget(n, n2, b, DropTargetEventTargetFilter.FILTER, true);
    }
    
    private Component getMouseEventTarget(final int n, final int n2, final boolean b, final EventTargetFilter eventTargetFilter, final boolean b2) {
        Component component = null;
        if (b2) {
            component = this.getMouseEventTargetImpl(n, n2, b, eventTargetFilter, true, b2);
        }
        if (component == null || component == this) {
            component = this.getMouseEventTargetImpl(n, n2, b, eventTargetFilter, false, b2);
        }
        return component;
    }
    
    private Component getMouseEventTargetImpl(final int n, final int n2, final boolean b, final EventTargetFilter eventTargetFilter, final boolean b2, final boolean b3) {
        synchronized (this.getTreeLock()) {
            for (int i = 0; i < this.component.size(); ++i) {
                final Component component = this.component.get(i);
                if (component != null && component.visible && ((!b2 && component.peer instanceof LightweightPeer) || (b2 && !(component.peer instanceof LightweightPeer))) && component.contains(n - component.x, n2 - component.y)) {
                    if (component instanceof Container) {
                        final Container container = (Container)component;
                        final Component mouseEventTarget = container.getMouseEventTarget(n - container.x, n2 - container.y, b, eventTargetFilter, b3);
                        if (mouseEventTarget != null) {
                            return mouseEventTarget;
                        }
                    }
                    else if (eventTargetFilter.accept(component)) {
                        return component;
                    }
                }
            }
            final boolean b4 = this.peer instanceof LightweightPeer || b;
            if (this.contains(n, n2) && b4 && eventTargetFilter.accept(this)) {
                return this;
            }
            return null;
        }
    }
    
    void proxyEnableEvents(final long n) {
        if (this.peer instanceof LightweightPeer) {
            if (this.parent != null) {
                this.parent.proxyEnableEvents(n);
            }
        }
        else if (this.dispatcher != null) {
            this.dispatcher.enableEvents(n);
        }
    }
    
    @Deprecated
    @Override
    public void deliverEvent(final Event event) {
        final Component component = this.getComponentAt(event.x, event.y);
        if (component != null && component != this) {
            event.translate(-component.x, -component.y);
            component.deliverEvent(event);
        }
        else {
            this.postEvent(event);
        }
    }
    
    @Override
    public Component getComponentAt(final int n, final int n2) {
        return this.locate(n, n2);
    }
    
    @Deprecated
    @Override
    public Component locate(final int n, final int n2) {
        if (!this.contains(n, n2)) {
            return null;
        }
        Container container = null;
        synchronized (this.getTreeLock()) {
            for (final Component component : this.component) {
                if (component.contains(n - component.x, n2 - component.y)) {
                    if (!component.isLightweight()) {
                        return component;
                    }
                    if (container != null) {
                        continue;
                    }
                    container = (Container)component;
                }
            }
        }
        return (container != null) ? container : this;
    }
    
    @Override
    public Component getComponentAt(final Point point) {
        return this.getComponentAt(point.x, point.y);
    }
    
    public Point getMousePosition(final boolean b) throws HeadlessException {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        final PointerInfo pointerInfo = AccessController.doPrivileged((PrivilegedAction<PointerInfo>)new PrivilegedAction<PointerInfo>() {
            @Override
            public PointerInfo run() {
                return MouseInfo.getPointerInfo();
            }
        });
        synchronized (this.getTreeLock()) {
            if (this.isSameOrAncestorOf(this.findUnderMouseInWindow(pointerInfo), b)) {
                return this.pointRelativeToComponent(pointerInfo.getLocation());
            }
            return null;
        }
    }
    
    @Override
    boolean isSameOrAncestorOf(final Component component, final boolean b) {
        return this == component || (b && this.isParentOf(component));
    }
    
    public Component findComponentAt(final int n, final int n2) {
        return this.findComponentAt(n, n2, true);
    }
    
    final Component findComponentAt(final int n, final int n2, final boolean b) {
        synchronized (this.getTreeLock()) {
            if (this.isRecursivelyVisible()) {
                return this.findComponentAtImpl(n, n2, b);
            }
        }
        return null;
    }
    
    final Component findComponentAtImpl(final int n, final int n2, final boolean b) {
        if (!this.contains(n, n2) || !this.visible || (!b && !this.enabled)) {
            return null;
        }
        Component child = null;
        for (final Component component : this.component) {
            final int n3 = n - component.x;
            final int n4 = n2 - component.y;
            if (!component.contains(n3, n4)) {
                continue;
            }
            if (!component.isLightweight()) {
                final Component child2 = getChildAt(component, n3, n4, b);
                if (child2 != null) {
                    return child2;
                }
                continue;
            }
            else {
                if (child != null) {
                    continue;
                }
                child = getChildAt(component, n3, n4, b);
            }
        }
        return (child != null) ? child : this;
    }
    
    private static Component getChildAt(Component component, final int n, final int n2, final boolean b) {
        if (component instanceof Container) {
            component = ((Container)component).findComponentAtImpl(n, n2, b);
        }
        else {
            component = component.getComponentAt(n, n2);
        }
        if (component != null && component.visible && (b || component.enabled)) {
            return component;
        }
        return null;
    }
    
    public Component findComponentAt(final Point point) {
        return this.findComponentAt(point.x, point.y);
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            super.addNotify();
            if (!(this.peer instanceof LightweightPeer)) {
                this.dispatcher = new LightweightDispatcher(this);
            }
            for (int i = 0; i < this.component.size(); ++i) {
                this.component.get(i).addNotify();
            }
        }
    }
    
    @Override
    public void removeNotify() {
        synchronized (this.getTreeLock()) {
            for (int i = this.component.size() - 1; i >= 0; --i) {
                final Component component = this.component.get(i);
                if (component != null) {
                    component.setAutoFocusTransferOnDisposal(false);
                    component.removeNotify();
                    component.setAutoFocusTransferOnDisposal(true);
                }
            }
            if (this.containsFocus() && KeyboardFocusManager.isAutoFocusTransferEnabledFor(this) && !this.transferFocus(false)) {
                this.transferFocusBackward(true);
            }
            if (this.dispatcher != null) {
                this.dispatcher.dispose();
                this.dispatcher = null;
            }
            super.removeNotify();
        }
    }
    
    public boolean isAncestorOf(final Component component) {
        Container container;
        if (component == null || (container = component.getParent()) == null) {
            return false;
        }
        while (container != null) {
            if (container == this) {
                return true;
            }
            container = container.getParent();
        }
        return false;
    }
    
    private void startLWModal() {
        this.modalAppContext = AppContext.getAppContext();
        final long mostRecentKeyEventTime = Toolkit.getEventQueue().getMostRecentKeyEventTime();
        final Component component = Component.isInstanceOf(this, "javax.swing.JInternalFrame") ? ((JInternalFrame)this).getMostRecentFocusOwner() : null;
        if (component != null) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().enqueueKeyEvents(mostRecentKeyEventTime, component);
        }
        final Container heavyweightContainer;
        synchronized (this.getTreeLock()) {
            heavyweightContainer = this.getHeavyweightContainer();
            if (heavyweightContainer.modalComp != null) {
                this.modalComp = heavyweightContainer.modalComp;
                heavyweightContainer.modalComp = this;
                return;
            }
            heavyweightContainer.modalComp = this;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ((EventDispatchThread)Thread.currentThread()).pumpEventsForHierarchy(new Conditional() {
                    @Override
                    public boolean evaluate() {
                        return Container.this.windowClosingException == null && heavyweightContainer.modalComp != null;
                    }
                }, Container.this);
            }
        };
        if (EventQueue.isDispatchThread()) {
            final SequencedEvent currentSequencedEvent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentSequencedEvent();
            if (currentSequencedEvent != null) {
                currentSequencedEvent.dispose();
            }
            runnable.run();
        }
        else {
            synchronized (this.getTreeLock()) {
                Toolkit.getEventQueue().postEvent(new PeerEvent(this, runnable, 1L));
                while (this.windowClosingException == null && heavyweightContainer.modalComp != null) {
                    try {
                        this.getTreeLock().wait();
                        continue;
                    }
                    catch (final InterruptedException ex) {}
                    break;
                }
            }
        }
        if (this.windowClosingException != null) {
            this.windowClosingException.fillInStackTrace();
            throw this.windowClosingException;
        }
        if (component != null) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().dequeueKeyEvents(mostRecentKeyEventTime, component);
        }
    }
    
    private void stopLWModal() {
        synchronized (this.getTreeLock()) {
            if (this.modalAppContext != null) {
                final Container heavyweightContainer = this.getHeavyweightContainer();
                if (heavyweightContainer != null) {
                    if (this.modalComp != null) {
                        heavyweightContainer.modalComp = this.modalComp;
                        this.modalComp = null;
                        return;
                    }
                    heavyweightContainer.modalComp = null;
                }
                SunToolkit.postEvent(this.modalAppContext, new PeerEvent(this, new WakingRunnable(), 1L));
            }
            EventQueue.invokeLater(new WakingRunnable());
            this.getTreeLock().notifyAll();
        }
    }
    
    @Override
    protected String paramString() {
        String s = super.paramString();
        final LayoutManager layoutMgr = this.layoutMgr;
        if (layoutMgr != null) {
            s = s + ",layout=" + layoutMgr.getClass().getName();
        }
        return s;
    }
    
    @Override
    public void list(final PrintStream printStream, final int n) {
        super.list(printStream, n);
        synchronized (this.getTreeLock()) {
            for (int i = 0; i < this.component.size(); ++i) {
                final Component component = this.component.get(i);
                if (component != null) {
                    component.list(printStream, n + 1);
                }
            }
        }
    }
    
    @Override
    public void list(final PrintWriter printWriter, final int n) {
        super.list(printWriter, n);
        synchronized (this.getTreeLock()) {
            for (int i = 0; i < this.component.size(); ++i) {
                final Component component = this.component.get(i);
                if (component != null) {
                    component.list(printWriter, n + 1);
                }
            }
        }
    }
    
    @Override
    public void setFocusTraversalKeys(final int n, final Set<? extends AWTKeyStroke> set) {
        if (n < 0 || n >= 4) {
            throw new IllegalArgumentException("invalid focus traversal key identifier");
        }
        this.setFocusTraversalKeys_NoIDCheck(n, set);
    }
    
    @Override
    public Set<AWTKeyStroke> getFocusTraversalKeys(final int n) {
        if (n < 0 || n >= 4) {
            throw new IllegalArgumentException("invalid focus traversal key identifier");
        }
        return this.getFocusTraversalKeys_NoIDCheck(n);
    }
    
    @Override
    public boolean areFocusTraversalKeysSet(final int n) {
        if (n < 0 || n >= 4) {
            throw new IllegalArgumentException("invalid focus traversal key identifier");
        }
        return this.focusTraversalKeys != null && this.focusTraversalKeys[n] != null;
    }
    
    @Override
    public boolean isFocusCycleRoot(final Container container) {
        return (this.isFocusCycleRoot() && container == this) || super.isFocusCycleRoot(container);
    }
    
    private Container findTraversalRoot() {
        final Container currentFocusCycleRoot = KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentFocusCycleRoot();
        Container focusCycleRootAncestor;
        if (currentFocusCycleRoot == this) {
            focusCycleRootAncestor = this;
        }
        else {
            focusCycleRootAncestor = this.getFocusCycleRootAncestor();
            if (focusCycleRootAncestor == null) {
                focusCycleRootAncestor = this;
            }
        }
        if (focusCycleRootAncestor != currentFocusCycleRoot) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRootPriv(focusCycleRootAncestor);
        }
        return focusCycleRootAncestor;
    }
    
    @Override
    final boolean containsFocus() {
        return this.isParentOf(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner());
    }
    
    private boolean isParentOf(Component parent) {
        synchronized (this.getTreeLock()) {
            while (parent != null && parent != this && !(parent instanceof Window)) {
                parent = parent.getParent();
            }
            return parent == this;
        }
    }
    
    @Override
    void clearMostRecentFocusOwnerOnHide() {
        int n = 0;
        Window containingWindow = null;
        synchronized (this.getTreeLock()) {
            containingWindow = this.getContainingWindow();
            if (containingWindow != null) {
                final Component mostRecentFocusOwner = KeyboardFocusManager.getMostRecentFocusOwner(containingWindow);
                n = ((mostRecentFocusOwner == this || this.isParentOf(mostRecentFocusOwner)) ? 1 : 0);
                synchronized (KeyboardFocusManager.class) {
                    final Component temporaryLostComponent = containingWindow.getTemporaryLostComponent();
                    if (this.isParentOf(temporaryLostComponent) || temporaryLostComponent == this) {
                        containingWindow.setTemporaryLostComponent(null);
                    }
                }
            }
        }
        if (n != 0) {
            KeyboardFocusManager.setMostRecentFocusOwner(containingWindow, null);
        }
    }
    
    @Override
    void clearCurrentFocusCycleRootOnHide() {
        final KeyboardFocusManager currentKeyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        final Container currentFocusCycleRoot = currentKeyboardFocusManager.getCurrentFocusCycleRoot();
        if (currentFocusCycleRoot == this || this.isParentOf(currentFocusCycleRoot)) {
            currentKeyboardFocusManager.setGlobalCurrentFocusCycleRootPriv(null);
        }
    }
    
    @Override
    final Container getTraversalRoot() {
        if (this.isFocusCycleRoot()) {
            return this.findTraversalRoot();
        }
        return super.getTraversalRoot();
    }
    
    public void setFocusTraversalPolicy(final FocusTraversalPolicy focusTraversalPolicy) {
        final FocusTraversalPolicy focusTraversalPolicy2;
        synchronized (this) {
            focusTraversalPolicy2 = this.focusTraversalPolicy;
            this.focusTraversalPolicy = focusTraversalPolicy;
        }
        this.firePropertyChange("focusTraversalPolicy", focusTraversalPolicy2, focusTraversalPolicy);
    }
    
    public FocusTraversalPolicy getFocusTraversalPolicy() {
        if (!this.isFocusTraversalPolicyProvider() && !this.isFocusCycleRoot()) {
            return null;
        }
        final FocusTraversalPolicy focusTraversalPolicy = this.focusTraversalPolicy;
        if (focusTraversalPolicy != null) {
            return focusTraversalPolicy;
        }
        final Container focusCycleRootAncestor = this.getFocusCycleRootAncestor();
        if (focusCycleRootAncestor != null) {
            return focusCycleRootAncestor.getFocusTraversalPolicy();
        }
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalPolicy();
    }
    
    public boolean isFocusTraversalPolicySet() {
        return this.focusTraversalPolicy != null;
    }
    
    public void setFocusCycleRoot(final boolean focusCycleRoot) {
        final boolean focusCycleRoot2;
        synchronized (this) {
            focusCycleRoot2 = this.focusCycleRoot;
            this.focusCycleRoot = focusCycleRoot;
        }
        this.firePropertyChange("focusCycleRoot", focusCycleRoot2, focusCycleRoot);
    }
    
    public boolean isFocusCycleRoot() {
        return this.focusCycleRoot;
    }
    
    public final void setFocusTraversalPolicyProvider(final boolean focusTraversalPolicyProvider) {
        final boolean focusTraversalPolicyProvider2;
        synchronized (this) {
            focusTraversalPolicyProvider2 = this.focusTraversalPolicyProvider;
            this.focusTraversalPolicyProvider = focusTraversalPolicyProvider;
        }
        this.firePropertyChange("focusTraversalPolicyProvider", focusTraversalPolicyProvider2, focusTraversalPolicyProvider);
    }
    
    public final boolean isFocusTraversalPolicyProvider() {
        return this.focusTraversalPolicyProvider;
    }
    
    public void transferFocusDownCycle() {
        if (this.isFocusCycleRoot()) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRootPriv(this);
            final Component defaultComponent = this.getFocusTraversalPolicy().getDefaultComponent(this);
            if (defaultComponent != null) {
                defaultComponent.requestFocus(CausedFocusEvent.Cause.TRAVERSAL_DOWN);
            }
        }
    }
    
    void preProcessKeyEvent(final KeyEvent keyEvent) {
        final Container parent = this.parent;
        if (parent != null) {
            parent.preProcessKeyEvent(keyEvent);
        }
    }
    
    void postProcessKeyEvent(final KeyEvent keyEvent) {
        final Container parent = this.parent;
        if (parent != null) {
            parent.postProcessKeyEvent(keyEvent);
        }
    }
    
    @Override
    boolean postsOldMouseEvents() {
        return true;
    }
    
    @Override
    public void applyComponentOrientation(final ComponentOrientation componentOrientation) {
        super.applyComponentOrientation(componentOrientation);
        synchronized (this.getTreeLock()) {
            for (int i = 0; i < this.component.size(); ++i) {
                this.component.get(i).applyComponentOrientation(componentOrientation);
            }
        }
    }
    
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        super.addPropertyChangeListener(propertyChangeListener);
    }
    
    @Override
    public void addPropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        super.addPropertyChangeListener(s, propertyChangeListener);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
        putFields.put("ncomponents", this.component.size());
        putFields.put("component", this.component.toArray(Container.EMPTY_ARRAY));
        putFields.put("layoutMgr", this.layoutMgr);
        putFields.put("dispatcher", this.dispatcher);
        putFields.put("maxSize", this.maxSize);
        putFields.put("focusCycleRoot", this.focusCycleRoot);
        putFields.put("containerSerializedDataVersion", this.containerSerializedDataVersion);
        putFields.put("focusTraversalPolicyProvider", this.focusTraversalPolicyProvider);
        objectOutputStream.writeFields();
        AWTEventMulticaster.save(objectOutputStream, "containerL", this.containerListener);
        objectOutputStream.writeObject(null);
        if (this.focusTraversalPolicy instanceof Serializable) {
            objectOutputStream.writeObject(this.focusTraversalPolicy);
        }
        else {
            objectOutputStream.writeObject(null);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        Component[] empty_ARRAY = (Component[])fields.get("component", null);
        if (empty_ARRAY == null) {
            empty_ARRAY = Container.EMPTY_ARRAY;
        }
        final int intValue = fields.get("ncomponents", 0);
        if (intValue < 0 || intValue > empty_ARRAY.length) {
            throw new InvalidObjectException("Incorrect number of components");
        }
        this.component = new ArrayList<Component>(intValue);
        for (int i = 0; i < intValue; ++i) {
            this.component.add(empty_ARRAY[i]);
        }
        this.layoutMgr = (LayoutManager)fields.get("layoutMgr", null);
        this.dispatcher = (LightweightDispatcher)fields.get("dispatcher", null);
        if (this.maxSize == null) {
            this.maxSize = (Dimension)fields.get("maxSize", null);
        }
        this.focusCycleRoot = fields.get("focusCycleRoot", false);
        this.containerSerializedDataVersion = fields.get("containerSerializedDataVersion", 1);
        this.focusTraversalPolicyProvider = fields.get("focusTraversalPolicyProvider", false);
        for (final Component component : this.component) {
            (component.parent = this).adjustListeningChildren(32768L, component.numListening(32768L));
            this.adjustListeningChildren(65536L, component.numListening(65536L));
            this.adjustDescendants(component.countHierarchyMembers());
        }
        Object object;
        while (null != (object = objectInputStream.readObject())) {
            if ("containerL" == ((String)object).intern()) {
                this.addContainerListener((ContainerListener)objectInputStream.readObject());
            }
            else {
                objectInputStream.readObject();
            }
        }
        try {
            final Object object2 = objectInputStream.readObject();
            if (object2 instanceof FocusTraversalPolicy) {
                this.focusTraversalPolicy = (FocusTraversalPolicy)object2;
            }
        }
        catch (final OptionalDataException ex) {
            if (!ex.eof) {
                throw ex;
            }
        }
    }
    
    Accessible getAccessibleAt(final Point point) {
        synchronized (this.getTreeLock()) {
            if (this instanceof Accessible) {
                AccessibleContext accessibleContext = ((Accessible)this).getAccessibleContext();
                if (accessibleContext != null) {
                    for (int accessibleChildrenCount = accessibleContext.getAccessibleChildrenCount(), i = 0; i < accessibleChildrenCount; ++i) {
                        final Accessible accessibleChild = accessibleContext.getAccessibleChild(i);
                        if (accessibleChild != null) {
                            accessibleContext = accessibleChild.getAccessibleContext();
                            if (accessibleContext != null) {
                                final AccessibleComponent accessibleComponent = accessibleContext.getAccessibleComponent();
                                if (accessibleComponent != null && accessibleComponent.isShowing()) {
                                    final Point location = accessibleComponent.getLocation();
                                    if (accessibleComponent.contains(new Point(point.x - location.x, point.y - location.y))) {
                                        return accessibleChild;
                                    }
                                }
                            }
                        }
                    }
                }
                return (Accessible)this;
            }
            Object o = this;
            if (!this.contains(point.x, point.y)) {
                o = null;
            }
            else {
                for (int componentCount = this.getComponentCount(), j = 0; j < componentCount; ++j) {
                    final Component component = this.getComponent(j);
                    if (component != null && component.isShowing()) {
                        final Point location2 = component.getLocation();
                        if (component.contains(point.x - location2.x, point.y - location2.y)) {
                            o = component;
                        }
                    }
                }
            }
            if (o instanceof Accessible) {
                return (Accessible)o;
            }
            return null;
        }
    }
    
    int getAccessibleChildrenCount() {
        synchronized (this.getTreeLock()) {
            int n = 0;
            final Component[] components = this.getComponents();
            for (int i = 0; i < components.length; ++i) {
                if (components[i] instanceof Accessible) {
                    ++n;
                }
            }
            return n;
        }
    }
    
    Accessible getAccessibleChild(final int n) {
        synchronized (this.getTreeLock()) {
            final Component[] components = this.getComponents();
            int n2 = 0;
            for (int i = 0; i < components.length; ++i) {
                if (components[i] instanceof Accessible) {
                    if (n2 == n) {
                        return (Accessible)components[i];
                    }
                    ++n2;
                }
            }
            return null;
        }
    }
    
    final void increaseComponentCount(final Component component) {
        synchronized (this.getTreeLock()) {
            if (!component.isDisplayable()) {
                throw new IllegalStateException("Peer does not exist while invoking the increaseComponentCount() method");
            }
            int numOfHWComponents = 0;
            int numOfLWComponents = 0;
            if (component instanceof Container) {
                numOfLWComponents = ((Container)component).numOfLWComponents;
                numOfHWComponents = ((Container)component).numOfHWComponents;
            }
            if (component.isLightweight()) {
                ++numOfLWComponents;
            }
            else {
                ++numOfHWComponents;
            }
            for (Container container = this; container != null; container = container.getContainer()) {
                final Container container2 = container;
                container2.numOfLWComponents += numOfLWComponents;
                final Container container3 = container;
                container3.numOfHWComponents += numOfHWComponents;
            }
        }
    }
    
    final void decreaseComponentCount(final Component component) {
        synchronized (this.getTreeLock()) {
            if (!component.isDisplayable()) {
                throw new IllegalStateException("Peer does not exist while invoking the decreaseComponentCount() method");
            }
            int numOfHWComponents = 0;
            int numOfLWComponents = 0;
            if (component instanceof Container) {
                numOfLWComponents = ((Container)component).numOfLWComponents;
                numOfHWComponents = ((Container)component).numOfHWComponents;
            }
            if (component.isLightweight()) {
                ++numOfLWComponents;
            }
            else {
                ++numOfHWComponents;
            }
            for (Container container = this; container != null; container = container.getContainer()) {
                final Container container2 = container;
                container2.numOfLWComponents -= numOfLWComponents;
                final Container container3 = container;
                container3.numOfHWComponents -= numOfHWComponents;
            }
        }
    }
    
    private int getTopmostComponentIndex() {
        this.checkTreeLock();
        if (this.getComponentCount() > 0) {
            return 0;
        }
        return -1;
    }
    
    private int getBottommostComponentIndex() {
        this.checkTreeLock();
        if (this.getComponentCount() > 0) {
            return this.getComponentCount() - 1;
        }
        return -1;
    }
    
    @Override
    final Region getOpaqueShape() {
        this.checkTreeLock();
        if (this.isLightweight() && this.isNonOpaqueForMixing() && this.hasLightweightDescendants()) {
            Region region = Region.EMPTY_REGION;
            for (int i = 0; i < this.getComponentCount(); ++i) {
                final Component component = this.getComponent(i);
                if (component.isLightweight() && component.isShowing()) {
                    region = region.getUnion(component.getOpaqueShape());
                }
            }
            return region.getIntersection(this.getNormalShape());
        }
        return super.getOpaqueShape();
    }
    
    final void recursiveSubtractAndApplyShape(final Region region) {
        this.recursiveSubtractAndApplyShape(region, this.getTopmostComponentIndex(), this.getBottommostComponentIndex());
    }
    
    final void recursiveSubtractAndApplyShape(final Region region, final int n) {
        this.recursiveSubtractAndApplyShape(region, n, this.getBottommostComponentIndex());
    }
    
    final void recursiveSubtractAndApplyShape(final Region region, final int n, final int n2) {
        this.checkTreeLock();
        if (Container.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            Container.mixingLog.fine("this = " + this + "; shape=" + region + "; fromZ=" + n + "; toZ=" + n2);
        }
        if (n == -1) {
            return;
        }
        if (region.isEmpty()) {
            return;
        }
        if (this.getLayout() != null && !this.isValid()) {
            return;
        }
        for (int i = n; i <= n2; ++i) {
            final Component component = this.getComponent(i);
            if (!component.isLightweight()) {
                component.subtractAndApplyShape(region);
            }
            else if (component instanceof Container && ((Container)component).hasHeavyweightDescendants() && component.isShowing()) {
                ((Container)component).recursiveSubtractAndApplyShape(region);
            }
        }
    }
    
    final void recursiveApplyCurrentShape() {
        this.recursiveApplyCurrentShape(this.getTopmostComponentIndex(), this.getBottommostComponentIndex());
    }
    
    final void recursiveApplyCurrentShape(final int n) {
        this.recursiveApplyCurrentShape(n, this.getBottommostComponentIndex());
    }
    
    final void recursiveApplyCurrentShape(final int n, final int n2) {
        this.checkTreeLock();
        if (Container.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            Container.mixingLog.fine("this = " + this + "; fromZ=" + n + "; toZ=" + n2);
        }
        if (n == -1) {
            return;
        }
        if (this.getLayout() != null && !this.isValid()) {
            return;
        }
        for (int i = n; i <= n2; ++i) {
            final Component component = this.getComponent(i);
            if (!component.isLightweight()) {
                component.applyCurrentShape();
            }
            if (component instanceof Container && ((Container)component).hasHeavyweightDescendants()) {
                ((Container)component).recursiveApplyCurrentShape();
            }
        }
    }
    
    private void recursiveShowHeavyweightChildren() {
        if (!this.hasHeavyweightDescendants() || !this.isVisible()) {
            return;
        }
        for (int i = 0; i < this.getComponentCount(); ++i) {
            final Component component = this.getComponent(i);
            if (component.isLightweight()) {
                if (component instanceof Container) {
                    ((Container)component).recursiveShowHeavyweightChildren();
                }
            }
            else if (component.isVisible()) {
                final ComponentPeer peer = component.getPeer();
                if (peer != null) {
                    peer.setVisible(true);
                }
            }
        }
    }
    
    private void recursiveHideHeavyweightChildren() {
        if (!this.hasHeavyweightDescendants()) {
            return;
        }
        for (int i = 0; i < this.getComponentCount(); ++i) {
            final Component component = this.getComponent(i);
            if (component.isLightweight()) {
                if (component instanceof Container) {
                    ((Container)component).recursiveHideHeavyweightChildren();
                }
            }
            else if (component.isVisible()) {
                final ComponentPeer peer = component.getPeer();
                if (peer != null) {
                    peer.setVisible(false);
                }
            }
        }
    }
    
    private void recursiveRelocateHeavyweightChildren(final Point point) {
        for (int i = 0; i < this.getComponentCount(); ++i) {
            final Component component = this.getComponent(i);
            if (component.isLightweight()) {
                if (component instanceof Container && ((Container)component).hasHeavyweightDescendants()) {
                    final Point point2 = new Point(point);
                    point2.translate(component.getX(), component.getY());
                    ((Container)component).recursiveRelocateHeavyweightChildren(point2);
                }
            }
            else {
                final ComponentPeer peer = component.getPeer();
                if (peer != null) {
                    peer.setBounds(point.x + component.getX(), point.y + component.getY(), component.getWidth(), component.getHeight(), 1);
                }
            }
        }
    }
    
    final boolean isRecursivelyVisibleUpToHeavyweightContainer() {
        if (!this.isLightweight()) {
            return true;
        }
        for (Container container = this; container != null && container.isLightweight(); container = container.getContainer()) {
            if (!container.isVisible()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    void mixOnShowing() {
        synchronized (this.getTreeLock()) {
            if (Container.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
                Container.mixingLog.fine("this = " + this);
            }
            final boolean lightweight = this.isLightweight();
            if (lightweight && this.isRecursivelyVisibleUpToHeavyweightContainer()) {
                this.recursiveShowHeavyweightChildren();
            }
            if (!this.isMixingNeeded()) {
                return;
            }
            if (!lightweight || (lightweight && this.hasHeavyweightDescendants())) {
                this.recursiveApplyCurrentShape();
            }
            super.mixOnShowing();
        }
    }
    
    @Override
    void mixOnHiding(final boolean b) {
        synchronized (this.getTreeLock()) {
            if (Container.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
                Container.mixingLog.fine("this = " + this + "; isLightweight=" + b);
            }
            if (b) {
                this.recursiveHideHeavyweightChildren();
            }
            super.mixOnHiding(b);
        }
    }
    
    @Override
    void mixOnReshaping() {
        synchronized (this.getTreeLock()) {
            if (Container.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
                Container.mixingLog.fine("this = " + this);
            }
            final boolean mixingNeeded = this.isMixingNeeded();
            if (this.isLightweight() && this.hasHeavyweightDescendants()) {
                final Point point = new Point(this.getX(), this.getY());
                for (Container container = this.getContainer(); container != null && container.isLightweight(); container = container.getContainer()) {
                    point.translate(container.getX(), container.getY());
                }
                this.recursiveRelocateHeavyweightChildren(point);
                if (!mixingNeeded) {
                    return;
                }
                this.recursiveApplyCurrentShape();
            }
            if (!mixingNeeded) {
                return;
            }
            super.mixOnReshaping();
        }
    }
    
    @Override
    void mixOnZOrderChanging(final int n, final int n2) {
        synchronized (this.getTreeLock()) {
            if (Container.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
                Container.mixingLog.fine("this = " + this + "; oldZ=" + n + "; newZ=" + n2);
            }
            if (!this.isMixingNeeded()) {
                return;
            }
            if (n2 < n && this.isLightweight() && this.hasHeavyweightDescendants()) {
                this.recursiveApplyCurrentShape();
            }
            super.mixOnZOrderChanging(n, n2);
        }
    }
    
    @Override
    void mixOnValidating() {
        synchronized (this.getTreeLock()) {
            if (Container.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
                Container.mixingLog.fine("this = " + this);
            }
            if (!this.isMixingNeeded()) {
                return;
            }
            if (this.hasHeavyweightDescendants()) {
                this.recursiveApplyCurrentShape();
            }
            if (this.isLightweight() && this.isNonOpaqueForMixing()) {
                this.subtractAndApplyShapeBelowMe();
            }
            super.mixOnValidating();
        }
    }
    
    static {
        log = PlatformLogger.getLogger("java.awt.Container");
        eventLog = PlatformLogger.getLogger("java.awt.event.Container");
        EMPTY_ARRAY = new Component[0];
        mixingLog = PlatformLogger.getLogger("java.awt.mixing.Container");
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("ncomponents", Integer.TYPE), new ObjectStreamField("component", Component[].class), new ObjectStreamField("layoutMgr", LayoutManager.class), new ObjectStreamField("dispatcher", LightweightDispatcher.class), new ObjectStreamField("maxSize", Dimension.class), new ObjectStreamField("focusCycleRoot", Boolean.TYPE), new ObjectStreamField("containerSerializedDataVersion", Integer.TYPE), new ObjectStreamField("focusTraversalPolicyProvider", Boolean.TYPE) };
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        AWTAccessor.setContainerAccessor(new AWTAccessor.ContainerAccessor() {
            @Override
            public void validateUnconditionally(final Container container) {
                container.validateUnconditionally();
            }
            
            @Override
            public Component findComponentAt(final Container container, final int n, final int n2, final boolean b) {
                return container.findComponentAt(n, n2, b);
            }
        });
        isJavaAwtSmartInvalidate = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("java.awt.smartInvalidate"));
        Container.descendUnconditionallyWhenValidating = false;
    }
    
    static class MouseEventTargetFilter implements EventTargetFilter
    {
        static final EventTargetFilter FILTER;
        
        private MouseEventTargetFilter() {
        }
        
        @Override
        public boolean accept(final Component component) {
            return (component.eventMask & 0x20L) != 0x0L || (component.eventMask & 0x10L) != 0x0L || (component.eventMask & 0x20000L) != 0x0L || component.mouseListener != null || component.mouseMotionListener != null || component.mouseWheelListener != null;
        }
        
        static {
            FILTER = new MouseEventTargetFilter();
        }
    }
    
    static class DropTargetEventTargetFilter implements EventTargetFilter
    {
        static final EventTargetFilter FILTER;
        
        private DropTargetEventTargetFilter() {
        }
        
        @Override
        public boolean accept(final Component component) {
            final DropTarget dropTarget = component.getDropTarget();
            return dropTarget != null && dropTarget.isActive();
        }
        
        static {
            FILTER = new DropTargetEventTargetFilter();
        }
    }
    
    static final class WakingRunnable implements Runnable
    {
        @Override
        public void run() {
        }
    }
    
    protected class AccessibleAWTContainer extends AccessibleAWTComponent
    {
        private static final long serialVersionUID = 5081320404842566097L;
        private transient volatile int propertyListenersCount;
        protected ContainerListener accessibleContainerHandler;
        
        protected AccessibleAWTContainer() {
            this.propertyListenersCount = 0;
            this.accessibleContainerHandler = null;
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            return Container.this.getAccessibleChildrenCount();
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            return Container.this.getAccessibleChild(n);
        }
        
        @Override
        public Accessible getAccessibleAt(final Point point) {
            return Container.this.getAccessibleAt(point);
        }
        
        @Override
        public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
            if (this.accessibleContainerHandler == null) {
                this.accessibleContainerHandler = new AccessibleContainerHandler();
            }
            if (this.propertyListenersCount++ == 0) {
                Container.this.addContainerListener(this.accessibleContainerHandler);
            }
            super.addPropertyChangeListener(propertyChangeListener);
        }
        
        @Override
        public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
            final int propertyListenersCount = this.propertyListenersCount - 1;
            this.propertyListenersCount = propertyListenersCount;
            if (propertyListenersCount == 0) {
                Container.this.removeContainerListener(this.accessibleContainerHandler);
            }
            super.removePropertyChangeListener(propertyChangeListener);
        }
        
        protected class AccessibleContainerHandler implements ContainerListener
        {
            @Override
            public void componentAdded(final ContainerEvent containerEvent) {
                final Component child = containerEvent.getChild();
                if (child != null && child instanceof Accessible) {
                    AccessibleAWTContainer.this.firePropertyChange("AccessibleChild", null, ((Accessible)child).getAccessibleContext());
                }
            }
            
            @Override
            public void componentRemoved(final ContainerEvent containerEvent) {
                final Component child = containerEvent.getChild();
                if (child != null && child instanceof Accessible) {
                    AccessibleAWTContainer.this.firePropertyChange("AccessibleChild", ((Accessible)child).getAccessibleContext(), null);
                }
            }
        }
    }
    
    interface EventTargetFilter
    {
        boolean accept(final Component p0);
    }
}
