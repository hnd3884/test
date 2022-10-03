package com.sun.java.swing.plaf.motif;

import java.beans.PropertyVetoException;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.FontMetrics;
import sun.swing.SwingUtilities2;
import java.awt.AWTEvent;
import sun.awt.AWTAccessor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.JInternalFrame;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import javax.swing.JLayeredPane;
import java.awt.Component;
import javax.swing.border.Border;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Font;
import java.util.EventListener;
import javax.swing.JPopupMenu;
import javax.swing.Icon;
import javax.swing.plaf.basic.BasicDesktopIconUI;

public class MotifDesktopIconUI extends BasicDesktopIconUI
{
    protected DesktopIconActionListener desktopIconActionListener;
    protected DesktopIconMouseListener desktopIconMouseListener;
    protected Icon defaultIcon;
    protected IconButton iconButton;
    protected IconLabel iconLabel;
    private MotifInternalFrameTitlePane sysMenuTitlePane;
    JPopupMenu systemMenu;
    EventListener mml;
    static final int LABEL_HEIGHT = 18;
    static final int LABEL_DIVIDER = 4;
    static final Font defaultTitleFont;
    
    public static ComponentUI createUI(final JComponent component) {
        return new MotifDesktopIconUI();
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        this.setDefaultIcon(UIManager.getIcon("DesktopIcon.icon"));
        this.iconButton = this.createIconButton(this.defaultIcon);
        this.sysMenuTitlePane = new MotifInternalFrameTitlePane(this.frame);
        this.systemMenu = this.sysMenuTitlePane.getSystemMenu();
        final MotifBorders.FrameBorder frameBorder = new MotifBorders.FrameBorder(this.desktopIcon);
        this.desktopIcon.setLayout(new BorderLayout());
        this.iconButton.setBorder(frameBorder);
        this.desktopIcon.add(this.iconButton, "Center");
        (this.iconLabel = this.createIconLabel(this.frame)).setBorder(frameBorder);
        this.desktopIcon.add(this.iconLabel, "South");
        this.desktopIcon.setSize(this.desktopIcon.getPreferredSize());
        this.desktopIcon.validate();
        JLayeredPane.putLayer(this.desktopIcon, JLayeredPane.getLayer(this.frame));
    }
    
    @Override
    protected void installComponents() {
    }
    
    @Override
    protected void uninstallComponents() {
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.desktopIconActionListener = this.createDesktopIconActionListener();
        this.desktopIconMouseListener = this.createDesktopIconMouseListener();
        this.iconButton.addActionListener(this.desktopIconActionListener);
        this.iconButton.addMouseListener(this.desktopIconMouseListener);
        this.iconLabel.addMouseListener(this.desktopIconMouseListener);
    }
    
    JInternalFrame.JDesktopIcon getDesktopIcon() {
        return this.desktopIcon;
    }
    
    void setDesktopIcon(final JInternalFrame.JDesktopIcon desktopIcon) {
        this.desktopIcon = desktopIcon;
    }
    
    JInternalFrame getFrame() {
        return this.frame;
    }
    
    void setFrame(final JInternalFrame frame) {
        this.frame = frame;
    }
    
    protected void showSystemMenu() {
        this.systemMenu.show(this.iconButton, 0, this.getDesktopIcon().getHeight());
    }
    
    protected void hideSystemMenu() {
        this.systemMenu.setVisible(false);
    }
    
    protected IconLabel createIconLabel(final JInternalFrame internalFrame) {
        return new IconLabel(internalFrame);
    }
    
    protected IconButton createIconButton(final Icon icon) {
        return new IconButton(icon);
    }
    
    protected DesktopIconActionListener createDesktopIconActionListener() {
        return new DesktopIconActionListener();
    }
    
