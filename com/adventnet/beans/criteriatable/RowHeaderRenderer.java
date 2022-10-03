package com.adventnet.beans.criteriatable;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.JButton;

public class RowHeaderRenderer extends JButton implements TableCellRenderer
{
    public Component getTableCellRendererComponent(final JTable table, final Object o, final boolean b, final boolean b2, final int n, final int n2) {
        this.getModel().setPressed(false);
        this.getModel().setArmed(false);
        if (b) {
            this.getModel().setPressed(true);
            this.getModel().setArmed(true);
        }
        return this;
    }
}
