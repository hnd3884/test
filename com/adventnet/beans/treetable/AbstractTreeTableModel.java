package com.adventnet.beans.treetable;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import javax.swing.event.EventListenerList;
import java.io.Serializable;

public abstract class AbstractTreeTableModel implements TreeTableModel, Serializable
{
    protected Object root;
    protected EventListenerList listenerList;
    
    public AbstractTreeTableModel(final Object root) {
        this.listenerList = new EventListenerList();
        this.root = root;
    }
    
    public Object getRoot() {
        return this.root;
    }
    
    public boolean isLeaf(final Object o) {
        return this.getChildCount(o) == 0;
    }
    
    public void valueForPathChanged(final TreePath treePath, final Object o) {
    }
    
    public int getIndexOfChild(final Object o, final Object o2) {
        for (int i = 0; i < this.getChildCount(o); ++i) {
            if (this.getChild(o, i).equals(o2)) {
                return i;
            }
        }
        return -1;
    }
    
    public void addTreeModelListener(final TreeModelListener treeModelListener) {
        this.listenerList.add(TreeModelListener.class, treeModelListener);
    }
    
    public void removeTreeModelListener(final TreeModelListener treeModelListener) {
        this.listenerList.remove(TreeModelListener.class, treeModelListener);
    }
    
    protected void fireTreeNodesChanged(final Object o, final Object[] array, final int[] array2, final Object[] array3) {
        final Object[] listenerList = this.listenerList.getListenerList();
        TreeModelEvent treeModelEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TreeModelListener.class) {
                if (treeModelEvent == null) {
                    treeModelEvent = new TreeModelEvent(o, array, array2, array3);
                }
                ((TreeModelListener)listenerList[i + 1]).treeNodesChanged(treeModelEvent);
            }
        }
    }
    
    protected void fireTreeNodesInserted(final Object o, final Object[] array, final int[] array2, final Object[] array3) {
        final Object[] listenerList = this.listenerList.getListenerList();
        TreeModelEvent treeModelEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TreeModelListener.class) {
                if (treeModelEvent == null) {
                    treeModelEvent = new TreeModelEvent(o, array, array2, array3);
                }
                ((TreeModelListener)listenerList[i + 1]).treeNodesInserted(treeModelEvent);
            }
        }
    }
    
    protected void fireTreeNodesRemoved(final Object o, final Object[] array, final int[] array2, final Object[] array3) {
        final Object[] listenerList = this.listenerList.getListenerList();
        TreeModelEvent treeModelEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TreeModelListener.class) {
                if (treeModelEvent == null) {
                    treeModelEvent = new TreeModelEvent(o, array, array2, array3);
                }
                ((TreeModelListener)listenerList[i + 1]).treeNodesRemoved(treeModelEvent);
            }
        }
    }
    
    protected void fireTreeStructureChanged(final Object o, final Object[] array, final int[] array2, final Object[] array3) {
        final Object[] listenerList = this.listenerList.getListenerList();
        TreeModelEvent treeModelEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TreeModelListener.class) {
                if (treeModelEvent == null) {
                    treeModelEvent = new TreeModelEvent(o, array, array2, array3);
                }
                ((TreeModelListener)listenerList[i + 1]).treeStructureChanged(treeModelEvent);
            }
        }
    }
    
    public Class getColumnClass(final int n) {
        return Object.class;
    }
    
    public boolean isCellEditable(final Object o, final int n) {
        return this.getColumnClass(n) == TreeTableModel.class;
    }
    
    public void setValueAt(final Object o, final Object o2, final int n) {
    }
}
