package sun.awt;

import java.util.Vector;
import java.util.Collections;
import java.awt.GraphicsConfiguration;
import java.util.WeakHashMap;
import sun.util.logging.PlatformLogger;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.peer.MouseInfoPeer;
import java.awt.event.WindowEvent;
import sun.security.action.GetPropertyAction;
import sun.awt.im.SimpleInputMethodWindow;
import sun.awt.im.InputContext;
import sun.security.util.SecurityConstants;
import java.awt.image.DataBufferInt;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.awt.image.BufferedImage;
import java.security.Permission;
import java.net.SocketPermission;
import java.io.FilePermission;
import sun.net.util.URLUtil;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.awt.image.ImageObserver;
import sun.awt.image.MultiResolutionToolkitImage;
import sun.awt.image.ToolkitImage;
import sun.awt.image.ByteArrayImageSource;
import sun.awt.image.MultiResolutionImage;
import sun.awt.image.FileImageSource;
import java.awt.image.ImageProducer;
import sun.awt.image.URLImageSource;
import java.awt.Image;
import java.net.URL;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import java.awt.peer.CanvasPeer;
import java.awt.Canvas;
import java.awt.peer.PanelPeer;
import java.awt.Panel;
import sun.font.FontDesignMetrics;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.awt.AWTEvent;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Container;
import java.awt.MenuComponent;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.TimeUnit;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.RobotPeer;
import java.awt.GraphicsDevice;
import java.awt.Robot;
import java.awt.peer.FontPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.SystemTray;
import java.awt.AWTException;
import java.awt.peer.TrayIconPeer;
import java.awt.TrayIcon;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.dnd.DragGestureEvent;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.CheckboxMenuItem;
import java.awt.peer.MenuItemPeer;
import java.awt.MenuItem;
import java.awt.peer.PopupMenuPeer;
import java.awt.PopupMenu;
import java.awt.peer.MenuPeer;
import java.awt.Menu;
import java.awt.peer.MenuBarPeer;
import java.awt.MenuBar;
import java.awt.peer.FileDialogPeer;
import java.awt.FileDialog;
import java.awt.peer.TextAreaPeer;
import java.awt.TextArea;
import java.awt.peer.ScrollPanePeer;
import java.awt.ScrollPane;
import java.awt.peer.ScrollbarPeer;
import java.awt.Scrollbar;
import java.awt.peer.CheckboxPeer;
import java.awt.Checkbox;
import java.awt.peer.ListPeer;
import java.awt.List;
import java.awt.peer.LabelPeer;
import java.awt.Label;
import java.awt.peer.ChoicePeer;
import java.awt.Choice;
import java.awt.peer.TextFieldPeer;
import java.awt.TextField;
import java.awt.peer.ButtonPeer;
import java.awt.Button;
import java.awt.peer.DialogPeer;
import java.awt.peer.FramePeer;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.peer.WindowPeer;
import java.awt.Window;
import java.awt.EventQueue;
import java.awt.RenderingHints;
import java.awt.Dialog;
import java.util.Locale;
import sun.misc.SoftCache;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.Toolkit;

public abstract class SunToolkit extends Toolkit implements WindowClosingSupport, WindowClosingListener, ComponentFactory, InputMethodSupport, KeyboardFocusManagerPeerProvider
{
    public static final int GRAB_EVENT_MASK = Integer.MIN_VALUE;
    private static final String POST_EVENT_QUEUE_KEY = "PostEventQueue";
    protected static int numberOfButtons;
    public static final int MAX_BUTTONS_SUPPORTED = 20;
    private static final ReentrantLock AWT_LOCK;
    private static final Condition AWT_LOCK_COND;
    private static final Map<Object, AppContext> appContextMap;
    static final SoftCache fileImgCache;
    static final SoftCache urlImgCache;
    private static Locale startupLocale;
    private transient WindowClosingListener windowClosingListener;
    private static DefaultMouseInfoPeer mPeer;
    private static Dialog.ModalExclusionType DEFAULT_MODAL_EXCLUSION_TYPE;
    private ModalityListenerList modalityListeners;
    public static final int DEFAULT_WAIT_TIME = 10000;
    private static final int MAX_ITERS = 20;
    private static final int MIN_ITERS = 0;
    private static final int MINIMAL_EDELAY = 0;
    private boolean eventDispatched;
    private boolean queueEmpty;
    private final Object waitLock;
    private static boolean touchKeyboardAutoShowIsEnabled;
    private static boolean checkedSystemAAFontSettings;
    private static boolean useSystemAAFontSettings;
    private static boolean lastExtraCondition;
    private static RenderingHints desktopFontHints;
    public static final String DESKTOPFONTHINTS = "awt.font.desktophints";
    private static Boolean sunAwtDisableMixing;
    private static final Object DEACTIVATION_TIMES_MAP_KEY;
    
    private static void initEQ(final AppContext appContext) {
        final String property = System.getProperty("AWT.EventQueueClass", "java.awt.EventQueue");
        EventQueue eventQueue;
        try {
            eventQueue = (EventQueue)Class.forName(property).newInstance();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            System.err.println("Failed loading " + property + ": " + ex);
            eventQueue = new EventQueue();
        }
        appContext.put(AppContext.EVENT_QUEUE_KEY, eventQueue);
        appContext.put("PostEventQueue", new PostEventQueue(eventQueue));
    }
    
    public SunToolkit() {
        this.windowClosingListener = null;
        this.modalityListeners = new ModalityListenerList();
        this.eventDispatched = false;
        this.queueEmpty = false;
        this.waitLock = "Wait Lock";
    }
    
    public boolean useBufferPerWindow() {
        return false;
    }
    
    @Override
    public abstract WindowPeer createWindow(final Window p0) throws HeadlessException;
    
    @Override
    public abstract FramePeer createFrame(final Frame p0) throws HeadlessException;
    
    public abstract FramePeer createLightweightFrame(final LightweightFrame p0) throws HeadlessException;
    
