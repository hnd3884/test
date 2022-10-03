package org.dom4j.swing;

import org.dom4j.Node;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;

public class LeafTreeNode implements TreeNode
{
    protected static final Enumeration EMPTY_ENUMERATION;
    private TreeNode parent;
    protected Node xmlNode;
    
    public LeafTreeNode() {
    }
    
    public LeafTreeNode(final Node xmlNode) {
        this.xmlNode = xmlNode;
    }
    
    public LeafTreeNode(final TreeNode parent, final Node xmlNode) {
        this.parent = parent;
        this.xmlNode = xmlNode;
    }
    
    public Enumeration children() {
        return LeafTreeNode.EMPTY_ENUMERATION;
    }
    
    public boolean getAllowsChildren() {
        return false;
    }
    
    public TreeNode getChildAt(final int childIndex) {
        return null;
    }
    
    public int getChildCount() {
        return 0;
    }
    
    public int getIndex(final TreeNode node) {
        return -1;
    }
    
    public TreeNode getParent() {
        return this.parent;
    }
    
    public boolean isLeaf() {
        return true;
    }
    
    public String toString() {
        final String text = this.xmlNode.getText();
        return (text != null) ? text.trim() : "";
    }
    
    public void setParent(final LeafTreeNode parent) {
        this.parent = parent;
    }
    
    public Node getXmlNode() {
        return this.xmlNode;
    }
    
    static {
        EMPTY_ENUMERATION = new Enumeration() {
            public boolean hasMoreElements() {
                return false;
            }
            
            public Object nextElement() {
                return null;
            }
        };
    }
}
