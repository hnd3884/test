package javax.swing;

import java.awt.EventQueue;
import java.awt.Color;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import sun.java2d.SunGraphicsEnvironment;
import sun.security.action.GetPropertyAction;
import sun.swing.SwingAccessor;
import sun.misc.SharedSecrets;
import com.sun.java.swing.SwingUtilities3;
import java.awt.AWTEvent;
import java.awt.event.InvocationEvent;
import java.awt.GraphicsDevice;
import java.awt.HeadlessException;
import java.awt.image.ImageObserver;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Graphics;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.HashSet;
import java.awt.Toolkit;
import sun.awt.AWTAccessor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import java.util.Iterator;
import java.awt.Frame;
import java.applet.Applet;
import java.awt.Window;
import sun.awt.SunToolkit;
import java.util.IdentityHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;
import sun.awt.DisplayChangedListener;
import sun.misc.JavaSecurityAccess;
import java.awt.Dimension;
import java.util.List;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Container;
import java.awt.image.VolatileImage;
import java.awt.GraphicsConfiguration;
import java.util.Map;

public class RepaintManager
{
    static final boolean HANDLE_TOP_LEVEL_PAINT;
    private static final short BUFFER_STRATEGY_NOT_SPECIFIED = 0;
    private static final short BUFFER_STRATEGY_SPECIFIED_ON = 1;
    private static final short BUFFER_STRATEGY_SPECIFIED_OFF = 2;
    private static final short BUFFER_STRATEGY_TYPE;
    private Map<GraphicsConfiguration, VolatileImage> volatileMap;
    private Map<Container, Rectangle> hwDirtyComponents;
    private Map<Component, Rectangle> dirtyComponents;
    private Map<Component, Rectangle> tmpDirtyComponents;
    private List<Component> invalidComponents;
    private List<Runnable> runnableList;
    boolean doubleBufferingEnabled;
    private Dimension doubleBufferMaxSize;
    DoubleBufferInfo standardDoubleBuffer;
    private PaintManager paintManager;
    private static final Object repaintManagerKey;
    static boolean volatileImageBufferEnabled;
    private static final int volatileBufferType;
    private static boolean nativeDoubleBuffering;
    private static final int VOLATILE_LOOP_MAX = 2;
    private int paintDepth;
    private short bufferStrategyType;
    private boolean painting;
    private JComponent repaintRoot;
    private Thread paintThread;
    private final ProcessingRunnable processingRunnable;
    private static final JavaSecurityAccess javaSecurityAccess;
    private static final DisplayChangedListener displayChangedHandler;
    Rectangle tmp;
    private List<SwingUtilities2.RepaintListener> repaintListeners;
    
    public static RepaintManager currentManager(final Component component) {
        return currentManager(AppContext.getAppContext());
    }
    
    static RepaintManager currentManager(final AppContext appContext) {
        RepaintManager repaintManager = (RepaintManager)appContext.get(RepaintManager.repaintManagerKey);
        if (repaintManager == null) {
            repaintManager = new RepaintManager(RepaintManager.BUFFER_STRATEGY_TYPE);
            appContext.put(RepaintManager.repaintManagerKey, repaintManager);
        }
        return repaintManager;
    }
    
    public static RepaintManager currentManager(final JComponent component) {
        return currentManager((Component)component);
    }
    
    public static void setCurrentManager(final RepaintManager repaintManager) {
        if (repaintManager != null) {
            SwingUtilities.appContextPut(RepaintManager.repaintManagerKey, repaintManager);
        }
        else {
            SwingUtilities.appContextRemove(RepaintManager.repaintManagerKey);
        }
    }
    
    public RepaintManager() {
        this((short)2);
    }
    
    private RepaintManager(final short bufferStrategyType) {
        this.volatileMap = new HashMap<GraphicsConfiguration, VolatileImage>(1);
        this.doubleBufferingEnabled = true;
        this.paintDepth = 0;
        this.tmp = new Rectangle();
        this.repaintListeners = new ArrayList<SwingUtilities2.RepaintListener>(1);
        this.doubleBufferingEnabled = !RepaintManager.nativeDoubleBuffering;
        synchronized (this) {
            this.dirtyComponents = new IdentityHashMap<Component, Rectangle>();
            this.tmpDirtyComponents = new IdentityHashMap<Component, Rectangle>();
            this.bufferStrategyType = bufferStrategyType;
            this.hwDirtyComponents = new IdentityHashMap<Container, Rectangle>();
        }
        this.processingRunnable = new ProcessingRunnable();
    }
    
    private void displayChanged() {
        this.clearImages();
    }
    
