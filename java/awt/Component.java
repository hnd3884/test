package java.awt;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleComponent;
import sun.awt.SubRegionShowable;
import sun.java2d.SunGraphics2D;
import sun.awt.image.VSyncedBSManager;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
import java.util.WeakHashMap;
import java.awt.geom.AffineTransform;
import sun.awt.AWTAccessor;
import sun.security.action.GetPropertyAction;
import javax.accessibility.AccessibleSelection;
import javax.swing.JComponent;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.Accessible;
import java.io.OptionalDataException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.util.Objects;
import java.applet.Applet;
import sun.awt.EmbeddedFrame;
import sun.awt.CausedFocusEvent;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.awt.im.InputMethodRequests;
import java.beans.PropertyChangeListener;
import java.util.EventListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import sun.awt.WindowClosingListener;
import java.awt.event.InputEvent;
import sun.awt.im.CompositionArea;
import java.awt.event.InputMethodEvent;
import java.awt.event.MouseWheelEvent;
import sun.awt.dnd.SunDropTargetEvent;
import java.awt.event.KeyEvent;
import sun.java2d.SunGraphicsEnvironment;
import java.awt.image.VolatileImage;
import java.awt.image.ImageProducer;
import java.awt.peer.ContainerPeer;
import java.awt.event.PaintEvent;
import sun.awt.SunToolkit;
import sun.font.FontManager;
import sun.font.FontDesignMetrics;
import sun.font.SunFontManager;
import sun.font.FontManagerFactory;
import sun.awt.ConstrainableGraphics;
import java.awt.image.ColorModel;
import java.awt.event.ComponentEvent;
import java.awt.peer.LightweightPeer;
import java.awt.im.InputContext;
import java.awt.event.FocusEvent;
import javax.accessibility.AccessibleState;
import java.security.PrivilegedAction;
import java.beans.Transient;
import java.security.AccessController;
import javax.accessibility.AccessibleContext;
import sun.awt.RequestFocusController;
import java.util.Map;
import sun.awt.EventQueueItem;
import sun.java2d.pipe.Region;
import java.beans.PropertyChangeSupport;
import java.awt.event.InputMethodListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.FocusListener;
import java.awt.event.ComponentListener;
import java.security.AccessControlContext;
import java.util.Set;
import java.util.Vector;
import java.awt.dnd.DropTarget;
import java.awt.image.BufferStrategy;
import java.util.Locale;
import sun.awt.AppContext;
import java.awt.peer.ComponentPeer;
import sun.util.logging.PlatformLogger;
import java.io.Serializable;
import java.awt.image.ImageObserver;

public abstract class Component implements ImageObserver, MenuContainer, Serializable
{
    private static final PlatformLogger log;
    private static final PlatformLogger eventLog;
    private static final PlatformLogger focusLog;
    private static final PlatformLogger mixingLog;
    transient ComponentPeer peer;
    transient Container parent;
    transient AppContext appContext;
    int x;
    int y;
    int width;
    int height;
    Color foreground;
    Color background;
    volatile Font font;
    Font peerFont;
    Cursor cursor;
    Locale locale;
    private transient volatile GraphicsConfiguration graphicsConfig;
    transient BufferStrategy bufferStrategy;
    boolean ignoreRepaint;
    boolean visible;
    boolean enabled;
    private volatile boolean valid;
    DropTarget dropTarget;
    Vector<PopupMenu> popups;
    private String name;
    private boolean nameExplicitlySet;
    private boolean focusable;
    private static final int FOCUS_TRAVERSABLE_UNKNOWN = 0;
    private static final int FOCUS_TRAVERSABLE_DEFAULT = 1;
    private static final int FOCUS_TRAVERSABLE_SET = 2;
    private int isFocusTraversableOverridden;
    Set<AWTKeyStroke>[] focusTraversalKeys;
    private static final String[] focusTraversalKeyPropertyNames;
    private boolean focusTraversalKeysEnabled;
    static final Object LOCK;
    private transient volatile AccessControlContext acc;
    Dimension minSize;
    boolean minSizeSet;
    Dimension prefSize;
    boolean prefSizeSet;
    Dimension maxSize;
    boolean maxSizeSet;
    transient ComponentOrientation componentOrientation;
    boolean newEventsOnly;
    transient ComponentListener componentListener;
    transient FocusListener focusListener;
    transient HierarchyListener hierarchyListener;
    transient HierarchyBoundsListener hierarchyBoundsListener;
    transient KeyListener keyListener;
    transient MouseListener mouseListener;
    transient MouseMotionListener mouseMotionListener;
    transient MouseWheelListener mouseWheelListener;
    transient InputMethodListener inputMethodListener;
    transient RuntimeException windowClosingException;
    static final String actionListenerK = "actionL";
    static final String adjustmentListenerK = "adjustmentL";
    static final String componentListenerK = "componentL";
    static final String containerListenerK = "containerL";
    static final String focusListenerK = "focusL";
    static final String itemListenerK = "itemL";
    static final String keyListenerK = "keyL";
    static final String mouseListenerK = "mouseL";
    static final String mouseMotionListenerK = "mouseMotionL";
    static final String mouseWheelListenerK = "mouseWheelL";
    static final String textListenerK = "textL";
    static final String ownedWindowK = "ownedL";
    static final String windowListenerK = "windowL";
    static final String inputMethodListenerK = "inputMethodL";
    static final String hierarchyListenerK = "hierarchyL";
    static final String hierarchyBoundsListenerK = "hierarchyBoundsL";
    static final String windowStateListenerK = "windowStateL";
    static final String windowFocusListenerK = "windowFocusL";
    long eventMask;
    static boolean isInc;
    static int incRate;
    public static final float TOP_ALIGNMENT = 0.0f;
    public static final float CENTER_ALIGNMENT = 0.5f;
    public static final float BOTTOM_ALIGNMENT = 1.0f;
    public static final float LEFT_ALIGNMENT = 0.0f;
    public static final float RIGHT_ALIGNMENT = 1.0f;
    private static final long serialVersionUID = -7644114512714619750L;
    private PropertyChangeSupport changeSupport;
    private transient Object objectLock;
    boolean isPacked;
    private int boundsOp;
    private transient Region compoundShape;
    private transient Region mixingCutoutRegion;
    private transient boolean isAddNotifyComplete;
    transient boolean backgroundEraseDisabled;
    transient EventQueueItem[] eventCache;
    private transient boolean coalescingEnabled;
    private static final Map<Class<?>, Boolean> coalesceMap;
    private static final Class[] coalesceEventsParams;
    private static RequestFocusController requestFocusController;
    private boolean autoFocusTransferOnDisposal;
    private int componentSerializedDataVersion;
    protected AccessibleContext accessibleContext;
    
    Object getObjectLock() {
        return this.objectLock;
    }
    
    final AccessControlContext getAccessControlContext() {
        if (this.acc == null) {
            throw new SecurityException("Component is missing AccessControlContext");
        }
        return this.acc;
    }
    
    int getBoundsOp() {
        assert Thread.holdsLock(this.getTreeLock());
        return this.boundsOp;
    }
    
    void setBoundsOp(final int boundsOp) {
        assert Thread.holdsLock(this.getTreeLock());
        if (boundsOp == 5) {
            this.boundsOp = 3;
        }
        else if (this.boundsOp == 3) {
            this.boundsOp = boundsOp;
        }
    }
    
    protected Component() {
        this.bufferStrategy = null;
        this.ignoreRepaint = false;
        this.visible = true;
        this.enabled = true;
        this.valid = false;
        this.nameExplicitlySet = false;
        this.focusable = true;
        this.isFocusTraversableOverridden = 0;
        this.focusTraversalKeysEnabled = true;
        this.acc = AccessController.getContext();
        this.componentOrientation = ComponentOrientation.UNKNOWN;
        this.newEventsOnly = false;
        this.windowClosingException = null;
        this.eventMask = 4096L;
        this.objectLock = new Object();
        this.isPacked = false;
        this.boundsOp = 3;
        this.compoundShape = null;
        this.mixingCutoutRegion = null;
        this.isAddNotifyComplete = false;
        this.coalescingEnabled = this.checkCoalescing();
        this.autoFocusTransferOnDisposal = true;
        this.componentSerializedDataVersion = 4;
        this.accessibleContext = null;
        this.appContext = AppContext.getAppContext();
    }
    
    void initializeFocusTraversalKeys() {
        this.focusTraversalKeys = new Set[3];
    }
    
    String constructComponentName() {
        return null;
    }
    
    public String getName() {
        if (this.name == null && !this.nameExplicitlySet) {
            synchronized (this.getObjectLock()) {
                if (this.name == null && !this.nameExplicitlySet) {
                    this.name = this.constructComponentName();
                }
            }
        }
        return this.name;
    }
    
    public void setName(final String name) {
        final String name2;
        synchronized (this.getObjectLock()) {
            name2 = this.name;
            this.name = name;
            this.nameExplicitlySet = true;
        }
        this.firePropertyChange("name", name2, name);
    }
    
    public Container getParent() {
        return this.getParent_NoClientCode();
    }
    
    final Container getParent_NoClientCode() {
        return this.parent;
    }
    
    Container getContainer() {
        return this.getParent_NoClientCode();
    }
    
    @Deprecated
    public ComponentPeer getPeer() {
        return this.peer;
    }
    
    public synchronized void setDropTarget(final DropTarget dropTarget) {
        if (dropTarget == this.dropTarget || (this.dropTarget != null && this.dropTarget.equals(dropTarget))) {
            return;
        }
        final DropTarget dropTarget2;
        if ((dropTarget2 = this.dropTarget) != null) {
            if (this.peer != null) {
                this.dropTarget.removeNotify(this.peer);
            }
            final DropTarget dropTarget3 = this.dropTarget;
            this.dropTarget = null;
            try {
                dropTarget3.setComponent(null);
            }
            catch (final IllegalArgumentException ex) {}
        }
        if ((this.dropTarget = dropTarget) != null) {
            try {
                this.dropTarget.setComponent(this);
                if (this.peer != null) {
                    this.dropTarget.addNotify(this.peer);
                }
            }
            catch (final IllegalArgumentException ex2) {
                if (dropTarget2 != null) {
                    try {
                        dropTarget2.setComponent(this);
                        if (this.peer != null) {
                            this.dropTarget.addNotify(this.peer);
                        }
                    }
                    catch (final IllegalArgumentException ex3) {}
                }
            }
        }
    }
    
    public synchronized DropTarget getDropTarget() {
        return this.dropTarget;
    }
    
    public GraphicsConfiguration getGraphicsConfiguration() {
        return this.getGraphicsConfiguration_NoClientCode();
    }
    
    final GraphicsConfiguration getGraphicsConfiguration_NoClientCode() {
        return this.graphicsConfig;
    }
    
    void setGraphicsConfiguration(final GraphicsConfiguration graphicsConfiguration) {
        synchronized (this.getTreeLock()) {
            if (this.updateGraphicsData(graphicsConfiguration)) {
                this.removeNotify();
                this.addNotify();
            }
        }
    }
    
    boolean updateGraphicsData(final GraphicsConfiguration graphicsConfig) {
        this.checkTreeLock();
        if (this.graphicsConfig == graphicsConfig) {
            return false;
        }
        this.graphicsConfig = graphicsConfig;
        final ComponentPeer peer = this.getPeer();
        return peer != null && peer.updateGraphicsData(graphicsConfig);
    }
    
    void checkGD(final String s) {
        if (this.graphicsConfig != null && !this.graphicsConfig.getDevice().getIDstring().equals(s)) {
            throw new IllegalArgumentException("adding a container to a container on a different GraphicsDevice");
        }
    }
    
    public final Object getTreeLock() {
        return Component.LOCK;
    }
    
    final void checkTreeLock() {
        if (!Thread.holdsLock(this.getTreeLock())) {
            throw new IllegalStateException("This function should be called while holding treeLock");
        }
    }
    
    public Toolkit getToolkit() {
        return this.getToolkitImpl();
    }
    
    final Toolkit getToolkitImpl() {
        final Container parent = this.parent;
        if (parent != null) {
            return parent.getToolkitImpl();
        }
        return Toolkit.getDefaultToolkit();
    }
    
    public boolean isValid() {
        return this.peer != null && this.valid;
    }
    
    public boolean isDisplayable() {
        return this.getPeer() != null;
    }
    
    @Transient
    public boolean isVisible() {
        return this.isVisible_NoClientCode();
    }
    
    final boolean isVisible_NoClientCode() {
        return this.visible;
    }
    
    boolean isRecursivelyVisible() {
        return this.visible && (this.parent == null || this.parent.isRecursivelyVisible());
    }
    
    private Rectangle getRecursivelyVisibleBounds() {
        final Container container = this.getContainer();
        final Rectangle bounds = this.getBounds();
        if (container == null) {
            return bounds;
        }
        final Rectangle recursivelyVisibleBounds = container.getRecursivelyVisibleBounds();
        recursivelyVisibleBounds.setLocation(0, 0);
        return recursivelyVisibleBounds.intersection(bounds);
    }
    
    Point pointRelativeToComponent(final Point point) {
        final Point locationOnScreen = this.getLocationOnScreen();
        return new Point(point.x - locationOnScreen.x, point.y - locationOnScreen.y);
    }
    
    Component findUnderMouseInWindow(final PointerInfo pointerInfo) {
        if (!this.isShowing()) {
            return null;
        }
        final Window containingWindow = this.getContainingWindow();
        if (!Toolkit.getDefaultToolkit().getMouseInfoPeer().isWindowUnderMouse(containingWindow)) {
            return null;
        }
        final Point pointRelativeToComponent = containingWindow.pointRelativeToComponent(pointerInfo.getLocation());
        return containingWindow.findComponentAt(pointRelativeToComponent.x, pointRelativeToComponent.y, true);
    }
    
