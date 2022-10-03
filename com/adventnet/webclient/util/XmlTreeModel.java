package com.adventnet.webclient.util;

import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import org.w3c.dom.Element;
import javax.swing.tree.TreeModel;

public class XmlTreeModel implements TreeModel
{
    public XmlTreeNode rootNode;
    
    public XmlTreeModel(final Element xmlElement) {
        this.rootNode = null;
        this.rootNode = new XmlTreeNode(xmlElement, null);
    }
    
    public void addTreeModelListener(final TreeModelListener listener) {
    }
    
    public Object getChild(final Object parent, final int index) {
        return null;
    }
    
    public int getChildCount(final Object parent) {
        return 0;
    }
    
    public int getIndexOfChild(final Object parent, final Object child) {
        return 0;
    }
    
    public Object getRoot() {
        return this.rootNode;
    }
    
    public boolean isLeaf(final Object node) {
        return false;
    }
    
    public void removeTreeModelListener(final TreeModelListener listener) {
    }
    
    public void valueForPathChanged(final TreePath path, final Object newValue) {
    }
}