    public synchronized void addInvalidComponent(final JComponent component) {
        final RepaintManager delegate = this.getDelegate(component);
        if (delegate != null) {
            delegate.addInvalidComponent(component);
            return;
        }
        final Container validateRoot = SwingUtilities.getValidateRoot(component, true);
        if (validateRoot == null) {
            return;
        }
        if (this.invalidComponents == null) {
            this.invalidComponents = new ArrayList<Component>();
        }
        else {
            for (int size = this.invalidComponents.size(), i = 0; i < size; ++i) {
                if (validateRoot == this.invalidComponents.get(i)) {
                    return;
                }
            }
        }
        this.invalidComponents.add(validateRoot);
        this.scheduleProcessingRunnable(SunToolkit.targetToAppContext(component));
    }
    
    public synchronized void removeInvalidComponent(final JComponent component) {
        final RepaintManager delegate = this.getDelegate(component);
        if (delegate != null) {
            delegate.removeInvalidComponent(component);
            return;
        }
        if (this.invalidComponents != null) {
            final int index = this.invalidComponents.indexOf(component);
            if (index != -1) {
                this.invalidComponents.remove(index);
            }
        }
    }
    
    private void addDirtyRegion0(final Container container, final int n, final int n2, final int n3, final int n4) {
        if (n3 <= 0 || n4 <= 0 || container == null) {
            return;
        }
        if (container.getWidth() <= 0 || container.getHeight() <= 0) {
            return;
        }
        if (this.extendDirtyRegion(container, n, n2, n3, n4)) {
            return;
        }
        Frame frame = null;
        Container parent = container;
        while (parent != null) {
            if (!parent.isVisible() || parent.getPeer() == null) {
                return;
            }
            if (parent instanceof Window || parent instanceof Applet) {
                if (parent instanceof Frame && (((Frame)parent).getExtendedState() & 0x1) == 0x1) {
                    return;
                }
                frame = (Frame)parent;
                break;
            }
            else {
                parent = parent.getParent();
            }
        }
        if (frame == null) {
            return;
        }
        synchronized (this) {
            if (this.extendDirtyRegion(container, n, n2, n3, n4)) {
                return;
            }
            this.dirtyComponents.put(container, new Rectangle(n, n2, n3, n4));
        }
        this.scheduleProcessingRunnable(SunToolkit.targetToAppContext(container));
    }
    
    public void addDirtyRegion(final JComponent component, final int n, final int n2, final int n3, final int n4) {
        final RepaintManager delegate = this.getDelegate(component);
        if (delegate != null) {
            delegate.addDirtyRegion(component, n, n2, n3, n4);
            return;
        }
        this.addDirtyRegion0(component, n, n2, n3, n4);
    }
    
    public void addDirtyRegion(final Window window, final int n, final int n2, final int n3, final int n4) {
        this.addDirtyRegion0(window, n, n2, n3, n4);
    }
    
    public void addDirtyRegion(final Applet applet, final int n, final int n2, final int n3, final int n4) {
        this.addDirtyRegion0(applet, n, n2, n3, n4);
    }
    
