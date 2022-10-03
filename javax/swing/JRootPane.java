package javax.swing;

import javax.accessibility.AccessibleRole;
import java.awt.Insets;
import java.awt.Dimension;
import java.io.Serializable;
import java.awt.LayoutManager2;
import java.awt.event.ActionEvent;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import javax.accessibility.AccessibleContext;
import java.awt.Shape;
import java.awt.Rectangle;
import sun.awt.AWTAccessor;
import java.awt.IllegalComponentStateException;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.RootPaneUI;
import java.awt.Component;
import java.awt.Container;
import javax.accessibility.Accessible;

public class JRootPane extends JComponent implements Accessible
{
    private static final String uiClassID = "RootPaneUI";
    private static final boolean LOG_DISABLE_TRUE_DOUBLE_BUFFERING;
    private static final boolean IGNORE_DISABLE_TRUE_DOUBLE_BUFFERING;
    public static final int NONE = 0;
    public static final int FRAME = 1;
    public static final int PLAIN_DIALOG = 2;
    public static final int INFORMATION_DIALOG = 3;
    public static final int ERROR_DIALOG = 4;
    public static final int COLOR_CHOOSER_DIALOG = 5;
    public static final int FILE_CHOOSER_DIALOG = 6;
    public static final int QUESTION_DIALOG = 7;
    public static final int WARNING_DIALOG = 8;
    private int windowDecorationStyle;
    protected JMenuBar menuBar;
    protected Container contentPane;
    protected JLayeredPane layeredPane;
    protected Component glassPane;
    protected JButton defaultButton;
    @Deprecated
    protected DefaultAction defaultPressAction;
    @Deprecated
    protected DefaultAction defaultReleaseAction;
    boolean useTrueDoubleBuffering;
    
    public JRootPane() {
        this.useTrueDoubleBuffering = true;
        this.setGlassPane(this.createGlassPane());
        this.setLayeredPane(this.createLayeredPane());
        this.setContentPane(this.createContentPane());
        this.setLayout(this.createRootLayout());
        this.setDoubleBuffered(true);
        this.updateUI();
    }
    
    @Override
    public void setDoubleBuffered(final boolean doubleBuffered) {
        if (this.isDoubleBuffered() != doubleBuffered) {
            super.setDoubleBuffered(doubleBuffered);
            RepaintManager.currentManager(this).doubleBufferingChanged(this);
        }
    }
    
    public int getWindowDecorationStyle() {
        return this.windowDecorationStyle;
    }
    
    public void setWindowDecorationStyle(final int windowDecorationStyle) {
        if (windowDecorationStyle < 0 || windowDecorationStyle > 8) {
            throw new IllegalArgumentException("Invalid decoration style");
        }
        this.firePropertyChange("windowDecorationStyle", this.getWindowDecorationStyle(), this.windowDecorationStyle = windowDecorationStyle);
    }
    
    public RootPaneUI getUI() {
        return (RootPaneUI)this.ui;
    }
    
    public void setUI(final RootPaneUI ui) {
        super.setUI(ui);
    }
    