    @Override
    public abstract DialogPeer createDialog(final Dialog p0) throws HeadlessException;
    
    @Override
    public abstract ButtonPeer createButton(final Button p0) throws HeadlessException;
    
    @Override
    public abstract TextFieldPeer createTextField(final TextField p0) throws HeadlessException;
    
    @Override
    public abstract ChoicePeer createChoice(final Choice p0) throws HeadlessException;
    
    @Override
    public abstract LabelPeer createLabel(final Label p0) throws HeadlessException;
    
    @Override
    public abstract ListPeer createList(final List p0) throws HeadlessException;
    
    @Override
    public abstract CheckboxPeer createCheckbox(final Checkbox p0) throws HeadlessException;
    
    @Override
    public abstract ScrollbarPeer createScrollbar(final Scrollbar p0) throws HeadlessException;
    
    @Override
    public abstract ScrollPanePeer createScrollPane(final ScrollPane p0) throws HeadlessException;
    
    @Override
    public abstract TextAreaPeer createTextArea(final TextArea p0) throws HeadlessException;
    
    @Override
    public abstract FileDialogPeer createFileDialog(final FileDialog p0) throws HeadlessException;
    
    @Override
    public abstract MenuBarPeer createMenuBar(final MenuBar p0) throws HeadlessException;
    
    @Override
    public abstract MenuPeer createMenu(final Menu p0) throws HeadlessException;
    
    @Override
    public abstract PopupMenuPeer createPopupMenu(final PopupMenu p0) throws HeadlessException;
    
    @Override
    public abstract MenuItemPeer createMenuItem(final MenuItem p0) throws HeadlessException;
    
    @Override
    public abstract CheckboxMenuItemPeer createCheckboxMenuItem(final CheckboxMenuItem p0) throws HeadlessException;
    
    @Override
    public abstract DragSourceContextPeer createDragSourceContextPeer(final DragGestureEvent p0) throws InvalidDnDOperationException;
    
    public abstract TrayIconPeer createTrayIcon(final TrayIcon p0) throws HeadlessException, AWTException;
    
    public abstract SystemTrayPeer createSystemTray(final SystemTray p0);
    
    public abstract boolean isTraySupported();
    
    @Override
    public abstract FontPeer getFontPeer(final String p0, final int p1);
    
    @Override
    public abstract RobotPeer createRobot(final Robot p0, final GraphicsDevice p1) throws AWTException;
    
    @Override
    public abstract KeyboardFocusManagerPeer getKeyboardFocusManagerPeer() throws HeadlessException;
    
    public static final void awtLock() {
        SunToolkit.AWT_LOCK.lock();
    }
    
    public static final boolean awtTryLock() {
        return SunToolkit.AWT_LOCK.tryLock();
    }
    
    public static final void awtUnlock() {
        SunToolkit.AWT_LOCK.unlock();
    }
    
    public static final void awtLockWait() throws InterruptedException {
        SunToolkit.AWT_LOCK_COND.await();
    }
    
    public static final void awtLockWait(final long n) throws InterruptedException {
        SunToolkit.AWT_LOCK_COND.await(n, TimeUnit.MILLISECONDS);
    }
    
    public static final void awtLockNotify() {
        SunToolkit.AWT_LOCK_COND.signal();
    }
    
    public static final void awtLockNotifyAll() {
        SunToolkit.AWT_LOCK_COND.signalAll();
    }
    
    public static final boolean isAWTLockHeldByCurrentThread() {
        return SunToolkit.AWT_LOCK.isHeldByCurrentThread();
    }
    
    public static AppContext createNewAppContext() {
        return createNewAppContext(Thread.currentThread().getThreadGroup());
    }
    
    static final AppContext createNewAppContext(final ThreadGroup threadGroup) {
        final AppContext appContext = new AppContext(threadGroup);
        initEQ(appContext);
        return appContext;
    }
    
    static void wakeupEventQueue(final EventQueue eventQueue, final boolean b) {
        AWTAccessor.getEventQueueAccessor().wakeup(eventQueue, b);
    }
    
    protected static Object targetToPeer(final Object o) {
        if (o != null && !GraphicsEnvironment.isHeadless()) {
            return AWTAutoShutdown.getInstance().getPeer(o);
        }
        return null;
    }
    
    protected static void targetCreatedPeer(final Object o, final Object o2) {
        if (o != null && o2 != null && !GraphicsEnvironment.isHeadless()) {
            AWTAutoShutdown.getInstance().registerPeer(o, o2);
        }
    }
    
    protected static void targetDisposedPeer(final Object o, final Object o2) {
        if (o != null && o2 != null && !GraphicsEnvironment.isHeadless()) {
            AWTAutoShutdown.getInstance().unregisterPeer(o, o2);
        }
    }
    
    private static boolean setAppContext(final Object o, final AppContext appContext) {
        if (o instanceof Component) {
            AWTAccessor.getComponentAccessor().setAppContext((Component)o, appContext);
        }
        else {
            if (!(o instanceof MenuComponent)) {
                return false;
            }
            AWTAccessor.getMenuComponentAccessor().setAppContext((MenuComponent)o, appContext);
        }
        return true;
    }
    
    private static AppContext getAppContext(final Object o) {
        if (o instanceof Component) {
            return AWTAccessor.getComponentAccessor().getAppContext((Component)o);
        }
        if (o instanceof MenuComponent) {
            return AWTAccessor.getMenuComponentAccessor().getAppContext((MenuComponent)o);
        }
        return null;
    }
    
    public static AppContext targetToAppContext(final Object o) {
        if (o == null) {
            return null;
        }
        AppContext appContext = getAppContext(o);
        if (appContext == null) {
            appContext = SunToolkit.appContextMap.get(o);
        }
        return appContext;
    }
    
    public static void setLWRequestStatus(final Window window, final boolean b) {
        AWTAccessor.getWindowAccessor().setLWRequestStatus(window, b);
    }
    
