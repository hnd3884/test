package javax.swing;

import java.awt.event.WindowEvent;
import java.awt.event.ComponentEvent;
import java.awt.AWTException;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
import java.awt.ImageCapabilities;
import com.sun.java.swing.SwingUtilities3;
import java.lang.reflect.InvocationTargetException;
import java.awt.event.ComponentListener;
import java.lang.ref.WeakReference;
import java.awt.event.WindowListener;
import java.awt.event.ComponentAdapter;
import sun.awt.SunToolkit;
import java.awt.Window;
import java.awt.Rectangle;
import sun.java2d.SunGraphics2D;
import sun.awt.SubRegionShowable;
import java.awt.Container;
import java.util.Iterator;
import java.util.List;
import java.security.AccessController;
import java.awt.BufferCapabilities;
import java.awt.Component;
import java.security.PrivilegedAction;
import java.awt.image.BufferStrategy;
import java.awt.Graphics;
import java.util.ArrayList;
import sun.util.logging.PlatformLogger;
import java.lang.reflect.Method;

class BufferStrategyPaintManager extends RepaintManager.PaintManager
{
    private static Method COMPONENT_CREATE_BUFFER_STRATEGY_METHOD;
    private static Method COMPONENT_GET_BUFFER_STRATEGY_METHOD;
    private static final PlatformLogger LOGGER;
    private ArrayList<BufferInfo> bufferInfos;
    private boolean painting;
    private boolean showing;
    private int accumulatedX;
    private int accumulatedY;
    private int accumulatedMaxX;
    private int accumulatedMaxY;
    private JComponent rootJ;
    private int xOffset;
    private int yOffset;
    private Graphics bsg;
    private BufferStrategy bufferStrategy;
    private BufferInfo bufferInfo;
    private boolean disposeBufferOnEnd;
    
    private static Method getGetBufferStrategyMethod() {
        if (BufferStrategyPaintManager.COMPONENT_GET_BUFFER_STRATEGY_METHOD == null) {
            getMethods();
        }
        return BufferStrategyPaintManager.COMPONENT_GET_BUFFER_STRATEGY_METHOD;
    }
    
    private static Method getCreateBufferStrategyMethod() {
        if (BufferStrategyPaintManager.COMPONENT_CREATE_BUFFER_STRATEGY_METHOD == null) {
            getMethods();
        }
        return BufferStrategyPaintManager.COMPONENT_CREATE_BUFFER_STRATEGY_METHOD;
    }
    