    public Point getMousePosition() throws HeadlessException {
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
            if (!this.isSameOrAncestorOf(this.findUnderMouseInWindow(pointerInfo), true)) {
                return null;
            }
            return this.pointRelativeToComponent(pointerInfo.getLocation());
        }
    }
    
    boolean isSameOrAncestorOf(final Component component, final boolean b) {
        return component == this;
    }
    
    public boolean isShowing() {
        if (this.visible && this.peer != null) {
            final Container parent = this.parent;
            return parent == null || parent.isShowing();
        }
        return false;
    }
    
    public boolean isEnabled() {
        return this.isEnabledImpl();
    }
    
    final boolean isEnabledImpl() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean b) {
        this.enable(b);
    }
    
    @Deprecated
    public void enable() {
        if (!this.enabled) {
            synchronized (this.getTreeLock()) {
                this.enabled = true;
                final ComponentPeer peer = this.peer;
                if (peer != null) {
                    peer.setEnabled(true);
                    if (this.visible && !this.getRecursivelyVisibleBounds().isEmpty()) {
                        this.updateCursorImmediately();
                    }
                }
            }
            if (this.accessibleContext != null) {
                this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.ENABLED);
            }
        }
    }
    
    @Deprecated
    public void enable(final boolean b) {
        if (b) {
            this.enable();
        }
        else {
            this.disable();
        }
    }
    
    @Deprecated
    public void disable() {
        if (this.enabled) {
            KeyboardFocusManager.clearMostRecentFocusOwner(this);
            synchronized (this.getTreeLock()) {
                this.enabled = false;
                if ((this.isFocusOwner() || (this.containsFocus() && !this.isLightweight())) && KeyboardFocusManager.isAutoFocusTransferEnabled()) {
                    this.transferFocus(false);
                }
                final ComponentPeer peer = this.peer;
                if (peer != null) {
                    peer.setEnabled(false);
                    if (this.visible && !this.getRecursivelyVisibleBounds().isEmpty()) {
                        this.updateCursorImmediately();
                    }
                }
            }
            if (this.accessibleContext != null) {
                this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.ENABLED);
            }
        }
    }
    
    public boolean isDoubleBuffered() {
        return false;
    }
    
    public void enableInputMethods(final boolean b) {
        if (b) {
            if ((this.eventMask & 0x1000L) != 0x0L) {
                return;
            }
            if (this.isFocusOwner()) {
                final InputContext inputContext = this.getInputContext();
                if (inputContext != null) {
                    inputContext.dispatchEvent(new FocusEvent(this, 1004));
                }
            }
            this.eventMask |= 0x1000L;
        }
        else {
            if ((this.eventMask & 0x1000L) != 0x0L) {
                final InputContext inputContext2 = this.getInputContext();
                if (inputContext2 != null) {
                    inputContext2.endComposition();
                    inputContext2.removeNotify(this);
                }
            }
            this.eventMask &= 0xFFFFFFFFFFFFEFFFL;
        }
    }
    
    public void setVisible(final boolean b) {
        this.show(b);
    }
    
    @Deprecated
    public void show() {
        if (!this.visible) {
            synchronized (this.getTreeLock()) {
                this.visible = true;
                this.mixOnShowing();
                final ComponentPeer peer = this.peer;
                if (peer != null) {
                    peer.setVisible(true);
                    this.createHierarchyEvents(1400, this, this.parent, 4L, Toolkit.enabledOnToolkit(32768L));
                    if (peer instanceof LightweightPeer) {
                        this.repaint();
                    }
                    this.updateCursorImmediately();
                }
                if (this.componentListener != null || (this.eventMask & 0x1L) != 0x0L || Toolkit.enabledOnToolkit(1L)) {
                    Toolkit.getEventQueue().postEvent(new ComponentEvent(this, 102));
                }
            }
            final Container parent = this.parent;
            if (parent != null) {
                parent.invalidate();
            }
        }
    }
    
    @Deprecated
    public void show(final boolean b) {
        if (b) {
            this.show();
        }
        else {
            this.hide();
        }
    }
    
    boolean containsFocus() {
        return this.isFocusOwner();
    }
    
    void clearMostRecentFocusOwnerOnHide() {
        KeyboardFocusManager.clearMostRecentFocusOwner(this);
    }
    
    void clearCurrentFocusCycleRootOnHide() {
    }
    
    @Deprecated
    public void hide() {
        this.isPacked = false;
        if (this.visible) {
            this.clearCurrentFocusCycleRootOnHide();
            this.clearMostRecentFocusOwnerOnHide();
            synchronized (this.getTreeLock()) {
                this.visible = false;
                this.mixOnHiding(this.isLightweight());
                if (this.containsFocus() && KeyboardFocusManager.isAutoFocusTransferEnabled()) {
                    this.transferFocus(true);
                }
                final ComponentPeer peer = this.peer;
                if (peer != null) {
                    peer.setVisible(false);
                    this.createHierarchyEvents(1400, this, this.parent, 4L, Toolkit.enabledOnToolkit(32768L));
                    if (peer instanceof LightweightPeer) {
                        this.repaint();
                    }
                    this.updateCursorImmediately();
                }
                if (this.componentListener != null || (this.eventMask & 0x1L) != 0x0L || Toolkit.enabledOnToolkit(1L)) {
                    Toolkit.getEventQueue().postEvent(new ComponentEvent(this, 103));
                }
            }
            final Container parent = this.parent;
            if (parent != null) {
                parent.invalidate();
            }
        }
    }
    
    @Transient
    public Color getForeground() {
        final Color foreground = this.foreground;
        if (foreground != null) {
            return foreground;
        }
        final Container parent = this.parent;
        return (parent != null) ? parent.getForeground() : null;
    }
    
    public void setForeground(Color foreground) {
        final Color foreground2 = this.foreground;
        final ComponentPeer peer = this.peer;
        this.foreground = foreground;
        if (peer != null) {
            foreground = this.getForeground();
            if (foreground != null) {
                peer.setForeground(foreground);
            }
        }
        this.firePropertyChange("foreground", foreground2, foreground);
    }
    
    public boolean isForegroundSet() {
        return this.foreground != null;
    }
    
    @Transient
    public Color getBackground() {
        final Color background = this.background;
        if (background != null) {
            return background;
        }
        final Container parent = this.parent;
        return (parent != null) ? parent.getBackground() : null;
    }
    
    public void setBackground(Color background) {
        final Color background2 = this.background;
        final ComponentPeer peer = this.peer;
        this.background = background;
        if (peer != null) {
            background = this.getBackground();
            if (background != null) {
                peer.setBackground(background);
            }
        }
        this.firePropertyChange("background", background2, background);
    }
    
    public boolean isBackgroundSet() {
        return this.background != null;
    }
    
    @Transient
    @Override
    public Font getFont() {
        return this.getFont_NoClientCode();
    }
    
    final Font getFont_NoClientCode() {
        final Font font = this.font;
        if (font != null) {
            return font;
        }
        final Container parent = this.parent;
        return (parent != null) ? parent.getFont_NoClientCode() : null;
    }
    
    public void setFont(Font font) {
        final Font font2;
        final Font font4;
        synchronized (this.getTreeLock()) {
            font2 = this.font;
            final Font font3 = font;
            this.font = font3;
            font4 = font3;
            final ComponentPeer peer = this.peer;
            if (peer != null) {
                font = this.getFont();
                if (font != null) {
                    peer.setFont(font);
                    this.peerFont = font;
                }
            }
        }
        this.firePropertyChange("font", font2, font4);
        if (font != font2 && (font2 == null || !font2.equals(font))) {
            this.invalidateIfValid();
        }
    }
    
    public boolean isFontSet() {
        return this.font != null;
    }
    
    public Locale getLocale() {
        final Locale locale = this.locale;
        if (locale != null) {
            return locale;
        }
        final Container parent = this.parent;
        if (parent == null) {
            throw new IllegalComponentStateException("This component must have a parent in order to determine its locale");
        }
        return parent.getLocale();
    }
    
    public void setLocale(final Locale locale) {
        this.firePropertyChange("locale", this.locale, this.locale = locale);
        this.invalidateIfValid();
    }
    
    public ColorModel getColorModel() {
        final ComponentPeer peer = this.peer;
        if (peer != null && !(peer instanceof LightweightPeer)) {
            return peer.getColorModel();
        }
        if (GraphicsEnvironment.isHeadless()) {
            return ColorModel.getRGBdefault();
        }
        return this.getToolkit().getColorModel();
    }
    
    public Point getLocation() {
        return this.location();
    }
    
    public Point getLocationOnScreen() {
        synchronized (this.getTreeLock()) {
            return this.getLocationOnScreen_NoTreeLock();
        }
    }
    
    final Point getLocationOnScreen_NoTreeLock() {
        if (this.peer == null || !this.isShowing()) {
            throw new IllegalComponentStateException("component must be showing on the screen to determine its location");
        }
        if (this.peer instanceof LightweightPeer) {
            final Container nativeContainer = this.getNativeContainer();
            final Point locationOnScreen = nativeContainer.peer.getLocationOnScreen();
            for (Component parent = this; parent != nativeContainer; parent = parent.getParent()) {
                final Point point = locationOnScreen;
                point.x += parent.x;
                final Point point2 = locationOnScreen;
                point2.y += parent.y;
            }
            return locationOnScreen;
        }
        return this.peer.getLocationOnScreen();
    }
    
    @Deprecated
    public Point location() {
        return this.location_NoClientCode();
    }
    
    private Point location_NoClientCode() {
        return new Point(this.x, this.y);
    }
    
    public void setLocation(final int n, final int n2) {
        this.move(n, n2);
    }
    
    @Deprecated
    public void move(final int n, final int n2) {
        synchronized (this.getTreeLock()) {
            this.setBoundsOp(1);
            this.setBounds(n, n2, this.width, this.height);
        }
    }
    
    public void setLocation(final Point point) {
        this.setLocation(point.x, point.y);
    }
    
    public Dimension getSize() {
        return this.size();
    }
    
    @Deprecated
    public Dimension size() {
        return new Dimension(this.width, this.height);
    }
    
    public void setSize(final int n, final int n2) {
        this.resize(n, n2);
    }
    
    @Deprecated
    public void resize(final int n, final int n2) {
        synchronized (this.getTreeLock()) {
            this.setBoundsOp(2);
            this.setBounds(this.x, this.y, n, n2);
        }
    }
    
    public void setSize(final Dimension dimension) {
        this.resize(dimension);
    }
    
    @Deprecated
    public void resize(final Dimension dimension) {
        this.setSize(dimension.width, dimension.height);
    }
    
    public Rectangle getBounds() {
        return this.bounds();
    }
    
    @Deprecated
    public Rectangle bounds() {
        return new Rectangle(this.x, this.y, this.width, this.height);
    }
    
    public void setBounds(final int n, final int n2, final int n3, final int n4) {
        this.reshape(n, n2, n3, n4);
    }
    
    @Deprecated
    public void reshape(final int x, final int y, final int width, final int height) {
        synchronized (this.getTreeLock()) {
            try {
                this.setBoundsOp(3);
                boolean b = this.width != width || this.height != height;
                boolean b2 = this.x != x || this.y != y;
                if (!b && !b2) {
                    return;
                }
                final int x2 = this.x;
                final int y2 = this.y;
                final int width2 = this.width;
                final int height2 = this.height;
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
                if (b) {
                    this.isPacked = false;
                }
                boolean b3 = true;
                this.mixOnReshaping();
                if (this.peer != null) {
                    if (!(this.peer instanceof LightweightPeer)) {
                        this.reshapeNativePeer(x, y, width, height, this.getBoundsOp());
                        b = (width2 != this.width || height2 != this.height);
                        b2 = (x2 != this.x || y2 != this.y);
                        if (this instanceof Window) {
                            b3 = false;
                        }
                    }
                    if (b) {
                        this.invalidate();
                    }
                    if (this.parent != null) {
                        this.parent.invalidateIfValid();
                    }
                }
                if (b3) {
                    this.notifyNewBounds(b, b2);
                }
                this.repaintParentIfNeeded(x2, y2, width2, height2);
            }
            finally {
                this.setBoundsOp(5);
            }
        }
    }
    
    private void repaintParentIfNeeded(final int n, final int n2, final int n3, final int n4) {
        if (this.parent != null && this.peer instanceof LightweightPeer && this.isShowing()) {
            this.parent.repaint(n, n2, n3, n4);
            this.repaint();
        }
    }
    
    private void reshapeNativePeer(final int n, final int n2, final int n3, final int n4, final int n5) {
        int n6 = n;
        int n7 = n2;
        for (Container container = this.parent; container != null && container.peer instanceof LightweightPeer; container = container.parent) {
            n6 += container.x;
            n7 += container.y;
        }
        this.peer.setBounds(n6, n7, n3, n4, n5);
    }
    
    private void notifyNewBounds(final boolean b, final boolean b2) {
        if (this.componentListener != null || (this.eventMask & 0x1L) != 0x0L || Toolkit.enabledOnToolkit(1L)) {
            if (b) {
                Toolkit.getEventQueue().postEvent(new ComponentEvent(this, 101));
            }
            if (b2) {
                Toolkit.getEventQueue().postEvent(new ComponentEvent(this, 100));
            }
        }
        else if (this instanceof Container && ((Container)this).countComponents() > 0) {
            final boolean enabledOnToolkit = Toolkit.enabledOnToolkit(65536L);
            if (b) {
                ((Container)this).createChildHierarchyEvents(1402, 0L, enabledOnToolkit);
            }
            if (b2) {
                ((Container)this).createChildHierarchyEvents(1401, 0L, enabledOnToolkit);
            }
        }
    }
    
    public void setBounds(final Rectangle rectangle) {
        this.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public Rectangle getBounds(final Rectangle rectangle) {
        if (rectangle == null) {
            return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
        rectangle.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        return rectangle;
    }
    
    public Dimension getSize(final Dimension dimension) {
        if (dimension == null) {
            return new Dimension(this.getWidth(), this.getHeight());
        }
        dimension.setSize(this.getWidth(), this.getHeight());
        return dimension;
    }
    
    public Point getLocation(final Point point) {
        if (point == null) {
            return new Point(this.getX(), this.getY());
        }
        point.setLocation(this.getX(), this.getY());
        return point;
    }
    
    public boolean isOpaque() {
        return this.getPeer() != null && !this.isLightweight();
    }
    
    public boolean isLightweight() {
        return this.getPeer() instanceof LightweightPeer;
    }
    
    public void setPreferredSize(final Dimension prefSize) {
        Dimension prefSize2;
        if (this.prefSizeSet) {
            prefSize2 = this.prefSize;
        }
        else {
            prefSize2 = null;
        }
        this.prefSize = prefSize;
        this.prefSizeSet = (prefSize != null);
        this.firePropertyChange("preferredSize", prefSize2, prefSize);
    }
    
    public boolean isPreferredSizeSet() {
        return this.prefSizeSet;
    }
    
    public Dimension getPreferredSize() {
        return this.preferredSize();
    }
    
    @Deprecated
    public Dimension preferredSize() {
        Dimension dimension = this.prefSize;
        if (dimension == null || (!this.isPreferredSizeSet() && !this.isValid())) {
            synchronized (this.getTreeLock()) {
                this.prefSize = ((this.peer != null) ? this.peer.getPreferredSize() : this.getMinimumSize());
                dimension = this.prefSize;
            }
        }
        return new Dimension(dimension);
    }
    
    public void setMinimumSize(final Dimension minSize) {
        Dimension minSize2;
        if (this.minSizeSet) {
            minSize2 = this.minSize;
        }
        else {
            minSize2 = null;
        }
        this.minSize = minSize;
        this.minSizeSet = (minSize != null);
        this.firePropertyChange("minimumSize", minSize2, minSize);
    }
    
    public boolean isMinimumSizeSet() {
        return this.minSizeSet;
    }
    
    public Dimension getMinimumSize() {
        return this.minimumSize();
    }
    
    @Deprecated
    public Dimension minimumSize() {
        Dimension dimension = this.minSize;
        if (dimension == null || (!this.isMinimumSizeSet() && !this.isValid())) {
            synchronized (this.getTreeLock()) {
                this.minSize = ((this.peer != null) ? this.peer.getMinimumSize() : this.size());
                dimension = this.minSize;
            }
        }
        return new Dimension(dimension);
    }
    
    public void setMaximumSize(final Dimension maxSize) {
        Dimension maxSize2;
        if (this.maxSizeSet) {
            maxSize2 = this.maxSize;
        }
        else {
            maxSize2 = null;
        }
        this.maxSize = maxSize;
        this.maxSizeSet = (maxSize != null);
        this.firePropertyChange("maximumSize", maxSize2, maxSize);
    }
    
    public boolean isMaximumSizeSet() {
        return this.maxSizeSet;
    }
    
    public Dimension getMaximumSize() {
        if (this.isMaximumSizeSet()) {
            return new Dimension(this.maxSize);
        }
        return new Dimension(32767, 32767);
    }
    
    public float getAlignmentX() {
        return 0.5f;
    }
    
    public float getAlignmentY() {
        return 0.5f;
    }
    
    public int getBaseline(final int n, final int n2) {
        if (n < 0 || n2 < 0) {
            throw new IllegalArgumentException("Width and height must be >= 0");
        }
        return -1;
    }
    
    public BaselineResizeBehavior getBaselineResizeBehavior() {
        return BaselineResizeBehavior.OTHER;
    }
    
    public void doLayout() {
        this.layout();
    }
    
    @Deprecated
    public void layout() {
    }
    
    public void validate() {
        synchronized (this.getTreeLock()) {
            final ComponentPeer peer = this.peer;
            final boolean valid = this.isValid();
            if (!valid && peer != null) {
                final Font font = this.getFont();
                final Font peerFont = this.peerFont;
                if (font != peerFont && (peerFont == null || !peerFont.equals(font))) {
                    peer.setFont(font);
                    this.peerFont = font;
                }
                peer.layout();
            }
            this.valid = true;
            if (!valid) {
                this.mixOnValidating();
            }
        }
    }
    
    public void invalidate() {
        synchronized (this.getTreeLock()) {
            this.valid = false;
            if (!this.isPreferredSizeSet()) {
                this.prefSize = null;
            }
            if (!this.isMinimumSizeSet()) {
                this.minSize = null;
            }
            if (!this.isMaximumSizeSet()) {
                this.maxSize = null;
            }
            this.invalidateParent();
        }
    }
    
    void invalidateParent() {
        if (this.parent != null) {
            this.parent.invalidateIfValid();
        }
    }
    
    final void invalidateIfValid() {
        if (this.isValid()) {
            this.invalidate();
        }
    }
    
    public void revalidate() {
        this.revalidateSynchronously();
    }
    
    final void revalidateSynchronously() {
        synchronized (this.getTreeLock()) {
            this.invalidate();
            Container container = this.getContainer();
            if (container == null) {
                this.validate();
            }
            else {
                while (!container.isValidateRoot() && container.getContainer() != null) {
                    container = container.getContainer();
                }
                container.validate();
            }
        }
    }
    
    public Graphics getGraphics() {
        if (!(this.peer instanceof LightweightPeer)) {
            final ComponentPeer peer = this.peer;
            return (peer != null) ? peer.getGraphics() : null;
        }
        if (this.parent == null) {
            return null;
        }
        final Graphics graphics = this.parent.getGraphics();
        if (graphics == null) {
            return null;
        }
        if (graphics instanceof ConstrainableGraphics) {
            ((ConstrainableGraphics)graphics).constrain(this.x, this.y, this.width, this.height);
        }
        else {
            graphics.translate(this.x, this.y);
            graphics.setClip(0, 0, this.width, this.height);
        }
        graphics.setFont(this.getFont());
        return graphics;
    }
    
    final Graphics getGraphics_NoClientCode() {
        final ComponentPeer peer = this.peer;
        if (!(peer instanceof LightweightPeer)) {
            return (peer != null) ? peer.getGraphics() : null;
        }
        final Container parent = this.parent;
        if (parent == null) {
            return null;
        }
        final Graphics graphics_NoClientCode = parent.getGraphics_NoClientCode();
        if (graphics_NoClientCode == null) {
            return null;
        }
        if (graphics_NoClientCode instanceof ConstrainableGraphics) {
            ((ConstrainableGraphics)graphics_NoClientCode).constrain(this.x, this.y, this.width, this.height);
        }
        else {
            graphics_NoClientCode.translate(this.x, this.y);
            graphics_NoClientCode.setClip(0, 0, this.width, this.height);
        }
        graphics_NoClientCode.setFont(this.getFont_NoClientCode());
        return graphics_NoClientCode;
    }
    
    public FontMetrics getFontMetrics(final Font font) {
        final FontManager instance = FontManagerFactory.getInstance();
        if (instance instanceof SunFontManager && ((SunFontManager)instance).usePlatformFontMetrics() && this.peer != null && !(this.peer instanceof LightweightPeer)) {
            return this.peer.getFontMetrics(font);
        }
        return FontDesignMetrics.getMetrics(font);
    }
    
    public void setCursor(final Cursor cursor) {
        this.cursor = cursor;
        this.updateCursorImmediately();
    }
    
    final void updateCursorImmediately() {
        if (this.peer instanceof LightweightPeer) {
            final Container nativeContainer = this.getNativeContainer();
            if (nativeContainer == null) {
                return;
            }
            final ComponentPeer peer = nativeContainer.getPeer();
            if (peer != null) {
                peer.updateCursorImmediately();
            }
        }
        else if (this.peer != null) {
            this.peer.updateCursorImmediately();
        }
    }
    
    public Cursor getCursor() {
        return this.getCursor_NoClientCode();
    }
    
    final Cursor getCursor_NoClientCode() {
        final Cursor cursor = this.cursor;
        if (cursor != null) {
            return cursor;
        }
        final Container parent = this.parent;
        if (parent != null) {
            return parent.getCursor_NoClientCode();
        }
        return Cursor.getPredefinedCursor(0);
    }
    
    public boolean isCursorSet() {
        return this.cursor != null;
    }
    
    public void paint(final Graphics graphics) {
    }
    
    public void update(final Graphics graphics) {
        this.paint(graphics);
    }
    
    public void paintAll(final Graphics graphics) {
        if (this.isShowing()) {
            GraphicsCallback.PeerPaintCallback.getInstance().runOneComponent(this, new Rectangle(0, 0, this.width, this.height), graphics, graphics.getClip(), 3);
        }
    }
    
    void lightweightPaint(final Graphics graphics) {
        this.paint(graphics);
    }
    
    void paintHeavyweightComponents(final Graphics graphics) {
    }
    
    public void repaint() {
        this.repaint(0L, 0, 0, this.width, this.height);
    }
    
    public void repaint(final long n) {
        this.repaint(n, 0, 0, this.width, this.height);
    }
    
    public void repaint(final int n, final int n2, final int n3, final int n4) {
        this.repaint(0L, n, n2, n3, n4);
    }
    
    public void repaint(final long n, int n2, int n3, int n4, int n5) {
        if (this.peer instanceof LightweightPeer) {
            if (this.parent != null) {
                if (n2 < 0) {
                    n4 += n2;
                    n2 = 0;
                }
                if (n3 < 0) {
                    n5 += n3;
                    n3 = 0;
                }
                final int n6 = (n4 > this.width) ? this.width : n4;
                final int n7 = (n5 > this.height) ? this.height : n5;
                if (n6 <= 0 || n7 <= 0) {
                    return;
                }
                this.parent.repaint(n, this.x + n2, this.y + n3, n6, n7);
            }
        }
        else if (this.isVisible() && this.peer != null && n4 > 0 && n5 > 0) {
            SunToolkit.postEvent(SunToolkit.targetToAppContext(this), new PaintEvent(this, 801, new Rectangle(n2, n3, n4, n5)));
        }
    }
    
    public void print(final Graphics graphics) {
        this.paint(graphics);
    }
    
    public void printAll(final Graphics graphics) {
        if (this.isShowing()) {
            GraphicsCallback.PeerPrintCallback.getInstance().runOneComponent(this, new Rectangle(0, 0, this.width, this.height), graphics, graphics.getClip(), 3);
        }
    }
    
    void lightweightPrint(final Graphics graphics) {
        this.print(graphics);
    }
    
    void printHeavyweightComponents(final Graphics graphics) {
    }
    
    private Insets getInsets_NoClientCode() {
        final ComponentPeer peer = this.peer;
        if (peer instanceof ContainerPeer) {
            return (Insets)((ContainerPeer)peer).getInsets().clone();
        }
        return new Insets(0, 0, 0, 0);
    }
    
    @Override
    public boolean imageUpdate(final Image image, final int n, final int n2, final int n3, final int n4, final int n5) {
        int incRate = -1;
        if ((n & 0x30) != 0x0) {
            incRate = 0;
        }
        else if ((n & 0x8) != 0x0 && Component.isInc) {
            incRate = Component.incRate;
            if (incRate < 0) {
                incRate = 0;
            }
        }
        if (incRate >= 0) {
            this.repaint(incRate, 0, 0, this.width, this.height);
        }
        return (n & 0xA0) == 0x0;
    }
    
    public Image createImage(final ImageProducer imageProducer) {
        final ComponentPeer peer = this.peer;
        if (peer != null && !(peer instanceof LightweightPeer)) {
            return peer.createImage(imageProducer);
        }
        return this.getToolkit().createImage(imageProducer);
    }
    
    public Image createImage(final int n, final int n2) {
        final ComponentPeer peer = this.peer;
        if (!(peer instanceof LightweightPeer)) {
            return (peer != null) ? peer.createImage(n, n2) : null;
        }
        if (this.parent != null) {
            return this.parent.createImage(n, n2);
        }
        return null;
    }
    
    public VolatileImage createVolatileImage(final int n, final int n2) {
        final ComponentPeer peer = this.peer;
        if (!(peer instanceof LightweightPeer)) {
            return (peer != null) ? peer.createVolatileImage(n, n2) : null;
        }
        if (this.parent != null) {
            return this.parent.createVolatileImage(n, n2);
        }
        return null;
    }
    
    public VolatileImage createVolatileImage(final int n, final int n2, final ImageCapabilities imageCapabilities) throws AWTException {
        return this.createVolatileImage(n, n2);
    }
    
    public boolean prepareImage(final Image image, final ImageObserver imageObserver) {
        return this.prepareImage(image, -1, -1, imageObserver);
    }
    
    public boolean prepareImage(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        final ComponentPeer peer = this.peer;
        if (peer instanceof LightweightPeer) {
            return (this.parent != null) ? this.parent.prepareImage(image, n, n2, imageObserver) : this.getToolkit().prepareImage(image, n, n2, imageObserver);
        }
        return (peer != null) ? peer.prepareImage(image, n, n2, imageObserver) : this.getToolkit().prepareImage(image, n, n2, imageObserver);
    }
    
    public int checkImage(final Image image, final ImageObserver imageObserver) {
        return this.checkImage(image, -1, -1, imageObserver);
    }
    
    public int checkImage(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        final ComponentPeer peer = this.peer;
        if (peer instanceof LightweightPeer) {
            return (this.parent != null) ? this.parent.checkImage(image, n, n2, imageObserver) : this.getToolkit().checkImage(image, n, n2, imageObserver);
        }
        return (peer != null) ? peer.checkImage(image, n, n2, imageObserver) : this.getToolkit().checkImage(image, n, n2, imageObserver);
    }
    
    void createBufferStrategy(final int n) {
        if (n > 1) {
            final BufferCapabilities bufferCapabilities = new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), BufferCapabilities.FlipContents.UNDEFINED);
            try {
                this.createBufferStrategy(n, bufferCapabilities);
                return;
            }
            catch (final AWTException ex) {}
        }
        final BufferCapabilities bufferCapabilities2 = new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), null);
        try {
            this.createBufferStrategy(n, bufferCapabilities2);
        }
        catch (final AWTException ex2) {
            final BufferCapabilities bufferCapabilities3 = new BufferCapabilities(new ImageCapabilities(false), new ImageCapabilities(false), null);
            try {
                this.createBufferStrategy(n, bufferCapabilities3);
            }
            catch (final AWTException ex3) {
                throw new InternalError("Could not create a buffer strategy", ex3);
            }
        }
    }
    
    void createBufferStrategy(final int n, BufferCapabilities bufferCapabilities) throws AWTException {
        if (n < 1) {
            throw new IllegalArgumentException("Number of buffers must be at least 1");
        }
        if (bufferCapabilities == null) {
            throw new IllegalArgumentException("No capabilities specified");
        }
        if (this.bufferStrategy != null) {
            this.bufferStrategy.dispose();
        }
        if (n == 1) {
            this.bufferStrategy = new SingleBufferStrategy(bufferCapabilities);
        }
        else {
            final SunGraphicsEnvironment sunGraphicsEnvironment = (SunGraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment();
            if (!bufferCapabilities.isPageFlipping() && sunGraphicsEnvironment.isFlipStrategyPreferred(this.peer)) {
                bufferCapabilities = new ProxyCapabilities(bufferCapabilities);
            }
            if (bufferCapabilities.isPageFlipping()) {
                this.bufferStrategy = new FlipSubRegionBufferStrategy(n, bufferCapabilities);
            }
            else {
                this.bufferStrategy = new BltSubRegionBufferStrategy(n, bufferCapabilities);
            }
        }
    }
    
    BufferStrategy getBufferStrategy() {
        return this.bufferStrategy;
    }
    
    Image getBackBuffer() {
        if (this.bufferStrategy != null) {
            if (this.bufferStrategy instanceof BltBufferStrategy) {
                return ((BltBufferStrategy)this.bufferStrategy).getBackBuffer();
            }
            if (this.bufferStrategy instanceof FlipBufferStrategy) {
                return ((FlipBufferStrategy)this.bufferStrategy).getBackBuffer();
            }
        }
        return null;
    }
    
    public void setIgnoreRepaint(final boolean ignoreRepaint) {
        this.ignoreRepaint = ignoreRepaint;
    }
    
    public boolean getIgnoreRepaint() {
        return this.ignoreRepaint;
    }
    
    public boolean contains(final int n, final int n2) {
        return this.inside(n, n2);
    }
    
    @Deprecated
    public boolean inside(final int n, final int n2) {
        return n >= 0 && n < this.width && n2 >= 0 && n2 < this.height;
    }
    
    public boolean contains(final Point point) {
        return this.contains(point.x, point.y);
    }
    
    public Component getComponentAt(final int n, final int n2) {
        return this.locate(n, n2);
    }
    
    @Deprecated
    public Component locate(final int n, final int n2) {
        return this.contains(n, n2) ? this : null;
    }
    
    public Component getComponentAt(final Point point) {
        return this.getComponentAt(point.x, point.y);
    }
    
    @Deprecated
    public void deliverEvent(final Event event) {
        this.postEvent(event);
    }
    
    public final void dispatchEvent(final AWTEvent awtEvent) {
        this.dispatchEventImpl(awtEvent);
    }
    
    void dispatchEventImpl(AWTEvent retargetFocusEvent) {
        final int id = retargetFocusEvent.getID();
        final AppContext appContext = this.appContext;
        if (appContext != null && !appContext.equals(AppContext.getAppContext()) && Component.eventLog.isLoggable(PlatformLogger.Level.FINE)) {
            Component.eventLog.fine("Event " + retargetFocusEvent + " is being dispatched on the wrong AppContext");
        }
        if (Component.eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
            Component.eventLog.finest("{0}", retargetFocusEvent);
        }
        if (!(retargetFocusEvent instanceof KeyEvent)) {
            EventQueue.setCurrentEventAndMostRecentTime(retargetFocusEvent);
        }
        if (retargetFocusEvent instanceof SunDropTargetEvent) {
            ((SunDropTargetEvent)retargetFocusEvent).dispatch();
            return;
        }
        if (!retargetFocusEvent.focusManagerIsDispatching) {
            if (retargetFocusEvent.isPosted) {
                retargetFocusEvent = KeyboardFocusManager.retargetFocusEvent(retargetFocusEvent);
                retargetFocusEvent.isPosted = true;
            }
            if (KeyboardFocusManager.getCurrentKeyboardFocusManager().dispatchEvent(retargetFocusEvent)) {
                return;
            }
        }
        if (retargetFocusEvent instanceof FocusEvent && Component.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
            Component.focusLog.finest("" + retargetFocusEvent);
        }
        if (id == 507 && !this.eventTypeEnabled(id) && this.peer != null && !this.peer.handlesWheelScrolling() && this.dispatchMouseWheelToAncestor((MouseWheelEvent)retargetFocusEvent)) {
            return;
        }
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        defaultToolkit.notifyAWTEventListeners(retargetFocusEvent);
        if (!retargetFocusEvent.isConsumed() && retargetFocusEvent instanceof KeyEvent) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().processKeyEvent(this, (KeyEvent)retargetFocusEvent);
            if (retargetFocusEvent.isConsumed()) {
                return;
            }
        }
        if (this.areInputMethodsEnabled()) {
            if ((retargetFocusEvent instanceof InputMethodEvent && !(this instanceof CompositionArea)) || retargetFocusEvent instanceof InputEvent || retargetFocusEvent instanceof FocusEvent) {
                final InputContext inputContext = this.getInputContext();
                if (inputContext != null) {
                    inputContext.dispatchEvent(retargetFocusEvent);
                    if (retargetFocusEvent.isConsumed()) {
                        if (retargetFocusEvent instanceof FocusEvent && Component.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                            Component.focusLog.finest("3579: Skipping " + retargetFocusEvent);
                        }
                        return;
                    }
                }
            }
        }
        else if (id == 1004) {
            final InputContext inputContext2 = this.getInputContext();
            if (inputContext2 != null && inputContext2 instanceof sun.awt.im.InputContext) {
                ((sun.awt.im.InputContext)inputContext2).disableNativeIM();
            }
        }
        switch (id) {
            case 401:
            case 402: {
                final Container container = (Container)((this instanceof Container) ? this : this.parent);
                if (container == null) {
                    break;
                }
                container.preProcessKeyEvent((KeyEvent)retargetFocusEvent);
                if (retargetFocusEvent.isConsumed()) {
                    if (Component.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                        Component.focusLog.finest("Pre-process consumed event");
                    }
                    return;
                }
                break;
            }
            case 201: {
                if (!(defaultToolkit instanceof WindowClosingListener)) {
                    break;
                }
                this.windowClosingException = ((WindowClosingListener)defaultToolkit).windowClosingNotify((WindowEvent)retargetFocusEvent);
                if (this.checkWindowClosingException()) {
                    return;
                }
                break;
            }
        }
        if (this.newEventsOnly) {
            if (this.eventEnabled(retargetFocusEvent)) {
                this.processEvent(retargetFocusEvent);
            }
        }
        else if (id == 507) {
            this.autoProcessMouseWheel((MouseWheelEvent)retargetFocusEvent);
        }
        else if (!(retargetFocusEvent instanceof MouseEvent) || this.postsOldMouseEvents()) {
            final Event convertToOld = retargetFocusEvent.convertToOld();
            if (convertToOld != null) {
                final int key = convertToOld.key;
                final int modifiers = convertToOld.modifiers;
                this.postEvent(convertToOld);
                if (convertToOld.isConsumed()) {
                    retargetFocusEvent.consume();
                }
                switch (convertToOld.id) {
                    case 401:
                    case 402:
                    case 403:
                    case 404: {
                        if (convertToOld.key != key) {
                            ((KeyEvent)retargetFocusEvent).setKeyChar(convertToOld.getKeyEventChar());
                        }
                        if (convertToOld.modifiers != modifiers) {
                            ((KeyEvent)retargetFocusEvent).setModifiers(convertToOld.modifiers);
                            break;
                        }
                        break;
                    }
                }
            }
        }
        if (id == 201 && !retargetFocusEvent.isConsumed() && defaultToolkit instanceof WindowClosingListener) {
            this.windowClosingException = ((WindowClosingListener)defaultToolkit).windowClosingDelivered((WindowEvent)retargetFocusEvent);
            if (this.checkWindowClosingException()) {
                return;
            }
        }
        if (!(retargetFocusEvent instanceof KeyEvent)) {
            ComponentPeer componentPeer = this.peer;
            if (retargetFocusEvent instanceof FocusEvent && (componentPeer == null || componentPeer instanceof LightweightPeer)) {
                final Component component = (Component)retargetFocusEvent.getSource();
                if (component != null) {
                    final Container nativeContainer = component.getNativeContainer();
                    if (nativeContainer != null) {
                        componentPeer = nativeContainer.getPeer();
                    }
                }
            }
            if (componentPeer != null) {
                componentPeer.handleEvent(retargetFocusEvent);
            }
        }
        if (SunToolkit.isTouchKeyboardAutoShowEnabled() && defaultToolkit instanceof SunToolkit && (retargetFocusEvent instanceof MouseEvent || retargetFocusEvent instanceof FocusEvent)) {
            ((SunToolkit)defaultToolkit).showOrHideTouchKeyboard(this, retargetFocusEvent);
        }
    }
    
    void autoProcessMouseWheel(final MouseWheelEvent mouseWheelEvent) {
    }
    
    boolean dispatchMouseWheelToAncestor(final MouseWheelEvent mouseWheelEvent) {
        int n = mouseWheelEvent.getX() + this.getX();
        int n2 = mouseWheelEvent.getY() + this.getY();
        if (Component.eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
            Component.eventLog.finest("dispatchMouseWheelToAncestor");
            Component.eventLog.finest("orig event src is of " + mouseWheelEvent.getSource().getClass());
        }
        synchronized (this.getTreeLock()) {
            Container container;
            for (container = this.getParent(); container != null && !container.eventEnabled(mouseWheelEvent); container = container.getParent()) {
                n += container.getX();
                n2 += container.getY();
                if (container instanceof Window) {
                    break;
                }
            }
            if (Component.eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
                Component.eventLog.finest("new event src is " + container.getClass());
            }
            if (container != null && container.eventEnabled(mouseWheelEvent)) {
                final MouseWheelEvent mouseWheelEvent2 = new MouseWheelEvent(container, mouseWheelEvent.getID(), mouseWheelEvent.getWhen(), mouseWheelEvent.getModifiers(), n, n2, mouseWheelEvent.getXOnScreen(), mouseWheelEvent.getYOnScreen(), mouseWheelEvent.getClickCount(), mouseWheelEvent.isPopupTrigger(), mouseWheelEvent.getScrollType(), mouseWheelEvent.getScrollAmount(), mouseWheelEvent.getWheelRotation(), mouseWheelEvent.getPreciseWheelRotation());
                mouseWheelEvent.copyPrivateDataInto(mouseWheelEvent2);
                container.dispatchEventToSelf(mouseWheelEvent2);
                if (mouseWheelEvent2.isConsumed()) {
                    mouseWheelEvent.consume();
                }
                return true;
            }
        }
        return false;
    }
    
    boolean checkWindowClosingException() {
        if (this.windowClosingException != null) {
            if (this instanceof Dialog) {
                ((Dialog)this).interruptBlocking();
            }
            else {
                this.windowClosingException.fillInStackTrace();
                this.windowClosingException.printStackTrace();
                this.windowClosingException = null;
            }
            return true;
        }
        return false;
    }
    
    boolean areInputMethodsEnabled() {
        return (this.eventMask & 0x1000L) != 0x0L && ((this.eventMask & 0x8L) != 0x0L || this.keyListener != null);
    }
    
    boolean eventEnabled(final AWTEvent awtEvent) {
        return this.eventTypeEnabled(awtEvent.id);
    }
    
    boolean eventTypeEnabled(final int n) {
        switch (n) {
            case 100:
            case 101:
            case 102:
            case 103: {
                if ((this.eventMask & 0x1L) != 0x0L || this.componentListener != null) {
                    return true;
                }
                break;
            }
            case 1004:
            case 1005: {
                if ((this.eventMask & 0x4L) != 0x0L || this.focusListener != null) {
                    return true;
                }
                break;
            }
            case 400:
            case 401:
            case 402: {
                if ((this.eventMask & 0x8L) != 0x0L || this.keyListener != null) {
                    return true;
                }
                break;
            }
            case 500:
            case 501:
            case 502:
            case 504:
            case 505: {
                if ((this.eventMask & 0x10L) != 0x0L || this.mouseListener != null) {
                    return true;
                }
                break;
            }
            case 503:
            case 506: {
                if ((this.eventMask & 0x20L) != 0x0L || this.mouseMotionListener != null) {
                    return true;
                }
                break;
            }
            case 507: {
                if ((this.eventMask & 0x20000L) != 0x0L || this.mouseWheelListener != null) {
                    return true;
                }
                break;
            }
            case 1100:
            case 1101: {
                if ((this.eventMask & 0x800L) != 0x0L || this.inputMethodListener != null) {
                    return true;
                }
                break;
            }
            case 1400: {
                if ((this.eventMask & 0x8000L) != 0x0L || this.hierarchyListener != null) {
                    return true;
                }
                break;
            }
            case 1401:
            case 1402: {
                if ((this.eventMask & 0x10000L) != 0x0L || this.hierarchyBoundsListener != null) {
                    return true;
                }
                break;
            }
            case 1001: {
                if ((this.eventMask & 0x80L) != 0x0L) {
                    return true;
                }
                break;
            }
            case 900: {
                if ((this.eventMask & 0x400L) != 0x0L) {
                    return true;
                }
                break;
            }
            case 701: {
                if ((this.eventMask & 0x200L) != 0x0L) {
                    return true;
                }
                break;
            }
            case 601: {
                if ((this.eventMask & 0x100L) != 0x0L) {
                    return true;
                }
                break;
            }
        }
        return n > 1999;
    }
    
    @Deprecated
    @Override
    public boolean postEvent(final Event event) {
        final ComponentPeer peer = this.peer;
        if (this.handleEvent(event)) {
            event.consume();
            return true;
        }
        final Container parent = this.parent;
        final int x = event.x;
        final int y = event.y;
        if (parent != null) {
            event.translate(this.x, this.y);
            if (parent.postEvent(event)) {
                event.consume();
                return true;
            }
            event.x = x;
            event.y = y;
        }
        return false;
    }
    
    public synchronized void addComponentListener(final ComponentListener componentListener) {
        if (componentListener == null) {
            return;
        }
        this.componentListener = AWTEventMulticaster.add(this.componentListener, componentListener);
        this.newEventsOnly = true;
    }
    
    public synchronized void removeComponentListener(final ComponentListener componentListener) {
        if (componentListener == null) {
            return;
        }
        this.componentListener = AWTEventMulticaster.remove(this.componentListener, componentListener);
    }
    
    public synchronized ComponentListener[] getComponentListeners() {
        return this.getListeners(ComponentListener.class);
    }
    
    public synchronized void addFocusListener(final FocusListener focusListener) {
        if (focusListener == null) {
            return;
        }
        this.focusListener = AWTEventMulticaster.add(this.focusListener, focusListener);
        this.newEventsOnly = true;
        if (this.peer instanceof LightweightPeer) {
            this.parent.proxyEnableEvents(4L);
        }
    }
    
    public synchronized void removeFocusListener(final FocusListener focusListener) {
        if (focusListener == null) {
            return;
        }
        this.focusListener = AWTEventMulticaster.remove(this.focusListener, focusListener);
    }
    
    public synchronized FocusListener[] getFocusListeners() {
        return this.getListeners(FocusListener.class);
    }
    
    public void addHierarchyListener(final HierarchyListener hierarchyListener) {
        if (hierarchyListener == null) {
            return;
        }
        boolean b;
        synchronized (this) {
            b = (this.hierarchyListener == null && (this.eventMask & 0x8000L) == 0x0L);
            this.hierarchyListener = AWTEventMulticaster.add(this.hierarchyListener, hierarchyListener);
            b = (b && this.hierarchyListener != null);
            this.newEventsOnly = true;
        }
        if (b) {
            synchronized (this.getTreeLock()) {
                this.adjustListeningChildrenOnParent(32768L, 1);
            }
        }
    }
    
    public void removeHierarchyListener(final HierarchyListener hierarchyListener) {
        if (hierarchyListener == null) {
            return;
        }
        boolean b;
        synchronized (this) {
            b = (this.hierarchyListener != null && (this.eventMask & 0x8000L) == 0x0L);
            this.hierarchyListener = AWTEventMulticaster.remove(this.hierarchyListener, hierarchyListener);
            b = (b && this.hierarchyListener == null);
        }
        if (b) {
            synchronized (this.getTreeLock()) {
                this.adjustListeningChildrenOnParent(32768L, -1);
            }
        }
    }
    
    public synchronized HierarchyListener[] getHierarchyListeners() {
        return this.getListeners(HierarchyListener.class);
    }
    
    public void addHierarchyBoundsListener(final HierarchyBoundsListener hierarchyBoundsListener) {
        if (hierarchyBoundsListener == null) {
            return;
        }
        boolean b;
        synchronized (this) {
            b = (this.hierarchyBoundsListener == null && (this.eventMask & 0x10000L) == 0x0L);
            this.hierarchyBoundsListener = AWTEventMulticaster.add(this.hierarchyBoundsListener, hierarchyBoundsListener);
            b = (b && this.hierarchyBoundsListener != null);
            this.newEventsOnly = true;
        }
        if (b) {
            synchronized (this.getTreeLock()) {
                this.adjustListeningChildrenOnParent(65536L, 1);
            }
        }
    }
    
    public void removeHierarchyBoundsListener(final HierarchyBoundsListener hierarchyBoundsListener) {
        if (hierarchyBoundsListener == null) {
            return;
        }
        boolean b;
        synchronized (this) {
            b = (this.hierarchyBoundsListener != null && (this.eventMask & 0x10000L) == 0x0L);
            this.hierarchyBoundsListener = AWTEventMulticaster.remove(this.hierarchyBoundsListener, hierarchyBoundsListener);
            b = (b && this.hierarchyBoundsListener == null);
        }
        if (b) {
            synchronized (this.getTreeLock()) {
                this.adjustListeningChildrenOnParent(65536L, -1);
            }
        }
    }
    
    int numListening(final long n) {
        if (Component.eventLog.isLoggable(PlatformLogger.Level.FINE) && n != 32768L && n != 65536L) {
            Component.eventLog.fine("Assertion failed");
        }
        if ((n == 32768L && (this.hierarchyListener != null || (this.eventMask & 0x8000L) != 0x0L)) || (n == 65536L && (this.hierarchyBoundsListener != null || (this.eventMask & 0x10000L) != 0x0L))) {
            return 1;
        }
        return 0;
    }
    
    int countHierarchyMembers() {
        return 1;
    }
    
    int createHierarchyEvents(final int n, final Component component, final Container container, final long n2, final boolean b) {
        switch (n) {
            case 1400: {
                if (this.hierarchyListener != null || (this.eventMask & 0x8000L) != 0x0L || b) {
                    this.dispatchEvent(new HierarchyEvent(this, n, component, container, n2));
                    return 1;
                }
                break;
            }
            case 1401:
            case 1402: {
                if (Component.eventLog.isLoggable(PlatformLogger.Level.FINE) && n2 != 0L) {
                    Component.eventLog.fine("Assertion (changeFlags == 0) failed");
                }
                if (this.hierarchyBoundsListener != null || (this.eventMask & 0x10000L) != 0x0L || b) {
                    this.dispatchEvent(new HierarchyEvent(this, n, component, container));
                    return 1;
                }
                break;
            }
            default: {
                if (Component.eventLog.isLoggable(PlatformLogger.Level.FINE)) {
                    Component.eventLog.fine("This code must never be reached");
                    break;
                }
                break;
            }
        }
        return 0;
    }
    
    public synchronized HierarchyBoundsListener[] getHierarchyBoundsListeners() {
        return this.getListeners(HierarchyBoundsListener.class);
    }
    
    void adjustListeningChildrenOnParent(final long n, final int n2) {
        if (this.parent != null) {
            this.parent.adjustListeningChildren(n, n2);
        }
    }
    
    public synchronized void addKeyListener(final KeyListener keyListener) {
        if (keyListener == null) {
            return;
        }
        this.keyListener = AWTEventMulticaster.add(this.keyListener, keyListener);
        this.newEventsOnly = true;
        if (this.peer instanceof LightweightPeer) {
            this.parent.proxyEnableEvents(8L);
        }
    }
    
    public synchronized void removeKeyListener(final KeyListener keyListener) {
        if (keyListener == null) {
            return;
        }
        this.keyListener = AWTEventMulticaster.remove(this.keyListener, keyListener);
    }
    
    public synchronized KeyListener[] getKeyListeners() {
        return this.getListeners(KeyListener.class);
    }
    
    public synchronized void addMouseListener(final MouseListener mouseListener) {
        if (mouseListener == null) {
            return;
        }
        this.mouseListener = AWTEventMulticaster.add(this.mouseListener, mouseListener);
        this.newEventsOnly = true;
        if (this.peer instanceof LightweightPeer) {
            this.parent.proxyEnableEvents(16L);
        }
    }
    
    public synchronized void removeMouseListener(final MouseListener mouseListener) {
        if (mouseListener == null) {
            return;
        }
        this.mouseListener = AWTEventMulticaster.remove(this.mouseListener, mouseListener);
    }
    
    public synchronized MouseListener[] getMouseListeners() {
        return this.getListeners(MouseListener.class);
    }
    
    public synchronized void addMouseMotionListener(final MouseMotionListener mouseMotionListener) {
        if (mouseMotionListener == null) {
            return;
        }
        this.mouseMotionListener = AWTEventMulticaster.add(this.mouseMotionListener, mouseMotionListener);
        this.newEventsOnly = true;
        if (this.peer instanceof LightweightPeer) {
            this.parent.proxyEnableEvents(32L);
        }
    }
    
    public synchronized void removeMouseMotionListener(final MouseMotionListener mouseMotionListener) {
        if (mouseMotionListener == null) {
            return;
        }
        this.mouseMotionListener = AWTEventMulticaster.remove(this.mouseMotionListener, mouseMotionListener);
    }
    
    public synchronized MouseMotionListener[] getMouseMotionListeners() {
        return this.getListeners(MouseMotionListener.class);
    }
    
    public synchronized void addMouseWheelListener(final MouseWheelListener mouseWheelListener) {
        if (mouseWheelListener == null) {
            return;
        }
        this.mouseWheelListener = AWTEventMulticaster.add(this.mouseWheelListener, mouseWheelListener);
        this.newEventsOnly = true;
        if (this.peer instanceof LightweightPeer) {
            this.parent.proxyEnableEvents(131072L);
        }
    }
    
    public synchronized void removeMouseWheelListener(final MouseWheelListener mouseWheelListener) {
        if (mouseWheelListener == null) {
            return;
        }
        this.mouseWheelListener = AWTEventMulticaster.remove(this.mouseWheelListener, mouseWheelListener);
    }
    
    public synchronized MouseWheelListener[] getMouseWheelListeners() {
        return this.getListeners(MouseWheelListener.class);
    }
    
    public synchronized void addInputMethodListener(final InputMethodListener inputMethodListener) {
        if (inputMethodListener == null) {
            return;
        }
        this.inputMethodListener = AWTEventMulticaster.add(this.inputMethodListener, inputMethodListener);
        this.newEventsOnly = true;
    }
    
    public synchronized void removeInputMethodListener(final InputMethodListener inputMethodListener) {
        if (inputMethodListener == null) {
            return;
        }
        this.inputMethodListener = AWTEventMulticaster.remove(this.inputMethodListener, inputMethodListener);
    }
    
    public synchronized InputMethodListener[] getInputMethodListeners() {
        return this.getListeners(InputMethodListener.class);
    }
    
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        EventListener eventListener = null;
        if (clazz == ComponentListener.class) {
            eventListener = this.componentListener;
        }
        else if (clazz == FocusListener.class) {
            eventListener = this.focusListener;
        }
        else if (clazz == HierarchyListener.class) {
            eventListener = this.hierarchyListener;
        }
        else if (clazz == HierarchyBoundsListener.class) {
            eventListener = this.hierarchyBoundsListener;
        }
        else if (clazz == KeyListener.class) {
            eventListener = this.keyListener;
        }
        else if (clazz == MouseListener.class) {
            eventListener = this.mouseListener;
        }
        else if (clazz == MouseMotionListener.class) {
            eventListener = this.mouseMotionListener;
        }
        else if (clazz == MouseWheelListener.class) {
            eventListener = this.mouseWheelListener;
        }
        else if (clazz == InputMethodListener.class) {
            eventListener = this.inputMethodListener;
        }
        else if (clazz == PropertyChangeListener.class) {
            return (T[])this.getPropertyChangeListeners();
        }
        return AWTEventMulticaster.getListeners(eventListener, clazz);
    }
    
    public InputMethodRequests getInputMethodRequests() {
        return null;
    }
    
    public InputContext getInputContext() {
        final Container parent = this.parent;
        if (parent == null) {
            return null;
        }
        return parent.getInputContext();
    }
    
    protected final void enableEvents(final long n) {
        long n2 = 0L;
        synchronized (this) {
            if ((n & 0x8000L) != 0x0L && this.hierarchyListener == null && (this.eventMask & 0x8000L) == 0x0L) {
                n2 |= 0x8000L;
            }
            if ((n & 0x10000L) != 0x0L && this.hierarchyBoundsListener == null && (this.eventMask & 0x10000L) == 0x0L) {
                n2 |= 0x10000L;
            }
            this.eventMask |= n;
            this.newEventsOnly = true;
        }
        if (this.peer instanceof LightweightPeer) {
            this.parent.proxyEnableEvents(this.eventMask);
        }
        if (n2 != 0L) {
            synchronized (this.getTreeLock()) {
                this.adjustListeningChildrenOnParent(n2, 1);
            }
        }
    }
    
    protected final void disableEvents(final long n) {
        long n2 = 0L;
        synchronized (this) {
            if ((n & 0x8000L) != 0x0L && this.hierarchyListener == null && (this.eventMask & 0x8000L) != 0x0L) {
                n2 |= 0x8000L;
            }
            if ((n & 0x10000L) != 0x0L && this.hierarchyBoundsListener == null && (this.eventMask & 0x10000L) != 0x0L) {
                n2 |= 0x10000L;
            }
            this.eventMask &= ~n;
        }
        if (n2 != 0L) {
            synchronized (this.getTreeLock()) {
                this.adjustListeningChildrenOnParent(n2, -1);
            }
        }
    }
    
    private boolean checkCoalescing() {
        if (this.getClass().getClassLoader() == null) {
            return false;
        }
        final Class<? extends Component> class1 = this.getClass();
        synchronized (Component.coalesceMap) {
            final Boolean b = Component.coalesceMap.get(class1);
            if (b != null) {
                return b;
            }
            final Boolean b2 = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
                @Override
                public Boolean run() {
                    return isCoalesceEventsOverriden(class1);
                }
            });
            Component.coalesceMap.put(class1, b2);
            return b2;
        }
    }
    
    private static boolean isCoalesceEventsOverriden(final Class<?> clazz) {
        assert Thread.holdsLock(Component.coalesceMap);
        final Class superclass = clazz.getSuperclass();
        if (superclass == null) {
            return false;
        }
        if (superclass.getClassLoader() != null) {
            final Boolean b = Component.coalesceMap.get(superclass);
            if (b == null) {
                if (isCoalesceEventsOverriden(superclass)) {
                    Component.coalesceMap.put(superclass, true);
                    return true;
                }
            }
            else if (b) {
                return true;
            }
        }
        try {
            clazz.getDeclaredMethod("coalesceEvents", (Class[])Component.coalesceEventsParams);
            return true;
        }
        catch (final NoSuchMethodException ex) {
            return false;
        }
    }
    
    final boolean isCoalescingEnabled() {
        return this.coalescingEnabled;
    }
    
    protected AWTEvent coalesceEvents(final AWTEvent awtEvent, final AWTEvent awtEvent2) {
        return null;
    }
    
    protected void processEvent(final AWTEvent awtEvent) {
        if (awtEvent instanceof FocusEvent) {
            this.processFocusEvent((FocusEvent)awtEvent);
        }
        else if (awtEvent instanceof MouseEvent) {
            switch (awtEvent.getID()) {
                case 500:
                case 501:
                case 502:
                case 504:
                case 505: {
                    this.processMouseEvent((MouseEvent)awtEvent);
                    break;
                }
                case 503:
                case 506: {
                    this.processMouseMotionEvent((MouseEvent)awtEvent);
                    break;
                }
                case 507: {
                    this.processMouseWheelEvent((MouseWheelEvent)awtEvent);
                    break;
                }
            }
        }
        else if (awtEvent instanceof KeyEvent) {
            this.processKeyEvent((KeyEvent)awtEvent);
        }
        else if (awtEvent instanceof ComponentEvent) {
            this.processComponentEvent((ComponentEvent)awtEvent);
        }
        else if (awtEvent instanceof InputMethodEvent) {
            this.processInputMethodEvent((InputMethodEvent)awtEvent);
        }
        else if (awtEvent instanceof HierarchyEvent) {
            switch (awtEvent.getID()) {
                case 1400: {
                    this.processHierarchyEvent((HierarchyEvent)awtEvent);
                    break;
                }
                case 1401:
                case 1402: {
                    this.processHierarchyBoundsEvent((HierarchyEvent)awtEvent);
                    break;
                }
            }
        }
    }
    
    protected void processComponentEvent(final ComponentEvent componentEvent) {
        final ComponentListener componentListener = this.componentListener;
        if (componentListener != null) {
            switch (componentEvent.getID()) {
                case 101: {
                    componentListener.componentResized(componentEvent);
                    break;
                }
                case 100: {
                    componentListener.componentMoved(componentEvent);
                    break;
                }
                case 102: {
                    componentListener.componentShown(componentEvent);
                    break;
                }
                case 103: {
                    componentListener.componentHidden(componentEvent);
                    break;
                }
            }
        }
    }
    
    protected void processFocusEvent(final FocusEvent focusEvent) {
        final FocusListener focusListener = this.focusListener;
        if (focusListener != null) {
            switch (focusEvent.getID()) {
                case 1004: {
                    focusListener.focusGained(focusEvent);
                    break;
                }
                case 1005: {
                    focusListener.focusLost(focusEvent);
                    break;
                }
            }
        }
    }
    
    protected void processKeyEvent(final KeyEvent keyEvent) {
        final KeyListener keyListener = this.keyListener;
        if (keyListener != null) {
            switch (keyEvent.getID()) {
                case 400: {
                    keyListener.keyTyped(keyEvent);
                    break;
                }
                case 401: {
                    keyListener.keyPressed(keyEvent);
                    break;
                }
                case 402: {
                    keyListener.keyReleased(keyEvent);
                    break;
                }
            }
        }
    }
    
    protected void processMouseEvent(final MouseEvent mouseEvent) {
        final MouseListener mouseListener = this.mouseListener;
        if (mouseListener != null) {
            switch (mouseEvent.getID()) {
                case 501: {
                    mouseListener.mousePressed(mouseEvent);
                    break;
                }
                case 502: {
                    mouseListener.mouseReleased(mouseEvent);
                    break;
                }
                case 500: {
                    mouseListener.mouseClicked(mouseEvent);
                    break;
                }
                case 505: {
                    mouseListener.mouseExited(mouseEvent);
                    break;
                }
                case 504: {
                    mouseListener.mouseEntered(mouseEvent);
                    break;
                }
            }
        }
    }
    
    protected void processMouseMotionEvent(final MouseEvent mouseEvent) {
        final MouseMotionListener mouseMotionListener = this.mouseMotionListener;
        if (mouseMotionListener != null) {
            switch (mouseEvent.getID()) {
                case 503: {
                    mouseMotionListener.mouseMoved(mouseEvent);
                    break;
                }
                case 506: {
                    mouseMotionListener.mouseDragged(mouseEvent);
                    break;
                }
            }
        }
    }
    
    protected void processMouseWheelEvent(final MouseWheelEvent mouseWheelEvent) {
        final MouseWheelListener mouseWheelListener = this.mouseWheelListener;
        if (mouseWheelListener != null) {
            switch (mouseWheelEvent.getID()) {
                case 507: {
                    mouseWheelListener.mouseWheelMoved(mouseWheelEvent);
                    break;
                }
            }
        }
    }
    
    boolean postsOldMouseEvents() {
        return false;
    }
    
    protected void processInputMethodEvent(final InputMethodEvent inputMethodEvent) {
        final InputMethodListener inputMethodListener = this.inputMethodListener;
        if (inputMethodListener != null) {
            switch (inputMethodEvent.getID()) {
                case 1100: {
                    inputMethodListener.inputMethodTextChanged(inputMethodEvent);
                    break;
                }
                case 1101: {
                    inputMethodListener.caretPositionChanged(inputMethodEvent);
                    break;
                }
            }
        }
    }
    
    protected void processHierarchyEvent(final HierarchyEvent hierarchyEvent) {
        final HierarchyListener hierarchyListener = this.hierarchyListener;
        if (hierarchyListener != null) {
            switch (hierarchyEvent.getID()) {
                case 1400: {
                    hierarchyListener.hierarchyChanged(hierarchyEvent);
                    break;
                }
            }
        }
    }
    
    protected void processHierarchyBoundsEvent(final HierarchyEvent hierarchyEvent) {
        final HierarchyBoundsListener hierarchyBoundsListener = this.hierarchyBoundsListener;
        if (hierarchyBoundsListener != null) {
            switch (hierarchyEvent.getID()) {
                case 1401: {
                    hierarchyBoundsListener.ancestorMoved(hierarchyEvent);
                    break;
                }
                case 1402: {
                    hierarchyBoundsListener.ancestorResized(hierarchyEvent);
                    break;
                }
            }
        }
    }
    
    @Deprecated
    public boolean handleEvent(final Event event) {
        switch (event.id) {
            case 504: {
                return this.mouseEnter(event, event.x, event.y);
            }
            case 505: {
                return this.mouseExit(event, event.x, event.y);
            }
            case 503: {
                return this.mouseMove(event, event.x, event.y);
            }
            case 501: {
                return this.mouseDown(event, event.x, event.y);
            }
            case 506: {
                return this.mouseDrag(event, event.x, event.y);
            }
            case 502: {
                return this.mouseUp(event, event.x, event.y);
            }
            case 401:
            case 403: {
                return this.keyDown(event, event.key);
            }
            case 402:
            case 404: {
                return this.keyUp(event, event.key);
            }
            case 1001: {
                return this.action(event, event.arg);
            }
            case 1004: {
                return this.gotFocus(event, event.arg);
            }
            case 1005: {
                return this.lostFocus(event, event.arg);
            }
            default: {
                return false;
            }
        }
    }
    
    @Deprecated
    public boolean mouseDown(final Event event, final int n, final int n2) {
        return false;
    }
    
    @Deprecated
    public boolean mouseDrag(final Event event, final int n, final int n2) {
        return false;
    }
    
    @Deprecated
    public boolean mouseUp(final Event event, final int n, final int n2) {
        return false;
    }
    
    @Deprecated
    public boolean mouseMove(final Event event, final int n, final int n2) {
        return false;
    }
    
    @Deprecated
    public boolean mouseEnter(final Event event, final int n, final int n2) {
        return false;
    }
    
    @Deprecated
    public boolean mouseExit(final Event event, final int n, final int n2) {
        return false;
    }
    
    @Deprecated
    public boolean keyDown(final Event event, final int n) {
        return false;
    }
    
    @Deprecated
    public boolean keyUp(final Event event, final int n) {
        return false;
    }
    
    @Deprecated
    public boolean action(final Event event, final Object o) {
        return false;
    }
    
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            ComponentPeer peer = this.peer;
            if (peer == null || peer instanceof LightweightPeer) {
                if (peer == null) {
                    peer = (this.peer = this.getToolkit().createComponent(this));
                }
                if (this.parent != null) {
                    long n = 0L;
                    if (this.mouseListener != null || (this.eventMask & 0x10L) != 0x0L) {
                        n |= 0x10L;
                    }
                    if (this.mouseMotionListener != null || (this.eventMask & 0x20L) != 0x0L) {
                        n |= 0x20L;
                    }
                    if (this.mouseWheelListener != null || (this.eventMask & 0x20000L) != 0x0L) {
                        n |= 0x20000L;
                    }
                    if (this.focusListener != null || (this.eventMask & 0x4L) != 0x0L) {
                        n |= 0x4L;
                    }
                    if (this.keyListener != null || (this.eventMask & 0x8L) != 0x0L) {
                        n |= 0x8L;
                    }
                    if (n != 0L) {
                        this.parent.proxyEnableEvents(n);
                    }
                }
            }
            else {
                final Container container = this.getContainer();
                if (container != null && container.isLightweight()) {
                    this.relocateComponent();
                    if (!container.isRecursivelyVisibleUpToHeavyweightContainer()) {
                        peer.setVisible(false);
                    }
                }
            }
            this.invalidate();
            for (int n2 = (this.popups != null) ? this.popups.size() : 0, i = 0; i < n2; ++i) {
                this.popups.elementAt(i).addNotify();
            }
            if (this.dropTarget != null) {
                this.dropTarget.addNotify(peer);
            }
            this.peerFont = this.getFont();
            if (this.getContainer() != null && !this.isAddNotifyComplete) {
                this.getContainer().increaseComponentCount(this);
            }
            this.updateZOrder();
            if (!this.isAddNotifyComplete) {
                this.mixOnShowing();
            }
            this.isAddNotifyComplete = true;
            if (this.hierarchyListener != null || (this.eventMask & 0x8000L) != 0x0L || Toolkit.enabledOnToolkit(32768L)) {
                this.dispatchEvent(new HierarchyEvent(this, 1400, this, this.parent, 0x2 | (this.isRecursivelyVisible() ? 4 : 0)));
            }
        }
    }
    
    public void removeNotify() {
        KeyboardFocusManager.clearMostRecentFocusOwner(this);
        if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner() == this) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalPermanentFocusOwner(null);
        }
        synchronized (this.getTreeLock()) {
            if (this.isFocusOwner() && KeyboardFocusManager.isAutoFocusTransferEnabledFor(this)) {
                this.transferFocus(true);
            }
            if (this.getContainer() != null && this.isAddNotifyComplete) {
                this.getContainer().decreaseComponentCount(this);
            }
            for (int n = (this.popups != null) ? this.popups.size() : 0, i = 0; i < n; ++i) {
                this.popups.elementAt(i).removeNotify();
            }
            if ((this.eventMask & 0x1000L) != 0x0L) {
                final InputContext inputContext = this.getInputContext();
                if (inputContext != null) {
                    inputContext.removeNotify(this);
                }
            }
            final ComponentPeer peer = this.peer;
            if (peer != null) {
                final boolean lightweight = this.isLightweight();
                if (this.bufferStrategy instanceof FlipBufferStrategy) {
                    ((FlipBufferStrategy)this.bufferStrategy).destroyBuffers();
                }
                if (this.dropTarget != null) {
                    this.dropTarget.removeNotify(this.peer);
                }
                if (this.visible) {
                    peer.setVisible(false);
                }
                this.peer = null;
                this.peerFont = null;
                Toolkit.getEventQueue().removeSourceEvents(this, false);
                KeyboardFocusManager.getCurrentKeyboardFocusManager().discardKeyEvents(this);
                peer.dispose();
                this.mixOnHiding(lightweight);
                this.isAddNotifyComplete = false;
                this.compoundShape = null;
            }
            if (this.hierarchyListener != null || (this.eventMask & 0x8000L) != 0x0L || Toolkit.enabledOnToolkit(32768L)) {
                this.dispatchEvent(new HierarchyEvent(this, 1400, this, this.parent, 0x2 | (this.isRecursivelyVisible() ? 4 : 0)));
            }
        }
    }
    
    @Deprecated
    public boolean gotFocus(final Event event, final Object o) {
        return false;
    }
    
    @Deprecated
    public boolean lostFocus(final Event event, final Object o) {
        return false;
    }
    
    @Deprecated
    public boolean isFocusTraversable() {
        if (this.isFocusTraversableOverridden == 0) {
            this.isFocusTraversableOverridden = 1;
        }
        return this.focusable;
    }
    
    public boolean isFocusable() {
        return this.isFocusTraversable();
    }
    
    public void setFocusable(final boolean focusable) {
        final boolean focusable2;
        synchronized (this) {
            focusable2 = this.focusable;
            this.focusable = focusable;
        }
        this.isFocusTraversableOverridden = 2;
        this.firePropertyChange("focusable", focusable2, focusable);
        if (focusable2 && !focusable) {
            if (this.isFocusOwner() && KeyboardFocusManager.isAutoFocusTransferEnabled()) {
                this.transferFocus(true);
            }
            KeyboardFocusManager.clearMostRecentFocusOwner(this);
        }
    }
    
    final boolean isFocusTraversableOverridden() {
        return this.isFocusTraversableOverridden != 1;
    }
    
    public void setFocusTraversalKeys(final int n, final Set<? extends AWTKeyStroke> set) {
        if (n < 0 || n >= 3) {
            throw new IllegalArgumentException("invalid focus traversal key identifier");
        }
        this.setFocusTraversalKeys_NoIDCheck(n, set);
    }
    
    public Set<AWTKeyStroke> getFocusTraversalKeys(final int n) {
        if (n < 0 || n >= 3) {
            throw new IllegalArgumentException("invalid focus traversal key identifier");
        }
        return this.getFocusTraversalKeys_NoIDCheck(n);
    }
    
    final void setFocusTraversalKeys_NoIDCheck(final int n, final Set<? extends AWTKeyStroke> set) {
        final Set<AWTKeyStroke> set2;
        synchronized (this) {
            if (this.focusTraversalKeys == null) {
                this.initializeFocusTraversalKeys();
            }
            if (set != null) {
                for (final AWTKeyStroke awtKeyStroke : set) {
                    if (awtKeyStroke == null) {
                        throw new IllegalArgumentException("cannot set null focus traversal key");
                    }
                    if (awtKeyStroke.getKeyChar() != '\uffff') {
                        throw new IllegalArgumentException("focus traversal keys cannot map to KEY_TYPED events");
                    }
                    for (int i = 0; i < this.focusTraversalKeys.length; ++i) {
                        if (i != n) {
                            if (this.getFocusTraversalKeys_NoIDCheck(i).contains(awtKeyStroke)) {
                                throw new IllegalArgumentException("focus traversal keys must be unique for a Component");
                            }
                        }
                    }
                }
            }
            set2 = this.focusTraversalKeys[n];
            this.focusTraversalKeys[n] = ((set != null) ? Collections.unmodifiableSet((Set<? extends AWTKeyStroke>)new HashSet<AWTKeyStroke>(set)) : null);
        }
        this.firePropertyChange(Component.focusTraversalKeyPropertyNames[n], set2, set);
    }
    
    final Set<AWTKeyStroke> getFocusTraversalKeys_NoIDCheck(final int n) {
        final Set<AWTKeyStroke> set = (this.focusTraversalKeys != null) ? this.focusTraversalKeys[n] : null;
        if (set != null) {
            return set;
        }
        final Container parent = this.parent;
        if (parent != null) {
            return parent.getFocusTraversalKeys(n);
        }
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalKeys(n);
    }
    
    public boolean areFocusTraversalKeysSet(final int n) {
        if (n < 0 || n >= 3) {
            throw new IllegalArgumentException("invalid focus traversal key identifier");
        }
        return this.focusTraversalKeys != null && this.focusTraversalKeys[n] != null;
    }
    
    public void setFocusTraversalKeysEnabled(final boolean focusTraversalKeysEnabled) {
        final boolean focusTraversalKeysEnabled2;
        synchronized (this) {
            focusTraversalKeysEnabled2 = this.focusTraversalKeysEnabled;
            this.focusTraversalKeysEnabled = focusTraversalKeysEnabled;
        }
        this.firePropertyChange("focusTraversalKeysEnabled", focusTraversalKeysEnabled2, focusTraversalKeysEnabled);
    }
    
    public boolean getFocusTraversalKeysEnabled() {
        return this.focusTraversalKeysEnabled;
    }
    
    public void requestFocus() {
        this.requestFocusHelper(false, true);
    }
    
    boolean requestFocus(final CausedFocusEvent.Cause cause) {
        return this.requestFocusHelper(false, true, cause);
    }
    
    protected boolean requestFocus(final boolean b) {
        return this.requestFocusHelper(b, true);
    }
    
    boolean requestFocus(final boolean b, final CausedFocusEvent.Cause cause) {
        return this.requestFocusHelper(b, true, cause);
    }
    
    public boolean requestFocusInWindow() {
        return this.requestFocusHelper(false, false);
    }
    
    boolean requestFocusInWindow(final CausedFocusEvent.Cause cause) {
        return this.requestFocusHelper(false, false, cause);
    }
    
    protected boolean requestFocusInWindow(final boolean b) {
        return this.requestFocusHelper(b, false);
    }
    
    boolean requestFocusInWindow(final boolean b, final CausedFocusEvent.Cause cause) {
        return this.requestFocusHelper(b, false, cause);
    }
    
    final boolean requestFocusHelper(final boolean b, final boolean b2) {
        return this.requestFocusHelper(b, b2, CausedFocusEvent.Cause.UNKNOWN);
    }
    
    final boolean requestFocusHelper(final boolean b, boolean b2, final CausedFocusEvent.Cause cause) {
        final AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof MouseEvent && SunToolkit.isSystemGenerated(currentEvent)) {
            final Component component = ((MouseEvent)currentEvent).getComponent();
            if (component == null || component.getContainingWindow() == this.getContainingWindow()) {
                Component.focusLog.finest("requesting focus by mouse event \"in window\"");
                b2 = false;
            }
        }
        if (!this.isRequestFocusAccepted(b, b2, cause)) {
            if (Component.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                Component.focusLog.finest("requestFocus is not accepted");
            }
            return false;
        }
        KeyboardFocusManager.setMostRecentFocusOwner(this);
        for (Component parent = this; parent != null && !(parent instanceof Window); parent = parent.parent) {
            if (!parent.isVisible()) {
                if (Component.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                    Component.focusLog.finest("component is recurively invisible");
                }
                return false;
            }
        }
        final Component component2 = (this.peer instanceof LightweightPeer) ? this.getNativeContainer() : this;
        if (component2 == null || !component2.isVisible()) {
            if (Component.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                Component.focusLog.finest("Component is not a part of visible hierarchy");
            }
            return false;
        }
        final ComponentPeer peer = component2.peer;
        if (peer == null) {
            if (Component.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                Component.focusLog.finest("Peer is null");
            }
            return false;
        }
        long n;
        if (EventQueue.isDispatchThread()) {
            n = Toolkit.getEventQueue().getMostRecentKeyEventTime();
        }
        else {
            n = System.currentTimeMillis();
        }
        final boolean requestFocus = peer.requestFocus(this, b, b2, n, cause);
        if (!requestFocus) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager(this.appContext).dequeueKeyEvents(n, this);
            if (Component.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                Component.focusLog.finest("Peer request failed");
            }
        }
        else if (Component.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
            Component.focusLog.finest("Pass for " + this);
        }
        return requestFocus;
    }
    
    private boolean isRequestFocusAccepted(final boolean b, final boolean b2, final CausedFocusEvent.Cause cause) {
        if (!this.isFocusable() || !this.isVisible()) {
            if (Component.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                Component.focusLog.finest("Not focusable or not visible");
            }
            return false;
        }
        if (this.peer == null) {
            if (Component.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                Component.focusLog.finest("peer is null");
            }
            return false;
        }
        final Window containingWindow = this.getContainingWindow();
        if (containingWindow == null || !containingWindow.isFocusableWindow()) {
            if (Component.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                Component.focusLog.finest("Component doesn't have toplevel");
            }
            return false;
        }
        Component component = KeyboardFocusManager.getMostRecentFocusOwner(containingWindow);
        if (component == null) {
            component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (component != null && component.getContainingWindow() != containingWindow) {
                component = null;
            }
        }
        if (component == this || component == null) {
            if (Component.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                Component.focusLog.finest("focus owner is null or this");
            }
            return true;
        }
        if (CausedFocusEvent.Cause.ACTIVATION == cause) {
            if (Component.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                Component.focusLog.finest("cause is activation");
            }
            return true;
        }
        final boolean acceptRequestFocus = Component.requestFocusController.acceptRequestFocus(component, this, b, b2, cause);
        if (Component.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
            Component.focusLog.finest("RequestFocusController returns {0}", acceptRequestFocus);
        }
        return acceptRequestFocus;
    }
    
    static synchronized void setRequestFocusController(final RequestFocusController requestFocusController) {
        if (requestFocusController == null) {
            Component.requestFocusController = new DummyRequestFocusController();
        }
        else {
            Component.requestFocusController = requestFocusController;
        }
    }
    
    public Container getFocusCycleRootAncestor() {
        Container container;
        for (container = this.parent; container != null && !container.isFocusCycleRoot(); container = container.parent) {}
        return container;
    }
    
    public boolean isFocusCycleRoot(final Container container) {
        return this.getFocusCycleRootAncestor() == container;
    }
    
    Container getTraversalRoot() {
        return this.getFocusCycleRootAncestor();
    }
    
    public void transferFocus() {
        this.nextFocus();
    }
    
    @Deprecated
    public void nextFocus() {
        this.transferFocus(false);
    }
    
    boolean transferFocus(final boolean b) {
        if (Component.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            Component.focusLog.finer("clearOnFailure = " + b);
        }
        final Component nextFocusCandidate = this.getNextFocusCandidate();
        boolean requestFocusInWindow = false;
        if (nextFocusCandidate != null && !nextFocusCandidate.isFocusOwner() && nextFocusCandidate != this) {
            requestFocusInWindow = nextFocusCandidate.requestFocusInWindow(CausedFocusEvent.Cause.TRAVERSAL_FORWARD);
        }
        if (b && !requestFocusInWindow) {
            if (Component.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                Component.focusLog.finer("clear global focus owner");
            }
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwnerPriv();
        }
        if (Component.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            Component.focusLog.finer("returning result: " + requestFocusInWindow);
        }
        return requestFocusInWindow;
    }
    
    final Component getNextFocusCandidate() {
        Container container;
        Component component;
        for (container = this.getTraversalRoot(), component = this; container != null && (!container.isShowing() || !container.canBeFocusOwner()); container = component.getFocusCycleRootAncestor()) {
            component = container;
        }
        if (Component.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            Component.focusLog.finer("comp = " + component + ", root = " + container);
        }
        Object o = null;
        if (container != null) {
            final FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
            Component component2 = focusTraversalPolicy.getComponentAfter(container, component);
            if (Component.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                Component.focusLog.finer("component after is " + component2);
            }
            if (component2 == null) {
                component2 = focusTraversalPolicy.getDefaultComponent(container);
                if (Component.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                    Component.focusLog.finer("default component is " + component2);
                }
            }
            if (component2 == null) {
                final Applet appletIfAncestor = EmbeddedFrame.getAppletIfAncestorOf(this);
                if (appletIfAncestor != null) {
                    component2 = appletIfAncestor;
                }
            }
            o = component2;
        }
        if (Component.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            Component.focusLog.finer("Focus transfer candidate: " + o);
        }
        return (Component)o;
    }
    
    public void transferFocusBackward() {
        this.transferFocusBackward(false);
    }
    
    boolean transferFocusBackward(final boolean b) {
        Container container;
        Component component;
        for (container = this.getTraversalRoot(), component = this; container != null && (!container.isShowing() || !container.canBeFocusOwner()); container = component.getFocusCycleRootAncestor()) {
            component = container;
        }
        boolean requestFocusInWindow = false;
        if (container != null) {
            final FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
            Component component2 = focusTraversalPolicy.getComponentBefore(container, component);
            if (component2 == null) {
                component2 = focusTraversalPolicy.getDefaultComponent(container);
            }
            if (component2 != null) {
                requestFocusInWindow = component2.requestFocusInWindow(CausedFocusEvent.Cause.TRAVERSAL_BACKWARD);
            }
        }
        if (b && !requestFocusInWindow) {
            if (Component.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                Component.focusLog.finer("clear global focus owner");
            }
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwnerPriv();
        }
        if (Component.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            Component.focusLog.finer("returning result: " + requestFocusInWindow);
        }
        return requestFocusInWindow;
    }
    
    public void transferFocusUpCycle() {
        Container container;
        for (container = this.getFocusCycleRootAncestor(); container != null && (!container.isShowing() || !container.isFocusable() || !container.isEnabled()); container = container.getFocusCycleRootAncestor()) {}
        if (container != null) {
            final Container focusCycleRootAncestor = container.getFocusCycleRootAncestor();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRootPriv((focusCycleRootAncestor != null) ? focusCycleRootAncestor : container);
            container.requestFocus(CausedFocusEvent.Cause.TRAVERSAL_UP);
        }
        else {
            final Window containingWindow = this.getContainingWindow();
            if (containingWindow != null) {
                final Component defaultComponent = containingWindow.getFocusTraversalPolicy().getDefaultComponent(containingWindow);
                if (defaultComponent != null) {
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRootPriv(containingWindow);
                    defaultComponent.requestFocus(CausedFocusEvent.Cause.TRAVERSAL_UP);
                }
            }
        }
    }
    
    public boolean hasFocus() {
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this;
    }
    
    public boolean isFocusOwner() {
        return this.hasFocus();
    }
    
    void setAutoFocusTransferOnDisposal(final boolean autoFocusTransferOnDisposal) {
        this.autoFocusTransferOnDisposal = autoFocusTransferOnDisposal;
    }
    
    boolean isAutoFocusTransferOnDisposal() {
        return this.autoFocusTransferOnDisposal;
    }
    
    public void add(final PopupMenu popupMenu) {
        synchronized (this.getTreeLock()) {
            if (popupMenu.parent != null) {
                popupMenu.parent.remove(popupMenu);
            }
            if (this.popups == null) {
                this.popups = new Vector<PopupMenu>();
            }
            this.popups.addElement(popupMenu);
            popupMenu.parent = this;
            if (this.peer != null && popupMenu.peer == null) {
                popupMenu.addNotify();
            }
        }
    }
    
    @Override
    public void remove(final MenuComponent menuComponent) {
        synchronized (this.getTreeLock()) {
            if (this.popups == null) {
                return;
            }
            final int index = this.popups.indexOf(menuComponent);
            if (index >= 0) {
                final PopupMenu popupMenu = (PopupMenu)menuComponent;
                if (popupMenu.peer != null) {
                    popupMenu.removeNotify();
                }
                popupMenu.parent = null;
                this.popups.removeElementAt(index);
                if (this.popups.size() == 0) {
                    this.popups = null;
                }
            }
        }
    }
    
    protected String paramString() {
        return Objects.toString(this.getName(), "") + ',' + this.x + ',' + this.y + ',' + this.width + 'x' + this.height + (this.isValid() ? "" : ",invalid") + (this.visible ? "" : ",hidden") + (this.enabled ? "" : ",disabled");
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + '[' + this.paramString() + ']';
    }
    
    public void list() {
        this.list(System.out, 0);
    }
    
    public void list(final PrintStream printStream) {
        this.list(printStream, 0);
    }
    
    public void list(final PrintStream printStream, final int n) {
        for (int i = 0; i < n; ++i) {
            printStream.print(" ");
        }
        printStream.println(this);
    }
    
    public void list(final PrintWriter printWriter) {
        this.list(printWriter, 0);
    }
    
    public void list(final PrintWriter printWriter, final int n) {
        for (int i = 0; i < n; ++i) {
            printWriter.print(" ");
        }
        printWriter.println(this);
    }
    
    final Container getNativeContainer() {
        Container container;
        for (container = this.getContainer(); container != null && container.peer instanceof LightweightPeer; container = container.getContainer()) {}
        return container;
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        synchronized (this.getObjectLock()) {
            if (propertyChangeListener == null) {
                return;
            }
            if (this.changeSupport == null) {
                this.changeSupport = new PropertyChangeSupport(this);
            }
            this.changeSupport.addPropertyChangeListener(propertyChangeListener);
        }
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        synchronized (this.getObjectLock()) {
            if (propertyChangeListener == null || this.changeSupport == null) {
                return;
            }
            this.changeSupport.removePropertyChangeListener(propertyChangeListener);
        }
    }
    
    public PropertyChangeListener[] getPropertyChangeListeners() {
        synchronized (this.getObjectLock()) {
            if (this.changeSupport == null) {
                return new PropertyChangeListener[0];
            }
            return this.changeSupport.getPropertyChangeListeners();
        }
    }
    
    public void addPropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        synchronized (this.getObjectLock()) {
            if (propertyChangeListener == null) {
                return;
            }
            if (this.changeSupport == null) {
                this.changeSupport = new PropertyChangeSupport(this);
            }
            this.changeSupport.addPropertyChangeListener(s, propertyChangeListener);
        }
    }
    
    public void removePropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        synchronized (this.getObjectLock()) {
            if (propertyChangeListener == null || this.changeSupport == null) {
                return;
            }
            this.changeSupport.removePropertyChangeListener(s, propertyChangeListener);
        }
    }
    
    public PropertyChangeListener[] getPropertyChangeListeners(final String s) {
        synchronized (this.getObjectLock()) {
            if (this.changeSupport == null) {
                return new PropertyChangeListener[0];
            }
            return this.changeSupport.getPropertyChangeListeners(s);
        }
    }
    
    protected void firePropertyChange(final String s, final Object o, final Object o2) {
        final PropertyChangeSupport changeSupport;
        synchronized (this.getObjectLock()) {
            changeSupport = this.changeSupport;
        }
        if (changeSupport == null || (o != null && o2 != null && o.equals(o2))) {
            return;
        }
        changeSupport.firePropertyChange(s, o, o2);
    }
    
    protected void firePropertyChange(final String s, final boolean b, final boolean b2) {
        final PropertyChangeSupport changeSupport = this.changeSupport;
        if (changeSupport == null || b == b2) {
            return;
        }
        changeSupport.firePropertyChange(s, b, b2);
    }
    
    protected void firePropertyChange(final String s, final int n, final int n2) {
        final PropertyChangeSupport changeSupport = this.changeSupport;
        if (changeSupport == null || n == n2) {
            return;
        }
        changeSupport.firePropertyChange(s, n, n2);
    }
    
    public void firePropertyChange(final String s, final byte b, final byte b2) {
        if (this.changeSupport == null || b == b2) {
            return;
        }
        this.firePropertyChange(s, b, (Object)b2);
    }
    
    public void firePropertyChange(final String s, final char c, final char c2) {
        if (this.changeSupport == null || c == c2) {
            return;
        }
        this.firePropertyChange(s, new Character(c), new Character(c2));
    }
    
    public void firePropertyChange(final String s, final short n, final short n2) {
        if (this.changeSupport == null || n == n2) {
            return;
        }
        this.firePropertyChange(s, n, (Object)n2);
    }
    
    public void firePropertyChange(final String s, final long n, final long n2) {
        if (this.changeSupport == null || n == n2) {
            return;
        }
        this.firePropertyChange(s, n, (Object)n2);
    }
    
    public void firePropertyChange(final String s, final float n, final float n2) {
        if (this.changeSupport == null || n == n2) {
            return;
        }
        this.firePropertyChange(s, n, (Object)n2);
    }
    
    public void firePropertyChange(final String s, final double n, final double n2) {
        if (this.changeSupport == null || n == n2) {
            return;
        }
        this.firePropertyChange(s, n, (Object)n2);
    }
    
    private void doSwingSerialization() {
        final Package package1 = Package.getPackage("javax.swing");
        for (Class<? extends Component> clazz = this.getClass(); clazz != null; clazz = (Class<? extends Component>)clazz.getSuperclass()) {
            if (clazz.getPackage() == package1 && clazz.getClassLoader() == null) {
                final Method[] array = AccessController.doPrivileged((PrivilegedAction<Method[]>)new PrivilegedAction<Method[]>() {
                    @Override
                    public Method[] run() {
                        return clazz.getDeclaredMethods();
                    }
                });
                for (int i = array.length - 1; i >= 0; --i) {
                    final Method method = array[i];
                    if (method.getName().equals("compWriteObjectNotify")) {
                        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                            @Override
                            public Void run() {
                                method.setAccessible(true);
                                return null;
                            }
                        });
                        try {
                            method.invoke(this, (Object[])null);
                        }
                        catch (final IllegalAccessException ex) {}
                        catch (final InvocationTargetException ex2) {}
                        return;
                    }
                }
            }
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        this.doSwingSerialization();
        objectOutputStream.defaultWriteObject();
        AWTEventMulticaster.save(objectOutputStream, "componentL", this.componentListener);
        AWTEventMulticaster.save(objectOutputStream, "focusL", this.focusListener);
        AWTEventMulticaster.save(objectOutputStream, "keyL", this.keyListener);
        AWTEventMulticaster.save(objectOutputStream, "mouseL", this.mouseListener);
        AWTEventMulticaster.save(objectOutputStream, "mouseMotionL", this.mouseMotionListener);
        AWTEventMulticaster.save(objectOutputStream, "inputMethodL", this.inputMethodListener);
        objectOutputStream.writeObject(null);
        objectOutputStream.writeObject(this.componentOrientation);
        AWTEventMulticaster.save(objectOutputStream, "hierarchyL", this.hierarchyListener);
        AWTEventMulticaster.save(objectOutputStream, "hierarchyBoundsL", this.hierarchyBoundsListener);
        objectOutputStream.writeObject(null);
        AWTEventMulticaster.save(objectOutputStream, "mouseWheelL", this.mouseWheelListener);
        objectOutputStream.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        this.objectLock = new Object();
        this.acc = AccessController.getContext();
        objectInputStream.defaultReadObject();
        this.appContext = AppContext.getAppContext();
        this.coalescingEnabled = this.checkCoalescing();
        if (this.componentSerializedDataVersion < 4) {
            this.focusable = true;
            this.isFocusTraversableOverridden = 0;
            this.initializeFocusTraversalKeys();
            this.focusTraversalKeysEnabled = true;
        }
        Object object;
        while (null != (object = objectInputStream.readObject())) {
            final String intern = ((String)object).intern();
            if ("componentL" == intern) {
                this.addComponentListener((ComponentListener)objectInputStream.readObject());
            }
            else if ("focusL" == intern) {
                this.addFocusListener((FocusListener)objectInputStream.readObject());
            }
            else if ("keyL" == intern) {
                this.addKeyListener((KeyListener)objectInputStream.readObject());
            }
            else if ("mouseL" == intern) {
                this.addMouseListener((MouseListener)objectInputStream.readObject());
            }
            else if ("mouseMotionL" == intern) {
                this.addMouseMotionListener((MouseMotionListener)objectInputStream.readObject());
            }
            else if ("inputMethodL" == intern) {
                this.addInputMethodListener((InputMethodListener)objectInputStream.readObject());
            }
            else {
                objectInputStream.readObject();
            }
        }
        Object object2 = null;
        try {
            object2 = objectInputStream.readObject();
        }
        catch (final OptionalDataException ex) {
            if (!ex.eof) {
                throw ex;
            }
        }
        if (object2 != null) {
            this.componentOrientation = (ComponentOrientation)object2;
        }
        else {
            this.componentOrientation = ComponentOrientation.UNKNOWN;
        }
        try {
            Object object3;
            while (null != (object3 = objectInputStream.readObject())) {
                final String intern2 = ((String)object3).intern();
                if ("hierarchyL" == intern2) {
                    this.addHierarchyListener((HierarchyListener)objectInputStream.readObject());
                }
                else if ("hierarchyBoundsL" == intern2) {
                    this.addHierarchyBoundsListener((HierarchyBoundsListener)objectInputStream.readObject());
                }
                else {
                    objectInputStream.readObject();
                }
            }
        }
        catch (final OptionalDataException ex2) {
            if (!ex2.eof) {
                throw ex2;
            }
        }
        try {
            Object object4;
            while (null != (object4 = objectInputStream.readObject())) {
                if ("mouseWheelL" == ((String)object4).intern()) {
                    this.addMouseWheelListener((MouseWheelListener)objectInputStream.readObject());
                }
                else {
                    objectInputStream.readObject();
                }
            }
        }
        catch (final OptionalDataException ex3) {
            if (!ex3.eof) {
                throw ex3;
            }
        }
        if (this.popups != null) {
            for (int size = this.popups.size(), i = 0; i < size; ++i) {
                this.popups.elementAt(i).parent = this;
            }
        }
    }
    
    public void setComponentOrientation(final ComponentOrientation componentOrientation) {
        this.firePropertyChange("componentOrientation", this.componentOrientation, this.componentOrientation = componentOrientation);
        this.invalidateIfValid();
    }
    
    public ComponentOrientation getComponentOrientation() {
        return this.componentOrientation;
    }
    
    public void applyComponentOrientation(final ComponentOrientation componentOrientation) {
        if (componentOrientation == null) {
            throw new NullPointerException();
        }
        this.setComponentOrientation(componentOrientation);
    }
    
    final boolean canBeFocusOwner() {
        return this.isEnabled() && this.isDisplayable() && this.isVisible() && this.isFocusable();
    }
    
    final boolean canBeFocusOwnerRecursively() {
        if (!this.canBeFocusOwner()) {
            return false;
        }
        synchronized (this.getTreeLock()) {
            if (this.parent != null) {
                return this.parent.canContainFocusOwner(this);
            }
        }
        return true;
    }
    
    final void relocateComponent() {
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                return;
            }
            int x = this.x;
            int y = this.y;
            for (Container container = this.getContainer(); container != null && container.isLightweight(); container = container.getContainer()) {
                x += container.x;
                y += container.y;
            }
            this.peer.setBounds(x, y, this.width, this.height, 1);
        }
    }
    
    Window getContainingWindow() {
        return SunToolkit.getContainingWindow(this);
    }
    
    private static native void initIDs();
    
    public AccessibleContext getAccessibleContext() {
        return this.accessibleContext;
    }
    
    int getAccessibleIndexInParent() {
        synchronized (this.getTreeLock()) {
            int n = -1;
            final Container parent = this.getParent();
            if (parent != null && parent instanceof Accessible) {
                final Component[] components = parent.getComponents();
                for (int i = 0; i < components.length; ++i) {
                    if (components[i] instanceof Accessible) {
                        ++n;
                    }
                    if (this.equals(components[i])) {
                        return n;
                    }
                }
            }
            return -1;
        }
    }
    
    AccessibleStateSet getAccessibleStateSet() {
        synchronized (this.getTreeLock()) {
            final AccessibleStateSet set = new AccessibleStateSet();
            if (this.isEnabled()) {
                set.add(AccessibleState.ENABLED);
            }
            if (this.isFocusTraversable()) {
                set.add(AccessibleState.FOCUSABLE);
            }
            if (this.isVisible()) {
                set.add(AccessibleState.VISIBLE);
            }
            if (this.isShowing()) {
                set.add(AccessibleState.SHOWING);
            }
            if (this.isFocusOwner()) {
                set.add(AccessibleState.FOCUSED);
            }
            if (this instanceof Accessible) {
                final AccessibleContext accessibleContext = ((Accessible)this).getAccessibleContext();
                if (accessibleContext != null) {
                    final Accessible accessibleParent = accessibleContext.getAccessibleParent();
                    if (accessibleParent != null) {
                        final AccessibleContext accessibleContext2 = accessibleParent.getAccessibleContext();
                        if (accessibleContext2 != null) {
                            final AccessibleSelection accessibleSelection = accessibleContext2.getAccessibleSelection();
                            if (accessibleSelection != null) {
                                set.add(AccessibleState.SELECTABLE);
                                final int accessibleIndexInParent = accessibleContext.getAccessibleIndexInParent();
                                if (accessibleIndexInParent >= 0 && accessibleSelection.isAccessibleChildSelected(accessibleIndexInParent)) {
                                    set.add(AccessibleState.SELECTED);
                                }
                            }
                        }
                    }
                }
            }
            if (isInstanceOf(this, "javax.swing.JComponent") && ((JComponent)this).isOpaque()) {
                set.add(AccessibleState.OPAQUE);
            }
            return set;
        }
    }
    
    static boolean isInstanceOf(final Object o, final String s) {
        if (o == null) {
            return false;
        }
        if (s == null) {
            return false;
        }
        for (Class<?> clazz = o.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            if (clazz.getName().equals(s)) {
                return true;
            }
        }
        return false;
    }
    
    final boolean areBoundsValid() {
        final Container container = this.getContainer();
        return container == null || container.isValid() || container.getLayout() == null;
    }
    
    void applyCompoundShape(Region empty_REGION) {
        this.checkTreeLock();
        if (!this.areBoundsValid()) {
            if (Component.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
                Component.mixingLog.fine("this = " + this + "; areBoundsValid = " + this.areBoundsValid());
            }
            return;
        }
        if (!this.isLightweight()) {
            final ComponentPeer peer = this.getPeer();
            if (peer != null) {
                if (empty_REGION.isEmpty()) {
                    empty_REGION = Region.EMPTY_REGION;
                }
                if (empty_REGION.equals(this.getNormalShape())) {
                    if (this.compoundShape == null) {
                        return;
                    }
                    peer.applyShape(this.compoundShape = null);
                }
                else {
                    if (empty_REGION.equals(this.getAppliedShape())) {
                        return;
                    }
                    this.compoundShape = empty_REGION;
                    final Point locationOnWindow = this.getLocationOnWindow();
                    if (Component.mixingLog.isLoggable(PlatformLogger.Level.FINER)) {
                        Component.mixingLog.fine("this = " + this + "; compAbsolute=" + locationOnWindow + "; shape=" + empty_REGION);
                    }
                    peer.applyShape(empty_REGION.getTranslatedRegion(-locationOnWindow.x, -locationOnWindow.y));
                }
            }
        }
    }
    
    private Region getAppliedShape() {
        this.checkTreeLock();
        return (this.compoundShape == null || this.isLightweight()) ? this.getNormalShape() : this.compoundShape;
    }
    
    Point getLocationOnWindow() {
        this.checkTreeLock();
        final Point location = this.getLocation();
        for (Container container = this.getContainer(); container != null && !(container instanceof Window); container = container.getContainer()) {
            final Point point = location;
            point.x += container.getX();
            final Point point2 = location;
            point2.y += container.getY();
        }
        return location;
    }
    
    final Region getNormalShape() {
        this.checkTreeLock();
        final Point locationOnWindow = this.getLocationOnWindow();
        return Region.getInstanceXYWH(locationOnWindow.x, locationOnWindow.y, this.getWidth(), this.getHeight());
    }
    
    Region getOpaqueShape() {
        this.checkTreeLock();
        if (this.mixingCutoutRegion != null) {
            return this.mixingCutoutRegion;
        }
        return this.getNormalShape();
    }
    
    final int getSiblingIndexAbove() {
        this.checkTreeLock();
        final Container container = this.getContainer();
        if (container == null) {
            return -1;
        }
        final int n = container.getComponentZOrder(this) - 1;
        return (n < 0) ? -1 : n;
    }
    
    final ComponentPeer getHWPeerAboveMe() {
        this.checkTreeLock();
        Container container = this.getContainer();
        int n = this.getSiblingIndexAbove();
        while (container != null) {
            for (int i = n; i > -1; --i) {
                final Component component = container.getComponent(i);
                if (component != null && component.isDisplayable() && !component.isLightweight()) {
                    return component.getPeer();
                }
            }
            if (!container.isLightweight()) {
                break;
            }
            n = container.getSiblingIndexAbove();
            container = container.getContainer();
        }
        return null;
    }
    
    final int getSiblingIndexBelow() {
        this.checkTreeLock();
        final Container container = this.getContainer();
        if (container == null) {
            return -1;
        }
        final int n = container.getComponentZOrder(this) + 1;
        return (n >= container.getComponentCount()) ? -1 : n;
    }
    
    final boolean isNonOpaqueForMixing() {
        return this.mixingCutoutRegion != null && this.mixingCutoutRegion.isEmpty();
    }
    
    private Region calculateCurrentShape() {
        this.checkTreeLock();
        Region region = this.getNormalShape();
        if (Component.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            Component.mixingLog.fine("this = " + this + "; normalShape=" + region);
        }
        if (this.getContainer() != null) {
            Component component = this;
            for (Container container = component.getContainer(); container != null; container = container.getContainer()) {
                for (int i = component.getSiblingIndexAbove(); i != -1; --i) {
                    final Component component2 = container.getComponent(i);
                    if (component2.isLightweight() && component2.isShowing()) {
                        region = region.getDifference(component2.getOpaqueShape());
                    }
                }
                if (!container.isLightweight()) {
                    break;
                }
                region = region.getIntersection(container.getNormalShape());
                component = container;
            }
        }
        if (Component.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            Component.mixingLog.fine("currentShape=" + region);
        }
        return region;
    }
    
    void applyCurrentShape() {
        this.checkTreeLock();
        if (!this.areBoundsValid()) {
            if (Component.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
                Component.mixingLog.fine("this = " + this + "; areBoundsValid = " + this.areBoundsValid());
            }
            return;
        }
        if (Component.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            Component.mixingLog.fine("this = " + this);
        }
        this.applyCompoundShape(this.calculateCurrentShape());
    }
    
    final void subtractAndApplyShape(final Region region) {
        this.checkTreeLock();
        if (Component.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            Component.mixingLog.fine("this = " + this + "; s=" + region);
        }
        this.applyCompoundShape(this.getAppliedShape().getDifference(region));
    }
    
    private final void applyCurrentShapeBelowMe() {
        this.checkTreeLock();
        Container container = this.getContainer();
        if (container != null && container.isShowing()) {
            container.recursiveApplyCurrentShape(this.getSiblingIndexBelow());
            for (Container container2 = container.getContainer(); !container.isOpaque() && container2 != null; container = container2, container2 = container.getContainer()) {
                container2.recursiveApplyCurrentShape(container.getSiblingIndexBelow());
            }
        }
    }
    
    final void subtractAndApplyShapeBelowMe() {
        this.checkTreeLock();
        Container container = this.getContainer();
        if (container != null && this.isShowing()) {
            final Region opaqueShape = this.getOpaqueShape();
            container.recursiveSubtractAndApplyShape(opaqueShape, this.getSiblingIndexBelow());
            for (Container container2 = container.getContainer(); !container.isOpaque() && container2 != null; container = container2, container2 = container.getContainer()) {
                container2.recursiveSubtractAndApplyShape(opaqueShape, container.getSiblingIndexBelow());
            }
        }
    }
    
    void mixOnShowing() {
        synchronized (this.getTreeLock()) {
            if (Component.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
                Component.mixingLog.fine("this = " + this);
            }
            if (!this.isMixingNeeded()) {
                return;
            }
            if (this.isLightweight()) {
                this.subtractAndApplyShapeBelowMe();
            }
            else {
                this.applyCurrentShape();
            }
        }
    }
    
    void mixOnHiding(final boolean b) {
        synchronized (this.getTreeLock()) {
            if (Component.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
                Component.mixingLog.fine("this = " + this + "; isLightweight = " + b);
            }
            if (!this.isMixingNeeded()) {
                return;
            }
            if (b) {
                this.applyCurrentShapeBelowMe();
            }
        }
    }
    
    void mixOnReshaping() {
        synchronized (this.getTreeLock()) {
            if (Component.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
                Component.mixingLog.fine("this = " + this);
            }
            if (!this.isMixingNeeded()) {
                return;
            }
            if (this.isLightweight()) {
                this.applyCurrentShapeBelowMe();
            }
            else {
                this.applyCurrentShape();
            }
        }
    }
    
    void mixOnZOrderChanging(final int n, final int n2) {
        synchronized (this.getTreeLock()) {
            final boolean b = n2 < n;
            final Container container = this.getContainer();
            if (Component.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
                Component.mixingLog.fine("this = " + this + "; oldZorder=" + n + "; newZorder=" + n2 + "; parent=" + container);
            }
            if (!this.isMixingNeeded()) {
                return;
            }
            if (this.isLightweight()) {
                if (b) {
                    if (container != null && this.isShowing()) {
                        container.recursiveSubtractAndApplyShape(this.getOpaqueShape(), this.getSiblingIndexBelow(), n);
                    }
                }
                else if (container != null) {
                    container.recursiveApplyCurrentShape(n, n2);
                }
            }
            else if (b) {
                this.applyCurrentShape();
            }
            else if (container != null) {
                Region region = this.getAppliedShape();
                for (int i = n; i < n2; ++i) {
                    final Component component = container.getComponent(i);
                    if (component.isLightweight() && component.isShowing()) {
                        region = region.getDifference(component.getOpaqueShape());
                    }
                }
                this.applyCompoundShape(region);
            }
        }
    }
    
    void mixOnValidating() {
    }
    
    final boolean isMixingNeeded() {
        if (SunToolkit.getSunAwtDisableMixing()) {
            if (Component.mixingLog.isLoggable(PlatformLogger.Level.FINEST)) {
                Component.mixingLog.finest("this = " + this + "; Mixing disabled via sun.awt.disableMixing");
            }
            return false;
        }
        if (!this.areBoundsValid()) {
            if (Component.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
                Component.mixingLog.fine("this = " + this + "; areBoundsValid = " + this.areBoundsValid());
            }
            return false;
        }
        final Window containingWindow = this.getContainingWindow();
        if (containingWindow == null) {
            if (Component.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
                Component.mixingLog.fine("this = " + this + "; containing window is null");
            }
            return false;
        }
        if (!containingWindow.hasHeavyweightDescendants() || !containingWindow.hasLightweightDescendants() || containingWindow.isDisposing()) {
            if (Component.mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
                Component.mixingLog.fine("containing window = " + containingWindow + "; has h/w descendants = " + containingWindow.hasHeavyweightDescendants() + "; has l/w descendants = " + containingWindow.hasLightweightDescendants() + "; disposing = " + containingWindow.isDisposing());
            }
            return false;
        }
        return true;
    }
    
    void updateZOrder() {
        this.peer.setZOrder(this.getHWPeerAboveMe());
    }
    
    static {
        log = PlatformLogger.getLogger("java.awt.Component");
        eventLog = PlatformLogger.getLogger("java.awt.event.Component");
        focusLog = PlatformLogger.getLogger("java.awt.focus.Component");
        mixingLog = PlatformLogger.getLogger("java.awt.mixing.Component");
        focusTraversalKeyPropertyNames = new String[] { "forwardFocusTraversalKeys", "backwardFocusTraversalKeys", "upCycleFocusTraversalKeys", "downCycleFocusTraversalKeys" };
        LOCK = new AWTTreeLock();
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("awt.image.incrementaldraw"));
        Component.isInc = (s == null || s.equals("true"));
        final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("awt.image.redrawrate"));
        Component.incRate = ((s2 != null) ? Integer.parseInt(s2) : 100);
        AWTAccessor.setComponentAccessor(new AWTAccessor.ComponentAccessor() {
            @Override
            public void setBackgroundEraseDisabled(final Component component, final boolean backgroundEraseDisabled) {
                component.backgroundEraseDisabled = backgroundEraseDisabled;
            }
            
            @Override
            public boolean getBackgroundEraseDisabled(final Component component) {
                return component.backgroundEraseDisabled;
            }
            
            @Override
            public Rectangle getBounds(final Component component) {
                return new Rectangle(component.x, component.y, component.width, component.height);
            }
            
            @Override
            public void setMixingCutoutShape(final Component component, final Shape shape) {
                final Region region = (shape == null) ? null : Region.getInstance(shape, null);
                synchronized (component.getTreeLock()) {
                    boolean b = false;
                    boolean b2 = false;
                    if (!component.isNonOpaqueForMixing()) {
                        b2 = true;
                    }
                    component.mixingCutoutRegion = region;
                    if (!component.isNonOpaqueForMixing()) {
                        b = true;
                    }
                    if (component.isMixingNeeded()) {
                        if (b2) {
                            component.mixOnHiding(component.isLightweight());
                        }
                        if (b) {
                            component.mixOnShowing();
                        }
                    }
                }
            }
            
            @Override
            public void setGraphicsConfiguration(final Component component, final GraphicsConfiguration graphicsConfiguration) {
                component.setGraphicsConfiguration(graphicsConfiguration);
            }
            
            @Override
            public boolean requestFocus(final Component component, final CausedFocusEvent.Cause cause) {
                return component.requestFocus(cause);
            }
            
            @Override
            public boolean canBeFocusOwner(final Component component) {
                return component.canBeFocusOwner();
            }
            
            @Override
            public boolean isVisible(final Component component) {
                return component.isVisible_NoClientCode();
            }
            
            @Override
            public void setRequestFocusController(final RequestFocusController requestFocusController) {
                Component.setRequestFocusController(requestFocusController);
            }
            
            @Override
            public AppContext getAppContext(final Component component) {
                return component.appContext;
            }
            
            @Override
            public void setAppContext(final Component component, final AppContext appContext) {
                component.appContext = appContext;
            }
            
            @Override
            public Container getParent(final Component component) {
                return component.getParent_NoClientCode();
            }
            
            @Override
            public void setParent(final Component component, final Container parent) {
                component.parent = parent;
            }
            
            @Override
            public void setSize(final Component component, final int width, final int height) {
                component.width = width;
                component.height = height;
            }
            
            @Override
            public Point getLocation(final Component component) {
                return component.location_NoClientCode();
            }
            
            @Override
            public void setLocation(final Component component, final int x, final int y) {
                component.x = x;
                component.y = y;
            }
            
            @Override
            public boolean isEnabled(final Component component) {
                return component.isEnabledImpl();
            }
            
            @Override
            public boolean isDisplayable(final Component component) {
                return component.peer != null;
            }
            
            @Override
            public Cursor getCursor(final Component component) {
                return component.getCursor_NoClientCode();
            }
            
            @Override
            public ComponentPeer getPeer(final Component component) {
                return component.peer;
            }
            
            @Override
            public void setPeer(final Component component, final ComponentPeer peer) {
                component.peer = peer;
            }
            
            @Override
            public boolean isLightweight(final Component component) {
                return component.peer instanceof LightweightPeer;
            }
            
            @Override
            public boolean getIgnoreRepaint(final Component component) {
                return component.ignoreRepaint;
            }
            
            @Override
            public int getWidth(final Component component) {
                return component.width;
            }
            
            @Override
            public int getHeight(final Component component) {
                return component.height;
            }
            
            @Override
            public int getX(final Component component) {
                return component.x;
            }
            
            @Override
            public int getY(final Component component) {
                return component.y;
            }
            
            @Override
            public Color getForeground(final Component component) {
                return component.foreground;
            }
            
            @Override
            public Color getBackground(final Component component) {
                return component.background;
            }
            
            @Override
            public void setBackground(final Component component, final Color background) {
                component.background = background;
            }
            
            @Override
            public Font getFont(final Component component) {
                return component.getFont_NoClientCode();
            }
            
            @Override
            public void processEvent(final Component component, final AWTEvent awtEvent) {
                component.processEvent(awtEvent);
            }
            
            @Override
            public AccessControlContext getAccessControlContext(final Component component) {
                return component.getAccessControlContext();
            }
            
            @Override
            public void revalidateSynchronously(final Component component) {
                component.revalidateSynchronously();
            }
        });
        coalesceMap = new WeakHashMap<Class<?>, Boolean>();
        coalesceEventsParams = new Class[] { AWTEvent.class, AWTEvent.class };
        Component.requestFocusController = new DummyRequestFocusController();
    }
    
    static class AWTTreeLock
    {
    }
    
    public enum BaselineResizeBehavior
    {
        CONSTANT_ASCENT, 
        CONSTANT_DESCENT, 
        CENTER_OFFSET, 
        OTHER;
    }
    
    private class ProxyCapabilities extends ExtendedBufferCapabilities
    {
        private BufferCapabilities orig;
        
        private ProxyCapabilities(final BufferCapabilities orig) {
            super(orig.getFrontBufferCapabilities(), orig.getBackBufferCapabilities(), (orig.getFlipContents() == FlipContents.BACKGROUND) ? FlipContents.BACKGROUND : FlipContents.COPIED);
            this.orig = orig;
        }
    }
    
    protected class FlipBufferStrategy extends BufferStrategy
    {
        protected int numBuffers;
        protected BufferCapabilities caps;
        protected Image drawBuffer;
        protected VolatileImage drawVBuffer;
        protected boolean validatedContents;
        int width;
        int height;
        
        protected FlipBufferStrategy(final int numBuffers, final BufferCapabilities caps) throws AWTException {
            if (!(Component.this instanceof Window) && !(Component.this instanceof Canvas)) {
                throw new ClassCastException("Component must be a Canvas or Window");
            }
            this.createBuffers(this.numBuffers = numBuffers, this.caps = caps);
        }
        
        protected void createBuffers(final int n, BufferCapabilities derive) throws AWTException {
            if (n < 2) {
                throw new IllegalArgumentException("Number of buffers cannot be less than two");
            }
            if (Component.this.peer == null) {
                throw new IllegalStateException("Component must have a valid peer");
            }
            if (derive == null || !derive.isPageFlipping()) {
                throw new IllegalArgumentException("Page flipping capabilities must be specified");
            }
            this.width = Component.this.getWidth();
            this.height = Component.this.getHeight();
            if (this.drawBuffer != null) {
                this.drawBuffer = null;
                this.drawVBuffer = null;
                this.destroyBuffers();
            }
            if (derive instanceof ExtendedBufferCapabilities) {
                final ExtendedBufferCapabilities extendedBufferCapabilities = (ExtendedBufferCapabilities)derive;
                if (extendedBufferCapabilities.getVSync() == ExtendedBufferCapabilities.VSyncType.VSYNC_ON && !VSyncedBSManager.vsyncAllowed(this)) {
                    derive = extendedBufferCapabilities.derive(ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT);
                }
            }
            Component.this.peer.createBuffers(n, derive);
            this.updateInternalBuffers();
        }
        
        private void updateInternalBuffers() {
            this.drawBuffer = this.getBackBuffer();
            if (this.drawBuffer instanceof VolatileImage) {
                this.drawVBuffer = (VolatileImage)this.drawBuffer;
            }
            else {
                this.drawVBuffer = null;
            }
        }
        
        protected Image getBackBuffer() {
            if (Component.this.peer != null) {
                return Component.this.peer.getBackBuffer();
            }
            throw new IllegalStateException("Component must have a valid peer");
        }
        
        protected void flip(final BufferCapabilities.FlipContents flipContents) {
            if (Component.this.peer != null) {
                final Image backBuffer = this.getBackBuffer();
                if (backBuffer != null) {
                    Component.this.peer.flip(0, 0, backBuffer.getWidth(null), backBuffer.getHeight(null), flipContents);
                }
                return;
            }
            throw new IllegalStateException("Component must have a valid peer");
        }
        
        void flipSubRegion(final int n, final int n2, final int n3, final int n4, final BufferCapabilities.FlipContents flipContents) {
            if (Component.this.peer != null) {
                Component.this.peer.flip(n, n2, n3, n4, flipContents);
                return;
            }
            throw new IllegalStateException("Component must have a valid peer");
        }
        
        protected void destroyBuffers() {
            VSyncedBSManager.releaseVsync(this);
            if (Component.this.peer != null) {
                Component.this.peer.destroyBuffers();
                return;
            }
            throw new IllegalStateException("Component must have a valid peer");
        }
        
        @Override
        public BufferCapabilities getCapabilities() {
            if (this.caps instanceof ProxyCapabilities) {
                return ((ProxyCapabilities)this.caps).orig;
            }
            return this.caps;
        }
        
        @Override
        public Graphics getDrawGraphics() {
            this.revalidate();
            return this.drawBuffer.getGraphics();
        }
        
        protected void revalidate() {
            this.revalidate(true);
        }
        
        void revalidate(final boolean b) {
            this.validatedContents = false;
            Label_0058: {
                if (b) {
                    if (Component.this.getWidth() == this.width) {
                        if (Component.this.getHeight() == this.height) {
                            break Label_0058;
                        }
                    }
                    try {
                        this.createBuffers(this.numBuffers, this.caps);
                    }
                    catch (final AWTException ex) {}
                    this.validatedContents = true;
                }
            }
            this.updateInternalBuffers();
            if (this.drawVBuffer != null) {
                final GraphicsConfiguration graphicsConfiguration_NoClientCode = Component.this.getGraphicsConfiguration_NoClientCode();
                final int validate = this.drawVBuffer.validate(graphicsConfiguration_NoClientCode);
                if (validate == 2) {
                    try {
                        this.createBuffers(this.numBuffers, this.caps);
                    }
                    catch (final AWTException ex2) {}
                    if (this.drawVBuffer != null) {
                        this.drawVBuffer.validate(graphicsConfiguration_NoClientCode);
                    }
                    this.validatedContents = true;
                }
                else if (validate == 1) {
                    this.validatedContents = true;
                }
            }
        }
        
        @Override
        public boolean contentsLost() {
            return this.drawVBuffer != null && this.drawVBuffer.contentsLost();
        }
        
        @Override
        public boolean contentsRestored() {
            return this.validatedContents;
        }
        
        @Override
        public void show() {
            this.flip(this.caps.getFlipContents());
        }
        
        void showSubRegion(final int n, final int n2, final int n3, final int n4) {
            this.flipSubRegion(n, n2, n3, n4, this.caps.getFlipContents());
        }
        
        @Override
        public void dispose() {
            if (Component.this.bufferStrategy == this) {
                Component.this.bufferStrategy = null;
                if (Component.this.peer != null) {
                    this.destroyBuffers();
                }
            }
        }
    }
    
    protected class BltBufferStrategy extends BufferStrategy
    {
        protected BufferCapabilities caps;
        protected VolatileImage[] backBuffers;
        protected boolean validatedContents;
        protected int width;
        protected int height;
        private Insets insets;
        
        protected BltBufferStrategy(final int n, final BufferCapabilities caps) {
            this.caps = caps;
            this.createBackBuffers(n - 1);
        }
        
        @Override
        public void dispose() {
            if (this.backBuffers != null) {
                for (int i = this.backBuffers.length - 1; i >= 0; --i) {
                    if (this.backBuffers[i] != null) {
                        this.backBuffers[i].flush();
                        this.backBuffers[i] = null;
                    }
                }
            }
            if (Component.this.bufferStrategy == this) {
                Component.this.bufferStrategy = null;
            }
        }
        
        protected void createBackBuffers(final int n) {
            if (n == 0) {
                this.backBuffers = null;
            }
            else {
                this.width = Component.this.getWidth();
                this.height = Component.this.getHeight();
                this.insets = Component.this.getInsets_NoClientCode();
                final int n2 = this.width - this.insets.left - this.insets.right;
                final int n3 = this.height - this.insets.top - this.insets.bottom;
                final int max = Math.max(1, n2);
                final int max2 = Math.max(1, n3);
                if (this.backBuffers == null) {
                    this.backBuffers = new VolatileImage[n];
                }
                else {
                    for (int i = 0; i < n; ++i) {
                        if (this.backBuffers[i] != null) {
                            this.backBuffers[i].flush();
                            this.backBuffers[i] = null;
                        }
                    }
                }
                for (int j = 0; j < n; ++j) {
                    this.backBuffers[j] = Component.this.createVolatileImage(max, max2);
                }
            }
        }
        
        @Override
        public BufferCapabilities getCapabilities() {
            return this.caps;
        }
        
        @Override
        public Graphics getDrawGraphics() {
            this.revalidate();
            final Image backBuffer = this.getBackBuffer();
            if (backBuffer == null) {
                return Component.this.getGraphics();
            }
            final SunGraphics2D sunGraphics2D = (SunGraphics2D)backBuffer.getGraphics();
            sunGraphics2D.constrain(-this.insets.left, -this.insets.top, backBuffer.getWidth(null) + this.insets.left, backBuffer.getHeight(null) + this.insets.top);
            return sunGraphics2D;
        }
        
        Image getBackBuffer() {
            if (this.backBuffers != null) {
                return this.backBuffers[this.backBuffers.length - 1];
            }
            return null;
        }
        
        @Override
        public void show() {
            this.showSubRegion(this.insets.left, this.insets.top, this.width - this.insets.right, this.height - this.insets.bottom);
        }
        
        void showSubRegion(int n, int n2, int n3, int n4) {
            if (this.backBuffers == null) {
                return;
            }
            n -= this.insets.left;
            n3 -= this.insets.left;
            n2 -= this.insets.top;
            n4 -= this.insets.top;
            Graphics graphics = Component.this.getGraphics_NoClientCode();
            if (graphics == null) {
                return;
            }
            try {
                graphics.translate(this.insets.left, this.insets.top);
                for (int i = 0; i < this.backBuffers.length; ++i) {
                    graphics.drawImage(this.backBuffers[i], n, n2, n3, n4, n, n2, n3, n4, null);
                    graphics.dispose();
                    graphics = null;
                    graphics = this.backBuffers[i].getGraphics();
                }
            }
            finally {
                if (graphics != null) {
                    graphics.dispose();
                }
            }
        }
        
        protected void revalidate() {
            this.revalidate(true);
        }
        
        void revalidate(final boolean b) {
            this.validatedContents = false;
            if (this.backBuffers == null) {
                return;
            }
            if (b) {
                final Insets access$400 = Component.this.getInsets_NoClientCode();
                if (Component.this.getWidth() != this.width || Component.this.getHeight() != this.height || !access$400.equals(this.insets)) {
                    this.createBackBuffers(this.backBuffers.length);
                    this.validatedContents = true;
                }
            }
            final GraphicsConfiguration graphicsConfiguration_NoClientCode = Component.this.getGraphicsConfiguration_NoClientCode();
            final int validate = this.backBuffers[this.backBuffers.length - 1].validate(graphicsConfiguration_NoClientCode);
            if (validate == 2) {
                if (b) {
                    this.createBackBuffers(this.backBuffers.length);
                    this.backBuffers[this.backBuffers.length - 1].validate(graphicsConfiguration_NoClientCode);
                }
                this.validatedContents = true;
            }
            else if (validate == 1) {
                this.validatedContents = true;
            }
        }
        
        @Override
        public boolean contentsLost() {
            return this.backBuffers != null && this.backBuffers[this.backBuffers.length - 1].contentsLost();
        }
        
        @Override
        public boolean contentsRestored() {
            return this.validatedContents;
        }
    }
    
    private class FlipSubRegionBufferStrategy extends FlipBufferStrategy implements SubRegionShowable
    {
        protected FlipSubRegionBufferStrategy(final int n, final BufferCapabilities bufferCapabilities) throws AWTException {
            super(n, bufferCapabilities);
        }
        
        @Override
        public void show(final int n, final int n2, final int n3, final int n4) {
            this.showSubRegion(n, n2, n3, n4);
        }
        
        @Override
        public boolean showIfNotLost(final int n, final int n2, final int n3, final int n4) {
            if (!this.contentsLost()) {
                this.showSubRegion(n, n2, n3, n4);
                return !this.contentsLost();
            }
            return false;
        }
    }
    
    private class BltSubRegionBufferStrategy extends BltBufferStrategy implements SubRegionShowable
    {
        protected BltSubRegionBufferStrategy(final int n, final BufferCapabilities bufferCapabilities) {
            super(n, bufferCapabilities);
        }
        
        @Override
        public void show(final int n, final int n2, final int n3, final int n4) {
            this.showSubRegion(n, n2, n3, n4);
        }
        
        @Override
        public boolean showIfNotLost(final int n, final int n2, final int n3, final int n4) {
            if (!this.contentsLost()) {
                this.showSubRegion(n, n2, n3, n4);
                return !this.contentsLost();
            }
            return false;
        }
    }
    
    private class SingleBufferStrategy extends BufferStrategy
    {
        private BufferCapabilities caps;
        
        public SingleBufferStrategy(final BufferCapabilities caps) {
            this.caps = caps;
        }
        
        @Override
        public BufferCapabilities getCapabilities() {
            return this.caps;
        }
        
        @Override
        public Graphics getDrawGraphics() {
            return Component.this.getGraphics();
        }
        
        @Override
        public boolean contentsLost() {
            return false;
        }
        
        @Override
        public boolean contentsRestored() {
            return false;
        }
        
        @Override
        public void show() {
        }
    }
    
    private static class DummyRequestFocusController implements RequestFocusController
    {
        @Override
        public boolean acceptRequestFocus(final Component component, final Component component2, final boolean b, final boolean b2, final CausedFocusEvent.Cause cause) {
            return true;
        }
    }
    
    protected abstract class AccessibleAWTComponent extends AccessibleContext implements Serializable, AccessibleComponent
    {
        private static final long serialVersionUID = 642321655757800191L;
        private transient volatile int propertyListenersCount;
        protected ComponentListener accessibleAWTComponentHandler;
        protected FocusListener accessibleAWTFocusHandler;
        
        protected AccessibleAWTComponent() {
            this.propertyListenersCount = 0;
            this.accessibleAWTComponentHandler = null;
            this.accessibleAWTFocusHandler = null;
        }
        
        @Override
        public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
            if (this.accessibleAWTComponentHandler == null) {
                this.accessibleAWTComponentHandler = new AccessibleAWTComponentHandler();
            }
            if (this.accessibleAWTFocusHandler == null) {
                this.accessibleAWTFocusHandler = new AccessibleAWTFocusHandler();
            }
            if (this.propertyListenersCount++ == 0) {
                Component.this.addComponentListener(this.accessibleAWTComponentHandler);
                Component.this.addFocusListener(this.accessibleAWTFocusHandler);
            }
            super.addPropertyChangeListener(propertyChangeListener);
        }
        
        @Override
        public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
            final int propertyListenersCount = this.propertyListenersCount - 1;
            this.propertyListenersCount = propertyListenersCount;
            if (propertyListenersCount == 0) {
                Component.this.removeComponentListener(this.accessibleAWTComponentHandler);
                Component.this.removeFocusListener(this.accessibleAWTFocusHandler);
            }
            super.removePropertyChangeListener(propertyChangeListener);
        }
        
        @Override
        public String getAccessibleName() {
            return this.accessibleName;
        }
        
        @Override
        public String getAccessibleDescription() {
            return this.accessibleDescription;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.AWT_COMPONENT;
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            return Component.this.getAccessibleStateSet();
        }
        
        @Override
        public Accessible getAccessibleParent() {
            if (this.accessibleParent != null) {
                return this.accessibleParent;
            }
            final Container parent = Component.this.getParent();
            if (parent instanceof Accessible) {
                return (Accessible)parent;
            }
            return null;
        }
        
        @Override
        public int getAccessibleIndexInParent() {
            return Component.this.getAccessibleIndexInParent();
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            return 0;
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            return null;
        }
        
        @Override
        public Locale getLocale() {
            return Component.this.getLocale();
        }
        
        @Override
        public AccessibleComponent getAccessibleComponent() {
            return this;
        }
        
        @Override
        public Color getBackground() {
            return Component.this.getBackground();
        }
        
        @Override
        public void setBackground(final Color background) {
            Component.this.setBackground(background);
        }
        
        @Override
        public Color getForeground() {
            return Component.this.getForeground();
        }
        
        @Override
        public void setForeground(final Color foreground) {
            Component.this.setForeground(foreground);
        }
        
        @Override
        public Cursor getCursor() {
            return Component.this.getCursor();
        }
        
        @Override
        public void setCursor(final Cursor cursor) {
            Component.this.setCursor(cursor);
        }
        
        @Override
        public Font getFont() {
            return Component.this.getFont();
        }
        
        @Override
        public void setFont(final Font font) {
            Component.this.setFont(font);
        }
        
        @Override
        public FontMetrics getFontMetrics(final Font font) {
            if (font == null) {
                return null;
            }
            return Component.this.getFontMetrics(font);
        }
        
        @Override
        public boolean isEnabled() {
            return Component.this.isEnabled();
        }
        
        @Override
        public void setEnabled(final boolean enabled) {
            final boolean enabled2 = Component.this.isEnabled();
            Component.this.setEnabled(enabled);
            if (enabled != enabled2 && Component.this.accessibleContext != null) {
                if (enabled) {
                    Component.this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.ENABLED);
                }
                else {
                    Component.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.ENABLED, null);
                }
            }
        }
        
        @Override
        public boolean isVisible() {
            return Component.this.isVisible();
        }
        
        @Override
        public void setVisible(final boolean visible) {
            final boolean visible2 = Component.this.isVisible();
            Component.this.setVisible(visible);
            if (visible != visible2 && Component.this.accessibleContext != null) {
                if (visible) {
                    Component.this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.VISIBLE);
                }
                else {
                    Component.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.VISIBLE, null);
                }
            }
        }
        
        @Override
        public boolean isShowing() {
            return Component.this.isShowing();
        }
        
        @Override
        public boolean contains(final Point point) {
            return Component.this.contains(point);
        }
        
        @Override
        public Point getLocationOnScreen() {
            synchronized (Component.this.getTreeLock()) {
                if (Component.this.isShowing()) {
                    return Component.this.getLocationOnScreen();
                }
                return null;
            }
        }
        
        @Override
        public Point getLocation() {
            return Component.this.getLocation();
        }
        
        @Override
        public void setLocation(final Point location) {
            Component.this.setLocation(location);
        }
        
        @Override
        public Rectangle getBounds() {
            return Component.this.getBounds();
        }
        
        @Override
        public void setBounds(final Rectangle bounds) {
            Component.this.setBounds(bounds);
        }
        
        @Override
        public Dimension getSize() {
            return Component.this.getSize();
        }
        
        @Override
        public void setSize(final Dimension size) {
            Component.this.setSize(size);
        }
        
        @Override
        public Accessible getAccessibleAt(final Point point) {
            return null;
        }
        
        @Override
        public boolean isFocusTraversable() {
            return Component.this.isFocusTraversable();
        }
        
        @Override
        public void requestFocus() {
            Component.this.requestFocus();
        }
        
        @Override
        public void addFocusListener(final FocusListener focusListener) {
            Component.this.addFocusListener(focusListener);
        }
        
        @Override
        public void removeFocusListener(final FocusListener focusListener) {
            Component.this.removeFocusListener(focusListener);
        }
        
        protected class AccessibleAWTComponentHandler implements ComponentListener
        {
            @Override
            public void componentHidden(final ComponentEvent componentEvent) {
                if (Component.this.accessibleContext != null) {
                    Component.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.VISIBLE, null);
                }
            }
            
            @Override
            public void componentShown(final ComponentEvent componentEvent) {
                if (Component.this.accessibleContext != null) {
                    Component.this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.VISIBLE);
                }
            }
            
            @Override
            public void componentMoved(final ComponentEvent componentEvent) {
            }
            
            @Override
            public void componentResized(final ComponentEvent componentEvent) {
            }
        }
        
        protected class AccessibleAWTFocusHandler implements FocusListener
        {
            @Override
            public void focusGained(final FocusEvent focusEvent) {
                if (Component.this.accessibleContext != null) {
                    Component.this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.FOCUSED);
                }
            }
            
            @Override
            public void focusLost(final FocusEvent focusEvent) {
                if (Component.this.accessibleContext != null) {
                    Component.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.FOCUSED, null);
                }
            }
        }
    }
}
