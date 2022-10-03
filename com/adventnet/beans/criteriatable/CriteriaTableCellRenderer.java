package com.adventnet.beans.criteriatable;

import java.awt.Component;
import javax.swing.JTable;
import java.awt.Font;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.JComboBox;

class CriteriaTableCellRenderer extends JComboBox implements TableCellRenderer
{
    DefaultTableCellRenderer defaultRenderer;
    
    public CriteriaTableCellRenderer() {
        this.defaultRenderer = new DefaultTableCellRenderer();
        final Font font = this.getFont();
        this.setFont(new Font(font.getName(), 0, font.getSize()));
    }
    
    public Component getTableCellRendererComponent(final JTable table, final Object selectedItem, final boolean b, final boolean b2, final int n, final int n2) {
        this.removeAllItems();
        this.addItem(selectedItem);
        this.setSelectedItem(selectedItem);
        this.setBackground(table.getBackground());
        this.setForeground(table.getForeground());
        this.defaultRenderer.setText(null);
        if (n == table.getRowCount() - 1 && n2 == 4) {
            return this.defaultRenderer;
        }
        if (n2 == 1 && table.getValueAt(n, 0) != null && ((CriteriaTable)table).getAttributeModel().getGroupingElementsCount() <= 0) {
            this.defaultRenderer.setText(selectedItem.toString());
            return this.defaultRenderer;
        }
        if (n2 == 4 && table.getValueAt(n + 1, 0) != null && table.getValueAt(n + 1, 0).equals("GROUP_END_INDEX")) {
            return this.defaultRenderer;
        }
        if (table.getValueAt(n, 0) != null) {
            if (n2 != 1 && n2 != 4) {
                return this.defaultRenderer;
            }
            if (n2 == 4) {
                if (n == table.getRowCount() - 1) {
                    return this.defaultRenderer;
                }
                if ((table.getValueAt(n + 1, 0) != null && table.getValueAt(n + 1, 0).equals("GROUP_END_INDEX")) || !table.getValueAt(n, 0).equals("GROUP_END_INDEX")) {
                    return this.defaultRenderer;
                }
            }
        }
        return this;
    }
}
