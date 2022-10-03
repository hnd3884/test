package com.adventnet.beans.treetable;

import com.adventnet.beans.xtable.ModelException;
import java.util.Locale;
import javax.swing.tree.TreePath;
import javax.swing.tree.MutableTreeNode;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.swing.tree.TreeNode;
import com.adventnet.beans.xtable.SortColumn;
import java.io.Serializable;

public class DefaultTreeTableModel extends AbstractTreeTableModel implements Serializable
{
    private String[] columnNames;
    private String[] methodNames;
    private String[] setterMethodNames;
    private Class[] cTypes;
    private TreeTableModelSorter sorter;
    private SortColumn[] modelSortedCols;
    private SortColumn[] viewSortedCols;
    
    public DefaultTreeTableModel(final TreeNode treeNode, final String[] columnNames, final String[] methodNames, final String[] setterMethodNames, final Class[] cTypes) {
        super(treeNode);
        this.columnNames = columnNames;
        this.methodNames = methodNames;
        this.setterMethodNames = setterMethodNames;
        this.cTypes = cTypes;
        this.init();
    }
    
    private void init() {
        this.sorter = new DefaultTreeTableModelSorter();
    }
    
    public void setTreeTableModelSorter(final TreeTableModelSorter sorter) {
        this.sorter = sorter;
    }
    
    public TreeTableModelSorter getTreeTableModelSorter() {
        return this.sorter;
    }
    
    public int getChildCount(final Object o) {
        if (o != null) {
            return ((TreeNode)o).getChildCount();
        }
        return -1;
    }
    
    public Object getChild(final Object o, final int n) {
        return ((TreeNode)o).getChildAt(n);
    }
    
    public boolean isLeaf(final Object o) {
        return ((TreeNode)o).isLeaf();
    }
    
    public int getColumnCount() {
        return this.columnNames.length;
    }
    
    public String getColumnName(final int n) {
        if (this.cTypes == null || n < 0 || n >= this.cTypes.length) {
            return null;
        }
        return this.columnNames[n];
    }
    
    public Class getColumnClass(final int n) {
        if (this.cTypes == null || n < 0 || n >= this.cTypes.length) {
            return null;
        }
        return this.cTypes[n];
    }
    
    public Object getValueAt(final Object o, final int n) {
        Method method = null;
        try {
            method = o.getClass().getMethod(this.methodNames[n], (Class<?>[])null);
            if (method != null) {
                return method.invoke(o, (Object[])null);
            }
        }
        catch (final Throwable t) {
            final NoSuchMethodException ex = new NoSuchMethodException(" The Method " + method + " is un invokable.");
        }
        return null;
    }
    
    public boolean isCellEditable(final Object o, final int n) {
        return !o.equals(this.root) && this.setterMethodNames != null && this.setterMethodNames[n] != null;
    }
    
    public void setValueAt(Object instance, final Object o, final int n) {
        boolean b = false;
        try {
            final Method[] methods = o.getClass().getMethods();
            for (int i = methods.length - 1; i >= 0; --i) {
                if (methods[i].getName().equals(this.setterMethodNames[n]) && methods[i].getParameterTypes() != null && methods[i].getParameterTypes().length == 1) {
                    final Class<?> clazz = methods[i].getParameterTypes()[0];
                    if (!clazz.isInstance(instance)) {
                        if (instance instanceof String && ((String)instance).length() == 0) {
                            instance = null;
                        }
                        else {
                            final Constructor constructor = clazz.getConstructor(String.class);
                            if (constructor != null) {
                                instance = constructor.newInstance(instance);
                            }
                            else {
                                instance = null;
                            }
                        }
                    }
                    methods[i].invoke(o, instance);
                    b = true;
                    break;
                }
            }
        }
        catch (final Throwable t) {
            System.out.println("The object" + instance + " is unable to set for the " + o + " at the column index " + n);
        }
        if (b) {
            final TreeNode parent = ((TreeNode)o).getParent();
            this.fireTreeNodesChanged(this, this.getPathToRoot(parent), new int[] { this.getIndexOfChild(parent, o) }, new Object[] { o });
        }
    }
    
    public TreeNode[] getPathToRoot(final TreeNode treeNode) {
        return this.getPathToRoot(treeNode, 0);
    }
    