    public static void checkAndSetPolicy(final Container container) {
        container.setFocusTraversalPolicy(KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalPolicy());
    }
    
    private static FocusTraversalPolicy createLayoutPolicy() {
        FocusTraversalPolicy focusTraversalPolicy = null;
        try {
            focusTraversalPolicy = (FocusTraversalPolicy)Class.forName("javax.swing.LayoutFocusTraversalPolicy").newInstance();
        }
        catch (final ClassNotFoundException ex) {
            assert false;
        }
        catch (final InstantiationException ex2) {
            assert false;
        }
        catch (final IllegalAccessException ex3) {
            assert false;
        }
        return focusTraversalPolicy;
    }
    
    public static void insertTargetMapping(final Object o, final AppContext appContext) {
        if (!setAppContext(o, appContext)) {
            SunToolkit.appContextMap.put(o, appContext);
        }
    }
    
    public static void postEvent(final AppContext appContext, final AWTEvent systemGenerated) {
        if (systemGenerated == null) {
            throw new NullPointerException();
        }
        final AWTAccessor.SequencedEventAccessor sequencedEventAccessor = AWTAccessor.getSequencedEventAccessor();
        if (sequencedEventAccessor != null && sequencedEventAccessor.isSequencedEvent(systemGenerated)) {
            final AWTEvent nested = sequencedEventAccessor.getNested(systemGenerated);
            if (nested.getID() == 208 && nested instanceof TimedWindowEvent) {
                final TimedWindowEvent timedWindowEvent = (TimedWindowEvent)nested;
                ((SunToolkit)Toolkit.getDefaultToolkit()).setWindowDeactivationTime((Window)timedWindowEvent.getSource(), timedWindowEvent.getWhen());
            }
        }
        setSystemGenerated(systemGenerated);
        final AppContext targetToAppContext = targetToAppContext(systemGenerated.getSource());
        if (targetToAppContext != null && !targetToAppContext.equals(appContext)) {
            throw new RuntimeException("Event posted on wrong app context : " + systemGenerated);
        }
        final PostEventQueue postEventQueue = (PostEventQueue)appContext.get("PostEventQueue");
        if (postEventQueue != null) {
            postEventQueue.postEvent(systemGenerated);
        }
    }
    
    public static void postPriorityEvent(final AWTEvent awtEvent) {
        postEvent(targetToAppContext(awtEvent.getSource()), new PeerEvent(Toolkit.getDefaultToolkit(), new Runnable() {
            @Override
            public void run() {
                AWTAccessor.getAWTEventAccessor().setPosted(awtEvent);
                ((Component)awtEvent.getSource()).dispatchEvent(awtEvent);
            }
        }, 2L));
    }
    
    public static void flushPendingEvents() {
        flushPendingEvents(AppContext.getAppContext());
    }
    
    public static void flushPendingEvents(final AppContext appContext) {
        final PostEventQueue postEventQueue = (PostEventQueue)appContext.get("PostEventQueue");
        if (postEventQueue != null) {
            postEventQueue.flush();
        }
    }
    
    public static void executeOnEventHandlerThread(final Object o, final Runnable runnable) {
        executeOnEventHandlerThread(new PeerEvent(o, runnable, 1L));
    }
    
    public static void executeOnEventHandlerThread(final Object o, final Runnable runnable, final long n) {
        executeOnEventHandlerThread(new PeerEvent(o, runnable, 1L) {
            @Override
            public long getWhen() {
                return n;
            }
        });
    }
    
    public static void executeOnEventHandlerThread(final PeerEvent peerEvent) {
        postEvent(targetToAppContext(peerEvent.getSource()), peerEvent);
    }
    
    public static void invokeLaterOnAppContext(final AppContext appContext, final Runnable runnable) {
        postEvent(appContext, new PeerEvent(Toolkit.getDefaultToolkit(), runnable, 1L));
    }
    
    public static void executeOnEDTAndWait(final Object o, final Runnable runnable) throws InterruptedException, InvocationTargetException {
        if (EventQueue.isDispatchThread()) {
            throw new Error("Cannot call executeOnEDTAndWait from any event dispatcher thread");
        }
        class AWTInvocationLock
        {
        }
        final AWTInvocationLock awtInvocationLock = new AWTInvocationLock();
        final PeerEvent peerEvent = new PeerEvent(o, runnable, awtInvocationLock, true, 1L);
        synchronized (awtInvocationLock) {
            executeOnEventHandlerThread(peerEvent);
            while (!peerEvent.isDispatched()) {
                awtInvocationLock.wait();
            }
        }
        final Throwable throwable = peerEvent.getThrowable();
        if (throwable != null) {
            throw new InvocationTargetException(throwable);
        }
    }
    
    public static boolean isDispatchThreadForAppContext(final Object o) {
        return AWTAccessor.getEventQueueAccessor().isDispatchThreadImpl((EventQueue)targetToAppContext(o).get(AppContext.EVENT_QUEUE_KEY));
    }
    
    @Override
    public Dimension getScreenSize() {
        return new Dimension(this.getScreenWidth(), this.getScreenHeight());
    }
    
    protected abstract int getScreenWidth();
    
    protected abstract int getScreenHeight();
    
    @Override
    public FontMetrics getFontMetrics(final Font font) {
        return FontDesignMetrics.getMetrics(font);
    }
    
    @Override
    public String[] getFontList() {
        return new String[] { "Dialog", "SansSerif", "Serif", "Monospaced", "DialogInput" };
    }
    
    @Override
    public PanelPeer createPanel(final Panel panel) {
        return (PanelPeer)this.createComponent(panel);
    }
    
    @Override
    public CanvasPeer createCanvas(final Canvas canvas) {
        return (CanvasPeer)this.createComponent(canvas);
    }
    
    public void disableBackgroundErase(final Canvas canvas) {
        this.disableBackgroundEraseImpl(canvas);
    }
    
