package com.sun.org.apache.xml.internal.security.utils;

import org.w3c.dom.Document;
import java.util.ArrayList;
import org.w3c.dom.Node;
import java.util.List;
import org.w3c.dom.NodeList;

public class HelperNodeList implements NodeList
{
    List<Node> nodes;
    boolean allNodesMustHaveSameParent;
    
    public HelperNodeList() {
        this(false);
    }
    
    public HelperNodeList(final boolean allNodesMustHaveSameParent) {
        this.nodes = new ArrayList<Node>();
        this.allNodesMustHaveSameParent = false;
        this.allNodesMustHaveSameParent = allNodesMustHaveSameParent;
    }
    
    @Override
    public Node item(final int n) {
        return this.nodes.get(n);
    }
    
    @Override
    public int getLength() {
        return this.nodes.size();
    }
    
    public void appendChild(final Node node) throws IllegalArgumentException {
        if (this.allNodesMustHaveSameParent && this.getLength() > 0 && this.item(0).getParentNode() != node.getParentNode()) {
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
