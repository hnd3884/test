package java.awt;

import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;
import java.awt.geom.Point2D;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.JComponent;
import javax.swing.RootPaneContainer;
import java.awt.geom.AffineTransform;
import sun.java2d.pipe.Region;
import java.awt.geom.Path2D;
import java.awt.image.BufferStrategy;
import java.awt.event.MouseWheelEvent;
import javax.accessibility.AccessibleContext;
import java.io.OptionalDataException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.util.ResourceBundle;
import java.beans.PropertyChangeListener;
import sun.awt.CausedFocusEvent;
import java.util.Set;
import java.awt.event.KeyEvent;
import java.util.EventListener;
import sun.awt.AppContext;
import java.util.Arrays;
import java.util.Locale;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.security.util.SecurityConstants;
import java.lang.reflect.InvocationTargetException;
import java.awt.event.WindowEvent;
import java.awt.peer.WindowPeer;
import java.util.Collection;
import java.util.ArrayList;
import sun.awt.SunToolkit;
import sun.java2d.DisposerRecord;
import sun.java2d.Disposer;
import java.util.concurrent.atomic.AtomicBoolean;
import sun.util.logging.PlatformLogger;
import java.awt.im.InputContext;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowStateListener;
import java.awt.event.WindowListener;
import java.lang.ref.WeakReference;
import java.util.Vector;
import sun.awt.util.IdentityArrayList;
import java.util.List;
import javax.accessibility.Accessible;

public class Window extends Container implements Accessible
{
    String warningString;
    transient List<Image> icons;
    private transient Component temporaryLostComponent;
    static boolean systemSyncLWRequests;
    boolean syncLWRequests;
    transient boolean beforeFirstShow;
    private transient boolean disposing;
    transient WindowDisposerRecord disposerRecord;
    static final int OPENED = 1;
    int state;
    private boolean alwaysOnTop;
    private static final IdentityArrayList<Window> allWindows;
    transient Vector<WeakReference<Window>> ownedWindowList;
    private transient WeakReference<Window> weakThis;
    transient boolean showWithParent;
    transient Dialog modalBlocker;
    Dialog.ModalExclusionType modalExclusionType;
    transient WindowListener windowListener;
    transient WindowStateListener windowStateListener;
    transient WindowFocusListener windowFocusListener;
    transient InputContext inputContext;
    private transient Object inputContextLock;
    private FocusManager focusMgr;
    private boolean focusableWindowState;
    private volatile boolean autoRequestFocus;
    transient boolean isInShow;
    private volatile float opacity;
    private Shape shape;
    private static final String base = "win";
    private static int nameCounter;
    private static final long serialVersionUID = 4497834738069338734L;
    private static final PlatformLogger log;
    private static final boolean locationByPlatformProp;
    transient boolean isTrayIconWindow;
    private transient volatile int securityWarningWidth;
    private transient volatile int securityWarningHeight;
    private transient double securityWarningPointX;
    private transient double securityWarningPointY;
    private transient float securityWarningAlignmentX;
    private transient float securityWarningAlignmentY;
    transient Object anchor;
    private static final AtomicBoolean beforeFirstWindowShown;
    private Type type;
    private int windowSerializedDataVersion;
    private volatile boolean locationByPlatform;
    
    private static native void initIDs();
    
    Window(final GraphicsConfiguration graphicsConfiguration) {
        this.syncLWRequests = false;
        this.beforeFirstShow = true;
        this.disposing = false;
        this.disposerRecord = null;
        this.ownedWindowList = new Vector<WeakReference<Window>>();
        this.inputContextLock = new Object();
        this.focusableWindowState = true;
        this.autoRequestFocus = true;
        this.isInShow = false;
        this.opacity = 1.0f;
        this.shape = null;
        this.isTrayIconWindow = false;
        this.securityWarningWidth = 0;
        this.securityWarningHeight = 0;
        this.securityWarningPointX = 2.0;
        this.securityWarningPointY = 0.0;
        this.securityWarningAlignmentX = 1.0f;
        this.securityWarningAlignmentY = 0.0f;
        this.anchor = new Object();
        this.type = Type.NORMAL;
        this.windowSerializedDataVersion = 2;
        this.locationByPlatform = Window.locationByPlatformProp;
        this.init(graphicsConfiguration);
    }
    
    private GraphicsConfiguration initGC(GraphicsConfiguration defaultConfiguration) {
        GraphicsEnvironment.checkHeadless();
        if (defaultConfiguration == null) {
            defaultConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        }
        this.setGraphicsConfiguration(defaultConfiguration);
        return defaultConfiguration;
    }
    
    private void init(GraphicsConfiguration initGC) {
        GraphicsEnvironment.checkHeadless();
        this.syncLWRequests = Window.systemSyncLWRequests;
        this.weakThis = new WeakReference<Window>(this);
        this.addToWindowList();
        this.setWarningString();
        this.cursor = Cursor.getPredefinedCursor(0);
        this.visible = false;
        initGC = this.initGC(initGC);
        if (initGC.getDevice().getType() != 0) {
            throw new IllegalArgumentException("not a screen device");
        }
        this.setLayout(new BorderLayout());
        final Rectangle bounds = initGC.getBounds();
        final Insets screenInsets = this.getToolkit().getScreenInsets(initGC);
        final int n = this.getX() + bounds.x + screenInsets.left;
        final int n2 = this.getY() + bounds.y + screenInsets.top;
        if (n != this.x || n2 != this.y) {
            this.setLocation(n, n2);
            this.setLocationByPlatform(Window.locationByPlatformProp);
        }
        this.modalExclusionType = Dialog.ModalExclusionType.NO_EXCLUDE;
        this.disposerRecord = new WindowDisposerRecord(this.appContext, this);
        Disposer.addRecord(this.anchor, this.disposerRecord);
        SunToolkit.checkAndSetPolicy(this);
    }
    
    Window() throws HeadlessException {
        this.syncLWRequests = false;
        this.beforeFirstShow = true;
        this.disposing = false;
        this.disposerRecord = null;
        this.ownedWindowList = new Vector<WeakReference<Window>>();
        this.inputContextLock = new Object();
        this.focusableWindowState = true;
        this.autoRequestFocus = true;
        this.isInShow = false;
        this.opacity = 1.0f;
        this.shape = null;
        this.isTrayIconWindow = false;
        this.securityWarningWidth = 0;
        this.securityWarningHeight = 0;
        this.securityWarningPointX = 2.0;
        this.securityWarningPointY = 0.0;
        this.securityWarningAlignmentX = 1.0f;
        this.securityWarningAlignmentY = 0.0f;
        this.anchor = new Object();
        this.type = Type.NORMAL;
        this.windowSerializedDataVersion = 2;
        this.locationByPlatform = Window.locationByPlatformProp;
        GraphicsEnvironment.checkHeadless();
        this.init(null);
    }
    
    public Window(final Frame frame) {
        this((frame == null) ? null : frame.getGraphicsConfiguration());
        this.ownedInit(frame);
    }
    
    public Window(final Window window) {
        this((window == null) ? null : window.getGraphicsConfiguration());
        this.ownedInit(window);
    }
    
