package javax.swing;

import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import java.awt.Window;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.Container;
import sun.awt.SunToolkit;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import javax.accessibility.AccessibleContext;
import javax.accessibility.Accessible;
import java.awt.Frame;

public class JFrame extends Frame implements WindowConstants, Accessible, RootPaneContainer, TransferHandler.HasGetTransferHandler
{
    public static final int EXIT_ON_CLOSE = 3;
    private static final Object defaultLookAndFeelDecoratedKey;
    private int defaultCloseOperation;
    private TransferHandler transferHandler;
    protected JRootPane rootPane;
    protected boolean rootPaneCheckingEnabled;
    protected AccessibleContext accessibleContext;
    
    public JFrame() throws HeadlessException {
        this.defaultCloseOperation = 1;
        this.rootPaneCheckingEnabled = false;
        this.accessibleContext = null;
        this.frameInit();
    }
    
    public JFrame(final GraphicsConfiguration graphicsConfiguration) {
        super(graphicsConfiguration);
        this.defaultCloseOperation = 1;
        this.rootPaneCheckingEnabled = false;
        this.accessibleContext = null;
        this.frameInit();
    }
    
    public JFrame(final String s) throws HeadlessException {
        super(s);
        this.defaultCloseOperation = 1;
        this.rootPaneCheckingEnabled = false;
        this.accessibleContext = null;
        this.frameInit();
    }
    
    public JFrame(final String s, final GraphicsConfiguration graphicsConfiguration) {
        super(s, graphicsConfiguration);
        this.defaultCloseOperation = 1;
        this.rootPaneCheckingEnabled = false;
        this.accessibleContext = null;
        this.frameInit();
    }
    
    protected void frameInit() {
        this.enableEvents(72L);
        this.setLocale(JComponent.getDefaultLocale());
        this.setRootPane(this.createRootPane());
        this.setBackground(UIManager.getColor("control"));
        this.setRootPaneCheckingEnabled(true);
        if (isDefaultLookAndFeelDecorated() && UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            this.setUndecorated(true);
            this.getRootPane().setWindowDecorationStyle(1);
        }
        SunToolkit.checkAndSetPolicy(this);
    }
    
    protected JRootPane createRootPane() {
        final JRootPane rootPane = new JRootPane();
        rootPane.setOpaque(true);
        return rootPane;
    }
    
    @Override
    protected void processWindowEvent(final WindowEvent windowEvent) {
        super.processWindowEvent(windowEvent);
        if (windowEvent.getID() == 201) {
            switch (this.defaultCloseOperation) {
                case 1: {
                    this.setVisible(false);
                    break;
                }
                case 2: {
                    this.dispose();
                    break;
                }
                case 3: {
                    System.exit(0);
                    break;
                }
            }
        }
    }
    
    public void setDefaultCloseOperation(final int defaultCloseOperation) {
        if (defaultCloseOperation != 0 && defaultCloseOperation != 1 && defaultCloseOperation != 2 && defaultCloseOperation != 3) {
            throw new IllegalArgumentException("defaultCloseOperation must be one of: DO_NOTHING_ON_CLOSE, HIDE_ON_CLOSE, DISPOSE_ON_CLOSE, or EXIT_ON_CLOSE");
        }
        if (defaultCloseOperation == 3) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkExit(0);
            }
        }
        if (this.defaultCloseOperation != defaultCloseOperation) {
            this.firePropertyChange("defaultCloseOperation", this.defaultCloseOperation, this.defaultCloseOperation = defaultCloseOperation);
        }
    }
    
    public int getDefaultCloseOperation() {
        return this.defaultCloseOperation;
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
    public void setIconImage(final Image iconImage) {
        super.setIconImage(iconImage);
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
    
    public static void setDefaultLookAndFeelDecorated(final boolean b) {
        if (b) {
            SwingUtilities.appContextPut(JFrame.defaultLookAndFeelDecoratedKey, Boolean.TRUE);
        }
        else {
            SwingUtilities.appContextPut(JFrame.defaultLookAndFeelDecoratedKey, Boolean.FALSE);
        }
    }
    
    public static boolean isDefaultLookAndFeelDecorated() {
        Boolean false = (Boolean)SwingUtilities.appContextGet(JFrame.defaultLookAndFeelDecoratedKey);
        if (false == null) {
            false = Boolean.FALSE;
        }
        return false;
    }
    
    @Override
    protected String paramString() {
        String s;
        if (this.defaultCloseOperation == 1) {
            s = "HIDE_ON_CLOSE";
        }
        else if (this.defaultCloseOperation == 2) {
            s = "DISPOSE_ON_CLOSE";
        }
        else if (this.defaultCloseOperation == 0) {
            s = "DO_NOTHING_ON_CLOSE";
        }
        else if (this.defaultCloseOperation == 3) {
            s = "EXIT_ON_CLOSE";
        }
        else {
            s = "";
        }
        return super.paramString() + ",defaultCloseOperation=" + s + ",rootPane=" + ((this.rootPane != null) ? this.rootPane.toString() : "") + ",rootPaneCheckingEnabled=" + (this.rootPaneCheckingEnabled ? "true" : "false");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJFrame();
        }
        return this.accessibleContext;
    }
    
    static {
        defaultLookAndFeelDecoratedKey = new StringBuffer("JFrame.defaultLookAndFeelDecorated");
    }
    
    protected class AccessibleJFrame extends AccessibleAWTFrame
    {
        @Override
        public String getAccessibleName() {
            if (this.accessibleName != null) {
                return this.accessibleName;
            }
            if (JFrame.this.getTitle() == null) {
                return super.getAccessibleName();
            }
            return JFrame.this.getTitle();
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (JFrame.this.isResizable()) {
                accessibleStateSet.add(AccessibleState.RESIZABLE);
            }
            if (JFrame.this.getFocusOwner() != null) {
                accessibleStateSet.add(AccessibleState.ACTIVE);
            }
            return accessibleStateSet;
        }
    }
}
