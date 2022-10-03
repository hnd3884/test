package javax.swing;

import java.awt.LayoutManager;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Container;
import sun.awt.SunToolkit;
import java.awt.GraphicsConfiguration;
import java.awt.Frame;
import javax.accessibility.AccessibleContext;
import javax.accessibility.Accessible;
import java.awt.Window;

public class JWindow extends Window implements Accessible, RootPaneContainer, TransferHandler.HasGetTransferHandler
{
    protected JRootPane rootPane;
    protected boolean rootPaneCheckingEnabled;
    private TransferHandler transferHandler;
    protected AccessibleContext accessibleContext;
    
    public JWindow() {
        this((Frame)null);
    }
    
    public JWindow(final GraphicsConfiguration graphicsConfiguration) {
        this(null, graphicsConfiguration);
        super.setFocusableWindowState(false);
    }
    
    public JWindow(final Frame frame) {
        super((frame == null) ? SwingUtilities.getSharedOwnerFrame() : frame);
        this.rootPaneCheckingEnabled = false;
        this.accessibleContext = null;
        if (frame == null) {
            this.addWindowListener(SwingUtilities.getSharedOwnerFrameShutdownListener());
        }
        this.windowInit();
    }
    
    public JWindow(final Window window) {
        super((window == null) ? SwingUtilities.getSharedOwnerFrame() : window);
        this.rootPaneCheckingEnabled = false;
        this.accessibleContext = null;
        if (window == null) {
            this.addWindowListener(SwingUtilities.getSharedOwnerFrameShutdownListener());
        }
        this.windowInit();
    }
    
    public JWindow(final Window window, final GraphicsConfiguration graphicsConfiguration) {
        super((window == null) ? SwingUtilities.getSharedOwnerFrame() : window, graphicsConfiguration);
        this.rootPaneCheckingEnabled = false;
        this.accessibleContext = null;
        if (window == null) {
            this.addWindowListener(SwingUtilities.getSharedOwnerFrameShutdownListener());
        }
        this.windowInit();
    }
    
    protected void windowInit() {
        this.setLocale(JComponent.getDefaultLocale());
        this.setRootPane(this.createRootPane());
        this.setRootPaneCheckingEnabled(true);
        SunToolkit.checkAndSetPolicy(this);
    }
    
    protected JRootPane createRootPane() {
        final JRootPane rootPane = new JRootPane();
        rootPane.setOpaque(true);
        return rootPane;
    }
    
    protected boolean isRootPaneCheckingEnabled() {
        return this.rootPaneCheckingEnabled;
    }
    
    public void setTransferHandler(final TransferHandler transferHandler) {
        final TransferHandler transferHandler2 = this.transferHandler;
        SwingUtilities.installSwingDropTargetAsNecessary(this, this.transferHandler = transferHandler);
        this.firePropertyChange("transferHandler", transferHandler2, transferHandler);
    }
    
    @Override
    public TransferHandler getTransferHandler() {
        return this.transferHandler;
    }
    
    @Override
    public void update(final Graphics graphics) {
        this.paint(graphics);
    }
    
    protected void setRootPaneCheckingEnabled(final boolean rootPaneCheckingEnabled) {
        this.rootPaneCheckingEnabled = rootPaneCheckingEnabled;
    }
    
    @Override
    protected void addImpl(final Component component, final Object o, final int n) {
        if (this.isRootPaneCheckingEnabled()) {
            this.getContentPane().add(component, o, n);
        }
        else {
            super.addImpl(component, o, n);
        }
    }
    
    @Override
    public void remove(final Component component) {
        if (component == this.rootPane) {
            super.remove(component);
        }
        else {
            this.getContentPane().remove(component);
        }
    }
    
    @Override
    public void setLayout(final LayoutManager layoutManager) {
        if (this.isRootPaneCheckingEnabled()) {
            this.getContentPane().setLayout(layoutManager);
        }
        else {
            super.setLayout(layoutManager);
        }
    }
    
    @Override
    public JRootPane getRootPane() {
        return this.rootPane;
    }
    
    protected void setRootPane(final JRootPane rootPane) {
        if (this.rootPane != null) {
            this.remove(this.rootPane);
        }
        this.rootPane = rootPane;
        if (this.rootPane != null) {
            final boolean rootPaneCheckingEnabled = this.isRootPaneCheckingEnabled();
            try {
                this.setRootPaneCheckingEnabled(false);
                this.add(this.rootPane, "Center");
            }
            finally {
                this.setRootPaneCheckingEnabled(rootPaneCheckingEnabled);
            }
        }
    }
    
    @Override
    public Container getContentPane() {
        return this.getRootPane().getContentPane();
    }
    
    @Override
    public void setContentPane(final Container contentPane) {
        this.getRootPane().setContentPane(contentPane);
    }
    
    @Override
    public JLayeredPane getLayeredPane() {
        return this.getRootPane().getLayeredPane();
    }
    
    @Override
    public void setLayeredPane(final JLayeredPane layeredPane) {
        this.getRootPane().setLayeredPane(layeredPane);
    }
    
    @Override
    public Component getGlassPane() {
        return this.getRootPane().getGlassPane();
    }
    
    @Override
    public void setGlassPane(final Component glassPane) {
        this.getRootPane().setGlassPane(glassPane);
    }
    
    @Override
    public Graphics getGraphics() {
        JComponent.getGraphicsInvoked(this);
        return super.getGraphics();
    }
    
    @Override
    public void repaint(final long n, final int n2, final int n3, final int n4, final int n5) {
        if (RepaintManager.HANDLE_TOP_LEVEL_PAINT) {
            RepaintManager.currentManager(this).addDirtyRegion(this, n2, n3, n4, n5);
        }
        else {
            super.repaint(n, n2, n3, n4, n5);
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",rootPaneCheckingEnabled=" + (this.rootPaneCheckingEnabled ? "true" : "false");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJWindow();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleJWindow extends AccessibleAWTWindow
    {
    }
}
