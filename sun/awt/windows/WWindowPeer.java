package sun.awt.windows;

import java.beans.PropertyChangeEvent;
import java.awt.KeyboardFocusManager;
import java.util.LinkedList;
import sun.awt.Win32GraphicsEnvironment;
import java.awt.Color;
import java.awt.peer.ComponentPeer;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsConfiguration;
import sun.awt.AWTAccessor;
import sun.awt.Win32GraphicsConfig;
import java.awt.GraphicsEnvironment;
import java.awt.Dialog;
import java.awt.image.DataBufferInt;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.AWTEventMulticaster;
import java.awt.AWTEvent;
import java.awt.event.WindowEvent;
import sun.awt.CausedFocusEvent;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import sun.java2d.pipe.Region;
import java.awt.Component;
import sun.awt.AppContext;
import sun.awt.Win32GraphicsDevice;
import java.util.List;
import sun.awt.SunToolkit;
import java.awt.Window;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import sun.util.logging.PlatformLogger;
import sun.awt.DisplayChangedListener;
import java.awt.peer.WindowPeer;

public class WWindowPeer extends WPanelPeer implements WindowPeer, DisplayChangedListener
{
    private static final PlatformLogger log;
    private static final PlatformLogger screenLog;
    private WWindowPeer modalBlocker;
    private boolean isOpaque;
    private TranslucentWindowPainter painter;
    private static final StringBuffer ACTIVE_WINDOWS_KEY;
    private static PropertyChangeListener activeWindowListener;
    private static final PropertyChangeListener guiDisposedListener;
    private WindowListener windowListener;
    private volatile Window.Type windowType;
    private volatile int sysX;
    private volatile int sysY;
    private volatile int sysW;
    private volatile int sysH;
    private float opacity;
    
    private static native void initIDs();
    
    @Override
    protected void disposeImpl() {
        final AppContext targetToAppContext = SunToolkit.targetToAppContext(this.target);
        synchronized (targetToAppContext) {
            final List list = (List)targetToAppContext.get(WWindowPeer.ACTIVE_WINDOWS_KEY);
            if (list != null) {
                list.remove(this);
            }
        }
        ((Win32GraphicsDevice)this.getGraphicsConfiguration().getDevice()).removeDisplayChangedListener(this);
        synchronized (this.getStateLock()) {
            final TranslucentWindowPainter painter = this.painter;
            if (painter != null) {
                painter.flush();
            }
        }
        super.disposeImpl();
    }
    
    @Override
    public void toFront() {
        this.updateFocusableWindowState();
        this._toFront();
    }
    
    private native void _toFront();
    
    @Override
    public native void toBack();
    
    private native void setAlwaysOnTopNative(final boolean p0);
    
    public void setAlwaysOnTop(final boolean alwaysOnTopNative) {
        if ((alwaysOnTopNative && ((Window)this.target).isVisible()) || !alwaysOnTopNative) {
            this.setAlwaysOnTopNative(alwaysOnTopNative);
        }
    }
    
    @Override
    public void updateAlwaysOnTopState() {
        this.setAlwaysOnTop(((Window)this.target).isAlwaysOnTop());
    }
    
    @Override
    public void updateFocusableWindowState() {
        this.setFocusableWindow(((Window)this.target).isFocusableWindow());
    }
    
    native void setFocusableWindow(final boolean p0);
    
    public void setTitle(String s) {
        if (s == null) {
            s = "";
        }
        this._setTitle(s);
    }
    
    private native void _setTitle(final String p0);
    
    public void setResizable(final boolean b) {
        this._setResizable(b);
    }
    
    private native void _setResizable(final boolean p0);
    
    WWindowPeer(final Window window) {
        super(window);
        this.modalBlocker = null;
        this.windowType = Window.Type.NORMAL;
        this.sysX = 0;
        this.sysY = 0;
        this.sysW = 0;
        this.sysH = 0;
        this.opacity = 1.0f;
    }
    
