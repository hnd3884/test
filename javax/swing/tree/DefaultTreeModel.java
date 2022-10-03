package javax.swing.tree;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Vector;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import java.beans.ConstructorProperties;
import javax.swing.event.EventListenerList;
import java.io.Serializable;

public class DefaultTreeModel implements Serializable, TreeModel
{
    protected TreeNode root;
    protected EventListenerList listenerList;
    protected boolean asksAllowsChildren;
    
    @ConstructorProperties({ "root" })
    public DefaultTreeModel(final TreeNode treeNode) {
        this(treeNode, false);
    }
    
    public DefaultTreeModel(final TreeNode root, final boolean asksAllowsChildren) {
        this.listenerList = new EventListenerList();
        this.root = root;
        this.asksAllowsChildren = asksAllowsChildren;
    }
    
    public void setAsksAllowsChildren(final boolean asksAllowsChildren) {
        this.asksAllowsChildren = asksAllowsChildren;
    }
    
    public boolean asksAllowsChildren() {
        return this.asksAllowsChildren;
    }
    
    public void setRoot(final TreeNode root) {
        final TreeNode root2 = this.root;
        this.root = root;
        if (root == null && root2 != null) {
            this.fireTreeStructureChanged(this, null);
        }
        else {
            this.nodeStructureChanged(root);
        }
    }
    
    @Override
    public Object getRoot() {
        return this.root;
    }
    
    @Override
    public int getIndexOfChild(final Object o, final Object o2) {
        if (o == null || o2 == null) {
            return -1;
        }
        return ((TreeNode)o).getIndex((TreeNode)o2);
    }
    
    @Override
    public Object getChild(final Object o, final int n) {
        return ((TreeNode)o).getChildAt(n);
    }
    
    @Override
    public int getChildCount(final Object o) {
        return ((TreeNode)o).getChildCount();
    }
    
    @Override
    public boolean isLeaf(final Object o) {
        if (this.asksAllowsChildren) {
            return !((TreeNode)o).getAllowsChildren();
        }
        return ((TreeNode)o).isLeaf();
    }
    
    public void reload() {
        this.reload(this.root);
    }
    
    @Override
    public void valueForPathChanged(final TreePath treePath, final Object userObject) {
        final MutableTreeNode mutableTreeNode = (MutableTreeNode)treePath.getLastPathComponent();
        mutableTreeNode.setUserObject(userObject);
        this.nodeChanged(mutableTreeNode);
    }
    
    public void insertNodeInto(final MutableTreeNode mutableTreeNode, final MutableTreeNode mutableTreeNode2, final int n) {
        mutableTreeNode2.insert(mutableTreeNode, n);
        this.nodesWereInserted(mutableTreeNode2, new int[] { n });
    }
    
    public void removeNodeFromParent(final MutableTreeNode mutableTreeNode) {
        final MutableTreeNode mutableTreeNode2 = (MutableTreeNode)mutableTreeNode.getParent();
        if (mutableTreeNode2 == null) {
            throw new IllegalArgumentException("node does not have a parent.");
        }
        final int[] array = { 0 };
        final Object[] array2 = { null };
        mutableTreeNode2.remove(array[0] = mutableTreeNode2.getIndex(mutableTreeNode));
        array2[0] = mutableTreeNode;
        this.nodesWereRemoved(mutableTreeNode2, array, array2);
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
    
    public void reload(final TreeNode treeNode) {
        if (treeNode != null) {
            this.fireTreeStructureChanged(this, this.getPathToRoot(treeNode), null, null);
        }
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
    
    public void nodesWereRemoved(final TreeNode treeNode, final int[] array, final Object[] array2) {
        if (treeNode != null && array != null) {
            this.fireTreeNodesRemoved(this, this.getPathToRoot(treeNode), array, array2);
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
                    }
                    this.fireTreeNodesChanged(this, this.getPathToRoot(treeNode), array, array2);
                }
            }
            else if (treeNode == this.getRoot()) {
                this.fireTreeNodesChanged(this, this.getPathToRoot(treeNode), null, null);
            }
        }
    }
    
    public void nodeStructureChanged(final TreeNode treeNode) {
        if (treeNode != null) {
            this.fireTreeStructureChanged(this, this.getPathToRoot(treeNode), null, null);
        }
    }
    
    public TreeNode[] getPathToRoot(final TreeNode treeNode) {
        return this.getPathToRoot(treeNode, 0);
    }
    
    protected TreeNode[] getPathToRoot(final TreeNode treeNode, int n) {
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
    
    @Override
    public void addTreeModelListener(final TreeModelListener treeModelListener) {
        this.listenerList.add(TreeModelListener.class, treeModelListener);
    }
    
    @Override
    public void removeTreeModelListener(final TreeModelListener treeModelListener) {
        this.listenerList.remove(TreeModelListener.class, treeModelListener);
    }
    
    public TreeModelListener[] getTreeModelListeners() {
        return this.listenerList.getListeners(TreeModelListener.class);
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
    
    private void fireTreeStructureChanged(final Object o, final TreePath treePath) {
        final Object[] listenerList = this.listenerList.getListenerList();
        TreeModelEvent treeModelEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TreeModelListener.class) {
                if (treeModelEvent == null) {
                    treeModelEvent = new TreeModelEvent(o, treePath);
                }
                ((TreeModelListener)listenerList[i + 1]).treeStructureChanged(treeModelEvent);
            }
        }
    }
    
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        return this.listenerList.getListeners(clazz);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final Vector vector = new Vector();
        objectOutputStream.defaultWriteObject();
        if (this.root != null && this.root instanceof Serializable) {
            vector.addElement("root");
            vector.addElement(this.root);
        }
        objectOutputStream.writeObject(vector);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final Vector vector = (Vector)objectInputStream.readObject();
        int n = 0;
        if (n < vector.size() && vector.elementAt(n).equals("root")) {
            this.root = (TreeNode)vector.elementAt(++n);
            ++n;
        }
    }
}