    private TreeNode[] getPathToRoot(final TreeNode treeNode, int n) {
        TreeNode[] pathToRoot;
        if (treeNode == null) {
            if (n == 0) {
                return null;
            }
            pathToRoot = new TreeNode[n];
        }
        else {
            ++n;
            if (treeNode == this.root) {
                pathToRoot = new TreeNode[n];
            }
            else {
                pathToRoot = this.getPathToRoot(treeNode.getParent(), n);
            }
            pathToRoot[pathToRoot.length - n] = treeNode;
        }
        return pathToRoot;
    }
    
    public void removeNodeFromParent(final TreeNode treeNode) {
        final MutableTreeNode mutableTreeNode = (MutableTreeNode)treeNode.getParent();
        if (mutableTreeNode == null) {
            throw new IllegalArgumentException("node does not have a parent.");
        }
        final int[] array = { 0 };
        final Object[] array2 = { null };
        mutableTreeNode.remove(array[0] = mutableTreeNode.getIndex(treeNode));
        array2[0] = treeNode;
        this.nodesWereRemoved(mutableTreeNode, array, array2);
    }
    
    public void nodesWereRemoved(final TreeNode treeNode, final int[] array, final Object[] array2) {
        if (treeNode != null && array != null) {
            this.fireTreeNodesRemoved(this, this.getPathToRoot(treeNode), array, array2);
        }
    }
    
    public void valueForPathChanged(final TreePath treePath, final Object userObject) {
        final MutableTreeNode mutableTreeNode = (MutableTreeNode)treePath.getLastPathComponent();
        mutableTreeNode.setUserObject(userObject);
        this.nodeChanged(mutableTreeNode);
    }
    
    public void nodeChanged(final TreeNode treeNode) {
        if (this.listenerList != null && treeNode != null) {
            final TreeNode parent = treeNode.getParent();
            if (parent != null) {
                final int index = parent.getIndex(treeNode);
                if (index != -1) {
                    this.nodesChanged(parent, new int[] { index });
                }
            }
            else if (treeNode == this.getRoot()) {
                this.nodesChanged(treeNode, null);
            }
        }
    }
    
    public void nodesChanged(final TreeNode treeNode, final int[] array) {
        if (treeNode != null) {
            if (array != null) {
                final int length = array.length;
                if (length > 0) {
                    final Object[] array2 = new Object[length];
                    for (int i = 0; i < length; ++i) {
                        array2[i] = treeNode.getChildAt(array[i]);
                        this.fireTreeNodesChanged(this, this.getPathToRoot(treeNode), array, array2);
                    }
                }
            }
            else if (treeNode == this.getRoot()) {
                this.fireTreeNodesChanged(this, this.getPathToRoot(treeNode), null, null);
            }
        }
    }
    
    public void reload(final TreeNode treeNode) {
        if (treeNode != null) {
            this.fireTreeStructureChanged(this, this.getPathToRoot(treeNode), null, null);
        }
    }
    
    public void insertNodeInto(final MutableTreeNode mutableTreeNode, final MutableTreeNode mutableTreeNode2, final int n) {
        mutableTreeNode2.insert(mutableTreeNode, n);
        this.nodesWereInserted(mutableTreeNode2, new int[] { n });
    }
    
    public void nodesWereInserted(final TreeNode treeNode, final int[] array) {
        if (this.listenerList != null && treeNode != null && array != null && array.length > 0) {
            final int length = array.length;
            final Object[] array2 = new Object[length];
            for (int i = 0; i < length; ++i) {
                array2[i] = treeNode.getChildAt(array[i]);
            }
            this.fireTreeNodesInserted(this, this.getPathToRoot(treeNode), array, array2);
        }
    }
    
    public void sortModel(final Object o, final SortColumn[] modelSortedCols) throws ModelException {
        this.modelSortedCols = modelSortedCols;
        if (modelSortedCols != null) {
            this.sorter.sort(this, o, modelSortedCols, null, 0, ((TreeNode)o).getChildCount());
        }
    }
    
    public SortColumn[] getModelSortedColumns() {
        return this.modelSortedCols;
    }
    
    public void sortView(final Object o, final SortColumn[] viewSortedCols) throws ModelException {
        this.viewSortedCols = viewSortedCols;
        if (viewSortedCols != null) {
            this.sorter.sort(this, o, viewSortedCols, null, 0, ((TreeNode)o).getChildCount());
        }
    }
    
    public SortColumn[] getViewSortedColumns() {
        return this.viewSortedCols;
    }
}
