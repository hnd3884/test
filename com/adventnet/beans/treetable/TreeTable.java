package com.adventnet.beans.treetable;

import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.TreePath;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListSelectionModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import java.util.EventObject;
import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import com.adventnet.beans.xtable.XTableModel;
import javax.swing.JTree;
import javax.swing.JTable;
import javax.swing.tree.TreeModel;
import com.adventnet.beans.xtable.XTable;

public class TreeTable extends XTable
{
    protected TreeTableCellRenderer tree;
    private TreeTableModel treeTableModel;
    private boolean sortEnabled;
    private TreeTableModel cacheableModel;
    
    public TreeTable() {
        this.treeTableModel = null;
        this.cacheableModel = null;
    }
    
    public TreeTable(final TreeTableModel treeTableModel) {
        this.treeTableModel = null;
        this.cacheableModel = null;
        this.setTreeTableModel(treeTableModel);
    }
    
    public TreeTableModel getTreeTableModel() {
        return this.treeTableModel;
    }
    
    public void setTreeTableModel(final TreeTableModel treeTableModel) {
        this.treeTableModel = treeTableModel;
        this.tree = new TreeTableCellRenderer(treeTableModel, this);
        super.setModel(new TreeTableModelAdapter(treeTableModel, this.tree));
        this.init();
    }
    
    private void init() {
        final ListToTreeSelectionModelWrapper selectionModel = new ListToTreeSelectionModelWrapper();
        this.tree.setSelectionModel(selectionModel);
        this.setSelectionModel(selectionModel.getListSelectionModel());
        this.setDefaultRenderer(TreeTableModel.class, this.tree);
        this.setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor(this.tree));
        if (this.tree.getRowHeight() < 1) {
            this.setRowHeight(20);
        }
    }
    
    public Object nodeForRow(final int n) {
        return ((TreeTableModelAdapter)this.getModel()).nodeForRow(n);
    }
    
    public void expandTree() {
        for (int i = 0; i < this.tree.getRowCount(); ++i) {
            this.tree.expandRow(i);
        }
        this.tree.updateUI();
    }
    
    public void collapseTree() {
        for (int i = this.tree.getRowCount() - 1; i >= 0; --i) {
            this.tree.collapseRow(i);
        }
        this.tree.updateUI();
    }
    
    public void updateUI() {
        super.updateUI();
        if (this.tree != null) {
            this.tree.updateUI();
            this.setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor(this.tree));
        }
        LookAndFeel.installColorsAndFont(this, "Tree.background", "Tree.foreground", "Tree.font");
    }
    
    public int getEditingRow() {
        return (this.getColumnClass(this.editingColumn) == TreeTableModel.class) ? -1 : this.editingRow;
    }
    
    private int realEditingRow() {
        return this.editingRow;
    }
    
    public void sizeColumnsToFit(final int n) {
        super.sizeColumnsToFit(n);
        if (this.getEditingColumn() != -1 && this.getColumnClass(this.editingColumn) == TreeTableModel.class) {
            final Rectangle cellRect = this.getCellRect(this.realEditingRow(), this.getEditingColumn(), false);
            final Component editorComponent = this.getEditorComponent();
            editorComponent.setBounds(cellRect);
            editorComponent.validate();
        }
    }
    
    public void setRowHeight(final int rowHeight) {
        super.setRowHeight(rowHeight);
        if (this.tree != null && this.tree.getRowHeight() != rowHeight) {
            this.tree.setRowHeight(this.getRowHeight());
        }
    }
    
    public JTree getTree() {
        return this.tree;
    }
    
    public boolean editCellAt(final int n, final int n2, final EventObject eventObject) {
        final boolean editCell = super.editCellAt(n, n2, eventObject);
        if (editCell && this.getColumnClass(n2) == TreeTableModel.class) {
            this.repaint(this.getCellRect(n, n2, false));
        }
        return editCell;
    }
    
    public void setRowStripsEnabled(final boolean rowStripsEnabled) {
        for (int i = 0; i < this.getColumnCount(); ++i) {
            if (!this.getColumnClass(i).equals(TreeTableModel.class) && this.getColumn(i).getCellRenderer() == null) {
                if (rowStripsEnabled) {
                    this.setCellRenderer(i, this.rowStripRenderer);
                }
                else {
                    this.setCellRenderer(i, null);
                }
            }
        }
        this.rowStripsEnabled = rowStripsEnabled;
    }
    
    public void setTreeColumnAsMandatory() {
        final int treeColumn = this.findTreeColumn();
        if (treeColumn >= 0) {
            this.setMandatoryColumn(treeColumn, true);
        }
    }
    
    public void setTreeColumnStandStill(final boolean b) {
        final int treeColumn = this.findTreeColumn();
        if (treeColumn > 0) {
            this.setStandStill(treeColumn, b);
        }
    }
    
    public int findTreeColumn() {
        for (int i = 0; i < this.getColumnCount(); ++i) {
            if (this.getColumnClass(i).equals(TreeTableModel.class)) {
                return i;
            }
        }
        return -1;
    }
    
    class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel
    {
        protected boolean updatingListSelectionModel;
        
        public ListToTreeSelectionModelWrapper() {
            this.getListSelectionModel().addListSelectionListener(this.createListSelectionListener());
        }
        
        ListSelectionModel getListSelectionModel() {
            return this.listSelectionModel;
        }
        
        public void resetRowSelection() {
            if (!this.updatingListSelectionModel) {
                this.updatingListSelectionModel = true;
                try {
                    super.resetRowSelection();
                }
                finally {
                    this.updatingListSelectionModel = false;
                }
            }
        }
        
        protected ListSelectionListener createListSelectionListener() {
            return new ListSelectionHandler();
        }
        
        protected void updateSelectedPathsFromSelectedRows() {
            if (!this.updatingListSelectionModel) {
                this.updatingListSelectionModel = true;
                try {
                    final int minSelectionIndex = this.listSelectionModel.getMinSelectionIndex();
                    final int maxSelectionIndex = this.listSelectionModel.getMaxSelectionIndex();
                    this.clearSelection();
                    if (minSelectionIndex != -1 && maxSelectionIndex != -1) {
                        for (int i = minSelectionIndex; i <= maxSelectionIndex; ++i) {
                            if (this.listSelectionModel.isSelectedIndex(i)) {
                                final TreePath pathForRow = TreeTable.this.tree.getPathForRow(i);
                                if (pathForRow != null) {
                                    this.addSelectionPath(pathForRow);
                                }
                            }
                        }
                    }
                }
                finally {
                    this.updatingListSelectionModel = false;
                }
            }
        }
        
        class ListSelectionHandler implements ListSelectionListener
        {
            public void valueChanged(final ListSelectionEvent listSelectionEvent) {
                ListToTreeSelectionModelWrapper.this.updateSelectedPathsFromSelectedRows();
            }
        }
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