    @Override
    void initialize() {
        super.initialize();
        this.updateInsets(this.insets_);
        if (((Window)this.target).getFont() == null) {
            final Font defaultFont = WWindowPeer.defaultFont;
            ((Window)this.target).setFont(defaultFont);
            this.setFont(defaultFont);
        }
        ((Win32GraphicsDevice)this.getGraphicsConfiguration().getDevice()).addDisplayChangedListener(this);
        initActiveWindowsTracking((Window)this.target);
        this.updateIconImages();
        final Shape shape = ((Window)this.target).getShape();
        if (shape != null) {
            this.applyShape(Region.getInstance(shape, null));
        }
        final float opacity = ((Window)this.target).getOpacity();
        if (opacity < 1.0f) {
            this.setOpacity(opacity);
        }
        synchronized (this.getStateLock()) {
            this.isOpaque = true;
            this.setOpaque(((Window)this.target).isOpaque());
        }
    }
    
    native void createAwtWindow(final WComponentPeer p0);
    
    void preCreate(final WComponentPeer wComponentPeer) {
        this.windowType = ((Window)this.target).getType();
    }
    
    @Override
    void create(final WComponentPeer wComponentPeer) {
        this.preCreate(wComponentPeer);
        this.createAwtWindow(wComponentPeer);
    }
    
    @Override
    final WComponentPeer getNativeParent() {
        return (WComponentPeer)WToolkit.targetToPeer(((Window)this.target).getOwner());
    }
    
    protected void realShow() {
        super.show();
    }
    
    @Override
    public void show() {
        this.updateFocusableWindowState();
        final boolean alwaysOnTop = ((Window)this.target).isAlwaysOnTop();
        this.updateGC();
        this.realShow();
        this.updateMinimumSize();
        if (((Window)this.target).isAlwaysOnTopSupported() && alwaysOnTop) {
            this.setAlwaysOnTop(alwaysOnTop);
        }
        synchronized (this.getStateLock()) {
            if (!this.isOpaque) {
                this.updateWindow(true);
            }
        }
        final WComponentPeer nativeParent = this.getNativeParent();
        if (nativeParent != null && nativeParent.isLightweightFramePeer()) {
            final Rectangle bounds = this.getBounds();
            this.handleExpose(0, 0, bounds.width, bounds.height);
        }
    }
    
    native void updateInsets(final Insets p0);
    
    static native int getSysMinWidth();
    
    static native int getSysMinHeight();
    
    static native int getSysIconWidth();
    
    static native int getSysIconHeight();
    
    static native int getSysSmIconWidth();
    
    static native int getSysSmIconHeight();
    
    native void setIconImagesData(final int[] p0, final int p1, final int p2, final int[] p3, final int p4, final int p5);
    
    synchronized native void reshapeFrame(final int p0, final int p1, final int p2, final int p3);
    
    public boolean requestWindowFocus(final CausedFocusEvent.Cause cause) {
        return this.focusAllowedFor() && this.requestWindowFocus(cause == CausedFocusEvent.Cause.MOUSE_EVENT);
    }
    
    private native boolean requestWindowFocus(final boolean p0);
    
    public boolean focusAllowedFor() {
        final Window window = (Window)this.target;
        return window.isVisible() && window.isEnabled() && window.isFocusableWindow() && !this.isModalBlocked();
    }
    
    @Override
    void hide() {
        final WindowListener windowListener = this.windowListener;
        if (windowListener != null) {
            windowListener.windowClosing(new WindowEvent((Window)this.target, 201));
        }
        super.hide();
    }
    
    @Override
    void preprocessPostEvent(final AWTEvent awtEvent) {
        if (awtEvent instanceof WindowEvent) {
            final WindowListener windowListener = this.windowListener;
            if (windowListener != null) {
                switch (awtEvent.getID()) {
                    case 201: {
                        windowListener.windowClosing((WindowEvent)awtEvent);
                        break;
                    }
                    case 203: {
                        windowListener.windowIconified((WindowEvent)awtEvent);
                        break;
                    }
                }
            }
        }
    }
    
    synchronized void addWindowListener(final WindowListener windowListener) {
        this.windowListener = AWTEventMulticaster.add(this.windowListener, windowListener);
    }
    
    synchronized void removeWindowListener(final WindowListener windowListener) {
        this.windowListener = AWTEventMulticaster.remove(this.windowListener, windowListener);
    }
    
