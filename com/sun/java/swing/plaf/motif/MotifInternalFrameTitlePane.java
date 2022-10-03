package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import java.awt.AWTEvent;
import sun.awt.AWTAccessor;
import java.awt.event.MouseMotionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.Action;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import java.awt.Component;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.JInternalFrame;
import java.awt.Dimension;
import java.awt.Color;
import javax.swing.JPopupMenu;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionListener;
import java.awt.LayoutManager;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class MotifInternalFrameTitlePane extends BasicInternalFrameTitlePane implements LayoutManager, ActionListener, PropertyChangeListener
{
    SystemButton systemButton;
    MinimizeButton minimizeButton;
    MaximizeButton maximizeButton;
    JPopupMenu systemMenu;
    Title title;
    Color color;
    Color highlight;
    Color shadow;
    public static final int BUTTON_SIZE = 19;
    static Dimension buttonDimension;
    
    public MotifInternalFrameTitlePane(final JInternalFrame internalFrame) {
        super(internalFrame);
    }
    
    @Override
    protected void installDefaults() {
        this.setFont(UIManager.getFont("InternalFrame.titleFont"));
        this.setPreferredSize(new Dimension(100, 19));
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
    }
    
    @Override
    protected PropertyChangeListener createPropertyChangeListener() {
        return this;
    }
    
    @Override
    protected LayoutManager createLayout() {
        return this;
    }
    
    JPopupMenu getSystemMenu() {
        return this.systemMenu;
    }
    
    @Override
    protected void assembleSystemMenu() {
        this.systemMenu = new JPopupMenu();
        this.systemMenu.add(this.restoreAction).setMnemonic(getButtonMnemonic("restore"));
        this.systemMenu.add(this.moveAction).setMnemonic(getButtonMnemonic("move"));
        this.systemMenu.add(this.sizeAction).setMnemonic(getButtonMnemonic("size"));
        this.systemMenu.add(this.iconifyAction).setMnemonic(getButtonMnemonic("minimize"));
        this.systemMenu.add(this.maximizeAction).setMnemonic(getButtonMnemonic("maximize"));
        this.systemMenu.add(new JSeparator());
        this.systemMenu.add(this.closeAction).setMnemonic(getButtonMnemonic("close"));
        (this.systemButton = new SystemButton()).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                MotifInternalFrameTitlePane.this.systemMenu.show(MotifInternalFrameTitlePane.this.systemButton, 0, 19);
            }
        });
        this.systemButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent mouseEvent) {
                try {
                    MotifInternalFrameTitlePane.this.frame.setSelected(true);
                }
                catch (final PropertyVetoException ex) {}
                if (mouseEvent.getClickCount() == 2) {
                    MotifInternalFrameTitlePane.this.closeAction.actionPerformed(new ActionEvent(mouseEvent.getSource(), 1001, null, mouseEvent.getWhen(), 0));
                    MotifInternalFrameTitlePane.this.systemMenu.setVisible(false);
                }
            }
        });
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
    protected void createButtons() {
        (this.minimizeButton = new MinimizeButton()).addActionListener(this.iconifyAction);
        (this.maximizeButton = new MaximizeButton()).addActionListener(this.maximizeAction);
    }
    
    @Override
    protected void addSubComponents() {
        (this.title = new Title(this.frame.getTitle())).setFont(this.getFont());
        this.add(this.systemButton);
        this.add(this.title);
        this.add(this.minimizeButton);
        this.add(this.maximizeButton);
    }
    
    @Override
    public void paintComponent(final Graphics graphics) {
    }
    
    void setColors(final Color color, final Color highlight, final Color shadow) {
        this.color = color;
        this.highlight = highlight;
        this.shadow = shadow;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        final String propertyName = propertyChangeEvent.getPropertyName();
        final JInternalFrame internalFrame = (JInternalFrame)propertyChangeEvent.getSource();
        if ("selected".equals(propertyName)) {
            this.repaint();
        }
        else if (propertyName.equals("maximizable")) {
            if (propertyChangeEvent.getNewValue() == Boolean.TRUE) {
                this.add(this.maximizeButton);
            }
            else {
                this.remove(this.maximizeButton);
            }
            this.revalidate();
            this.repaint();
        }
        else if (propertyName.equals("iconable")) {
            if (propertyChangeEvent.getNewValue() == Boolean.TRUE) {
                this.add(this.minimizeButton);
            }
            else {
                this.remove(this.minimizeButton);
            }
            this.revalidate();
            this.repaint();
        }
        else if (propertyName.equals("title")) {
            this.repaint();
        }
        this.enableActions();
    }
    
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
        return new Dimension(100, 19);
    }
    
    @Override
    public void layoutContainer(final Container container) {
        final int width = this.getWidth();
        this.systemButton.setBounds(0, 0, 19, 19);
        int n = width - 19;
        if (this.frame.isMaximizable()) {
            this.maximizeButton.setBounds(n, 0, 19, 19);
            n -= 19;
        }
        else if (this.maximizeButton.getParent() != null) {
            this.maximizeButton.getParent().remove(this.maximizeButton);
        }
        if (this.frame.isIconifiable()) {
            this.minimizeButton.setBounds(n, 0, 19, 19);
            n -= 19;
        }
        else if (this.minimizeButton.getParent() != null) {
            this.minimizeButton.getParent().remove(this.minimizeButton);
        }
        this.title.setBounds(19, 0, n, 19);
    }
    
    @Override
    protected void showSystemMenu() {
        this.systemMenu.show(this.systemButton, 0, 19);
    }
    
    protected void hideSystemMenu() {
        this.systemMenu.setVisible(false);
    }
    
    static {
        MotifInternalFrameTitlePane.buttonDimension = new Dimension(19, 19);
    }
    
    private abstract class FrameButton extends JButton
    {
        FrameButton() {
            this.setFocusPainted(false);
            this.setBorderPainted(false);
        }
        
        @Override
        public boolean isFocusTraversable() {
            return false;
        }
        
        @Override
        public void requestFocus() {
        }
        
        @Override
        public Dimension getMinimumSize() {
            return MotifInternalFrameTitlePane.buttonDimension;
        }
        
        @Override
        public Dimension getPreferredSize() {
            return MotifInternalFrameTitlePane.buttonDimension;
        }
        
        public void paintComponent(final Graphics graphics) {
            final Dimension size = this.getSize();
            final int n = size.width - 1;
            final int n2 = size.height - 1;
            graphics.setColor(MotifInternalFrameTitlePane.this.color);
            graphics.fillRect(1, 1, size.width, size.height);
            final boolean pressed = this.getModel().isPressed();
            graphics.setColor(pressed ? MotifInternalFrameTitlePane.this.shadow : MotifInternalFrameTitlePane.this.highlight);
            graphics.drawLine(0, 0, n, 0);
            graphics.drawLine(0, 0, 0, n2);
            graphics.setColor(pressed ? MotifInternalFrameTitlePane.this.highlight : MotifInternalFrameTitlePane.this.shadow);
            graphics.drawLine(1, n2, n, n2);
            graphics.drawLine(n, 1, n, n2);
        }
    }
    
    private class MinimizeButton extends FrameButton
    {
        @Override
        public void paintComponent(final Graphics graphics) {
            super.paintComponent(graphics);
            graphics.setColor(MotifInternalFrameTitlePane.this.highlight);
            graphics.drawLine(7, 8, 7, 11);
            graphics.drawLine(7, 8, 10, 8);
            graphics.setColor(MotifInternalFrameTitlePane.this.shadow);
            graphics.drawLine(8, 11, 10, 11);
            graphics.drawLine(11, 9, 11, 11);
        }
    }
    
    private class MaximizeButton extends FrameButton
    {
        @Override
        public void paintComponent(final Graphics graphics) {
            super.paintComponent(graphics);
            final int n = 14;
            final boolean maximum = MotifInternalFrameTitlePane.this.frame.isMaximum();
            graphics.setColor(maximum ? MotifInternalFrameTitlePane.this.shadow : MotifInternalFrameTitlePane.this.highlight);
            graphics.drawLine(4, 4, 4, n);
            graphics.drawLine(4, 4, n, 4);
            graphics.setColor(maximum ? MotifInternalFrameTitlePane.this.highlight : MotifInternalFrameTitlePane.this.shadow);
            graphics.drawLine(5, n, n, n);
            graphics.drawLine(n, 5, n, n);
        }
    }
    
    private class SystemButton extends FrameButton
    {
        @Override
        public boolean isFocusTraversable() {
            return false;
        }
        
        @Override
        public void requestFocus() {
        }
        
        @Override
        public void paintComponent(final Graphics graphics) {
            super.paintComponent(graphics);
            graphics.setColor(MotifInternalFrameTitlePane.this.highlight);
            graphics.drawLine(4, 8, 4, 11);
            graphics.drawLine(4, 8, 14, 8);
            graphics.setColor(MotifInternalFrameTitlePane.this.shadow);
            graphics.drawLine(5, 11, 14, 11);
            graphics.drawLine(14, 9, 14, 11);
        }
    }
    
    private class Title extends FrameButton
    {
        Title(final String text) {
            this.setText(text);
            this.setHorizontalAlignment(0);
            this.setBorder(BorderFactory.createBevelBorder(0, UIManager.getColor("activeCaptionBorder"), UIManager.getColor("inactiveCaptionBorder")));
            this.addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(final MouseEvent mouseEvent) {
                    Title.this.forwardEventToParent(mouseEvent);
                }
                
                @Override
                public void mouseMoved(final MouseEvent mouseEvent) {
                    Title.this.forwardEventToParent(mouseEvent);
                }
            });
            this.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent mouseEvent) {
                    Title.this.forwardEventToParent(mouseEvent);
                }
                
                @Override
                public void mousePressed(final MouseEvent mouseEvent) {
                    Title.this.forwardEventToParent(mouseEvent);
                }
                
                @Override
                public void mouseReleased(final MouseEvent mouseEvent) {
                    Title.this.forwardEventToParent(mouseEvent);
                }
                
                @Override
                public void mouseEntered(final MouseEvent mouseEvent) {
                    Title.this.forwardEventToParent(mouseEvent);
                }
                
                @Override
                public void mouseExited(final MouseEvent mouseEvent) {
                    Title.this.forwardEventToParent(mouseEvent);
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
        public void paintComponent(final Graphics graphics) {
            super.paintComponent(graphics);
            if (MotifInternalFrameTitlePane.this.frame.isSelected()) {
                graphics.setColor(UIManager.getColor("activeCaptionText"));
            }
            else {
                graphics.setColor(UIManager.getColor("inactiveCaptionText"));
            }
            final Dimension size = this.getSize();
            final String title = MotifInternalFrameTitlePane.this.frame.getTitle();
            if (title != null) {
                MotifGraphicsUtils.drawStringInRect(MotifInternalFrameTitlePane.this.frame, graphics, title, 0, 0, size.width, size.height, 0);
            }
        }
    }
}
