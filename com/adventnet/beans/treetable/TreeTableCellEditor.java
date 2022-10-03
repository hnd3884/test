package com.adventnet.beans.treetable;

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.Icon;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.DefaultCellEditor;

public class TreeTableCellEditor extends DefaultCellEditor
{
    JTree tree;
    JTable table;
    
    public TreeTableCellEditor(final JTree tree) {
        super(new TreeTableTextField());
        this.tree = null;
        this.table = null;
        this.tree = tree;
    }
    
    public Component getTableCellEditorComponent(final JTable table, final Object o, final boolean b, final int n, final int n2) {
        final Component tableCellEditorComponent = super.getTableCellEditorComponent(table, o, b, n, n2);
        final JTree tree = this.tree;
        this.table = table;
        final int n3 = tree.isRootVisible() ? n : (n - 1);
        int x = tree.getRowBounds(n3).x;
        final TreeCellRenderer cellRenderer = tree.getCellRenderer();
        if (cellRenderer instanceof DefaultTreeCellRenderer) {
            Icon icon;
            if (tree.getModel().isLeaf(tree.getPathForRow(n3).getLastPathComponent())) {
                icon = ((DefaultTreeCellRenderer)cellRenderer).getLeafIcon();
            }
            else if (this.tree.isExpanded(n3)) {
                icon = ((DefaultTreeCellRenderer)cellRenderer).getOpenIcon();
            }
            else {
                icon = ((DefaultTreeCellRenderer)cellRenderer).getClosedIcon();
            }
            if (icon != null) {
                x += ((DefaultTreeCellRenderer)cellRenderer).getIconTextGap() + icon.getIconWidth();
            }
        }
        ((TreeTableTextField)this.getComponent()).offset = x;
        return tableCellEditorComponent;
    }
    
    public boolean isCellEditable(final EventObject eventObject) {
        if (eventObject instanceof MouseEvent) {
            final MouseEvent mouseEvent = (MouseEvent)eventObject;
            if (this.table == null) {
                this.table = (JTable)mouseEvent.getSource();
            }
            if (mouseEvent.getModifiers() == 0 || mouseEvent.getModifiers() == 16) {
                for (int i = this.table.getColumnCount() - 1; i >= 0; --i) {
                    if (this.table.getColumnClass(i) == TreeTableModel.class) {
                        this.tree.dispatchEvent(new MouseEvent(this.tree, mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers(), mouseEvent.getX() - this.table.getCellRect(0, i, true).x, mouseEvent.getY(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger()));
                        break;
                    }
                }
            }
            return mouseEvent.getClickCount() >= 3;
        }
        return eventObject == null;
    }
    
    static class TreeTableTextField extends JTextField
    {
        public int offset;
        
        public void reshape(final int n, final int n2, final int n3, final int n4) {
            final int max = Math.max(n, this.offset);
            super.reshape(max, n2, n3 - (max - n), n4);
        }
    }
}
