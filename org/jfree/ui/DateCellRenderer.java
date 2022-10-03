package org.jfree.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import javax.swing.JTable;
import java.text.DateFormat;
import javax.swing.table.DefaultTableCellRenderer;

public class DateCellRenderer extends DefaultTableCellRenderer
{
    private DateFormat formatter;
    
    public DateCellRenderer() {
        this(DateFormat.getDateTimeInstance());
    }
    
    public DateCellRenderer(final DateFormat formatter) {
        this.formatter = formatter;
        this.setHorizontalAlignment(0);
    }
    
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        this.setFont(null);
        if (value != null) {
            this.setText(this.formatter.format(value));
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
