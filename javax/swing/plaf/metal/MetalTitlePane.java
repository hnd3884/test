package javax.swing.plaf.metal;

import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.image.ImageObserver;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.border.EmptyBorder;
import java.util.List;
import sun.awt.SunToolkit;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import sun.swing.SwingUtilities2;
import java.awt.Graphics;
import java.awt.Dialog;
import javax.swing.plaf.UIResource;
import java.awt.LayoutManager;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import java.awt.Toolkit;
import javax.swing.JMenu;
import java.awt.AWTEvent;
import java.awt.event.WindowEvent;
import java.awt.Frame;
import java.awt.Component;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.JRootPane;
import java.awt.Window;
import java.awt.event.WindowListener;
import java.awt.Image;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.Action;
import javax.swing.JMenuBar;
import java.beans.PropertyChangeListener;
import javax.swing.border.Border;
import javax.swing.JComponent;

class MetalTitlePane extends JComponent
{
    private static final Border handyEmptyBorder;
    private static final int IMAGE_HEIGHT = 16;
    private static final int IMAGE_WIDTH = 16;
    private PropertyChangeListener propertyChangeListener;
    private JMenuBar menuBar;
    private Action closeAction;
    private Action iconifyAction;
    private Action restoreAction;
    private Action maximizeAction;
    private JButton toggleButton;
    private JButton iconifyButton;
    private JButton closeButton;
    private Icon maximizeIcon;
    private Icon minimizeIcon;
    private Image systemIcon;
    private WindowListener windowListener;
    private Window window;
    private JRootPane rootPane;
    private int buttonsWidth;
    private int state;
    private MetalRootPaneUI rootPaneUI;
    private Color inactiveBackground;
    private Color inactiveForeground;
    private Color inactiveShadow;
    private Color activeBumpsHighlight;
    private Color activeBumpsShadow;
    private Color activeBackground;
    private Color activeForeground;
    private Color activeShadow;
    private MetalBumps activeBumps;
    private MetalBumps inactiveBumps;
    
