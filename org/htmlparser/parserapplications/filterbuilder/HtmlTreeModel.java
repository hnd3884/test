package org.htmlparser.parserapplications.filterbuilder;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import org.htmlparser.tags.Html;
import org.htmlparser.util.NodeList;
import org.htmlparser.Node;
import java.util.Vector;
import javax.swing.tree.TreeModel;

public class HtmlTreeModel implements TreeModel
{
    protected Vector mTreeListeners;
    protected Node mRoot;
    
    public HtmlTreeModel(final NodeList root) {
        this.mTreeListeners = new Vector();
        (this.mRoot = new Html()).setChildren(root);
    }
    
    public void addTreeModelListener(final TreeModelListener l) {
        synchronized (this.mTreeListeners) {
            if (!this.mTreeListeners.contains(l)) {
                this.mTreeListeners.addElement(l);
            }
        }
    }
    
    public void removeTreeModelListener(final TreeModelListener l) {
        synchronized (this.mTreeListeners) {
            this.mTreeListeners.removeElement(l);
        }
    }
    
    public Object getChild(final Object parent, final int index) {
        final Node node = (Node)parent;
        final NodeList list = node.getChildren();
        if (null == list) {
            throw new IllegalArgumentException("invalid parent for getChild()");
        }
        final Object ret = list.elementAt(index);
        return ret;
    }
    
    public int getChildCount(final Object parent) {
        int ret = 0;
        final Node node = (Node)parent;
        final NodeList list = node.getChildren();
        if (null != list) {
            ret = list.size();
        }
        return ret;
    }
    
    public int getIndexOfChild(final Object parent, final Object child) {
        int ret = -1;
        final Node node = (Node)parent;
        final NodeList list = node.getChildren();
        if (null == list) {
            throw new IllegalArgumentException("invalid parent for getIndexOfChild()");
        }
        for (int count = list.size(), i = 0; i < count; ++i) {
            if (child == list.elementAt(i)) {
                ret = i;
                break;
            }
        }
        if (0 > ret) {
            throw new IllegalArgumentException("child not found in getIndexOfChild()");
        }
        return ret;
    }
    
    public Object getRoot() {
        return this.mRoot;
    }
    
    public boolean isLeaf(final Object node) {
        final NodeList list = ((Node)node).getChildren();
        final boolean ret = null == list || 0 == list.size();
        return ret;
    }
    
    public void valueForPathChanged(final TreePath path, final Object newValue) {
        final TreeModelEvent event = new TreeModelEvent(this, path);
        final Vector v;
        synchronized (this.mTreeListeners) {
            v = (Vector)this.mTreeListeners.clone();
        }
        for (int i = 0; i < v.size(); ++i) {
            final TreeModelListener listener = v.elementAt(i);
            listener.treeStructureChanged(event);
        }
    }
}
