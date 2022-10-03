package org.jfree.ui;

import java.awt.Color;
import java.text.NumberFormat;
import java.awt.Font;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class NumberCellRenderer extends DefaultTableCellRenderer
{
    public NumberCellRenderer() {
        this.setHorizontalAlignment(4);
    }
    
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        this.setFont(null);
        final NumberFormat nf = NumberFormat.getNumberInstance();
        if (value != null) {
            this.setText(nf.format(value));
        }
        else {
            this.setText("");
        }
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
        }
        else {
            this.setBackground(null);
        }
        return this;
    }
}