    private static void getMethods() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    BufferStrategyPaintManager.COMPONENT_CREATE_BUFFER_STRATEGY_METHOD = Component.class.getDeclaredMethod("createBufferStrategy", Integer.TYPE, BufferCapabilities.class);
                    BufferStrategyPaintManager.COMPONENT_CREATE_BUFFER_STRATEGY_METHOD.setAccessible(true);
                    BufferStrategyPaintManager.COMPONENT_GET_BUFFER_STRATEGY_METHOD = Component.class.getDeclaredMethod("getBufferStrategy", (Class<?>[])new Class[0]);
                    BufferStrategyPaintManager.COMPONENT_GET_BUFFER_STRATEGY_METHOD.setAccessible(true);
                }
                catch (final SecurityException ex) {
                    assert false;
                }
                catch (final NoSuchMethodException ex2) {
                    assert false;
                }
                return null;
            }
        });
    }
    
    BufferStrategyPaintManager() {
        this.bufferInfos = new ArrayList<BufferInfo>(1);
    }
    
    @Override
    protected void dispose() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final ArrayList access$300;
                synchronized (BufferStrategyPaintManager.this) {
                    while (BufferStrategyPaintManager.this.showing) {
                        try {
                            BufferStrategyPaintManager.this.wait();
                        }
                        catch (final InterruptedException ex) {}
                    }
                    access$300 = BufferStrategyPaintManager.this.bufferInfos;
                    BufferStrategyPaintManager.this.bufferInfos = null;
                }
                BufferStrategyPaintManager.this.dispose(access$300);
            }
        });
    }
    
    private void dispose(final List<BufferInfo> list) {
        if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
            BufferStrategyPaintManager.LOGGER.finer("BufferStrategyPaintManager disposed", new RuntimeException());
        }
        if (list != null) {
            final Iterator<BufferInfo> iterator = list.iterator();
            while (iterator.hasNext()) {
                iterator.next().dispose();
            }
        }
    }
    
    @Override
    public boolean show(final Container container, final int n, final int n2, final int n3, final int n4) {
        synchronized (this) {
            if (this.painting) {
                return false;
            }
            this.showing = true;
        }
        try {
            final BufferInfo bufferInfo = this.getBufferInfo(container);
            final BufferStrategy bufferStrategy;
            if (bufferInfo != null && bufferInfo.isInSync() && (bufferStrategy = bufferInfo.getBufferStrategy(false)) != null) {
                final SubRegionShowable subRegionShowable = (SubRegionShowable)bufferStrategy;
                final boolean paintAllOnExpose = bufferInfo.getPaintAllOnExpose();
                bufferInfo.setPaintAllOnExpose(false);
                if (subRegionShowable.showIfNotLost(n, n2, n + n3, n2 + n4)) {
                    return !paintAllOnExpose;
                }
                this.bufferInfo.setContentsLostDuringExpose(true);
            }
        }
        finally {
            synchronized (this) {
                this.showing = false;
                this.notifyAll();
            }
        }
        return false;
    }
    
    @Override
    public boolean paint(final JComponent component, final JComponent component2, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        final Container fetchRoot = this.fetchRoot(component);
        if (this.prepare(component, fetchRoot, true, n, n2, n3, n4)) {
            if (graphics instanceof SunGraphics2D && ((SunGraphics2D)graphics).getDestination() == fetchRoot) {
                final int constrainX = ((SunGraphics2D)this.bsg).constrainX;
                final int constrainY = ((SunGraphics2D)this.bsg).constrainY;
                if (constrainX != 0 || constrainY != 0) {
                    this.bsg.translate(-constrainX, -constrainY);
                }
                ((SunGraphics2D)this.bsg).constrain(this.xOffset + constrainX, this.yOffset + constrainY, n + n3, n2 + n4);
                this.bsg.setClip(n, n2, n3, n4);
                component.paintToOffscreen(this.bsg, n, n2, n3, n4, n + n3, n2 + n4);
                this.accumulate(this.xOffset + n, this.yOffset + n2, n3, n4);
                return true;
            }
            this.bufferInfo.setInSync(false);
        }
        if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
            BufferStrategyPaintManager.LOGGER.finer("prepare failed");
        }
        return super.paint(component, component2, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void copyArea(final JComponent component, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final boolean b) {
        if (this.prepare(component, this.fetchRoot(component), false, 0, 0, 0, 0) && this.bufferInfo.isInSync()) {
            if (b) {
                final Rectangle visibleRect = component.getVisibleRect();
                final int n7 = this.xOffset + n;
                final int n8 = this.yOffset + n2;
                this.bsg.clipRect(this.xOffset + visibleRect.x, this.yOffset + visibleRect.y, visibleRect.width, visibleRect.height);
                this.bsg.copyArea(n7, n8, n3, n4, n5, n6);
            }
            else {
                this.bsg.copyArea(this.xOffset + n, this.yOffset + n2, n3, n4, n5, n6);
            }
            this.accumulate(n + this.xOffset + n5, n2 + this.yOffset + n6, n3, n4);
        }
        else {
            if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
                BufferStrategyPaintManager.LOGGER.finer("copyArea: prepare failed or not in sync");
            }
            if (!this.flushAccumulatedRegion()) {
                this.rootJ.repaint();
            }
            else {
                super.copyArea(component, graphics, n, n2, n3, n4, n5, n6, b);
            }
        }
    }
    
    @Override
    public void beginPaint() {
        synchronized (this) {
            this.painting = true;
            while (this.showing) {
                try {
                    this.wait();
                }
                catch (final InterruptedException ex) {}
            }
        }
        if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINEST)) {
            BufferStrategyPaintManager.LOGGER.finest("beginPaint");
        }
        this.resetAccumulated();
    }
    
    @Override
    public void endPaint() {
        if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINEST)) {
            BufferStrategyPaintManager.LOGGER.finest("endPaint: region " + this.accumulatedX + " " + this.accumulatedY + " " + this.accumulatedMaxX + " " + this.accumulatedMaxY);
        }
        if (this.painting && !this.flushAccumulatedRegion()) {
            if (!this.isRepaintingRoot()) {
                this.repaintRoot(this.rootJ);
            }
            else {
                this.resetDoubleBufferPerWindow();
                this.rootJ.repaint();
            }
        }
        Object bufferInfo = null;
        synchronized (this) {
            this.painting = false;
            if (this.disposeBufferOnEnd) {
                this.disposeBufferOnEnd = false;
                bufferInfo = this.bufferInfo;
                this.bufferInfos.remove(bufferInfo);
            }
        }
        if (bufferInfo != null) {
            ((BufferInfo)bufferInfo).dispose();
        }
    }
    
    private boolean flushAccumulatedRegion() {
        boolean b = true;
        if (this.accumulatedX != Integer.MAX_VALUE) {
            final SubRegionShowable subRegionShowable = (SubRegionShowable)this.bufferStrategy;
            boolean b2 = this.bufferStrategy.contentsLost();
            if (!b2) {
                subRegionShowable.show(this.accumulatedX, this.accumulatedY, this.accumulatedMaxX, this.accumulatedMaxY);
                b2 = this.bufferStrategy.contentsLost();
            }
            if (b2) {
                if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
                    BufferStrategyPaintManager.LOGGER.finer("endPaint: contents lost");
                }
                this.bufferInfo.setInSync(false);
                b = false;
            }
        }
        this.resetAccumulated();
        return b;
    }
    
    private void resetAccumulated() {
        this.accumulatedX = Integer.MAX_VALUE;
        this.accumulatedY = Integer.MAX_VALUE;
        this.accumulatedMaxX = 0;
        this.accumulatedMaxY = 0;
    }
    
    @Override
    public void doubleBufferingChanged(final JRootPane rootPane) {
        if ((!rootPane.isDoubleBuffered() || !rootPane.getUseTrueDoubleBuffering()) && rootPane.getParent() != null) {
            if (!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        BufferStrategyPaintManager.this.doubleBufferingChanged0(rootPane);
                    }
                });
            }
            else {
                this.doubleBufferingChanged0(rootPane);
            }
        }
    }
    
    private void doubleBufferingChanged0(final JRootPane rootPane) {
        BufferInfo bufferInfo;
        synchronized (this) {
            while (this.showing) {
                try {
                    this.wait();
                }
                catch (final InterruptedException ex) {}
            }
            bufferInfo = this.getBufferInfo(rootPane.getParent());
            if (this.painting && this.bufferInfo == bufferInfo) {
                this.disposeBufferOnEnd = true;
                bufferInfo = null;
            }
            else if (bufferInfo != null) {
                this.bufferInfos.remove(bufferInfo);
            }
        }
        if (bufferInfo != null) {
            bufferInfo.dispose();
        }
    }
    
    private boolean prepare(final JComponent component, final Container container, final boolean b, final int n, final int n2, final int n3, final int n4) {
        if (this.bsg != null) {
            this.bsg.dispose();
            this.bsg = null;
        }
        this.bufferStrategy = null;
        if (container != null) {
            boolean b2 = false;
            BufferInfo bufferInfo = this.getBufferInfo(container);
            if (bufferInfo == null) {
                b2 = true;
                bufferInfo = new BufferInfo(container);
                this.bufferInfos.add(bufferInfo);
                if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
                    BufferStrategyPaintManager.LOGGER.finer("prepare: new BufferInfo: " + container);
                }
            }
            this.bufferInfo = bufferInfo;
            if (!bufferInfo.hasBufferStrategyChanged()) {
                this.bufferStrategy = bufferInfo.getBufferStrategy(true);
                if (this.bufferStrategy != null) {
                    this.bsg = this.bufferStrategy.getDrawGraphics();
                    if (this.bufferStrategy.contentsRestored()) {
                        b2 = true;
                        if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
                            BufferStrategyPaintManager.LOGGER.finer("prepare: contents restored in prepare");
                        }
                    }
                    if (bufferInfo.getContentsLostDuringExpose()) {
                        b2 = true;
                        bufferInfo.setContentsLostDuringExpose(false);
                        if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
                            BufferStrategyPaintManager.LOGGER.finer("prepare: contents lost on expose");
                        }
                    }
                    if (b && component == this.rootJ && n == 0 && n2 == 0 && component.getWidth() == n3 && component.getHeight() == n4) {
                        bufferInfo.setInSync(true);
                    }
                    else if (b2) {
                        bufferInfo.setInSync(false);
                        if (!this.isRepaintingRoot()) {
                            this.repaintRoot(this.rootJ);
                        }
                        else {
                            this.resetDoubleBufferPerWindow();
                        }
                    }
                    return this.bufferInfos != null;
                }
                return false;
            }
        }
        return false;
    }
    
    private Container fetchRoot(final JComponent rootJ) {
        int n = 0;
        this.rootJ = rootJ;
        Container parent = rootJ;
        final int n2 = 0;
        this.yOffset = n2;
        this.xOffset = n2;
        while (parent != null && !(parent instanceof Window) && !SunToolkit.isInstanceOf(parent, "java.applet.Applet")) {
            this.xOffset += parent.getX();
            this.yOffset += parent.getY();
            parent = parent.getParent();
            if (parent != null) {
                if (parent instanceof JComponent) {
                    this.rootJ = (JComponent)parent;
                }
                else {
                    if (parent.isLightweight()) {
                        continue;
                    }
                    if (n != 0) {
                        return null;
                    }
                    n = 1;
                }
            }
        }
        if (parent instanceof RootPaneContainer && this.rootJ instanceof JRootPane && this.rootJ.isDoubleBuffered() && ((JRootPane)this.rootJ).getUseTrueDoubleBuffering()) {
            return parent;
        }
        return null;
    }
    
    private void resetDoubleBufferPerWindow() {
        if (this.bufferInfos != null) {
            this.dispose(this.bufferInfos);
            this.bufferInfos = null;
            this.repaintManager.setPaintManager(null);
        }
    }
    
    private BufferInfo getBufferInfo(final Container container) {
        for (int i = this.bufferInfos.size() - 1; i >= 0; --i) {
            final BufferInfo bufferInfo = this.bufferInfos.get(i);
            final Container root = bufferInfo.getRoot();
            if (root == null) {
                this.bufferInfos.remove(i);
                if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
                    BufferStrategyPaintManager.LOGGER.finer("BufferInfo pruned, root null");
                }
            }
            else if (root == container) {
                return bufferInfo;
            }
        }
        return null;
    }
    
    private void accumulate(final int n, final int n2, final int n3, final int n4) {
        this.accumulatedX = Math.min(n, this.accumulatedX);
        this.accumulatedY = Math.min(n2, this.accumulatedY);
        this.accumulatedMaxX = Math.max(this.accumulatedMaxX, n + n3);
        this.accumulatedMaxY = Math.max(this.accumulatedMaxY, n2 + n4);
    }
    
    static {
        LOGGER = PlatformLogger.getLogger("javax.swing.BufferStrategyPaintManager");
    }
    
    private class BufferInfo extends ComponentAdapter implements WindowListener
    {
        private WeakReference<BufferStrategy> weakBS;
        private WeakReference<Container> root;
        private boolean inSync;
        private boolean contentsLostDuringExpose;
        private boolean paintAllOnExpose;
        
        public BufferInfo(final Container container) {
            this.root = new WeakReference<Container>(container);
            container.addComponentListener(this);
            if (container instanceof Window) {
                ((Window)container).addWindowListener(this);
            }
        }
        
        public void setPaintAllOnExpose(final boolean paintAllOnExpose) {
            this.paintAllOnExpose = paintAllOnExpose;
        }
        
        public boolean getPaintAllOnExpose() {
            return this.paintAllOnExpose;
        }
        
        public void setContentsLostDuringExpose(final boolean contentsLostDuringExpose) {
            this.contentsLostDuringExpose = contentsLostDuringExpose;
        }
        
        public boolean getContentsLostDuringExpose() {
            return this.contentsLostDuringExpose;
        }
        
        public void setInSync(final boolean inSync) {
            this.inSync = inSync;
        }
        
        public boolean isInSync() {
            return this.inSync;
        }
        
        public Container getRoot() {
            return (this.root == null) ? null : this.root.get();
        }
        
        public BufferStrategy getBufferStrategy(final boolean b) {
            BufferStrategy bufferStrategy = (this.weakBS == null) ? null : this.weakBS.get();
            if (bufferStrategy == null && b) {
                bufferStrategy = this.createBufferStrategy();
                if (bufferStrategy != null) {
                    this.weakBS = new WeakReference<BufferStrategy>(bufferStrategy);
                }
                if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
                    BufferStrategyPaintManager.LOGGER.finer("getBufferStrategy: created bs: " + bufferStrategy);
                }
            }
            return bufferStrategy;
        }
        
        public boolean hasBufferStrategyChanged() {
            final Container root = this.getRoot();
            if (root != null) {
                BufferStrategy bufferStrategy = null;
                final BufferStrategy bufferStrategy2 = this.getBufferStrategy(false);
                if (root instanceof Window) {
                    bufferStrategy = ((Window)root).getBufferStrategy();
                }
                else {
                    try {
                        bufferStrategy = (BufferStrategy)getGetBufferStrategyMethod().invoke(root, new Object[0]);
                    }
                    catch (final InvocationTargetException ex) {
                        assert false;
                    }
                    catch (final IllegalArgumentException ex2) {
                        assert false;
                    }
                    catch (final IllegalAccessException ex3) {
                        assert false;
                    }
                }
                if (bufferStrategy != bufferStrategy2) {
                    if (bufferStrategy2 != null) {
                        bufferStrategy2.dispose();
                    }
                    this.weakBS = null;
                    return true;
                }
            }
            return false;
        }
        
        private BufferStrategy createBufferStrategy() {
            final Container root = this.getRoot();
            if (root == null) {
                return null;
            }
            BufferStrategy bufferStrategy = null;
            if (SwingUtilities3.isVsyncRequested(root)) {
                bufferStrategy = this.createBufferStrategy(root, true);
                if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
                    BufferStrategyPaintManager.LOGGER.finer("createBufferStrategy: using vsynced strategy");
                }
            }
            if (bufferStrategy == null) {
                bufferStrategy = this.createBufferStrategy(root, false);
            }
            if (!(bufferStrategy instanceof SubRegionShowable)) {
                bufferStrategy = null;
            }
            return bufferStrategy;
        }
        
        private BufferStrategy createBufferStrategy(final Container container, final boolean b) {
            BufferCapabilities bufferCapabilities;
            if (b) {
                bufferCapabilities = new ExtendedBufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), BufferCapabilities.FlipContents.COPIED, ExtendedBufferCapabilities.VSyncType.VSYNC_ON);
            }
            else {
                bufferCapabilities = new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), null);
            }
            BufferStrategy bufferStrategy = null;
            if (SunToolkit.isInstanceOf(container, "java.applet.Applet")) {
                try {
                    getCreateBufferStrategyMethod().invoke(container, 2, bufferCapabilities);
                    bufferStrategy = (BufferStrategy)getGetBufferStrategyMethod().invoke(container, new Object[0]);
                }
                catch (final InvocationTargetException ex) {
                    if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
                        BufferStrategyPaintManager.LOGGER.finer("createBufferStratety failed", ex);
                    }
                }
                catch (final IllegalArgumentException ex2) {
                    assert false;
                }
                catch (final IllegalAccessException ex3) {
                    assert false;
                }
            }
            else {
                try {
                    ((Window)container).createBufferStrategy(2, bufferCapabilities);
                    bufferStrategy = ((Window)container).getBufferStrategy();
                }
                catch (final AWTException ex4) {
                    if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
                        BufferStrategyPaintManager.LOGGER.finer("createBufferStratety failed", ex4);
                    }
                }
            }
            return bufferStrategy;
        }
        
        public void dispose() {
            final Container root = this.getRoot();
            if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
                BufferStrategyPaintManager.LOGGER.finer("disposed BufferInfo for: " + root);
            }
            if (root != null) {
                root.removeComponentListener(this);
                if (root instanceof Window) {
                    ((Window)root).removeWindowListener(this);
                }
                final BufferStrategy bufferStrategy = this.getBufferStrategy(false);
                if (bufferStrategy != null) {
                    bufferStrategy.dispose();
                }
            }
            this.root = null;
            this.weakBS = null;
        }
        
        @Override
        public void componentHidden(final ComponentEvent componentEvent) {
            final Container root = this.getRoot();
            if (root != null && root.isVisible()) {
                root.repaint();
            }
            else {
                this.setPaintAllOnExpose(true);
            }
        }
        
        @Override
        public void windowIconified(final WindowEvent windowEvent) {
            this.setPaintAllOnExpose(true);
        }
        
        @Override
        public void windowClosed(final WindowEvent windowEvent) {
            synchronized (BufferStrategyPaintManager.this) {
                while (BufferStrategyPaintManager.this.showing) {
                    try {
                        BufferStrategyPaintManager.this.wait();
                    }
                    catch (final InterruptedException ex) {}
                }
                BufferStrategyPaintManager.this.bufferInfos.remove(this);
            }
            this.dispose();
        }
        
        @Override
        public void windowOpened(final WindowEvent windowEvent) {
        }
        
        @Override
        public void windowClosing(final WindowEvent windowEvent) {
        }
        
        @Override
        public void windowDeiconified(final WindowEvent windowEvent) {
        }
        
        @Override
        public void windowActivated(final WindowEvent windowEvent) {
        }
        
        @Override
        public void windowDeactivated(final WindowEvent windowEvent) {
        }
    }
}