    protected DesktopIconMouseListener createDesktopIconMouseListener() {
        return new DesktopIconMouseListener();
    }
    
    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();
        this.desktopIcon.setLayout(null);
        this.desktopIcon.remove(this.iconButton);
        this.desktopIcon.remove(this.iconLabel);
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.iconButton.removeActionListener(this.desktopIconActionListener);
        this.iconButton.removeMouseListener(this.desktopIconMouseListener);
        this.sysMenuTitlePane.uninstallListeners();
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        final JInternalFrame internalFrame = this.desktopIcon.getInternalFrame();
        int iconWidth = this.defaultIcon.getIconWidth();
        int n = this.defaultIcon.getIconHeight() + 18 + 4;
        final Border border = internalFrame.getBorder();
        if (border != null) {
            iconWidth += border.getBorderInsets(internalFrame).left + border.getBorderInsets(internalFrame).right;
            n += border.getBorderInsets(internalFrame).bottom + border.getBorderInsets(internalFrame).top;
        }
        return new Dimension(iconWidth, n);
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        return this.getMinimumSize(component);
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        return this.getMinimumSize(component);
    }
    
    public Icon getDefaultIcon() {
        return this.defaultIcon;
    }
    
    public void setDefaultIcon(final Icon defaultIcon) {
        this.defaultIcon = defaultIcon;
    }
    
    static {
        defaultTitleFont = new Font("SansSerif", 0, 12);
    }
    
    protected class IconLabel extends JPanel
    {
        JInternalFrame frame;
        
        IconLabel(final JInternalFrame frame) {
            this.frame = frame;
            this.setFont(MotifDesktopIconUI.defaultTitleFont);
            this.addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(final MouseEvent mouseEvent) {
                    IconLabel.this.forwardEventToParent(mouseEvent);
                }
                
                @Override
                public void mouseMoved(final MouseEvent mouseEvent) {
                    IconLabel.this.forwardEventToParent(mouseEvent);
                }
            });
            this.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent mouseEvent) {
                    IconLabel.this.forwardEventToParent(mouseEvent);
                }
                
                @Override
                public void mousePressed(final MouseEvent mouseEvent) {
                    IconLabel.this.forwardEventToParent(mouseEvent);
                }
                
                @Override
                public void mouseReleased(final MouseEvent mouseEvent) {
                    IconLabel.this.forwardEventToParent(mouseEvent);
                }
                
                @Override
                public void mouseEntered(final MouseEvent mouseEvent) {
                    IconLabel.this.forwardEventToParent(mouseEvent);
                }
                
                @Override
                public void mouseExited(final MouseEvent mouseEvent) {
                    IconLabel.this.forwardEventToParent(mouseEvent);
                }
            });
        }
        
        void forwardEventToParent(final MouseEvent mouseEvent) {
            final MouseEvent mouseEvent2 = new MouseEvent(this.getParent(), mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers(), mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), 0);
            final AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
            mouseEventAccessor.setCausedByTouchEvent(mouseEvent2, mouseEventAccessor.isCausedByTouchEvent(mouseEvent));
            this.getParent().dispatchEvent(mouseEvent2);
        }
        
        @Override
        public boolean isFocusTraversable() {
            return false;
        }
        
        @Override
        public Dimension getMinimumSize() {
            return new Dimension(MotifDesktopIconUI.this.defaultIcon.getIconWidth() + 1, 22);
        }
        
        @Override
        public Dimension getPreferredSize() {
            final String title = this.frame.getTitle();
            final FontMetrics fontMetrics = this.frame.getFontMetrics(MotifDesktopIconUI.defaultTitleFont);
            int n = 4;
            if (title != null) {
                n += SwingUtilities2.stringWidth(this.frame, fontMetrics, title);
            }
            return new Dimension(n, 22);
        }
        
        @Override
        public void paint(final Graphics graphics) {
            super.paint(graphics);
            final int n = this.getWidth() - 1;
            graphics.setColor(UIManager.getColor("inactiveCaptionBorder").darker().darker());
            graphics.setClip(0, 0, this.getWidth(), this.getHeight());
            graphics.drawLine(n - 1, 1, n - 1, 1);
            graphics.drawLine(n, 0, n, 0);
            graphics.setColor(UIManager.getColor("inactiveCaption"));
            graphics.fillRect(2, 1, n - 3, 19);
            graphics.setClip(2, 1, n - 4, 18);
            final int n2 = 18 - SwingUtilities2.getFontMetrics(this.frame, graphics).getDescent();
            graphics.setColor(UIManager.getColor("inactiveCaptionText"));
            final String title = this.frame.getTitle();
            if (title != null) {
                SwingUtilities2.drawString(this.frame, graphics, title, 4, n2);
            }
        }
    }
    
    protected class IconButton extends JButton
    {
        Icon icon;
        
        IconButton(final Icon icon) {
            super(icon);
            this.icon = icon;
            this.addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(final MouseEvent mouseEvent) {
                    IconButton.this.forwardEventToParent(mouseEvent);
                }
                
                @Override
                public void mouseMoved(final MouseEvent mouseEvent) {
                    IconButton.this.forwardEventToParent(mouseEvent);
                }
            });
            this.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent mouseEvent) {
                    IconButton.this.forwardEventToParent(mouseEvent);
                }
                
                @Override
                public void mousePressed(final MouseEvent mouseEvent) {
                    IconButton.this.forwardEventToParent(mouseEvent);
                }
                
                @Override
                public void mouseReleased(final MouseEvent mouseEvent) {
                    if (!MotifDesktopIconUI.this.systemMenu.isShowing()) {
                        IconButton.this.forwardEventToParent(mouseEvent);
                    }
                }
                
                @Override
                public void mouseEntered(final MouseEvent mouseEvent) {
                    IconButton.this.forwardEventToParent(mouseEvent);
                }
                
                @Override
                public void mouseExited(final MouseEvent mouseEvent) {
                    IconButton.this.forwardEventToParent(mouseEvent);
                }
            });
        }
        
        void forwardEventToParent(final MouseEvent mouseEvent) {
            final MouseEvent mouseEvent2 = new MouseEvent(this.getParent(), mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers(), mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), 0);
            final AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
            mouseEventAccessor.setCausedByTouchEvent(mouseEvent2, mouseEventAccessor.isCausedByTouchEvent(mouseEvent));
            this.getParent().dispatchEvent(mouseEvent2);
        }
        
        @Override
        public boolean isFocusTraversable() {
            return false;
        }
    }
    
    protected class DesktopIconActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            MotifDesktopIconUI.this.systemMenu.show(MotifDesktopIconUI.this.iconButton, 0, MotifDesktopIconUI.this.getDesktopIcon().getHeight());
        }
    }
    
    protected class DesktopIconMouseListener extends MouseAdapter
    {
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (mouseEvent.getClickCount() > 1) {
                try {
                    MotifDesktopIconUI.this.getFrame().setIcon(false);
                }
                catch (final PropertyVetoException ex) {}
                MotifDesktopIconUI.this.systemMenu.setVisible(false);
                MotifDesktopIconUI.this.getFrame().getDesktopPane().getDesktopManager().endDraggingFrame((JComponent)mouseEvent.getSource());
            }
        }
    }
}
