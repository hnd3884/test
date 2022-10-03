package com.adventnet.webclient.util;

import java.util.Enumeration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.Vector;
import org.w3c.dom.Element;
import javax.swing.tree.TreeNode;

public class XmlTreeNode implements TreeNode
{
    private Element rootElement;
    private Vector childList;
    private XmlTreeNode parent;
    
    public XmlTreeNode(final Element xmlElement, final XmlTreeNode parent) {
        this.rootElement = null;
        this.childList = new Vector();
        this.parent = null;
        this.rootElement = xmlElement;
        final NodeList list = this.rootElement.getChildNodes();
        for (int size = list.getLength(), i = 0; i < size; ++i) {
            final Node node = list.item(i);
            if (node != null && node.getNodeType() == 1) {
                final Element childNode = (Element)node;
                this.childList.add(new XmlTreeNode(childNode, this));
            }
        }
        this.parent = parent;
    }
    
    public Enumeration children() {
        return null;
    }
    
    public boolean getAllowsChildren() {
        return true;
    }
    
    public TreeNode getChildAt(final int childIndex) {
        return this.childList.get(childIndex);
    }
    
    public int getChildCount() {
        return this.childList.size();
    }
    
    public int getIndex(final TreeNode node) {
        return 0;
    }
    
    public TreeNode getParent() {
        return this.parent;
    }
    
    public boolean isLeaf() {
        final boolean hasChilds = this.rootElement.hasChildNodes();
        return !hasChilds;
    }
    
    public Object getUserObject() {
        return this.rootElement;
    }
}
