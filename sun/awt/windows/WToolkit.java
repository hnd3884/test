package sun.awt.windows;

import java.awt.peer.DesktopPeer;
import java.awt.Desktop;
import java.awt.event.FocusEvent;
import sun.awt.AWTAccessor;
import java.awt.event.MouseEvent;
import java.awt.AWTEvent;
import javax.swing.text.JTextComponent;
import java.awt.TextComponent;
import java.awt.Toolkit;
import java.awt.RenderingHints;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.awt.AWTException;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.dnd.DragGestureEvent;
import java.util.concurrent.Executors;
import java.awt.EventQueue;
import sun.awt.AppContext;
import sun.awt.DisplayChangedListener;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Image;
import java.util.Locale;
import java.awt.font.TextAttribute;
import java.util.Map;
import java.awt.im.InputMethodHighlight;
import java.awt.im.spi.InputMethodDescriptor;
import sun.security.util.SecurityConstants;
import java.awt.datatransfer.Clipboard;
import sun.print.PrintJob2D;
import java.awt.PageAttributes;
import java.awt.JobAttributes;
import java.awt.PrintJob;
import java.util.Properties;
import sun.java2d.d3d.D3DRenderQueue;
import sun.java2d.opengl.OGLRenderQueue;
import sun.font.FontManager;
import sun.font.SunFontManager;
import sun.font.FontManagerFactory;
import java.awt.FontMetrics;
import java.awt.Font;
import sun.awt.Win32GraphicsEnvironment;
import sun.awt.Win32GraphicsDevice;
import java.awt.Insets;
import java.awt.HeadlessException;
import java.awt.peer.KeyboardFocusManagerPeer;
import sun.awt.datatransfer.DataTransferer;
import java.awt.peer.SystemTrayPeer;
import java.awt.SystemTray;
import java.awt.peer.TrayIconPeer;
import java.awt.TrayIcon;
import sun.awt.EmbeddedFrame;
import java.awt.peer.RobotPeer;
import java.awt.GraphicsDevice;
import java.awt.Robot;
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
import java.awt.peer.DialogPeer;
import java.awt.Dialog;
import java.awt.peer.WindowPeer;
import java.awt.Window;
import java.awt.peer.PanelPeer;
import java.awt.Panel;
import java.awt.peer.CanvasPeer;
import java.awt.Canvas;
import sun.awt.LightweightFrame;
import java.awt.peer.FramePeer;
import java.awt.Frame;
import java.awt.peer.ChoicePeer;
import java.awt.Choice;
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
import java.awt.peer.TextFieldPeer;
import java.awt.TextField;
import java.awt.peer.ButtonPeer;
import java.awt.Button;
import sun.misc.ThreadGroupUtils;
import sun.awt.AWTAutoShutdown;
import sun.java2d.DisposerRecord;
import sun.java2d.Disposer;
import sun.misc.PerformanceLogger;
import java.awt.GraphicsEnvironment;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.awt.Component;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.awt.image.ColorModel;
import java.awt.peer.FontPeer;
import java.util.Hashtable;
import java.awt.GraphicsConfiguration;
import sun.util.logging.PlatformLogger;
import sun.awt.SunToolkit;

public final class WToolkit extends SunToolkit implements Runnable
{
    private static final PlatformLogger log;
    public static final String XPSTYLE_THEME_ACTIVE = "win.xpstyle.themeActive";
    static GraphicsConfiguration config;
    WClipboard clipboard;
    private Hashtable<String, FontPeer> cacheFontPeer;
    private WDesktopProperties wprops;
    protected boolean dynamicLayoutSetting;
    private static boolean areExtraMouseButtonsEnabled;
    private static boolean loaded;
    private final Object anchor;
    private boolean inited;
    static ColorModel screenmodel;
    private static ExecutorService displayChangeExecutor;
    private static final String prefix = "DnD.Cursor.";
    private static final String postfix = ".32x32";
    private static final String awtPrefix = "awt.";
    private static final String dndPrefix = "DnD.";
    private static final WeakReference<Component> NULL_COMPONENT_WR;
    private volatile WeakReference<Component> compOnTouchDownEvent;
    private volatile WeakReference<Component> compOnMousePressedEvent;
    
    private static native void initIDs();
    