    void scheduleHeavyWeightPaints() {
        final Map<Container, Rectangle> hwDirtyComponents;
        synchronized (this) {
            if (this.hwDirtyComponents.size() == 0) {
                return;
            }
            hwDirtyComponents = this.hwDirtyComponents;
            this.hwDirtyComponents = new IdentityHashMap<Container, Rectangle>();
        }
        for (final Container container : hwDirtyComponents.keySet()) {
            final Rectangle rectangle = hwDirtyComponents.get(container);
            if (container instanceof Window) {
                this.addDirtyRegion((Window)container, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            }
            else if (container instanceof Applet) {
                this.addDirtyRegion((Applet)container, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            }
            else {
                this.addDirtyRegion0(container, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            }
        }
    }
    
    void nativeAddDirtyRegion(final AppContext appContext, final Container container, final int n, final int n2, final int n3, final int n4) {
        if (n3 > 0 && n4 > 0) {
            synchronized (this) {
                final Rectangle rectangle = this.hwDirtyComponents.get(container);
                if (rectangle == null) {
                    this.hwDirtyComponents.put(container, new Rectangle(n, n2, n3, n4));
                }
                else {
                    this.hwDirtyComponents.put(container, SwingUtilities.computeUnion(n, n2, n3, n4, rectangle));
                }
            }
            this.scheduleProcessingRunnable(appContext);
        }
    }
    
    void nativeQueueSurfaceDataRunnable(final AppContext appContext, final Component component, final Runnable runnable) {
        synchronized (this) {
            if (this.runnableList == null) {
                this.runnableList = new LinkedList<Runnable>();
            }
            this.runnableList.add(new Runnable() {
                @Override
                public void run() {
                    RepaintManager.javaSecurityAccess.doIntersectionPrivilege((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                        @Override
                        public Void run() {
                            runnable.run();
                            return null;
                        }
                    }, AccessController.getContext(), AWTAccessor.getComponentAccessor().getAccessControlContext(component));
                }
            });
        }
        this.scheduleProcessingRunnable(appContext);
    }
    
    private synchronized boolean extendDirtyRegion(final Component component, final int n, final int n2, final int n3, final int n4) {
        final Rectangle rectangle = this.dirtyComponents.get(component);
        if (rectangle != null) {
            SwingUtilities.computeUnion(n, n2, n3, n4, rectangle);
            return true;
        }
        return false;
    }
    
    public Rectangle getDirtyRegion(final JComponent component) {
        final RepaintManager delegate = this.getDelegate(component);
        if (delegate != null) {
            return delegate.getDirtyRegion(component);
        }
        final Rectangle rectangle;
        synchronized (this) {
            rectangle = this.dirtyComponents.get(component);
        }
        if (rectangle == null) {
            return new Rectangle(0, 0, 0, 0);
        }
        return new Rectangle(rectangle);
    }
    
    public void markCompletelyDirty(final JComponent component) {
        final RepaintManager delegate = this.getDelegate(component);
        if (delegate != null) {
            delegate.markCompletelyDirty(component);
            return;
        }
        this.addDirtyRegion(component, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    public void markCompletelyClean(final JComponent component) {
        final RepaintManager delegate = this.getDelegate(component);
        if (delegate != null) {
            delegate.markCompletelyClean(component);
            return;
        }
        synchronized (this) {
            this.dirtyComponents.remove(component);
        }
    }
    
    public boolean isCompletelyDirty(final JComponent component) {
        final RepaintManager delegate = this.getDelegate(component);
        if (delegate != null) {
            return delegate.isCompletelyDirty(component);
        }
        final Rectangle dirtyRegion = this.getDirtyRegion(component);
        return dirtyRegion.width == Integer.MAX_VALUE && dirtyRegion.height == Integer.MAX_VALUE;
    }
    
    public void validateInvalidComponents() {
        final List<Component> invalidComponents;
        synchronized (this) {
            if (this.invalidComponents == null) {
                return;
            }
            invalidComponents = this.invalidComponents;
            this.invalidComponents = null;
        }
        for (int size = invalidComponents.size(), i = 0; i < size; ++i) {
            final Component component = invalidComponents.get(i);
            RepaintManager.javaSecurityAccess.doIntersectionPrivilege((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    component.validate();
                    return null;
                }
            }, AccessController.getContext(), AWTAccessor.getComponentAccessor().getAccessControlContext(component));
        }
    }
    
    private void prePaintDirtyRegions() {
        final Map<Component, Rectangle> dirtyComponents;
        final List<Runnable> runnableList;
        synchronized (this) {
            dirtyComponents = this.dirtyComponents;
            runnableList = this.runnableList;
            this.runnableList = null;
        }
        if (runnableList != null) {
            final Iterator<Runnable> iterator = runnableList.iterator();
            while (iterator.hasNext()) {
                iterator.next().run();
            }
        }
        this.paintDirtyRegions();
        if (dirtyComponents.size() > 0) {
            this.paintDirtyRegions(dirtyComponents);
        }
    }
    
    private void updateWindows(final Map<Component, Rectangle> map) {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (!(defaultToolkit instanceof SunToolkit) || !((SunToolkit)defaultToolkit).needUpdateWindow()) {
            return;
        }
        final HashSet set = new HashSet();
        for (final Component component : map.keySet()) {
            final Window window = (Window)((component instanceof Window) ? component : SwingUtilities.getWindowAncestor(component));
            if (window != null && !window.isOpaque()) {
                set.add(window);
            }
        }
        final Iterator iterator2 = set.iterator();
        while (iterator2.hasNext()) {
            AWTAccessor.getWindowAccessor().updateWindow((Window)iterator2.next());
        }
    }
    
    boolean isPainting() {
        return this.painting;
    }
    
    public void paintDirtyRegions() {
        synchronized (this) {
            final Map<Component, Rectangle> tmpDirtyComponents = this.tmpDirtyComponents;
            this.tmpDirtyComponents = this.dirtyComponents;
            (this.dirtyComponents = tmpDirtyComponents).clear();
        }
        this.paintDirtyRegions(this.tmpDirtyComponents);
    }
    
    private void paintDirtyRegions(final Map<Component, Rectangle> map) {
        if (map.isEmpty()) {
            return;
        }
        final ArrayList list = new ArrayList(map.size());
        final Iterator<Component> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            this.collectDirtyComponents(map, iterator.next(), list);
        }
        final AtomicInteger atomicInteger = new AtomicInteger(list.size());
        this.painting = true;
        try {
            for (int i = 0; i < atomicInteger.get(); ++i) {
                final int n = i;
                final Component component = (Component)list.get(i);
                RepaintManager.javaSecurityAccess.doIntersectionPrivilege((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        final Rectangle rectangle = map.get(component);
                        if (rectangle == null) {
                            return null;
                        }
                        SwingUtilities.computeIntersection(0, 0, component.getWidth(), component.getHeight(), rectangle);
                        if (component instanceof JComponent) {
                            ((JComponent)component).paintImmediately(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                        }
                        else if (component.isShowing()) {
                            final Graphics safelyGetGraphics = JComponent.safelyGetGraphics(component, component);
                            if (safelyGetGraphics != null) {
                                safelyGetGraphics.setClip(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                                try {
                                    component.paint(safelyGetGraphics);
                                }
                                finally {
                                    safelyGetGraphics.dispose();
                                }
                            }
                        }
                        if (RepaintManager.this.repaintRoot != null) {
                            RepaintManager.this.adjustRoots(RepaintManager.this.repaintRoot, list, n + 1);
                            atomicInteger.set(list.size());
                            RepaintManager.this.paintManager.isRepaintingRoot = true;
                            RepaintManager.this.repaintRoot.paintImmediately(0, 0, RepaintManager.this.repaintRoot.getWidth(), RepaintManager.this.repaintRoot.getHeight());
                            RepaintManager.this.paintManager.isRepaintingRoot = false;
                            RepaintManager.this.repaintRoot = null;
                        }
                        return null;
                    }
                }, AccessController.getContext(), AWTAccessor.getComponentAccessor().getAccessControlContext(component));
            }
        }
        finally {
            this.painting = false;
        }
        this.updateWindows(map);
        map.clear();
    }
    
    private void adjustRoots(final JComponent component, final List<Component> list, final int n) {
        for (int i = list.size() - 1; i >= n; --i) {
            Component parent;
            for (parent = list.get(i); parent != component && parent != null && parent instanceof JComponent; parent = parent.getParent()) {}
            if (parent == component) {
                list.remove(i);
            }
        }
    }
    
    void collectDirtyComponents(final Map<Component, Rectangle> map, final Component component, final List<Component> list) {
        Component component2 = component;
        Component component3 = component;
        int n = component.getX();
        int n2 = component.getY();
        final int width = component.getWidth();
        final int height = component.getHeight();
        int n4;
        int n3 = n4 = 0;
        int n6;
        int n5 = n6 = 0;
        this.tmp.setBounds(map.get(component));
        SwingUtilities.computeIntersection(0, 0, width, height, this.tmp);
        if (this.tmp.isEmpty()) {
            return;
        }
        while (true) {
            while (component3 instanceof JComponent) {
                final Container parent = component3.getParent();
                if (parent == null) {
                    if (component != component2) {
                        this.tmp.setLocation(this.tmp.x + n3 - n4, this.tmp.y + n5 - n6);
                        SwingUtilities.computeUnion(this.tmp.x, this.tmp.y, this.tmp.width, this.tmp.height, map.get(component2));
                    }
                    if (!list.contains(component2)) {
                        list.add(component2);
                    }
                    return;
                }
                component3 = parent;
                n4 += n;
                n6 += n2;
                this.tmp.setLocation(this.tmp.x + n, this.tmp.y + n2);
                n = component3.getX();
                n2 = component3.getY();
                this.tmp = SwingUtilities.computeIntersection(0, 0, component3.getWidth(), component3.getHeight(), this.tmp);
                if (this.tmp.isEmpty()) {
                    return;
                }
                if (map.get(component3) == null) {
                    continue;
                }
                component2 = component3;
                n3 = n4;
                n5 = n6;
            }
            continue;
        }
    }
    
    @Override
    public synchronized String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.dirtyComponents != null) {
            sb.append("" + this.dirtyComponents);
        }
        return sb.toString();
    }
    
    public Image getOffscreenBuffer(final Component component, final int n, final int n2) {
        final RepaintManager delegate = this.getDelegate(component);
        if (delegate != null) {
            return delegate.getOffscreenBuffer(component, n, n2);
        }
        return this._getOffscreenBuffer(component, n, n2);
    }
    
    public Image getVolatileOffscreenBuffer(final Component component, final int n, final int n2) {
        final RepaintManager delegate = this.getDelegate(component);
        if (delegate != null) {
            return delegate.getVolatileOffscreenBuffer(component, n, n2);
        }
        if (!((Window)((component instanceof Window) ? component : SwingUtilities.getWindowAncestor(component))).isOpaque()) {
            final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            if (defaultToolkit instanceof SunToolkit && ((SunToolkit)defaultToolkit).needUpdateWindow()) {
                return null;
            }
        }
        GraphicsConfiguration graphicsConfiguration = component.getGraphicsConfiguration();
        if (graphicsConfiguration == null) {
            graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        }
        final Dimension doubleBufferMaximumSize = this.getDoubleBufferMaximumSize();
        final int n3 = (n < 1) ? 1 : ((n > doubleBufferMaximumSize.width) ? doubleBufferMaximumSize.width : n);
        final int n4 = (n2 < 1) ? 1 : ((n2 > doubleBufferMaximumSize.height) ? doubleBufferMaximumSize.height : n2);
        VolatileImage compatibleVolatileImage = this.volatileMap.get(graphicsConfiguration);
        if (compatibleVolatileImage == null || compatibleVolatileImage.getWidth() < n3 || compatibleVolatileImage.getHeight() < n4) {
            if (compatibleVolatileImage != null) {
                compatibleVolatileImage.flush();
            }
            compatibleVolatileImage = graphicsConfiguration.createCompatibleVolatileImage(n3, n4, RepaintManager.volatileBufferType);
            this.volatileMap.put(graphicsConfiguration, compatibleVolatileImage);
        }
        return compatibleVolatileImage;
    }
    
    private Image _getOffscreenBuffer(final Component component, final int n, final int n2) {
        final Dimension doubleBufferMaximumSize = this.getDoubleBufferMaximumSize();
        if (!((Window)((component instanceof Window) ? component : SwingUtilities.getWindowAncestor(component))).isOpaque()) {
            final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            if (defaultToolkit instanceof SunToolkit && ((SunToolkit)defaultToolkit).needUpdateWindow()) {
                return null;
            }
        }
        if (this.standardDoubleBuffer == null) {
            this.standardDoubleBuffer = new DoubleBufferInfo();
        }
        final DoubleBufferInfo standardDoubleBuffer = this.standardDoubleBuffer;
        int max = (n < 1) ? 1 : ((n > doubleBufferMaximumSize.width) ? doubleBufferMaximumSize.width : n);
        int max2 = (n2 < 1) ? 1 : ((n2 > doubleBufferMaximumSize.height) ? doubleBufferMaximumSize.height : n2);
        if (standardDoubleBuffer.needsReset || (standardDoubleBuffer.image != null && (standardDoubleBuffer.size.width < max || standardDoubleBuffer.size.height < max2))) {
            standardDoubleBuffer.needsReset = false;
            if (standardDoubleBuffer.image != null) {
                standardDoubleBuffer.image.flush();
                standardDoubleBuffer.image = null;
            }
            max = Math.max(standardDoubleBuffer.size.width, max);
            max2 = Math.max(standardDoubleBuffer.size.height, max2);
        }
        Image image = standardDoubleBuffer.image;
        if (standardDoubleBuffer.image == null) {
            image = component.createImage(max, max2);
            standardDoubleBuffer.size = new Dimension(max, max2);
            if (component instanceof JComponent) {
                ((JComponent)component).setCreatedDoubleBuffer(true);
                standardDoubleBuffer.image = image;
            }
        }
        return image;
    }
    
    public void setDoubleBufferMaximumSize(final Dimension doubleBufferMaxSize) {
        this.doubleBufferMaxSize = doubleBufferMaxSize;
        if (this.doubleBufferMaxSize == null) {
            this.clearImages();
        }
        else {
            this.clearImages(doubleBufferMaxSize.width, doubleBufferMaxSize.height);
        }
    }
    
    private void clearImages() {
        this.clearImages(0, 0);
    }
    
    private void clearImages(final int n, final int n2) {
        if (this.standardDoubleBuffer != null && this.standardDoubleBuffer.image != null && (this.standardDoubleBuffer.image.getWidth(null) > n || this.standardDoubleBuffer.image.getHeight(null) > n2)) {
            this.standardDoubleBuffer.image.flush();
            this.standardDoubleBuffer.image = null;
        }
        final Iterator<GraphicsConfiguration> iterator = this.volatileMap.keySet().iterator();
        while (iterator.hasNext()) {
            final VolatileImage volatileImage = this.volatileMap.get(iterator.next());
            if (volatileImage.getWidth() > n || volatileImage.getHeight() > n2) {
                volatileImage.flush();
                iterator.remove();
            }
        }
    }
    
    public Dimension getDoubleBufferMaximumSize() {
        if (this.doubleBufferMaxSize == null) {
            try {
                Rectangle union = new Rectangle();
                final GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
                for (int length = screenDevices.length, i = 0; i < length; ++i) {
                    union = union.union(screenDevices[i].getDefaultConfiguration().getBounds());
                }
                this.doubleBufferMaxSize = new Dimension(union.width, union.height);
            }
            catch (final HeadlessException ex) {
                this.doubleBufferMaxSize = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
            }
        }
        return this.doubleBufferMaxSize;
    }
    
    public void setDoubleBufferingEnabled(final boolean doubleBufferingEnabled) {
        this.doubleBufferingEnabled = doubleBufferingEnabled;
        final PaintManager paintManager = this.getPaintManager();
        if (!doubleBufferingEnabled && paintManager.getClass() != PaintManager.class) {
            this.setPaintManager(new PaintManager());
        }
    }
    
    public boolean isDoubleBufferingEnabled() {
        return this.doubleBufferingEnabled;
    }
    
    void resetDoubleBuffer() {
        if (this.standardDoubleBuffer != null) {
            this.standardDoubleBuffer.needsReset = true;
        }
    }
    
    void resetVolatileDoubleBuffer(final GraphicsConfiguration graphicsConfiguration) {
        final Image image = this.volatileMap.remove(graphicsConfiguration);
        if (image != null) {
            image.flush();
        }
    }
    
    boolean useVolatileDoubleBuffer() {
        return RepaintManager.volatileImageBufferEnabled;
    }
    
    private synchronized boolean isPaintingThread() {
        return Thread.currentThread() == this.paintThread;
    }
    
    void paint(final JComponent component, final JComponent component2, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        PaintManager paintManager = this.getPaintManager();
        if (!this.isPaintingThread() && paintManager.getClass() != PaintManager.class) {
            paintManager = new PaintManager();
            paintManager.repaintManager = this;
        }
        if (!paintManager.paint(component, component2, graphics, n, n2, n3, n4)) {
            graphics.setClip(n, n2, n3, n4);
            component.paintToOffscreen(graphics, n, n2, n3, n4, n + n3, n2 + n4);
        }
    }
    
    void copyArea(final JComponent component, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final boolean b) {
        this.getPaintManager().copyArea(component, graphics, n, n2, n3, n4, n5, n6, b);
    }
    
    private void addRepaintListener(final SwingUtilities2.RepaintListener repaintListener) {
        this.repaintListeners.add(repaintListener);
    }
    
    private void removeRepaintListener(final SwingUtilities2.RepaintListener repaintListener) {
        this.repaintListeners.remove(repaintListener);
    }
    
    void notifyRepaintPerformed(final JComponent component, final int n, final int n2, final int n3, final int n4) {
        final Iterator<SwingUtilities2.RepaintListener> iterator = this.repaintListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().repaintPerformed(component, n, n2, n3, n4);
        }
    }
    
    void beginPaint() {
        boolean b = false;
        final Thread currentThread = Thread.currentThread();
        final int paintDepth;
        synchronized (this) {
            paintDepth = this.paintDepth;
            if (this.paintThread == null || currentThread == this.paintThread) {
                this.paintThread = currentThread;
                ++this.paintDepth;
            }
            else {
                b = true;
            }
        }
        if (!b && paintDepth == 0) {
            this.getPaintManager().beginPaint();
        }
    }
    
    void endPaint() {
        if (this.isPaintingThread()) {
            PaintManager paintManager = null;
            synchronized (this) {
                final int paintDepth = this.paintDepth - 1;
                this.paintDepth = paintDepth;
                if (paintDepth == 0) {
                    paintManager = this.getPaintManager();
                }
            }
            if (paintManager != null) {
                paintManager.endPaint();
                synchronized (this) {
                    this.paintThread = null;
                }
            }
        }
    }
    
    boolean show(final Container container, final int n, final int n2, final int n3, final int n4) {
        return this.getPaintManager().show(container, n, n2, n3, n4);
    }
    
    void doubleBufferingChanged(final JRootPane rootPane) {
        this.getPaintManager().doubleBufferingChanged(rootPane);
    }
    
    void setPaintManager(PaintManager paintManager) {
        if (paintManager == null) {
            paintManager = new PaintManager();
        }
        final PaintManager paintManager2;
        synchronized (this) {
            paintManager2 = this.paintManager;
            this.paintManager = paintManager;
            paintManager.repaintManager = this;
        }
        if (paintManager2 != null) {
            paintManager2.dispose();
        }
    }
    
    private synchronized PaintManager getPaintManager() {
        if (this.paintManager == null) {
            PaintManager paintManager = null;
            if (this.doubleBufferingEnabled && !RepaintManager.nativeDoubleBuffering) {
                switch (this.bufferStrategyType) {
                    case 0: {
                        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                        if (defaultToolkit instanceof SunToolkit) {
                            if (((SunToolkit)defaultToolkit).useBufferPerWindow()) {
                                paintManager = new BufferStrategyPaintManager();
                            }
                            break;
                        }
                        break;
                    }
                    case 1: {
                        paintManager = new BufferStrategyPaintManager();
                        break;
                    }
                }
            }
            this.setPaintManager(paintManager);
        }
        return this.paintManager;
    }
    
    private void scheduleProcessingRunnable(final AppContext appContext) {
        if (this.processingRunnable.markPending()) {
            if (Toolkit.getDefaultToolkit() instanceof SunToolkit) {
                SunToolkit.getSystemEventQueueImplPP(appContext).postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), this.processingRunnable));
            }
            else {
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), this.processingRunnable));
            }
        }
    }
    
    private RepaintManager getDelegate(final Component component) {
        RepaintManager delegateRepaintManager = SwingUtilities3.getDelegateRepaintManager(component);
        if (this == delegateRepaintManager) {
            delegateRepaintManager = null;
        }
        return delegateRepaintManager;
    }
    
    static {
        repaintManagerKey = RepaintManager.class;
        RepaintManager.volatileImageBufferEnabled = true;
        javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
        displayChangedHandler = new DisplayChangedHandler();
        SwingAccessor.setRepaintManagerAccessor(new SwingAccessor.RepaintManagerAccessor() {
            @Override
            public void addRepaintListener(final RepaintManager repaintManager, final SwingUtilities2.RepaintListener repaintListener) {
                repaintManager.addRepaintListener(repaintListener);
            }
            
            @Override
            public void removeRepaintListener(final RepaintManager repaintManager, final SwingUtilities2.RepaintListener repaintListener) {
                repaintManager.removeRepaintListener(repaintListener);
            }
        });
        RepaintManager.volatileImageBufferEnabled = "true".equals(AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("swing.volatileImageBufferEnabled", "true")));
        final boolean headless = GraphicsEnvironment.isHeadless();
        if (RepaintManager.volatileImageBufferEnabled && headless) {
            RepaintManager.volatileImageBufferEnabled = false;
        }
        RepaintManager.nativeDoubleBuffering = "true".equals(AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("awt.nativeDoubleBuffering")));
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("swing.bufferPerWindow"));
        if (headless) {
            BUFFER_STRATEGY_TYPE = 2;
        }
        else if (s == null) {
            BUFFER_STRATEGY_TYPE = 0;
        }
        else if ("true".equals(s)) {
            BUFFER_STRATEGY_TYPE = 1;
        }
        else {
            BUFFER_STRATEGY_TYPE = 2;
        }
        HANDLE_TOP_LEVEL_PAINT = "true".equals(AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("swing.handleTopLevelPaint", "true")));
        final GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (localGraphicsEnvironment instanceof SunGraphicsEnvironment) {
            ((SunGraphicsEnvironment)localGraphicsEnvironment).addDisplayChangedListener(RepaintManager.displayChangedHandler);
        }
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (defaultToolkit instanceof SunToolkit && ((SunToolkit)defaultToolkit).isSwingBackbufferTranslucencySupported()) {
            volatileBufferType = 3;
        }
        else {
            volatileBufferType = 1;
        }
    }
    
    static class PaintManager
    {
        protected RepaintManager repaintManager;
        boolean isRepaintingRoot;
        
        public boolean paint(final JComponent component, final JComponent component2, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            boolean b = false;
            final Image validImage;
            if (this.repaintManager.useVolatileDoubleBuffer() && (validImage = this.getValidImage(this.repaintManager.getVolatileOffscreenBuffer(component2, n3, n4))) != null) {
                VolatileImage volatileImage = (VolatileImage)validImage;
                final GraphicsConfiguration graphicsConfiguration = component2.getGraphicsConfiguration();
                for (int n5 = 0; !b && n5 < 2; b = !volatileImage.contentsLost(), ++n5) {
                    if (volatileImage.validate(graphicsConfiguration) == 2) {
                        this.repaintManager.resetVolatileDoubleBuffer(graphicsConfiguration);
                        volatileImage = (VolatileImage)this.repaintManager.getVolatileOffscreenBuffer(component2, n3, n4);
                    }
                    this.paintDoubleBuffered(component, volatileImage, graphics, n, n2, n3, n4);
                }
            }
            final Image validImage2;
            if (!b && (validImage2 = this.getValidImage(this.repaintManager.getOffscreenBuffer(component2, n3, n4))) != null) {
                this.paintDoubleBuffered(component, validImage2, graphics, n, n2, n3, n4);
                b = true;
            }
            return b;
        }
        
        public void copyArea(final JComponent component, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final boolean b) {
            graphics.copyArea(n, n2, n3, n4, n5, n6);
        }
        
        public void beginPaint() {
        }
        
        public void endPaint() {
        }
        
        public boolean show(final Container container, final int n, final int n2, final int n3, final int n4) {
            return false;
        }
        
        public void doubleBufferingChanged(final JRootPane rootPane) {
        }
        
        protected void paintDoubleBuffered(final JComponent component, final Image image, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Graphics graphics2 = image.getGraphics();
            final int min = Math.min(n3, image.getWidth(null));
            final int min2 = Math.min(n4, image.getHeight(null));
            try {
                for (int i = n, n5 = n + n3; i < n5; i += min) {
                    for (int j = n2, n6 = n2 + n4; j < n6; j += min2) {
                        graphics2.translate(-i, -j);
                        graphics2.setClip(i, j, min, min2);
                        if (RepaintManager.volatileBufferType != 1 && graphics2 instanceof Graphics2D) {
                            final Graphics2D graphics2D = (Graphics2D)graphics2;
                            final Color background = graphics2D.getBackground();
                            graphics2D.setBackground(component.getBackground());
                            graphics2D.clearRect(i, j, min, min2);
                            graphics2D.setBackground(background);
                        }
                        component.paintToOffscreen(graphics2, i, j, min, min2, n5, n6);
                        graphics.setClip(i, j, min, min2);
                        if (RepaintManager.volatileBufferType != 1 && graphics instanceof Graphics2D) {
                            final Graphics2D graphics2D2 = (Graphics2D)graphics;
                            final Composite composite = graphics2D2.getComposite();
                            graphics2D2.setComposite(AlphaComposite.Src);
                            graphics2D2.drawImage(image, i, j, component);
                            graphics2D2.setComposite(composite);
                        }
                        else {
                            graphics.drawImage(image, i, j, component);
                        }
                        graphics2.translate(i, j);
                    }
                }
            }
            finally {
                graphics2.dispose();
            }
        }
        
        private Image getValidImage(final Image image) {
            if (image != null && image.getWidth(null) > 0 && image.getHeight(null) > 0) {
                return image;
            }
            return null;
        }
        
        protected void repaintRoot(final JComponent component) {
            assert this.repaintManager.repaintRoot == null;
            if (this.repaintManager.painting) {
                this.repaintManager.repaintRoot = component;
            }
            else {
                component.repaint();
            }
        }
        
        protected boolean isRepaintingRoot() {
            return this.isRepaintingRoot;
        }
        
        protected void dispose() {
        }
    }
    
    private class DoubleBufferInfo
    {
        public Image image;
        public Dimension size;
        public boolean needsReset;
        
        private DoubleBufferInfo() {
            this.needsReset = false;
        }
    }
    
    private static final class DisplayChangedHandler implements DisplayChangedListener
    {
        DisplayChangedHandler() {
        }
        
        @Override
        public void displayChanged() {
            scheduleDisplayChanges();
        }
        
        @Override
        public void paletteChanged() {
        }
        
        private static void scheduleDisplayChanges() {
            for (final AppContext appContext : AppContext.getAppContexts()) {
                synchronized (appContext) {
                    if (appContext.isDisposed()) {
                        continue;
                    }
                    final EventQueue eventQueue = (EventQueue)appContext.get(AppContext.EVENT_QUEUE_KEY);
                    if (eventQueue == null) {
                        continue;
                    }
                    eventQueue.postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), new DisplayChangedRunnable()));
                }
            }
        }
    }
    
    private static final class DisplayChangedRunnable implements Runnable
    {
        @Override
        public void run() {
            RepaintManager.currentManager(null).displayChanged();
        }
    }
    
    private final class ProcessingRunnable implements Runnable
    {
        private boolean pending;
        
        public synchronized boolean markPending() {
            return !this.pending && (this.pending = true);
        }
        
        @Override
        public void run() {
            synchronized (this) {
                this.pending = false;
            }
            RepaintManager.this.scheduleHeavyWeightPaints();
            RepaintManager.this.validateInvalidComponents();
            RepaintManager.this.prePaintDirtyRegions();
        }
    }
}
