package javax.swing.plaf.metal;

import java.awt.event.MouseEvent;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.JList;
import javax.swing.JComboBox;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.ComboBoxEditor;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class MetalComboBoxUI extends BasicComboBoxUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new MetalComboBoxUI();
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        if (MetalLookAndFeel.usingOcean()) {
            super.paint(graphics, component);
        }
    }
    
    @Override
    public void paintCurrentValue(final Graphics graphics, final Rectangle rectangle, final boolean b) {
        if (MetalLookAndFeel.usingOcean()) {
            rectangle.x += 2;
            rectangle.width -= 3;
            if (this.arrowButton != null) {
                final Insets insets = this.arrowButton.getInsets();
                rectangle.y += insets.top;
                rectangle.height -= insets.top + insets.bottom;
            }
            else {
                rectangle.y += 2;
                rectangle.height -= 4;
            }
            super.paintCurrentValue(graphics, rectangle, b);
        }
        else if (graphics == null || rectangle == null) {
            throw new NullPointerException("Must supply a non-null Graphics and Rectangle");
        }
    }
    
    @Override
    public void paintCurrentValueBackground(final Graphics graphics, final Rectangle rectangle, final boolean b) {
        if (MetalLookAndFeel.usingOcean()) {
            graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
            graphics.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height - 1);
            graphics.setColor(MetalLookAndFeel.getControlShadow());
            graphics.drawRect(rectangle.x + 1, rectangle.y + 1, rectangle.width - 2, rectangle.height - 3);
            if (b && !this.isPopupVisible(this.comboBox) && this.arrowButton != null) {
                graphics.setColor(this.listBox.getSelectionBackground());
                final Insets insets = this.arrowButton.getInsets();
                if (insets.top > 2) {
                    graphics.fillRect(rectangle.x + 2, rectangle.y + 2, rectangle.width - 3, insets.top - 2);
                }
                if (insets.bottom > 2) {
                    graphics.fillRect(rectangle.x + 2, rectangle.y + rectangle.height - insets.bottom, rectangle.width - 3, insets.bottom - 2);
                }
            }
        }
        else if (graphics == null || rectangle == null) {
            throw new NullPointerException("Must supply a non-null Graphics and Rectangle");
        }
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, int n2) {
        int n3;
        if (MetalLookAndFeel.usingOcean() && n2 >= 4) {
            n2 -= 4;
            n3 = super.getBaseline(component, n, n2);
            if (n3 >= 0) {
                n3 += 2;
            }
        }
        else {
            n3 = super.getBaseline(component, n, n2);
        }
        return n3;
    }
    
    @Override
    protected ComboBoxEditor createEditor() {
        return new MetalComboBoxEditor.UIResource();
    }
    
    @Override
    protected ComboPopup createPopup() {
        return super.createPopup();
    }
    
    @Override
    protected JButton createArrowButton() {
        final MetalComboBoxButton metalComboBoxButton = new MetalComboBoxButton(this.comboBox, new MetalComboBoxIcon(), this.comboBox.isEditable() || MetalLookAndFeel.usingOcean(), this.currentValuePane, this.listBox);
        metalComboBoxButton.setMargin(new Insets(0, 1, 1, 3));
        if (MetalLookAndFeel.usingOcean()) {
            metalComboBoxButton.putClientProperty(MetalBorders.NO_BUTTON_ROLLOVER, Boolean.TRUE);
        }
        this.updateButtonForOcean(metalComboBoxButton);
        return metalComboBoxButton;
    }
    
    private void updateButtonForOcean(final JButton button) {
        if (MetalLookAndFeel.usingOcean()) {
            button.setFocusPainted(this.comboBox.isEditable());
        }
    }
    
    public PropertyChangeListener createPropertyChangeListener() {
        return new MetalPropertyChangeListener();
    }
    
    @Deprecated
    protected void editablePropertyChanged(final PropertyChangeEvent propertyChangeEvent) {
    }
    
    @Override
    protected LayoutManager createLayoutManager() {
        return new MetalComboBoxLayoutManager();
    }
    
    public void layoutComboBox(final Container container, final MetalComboBoxLayoutManager metalComboBoxLayoutManager) {
        if (this.comboBox.isEditable() && !MetalLookAndFeel.usingOcean()) {
            metalComboBoxLayoutManager.superLayout(container);
            return;
        }
        if (this.arrowButton != null) {
            if (MetalLookAndFeel.usingOcean()) {
                final Insets insets = this.comboBox.getInsets();
                final int width = this.arrowButton.getMinimumSize().width;
                this.arrowButton.setBounds(MetalUtils.isLeftToRight(this.comboBox) ? (this.comboBox.getWidth() - insets.right - width) : insets.left, insets.top, width, this.comboBox.getHeight() - insets.top - insets.bottom);
            }
            else {
                final Insets insets2 = this.comboBox.getInsets();
                this.arrowButton.setBounds(insets2.left, insets2.top, this.comboBox.getWidth() - (insets2.left + insets2.right), this.comboBox.getHeight() - (insets2.top + insets2.bottom));
            }
        }
        if (this.editor != null && MetalLookAndFeel.usingOcean()) {
            this.editor.setBounds(this.rectangleForCurrentValue());
        }
    }
    
    @Deprecated
    protected void removeListeners() {
        if (this.propertyChangeListener != null) {
            this.comboBox.removePropertyChangeListener(this.propertyChangeListener);
        }
    }
    
    public void configureEditor() {
        super.configureEditor();
    }
    
    public void unconfigureEditor() {
        super.unconfigureEditor();
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        if (!this.isMinimumSizeDirty) {
            return new Dimension(this.cachedMinimumSize);
        }
        Dimension dimension;
        if (!this.comboBox.isEditable() && this.arrowButton != null) {
            final Insets insets = this.arrowButton.getInsets();
            final Insets insets2 = this.comboBox.getInsets();
            final Dimension displaySize;
            dimension = (displaySize = this.getDisplaySize());
            displaySize.width += insets2.left + insets2.right;
            final Dimension dimension2 = dimension;
            dimension2.width += insets.right;
            final Dimension dimension3 = dimension;
            dimension3.width += this.arrowButton.getMinimumSize().width;
            final Dimension dimension4 = dimension;
            dimension4.height += insets2.top + insets2.bottom;
            final Dimension dimension5 = dimension;
            dimension5.height += insets.top + insets.bottom;
        }
        else if (this.comboBox.isEditable() && this.arrowButton != null && this.editor != null) {
            dimension = super.getMinimumSize(component);
            final Insets margin = this.arrowButton.getMargin();
            final Dimension dimension6 = dimension;
            dimension6.height += margin.top + margin.bottom;
            final Dimension dimension7 = dimension;
            dimension7.width += margin.left + margin.right;
        }
        else {
            dimension = super.getMinimumSize(component);
        }
        this.cachedMinimumSize.setSize(dimension.width, dimension.height);
        this.isMinimumSizeDirty = false;
        return new Dimension(this.cachedMinimumSize);
    }
    
    public class MetalPropertyChangeListener extends PropertyChangeHandler
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            super.propertyChange(propertyChangeEvent);
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName == "editable") {
                if (MetalComboBoxUI.this.arrowButton instanceof MetalComboBoxButton) {
                    ((MetalComboBoxButton)MetalComboBoxUI.this.arrowButton).setIconOnly(MetalComboBoxUI.this.comboBox.isEditable() || MetalLookAndFeel.usingOcean());
                }
                MetalComboBoxUI.this.comboBox.repaint();
                MetalComboBoxUI.this.updateButtonForOcean(MetalComboBoxUI.this.arrowButton);
            }
            else if (propertyName == "background") {
                final Color color = (Color)propertyChangeEvent.getNewValue();
                MetalComboBoxUI.this.arrowButton.setBackground(color);
                MetalComboBoxUI.this.listBox.setBackground(color);
            }
            else if (propertyName == "foreground") {
                final Color color2 = (Color)propertyChangeEvent.getNewValue();
                MetalComboBoxUI.this.arrowButton.setForeground(color2);
                MetalComboBoxUI.this.listBox.setForeground(color2);
            }
        }
    }
    
    public class MetalComboBoxLayoutManager extends ComboBoxLayoutManager
    {
        @Override
        public void layoutContainer(final Container container) {
            MetalComboBoxUI.this.layoutComboBox(container, this);
        }
        
        public void superLayout(final Container container) {
            super.layoutContainer(container);
        }
    }
    
    @Deprecated
    public class MetalComboPopup extends BasicComboPopup
    {
        public MetalComboPopup(final JComboBox comboBox) {
            super(comboBox);
        }
        
        public void delegateFocus(final MouseEvent mouseEvent) {
            super.delegateFocus(mouseEvent);
        }
    }
}
