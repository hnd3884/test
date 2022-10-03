package javax.swing.plaf.synth;

import java.awt.Container;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.awt.LayoutManager;
import java.awt.FontMetrics;
import sun.swing.SwingUtilities2;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.UIManager;
import javax.swing.JSeparator;
import javax.swing.plaf.UIResource;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

class SynthInternalFrameTitlePane extends BasicInternalFrameTitlePane implements SynthUI, PropertyChangeListener
{
    protected JPopupMenu systemPopupMenu;
    protected JButton menuButton;
    private SynthStyle style;
    private int titleSpacing;
    private int buttonSpacing;
    private int titleAlignment;
    
    public SynthInternalFrameTitlePane(final JInternalFrame internalFrame) {
        super(internalFrame);
    }
    
    @Override
    public String getUIClassID() {
        return "InternalFrameTitlePaneUI";
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, this.getComponentState(component));
    }
    
    public SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    private Region getRegion(final JComponent component) {
        return SynthLookAndFeel.getRegion(component);
    }
    
    private int getComponentState(final JComponent component) {
        if (this.frame != null && this.frame.isSelected()) {
            return 512;
        }
        return SynthLookAndFeel.getComponentState(component);
    }
    
    @Override
    protected void addSubComponents() {
        this.menuButton.setName("InternalFrameTitlePane.menuButton");
        this.iconButton.setName("InternalFrameTitlePane.iconifyButton");
        this.maxButton.setName("InternalFrameTitlePane.maximizeButton");
        this.closeButton.setName("InternalFrameTitlePane.closeButton");
        this.add(this.menuButton);
        this.add(this.iconButton);
        this.add(this.maxButton);
        this.add(this.closeButton);
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.frame.addPropertyChangeListener(this);
        this.addPropertyChangeListener(this);
    }
    
    @Override
    protected void uninstallListeners() {
        this.frame.removePropertyChangeListener(this);
        this.removePropertyChangeListener(this);
        super.uninstallListeners();
    }
    
    private void updateStyle(final JComponent component) {
        final SynthContext context = this.getContext(this, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style) {
            this.maxIcon = this.style.getIcon(context, "InternalFrameTitlePane.maximizeIcon");
            this.minIcon = this.style.getIcon(context, "InternalFrameTitlePane.minimizeIcon");
            this.iconIcon = this.style.getIcon(context, "InternalFrameTitlePane.iconifyIcon");
            this.closeIcon = this.style.getIcon(context, "InternalFrameTitlePane.closeIcon");
            this.titleSpacing = this.style.getInt(context, "InternalFrameTitlePane.titleSpacing", 2);
            this.buttonSpacing = this.style.getInt(context, "InternalFrameTitlePane.buttonSpacing", 2);
            final String s = (String)this.style.get(context, "InternalFrameTitlePane.titleAlignment");
            this.titleAlignment = 10;
            if (s != null) {
                final String upperCase = s.toUpperCase();
                if (upperCase.equals("TRAILING")) {
                    this.titleAlignment = 11;
                }
                else if (upperCase.equals("CENTER")) {
                    this.titleAlignment = 0;
                }
            }
        }
        context.dispose();
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        this.updateStyle(this);
    }
    
    @Override
    protected void uninstallDefaults() {
        final SynthContext context = this.getContext(this, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
        final JInternalFrame.JDesktopIcon desktopIcon = this.frame.getDesktopIcon();
        if (desktopIcon != null && desktopIcon.getComponentPopupMenu() == this.systemPopupMenu) {
            desktopIcon.setComponentPopupMenu(null);
        }
        super.uninstallDefaults();
    }
    
    @Override
    protected void assembleSystemMenu() {
        this.addSystemMenuItems(this.systemPopupMenu = new JPopupMenuUIResource());
        this.enableActions();
        this.menuButton = this.createNoFocusButton();
        this.updateMenuIcon();
        this.menuButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent mouseEvent) {
                try {
                    SynthInternalFrameTitlePane.this.frame.setSelected(true);
                }
                catch (final PropertyVetoException ex) {}
                SynthInternalFrameTitlePane.this.showSystemMenu();
            }
        });
        final JPopupMenu componentPopupMenu = this.frame.getComponentPopupMenu();
        if (componentPopupMenu == null || componentPopupMenu instanceof UIResource) {
            this.frame.setComponentPopupMenu(this.systemPopupMenu);
        }
        if (this.frame.getDesktopIcon() != null) {
            final JPopupMenu componentPopupMenu2 = this.frame.getDesktopIcon().getComponentPopupMenu();
            if (componentPopupMenu2 == null || componentPopupMenu2 instanceof UIResource) {
                this.frame.getDesktopIcon().setComponentPopupMenu(this.systemPopupMenu);
            }
        }
        this.setInheritsPopupMenu(true);
    }
    
    protected void addSystemMenuItems(final JPopupMenu popupMenu) {
        popupMenu.add(this.restoreAction).setMnemonic(getButtonMnemonic("restore"));
        popupMenu.add(this.moveAction).setMnemonic(getButtonMnemonic("move"));
        popupMenu.add(this.sizeAction).setMnemonic(getButtonMnemonic("size"));
        popupMenu.add(this.iconifyAction).setMnemonic(getButtonMnemonic("minimize"));
        popupMenu.add(this.maximizeAction).setMnemonic(getButtonMnemonic("maximize"));
        popupMenu.add(new JSeparator());
        popupMenu.add(this.closeAction).setMnemonic(getButtonMnemonic("close"));
    }
    
    private static int getButtonMnemonic(final String s) {
        try {
            return Integer.parseInt(UIManager.getString("InternalFrameTitlePane." + s + "Button.mnemonic"));
        }
        catch (final NumberFormatException ex) {
            return -1;
        }
    }
    
    @Override
    protected void showSystemMenu() {
        final Insets insets = this.frame.getInsets();
        if (!this.frame.isIcon()) {
            this.systemPopupMenu.show(this.frame, this.menuButton.getX(), this.getY() + this.getHeight());
        }
        else {
            this.systemPopupMenu.show(this.menuButton, this.getX() - insets.left - insets.right, this.getY() - this.systemPopupMenu.getPreferredSize().height - insets.bottom - insets.top);
        }
    }
    
    @Override
    public void paintComponent(final Graphics graphics) {
        final SynthContext context = this.getContext(this);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintInternalFrameTitlePaneBackground(context, graphics, 0, 0, this.getWidth(), this.getHeight());
        this.paint(context, graphics);
        context.dispose();
    }
    
    protected void paint(final SynthContext synthContext, final Graphics graphics) {
        final String title = this.frame.getTitle();
        if (title != null) {
            final SynthStyle style = synthContext.getStyle();
            graphics.setColor(style.getColor(synthContext, ColorType.TEXT_FOREGROUND));
            graphics.setFont(style.getFont(synthContext));
            final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.frame, graphics);
            final int n = (this.getHeight() + fontMetrics.getAscent() - fontMetrics.getLeading() - fontMetrics.getDescent()) / 2;
            JComponent component = null;
            if (this.frame.isIconifiable()) {
                component = this.iconButton;
            }
            else if (this.frame.isMaximizable()) {
                component = this.maxButton;
            }
            else if (this.frame.isClosable()) {
                component = this.closeButton;
            }
            final boolean leftToRight = SynthLookAndFeel.isLeftToRight(this.frame);
            int titleAlignment = this.titleAlignment;
            int n2;
            int min;
            if (leftToRight) {
                if (component != null) {
                    n2 = component.getX() - this.titleSpacing;
                }
                else {
                    n2 = this.frame.getWidth() - this.frame.getInsets().right - this.titleSpacing;
                }
                min = this.menuButton.getX() + this.menuButton.getWidth() + this.titleSpacing;
            }
            else {
                if (component != null) {
                    min = component.getX() + component.getWidth() + this.titleSpacing;
                }
                else {
                    min = this.frame.getInsets().left + this.titleSpacing;
                }
                n2 = this.menuButton.getX() - this.titleSpacing;
                if (titleAlignment == 10) {
                    titleAlignment = 11;
                }
                else if (titleAlignment == 11) {
                    titleAlignment = 10;
                }
            }
            final String title2 = this.getTitle(title, fontMetrics, n2 - min);
            if (title2 == title) {
                if (titleAlignment == 11) {
                    min = n2 - style.getGraphicsUtils(synthContext).computeStringWidth(synthContext, graphics.getFont(), fontMetrics, title);
                }
                else if (titleAlignment == 0) {
                    final int computeStringWidth = style.getGraphicsUtils(synthContext).computeStringWidth(synthContext, graphics.getFont(), fontMetrics, title);
                    min = Math.min(n2 - computeStringWidth, Math.max(min, (this.getWidth() - computeStringWidth) / 2));
                }
            }
            style.getGraphicsUtils(synthContext).paintText(synthContext, graphics, title2, min, n - fontMetrics.getAscent(), -1);
        }
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintInternalFrameTitlePaneBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    protected LayoutManager createLayout() {
        final SynthContext context = this.getContext(this);
        final LayoutManager layoutManager = (LayoutManager)this.style.get(context, "InternalFrameTitlePane.titlePaneLayout");
        context.dispose();
        return (layoutManager != null) ? layoutManager : new SynthTitlePaneLayout();
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getSource() == this) {
            if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
                this.updateStyle(this);
            }
        }
        else if (propertyChangeEvent.getPropertyName() == "frameIcon") {
            this.updateMenuIcon();
        }
    }
    
    private void updateMenuIcon() {
        Icon frameIcon = this.frame.getFrameIcon();
        final SynthContext context = this.getContext(this);
        if (frameIcon != null) {
            final Dimension dimension = (Dimension)context.getStyle().get(context, "InternalFrameTitlePane.maxFrameIconSize");
            int width = 16;
            int height = 16;
            if (dimension != null) {
                width = dimension.width;
                height = dimension.height;
            }
            if ((frameIcon.getIconWidth() > width || frameIcon.getIconHeight() > height) && frameIcon instanceof ImageIcon) {
                frameIcon = new ImageIcon(((ImageIcon)frameIcon).getImage().getScaledInstance(width, height, 4));
            }
        }
        context.dispose();
        this.menuButton.setIcon(frameIcon);
    }
    
    private JButton createNoFocusButton() {
        final JButton button = new JButton();
        button.setFocusable(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        return button;
    }
    
    private static class JPopupMenuUIResource extends JPopupMenu implements UIResource
    {
    }
    
    class SynthTitlePaneLayout implements LayoutManager
    {
        @Override
        public void addLayoutComponent(final String s, final Component component) {
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            return this.minimumLayoutSize(container);
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            final SynthContext context = SynthInternalFrameTitlePane.this.getContext(SynthInternalFrameTitlePane.this);
            int n = 0;
            int n2 = 0;
            int n3 = 0;
            if (SynthInternalFrameTitlePane.this.frame.isClosable()) {
                final Dimension preferredSize = SynthInternalFrameTitlePane.this.closeButton.getPreferredSize();
                n += preferredSize.width;
                n2 = Math.max(preferredSize.height, n2);
                ++n3;
            }
            if (SynthInternalFrameTitlePane.this.frame.isMaximizable()) {
                final Dimension preferredSize2 = SynthInternalFrameTitlePane.this.maxButton.getPreferredSize();
                n += preferredSize2.width;
                n2 = Math.max(preferredSize2.height, n2);
                ++n3;
            }
            if (SynthInternalFrameTitlePane.this.frame.isIconifiable()) {
                final Dimension preferredSize3 = SynthInternalFrameTitlePane.this.iconButton.getPreferredSize();
                n += preferredSize3.width;
                n2 = Math.max(preferredSize3.height, n2);
                ++n3;
            }
            final Dimension preferredSize4 = SynthInternalFrameTitlePane.this.menuButton.getPreferredSize();
            final int n4 = n + preferredSize4.width;
            final int max = Math.max(preferredSize4.height, n2);
            final int n5 = n4 + Math.max(0, (n3 - 1) * SynthInternalFrameTitlePane.this.buttonSpacing);
            final FontMetrics fontMetrics = SynthInternalFrameTitlePane.this.getFontMetrics(SynthInternalFrameTitlePane.this.getFont());
            final SynthGraphicsUtils graphicsUtils = context.getStyle().getGraphicsUtils(context);
            final String title = SynthInternalFrameTitlePane.this.frame.getTitle();
            final int n6 = (title != null) ? graphicsUtils.computeStringWidth(context, fontMetrics.getFont(), fontMetrics, title) : 0;
            int n7;
            if (((title != null) ? title.length() : 0) > 3) {
                final int computeStringWidth = graphicsUtils.computeStringWidth(context, fontMetrics.getFont(), fontMetrics, title.substring(0, 3) + "...");
                n7 = n5 + ((n6 < computeStringWidth) ? n6 : computeStringWidth);
            }
            else {
                n7 = n5 + n6;
            }
            final int max2 = Math.max(fontMetrics.getHeight() + 2, max);
            final int n8 = n7 + (SynthInternalFrameTitlePane.this.titleSpacing + SynthInternalFrameTitlePane.this.titleSpacing);
            final Insets insets = SynthInternalFrameTitlePane.this.getInsets();
            final int n9 = max2 + (insets.top + insets.bottom);
            final int n10 = n8 + (insets.left + insets.right);
            context.dispose();
            return new Dimension(n10, n9);
        }
        
        private int center(final Component component, final Insets insets, int n, final boolean b) {
            final Dimension preferredSize = component.getPreferredSize();
            if (b) {
                n -= preferredSize.width;
            }
            component.setBounds(n, insets.top + (SynthInternalFrameTitlePane.this.getHeight() - insets.top - insets.bottom - preferredSize.height) / 2, preferredSize.width, preferredSize.height);
            if (preferredSize.width <= 0) {
                return n;
            }
            if (b) {
                return n - SynthInternalFrameTitlePane.this.buttonSpacing;
            }
            return n + preferredSize.width + SynthInternalFrameTitlePane.this.buttonSpacing;
        }
        
        @Override
        public void layoutContainer(final Container container) {
            final Insets insets = container.getInsets();
            if (SynthLookAndFeel.isLeftToRight(SynthInternalFrameTitlePane.this.frame)) {
                this.center(SynthInternalFrameTitlePane.this.menuButton, insets, insets.left, false);
                int n = SynthInternalFrameTitlePane.this.getWidth() - insets.right;
                if (SynthInternalFrameTitlePane.this.frame.isClosable()) {
                    n = this.center(SynthInternalFrameTitlePane.this.closeButton, insets, n, true);
                }
                if (SynthInternalFrameTitlePane.this.frame.isMaximizable()) {
                    n = this.center(SynthInternalFrameTitlePane.this.maxButton, insets, n, true);
                }
                if (SynthInternalFrameTitlePane.this.frame.isIconifiable()) {
                    this.center(SynthInternalFrameTitlePane.this.iconButton, insets, n, true);
                }
            }
            else {
                this.center(SynthInternalFrameTitlePane.this.menuButton, insets, SynthInternalFrameTitlePane.this.getWidth() - insets.right, true);
                int n2 = insets.left;
                if (SynthInternalFrameTitlePane.this.frame.isClosable()) {
                    n2 = this.center(SynthInternalFrameTitlePane.this.closeButton, insets, n2, false);
                }
                if (SynthInternalFrameTitlePane.this.frame.isMaximizable()) {
                    n2 = this.center(SynthInternalFrameTitlePane.this.maxButton, insets, n2, false);
                }
                if (SynthInternalFrameTitlePane.this.frame.isIconifiable()) {
                    this.center(SynthInternalFrameTitlePane.this.iconButton, insets, n2, false);
                }
            }
        }
    }
}
