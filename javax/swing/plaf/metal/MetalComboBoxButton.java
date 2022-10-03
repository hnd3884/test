package javax.swing.plaf.metal;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Container;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.UIManager;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.CellRendererPane;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.JButton;

public class MetalComboBoxButton extends JButton
{
    protected JComboBox comboBox;
    protected JList listBox;
    protected CellRendererPane rendererPane;
    protected Icon comboIcon;
    protected boolean iconOnly;
    
    public final JComboBox getComboBox() {
        return this.comboBox;
    }
    
    public final void setComboBox(final JComboBox comboBox) {
        this.comboBox = comboBox;
    }
    
    public final Icon getComboIcon() {
        return this.comboIcon;
    }
    
    public final void setComboIcon(final Icon comboIcon) {
        this.comboIcon = comboIcon;
    }
    
    public final boolean isIconOnly() {
        return this.iconOnly;
    }
    
    public final void setIconOnly(final boolean iconOnly) {
        this.iconOnly = iconOnly;
    }
    
    MetalComboBoxButton() {
        super("");
        this.iconOnly = false;
        this.setModel(new DefaultButtonModel() {
            @Override
            public void setArmed(final boolean b) {
                super.setArmed(this.isPressed() || b);
            }
        });
    }
    
    public MetalComboBoxButton(final JComboBox comboBox, final Icon comboIcon, final CellRendererPane rendererPane, final JList listBox) {
        this();
        this.comboBox = comboBox;
        this.comboIcon = comboIcon;
        this.rendererPane = rendererPane;
        this.listBox = listBox;
        this.setEnabled(this.comboBox.isEnabled());
    }
    
    public MetalComboBoxButton(final JComboBox comboBox, final Icon icon, final boolean iconOnly, final CellRendererPane cellRendererPane, final JList list) {
        this(comboBox, icon, cellRendererPane, list);
        this.iconOnly = iconOnly;
    }
    
    @Override
    public boolean isFocusTraversable() {
        return false;
    }
    
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            this.setBackground(this.comboBox.getBackground());
            this.setForeground(this.comboBox.getForeground());
        }
        else {
            this.setBackground(UIManager.getColor("ComboBox.disabledBackground"));
            this.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
        }
    }
    
    public void paintComponent(final Graphics graphics) {
        final boolean leftToRight = MetalUtils.isLeftToRight(this.comboBox);
        super.paintComponent(graphics);
        final Insets insets = this.getInsets();
        final int n = this.getWidth() - (insets.left + insets.right);
        final int n2 = this.getHeight() - (insets.top + insets.bottom);
        if (n2 <= 0 || n <= 0) {
            return;
        }
        final int left = insets.left;
        final int top = insets.top;
        final int n3 = left + (n - 1);
        final int n4 = top + (n2 - 1);
        int iconWidth = 0;
        final int n5 = leftToRight ? n3 : left;
        if (this.comboIcon != null) {
            iconWidth = this.comboIcon.getIconWidth();
            final int iconHeight = this.comboIcon.getIconHeight();
            int n6;
            int n7;
            if (this.iconOnly) {
                n6 = this.getWidth() / 2 - iconWidth / 2;
                n7 = this.getHeight() / 2 - iconHeight / 2;
            }
            else {
                if (leftToRight) {
                    n6 = left + (n - 1) - iconWidth;
                }
                else {
                    n6 = left;
                }
                n7 = top + (n4 - top) / 2 - iconHeight / 2;
            }
            this.comboIcon.paintIcon(this, graphics, n6, n7);
            if (this.comboBox.hasFocus() && (!MetalLookAndFeel.usingOcean() || this.comboBox.isEditable())) {
                graphics.setColor(MetalLookAndFeel.getFocusColor());
                graphics.drawRect(left - 1, top - 1, n + 3, n2 + 1);
            }
        }
        if (MetalLookAndFeel.usingOcean()) {
            return;
        }
        if (!this.iconOnly && this.comboBox != null) {
            final Component listCellRendererComponent = this.comboBox.getRenderer().getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, this.getModel().isPressed(), false);
            listCellRendererComponent.setFont(this.rendererPane.getFont());
            if (this.model.isArmed() && this.model.isPressed()) {
                if (this.isOpaque()) {
                    listCellRendererComponent.setBackground(UIManager.getColor("Button.select"));
                }
                listCellRendererComponent.setForeground(this.comboBox.getForeground());
            }
            else if (!this.comboBox.isEnabled()) {
                if (this.isOpaque()) {
                    listCellRendererComponent.setBackground(UIManager.getColor("ComboBox.disabledBackground"));
                }
                listCellRendererComponent.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
            }
            else {
                listCellRendererComponent.setForeground(this.comboBox.getForeground());
                listCellRendererComponent.setBackground(this.comboBox.getBackground());
            }
            final int n8 = n - (insets.right + iconWidth);
            boolean b = false;
            if (listCellRendererComponent instanceof JPanel) {
                b = true;
            }
            if (leftToRight) {
                this.rendererPane.paintComponent(graphics, listCellRendererComponent, this, left, top, n8, n2, b);
            }
            else {
                this.rendererPane.paintComponent(graphics, listCellRendererComponent, this, left + iconWidth, top, n8, n2, b);
            }
        }
    }
    
    @Override
    public Dimension getMinimumSize() {
        final Dimension dimension = new Dimension();
        final Insets insets = this.getInsets();
        dimension.width = insets.left + this.getComboIcon().getIconWidth() + insets.right;
        dimension.height = insets.bottom + this.getComboIcon().getIconHeight() + insets.top;
        return dimension;
    }
}
