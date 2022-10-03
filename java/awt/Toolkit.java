package java.awt;

import java.util.Iterator;
import sun.awt.PeerEvent;
import java.beans.PropertyChangeEvent;
import sun.awt.AppContext;
import sun.awt.UngrabEvent;
import sun.util.CoreResourceBundleControl;
import java.util.Locale;
import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.util.ArrayList;
import java.util.EventListener;
import java.awt.event.AWTEventListenerProxy;
import java.beans.PropertyChangeListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.dnd.DragGestureEvent;
import sun.security.util.SecurityConstants;
import java.util.MissingResourceException;
import java.awt.datatransfer.Clipboard;
import java.awt.image.ImageProducer;
import java.awt.image.ImageObserver;
import java.net.URL;
import sun.awt.HeadlessToolkit;
import java.util.StringTokenizer;
import java.security.AccessController;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.io.File;
import java.awt.image.ColorModel;
import java.awt.peer.FontPeer;
import sun.awt.NullComponentPeer;
import java.awt.peer.MouseInfoPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.WindowPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.ButtonPeer;
import java.awt.peer.DesktopPeer;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.awt.event.AWTEventListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.ResourceBundle;
import java.awt.peer.LightweightPeer;

public abstract class Toolkit
{
    private static LightweightPeer lightweightMarker;
    private static Toolkit toolkit;
    private static String atNames;
    private static ResourceBundle resources;
    private static ResourceBundle platformResources;
    private static boolean loaded;
    protected final Map<String, Object> desktopProperties;
    protected final PropertyChangeSupport desktopPropsSupport;
    private static final int LONG_BITS = 64;
    private int[] calls;
    private static volatile long enabledOnToolkitMask;
    private AWTEventListener eventListener;
    private WeakHashMap<AWTEventListener, SelectiveAWTEventListener> listener2SelectiveListener;
    
    public Toolkit() {
        this.desktopProperties = new HashMap<String, Object>();
        this.desktopPropsSupport = createPropertyChangeSupport(this);
        this.calls = new int[64];
        this.eventListener = null;
        this.listener2SelectiveListener = new WeakHashMap<AWTEventListener, SelectiveAWTEventListener>();
    }
    
    protected abstract DesktopPeer createDesktopPeer(final Desktop p0) throws HeadlessException;
    
    protected abstract ButtonPeer createButton(final Button p0) throws HeadlessException;
    
    protected abstract TextFieldPeer createTextField(final TextField p0) throws HeadlessException;
    
    protected abstract LabelPeer createLabel(final Label p0) throws HeadlessException;
    
    protected abstract ListPeer createList(final List p0) throws HeadlessException;
    
    protected abstract CheckboxPeer createCheckbox(final Checkbox p0) throws HeadlessException;
    
    protected abstract ScrollbarPeer createScrollbar(final Scrollbar p0) throws HeadlessException;
    
    protected abstract ScrollPanePeer createScrollPane(final ScrollPane p0) throws HeadlessException;
    
    protected abstract TextAreaPeer createTextArea(final TextArea p0) throws HeadlessException;
    
    protected abstract ChoicePeer createChoice(final Choice p0) throws HeadlessException;
    
    protected abstract FramePeer createFrame(final Frame p0) throws HeadlessException;
    
    protected abstract CanvasPeer createCanvas(final Canvas p0);
    
    protected abstract PanelPeer createPanel(final Panel p0);
    
    protected abstract WindowPeer createWindow(final Window p0) throws HeadlessException;
    
    protected abstract DialogPeer createDialog(final Dialog p0) throws HeadlessException;
    
    protected abstract MenuBarPeer createMenuBar(final MenuBar p0) throws HeadlessException;
    
    protected abstract MenuPeer createMenu(final Menu p0) throws HeadlessException;
    
    protected abstract PopupMenuPeer createPopupMenu(final PopupMenu p0) throws HeadlessException;
    
    protected abstract MenuItemPeer createMenuItem(final MenuItem p0) throws HeadlessException;
    
    protected abstract FileDialogPeer createFileDialog(final FileDialog p0) throws HeadlessException;
    
