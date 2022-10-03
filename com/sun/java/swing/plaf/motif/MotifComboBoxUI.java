package com.sun.java.swing.plaf.motif;

import java.beans.PropertyChangeEvent;
import java.awt.Color;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.JComboBox;
import java.beans.PropertyChangeListener;
import java.awt.LayoutManager;
import java.awt.Container;
import javax.swing.border.Border;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.Icon;
import java.io.Serializable;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class MotifComboBoxUI extends BasicComboBoxUI implements Serializable
{
    Icon arrowIcon;
    static final int HORIZ_MARGIN = 3;
    
    public static ComponentUI createUI(final JComponent component) {
        return new MotifComboBoxUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        this.arrowIcon = new MotifComboBoxArrowIcon(UIManager.getColor("controlHighlight"), UIManager.getColor("controlShadow"), UIManager.getColor("control"));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (MotifComboBoxUI.this.motifGetEditor() != null) {
                    MotifComboBoxUI.this.motifGetEditor().setBackground(UIManager.getColor("text"));
                }
            }
        });
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        if (!this.isMinimumSizeDirty) {
            return new Dimension(this.cachedMinimumSize);
        }
        final Insets insets = this.getInsets();
        final Dimension displaySize;
        final Dimension dimension = displaySize = this.getDisplaySize();
        displaySize.height += insets.top + insets.bottom;
        final int iconAreaWidth = this.iconAreaWidth();
        final Dimension dimension2 = dimension;
        dimension2.width += insets.left + insets.right + iconAreaWidth;
        this.cachedMinimumSize.setSize(dimension.width, dimension.height);
        this.isMinimumSizeDirty = false;
        return dimension;
    }
    
    @Override
    protected ComboPopup createPopup() {
        return new MotifComboPopup(this.comboBox);
    }
    
    @Override
    protected void installComponents() {
        if (this.comboBox.isEditable()) {
            this.addEditor();
        }
        this.comboBox.add(this.currentValuePane);
    }
    
    @Override
    protected void uninstallComponents() {
        this.removeEditor();
        this.comboBox.removeAll();
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final boolean hasFocus = this.comboBox.hasFocus();
        if (this.comboBox.isEnabled()) {
            graphics.setColor(this.comboBox.getBackground());
        }
        else {
            graphics.setColor(UIManager.getColor("ComboBox.disabledBackground"));
        }
        graphics.fillRect(0, 0, component.getWidth(), component.getHeight());
        if (!this.comboBox.isEditable()) {
            this.paintCurrentValue(graphics, this.rectangleForCurrentValue(), hasFocus);
        }
        final Rectangle rectangleForArrowIcon = this.rectangleForArrowIcon();
        this.arrowIcon.paintIcon(component, graphics, rectangleForArrowIcon.x, rectangleForArrowIcon.y);
        if (!this.comboBox.isEditable()) {
            final Border border = this.comboBox.getBorder();
            Insets borderInsets;
            if (border != null) {
                borderInsets = border.getBorderInsets(this.comboBox);
            }
            else {
                borderInsets = new Insets(0, 0, 0, 0);
            }
            if (MotifGraphicsUtils.isLeftToRight(this.comboBox)) {
                final Rectangle rectangle = rectangleForArrowIcon;
                rectangle.x -= 5;
            }
            else {
                final Rectangle rectangle2 = rectangleForArrowIcon;
                rectangle2.x += rectangleForArrowIcon.width + 3 + 1;
            }
            rectangleForArrowIcon.y = borderInsets.top;
            rectangleForArrowIcon.width = 1;
            rectangleForArrowIcon.height = this.comboBox.getBounds().height - borderInsets.bottom - borderInsets.top;
            graphics.setColor(UIManager.getColor("controlShadow"));
            graphics.fillRect(rectangleForArrowIcon.x, rectangleForArrowIcon.y, rectangleForArrowIcon.width, rectangleForArrowIcon.height);
            final Rectangle rectangle3 = rectangleForArrowIcon;
            ++rectangle3.x;
            graphics.setColor(UIManager.getColor("controlHighlight"));
            graphics.fillRect(rectangleForArrowIcon.x, rectangleForArrowIcon.y, rectangleForArrowIcon.width, rectangleForArrowIcon.height);
        }
    }
    
    @Override
    public void paintCurrentValue(final Graphics graphics, final Rectangle rectangle, final boolean b) {
        final Component listCellRendererComponent = this.comboBox.getRenderer().getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, false, false);
        listCellRendererComponent.setFont(this.comboBox.getFont());
        if (this.comboBox.isEnabled()) {
            listCellRendererComponent.setForeground(this.comboBox.getForeground());
            listCellRendererComponent.setBackground(this.comboBox.getBackground());
        }
        else {
            listCellRendererComponent.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
            listCellRendererComponent.setBackground(UIManager.getColor("ComboBox.disabledBackground"));
        }
        this.currentValuePane.paintComponent(graphics, listCellRendererComponent, this.comboBox, rectangle.x, rectangle.y, rectangle.width, listCellRendererComponent.getPreferredSize().height);
    }
    
    protected Rectangle rectangleForArrowIcon() {
        final Rectangle bounds = this.comboBox.getBounds();
        final Border border = this.comboBox.getBorder();
        Insets borderInsets;
        if (border != null) {
            borderInsets = border.getBorderInsets(this.comboBox);
        }
        else {
            borderInsets = new Insets(0, 0, 0, 0);
        }
        bounds.x = borderInsets.left;
        bounds.y = borderInsets.top;
        final Rectangle rectangle = bounds;
        rectangle.width -= borderInsets.left + borderInsets.right;
        final Rectangle rectangle2 = bounds;
        rectangle2.height -= borderInsets.top + borderInsets.bottom;
        if (MotifGraphicsUtils.isLeftToRight(this.comboBox)) {
            bounds.x = bounds.x + bounds.width - 3 - this.arrowIcon.getIconWidth();
        }
        else {
            final Rectangle rectangle3 = bounds;
            rectangle3.x += 3;
        }
        bounds.y += (bounds.height - this.arrowIcon.getIconHeight()) / 2;
        bounds.width = this.arrowIcon.getIconWidth();
        bounds.height = this.arrowIcon.getIconHeight();
        return bounds;
    }
    
    @Override
    protected Rectangle rectangleForCurrentValue() {
        final int width = this.comboBox.getWidth();
        final int height = this.comboBox.getHeight();
        final Insets insets = this.getInsets();
        if (MotifGraphicsUtils.isLeftToRight(this.comboBox)) {
            return new Rectangle(insets.left, insets.top, width - (insets.left + insets.right) - this.iconAreaWidth(), height - (insets.top + insets.bottom));
        }
        return new Rectangle(insets.left + this.iconAreaWidth(), insets.top, width - (insets.left + insets.right) - this.iconAreaWidth(), height - (insets.top + insets.bottom));
    }
    
    public int iconAreaWidth() {
        if (this.comboBox.isEditable()) {
            return this.arrowIcon.getIconWidth() + 6;
        }
        return this.arrowIcon.getIconWidth() + 9 + 2;
    }
    
    public void configureEditor() {
        super.configureEditor();
        this.editor.setBackground(UIManager.getColor("text"));
    }
    
    @Override
    protected LayoutManager createLayoutManager() {
        return new ComboBoxLayoutManager();
    }
    
    private Component motifGetEditor() {
        return this.editor;
    }
    
    @Override
    protected PropertyChangeListener createPropertyChangeListener() {
        return new MotifPropertyChangeListener();
    }
    
    protected class MotifComboPopup extends BasicComboPopup
    {
        public MotifComboPopup(final JComboBox comboBox) {
            super(comboBox);
        }
        
        public MouseMotionListener createListMouseMotionListener() {
            return new MouseMotionAdapter() {};
        }
        
        public KeyListener createKeyListener() {
            return super.createKeyListener();
        }
        
        protected class InvocationKeyHandler extends BasicComboPopup.InvocationKeyHandler
        {
        }
    }
    
    public class ComboBoxLayoutManager extends BasicComboBoxUI.ComboBoxLayoutManager
    {
        @Override
        public void layoutContainer(final Container container) {
            if (MotifComboBoxUI.this.motifGetEditor() != null) {
                final Rectangle rectangleForCurrentValue;
                final Rectangle bounds = rectangleForCurrentValue = MotifComboBoxUI.this.rectangleForCurrentValue();
                ++rectangleForCurrentValue.x;
                final Rectangle rectangle = bounds;
                ++rectangle.y;
                final Rectangle rectangle2 = bounds;
                --rectangle2.width;
                final Rectangle rectangle3 = bounds;
                rectangle3.height -= 2;
                MotifComboBoxUI.this.motifGetEditor().setBounds(bounds);
            }
        }
    }
    
    static class MotifComboBoxArrowIcon implements Icon, Serializable
    {
        private Color lightShadow;
        private Color darkShadow;
        private Color fill;
        
        public MotifComboBoxArrowIcon(final Color lightShadow, final Color darkShadow, final Color fill) {
            this.lightShadow = lightShadow;
            this.darkShadow = darkShadow;
            this.fill = fill;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final int iconWidth = this.getIconWidth();
            final int iconHeight = this.getIconHeight();
            graphics.setColor(this.lightShadow);
            graphics.drawLine(n, n2, n + iconWidth - 1, n2);
            graphics.drawLine(n, n2 + 1, n + iconWidth - 3, n2 + 1);
            graphics.setColor(this.darkShadow);
            graphics.drawLine(n + iconWidth - 2, n2 + 1, n + iconWidth - 1, n2 + 1);
            int n3 = n + 1;
            int n4 = n2 + 2;
            int n5 = iconWidth - 6;
            while (n4 + 1 < n2 + iconHeight) {
                graphics.setColor(this.lightShadow);
                graphics.drawLine(n3, n4, n3 + 1, n4);
                graphics.drawLine(n3, n4 + 1, n3 + 1, n4 + 1);
                if (n5 > 0) {
                    graphics.setColor(this.fill);
                    graphics.drawLine(n3 + 2, n4, n3 + 1 + n5, n4);
                    graphics.drawLine(n3 + 2, n4 + 1, n3 + 1 + n5, n4 + 1);
                }
                graphics.setColor(this.darkShadow);
                graphics.drawLine(n3 + n5 + 2, n4, n3 + n5 + 3, n4);
                graphics.drawLine(n3 + n5 + 2, n4 + 1, n3 + n5 + 3, n4 + 1);
                ++n3;
                n5 -= 2;
                n4 += 2;
            }
            graphics.setColor(this.darkShadow);
            graphics.drawLine(n + iconWidth / 2, n2 + iconHeight - 1, n + iconWidth / 2, n2 + iconHeight - 1);
        }
        
        @Override
        public int getIconWidth() {
            return 11;
        }
        
        @Override
        public int getIconHeight() {
            return 11;
        }
    }
    
    private class MotifPropertyChangeListener extends PropertyChangeHandler
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            super.propertyChange(propertyChangeEvent);
            if (propertyChangeEvent.getPropertyName() == "enabled" && MotifComboBoxUI.this.comboBox.isEnabled()) {
                final Component access$000 = MotifComboBoxUI.this.motifGetEditor();
                if (access$000 != null) {
                    access$000.setBackground(UIManager.getColor("text"));
                }
            }
        }
    }
}
