package com.adventnet.nms.util;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.io.Serializable;

public class XMLNode implements Serializable
{
    public static final int ELEMENT = 1;
    public static final int DOCUMENT = 2;
    public static final int ENTITYREF = 3;
    public static final int COMMENT = 4;
    public static final int PCDATA = 5;
    public static final int CDATA = 6;
    public static final int TEXT_NODE = 7;
    private int nodeType;
    private String nodeValue;
    private String nodeName;
    private Hashtable attributeList;
    private Vector childNodes;
    private XMLNode parentNode;
    Vector attrNameVector;
    
    public XMLNode() {
        this.childNodes = new Vector();
    }
    
    public int getNodeType() {
        return this.nodeType;
    }
    
    public void setNodeType(final int nodeType) {
        this.nodeType = nodeType;
    }
    
    public String getNodeValue() {
        return this.nodeValue;
    }
    
    public void setNodeValue(final String nodeValue) {
        this.nodeValue = nodeValue;
    }
    
    public String getNodeName() {
        return this.nodeName;
    }
    
    public void setNodeName(final String nodeName) {
        this.nodeName = nodeName;
    }
    
    public Hashtable getAttributeList() {
        return this.attributeList;
    }
    
    public void setAttributeList(final Hashtable attributeList) {
        this.attributeList = attributeList;
    }
    
    public Vector getChildNodes() {
        return (Vector)this.childNodes.clone();
    }
    
    public void setChildNodes(final Vector childNodes) {
        this.childNodes = childNodes;
    }
    
    public XMLNode getParentNode() {
        return this.parentNode;
    }
    
    public void setParentNode(final XMLNode parentNode) {
        this.parentNode = parentNode;
    }
    
    public String getNodeId() throws Exception {
        return (String)this.getAttribute("ID");
    }
    
    public Object getAttribute(final String s) {
        if (this.attributeList == null) {
            return null;
        }
        if (s.equals("ID") && this.attributeList.get(s) == null) {
            return this.getAttribute("TREE-NAME");
        }
        return this.attributeList.get(s);
    }
    
    public Vector getAttrKeysVector() {
        return this.attrNameVector;
    }
    
    public void setAttribute(final String s, final String s2) {
        if (this.attributeList == null) {
            this.attrNameVector = new Vector(5);
            this.attributeList = new Hashtable(5);
        }
        this.attributeList.put(s, s2);
        if (this.attrNameVector != null) {
            this.attrNameVector.addElement(s);
        }
    }
    
    public void setAttribute(final String s, final Object o) {
        if (this.attributeList == null) {
            this.attrNameVector = new Vector(5);
            this.attributeList = new Hashtable(5);
        }
        this.attributeList.put(s, o);
        if (this.attrNameVector != null) {
            this.attrNameVector.addElement(s);
        }
    }
    
    public void removeAttribute(final String s) {
        this.attributeList.remove(s);
    }
    
    public void addChildNode(final XMLNode xmlNode) {
        xmlNode.setParentNode(this);
        this.childNodes.addElement(xmlNode);
    }
    
    public void insertChildNode(final XMLNode xmlNode, final int n) {
        xmlNode.setParentNode(this);
        this.childNodes.insertElementAt(xmlNode, n);
    }
    
    public void deleteNode() {
        this.childNodes.removeAllElements();
    }
    
    public void deleteChildNode(final XMLNode xmlNode) {
        if (this.childNodes.contains(xmlNode)) {
            this.childNodes.removeElement(xmlNode);
        }
    }
    
    public boolean isLeaf() {
        return this.getChildCount() == 0;
    }
    
    public int removeFromParent() {
        int removeFromParent = 0;
        final Enumeration elements = this.childNodes.elements();
        while (elements.hasMoreElements()) {
            final XMLNode xmlNode = (XMLNode)elements.nextElement();
            if (xmlNode.getChildNodes().size() != 0) {
                removeFromParent = xmlNode.removeFromParent();
            }
        }
        this.childNodes.removeAllElements();
        this.parentNode.deleteChildNode(this);
        return removeFromParent + this.childNodes.size();
    }
    
    public int getChildCount() {
        return this.childNodes.size();
    }
    
    public Enumeration children() {
        return this.childNodes.elements();
    }
    
    public Object clone() {
        XMLNode xmlNode = null;
        try {
            xmlNode = (XMLNode)super.clone();
            xmlNode.attributeList = (Hashtable)this.attributeList.clone();
            xmlNode.childNodes = new Vector();
            if (this.childNodes != null) {
                final Enumeration elements = this.childNodes.elements();
                while (elements.hasMoreElements()) {
                    xmlNode.addChildNode((XMLNode)((XMLNode)elements.nextElement()).clone());
                }
            }
        }
        catch (final Exception ex) {
            System.err.println("DeviceTreeNode: exception while cloning");
        }
        return xmlNode;
    }
    
    public String toString() {
        return this.nodeName;
    }
    
    public boolean containsChildNodeID(final String s) {
        final int size = this.childNodes.size();
        if (size == 0) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (((String)((XMLNode)this.childNodes.elementAt(i)).getAttribute("ID")).equals(s)) {
                return true;
            }
        }
        return false;
    }
}
