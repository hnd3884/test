package sun.awt.windows;

import sun.java2d.pipe.Region;
import sun.awt.Win32GraphicsEnvironment;
import java.awt.peer.ContainerPeer;
import java.awt.AWTException;
import java.awt.dnd.DropTarget;
import sun.awt.AWTAccessor;
import java.awt.Toolkit;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.ToolkitImage;
import java.awt.image.ImageProducer;
import java.awt.FontMetrics;
import java.awt.Window;
import java.awt.SystemColor;
import java.awt.image.ColorModel;
import java.awt.event.InvocationEvent;
import sun.awt.PaintEventDispatcher;
import sun.java2d.ScreenUpdateManager;
import java.awt.GraphicsConfiguration;
import java.awt.event.FocusEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.InputEvent;
import java.awt.AWTEvent;
import sun.awt.CausedFocusEvent;
import sun.awt.KeyboardFocusManagerPeerImpl;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;
import sun.awt.event.IgnorePaintEvent;
import java.awt.event.PaintEvent;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import sun.java2d.opengl.OGLSurfaceData;
import sun.java2d.d3d.D3DSurfaceData;
import java.awt.Container;
import sun.awt.SunToolkit;
import sun.java2d.InvalidPipeException;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Point;
import java.awt.Font;
import java.awt.Color;
import java.awt.BufferCapabilities;
import java.awt.image.VolatileImage;
import sun.awt.Win32GraphicsConfig;
import sun.awt.RepaintArea;
import sun.java2d.SurfaceData;
import sun.util.logging.PlatformLogger;
import java.awt.dnd.peer.DropTargetPeer;
import java.awt.peer.ComponentPeer;

public abstract class WComponentPeer extends WObjectPeer implements ComponentPeer, DropTargetPeer
{
    protected volatile long hwnd;
    private static final PlatformLogger log;
    private static final PlatformLogger shapeLog;
    private static final PlatformLogger focusLog;
    SurfaceData surfaceData;
    private RepaintArea paintArea;
    protected Win32GraphicsConfig winGraphicsConfig;
    boolean isLayouting;
    boolean paintPending;
    int oldWidth;
    int oldHeight;
    private int numBackBuffers;
    private VolatileImage backBuffer;
    private BufferCapabilities backBufferCaps;
    private Color foreground;
    private Color background;
    private Font font;
    int nDropTargets;
    long nativeDropTargetContext;
    public int serialNum;
    private static final double BANDING_DIVISOR = 4.0;
    static final Font defaultFont;
    private int updateX1;
    private int updateY1;
    private int updateX2;
    private int updateY2;
    private volatile boolean isAccelCapable;
    
    @Override
    public native boolean isObscured();
    
    @Override
    public boolean canDetermineObscurity() {
        return true;
    }
    
    private synchronized native void pShow();
    
    synchronized native void hide();
    
    synchronized native void enable();
    
    synchronized native void disable();
    
    public long getHWnd() {
        return this.hwnd;
    }
    
    @Override
    public native Point getLocationOnScreen();
    
    @Override
    public void setVisible(final boolean b) {
        if (b) {
            this.show();
        }
        else {
            this.hide();
        }
    }
    
    public void show() {
        final Dimension size = ((Component)this.target).getSize();
        this.oldHeight = size.height;
        this.oldWidth = size.width;
        this.pShow();
    }
    
    @Override
    public void setEnabled(final boolean b) {
        if (b) {
            this.enable();
        }
        else {
            this.disable();
        }
    }
    
    private native void reshapeNoCheck(final int p0, final int p1, final int p2, final int p3);
    
