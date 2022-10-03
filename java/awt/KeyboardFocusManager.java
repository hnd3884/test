package java.awt;

import java.util.WeakHashMap;
import sun.awt.AWTAccessor;
import java.security.Permission;
import java.lang.reflect.AccessibleObject;
import java.awt.peer.LightweightPeer;
import java.awt.event.WindowEvent;
import java.awt.event.FocusEvent;
import sun.awt.CausedFocusEvent;
import sun.awt.SunToolkit;
import java.awt.event.KeyEvent;
import java.util.List;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Collection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.beans.PropertyVetoException;
import sun.awt.KeyboardFocusManagerPeerProvider;
import java.util.HashSet;
import java.util.Collections;
import java.util.StringTokenizer;
import sun.awt.AppContext;
import java.lang.reflect.Field;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.LinkedList;
import java.beans.PropertyChangeSupport;
import java.beans.VetoableChangeSupport;
import java.util.Set;
import java.awt.peer.KeyboardFocusManagerPeer;
import sun.util.logging.PlatformLogger;

public abstract class KeyboardFocusManager implements KeyEventDispatcher, KeyEventPostProcessor
{
    private static final PlatformLogger focusLog;
    transient KeyboardFocusManagerPeer peer;
    private static final PlatformLogger log;
    public static final int FORWARD_TRAVERSAL_KEYS = 0;
    public static final int BACKWARD_TRAVERSAL_KEYS = 1;
    public static final int UP_CYCLE_TRAVERSAL_KEYS = 2;
    public static final int DOWN_CYCLE_TRAVERSAL_KEYS = 3;
    static final int TRAVERSAL_KEY_LENGTH = 4;
    private static Component focusOwner;
    private static Component permanentFocusOwner;
    private static Window focusedWindow;
    private static Window activeWindow;
    private FocusTraversalPolicy defaultPolicy;
    private static final String[] defaultFocusTraversalKeyPropertyNames;
    private static final AWTKeyStroke[][] defaultFocusTraversalKeyStrokes;
    private Set<AWTKeyStroke>[] defaultFocusTraversalKeys;
    private static Container currentFocusCycleRoot;
    private VetoableChangeSupport vetoableSupport;
    private PropertyChangeSupport changeSupport;
    private LinkedList<KeyEventDispatcher> keyEventDispatchers;
    private LinkedList<KeyEventPostProcessor> keyEventPostProcessors;
    private static Map<Window, WeakReference<Component>> mostRecentFocusOwners;
    private static AWTPermission replaceKeyboardFocusManagerPermission;
    transient SequencedEvent currentSequencedEvent;
    private static LinkedList<HeavyweightFocusRequest> heavyweightRequests;
    private static LinkedList<LightweightFocusRequest> currentLightweightRequests;
    private static boolean clearingCurrentLightweightRequests;
    private static boolean allowSyncFocusRequests;
    private static Component newFocusOwner;
    private static volatile boolean disableRestoreFocus;
    static final int SNFH_FAILURE = 0;
    static final int SNFH_SUCCESS_HANDLED = 1;
    static final int SNFH_SUCCESS_PROCEED = 2;
    static Field proxyActive;
    
    private static native void initIDs();
    
    public static KeyboardFocusManager getCurrentKeyboardFocusManager() {
        return getCurrentKeyboardFocusManager(AppContext.getAppContext());
    }
    
    static synchronized KeyboardFocusManager getCurrentKeyboardFocusManager(final AppContext appContext) {
        KeyboardFocusManager keyboardFocusManager = (KeyboardFocusManager)appContext.get(KeyboardFocusManager.class);
        if (keyboardFocusManager == null) {
            keyboardFocusManager = new DefaultKeyboardFocusManager();
            appContext.put(KeyboardFocusManager.class, keyboardFocusManager);
        }
        return keyboardFocusManager;
    }
    
    public static void setCurrentKeyboardFocusManager(final KeyboardFocusManager keyboardFocusManager) throws SecurityException {
        checkReplaceKFMPermission();
        KeyboardFocusManager keyboardFocusManager2 = null;
        synchronized (KeyboardFocusManager.class) {
            final AppContext appContext = AppContext.getAppContext();
            if (keyboardFocusManager != null) {
                keyboardFocusManager2 = getCurrentKeyboardFocusManager(appContext);
                appContext.put(KeyboardFocusManager.class, keyboardFocusManager);
            }
            else {
                keyboardFocusManager2 = getCurrentKeyboardFocusManager(appContext);
                appContext.remove(KeyboardFocusManager.class);
            }
        }
        if (keyboardFocusManager2 != null) {
            keyboardFocusManager2.firePropertyChange("managingFocus", Boolean.TRUE, Boolean.FALSE);
        }
        if (keyboardFocusManager != null) {
            keyboardFocusManager.firePropertyChange("managingFocus", Boolean.FALSE, Boolean.TRUE);
        }
    }
    
    final void setCurrentSequencedEvent(final SequencedEvent currentSequencedEvent) {
        synchronized (SequencedEvent.class) {
            assert this.currentSequencedEvent == null;
            this.currentSequencedEvent = currentSequencedEvent;
        }
    }
    
    final SequencedEvent getCurrentSequencedEvent() {
        synchronized (SequencedEvent.class) {
            return this.currentSequencedEvent;
        }
    }
    
