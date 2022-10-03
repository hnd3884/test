package com.adventnet.beans.rangenavigator;

import javax.swing.Icon;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.BorderFactory;
import java.awt.Color;
import javax.swing.border.Border;
import javax.swing.ListCellRenderer;
import javax.swing.JToggleButton;

class ComboBoxRenderer extends JToggleButton implements ListCellRenderer
{
    Border lineBorder;
    Border emptyBorder;
    
    public ComboBoxRenderer() {
        this.lineBorder = BorderFactory.createLineBorder(Color.black, 1);
        this.emptyBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
        this.setOpaque(true);
        this.setHorizontalAlignment(0);
        this.setVerticalAlignment(0);
    }
    
    public Component getListCellRendererComponent(final JList list, final Object o, final int n, final boolean b, final boolean b2) {
        this.setComponentOrientation(list.getComponentOrientation());
        if (b) {
            this.setBackground(list.getSelectionBackground());
            this.setForeground(list.getSelectionForeground());
        }
        else {
            this.setBackground(list.getBackground());
            this.setForeground(list.getForeground());
        }
        if (o instanceof Icon) {
            this.setIcon((Icon)o);
            this.setText("");
        }
        else {
            if (o instanceof JToggleButton) {
                return (JToggleButton)o;
            }
            this.setIcon(null);
            this.setText((o == null) ? "" : o.toString());
        }
        this.setEnabled(list.isEnabled());
        this.setFont(list.getFont());
        if (b2) {
            this.setBorder(this.lineBorder);
        }
        else {
            this.setBorder(this.emptyBorder);
        }
        return this;
    }
}