    public Window(final Window window, final GraphicsConfiguration graphicsConfiguration) {
        this(graphicsConfiguration);
        this.ownedInit(window);
    }
    
    private void ownedInit(final Window parent) {
        this.parent = parent;
        if (parent != null) {
            parent.addOwnedWindow(this.weakThis);
            if (parent.isAlwaysOnTop()) {
                try {
                    this.setAlwaysOnTop(true);
                }
                catch (final SecurityException ex) {}
            }
        }
        this.disposerRecord.updateOwner();
    }
    
    @Override
    String constructComponentName() {
        synchronized (Window.class) {
            return "win" + Window.nameCounter++;
        }
    }
    
    public List<Image> getIconImages() {
        final List<Image> icons = this.icons;
        if (icons == null || icons.size() == 0) {
            return new ArrayList<Image>();
        }
        return new ArrayList<Image>(icons);
    }
    
    public synchronized void setIconImages(final List<? extends Image> list) {
        this.icons = ((list == null) ? new ArrayList<Image>() : new ArrayList<Image>(list));
        final WindowPeer windowPeer = (WindowPeer)this.peer;
        if (windowPeer != null) {
            windowPeer.updateIconImages();
        }
        this.firePropertyChange("iconImage", null, null);
    }
    
    public void setIconImage(final Image image) {
        final ArrayList iconImages = new ArrayList();
        if (image != null) {
            iconImages.add(image);
        }
        this.setIconImages(iconImages);
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            final Container parent = this.parent;
            if (parent != null && parent.getPeer() == null) {
                parent.addNotify();
            }
            if (this.peer == null) {
                this.peer = this.getToolkit().createWindow(this);
            }
            synchronized (Window.allWindows) {
                Window.allWindows.add(this);
            }
            super.addNotify();
        }
    }
    
    @Override
    public void removeNotify() {
        synchronized (this.getTreeLock()) {
            synchronized (Window.allWindows) {
                Window.allWindows.remove(this);
            }
            super.removeNotify();
        }
    }
    
    public void pack() {
        final Container parent = this.parent;
        if (parent != null && parent.getPeer() == null) {
            parent.addNotify();
        }
        if (this.peer == null) {
            this.addNotify();
        }
        final Dimension preferredSize = this.getPreferredSize();
        if (this.peer != null) {
            this.setClientSize(preferredSize.width, preferredSize.height);
        }
        if (this.beforeFirstShow) {
            this.isPacked = true;
        }
        this.validateUnconditionally();
    }
    
    @Override
    public void setMinimumSize(final Dimension minimumSize) {
        synchronized (this.getTreeLock()) {
            super.setMinimumSize(minimumSize);
            final Dimension size = this.getSize();
            if (this.isMinimumSizeSet() && (size.width < minimumSize.width || size.height < minimumSize.height)) {
                this.setSize(Math.max(this.width, minimumSize.width), Math.max(this.height, minimumSize.height));
            }
            if (this.peer != null) {
                ((WindowPeer)this.peer).updateMinimumSize();
            }
        }
    }
    
    @Override
    public void setSize(final Dimension size) {
        super.setSize(size);
    }
    
    @Override
    public void setSize(final int n, final int n2) {
        super.setSize(n, n2);
    }
    
    @Override
    public void setLocation(final int n, final int n2) {
        super.setLocation(n, n2);
    }
    
    @Override
    public void setLocation(final Point location) {
        super.setLocation(location);
    }
    
    @Deprecated
    @Override
    public void reshape(final int n, final int n2, int width, int height) {
        if (this.isMinimumSizeSet()) {
            final Dimension minimumSize = this.getMinimumSize();
            if (width < minimumSize.width) {
                width = minimumSize.width;
            }
            if (height < minimumSize.height) {
                height = minimumSize.height;
            }
        }
        super.reshape(n, n2, width, height);
    }
    
    void setClientSize(final int n, final int n2) {
        synchronized (this.getTreeLock()) {
            this.setBoundsOp(4);
            this.setBounds(this.x, this.y, n, n2);
        }
    }
    
    final void closeSplashScreen() {
        if (this.isTrayIconWindow) {
            return;
        }
        if (Window.beforeFirstWindowShown.getAndSet(false)) {
            SunToolkit.closeSplashScreen();
            SplashScreen.markClosed();
        }
    }
    
    @Override
    public void setVisible(final boolean visible) {
        super.setVisible(visible);
    }
    
    @Deprecated
    @Override
    public void show() {
        if (this.peer == null) {
            this.addNotify();
        }
        this.validateUnconditionally();
        this.isInShow = true;
        if (this.visible) {
            this.toFront();
        }
        else {
            this.beforeFirstShow = false;
            this.closeSplashScreen();
            Dialog.checkShouldBeBlocked(this);
            super.show();
            this.locationByPlatform = false;
            for (int i = 0; i < this.ownedWindowList.size(); ++i) {
                final Window window = this.ownedWindowList.elementAt(i).get();
                if (window != null && window.showWithParent) {
                    window.show();
                    window.showWithParent = false;
                }
            }
            if (!this.isModalBlocked()) {
                this.updateChildrenBlocking();
            }
            else {
                this.modalBlocker.toFront_NoClientCode();
            }
            if (this instanceof Frame || this instanceof Dialog) {
                updateChildFocusableWindowState(this);
            }
        }
        this.isInShow = false;
        if ((this.state & 0x1) == 0x0) {
            this.postWindowEvent(200);
            this.state |= 0x1;
        }
    }
    
    static void updateChildFocusableWindowState(final Window window) {
        if (window.getPeer() != null && window.isShowing()) {
            ((WindowPeer)window.getPeer()).updateFocusableWindowState();
        }
        for (int i = 0; i < window.ownedWindowList.size(); ++i) {
            final Window window2 = window.ownedWindowList.elementAt(i).get();
            if (window2 != null) {
                updateChildFocusableWindowState(window2);
            }
        }
    }
    
    synchronized void postWindowEvent(final int n) {
        if (this.windowListener != null || (this.eventMask & 0x40L) != 0x0L || Toolkit.enabledOnToolkit(64L)) {
            Toolkit.getEventQueue().postEvent(new WindowEvent(this, n));
        }
    }
    
    @Deprecated
    @Override
    public void hide() {
        synchronized (this.ownedWindowList) {
            for (int i = 0; i < this.ownedWindowList.size(); ++i) {
                final Window window = this.ownedWindowList.elementAt(i).get();
                if (window != null && window.visible) {
                    window.hide();
                    window.showWithParent = true;
                }
            }
        }
        if (this.isModalBlocked()) {
            this.modalBlocker.unblockWindow(this);
        }
        super.hide();
        this.locationByPlatform = false;
    }
    
    @Override
    final void clearMostRecentFocusOwnerOnHide() {
    }
    
    public void dispose() {
        this.doDispose();
    }
    
    void disposeImpl() {
        this.dispose();
        if (this.getPeer() != null) {
            this.doDispose();
        }
    }
    
    void doDispose() {
        final boolean displayable = this.isDisplayable();
        class DisposeAction implements Runnable
        {
            @Override
            public void run() {
                Window.this.disposing = true;
                try {
                    final GraphicsDevice device = Window.this.getGraphicsConfiguration().getDevice();
                    if (device.getFullScreenWindow() == Window.this) {
                        device.setFullScreenWindow(null);
                    }
                    final Object[] array;
                    synchronized (Window.this.ownedWindowList) {
                        array = new Object[Window.this.ownedWindowList.size()];
                        Window.this.ownedWindowList.copyInto(array);
                    }
                    for (int i = 0; i < array.length; ++i) {
                        final Window window = (Window)((WeakReference)array[i]).get();
                        if (window != null) {
                            window.disposeImpl();
                        }
                    }
                    Window.this.hide();
                    Window.this.beforeFirstShow = true;
                    Window.this.removeNotify();
                    synchronized (Window.this.inputContextLock) {
                        if (Window.this.inputContext != null) {
                            Window.this.inputContext.dispose();
                            Window.this.inputContext = null;
                        }
                    }
                    Window.this.clearCurrentFocusCycleRootOnHide();
                }
                finally {
                    Window.this.disposing = false;
                }
            }
        }
        final DisposeAction disposeAction = new DisposeAction();
        if (EventQueue.isDispatchThread()) {
            disposeAction.run();
        }
        else {
            try {
                EventQueue.invokeAndWait(this, disposeAction);
            }
            catch (final InterruptedException ex) {
                System.err.println("Disposal was interrupted:");
                ex.printStackTrace();
            }
            catch (final InvocationTargetException ex2) {
                System.err.println("Exception during disposal:");
                ex2.printStackTrace();
            }
        }
        if (displayable) {
            this.postWindowEvent(202);
        }
    }
    
    @Override
    void adjustListeningChildrenOnParent(final long n, final int n2) {
    }
    
    @Override
    void adjustDecendantsOnParent(final int n) {
    }
    
    public void toFront() {
        this.toFront_NoClientCode();
    }
    
    final void toFront_NoClientCode() {
        if (this.visible) {
            final WindowPeer windowPeer = (WindowPeer)this.peer;
            if (windowPeer != null) {
                windowPeer.toFront();
            }
            if (this.isModalBlocked()) {
                this.modalBlocker.toFront_NoClientCode();
            }
        }
    }
    
    public void toBack() {
        this.toBack_NoClientCode();
    }
    
    final void toBack_NoClientCode() {
        if (this.isAlwaysOnTop()) {
            try {
                this.setAlwaysOnTop(false);
            }
            catch (final SecurityException ex) {}
        }
        if (this.visible) {
            final WindowPeer windowPeer = (WindowPeer)this.peer;
            if (windowPeer != null) {
                windowPeer.toBack();
            }
        }
    }
    
    @Override
    public Toolkit getToolkit() {
        return Toolkit.getDefaultToolkit();
    }
    
    public final String getWarningString() {
        return this.warningString;
    }
    
    private void setWarningString() {
        this.warningString = null;
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            try {
                securityManager.checkPermission(SecurityConstants.AWT.TOPLEVEL_WINDOW_PERMISSION);
            }
            catch (final SecurityException ex) {
                this.warningString = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("awt.appletWarning", "Java Applet Window"));
            }
        }
    }
    
    @Override
    public Locale getLocale() {
        if (this.locale == null) {
            return Locale.getDefault();
        }
        return this.locale;
    }
    
    @Override
    public InputContext getInputContext() {
        synchronized (this.inputContextLock) {
            if (this.inputContext == null) {
                this.inputContext = InputContext.getInstance();
            }
        }
        return this.inputContext;
    }
    
    @Override
    public void setCursor(Cursor predefinedCursor) {
        if (predefinedCursor == null) {
            predefinedCursor = Cursor.getPredefinedCursor(0);
        }
        super.setCursor(predefinedCursor);
    }
    
    public Window getOwner() {
        return this.getOwner_NoClientCode();
    }
    
    final Window getOwner_NoClientCode() {
        return (Window)this.parent;
    }
    
    public Window[] getOwnedWindows() {
        return this.getOwnedWindows_NoClientCode();
    }
    
    final Window[] getOwnedWindows_NoClientCode() {
        Window[] array2;
        synchronized (this.ownedWindowList) {
            final int size = this.ownedWindowList.size();
            int n = 0;
            final Window[] array = new Window[size];
            for (int i = 0; i < size; ++i) {
                array[n] = (Window)this.ownedWindowList.elementAt(i).get();
                if (array[n] != null) {
                    ++n;
                }
            }
            if (size != n) {
                array2 = Arrays.copyOf(array, n);
            }
            else {
                array2 = array;
            }
        }
        return array2;
    }
    
    boolean isModalBlocked() {
        return this.modalBlocker != null;
    }
    
    void setModalBlocked(final Dialog dialog, final boolean b, final boolean b2) {
        this.modalBlocker = (b ? dialog : null);
        if (b2) {
            final WindowPeer windowPeer = (WindowPeer)this.peer;
            if (windowPeer != null) {
                windowPeer.setModalBlocked(dialog, b);
            }
        }
    }
    
    Dialog getModalBlocker() {
        return this.modalBlocker;
    }
    
    static IdentityArrayList<Window> getAllWindows() {
        synchronized (Window.allWindows) {
            final IdentityArrayList list = new IdentityArrayList();
            list.addAll(Window.allWindows);
            return list;
        }
    }
    
    static IdentityArrayList<Window> getAllUnblockedWindows() {
        synchronized (Window.allWindows) {
            final IdentityArrayList list = new IdentityArrayList();
            for (int i = 0; i < Window.allWindows.size(); ++i) {
                final Window window = Window.allWindows.get(i);
                if (!window.isModalBlocked()) {
                    list.add(window);
                }
            }
            return list;
        }
    }
    
    private static Window[] getWindows(final AppContext appContext) {
        synchronized (Window.class) {
            final Vector vector = (Vector)appContext.get(Window.class);
            Window[] array2;
            if (vector != null) {
                final int size = vector.size();
                int n = 0;
                final Window[] array = new Window[size];
                for (int i = 0; i < size; ++i) {
                    final Window window = vector.get(i).get();
                    if (window != null) {
                        array[n++] = window;
                    }
                }
                if (size != n) {
                    array2 = Arrays.copyOf(array, n);
                }
                else {
                    array2 = array;
                }
            }
            else {
                array2 = new Window[0];
            }
            return array2;
        }
    }
    
    public static Window[] getWindows() {
        return getWindows(AppContext.getAppContext());
    }
    
    public static Window[] getOwnerlessWindows() {
        final Window[] windows = getWindows();
        int n = 0;
        final Window[] array = windows;
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i].getOwner() == null) {
                ++n;
            }
        }
        final Window[] array2 = new Window[n];
        int n2 = 0;
        for (final Window window : windows) {
            if (window.getOwner() == null) {
                array2[n2++] = window;
            }
        }
        return array2;
    }
    
    Window getDocumentRoot() {
        synchronized (this.getTreeLock()) {
            Window owner;
            for (owner = this; owner.getOwner() != null; owner = owner.getOwner()) {}
            return owner;
        }
    }
    
    public void setModalExclusionType(Dialog.ModalExclusionType modalExclusionType) {
        if (modalExclusionType == null) {
            modalExclusionType = Dialog.ModalExclusionType.NO_EXCLUDE;
        }
        if (!Toolkit.getDefaultToolkit().isModalExclusionTypeSupported(modalExclusionType)) {
            modalExclusionType = Dialog.ModalExclusionType.NO_EXCLUDE;
        }
        if (this.modalExclusionType == modalExclusionType) {
            return;
        }
        if (modalExclusionType == Dialog.ModalExclusionType.TOOLKIT_EXCLUDE) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkPermission(SecurityConstants.AWT.TOOLKIT_MODALITY_PERMISSION);
            }
        }
        this.modalExclusionType = modalExclusionType;
    }
    
    public Dialog.ModalExclusionType getModalExclusionType() {
        return this.modalExclusionType;
    }
    
    boolean isModalExcluded(final Dialog.ModalExclusionType modalExclusionType) {
        if (this.modalExclusionType != null && this.modalExclusionType.compareTo(modalExclusionType) >= 0) {
            return true;
        }
        final Window owner_NoClientCode = this.getOwner_NoClientCode();
        return owner_NoClientCode != null && owner_NoClientCode.isModalExcluded(modalExclusionType);
    }
    
    void updateChildrenBlocking() {
        final Vector vector = new Vector();
        final Window[] ownedWindows = this.getOwnedWindows();
        for (int i = 0; i < ownedWindows.length; ++i) {
            vector.add(ownedWindows[i]);
        }
        for (int j = 0; j < vector.size(); ++j) {
            final Window window = vector.get(j);
            if (window.isVisible()) {
                if (window.isModalBlocked()) {
                    window.getModalBlocker().unblockWindow(window);
                }
                Dialog.checkShouldBeBlocked(window);
                final Window[] ownedWindows2 = window.getOwnedWindows();
                for (int k = 0; k < ownedWindows2.length; ++k) {
                    vector.add(ownedWindows2[k]);
                }
            }
        }
    }
    
    public synchronized void addWindowListener(final WindowListener windowListener) {
        if (windowListener == null) {
            return;
        }
        this.newEventsOnly = true;
        this.windowListener = AWTEventMulticaster.add(this.windowListener, windowListener);
    }
    
    public synchronized void addWindowStateListener(final WindowStateListener windowStateListener) {
        if (windowStateListener == null) {
            return;
        }
        this.windowStateListener = AWTEventMulticaster.add(this.windowStateListener, windowStateListener);
        this.newEventsOnly = true;
    }
    
    public synchronized void addWindowFocusListener(final WindowFocusListener windowFocusListener) {
        if (windowFocusListener == null) {
            return;
        }
        this.windowFocusListener = AWTEventMulticaster.add(this.windowFocusListener, windowFocusListener);
        this.newEventsOnly = true;
    }
    
    public synchronized void removeWindowListener(final WindowListener windowListener) {
        if (windowListener == null) {
            return;
        }
        this.windowListener = AWTEventMulticaster.remove(this.windowListener, windowListener);
    }
    
    public synchronized void removeWindowStateListener(final WindowStateListener windowStateListener) {
        if (windowStateListener == null) {
            return;
        }
        this.windowStateListener = AWTEventMulticaster.remove(this.windowStateListener, windowStateListener);
    }
    
    public synchronized void removeWindowFocusListener(final WindowFocusListener windowFocusListener) {
        if (windowFocusListener == null) {
            return;
        }
        this.windowFocusListener = AWTEventMulticaster.remove(this.windowFocusListener, windowFocusListener);
    }
    
    public synchronized WindowListener[] getWindowListeners() {
        return this.getListeners(WindowListener.class);
    }
    
    public synchronized WindowFocusListener[] getWindowFocusListeners() {
        return this.getListeners(WindowFocusListener.class);
    }
    
    public synchronized WindowStateListener[] getWindowStateListeners() {
        return this.getListeners(WindowStateListener.class);
    }
    
    @Override
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        Object o;
        if (clazz == WindowFocusListener.class) {
            o = this.windowFocusListener;
        }
        else if (clazz == WindowStateListener.class) {
            o = this.windowStateListener;
        }
        else {
            if (clazz != WindowListener.class) {
                return super.getListeners(clazz);
            }
            o = this.windowListener;
        }
        return AWTEventMulticaster.getListeners((EventListener)o, clazz);
    }
    
    @Override
    boolean eventEnabled(final AWTEvent awtEvent) {
        switch (awtEvent.id) {
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206: {
                return (this.eventMask & 0x40L) != 0x0L || this.windowListener != null;
            }
            case 207:
            case 208: {
                return (this.eventMask & 0x80000L) != 0x0L || this.windowFocusListener != null;
            }
            case 209: {
                return (this.eventMask & 0x40000L) != 0x0L || this.windowStateListener != null;
            }
            default: {
                return super.eventEnabled(awtEvent);
            }
        }
    }
    
    @Override
    protected void processEvent(final AWTEvent awtEvent) {
        if (awtEvent instanceof WindowEvent) {
            switch (awtEvent.getID()) {
                case 200:
                case 201:
                case 202:
                case 203:
                case 204:
                case 205:
                case 206: {
                    this.processWindowEvent((WindowEvent)awtEvent);
                    break;
                }
                case 207:
                case 208: {
                    this.processWindowFocusEvent((WindowEvent)awtEvent);
                    break;
                }
                case 209: {
                    this.processWindowStateEvent((WindowEvent)awtEvent);
                    break;
                }
            }
            return;
        }
        super.processEvent(awtEvent);
    }
    
    protected void processWindowEvent(final WindowEvent windowEvent) {
        final WindowListener windowListener = this.windowListener;
        if (windowListener != null) {
            switch (windowEvent.getID()) {
                case 200: {
                    windowListener.windowOpened(windowEvent);
                    break;
                }
                case 201: {
                    windowListener.windowClosing(windowEvent);
                    break;
                }
                case 202: {
                    windowListener.windowClosed(windowEvent);
                    break;
                }
                case 203: {
                    windowListener.windowIconified(windowEvent);
                    break;
                }
                case 204: {
                    windowListener.windowDeiconified(windowEvent);
                    break;
                }
                case 205: {
                    windowListener.windowActivated(windowEvent);
                    break;
                }
                case 206: {
                    windowListener.windowDeactivated(windowEvent);
                    break;
                }
            }
        }
    }
    
    protected void processWindowFocusEvent(final WindowEvent windowEvent) {
        final WindowFocusListener windowFocusListener = this.windowFocusListener;
        if (windowFocusListener != null) {
            switch (windowEvent.getID()) {
                case 207: {
                    windowFocusListener.windowGainedFocus(windowEvent);
                    break;
                }
                case 208: {
                    windowFocusListener.windowLostFocus(windowEvent);
                    break;
                }
            }
        }
    }
    
    protected void processWindowStateEvent(final WindowEvent windowEvent) {
        final WindowStateListener windowStateListener = this.windowStateListener;
        if (windowStateListener != null) {
            switch (windowEvent.getID()) {
                case 209: {
                    windowStateListener.windowStateChanged(windowEvent);
                    break;
                }
            }
        }
    }
    
    @Override
    void preProcessKeyEvent(final KeyEvent keyEvent) {
        if (keyEvent.isActionKey() && keyEvent.getKeyCode() == 112 && keyEvent.isControlDown() && keyEvent.isShiftDown() && keyEvent.getID() == 401) {
            this.list(System.out, 0);
        }
    }
    
    @Override
    void postProcessKeyEvent(final KeyEvent keyEvent) {
    }
    
    public final void setAlwaysOnTop(final boolean b) throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.AWT.SET_WINDOW_ALWAYS_ON_TOP_PERMISSION);
        }
        final boolean alwaysOnTop;
        synchronized (this) {
            alwaysOnTop = this.alwaysOnTop;
            this.alwaysOnTop = b;
        }
        if (alwaysOnTop != b) {
            if (this.isAlwaysOnTopSupported()) {
                final WindowPeer windowPeer = (WindowPeer)this.peer;
                synchronized (this.getTreeLock()) {
                    if (windowPeer != null) {
                        windowPeer.updateAlwaysOnTopState();
                    }
                }
            }
            this.firePropertyChange("alwaysOnTop", alwaysOnTop, b);
        }
        this.setOwnedWindowsAlwaysOnTop(b);
    }
    
    private void setOwnedWindowsAlwaysOnTop(final boolean alwaysOnTop) {
        final WeakReference[] array;
        synchronized (this.ownedWindowList) {
            array = new WeakReference[this.ownedWindowList.size()];
            this.ownedWindowList.copyInto(array);
        }
        final WeakReference[] array2 = array;
        for (int length = array2.length, i = 0; i < length; ++i) {
            final Window window = (Window)array2[i].get();
            if (window != null) {
                try {
                    window.setAlwaysOnTop(alwaysOnTop);
                }
                catch (final SecurityException ex) {}
            }
        }
    }
    
    public boolean isAlwaysOnTopSupported() {
        return Toolkit.getDefaultToolkit().isAlwaysOnTopSupported();
    }
    
    public final boolean isAlwaysOnTop() {
        return this.alwaysOnTop;
    }
    
    public Component getFocusOwner() {
        return this.isFocused() ? KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() : null;
    }
    
    public Component getMostRecentFocusOwner() {
        if (this.isFocused()) {
            return this.getFocusOwner();
        }
        final Component mostRecentFocusOwner = KeyboardFocusManager.getMostRecentFocusOwner(this);
        if (mostRecentFocusOwner != null) {
            return mostRecentFocusOwner;
        }
        return this.isFocusableWindow() ? this.getFocusTraversalPolicy().getInitialComponent(this) : null;
    }
    
    public boolean isActive() {
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow() == this;
    }
    
    public boolean isFocused() {
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getGlobalFocusedWindow() == this;
    }
    
    @Override
    public Set<AWTKeyStroke> getFocusTraversalKeys(final int n) {
        if (n < 0 || n >= 4) {
            throw new IllegalArgumentException("invalid focus traversal key identifier");
        }
        final Set<AWTKeyStroke> set = (this.focusTraversalKeys != null) ? this.focusTraversalKeys[n] : null;
        if (set != null) {
            return set;
        }
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalKeys(n);
    }
    
    @Override
    public final void setFocusCycleRoot(final boolean b) {
    }
    
    @Override
    public final boolean isFocusCycleRoot() {
        return true;
    }
    
    @Override
    public final Container getFocusCycleRootAncestor() {
        return null;
    }
    
    public final boolean isFocusableWindow() {
        if (!this.getFocusableWindowState()) {
            return false;
        }
        if (this instanceof Frame || this instanceof Dialog) {
            return true;
        }
        if (this.getFocusTraversalPolicy().getDefaultComponent(this) == null) {
            return false;
        }
        for (Window window = this.getOwner(); window != null; window = window.getOwner()) {
            if (window instanceof Frame || window instanceof Dialog) {
                return window.isShowing();
            }
        }
        return false;
    }
    
    public boolean getFocusableWindowState() {
        return this.focusableWindowState;
    }
    
    public void setFocusableWindowState(final boolean focusableWindowState) {
        final boolean focusableWindowState2;
        synchronized (this) {
            focusableWindowState2 = this.focusableWindowState;
            this.focusableWindowState = focusableWindowState;
        }
        final WindowPeer windowPeer = (WindowPeer)this.peer;
        if (windowPeer != null) {
            windowPeer.updateFocusableWindowState();
        }
        this.firePropertyChange("focusableWindowState", focusableWindowState2, focusableWindowState);
        if (focusableWindowState2 && !focusableWindowState && this.isFocused()) {
            for (Window window = this.getOwner(); window != null; window = window.getOwner()) {
                final Component mostRecentFocusOwner = KeyboardFocusManager.getMostRecentFocusOwner(window);
                if (mostRecentFocusOwner != null && mostRecentFocusOwner.requestFocus(false, CausedFocusEvent.Cause.ACTIVATION)) {
                    return;
                }
            }
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwnerPriv();
        }
    }
    
    public void setAutoRequestFocus(final boolean autoRequestFocus) {
        this.autoRequestFocus = autoRequestFocus;
    }
    
    public boolean isAutoRequestFocus() {
        return this.autoRequestFocus;
    }
    
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        super.addPropertyChangeListener(propertyChangeListener);
    }
    
    @Override
    public void addPropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        super.addPropertyChangeListener(s, propertyChangeListener);
    }
    
    @Override
    public boolean isValidateRoot() {
        return true;
    }
    
    @Override
    void dispatchEventImpl(final AWTEvent awtEvent) {
        if (awtEvent.getID() == 101) {
            this.invalidate();
            this.validate();
        }
        super.dispatchEventImpl(awtEvent);
    }
    
    @Deprecated
    @Override
    public boolean postEvent(final Event event) {
        if (this.handleEvent(event)) {
            event.consume();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isShowing() {
        return this.visible;
    }
    
    boolean isDisposing() {
        return this.disposing;
    }
    
    @Deprecated
    public void applyResourceBundle(final ResourceBundle resourceBundle) {
        this.applyComponentOrientation(ComponentOrientation.getOrientation(resourceBundle));
    }
    
    @Deprecated
    public void applyResourceBundle(final String s) {
        this.applyResourceBundle(ResourceBundle.getBundle(s, Locale.getDefault(), ClassLoader.getSystemClassLoader()));
    }
    
    void addOwnedWindow(final WeakReference<Window> weakReference) {
        if (weakReference != null) {
            synchronized (this.ownedWindowList) {
                if (!this.ownedWindowList.contains(weakReference)) {
                    this.ownedWindowList.addElement(weakReference);
                }
            }
        }
    }
    
    void removeOwnedWindow(final WeakReference<Window> weakReference) {
        if (weakReference != null) {
            this.ownedWindowList.removeElement(weakReference);
        }
    }
    
    void connectOwnedWindow(final Window window) {
        ((Window)(window.parent = this)).addOwnedWindow(window.weakThis);
        window.disposerRecord.updateOwner();
    }
    
    private void addToWindowList() {
        synchronized (Window.class) {
            Vector vector = (Vector)this.appContext.get(Window.class);
            if (vector == null) {
                vector = new Vector();
                this.appContext.put(Window.class, vector);
            }
            vector.add(this.weakThis);
        }
    }
    
    private static void removeFromWindowList(final AppContext appContext, final WeakReference<Window> weakReference) {
        synchronized (Window.class) {
            final Vector vector = (Vector)appContext.get(Window.class);
            if (vector != null) {
                vector.remove(weakReference);
            }
        }
    }
    
    private void removeFromWindowList() {
        removeFromWindowList(this.appContext, this.weakThis);
    }
    
    public void setType(final Type type) {
        if (type == null) {
            throw new IllegalArgumentException("type should not be null.");
        }
        synchronized (this.getTreeLock()) {
            if (this.isDisplayable()) {
                throw new IllegalComponentStateException("The window is displayable.");
            }
            synchronized (this.getObjectLock()) {
                this.type = type;
            }
        }
    }
    
    public Type getType() {
        synchronized (this.getObjectLock()) {
            return this.type;
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        synchronized (this) {
            this.focusMgr = new FocusManager();
            this.focusMgr.focusRoot = this;
            this.focusMgr.focusOwner = this.getMostRecentFocusOwner();
            objectOutputStream.defaultWriteObject();
            this.focusMgr = null;
            AWTEventMulticaster.save(objectOutputStream, "windowL", this.windowListener);
            AWTEventMulticaster.save(objectOutputStream, "windowFocusL", this.windowFocusListener);
            AWTEventMulticaster.save(objectOutputStream, "windowStateL", this.windowStateListener);
        }
        objectOutputStream.writeObject(null);
        synchronized (this.ownedWindowList) {
            for (int i = 0; i < this.ownedWindowList.size(); ++i) {
                final Window window = this.ownedWindowList.elementAt(i).get();
                if (window != null) {
                    objectOutputStream.writeObject("ownedL");
                    objectOutputStream.writeObject(window);
                }
            }
        }
        objectOutputStream.writeObject(null);
        if (this.icons != null) {
            for (final Image image : this.icons) {
                if (image instanceof Serializable) {
                    objectOutputStream.writeObject(image);
                }
            }
        }
        objectOutputStream.writeObject(null);
    }
    
    private void initDeserializedWindow() {
        this.setWarningString();
        this.inputContextLock = new Object();
        this.visible = false;
        this.weakThis = new WeakReference<Window>(this);
        this.anchor = new Object();
        this.disposerRecord = new WindowDisposerRecord(this.appContext, this);
        Disposer.addRecord(this.anchor, this.disposerRecord);
        this.addToWindowList();
        this.initGC(null);
        this.ownedWindowList = new Vector<WeakReference<Window>>();
    }
    
    private void deserializeResources(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        if (this.windowSerializedDataVersion < 2) {
            if (this.focusMgr != null && this.focusMgr.focusOwner != null) {
                KeyboardFocusManager.setMostRecentFocusOwner(this, this.focusMgr.focusOwner);
            }
            this.focusableWindowState = true;
        }
        Object object;
        while (null != (object = objectInputStream.readObject())) {
            final String intern = ((String)object).intern();
            if ("windowL" == intern) {
                this.addWindowListener((WindowListener)objectInputStream.readObject());
            }
            else if ("windowFocusL" == intern) {
                this.addWindowFocusListener((WindowFocusListener)objectInputStream.readObject());
            }
            else if ("windowStateL" == intern) {
                this.addWindowStateListener((WindowStateListener)objectInputStream.readObject());
            }
            else {
                objectInputStream.readObject();
            }
        }
        try {
            Object object2;
            while (null != (object2 = objectInputStream.readObject())) {
                if ("ownedL" == ((String)object2).intern()) {
                    this.connectOwnedWindow((Window)objectInputStream.readObject());
                }
                else {
                    objectInputStream.readObject();
                }
            }
            Object o = objectInputStream.readObject();
            this.icons = new ArrayList<Image>();
            while (o != null) {
                if (o instanceof Image) {
                    this.icons.add((Image)o);
                }
                o = objectInputStream.readObject();
            }
        }
        catch (final OptionalDataException ex) {}
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        GraphicsEnvironment.checkHeadless();
        this.initDeserializedWindow();
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        this.syncLWRequests = fields.get("syncLWRequests", Window.systemSyncLWRequests);
        this.state = fields.get("state", 0);
        this.focusableWindowState = fields.get("focusableWindowState", true);
        this.windowSerializedDataVersion = fields.get("windowSerializedDataVersion", 1);
        this.locationByPlatform = fields.get("locationByPlatform", Window.locationByPlatformProp);
        this.focusMgr = (FocusManager)fields.get("focusMgr", null);
        this.setModalExclusionType((Dialog.ModalExclusionType)fields.get("modalExclusionType", Dialog.ModalExclusionType.NO_EXCLUDE));
        final boolean value = fields.get("alwaysOnTop", false);
        if (value) {
            this.setAlwaysOnTop(value);
        }
        this.shape = (Shape)fields.get("shape", null);
        this.opacity = fields.get("opacity", 1.0f);
        this.securityWarningWidth = 0;
        this.securityWarningHeight = 0;
        this.securityWarningPointX = 2.0;
        this.securityWarningPointY = 0.0;
        this.securityWarningAlignmentX = 1.0f;
        this.securityWarningAlignmentY = 0.0f;
        this.deserializeResources(objectInputStream);
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleAWTWindow();
        }
        return this.accessibleContext;
    }
    
    @Override
    void setGraphicsConfiguration(GraphicsConfiguration defaultConfiguration) {
        if (defaultConfiguration == null) {
            defaultConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        }
        synchronized (this.getTreeLock()) {
            super.setGraphicsConfiguration(defaultConfiguration);
            if (Window.log.isLoggable(PlatformLogger.Level.FINER)) {
                Window.log.finer("+ Window.setGraphicsConfiguration(): new GC is \n+ " + this.getGraphicsConfiguration_NoClientCode() + "\n+ this is " + this);
            }
        }
    }
    
    public void setLocationRelativeTo(final Component component) {
        this.getGraphicsConfiguration_NoClientCode().getBounds();
        final Dimension size = this.getSize();
        final Window containingWindow = SunToolkit.getContainingWindow(component);
        Rectangle rectangle;
        int x;
        int y;
        if (component == null || containingWindow == null) {
            final GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            rectangle = localGraphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
            final Point centerPoint = localGraphicsEnvironment.getCenterPoint();
            x = centerPoint.x - size.width / 2;
            y = centerPoint.y - size.height / 2;
        }
        else if (!component.isShowing()) {
            rectangle = containingWindow.getGraphicsConfiguration().getBounds();
            x = rectangle.x + (rectangle.width - size.width) / 2;
            y = rectangle.y + (rectangle.height - size.height) / 2;
        }
        else {
            rectangle = containingWindow.getGraphicsConfiguration().getBounds();
            final Dimension size2 = component.getSize();
            final Point locationOnScreen = component.getLocationOnScreen();
            x = locationOnScreen.x + (size2.width - size.width) / 2;
            y = locationOnScreen.y + (size2.height - size.height) / 2;
            if (y + size.height > rectangle.y + rectangle.height) {
                y = rectangle.y + rectangle.height - size.height;
                if (locationOnScreen.x - rectangle.x + size2.width / 2 < rectangle.width / 2) {
                    x = locationOnScreen.x + size2.width;
                }
                else {
                    x = locationOnScreen.x - size.width;
                }
            }
        }
        if (y + size.height > rectangle.y + rectangle.height) {
            y = rectangle.y + rectangle.height - size.height;
        }
        if (y < rectangle.y) {
            y = rectangle.y;
        }
        if (x + size.width > rectangle.x + rectangle.width) {
            x = rectangle.x + rectangle.width - size.width;
        }
        if (x < rectangle.x) {
            x = rectangle.x;
        }
        this.setLocation(x, y);
    }
    
    void deliverMouseWheelToAncestor(final MouseWheelEvent mouseWheelEvent) {
    }
    
    @Override
    boolean dispatchMouseWheelToAncestor(final MouseWheelEvent mouseWheelEvent) {
        return false;
    }
    
    public void createBufferStrategy(final int n) {
        super.createBufferStrategy(n);
    }
    
    public void createBufferStrategy(final int n, final BufferCapabilities bufferCapabilities) throws AWTException {
        super.createBufferStrategy(n, bufferCapabilities);
    }
    
    public BufferStrategy getBufferStrategy() {
        return super.getBufferStrategy();
    }
    
    Component getTemporaryLostComponent() {
        return this.temporaryLostComponent;
    }
    
    Component setTemporaryLostComponent(final Component temporaryLostComponent) {
        final Component temporaryLostComponent2 = this.temporaryLostComponent;
        if (temporaryLostComponent == null || temporaryLostComponent.canBeFocusOwner()) {
            this.temporaryLostComponent = temporaryLostComponent;
        }
        else {
            this.temporaryLostComponent = null;
        }
        return temporaryLostComponent2;
    }
    
    @Override
    boolean canContainFocusOwner(final Component component) {
        return super.canContainFocusOwner(component) && this.isFocusableWindow();
    }
    
    public void setLocationByPlatform(final boolean locationByPlatform) {
        synchronized (this.getTreeLock()) {
            if (locationByPlatform && this.isShowing()) {
                throw new IllegalComponentStateException("The window is showing on screen.");
            }
            this.locationByPlatform = locationByPlatform;
        }
    }
    
    public boolean isLocationByPlatform() {
        return this.locationByPlatform;
    }
    
    @Override
    public void setBounds(final int n, final int n2, final int n3, final int n4) {
        synchronized (this.getTreeLock()) {
            if (this.getBoundsOp() == 1 || this.getBoundsOp() == 3) {
                this.locationByPlatform = false;
            }
            super.setBounds(n, n2, n3, n4);
        }
    }
    
    @Override
    public void setBounds(final Rectangle rectangle) {
        this.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    @Override
    boolean isRecursivelyVisible() {
        return this.visible;
    }
    
    public float getOpacity() {
        return this.opacity;
    }
    
    public void setOpacity(final float n) {
        synchronized (this.getTreeLock()) {
            if (n < 0.0f || n > 1.0f) {
                throw new IllegalArgumentException("The value of opacity should be in the range [0.0f .. 1.0f].");
            }
            if (n < 1.0f) {
                final GraphicsConfiguration graphicsConfiguration = this.getGraphicsConfiguration();
                final GraphicsDevice device = graphicsConfiguration.getDevice();
                if (graphicsConfiguration.getDevice().getFullScreenWindow() == this) {
                    throw new IllegalComponentStateException("Setting opacity for full-screen window is not supported.");
                }
                if (!device.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT)) {
                    throw new UnsupportedOperationException("TRANSLUCENT translucency is not supported.");
                }
            }
            this.opacity = n;
            final WindowPeer windowPeer = (WindowPeer)this.getPeer();
            if (windowPeer != null) {
                windowPeer.setOpacity(n);
            }
        }
    }
    
    public Shape getShape() {
        synchronized (this.getTreeLock()) {
            return (this.shape == null) ? null : new Path2D.Float(this.shape);
        }
    }
    
    public void setShape(final Shape shape) {
        synchronized (this.getTreeLock()) {
            if (shape != null) {
                final GraphicsConfiguration graphicsConfiguration = this.getGraphicsConfiguration();
                final GraphicsDevice device = graphicsConfiguration.getDevice();
                if (graphicsConfiguration.getDevice().getFullScreenWindow() == this) {
                    throw new IllegalComponentStateException("Setting shape for full-screen window is not supported.");
                }
                if (!device.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT)) {
                    throw new UnsupportedOperationException("PERPIXEL_TRANSPARENT translucency is not supported.");
                }
            }
            this.shape = ((shape == null) ? null : new Path2D.Float(shape));
            final WindowPeer windowPeer = (WindowPeer)this.getPeer();
            if (windowPeer != null) {
                windowPeer.applyShape((shape == null) ? null : Region.getInstance(shape, null));
            }
        }
    }
    
    @Override
    public Color getBackground() {
        return super.getBackground();
    }
    
    @Override
    public void setBackground(final Color background) {
        final Color background2 = this.getBackground();
        super.setBackground(background);
        if (background2 != null && background2.equals(background)) {
            return;
        }
        final int n = (background2 != null) ? background2.getAlpha() : 255;
        final int n2 = (background != null) ? background.getAlpha() : 255;
        if (n == 255 && n2 < 255) {
            final GraphicsConfiguration graphicsConfiguration = this.getGraphicsConfiguration();
            final GraphicsDevice device = graphicsConfiguration.getDevice();
            if (graphicsConfiguration.getDevice().getFullScreenWindow() == this) {
                throw new IllegalComponentStateException("Making full-screen window non opaque is not supported.");
            }
            if (!graphicsConfiguration.isTranslucencyCapable()) {
                final GraphicsConfiguration translucencyCapableGC = device.getTranslucencyCapableGC();
                if (translucencyCapableGC == null) {
                    throw new UnsupportedOperationException("PERPIXEL_TRANSLUCENT translucency is not supported");
                }
                this.setGraphicsConfiguration(translucencyCapableGC);
            }
            setLayersOpaque(this, false);
        }
        else if (n < 255 && n2 == 255) {
            setLayersOpaque(this, true);
        }
        final WindowPeer windowPeer = (WindowPeer)this.getPeer();
        if (windowPeer != null) {
            windowPeer.setOpaque(n2 == 255);
        }
    }
    
    @Override
    public boolean isOpaque() {
        final Color background = this.getBackground();
        return background == null || background.getAlpha() == 255;
    }
    
    private void updateWindow() {
        synchronized (this.getTreeLock()) {
            final WindowPeer windowPeer = (WindowPeer)this.getPeer();
            if (windowPeer != null) {
                windowPeer.updateWindow();
            }
        }
    }
    
    @Override
    public void paint(final Graphics graphics) {
        if (!this.isOpaque()) {
            final Graphics create = graphics.create();
            try {
                if (create instanceof Graphics2D) {
                    create.setColor(this.getBackground());
                    ((Graphics2D)create).setComposite(AlphaComposite.getInstance(2));
                    create.fillRect(0, 0, this.getWidth(), this.getHeight());
                }
            }
            finally {
                create.dispose();
            }
        }
        super.paint(graphics);
    }
    
    private static void setLayersOpaque(final Component component, final boolean opaque) {
        if (SunToolkit.isInstanceOf(component, "javax.swing.RootPaneContainer")) {
            final JRootPane rootPane = ((RootPaneContainer)component).getRootPane();
            final JLayeredPane layeredPane = rootPane.getLayeredPane();
            final Container contentPane = rootPane.getContentPane();
            final JComponent component2 = (contentPane instanceof JComponent) ? ((JComponent)contentPane) : null;
            layeredPane.setOpaque(opaque);
            rootPane.setOpaque(opaque);
            if (component2 != null) {
                component2.setOpaque(opaque);
                if (component2.getComponentCount() > 0) {
                    final Component component3 = component2.getComponent(0);
                    if (component3 instanceof RootPaneContainer) {
                        setLayersOpaque(component3, opaque);
                    }
                }
            }
        }
    }
    
    @Override
    final Container getContainer() {
        return null;
    }
    
    @Override
    final void applyCompoundShape(final Region region) {
    }
    
    @Override
    final void applyCurrentShape() {
    }
    
    @Override
    final void mixOnReshaping() {
    }
    
    @Override
    final Point getLocationOnWindow() {
        return new Point(0, 0);
    }
    
    private static double limit(double n, final double n2, final double n3) {
        n = Math.max(n, n2);
        n = Math.min(n, n3);
        return n;
    }
    
    private Point2D calculateSecurityWarningPosition(final double n, final double n2, final double n3, final double n4) {
        final double n5 = n + n3 * this.securityWarningAlignmentX + this.securityWarningPointX;
        final double n6 = n2 + n4 * this.securityWarningAlignmentY + this.securityWarningPointY;
        final double limit = limit(n5, n - this.securityWarningWidth - 2.0, n + n3 + 2.0);
        final double limit2 = limit(n6, n2 - this.securityWarningHeight - 2.0, n2 + n4 + 2.0);
        final GraphicsConfiguration graphicsConfiguration_NoClientCode = this.getGraphicsConfiguration_NoClientCode();
        final Rectangle bounds = graphicsConfiguration_NoClientCode.getBounds();
        final Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration_NoClientCode);
        return new Point2D.Double(limit(limit, bounds.x + screenInsets.left, bounds.x + bounds.width - screenInsets.right - this.securityWarningWidth), limit(limit2, bounds.y + screenInsets.top, bounds.y + bounds.height - screenInsets.bottom - this.securityWarningHeight));
    }
    
    @Override
    void updateZOrder() {
    }
    
    static {
        Window.systemSyncLWRequests = false;
        allWindows = new IdentityArrayList<Window>();
        Window.nameCounter = 0;
        log = PlatformLogger.getLogger("java.awt.Window");
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.awt.syncLWRequests"));
        Window.systemSyncLWRequests = (s != null && s.equals("true"));
        final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.awt.Window.locationByPlatform"));
        locationByPlatformProp = (s2 != null && s2.equals("true"));
        beforeFirstWindowShown = new AtomicBoolean(true);
        AWTAccessor.setWindowAccessor(new AWTAccessor.WindowAccessor() {
            @Override
            public float getOpacity(final Window window) {
                return window.opacity;
            }
            
            @Override
            public void setOpacity(final Window window, final float opacity) {
                window.setOpacity(opacity);
            }
            
            @Override
            public Shape getShape(final Window window) {
                return window.getShape();
            }
            
            @Override
            public void setShape(final Window window, final Shape shape) {
                window.setShape(shape);
            }
            
            @Override
            public void setOpaque(final Window window, final boolean b) {
                Color background = window.getBackground();
                if (background == null) {
                    background = new Color(0, 0, 0, 0);
                }
                window.setBackground(new Color(background.getRed(), background.getGreen(), background.getBlue(), b ? 255 : 0));
            }
            
            @Override
            public void updateWindow(final Window window) {
                window.updateWindow();
            }
            
            @Override
            public Dimension getSecurityWarningSize(final Window window) {
                return new Dimension(window.securityWarningWidth, window.securityWarningHeight);
            }
            
            @Override
            public void setSecurityWarningSize(final Window window, final int n, final int n2) {
                window.securityWarningWidth = n;
                window.securityWarningHeight = n2;
            }
            
            @Override
            public void setSecurityWarningPosition(final Window window, final Point2D point2D, final float n, final float n2) {
                window.securityWarningPointX = point2D.getX();
                window.securityWarningPointY = point2D.getY();
                window.securityWarningAlignmentX = n;
                window.securityWarningAlignmentY = n2;
                synchronized (window.getTreeLock()) {
                    final WindowPeer windowPeer = (WindowPeer)window.getPeer();
                    if (windowPeer != null) {
                        windowPeer.repositionSecurityWarning();
                    }
                }
            }
            
            @Override
            public Point2D calculateSecurityWarningPosition(final Window window, final double n, final double n2, final double n3, final double n4) {
                return window.calculateSecurityWarningPosition(n, n2, n3, n4);
            }
            
            @Override
            public void setLWRequestStatus(final Window window, final boolean syncLWRequests) {
                window.syncLWRequests = syncLWRequests;
            }
            
            @Override
            public boolean isAutoRequestFocus(final Window window) {
                return window.autoRequestFocus;
            }
            
            @Override
            public boolean isTrayIconWindow(final Window window) {
                return window.isTrayIconWindow;
            }
            
            @Override
            public void setTrayIconWindow(final Window window, final boolean isTrayIconWindow) {
                window.isTrayIconWindow = isTrayIconWindow;
            }
            
            @Override
            public Window[] getOwnedWindows(final Window window) {
                return window.getOwnedWindows_NoClientCode();
            }
        });
    }
    
    public enum Type
    {
        NORMAL, 
        UTILITY, 
        POPUP;
    }
    
    static class WindowDisposerRecord implements DisposerRecord
    {
        WeakReference<Window> owner;
        final WeakReference<Window> weakThis;
        final WeakReference<AppContext> context;
        
        WindowDisposerRecord(final AppContext appContext, final Window window) {
            this.weakThis = window.weakThis;
            this.context = new WeakReference<AppContext>(appContext);
        }
        
        public void updateOwner() {
            final Window window = this.weakThis.get();
            this.owner = ((window == null) ? null : new WeakReference<Window>(window.getOwner()));
        }
        
        @Override
        public void dispose() {
            if (this.owner != null) {
                final Window window = this.owner.get();
                if (window != null) {
                    window.removeOwnedWindow(this.weakThis);
                }
            }
            final AppContext appContext = this.context.get();
            if (null != appContext) {
                removeFromWindowList(appContext, this.weakThis);
            }
        }
    }
    
    protected class AccessibleAWTWindow extends AccessibleAWTContainer
    {
        private static final long serialVersionUID = 4215068635060671780L;
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.WINDOW;
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (Window.this.getFocusOwner() != null) {
                accessibleStateSet.add(AccessibleState.ACTIVE);
            }
            return accessibleStateSet;
        }
    }
}