    @Override
    public void setBounds(final int n, final int n2, final int oldWidth, final int oldHeight, final int n3) {
        this.paintPending = (oldWidth != this.oldWidth || oldHeight != this.oldHeight);
        if ((n3 & 0x4000) != 0x0) {
            this.reshapeNoCheck(n, n2, oldWidth, oldHeight);
        }
        else {
            this.reshape(n, n2, oldWidth, oldHeight);
        }
        Label_0093: {
            if (oldWidth == this.oldWidth) {
                if (oldHeight == this.oldHeight) {
                    break Label_0093;
                }
            }
            try {
                this.replaceSurfaceData();
            }
            catch (final InvalidPipeException ex) {}
            this.oldWidth = oldWidth;
            this.oldHeight = oldHeight;
        }
        ++this.serialNum;
    }
    
    void dynamicallyLayoutContainer() {
        if (WComponentPeer.log.isLoggable(PlatformLogger.Level.FINE) && SunToolkit.getNativeContainer((Component)this.target) != null) {
            WComponentPeer.log.fine("Assertion (parent == null) failed");
        }
        final Container container = (Container)this.target;
        SunToolkit.executeOnEventHandlerThread(container, new Runnable() {
            @Override
            public void run() {
                container.invalidate();
                container.validate();
                if (!(WComponentPeer.this.surfaceData instanceof D3DSurfaceData.D3DWindowSurfaceData)) {
                    if (!(WComponentPeer.this.surfaceData instanceof OGLSurfaceData)) {
                        return;
                    }
                }
                try {
                    WComponentPeer.this.replaceSurfaceData();
                }
                catch (final InvalidPipeException ex) {}
            }
        });
    }
    
    void paintDamagedAreaImmediately() {
        this.updateWindow();
        SunToolkit.flushPendingEvents();
        this.paintArea.paint(this.target, this.shouldClearRectBeforePaint());
    }
    
    synchronized native void updateWindow();
    
    @Override
    public void paint(final Graphics graphics) {
        ((Component)this.target).paint(graphics);
    }
    
    public void repaint(final long n, final int n2, final int n3, final int n4, final int n5) {
    }
    