    @Override
    public void updateUI() {
        this.setUI((RootPaneUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "RootPaneUI";
    }
    
    protected JLayeredPane createLayeredPane() {
        final JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setName(this.getName() + ".layeredPane");
        return layeredPane;
    }
    
    protected Container createContentPane() {
        final JPanel panel = new JPanel();
        panel.setName(this.getName() + ".contentPane");
        panel.setLayout(new BorderLayout() {
            @Override
            public void addLayoutComponent(final Component component, Object o) {
                if (o == null) {
                    o = "Center";
                }
                super.addLayoutComponent(component, o);
            }
        });
        return panel;
    }
    
    protected Component createGlassPane() {
        final JPanel panel = new JPanel();
        panel.setName(this.getName() + ".glassPane");
        panel.setVisible(false);
        panel.setOpaque(false);
        return panel;
    }
    
    protected LayoutManager createRootLayout() {
        return new RootLayout();
    }
    
    public void setJMenuBar(final JMenuBar menuBar) {
        if (this.menuBar != null && this.menuBar.getParent() == this.layeredPane) {
            this.layeredPane.remove(this.menuBar);
        }
        this.menuBar = menuBar;
        if (this.menuBar != null) {
            this.layeredPane.add(this.menuBar, JLayeredPane.FRAME_CONTENT_LAYER);
        }
    }
    
    @Deprecated
    public void setMenuBar(final JMenuBar menuBar) {
        if (this.menuBar != null && this.menuBar.getParent() == this.layeredPane) {
            this.layeredPane.remove(this.menuBar);
        }
        this.menuBar = menuBar;
        if (this.menuBar != null) {
            this.layeredPane.add(this.menuBar, JLayeredPane.FRAME_CONTENT_LAYER);
        }
    }
    
    public JMenuBar getJMenuBar() {
        return this.menuBar;
    }
    
    @Deprecated
    public JMenuBar getMenuBar() {
        return this.menuBar;
    }
    
    public void setContentPane(final Container contentPane) {
        if (contentPane == null) {
            throw new IllegalComponentStateException("contentPane cannot be set to null.");
        }
        if (this.contentPane != null && this.contentPane.getParent() == this.layeredPane) {
            this.layeredPane.remove(this.contentPane);
        }
        this.contentPane = contentPane;
        this.layeredPane.add(this.contentPane, JLayeredPane.FRAME_CONTENT_LAYER);
    }
    
    public Container getContentPane() {
        return this.contentPane;
    }
    
    public void setLayeredPane(final JLayeredPane layeredPane) {
        if (layeredPane == null) {
            throw new IllegalComponentStateException("layeredPane cannot be set to null.");
        }
        if (this.layeredPane != null && this.layeredPane.getParent() == this) {
            this.remove(this.layeredPane);
        }
        this.add(this.layeredPane = layeredPane, -1);
    }
    
    public JLayeredPane getLayeredPane() {
        return this.layeredPane;
    }
    
    public void setGlassPane(final Component glassPane) {
        if (glassPane == null) {
            throw new NullPointerException("glassPane cannot be set to null.");
        }
        AWTAccessor.getComponentAccessor().setMixingCutoutShape(glassPane, new Rectangle());
        boolean visible = false;
        if (this.glassPane != null && this.glassPane.getParent() == this) {
            this.remove(this.glassPane);
            visible = this.glassPane.isVisible();
        }
        glassPane.setVisible(visible);
        this.add(this.glassPane = glassPane, 0);
        if (visible) {
            this.repaint();
        }
    }
    
    public Component getGlassPane() {
        return this.glassPane;
    }
    
    @Override
    public boolean isValidateRoot() {
        return true;
    }
    
    @Override
    public boolean isOptimizedDrawingEnabled() {
        return !this.glassPane.isVisible();
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        this.enableEvents(8L);
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
    }
    
    public void setDefaultButton(final JButton defaultButton) {
        final JButton defaultButton2 = this.defaultButton;
        if (defaultButton2 != defaultButton) {
            this.defaultButton = defaultButton;
            if (defaultButton2 != null) {
                defaultButton2.repaint();
            }
            if (defaultButton != null) {
                defaultButton.repaint();
            }
        }
        this.firePropertyChange("defaultButton", defaultButton2, defaultButton);
    }
    
    public JButton getDefaultButton() {
        return this.defaultButton;
    }
    
    final void setUseTrueDoubleBuffering(final boolean useTrueDoubleBuffering) {
        this.useTrueDoubleBuffering = useTrueDoubleBuffering;
    }
    
    final boolean getUseTrueDoubleBuffering() {
        return this.useTrueDoubleBuffering;
    }
    
    final void disableTrueDoubleBuffering() {
        if (this.useTrueDoubleBuffering && !JRootPane.IGNORE_DISABLE_TRUE_DOUBLE_BUFFERING) {
            if (JRootPane.LOG_DISABLE_TRUE_DOUBLE_BUFFERING) {
                System.out.println("Disabling true double buffering for " + this);
                Thread.dumpStack();
            }
            this.useTrueDoubleBuffering = false;
            RepaintManager.currentManager(this).doubleBufferingChanged(this);
        }
    }
    
    @Override
    protected void addImpl(final Component component, final Object o, final int n) {
        super.addImpl(component, o, n);
        if (this.glassPane != null && this.glassPane.getParent() == this && this.getComponent(0) != this.glassPane) {
            this.add(this.glassPane, 0);
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString();
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJRootPane();
        }
        return this.accessibleContext;
    }
    
    static {
        LOG_DISABLE_TRUE_DOUBLE_BUFFERING = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("swing.logDoubleBufferingDisable"));
        IGNORE_DISABLE_TRUE_DOUBLE_BUFFERING = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("swing.ignoreDoubleBufferingDisable"));
    }
    
    static class DefaultAction extends AbstractAction
    {
        JButton owner;
        JRootPane root;
        boolean press;
        
        DefaultAction(final JRootPane root, final boolean press) {
            this.root = root;
            this.press = press;
        }
        
