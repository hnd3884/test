package sun.swing;

import java.awt.Window;
import javax.swing.JComponent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.peer.ComponentPeer;
import sun.awt.OverrideNativeWindowHandle;
import sun.awt.AWTAccessor;
import java.awt.MouseInfo;
import javax.swing.JLayeredPane;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.image.DataBufferInt;
import sun.awt.DisplayChangedListener;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.RepaintManager;
import java.awt.Rectangle;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.awt.Color;
import java.awt.FocusTraversalPolicy;
import javax.swing.LayoutFocusTraversalPolicy;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.beans.PropertyChangeListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import java.awt.Component;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import sun.awt.LightweightFrame;

public final class JLightweightFrame extends LightweightFrame implements RootPaneContainer
{
    private final JRootPane rootPane;
    private LightweightContent content;
    private Component component;
    private JPanel contentPane;
    private BufferedImage bbImage;
    private volatile int scaleFactor;
    private static boolean copyBufferEnabled;
    private int[] copyBuffer;
    private PropertyChangeListener layoutSizeListener;
    private SwingUtilities2.RepaintListener repaintListener;
    
    public JLightweightFrame() {
        this.rootPane = new JRootPane();
        this.scaleFactor = 1;
        JLightweightFrame.copyBufferEnabled = "true".equals(AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("swing.jlf.copyBufferEnabled", "true")));
        this.add(this.rootPane, "Center");
        this.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy());
        if (this.getGraphicsConfiguration().isTranslucencyCapable()) {
            this.setBackground(new Color(0, 0, 0, 0));
        }
        this.layoutSizeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                final Dimension dimension = (Dimension)propertyChangeEvent.getNewValue();
                if ("preferredSize".equals(propertyChangeEvent.getPropertyName())) {
                    JLightweightFrame.this.content.preferredSizeChanged(dimension.width, dimension.height);
                }
                else if ("maximumSize".equals(propertyChangeEvent.getPropertyName())) {
                    JLightweightFrame.this.content.maximumSizeChanged(dimension.width, dimension.height);
                }
                else if ("minimumSize".equals(propertyChangeEvent.getPropertyName())) {
                    JLightweightFrame.this.content.minimumSizeChanged(dimension.width, dimension.height);
                }
            }
        };
        this.repaintListener = ((component2, n2, n4, n6, n8) -> {
            SwingUtilities.getWindowAncestor(component2);
            final JLightweightFrame lightweightFrame;
            if (lightweightFrame != this) {
                return;
            }
            else {
                SwingUtilities.convertPoint(component2, n2, n4, lightweightFrame);
                final Point point;
                new Rectangle(point.x, point.y, n6, n8).intersection(new Rectangle(0, 0, this.bbImage.getWidth() / this.scaleFactor, this.bbImage.getHeight() / this.scaleFactor));
                final Rectangle rectangle;
                if (!rectangle.isEmpty()) {
                    this.notifyImageUpdated(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                }
                return;
            }
        });
        SwingAccessor.getRepaintManagerAccessor().addRepaintListener(RepaintManager.currentManager(this), this.repaintListener);
    }
    
    @Override
    public void dispose() {
        SwingAccessor.getRepaintManagerAccessor().removeRepaintListener(RepaintManager.currentManager(this), this.repaintListener);
        super.dispose();
    }
    
    public void setContent(final LightweightContent content) {
        if (content == null) {
            System.err.println("JLightweightFrame.setContent: content may not be null!");
            return;
        }
        this.content = content;
        this.component = content.getComponent();
        final Dimension preferredSize = this.component.getPreferredSize();
        content.preferredSizeChanged(preferredSize.width, preferredSize.height);
        final Dimension maximumSize = this.component.getMaximumSize();
        content.maximumSizeChanged(maximumSize.width, maximumSize.height);
        final Dimension minimumSize = this.component.getMinimumSize();
        content.minimumSizeChanged(minimumSize.width, minimumSize.height);
        this.initInterior();
    }
    
    @Override
    public Graphics getGraphics() {
        if (this.bbImage == null) {
            return null;
        }
        final Graphics2D graphics = this.bbImage.createGraphics();
        graphics.setBackground(this.getBackground());
        graphics.setColor(this.getForeground());
        graphics.setFont(this.getFont());
        graphics.scale(this.scaleFactor, this.scaleFactor);
        return graphics;
    }
    
    @Override
    public void grabFocus() {
        if (this.content != null) {
            this.content.focusGrabbed();
        }
    }
    
    @Override
    public void ungrabFocus() {
        if (this.content != null) {
            this.content.focusUngrabbed();
        }
    }
    
    @Override
    public int getScaleFactor() {
        return this.scaleFactor;
    }
    
    @Override
    public void notifyDisplayChanged(final int scaleFactor) {
        if (scaleFactor != this.scaleFactor) {
            if (!JLightweightFrame.copyBufferEnabled) {
                this.content.paintLock();
            }
            try {
                if (this.bbImage != null) {
                    this.resizeBuffer(this.getWidth(), this.getHeight(), scaleFactor);
                }
            }
            finally {
                if (!JLightweightFrame.copyBufferEnabled) {
                    this.content.paintUnlock();
                }
            }
            this.scaleFactor = scaleFactor;
        }
        if (this.getPeer() instanceof DisplayChangedListener) {
            ((DisplayChangedListener)this.getPeer()).displayChanged();
        }
        this.repaint();
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        if (this.getPeer() instanceof DisplayChangedListener) {
            ((DisplayChangedListener)this.getPeer()).displayChanged();
        }
    }
    
    private void syncCopyBuffer(final boolean b, int n, int n2, int n3, int n4, final int n5) {
        this.content.paintLock();
        try {
            final int[] data = ((DataBufferInt)this.bbImage.getRaster().getDataBuffer()).getData();
            if (b) {
                this.copyBuffer = new int[data.length];
            }
            final int width = this.bbImage.getWidth();
            n *= n5;
            n2 *= n5;
            n3 *= n5;
            n4 *= n5;
            for (int i = 0; i < n4; ++i) {
                final int n6 = (n2 + i) * width + n;
                System.arraycopy(data, n6, this.copyBuffer, n6, n3);
            }
        }
        finally {
            this.content.paintUnlock();
        }
    }
    
    private void notifyImageUpdated(final int n, final int n2, final int n3, final int n4) {
        if (JLightweightFrame.copyBufferEnabled) {
            this.syncCopyBuffer(false, n, n2, n3, n4, this.scaleFactor);
        }
        this.content.imageUpdated(n, n2, n3, n4);
    }
    
    private void initInterior() {
        (this.contentPane = new JPanel() {
            @Override
            public void paint(final Graphics graphics) {
                if (!JLightweightFrame.copyBufferEnabled) {
                    JLightweightFrame.this.content.paintLock();
                }
                try {
                    super.paint(graphics);
                    final Rectangle rectangle = (graphics.getClipBounds() != null) ? graphics.getClipBounds() : new Rectangle(0, 0, JLightweightFrame.this.contentPane.getWidth(), JLightweightFrame.this.contentPane.getHeight());
                    rectangle.x = Math.max(0, rectangle.x);
                    rectangle.y = Math.max(0, rectangle.y);
                    rectangle.width = Math.min(JLightweightFrame.this.contentPane.getWidth(), rectangle.width);
                    rectangle.height = Math.min(JLightweightFrame.this.contentPane.getHeight(), rectangle.height);
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            final Rectangle intersection = JLightweightFrame.this.contentPane.getBounds().intersection(rectangle);
                            JLightweightFrame.this.notifyImageUpdated(intersection.x, intersection.y, intersection.width, intersection.height);
                        }
                    });
                }
                finally {
                    if (!JLightweightFrame.copyBufferEnabled) {
                        JLightweightFrame.this.content.paintUnlock();
                    }
                }
            }
            
            @Override
            protected boolean isPaintingOrigin() {
                return true;
            }
        }).setLayout(new BorderLayout());
        this.contentPane.add(this.component);
        if ("true".equals(AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("swing.jlf.contentPaneTransparent", "false")))) {
            this.contentPane.setOpaque(false);
        }
        this.setContentPane(this.contentPane);
        this.contentPane.addContainerListener(new ContainerListener() {
            @Override
            public void componentAdded(final ContainerEvent containerEvent) {
                final Component access$500 = JLightweightFrame.this.component;
                if (containerEvent.getChild() == access$500) {
                    access$500.addPropertyChangeListener("preferredSize", JLightweightFrame.this.layoutSizeListener);
                    access$500.addPropertyChangeListener("maximumSize", JLightweightFrame.this.layoutSizeListener);
                    access$500.addPropertyChangeListener("minimumSize", JLightweightFrame.this.layoutSizeListener);
                }
            }
            
            @Override
            public void componentRemoved(final ContainerEvent containerEvent) {
                final Component access$500 = JLightweightFrame.this.component;
                if (containerEvent.getChild() == access$500) {
                    access$500.removePropertyChangeListener(JLightweightFrame.this.layoutSizeListener);
                }
            }
        });
    }
    
    @Override
    public void reshape(final int n, final int n2, final int n3, final int n4) {
        super.reshape(n, n2, n3, n4);
        if (n3 == 0 || n4 == 0) {
            return;
        }
        if (!JLightweightFrame.copyBufferEnabled) {
            this.content.paintLock();
        }
        try {
            boolean b = this.bbImage == null;
            int max = n3;
            int max2 = n4;
            if (this.bbImage != null) {
                final int n5 = this.bbImage.getWidth() / this.scaleFactor;
                final int n6 = this.bbImage.getHeight() / this.scaleFactor;
                if (n3 != n5 || n4 != n6) {
                    b = true;
                    if (this.bbImage != null) {
                        final int n7 = n5;
                        final int n8 = n6;
                        if (n7 >= max && n8 >= max2) {
                            b = false;
                        }
                        else {
                            if (n7 >= max) {
                                max = n7;
                            }
                            else {
                                max = Math.max((int)(n7 * 1.2), n3);
                            }
                            if (n8 >= max2) {
                                max2 = n8;
                            }
                            else {
                                max2 = Math.max((int)(n8 * 1.2), n4);
                            }
                        }
                    }
                }
            }
            if (b) {
                this.resizeBuffer(max, max2, this.scaleFactor);
                return;
            }
            this.content.imageReshaped(0, 0, n3, n4);
        }
        finally {
            if (!JLightweightFrame.copyBufferEnabled) {
                this.content.paintUnlock();
            }
        }
    }
    
    private void resizeBuffer(final int n, final int n2, final int n3) {
        this.bbImage = new BufferedImage(n * n3, n2 * n3, 3);
        int[] array = ((DataBufferInt)this.bbImage.getRaster().getDataBuffer()).getData();
        if (JLightweightFrame.copyBufferEnabled) {
            this.syncCopyBuffer(true, 0, 0, n, n2, n3);
            array = this.copyBuffer;
        }
        this.content.imageBufferReset(array, 0, 0, n, n2, n * n3, n3);
    }
    
    @Override
    public JRootPane getRootPane() {
        return this.rootPane;
    }
    
    @Override
    public void setContentPane(final Container contentPane) {
        this.getRootPane().setContentPane(contentPane);
    }
    
    @Override
    public Container getContentPane() {
        return this.getRootPane().getContentPane();
    }
    
    @Override
    public void setLayeredPane(final JLayeredPane layeredPane) {
        this.getRootPane().setLayeredPane(layeredPane);
    }
    
    @Override
    public JLayeredPane getLayeredPane() {
        return this.getRootPane().getLayeredPane();
    }
    
    @Override
    public void setGlassPane(final Component glassPane) {
        this.getRootPane().setGlassPane(glassPane);
    }
    
    @Override
    public Component getGlassPane() {
        return this.getRootPane().getGlassPane();
    }
    
    private void updateClientCursor() {
        final Point location = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(location, this);
        final Component deepestComponent = SwingUtilities.getDeepestComponentAt(this, location.x, location.y);
        if (deepestComponent != null) {
            this.content.setCursor(deepestComponent.getCursor());
        }
    }
    
    public void overrideNativeWindowHandle(final long n, final Runnable runnable) {
        final ComponentPeer peer = AWTAccessor.getComponentAccessor().getPeer(this);
        if (peer instanceof OverrideNativeWindowHandle) {
            ((OverrideNativeWindowHandle)peer).overrideWindowHandle(n);
        }
        if (runnable != null) {
            runnable.run();
        }
    }
    
    @Override
    public <T extends DragGestureRecognizer> T createDragGestureRecognizer(final Class<T> clazz, final DragSource dragSource, final Component component, final int n, final DragGestureListener dragGestureListener) {
        return (T)((this.content == null) ? null : this.content.createDragGestureRecognizer(clazz, dragSource, component, n, dragGestureListener));
    }
    
    @Override
    public DragSourceContextPeer createDragSourceContextPeer(final DragGestureEvent dragGestureEvent) throws InvalidDnDOperationException {
        return (this.content == null) ? null : this.content.createDragSourceContextPeer(dragGestureEvent);
    }
    
    @Override
    public void addDropTarget(final DropTarget dropTarget) {
        if (this.content == null) {
            return;
        }
        this.content.addDropTarget(dropTarget);
    }
    
    @Override
    public void removeDropTarget(final DropTarget dropTarget) {
        if (this.content == null) {
            return;
        }
        this.content.removeDropTarget(dropTarget);
    }
    
    static {
        SwingAccessor.setJLightweightFrameAccessor(new SwingAccessor.JLightweightFrameAccessor() {
            @Override
            public void updateCursor(final JLightweightFrame lightweightFrame) {
                lightweightFrame.updateClientCursor();
            }
        });
        JLightweightFrame.copyBufferEnabled = "true".equals(AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("swing.jlf.copyBufferEnabled", "true")));
    }
}