    private native int[] createPrintedPixels(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    @Override
    public void print(final Graphics graphics) {
        final Component component = (Component)this.target;
        final int width = component.getWidth();
        final int height = component.getHeight();
        int n = (int)(height / 4.0);
        if (n == 0) {
            n = height;
        }
        for (int i = 0; i < height; i += n) {
            int n2 = i + n - 1;
            if (n2 >= height) {
                n2 = height - 1;
            }
            final int n3 = n2 - i + 1;
            final Color background = component.getBackground();
            final int[] printedPixels = this.createPrintedPixels(0, i, width, n3, (background == null) ? 255 : background.getAlpha());
            if (printedPixels != null) {
                final BufferedImage bufferedImage = new BufferedImage(width, n3, 2);
                bufferedImage.setRGB(0, 0, width, n3, printedPixels, 0, width);
                graphics.drawImage(bufferedImage, 0, i, null);
                bufferedImage.flush();
            }
        }
        component.print(graphics);
    }
    
    @Override
    public void coalescePaintEvent(final PaintEvent paintEvent) {
        final Rectangle updateRect = paintEvent.getUpdateRect();
        if (!(paintEvent instanceof IgnorePaintEvent)) {
            this.paintArea.add(updateRect, paintEvent.getID());
        }
        if (WComponentPeer.log.isLoggable(PlatformLogger.Level.FINEST)) {
            switch (paintEvent.getID()) {
                case 801: {
                    WComponentPeer.log.finest("coalescePaintEvent: UPDATE: add: x = " + updateRect.x + ", y = " + updateRect.y + ", width = " + updateRect.width + ", height = " + updateRect.height);
                    return;
                }
                case 800: {
                    WComponentPeer.log.finest("coalescePaintEvent: PAINT: add: x = " + updateRect.x + ", y = " + updateRect.y + ", width = " + updateRect.width + ", height = " + updateRect.height);
                }
            }
        }
    }
    
    public synchronized native void reshape(final int p0, final int p1, final int p2, final int p3);
    
    public boolean handleJavaKeyEvent(final KeyEvent keyEvent) {
        return false;
    }
    
    public void handleJavaMouseEvent(final MouseEvent mouseEvent) {
        switch (mouseEvent.getID()) {
            case 501: {
                if (this.target == mouseEvent.getSource() && !((Component)this.target).isFocusOwner() && KeyboardFocusManagerPeerImpl.shouldFocusOnClick((Component)this.target)) {
                    KeyboardFocusManagerPeerImpl.requestFocusFor((Component)this.target, CausedFocusEvent.Cause.MOUSE_EVENT);
                    break;
                }
                break;
            }
        }
    }
    
    native void nativeHandleEvent(final AWTEvent p0);
    
    @Override
    public void handleEvent(final AWTEvent awtEvent) {
        final int id = awtEvent.getID();
        if (awtEvent instanceof InputEvent && !((InputEvent)awtEvent).isConsumed() && ((Component)this.target).isEnabled()) {
            if (awtEvent instanceof MouseEvent && !(awtEvent instanceof MouseWheelEvent)) {
                this.handleJavaMouseEvent((MouseEvent)awtEvent);
            }
            else if (awtEvent instanceof KeyEvent && this.handleJavaKeyEvent((KeyEvent)awtEvent)) {
                return;
            }
        }
        switch (id) {
            case 800: {
                this.paintPending = false;
            }
            case 801: {
                if (!this.isLayouting && !this.paintPending) {
                    this.paintArea.paint(this.target, this.shouldClearRectBeforePaint());
                }
                return;
            }
            case 1004:
            case 1005: {
                this.handleJavaFocusEvent((FocusEvent)awtEvent);
                break;
            }
        }
        this.nativeHandleEvent(awtEvent);
    }
    
    void handleJavaFocusEvent(final FocusEvent focusEvent) {
        if (WComponentPeer.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            WComponentPeer.focusLog.finer(focusEvent.toString());
        }
        this.setFocus(focusEvent.getID() == 1004);
    }
    
    native void setFocus(final boolean p0);
    
    @Override
    public Dimension getMinimumSize() {
        return ((Component)this.target).getSize();
    }
    
    @Override
    public Dimension getPreferredSize() {
        return this.getMinimumSize();
    }
    
    @Override
    public void layout() {
    }
    
    public Rectangle getBounds() {
        return ((Component)this.target).getBounds();
    }
    
    @Override
    public boolean isFocusable() {
        return false;
    }
    
    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {
        if (this.winGraphicsConfig != null) {
            return this.winGraphicsConfig;
        }
        return ((Component)this.target).getGraphicsConfiguration();
    }
    
    public SurfaceData getSurfaceData() {
        return this.surfaceData;
    }
    
    public void replaceSurfaceData() {
        this.replaceSurfaceData(this.numBackBuffers, this.backBufferCaps);
    }
    
    public void createScreenSurface(final boolean b) {
        this.surfaceData = ScreenUpdateManager.getInstance().createScreenSurface((Win32GraphicsConfig)this.getGraphicsConfiguration(), this, this.numBackBuffers, b);
    }
    
    public void replaceSurfaceData(final int numBackBuffers, final BufferCapabilities backBufferCaps) {
        SurfaceData surfaceData = null;
        Image backBuffer = null;
        synchronized (((Component)this.target).getTreeLock()) {
            synchronized (this) {
                if (this.pData == 0L) {
                    return;
                }
                this.numBackBuffers = numBackBuffers;
                final ScreenUpdateManager instance = ScreenUpdateManager.getInstance();
                surfaceData = this.surfaceData;
                instance.dropScreenSurface(surfaceData);
                this.createScreenSurface(true);
                if (surfaceData != null) {
                    surfaceData.invalidate();
                }
                backBuffer = this.backBuffer;
                if (this.numBackBuffers > 0) {
                    this.backBufferCaps = backBufferCaps;
                    this.backBuffer = ((Win32GraphicsConfig)this.getGraphicsConfiguration()).createBackBuffer(this);
                }
                else if (this.backBuffer != null) {
                    this.backBufferCaps = null;
                    this.backBuffer = null;
                }
            }
        }
        if (surfaceData != null) {
            surfaceData.flush();
        }
        if (backBuffer != null) {
            backBuffer.flush();
        }
    }
    
    public void replaceSurfaceDataLater() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!WComponentPeer.this.isDisposed()) {
                    try {
                        WComponentPeer.this.replaceSurfaceData();
                    }
                    catch (final InvalidPipeException ex) {}
                }
            }
        };
        final Component component = (Component)this.target;
        if (!PaintEventDispatcher.getPaintEventDispatcher().queueSurfaceDataReplacing(component, runnable)) {
            this.postEvent(new InvocationEvent(component, runnable));
        }
    }
    
    @Override
    public boolean updateGraphicsData(final GraphicsConfiguration graphicsConfiguration) {
        this.winGraphicsConfig = (Win32GraphicsConfig)graphicsConfiguration;
        try {
            this.replaceSurfaceData();
        }
        catch (final InvalidPipeException ex) {}
        return false;
    }
    
    @Override
    public ColorModel getColorModel() {
        final GraphicsConfiguration graphicsConfiguration = this.getGraphicsConfiguration();
        if (graphicsConfiguration != null) {
            return graphicsConfiguration.getColorModel();
        }
        return null;
    }
    
    public ColorModel getDeviceColorModel() {
        final Win32GraphicsConfig win32GraphicsConfig = (Win32GraphicsConfig)this.getGraphicsConfiguration();
        if (win32GraphicsConfig != null) {
            return win32GraphicsConfig.getDeviceColorModel();
        }
        return null;
    }
    
    public ColorModel getColorModel(final int n) {
        final GraphicsConfiguration graphicsConfiguration = this.getGraphicsConfiguration();
        if (graphicsConfiguration != null) {
            return graphicsConfiguration.getColorModel(n);
        }
        return null;
    }
    
    @Override
    public Graphics getGraphics() {
        if (this.isDisposed()) {
            return null;
        }
        final Component component = (Component)this.getTarget();
        final Window containingWindow = SunToolkit.getContainingWindow(component);
        if (containingWindow != null) {
            final Graphics translucentGraphics = ((WWindowPeer)containingWindow.getPeer()).getTranslucentGraphics();
            if (translucentGraphics != null) {
                int n = 0;
                int n2 = 0;
                for (Component parent = component; parent != containingWindow; parent = parent.getParent()) {
                    n += parent.getX();
                    n2 += parent.getY();
                }
                translucentGraphics.translate(n, n2);
                translucentGraphics.clipRect(0, 0, component.getWidth(), component.getHeight());
                return translucentGraphics;
            }
        }
        final SurfaceData surfaceData = this.surfaceData;
        if (surfaceData != null) {
            Color color = this.background;
            if (color == null) {
                color = SystemColor.window;
            }
            Color color2 = this.foreground;
            if (color2 == null) {
                color2 = SystemColor.windowText;
            }
            Font font = this.font;
            if (font == null) {
                font = WComponentPeer.defaultFont;
            }
            return ScreenUpdateManager.getInstance().createGraphics(surfaceData, this, color2, color, font);
        }
        return null;
    }
    
    @Override
    public FontMetrics getFontMetrics(final Font font) {
        return WFontMetrics.getFontMetrics(font);
    }
    
    private synchronized native void _dispose();
    
    @Override
    protected void disposeImpl() {
        final SurfaceData surfaceData = this.surfaceData;
        this.surfaceData = null;
        ScreenUpdateManager.getInstance().dropScreenSurface(surfaceData);
        surfaceData.invalidate();
        WToolkit.targetDisposedPeer(this.target, this);
        this._dispose();
    }
    
    public void disposeLater() {
        this.postEvent(new InvocationEvent(this.target, new Runnable() {
            @Override
            public void run() {
                WComponentPeer.this.dispose();
            }
        }));
    }
    
    @Override
    public synchronized void setForeground(final Color foreground) {
        this.foreground = foreground;
        this._setForeground(foreground.getRGB());
    }
    
    @Override
    public synchronized void setBackground(final Color background) {
        this.background = background;
        this._setBackground(background.getRGB());
    }
    
    public Color getBackgroundNoSync() {
        return this.background;
    }
    
    private native void _setForeground(final int p0);
    
    private native void _setBackground(final int p0);
    
    @Override
    public synchronized void setFont(final Font font) {
        this._setFont(this.font = font);
    }
    
    synchronized native void _setFont(final Font p0);
    
    @Override
    public void updateCursorImmediately() {
        WGlobalCursorManager.getCursorManager().updateCursorImmediately();
    }
    
    @Override
    public boolean requestFocus(final Component component, final boolean b, final boolean b2, final long n, final CausedFocusEvent.Cause cause) {
        if (KeyboardFocusManagerPeerImpl.processSynchronousLightweightTransfer((Component)this.target, component, b, b2, n)) {
            return true;
        }
        switch (KeyboardFocusManagerPeerImpl.shouldNativelyFocusHeavyweight((Component)this.target, component, b, b2, n, cause)) {
            case 0: {
                return false;
            }
            case 2: {
                if (WComponentPeer.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                    WComponentPeer.focusLog.finer("Proceeding with request to " + component + " in " + this.target);
                }
                final Window containingWindow = SunToolkit.getContainingWindow((Component)this.target);
                if (containingWindow == null) {
                    return this.rejectFocusRequestHelper("WARNING: Parent window is null");
                }
                final WWindowPeer wWindowPeer = (WWindowPeer)containingWindow.getPeer();
                if (wWindowPeer == null) {
                    return this.rejectFocusRequestHelper("WARNING: Parent window's peer is null");
                }
                final boolean requestWindowFocus = wWindowPeer.requestWindowFocus(cause);
                if (WComponentPeer.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                    WComponentPeer.focusLog.finer("Requested window focus: " + requestWindowFocus);
                }
                if (!requestWindowFocus || !containingWindow.isFocused()) {
                    return this.rejectFocusRequestHelper("Waiting for asynchronous processing of the request");
                }
                return WKeyboardFocusManagerPeer.deliverFocus(component, (Component)this.target, b, b2, n, cause);
            }
            case 1: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean rejectFocusRequestHelper(final String s) {
        if (WComponentPeer.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            WComponentPeer.focusLog.finer(s);
        }
        KeyboardFocusManagerPeerImpl.removeLastFocusRequest((Component)this.target);
        return false;
    }
    
    @Override
    public Image createImage(final ImageProducer imageProducer) {
        return new ToolkitImage(imageProducer);
    }
    
    @Override
    public Image createImage(final int n, final int n2) {
        return ((Win32GraphicsConfig)this.getGraphicsConfiguration()).createAcceleratedImage((Component)this.target, n, n2);
    }
    
    @Override
    public VolatileImage createVolatileImage(final int n, final int n2) {
        return new SunVolatileImage((Component)this.target, n, n2);
    }
    
    @Override
    public boolean prepareImage(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        return Toolkit.getDefaultToolkit().prepareImage(image, n, n2, imageObserver);
    }
    
    @Override
    public int checkImage(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        return Toolkit.getDefaultToolkit().checkImage(image, n, n2, imageObserver);
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[" + this.target + "]";
    }
    
    WComponentPeer(final Component target) {
        this.isLayouting = false;
        this.paintPending = false;
        this.oldWidth = -1;
        this.oldHeight = -1;
        this.numBackBuffers = 0;
        this.backBuffer = null;
        this.backBufferCaps = null;
        this.serialNum = 0;
        this.isAccelCapable = true;
        this.target = target;
        this.paintArea = new RepaintArea();
        this.create(this.getNativeParent());
        this.checkCreation();
        this.createScreenSurface(false);
        this.initialize();
        this.start();
    }
    
    abstract void create(final WComponentPeer p0);
    
    WComponentPeer getNativeParent() {
        return (WComponentPeer)WToolkit.targetToPeer(SunToolkit.getNativeContainer((Component)this.target));
    }
    
    protected void checkCreation() {
        if (this.hwnd != 0L && this.pData != 0L) {
            return;
        }
        if (this.createError != null) {
            throw this.createError;
        }
        throw new InternalError("couldn't create component peer");
    }
    
    synchronized native void start();
    
    void initialize() {
        if (((Component)this.target).isVisible()) {
            this.show();
        }
        final Color foreground = ((Component)this.target).getForeground();
        if (foreground != null) {
            this.setForeground(foreground);
        }
        final Font font = ((Component)this.target).getFont();
        if (font != null) {
            this.setFont(font);
        }
        if (!((Component)this.target).isEnabled()) {
            this.disable();
        }
        final Rectangle bounds = ((Component)this.target).getBounds();
        this.setBounds(bounds.x, bounds.y, bounds.width, bounds.height, 3);
    }
    
    void handleRepaint(final int n, final int n2, final int n3, final int n4) {
    }
    
    void handleExpose(final int n, final int n2, final int n3, final int n4) {
        this.postPaintIfNecessary(n, n2, n3, n4);
    }
    
    public void handlePaint(final int n, final int n2, final int n3, final int n4) {
        this.postPaintIfNecessary(n, n2, n3, n4);
    }
    
    private void postPaintIfNecessary(final int n, final int n2, final int n3, final int n4) {
        if (!AWTAccessor.getComponentAccessor().getIgnoreRepaint((Component)this.target)) {
            final PaintEvent paintEvent = PaintEventDispatcher.getPaintEventDispatcher().createPaintEvent((Component)this.target, n, n2, n3, n4);
            if (paintEvent != null) {
                this.postEvent(paintEvent);
            }
        }
    }
    
    void postEvent(final AWTEvent awtEvent) {
        this.preprocessPostEvent(awtEvent);
        SunToolkit.postEvent(SunToolkit.targetToAppContext(this.target), awtEvent);
    }
    
    void preprocessPostEvent(final AWTEvent awtEvent) {
    }
    
    public void beginLayout() {
        this.isLayouting = true;
    }
    
    public void endLayout() {
        if (!this.paintArea.isEmpty() && !this.paintPending && !((Component)this.target).getIgnoreRepaint()) {
            this.postEvent(new PaintEvent((Component)this.target, 800, new Rectangle()));
        }
        this.isLayouting = false;
    }
    
    public native void beginValidate();
    
    public native void endValidate();
    
    public Dimension preferredSize() {
        return this.getPreferredSize();
    }
    
    @Override
    public synchronized void addDropTarget(final DropTarget dropTarget) {
        if (this.nDropTargets == 0) {
            this.nativeDropTargetContext = this.addNativeDropTarget();
        }
        ++this.nDropTargets;
    }
    
    @Override
    public synchronized void removeDropTarget(final DropTarget dropTarget) {
        --this.nDropTargets;
        if (this.nDropTargets == 0) {
            this.removeNativeDropTarget();
            this.nativeDropTargetContext = 0L;
        }
    }
    
    native long addNativeDropTarget();
    
    native void removeNativeDropTarget();
    
    native boolean nativeHandlesWheelScrolling();
    
    @Override
    public boolean handlesWheelScrolling() {
        return this.nativeHandlesWheelScrolling();
    }
    
    public boolean isPaintPending() {
        return this.paintPending && this.isLayouting;
    }
    
    @Override
    public void createBuffers(final int n, final BufferCapabilities bufferCapabilities) throws AWTException {
        ((Win32GraphicsConfig)this.getGraphicsConfiguration()).assertOperationSupported((Component)this.target, n, bufferCapabilities);
        try {
            this.replaceSurfaceData(n - 1, bufferCapabilities);
        }
        catch (final InvalidPipeException ex) {
            throw new AWTException(ex.getMessage());
        }
    }
    
    @Override
    public void destroyBuffers() {
        this.replaceSurfaceData(0, null);
    }
    
    @Override
    public void flip(final int n, final int n2, final int n3, final int n4, final BufferCapabilities.FlipContents flipContents) {
        final VolatileImage backBuffer = this.backBuffer;
        if (backBuffer == null) {
            throw new IllegalStateException("Buffers have not been created");
        }
        ((Win32GraphicsConfig)this.getGraphicsConfiguration()).flip(this, (Component)this.target, backBuffer, n, n2, n3, n4, flipContents);
    }
    
    @Override
    public synchronized Image getBackBuffer() {
        final VolatileImage backBuffer = this.backBuffer;
        if (backBuffer == null) {
            throw new IllegalStateException("Buffers have not been created");
        }
        return backBuffer;
    }
    
    public BufferCapabilities getBackBufferCaps() {
        return this.backBufferCaps;
    }
    
    public int getBackBuffersNum() {
        return this.numBackBuffers;
    }
    
    public boolean shouldClearRectBeforePaint() {
        return true;
    }
    
    native void pSetParent(final ComponentPeer p0);
    
    @Override
    public void reparent(final ContainerPeer containerPeer) {
        this.pSetParent(containerPeer);
    }
    
    @Override
    public boolean isReparentSupported() {
        return true;
    }
    
    public void setBoundsOperation(final int n) {
    }
    
    public boolean isAccelCapable() {
        return this.isAccelCapable && isContainingTopLevelAccelCapable((Component)this.target) && (!SunToolkit.isContainingTopLevelTranslucent((Component)this.target) || Win32GraphicsEnvironment.isVistaOS());
    }
    
    public void disableAcceleration() {
        this.isAccelCapable = false;
    }
    
    native void setRectangularShape(final int p0, final int p1, final int p2, final int p3, final Region p4);
    
    private static final boolean isContainingTopLevelAccelCapable(Component parent) {
        while (parent != null && !(parent instanceof WEmbeddedFrame)) {
            parent = parent.getParent();
        }
        return parent == null || ((WEmbeddedFramePeer)parent.getPeer()).isAccelCapable();
    }
    
    @Override
    public void applyShape(final Region region) {
        if (WComponentPeer.shapeLog.isLoggable(PlatformLogger.Level.FINER)) {
            WComponentPeer.shapeLog.finer("*** INFO: Setting shape: PEER: " + this + "; TARGET: " + this.target + "; SHAPE: " + region);
        }
        if (region != null) {
            this.setRectangularShape(region.getLoX(), region.getLoY(), region.getHiX(), region.getHiY(), region.isRectangular() ? null : region);
        }
        else {
            this.setRectangularShape(0, 0, 0, 0, null);
        }
    }
    
    @Override
    public void setZOrder(final ComponentPeer componentPeer) {
        this.setZOrder((componentPeer != null) ? ((WComponentPeer)componentPeer).getHWnd() : 0L);
    }
    
    private native void setZOrder(final long p0);
    
    public boolean isLightweightFramePeer() {
        return false;
    }
    
    static {
        log = PlatformLogger.getLogger("sun.awt.windows.WComponentPeer");
        shapeLog = PlatformLogger.getLogger("sun.awt.windows.shape.WComponentPeer");
        focusLog = PlatformLogger.getLogger("sun.awt.windows.focus.WComponentPeer");
        defaultFont = new Font("Dialog", 0, 12);
    }
}
