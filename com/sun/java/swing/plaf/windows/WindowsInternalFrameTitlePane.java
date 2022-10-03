package com.sun.java.swing.plaf.windows;

import javax.swing.UIDefaults;
import javax.swing.plaf.UIResource;
import java.beans.PropertyChangeEvent;
import java.awt.Container;
import java.awt.Insets;
import javax.swing.JButton;
import java.awt.LayoutManager;
import java.beans.PropertyChangeListener;
import javax.swing.border.Border;
import javax.swing.JSeparator;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.Icon;
import java.awt.Paint;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import javax.swing.LookAndFeel;
import java.awt.Point;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import sun.swing.SwingUtilities2;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import java.awt.Component;
import javax.swing.JInternalFrame;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import java.awt.Color;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class WindowsInternalFrameTitlePane extends BasicInternalFrameTitlePane
{
    private Color selectedTitleGradientColor;
    private Color notSelectedTitleGradientColor;
    private JPopupMenu systemPopupMenu;
    private JLabel systemLabel;
    private Font titleFont;
    private int titlePaneHeight;
    private int buttonWidth;
    private int buttonHeight;
    private boolean hotTrackingOn;
    
    public WindowsInternalFrameTitlePane(final JInternalFrame internalFrame) {
        super(internalFrame);
    }
    
    @Override
    protected void addSubComponents() {
        this.add(this.systemLabel);
        this.add(this.iconButton);
        this.add(this.maxButton);
        this.add(this.closeButton);
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        this.titlePaneHeight = UIManager.getInt("InternalFrame.titlePaneHeight");
        this.buttonWidth = UIManager.getInt("InternalFrame.titleButtonWidth") - 4;
        this.buttonHeight = UIManager.getInt("InternalFrame.titleButtonHeight") - 4;
        final Object value = UIManager.get("InternalFrame.titleButtonToolTipsOn");
        this.hotTrackingOn = (!(value instanceof Boolean) || (boolean)value);
        if (XPStyle.getXP() != null) {
            this.buttonWidth = this.buttonHeight;
            final Dimension partSize = XPStyle.getPartSize(TMSchema.Part.WP_CLOSEBUTTON, TMSchema.State.NORMAL);
            if (partSize != null && partSize.width != 0 && partSize.height != 0) {
                this.buttonWidth = (int)(this.buttonWidth * (float)partSize.width / partSize.height);
            }
        }
        else {
            this.buttonWidth += 2;
            this.setBorder(BorderFactory.createLineBorder(UIManager.getColor("InternalFrame.activeBorderColor"), 1));
        }
        this.selectedTitleGradientColor = UIManager.getColor("InternalFrame.activeTitleGradient");
        this.notSelectedTitleGradientColor = UIManager.getColor("InternalFrame.inactiveTitleGradient");
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
    }
    
    @Override
    protected void createButtons() {
        super.createButtons();
        if (XPStyle.getXP() != null) {
            this.iconButton.setContentAreaFilled(false);
            this.maxButton.setContentAreaFilled(false);
            this.closeButton.setContentAreaFilled(false);
        }
    }
    
    @Override
    protected void setButtonIcons() {
        super.setButtonIcons();
        if (!this.hotTrackingOn) {
            this.iconButton.setToolTipText(null);
            this.maxButton.setToolTipText(null);
            this.closeButton.setToolTipText(null);
        }
    }
    
    @Override
    public void paintComponent(final Graphics graphics) {
        final XPStyle xp = XPStyle.getXP();
        this.paintTitleBackground(graphics);
        final String title = this.frame.getTitle();
        if (title != null) {
            final boolean selected = this.frame.isSelected();
            final Font font = graphics.getFont();
            final Font font2 = (this.titleFont != null) ? this.titleFont : this.getFont();
            graphics.setFont(font2);
            final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.frame, graphics, font2);
            final int n = (this.getHeight() + fontMetrics.getAscent() - fontMetrics.getLeading() - fontMetrics.getDescent()) / 2;
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
            final int n2 = 2;
            int n3;
            int stringWidth;
            if (WindowsGraphicsUtils.isLeftToRight(this.frame)) {
                if (rectangle.x == 0) {
                    rectangle.x = this.frame.getWidth() - this.frame.getInsets().right;
                }
                n3 = this.systemLabel.getX() + this.systemLabel.getWidth() + n2;
                if (xp != null) {
                    n3 += 2;
                }
                stringWidth = rectangle.x - n3 - n2;
            }
            else {
                if (rectangle.x == 0) {
                    rectangle.x = this.frame.getInsets().left;
                }
                stringWidth = SwingUtilities2.stringWidth(this.frame, fontMetrics, title);
                int n4 = rectangle.x + rectangle.width + n2;
                if (xp != null) {
                    n4 += 2;
                }
                final int n5 = this.systemLabel.getX() - n2 - n4;
                if (n5 > stringWidth) {
                    n3 = this.systemLabel.getX() - n2 - stringWidth;
                }
                else {
                    n3 = n4;
                    stringWidth = n5;
                }
            }
            final String title2 = this.getTitle(this.frame.getTitle(), fontMetrics, stringWidth);
            if (xp != null) {
                String string = null;
                if (selected) {
                    string = xp.getString(this, TMSchema.Part.WP_CAPTION, TMSchema.State.ACTIVE, TMSchema.Prop.TEXTSHADOWTYPE);
                }
                if ("single".equalsIgnoreCase(string)) {
                    final Point point = xp.getPoint(this, TMSchema.Part.WP_WINDOW, TMSchema.State.ACTIVE, TMSchema.Prop.TEXTSHADOWOFFSET);
                    final Color color = xp.getColor(this, TMSchema.Part.WP_WINDOW, TMSchema.State.ACTIVE, TMSchema.Prop.TEXTSHADOWCOLOR, null);
                    if (point != null && color != null) {
                        graphics.setColor(color);
                        SwingUtilities2.drawString(this.frame, graphics, title2, n3 + point.x, n + point.y);
                    }
                }
            }
            graphics.setColor(selected ? this.selectedTextColor : this.notSelectedTextColor);
            SwingUtilities2.drawString(this.frame, graphics, title2, n3, n);
            graphics.setFont(font);
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        return this.getMinimumSize();
    }
    
    @Override
    public Dimension getMinimumSize() {
        final Dimension dimension = new Dimension(super.getMinimumSize());
        dimension.height = this.titlePaneHeight + 2;
        if (XPStyle.getXP() != null) {
            if (this.frame.isMaximum()) {
                final Dimension dimension2 = dimension;
                --dimension2.height;
            }
            else {
                final Dimension dimension3 = dimension;
                dimension3.height += 3;
            }
        }
        return dimension;
    }
    
    @Override
    protected void paintTitleBackground(final Graphics graphics) {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            xp.getSkin(this, this.frame.isIcon() ? TMSchema.Part.WP_MINCAPTION : (this.frame.isMaximum() ? TMSchema.Part.WP_MAXCAPTION : TMSchema.Part.WP_CAPTION)).paintSkin(graphics, 0, 0, this.getWidth(), this.getHeight(), this.frame.isSelected() ? TMSchema.State.ACTIVE : TMSchema.State.INACTIVE);
        }
        else if ((boolean)LookAndFeel.getDesktopPropertyValue("win.frame.captionGradientsOn", false) && graphics instanceof Graphics2D) {
            final Graphics2D graphics2D = (Graphics2D)graphics;
            final Paint paint = graphics2D.getPaint();
            final boolean selected = this.frame.isSelected();
            final int width = this.getWidth();
            if (selected) {
                graphics2D.setPaint(new GradientPaint(0.0f, 0.0f, this.selectedTitleColor, (float)(int)(width * 0.75), 0.0f, this.selectedTitleGradientColor));
            }
            else {
                graphics2D.setPaint(new GradientPaint(0.0f, 0.0f, this.notSelectedTitleColor, (float)(int)(width * 0.75), 0.0f, this.notSelectedTitleGradientColor));
            }
            graphics2D.fillRect(0, 0, this.getWidth(), this.getHeight());
            graphics2D.setPaint(paint);
        }
        else {
            super.paintTitleBackground(graphics);
        }
    }
    
    @Override
    protected void assembleSystemMenu() {
        this.addSystemMenuItems(this.systemPopupMenu = new JPopupMenu());
        this.enableActions();
        (this.systemLabel = new JLabel(this.frame.getFrameIcon()) {
            @Override
            protected void paintComponent(Graphics create) {
                int n = 0;
                int n2 = 0;
                final int width = this.getWidth();
                final int height = this.getHeight();
                create = create.create();
                if (this.isOpaque()) {
                    create.setColor(this.getBackground());
                    create.fillRect(0, 0, width, height);
                }
                final Icon icon = this.getIcon();
                final int iconWidth;
                final int iconHeight;
                if (icon != null && (iconWidth = icon.getIconWidth()) > 0 && (iconHeight = icon.getIconHeight()) > 0) {
                    double n3;
                    if (iconWidth > iconHeight) {
                        n2 = (height - width * iconHeight / iconWidth) / 2;
                        n3 = width / (double)iconWidth;
                    }
                    else {
                        n = (width - height * iconWidth / iconHeight) / 2;
                        n3 = height / (double)iconHeight;
                    }
                    ((Graphics2D)create).translate(n, n2);
                    ((Graphics2D)create).scale(n3, n3);
                    icon.paintIcon(this, create, 0, 0);
                }
                create.dispose();
            }
        }).addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2 && WindowsInternalFrameTitlePane.this.frame.isClosable() && !WindowsInternalFrameTitlePane.this.frame.isIcon()) {
                    WindowsInternalFrameTitlePane.this.systemPopupMenu.setVisible(false);
                    WindowsInternalFrameTitlePane.this.frame.doDefaultCloseAction();
                }
                else {
                    super.mouseClicked(mouseEvent);
                }
            }
            
            @Override
            public void mousePressed(final MouseEvent mouseEvent) {
                try {
                    WindowsInternalFrameTitlePane.this.frame.setSelected(true);
                }
                catch (final PropertyVetoException ex) {}
                WindowsInternalFrameTitlePane.this.showSystemPopupMenu(mouseEvent.getComponent());
            }
        });
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
        this.showSystemPopupMenu(this.systemLabel);
    }
    
    private void showSystemPopupMenu(final Component component) {
        final Dimension dimension = new Dimension();
        final Border border = this.frame.getBorder();
        if (border != null) {
            final Dimension dimension2 = dimension;
            dimension2.width += border.getBorderInsets(this.frame).left + border.getBorderInsets(this.frame).right;
            final Dimension dimension3 = dimension;
            dimension3.height += border.getBorderInsets(this.frame).bottom + border.getBorderInsets(this.frame).top;
        }
        if (!this.frame.isIcon()) {
            this.systemPopupMenu.show(component, this.getX() - dimension.width, this.getY() + this.getHeight() - dimension.height);
        }
        else {
            this.systemPopupMenu.show(component, this.getX() - dimension.width, this.getY() - this.systemPopupMenu.getPreferredSize().height - dimension.height);
        }
    }
    
    @Override
    protected PropertyChangeListener createPropertyChangeListener() {
        return new WindowsPropertyChangeHandler();
    }
    
    @Override
    protected LayoutManager createLayout() {
        return new WindowsTitlePaneLayout();
    }
    
    public class WindowsTitlePaneLayout extends TitlePaneLayout
    {
        private Insets captionMargin;
        private Insets contentMargin;
        private XPStyle xp;
        
        WindowsTitlePaneLayout() {
            this.captionMargin = null;
            this.contentMargin = null;
            this.xp = XPStyle.getXP();
            if (this.xp != null) {
                this.captionMargin = this.xp.getMargin(WindowsInternalFrameTitlePane.this, TMSchema.Part.WP_CAPTION, null, TMSchema.Prop.CAPTIONMARGINS);
                this.contentMargin = this.xp.getMargin(WindowsInternalFrameTitlePane.this, TMSchema.Part.WP_CAPTION, null, TMSchema.Prop.CONTENTMARGINS);
            }
            if (this.captionMargin == null) {
                this.captionMargin = new Insets(0, 2, 0, 2);
            }
            if (this.contentMargin == null) {
                this.contentMargin = new Insets(0, 0, 0, 0);
            }
        }
        
        private int layoutButton(final JComponent component, final TMSchema.Part part, int n, final int n2, final int n3, final int n4, final int n5, final boolean b) {
            if (!b) {
                n -= n3;
            }
            component.setBounds(n, n2, n3, n4);
            if (b) {
                n += n3 + 2;
            }
            else {
                n -= 2;
            }
            return n;
        }
        
        @Override
        public void layoutContainer(final Container container) {
            final boolean leftToRight = WindowsGraphicsUtils.isLeftToRight(WindowsInternalFrameTitlePane.this.frame);
            final int width = WindowsInternalFrameTitlePane.this.getWidth();
            final int height = WindowsInternalFrameTitlePane.this.getHeight();
            final int n = (this.xp != null) ? ((height - 2) * 6 / 10) : (height - 4);
            int n2;
            if (this.xp != null) {
                n2 = (leftToRight ? (this.captionMargin.left + 2) : (width - this.captionMargin.right - 2));
            }
            else {
                n2 = (leftToRight ? this.captionMargin.left : (width - this.captionMargin.right));
            }
            this.layoutButton(WindowsInternalFrameTitlePane.this.systemLabel, TMSchema.Part.WP_SYSBUTTON, n2, (height - n) / 2, n, n, 0, leftToRight);
            int n3;
            int n4;
            if (this.xp != null) {
                n3 = (leftToRight ? (width - this.captionMargin.right - 2) : (this.captionMargin.left + 2));
                n4 = 1;
                if (WindowsInternalFrameTitlePane.this.frame.isMaximum()) {
                    ++n4;
                }
                else {
                    n4 += 5;
                }
            }
            else {
                n3 = (leftToRight ? (width - this.captionMargin.right) : this.captionMargin.left);
                n4 = (height - WindowsInternalFrameTitlePane.this.buttonHeight) / 2;
            }
            if (WindowsInternalFrameTitlePane.this.frame.isClosable()) {
                n3 = this.layoutButton(WindowsInternalFrameTitlePane.this.closeButton, TMSchema.Part.WP_CLOSEBUTTON, n3, n4, WindowsInternalFrameTitlePane.this.buttonWidth, WindowsInternalFrameTitlePane.this.buttonHeight, 2, !leftToRight);
            }
            if (WindowsInternalFrameTitlePane.this.frame.isMaximizable()) {
                n3 = this.layoutButton(WindowsInternalFrameTitlePane.this.maxButton, TMSchema.Part.WP_MAXBUTTON, n3, n4, WindowsInternalFrameTitlePane.this.buttonWidth, WindowsInternalFrameTitlePane.this.buttonHeight, (this.xp != null) ? 2 : 0, !leftToRight);
            }
            if (WindowsInternalFrameTitlePane.this.frame.isIconifiable()) {
                this.layoutButton(WindowsInternalFrameTitlePane.this.iconButton, TMSchema.Part.WP_MINBUTTON, n3, n4, WindowsInternalFrameTitlePane.this.buttonWidth, WindowsInternalFrameTitlePane.this.buttonHeight, 0, !leftToRight);
            }
        }
    }
    
    public class WindowsPropertyChangeHandler extends PropertyChangeHandler
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if ("frameIcon".equals(propertyChangeEvent.getPropertyName()) && WindowsInternalFrameTitlePane.this.systemLabel != null) {
                WindowsInternalFrameTitlePane.this.systemLabel.setIcon(WindowsInternalFrameTitlePane.this.frame.getFrameIcon());
            }
            super.propertyChange(propertyChangeEvent);
        }
    }
    
    public static class ScalableIconUIResource implements Icon, UIResource
    {
        private static final int SIZE = 16;
        private Icon[] icons;
        
        public ScalableIconUIResource(final Object[] array) {
            this.icons = new Icon[array.length];
            for (int i = 0; i < array.length; ++i) {
                if (array[i] instanceof UIDefaults.LazyValue) {
                    this.icons[i] = (Icon)((UIDefaults.LazyValue)array[i]).createValue(null);
                }
                else {
                    this.icons[i] = (Icon)array[i];
                }
            }
        }
        
        protected Icon getBestIcon(final int n) {
            if (this.icons != null && this.icons.length > 0) {
                int n2 = 0;
                int n3 = Integer.MAX_VALUE;
                for (int i = 0; i < this.icons.length; ++i) {
                    final Icon icon = this.icons[i];
                    final int iconWidth;
                    if (icon != null && (iconWidth = icon.getIconWidth()) > 0) {
                        final int abs = Math.abs(iconWidth - n);
                        if (abs < n3) {
                            n3 = abs;
                            n2 = i;
                        }
                    }
                }
                return this.icons[n2];
            }
            return null;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final Graphics2D graphics2D = (Graphics2D)graphics.create();
            final int iconWidth = this.getIconWidth();
            final Icon bestIcon = this.getBestIcon((int)(iconWidth * graphics2D.getTransform().getScaleX()));
            final int iconWidth2;
            if (bestIcon != null && (iconWidth2 = bestIcon.getIconWidth()) > 0) {
                final double n3 = iconWidth / (double)iconWidth2;
                graphics2D.translate(n, n2);
                graphics2D.scale(n3, n3);
                bestIcon.paintIcon(component, graphics2D, 0, 0);
            }
            graphics2D.dispose();
        }
        
        @Override
        public int getIconWidth() {
            return 16;
        }
        
        @Override
        public int getIconHeight() {
            return 16;
        }
    }
}
