package org.apache.xml.security.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.util.ArrayList;
import org.w3c.dom.NodeList;

public class HelperNodeList implements NodeList
{
    ArrayList nodes;
    boolean _allNodesMustHaveSameParent;
    
    public HelperNodeList() {
        this(false);
    }
    
    public HelperNodeList(final boolean allNodesMustHaveSameParent) {
        this.nodes = new ArrayList(20);
        this._allNodesMustHaveSameParent = false;
        this._allNodesMustHaveSameParent = allNodesMustHaveSameParent;
    }
    
    public Node item(final int n) {
        return this.nodes.get(n);
    }
    
    public int getLength() {
        return this.nodes.size();
    }
    
    public void appendChild(final Node node) throws IllegalArgumentException {
        if (this._allNodesMustHaveSameParent && this.getLength() > 0 && this.item(0).getParentNode() != node.getParentNode()) {
            throw new IllegalArgumentException("Nodes have not the same Parent");
        }
        this.nodes.add(node);
    }
    
    public Document getOwnerDocument() {
        if (this.getLength() == 0) {
            return null;
        }
        return XMLUtils.getOwnerDocument(this.item(0));
    }
}
