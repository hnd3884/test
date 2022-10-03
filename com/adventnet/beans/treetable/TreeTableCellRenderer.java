package com.adventnet.beans.treetable;

import javax.swing.table.TableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTree;

public class TreeTableCellRenderer extends JTree implements TableCellRenderer
{
    protected int visibleRow;
    protected Border highlightBorder;
    protected JTable table;
    
    public TreeTableCellRenderer(final TreeModel treeModel, final JTable table) {
        super(treeModel);
        this.table = null;
        this.table = table;
    }
    
    public void updateUI() {
        super.updateUI();
        final TreeCellRenderer cellRenderer = this.getCellRenderer();
        if (cellRenderer instanceof DefaultTreeCellRenderer) {
            final DefaultTreeCellRenderer defaultTreeCellRenderer = (DefaultTreeCellRenderer)cellRenderer;
            defaultTreeCellRenderer.setTextSelectionColor(UIManager.getColor("Table.selectionForeground"));
            defaultTreeCellRenderer.setBackgroundSelectionColor(UIManager.getColor("Table.selectionBackground"));
        }
    }
    
    public void setRowHeight(final int rowHeight) {
        if (rowHeight > 0) {
            super.setRowHeight(rowHeight);
            if (this.table != null && this.table.getRowHeight() != rowHeight) {
                this.table.setRowHeight(this.getRowHeight());
            }
        }
    }
    
    public void setBounds(final int n, final int n2, final int n3, final int n4) {
        super.setBounds(n, 0, n3, this.table.getHeight());
    }
    
    public void paint(final Graphics graphics) {
        graphics.translate(0, -this.visibleRow * this.getRowHeight());
        super.paint(graphics);
        if (this.highlightBorder != null) {
            this.highlightBorder.paintBorder(this, graphics, 0, this.visibleRow * this.getRowHeight(), this.getWidth(), this.getRowHeight());
        }
    }
    
    public Component getTableCellRendererComponent(final JTable table, final Object o, final boolean b, final boolean b2, final int visibleRow, final int n) {
        Color backgroundNonSelectionColor;
        Color color;
        if (b) {
            backgroundNonSelectionColor = table.getSelectionBackground();
            color = table.getSelectionForeground();
        }
        else {
            backgroundNonSelectionColor = table.getBackground();
            color = table.getForeground();
        }
        this.highlightBorder = null;
        this.visibleRow = visibleRow;
        this.setBackground(backgroundNonSelectionColor);
        final TreeCellRenderer cellRenderer = this.getCellRenderer();
        if (cellRenderer instanceof DefaultTreeCellRenderer) {
            final DefaultTreeCellRenderer defaultTreeCellRenderer = (DefaultTreeCellRenderer)cellRenderer;
            if (b) {
                defaultTreeCellRenderer.setTextSelectionColor(color);
                defaultTreeCellRenderer.setBackgroundSelectionColor(backgroundNonSelectionColor);
            }
            else {
                defaultTreeCellRenderer.setTextNonSelectionColor(color);
                defaultTreeCellRenderer.setBackgroundNonSelectionColor(backgroundNonSelectionColor);
            }
        }
        return this;
    }
    
    public TableModel getTableModel(final TreeTableModel treeTableModel) {
        return new TreeTableModelAdapter(treeTableModel, this);
    }
}