    protected abstract CheckboxMenuItemPeer createCheckboxMenuItem(final CheckboxMenuItem p0) throws HeadlessException;
    
    protected MouseInfoPeer getMouseInfoPeer() {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    protected LightweightPeer createComponent(final Component component) {
        if (Toolkit.lightweightMarker == null) {
            Toolkit.lightweightMarker = new NullComponentPeer();
        }
        return Toolkit.lightweightMarker;
    }
    
    @Deprecated
    protected abstract FontPeer getFontPeer(final String p0, final int p1);
    
    protected void loadSystemColors(final int[] array) throws HeadlessException {
        GraphicsEnvironment.checkHeadless();
    }
    
    public void setDynamicLayout(final boolean dynamicLayout) throws HeadlessException {
        GraphicsEnvironment.checkHeadless();
        if (this != getDefaultToolkit()) {
            getDefaultToolkit().setDynamicLayout(dynamicLayout);
        }
    }
    
    protected boolean isDynamicLayoutSet() throws HeadlessException {
        GraphicsEnvironment.checkHeadless();
        return this != getDefaultToolkit() && getDefaultToolkit().isDynamicLayoutSet();
    }
    
    public boolean isDynamicLayoutActive() throws HeadlessException {
        GraphicsEnvironment.checkHeadless();
        return this != getDefaultToolkit() && getDefaultToolkit().isDynamicLayoutActive();
    }
    
    public abstract Dimension getScreenSize() throws HeadlessException;
    
    public abstract int getScreenResolution() throws HeadlessException;
    
    public Insets getScreenInsets(final GraphicsConfiguration graphicsConfiguration) throws HeadlessException {
        GraphicsEnvironment.checkHeadless();
        if (this != getDefaultToolkit()) {
            return getDefaultToolkit().getScreenInsets(graphicsConfiguration);
        }
        return new Insets(0, 0, 0, 0);
    }
    
    public abstract ColorModel getColorModel() throws HeadlessException;
    
    @Deprecated
    public abstract String[] getFontList();
    
    @Deprecated
    public abstract FontMetrics getFontMetrics(final Font p0);
    
    public abstract void sync();
    
    private static void initAssistiveTechnologies() {
        Toolkit.atNames = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            final /* synthetic */ String val$sep = File.separator;
            final /* synthetic */ Properties val$properties = new Properties();
            
            @Override
            public String run() {
                try {
                    final FileInputStream fileInputStream = new FileInputStream(new File(System.getProperty("user.home") + this.val$sep + ".accessibility.properties"));
                    this.val$properties.load(fileInputStream);
                    fileInputStream.close();
                }
                catch (final Exception ex) {}
                if (this.val$properties.size() == 0) {
                    try {
                        final FileInputStream fileInputStream2 = new FileInputStream(new File(System.getProperty("java.home") + this.val$sep + "lib" + this.val$sep + "accessibility.properties"));
                        this.val$properties.load(fileInputStream2);
                        fileInputStream2.close();
                    }
                    catch (final Exception ex2) {}
                }
                if (System.getProperty("javax.accessibility.screen_magnifier_present") == null) {
                    final String property = this.val$properties.getProperty("screen_magnifier_present", null);
                    if (property != null) {
                        System.setProperty("javax.accessibility.screen_magnifier_present", property);
                    }
                }
                String s = System.getProperty("javax.accessibility.assistive_technologies");
                if (s == null) {
                    s = this.val$properties.getProperty("assistive_technologies", null);
                    if (s != null) {
                        System.setProperty("javax.accessibility.assistive_technologies", s);
                    }
                }
                return s;
            }
        });
    }
    
    private static void loadAssistiveTechnologies() {
        if (Toolkit.atNames != null) {
            final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
            final StringTokenizer stringTokenizer = new StringTokenizer(Toolkit.atNames, " ,");
            while (stringTokenizer.hasMoreTokens()) {
                final String nextToken = stringTokenizer.nextToken();
                try {
                    Class<?> clazz;
                    if (systemClassLoader != null) {
                        clazz = systemClassLoader.loadClass(nextToken);
                    }
                    else {
                        clazz = Class.forName(nextToken);
                    }
                    clazz.newInstance();
                    continue;
                }
                catch (final ClassNotFoundException ex) {
                    throw new AWTError("Assistive Technology not found: " + nextToken);
                }
                catch (final InstantiationException ex2) {
                    throw new AWTError("Could not instantiate Assistive Technology: " + nextToken);
                }
                catch (final IllegalAccessException ex3) {
                    throw new AWTError("Could not access Assistive Technology: " + nextToken);
                }
                catch (final Exception ex4) {
                    throw new AWTError("Error trying to install Assistive Technology: " + nextToken + " " + ex4);
                }
                break;
            }
        }
    }
    
    public static synchronized Toolkit getDefaultToolkit() {
        if (Toolkit.toolkit == null) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    Class<?> clazz = null;
                    final String property = System.getProperty("awt.toolkit");
                    try {
                        clazz = Class.forName(property);
                    }
                    catch (final ClassNotFoundException ex) {
                        final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
                        if (systemClassLoader != null) {
                            try {
                                clazz = systemClassLoader.loadClass(property);
                            }
                            catch (final ClassNotFoundException ex2) {
                                throw new AWTError("Toolkit not found: " + property);
                            }
                        }
                    }
                    try {
                        if (clazz != null) {
                            Toolkit.toolkit = (Toolkit)clazz.newInstance();
                            if (GraphicsEnvironment.isHeadless()) {
                                Toolkit.toolkit = new HeadlessToolkit(Toolkit.toolkit);
                            }
                        }
                    }
                    catch (final InstantiationException ex3) {
                        throw new AWTError("Could not instantiate Toolkit: " + property);
                    }
                    catch (final IllegalAccessException ex4) {
                        throw new AWTError("Could not access Toolkit: " + property);
                    }
                    return null;
                }
            });
            loadAssistiveTechnologies();
        }
        return Toolkit.toolkit;
    }
    
    public abstract Image getImage(final String p0);
    
    public abstract Image getImage(final URL p0);
    
    public abstract Image createImage(final String p0);
    
    public abstract Image createImage(final URL p0);
    
    public abstract boolean prepareImage(final Image p0, final int p1, final int p2, final ImageObserver p3);
    
    public abstract int checkImage(final Image p0, final int p1, final int p2, final ImageObserver p3);
    
    public abstract Image createImage(final ImageProducer p0);
    
    public Image createImage(final byte[] array) {
        return this.createImage(array, 0, array.length);
    }
    
    public abstract Image createImage(final byte[] p0, final int p1, final int p2);
    
    public abstract PrintJob getPrintJob(final Frame p0, final String p1, final Properties p2);
    
    public PrintJob getPrintJob(final Frame frame, final String s, final JobAttributes jobAttributes, final PageAttributes pageAttributes) {
        if (this != getDefaultToolkit()) {
            return getDefaultToolkit().getPrintJob(frame, s, jobAttributes, pageAttributes);
        }
        return this.getPrintJob(frame, s, null);
    }
    
    public abstract void beep();
    
    public abstract Clipboard getSystemClipboard() throws HeadlessException;
    
    public Clipboard getSystemSelection() throws HeadlessException {
        GraphicsEnvironment.checkHeadless();
        if (this != getDefaultToolkit()) {
            return getDefaultToolkit().getSystemSelection();
        }
        GraphicsEnvironment.checkHeadless();
        return null;
    }
    
    public int getMenuShortcutKeyMask() throws HeadlessException {
        GraphicsEnvironment.checkHeadless();
        return 2;
    }
    
    public boolean getLockingKeyState(final int n) throws UnsupportedOperationException {
        GraphicsEnvironment.checkHeadless();
        if (n != 20 && n != 144 && n != 145 && n != 262) {
            throw new IllegalArgumentException("invalid key for Toolkit.getLockingKeyState");
        }
        throw new UnsupportedOperationException("Toolkit.getLockingKeyState");
    }
    
    public void setLockingKeyState(final int n, final boolean b) throws UnsupportedOperationException {
        GraphicsEnvironment.checkHeadless();
        if (n != 20 && n != 144 && n != 145 && n != 262) {
            throw new IllegalArgumentException("invalid key for Toolkit.setLockingKeyState");
        }
        throw new UnsupportedOperationException("Toolkit.setLockingKeyState");
    }
    
    protected static Container getNativeContainer(final Component component) {
        return component.getNativeContainer();
    }
    
    public Cursor createCustomCursor(final Image image, final Point point, final String s) throws IndexOutOfBoundsException, HeadlessException {
        if (this != getDefaultToolkit()) {
            return getDefaultToolkit().createCustomCursor(image, point, s);
        }
        return new Cursor(0);
    }
    
    public Dimension getBestCursorSize(final int n, final int n2) throws HeadlessException {
        GraphicsEnvironment.checkHeadless();
        if (this != getDefaultToolkit()) {
            return getDefaultToolkit().getBestCursorSize(n, n2);
        }
        return new Dimension(0, 0);
    }
    
    public int getMaximumCursorColors() throws HeadlessException {
        GraphicsEnvironment.checkHeadless();
        if (this != getDefaultToolkit()) {
            return getDefaultToolkit().getMaximumCursorColors();
        }
        return 0;
    }
    
    public boolean isFrameStateSupported(final int n) throws HeadlessException {
        GraphicsEnvironment.checkHeadless();
        if (this != getDefaultToolkit()) {
            return getDefaultToolkit().isFrameStateSupported(n);
        }
        return n == 0;
    }
    
    private static void setPlatformResources(final ResourceBundle platformResources) {
        Toolkit.platformResources = platformResources;
    }
    
    private static native void initIDs();
    
    static void loadLibraries() {
        if (!Toolkit.loaded) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    System.loadLibrary("awt");
                    return null;
                }
            });
            Toolkit.loaded = true;
        }
    }
    
    public static String getProperty(final String s, final String s2) {
        if (Toolkit.platformResources != null) {
            try {
                return Toolkit.platformResources.getString(s);
            }
            catch (final MissingResourceException ex) {}
        }
        if (Toolkit.resources != null) {
            try {
                return Toolkit.resources.getString(s);
            }
            catch (final MissingResourceException ex2) {}
        }
        return s2;
    }
    
    public final EventQueue getSystemEventQueue() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.AWT.CHECK_AWT_EVENTQUEUE_PERMISSION);
        }
        return this.getSystemEventQueueImpl();
    }
    
    protected abstract EventQueue getSystemEventQueueImpl();
    
    static EventQueue getEventQueue() {
        return getDefaultToolkit().getSystemEventQueueImpl();
    }
    
    public abstract DragSourceContextPeer createDragSourceContextPeer(final DragGestureEvent p0) throws InvalidDnDOperationException;
    
    public <T extends DragGestureRecognizer> T createDragGestureRecognizer(final Class<T> clazz, final DragSource dragSource, final Component component, final int n, final DragGestureListener dragGestureListener) {
        return null;
    }
    
    public final synchronized Object getDesktopProperty(final String s) {
        if (this instanceof HeadlessToolkit) {
            return ((HeadlessToolkit)this).getUnderlyingToolkit().getDesktopProperty(s);
        }
        if (this.desktopProperties.isEmpty()) {
            this.initializeDesktopProperties();
        }
        if (s.equals("awt.dynamicLayoutSupported")) {
            return getDefaultToolkit().lazilyLoadDesktopProperty(s);
        }
        Object o = this.desktopProperties.get(s);
        if (o == null) {
            o = this.lazilyLoadDesktopProperty(s);
            if (o != null) {
                this.setDesktopProperty(s, o);
            }
        }
        if (o instanceof RenderingHints) {
            o = ((RenderingHints)o).clone();
        }
        return o;
    }
    
    protected final void setDesktopProperty(final String s, final Object o) {
        if (this instanceof HeadlessToolkit) {
            ((HeadlessToolkit)this).getUnderlyingToolkit().setDesktopProperty(s, o);
            return;
        }
        final Object value;
        synchronized (this) {
            value = this.desktopProperties.get(s);
            this.desktopProperties.put(s, o);
        }
        if (value != null || o != null) {
            this.desktopPropsSupport.firePropertyChange(s, value, o);
        }
    }
    
    protected Object lazilyLoadDesktopProperty(final String s) {
        return null;
    }
    
    protected void initializeDesktopProperties() {
    }
    
    public void addPropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        this.desktopPropsSupport.addPropertyChangeListener(s, propertyChangeListener);
    }
    
    public void removePropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        this.desktopPropsSupport.removePropertyChangeListener(s, propertyChangeListener);
    }
    
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return this.desktopPropsSupport.getPropertyChangeListeners();
    }
    
    public PropertyChangeListener[] getPropertyChangeListeners(final String s) {
        return this.desktopPropsSupport.getPropertyChangeListeners(s);
    }
    
    public boolean isAlwaysOnTopSupported() {
        return true;
    }
    
    public abstract boolean isModalityTypeSupported(final Dialog.ModalityType p0);
    
    public abstract boolean isModalExclusionTypeSupported(final Dialog.ModalExclusionType p0);
    
    private static AWTEventListener deProxyAWTEventListener(final AWTEventListener awtEventListener) {
        AWTEventListener awtEventListener2 = awtEventListener;
        if (awtEventListener2 == null) {
            return null;
        }
        if (awtEventListener instanceof AWTEventListenerProxy) {
            awtEventListener2 = ((AWTEventListenerProxy)awtEventListener).getListener();
        }
        return awtEventListener2;
    }
    
    public void addAWTEventListener(final AWTEventListener awtEventListener, final long n) {
        final AWTEventListener deProxyAWTEventListener = deProxyAWTEventListener(awtEventListener);
        if (deProxyAWTEventListener == null) {
            return;
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
        }
        synchronized (this) {
            SelectiveAWTEventListener selectiveAWTEventListener = this.listener2SelectiveListener.get(deProxyAWTEventListener);
            if (selectiveAWTEventListener == null) {
                selectiveAWTEventListener = new SelectiveAWTEventListener(deProxyAWTEventListener, n);
                this.listener2SelectiveListener.put(deProxyAWTEventListener, selectiveAWTEventListener);
                this.eventListener = ToolkitEventMulticaster.add(this.eventListener, selectiveAWTEventListener);
            }
            selectiveAWTEventListener.orEventMasks(n);
            Toolkit.enabledOnToolkitMask |= n;
            long n2 = n;
            for (int n3 = 0; n3 < 64 && n2 != 0L; n2 >>>= 1, ++n3) {
                if ((n2 & 0x1L) != 0x0L) {
                    final int[] calls = this.calls;
                    final int n4 = n3;
                    ++calls[n4];
                }
            }
        }
    }
    
    public void removeAWTEventListener(final AWTEventListener awtEventListener) {
        final AWTEventListener deProxyAWTEventListener = deProxyAWTEventListener(awtEventListener);
        if (awtEventListener == null) {
            return;
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
        }
        synchronized (this) {
            final SelectiveAWTEventListener selectiveAWTEventListener = this.listener2SelectiveListener.get(deProxyAWTEventListener);
            if (selectiveAWTEventListener != null) {
                this.listener2SelectiveListener.remove(deProxyAWTEventListener);
                final int[] calls = selectiveAWTEventListener.getCalls();
                for (int i = 0; i < 64; ++i) {
                    final int[] calls2 = this.calls;
                    final int n = i;
                    calls2[n] -= calls[i];
                    assert this.calls[i] >= 0 : "Negative Listeners count";
                    if (this.calls[i] == 0) {
                        Toolkit.enabledOnToolkitMask &= ~(1L << i);
                    }
                }
            }
            this.eventListener = ToolkitEventMulticaster.remove(this.eventListener, (selectiveAWTEventListener == null) ? deProxyAWTEventListener : selectiveAWTEventListener);
        }
    }
    
    static boolean enabledOnToolkit(final long n) {
        return (Toolkit.enabledOnToolkitMask & n) != 0x0L;
    }
    
    synchronized int countAWTEventListeners(long n) {
        int n2;
        for (n2 = 0; n != 0L; n >>>= 1, ++n2) {}
        --n2;
        return this.calls[n2];
    }
    
    public AWTEventListener[] getAWTEventListeners() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
        }
        synchronized (this) {
            final AWTEventListener[] listeners = AWTEventMulticaster.getListeners(this.eventListener, AWTEventListener.class);
            final AWTEventListener[] array = new AWTEventListener[listeners.length];
            for (int i = 0; i < listeners.length; ++i) {
                final SelectiveAWTEventListener selectiveAWTEventListener = (SelectiveAWTEventListener)listeners[i];
                array[i] = new AWTEventListenerProxy(selectiveAWTEventListener.getEventMask(), selectiveAWTEventListener.getListener());
            }
            return array;
        }
    }
    
    public AWTEventListener[] getAWTEventListeners(final long n) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
        }
        synchronized (this) {
            final AWTEventListener[] listeners = AWTEventMulticaster.getListeners(this.eventListener, AWTEventListener.class);
            final ArrayList list = new ArrayList(listeners.length);
            for (int i = 0; i < listeners.length; ++i) {
                final SelectiveAWTEventListener selectiveAWTEventListener = (SelectiveAWTEventListener)listeners[i];
                if ((selectiveAWTEventListener.getEventMask() & n) == n) {
                    list.add((Object)new AWTEventListenerProxy(selectiveAWTEventListener.getEventMask(), selectiveAWTEventListener.getListener()));
                }
            }
            return (AWTEventListener[])list.toArray((Object[])new AWTEventListener[0]);
        }
    }
    
    void notifyAWTEventListeners(final AWTEvent awtEvent) {
        if (this instanceof HeadlessToolkit) {
            ((HeadlessToolkit)this).getUnderlyingToolkit().notifyAWTEventListeners(awtEvent);
            return;
        }
        final AWTEventListener eventListener = this.eventListener;
        if (eventListener != null) {
            eventListener.eventDispatched(awtEvent);
        }
    }
    
    public abstract Map<TextAttribute, ?> mapInputMethodHighlight(final InputMethodHighlight p0) throws HeadlessException;
    
    private static PropertyChangeSupport createPropertyChangeSupport(final Toolkit toolkit) {
        if (toolkit instanceof SunToolkit || toolkit instanceof HeadlessToolkit) {
            return new DesktopPropertyChangeSupport(toolkit);
        }
        return new PropertyChangeSupport(toolkit);
    }
    
    public boolean areExtraMouseButtonsEnabled() throws HeadlessException {
        GraphicsEnvironment.checkHeadless();
        return getDefaultToolkit().areExtraMouseButtonsEnabled();
    }
    
    static {
        Toolkit.loaded = false;
        AWTAccessor.setToolkitAccessor(new AWTAccessor.ToolkitAccessor() {
            @Override
            public void setPlatformResources(final ResourceBundle resourceBundle) {
                setPlatformResources(resourceBundle);
            }
        });
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                try {
                    Toolkit.resources = ResourceBundle.getBundle("sun.awt.resources.awt", Locale.getDefault(), ClassLoader.getSystemClassLoader(), CoreResourceBundleControl.getRBControlInstance());
                }
                catch (final MissingResourceException ex) {}
                return null;
            }
        });
        loadLibraries();
        initAssistiveTechnologies();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
    }
    
    private static class ToolkitEventMulticaster extends AWTEventMulticaster implements AWTEventListener
    {
        ToolkitEventMulticaster(final AWTEventListener awtEventListener, final AWTEventListener awtEventListener2) {
            super(awtEventListener, awtEventListener2);
        }
        
        static AWTEventListener add(final AWTEventListener awtEventListener, final AWTEventListener awtEventListener2) {
            if (awtEventListener == null) {
                return awtEventListener2;
            }
            if (awtEventListener2 == null) {
                return awtEventListener;
            }
            return new ToolkitEventMulticaster(awtEventListener, awtEventListener2);
        }
        
        static AWTEventListener remove(final AWTEventListener awtEventListener, final AWTEventListener awtEventListener2) {
            return (AWTEventListener)AWTEventMulticaster.removeInternal(awtEventListener, awtEventListener2);
        }
        
        @Override
        protected EventListener remove(final EventListener eventListener) {
            if (eventListener == this.a) {
                return this.b;
            }
            if (eventListener == this.b) {
                return this.a;
            }
            final AWTEventListener awtEventListener = (AWTEventListener)AWTEventMulticaster.removeInternal(this.a, eventListener);
            final AWTEventListener awtEventListener2 = (AWTEventListener)AWTEventMulticaster.removeInternal(this.b, eventListener);
            if (awtEventListener == this.a && awtEventListener2 == this.b) {
                return this;
            }
            return add(awtEventListener, awtEventListener2);
        }
        
        @Override
        public void eventDispatched(final AWTEvent awtEvent) {
            ((AWTEventListener)this.a).eventDispatched(awtEvent);
            ((AWTEventListener)this.b).eventDispatched(awtEvent);
        }
    }
    
    private class SelectiveAWTEventListener implements AWTEventListener
    {
        AWTEventListener listener;
        private long eventMask;
        int[] calls;
        
        public AWTEventListener getListener() {
            return this.listener;
        }
        
        public long getEventMask() {
            return this.eventMask;
        }
        
        public int[] getCalls() {
            return this.calls;
        }
        
        public void orEventMasks(long n) {
            this.eventMask |= n;
            for (int n2 = 0; n2 < 64 && n != 0L; n >>>= 1, ++n2) {
                if ((n & 0x1L) != 0x0L) {
                    final int[] calls = this.calls;
                    final int n3 = n2;
                    ++calls[n3];
                }
            }
        }
        
        SelectiveAWTEventListener(final AWTEventListener listener, final long eventMask) {
            this.calls = new int[64];
            this.listener = listener;
            this.eventMask = eventMask;
        }
        
        @Override
        public void eventDispatched(final AWTEvent awtEvent) {
            long n;
            if (((n = (this.eventMask & 0x1L)) != 0L && awtEvent.id >= 100 && awtEvent.id <= 103) || ((n = (this.eventMask & 0x2L)) != 0L && awtEvent.id >= 300 && awtEvent.id <= 301) || ((n = (this.eventMask & 0x4L)) != 0L && awtEvent.id >= 1004 && awtEvent.id <= 1005) || ((n = (this.eventMask & 0x8L)) != 0L && awtEvent.id >= 400 && awtEvent.id <= 402) || ((n = (this.eventMask & 0x20000L)) != 0L && awtEvent.id == 507) || ((n = (this.eventMask & 0x20L)) != 0L && (awtEvent.id == 503 || awtEvent.id == 506)) || ((n = (this.eventMask & 0x10L)) != 0L && awtEvent.id != 503 && awtEvent.id != 506 && awtEvent.id != 507 && awtEvent.id >= 500 && awtEvent.id <= 507) || ((n = (this.eventMask & 0x40L)) != 0L && awtEvent.id >= 200 && awtEvent.id <= 209) || ((n = (this.eventMask & 0x80L)) != 0L && awtEvent.id >= 1001 && awtEvent.id <= 1001) || ((n = (this.eventMask & 0x100L)) != 0L && awtEvent.id >= 601 && awtEvent.id <= 601) || ((n = (this.eventMask & 0x200L)) != 0L && awtEvent.id >= 701 && awtEvent.id <= 701) || ((n = (this.eventMask & 0x400L)) != 0L && awtEvent.id >= 900 && awtEvent.id <= 900) || ((n = (this.eventMask & 0x800L)) != 0L && awtEvent.id >= 1100 && awtEvent.id <= 1101) || ((n = (this.eventMask & 0x2000L)) != 0L && awtEvent.id >= 800 && awtEvent.id <= 801) || ((n = (this.eventMask & 0x4000L)) != 0L && awtEvent.id >= 1200 && awtEvent.id <= 1200) || ((n = (this.eventMask & 0x8000L)) != 0L && awtEvent.id == 1400) || ((n = (this.eventMask & 0x10000L)) != 0L && (awtEvent.id == 1401 || awtEvent.id == 1402)) || ((n = (this.eventMask & 0x40000L)) != 0L && awtEvent.id == 209) || ((n = (this.eventMask & 0x80000L)) != 0L && (awtEvent.id == 207 || awtEvent.id == 208)) || ((n = (this.eventMask & 0xFFFFFFFF80000000L)) != 0L && awtEvent instanceof UngrabEvent)) {
                int n2 = 0;
                for (long n3 = n; n3 != 0L; n3 >>>= 1, ++n2) {}
                --n2;
                for (int i = 0; i < this.calls[n2]; ++i) {
                    this.listener.eventDispatched(awtEvent);
                }
            }
        }
    }
    
    private static class DesktopPropertyChangeSupport extends PropertyChangeSupport
    {
        private static final StringBuilder PROP_CHANGE_SUPPORT_KEY;
        private final Object source;
        
        public DesktopPropertyChangeSupport(final Object source) {
            super(source);
            this.source = source;
        }
        
        @Override
        public synchronized void addPropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
            PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(DesktopPropertyChangeSupport.PROP_CHANGE_SUPPORT_KEY);
            if (null == propertyChangeSupport) {
                propertyChangeSupport = new PropertyChangeSupport(this.source);
                AppContext.getAppContext().put(DesktopPropertyChangeSupport.PROP_CHANGE_SUPPORT_KEY, propertyChangeSupport);
            }
            propertyChangeSupport.addPropertyChangeListener(s, propertyChangeListener);
        }
        
        @Override
        public synchronized void removePropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
            final PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(DesktopPropertyChangeSupport.PROP_CHANGE_SUPPORT_KEY);
            if (null != propertyChangeSupport) {
                propertyChangeSupport.removePropertyChangeListener(s, propertyChangeListener);
            }
        }
        
        @Override
        public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
            final PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(DesktopPropertyChangeSupport.PROP_CHANGE_SUPPORT_KEY);
            if (null != propertyChangeSupport) {
                return propertyChangeSupport.getPropertyChangeListeners();
            }
            return new PropertyChangeListener[0];
        }
        
        @Override
        public synchronized PropertyChangeListener[] getPropertyChangeListeners(final String s) {
            final PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(DesktopPropertyChangeSupport.PROP_CHANGE_SUPPORT_KEY);
            if (null != propertyChangeSupport) {
                return propertyChangeSupport.getPropertyChangeListeners(s);
            }
            return new PropertyChangeListener[0];
        }
        
        @Override
        public synchronized void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
            PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(DesktopPropertyChangeSupport.PROP_CHANGE_SUPPORT_KEY);
            if (null == propertyChangeSupport) {
                propertyChangeSupport = new PropertyChangeSupport(this.source);
                AppContext.getAppContext().put(DesktopPropertyChangeSupport.PROP_CHANGE_SUPPORT_KEY, propertyChangeSupport);
            }
            propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
        }
        
        @Override
        public synchronized void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
            final PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(DesktopPropertyChangeSupport.PROP_CHANGE_SUPPORT_KEY);
            if (null != propertyChangeSupport) {
                propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
            }
        }
        
        @Override
        public void firePropertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final Object oldValue = propertyChangeEvent.getOldValue();
            final Object newValue = propertyChangeEvent.getNewValue();
            propertyChangeEvent.getPropertyName();
            if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
                return;
            }
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    final PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(DesktopPropertyChangeSupport.PROP_CHANGE_SUPPORT_KEY);
                    if (null != propertyChangeSupport) {
                        propertyChangeSupport.firePropertyChange(propertyChangeEvent);
                    }
                }
            };
            final AppContext appContext = AppContext.getAppContext();
            for (final AppContext appContext2 : AppContext.getAppContexts()) {
                if (null != appContext2) {
                    if (appContext2.isDisposed()) {
                        continue;
                    }
                    if (appContext == appContext2) {
                        runnable.run();
                    }
                    else {
                        SunToolkit.postEvent(appContext2, new PeerEvent(this.source, runnable, 2L));
                    }
                }
            }
        }
        
        static {
            PROP_CHANGE_SUPPORT_KEY = new StringBuilder("desktop property change support key");
        }
    }
}
