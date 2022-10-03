package com.sun.org.apache.xalan.internal.lib;

import java.util.Map;
import java.util.HashMap;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.utils.DOM2Helper;
import com.sun.org.apache.xpath.internal.NodeSet;
import org.w3c.dom.NodeList;

public class ExsltSets extends ExsltBase
{
    public static NodeList leading(final NodeList nl1, final NodeList nl2) {
        if (nl2.getLength() == 0) {
            return nl1;
        }
        final NodeSet ns1 = new NodeSet(nl1);
        final NodeSet leadNodes = new NodeSet();
        final Node endNode = nl2.item(0);
        if (!ns1.contains(endNode)) {
            return leadNodes;
        }
        for (int i = 0; i < nl1.getLength(); ++i) {
            final Node testNode = nl1.item(i);
            if (DOM2Helper.isNodeAfter(testNode, endNode) && !DOM2Helper.isNodeTheSame(testNode, endNode)) {
                leadNodes.addElement(testNode);
            }
        }
        return leadNodes;
    }
    
    public static NodeList trailing(final NodeList nl1, final NodeList nl2) {
        if (nl2.getLength() == 0) {
            return nl1;
        }
        final NodeSet ns1 = new NodeSet(nl1);
        final NodeSet trailNodes = new NodeSet();
        final Node startNode = nl2.item(0);
        if (!ns1.contains(startNode)) {
            return trailNodes;
        }
        for (int i = 0; i < nl1.getLength(); ++i) {
            final Node testNode = nl1.item(i);
            if (DOM2Helper.isNodeAfter(startNode, testNode) && !DOM2Helper.isNodeTheSame(startNode, testNode)) {
                trailNodes.addElement(testNode);
            }
        }
        return trailNodes;
    }
    
    public static NodeList intersection(final NodeList nl1, final NodeList nl2) {
        final NodeSet ns1 = new NodeSet(nl1);
        final NodeSet ns2 = new NodeSet(nl2);
        final NodeSet inter = new NodeSet();
        inter.setShouldCacheNodes(true);
        for (int i = 0; i < ns1.getLength(); ++i) {
            final Node n = ns1.elementAt(i);
            if (ns2.contains(n)) {
                inter.addElement(n);
            }
        }
        return inter;
    }
    
    public static NodeList difference(final NodeList nl1, final NodeList nl2) {
        final NodeSet ns1 = new NodeSet(nl1);
        final NodeSet ns2 = new NodeSet(nl2);
        final NodeSet diff = new NodeSet();
        diff.setShouldCacheNodes(true);
        for (int i = 0; i < ns1.getLength(); ++i) {
            final Node n = ns1.elementAt(i);
            if (!ns2.contains(n)) {
                diff.addElement(n);
            }
        }
        return diff;
    }
    
    public static NodeList distinct(final NodeList nl) {
        final NodeSet dist = new NodeSet();
        dist.setShouldCacheNodes(true);
        final Map<String, Node> stringTable = new HashMap<String, Node>();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node currNode = nl.item(i);
            final String key = ExsltBase.toString(currNode);
            if (key == null) {
                dist.addElement(currNode);
            }
            else if (!stringTable.containsKey(key)) {
                stringTable.put(key, currNode);
                dist.addElement(currNode);
            }
        }
        return dist;
    }
    
    public static boolean hasSameNode(final NodeList nl1, final NodeList nl2) {
        final NodeSet ns1 = new NodeSet(nl1);
        final NodeSet ns2 = new NodeSet(nl2);
        for (int i = 0; i < ns1.getLength(); ++i) {
            if (ns2.contains(ns1.elementAt(i))) {
                return true;
            }
        }
        return false;
    }
}