    static Set<AWTKeyStroke> initFocusTraversalKeysSet(final String s, final Set<AWTKeyStroke> set) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s, ",");
        while (stringTokenizer.hasMoreTokens()) {
            set.add(AWTKeyStroke.getAWTKeyStroke(stringTokenizer.nextToken()));
        }
        return set.isEmpty() ? Collections.EMPTY_SET : Collections.unmodifiableSet((Set<? extends AWTKeyStroke>)set);
    }
    
    public KeyboardFocusManager() {
        this.defaultPolicy = new DefaultFocusTraversalPolicy();
        this.defaultFocusTraversalKeys = new Set[4];
        this.currentSequencedEvent = null;
        for (int i = 0; i < 4; ++i) {
            final HashSet set = new HashSet();
            for (int j = 0; j < KeyboardFocusManager.defaultFocusTraversalKeyStrokes[i].length; ++j) {
                set.add(KeyboardFocusManager.defaultFocusTraversalKeyStrokes[i][j]);
            }
            this.defaultFocusTraversalKeys[i] = (Set<AWTKeyStroke>)(set.isEmpty() ? Collections.EMPTY_SET : Collections.unmodifiableSet((Set<?>)set));
        }
        this.initPeer();
    }
    
    private void initPeer() {
        this.peer = ((KeyboardFocusManagerPeerProvider)Toolkit.getDefaultToolkit()).getKeyboardFocusManagerPeer();
    }
    
    public Component getFocusOwner() {
        synchronized (KeyboardFocusManager.class) {
            if (KeyboardFocusManager.focusOwner == null) {
                return null;
            }
            return (KeyboardFocusManager.focusOwner.appContext == AppContext.getAppContext()) ? KeyboardFocusManager.focusOwner : null;
        }
    }
    
    protected Component getGlobalFocusOwner() throws SecurityException {
        synchronized (KeyboardFocusManager.class) {
            this.checkKFMSecurity();
            return KeyboardFocusManager.focusOwner;
        }
    }
    
    protected void setGlobalFocusOwner(final Component focusOwner) throws SecurityException {
        Object focusOwner2 = null;
        boolean b = false;
        if (focusOwner == null || focusOwner.isFocusable()) {
            synchronized (KeyboardFocusManager.class) {
                this.checkKFMSecurity();
                focusOwner2 = this.getFocusOwner();
                try {
                    this.fireVetoableChange("focusOwner", focusOwner2, focusOwner);
                }
                catch (final PropertyVetoException ex) {
                    return;
                }
                KeyboardFocusManager.focusOwner = focusOwner;
                if (focusOwner != null && (this.getCurrentFocusCycleRoot() == null || !focusOwner.isFocusCycleRoot(this.getCurrentFocusCycleRoot()))) {
                    Container focusCycleRootAncestor = focusOwner.getFocusCycleRootAncestor();
                    if (focusCycleRootAncestor == null && focusOwner instanceof Window) {
                        focusCycleRootAncestor = (Container)focusOwner;
                    }
                    if (focusCycleRootAncestor != null) {
                        this.setGlobalCurrentFocusCycleRootPriv(focusCycleRootAncestor);
                    }
                }
                b = true;
            }
        }
        if (b) {
            this.firePropertyChange("focusOwner", focusOwner2, focusOwner);
        }
    }
    
    public void clearFocusOwner() {
        if (this.getFocusOwner() != null) {
            this.clearGlobalFocusOwner();
        }
    }
    
    public void clearGlobalFocusOwner() throws SecurityException {
        checkReplaceKFMPermission();
        if (!GraphicsEnvironment.isHeadless()) {
            Toolkit.getDefaultToolkit();
            this._clearGlobalFocusOwner();
        }
    }
    
    private void _clearGlobalFocusOwner() {
        this.peer.clearGlobalFocusOwner(markClearGlobalFocusOwner());
    }
    
    void clearGlobalFocusOwnerPriv() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                KeyboardFocusManager.this.clearGlobalFocusOwner();
                return null;
            }
        });
    }
    
    Component getNativeFocusOwner() {
        return this.peer.getCurrentFocusOwner();
    }
    
    void setNativeFocusOwner(final Component currentFocusOwner) {
        if (KeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
            KeyboardFocusManager.focusLog.finest("Calling peer {0} setCurrentFocusOwner for {1}", String.valueOf(this.peer), String.valueOf(currentFocusOwner));
        }
        this.peer.setCurrentFocusOwner(currentFocusOwner);
    }
    
    Window getNativeFocusedWindow() {
        return this.peer.getCurrentFocusedWindow();
    }
    
    public Component getPermanentFocusOwner() {
        synchronized (KeyboardFocusManager.class) {
            if (KeyboardFocusManager.permanentFocusOwner == null) {
                return null;
            }
            return (KeyboardFocusManager.permanentFocusOwner.appContext == AppContext.getAppContext()) ? KeyboardFocusManager.permanentFocusOwner : null;
        }
    }
    
    protected Component getGlobalPermanentFocusOwner() throws SecurityException {
        synchronized (KeyboardFocusManager.class) {
            this.checkKFMSecurity();
            return KeyboardFocusManager.permanentFocusOwner;
        }
    }
    
    protected void setGlobalPermanentFocusOwner(final Component permanentFocusOwner) throws SecurityException {
        Object permanentFocusOwner2 = null;
        boolean b = false;
        if (permanentFocusOwner == null || permanentFocusOwner.isFocusable()) {
            synchronized (KeyboardFocusManager.class) {
                this.checkKFMSecurity();
                permanentFocusOwner2 = this.getPermanentFocusOwner();
                try {
                    this.fireVetoableChange("permanentFocusOwner", permanentFocusOwner2, permanentFocusOwner);
                }
                catch (final PropertyVetoException ex) {
                    return;
                }
                setMostRecentFocusOwner(KeyboardFocusManager.permanentFocusOwner = permanentFocusOwner);
                b = true;
            }
        }
        if (b) {
            this.firePropertyChange("permanentFocusOwner", permanentFocusOwner2, permanentFocusOwner);
        }
    }
    
    public Window getFocusedWindow() {
        synchronized (KeyboardFocusManager.class) {
            if (KeyboardFocusManager.focusedWindow == null) {
                return null;
            }
            return (KeyboardFocusManager.focusedWindow.appContext == AppContext.getAppContext()) ? KeyboardFocusManager.focusedWindow : null;
        }
    }
    
    protected Window getGlobalFocusedWindow() throws SecurityException {
        synchronized (KeyboardFocusManager.class) {
            this.checkKFMSecurity();
            return KeyboardFocusManager.focusedWindow;
        }
    }
    
    protected void setGlobalFocusedWindow(final Window focusedWindow) throws SecurityException {
        Object focusedWindow2 = null;
        boolean b = false;
        if (focusedWindow == null || focusedWindow.isFocusableWindow()) {
            synchronized (KeyboardFocusManager.class) {
                this.checkKFMSecurity();
                focusedWindow2 = this.getFocusedWindow();
                try {
                    this.fireVetoableChange("focusedWindow", focusedWindow2, focusedWindow);
                }
                catch (final PropertyVetoException ex) {
                    return;
                }
                KeyboardFocusManager.focusedWindow = focusedWindow;
                b = true;
            }
        }
        if (b) {
            this.firePropertyChange("focusedWindow", focusedWindow2, focusedWindow);
        }
    }
    
    public Window getActiveWindow() {
        synchronized (KeyboardFocusManager.class) {
            if (KeyboardFocusManager.activeWindow == null) {
                return null;
            }
            return (KeyboardFocusManager.activeWindow.appContext == AppContext.getAppContext()) ? KeyboardFocusManager.activeWindow : null;
        }
    }
    
    protected Window getGlobalActiveWindow() throws SecurityException {
        synchronized (KeyboardFocusManager.class) {
            this.checkKFMSecurity();
            return KeyboardFocusManager.activeWindow;
        }
    }
    
    protected void setGlobalActiveWindow(final Window activeWindow) throws SecurityException {
        final Window activeWindow2;
        synchronized (KeyboardFocusManager.class) {
            this.checkKFMSecurity();
            activeWindow2 = this.getActiveWindow();
            if (KeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                KeyboardFocusManager.focusLog.finer("Setting global active window to " + activeWindow + ", old active " + activeWindow2);
            }
            try {
                this.fireVetoableChange("activeWindow", activeWindow2, activeWindow);
            }
            catch (final PropertyVetoException ex) {
                return;
            }
            KeyboardFocusManager.activeWindow = activeWindow;
        }
        this.firePropertyChange("activeWindow", activeWindow2, activeWindow);
    }
    
    public synchronized FocusTraversalPolicy getDefaultFocusTraversalPolicy() {
        return this.defaultPolicy;
    }
    
    public void setDefaultFocusTraversalPolicy(final FocusTraversalPolicy defaultPolicy) {
        if (defaultPolicy == null) {
            throw new IllegalArgumentException("default focus traversal policy cannot be null");
        }
        final FocusTraversalPolicy defaultPolicy2;
        synchronized (this) {
            defaultPolicy2 = this.defaultPolicy;
            this.defaultPolicy = defaultPolicy;
        }
        this.firePropertyChange("defaultFocusTraversalPolicy", defaultPolicy2, defaultPolicy);
    }
    
    public void setDefaultFocusTraversalKeys(final int n, final Set<? extends AWTKeyStroke> set) {
        if (n < 0 || n >= 4) {
            throw new IllegalArgumentException("invalid focus traversal key identifier");
        }
        if (set == null) {
            throw new IllegalArgumentException("cannot set null Set of default focus traversal keys");
        }
        final Set<AWTKeyStroke> set2;
        synchronized (this) {
            for (final AWTKeyStroke awtKeyStroke : set) {
                if (awtKeyStroke == null) {
                    throw new IllegalArgumentException("cannot set null focus traversal key");
                }
                if (awtKeyStroke.getKeyChar() != '\uffff') {
                    throw new IllegalArgumentException("focus traversal keys cannot map to KEY_TYPED events");
                }
                for (int i = 0; i < 4; ++i) {
                    if (i != n) {
                        if (this.defaultFocusTraversalKeys[i].contains(awtKeyStroke)) {
                            throw new IllegalArgumentException("focus traversal keys must be unique for a Component");
                        }
                    }
                }
            }
            set2 = this.defaultFocusTraversalKeys[n];
            this.defaultFocusTraversalKeys[n] = Collections.unmodifiableSet((Set<? extends AWTKeyStroke>)new HashSet<AWTKeyStroke>(set));
        }
        this.firePropertyChange(KeyboardFocusManager.defaultFocusTraversalKeyPropertyNames[n], set2, set);
    }
    
    public Set<AWTKeyStroke> getDefaultFocusTraversalKeys(final int n) {
        if (n < 0 || n >= 4) {
            throw new IllegalArgumentException("invalid focus traversal key identifier");
        }
        return this.defaultFocusTraversalKeys[n];
    }
    
    public Container getCurrentFocusCycleRoot() {
        synchronized (KeyboardFocusManager.class) {
            if (KeyboardFocusManager.currentFocusCycleRoot == null) {
                return null;
            }
            return (KeyboardFocusManager.currentFocusCycleRoot.appContext == AppContext.getAppContext()) ? KeyboardFocusManager.currentFocusCycleRoot : null;
        }
    }
    
    protected Container getGlobalCurrentFocusCycleRoot() throws SecurityException {
        synchronized (KeyboardFocusManager.class) {
            this.checkKFMSecurity();
            return KeyboardFocusManager.currentFocusCycleRoot;
        }
    }
    
    public void setGlobalCurrentFocusCycleRoot(final Container currentFocusCycleRoot) throws SecurityException {
        checkReplaceKFMPermission();
        final Container currentFocusCycleRoot2;
        synchronized (KeyboardFocusManager.class) {
            currentFocusCycleRoot2 = this.getCurrentFocusCycleRoot();
            KeyboardFocusManager.currentFocusCycleRoot = currentFocusCycleRoot;
        }
        this.firePropertyChange("currentFocusCycleRoot", currentFocusCycleRoot2, currentFocusCycleRoot);
    }
    
    void setGlobalCurrentFocusCycleRootPriv(final Container container) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                KeyboardFocusManager.this.setGlobalCurrentFocusCycleRoot(container);
                return null;
            }
        });
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (propertyChangeListener != null) {
            synchronized (this) {
                if (this.changeSupport == null) {
                    this.changeSupport = new PropertyChangeSupport(this);
                }
                this.changeSupport.addPropertyChangeListener(propertyChangeListener);
            }
        }
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (propertyChangeListener != null) {
            synchronized (this) {
                if (this.changeSupport != null) {
                    this.changeSupport.removePropertyChangeListener(propertyChangeListener);
                }
            }
        }
    }
    
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        if (this.changeSupport == null) {
            this.changeSupport = new PropertyChangeSupport(this);
        }
        return this.changeSupport.getPropertyChangeListeners();
    }
    
    public void addPropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        if (propertyChangeListener != null) {
            synchronized (this) {
                if (this.changeSupport == null) {
                    this.changeSupport = new PropertyChangeSupport(this);
                }
                this.changeSupport.addPropertyChangeListener(s, propertyChangeListener);
            }
        }
    }
    
    public void removePropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        if (propertyChangeListener != null) {
            synchronized (this) {
                if (this.changeSupport != null) {
                    this.changeSupport.removePropertyChangeListener(s, propertyChangeListener);
                }
            }
        }
    }
    
    public synchronized PropertyChangeListener[] getPropertyChangeListeners(final String s) {
        if (this.changeSupport == null) {
            this.changeSupport = new PropertyChangeSupport(this);
        }
        return this.changeSupport.getPropertyChangeListeners(s);
    }
    
    protected void firePropertyChange(final String s, final Object o, final Object o2) {
        if (o == o2) {
            return;
        }
        final PropertyChangeSupport changeSupport = this.changeSupport;
        if (changeSupport != null) {
            changeSupport.firePropertyChange(s, o, o2);
        }
    }
    
    public void addVetoableChangeListener(final VetoableChangeListener vetoableChangeListener) {
        if (vetoableChangeListener != null) {
            synchronized (this) {
                if (this.vetoableSupport == null) {
                    this.vetoableSupport = new VetoableChangeSupport(this);
                }
                this.vetoableSupport.addVetoableChangeListener(vetoableChangeListener);
            }
        }
    }
    
    public void removeVetoableChangeListener(final VetoableChangeListener vetoableChangeListener) {
        if (vetoableChangeListener != null) {
            synchronized (this) {
                if (this.vetoableSupport != null) {
                    this.vetoableSupport.removeVetoableChangeListener(vetoableChangeListener);
                }
            }
        }
    }
    
    public synchronized VetoableChangeListener[] getVetoableChangeListeners() {
        if (this.vetoableSupport == null) {
            this.vetoableSupport = new VetoableChangeSupport(this);
        }
        return this.vetoableSupport.getVetoableChangeListeners();
    }
    
    public void addVetoableChangeListener(final String s, final VetoableChangeListener vetoableChangeListener) {
        if (vetoableChangeListener != null) {
            synchronized (this) {
                if (this.vetoableSupport == null) {
                    this.vetoableSupport = new VetoableChangeSupport(this);
                }
                this.vetoableSupport.addVetoableChangeListener(s, vetoableChangeListener);
            }
        }
    }
    
    public void removeVetoableChangeListener(final String s, final VetoableChangeListener vetoableChangeListener) {
        if (vetoableChangeListener != null) {
            synchronized (this) {
                if (this.vetoableSupport != null) {
                    this.vetoableSupport.removeVetoableChangeListener(s, vetoableChangeListener);
                }
            }
        }
    }
    
    public synchronized VetoableChangeListener[] getVetoableChangeListeners(final String s) {
        if (this.vetoableSupport == null) {
            this.vetoableSupport = new VetoableChangeSupport(this);
        }
        return this.vetoableSupport.getVetoableChangeListeners(s);
    }
    
    protected void fireVetoableChange(final String s, final Object o, final Object o2) throws PropertyVetoException {
        if (o == o2) {
            return;
        }
        final VetoableChangeSupport vetoableSupport = this.vetoableSupport;
        if (vetoableSupport != null) {
            vetoableSupport.fireVetoableChange(s, o, o2);
        }
    }
    
    public void addKeyEventDispatcher(final KeyEventDispatcher keyEventDispatcher) {
        if (keyEventDispatcher != null) {
            synchronized (this) {
                if (this.keyEventDispatchers == null) {
                    this.keyEventDispatchers = new LinkedList<KeyEventDispatcher>();
                }
                this.keyEventDispatchers.add(keyEventDispatcher);
            }
        }
    }
    
    public void removeKeyEventDispatcher(final KeyEventDispatcher keyEventDispatcher) {
        if (keyEventDispatcher != null) {
            synchronized (this) {
                if (this.keyEventDispatchers != null) {
                    this.keyEventDispatchers.remove(keyEventDispatcher);
                }
            }
        }
    }
    
    protected synchronized List<KeyEventDispatcher> getKeyEventDispatchers() {
        return (this.keyEventDispatchers != null) ? ((List)this.keyEventDispatchers.clone()) : null;
    }
    
    public void addKeyEventPostProcessor(final KeyEventPostProcessor keyEventPostProcessor) {
        if (keyEventPostProcessor != null) {
            synchronized (this) {
                if (this.keyEventPostProcessors == null) {
                    this.keyEventPostProcessors = new LinkedList<KeyEventPostProcessor>();
                }
                this.keyEventPostProcessors.add(keyEventPostProcessor);
            }
        }
    }
    
    public void removeKeyEventPostProcessor(final KeyEventPostProcessor keyEventPostProcessor) {
        if (keyEventPostProcessor != null) {
            synchronized (this) {
                if (this.keyEventPostProcessors != null) {
                    this.keyEventPostProcessors.remove(keyEventPostProcessor);
                }
            }
        }
    }
    
    protected List<KeyEventPostProcessor> getKeyEventPostProcessors() {
        return (this.keyEventPostProcessors != null) ? ((List)this.keyEventPostProcessors.clone()) : null;
    }
    
    static void setMostRecentFocusOwner(final Component component) {
        Component parent;
        for (parent = component; parent != null && !(parent instanceof Window); parent = parent.parent) {}
        if (parent != null) {
            setMostRecentFocusOwner((Window)parent, component);
        }
    }
    
    static synchronized void setMostRecentFocusOwner(final Window window, final Component component) {
        WeakReference<Component> weakReference = null;
        if (component != null) {
            weakReference = new WeakReference<Component>(component);
        }
        KeyboardFocusManager.mostRecentFocusOwners.put(window, weakReference);
    }
    
    static void clearMostRecentFocusOwner(final Component component) {
        if (component == null) {
            return;
        }
        Container container;
        synchronized (component.getTreeLock()) {
            for (container = component.getParent(); container != null && !(container instanceof Window); container = container.getParent()) {}
        }
        synchronized (KeyboardFocusManager.class) {
            if (container != null && getMostRecentFocusOwner((Window)container) == component) {
                setMostRecentFocusOwner((Window)container, null);
            }
            if (container != null) {
                final Window window = (Window)container;
                if (window.getTemporaryLostComponent() == component) {
                    window.setTemporaryLostComponent(null);
                }
            }
        }
    }
    
    static synchronized Component getMostRecentFocusOwner(final Window window) {
        final WeakReference weakReference = KeyboardFocusManager.mostRecentFocusOwners.get(window);
        return (weakReference == null) ? null : ((Component)weakReference.get());
    }
    
    public abstract boolean dispatchEvent(final AWTEvent p0);
    
    public final void redispatchEvent(final Component component, final AWTEvent awtEvent) {
        awtEvent.focusManagerIsDispatching = true;
        component.dispatchEvent(awtEvent);
        awtEvent.focusManagerIsDispatching = false;
    }
    
    @Override
    public abstract boolean dispatchKeyEvent(final KeyEvent p0);
    
    @Override
    public abstract boolean postProcessKeyEvent(final KeyEvent p0);
    
    public abstract void processKeyEvent(final Component p0, final KeyEvent p1);
    
    protected abstract void enqueueKeyEvents(final long p0, final Component p1);
    
    protected abstract void dequeueKeyEvents(final long p0, final Component p1);
    
    protected abstract void discardKeyEvents(final Component p0);
    
    public abstract void focusNextComponent(final Component p0);
    
    public abstract void focusPreviousComponent(final Component p0);
    
    public abstract void upFocusCycle(final Component p0);
    
    public abstract void downFocusCycle(final Container p0);
    
    public final void focusNextComponent() {
        final Component focusOwner = this.getFocusOwner();
        if (focusOwner != null) {
            this.focusNextComponent(focusOwner);
        }
    }
    
    public final void focusPreviousComponent() {
        final Component focusOwner = this.getFocusOwner();
        if (focusOwner != null) {
            this.focusPreviousComponent(focusOwner);
        }
    }
    
    public final void upFocusCycle() {
        final Component focusOwner = this.getFocusOwner();
        if (focusOwner != null) {
            this.upFocusCycle(focusOwner);
        }
    }
    
    public final void downFocusCycle() {
        final Component focusOwner = this.getFocusOwner();
        if (focusOwner instanceof Container) {
            this.downFocusCycle((Container)focusOwner);
        }
    }
    
    void dumpRequests() {
        System.err.println(">>> Requests dump, time: " + System.currentTimeMillis());
        synchronized (KeyboardFocusManager.heavyweightRequests) {
            final Iterator<Object> iterator = KeyboardFocusManager.heavyweightRequests.iterator();
            while (iterator.hasNext()) {
                System.err.println(">>> Req: " + iterator.next());
            }
        }
        System.err.println("");
    }
    
    static boolean processSynchronousLightweightTransfer(final Component component, Component component2, final boolean b, final boolean b2, final long n) {
        final Window containingWindow = SunToolkit.getContainingWindow(component);
        if (containingWindow == null || !containingWindow.syncLWRequests) {
            return false;
        }
        if (component2 == null) {
            component2 = component;
        }
        final KeyboardFocusManager currentKeyboardFocusManager = getCurrentKeyboardFocusManager(SunToolkit.targetToAppContext(component2));
        FocusEvent focusEvent = null;
        FocusEvent focusEvent2 = null;
        final Component globalFocusOwner = currentKeyboardFocusManager.getGlobalFocusOwner();
        synchronized (KeyboardFocusManager.heavyweightRequests) {
            if (getLastHWRequest() == null && component == currentKeyboardFocusManager.getNativeFocusOwner() && KeyboardFocusManager.allowSyncFocusRequests) {
                if (component2 == globalFocusOwner) {
                    return true;
                }
                currentKeyboardFocusManager.enqueueKeyEvents(n, component2);
                KeyboardFocusManager.heavyweightRequests.add(new HeavyweightFocusRequest(component, component2, b, CausedFocusEvent.Cause.UNKNOWN));
                if (globalFocusOwner != null) {
                    focusEvent = new FocusEvent(globalFocusOwner, 1005, b, component2);
                }
                focusEvent2 = new FocusEvent(component2, 1004, b, globalFocusOwner);
            }
        }
        boolean b3 = false;
        final boolean clearingCurrentLightweightRequests = KeyboardFocusManager.clearingCurrentLightweightRequests;
        Throwable t = null;
        try {
            KeyboardFocusManager.clearingCurrentLightweightRequests = false;
            synchronized (Component.LOCK) {
                if (focusEvent != null && globalFocusOwner != null) {
                    focusEvent.isPosted = true;
                    t = dispatchAndCatchException(t, globalFocusOwner, focusEvent);
                    b3 = true;
                }
                if (focusEvent2 != null && component2 != null) {
                    focusEvent2.isPosted = true;
                    t = dispatchAndCatchException(t, component2, focusEvent2);
                    b3 = true;
                }
            }
        }
        finally {
            KeyboardFocusManager.clearingCurrentLightweightRequests = clearingCurrentLightweightRequests;
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        return b3;
    }
    
    static int shouldNativelyFocusHeavyweight(final Component component, Component component2, final boolean b, final boolean b2, final long n, final CausedFocusEvent.Cause cause) {
        if (KeyboardFocusManager.log.isLoggable(PlatformLogger.Level.FINE)) {
            if (component == null) {
                KeyboardFocusManager.log.fine("Assertion (heavyweight != null) failed");
            }
            if (n == 0L) {
                KeyboardFocusManager.log.fine("Assertion (time != 0) failed");
            }
        }
        if (component2 == null) {
            component2 = component;
        }
        final KeyboardFocusManager currentKeyboardFocusManager = getCurrentKeyboardFocusManager(SunToolkit.targetToAppContext(component2));
        final KeyboardFocusManager currentKeyboardFocusManager2 = getCurrentKeyboardFocusManager();
        final Component globalFocusOwner = currentKeyboardFocusManager2.getGlobalFocusOwner();
        final Component nativeFocusOwner = currentKeyboardFocusManager2.getNativeFocusOwner();
        final Window nativeFocusedWindow = currentKeyboardFocusManager2.getNativeFocusedWindow();
        if (KeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            KeyboardFocusManager.focusLog.finer("SNFH for {0} in {1}", String.valueOf(component2), String.valueOf(component));
        }
        if (KeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
            KeyboardFocusManager.focusLog.finest("0. Current focus owner {0}", String.valueOf(globalFocusOwner));
            KeyboardFocusManager.focusLog.finest("0. Native focus owner {0}", String.valueOf(nativeFocusOwner));
            KeyboardFocusManager.focusLog.finest("0. Native focused window {0}", String.valueOf(nativeFocusedWindow));
        }
        synchronized (KeyboardFocusManager.heavyweightRequests) {
            HeavyweightFocusRequest lastHWRequest = getLastHWRequest();
            if (KeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                KeyboardFocusManager.focusLog.finest("Request {0}", String.valueOf(lastHWRequest));
            }
            if (lastHWRequest == null && component == nativeFocusOwner && component.getContainingWindow() == nativeFocusedWindow) {
                if (component2 == globalFocusOwner) {
                    if (KeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                        KeyboardFocusManager.focusLog.finest("1. SNFH_FAILURE for {0}", String.valueOf(component2));
                    }
                    return 0;
                }
                currentKeyboardFocusManager.enqueueKeyEvents(n, component2);
                KeyboardFocusManager.heavyweightRequests.add(new HeavyweightFocusRequest(component, component2, b, cause));
                if (globalFocusOwner != null) {
                    SunToolkit.postEvent(globalFocusOwner.appContext, new CausedFocusEvent(globalFocusOwner, 1005, b, component2, cause));
                }
                SunToolkit.postEvent(component2.appContext, new CausedFocusEvent(component2, 1004, b, globalFocusOwner, cause));
                if (KeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                    KeyboardFocusManager.focusLog.finest("2. SNFH_HANDLED for {0}", String.valueOf(component2));
                }
                return 1;
            }
            else {
                if (lastHWRequest != null && lastHWRequest.heavyweight == component) {
                    if (lastHWRequest.addLightweightRequest(component2, b, cause)) {
                        currentKeyboardFocusManager.enqueueKeyEvents(n, component2);
                    }
                    if (KeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                        KeyboardFocusManager.focusLog.finest("3. SNFH_HANDLED for lightweight" + component2 + " in " + component);
                    }
                    return 1;
                }
                if (!b2) {
                    if (lastHWRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
                        final int size = KeyboardFocusManager.heavyweightRequests.size();
                        lastHWRequest = ((size >= 2) ? KeyboardFocusManager.heavyweightRequests.get(size - 2) : null);
                    }
                    if (focusedWindowChanged(component, (lastHWRequest != null) ? lastHWRequest.heavyweight : nativeFocusedWindow)) {
                        if (KeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                            KeyboardFocusManager.focusLog.finest("4. SNFH_FAILURE for " + component2);
                        }
                        return 0;
                    }
                }
                currentKeyboardFocusManager.enqueueKeyEvents(n, component2);
                KeyboardFocusManager.heavyweightRequests.add(new HeavyweightFocusRequest(component, component2, b, cause));
                if (KeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                    KeyboardFocusManager.focusLog.finest("5. SNFH_PROCEED for " + component2);
                }
                return 2;
            }
        }
    }
    
    static Window markClearGlobalFocusOwner() {
        final Window nativeFocusedWindow = getCurrentKeyboardFocusManager().getNativeFocusedWindow();
        synchronized (KeyboardFocusManager.heavyweightRequests) {
            final HeavyweightFocusRequest lastHWRequest = getLastHWRequest();
            if (lastHWRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
                return null;
            }
            KeyboardFocusManager.heavyweightRequests.add(HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER);
            Container parent_NoClientCode;
            for (parent_NoClientCode = ((lastHWRequest != null) ? SunToolkit.getContainingWindow(lastHWRequest.heavyweight) : nativeFocusedWindow); parent_NoClientCode != null && !(parent_NoClientCode instanceof Frame) && !(parent_NoClientCode instanceof Dialog); parent_NoClientCode = parent_NoClientCode.getParent_NoClientCode()) {}
            return (Window)parent_NoClientCode;
        }
    }
    
    Component getCurrentWaitingRequest(final Component component) {
        synchronized (KeyboardFocusManager.heavyweightRequests) {
            final HeavyweightFocusRequest firstHWRequest = getFirstHWRequest();
            if (firstHWRequest != null && firstHWRequest.heavyweight == component) {
                final LightweightFocusRequest lightweightFocusRequest = firstHWRequest.lightweightRequests.getFirst();
                if (lightweightFocusRequest != null) {
                    return lightweightFocusRequest.component;
                }
            }
        }
        return null;
    }
    
    static boolean isAutoFocusTransferEnabled() {
        synchronized (KeyboardFocusManager.heavyweightRequests) {
            return KeyboardFocusManager.heavyweightRequests.size() == 0 && !KeyboardFocusManager.disableRestoreFocus && null == KeyboardFocusManager.currentLightweightRequests;
        }
    }
    
    static boolean isAutoFocusTransferEnabledFor(final Component component) {
        return isAutoFocusTransferEnabled() && component.isAutoFocusTransferOnDisposal();
    }
    
    private static Throwable dispatchAndCatchException(final Throwable t, final Component component, final FocusEvent focusEvent) {
        Throwable t2 = null;
        try {
            component.dispatchEvent(focusEvent);
        }
        catch (final RuntimeException ex) {
            t2 = ex;
        }
        catch (final Error error) {
            t2 = error;
        }
        if (t2 != null) {
            if (t != null) {
                handleException(t);
            }
            return t2;
        }
        return t;
    }
    
    private static void handleException(final Throwable t) {
        t.printStackTrace();
    }
    
    static void processCurrentLightweightRequests() {
        final KeyboardFocusManager currentKeyboardFocusManager = getCurrentKeyboardFocusManager();
        LinkedList<LightweightFocusRequest> currentLightweightRequests = null;
        final Component globalFocusOwner = currentKeyboardFocusManager.getGlobalFocusOwner();
        if (globalFocusOwner != null && globalFocusOwner.appContext != AppContext.getAppContext()) {
            return;
        }
        synchronized (KeyboardFocusManager.heavyweightRequests) {
            if (KeyboardFocusManager.currentLightweightRequests == null) {
                return;
            }
            KeyboardFocusManager.clearingCurrentLightweightRequests = true;
            KeyboardFocusManager.disableRestoreFocus = true;
            currentLightweightRequests = KeyboardFocusManager.currentLightweightRequests;
            KeyboardFocusManager.allowSyncFocusRequests = (currentLightweightRequests.size() < 2);
            KeyboardFocusManager.currentLightweightRequests = null;
        }
        Throwable t = null;
        try {
            if (currentLightweightRequests != null) {
                Component component = null;
                final Iterator<Object> iterator = currentLightweightRequests.iterator();
                while (iterator.hasNext()) {
                    final Component globalFocusOwner2 = currentKeyboardFocusManager.getGlobalFocusOwner();
                    final LightweightFocusRequest lightweightFocusRequest = iterator.next();
                    if (!iterator.hasNext()) {
                        KeyboardFocusManager.disableRestoreFocus = false;
                    }
                    FocusEvent focusEvent = null;
                    if (globalFocusOwner2 != null) {
                        focusEvent = new CausedFocusEvent(globalFocusOwner2, 1005, lightweightFocusRequest.temporary, lightweightFocusRequest.component, lightweightFocusRequest.cause);
                    }
                    final CausedFocusEvent causedFocusEvent = new CausedFocusEvent(lightweightFocusRequest.component, 1004, lightweightFocusRequest.temporary, (globalFocusOwner2 == null) ? component : globalFocusOwner2, lightweightFocusRequest.cause);
                    if (globalFocusOwner2 != null) {
                        focusEvent.isPosted = true;
                        t = dispatchAndCatchException(t, globalFocusOwner2, focusEvent);
                    }
                    causedFocusEvent.isPosted = true;
                    t = dispatchAndCatchException(t, lightweightFocusRequest.component, causedFocusEvent);
                    if (currentKeyboardFocusManager.getGlobalFocusOwner() == lightweightFocusRequest.component) {
                        component = lightweightFocusRequest.component;
                    }
                }
            }
        }
        finally {
            KeyboardFocusManager.clearingCurrentLightweightRequests = false;
            KeyboardFocusManager.disableRestoreFocus = false;
            KeyboardFocusManager.allowSyncFocusRequests = true;
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
    }
    
    static FocusEvent retargetUnexpectedFocusEvent(final FocusEvent focusEvent) {
        synchronized (KeyboardFocusManager.heavyweightRequests) {
            if (removeFirstRequest()) {
                return (FocusEvent)retargetFocusEvent(focusEvent);
            }
            final Component component = focusEvent.getComponent();
            final Component oppositeComponent = focusEvent.getOppositeComponent();
            boolean b = false;
            if (focusEvent.getID() == 1005 && (oppositeComponent == null || isTemporary(oppositeComponent, component))) {
                b = true;
            }
            return new CausedFocusEvent(component, focusEvent.getID(), b, oppositeComponent, CausedFocusEvent.Cause.NATIVE_SYSTEM);
        }
    }
    
    static FocusEvent retargetFocusGained(final FocusEvent focusEvent) {
        assert focusEvent.getID() == 1004;
        final Component globalFocusOwner = getCurrentKeyboardFocusManager().getGlobalFocusOwner();
        Component component = focusEvent.getComponent();
        final Component oppositeComponent = focusEvent.getOppositeComponent();
        Component component2 = getHeavyweight(component);
        synchronized (KeyboardFocusManager.heavyweightRequests) {
            final HeavyweightFocusRequest firstHWRequest = getFirstHWRequest();
            if (firstHWRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
                return retargetUnexpectedFocusEvent(focusEvent);
            }
            if (component != null && component2 == null && firstHWRequest != null && component == firstHWRequest.getFirstLightweightRequest().component) {
                component = (component2 = firstHWRequest.heavyweight);
            }
            if (firstHWRequest != null && component2 == firstHWRequest.heavyweight) {
                KeyboardFocusManager.heavyweightRequests.removeFirst();
                final LightweightFocusRequest lightweightFocusRequest = firstHWRequest.lightweightRequests.removeFirst();
                final Component component3 = lightweightFocusRequest.component;
                if (globalFocusOwner != null) {
                    KeyboardFocusManager.newFocusOwner = component3;
                }
                final boolean b = oppositeComponent != null && !isTemporary(component3, oppositeComponent) && lightweightFocusRequest.temporary;
                if (firstHWRequest.lightweightRequests.size() > 0) {
                    KeyboardFocusManager.currentLightweightRequests = firstHWRequest.lightweightRequests;
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            KeyboardFocusManager.processCurrentLightweightRequests();
                        }
                    });
                }
                return new CausedFocusEvent(component3, 1004, b, oppositeComponent, lightweightFocusRequest.cause);
            }
            if (globalFocusOwner != null && globalFocusOwner.getContainingWindow() == component && (firstHWRequest == null || component != firstHWRequest.heavyweight)) {
                return new CausedFocusEvent(globalFocusOwner, 1004, false, null, CausedFocusEvent.Cause.ACTIVATION);
            }
            return retargetUnexpectedFocusEvent(focusEvent);
        }
    }
    
    static FocusEvent retargetFocusLost(FocusEvent focusEvent) {
        assert focusEvent.getID() == 1005;
        final Component globalFocusOwner = getCurrentKeyboardFocusManager().getGlobalFocusOwner();
        final Component oppositeComponent = focusEvent.getOppositeComponent();
        final Component heavyweight = getHeavyweight(oppositeComponent);
        synchronized (KeyboardFocusManager.heavyweightRequests) {
            final HeavyweightFocusRequest firstHWRequest = getFirstHWRequest();
            if (firstHWRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
                if (globalFocusOwner != null) {
                    KeyboardFocusManager.heavyweightRequests.removeFirst();
                    return new CausedFocusEvent(globalFocusOwner, 1005, false, null, CausedFocusEvent.Cause.CLEAR_GLOBAL_FOCUS_OWNER);
                }
            }
            else if (oppositeComponent == null) {
                if (globalFocusOwner != null) {
                    return new CausedFocusEvent(globalFocusOwner, 1005, true, null, CausedFocusEvent.Cause.ACTIVATION);
                }
                return focusEvent;
            }
            else if (firstHWRequest != null && (heavyweight == firstHWRequest.heavyweight || (heavyweight == null && oppositeComponent == firstHWRequest.getFirstLightweightRequest().component))) {
                if (globalFocusOwner == null) {
                    return focusEvent;
                }
                final LightweightFocusRequest lightweightFocusRequest = firstHWRequest.lightweightRequests.getFirst();
                return new CausedFocusEvent(globalFocusOwner, 1005, isTemporary(oppositeComponent, globalFocusOwner) || lightweightFocusRequest.temporary, lightweightFocusRequest.component, lightweightFocusRequest.cause);
            }
            else if (focusedWindowChanged(oppositeComponent, globalFocusOwner)) {
                if (!focusEvent.isTemporary() && globalFocusOwner != null) {
                    focusEvent = new CausedFocusEvent(globalFocusOwner, 1005, true, oppositeComponent, CausedFocusEvent.Cause.ACTIVATION);
                }
                return focusEvent;
            }
            return retargetUnexpectedFocusEvent(focusEvent);
        }
    }
    
    static AWTEvent retargetFocusEvent(AWTEvent awtEvent) {
        if (KeyboardFocusManager.clearingCurrentLightweightRequests) {
            return awtEvent;
        }
        final KeyboardFocusManager currentKeyboardFocusManager = getCurrentKeyboardFocusManager();
        if (KeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            if (awtEvent instanceof FocusEvent || awtEvent instanceof WindowEvent) {
                KeyboardFocusManager.focusLog.finer(">>> {0}", String.valueOf(awtEvent));
            }
            if (KeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINER) && awtEvent instanceof KeyEvent) {
                KeyboardFocusManager.focusLog.finer("    focus owner is {0}", String.valueOf(currentKeyboardFocusManager.getGlobalFocusOwner()));
                KeyboardFocusManager.focusLog.finer(">>> {0}", String.valueOf(awtEvent));
            }
        }
        synchronized (KeyboardFocusManager.heavyweightRequests) {
            if (KeyboardFocusManager.newFocusOwner != null && awtEvent.getID() == 1005) {
                final FocusEvent focusEvent = (FocusEvent)awtEvent;
                if (currentKeyboardFocusManager.getGlobalFocusOwner() == focusEvent.getComponent() && focusEvent.getOppositeComponent() == KeyboardFocusManager.newFocusOwner) {
                    KeyboardFocusManager.newFocusOwner = null;
                    return awtEvent;
                }
            }
        }
        processCurrentLightweightRequests();
        switch (awtEvent.getID()) {
            case 1004: {
                awtEvent = retargetFocusGained((FocusEvent)awtEvent);
                break;
            }
            case 1005: {
                awtEvent = retargetFocusLost((FocusEvent)awtEvent);
                break;
            }
        }
        return awtEvent;
    }
    
    void clearMarkers() {
    }
    
    static boolean removeFirstRequest() {
        final KeyboardFocusManager currentKeyboardFocusManager = getCurrentKeyboardFocusManager();
        synchronized (KeyboardFocusManager.heavyweightRequests) {
            final HeavyweightFocusRequest firstHWRequest = getFirstHWRequest();
            if (firstHWRequest != null) {
                KeyboardFocusManager.heavyweightRequests.removeFirst();
                if (firstHWRequest.lightweightRequests != null) {
                    final Iterator<Object> iterator = firstHWRequest.lightweightRequests.iterator();
                    while (iterator.hasNext()) {
                        currentKeyboardFocusManager.dequeueKeyEvents(-1L, iterator.next().component);
                    }
                }
            }
            if (KeyboardFocusManager.heavyweightRequests.size() == 0) {
                currentKeyboardFocusManager.clearMarkers();
            }
            return KeyboardFocusManager.heavyweightRequests.size() > 0;
        }
    }
    
    static void removeLastFocusRequest(final Component component) {
        if (KeyboardFocusManager.log.isLoggable(PlatformLogger.Level.FINE) && component == null) {
            KeyboardFocusManager.log.fine("Assertion (heavyweight != null) failed");
        }
        final KeyboardFocusManager currentKeyboardFocusManager = getCurrentKeyboardFocusManager();
        synchronized (KeyboardFocusManager.heavyweightRequests) {
            final HeavyweightFocusRequest lastHWRequest = getLastHWRequest();
            if (lastHWRequest != null && lastHWRequest.heavyweight == component) {
                KeyboardFocusManager.heavyweightRequests.removeLast();
            }
            if (KeyboardFocusManager.heavyweightRequests.size() == 0) {
                currentKeyboardFocusManager.clearMarkers();
            }
        }
    }
    
    private static boolean focusedWindowChanged(final Component component, final Component component2) {
        final Window containingWindow = SunToolkit.getContainingWindow(component);
        final Window containingWindow2 = SunToolkit.getContainingWindow(component2);
        return (containingWindow == null && containingWindow2 == null) || containingWindow == null || containingWindow2 == null || containingWindow != containingWindow2;
    }
    
    private static boolean isTemporary(final Component component, final Component component2) {
        final Window containingWindow = SunToolkit.getContainingWindow(component);
        final Window containingWindow2 = SunToolkit.getContainingWindow(component2);
        return (containingWindow != null || containingWindow2 != null) && (containingWindow == null || (containingWindow2 != null && containingWindow != containingWindow2));
    }
    
    static Component getHeavyweight(final Component component) {
        if (component == null || component.getPeer() == null) {
            return null;
        }
        if (component.getPeer() instanceof LightweightPeer) {
            return component.getNativeContainer();
        }
        return component;
    }
    
    private static boolean isProxyActiveImpl(final KeyEvent keyEvent) {
        if (KeyboardFocusManager.proxyActive == null) {
            KeyboardFocusManager.proxyActive = AccessController.doPrivileged((PrivilegedAction<Field>)new PrivilegedAction<Field>() {
                @Override
                public Field run() {
                    AccessibleObject declaredField = null;
                    try {
                        declaredField = KeyEvent.class.getDeclaredField("isProxyActive");
                        if (declaredField != null) {
                            declaredField.setAccessible(true);
                        }
                    }
                    catch (final NoSuchFieldException ex) {
                        assert false;
                    }
                    return (Field)declaredField;
                }
            });
        }
        try {
            return KeyboardFocusManager.proxyActive.getBoolean(keyEvent);
        }
        catch (final IllegalAccessException ex) {
            assert false;
            return false;
        }
    }
    
    static boolean isProxyActive(final KeyEvent keyEvent) {
        return !GraphicsEnvironment.isHeadless() && isProxyActiveImpl(keyEvent);
    }
    
    private static HeavyweightFocusRequest getLastHWRequest() {
        synchronized (KeyboardFocusManager.heavyweightRequests) {
            return (KeyboardFocusManager.heavyweightRequests.size() > 0) ? KeyboardFocusManager.heavyweightRequests.getLast() : null;
        }
    }
    
    private static HeavyweightFocusRequest getFirstHWRequest() {
        synchronized (KeyboardFocusManager.heavyweightRequests) {
            return (KeyboardFocusManager.heavyweightRequests.size() > 0) ? KeyboardFocusManager.heavyweightRequests.getFirst() : null;
        }
    }
    
    private static void checkReplaceKFMPermission() throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            if (KeyboardFocusManager.replaceKeyboardFocusManagerPermission == null) {
                KeyboardFocusManager.replaceKeyboardFocusManagerPermission = new AWTPermission("replaceKeyboardFocusManager");
            }
            securityManager.checkPermission(KeyboardFocusManager.replaceKeyboardFocusManagerPermission);
        }
    }
    
    private void checkKFMSecurity() throws SecurityException {
        if (this != getCurrentKeyboardFocusManager()) {
            checkReplaceKFMPermission();
        }
    }
    
    static {
        focusLog = PlatformLogger.getLogger("java.awt.focus.KeyboardFocusManager");
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        AWTAccessor.setKeyboardFocusManagerAccessor(new AWTAccessor.KeyboardFocusManagerAccessor() {
            @Override
            public int shouldNativelyFocusHeavyweight(final Component component, final Component component2, final boolean b, final boolean b2, final long n, final CausedFocusEvent.Cause cause) {
                return KeyboardFocusManager.shouldNativelyFocusHeavyweight(component, component2, b, b2, n, cause);
            }
            
            @Override
            public boolean processSynchronousLightweightTransfer(final Component component, final Component component2, final boolean b, final boolean b2, final long n) {
                return KeyboardFocusManager.processSynchronousLightweightTransfer(component, component2, b, b2, n);
            }
            
            @Override
            public void removeLastFocusRequest(final Component component) {
                KeyboardFocusManager.removeLastFocusRequest(component);
            }
            
            @Override
            public void setMostRecentFocusOwner(final Window window, final Component component) {
                KeyboardFocusManager.setMostRecentFocusOwner(window, component);
            }
            
            @Override
            public KeyboardFocusManager getCurrentKeyboardFocusManager(final AppContext appContext) {
                return KeyboardFocusManager.getCurrentKeyboardFocusManager(appContext);
            }
            
            @Override
            public Container getCurrentFocusCycleRoot() {
                return KeyboardFocusManager.currentFocusCycleRoot;
            }
        });
        log = PlatformLogger.getLogger("java.awt.KeyboardFocusManager");
        defaultFocusTraversalKeyPropertyNames = new String[] { "forwardDefaultFocusTraversalKeys", "backwardDefaultFocusTraversalKeys", "upCycleDefaultFocusTraversalKeys", "downCycleDefaultFocusTraversalKeys" };
        defaultFocusTraversalKeyStrokes = new AWTKeyStroke[][] { { AWTKeyStroke.getAWTKeyStroke(9, 0, false), AWTKeyStroke.getAWTKeyStroke(9, 130, false) }, { AWTKeyStroke.getAWTKeyStroke(9, 65, false), AWTKeyStroke.getAWTKeyStroke(9, 195, false) }, new AWTKeyStroke[0], new AWTKeyStroke[0] };
        KeyboardFocusManager.mostRecentFocusOwners = new WeakHashMap<Window, WeakReference<Component>>();
        KeyboardFocusManager.heavyweightRequests = new LinkedList<HeavyweightFocusRequest>();
        KeyboardFocusManager.allowSyncFocusRequests = true;
        KeyboardFocusManager.newFocusOwner = null;
    }
    
    private static final class LightweightFocusRequest
    {
        final Component component;
        final boolean temporary;
        final CausedFocusEvent.Cause cause;
        
        LightweightFocusRequest(final Component component, final boolean temporary, final CausedFocusEvent.Cause cause) {
            this.component = component;
            this.temporary = temporary;
            this.cause = cause;
        }
        
        @Override
        public String toString() {
            return "LightweightFocusRequest[component=" + this.component + ",temporary=" + this.temporary + ", cause=" + this.cause + "]";
        }
    }
    
    private static final class HeavyweightFocusRequest
    {
        final Component heavyweight;
        final LinkedList<LightweightFocusRequest> lightweightRequests;
        static final HeavyweightFocusRequest CLEAR_GLOBAL_FOCUS_OWNER;
        
        private HeavyweightFocusRequest() {
            this.heavyweight = null;
            this.lightweightRequests = null;
        }
        
        HeavyweightFocusRequest(final Component heavyweight, final Component component, final boolean b, final CausedFocusEvent.Cause cause) {
            if (KeyboardFocusManager.log.isLoggable(PlatformLogger.Level.FINE) && heavyweight == null) {
                KeyboardFocusManager.log.fine("Assertion (heavyweight != null) failed");
            }
            this.heavyweight = heavyweight;
            this.lightweightRequests = new LinkedList<LightweightFocusRequest>();
            this.addLightweightRequest(component, b, cause);
        }
        
        boolean addLightweightRequest(final Component component, final boolean b, final CausedFocusEvent.Cause cause) {
            if (KeyboardFocusManager.log.isLoggable(PlatformLogger.Level.FINE)) {
                if (this == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
                    KeyboardFocusManager.log.fine("Assertion (this != HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) failed");
                }
                if (component == null) {
                    KeyboardFocusManager.log.fine("Assertion (descendant != null) failed");
                }
            }
            if (component != ((this.lightweightRequests.size() > 0) ? this.lightweightRequests.getLast().component : null)) {
                this.lightweightRequests.add(new LightweightFocusRequest(component, b, cause));
                return true;
            }
            return false;
        }
        
        LightweightFocusRequest getFirstLightweightRequest() {
            if (this == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
                return null;
            }
            return this.lightweightRequests.getFirst();
        }
        
        @Override
        public String toString() {
            int n = 1;
            final String string = "HeavyweightFocusRequest[heavweight=" + this.heavyweight + ",lightweightRequests=";
            String s;
            if (this.lightweightRequests == null) {
                s = string + (Object)null;
            }
            else {
                String s2 = string + "[";
                for (final LightweightFocusRequest lightweightFocusRequest : this.lightweightRequests) {
                    if (n != 0) {
                        n = 0;
                    }
                    else {
                        s2 += ",";
                    }
                    s2 += lightweightFocusRequest;
                }
                s = s2 + "]";
            }
            return s + "]";
        }
        
        static {
            CLEAR_GLOBAL_FOCUS_OWNER = new HeavyweightFocusRequest();
        }
    }
}
