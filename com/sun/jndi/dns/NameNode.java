package com.sun.jndi.dns;

import java.util.Hashtable;

class NameNode
{
    private String label;
    private Hashtable<String, NameNode> children;
    private boolean isZoneCut;
    private int depth;
    
    NameNode(final String label) {
        this.children = null;
        this.isZoneCut = false;
        this.depth = 0;
        this.label = label;
    }
    
    protected NameNode newNameNode(final String s) {
        return new NameNode(s);
    }
    
    String getLabel() {
        return this.label;
    }
    
    int depth() {
        return this.depth;
    }
    
    boolean isZoneCut() {
        return this.isZoneCut;
    }
    
    void setZoneCut(final boolean isZoneCut) {
        this.isZoneCut = isZoneCut;
    }
    
    Hashtable<String, NameNode> getChildren() {
        return this.children;
    }
    
    NameNode get(final String s) {
        return (this.children != null) ? this.children.get(s) : null;
    }
    
    NameNode get(final DnsName dnsName, final int n) {
        NameNode value = this;
        for (int n2 = n; n2 < dnsName.size() && value != null; value = value.get(dnsName.getKey(n2)), ++n2) {}
        return value;
    }
    
    NameNode add(final DnsName dnsName, final int n) {
        NameNode nameNode = this;
        for (int i = n; i < dnsName.size(); ++i) {
            final String value = dnsName.get(i);
            final String key = dnsName.getKey(i);
            NameNode nameNode2 = null;
            if (nameNode.children == null) {
                nameNode.children = new Hashtable<String, NameNode>();
            }
            else {
                nameNode2 = nameNode.children.get(key);
            }
            if (nameNode2 == null) {
                nameNode2 = this.newNameNode(value);
                nameNode2.depth = nameNode.depth + 1;
                nameNode.children.put(key, nameNode2);
            }
            nameNode = nameNode2;
        }
        return nameNode;
    }
}
