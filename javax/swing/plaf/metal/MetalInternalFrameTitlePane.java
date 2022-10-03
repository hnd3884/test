package javax.swing.plaf.metal;

import java.awt.Dimension;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Rectangle;
import javax.swing.JComponent;
import sun.swing.SwingUtilities2;
import javax.swing.plaf.ColorUIResource;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.beans.PropertyChangeListener;
import java.awt.Component;
import javax.swing.JMenu;
import javax.swing.UIManager;
import javax.swing.JInternalFrame;
import java.awt.Color;
import javax.swing.border.Border;
import javax.swing.Icon;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class MetalInternalFrameTitlePane extends BasicInternalFrameTitlePane
{
    protected boolean isPalette;
    protected Icon paletteCloseIcon;
    protected int paletteTitleHeight;
    private static final Border handyEmptyBorder;
    private String selectedBackgroundKey;
    private String selectedForegroundKey;
    private String selectedShadowKey;
    private boolean wasClosable;
    int buttonsWidth;
    MetalBumps activeBumps;
    MetalBumps inactiveBumps;
    MetalBumps paletteBumps;
    private Color activeBumpsHighlight;
    private Color activeBumpsShadow;
    
    public MetalInternalFrameTitlePane(final JInternalFrame internalFrame) {
        super(internalFrame);
        this.isPalette = false;
        this.buttonsWidth = 0;
        this.activeBumps = new MetalBumps(0, 0, MetalLookAndFeel.getPrimaryControlHighlight(), MetalLookAndFeel.getPrimaryControlDarkShadow(), (UIManager.get("InternalFrame.activeTitleGradient") != null) ? null : MetalLookAndFeel.getPrimaryControl());
        this.inactiveBumps = new MetalBumps(0, 0, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), (UIManager.get("InternalFrame.inactiveTitleGradient") != null) ? null : MetalLookAndFeel.getControl());
        this.activeBumpsHighlight = MetalLookAndFeel.getPrimaryControlHighlight();
        this.activeBumpsShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        this.updateOptionPaneState();
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        this.setFont(UIManager.getFont("InternalFrame.titleFont"));
        this.paletteTitleHeight = UIManager.getInt("InternalFrame.paletteTitleHeight");
        this.paletteCloseIcon = UIManager.getIcon("InternalFrame.paletteCloseIcon");
        this.wasClosable = this.frame.isClosable();
        final String s = null;
        this.selectedBackgroundKey = s;
        this.selectedForegroundKey = s;
        if (MetalLookAndFeel.usingOcean()) {
            this.setOpaque(true);
        }
    }
    
    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();
        if (this.wasClosable != this.frame.isClosable()) {
            this.frame.setClosable(this.wasClosable);
        }
    }
    
    @Override
    protected void createButtons() {
        super.createButtons();
        final Boolean b = this.frame.isSelected() ? Boolean.TRUE : Boolean.FALSE;
        this.iconButton.putClientProperty("paintActive", b);
        this.iconButton.setBorder(MetalInternalFrameTitlePane.handyEmptyBorder);
        this.maxButton.putClientProperty("paintActive", b);
        this.maxButton.setBorder(MetalInternalFrameTitlePane.handyEmptyBorder);
        this.closeButton.putClientProperty("paintActive", b);
        this.closeButton.setBorder(MetalInternalFrameTitlePane.handyEmptyBorder);
        this.closeButton.setBackground(MetalLookAndFeel.getPrimaryControlShadow());
        if (MetalLookAndFeel.usingOcean()) {
            this.iconButton.setContentAreaFilled(false);
            this.maxButton.setContentAreaFilled(false);
            this.closeButton.setContentAreaFilled(false);
        }
    }
    
    @Override
    protected void assembleSystemMenu() {
    }
    
    @Override
    protected void addSystemMenuItems(final JMenu menu) {
    }
    
    @Override
    protected void showSystemMenu() {
    }
    
    @Override
    protected void addSubComponents() {
        this.add(this.iconButton);
        this.add(this.maxButton);
        this.add(this.closeButton);
    }
    
    @Override
    protected PropertyChangeListener createPropertyChangeListener() {
        return new MetalPropertyChangeHandler();
    }
    
    @Override
    protected LayoutManager createLayout() {
        return new MetalTitlePaneLayout();
    }
    
    public void paintPalette(final Graphics graphics) {
        final boolean leftToRight = MetalUtils.isLeftToRight(this.frame);
        final int width = this.getWidth();
        final int height = this.getHeight();
        if (this.paletteBumps == null) {
            this.paletteBumps = new MetalBumps(0, 0, MetalLookAndFeel.getPrimaryControlHighlight(), MetalLookAndFeel.getPrimaryControlInfo(), MetalLookAndFeel.getPrimaryControlShadow());
        }
        final ColorUIResource primaryControlShadow = MetalLookAndFeel.getPrimaryControlShadow();
        final ColorUIResource primaryControlDarkShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();
        graphics.setColor(primaryControlShadow);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(primaryControlDarkShadow);
        graphics.drawLine(0, height - 1, width, height - 1);
        final int n = leftToRight ? 4 : (this.buttonsWidth + 4);
        this.paletteBumps.setBumpArea(width - this.buttonsWidth - 8, this.getHeight() - 4);
        this.paletteBumps.paintIcon(this, graphics, n, 2);
    }
    
    @Override
    public void paintComponent(final Graphics graphics) {
        if (this.isPalette) {
            this.paintPalette(graphics);
            return;
        }
        final boolean leftToRight = MetalUtils.isLeftToRight(this.frame);
        final boolean selected = this.frame.isSelected();
        final int width = this.getWidth();
        final int height = this.getHeight();
        Color color = null;
        Color color2 = null;
        Color color3 = null;
        MetalBumps metalBumps;
        String s;
        if (selected) {
            if (!MetalLookAndFeel.usingOcean()) {
                this.closeButton.setContentAreaFilled(true);
                this.maxButton.setContentAreaFilled(true);
                this.iconButton.setContentAreaFilled(true);
            }
            if (this.selectedBackgroundKey != null) {
                color = UIManager.getColor(this.selectedBackgroundKey);
            }
            if (color == null) {
                color = MetalLookAndFeel.getWindowTitleBackground();
            }
            if (this.selectedForegroundKey != null) {
                color2 = UIManager.getColor(this.selectedForegroundKey);
            }
            if (this.selectedShadowKey != null) {
                color3 = UIManager.getColor(this.selectedShadowKey);
            }
            if (color3 == null) {
                color3 = MetalLookAndFeel.getPrimaryControlDarkShadow();
            }
            if (color2 == null) {
                color2 = MetalLookAndFeel.getWindowTitleForeground();
            }
            this.activeBumps.setBumpColors(this.activeBumpsHighlight, this.activeBumpsShadow, (UIManager.get("InternalFrame.activeTitleGradient") != null) ? null : color);
            metalBumps = this.activeBumps;
            s = "InternalFrame.activeTitleGradient";
        }
        else {
            if (!MetalLookAndFeel.usingOcean()) {
                this.closeButton.setContentAreaFilled(false);
                this.maxButton.setContentAreaFilled(false);
                this.iconButton.setContentAreaFilled(false);
            }
            color = MetalLookAndFeel.getWindowTitleInactiveBackground();
            color2 = MetalLookAndFeel.getWindowTitleInactiveForeground();
            color3 = MetalLookAndFeel.getControlDarkShadow();
            metalBumps = this.inactiveBumps;
            s = "InternalFrame.inactiveTitleGradient";
        }
        if (!MetalUtils.drawGradient(this, graphics, s, 0, 0, width, height, true)) {
            graphics.setColor(color);
            graphics.fillRect(0, 0, width, height);
        }
        graphics.setColor(color3);
        graphics.drawLine(0, height - 1, width, height - 1);
        graphics.drawLine(0, 0, 0, 0);
        graphics.drawLine(width - 1, 0, width - 1, 0);
        int n = leftToRight ? 5 : (width - 5);
        final String title = this.frame.getTitle();
        final Icon frameIcon = this.frame.getFrameIcon();
        if (frameIcon != null) {
            if (!leftToRight) {
                n -= frameIcon.getIconWidth();
            }
            frameIcon.paintIcon(this.frame, graphics, n, height / 2 - frameIcon.getIconHeight() / 2);
            n += (leftToRight ? (frameIcon.getIconWidth() + 5) : -5);
        }
        if (title != null) {
            final Font font = this.getFont();
            graphics.setFont(font);
            final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.frame, graphics, font);
            fontMetrics.getHeight();
            graphics.setColor(color2);
            final int n2 = (height - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
            Rectangle rectangle = new Rectangle(0, 0, 0, 0);
            if (this.frame.isIconifiable()) {
                rectangle = this.iconButton.getBounds();
            }
            else if (this.frame.isMaximizable()) {
                rectangle = this.maxButton.getBounds();
            }
            else if (this.frame.isClosable()) {
                rectangle = this.closeButton.getBounds();
            }
            String s2;
            if (leftToRight) {
                if (rectangle.x == 0) {
                    rectangle.x = this.frame.getWidth() - this.frame.getInsets().right - 2;
                }
                s2 = this.getTitle(title, fontMetrics, rectangle.x - n - 4);
            }
            else {
                s2 = this.getTitle(title, fontMetrics, n - rectangle.x - rectangle.width - 4);
                n -= SwingUtilities2.stringWidth(this.frame, fontMetrics, s2);
            }
            final int stringWidth = SwingUtilities2.stringWidth(this.frame, fontMetrics, s2);
            SwingUtilities2.drawString(this.frame, graphics, s2, n, n2);
            n += (leftToRight ? (stringWidth + 5) : -5);
        }
        int n3;
        int n4;
        if (leftToRight) {
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
    
    public void setPalette(final boolean isPalette) {
        this.isPalette = isPalette;
        if (this.isPalette) {
            this.closeButton.setIcon(this.paletteCloseIcon);
            if (this.frame.isMaximizable()) {
                this.remove(this.maxButton);
            }
            if (this.frame.isIconifiable()) {
                this.remove(this.iconButton);
            }
        }
        else {
            this.closeButton.setIcon(this.closeIcon);
            if (this.frame.isMaximizable()) {
                this.add(this.maxButton);
            }
            if (this.frame.isIconifiable()) {
                this.add(this.iconButton);
            }
        }
        this.revalidate();
        this.repaint();
    }
    
    private void updateOptionPaneState() {
        int intValue = -2;
        boolean wasClosable = this.wasClosable;
        final Object clientProperty = this.frame.getClientProperty("JInternalFrame.messageType");
        if (clientProperty == null) {
            return;
        }
        if (clientProperty instanceof Integer) {
            intValue = (int)clientProperty;
        }
        switch (intValue) {
            case 0: {
                this.selectedBackgroundKey = "OptionPane.errorDialog.titlePane.background";
                this.selectedForegroundKey = "OptionPane.errorDialog.titlePane.foreground";
                this.selectedShadowKey = "OptionPane.errorDialog.titlePane.shadow";
                wasClosable = false;
                break;
            }
            case 3: {
                this.selectedBackgroundKey = "OptionPane.questionDialog.titlePane.background";
                this.selectedForegroundKey = "OptionPane.questionDialog.titlePane.foreground";
                this.selectedShadowKey = "OptionPane.questionDialog.titlePane.shadow";
                wasClosable = false;
                break;
            }
            case 2: {
                this.selectedBackgroundKey = "OptionPane.warningDialog.titlePane.background";
                this.selectedForegroundKey = "OptionPane.warningDialog.titlePane.foreground";
                this.selectedShadowKey = "OptionPane.warningDialog.titlePane.shadow";
                wasClosable = false;
                break;
            }
            case -1:
            case 1: {
                final String selectedBackgroundKey = null;
                this.selectedShadowKey = selectedBackgroundKey;
                this.selectedForegroundKey = selectedBackgroundKey;
                this.selectedBackgroundKey = selectedBackgroundKey;
                wasClosable = false;
                break;
            }
            default: {
                final String selectedBackgroundKey2 = null;
                this.selectedShadowKey = selectedBackgroundKey2;
                this.selectedForegroundKey = selectedBackgroundKey2;
                this.selectedBackgroundKey = selectedBackgroundKey2;
                break;
            }
        }
        if (wasClosable != this.frame.isClosable()) {
            this.frame.setClosable(wasClosable);
        }
    }
    
    static {
        handyEmptyBorder = new EmptyBorder(0, 0, 0, 0);
    }
    
    class MetalPropertyChangeHandler extends PropertyChangeHandler
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName.equals("selected")) {
                final Boolean b = (Boolean)propertyChangeEvent.getNewValue();
                MetalInternalFrameTitlePane.this.iconButton.putClientProperty("paintActive", b);
                MetalInternalFrameTitlePane.this.closeButton.putClientProperty("paintActive", b);
                MetalInternalFrameTitlePane.this.maxButton.putClientProperty("paintActive", b);
            }
            else if ("JInternalFrame.messageType".equals(propertyName)) {
                MetalInternalFrameTitlePane.this.updateOptionPaneState();
                MetalInternalFrameTitlePane.this.frame.repaint();
            }
            super.propertyChange(propertyChangeEvent);
        }
    }
    
    class MetalTitlePaneLayout extends TitlePaneLayout
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
            int n = 30;
            if (MetalInternalFrameTitlePane.this.frame.isClosable()) {
                n += 21;
            }
            if (MetalInternalFrameTitlePane.this.frame.isMaximizable()) {
                n += 16 + (MetalInternalFrameTitlePane.this.frame.isClosable() ? 10 : 4);
            }
            if (MetalInternalFrameTitlePane.this.frame.isIconifiable()) {
                n += 16 + (MetalInternalFrameTitlePane.this.frame.isMaximizable() ? 2 : (MetalInternalFrameTitlePane.this.frame.isClosable() ? 10 : 4));
            }
            final FontMetrics fontMetrics = MetalInternalFrameTitlePane.this.frame.getFontMetrics(MetalInternalFrameTitlePane.this.getFont());
            final String title = MetalInternalFrameTitlePane.this.frame.getTitle();
            final int n2 = (title != null) ? SwingUtilities2.stringWidth(MetalInternalFrameTitlePane.this.frame, fontMetrics, title) : 0;
            int n3;
            if (((title != null) ? title.length() : 0) > 2) {
                final int stringWidth = SwingUtilities2.stringWidth(MetalInternalFrameTitlePane.this.frame, fontMetrics, MetalInternalFrameTitlePane.this.frame.getTitle().substring(0, 2) + "...");
                n3 = n + ((n2 < stringWidth) ? n2 : stringWidth);
            }
            else {
                n3 = n + n2;
            }
            int n4;
            if (MetalInternalFrameTitlePane.this.isPalette) {
                n4 = MetalInternalFrameTitlePane.this.paletteTitleHeight;
            }
            else {
                int height = fontMetrics.getHeight();
                height += 7;
                final Icon frameIcon = MetalInternalFrameTitlePane.this.frame.getFrameIcon();
                int min = 0;
                if (frameIcon != null) {
                    min = Math.min(frameIcon.getIconHeight(), 16);
                }
                min += 5;
                n4 = Math.max(height, min);
            }
            return new Dimension(n3, n4);
        }
        
        @Override
        public void layoutContainer(final Container container) {
            final boolean leftToRight = MetalUtils.isLeftToRight(MetalInternalFrameTitlePane.this.frame);
            final int width = MetalInternalFrameTitlePane.this.getWidth();
            int n = leftToRight ? width : 0;
            final int n2 = 2;
            final int iconHeight = MetalInternalFrameTitlePane.this.closeButton.getIcon().getIconHeight();
            final int iconWidth = MetalInternalFrameTitlePane.this.closeButton.getIcon().getIconWidth();
            if (MetalInternalFrameTitlePane.this.frame.isClosable()) {
                if (MetalInternalFrameTitlePane.this.isPalette) {
                    final int n3 = 3;
                    n += (leftToRight ? (-n3 - (iconWidth + 2)) : n3);
                    MetalInternalFrameTitlePane.this.closeButton.setBounds(n, n2, iconWidth + 2, MetalInternalFrameTitlePane.this.getHeight() - 4);
                    if (!leftToRight) {
                        n += iconWidth + 2;
                    }
                }
                else {
                    final int n4 = 4;
                    n += (leftToRight ? (-n4 - iconWidth) : n4);
                    MetalInternalFrameTitlePane.this.closeButton.setBounds(n, n2, iconWidth, iconHeight);
                    if (!leftToRight) {
                        n += iconWidth;
                    }
                }
            }
            if (MetalInternalFrameTitlePane.this.frame.isMaximizable() && !MetalInternalFrameTitlePane.this.isPalette) {
                final int n5 = MetalInternalFrameTitlePane.this.frame.isClosable() ? 10 : 4;
                n += (leftToRight ? (-n5 - iconWidth) : n5);
                MetalInternalFrameTitlePane.this.maxButton.setBounds(n, n2, iconWidth, iconHeight);
                if (!leftToRight) {
                    n += iconWidth;
                }
            }
            if (MetalInternalFrameTitlePane.this.frame.isIconifiable() && !MetalInternalFrameTitlePane.this.isPalette) {
                final int n6 = MetalInternalFrameTitlePane.this.frame.isMaximizable() ? 2 : (MetalInternalFrameTitlePane.this.frame.isClosable() ? 10 : 4);
                n += (leftToRight ? (-n6 - iconWidth) : n6);
                MetalInternalFrameTitlePane.this.iconButton.setBounds(n, n2, iconWidth, iconHeight);
                if (!leftToRight) {
                    n += iconWidth;
                }
            }
            MetalInternalFrameTitlePane.this.buttonsWidth = (leftToRight ? (width - n) : n);
        }
    }
}