    public static void loadLibraries() {
        if (!WToolkit.loaded) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    System.loadLibrary("awt");
                    return null;
                }
            });
            WToolkit.loaded = true;
        }
    }
    
    private static native String getWindowsVersion();
    
    private static native void disableCustomPalette();
    
    public static void resetGC() {
        if (GraphicsEnvironment.isHeadless()) {
            WToolkit.config = null;
        }
        else {
            WToolkit.config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        }
    }
    
    public static native boolean embeddedInit();
    
    public static native boolean embeddedDispose();
    
    public native void embeddedEventLoopIdleProcessing();
    
    private static native void postDispose();
    
    private static native boolean startToolkitThread(final Runnable p0, final ThreadGroup p1);
    
    public WToolkit() {
        this.dynamicLayoutSetting = false;
        this.anchor = new Object();
        this.inited = false;
        this.compOnTouchDownEvent = WToolkit.NULL_COMPONENT_WR;
        this.compOnMousePressedEvent = WToolkit.NULL_COMPONENT_WR;
        if (PerformanceLogger.loggingEnabled()) {
            PerformanceLogger.setTime("WToolkit construction");
        }
        Disposer.addRecord(this.anchor, new ToolkitDisposer());
        AWTAutoShutdown.notifyToolkitThreadBusy();
        final ThreadGroup threadGroup = AccessController.doPrivileged(ThreadGroupUtils::getRootThreadGroup);
        if (!startToolkitThread(this, threadGroup)) {
            final Thread thread = new Thread(threadGroup, this, "AWT-Windows");
            thread.setDaemon(true);
            thread.start();
        }
        try {
            synchronized (this) {
                while (!this.inited) {
                    this.wait();
                }
            }
        }
        catch (final InterruptedException ex) {}
        this.setDynamicLayout(true);
        WToolkit.areExtraMouseButtonsEnabled = Boolean.parseBoolean(System.getProperty("sun.awt.enableExtraMouseButtons", "true"));
        System.setProperty("sun.awt.enableExtraMouseButtons", "" + WToolkit.areExtraMouseButtonsEnabled);
        setExtraMouseButtonsEnabledNative(WToolkit.areExtraMouseButtonsEnabled);
    }
    
    private final void registerShutdownHook() {
        AccessController.doPrivileged(() -> {
            final Thread thread = new Thread(ThreadGroupUtils.getRootThreadGroup(), this::shutdown);
            thread.setContextClassLoader(null);
            Runtime.getRuntime().addShutdownHook(thread);
            return null;
        });
    }
    
    @Override
    public void run() {
        AccessController.doPrivileged(() -> {
            Thread.currentThread().setContextClassLoader(null);
            return null;
        });
        Thread.currentThread().setPriority(6);
        final boolean init = this.init();
        if (init) {
            this.registerShutdownHook();
        }
        synchronized (this) {
            this.inited = true;
            this.notifyAll();
        }
        if (init) {
            this.eventLoop();
        }
    }
    
    private native boolean init();
    
    private native void eventLoop();
    
    private native void shutdown();
    
    static native void startSecondaryEventLoop();
    
    static native void quitSecondaryEventLoop();
    
    @Override
    public ButtonPeer createButton(final Button button) {
        final WButtonPeer wButtonPeer = new WButtonPeer(button);
        SunToolkit.targetCreatedPeer(button, wButtonPeer);
        return wButtonPeer;
    }
    
    @Override
    public TextFieldPeer createTextField(final TextField textField) {
        final WTextFieldPeer wTextFieldPeer = new WTextFieldPeer(textField);
        SunToolkit.targetCreatedPeer(textField, wTextFieldPeer);
        return wTextFieldPeer;
    }
    
    @Override
    public LabelPeer createLabel(final Label label) {
        final WLabelPeer wLabelPeer = new WLabelPeer(label);
        SunToolkit.targetCreatedPeer(label, wLabelPeer);
        return wLabelPeer;
    }
    
    @Override
    public ListPeer createList(final List list) {
        final WListPeer wListPeer = new WListPeer(list);
        SunToolkit.targetCreatedPeer(list, wListPeer);
        return wListPeer;
    }
    
    @Override
    public CheckboxPeer createCheckbox(final Checkbox checkbox) {
        final WCheckboxPeer wCheckboxPeer = new WCheckboxPeer(checkbox);
        SunToolkit.targetCreatedPeer(checkbox, wCheckboxPeer);
        return wCheckboxPeer;
    }
    
    @Override
    public ScrollbarPeer createScrollbar(final Scrollbar scrollbar) {
        final WScrollbarPeer wScrollbarPeer = new WScrollbarPeer(scrollbar);
        SunToolkit.targetCreatedPeer(scrollbar, wScrollbarPeer);
        return wScrollbarPeer;
    }
    
    @Override
    public ScrollPanePeer createScrollPane(final ScrollPane scrollPane) {
        final WScrollPanePeer wScrollPanePeer = new WScrollPanePeer(scrollPane);
        SunToolkit.targetCreatedPeer(scrollPane, wScrollPanePeer);
        return wScrollPanePeer;
    }
    
    @Override
    public TextAreaPeer createTextArea(final TextArea textArea) {
        final WTextAreaPeer wTextAreaPeer = new WTextAreaPeer(textArea);
        SunToolkit.targetCreatedPeer(textArea, wTextAreaPeer);
        return wTextAreaPeer;
    }
    
    @Override
    public ChoicePeer createChoice(final Choice choice) {
        final WChoicePeer wChoicePeer = new WChoicePeer(choice);
        SunToolkit.targetCreatedPeer(choice, wChoicePeer);
        return wChoicePeer;
    }
    
    @Override
    public FramePeer createFrame(final Frame frame) {
        final WFramePeer wFramePeer = new WFramePeer(frame);
        SunToolkit.targetCreatedPeer(frame, wFramePeer);
        return wFramePeer;
    }
    
    @Override
    public FramePeer createLightweightFrame(final LightweightFrame lightweightFrame) {
        final WLightweightFramePeer wLightweightFramePeer = new WLightweightFramePeer(lightweightFrame);
        SunToolkit.targetCreatedPeer(lightweightFrame, wLightweightFramePeer);
        return wLightweightFramePeer;
    }
    
    @Override
    public CanvasPeer createCanvas(final Canvas canvas) {
        final WCanvasPeer wCanvasPeer = new WCanvasPeer(canvas);
        SunToolkit.targetCreatedPeer(canvas, wCanvasPeer);
        return wCanvasPeer;
    }
    
    @Override
    public void disableBackgroundErase(final Canvas canvas) {
        final WCanvasPeer wCanvasPeer = (WCanvasPeer)canvas.getPeer();
        if (wCanvasPeer == null) {
            throw new IllegalStateException("Canvas must have a valid peer");
        }
        wCanvasPeer.disableBackgroundErase();
    }
    
    @Override
    public PanelPeer createPanel(final Panel panel) {
        final WPanelPeer wPanelPeer = new WPanelPeer(panel);
        SunToolkit.targetCreatedPeer(panel, wPanelPeer);
        return wPanelPeer;
    }
    
    @Override
    public WindowPeer createWindow(final Window window) {
        final WWindowPeer wWindowPeer = new WWindowPeer(window);
        SunToolkit.targetCreatedPeer(window, wWindowPeer);
        return wWindowPeer;
    }
    
    @Override
    public DialogPeer createDialog(final Dialog dialog) {
        final WDialogPeer wDialogPeer = new WDialogPeer(dialog);
        SunToolkit.targetCreatedPeer(dialog, wDialogPeer);
        return wDialogPeer;
    }
    
    @Override
    public FileDialogPeer createFileDialog(final FileDialog fileDialog) {
        final WFileDialogPeer wFileDialogPeer = new WFileDialogPeer(fileDialog);
        SunToolkit.targetCreatedPeer(fileDialog, wFileDialogPeer);
        return wFileDialogPeer;
    }
    
    @Override
    public MenuBarPeer createMenuBar(final MenuBar menuBar) {
        final WMenuBarPeer wMenuBarPeer = new WMenuBarPeer(menuBar);
        SunToolkit.targetCreatedPeer(menuBar, wMenuBarPeer);
        return wMenuBarPeer;
    }
    
    @Override
    public MenuPeer createMenu(final Menu menu) {
        final WMenuPeer wMenuPeer = new WMenuPeer(menu);
        SunToolkit.targetCreatedPeer(menu, wMenuPeer);
        return wMenuPeer;
    }
    
    @Override
    public PopupMenuPeer createPopupMenu(final PopupMenu popupMenu) {
        final WPopupMenuPeer wPopupMenuPeer = new WPopupMenuPeer(popupMenu);
        SunToolkit.targetCreatedPeer(popupMenu, wPopupMenuPeer);
        return wPopupMenuPeer;
    }
    
    @Override
    public MenuItemPeer createMenuItem(final MenuItem menuItem) {
        final WMenuItemPeer wMenuItemPeer = new WMenuItemPeer(menuItem);
        SunToolkit.targetCreatedPeer(menuItem, wMenuItemPeer);
        return wMenuItemPeer;
    }
    
    @Override
    public CheckboxMenuItemPeer createCheckboxMenuItem(final CheckboxMenuItem checkboxMenuItem) {
        final WCheckboxMenuItemPeer wCheckboxMenuItemPeer = new WCheckboxMenuItemPeer(checkboxMenuItem);
        SunToolkit.targetCreatedPeer(checkboxMenuItem, wCheckboxMenuItemPeer);
        return wCheckboxMenuItemPeer;
    }
    
    @Override
    public RobotPeer createRobot(final Robot robot, final GraphicsDevice graphicsDevice) {
        return new WRobotPeer(graphicsDevice);
    }
    
    public WEmbeddedFramePeer createEmbeddedFrame(final WEmbeddedFrame wEmbeddedFrame) {
        final WEmbeddedFramePeer wEmbeddedFramePeer = new WEmbeddedFramePeer(wEmbeddedFrame);
        SunToolkit.targetCreatedPeer(wEmbeddedFrame, wEmbeddedFramePeer);
        return wEmbeddedFramePeer;
    }
    
    WPrintDialogPeer createWPrintDialog(final WPrintDialog wPrintDialog) {
        final WPrintDialogPeer wPrintDialogPeer = new WPrintDialogPeer(wPrintDialog);
        SunToolkit.targetCreatedPeer(wPrintDialog, wPrintDialogPeer);
        return wPrintDialogPeer;
    }
    
    WPageDialogPeer createWPageDialog(final WPageDialog wPageDialog) {
        final WPageDialogPeer wPageDialogPeer = new WPageDialogPeer(wPageDialog);
        SunToolkit.targetCreatedPeer(wPageDialog, wPageDialogPeer);
        return wPageDialogPeer;
    }
    
    @Override
    public TrayIconPeer createTrayIcon(final TrayIcon trayIcon) {
        final WTrayIconPeer wTrayIconPeer = new WTrayIconPeer(trayIcon);
        SunToolkit.targetCreatedPeer(trayIcon, wTrayIconPeer);
        return wTrayIconPeer;
    }
    
    @Override
    public SystemTrayPeer createSystemTray(final SystemTray systemTray) {
        return new WSystemTrayPeer(systemTray);
    }
    
    @Override
    public boolean isTraySupported() {
        return true;
    }
    
    @Override
    public DataTransferer getDataTransferer() {
        return WDataTransferer.getInstanceImpl();
    }
    
    @Override
    public KeyboardFocusManagerPeer getKeyboardFocusManagerPeer() throws HeadlessException {
        return WKeyboardFocusManagerPeer.getInstance();
    }
    
    private native void setDynamicLayoutNative(final boolean p0);
    
    @Override
    public void setDynamicLayout(final boolean dynamicLayoutSetting) {
        if (dynamicLayoutSetting == this.dynamicLayoutSetting) {
            return;
        }
        this.setDynamicLayoutNative(this.dynamicLayoutSetting = dynamicLayoutSetting);
    }
    
    @Override
    protected boolean isDynamicLayoutSet() {
        return this.dynamicLayoutSetting;
    }
    
    private native boolean isDynamicLayoutSupportedNative();
    
    @Override
    public boolean isDynamicLayoutActive() {
        return this.isDynamicLayoutSet() && this.isDynamicLayoutSupported();
    }
    
    @Override
    public boolean isFrameStateSupported(final int n) {
        switch (n) {
            case 0:
            case 1:
            case 6: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static native ColorModel makeColorModel();
    
    static ColorModel getStaticColorModel() {
        if (GraphicsEnvironment.isHeadless()) {
            throw new IllegalArgumentException();
        }
        if (WToolkit.config == null) {
            resetGC();
        }
        return WToolkit.config.getColorModel();
    }
    
    @Override
    public ColorModel getColorModel() {
        return getStaticColorModel();
    }
    
    @Override
    public Insets getScreenInsets(final GraphicsConfiguration graphicsConfiguration) {
        return this.getScreenInsets(((Win32GraphicsDevice)graphicsConfiguration.getDevice()).getScreen());
    }
    
    @Override
    public int getScreenResolution() {
        return ((Win32GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment()).getXResolution();
    }
    
    @Override
    protected native int getScreenWidth();
    
    @Override
    protected native int getScreenHeight();
    
    private native Insets getScreenInsets(final int p0);
    
    @Override
    public FontMetrics getFontMetrics(final Font font) {
        final FontManager instance = FontManagerFactory.getInstance();
        if (instance instanceof SunFontManager && ((SunFontManager)instance).usePlatformFontMetrics()) {
            return WFontMetrics.getFontMetrics(font);
        }
        return super.getFontMetrics(font);
    }
    
    @Override
    public FontPeer getFontPeer(final String s, final int n) {
        final String lowerCase = s.toLowerCase();
        if (null != this.cacheFontPeer) {
            final FontPeer fontPeer = this.cacheFontPeer.get(lowerCase + n);
            if (null != fontPeer) {
                return fontPeer;
            }
        }
        final WFontPeer wFontPeer = new WFontPeer(s, n);
        if (wFontPeer != null) {
            if (null == this.cacheFontPeer) {
                this.cacheFontPeer = new Hashtable<String, FontPeer>(5, 0.9f);
            }
            if (null != this.cacheFontPeer) {
                this.cacheFontPeer.put(lowerCase + n, wFontPeer);
            }
        }
        return wFontPeer;
    }
    
    private native void nativeSync();
    
    @Override
    public void sync() {
        this.nativeSync();
        OGLRenderQueue.sync();
        D3DRenderQueue.sync();
    }
    
    @Override
    public PrintJob getPrintJob(final Frame frame, final String s, final Properties properties) {
        return this.getPrintJob(frame, s, null, null);
    }
    
    @Override
    public PrintJob getPrintJob(final Frame frame, final String s, final JobAttributes jobAttributes, final PageAttributes pageAttributes) {
        if (frame == null) {
            throw new NullPointerException("frame must not be null");
        }
        PrintJob2D printJob2D = new PrintJob2D(frame, s, jobAttributes, pageAttributes);
        if (!printJob2D.printDialog()) {
            printJob2D = null;
        }
        return printJob2D;
    }
    
    @Override
    public native void beep();
    
    @Override
    public boolean getLockingKeyState(final int n) {
        if (n != 20 && n != 144 && n != 145 && n != 262) {
            throw new IllegalArgumentException("invalid key for Toolkit.getLockingKeyState");
        }
        return this.getLockingKeyStateNative(n);
    }
    
    private native boolean getLockingKeyStateNative(final int p0);
    
    @Override
    public void setLockingKeyState(final int n, final boolean b) {
        if (n != 20 && n != 144 && n != 145 && n != 262) {
            throw new IllegalArgumentException("invalid key for Toolkit.setLockingKeyState");
        }
        this.setLockingKeyStateNative(n, b);
    }
    
    private native void setLockingKeyStateNative(final int p0, final boolean p1);
    
    @Override
    public Clipboard getSystemClipboard() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
        }
        synchronized (this) {
            if (this.clipboard == null) {
                this.clipboard = new WClipboard();
            }
        }
        return this.clipboard;
    }
    
    @Override
    protected native void loadSystemColors(final int[] p0);
    
    public static final Object targetToPeer(final Object o) {
        return SunToolkit.targetToPeer(o);
    }
    
    public static final void targetDisposedPeer(final Object o, final Object o2) {
        SunToolkit.targetDisposedPeer(o, o2);
    }
    
    @Override
    public InputMethodDescriptor getInputMethodAdapterDescriptor() {
        return new WInputMethodDescriptor();
    }
    
    @Override
    public Map<TextAttribute, ?> mapInputMethodHighlight(final InputMethodHighlight inputMethodHighlight) {
        return WInputMethod.mapInputMethodHighlight(inputMethodHighlight);
    }
    
    @Override
    public boolean enableInputMethodsForTextComponent() {
        return true;
    }
    
    @Override
    public Locale getDefaultKeyboardLocale() {
        final Locale nativeLocale = WInputMethod.getNativeLocale();
        if (nativeLocale == null) {
            return super.getDefaultKeyboardLocale();
        }
        return nativeLocale;
    }
    
    @Override
    public Cursor createCustomCursor(final Image image, final Point point, final String s) throws IndexOutOfBoundsException {
        return new WCustomCursor(image, point, s);
    }
    
    @Override
    public Dimension getBestCursorSize(final int n, final int n2) {
        return new Dimension(WCustomCursor.getCursorWidth(), WCustomCursor.getCursorHeight());
    }
    
    @Override
    public native int getMaximumCursorColors();
    
    static void paletteChanged() {
        ((Win32GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment()).paletteChanged();
    }
    
    public static void displayChanged() {
        final Runnable runnable = () -> {
            GraphicsEnvironment.getLocalGraphicsEnvironment();
            final DisplayChangedListener displayChangedListener;
            if (displayChangedListener instanceof DisplayChangedListener) {
                displayChangedListener.displayChanged();
            }
            return;
        };
        if (AppContext.getAppContext() != null) {
            EventQueue.invokeLater(runnable);
        }
        else {
            if (WToolkit.displayChangeExecutor == null) {
                WToolkit.displayChangeExecutor = Executors.newFixedThreadPool(1, runnable2 -> {
                    Executors.defaultThreadFactory().newThread(runnable2);
                    final Thread thread;
                    thread.setDaemon(true);
                    return thread;
                });
            }
            WToolkit.displayChangeExecutor.submit(runnable);
        }
    }
    
    @Override
    public DragSourceContextPeer createDragSourceContextPeer(final DragGestureEvent dragGestureEvent) throws InvalidDnDOperationException {
        final LightweightFrame lightweightFrame = SunToolkit.getLightweightFrame(dragGestureEvent.getComponent());
        if (lightweightFrame != null) {
            return lightweightFrame.createDragSourceContextPeer(dragGestureEvent);
        }
        return WDragSourceContextPeer.createDragSourceContextPeer(dragGestureEvent);
    }
    
    @Override
    public <T extends DragGestureRecognizer> T createDragGestureRecognizer(final Class<T> clazz, final DragSource dragSource, final Component component, final int n, final DragGestureListener dragGestureListener) {
        final LightweightFrame lightweightFrame = SunToolkit.getLightweightFrame(component);
        if (lightweightFrame != null) {
            return lightweightFrame.createDragGestureRecognizer(clazz, dragSource, component, n, dragGestureListener);
        }
        if (MouseDragGestureRecognizer.class.equals(clazz)) {
            return (T)new WMouseDragGestureRecognizer(dragSource, component, n, dragGestureListener);
        }
        return null;
    }
    
    @Override
    protected Object lazilyLoadDesktopProperty(final String s) {
        if (s.startsWith("DnD.Cursor.")) {
            final String string = s.substring("DnD.Cursor.".length(), s.length()) + ".32x32";
            try {
                return Cursor.getSystemCustomCursor(string);
            }
            catch (final AWTException ex) {
                throw new RuntimeException("cannot load system cursor: " + string, ex);
            }
        }
        if (s.equals("awt.dynamicLayoutSupported")) {
            return this.isDynamicLayoutSupported();
        }
        if (WDesktopProperties.isWindowsProperty(s) || s.startsWith("awt.") || s.startsWith("DnD.")) {
            synchronized (this) {
                this.lazilyInitWProps();
                return this.desktopProperties.get(s);
            }
        }
        return super.lazilyLoadDesktopProperty(s);
    }
    
    private synchronized void lazilyInitWProps() {
        if (this.wprops == null) {
            this.wprops = new WDesktopProperties(this);
            this.updateProperties(this.wprops.getProperties());
        }
    }
    
    private synchronized boolean isDynamicLayoutSupported() {
        final boolean dynamicLayoutSupportedNative = this.isDynamicLayoutSupportedNative();
        this.lazilyInitWProps();
        final Boolean b = this.desktopProperties.get("awt.dynamicLayoutSupported");
        if (WToolkit.log.isLoggable(PlatformLogger.Level.FINER)) {
            WToolkit.log.finer("In WTK.isDynamicLayoutSupported()   nativeDynamic == " + dynamicLayoutSupportedNative + "   wprops.dynamic == " + b);
        }
        if (b == null || dynamicLayoutSupportedNative != b) {
            this.windowsSettingChange();
            return dynamicLayoutSupportedNative;
        }
        return b;
    }
    
    private void windowsSettingChange() {
        final Map<String, Object> wProps = this.getWProps();
        if (wProps == null) {
            return;
        }
        this.updateXPStyleEnabled(wProps.get("win.xpstyle.themeActive"));
        if (AppContext.getAppContext() == null) {
            this.updateProperties(wProps);
        }
        else {
            EventQueue.invokeLater(() -> this.updateProperties(map));
        }
    }
    
    private synchronized void updateProperties(final Map<String, Object> map) {
        if (null == map) {
            return;
        }
        this.updateXPStyleEnabled(map.get("win.xpstyle.themeActive"));
        for (final String s : map.keySet()) {
            final Object value = map.get(s);
            if (WToolkit.log.isLoggable(PlatformLogger.Level.FINER)) {
                WToolkit.log.finer("changed " + s + " to " + value);
            }
            this.setDesktopProperty(s, value);
        }
    }
    
    private synchronized Map<String, Object> getWProps() {
        return (this.wprops != null) ? this.wprops.getProperties() : null;
    }
    
    private void updateXPStyleEnabled(final Object o) {
        ThemeReader.xpStyleEnabled = Boolean.TRUE.equals(o);
    }
    
    @Override
    public synchronized void addPropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        if (s == null) {
            return;
        }
        if (WDesktopProperties.isWindowsProperty(s) || s.startsWith("awt.") || s.startsWith("DnD.")) {
            this.lazilyInitWProps();
        }
        super.addPropertyChangeListener(s, propertyChangeListener);
    }
    
    @Override
    protected synchronized void initializeDesktopProperties() {
        this.desktopProperties.put("DnD.Autoscroll.initialDelay", 50);
        this.desktopProperties.put("DnD.Autoscroll.interval", 50);
        this.desktopProperties.put("DnD.isDragImageSupported", Boolean.TRUE);
        this.desktopProperties.put("Shell.shellFolderManager", "sun.awt.shell.Win32ShellFolderManager2");
    }
    
    @Override
    protected synchronized RenderingHints getDesktopAAHints() {
        if (this.wprops == null) {
            return null;
        }
        return this.wprops.getDesktopAAHints();
    }
    
    @Override
    public boolean isModalityTypeSupported(final Dialog.ModalityType modalityType) {
        return modalityType == null || modalityType == Dialog.ModalityType.MODELESS || modalityType == Dialog.ModalityType.DOCUMENT_MODAL || modalityType == Dialog.ModalityType.APPLICATION_MODAL || modalityType == Dialog.ModalityType.TOOLKIT_MODAL;
    }
    
    @Override
    public boolean isModalExclusionTypeSupported(final Dialog.ModalExclusionType modalExclusionType) {
        return modalExclusionType == null || modalExclusionType == Dialog.ModalExclusionType.NO_EXCLUDE || modalExclusionType == Dialog.ModalExclusionType.APPLICATION_EXCLUDE || modalExclusionType == Dialog.ModalExclusionType.TOOLKIT_EXCLUDE;
    }
    
    public static WToolkit getWToolkit() {
        return (WToolkit)Toolkit.getDefaultToolkit();
    }
    
    @Override
    public boolean useBufferPerWindow() {
        return !Win32GraphicsEnvironment.isDWMCompositionEnabled();
    }
    
    @Override
    public void grab(final Window window) {
        if (window.getPeer() != null) {
            ((WWindowPeer)window.getPeer()).grab();
        }
    }
    
    @Override
    public void ungrab(final Window window) {
        if (window.getPeer() != null) {
            ((WWindowPeer)window.getPeer()).ungrab();
        }
    }
    
    private boolean isComponentValidForTouchKeyboard(final Component component) {
        return component != null && component.isEnabled() && component.isFocusable() && ((component instanceof TextComponent && ((TextComponent)component).isEditable()) || (component instanceof JTextComponent && ((JTextComponent)component).isEditable()));
    }
    
    @Override
    public void showOrHideTouchKeyboard(final Component component, final AWTEvent awtEvent) {
        if (!(component instanceof TextComponent) && !(component instanceof JTextComponent)) {
            return;
        }
        if (awtEvent instanceof MouseEvent && this.isComponentValidForTouchKeyboard(component)) {
            final MouseEvent mouseEvent = (MouseEvent)awtEvent;
            if (mouseEvent.getID() == 501) {
                if (AWTAccessor.getMouseEventAccessor().isCausedByTouchEvent(mouseEvent)) {
                    this.compOnTouchDownEvent = new WeakReference<Component>(component);
                }
                else {
                    this.compOnMousePressedEvent = new WeakReference<Component>(component);
                }
            }
            else if (mouseEvent.getID() == 502) {
                if (AWTAccessor.getMouseEventAccessor().isCausedByTouchEvent(mouseEvent)) {
                    if (this.compOnTouchDownEvent.get() == component) {
                        this.showTouchKeyboard(true);
                    }
                    this.compOnTouchDownEvent = WToolkit.NULL_COMPONENT_WR;
                }
                else {
                    if (this.compOnMousePressedEvent.get() == component) {
                        this.showTouchKeyboard(false);
                    }
                    this.compOnMousePressedEvent = WToolkit.NULL_COMPONENT_WR;
                }
            }
        }
        else if (awtEvent instanceof FocusEvent) {
            final FocusEvent focusEvent = (FocusEvent)awtEvent;
            if (focusEvent.getID() == 1005 && !this.isComponentValidForTouchKeyboard(focusEvent.getOppositeComponent())) {
                this.hideTouchKeyboard();
            }
        }
    }
    
    private native void showTouchKeyboard(final boolean p0);
    
    private native void hideTouchKeyboard();
    
    public native boolean syncNativeQueue(final long p0);
    
    @Override
    public boolean isDesktopSupported() {
        return true;
    }
    
    public DesktopPeer createDesktopPeer(final Desktop desktop) {
        return new WDesktopPeer();
    }
    
    private static native void setExtraMouseButtonsEnabledNative(final boolean p0);
    
    @Override
    public boolean areExtraMouseButtonsEnabled() throws HeadlessException {
        return WToolkit.areExtraMouseButtonsEnabled;
    }
    
    private synchronized native int getNumberOfButtonsImpl();
    
    @Override
    public int getNumberOfButtons() {
        if (WToolkit.numberOfButtons == 0) {
            WToolkit.numberOfButtons = this.getNumberOfButtonsImpl();
        }
        return (WToolkit.numberOfButtons > 20) ? 20 : WToolkit.numberOfButtons;
    }
    
    @Override
    public boolean isWindowOpacitySupported() {
        return true;
    }
    
    @Override
    public boolean isWindowShapingSupported() {
        return true;
    }
    
    @Override
    public boolean isWindowTranslucencySupported() {
        return true;
    }
    
    @Override
    public boolean isTranslucencyCapable(final GraphicsConfiguration graphicsConfiguration) {
        return true;
    }
    
    @Override
    public boolean needUpdateWindow() {
        return true;
    }
    
    static {
        log = PlatformLogger.getLogger("sun.awt.windows.WToolkit");
        WToolkit.areExtraMouseButtonsEnabled = true;
        WToolkit.loaded = false;
        loadLibraries();
        initIDs();
        if (WToolkit.log.isLoggable(PlatformLogger.Level.FINE)) {
            WToolkit.log.fine("Win version: " + getWindowsVersion());
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                final String property = System.getProperty("browser");
                if (property != null && property.equals("sun.plugin")) {
                    disableCustomPalette();
                }
                return null;
            }
        });
        NULL_COMPONENT_WR = new WeakReference<Component>(null);
    }
    
    static class ToolkitDisposer implements DisposerRecord
    {
        @Override
        public void dispose() {
            postDispose();
        }
    }
}