    public void disableBackgroundErase(final Component component) {
        this.disableBackgroundEraseImpl(component);
    }
    
    private void disableBackgroundEraseImpl(final Component component) {
        AWTAccessor.getComponentAccessor().setBackgroundEraseDisabled(component, true);
    }
    
    public static boolean getSunAwtNoerasebackground() {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.awt.noerasebackground"));
    }
    
    public static boolean getSunAwtErasebackgroundonresize() {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.awt.erasebackgroundonresize"));
    }
    
    static Image getImageFromHash(final Toolkit toolkit, final URL url) {
        checkPermissions(url);
        synchronized (SunToolkit.urlImgCache) {
            final String string = url.toString();
            Image image = (Image)SunToolkit.urlImgCache.get(string);
            if (image == null) {
                try {
                    image = toolkit.createImage(new URLImageSource(url));
                    SunToolkit.urlImgCache.put(string, image);
                }
                catch (final Exception ex) {}
            }
            return image;
        }
    }
    
    static Image getImageFromHash(final Toolkit toolkit, final String s) {
        checkPermissions(s);
        synchronized (SunToolkit.fileImgCache) {
            Image image = (Image)SunToolkit.fileImgCache.get(s);
            if (image == null) {
                try {
                    image = toolkit.createImage(new FileImageSource(s));
                    SunToolkit.fileImgCache.put(s, image);
                }
                catch (final Exception ex) {}
            }
            return image;
        }
    }
    
    @Override
    public Image getImage(final String s) {
        return getImageFromHash(this, s);
    }
    
    @Override
    public Image getImage(final URL url) {
        return getImageFromHash(this, url);
    }
    
    protected Image getImageWithResolutionVariant(final String s, final String s2) {
        synchronized (SunToolkit.fileImgCache) {
            final Image imageFromHash = getImageFromHash(this, s);
            if (imageFromHash instanceof MultiResolutionImage) {
                return imageFromHash;
            }
            final Image imageWithResolutionVariant = createImageWithResolutionVariant(imageFromHash, getImageFromHash(this, s2));
            SunToolkit.fileImgCache.put(s, imageWithResolutionVariant);
            return imageWithResolutionVariant;
        }
    }
    
    protected Image getImageWithResolutionVariant(final URL url, final URL url2) {
        synchronized (SunToolkit.urlImgCache) {
            final Image imageFromHash = getImageFromHash(this, url);
            if (imageFromHash instanceof MultiResolutionImage) {
                return imageFromHash;
            }
            final Image imageWithResolutionVariant = createImageWithResolutionVariant(imageFromHash, getImageFromHash(this, url2));
            SunToolkit.urlImgCache.put(url.toString(), imageWithResolutionVariant);
            return imageWithResolutionVariant;
        }
    }
    
    @Override
    public Image createImage(final String s) {
        checkPermissions(s);
        return this.createImage(new FileImageSource(s));
    }
    
    @Override
    public Image createImage(final URL url) {
        checkPermissions(url);
        return this.createImage(new URLImageSource(url));
    }
    
    @Override
    public Image createImage(final byte[] array, final int n, final int n2) {
        return this.createImage(new ByteArrayImageSource(array, n, n2));
    }
    
    @Override
    public Image createImage(final ImageProducer imageProducer) {
        return new ToolkitImage(imageProducer);
    }
    
    public static Image createImageWithResolutionVariant(final Image image, final Image image2) {
        return new MultiResolutionToolkitImage(image, image2);
    }
    
    @Override
    public int checkImage(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        if (!(image instanceof ToolkitImage)) {
            return 32;
        }
        final ToolkitImage toolkitImage = (ToolkitImage)image;
        int check;
        if (n == 0 || n2 == 0) {
            check = 32;
        }
        else {
            check = toolkitImage.getImageRep().check(imageObserver);
        }
        return (toolkitImage.check(imageObserver) | check) & this.checkResolutionVariant(image, n, n2, imageObserver);
    }
    
    @Override
    public boolean prepareImage(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        if (n == 0 || n2 == 0) {
            return true;
        }
        if (!(image instanceof ToolkitImage)) {
            return true;
        }
        final ToolkitImage toolkitImage = (ToolkitImage)image;
        if (toolkitImage.hasError()) {
            if (imageObserver != null) {
                imageObserver.imageUpdate(image, 192, -1, -1, -1, -1);
            }
            return false;
        }
        return toolkitImage.getImageRep().prepare(imageObserver) & this.prepareResolutionVariant(image, n, n2, imageObserver);
    }
    
    private int checkResolutionVariant(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        final ToolkitImage resolutionVariant = getResolutionVariant(image);
        final int rvSize = getRVSize(n);
        final int rvSize2 = getRVSize(n2);
        return (resolutionVariant == null || resolutionVariant.hasError()) ? 65535 : this.checkImage(resolutionVariant, rvSize, rvSize2, MultiResolutionToolkitImage.getResolutionVariantObserver(image, imageObserver, n, n2, rvSize, rvSize2, true));
    }
    
    private boolean prepareResolutionVariant(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        final ToolkitImage resolutionVariant = getResolutionVariant(image);
        final int rvSize = getRVSize(n);
        final int rvSize2 = getRVSize(n2);
        return resolutionVariant == null || resolutionVariant.hasError() || this.prepareImage(resolutionVariant, rvSize, rvSize2, MultiResolutionToolkitImage.getResolutionVariantObserver(image, imageObserver, n, n2, rvSize, rvSize2, true));
    }
    
    private static int getRVSize(final int n) {
        return (n == -1) ? -1 : (2 * n);
    }
    
    private static ToolkitImage getResolutionVariant(final Image image) {
        if (image instanceof MultiResolutionToolkitImage) {
            final Image resolutionVariant = ((MultiResolutionToolkitImage)image).getResolutionVariant();
            if (resolutionVariant instanceof ToolkitImage) {
                return (ToolkitImage)resolutionVariant;
            }
        }
        return null;
    }
    
