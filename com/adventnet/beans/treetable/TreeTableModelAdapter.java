package com.adventnet.beans.treetable;

import com.adventnet.beans.xtable.ModelException;
import com.adventnet.beans.xtable.SortColumn;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.JTree;
import com.adventnet.beans.xtable.DefaultXTableModel;

public class TreeTableModelAdapter extends DefaultXTableModel
{
    private JTree tree;
    private TreeTableModel treeTableModel;
    
    public TreeTableModelAdapter(final TreeTableModel treeTableModel, final JTree tree) {
        this.tree = tree;
        this.treeTableModel = treeTableModel;
        tree.addTreeExpansionListener(new TreeExpansionListener() {
            public void treeExpanded(final TreeExpansionEvent treeExpansionEvent) {
                TreeTableModelAdapter.this.fireTableDataChanged();
            }
            
            public void treeCollapsed(final TreeExpansionEvent treeExpansionEvent) {
                TreeTableModelAdapter.this.fireTableDataChanged();
            }
        });
        treeTableModel.addTreeModelListener(new TreeModelListener() {
            public void treeNodesChanged(final TreeModelEvent treeModelEvent) {
                TreeTableModelAdapter.this.delayedFireTableDataChanged();
            }
            
            public void treeNodesInserted(final TreeModelEvent treeModelEvent) {
                TreeTableModelAdapter.this.delayedFireTableDataChanged();
            }
            
            public void treeNodesRemoved(final TreeModelEvent treeModelEvent) {
                TreeTableModelAdapter.this.delayedFireTableDataChanged();
            }
            
            public void treeStructureChanged(final TreeModelEvent treeModelEvent) {
                TreeTableModelAdapter.this.delayedFireTableDataChanged();
            }
        });
    }
    
    public int getColumnCount() {
        if (this.treeTableModel == null) {
            return super.getColumnCount();
        }
        return this.treeTableModel.getColumnCount();
    }
    
    public String getColumnName(final int n) {
        return this.treeTableModel.getColumnName(n);
    }
    
    public Class getColumnClass(final int n) {
        return this.treeTableModel.getColumnClass(n);
    }
    
    public int getRowCount() {
        if (this.tree == null) {
            return super.getRowCount();
        }
        return this.tree.getRowCount();
    }
    
    protected Object nodeForRow(final int n) {
        return this.tree.getPathForRow(n).getLastPathComponent();
    }
    
    public Object getValueAt(final int n, final int n2) {
        return this.treeTableModel.getValueAt(this.nodeForRow(n), n2);
    }
    
    public boolean isCellEditable(final int n, final int n2) {
        return this.treeTableModel.isCellEditable(this.nodeForRow(n), n2);
    }
    
    public void setValueAt(final Object o, final int n, final int n2) {
        if (o != null) {
            this.treeTableModel.setValueAt(o, this.nodeForRow(n), n2);
        }
    }
    
    protected void delayedFireTableDataChanged() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TreeTableModelAdapter.this.fireTableDataChanged();
            }
        });
    }
    
    public TreeTableModel getModel() {
        return this.treeTableModel;
    }
    
    public void sortModel(final SortColumn[] array) throws ModelException {
        this.getModel().sortModel(this.tree.getModel().getRoot(), array);
    }
    
    public void sortView(final SortColumn[] array) throws ModelException {
        this.getModel().sortView(this.tree.getModel().getRoot(), array);
    }
    
    public SortColumn[] getModelSortedColumns() {
        return this.getModel().getModelSortedColumns();
    }
    
    public SortColumn[] getViewSortedColumns() {
        return this.getModel().getViewSortedColumns();
    }
}