    public MetalTitlePane(final JRootPane rootPane, final MetalRootPaneUI rootPaneUI) {
        this.inactiveBackground = UIManager.getColor("inactiveCaption");
        this.inactiveForeground = UIManager.getColor("inactiveCaptionText");
        this.inactiveShadow = UIManager.getColor("inactiveCaptionBorder");
        this.activeBumpsHighlight = MetalLookAndFeel.getPrimaryControlHighlight();
        this.activeBumpsShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();
        this.activeBackground = null;
        this.activeForeground = null;
        this.activeShadow = null;
        this.activeBumps = new MetalBumps(0, 0, this.activeBumpsHighlight, this.activeBumpsShadow, MetalLookAndFeel.getPrimaryControl());
        this.inactiveBumps = new MetalBumps(0, 0, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), MetalLookAndFeel.getControl());
        this.rootPane = rootPane;
        this.rootPaneUI = rootPaneUI;
        this.state = -1;
        this.installSubcomponents();
        this.determineColors();
        this.installDefaults();
        this.setLayout(this.createLayout());
    }
    
    private void uninstall() {
        this.uninstallListeners();
        this.window = null;
        this.removeAll();
    }
    
    private void installListeners() {
        if (this.window != null) {
            this.windowListener = this.createWindowListener();
            this.window.addWindowListener(this.windowListener);
            this.propertyChangeListener = this.createWindowPropertyChangeListener();
            this.window.addPropertyChangeListener(this.propertyChangeListener);
        }
    }
    
    private void uninstallListeners() {
        if (this.window != null) {
            this.window.removeWindowListener(this.windowListener);
            this.window.removePropertyChangeListener(this.propertyChangeListener);
        }
    }
    
    private WindowListener createWindowListener() {
        return new WindowHandler();
    }
    
    private PropertyChangeListener createWindowPropertyChangeListener() {
        return new PropertyChangeHandler();
    }
    
    @Override
    public JRootPane getRootPane() {
        return this.rootPane;
    }
    
    private int getWindowDecorationStyle() {
        return this.getRootPane().getWindowDecorationStyle();
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        this.uninstallListeners();
        this.window = SwingUtilities.getWindowAncestor(this);
        if (this.window != null) {
            if (this.window instanceof Frame) {
                this.setState(((Frame)this.window).getExtendedState());
            }
            else {
                this.setState(0);
            }
            this.setActive(this.window.isActive());
            this.installListeners();
            this.updateSystemIcon();
        }
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        this.uninstallListeners();
        this.window = null;
    }
    
    private void installSubcomponents() {
        final int windowDecorationStyle = this.getWindowDecorationStyle();
        if (windowDecorationStyle == 1) {
            this.createActions();
            this.add(this.menuBar = this.createMenuBar());
            this.createButtons();
            this.add(this.iconifyButton);
            this.add(this.toggleButton);
            this.add(this.closeButton);
        }
        else if (windowDecorationStyle == 2 || windowDecorationStyle == 3 || windowDecorationStyle == 4 || windowDecorationStyle == 5 || windowDecorationStyle == 6 || windowDecorationStyle == 7 || windowDecorationStyle == 8) {
            this.createActions();
            this.createButtons();
            this.add(this.closeButton);
        }
    }
    
    private void determineColors() {
        switch (this.getWindowDecorationStyle()) {
            case 1: {
                this.activeBackground = UIManager.getColor("activeCaption");
                this.activeForeground = UIManager.getColor("activeCaptionText");
                this.activeShadow = UIManager.getColor("activeCaptionBorder");
                break;
            }
            case 4: {
                this.activeBackground = UIManager.getColor("OptionPane.errorDialog.titlePane.background");
                this.activeForeground = UIManager.getColor("OptionPane.errorDialog.titlePane.foreground");
                this.activeShadow = UIManager.getColor("OptionPane.errorDialog.titlePane.shadow");
                break;
            }
            case 5:
            case 6:
            case 7: {
                this.activeBackground = UIManager.getColor("OptionPane.questionDialog.titlePane.background");
                this.activeForeground = UIManager.getColor("OptionPane.questionDialog.titlePane.foreground");
                this.activeShadow = UIManager.getColor("OptionPane.questionDialog.titlePane.shadow");
                break;
            }
            case 8: {
                this.activeBackground = UIManager.getColor("OptionPane.warningDialog.titlePane.background");
                this.activeForeground = UIManager.getColor("OptionPane.warningDialog.titlePane.foreground");
                this.activeShadow = UIManager.getColor("OptionPane.warningDialog.titlePane.shadow");
                break;
            }
            default: {
                this.activeBackground = UIManager.getColor("activeCaption");
                this.activeForeground = UIManager.getColor("activeCaptionText");
                this.activeShadow = UIManager.getColor("activeCaptionBorder");
                break;
            }
        }
        this.activeBumps.setBumpColors(this.activeBumpsHighlight, this.activeBumpsShadow, this.activeBackground);
    }
    
    private void installDefaults() {
        this.setFont(UIManager.getFont("InternalFrame.titleFont", this.getLocale()));
    }
    
    private void uninstallDefaults() {
    }
    
    protected JMenuBar createMenuBar() {
        (this.menuBar = new SystemMenuBar()).setFocusable(false);
        this.menuBar.setBorderPainted(true);
        this.menuBar.add(this.createMenu());
        return this.menuBar;
    }
    
    private void close() {
        final Window window = this.getWindow();
        if (window != null) {
            window.dispatchEvent(new WindowEvent(window, 201));
        }
    }
    
    private void iconify() {
        final Frame frame = this.getFrame();
        if (frame != null) {
            frame.setExtendedState(this.state | 0x1);
        }
    }
    
    private void maximize() {
        final Frame frame = this.getFrame();
        if (frame != null) {
            frame.setExtendedState(this.state | 0x6);
        }
    }
    
    private void restore() {
        final Frame frame = this.getFrame();
        if (frame == null) {
            return;
        }
        if ((this.state & 0x1) != 0x0) {
            frame.setExtendedState(this.state & 0xFFFFFFFE);
        }
        else {
            frame.setExtendedState(this.state & 0xFFFFFFF9);
        }
    }
    
    private void createActions() {
        this.closeAction = new CloseAction();
        if (this.getWindowDecorationStyle() == 1) {
            this.iconifyAction = new IconifyAction();
            this.restoreAction = new RestoreAction();
            this.maximizeAction = new MaximizeAction();
        }
    }
    
    private JMenu createMenu() {
        final JMenu menu = new JMenu("");
        if (this.getWindowDecorationStyle() == 1) {
            this.addMenuItems(menu);
        }
        return menu;
    }
    
    private void addMenuItems(final JMenu menu) {
        this.getRootPane().getLocale();
        final JMenuItem add = menu.add(this.restoreAction);
        final int int1 = MetalUtils.getInt("MetalTitlePane.restoreMnemonic", -1);
        if (int1 != -1) {
            add.setMnemonic(int1);
        }
        final JMenuItem add2 = menu.add(this.iconifyAction);
        final int int2 = MetalUtils.getInt("MetalTitlePane.iconifyMnemonic", -1);
        if (int2 != -1) {
            add2.setMnemonic(int2);
        }
        if (Toolkit.getDefaultToolkit().isFrameStateSupported(6)) {
            final JMenuItem add3 = menu.add(this.maximizeAction);
            final int int3 = MetalUtils.getInt("MetalTitlePane.maximizeMnemonic", -1);
            if (int3 != -1) {
                add3.setMnemonic(int3);
            }
        }
        menu.add(new JSeparator());
        final JMenuItem add4 = menu.add(this.closeAction);
        final int int4 = MetalUtils.getInt("MetalTitlePane.closeMnemonic", -1);
        if (int4 != -1) {
            add4.setMnemonic(int4);
        }
    }
    
    private JButton createTitleButton() {
        final JButton button = new JButton();
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setOpaque(true);
        return button;
    }
    
    private void createButtons() {
        (this.closeButton = this.createTitleButton()).setAction(this.closeAction);
        this.closeButton.setText(null);
        this.closeButton.putClientProperty("paintActive", Boolean.TRUE);
        this.closeButton.setBorder(MetalTitlePane.handyEmptyBorder);
        this.closeButton.putClientProperty("AccessibleName", "Close");
        this.closeButton.setIcon(UIManager.getIcon("InternalFrame.closeIcon"));
        if (this.getWindowDecorationStyle() == 1) {
            this.maximizeIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
            this.minimizeIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
            (this.iconifyButton = this.createTitleButton()).setAction(this.iconifyAction);
            this.iconifyButton.setText(null);
            this.iconifyButton.putClientProperty("paintActive", Boolean.TRUE);
            this.iconifyButton.setBorder(MetalTitlePane.handyEmptyBorder);
            this.iconifyButton.putClientProperty("AccessibleName", "Iconify");
            this.iconifyButton.setIcon(UIManager.getIcon("InternalFrame.iconifyIcon"));
            (this.toggleButton = this.createTitleButton()).setAction(this.restoreAction);
            this.toggleButton.putClientProperty("paintActive", Boolean.TRUE);
            this.toggleButton.setBorder(MetalTitlePane.handyEmptyBorder);
            this.toggleButton.putClientProperty("AccessibleName", "Maximize");
            this.toggleButton.setIcon(this.maximizeIcon);
        }
    }
    
    private LayoutManager createLayout() {
        return new TitlePaneLayout();
    }
    
    private void setActive(final boolean b) {
        final Boolean b2 = b ? Boolean.TRUE : Boolean.FALSE;
        this.closeButton.putClientProperty("paintActive", b2);
        if (this.getWindowDecorationStyle() == 1) {
            this.iconifyButton.putClientProperty("paintActive", b2);
            this.toggleButton.putClientProperty("paintActive", b2);
        }
        this.getRootPane().repaint();
    }
    
    private void setState(final int n) {
        this.setState(n, false);
    }
    
    private void setState(final int state, final boolean b) {
        if (this.getWindow() != null && this.getWindowDecorationStyle() == 1) {
            if (this.state == state && !b) {
                return;
            }
            final Frame frame = this.getFrame();
            if (frame != null) {
                final JRootPane rootPane = this.getRootPane();
                if ((state & 0x6) != 0x0 && (rootPane.getBorder() == null || rootPane.getBorder() instanceof UIResource) && frame.isShowing()) {
                    rootPane.setBorder(null);
                }
                else if ((state & 0x6) == 0x0) {
                    this.rootPaneUI.installBorder(rootPane);
                }
                if (frame.isResizable()) {
                    if ((state & 0x6) != 0x0) {
                        this.updateToggleButton(this.restoreAction, this.minimizeIcon);
                        this.maximizeAction.setEnabled(false);
                        this.restoreAction.setEnabled(true);
                    }
                    else {
                        this.updateToggleButton(this.maximizeAction, this.maximizeIcon);
                        this.maximizeAction.setEnabled(true);
                        this.restoreAction.setEnabled(false);
                    }
                    if (this.toggleButton.getParent() == null || this.iconifyButton.getParent() == null) {
                        this.add(this.toggleButton);
                        this.add(this.iconifyButton);
                        this.revalidate();
                        this.repaint();
                    }
                    this.toggleButton.setText(null);
                }
                else {
                    this.maximizeAction.setEnabled(false);
                    this.restoreAction.setEnabled(false);
                    if (this.toggleButton.getParent() != null) {
                        this.remove(this.toggleButton);
                        this.revalidate();
                        this.repaint();
                    }
                }
            }
            else {
                this.maximizeAction.setEnabled(false);
                this.restoreAction.setEnabled(false);
                this.iconifyAction.setEnabled(false);
                this.remove(this.toggleButton);
                this.remove(this.iconifyButton);
                this.revalidate();
                this.repaint();
            }
            this.closeAction.setEnabled(true);
            this.state = state;
        }
    }
    
    private void updateToggleButton(final Action action, final Icon icon) {
        this.toggleButton.setAction(action);
        this.toggleButton.setIcon(icon);
        this.toggleButton.setText(null);
    }
    
    private Frame getFrame() {
        final Window window = this.getWindow();
        if (window instanceof Frame) {
            return (Frame)window;
        }
        return null;
    }
    
    private Window getWindow() {
        return this.window;
    }
    
    private String getTitle() {
        final Window window = this.getWindow();
        if (window instanceof Frame) {
            return ((Frame)window).getTitle();
        }
        if (window instanceof Dialog) {
            return ((Dialog)window).getTitle();
        }
        return null;
    }
    
    public void paintComponent(final Graphics graphics) {
        if (this.getFrame() != null) {
            this.setState(this.getFrame().getExtendedState());
        }
        final JRootPane rootPane = this.getRootPane();
        final Window window = this.getWindow();
        final boolean b = (window == null) ? rootPane.getComponentOrientation().isLeftToRight() : window.getComponentOrientation().isLeftToRight();
        final boolean b2 = window == null || window.isActive();
        final int width = this.getWidth();
        final int height = this.getHeight();
        Color color;
        Color color2;
        Color color3;
        MetalBumps metalBumps;
        if (b2) {
            color = this.activeBackground;
            color2 = this.activeForeground;
            color3 = this.activeShadow;
            metalBumps = this.activeBumps;
        }
        else {
            color = this.inactiveBackground;
            color2 = this.inactiveForeground;
            color3 = this.inactiveShadow;
            metalBumps = this.inactiveBumps;
        }
        graphics.setColor(color);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(color3);
        graphics.drawLine(0, height - 1, width, height - 1);
        graphics.drawLine(0, 0, 0, 0);
        graphics.drawLine(width - 1, 0, width - 1, 0);
        int n = b ? 5 : (width - 5);
        if (this.getWindowDecorationStyle() == 1) {
            n += (b ? 21 : -21);
        }
        final String title = this.getTitle();
        if (title != null) {
            final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(rootPane, graphics);
            graphics.setColor(color2);
            final int n2 = (height - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
            Rectangle bounds = new Rectangle(0, 0, 0, 0);
            if (this.iconifyButton != null && this.iconifyButton.getParent() != null) {
                bounds = this.iconifyButton.getBounds();
            }
            String s;
            if (b) {
                if (bounds.x == 0) {
                    bounds.x = window.getWidth() - window.getInsets().right - 2;
                }
                s = SwingUtilities2.clipStringIfNecessary(rootPane, fontMetrics, title, bounds.x - n - 4);
            }
            else {
                s = SwingUtilities2.clipStringIfNecessary(rootPane, fontMetrics, title, n - bounds.x - bounds.width - 4);
                n -= SwingUtilities2.stringWidth(rootPane, fontMetrics, s);
            }
            final int stringWidth = SwingUtilities2.stringWidth(rootPane, fontMetrics, s);
            SwingUtilities2.drawString(rootPane, graphics, s, n, n2);
            n += (b ? (stringWidth + 5) : -5);
        }
        int n3;
        int n4;
        if (b) {
            n3 = width - this.buttonsWidth - n - 5;
            n4 = n;
        }
        else {
            n3 = n - this.buttonsWidth - 5;
            n4 = this.buttonsWidth + 5;
        }
        final int n5 = 3;
        metalBumps.setBumpArea(n3, this.getHeight() - 2 * n5);
        metalBumps.paintIcon(this, graphics, n4, n5);
    }
    
    private void updateSystemIcon() {
        final Window window = this.getWindow();
        if (window == null) {
            this.systemIcon = null;
            return;
        }
        final List<Image> iconImages = window.getIconImages();
        assert iconImages != null;
        if (iconImages.size() == 0) {
            this.systemIcon = null;
        }
        else if (iconImages.size() == 1) {
            this.systemIcon = iconImages.get(0);
        }
        else {
            this.systemIcon = SunToolkit.getScaledIconImage(iconImages, 16, 16);
        }
    }
    
    static {
        handyEmptyBorder = new EmptyBorder(0, 0, 0, 0);
    }
    
    private class CloseAction extends AbstractAction
    {
        public CloseAction() {
            super(UIManager.getString("MetalTitlePane.closeTitle", MetalTitlePane.this.getLocale()));
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            MetalTitlePane.this.close();
        }
    }
    
    private class IconifyAction extends AbstractAction
    {
        public IconifyAction() {
            super(UIManager.getString("MetalTitlePane.iconifyTitle", MetalTitlePane.this.getLocale()));
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            MetalTitlePane.this.iconify();
        }
    }
    
    private class RestoreAction extends AbstractAction
    {
        public RestoreAction() {
            super(UIManager.getString("MetalTitlePane.restoreTitle", MetalTitlePane.this.getLocale()));
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            MetalTitlePane.this.restore();
        }
    }
    
    private class MaximizeAction extends AbstractAction
    {
        public MaximizeAction() {
            super(UIManager.getString("MetalTitlePane.maximizeTitle", MetalTitlePane.this.getLocale()));
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            MetalTitlePane.this.maximize();
        }
    }
    
    private class SystemMenuBar extends JMenuBar
    {
        @Override
        public void paint(final Graphics graphics) {
            if (this.isOpaque()) {
                graphics.setColor(this.getBackground());
                graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
            if (MetalTitlePane.this.systemIcon != null) {
                graphics.drawImage(MetalTitlePane.this.systemIcon, 0, 0, 16, 16, null);
            }
            else {
                final Icon icon = UIManager.getIcon("InternalFrame.icon");
                if (icon != null) {
                    icon.paintIcon(this, graphics, 0, 0);
                }
            }
        }
        
        @Override
        public Dimension getMinimumSize() {
            return this.getPreferredSize();
        }
        
        @Override
        public Dimension getPreferredSize() {
            final Dimension preferredSize = super.getPreferredSize();
            return new Dimension(Math.max(16, preferredSize.width), Math.max(preferredSize.height, 16));
        }
    }
    
    private class TitlePaneLayout implements LayoutManager
    {
        @Override
        public void addLayoutComponent(final String s, final Component component) {
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            final int computeHeight = this.computeHeight();
            return new Dimension(computeHeight, computeHeight);
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            return this.preferredLayoutSize(container);
        }
        
        private int computeHeight() {
            int height = MetalTitlePane.this.rootPane.getFontMetrics(MetalTitlePane.this.getFont()).getHeight();
            height += 7;
            int n = 0;
            if (MetalTitlePane.this.getWindowDecorationStyle() == 1) {
                n = 16;
            }
            return Math.max(height, n);
        }
        
        @Override
        public void layoutContainer(final Container container) {
            final boolean b = (MetalTitlePane.this.window == null) ? MetalTitlePane.this.getRootPane().getComponentOrientation().isLeftToRight() : MetalTitlePane.this.window.getComponentOrientation().isLeftToRight();
            final int width = MetalTitlePane.this.getWidth();
            final int n = 3;
            int iconHeight;
            int iconWidth;
            if (MetalTitlePane.this.closeButton != null && MetalTitlePane.this.closeButton.getIcon() != null) {
                iconHeight = MetalTitlePane.this.closeButton.getIcon().getIconHeight();
                iconWidth = MetalTitlePane.this.closeButton.getIcon().getIconWidth();
            }
            else {
                iconHeight = 16;
                iconWidth = 16;
            }
            final boolean b2 = (b ? width : false) != 0;
            final int n2 = 5;
            final int n3 = b ? n2 : (width - iconWidth - n2);
            if (MetalTitlePane.this.menuBar != null) {
                MetalTitlePane.this.menuBar.setBounds(n3, n, iconWidth, iconHeight);
            }
            final int n4 = b ? width : 0;
            final int n5 = 4;
            int n6 = n4 + (b ? (-n5 - iconWidth) : n5);
            if (MetalTitlePane.this.closeButton != null) {
                MetalTitlePane.this.closeButton.setBounds(n6, n, iconWidth, iconHeight);
            }
            if (!b) {
                n6 += iconWidth;
            }
            if (MetalTitlePane.this.getWindowDecorationStyle() == 1) {
                if (Toolkit.getDefaultToolkit().isFrameStateSupported(6) && MetalTitlePane.this.toggleButton.getParent() != null) {
                    final int n7 = 10;
                    n6 += (b ? (-n7 - iconWidth) : n7);
                    MetalTitlePane.this.toggleButton.setBounds(n6, n, iconWidth, iconHeight);
                    if (!b) {
                        n6 += iconWidth;
                    }
                }
                if (MetalTitlePane.this.iconifyButton != null && MetalTitlePane.this.iconifyButton.getParent() != null) {
                    final int n8 = 2;
                    n6 += (b ? (-n8 - iconWidth) : n8);
                    MetalTitlePane.this.iconifyButton.setBounds(n6, n, iconWidth, iconHeight);
                    if (!b) {
                        n6 += iconWidth;
                    }
                }
            }
            MetalTitlePane.this.buttonsWidth = (b ? (width - n6) : n6);
        }
    }
    
    private class PropertyChangeHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if ("resizable".equals(propertyName) || "state".equals(propertyName)) {
                final Frame access$1700 = MetalTitlePane.this.getFrame();
                if (access$1700 != null) {
                    MetalTitlePane.this.setState(access$1700.getExtendedState(), true);
                }
                if ("resizable".equals(propertyName)) {
                    MetalTitlePane.this.getRootPane().repaint();
                }
            }
            else if ("title".equals(propertyName)) {
                MetalTitlePane.this.repaint();
            }
            else if ("componentOrientation" == propertyName) {
                MetalTitlePane.this.revalidate();
                MetalTitlePane.this.repaint();
            }
            else if ("iconImage" == propertyName) {
                MetalTitlePane.this.updateSystemIcon();
                MetalTitlePane.this.revalidate();
                MetalTitlePane.this.repaint();
            }
        }
    }
    
    private class WindowHandler extends WindowAdapter
    {
        @Override
        public void windowActivated(final WindowEvent windowEvent) {
            MetalTitlePane.this.setActive(true);
        }
        
        @Override
        public void windowDeactivated(final WindowEvent windowEvent) {
            MetalTitlePane.this.setActive(false);
        }
    }
}
