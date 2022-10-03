package com.pras.abx;

import java.util.ArrayList;

public class Node
{
    public static int ROOT;
    int index;
    int linenumber;
    String name;
    String namespacePrefix;
    String namespaceURI;
    int namespaceLineNumber;
    ArrayList<Attribute> attrs;
    
    static {
        Node.ROOT = 1;
    }
    
    public Node() {
        this.attrs = new ArrayList<Attribute>();
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public void setIndex(final int index) {
        this.index = index;
    }
    
    public int getLinenumber() {
        return this.linenumber;
    }
    
    public void setLinenumber(final int linenumber) {
        this.linenumber = linenumber;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getNamespacePrefix() {
        return this.namespacePrefix;
    }
    
    public void setNamespacePrefix(final String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
    }
    
    public String getNamespaceURI() {
        return this.namespaceURI;
    }
    
    public void setNamespaceURI(final String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }
    
    public int getNamespaceLineNumber() {
        return this.namespaceLineNumber;
    }
    
    public void setNamespaceLineNumber(final int namespaceLineNumber) {
        this.namespaceLineNumber = namespaceLineNumber;
    }
    
    public ArrayList<Attribute> getAttrs() {
        return this.attrs;
    }
    
    public void addAttribute(final Attribute attr) {
        this.attrs.add(attr);
    }
    
    public void setAttrs(final ArrayList<Attribute> attrs) {
        this.attrs = attrs;
    }
}
