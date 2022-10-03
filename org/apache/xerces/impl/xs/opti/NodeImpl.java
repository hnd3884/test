package org.apache.xerces.impl.xs.opti;

public class NodeImpl extends DefaultNode
{
    String prefix;
    String localpart;
    String rawname;
    String uri;
    short nodeType;
    boolean hidden;
    
    public NodeImpl() {
    }
    
    public NodeImpl(final String prefix, final String localpart, final String rawname, final String uri, final short nodeType) {
        this.prefix = prefix;
        this.localpart = localpart;
        this.rawname = rawname;
        this.uri = uri;
        this.nodeType = nodeType;
    }
    
    public String getNodeName() {
        return this.rawname;
    }
    
    public String getNamespaceURI() {
        return this.uri;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public String getLocalName() {
        return this.localpart;
    }
    
    public short getNodeType() {
        return this.nodeType;
    }
    
    public void setReadOnly(final boolean hidden, final boolean b) {
        this.hidden = hidden;
    }
    
    public boolean getReadOnly() {
        return this.hidden;
    }
    
    public String toString() {
        return "[" + this.getNodeName() + ": " + this.getNodeValue() + "]";
    }
}
