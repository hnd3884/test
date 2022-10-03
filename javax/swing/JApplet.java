package javax.swing;

import java.awt.Graphics;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Container;
import sun.awt.SunToolkit;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.accessibility.AccessibleContext;
import javax.accessibility.Accessible;
import java.applet.Applet;

public class JApplet extends Applet implements Accessible, RootPaneContainer, TransferHandler.HasGetTransferHandler
{
    protected JRootPane rootPane;
    protected boolean rootPaneCheckingEnabled;
    private TransferHandler transferHandler;
    protected AccessibleContext accessibleContext;
    
    public JApplet() throws HeadlessException {
        this.rootPaneCheckingEnabled = false;
        this.accessibleContext = null;
        final TimerQueue sharedInstance = TimerQueue.sharedInstance();
        if (sharedInstance != null) {
            sharedInstance.startIfNeeded();
        }
        this.setForeground(Color.black);
        this.setBackground(Color.white);
        this.setLocale(JComponent.getDefaultLocale());
        this.setLayout(new BorderLayout());
        this.setRootPane(this.createRootPane());
        this.setRootPaneCheckingEnabled(true);
        this.setFocusTraversalPolicyProvider(true);
        SunToolkit.checkAndSetPolicy(this);
        this.enableEvents(8L);
    }
    
    protected JRootPane createRootPane() {
        final JRootPane rootPane = new JRootPane();
        rootPane.setOpaque(true);
        return rootPane;
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
    
    public void setJMenuBar(final JMenuBar menuBar) {
        this.getRootPane().setMenuBar(menuBar);
    }
    
    public JMenuBar getJMenuBar() {
        return this.getRootPane().getMenuBar();
    }
    
    protected boolean isRootPaneCheckingEnabled() {
        return this.rootPaneCheckingEnabled;
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
        return super.paramString() + ",rootPane=" + ((this.rootPane != null) ? this.rootPane.toString() : "") + ",rootPaneCheckingEnabled=" + (this.rootPaneCheckingEnabled ? "true" : "false");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJApplet();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleJApplet extends AccessibleApplet
    {
    }
}