        public void setOwner(final JButton owner) {
            this.owner = owner;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (this.owner != null && SwingUtilities.getRootPane(this.owner) == this.root) {
                final ButtonModel model = this.owner.getModel();
                if (this.press) {
                    model.setArmed(true);
                    model.setPressed(true);
                }
                else {
                    model.setPressed(false);
                }
            }
        }
        
        @Override
        public boolean isEnabled() {
            return this.owner.getModel().isEnabled();
        }
    }
    
    protected class RootLayout implements LayoutManager2, Serializable
    {
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            final Insets insets = JRootPane.this.getInsets();
            Dimension dimension;
            if (JRootPane.this.contentPane != null) {
                dimension = JRootPane.this.contentPane.getPreferredSize();
            }
            else {
                dimension = container.getSize();
            }
            Dimension preferredSize;
            if (JRootPane.this.menuBar != null && JRootPane.this.menuBar.isVisible()) {
                preferredSize = JRootPane.this.menuBar.getPreferredSize();
            }
            else {
                preferredSize = new Dimension(0, 0);
            }
            return new Dimension(Math.max(dimension.width, preferredSize.width) + insets.left + insets.right, dimension.height + preferredSize.height + insets.top + insets.bottom);
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            final Insets insets = JRootPane.this.getInsets();
            Dimension dimension;
            if (JRootPane.this.contentPane != null) {
                dimension = JRootPane.this.contentPane.getMinimumSize();
            }
            else {
                dimension = container.getSize();
            }
            Dimension minimumSize;
            if (JRootPane.this.menuBar != null && JRootPane.this.menuBar.isVisible()) {
                minimumSize = JRootPane.this.menuBar.getMinimumSize();
            }
            else {
                minimumSize = new Dimension(0, 0);
            }
            return new Dimension(Math.max(dimension.width, minimumSize.width) + insets.left + insets.right, dimension.height + minimumSize.height + insets.top + insets.bottom);
        }
        
        @Override
        public Dimension maximumLayoutSize(final Container container) {
            final Insets insets = JRootPane.this.getInsets();
            Dimension maximumSize;
            if (JRootPane.this.menuBar != null && JRootPane.this.menuBar.isVisible()) {
                maximumSize = JRootPane.this.menuBar.getMaximumSize();
            }
            else {
                maximumSize = new Dimension(0, 0);
            }
            Dimension maximumSize2;
            if (JRootPane.this.contentPane != null) {
                maximumSize2 = JRootPane.this.contentPane.getMaximumSize();
            }
            else {
                maximumSize2 = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE - insets.top - insets.bottom - maximumSize.height - 1);
            }
            return new Dimension(Math.min(maximumSize2.width, maximumSize.width) + insets.left + insets.right, maximumSize2.height + maximumSize.height + insets.top + insets.bottom);
        }
        
        @Override
        public void layoutContainer(final Container container) {
            final Rectangle bounds = container.getBounds();
            final Insets insets = JRootPane.this.getInsets();
            int n = 0;
            final int n2 = bounds.width - insets.right - insets.left;
            final int n3 = bounds.height - insets.top - insets.bottom;
            if (JRootPane.this.layeredPane != null) {
                JRootPane.this.layeredPane.setBounds(insets.left, insets.top, n2, n3);
            }
            if (JRootPane.this.glassPane != null) {
                JRootPane.this.glassPane.setBounds(insets.left, insets.top, n2, n3);
            }
            if (JRootPane.this.menuBar != null && JRootPane.this.menuBar.isVisible()) {
                final Dimension preferredSize = JRootPane.this.menuBar.getPreferredSize();
                JRootPane.this.menuBar.setBounds(0, 0, n2, preferredSize.height);
                n += preferredSize.height;
            }
            if (JRootPane.this.contentPane != null) {
                JRootPane.this.contentPane.setBounds(0, n, n2, n3 - n);
            }
        }
        
        @Override
        public void addLayoutComponent(final String s, final Component component) {
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
        }
        
        @Override
        public void addLayoutComponent(final Component component, final Object o) {
        }
        
        @Override
        public float getLayoutAlignmentX(final Container container) {
            return 0.0f;
        }
        
        @Override
        public float getLayoutAlignmentY(final Container container) {
            return 0.0f;
        }
        
        @Override
        public void invalidateLayout(final Container container) {
        }
    }
    
    protected class AccessibleJRootPane extends AccessibleJComponent
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.ROOT_PANE;
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            return super.getAccessibleChildrenCount();
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            return super.getAccessibleChild(n);
        }
    }
}