    @Override
    public void updateMinimumSize() {
        Dimension minimumSize = null;
        if (((Component)this.target).isMinimumSizeSet()) {
            minimumSize = ((Component)this.target).getMinimumSize();
        }
        if (minimumSize != null) {
            final int sysMinWidth = getSysMinWidth();
            final int sysMinHeight = getSysMinHeight();
            this.setMinSize((minimumSize.width >= sysMinWidth) ? minimumSize.width : sysMinWidth, (minimumSize.height >= sysMinHeight) ? minimumSize.height : sysMinHeight);
        }
        else {
            this.setMinSize(0, 0);
        }
    }
    
    @Override
    public void updateIconImages() {
        final List<Image> iconImages = ((Window)this.target).getIconImages();
        if (iconImages == null || iconImages.size() == 0) {
            this.setIconImagesData(null, 0, 0, null, 0, 0);
        }
        else {
            final int sysIconWidth = getSysIconWidth();
            final int sysIconHeight = getSysIconHeight();
            final int sysSmIconWidth = getSysSmIconWidth();
            final int sysSmIconHeight = getSysSmIconHeight();
            final DataBufferInt scaledIconData = SunToolkit.getScaledIconData(iconImages, sysIconWidth, sysIconHeight);
            final DataBufferInt scaledIconData2 = SunToolkit.getScaledIconData(iconImages, sysSmIconWidth, sysSmIconHeight);
            if (scaledIconData != null && scaledIconData2 != null) {
                this.setIconImagesData(scaledIconData.getData(), sysIconWidth, sysIconHeight, scaledIconData2.getData(), sysSmIconWidth, sysSmIconHeight);
            }
            else {
                this.setIconImagesData(null, 0, 0, null, 0, 0);
            }
        }
    }
    
    native void setMinSize(final int p0, final int p1);
    
    public boolean isModalBlocked() {
        return this.modalBlocker != null;
    }
    
    @Override
    public void setModalBlocked(final Dialog dialog, final boolean b) {
        synchronized (((Component)this.getTarget()).getTreeLock()) {
            final WWindowPeer modalBlocker = (WWindowPeer)dialog.getPeer();
            if (b) {
                this.modalBlocker = modalBlocker;
                if (modalBlocker instanceof WFileDialogPeer) {
                    ((WFileDialogPeer)modalBlocker).blockWindow(this);
                }
                else if (modalBlocker instanceof WPrintDialogPeer) {
                    ((WPrintDialogPeer)modalBlocker).blockWindow(this);
                }
                else {
                    this.modalDisable(dialog, modalBlocker.getHWnd());
                }
            }
            else {
                this.modalBlocker = null;
                if (modalBlocker instanceof WFileDialogPeer) {
                    ((WFileDialogPeer)modalBlocker).unblockWindow(this);
                }
                else if (modalBlocker instanceof WPrintDialogPeer) {
                    ((WPrintDialogPeer)modalBlocker).unblockWindow(this);
                }
                else {
                    this.modalEnable(dialog);
                }
            }
        }
    }
    
    native void modalDisable(final Dialog p0, final long p1);
    
    native void modalEnable(final Dialog p0);
    
    public static long[] getActiveWindowHandles(final Component component) {
        final AppContext targetToAppContext = SunToolkit.targetToAppContext(component);
        if (targetToAppContext == null) {
            return null;
        }
        synchronized (targetToAppContext) {
            final List list = (List)targetToAppContext.get(WWindowPeer.ACTIVE_WINDOWS_KEY);
            if (list == null) {
                return null;
            }
            final long[] array = new long[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((WWindowPeer)list.get(i)).getHWnd();
            }
            return array;
        }
    }
    
    void draggedToNewScreen() {
        this.displayChanged();
    }
    
