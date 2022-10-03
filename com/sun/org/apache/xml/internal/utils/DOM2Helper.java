package com.sun.org.apache.xml.internal.utils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy;
import org.w3c.dom.Node;

public final class DOM2Helper
{
    private DOM2Helper() {
    }
    
    public static String getLocalNameOfNode(final Node n) {
        final String name = n.getLocalName();
        return (null == name) ? getLocalNameOfNodeFallback(n) : name;
    }
    
    private static String getLocalNameOfNodeFallback(final Node n) {
        final String qname = n.getNodeName();
        final int index = qname.indexOf(58);
        return (index < 0) ? qname : qname.substring(index + 1);
    }
    
    public static String getNamespaceOfNode(final Node n) {
        return n.getNamespaceURI();
    }
    
    public static boolean isNodeAfter(final Node node1, final Node node2) {
        if (node1 == node2 || isNodeTheSame(node1, node2)) {
            return true;
        }
        boolean isNodeAfter = true;
        Node parent1 = getParentOfNode(node1);
        Node parent2 = getParentOfNode(node2);
        if (parent1 == parent2 || isNodeTheSame(parent1, parent2)) {
            if (null != parent1) {
                isNodeAfter = isNodeAfterSibling(parent1, node1, node2);
            }
        }
        else {
            int nParents1 = 2;
            int nParents2 = 2;
            while (parent1 != null) {
                ++nParents1;
                parent1 = getParentOfNode(parent1);
            }
            while (parent2 != null) {
                ++nParents2;
                parent2 = getParentOfNode(parent2);
            }
            Node startNode1 = node1;
            Node startNode2 = node2;
            if (nParents1 < nParents2) {
                for (int adjust = nParents2 - nParents1, i = 0; i < adjust; ++i) {
                    startNode2 = getParentOfNode(startNode2);
                }
            }
            else if (nParents1 > nParents2) {
                for (int adjust = nParents1 - nParents2, i = 0; i < adjust; ++i) {
                    startNode1 = getParentOfNode(startNode1);
                }
            }
            Node prevChild1 = null;
            Node prevChild2 = null;
            while (null != startNode1) {
                if (startNode1 == startNode2 || isNodeTheSame(startNode1, startNode2)) {
                    if (null == prevChild1) {
                        isNodeAfter = (nParents1 < nParents2);
                        break;
                    }
                    isNodeAfter = isNodeAfterSibling(startNode1, prevChild1, prevChild2);
                    break;
                }
                else {
                    prevChild1 = startNode1;
                    startNode1 = getParentOfNode(startNode1);
                    prevChild2 = startNode2;
                    startNode2 = getParentOfNode(startNode2);
                }
            }
        }
        return isNodeAfter;
    }
    
    public static boolean isNodeTheSame(final Node node1, final Node node2) {
        if (node1 instanceof DTMNodeProxy && node2 instanceof DTMNodeProxy) {
            return ((DTMNodeProxy)node1).equals(node2);
        }
        return node1 == node2;
    }
    
    public static Node getParentOfNode(final Node node) {
        Node parent = node.getParentNode();
        if (parent == null && 2 == node.getNodeType()) {
            parent = ((Attr)node).getOwnerElement();
        }
        return parent;
    }
    
    private static boolean isNodeAfterSibling(final Node parent, final Node child1, final Node child2) {
        boolean isNodeAfterSibling = false;
        final short child1type = child1.getNodeType();
        final short child2type = child2.getNodeType();
        if (2 != child1type && 2 == child2type) {
            isNodeAfterSibling = false;
        }
        else if (2 == child1type && 2 != child2type) {
            isNodeAfterSibling = true;
        }
        else if (2 == child1type) {
            final NamedNodeMap children = parent.getAttributes();
            final int nNodes = children.getLength();
            boolean found1 = false;
            boolean found2 = false;
            for (int i = 0; i < nNodes; ++i) {
                final Node child3 = children.item(i);
                if (child1 == child3 || isNodeTheSame(child1, child3)) {
                    if (found2) {
                        isNodeAfterSibling = false;
                        break;
                    }
                    found1 = true;
                }
                else if (child2 == child3 || isNodeTheSame(child2, child3)) {
                    if (found1) {
                        isNodeAfterSibling = true;
                        break;
                    }
                    found2 = true;
                }
            }
        }
        else {
            Node child4 = parent.getFirstChild();
            boolean found3 = false;
            boolean found4 = false;
            while (null != child4) {
                if (child1 == child4 || isNodeTheSame(child1, child4)) {
                    if (found4) {
                        isNodeAfterSibling = false;
                        break;
                    }
                    found3 = true;
                }
                else if (child2 == child4 || isNodeTheSame(child2, child4)) {
                    if (found3) {
                        isNodeAfterSibling = true;
                        break;
                    }
                    found4 = true;
                }
                child4 = child4.getNextSibling();
            }
        }
        return isNodeAfterSibling;
    }
}