    protected static boolean imageCached(final String s) {
        return SunToolkit.fileImgCache.containsKey(s);
    }
    
    protected static boolean imageCached(final URL url) {
        return SunToolkit.urlImgCache.containsKey(url.toString());
    }
    
    protected static boolean imageExists(final String s) {
        if (s != null) {
            checkPermissions(s);
            return new File(s).exists();
        }
        return false;
    }
    
    protected static boolean imageExists(final URL url) {
        if (url != null) {
            checkPermissions(url);
            try (final InputStream openStream = url.openStream()) {
                return true;
            }
            catch (final IOException ex) {
                return false;
            }
        }
        return false;
    }
    
    private static void checkPermissions(final String s) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkRead(s);
        }
    }
    
    private static void checkPermissions(final URL url) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            try {
                final Permission connectPermission = URLUtil.getConnectPermission(url);
                if (connectPermission != null) {
                    try {
                        securityManager.checkPermission(connectPermission);
                    }
                    catch (final SecurityException ex) {
                        if (connectPermission instanceof FilePermission && connectPermission.getActions().indexOf("read") != -1) {
                            securityManager.checkRead(connectPermission.getName());
                        }
                        else {
                            if (!(connectPermission instanceof SocketPermission) || connectPermission.getActions().indexOf("connect") == -1) {
                                throw ex;
                            }
                            securityManager.checkConnect(url.getHost(), url.getPort());
                        }
                    }
                }
            }
            catch (final IOException ex2) {
                securityManager.checkConnect(url.getHost(), url.getPort());
            }
        }
    }
    
    public static BufferedImage getScaledIconImage(final java.util.List<Image> list, final int n, final int n2) {
        if (n == 0 || n2 == 0) {
            return null;
        }
        Image image = null;
        int n3 = 0;
        int n4 = 0;
        double n5 = 3.0;
        for (final Image image2 : list) {
            if (image2 == null) {
                continue;
            }
            if (image2 instanceof ToolkitImage) {
                ((ToolkitImage)image2).getImageRep().reconstruct(32);
            }
            int width;
            int height;
            try {
                width = image2.getWidth(null);
                height = image2.getHeight(null);
            }
            catch (final Exception ex) {
                continue;
            }
            if (width <= 0 || height <= 0) {
                continue;
            }
            final double min = Math.min(n / (double)width, n2 / (double)height);
            int n6;
            int n7;
            double n8;
            if (min >= 2.0) {
                final double floor = Math.floor(min);
                n6 = width * (int)floor;
                n7 = height * (int)floor;
                n8 = 1.0 - 0.5 / floor;
            }
            else if (min >= 1.0) {
                final double floor = 1.0;
                n6 = width;
                n7 = height;
                n8 = 0.0;
            }
            else if (min >= 0.75) {
                final double floor = 0.75;
                n6 = width * 3 / 4;
                n7 = height * 3 / 4;
                n8 = 0.3;
            }
            else if (min >= 0.6666) {
                final double floor = 0.6666;
                n6 = width * 2 / 3;
                n7 = height * 2 / 3;
                n8 = 0.33;
            }
            else {
                final double ceil = Math.ceil(1.0 / min);
                final double floor = 1.0 / ceil;
                n6 = (int)Math.round(width / ceil);
                n7 = (int)Math.round(height / ceil);
                n8 = 1.0 - 1.0 / ceil;
            }
            final double n9 = (n - (double)n6) / n + (n2 - (double)n7) / n2 + n8;
            if (n9 < n5) {
                n5 = n9;
                image = image2;
                n3 = n6;
                n4 = n7;
            }
            if (n9 == 0.0) {
                break;
            }
        }
        if (image == null) {
            return null;
        }
        final BufferedImage bufferedImage = new BufferedImage(n, n2, 2);
        final Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        try {
            graphics.drawImage(image, (n - n3) / 2, (n2 - n4) / 2, n3, n4, null);
        }
        finally {
            graphics.dispose();
        }
        return bufferedImage;
    }
    
    public static DataBufferInt getScaledIconData(final java.util.List<Image> list, final int n, final int n2) {
        final BufferedImage scaledIconImage = getScaledIconImage(list, n, n2);
        if (scaledIconImage == null) {
            return null;
        }
        return (DataBufferInt)scaledIconImage.getRaster().getDataBuffer();
    }
    
    @Override
    protected EventQueue getSystemEventQueueImpl() {
        return getSystemEventQueueImplPP();
    }
    
    static EventQueue getSystemEventQueueImplPP() {
        return getSystemEventQueueImplPP(AppContext.getAppContext());
    }
    
    public static EventQueue getSystemEventQueueImplPP(final AppContext appContext) {
        return (EventQueue)appContext.get(AppContext.EVENT_QUEUE_KEY);
    }
    
    public static Container getNativeContainer(final Component component) {
        return Toolkit.getNativeContainer(component);
    }
    
    public static Component getHeavyweightComponent(Component parent) {
        while (parent != null && AWTAccessor.getComponentAccessor().isLightweight(parent)) {
            parent = AWTAccessor.getComponentAccessor().getParent(parent);
        }
        return parent;
    }
    
    public int getFocusAcceleratorKeyMask() {
        return 8;
    }
    
    public boolean isPrintableCharacterModifiersMask(final int n) {
        return (n & 0x8) == (n & 0x2);
    }
    
    public boolean canPopupOverlapTaskBar() {
        boolean b = true;
        try {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkPermission(SecurityConstants.AWT.SET_WINDOW_ALWAYS_ON_TOP_PERMISSION);
            }
        }
        catch (final SecurityException ex) {
            b = false;
        }
        return b;
    }
    
    @Override
    public Window createInputMethodWindow(final String s, final InputContext inputContext) {
        return new SimpleInputMethodWindow(s, inputContext);
    }
    
    @Override
    public boolean enableInputMethodsForTextComponent() {
        return false;
    }
    
    public static Locale getStartupLocale() {
        if (SunToolkit.startupLocale == null) {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("user.language", "en"));
            final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("user.region"));
            String substring;
            String substring2;
            if (s2 != null) {
                final int index = s2.indexOf(95);
                if (index >= 0) {
                    substring = s2.substring(0, index);
                    substring2 = s2.substring(index + 1);
                }
                else {
                    substring = s2;
                    substring2 = "";
                }
            }
            else {
                substring = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("user.country", ""));
                substring2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("user.variant", ""));
            }
            SunToolkit.startupLocale = new Locale(s, substring, substring2);
        }
        return SunToolkit.startupLocale;
    }
    
    @Override
    public Locale getDefaultKeyboardLocale() {
        return getStartupLocale();
    }
    
    @Override
    public WindowClosingListener getWindowClosingListener() {
        return this.windowClosingListener;
    }
    
    @Override
    public void setWindowClosingListener(final WindowClosingListener windowClosingListener) {
        this.windowClosingListener = windowClosingListener;
    }
    
    @Override
    public RuntimeException windowClosingNotify(final WindowEvent windowEvent) {
        if (this.windowClosingListener != null) {
            return this.windowClosingListener.windowClosingNotify(windowEvent);
        }
        return null;
    }
    
    @Override
    public RuntimeException windowClosingDelivered(final WindowEvent windowEvent) {
        if (this.windowClosingListener != null) {
            return this.windowClosingListener.windowClosingDelivered(windowEvent);
        }
        return null;
    }
    
    @Override
    protected synchronized MouseInfoPeer getMouseInfoPeer() {
        if (SunToolkit.mPeer == null) {
            SunToolkit.mPeer = new DefaultMouseInfoPeer();
        }
        return SunToolkit.mPeer;
    }
    
    public static boolean needsXEmbed() {
        if ("true".equals(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.awt.noxembed", "false")))) {
            return false;
        }
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        return defaultToolkit instanceof SunToolkit && ((SunToolkit)defaultToolkit).needsXEmbedImpl();
    }
    
    protected boolean needsXEmbedImpl() {
        return false;
    }
    
    protected final boolean isXEmbedServerRequested() {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.awt.xembedserver"));
    }
    
    public static boolean isModalExcludedSupported() {
        return Toolkit.getDefaultToolkit().isModalExclusionTypeSupported(SunToolkit.DEFAULT_MODAL_EXCLUSION_TYPE);
    }
    
    protected boolean isModalExcludedSupportedImpl() {
        return false;
    }
    
    public static void setModalExcluded(final Window window) {
        if (SunToolkit.DEFAULT_MODAL_EXCLUSION_TYPE == null) {
            SunToolkit.DEFAULT_MODAL_EXCLUSION_TYPE = Dialog.ModalExclusionType.APPLICATION_EXCLUDE;
        }
        window.setModalExclusionType(SunToolkit.DEFAULT_MODAL_EXCLUSION_TYPE);
    }
    
    public static boolean isModalExcluded(final Window window) {
        if (SunToolkit.DEFAULT_MODAL_EXCLUSION_TYPE == null) {
            SunToolkit.DEFAULT_MODAL_EXCLUSION_TYPE = Dialog.ModalExclusionType.APPLICATION_EXCLUDE;
        }
        return window.getModalExclusionType().compareTo(SunToolkit.DEFAULT_MODAL_EXCLUSION_TYPE) >= 0;
    }
    
    @Override
    public boolean isModalityTypeSupported(final Dialog.ModalityType modalityType) {
        return modalityType == Dialog.ModalityType.MODELESS || modalityType == Dialog.ModalityType.APPLICATION_MODAL;
    }
    
    @Override
    public boolean isModalExclusionTypeSupported(final Dialog.ModalExclusionType modalExclusionType) {
        return modalExclusionType == Dialog.ModalExclusionType.NO_EXCLUDE;
    }
    
    public void addModalityListener(final ModalityListener modalityListener) {
        this.modalityListeners.add(modalityListener);
    }
    
    public void removeModalityListener(final ModalityListener modalityListener) {
        this.modalityListeners.remove(modalityListener);
    }
    
    public void notifyModalityPushed(final Dialog dialog) {
        this.notifyModalityChange(1300, dialog);
    }
    
    public void notifyModalityPopped(final Dialog dialog) {
        this.notifyModalityChange(1301, dialog);
    }
    
    final void notifyModalityChange(final int n, final Dialog dialog) {
        new ModalityEvent(dialog, this.modalityListeners, n).dispatch();
    }
    
    public static boolean isLightweightOrUnknown(final Component component) {
        return component.isLightweight() || !(getDefaultToolkit() instanceof SunToolkit) || (!(component instanceof Button) && !(component instanceof Canvas) && !(component instanceof Checkbox) && !(component instanceof Choice) && !(component instanceof Label) && !(component instanceof List) && !(component instanceof Panel) && !(component instanceof Scrollbar) && !(component instanceof ScrollPane) && !(component instanceof TextArea) && !(component instanceof TextField) && !(component instanceof Window));
    }
    
    public void realSync() throws OperationTimedOut, InfiniteLoop {
        this.realSync(10000L);
    }
    
    public void realSync(final long n) throws OperationTimedOut, InfiniteLoop {
        if (EventQueue.isDispatchThread()) {
            throw new IllegalThreadException("The SunToolkit.realSync() method cannot be used on the event dispatch thread (EDT).");
        }
        int n2 = 0;
        do {
            this.sync();
            int i;
            for (i = 0; i < 0; ++i) {
                this.syncNativeQueue(n);
            }
            while (this.syncNativeQueue(n) && i < 20) {
                ++i;
            }
            if (i >= 20) {
                throw new InfiniteLoop();
            }
            int j;
            for (j = 0; j < 0; ++j) {
                this.waitForIdle(n);
            }
            while (this.waitForIdle(n) && j < 20) {
                ++j;
            }
            if (j >= 20) {
                throw new InfiniteLoop();
            }
            ++n2;
        } while ((this.syncNativeQueue(n) || this.waitForIdle(n)) && n2 < 20);
    }
    
    protected abstract boolean syncNativeQueue(final long p0);
    
    private boolean isEQEmpty() {
        return AWTAccessor.getEventQueueAccessor().noEvents(this.getSystemEventQueueImpl());
    }
    
    protected final boolean waitForIdle(final long n) {
        flushPendingEvents();
        final boolean eqEmpty = this.isEQEmpty();
        this.queueEmpty = false;
        this.eventDispatched = false;
        synchronized (this.waitLock) {
            postEvent(AppContext.getAppContext(), new PeerEvent(this.getSystemEventQueueImpl(), null, 4L) {
                @Override
                public void dispatch() {
                    int i;
                    for (i = 0; i < 0; ++i) {
                        SunToolkit.this.syncNativeQueue(n);
                    }
                    while (SunToolkit.this.syncNativeQueue(n) && i < 20) {
                        ++i;
                    }
                    SunToolkit.flushPendingEvents();
                    synchronized (SunToolkit.this.waitLock) {
                        SunToolkit.this.queueEmpty = SunToolkit.this.isEQEmpty();
                        SunToolkit.this.eventDispatched = true;
                        SunToolkit.this.waitLock.notifyAll();
                    }
                }
            });
            try {
                while (!this.eventDispatched) {
                    this.waitLock.wait();
                }
            }
            catch (final InterruptedException ex) {
                return false;
            }
        }
        try {
            Thread.sleep(0L);
        }
        catch (final InterruptedException ex2) {
            throw new RuntimeException("Interrupted");
        }
        flushPendingEvents();
        synchronized (this.waitLock) {
            return !this.queueEmpty || !this.isEQEmpty() || !eqEmpty;
        }
    }
    
    public abstract void grab(final Window p0);
    
    public abstract void ungrab(final Window p0);
    
    public void showOrHideTouchKeyboard(final Component component, final AWTEvent awtEvent) {
    }
    
    public static boolean isTouchKeyboardAutoShowEnabled() {
        return SunToolkit.touchKeyboardAutoShowIsEnabled;
    }
    
    public static native void closeSplashScreen();
    
    private void fireDesktopFontPropertyChanges() {
        this.setDesktopProperty("awt.font.desktophints", getDesktopFontHints());
    }
    
    public static void setAAFontSettingsCondition(final boolean lastExtraCondition) {
        if (lastExtraCondition != SunToolkit.lastExtraCondition) {
            SunToolkit.lastExtraCondition = lastExtraCondition;
            if (SunToolkit.checkedSystemAAFontSettings) {
                SunToolkit.checkedSystemAAFontSettings = false;
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                if (defaultToolkit instanceof SunToolkit) {
                    ((SunToolkit)defaultToolkit).fireDesktopFontPropertyChanges();
                }
            }
        }
    }
    
    private static RenderingHints getDesktopAAHintsByName(String lowerCase) {
        Object o = null;
        lowerCase = lowerCase.toLowerCase(Locale.ENGLISH);
        if (lowerCase.equals("on")) {
            o = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
        }
        else if (lowerCase.equals("gasp")) {
            o = RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
        }
        else if (lowerCase.equals("lcd") || lowerCase.equals("lcd_hrgb")) {
            o = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
        }
        else if (lowerCase.equals("lcd_hbgr")) {
            o = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
        }
        else if (lowerCase.equals("lcd_vrgb")) {
            o = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB;
        }
        else if (lowerCase.equals("lcd_vbgr")) {
            o = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR;
        }
        if (o != null) {
            final RenderingHints renderingHints = new RenderingHints(null);
            renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, o);
            return renderingHints;
        }
        return null;
    }
    
    private static boolean useSystemAAFontSettings() {
        if (!SunToolkit.checkedSystemAAFontSettings) {
            SunToolkit.useSystemAAFontSettings = true;
            String s = null;
            if (Toolkit.getDefaultToolkit() instanceof SunToolkit) {
                s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("awt.useSystemAAFontSettings"));
            }
            if (s != null && !(SunToolkit.useSystemAAFontSettings = Boolean.valueOf(s))) {
                SunToolkit.desktopFontHints = getDesktopAAHintsByName(s);
            }
            if (SunToolkit.useSystemAAFontSettings) {
                SunToolkit.useSystemAAFontSettings = SunToolkit.lastExtraCondition;
            }
            SunToolkit.checkedSystemAAFontSettings = true;
        }
        return SunToolkit.useSystemAAFontSettings;
    }
    
    protected RenderingHints getDesktopAAHints() {
        return null;
    }
    
    public static RenderingHints getDesktopFontHints() {
        if (useSystemAAFontSettings()) {
            final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            if (defaultToolkit instanceof SunToolkit) {
                return ((SunToolkit)defaultToolkit).getDesktopAAHints();
            }
            return null;
        }
        else {
            if (SunToolkit.desktopFontHints != null) {
                return (RenderingHints)SunToolkit.desktopFontHints.clone();
            }
            return null;
        }
    }
    
    public abstract boolean isDesktopSupported();
    
    public static synchronized void consumeNextKeyTyped(final KeyEvent keyEvent) {
        try {
            AWTAccessor.getDefaultKeyboardFocusManagerAccessor().consumeNextKeyTyped((DefaultKeyboardFocusManager)KeyboardFocusManager.getCurrentKeyboardFocusManager(), keyEvent);
        }
        catch (final ClassCastException ex) {
            ex.printStackTrace();
        }
    }
    
    protected static void dumpPeers(final PlatformLogger platformLogger) {
        AWTAutoShutdown.getInstance().dumpPeers(platformLogger);
    }
    
    public static Window getContainingWindow(Component parent) {
        while (parent != null && !(parent instanceof Window)) {
            parent = parent.getParent();
        }
        return (Window)parent;
    }
    
    public static synchronized boolean getSunAwtDisableMixing() {
        if (SunToolkit.sunAwtDisableMixing == null) {
            SunToolkit.sunAwtDisableMixing = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.awt.disableMixing"));
        }
        return SunToolkit.sunAwtDisableMixing;
    }
    
    public boolean isNativeGTKAvailable() {
        return false;
    }
    
    public synchronized void setWindowDeactivationTime(final Window window, final long n) {
        final AppContext appContext = getAppContext(window);
        WeakHashMap weakHashMap = (WeakHashMap)appContext.get(SunToolkit.DEACTIVATION_TIMES_MAP_KEY);
        if (weakHashMap == null) {
            weakHashMap = new WeakHashMap();
            appContext.put(SunToolkit.DEACTIVATION_TIMES_MAP_KEY, weakHashMap);
        }
        weakHashMap.put(window, n);
    }
    
    public synchronized long getWindowDeactivationTime(final Window window) {
        final WeakHashMap weakHashMap = (WeakHashMap)getAppContext(window).get(SunToolkit.DEACTIVATION_TIMES_MAP_KEY);
        if (weakHashMap == null) {
            return -1L;
        }
        final Long n = weakHashMap.get(window);
        return (n == null) ? -1L : n;
    }
    
    public boolean isWindowOpacitySupported() {
        return false;
    }
    
    public boolean isWindowShapingSupported() {
        return false;
    }
    
    public boolean isWindowTranslucencySupported() {
        return false;
    }
    
    public boolean isTranslucencyCapable(final GraphicsConfiguration graphicsConfiguration) {
        return false;
    }
    
    public boolean isSwingBackbufferTranslucencySupported() {
        return false;
    }
    
    public static boolean isContainingTopLevelOpaque(final Component component) {
        final Window containingWindow = getContainingWindow(component);
        return containingWindow != null && containingWindow.isOpaque();
    }
    
    public static boolean isContainingTopLevelTranslucent(final Component component) {
        final Window containingWindow = getContainingWindow(component);
        return containingWindow != null && containingWindow.getOpacity() < 1.0f;
    }
    
    public boolean needUpdateWindow() {
        return false;
    }
    
    public int getNumberOfButtons() {
        return 3;
    }
    
    public static boolean isInstanceOf(final Object o, final String s) {
        return o != null && s != null && isInstanceOf(o.getClass(), s);
    }
    
    private static boolean isInstanceOf(final Class<?> clazz, final String s) {
        if (clazz == null) {
            return false;
        }
        if (clazz.getName().equals(s)) {
            return true;
        }
        final Class[] interfaces = clazz.getInterfaces();
        for (int length = interfaces.length, i = 0; i < length; ++i) {
            if (interfaces[i].getName().equals(s)) {
                return true;
            }
        }
        return isInstanceOf(clazz.getSuperclass(), s);
    }
    
    protected static LightweightFrame getLightweightFrame(Component parent) {
        while (parent != null) {
            if (parent instanceof LightweightFrame) {
                return (LightweightFrame)parent;
            }
            if (parent instanceof Window) {
                return null;
            }
            parent = parent.getParent();
        }
        return null;
    }
    
    public static void setSystemGenerated(final AWTEvent systemGenerated) {
        AWTAccessor.getAWTEventAccessor().setSystemGenerated(systemGenerated);
    }
    
    public static boolean isSystemGenerated(final AWTEvent awtEvent) {
        return AWTAccessor.getAWTEventAccessor().isSystemGenerated(awtEvent);
    }
    
    static {
        if (AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.awt.nativedebug"))) {
            DebugSettings.init();
        }
        SunToolkit.touchKeyboardAutoShowIsEnabled = Boolean.valueOf(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("awt.touchKeyboardAutoShowIsEnabled", "true")));
        SunToolkit.numberOfButtons = 0;
        AWT_LOCK = new ReentrantLock();
        AWT_LOCK_COND = SunToolkit.AWT_LOCK.newCondition();
        appContextMap = Collections.synchronizedMap(new WeakHashMap<Object, AppContext>());
        fileImgCache = new SoftCache();
        urlImgCache = new SoftCache();
        SunToolkit.startupLocale = null;
        SunToolkit.mPeer = null;
        SunToolkit.DEFAULT_MODAL_EXCLUSION_TYPE = null;
        SunToolkit.lastExtraCondition = true;
        SunToolkit.sunAwtDisableMixing = null;
        DEACTIVATION_TIMES_MAP_KEY = new Object();
    }
    
    static class ModalityListenerList implements ModalityListener
    {
        Vector<ModalityListener> listeners;
        
        ModalityListenerList() {
            this.listeners = new Vector<ModalityListener>();
        }
        
        void add(final ModalityListener modalityListener) {
            this.listeners.addElement(modalityListener);
        }
        
        void remove(final ModalityListener modalityListener) {
            this.listeners.removeElement(modalityListener);
        }
        
        @Override
        public void modalityPushed(final ModalityEvent modalityEvent) {
            final Iterator<ModalityListener> iterator = this.listeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().modalityPushed(modalityEvent);
            }
        }
        
        @Override
        public void modalityPopped(final ModalityEvent modalityEvent) {
            final Iterator<ModalityListener> iterator = this.listeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().modalityPopped(modalityEvent);
            }
        }
    }
    
    public static class OperationTimedOut extends RuntimeException
    {
        public OperationTimedOut(final String s) {
            super(s);
        }
        
        public OperationTimedOut() {
        }
    }
    
    public static class InfiniteLoop extends RuntimeException
    {
    }
    
    public static class IllegalThreadException extends RuntimeException
    {
        public IllegalThreadException(final String s) {
            super(s);
        }
        
        public IllegalThreadException() {
        }
    }
}