    public void updateGC() {
        final int screenImOn = this.getScreenImOn();
        if (WWindowPeer.screenLog.isLoggable(PlatformLogger.Level.FINER)) {
            WWindowPeer.log.finer("Screen number: " + screenImOn);
        }
        final Win32GraphicsDevice win32GraphicsDevice = (Win32GraphicsDevice)this.winGraphicsConfig.getDevice();
        final GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        Win32GraphicsDevice win32GraphicsDevice2;
        if (screenImOn >= screenDevices.length) {
            win32GraphicsDevice2 = (Win32GraphicsDevice)GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        }
        else {
            win32GraphicsDevice2 = (Win32GraphicsDevice)screenDevices[screenImOn];
        }
        this.winGraphicsConfig = (Win32GraphicsConfig)win32GraphicsDevice2.getDefaultConfiguration();
        if (WWindowPeer.screenLog.isLoggable(PlatformLogger.Level.FINE) && this.winGraphicsConfig == null) {
            WWindowPeer.screenLog.fine("Assertion (winGraphicsConfig != null) failed");
        }
        if (win32GraphicsDevice != win32GraphicsDevice2) {
            win32GraphicsDevice.removeDisplayChangedListener(this);
            win32GraphicsDevice2.addDisplayChangedListener(this);
        }
        AWTAccessor.getComponentAccessor().setGraphicsConfiguration((Component)this.target, this.winGraphicsConfig);
    }
    
    @Override
    public void displayChanged() {
        SunToolkit.executeOnEventHandlerThread(this.target, this::updateGC);
    }
    
    @Override
    public void paletteChanged() {
    }
    
    private native int getScreenImOn();
    
    public final native void setFullScreenExclusiveModeState(final boolean p0);
    
    public void grab() {
        this.nativeGrab();
    }
    
    public void ungrab() {
        this.nativeUngrab();
    }
    
    private native void nativeGrab();
    
    private native void nativeUngrab();
    
    private final boolean hasWarningWindow() {
        return ((Window)this.target).getWarningString() != null;
    }
    
    boolean isTargetUndecorated() {
        return true;
    }
    
    @Override
    public native void repositionSecurityWarning();
    
    @Override
    public void setBounds(final int sysX, final int sysY, final int sysW, final int sysH, final int n) {
        super.setBounds(this.sysX = sysX, this.sysY = sysY, this.sysW = sysW, this.sysH = sysH, n);
    }
    
    @Override
    public void print(final Graphics graphics) {
        final Shape shape = AWTAccessor.getWindowAccessor().getShape((Window)this.target);
        if (shape != null) {
            graphics.setClip(shape);
        }
        super.print(graphics);
    }
    
    private void replaceSurfaceDataRecursively(final Component component) {
        if (component instanceof Container) {
            final Component[] components = ((Container)component).getComponents();
            for (int length = components.length, i = 0; i < length; ++i) {
                this.replaceSurfaceDataRecursively(components[i]);
            }
        }
        final ComponentPeer peer = component.getPeer();
        if (peer instanceof WComponentPeer) {
            ((WComponentPeer)peer).replaceSurfaceDataLater();
        }
    }
    
    public final Graphics getTranslucentGraphics() {
        synchronized (this.getStateLock()) {
            return this.isOpaque ? null : this.painter.getBackBuffer(false).getGraphics();
        }
    }
    
    @Override
    public void setBackground(final Color background) {
        super.setBackground(background);
        synchronized (this.getStateLock()) {
            if (!this.isOpaque && ((Window)this.target).isVisible()) {
                this.updateWindow(true);
            }
        }
    }
    
    private native void setOpacity(final int p0);
    
    @Override
    public void setOpacity(final float opacity) {
        if (!((SunToolkit)((Window)this.target).getToolkit()).isWindowOpacitySupported()) {
            return;
        }
        if (opacity < 0.0f || opacity > 1.0f) {
            throw new IllegalArgumentException("The value of opacity should be in the range [0.0f .. 1.0f].");
        }
        if (((this.opacity == 1.0f && opacity < 1.0f) || (this.opacity < 1.0f && opacity == 1.0f)) && !Win32GraphicsEnvironment.isVistaOS()) {
            this.replaceSurfaceDataRecursively((Component)this.getTarget());
        }
        this.opacity = opacity;
        int opacity2 = (int)(opacity * 255.0f);
        if (opacity2 < 0) {
            opacity2 = 0;
        }
        if (opacity2 > 255) {
            opacity2 = 255;
        }
        this.setOpacity(opacity2);
        synchronized (this.getStateLock()) {
            if (!this.isOpaque && ((Window)this.target).isVisible()) {
                this.updateWindow(true);
            }
        }
    }
    
    private native void setOpaqueImpl(final boolean p0);
    
    @Override
    public void setOpaque(final boolean isOpaque) {
        synchronized (this.getStateLock()) {
            if (this.isOpaque == isOpaque) {
                return;
            }
        }
        final Window window = (Window)this.getTarget();
        if (!isOpaque) {
            final SunToolkit sunToolkit = (SunToolkit)window.getToolkit();
            if (!sunToolkit.isWindowTranslucencySupported() || !sunToolkit.isTranslucencyCapable(window.getGraphicsConfiguration())) {
                return;
            }
        }
        final boolean vistaOS = Win32GraphicsEnvironment.isVistaOS();
        if (this.isOpaque != isOpaque && !vistaOS) {
            this.replaceSurfaceDataRecursively(window);
        }
        synchronized (this.getStateLock()) {
            this.setOpaqueImpl(this.isOpaque = isOpaque);
            if (isOpaque) {
                final TranslucentWindowPainter painter = this.painter;
                if (painter != null) {
                    painter.flush();
                    this.painter = null;
                }
            }
            else {
                this.painter = TranslucentWindowPainter.createInstance(this);
            }
        }
        if (vistaOS) {
            final Shape shape = window.getShape();
            if (shape != null) {
                window.setShape(shape);
            }
        }
        if (window.isVisible()) {
            this.updateWindow(true);
        }
    }
    
    native void updateWindowImpl(final int[] p0, final int p1, final int p2);
    
    @Override
    public void updateWindow() {
        this.updateWindow(false);
    }
    
    private void updateWindow(final boolean b) {
        final Window window = (Window)this.target;
        synchronized (this.getStateLock()) {
            if (this.isOpaque || !window.isVisible() || window.getWidth() <= 0 || window.getHeight() <= 0) {
                return;
            }
            final TranslucentWindowPainter painter = this.painter;
            if (painter != null) {
                painter.updateWindow(b);
            }
            else if (WWindowPeer.log.isLoggable(PlatformLogger.Level.FINER)) {
                WWindowPeer.log.finer("Translucent window painter is null in updateWindow");
            }
        }
    }
    
    private static void initActiveWindowsTracking(final Window window) {
        final AppContext appContext = AppContext.getAppContext();
        synchronized (appContext) {
            if (appContext.get(WWindowPeer.ACTIVE_WINDOWS_KEY) == null) {
                appContext.put(WWindowPeer.ACTIVE_WINDOWS_KEY, new LinkedList());
                appContext.addPropertyChangeListener("guidisposed", WWindowPeer.guiDisposedListener);
                KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("activeWindow", WWindowPeer.activeWindowListener);
            }
        }
    }
    
    static {
        log = PlatformLogger.getLogger("sun.awt.windows.WWindowPeer");
        screenLog = PlatformLogger.getLogger("sun.awt.windows.screen.WWindowPeer");
        ACTIVE_WINDOWS_KEY = new StringBuffer("active_windows_list");
        WWindowPeer.activeWindowListener = new ActiveWindowListener();
        guiDisposedListener = new GuiDisposedListener();
        initIDs();
    }
    
    private static class GuiDisposedListener implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (!(boolean)propertyChangeEvent.getNewValue() && WWindowPeer.log.isLoggable(PlatformLogger.Level.FINE)) {
                WWindowPeer.log.fine(" Assertion (newValue != true) failed for AppContext.GUI_DISPOSED ");
            }
            final AppContext appContext = AppContext.getAppContext();
            synchronized (appContext) {
                appContext.remove(WWindowPeer.ACTIVE_WINDOWS_KEY);
                appContext.removePropertyChangeListener("guidisposed", this);
                KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("activeWindow", WWindowPeer.activeWindowListener);
            }
        }
    }
    
    private static class ActiveWindowListener implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final Window window = (Window)propertyChangeEvent.getNewValue();
            if (window == null) {
                return;
            }
            final AppContext targetToAppContext = SunToolkit.targetToAppContext(window);
            synchronized (targetToAppContext) {
                final WWindowPeer wWindowPeer = (WWindowPeer)window.getPeer();
                final List list = (List)targetToAppContext.get(WWindowPeer.ACTIVE_WINDOWS_KEY);
                if (list != null) {
                    list.remove(wWindowPeer);
                    list.add(wWindowPeer);
                }
            }
        }
    }
}
